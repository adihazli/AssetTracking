package com.sato.satoats;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.azizanbusri.mainmenugrid.GridViewAdapter.GridViewAdapter;
import com.example.azizanbusri.mainmenugrid.GridViewModel.GridViewModel;
import com.example.azizanbusri.mainmenugrid.GridViewModel.GridViewTextView;
import com.sato.satoats.Audit.AuditActivity;
import com.sato.satoats.Audit.ViewAuditActivity;
import com.sato.satoats.Loan.ScanLoanItemActivity;
import com.sato.satoats.Loan.ScanReturnLoanActivity;
import com.sato.satoats.Loan.ViewLoanActivity;
import com.sato.satoats.Loan.ViewReturnLoanActivity;
import com.sato.satoats.Transfer.LocationTransferActivity;
import com.sato.satoats.Transfer.ViewTransferActivity;
import com.sato.satoats.Utilities.CheckConnection;
import com.sato.satoats.Utilities.CommonDBItem;
import com.sato.satoats.Utilities.LogoutDialog;
import com.sato.satoats.Utilities.Message;
import com.sato.satoats.Utilities.MssqlConnection;
import com.sato.satoats.Utilities.SqliteDBHelper;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MenuActivity extends AppCompatActivity {

    GridView gridViewMenu;
    SharedPreferences sharedPreferences;
    String user,userName,userPass,port,dbName,ipAddress;
    List<GridViewModel> mList = new ArrayList<>();
    List<GridViewTextView> mListTxt = new ArrayList<>();
    GridView mGridView;

    ArrayList<String> arrString = new ArrayList<>();
    ArrayList<Drawable> arrImage;

    MssqlConnection conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Asset Tracking System");

        mGridView = findViewById(R.id.gridViewMenu);

        sharedPreferences = getSharedPreferences(getString(R.string.MyPref), Context.MODE_PRIVATE);
        user = sharedPreferences.getString(getString(R.string.userID), "");
        userName = sharedPreferences.getString(getString(R.string.DBuserName), "");
        userPass = sharedPreferences.getString(getString(R.string.DBpassword), "");
        port = sharedPreferences.getString(getString(R.string.ServerPort), "");
        dbName = sharedPreferences.getString(getString(R.string.DatabaseName), "");
        ipAddress = sharedPreferences.getString(getString(R.string.IpAddress), "");

        conn = new MssqlConnection();

        setViewForUser();
        printMGrid();

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (mList.get(position).getGridViewTitle()) {
                    case "Asset Audit" :
                        startActivity(new Intent(MenuActivity.this, AuditActivity.class));
                        break;
                    case "View Audit" :
                        startActivity(new Intent(MenuActivity.this, ViewAuditActivity.class));
                        break;
                    case "Location Transfer" :
                        startActivity(new Intent(MenuActivity.this, LocationTransferActivity.class));
                        break;
                    case "View Transfer":
                        startActivity(new Intent(MenuActivity.this, ViewTransferActivity.class));
                        break;
                    case "Loan Item":
                        startActivity(new Intent(MenuActivity.this, ScanLoanItemActivity.class));
                        break;
                    case "Return Item":
                        startActivity(new Intent(MenuActivity.this, ScanReturnLoanActivity.class));
                        break;
                    case "View Loan Item":
                        startActivity(new Intent(MenuActivity.this, ViewLoanActivity.class));
                        break;
                    case "View Return Item":
                        startActivity(new Intent(MenuActivity.this, ViewReturnLoanActivity.class));
                        break;

                    case "Upload":

                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MenuActivity.this);
                        alertDialog.setIcon(MenuActivity.this.getResources().getDrawable(R.drawable.icon_upload, MenuActivity.this.getTheme()));
                        alertDialog.setTitle("Upload data to Cloud");
                        alertDialog.setMessage("Are you sure your want to upload all data");
                        alertDialog.setNegativeButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                doUpload();
                                System.out.println("Upload Data");
                            }
                        });
                        alertDialog.setPositiveButton("Cancel", null);
                        alertDialog.setNeutralButton("Clear data", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(MenuActivity.this);
                                alertDialog2.setIcon(MenuActivity.this.getResources().getDrawable(R.drawable.ico_warn, MenuActivity.this.getTheme()));
                                alertDialog2.setTitle("Delete Data");
                                alertDialog2.setMessage("Are you sure your want to delete sync to cloud data");
                                alertDialog2.setNegativeButton("Confirm", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        sqliteDeleteData(CommonDBItem.SQLiteUploadAuditDTl);
                                        sqliteDeleteData(CommonDBItem.SQLiteUploadTransfer);
                                        System.out.println("Clear Data");
                                        setViewForUser();
                                        printMGrid();
                                    }
                                });
                                alertDialog2.setPositiveButton("Cancel", null);
                                alertDialog2.create().show();                           }
                        });
                        alertDialog.create().show();


                    default:
                        break;
                }
            }
        });
        doSqliteDB();

    }

    public void printMGrid() {
        mList = new ArrayList<>();

        for (int i = 0; i < arrString.size(); i++) {
            GridViewModel gridViewModel = new GridViewModel(arrImage.get(i), arrString.get(i));
            GridViewTextView gridViewTextView = new GridViewTextView("#191970", 14);
            mList.add(gridViewModel);
            mListTxt.add(gridViewTextView);
        }

        GridViewAdapter mGridViewAdapter = new GridViewAdapter(getApplicationContext(), mList, mListTxt);
        mGridView.setAdapter(mGridViewAdapter);
    }

    public void setViewForUser() {

        ArrayList arrayListUserModule = getSqliteUserModuleData(user);
        HashMap mappingHashUserModule = new HashMap<>();
        arrString = new ArrayList<>();
        arrImage = new ArrayList<>();

        if (arrayListUserModule != null) {
           // System.out.println("UserModule :" + arrayListUserModule.size());

            for (int  i = 0; i < arrayListUserModule.size(); i++) {

                arrString.add(String.valueOf(((HashMap) arrayListUserModule.get(i)).get("ModuleName")));
                String icon = String.valueOf(((HashMap) arrayListUserModule.get(i)).get("ICONNM"));
                int resId = getResources().getIdentifier(icon, "drawable", getPackageName());
                Drawable drawable = getDrawable(resId);
                arrImage.add( drawable);

            }

        }

        if (!checkSaveData()) {
            System.out.println("x ada");
        } else {
            System.out.println("ada");
            arrString.add("Upload");
            arrImage.add( getResources().getDrawable(R.drawable.icon_upload, getTheme()));
        }
    }

    public ArrayList getSqliteUserModuleData(String user) {
        SqliteDBHelper sqliteDBHelper = new SqliteDBHelper(MenuActivity.this,getString(R.string.appdb_name), getFilesDir().getPath());
        return sqliteDBHelper.getTableDataFromSqlite("Select * from UserPermission where USERNM = '"+user+"' order by APPCD asc");
    }

    public void doSqliteDB() {
        SqliteDBHelper sqliteDBHelper = new SqliteDBHelper(MenuActivity.this, getString(R.string.appdb_name), getFilesDir().getPath());

        if (!sqliteDBHelper.checkTableExistence(CommonDBItem.SQLiteUploadAuditDTl)) {
            //LabelNo text, LotNo text, UOM text, Quantity text, ReturnDate text, scanUID text, ScannerID text, ReturnUser text, AcceptUser text,
            sqliteDBHelper.createDatabaseTable(CommonDBItem.SQLiteUploadAuditDTl,
                    " Plant text, ScanDate text," +
                    "ItemID text, Description text,"+
                    "OriginalLocID text, LocationID text,"+
                    "ItemFlag text, AssetAuditID text,"+
                    "AssetAuditDesc text, AuditFlag text,"+
                    "Flag text, ScannerID text");

            System.out.println("create " + CommonDBItem.SQLiteUploadAuditDTl);
        }

        if (!sqliteDBHelper.checkTableExistence(CommonDBItem.SQLiteUploadTransfer)) {
            //LabelNo text, LotNo text, UOM text, Quantity text, ReturnDate text, scanUID text, ScannerID text, ReturnUser text, AcceptUser text,
            sqliteDBHelper.createDatabaseTable(CommonDBItem.SQLiteUploadTransfer,
                    " Plant text, ScanDate text," +
                            "ItemID text, Description text,"+
                            "OriginalLocID text, LocationID text,"+
                            "CostCenter text, ScannerID text");

            System.out.println("create " + CommonDBItem.SQLiteUploadTransfer);
        }

    }

    public boolean checkSaveData() {
        SqliteDBHelper sqliteDBHelper = new SqliteDBHelper(MenuActivity.this, getString(R.string.appdb_name), getFilesDir().getPath());
//        boolean isContainData = false;

        if (sqliteDBHelper.checkTableExistence(CommonDBItem.SQLiteUploadAuditDTl)) {
            if ((sqliteDBHelper.countTableData("Select * from "+  CommonDBItem.SQLiteUploadAuditDTl) > 0)) {
                return true;
            }
        }

        if (sqliteDBHelper.checkTableExistence(CommonDBItem.SQLiteUploadTransfer)) {
            if ((sqliteDBHelper.countTableData("Select * from "+  CommonDBItem.SQLiteUploadTransfer) > 0)) {
                return true;
            }
        }

        return false;
    }

    public void sqliteDeleteData(String strTable) {
        SqliteDBHelper sqliteDBHelper = new SqliteDBHelper(MenuActivity.this,getString(R.string.appdb_name), getFilesDir().getPath());
        sqliteDBHelper.sqlStatement("delete from " + strTable);
    }

    public void doUpload(){

        ArrayList arrayListAuditlist = getSqliteAuditListData();
        System.out.println(arrayListAuditlist);

        for (int i = 0; i < arrayListAuditlist.size();i++){
            System.out.println(((HashMap)arrayListAuditlist.get(i)).get("Flag"));
            if(((HashMap)arrayListAuditlist.get(i)).get("Flag").equals("1")){

                System.out.println("update TSTAudit");
                String plant = String.valueOf(((HashMap)arrayListAuditlist.get(i)).get("Plant"));
                String item = String.valueOf(((HashMap)arrayListAuditlist.get(i)).get("ItemID"));
                String auditId = String.valueOf(((HashMap)arrayListAuditlist.get(i)).get("AssetAuditID"));
                String itemLoc = String.valueOf(((HashMap)arrayListAuditlist.get(i)).get("LocationID"));
                String flagVal = String.valueOf(((HashMap)arrayListAuditlist.get(i)).get("Flag"));

                String scanDate = String.valueOf(((HashMap)arrayListAuditlist.get(i)).get("ScanDate"));
                String cropDate = scanDate.substring(7,20);
                Date getDate = new Date(Long.parseLong(cropDate));
                String formateDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(getDate);

                AsyncCheckServer UploadAudit = new AsyncCheckServer(MenuActivity.this, new MyInterface() {
                    @Override
                    public void myMethod(boolean result) {
                        if (result == true) {

                            String qEX = "EXEC [SP900UpdateAuditDTL]  '" + flagVal + "','" + plant + "','" + item + "','" + auditId + "','" + itemLoc + "','" + formateDate + "'";
                            String  execSPTransfer = conn.execSP(qEX, userName, userPass, port, dbName, ipAddress);

                            if(execSPTransfer.equals("Procedure execute")) {
                                sqliteDeleteAuditItemRowData("UploadAudit", auditId, item);
                                setViewForUser();
                                printMGrid();
                            }else{
                                Message.message(MenuActivity.this, execSPTransfer);
                            }

//                            String q = "UPDATE TSTAssetAuditDtl SET Flag = 1,ScanDate = '" + formateDate + "' WHERE ItemID = '" + item + "' AND [AssetAuditID] = '" + auditId + "'";
//                            String setItem = conn.updateData(q, userName, userPass, port, dbName, ipAddress);
//                            System.out.println(setItem);
//                            if(setItem.equals("OK")){
//                                sqliteDeleteAuditItemRowData("UploadAudit",auditId,item);
//                                setViewForUser();
//                                printMGrid();
//                            }else{
//                                Message.message(MenuActivity.this, setItem);
//                            }

                        } else {
                            Message.message(MenuActivity.this, "Can't connect to server");
                        }
                    }
                });
                UploadAudit.execute();


            }else if(((HashMap)arrayListAuditlist.get(i)).get("Flag").equals("8")){

                System.out.println("update TSTAudit");
                String plant = String.valueOf(((HashMap)arrayListAuditlist.get(i)).get("Plant"));
                String item = String.valueOf(((HashMap)arrayListAuditlist.get(i)).get("ItemID"));
                String auditId = String.valueOf(((HashMap)arrayListAuditlist.get(i)).get("AssetAuditID"));
                String itemLoc = String.valueOf(((HashMap)arrayListAuditlist.get(i)).get("LocationID"));
                String flagVal = String.valueOf(((HashMap)arrayListAuditlist.get(i)).get("Flag"));

                String scanDate = String.valueOf(((HashMap)arrayListAuditlist.get(i)).get("ScanDate"));
                String cropDate = scanDate.substring(7,20);
                Date getDate = new Date(Long.parseLong(cropDate));
                String formateDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(getDate);

                AsyncCheckServer UploadAudit = new AsyncCheckServer(MenuActivity.this, new MyInterface() {
                    @Override
                    public void myMethod(boolean result) {
                        if (result == true) {

                            String qEX = "EXEC [SP900UpdateAuditDTL]  '" + flagVal + "','" + plant + "','" + item + "','" + auditId + "','" + itemLoc + "','" + formateDate + "'";
                            String  execSPTransfer = conn.execSP(qEX, userName, userPass, port, dbName, ipAddress);

                            if(execSPTransfer.equals("Procedure execute")) {
                                sqliteDeleteAuditItemRowData("UploadAudit", auditId, item);
                                setViewForUser();
                                printMGrid();
                            }else{
                                Message.message(MenuActivity.this, execSPTransfer);
                            }

//                            String q = "UPDATE TSTAssetAuditDtl SET Flag = 8,ScanDate = '" + formateDate + "' WHERE ItemID = '" + item + "' AND [AssetAuditID] = '" + auditId + "'";
//                            String setItem = conn.updateData(q, userName, userPass, port, dbName, ipAddress);
//                            System.out.println(setItem);
//                            if(setItem.equals("OK")) {
//                                sqliteDeleteAuditItemRowData("UploadAudit", auditId, item);
//                                setViewForUser();
//                                printMGrid();
//                            }else{
//                                Message.message(MenuActivity.this, setItem);
//                            }
                        } else {
                            Message.message(MenuActivity.this, "Can't connect to server");
                        }
                    }
                });
                UploadAudit.execute();



            }else {
                System.out.println("Insert in TSTAudit");
                String plant = String.valueOf(((HashMap)arrayListAuditlist.get(i)).get("Plant"));
                String item = String.valueOf(((HashMap)arrayListAuditlist.get(i)).get("ItemID"));
                String auditId = String.valueOf(((HashMap)arrayListAuditlist.get(i)).get("AssetAuditID"));
                String itemLoc = String.valueOf(((HashMap)arrayListAuditlist.get(i)).get("LocationID"));
                String flagVal = String.valueOf(((HashMap)arrayListAuditlist.get(i)).get("Flag"));

                String scanDate = String.valueOf(((HashMap)arrayListAuditlist.get(i)).get("ScanDate"));
                String cropDate = scanDate.substring(7,20);
                Date getDate = new Date(Long.parseLong(cropDate));
                String formateDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(getDate);

                AsyncCheckServer UploadAudit = new AsyncCheckServer(MenuActivity.this, new MyInterface() {
                    @Override
                    public void myMethod(boolean result) {
                        if (result == true) {

                            String qEX = "EXEC [SP900UpdateAuditDTL]  '" + flagVal + "','" + plant + "','" + item + "','" + auditId + "','" + itemLoc + "','" + formateDate + "'";
                            String  execSPTransfer = conn.execSP(qEX, userName, userPass, port, dbName, ipAddress);

                            if(execSPTransfer.trim().equals("Procedure execute")) {
                                sqliteDeleteAuditItemRowData("UploadAudit", auditId, item);
                                setViewForUser();
                                printMGrid();
                            }else{
                                Message.message(MenuActivity.this, execSPTransfer);
                            }

//                            String q = "INSERT INTO TSTAssetAuditDtl(Plant,AssetAuditID,ItemID,LocationID,Flag,ScanDate) VALUES ('" + plant  + "','" + auditId + "','" + item + "','" + itemLoc  + "','"+flagVal+"','" + formateDate + "') " ;
//                            String inserItem = conn.insertData(q, userName, userPass, port, dbName, ipAddress);
//                            System.out.println(inserItem);
//                            if(inserItem.equals("OK")) {
//                                sqliteDeleteAuditItemRowData("UploadAudit", auditId, item);
//                                setViewForUser();
//                                printMGrid();
//                            }else{
//                                Message.message(MenuActivity.this, inserItem);
//                            }

                        } else {
                            Message.message(MenuActivity.this, "Can't connect to server");
                        }
                    }
                });
                UploadAudit.execute();
            }
//            setViewForUser();
//            printMGrid();
        }

        ArrayList arrayListTransferlist = getSqliteTransferListData();
        System.out.println(arrayListTransferlist);

        for (int i = 0; i < arrayListTransferlist.size();i++){

            System.out.println("Update in transfer item");
            String plant = String.valueOf(((HashMap)arrayListTransferlist.get(i)).get("Plant"));
            String item = String.valueOf(((HashMap)arrayListTransferlist.get(i)).get("ItemID"));
            String itemLocPrev = String.valueOf(((HashMap)arrayListTransferlist.get(i)).get("OriginalLocID"));
            String itemLocNew = String.valueOf(((HashMap)arrayListTransferlist.get(i)).get("LocationID"));
            String costCentre = String.valueOf(((HashMap)arrayListTransferlist.get(i)).get("CostCenter"));

            String scanDate = String.valueOf(((HashMap)arrayListTransferlist.get(i)).get("ScanDate"));
            String cropDate = scanDate.substring(7,20);
            Date getDate = new Date(Long.parseLong(cropDate));
            String formateDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(getDate);

            AsyncCheckServer UploadTransfer = new AsyncCheckServer(MenuActivity.this, new MyInterface() {
                @Override
                public void myMethod(boolean result) {
                    if (result == true) {

                        String qEX = "EXEC [SP900SaveLocationTransfer]  '" + plant + "','" + item + "','" + itemLocNew + "','" + costCentre + "','" + formateDate + "','" + user +  "'";
                        String  execSPTransfer = conn.execSP(qEX, userName, userPass, port, dbName, ipAddress);
                        System.out.println( execSPTransfer);

                        if(execSPTransfer.trim().equals("Procedure execute")) {
                            System.out.println( "delete item :"+ item);
                            sqliteDeleteTransfertemRowData(CommonDBItem.SQLiteUploadTransfer, item,itemLocPrev,itemLocNew);
                            setViewForUser();
                            printMGrid();
                        }else{
                            Message.message(MenuActivity.this, execSPTransfer);
                        }
//                        String q = "UPDATE MSTItem SET LocationID = '" + itemLocNew  + "',CostCenter = '" + costCentre  + "' WHERE [ItemID] = '" + item + "' " ;
//                        String updateItem = conn.insertData(q, userName, userPass, port, dbName, ipAddress);
//                        System.out.println(updateItem);
//                        if(updateItem.equals("OK")) {
//                            sqliteDeleteTransfertemRowData(CommonDBItem.SQLiteUploadTransfer, item,itemLocPrev,itemLocNew);
//                            setViewForUser();
//                            printMGrid();
//                        }else{
//                            Message.message(MenuActivity.this, updateItem);
//                        }

                    } else {
                        Message.message(MenuActivity.this, "Can't connect to server");
                    }
                }
            });
            UploadTransfer.execute();

        }
    }

    public void sqliteDeleteAuditItemRowData(String strTable,String AuditID,String ItemId) {
        System.out.println(ItemId);
        System.out.println(AuditID);
        SqliteDBHelper sqliteDBHelper = new SqliteDBHelper(MenuActivity.this,getString(R.string.appdb_name), getFilesDir().getPath());
        sqliteDBHelper.sqlStatement("delete from " + strTable+ " where ItemID = '"+ ItemId+"' AND AssetAuditID = '"+AuditID+"'");

    }

    public void sqliteDeleteTransfertemRowData(String strTable,String ItemId,String prevLoc,String currentLoc) {
        System.out.println("Delete : " +ItemId+"Prev Location"+prevLoc);
        SqliteDBHelper sqliteDBHelper = new SqliteDBHelper(MenuActivity.this,getString(R.string.appdb_name), getFilesDir().getPath());
        sqliteDBHelper.sqlStatement("delete from " + strTable+ " where ItemID = '"+ ItemId+"' AND OriginalLocID = '"+prevLoc+"' AND LocationID ='"+currentLoc+"'");

    }

    public ArrayList getSqliteAuditListData() {
        SqliteDBHelper sqliteDBHelper = new SqliteDBHelper(MenuActivity.this,getString(R.string.appdb_name), getFilesDir().getPath());
        return sqliteDBHelper.getTableDataFromSqlite("Select * from "+ CommonDBItem.SQLiteUploadAuditDTl);
    }

    public ArrayList getSqliteTransferListData() {
        SqliteDBHelper sqliteDBHelper = new SqliteDBHelper(MenuActivity.this,getString(R.string.appdb_name), getFilesDir().getPath());
        return sqliteDBHelper.getTableDataFromSqlite("Select * from "+ CommonDBItem.SQLiteUploadTransfer);
    }

    public class UploadAudit extends AsyncTask<Object,String,String> {
        String z = "";
        Boolean isSuccess = false;
        private String result;
        ProgressDialog progressDialog;
        ArrayList arrayList;

        public UploadAudit(ArrayList arrayList) {
            this.arrayList = arrayList;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(MenuActivity.this,
                    "Loading",
                    "Please wait..");
        }

        @Override
        protected String doInBackground(Object... objects) {

            for (int i = 0; i < arrayList.size();i++){
                System.out.println(((HashMap)arrayList.get(i)).get("Flag"));
                if(((HashMap)arrayList.get(i)).get("Flag").equals("1")||((HashMap)arrayList.get(i)).get("Flag").equals("8")){
                    System.out.println("update TSTAudit");
                }else{
                    System.out.println("Insert in TSTAudit");
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(String r)
        {
            progressDialog.dismiss();
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MenuActivity.this);
            alertDialog.setTitle("Upload to Database");
            alertDialog.setMessage("Successfully Upload Data");
            alertDialog.setPositiveButton("OK", null);
            alertDialog.create().show();

        }
    }

    public interface MyInterface {
        public void myMethod(boolean result);
    }

    private class AsyncCheckServer extends AsyncTask<Void, Void, Boolean> {

        private Context mContext;
        private MyInterface mListener;
        ProgressDialog progressDialog;

        public AsyncCheckServer(Context context, MyInterface mListener) {
            mContext = context;
            this.mListener  = mListener;
        }

        @Override
        protected Boolean doInBackground(Void... Param) {
            CheckConnection checkConnection = new CheckConnection();
            boolean online = checkConnection.checkServer(userName, userPass, port, dbName, ipAddress);
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
            progressDialog = ProgressDialog.show(MenuActivity.this,
                    "Checking Server connection",
                    "Connecting to server...");
        }
    }

    //logOut
    @Override
    public void onBackPressed() {
        LogoutDialog dialog = new LogoutDialog();
        dialog.logOut(MenuActivity.this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                LogoutDialog dialog = new LogoutDialog();
                dialog.logOut(MenuActivity.this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
