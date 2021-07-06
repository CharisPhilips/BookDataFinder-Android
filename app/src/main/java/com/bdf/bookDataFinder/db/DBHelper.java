package com.bdf.bookDataFinder.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bdf.bookDataFinder.common.datas.CreditCardData;
import com.bdf.bookDataFinder.common.datas.Pdfbook;
import com.bdf.bookDataFinder.common.datas.UserProfile;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "bookData.db";
    //user
    public static final String TABLE_USER = "tb_user";
    public static final String COLUMN_USER_ID = "f_userid";
    public static final String COLUMN_USER_EMAIL = "f_email";
    public static final String COLUMN_USER_PASSWORD = "f_password";
    public static final String COLUMN_USER_SERVERID = "f_serverid";
    //payment
    public static final String TABLE_CREDITCARD = "tb_credit";
    public static final String COLUMN_CREDITCARD_ID = "f_credit_id";
    public static final String COLUMN_CREDITCARD_CARDNO = "f_credit_cardnumber";
    public static final String COLUMN_CREDITCARD_EXPIRATION_MONTH = "f_credit_expiration_month";
    public static final String COLUMN_CREDITCARD_EXPIRATION_YEAR = "f_credit_expiration_year";
    public static final String COLUMN_CREDITCARD_CVC = "f_credit_cvc";
    public static final String COLUMN_CREDITCARD_USER_ID = "f_credit_userid";

    //pdffile
    public static final String TABLE_PDFFILE = "tb_pdffile";
    public static final String COLUMN_PDFFILE_ID = "f_pdf_id";
    public static final String COLUMN_PDFFILE_STOREPATH = "f_pdf_storepath";
    public static final String COLUMN_PDFFILE_DISPLAYNAME = "f_pdf_displayname";
    public static final String COLUMN_PDFFILE_CATEGORYID = "f_pdf_categoryid";
    public static final String COLUMN_PDFFILE_SERVERID = "f_pdf_serverid";
    public static final String COLUMN_PDFFILE_CREDIT_USER_ID = "f_pdf_userid";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //create table user
        String sqlQuery = String.format("create table %s (%s integer primary key, %s text, %s text, %s integer)",
                TABLE_USER, COLUMN_USER_ID, COLUMN_USER_EMAIL, COLUMN_USER_PASSWORD, COLUMN_USER_SERVERID);
        db.execSQL(sqlQuery);
        //create table payment
        sqlQuery = String.format("create table %s (%s integer primary key, %s text, %s integer, %s integer, %s text, %s integer)",
                TABLE_CREDITCARD, COLUMN_CREDITCARD_ID, COLUMN_CREDITCARD_CARDNO, COLUMN_CREDITCARD_EXPIRATION_MONTH, COLUMN_CREDITCARD_EXPIRATION_YEAR, COLUMN_CREDITCARD_CVC, COLUMN_CREDITCARD_USER_ID);
        db.execSQL(sqlQuery);
        sqlQuery = String.format("create table %s (%s integer primary key, %s text, %s text, %s integer, %s text, %s integer)",
                TABLE_PDFFILE, COLUMN_PDFFILE_ID, COLUMN_PDFFILE_STOREPATH, COLUMN_PDFFILE_DISPLAYNAME, COLUMN_PDFFILE_CATEGORYID, COLUMN_PDFFILE_SERVERID, COLUMN_PDFFILE_CREDIT_USER_ID);
        db.execSQL(sqlQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(String.format("DROP TABLE IF EXISTS %s", TABLE_USER));
        db.execSQL(String.format("DROP TABLE IF EXISTS %s", TABLE_PDFFILE));
        onCreate(db);
    }

    ////////////////user////////////////////
//    public UserProfile getUserFromCursor() {
//        UserProfile dbUesr = GetUserFromCursor(getTotalUserDataCursor());
//        if(dbUesr==null) {
//            dbUesr = new UserProfile();
//            registerUser(null, null, -1L);
//        }
//        return dbUesr;
//    }

    public UserProfile getUserByEMailAndPwd(String email, String password) {
        UserProfile result = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_USER + " where " + COLUMN_USER_EMAIL + "=?" + " and " + COLUMN_USER_PASSWORD + "=?", new String[]{email, password});
        result = GetUserFromCursor(cursor);
        cursor.close();
        return result;
    }

    public UserProfile getUserByEMail(String email) {
        UserProfile result = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_USER + " where " + COLUMN_USER_EMAIL + "=?", new String[]{email});
        result = GetUserFromCursor(cursor);
        cursor.close();
        return result;
    }

    public UserProfile getUserByDbId(long dbid) {
        UserProfile result = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_USER + " where " + COLUMN_USER_ID + "=?", new String[]{String.valueOf(dbid)});
        result = GetUserFromCursor(cursor);
        cursor.close();
        return result;
    }

    public UserProfile registerUser(UserProfile user) {
        UserProfile userInfo = getUserByEMail(user.email);
        if(userInfo==null) {
            long dbId = InsertUser(user.email, user.password, user.id);
            user.dbId = dbId;
        }
        else {
            user.dbId = userInfo.dbId;
            UpdateUser(user.dbId, user.email, user.password, user.id);
        }
        return getUserByDbId(user.dbId);
    }

    private long InsertUser(String email, String password, Long serverId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = GetContentValuesFromUser(email, password, serverId);
        return (db.insert(TABLE_USER, null, contentValues));
    }

    private boolean UpdateUser(long id, String email, String password, Long serverId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = GetContentValuesFromUser(email, password, serverId);
        return (db.update(TABLE_USER, contentValues, COLUMN_USER_ID + "=? ", new String[]{String.valueOf(id)}) > 0);
    }

    public int getUserRowCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_USER);
        return numRows;
    }

    private static ContentValues GetContentValuesFromUser(String email, String password, long serverId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USER_EMAIL, email);
        contentValues.put(COLUMN_USER_PASSWORD, password);
        contentValues.put(COLUMN_USER_SERVERID, serverId);
        return contentValues;
    }

    private static List<UserProfile> GetUsersFromCursor(Cursor cursor) {
        List<UserProfile> resultList = new ArrayList<UserProfile>();
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            UserProfile result = new UserProfile();
            cursor.moveToFirst();
            result.dbId = cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID));
            result.email = cursor.getString(cursor.getColumnIndex(COLUMN_USER_EMAIL));
            result.password = cursor.getString(cursor.getColumnIndex(COLUMN_USER_PASSWORD));
            result.id = cursor.getLong(cursor.getColumnIndex(COLUMN_USER_SERVERID));
            resultList.add(result);
            cursor.moveToNext();
        }
        cursor.close();
        return resultList;
    }

    private static UserProfile GetUserFromCursor(Cursor cursor) {
        List<UserProfile> resultList = GetUsersFromCursor(cursor);
        if (resultList != null && resultList.size() == 1) {
            return resultList.get(0);
        }
        return null;
    }

    //credit card
    public CreditCardData registerCreditcard(CreditCardData card) {
        CreditCardData cardDB = getCreditcardDataByUserid(card.userid);
        if(cardDB ==null) {
            long dbId = InsertCreditcard(card.cardno, card.expmonth, card.expyear, card.cardcvc, card.userid);
            card.dbid = dbId;
        }
        else {
            card.dbid = cardDB.dbid;
            UpdateCreditcard(card.dbid, card.cardno, card.expmonth, card.expyear, card.cardcvc, card.userid);
        }
        return getCreditcardDataByDbid(card.dbid);
    }

    private long InsertCreditcard(String creditCardNumber, Integer expirationMonth, Integer expirationYear, String cvc, long userid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = GetContentValuesFromCreditcard(creditCardNumber, expirationMonth, expirationYear, cvc, userid);
        return db.insert(TABLE_CREDITCARD, null, contentValues);
    }

    private boolean UpdateCreditcard(long id, String creditCardNumber, Integer expirationMonth, Integer expirationYear, String cvc, long userid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = GetContentValuesFromCreditcard(creditCardNumber, expirationMonth, expirationYear, cvc, userid);
        return (db.update(TABLE_CREDITCARD, contentValues, COLUMN_CREDITCARD_ID + "=? ", new String[]{String.valueOf(id)}) > 0);
    }

    public CreditCardData getCreditcardDataByDbid(long dbid) {
        CreditCardData result = null;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_CREDITCARD + " where " + COLUMN_CREDITCARD_ID + "=?", new String[]{String.valueOf(dbid)});
        result = GetCreditcardFromCursor(cursor);
        cursor.close();
        return result;
    }

    public CreditCardData getCreditcardDataByUserid(long userid) {
        CreditCardData result = null;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_CREDITCARD + " where " + COLUMN_CREDITCARD_USER_ID + "=?", new String[]{String.valueOf(userid)});
        result = GetCreditcardFromCursor(cursor);
        cursor.close();
        return result;
    }

    private static ContentValues GetContentValuesFromCreditcard(String creditCardNo, Integer expirationMonth, Integer expirationYear, String cvc, long userid) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_CREDITCARD_CARDNO, creditCardNo);
        contentValues.put(COLUMN_CREDITCARD_EXPIRATION_MONTH, expirationMonth);
        contentValues.put(COLUMN_CREDITCARD_EXPIRATION_YEAR, expirationYear);
        contentValues.put(COLUMN_CREDITCARD_CVC, cvc);
        contentValues.put(COLUMN_CREDITCARD_USER_ID, userid);
        return contentValues;
    }

    private static CreditCardData GetCreditcardFromCursor(Cursor cursor) {
        CreditCardData result = null;
        cursor.moveToFirst();
        if (cursor.isAfterLast() == false) {
            long cc_dbid = cursor.getLong(cursor.getColumnIndex(COLUMN_CREDITCARD_ID));
            String cc_number = cursor.getString(cursor.getColumnIndex(COLUMN_CREDITCARD_CARDNO));
            int cc_expiration_month = cursor.getInt(cursor.getColumnIndex(COLUMN_CREDITCARD_EXPIRATION_MONTH));
            int cc_expiration_year = cursor.getInt(cursor.getColumnIndex(COLUMN_CREDITCARD_EXPIRATION_YEAR));
            String cc_cvc = cursor.getString(cursor.getColumnIndex(COLUMN_CREDITCARD_CVC));
            long userid = cursor.getLong(cursor.getColumnIndex(COLUMN_CREDITCARD_USER_ID));
            result = new CreditCardData(cc_dbid, cc_number, cc_expiration_month, cc_expiration_year, cc_cvc, userid);
        }
        return result;
    }

    /////////////pdf/////////////////
    public Pdfbook registerPdfFile(Pdfbook pdf, long userid) {
        //String storePath, String displayName, long categoryId, String serverId,
        Pdfbook pdfData = getPdffileByServeridAndUserid(pdf.id, userid);
        if (pdfData == null) {
            long dbid = insertPdfFile(pdf.filePath, pdf.displayName, pdf.categoryid, pdf.id, userid);
            pdf.dbid = dbid;
        } else {
            pdf.dbid = pdfData.dbid;
            updatePdfFile(pdf.dbid, pdf.filePath, pdf.displayName, pdf.categoryid, pdf.id, userid);
        }
        return getPdffileByDbid(pdf.dbid);
    }

    public long insertPdfFile(String storePath, String displayName, long categoryId, String serverId, long userid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = GetContentValuesFromPdfFile(storePath, displayName, categoryId, serverId, userid);
        return db.insert(TABLE_PDFFILE, null, contentValues);
    }

    public boolean updatePdfFile(long id, String storePath, String displayName, long categoryId, String serverId, long userid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = GetContentValuesFromPdfFile(storePath, displayName, categoryId, serverId, userid);
        return (db.update(TABLE_PDFFILE, contentValues, COLUMN_PDFFILE_ID + "=? ", new String[]{String.valueOf(id)}) > 0);
    }

    public Pdfbook getPdffileByServeridAndUserid(String serverId, long userId) {
        Pdfbook result = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_PDFFILE + " where " + COLUMN_PDFFILE_SERVERID + "=?" + " and " + COLUMN_PDFFILE_CREDIT_USER_ID + "=?", new String[]{String.valueOf(serverId), String.valueOf(userId)});
        List<Pdfbook> resultList = GetPdffileFromCursor(cursor);
        if (resultList != null && resultList.size() == 1) {
            result = resultList.get(0);
        }
        cursor.close();
        return result;
    }

    public Pdfbook getPdffileByDbid(long dbId) {
        Pdfbook result = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_PDFFILE + " where " + COLUMN_PDFFILE_ID + "=?", new String[]{String.valueOf(dbId)});
        List<Pdfbook> resultList = GetPdffileFromCursor(cursor);
        if (resultList != null && resultList.size() == 1) {
            result = resultList.get(0);
        }
        cursor.close();
        return result;
    }

    public List<Pdfbook> getPdffileByCategoryidAndUserid(long categoryId, long userId) {
        List<Pdfbook> result = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_PDFFILE + " where " + COLUMN_PDFFILE_CREDIT_USER_ID + "=?", new String[]{String.valueOf(userId)});
        result = GetPdffileFromCursor(cursor);
        cursor.close();
        return result;
    }

    public int getPdfFileRowCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_PDFFILE);
        return numRows;
    }

    public boolean deletePdffile(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return (db.delete(TABLE_PDFFILE, COLUMN_PDFFILE_ID + "=? ",
                new String[]{String.valueOf(id)}) > 0);
    }

    private static ContentValues GetContentValuesFromPdfFile(String storePath, String displayName, long categoryId, String serverId, long userid) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_PDFFILE_STOREPATH, storePath);
        contentValues.put(COLUMN_PDFFILE_DISPLAYNAME, displayName);
        contentValues.put(COLUMN_PDFFILE_CATEGORYID, categoryId);
        contentValues.put(COLUMN_PDFFILE_SERVERID, serverId);
        contentValues.put(COLUMN_PDFFILE_CREDIT_USER_ID, userid);
        return contentValues;
    }

    private static List<Pdfbook> GetPdffileFromCursor(Cursor cursor) {
        List<Pdfbook> result = new ArrayList<Pdfbook>();
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            Pdfbook data = new Pdfbook();
            data.dbid = cursor.getLong(cursor.getColumnIndex(COLUMN_PDFFILE_ID));
            data.filePath = cursor.getString(cursor.getColumnIndex(COLUMN_PDFFILE_STOREPATH));
            data.displayName = cursor.getString(cursor.getColumnIndex(COLUMN_PDFFILE_DISPLAYNAME));
            data.categoryid = cursor.getLong(cursor.getColumnIndex(COLUMN_PDFFILE_CATEGORYID));
            data.id = cursor.getString(cursor.getColumnIndex(COLUMN_PDFFILE_SERVERID));
            result.add(data);
            cursor.moveToNext();
        }
        cursor.close();
        return result;
    }

}