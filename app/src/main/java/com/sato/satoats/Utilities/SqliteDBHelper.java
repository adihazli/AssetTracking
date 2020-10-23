package com.sato.satoats.Utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.util.*;

public class SqliteDBHelper extends SQLiteOpenHelper {

    String strDBName;
    SQLiteDatabase db;
    String strpath;

    public SqliteDBHelper(Context context, String strDBName, String strpath) {
        super(context, strpath +"/"+ strDBName, null, 1);
        this.strDBName = strDBName;
        this.strpath = strpath;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean checkDatabase() {
        SQLiteDatabase checkDB = null;
        try {
            System.out.println("path db : " + strpath+"/"+strDBName);
            checkDB = SQLiteDatabase.openDatabase(strpath+"/"+strDBName, null, SQLiteDatabase.OPEN_READONLY);
            checkDB.close();
        } catch (SQLiteException e) {
            System.out.println("database doesn't exist yet");
        }
        return checkDB != null;
    }

    public void createDatabaseTable(String strTablename, String strTableColumnDetails) {
        String sqlStatement = "CREATE TABLE "+strTablename+" (" +strTableColumnDetails+")";
        db = this.getWritableDatabase();
        if (checkDatabase()) {
            if (!checkTableExistence(strTablename)) {
                db.execSQL(sqlStatement);
            } else {
                System.out.println("table already Created");
            }
        } else {
            db.execSQL(sqlStatement);
            System.out.println("Database Doesn't exist");
        }
        db.close();
    }

    public boolean checkTableExistence(String strTablename) {
        Boolean isAvailable = false;
        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        while (cursor.moveToNext()) {
            if (strTablename.equals(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(0)))) ) {
                isAvailable = true;
            }
        }
        return isAvailable;
    }

    public boolean insertTableData(String tableName, ContentValues cValues) {
        db = this.getWritableDatabase();
        boolean isSuccess = false;
        if (db.insert(tableName, null, cValues) != -1 )  {
            isSuccess = true;
        }
        db.close();
        return isSuccess;
    }

    public boolean insertTableDataTrans(String tableName, ArrayList arrContentValue) {
        db = this.getWritableDatabase();
        db.beginTransactionNonExclusive();
        boolean isSuccess = false;

        for (int i = 0; i < arrContentValue.size(); i++) {
            if (db.insert(tableName, null, (ContentValues) arrContentValue.get(i)) != -1 )  {
                isSuccess = true;
            } else { isSuccess = false; }
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        return isSuccess;
    }

    public boolean multiInsertTableData(String tableName, ContentValues cValues, ArrayList arrList) {
        db = this.getWritableDatabase();
        db.beginTransaction();
        boolean isSuccess = false;
        for (int i = 0; i < arrList.size(); i++) {
            if (db.insert(tableName, null, (ContentValues) arrList.get(i)) != -1 )  {
                isSuccess = true;
            }
        }

        if (isSuccess) {
            db.setTransactionSuccessful();
        }

        db.endTransaction();
        db.close();
        return false;
    }

    public int updateTableData(String tblName, ContentValues cValues , String whereClause, String[] whereArgs) {
        db = this.getWritableDatabase();
        int intResult = this.getWritableDatabase().update(tblName, cValues, whereClause, whereArgs);
        this.getWritableDatabase().close();
        return intResult;
    }

    public ArrayList getTableDataFromSqlite(String sqlStatement) {
        ArrayList arrayList = new ArrayList();
        db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlStatement, null);
        while (cursor.moveToNext()){
            HashMap<String, Object> thisHashMap = new HashMap();
            if (cursor.getColumnCount() > 0) {
                for (int cursorColumn = 0 ;cursorColumn < cursor.getColumnCount(); cursorColumn++) {
                    thisHashMap.put(cursor.getColumnNames()[cursorColumn], cursor.getString(cursorColumn));
                }
            }
            arrayList.add(thisHashMap);
        }
        db.close();
        cursor.close();
        return arrayList;
    }

    public int deleteTableData(String tblName, String whereClause, String[] whereArgs){
        int intResult = this.getWritableDatabase().delete(tblName, whereClause, whereArgs);
        this.getWritableDatabase().close();
        return intResult;
    }

    public int countTableData(String sqlStatement) {
        Cursor cursor = this.getReadableDatabase().rawQuery(sqlStatement, null);
        System.out.println("cursor.getCount() : " + cursor.getCount());
        int intCount = cursor.getCount();
        cursor.close();
        return  intCount;
    }

    public ArrayList sqlStatement(String sqlStatement) {
        ArrayList arrayList = new ArrayList();
        db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlStatement, null);
        while (cursor.moveToNext()){
            HashMap<String, Object> thisHashMap = new HashMap();
            if (cursor.getColumnCount() > 0) {
                for (int cursorColumn = 0 ;cursorColumn < cursor.getColumnCount(); cursorColumn++) {
                    thisHashMap.put(cursor.getColumnNames()[cursorColumn], cursor.getString(cursorColumn));
                }
            }
            arrayList.add(thisHashMap);
        }
        db.close();
        cursor.close();
        return arrayList;
    }

    public void insertUsingTransaction(String tblName, ArrayList arrHash) {
    //Create SqlStatment base on hashMap
        String forSqlColumns = "";
        String forSqlValues = "";

        ArrayList listOfKey = new ArrayList();
        int thisListPos = 0;

        Set<String> thisSet = new HashSet<>();
        if (arrHash.get(0) != null) {
            thisSet = ((HashMap)arrHash.get(0)).keySet();
            for (String strSet : thisSet) {
                listOfKey.add(thisListPos, strSet);
                forSqlColumns = forSqlColumns + strSet + ",";
                forSqlValues = forSqlValues + "?,";
                thisListPos = thisListPos + 1;
            }
        }

        forSqlColumns = "("+forSqlColumns.substring(0, forSqlColumns.length() - 1)+")";
        forSqlValues = "("+forSqlValues.substring(0, forSqlValues.length() - 1)+")";
        String myStrSql = "INSERT INTO " + tblName + forSqlColumns + " VALUES " + forSqlValues;
        System.out.println("myStrSql : " + myStrSql);

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransactionNonExclusive();

        SQLiteStatement stmt = db.compileStatement(myStrSql);
        //Populate looping data on stmnt binding
        System.out.println("Start");
        for (int i = 0; i < arrHash.size(); i++) {
            for (int x = 0; x < listOfKey.size(); x++) {
                int columnPos = x + 1;
                stmt.bindString(columnPos, String.valueOf(((HashMap)arrHash.get(i)).get(listOfKey.get(x))));
            }
            stmt.execute();
            stmt.clearBindings();
        }

        System.out.println("End");
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    public void insertUsingTransactionTesting(String tblName, LinkedHashMap hashMap) {
        //Create SqlStatment base on hashMap
        String forSqlColumns = "";
        String forSqlValues = "";

        ArrayList listOfKey = new ArrayList();
        int thisListPos = 0;
        ArrayList arrHash = new ArrayList();
        for (int loopItem = 0; loopItem < 6500; loopItem++) {
            HashMap hasData = new HashMap();

            hasData.put("LabelNo", "Trans" +loopItem); // - 1
            hasData.put("PONo", "PO1234");
            hasData.put("ReceiveDate", "\\/Date("+ String.valueOf(new Date().getTime())+")\\/");
            hasData.put("ItemCode", "12345"+loopItem);
            hasData.put("UOM", "Unit01");
            hasData.put("Quantity", 2.00);
            hasData.put("LotNo", "lot001");
            hasData.put("Notes", "thisNote");
            hasData.put("Flag", 0);
            hasData.put("ScanDate", "\\/Date("+ String.valueOf(new Date().getTime())+")\\/");
            hasData.put("ScanUID", "admin");
            hasData.put("ScannerID", "scanner001");
            hasData.put("LocationCode", "location001");
            hasData.put("SupplierDO", "scanner001");
            hasData.put("CDATE", "\\/Date("+ String.valueOf(new Date().getTime())+")\\/");

            arrHash.add(hasData);
        }

        Set<String> thisSet = new HashSet<>();
        if (arrHash.get(0) != null) {
            thisSet = ((HashMap)arrHash.get(0)).keySet();
            for (String strSet : thisSet) {
                listOfKey.add(thisListPos, strSet);
                forSqlColumns = forSqlColumns + strSet + ",";
                forSqlValues = forSqlValues + "?,";
                thisListPos = thisListPos + 1;
            }
        }

        forSqlColumns = "("+forSqlColumns.substring(0, forSqlColumns.length() - 1)+")";
        forSqlValues = "("+forSqlValues.substring(0, forSqlValues.length() - 1)+")";
        String myStrSql = "INSERT INTO " + tblName + forSqlColumns + " VALUES " + forSqlValues;
        System.out.println("myStrSql : " + myStrSql);

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransactionNonExclusive();

        SQLiteStatement stmt = db.compileStatement(myStrSql);
        //Populate looping data on stmnt binding
        System.out.println("Start");
        for (int i = 0; i < arrHash.size(); i++) {
            for (int x = 0; x < listOfKey.size(); x++) {
                int columnPos = x + 1;
                stmt.bindString(columnPos, String.valueOf(((HashMap)arrHash.get(i)).get(listOfKey.get(x))));
            }
            stmt.execute();
            stmt.clearBindings();
        }

        System.out.println("End");
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }



}
