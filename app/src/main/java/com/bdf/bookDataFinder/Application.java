package com.bdf.bookDataFinder;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.multidex.MultiDexApplication;

import com.bdf.bookDataFinder.apis.Api;
import com.bdf.bookDataFinder.common.Constants;
import com.bdf.bookDataFinder.common.Global;
import com.bdf.bookDataFinder.common.datas.CategoryData;
import com.bdf.bookDataFinder.common.datas.CreditCardData;
import com.bdf.bookDataFinder.common.datas.ErrorResponse;
import com.bdf.bookDataFinder.common.datas.UserProfile;
import com.bdf.bookDataFinder.common.utils.StringUtils;
import com.bdf.bookDataFinder.common.utils.ViewUtils;
import com.bdf.bookDataFinder.controller.listener.ILoginListner;
import com.bdf.bookDataFinder.db.DBHelper;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Application extends MultiDexApplication implements ILoginListner {
    private static final String TAG = "BookDataFinderApplication";
    public static Application s_Application = null;

    public DBHelper dbHelper = null;
    public Api api = null;
    private boolean isFinishLoading = false;
    private SharedPreferences userSharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new BDFActivityLifecycleCallbacks(this));
        s_Application = this;

        this.api = new Api();
        //write pdf data folder
        File rootPdfPath = StringUtils.getRootPdfDirPath(this);
        rootPdfPath.mkdirs();

        this.dbHelper = new DBHelper(this);
        this.dbHelper.getWritableDatabase();
        this.userSharedPreferences = getSharedPreferences(Constants.KEY_USER_PREFERENCE, Context.MODE_PRIVATE);
        readSharedPreferenceEmailData();

        if(isOnlineCheck()) {
            Call<CategoryData> callCategory = Global.getApi().getApiService().getBookCategory();
            callCategory.enqueue(new Callback<CategoryData>() {
                @Override
                public void onResponse(Call<CategoryData> call, Response<CategoryData> response) {
                    Global.g_category = response.body();
                    writeJSonDataCategories();
                }

                @Override
                public void onFailure(Call<CategoryData> call, Throwable error) {
                    readJSonDataCategories();
                }
            });
            if(Global.g_user!=null) {
                login();
            }
        }
        else {
            readJSonDataCategories();
        }

    }

    private void readSharedPreferenceEmailData() {
        String currUserEmail = userSharedPreferences.getString(Constants.KEY_USER_PREFERENCE_EMAIL, null);
        String currUserPass = userSharedPreferences.getString(Constants.KEY_USER_PREFERENCE_PASS, null);
        if(currUserEmail!=null && currUserPass!=null) {
            Global.g_user = dbHelper.getUserByEMailAndPwd(currUserEmail, currUserPass);
            CreditCardData card = Global.getDBHelper().getCreditcardDataByUserid(Global.g_user.dbId);
            if(card!=null) {
                Global.g_user.addCreditCard(card);
            }
        }
    }

    public void writeSharedPreferenceEmailData(String email, String pass) {
        SharedPreferences.Editor edit = userSharedPreferences.edit();
        edit.putString(Constants.KEY_USER_PREFERENCE_EMAIL, email);
        edit.putString(Constants.KEY_USER_PREFERENCE_PASS, pass);
        edit.commit();
    }

    private void writeJSonDataCategories() {
        try {
            JSONObject obj = new JSONObject();
            Gson gson = new Gson();
            String json = gson.toJson(Global.g_category);
            File fileCategoryJson = StringUtils.getCategoryFilePath(this);
            Writer output = new BufferedWriter(new FileWriter(fileCategoryJson));
            output.write(json);
            output.close();
            isFinishLoading = true;
        } catch (Exception e) {
            ViewUtils.showToast(this, e.getMessage());
            isFinishLoading = true;
        }
    }

    private void readJSonDataCategories() {
        try {
            String json = loadJSONFromAsset();

            if(json==null) {
                isFinishLoading = true;
                return;
            }
            JSONObject obj = new JSONObject(json);
            JSONArray jsonArray = obj.getJSONArray("category");

            int nLength = jsonArray.length();
            if (nLength > 0) {
                Global.g_category = new CategoryData();
                for (int i = 0; i < nLength; i++) {
                    CategoryData sub = CategoryData.fromJson((Object) jsonArray.get(i));
                    Global.g_category.addSubCategory(sub);
                }
            }
            isFinishLoading = true;

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //pdf realtion event callback end
    public String loadJSONFromAsset() {
        String json = null;
        try {
            File fileCategoryJson = StringUtils.getCategoryFilePath(this);
            if(fileCategoryJson==null || !fileCategoryJson.exists()) { return null; }
            InputStream is = new FileInputStream(fileCategoryJson);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public boolean isFinishLoading() {
        return isFinishLoading;
    }

    /*
     * isOnlineCheck - Check if there is a NetworkConnection
     * @return boolean
     */
    protected boolean isOnlineCheck() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            Global.IS_CONNECT_INTERNET = true;
        } else {
            Global.IS_CONNECT_INTERNET = false;
        }
        return Global.IS_CONNECT_INTERNET;
    }

    public void login() {
        HashMap hashMap = new HashMap();
        hashMap.put("email", Global.g_user.email);
        hashMap.put("password", Global.g_user.password);

        Call<UserProfile> callLogin = Global.getApi().getApiService().login(hashMap);
        callLogin.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                if(response.isSuccessful()) {
                    Application.this.onSuccess(response.body());
                }
                else {
                    try {
                        ErrorResponse error = StringUtils.getErrorResponse(response);
                        if(error!=null) {
                            Application.this.onError(error.getMessage());
                        }
                    } catch (Exception e) {
                        Application.this.onError(Application.s_Application.getResources().getString(R.string.message_login_fail));
                    }
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable error) {
                Application.this.onError(Application.s_Application.getResources().getString(R.string.message_login_fail));
            }
        });

    }

    @Override
    public void onSuccess(UserProfile user) {
        if (user != null) {
            //login success
            Global.saveUserData(user);
        }
    }

    @Override
    public void onError(String message) {
        ViewUtils.showToast(this, message);
    }

    private class BDFActivityLifecycleCallbacks implements ActivityLifecycleCallbacks {

        private int foregroundActivities;
        private boolean isChangingConfiguration;
        private long time;

        public BDFActivityLifecycleCallbacks(Application tcApplication) {

        }

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {
            foregroundActivities++;
            if (foregroundActivities == 1 && !isChangingConfiguration) {
                // 应用进入前台
                time = System.currentTimeMillis();
            }
            isChangingConfiguration = false;
        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
            foregroundActivities--;
            if (foregroundActivities == 0) {
            }
            isChangingConfiguration = activity.isChangingConfigurations();
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    }
}
