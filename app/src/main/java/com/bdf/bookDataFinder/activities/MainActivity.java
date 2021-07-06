package com.bdf.bookDataFinder.activities;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.bdf.bookDataFinder.Application;
import com.bdf.bookDataFinder.R;
import com.bdf.bookDataFinder.async.RetrivePDFStream;
import com.bdf.bookDataFinder.common.Constants;
import com.bdf.bookDataFinder.common.Global;
import com.bdf.bookDataFinder.common.treeview.IPdfFileEvent;
import com.bdf.bookDataFinder.common.utils.StringUtils;
import com.bdf.bookDataFinder.common.utils.ViewUtils;
import com.bdf.bookDataFinder.controller.listener.IAccountListener;
import com.bdf.bookDataFinder.controller.listener.ILoginEventListener;
import com.bdf.bookDataFinder.controller.listener.IProgressListener;
import com.bdf.bookDataFinder.controller.listener.ISignupEventListener;
import com.bdf.bookDataFinder.views.changepwd.ChangepwdFragment;
import com.bdf.bookDataFinder.views.home.HomeFragment;
import com.bdf.bookDataFinder.views.login.LoginFragment;
import com.bdf.bookDataFinder.views.payment.PaymentFragment;
import com.bdf.bookDataFinder.views.search.SearchFragment;
import com.bdf.bookDataFinder.views.signup.SignupFragment;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements IPdfFileEvent, OnPageChangeListener, OnLoadCompleteListener, OnPageErrorListener, BottomNavigationView.OnNavigationItemSelectedListener, IProgressListener, IAccountListener, ILoginEventListener, ISignupEventListener {

    private PDFView pdfView;
    private View fragment;
    private NavController navController;
    private BottomNavigationView bottomNavigationView;

    //child bottom bars
    private HomeFragment fragchildHome = null;
    private SearchFragment fragchildSearch = null;
    private LoginFragment fragchildlogin = null;
    private SignupFragment fragchildSignup = null;
    private ChangepwdFragment fragchildChangepwd = null;
    private PaymentFragment fragchildPayment = null;

    private boolean isPageFragOrPdf = true;
    //double return key
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //init views
        initViews();
        // initializing the listeners
        initListeners();
        // initializing the objects
//        initObjects();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private void initViews() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Global.DISPLAY_HEIGHT = displayMetrics.heightPixels;
        Global.DISPLAY_WIDTH = displayMetrics.widthPixels;

        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            Global.STATUSBAR_HEGIHT = getResources().getDimensionPixelSize(resourceId);
        }
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            Global.ACTIONBAR_HEGIHT = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }

        this.bottomNavigationView = findViewById(R.id.nav_bottomMenuView);
        if (Global.isLogin()) {
            this.bottomNavigationView.getMenu().clear();
            this.bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu_logined);
        } else {
            this.bottomNavigationView.getMenu().clear();
            this.bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu_nologined);
        }

        this.pdfView = findViewById(R.id.pdfView);
        this.fragment = (View) findViewById(R.id.nav_host_fragment);
        this.navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //SetActionbarWithNavController(false, this.navController);
        NavigationUI.setupWithNavController(this.bottomNavigationView, this.navController);

//        BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
//        for (int i = 0; i < menuView.getChildCount(); i++) {
//            final View iconView = menuView.getChildAt(i).findViewById(com.google.android.material.R.dbid.icon);
//            ViewGroup.LayoutParams layoutParams = iconView.getLayoutParams();
//            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
//            // set your height here
//            layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, displayMetrics);
//            // set your width here
//            layoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, displayMetrics);
//            iconView.setLayoutParams(layoutParams);
//        }
    }

    private void initListeners() {
        this.bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    private void initObjects() {

        loadFragment(new HomeFragment());
        //set icon top left of display
        ActionBar actionBar = getSupportActionBar();
//        actionBar.setHomeAsUpIndicator(R.drawable.ic_ebook);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        //modify bottomNavigation icon size
        LayoutInflater inflator = LayoutInflater.from(this);
        View v = inflator.inflate(R.layout.item_title, null);
        TextView title = ((TextView) v.findViewById(R.id.title));
        title.setText(this.getTitle());

        AssetManager am = getApplicationContext().getAssets();
        Typeface typeface = Typeface.createFromAsset(am, String.format(Locale.US, "fonts/%s", "goodTimes.ttf"));
        title.setTypeface(typeface);
        actionBar.setCustomView(v);
    }

    public void switchView(boolean isFragOrPdf) {
        if (isFragOrPdf) {
            fragment.setVisibility(View.VISIBLE);
            pdfView.setVisibility(View.INVISIBLE);
        } else {
            pdfView.setVisibility(View.VISIBLE);
            fragment.setVisibility(View.INVISIBLE);
        }
        this.isPageFragOrPdf = isFragOrPdf;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (Global.isLogin()) {
            getMenuInflater().inflate(R.menu.top_nav_menu_logined, menu);
        } else {
            getMenuInflater().inflate(R.menu.top_nav_menu_nologined, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        navigateControl(item.getItemId());
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        navigateControl(item.getItemId());
        return true;
    }

    private void navigateControl(int nResId) {
        Fragment fragment = null;
        switch (nResId) {
            case R.id.navigation_home:
                if (this.fragchildHome == null) {
                    this.fragchildHome = new HomeFragment();
                }
                fragment = this.fragchildHome;
                loadFragment(fragment);
                break;
            case R.id.navigation_search:
                if (this.fragchildSearch == null) {
                    this.fragchildSearch = new SearchFragment();
                }
                fragment = this.fragchildSearch;
                loadFragment(fragment);
                break;
            case R.id.navigation_login:
                if (this.fragchildlogin == null) {
                    this.fragchildlogin = new LoginFragment();
                    this.fragchildlogin.setLoginEventListener(this);
                }
                fragment = this.fragchildlogin;
                loadFragment(fragment);
                break;
            case R.id.navigation_signup:
                if (this.fragchildSignup == null) {
                    this.fragchildSignup = new SignupFragment();
                    this.fragchildSignup.setSignupEventListener(this);
                }
                fragment = this.fragchildSignup;
                loadFragment(fragment);
                break;
            case R.id.navigation_changepwd:
                if (this.fragchildChangepwd == null) {
                    this.fragchildChangepwd = new ChangepwdFragment();
                }
                fragment = this.fragchildChangepwd;
                loadFragment(fragment);
                break;
            case R.id.navigation_payment:
                if (this.fragchildPayment == null) {
                    this.fragchildPayment = new PaymentFragment();
                }
                fragment = this.fragchildPayment;
                loadFragment(fragment);
                break;
            case R.id.navigation_logout:
                logout();
                break;
            case R.id.navigation_gotogoogle:
                gotoHomepage();
                break;
            default:
        }
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment, fragment)
                    .commit();
            switchView(true);
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onPdfFileSelect(long nPdfFileId) {
        //must be code here that get data from server
        try {
            int nFileStatus = Global.getPdfFileStatus(String.valueOf(nPdfFileId));
            if (nFileStatus == Constants.STATUS_FILE_CAN_DOWNLOAD) {
                switchView(false);
                showProgress();
                RetrivePDFStream asyncPdfStream = new RetrivePDFStream(this.pdfView, this, this, this, this, this);
                String downloadUrl = Constants.BACKEND_URL + "bookDataFinder/api/download?" + "bookId=" + String.valueOf(nPdfFileId) + "&" + "userId=" + String.valueOf(Global.g_user.id);
                asyncPdfStream.execute(downloadUrl);
                return true;
            } else if (nFileStatus == Constants.STATUS_FILE_ALREADY_EXIST) {
                switchView(false);
                showProgress();
                File pdfFile = StringUtils.getPdfFilePath(Application.s_Application, nPdfFileId);
                this.pdfView.fromFile(pdfFile).defaultPage(0)
                        .onPageChange(this)
                        .enableAnnotationRendering(true)
                        .onLoad(this)
                        .scrollHandle(new DefaultScrollHandle(this))
                        .spacing(10) // in dp
                        .onPageError(this)
                        .pageFitPolicy(FitPolicy.BOTH)
                        .load();
                return true;
            } else {
                ViewUtils.showToast(this, getResources().getString(R.string.message_payment_toread_pdf));
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void onPdfDownload(long nPdfFileId, String strPdfName) {

    }

    //pdf realtion event callback start
    @Override
    public void loadComplete(int nPages) {
        hideProgress();
    }

    @Override
    public void onPageError(int page, Throwable t) {

    }

    public void onPageChanged(int page, int pageCount) {

    }

    @Override
    public void onBackPressed() {
        if (!isPageFragOrPdf) {
            switchView(true);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            ViewUtils.showToast(this, getResources().getString(R.string.message_back_again_to_exit));

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, Constants.DOUBLE_BACKKEY_TIMEOUT);
        }
    }

    @Override
    public void showProgress() {
        View container = findViewById(R.id.progress_container);
        container.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    public void hideProgress() {
        View container = findViewById(R.id.progress_container);
        container.setVisibility(View.INVISIBLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }

    public void logout() {
        Global.saveUserData(null);
        onChangeAccount();
        navigateControl(R.id.navigation_login);
    }

    public void gotoHomepage() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.goto_hompage)));
        startActivity(browserIntent);
    }

    @Override
    public void onChangeAccount() {
        if (Global.isLogin()) {
            this.bottomNavigationView.getMenu().clear();
            this.bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu_logined);
        } else {
            this.bottomNavigationView.getMenu().clear();
            this.bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu_nologined);
        }

        invalidateOptionsMenu();

        if (this.fragchildHome != null) {
            this.fragchildHome.onDataUpdate();
        }
    }

    @Override
    public void onGotoSignup() {
        navigateControl(R.id.navigation_signup);
    }

    @Override
    public void onLoginSuccess() {
        onChangeAccount();
        navigateControl(R.id.navigation_home);
    }

    @Override
    public void onSignupSuccess() {
        onChangeAccount();
        navigateControl(R.id.navigation_home);
    }
}
