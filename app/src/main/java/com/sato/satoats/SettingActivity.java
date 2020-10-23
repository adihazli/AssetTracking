package com.sato.satoats;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

import com.sato.satoats.Utilities.CheckConnection;
import com.sato.satoats.Utilities.CommonDBItem;
import com.sato.satoats.Utilities.CommonFunction;
import com.sato.satoats.Utilities.LogoutDialog;
import com.sato.satoats.Utilities.Message;
import com.sato.satoats.Utilities.MssqlConnection;
import com.sato.satoats.Utilities.SqliteDBHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class SettingActivity extends AppCompatActivity {

    Button testCon,testMail,saveMSSQL,saveEmail;
    EditText ipAddET,portET,dbNameET,userNameET,userPassET,deviceNameET;
    EditText ETemailAdd,ETemailPass,ETemailHost,ETemailPort,ETRecEmail;
    ImageView syncAudit,syncMSTData,syncLoan;
    SharedPreferences sharedpreferences;
    MssqlConnection conn;
    Switch EmailNotification,scanSetup;
    ArrayList<String> location = new ArrayList<>();
    ArrayList<String> costCenter = new ArrayList<>();
    ArrayList<String> employeeID = new ArrayList<>();
    ArrayList<String> purposeList = new ArrayList<>();


    String ipAddress,port,dbName,userName,userPass,deviceName,cameraScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Setting");

        conn = new MssqlConnection();
        sharedpreferences = getSharedPreferences(getString(R.string.MyPref),Context.MODE_PRIVATE);

        saveMSSQL = (Button)findViewById(R.id.SaveMSSQL);
        testCon = (Button)findViewById(R.id.TestConn);

        deviceNameET = findViewById(R.id.deviceName);
        ipAddET = findViewById(R.id.ipAddET);
        portET = findViewById(R.id.portET);
        dbNameET = findViewById(R.id.dbNameET);
        userNameET = findViewById(R.id.userNameET);
        userPassET = findViewById(R.id.userPassET);
        syncAudit = findViewById(R.id.syncAssetAudit);
        syncMSTData = findViewById(R.id.syncMSTData);
        syncLoan = findViewById(R.id.syncItemLoan);

        deviceName = sharedpreferences.getString(getString(R.string.DeviceName), "");
        ipAddress = sharedpreferences.getString(getString(R.string.IpAddress), "");
        port = sharedpreferences.getString(getString(R.string.ServerPort), "");
        dbName = sharedpreferences.getString(getString(R.string.DatabaseName), "");
        userName = sharedpreferences.getString(getString(R.string.DBuserName), "");
        userPass = sharedpreferences.getString(getString(R.string.DBpassword), "");

        deviceNameET.setText(deviceName);
        ipAddET.setText(ipAddress);
        portET.setText(port);
        dbNameET.setText(dbName);
        userNameET.setText(userName);
        userPassET.setText(userPass);

        saveMSSQLData();
        testConnection();
        syncAuditData();
        syncMaster();
        syncLoanData();

    }

    public void testConnection(){
        testCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ipAddress = ipAddET.getText().toString().trim();
                port = portET.getText().toString().trim();
                dbName = dbNameET.getText().toString().trim();
                userName = userNameET.getText().toString().trim();
                userPass = userPassET.getText().toString().trim();

                AsyncTestCon check = new AsyncTestCon( SettingActivity.this, new MyInterface() {
                    @Override
                    public void myMethod(boolean result) {
                        if (result == true) {
                            Message.message(SettingActivity.this,"Connection to server Successful");

                        } else {
                            Message.message(SettingActivity.this,"Can't connect to server");
                        }
                    }
                });
                check.execute();

            }
        });
    }

    public interface MyInterface {
        public void myMethod(boolean result);
    }

    public void saveMSSQLData(){
        saveMSSQL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deviceName = deviceNameET.getText().toString().trim();
                ipAddress = ipAddET.getText().toString().trim();
                port = portET.getText().toString().trim();
                dbName = dbNameET.getText().toString().trim();
                userName = userNameET.getText().toString().trim();
                userPass = userPassET.getText().toString().trim();

                GetAppData getData = new GetAppData();
                getData.execute("");

//                Store Data in SharedPreferences.
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(getString(R.string.DeviceName), deviceName);
                editor.putString(getString(R.string.IpAddress), ipAddress);
                editor.putString(getString(R.string.ServerPort), port);
                editor.putString(getString(R.string.DatabaseName), dbName);
                editor.putString(getString(R.string.DBuserName), userName);
                editor.putString(getString(R.string.DBpassword),userPass);
                editor.commit();

                Message.message(SettingActivity.this,"Configuration data saved.");

            }
        });
    }

    public void syncAuditData(){
        syncAudit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SyncAuditItem syncAuditItem = new SyncAuditItem();
                syncAuditItem.execute("");

            }
        });
    }

    public void syncLoanData(){
        syncLoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SyncLoanItem syncLoanItem = new SyncLoanItem();
                syncLoanItem.execute("");
            }
        });
    }

    public void syncMaster(){
        syncMSTData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SyncMasterData syncMasterData = new SyncMasterData();
                syncMasterData.execute("");

            }
        });
    }

    public void scanSetupToggle(){
        scanSetup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cameraScan = "1";
                   // SharedPreferences.Editor editor = sharedpreferences.edit();
                   // editor.putString(getString(R.string.Camera), cameraScan);
                  //  editor.commit();
                }
                else {
                    cameraScan = "0";
                   // SharedPreferences.Editor editor = sharedpreferences.edit();
                   // editor.putString(getString(R.string.Camera), cameraScan);
                   // editor.commit();
                }

            }
        });
    }

    public void dropSqliteData(String strTableName) {
        SqliteDBHelper sqliteDBHelper = new SqliteDBHelper(SettingActivity.this,getString(R.string.appdb_name), getFilesDir().getPath());
        sqliteDBHelper.sqlStatement("DROP TABLE IF EXISTS "+strTableName);
    }

    public void createTable(String strTableName, String tableColumnName) {
        SqliteDBHelper sqliteDBHelper = new SqliteDBHelper(SettingActivity.this,getString(R.string.appdb_name), getFilesDir().getPath());
        sqliteDBHelper.createDatabaseTable(strTableName, tableColumnName);
    }

    public void addDataIntoTableTran(String tableName, ArrayList thisList) {
        SqliteDBHelper sqliteDBHelper = new SqliteDBHelper(SettingActivity.this,getString(R.string.appdb_name), getFilesDir().getPath());
        sqliteDBHelper.insertTableDataTrans(tableName , thisList);
    }

    public class GetAppData extends AsyncTask<String,String,String> {
        String z = "";
        Boolean isSuccess = false;
        private String result;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(SettingActivity.this,
                    "Loading",
                    "Please wait..");
        }

        @Override
        protected String doInBackground(String... params) {

            getUserPermissionData(CommonDBItem.SQLiteUserPermission);
            getUserData(CommonDBItem.SQLiteUserData);

            return result;
        }

        @Override
        protected void onPostExecute(String r)
        {
            progressDialog.dismiss();
        }
    }

    public void getUserData(String tableName) {
        String tableColumnName = "";
        String TableColumnType = "";
        String userPermission = "select * from " + CommonDBItem.UserData;
        ArrayList<Object> dataUser = conn.getDBData(userPermission, userName, userPass, port, dbName, ipAddress);
        if (dataUser.size() > 0) {

            List<String> tkey = new ArrayList<>(((HashMap) dataUser.get(0)).keySet());
            for (String strTemp : tkey) {
                tableColumnName = strTemp + "," + tableColumnName;
            }
           // System.out.println(tableColumnName.substring(0, tableColumnName.length() - 1));
            dropSqliteData(tableName);

            if (!tableColumnName.trim().isEmpty()) {
                createTable(tableName, tableColumnName.substring(0, tableColumnName.length() - 1));
            }

            ArrayList<ContentValues> contentValues = new ArrayList<>();
            for (int x = 0; x < dataUser.size(); x++) {
                System.out.println(dataUser.get(x));
                ContentValues thisContent = new ContentValues();
                Iterator<String> keys = tkey.iterator();

                while (keys.hasNext()) {
                    String key = keys.next();
                    if (((HashMap) dataUser.get(x)).get(key) instanceof String) {
                        TableColumnType = "Text";
                        thisContent.put(key, String.valueOf(((HashMap) dataUser.get(x)).get(key)).trim());
                    } else if (((HashMap) dataUser.get(x)).get(key) instanceof Integer) {
                        TableColumnType = "Integer";
                        thisContent.put(key, (Integer) (((HashMap) dataUser.get(x)).get(key)));
                    } else if (((HashMap) dataUser.get(x)).get(key) instanceof Double) {
                        TableColumnType = "Double";
                        thisContent.put(key, (Double) (((HashMap) dataUser.get(x))).get(key));
                    }
                    if (x == 0) {
                        tableColumnName = tableColumnName + key + " " + TableColumnType + ",";
                    }
                }
                contentValues.add(thisContent);

            }

            if (contentValues.size() > 0) {
//                                addWebServiceDataIntoTable(ConstWebService.BFVW000MSTUserJSON, contentValues);
                addDataIntoTableTran(tableName, contentValues);
            }
        }
    }

    public void getUserPermissionData(String tableName) {
        String tableColumnName = "";
        String TableColumnType = "";
        String userPermission = "select * from " + CommonDBItem.UserPermission + " where (APPCD >900) and (APPCD <999)";
        ArrayList<Object> dataPermission = conn.getDBData(userPermission, userName, userPass, port, dbName, ipAddress);
        if (dataPermission.size() > 0) {

            List<String> tkey = new ArrayList<>(((HashMap) dataPermission.get(0)).keySet());
            for (String strTemp : tkey) {
                tableColumnName = strTemp + "," + tableColumnName;
            }
            System.out.println(tableColumnName.substring(0, tableColumnName.length() - 1));
            dropSqliteData(tableName);

            if (!tableColumnName.trim().isEmpty()) {
                createTable(tableName, tableColumnName.substring(0, tableColumnName.length() - 1));
            }

            ArrayList<ContentValues> contentValues = new ArrayList<>();
            for (int x = 0; x < dataPermission.size(); x++) {
                System.out.println(dataPermission.get(x));
                ContentValues thisContent = new ContentValues();
                Iterator<String> keys = tkey.iterator();

                while (keys.hasNext()) {
                    String key = keys.next();
                    if (((HashMap) dataPermission.get(x)).get(key) instanceof String) {
                        TableColumnType = "Text";
                        thisContent.put(key, String.valueOf(((HashMap) dataPermission.get(x)).get(key)).trim());
                    } else if (((HashMap) dataPermission.get(x)).get(key) instanceof Integer) {
                        TableColumnType = "Integer";
                        thisContent.put(key, (Integer) (((HashMap) dataPermission.get(x)).get(key)));
                    } else if (((HashMap) dataPermission.get(x)).get(key) instanceof Double) {
                        TableColumnType = "Double";
                        thisContent.put(key, (Double) (((HashMap) dataPermission.get(x))).get(key));
                    }
                    if (x == 0) {
                        tableColumnName = tableColumnName + key + " " + TableColumnType + ",";
                    }
                }
                contentValues.add(thisContent);

            }

            if (contentValues.size() > 0) {
//                                addWebServiceDataIntoTable(ConstWebService.BFVW000MSTUserJSON, contentValues);
                addDataIntoTableTran(tableName, contentValues);
            }
        }
    }

    public class SyncMasterData extends AsyncTask<String,String,String> {
        String z = "";
        Boolean isSuccess = false;
        private String result;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(SettingActivity.this,
                    "Loading",
                    "Please wait..");
        }

        @Override
        protected String doInBackground(String... params) {

            getMSTItem(CommonDBItem.SQLiteMSTItem);
            getMSTLocation(CommonDBItem.SQLiteMSTLocation);
            getMSTCostCentre(CommonDBItem.SQLiteMSTCosCentre);

            return result;
        }

        @Override
        protected void onPostExecute(String r)
        {

            progressDialog.dismiss();
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(SettingActivity.this);
            alertDialog.setTitle("Sync Database");
            alertDialog.setMessage("Successfully Sync Data");
            alertDialog.setPositiveButton("OK", null);
            alertDialog.create().show();

        }
    }

    public void getMSTItem(String tableName) {
        String tableColumnName = "";
        String TableColumnType = "";
        String userPermission = "select * from " + CommonDBItem.MSTItem;
        ArrayList<Object> dataItem = conn.getDBData(userPermission, userName, userPass, port, dbName, ipAddress);
        if (dataItem.size() > 0) {

            List<String> tkey = new ArrayList<>(((HashMap) dataItem.get(0)).keySet());
            for (String strTemp : tkey) {
                tableColumnName = strTemp + "," + tableColumnName;
            }
            // System.out.println(tableColumnName.substring(0, tableColumnName.length() - 1));
            dropSqliteData(tableName);

            if (!tableColumnName.trim().isEmpty()) {
                createTable(tableName, tableColumnName.substring(0, tableColumnName.length() - 1));
            }

            ArrayList<ContentValues> contentValues = new ArrayList<>();
            for (int x = 0; x < dataItem.size(); x++) {
                System.out.println(dataItem.get(x));
                ContentValues thisContent = new ContentValues();
                Iterator<String> keys = tkey.iterator();

                while (keys.hasNext()) {
                    String key = keys.next();
                    if (((HashMap) dataItem.get(x)).get(key) instanceof String) {
                        TableColumnType = "Text";
                        thisContent.put(key, String.valueOf(((HashMap) dataItem.get(x)).get(key)).trim());
                    } else if (((HashMap) dataItem.get(x)).get(key) instanceof Integer) {
                        TableColumnType = "Integer";
                        thisContent.put(key, (Integer) (((HashMap) dataItem.get(x)).get(key)));
                    } else if (((HashMap) dataItem.get(x)).get(key) instanceof Double) {
                        TableColumnType = "Double";
                        thisContent.put(key, (Double) (((HashMap) dataItem.get(x))).get(key));
                    }
                    if (x == 0) {
                        tableColumnName = tableColumnName + key + " " + TableColumnType + ",";
                    }
                }
                contentValues.add(thisContent);

            }

            if (contentValues.size() > 0) {
//                                addWebServiceDataIntoTable(ConstWebService.BFVW000MSTUserJSON, contentValues);
                addDataIntoTableTran(tableName, contentValues);
            }
        }
    }

    public void getMSTLocation(String tableName) {
        String tableColumnName = "";
        String TableColumnType = "";
        String userPermission = "select * from " + CommonDBItem.MSTLocation;
        ArrayList<Object> dataItem = conn.getDBData(userPermission, userName, userPass, port, dbName, ipAddress);
        if (dataItem.size() > 0) {

            List<String> tkey = new ArrayList<>(((HashMap) dataItem.get(0)).keySet());
            for (String strTemp : tkey) {
                tableColumnName = strTemp + "," + tableColumnName;
            }
            // System.out.println(tableColumnName.substring(0, tableColumnName.length() - 1));
            dropSqliteData(tableName);

            if (!tableColumnName.trim().isEmpty()) {
                createTable(tableName, tableColumnName.substring(0, tableColumnName.length() - 1));
            }

            ArrayList<ContentValues> contentValues = new ArrayList<>();
            for (int x = 0; x < dataItem.size(); x++) {
                System.out.println(dataItem.get(x));
                ContentValues thisContent = new ContentValues();
                Iterator<String> keys = tkey.iterator();

                while (keys.hasNext()) {
                    String key = keys.next();
                    if (((HashMap) dataItem.get(x)).get(key) instanceof String) {
                        TableColumnType = "Text";
                        thisContent.put(key, String.valueOf(((HashMap) dataItem.get(x)).get(key)).trim());
                    } else if (((HashMap) dataItem.get(x)).get(key) instanceof Integer) {
                        TableColumnType = "Integer";
                        thisContent.put(key, (Integer) (((HashMap) dataItem.get(x)).get(key)));
                    } else if (((HashMap) dataItem.get(x)).get(key) instanceof Double) {
                        TableColumnType = "Double";
                        thisContent.put(key, (Double) (((HashMap) dataItem.get(x))).get(key));
                    }
                    if (x == 0) {
                        tableColumnName = tableColumnName + key + " " + TableColumnType + ",";
                    }
                }
                contentValues.add(thisContent);

            }

            if (contentValues.size() > 0) {
//                                addWebServiceDataIntoTable(ConstWebService.BFVW000MSTUserJSON, contentValues);
                addDataIntoTableTran(tableName, contentValues);
            }
        }
    }

    public void getMSTCostCentre(String tableName) {
        String tableColumnName = "";
        String TableColumnType = "";
        String userPermission = "select * from " + CommonDBItem.MSTCostCentre;
        ArrayList<Object> dataItem = conn.getDBData(userPermission, userName, userPass, port, dbName, ipAddress);
        if (dataItem.size() > 0) {

            List<String> tkey = new ArrayList<>(((HashMap) dataItem.get(0)).keySet());
            for (String strTemp : tkey) {
                tableColumnName = strTemp + "," + tableColumnName;
            }
            // System.out.println(tableColumnName.substring(0, tableColumnName.length() - 1));
            dropSqliteData(tableName);

            if (!tableColumnName.trim().isEmpty()) {
                createTable(tableName, tableColumnName.substring(0, tableColumnName.length() - 1));
            }

            ArrayList<ContentValues> contentValues = new ArrayList<>();
            for (int x = 0; x < dataItem.size(); x++) {
                System.out.println(dataItem.get(x));
                ContentValues thisContent = new ContentValues();
                Iterator<String> keys = tkey.iterator();

                while (keys.hasNext()) {
                    String key = keys.next();
                    if (((HashMap) dataItem.get(x)).get(key) instanceof String) {
                        TableColumnType = "Text";
                        thisContent.put(key, String.valueOf(((HashMap) dataItem.get(x)).get(key)).trim());
                    } else if (((HashMap) dataItem.get(x)).get(key) instanceof Integer) {
                        TableColumnType = "Integer";
                        thisContent.put(key, (Integer) (((HashMap) dataItem.get(x)).get(key)));
                    } else if (((HashMap) dataItem.get(x)).get(key) instanceof Double) {
                        TableColumnType = "Double";
                        thisContent.put(key, (Double) (((HashMap) dataItem.get(x))).get(key));
                    }
                    if (x == 0) {
                        tableColumnName = tableColumnName + key + " " + TableColumnType + ",";
                    }
                }
                contentValues.add(thisContent);

            }

            if (contentValues.size() > 0) {
//                                addWebServiceDataIntoTable(ConstWebService.BFVW000MSTUserJSON, contentValues);
                addDataIntoTableTran(tableName, contentValues);
            }
        }
    }

    public class SyncAuditItem extends AsyncTask<String,String,String> {
        String z = "";
        Boolean isSuccess = false;
        private String result;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(SettingActivity.this,
                    "Loading",
                    "Please wait..");
        }

        @Override
        protected String doInBackground(String... params) {

            getAuditDTL(CommonDBItem.SQLiteAuditDTl);
            getAuditHR(CommonDBItem.SQLiteAuditHr);

            return result;
        }

        @Override
        protected void onPostExecute(String r)
        {

            progressDialog.dismiss();
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(SettingActivity.this);
            alertDialog.setTitle("Sync Database");
            alertDialog.setMessage("Successfully Sync Data");
            alertDialog.setPositiveButton("OK", null);
            alertDialog.create().show();

        }
    }

    public void getAuditDTL(String tableName) {
        String tableColumnName = "";
        String TableColumnType = "";
        String userPermission = "select * from " + CommonDBItem.AuditDtl;
        ArrayList<Object> dataItem = conn.getDBData(userPermission, userName, userPass, port, dbName, ipAddress);
        if (dataItem.size() > 0) {

            List<String> tkey = new ArrayList<>(((HashMap) dataItem.get(0)).keySet());
            for (String strTemp : tkey) {
                tableColumnName = strTemp + "," + tableColumnName;
            }
            // System.out.println(tableColumnName.substring(0, tableColumnName.length() - 1));
            dropSqliteData(tableName);

            if (!tableColumnName.trim().isEmpty()) {
                createTable(tableName, tableColumnName.substring(0, tableColumnName.length() - 1));
            }

            ArrayList<ContentValues> contentValues = new ArrayList<>();
            for (int x = 0; x < dataItem.size(); x++) {
                System.out.println(dataItem.get(x));
                ContentValues thisContent = new ContentValues();
                Iterator<String> keys = tkey.iterator();

                while (keys.hasNext()) {
                    String key = keys.next();
                    if (((HashMap) dataItem.get(x)).get(key) instanceof String) {
                        TableColumnType = "Text";
                        thisContent.put(key, String.valueOf(((HashMap) dataItem.get(x)).get(key)).trim());
                    } else if (((HashMap) dataItem.get(x)).get(key) instanceof Integer) {
                        TableColumnType = "Integer";
                        thisContent.put(key, (Integer) (((HashMap) dataItem.get(x)).get(key)));
                    } else if (((HashMap) dataItem.get(x)).get(key) instanceof Double) {
                        TableColumnType = "Double";
                        thisContent.put(key, (Double) (((HashMap) dataItem.get(x))).get(key));
                    }
                    if (x == 0) {
                        tableColumnName = tableColumnName + key + " " + TableColumnType + ",";
                    }
                }
                contentValues.add(thisContent);

            }

            if (contentValues.size() > 0) {
//                                addWebServiceDataIntoTable(ConstWebService.BFVW000MSTUserJSON, contentValues);
                addDataIntoTableTran(tableName, contentValues);
            }
        }
    }

    public void getAuditHR(String tableName) {
        String tableColumnName = "";
        String TableColumnType = "";
        String userPermission = "select * from " + CommonDBItem.AuditHr;
        ArrayList<Object> dataItem = conn.getDBData(userPermission, userName, userPass, port, dbName, ipAddress);
        if (dataItem.size() > 0) {

            List<String> tkey = new ArrayList<>(((HashMap) dataItem.get(0)).keySet());
            for (String strTemp : tkey) {
                tableColumnName = strTemp + "," + tableColumnName;
            }
            // System.out.println(tableColumnName.substring(0, tableColumnName.length() - 1));
            dropSqliteData(tableName);

            if (!tableColumnName.trim().isEmpty()) {
                createTable(tableName, tableColumnName.substring(0, tableColumnName.length() - 1));
            }

            ArrayList<ContentValues> contentValues = new ArrayList<>();
            for (int x = 0; x < dataItem.size(); x++) {
                System.out.println(dataItem.get(x));
                ContentValues thisContent = new ContentValues();
                Iterator<String> keys = tkey.iterator();

                while (keys.hasNext()) {
                    String key = keys.next();
                    if (((HashMap) dataItem.get(x)).get(key) instanceof String) {
                        TableColumnType = "Text";
                        thisContent.put(key, String.valueOf(((HashMap) dataItem.get(x)).get(key)).trim());
                    } else if (((HashMap) dataItem.get(x)).get(key) instanceof Integer) {
                        TableColumnType = "Integer";
                        thisContent.put(key, (Integer) (((HashMap) dataItem.get(x)).get(key)));
                    } else if (((HashMap) dataItem.get(x)).get(key) instanceof Double) {
                        TableColumnType = "Double";
                        thisContent.put(key, (Double) (((HashMap) dataItem.get(x))).get(key));
                    }
                    if (x == 0) {
                        tableColumnName = tableColumnName + key + " " + TableColumnType + ",";
                    }
                }
                contentValues.add(thisContent);

            }

            if (contentValues.size() > 0) {
//                                addWebServiceDataIntoTable(ConstWebService.BFVW000MSTUserJSON, contentValues);
                addDataIntoTableTran(tableName, contentValues);
            }
        }
    }

    public class SyncLoanItem extends AsyncTask<String,String,String> {
        String z = "";
        Boolean isSuccess = false;
        private String result;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(SettingActivity.this,
                    "Loading",
                    "Please wait..");
        }

        @Override
        protected String doInBackground(String... params) {

            getLoanDTL(CommonDBItem.SQLiteLoanItemDtl);
            getLoanHR(CommonDBItem.SQLiteLoanItemHr);

            return result;
        }

        @Override
        protected void onPostExecute(String r)
        {

            progressDialog.dismiss();
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(SettingActivity.this);
            alertDialog.setTitle("Sync Database");
            alertDialog.setMessage("Successfully Sync Data");
            alertDialog.setPositiveButton("OK", null);
            alertDialog.create().show();

        }
    }

    public void getLoanHR(String tableName) {
        String tableColumnName = "";
        String TableColumnType = "";
        String userPermission = "select * from " + CommonDBItem.LoanHr;
        ArrayList<Object> dataItem = conn.getDBData(userPermission, userName, userPass, port, dbName, ipAddress);
        if (dataItem.size() > 0) {

            List<String> tkey = new ArrayList<>(((HashMap) dataItem.get(0)).keySet());
            for (String strTemp : tkey) {
                tableColumnName = strTemp + "," + tableColumnName;
            }
            // System.out.println(tableColumnName.substring(0, tableColumnName.length() - 1));
            dropSqliteData(tableName);

            if (!tableColumnName.trim().isEmpty()) {
                createTable(tableName, tableColumnName.substring(0, tableColumnName.length() - 1));
            }

            ArrayList<ContentValues> contentValues = new ArrayList<>();
            for (int x = 0; x < dataItem.size(); x++) {
                System.out.println(dataItem.get(x));
                ContentValues thisContent = new ContentValues();
                Iterator<String> keys = tkey.iterator();

                while (keys.hasNext()) {
                    String key = keys.next();
                    if (((HashMap) dataItem.get(x)).get(key) instanceof String) {
                        TableColumnType = "Text";
                        thisContent.put(key, String.valueOf(((HashMap) dataItem.get(x)).get(key)).trim());
                    } else if (((HashMap) dataItem.get(x)).get(key) instanceof Integer) {
                        TableColumnType = "Integer";
                        thisContent.put(key, (Integer) (((HashMap) dataItem.get(x)).get(key)));
                    } else if (((HashMap) dataItem.get(x)).get(key) instanceof Double) {
                        TableColumnType = "Double";
                        thisContent.put(key, (Double) (((HashMap) dataItem.get(x))).get(key));
                    }
                    if (x == 0) {
                        tableColumnName = tableColumnName + key + " " + TableColumnType + ",";
                    }
                }
                contentValues.add(thisContent);

            }

            if (contentValues.size() > 0) {
//                                addWebServiceDataIntoTable(ConstWebService.BFVW000MSTUserJSON, contentValues);
                addDataIntoTableTran(tableName, contentValues);
            }
        }
    }

    public void getLoanDTL(String tableName) {
        String tableColumnName = "";
        String TableColumnType = "";
        String userPermission = "select * from " + CommonDBItem.LoanDtl;
        ArrayList<Object> dataItem = conn.getDBData(userPermission, userName, userPass, port, dbName, ipAddress);
        if (dataItem.size() > 0) {

            List<String> tkey = new ArrayList<>(((HashMap) dataItem.get(0)).keySet());
            for (String strTemp : tkey) {
                tableColumnName = strTemp + "," + tableColumnName;
            }
            // System.out.println(tableColumnName.substring(0, tableColumnName.length() - 1));
            dropSqliteData(tableName);

            if (!tableColumnName.trim().isEmpty()) {
                createTable(tableName, tableColumnName.substring(0, tableColumnName.length() - 1));
            }

            ArrayList<ContentValues> contentValues = new ArrayList<>();
            for (int x = 0; x < dataItem.size(); x++) {
                System.out.println(dataItem.get(x));
                ContentValues thisContent = new ContentValues();
                Iterator<String> keys = tkey.iterator();

                while (keys.hasNext()) {
                    String key = keys.next();
                    if (((HashMap) dataItem.get(x)).get(key) instanceof String) {
                        TableColumnType = "Text";
                        thisContent.put(key, String.valueOf(((HashMap) dataItem.get(x)).get(key)).trim());
                    } else if (((HashMap) dataItem.get(x)).get(key) instanceof Integer) {
                        TableColumnType = "Integer";
                        thisContent.put(key, (Integer) (((HashMap) dataItem.get(x)).get(key)));
                    } else if (((HashMap) dataItem.get(x)).get(key) instanceof Double) {
                        TableColumnType = "Double";
                        thisContent.put(key, (Double) (((HashMap) dataItem.get(x))).get(key));
                    }
                    if (x == 0) {
                        tableColumnName = tableColumnName + key + " " + TableColumnType + ",";
                    }
                }
                contentValues.add(thisContent);

            }

            if (contentValues.size() > 0) {
//                                addWebServiceDataIntoTable(ConstWebService.BFVW000MSTUserJSON, contentValues);
                addDataIntoTableTran(tableName, contentValues);
            }
        }
    }

    private class AsyncTestCon extends AsyncTask<Void, Void, Boolean> {

        private Context mContext;
        private MyInterface mListener;
        ProgressDialog progressDialog;

        public  AsyncTestCon(Context context, MyInterface mListener) {

            mContext = context;
            this.mListener  = mListener;

        }

        @Override
        protected Boolean doInBackground(Void... Param) {
            CheckConnection checkConnection = new CheckConnection();
            //boolean online = checkConnection.TestConnection(userName,userPass,port,dbName,ipAddress);
            boolean online = checkConnection.checkServer(userName,userPass,port,dbName,ipAddress);

            return online;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            // execution of result of Long time consuming operation
            progressDialog.dismiss();
            if (mListener != null)
                mListener.myMethod(result);

        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(SettingActivity.this,
                    "Checking Server connection",
                    "Connecting to server...");
        }

    }

    // hide the soft keyboard when touch other place on screen
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(SettingActivity.this, MainActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
