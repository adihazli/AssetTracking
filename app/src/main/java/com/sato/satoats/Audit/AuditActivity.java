package com.sato.satoats.Audit;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.zxing.Result;
import com.sato.satoats.MenuActivity;
import com.sato.satoats.R;
import com.sato.satoats.RecyclerViewModelClass.DynaDrawableModel;
import com.sato.satoats.RecyclerViewModelClass.DynaLayoutParam;
import com.sato.satoats.RecyclerViewModelClass.DynaTextModel;
import com.sato.satoats.RecyclerViewModelClass.SingleModelItem;
import com.sato.satoats.SettingActivity;
import com.sato.satoats.Utilities.CommonDBItem;
import com.sato.satoats.Utilities.CustomAlertDialog;
import com.sato.satoats.Utilities.Message;
import com.sato.satoats.Utilities.SqliteDBHelper;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static com.sato.satoats.RecyclerViewModelClass.SingleModelItem.toHide;
import static com.sato.satoats.RecyclerViewModelClass.SingleModelItem.toShow;

public class AuditActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    FrameLayout contentFrame;
    TextView auditLocTV,auditIDTV,auditItemID;
    SharedPreferences sharedPreferences;
    String item,deviceName,plant,spinnerAuditItem = " ",spinnerAuditLoc = "";
    int spinnerAuditPos,spinnerAuditLocPos;
    ArrayList<String> scannedItem;
    ArrayList<String> DisplayScannedItem;
    ListView listView;
    ArrayAdapter adapter,locAdapter,testadapter;
    ImageView searchAuditID;
    Spinner spinnerAudit,spinnerLocation;
    boolean auditIDSet = false;

    ArrayList<String> auditID = new ArrayList<>();
    ArrayList<String> ItemAudit = new ArrayList<>();
    ArrayList<String> auditLocation = new ArrayList<>();
    ArrayList auditItem = new ArrayList<>();
    ArrayList scannedAuditItem = new ArrayList();


    EditText testEditText;
    Button saveBtn,testBtn;
    ArrayList<String> scannedItemTest = new ArrayList<>();
    Boolean saveList = false;

    CustomAlertDialog customAlertDialog;

    HashMap<String, String> tableElement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Asset Audit");

        testBtn =findViewById(R.id.testInsertBtn);
        testEditText = findViewById(R.id.testItemID);
        testEditText.setText("FF09X/0220/0146/1   ");
        testadapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,scannedItemTest);
        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(ItemAudit);
                String testing = testEditText.getText().toString().trim();

                boolean insertTest = false;

                if(!spinnerAuditItem.trim().isEmpty()&&!spinnerAuditLoc.trim().isEmpty()){
                    if(ItemAudit.contains(testing)){
                        if(scannedItemTest.contains(testing)){
                            Message.message(AuditActivity.this,"Same Item");
                            insertTest = true;
                        }

                        if(!insertTest){
                            AsyncCheckAuditItem asyncCheckAuditItem = new AsyncCheckAuditItem(AuditActivity.this,testing);
                            asyncCheckAuditItem.execute();
                        }

                    }else {
                        AsyncCheckNonAuditItem asyncCheckNonAuditItem = new AsyncCheckNonAuditItem(AuditActivity.this,testing);
                        asyncCheckNonAuditItem.execute();
                    }

                }
            }
        });

        scannedItem = new ArrayList<>();
        contentFrame = findViewById(R.id.content_frame);
        sharedPreferences = this.getSharedPreferences(getString(R.string.MyPref), Context.MODE_PRIVATE);
        deviceName = sharedPreferences.getString(getString(R.string.DeviceName), "");
        plant = sharedPreferences.getString(getString(R.string.plant), "");

        auditLocTV = findViewById(R.id.auditLocTV);
        auditIDTV = findViewById(R.id.auditIDTV);
        auditItemID = findViewById(R.id.auditItemID);
        listView =  findViewById(R.id.scanItem);
        searchAuditID = findViewById(R.id.searchAudit);
        saveBtn =findViewById(R.id.saveBtn);


//        adapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,scannedItem);
//        listView.setAdapter(adapter);

        callHandlerZxying();
        searchAuditList();
        saveBtnTap();

    }

    public void saveBtnTap(){
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String errorMessage = "";

                if(!saveList){
                    System.out.println("Item save!");
                    if(auditItem.size()>0){
                        if (errorMessage.equals("")) {
                            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(AuditActivity.this);
                            alertDialog.setIcon(AuditActivity.this.getResources().getDrawable(R.drawable.ico_warn, AuditActivity.this.getTheme()));
                            alertDialog.setTitle("Save Audit Item");
                            alertDialog.setMessage("Are you sure your want to save scanned audit data");
                            alertDialog.setNegativeButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    saveList = true;
                                    doUploadAuditSqlite(CommonDBItem.SQLiteUploadAuditDTl,auditItem);
                                }
                            });

                            alertDialog.setPositiveButton("Cancel", null);
                            alertDialog.create().show();


                        } else {
                            customAlertDialog.DialogWarningAlert(AuditActivity.this, "Warning", errorMessage).show();
                        }


                    }

                }else {
                    System.out.println("dont press many time!");
                }
            }
        });
    }

    public void searchAuditList(){
        searchAuditID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(AuditActivity.this);

                dialog.setContentView(R.layout.row_spinner_asset_audit);
                dialog.setCancelable(true);

                spinnerAudit = dialog.findViewById(R.id.spinnerAudit);
                spinnerLocation = dialog.findViewById(R.id.spinnerLocation);
                spinnerLocation.setEnabled(false);
                AsyncSetAuditIDSpinner asyncSetAuditIDSpinner = new AsyncSetAuditIDSpinner(AuditActivity.this);
                asyncSetAuditIDSpinner.execute();

                Button button = (Button) dialog.findViewById(R.id.buttonSelect);

                spinnerAudit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        spinnerAuditItem = auditID.get(position);
                        spinnerAuditPos = position;

                        if(position != 0){
                            spinnerLocation.setEnabled(true);
                            AsyncSetAuditLocationSpinner asyncSetAuditLocationSpinner = new AsyncSetAuditLocationSpinner(AuditActivity.this);
                            asyncSetAuditLocationSpinner.execute();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                spinnerLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        spinnerAuditLoc = auditLocation.get(position);
                        spinnerAuditLocPos = position;

                        if(position != 0){

                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(spinnerAuditPos != 0 && spinnerAuditLocPos != 0) {
                            AsyncGetItemIDForAudit asyncGetItemIDForAudit =new AsyncGetItemIDForAudit(AuditActivity.this);
                            asyncGetItemIDForAudit.execute();
                            dialog.dismiss();
                        }else{
                            Message.message(AuditActivity.this,"Please select Audit ID & Location");
                        }
                    }
                });
                dialog.show();

            }
        });
    }

    public ArrayList<String> getAuditID(String strTableName){
        ArrayList<String> audit = new ArrayList<>();
        ArrayList list = new ArrayList();
        auditID.clear();
        audit.add("Please Select Audit ID");
        SqliteDBHelper sqliteDBHelper = new SqliteDBHelper(AuditActivity.this, getString(R.string.appdb_name), getFilesDir().getPath());

        if (sqliteDBHelper.checkTableExistence(strTableName)) {
            list = sqliteDBHelper.getTableDataFromSqlite("select Asset_Audit_ID_Search from " + strTableName +" order by CDATE desc");
        } else {
            System.out.println("Table Don't Exist");
        }


        for(int i =0;i < list.size();i++){
            if(!audit.contains(((HashMap)list.get(i)).get("Asset_Audit_ID_Search"))){
              audit.add( String.valueOf(((HashMap)list.get(i)).get("Asset_Audit_ID_Search")));
            }
        }

        return audit;
    }

    public class AsyncSetAuditIDSpinner extends AsyncTask<Object, String, ArrayList> {
        Context context;
        ProgressDialog progressDialog;

        public AsyncSetAuditIDSpinner(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList doInBackground(Object... objects) {
            ArrayList isSuccess = getAuditID(CommonDBItem.SQLiteAuditHr);
            return isSuccess;
        }

        @Override
        protected void onPostExecute(ArrayList arrayList) {
            auditID = arrayList;
            adapter = new ArrayAdapter(AuditActivity.this,R.layout.spinner_custom,auditID);
            spinnerAudit.setAdapter(adapter);

        }

    }

    public ArrayList<String> getAuditLoc(String strTableName,String auditID){
        ArrayList<String> location = new ArrayList<>();
        ArrayList list = new ArrayList();
        location.clear();
        location.add("Please Select Location");
        SqliteDBHelper sqliteDBHelper = new SqliteDBHelper(AuditActivity.this, getString(R.string.appdb_name), getFilesDir().getPath());

        if (sqliteDBHelper.checkTableExistence(strTableName)) {
            list = sqliteDBHelper.getTableDataFromSqlite("select LocationID from " + strTableName +" where AssetAuditID ='"+auditID+"'");
        } else {
            System.out.println("Table Don't Exist");
        }


        for(int i =0;i < list.size();i++){
            if(!location.contains(((HashMap)list.get(i)).get("LocationID"))){
                location.add( String.valueOf(((HashMap)list.get(i)).get("LocationID")));
            }
        }

        return location;
    }

    public class AsyncSetAuditLocationSpinner extends AsyncTask<Object, String, ArrayList> {
        Context context;
        ProgressDialog progressDialog;

        public AsyncSetAuditLocationSpinner(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList doInBackground(Object... objects) {
            ArrayList isSuccess = getAuditLoc(CommonDBItem.SQLiteAuditDTl,spinnerAuditItem);
            return isSuccess;
        }

        @Override
        protected void onPostExecute(ArrayList arrayList) {
            auditLocation = arrayList;
            locAdapter = new ArrayAdapter(AuditActivity.this,R.layout.spinner_custom,auditLocation);
            spinnerLocation.setAdapter(locAdapter);

        }

    }

    public ArrayList<String> getItemIDForAudit(String strTableName,String auditID){
        ItemAudit = new ArrayList<>();
        ArrayList list = new ArrayList();
        SqliteDBHelper sqliteDBHelper = new SqliteDBHelper(AuditActivity.this, getString(R.string.appdb_name), getFilesDir().getPath());

        if (sqliteDBHelper.checkTableExistence(strTableName)) {
            list = sqliteDBHelper.getTableDataFromSqlite("select ItemID from " + strTableName +" where AssetAuditID ='"+auditID+"'");
        } else {
            System.out.println("Table Don't Exist");
        }

        return list;
    }

    public class AsyncGetItemIDForAudit extends AsyncTask<Object, String, ArrayList> {
        Context context;
        ProgressDialog progressDialog;

        public AsyncGetItemIDForAudit(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList doInBackground(Object... objects) {
            ArrayList isSuccess = getItemIDForAudit(CommonDBItem.SQLiteAuditDTl,spinnerAuditItem);
            return isSuccess;
        }

        @Override
        protected void onPostExecute(ArrayList arrayList) {
            for(int i =0;i < arrayList.size();i++){
                if(!ItemAudit.contains(((HashMap)arrayList.get(i)).get("ItemID"))){
                    ItemAudit.add( String.valueOf(((HashMap)arrayList.get(i)).get("ItemID")));
                }
            }
            auditIDTV.setText("Audit ID : " + spinnerAuditItem);
            auditLocTV.setText("Location : "+ spinnerAuditLoc);

        }

    }

    public ArrayList<String> getAuditItem(String strTableName,String auditID,String itemId){

        ArrayList list = new ArrayList();
        SqliteDBHelper sqliteDBHelper = new SqliteDBHelper(AuditActivity.this, getString(R.string.appdb_name), getFilesDir().getPath());

        if (sqliteDBHelper.checkTableExistence(strTableName)) {
            list = sqliteDBHelper.getTableDataFromSqlite("select Plant,Description,AssetAuditDesc,ItemID,AssetAuditID,Flag,ItemFlag,OriginalLocID,LocationID from " + strTableName +" where AssetAuditID ='"+auditID+"' and ItemID ='"+itemId+"'");
        } else {
            System.out.println("Table Don't Exist");
        }

        return list;
    }

    public class AsyncCheckAuditItem extends AsyncTask<Object, String, ArrayList> {
        Context context;
        String item;
        ProgressDialog progressDialog;

        public AsyncCheckAuditItem(Context context,String item) {
            this.context = context;
            this.item = item;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(AuditActivity.this);
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected ArrayList doInBackground(Object... objects) {
            ArrayList isSuccess = getAuditItem(CommonDBItem.SQLiteAuditDTl,spinnerAuditItem,item);
            return isSuccess;
        }

        @Override
        protected void onPostExecute(ArrayList arrayList) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
                if (arrayList.size() > 0) {

                    for (int i = 0; i < arrayList.size(); i++) {
                        if(String.valueOf(((HashMap)arrayList.get(i)).get("LocationID")).equals(spinnerAuditLoc)){
                            auditItem.add(populateScannedItem((HashMap)arrayList.get(i),"1",item,spinnerAuditLoc));
                            System.out.println("Scanned item : "+auditItem);
                            scannedItemTest.add(item);
                            scannedItem.add(item);
                        } else{
                            final int x = i;
                            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(AuditActivity.this);
                            alertDialog.setIcon(AuditActivity.this.getResources().getDrawable(R.drawable.ico_warn, AuditActivity.this.getTheme()));
                            alertDialog.setTitle("Different Location Item");
                            alertDialog.setMessage("Item in Audit list but in different location. Are you sure your want to insert scanned item in this location?");
                            alertDialog.setNegativeButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    auditItem.add(populateScannedItem((HashMap)arrayList.get(x),"8",item,spinnerAuditLoc));
                                    System.out.println("Scanned item : "+auditItem);
                                    scannedItemTest.add(item);
                                    scannedItem.add(item);
                                    listView.setAdapter(testadapter);
                                }
                            });

                            alertDialog.setPositiveButton("Cancel", null);
                            alertDialog.create().show();

                        }
                    }
                    listView.setAdapter(testadapter);
                }
                else {
                    System.out.println("New Item");

                }
            } else {

            }

        }

    }

    public ArrayList<String> getItemDataFromMSTItem(String strTableName,String item){

        ArrayList list = new ArrayList();
        SqliteDBHelper sqliteDBHelper = new SqliteDBHelper(AuditActivity.this, getString(R.string.appdb_name), getFilesDir().getPath());

        if (sqliteDBHelper.checkTableExistence(strTableName)) {
            list = sqliteDBHelper.getTableDataFromSqlite("select * from " + strTableName +" where Item_ID_Search ='"+item+"'");
        } else {
            System.out.println("Table Don't Exist");
        }

        return list;
    }

    public class AsyncCheckNonAuditItem extends AsyncTask<Object, String, ArrayList> {
        Context context;
        String item;
        ProgressDialog progressDialog;

        public AsyncCheckNonAuditItem(Context context,String item) {
            this.context = context;
            this.item = item;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(AuditActivity.this);
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected ArrayList doInBackground(Object... objects) {
            ArrayList isSuccess = getItemDataFromMSTItem(CommonDBItem.SQLiteMSTItem,item);
            System.out.println(isSuccess);
            return isSuccess;
        }

        @Override
        protected void onPostExecute(ArrayList arrayList) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
                if (arrayList.size() > 0) {

                    for (int i = 0; i < arrayList.size(); i++) {
                        if(String.valueOf(((HashMap)arrayList.get(i)).get("Location_ID_Search")).equals(spinnerAuditLoc)){
                            final int x = i;
                            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(AuditActivity.this);
                            alertDialog.setIcon(AuditActivity.this.getResources().getDrawable(R.drawable.ico_warn, AuditActivity.this.getTheme()));
                            alertDialog.setTitle("Non Audit list Item");
                            alertDialog.setMessage("Item not in Audit list but location changed to Audit Location. Are you sure your want to insert scanned item in this location?");
                            alertDialog.setNegativeButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    auditItem.add(populateScannedNonAuditItem((HashMap)arrayList.get(x),"7",item,spinnerAuditLoc));
                                    System.out.println("Scanned item : "+auditItem);
                                    scannedItemTest.add(item);
                                    scannedItem.add(item);
                                    listView.setAdapter(testadapter);
                                }
                            });

                            alertDialog.setPositiveButton("Cancel", null);
                            alertDialog.create().show();
                        } else{
                            final int x = i;
                            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(AuditActivity.this);
                            alertDialog.setIcon(AuditActivity.this.getResources().getDrawable(R.drawable.ico_warn, AuditActivity.this.getTheme()));
                            alertDialog.setTitle("Non Audit list Item");
                            alertDialog.setMessage("Item not in Audit list. Are you sure your want to insert scanned item in this location?");
                            alertDialog.setNegativeButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    auditItem.add(populateScannedItem((HashMap)arrayList.get(x),"4",item,spinnerAuditLoc));
                                    System.out.println("Scanned item : "+auditItem);
                                    scannedItemTest.add(item);
                                    scannedItem.add(item);
                                    listView.setAdapter(testadapter);
                                }
                            });

                            alertDialog.setPositiveButton("Cancel", null);
                            alertDialog.create().show();

                        }
                    }

                }
                else {
                    System.out.println("Unknown Item");

                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(AuditActivity.this);
                    alertDialog.setIcon(AuditActivity.this.getResources().getDrawable(R.drawable.ico_warn, AuditActivity.this.getTheme()));
                    alertDialog.setTitle("Unknown Item");
                    alertDialog.setMessage("Unknown Item. Are you sure your want to insert scanned item in this location?");
                    alertDialog.setNegativeButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            auditItem.add(populateUnknownScannedItem(item,spinnerAuditLoc));
                            System.out.println(auditItem);
                            scannedItemTest.add(item);
                            listView.setAdapter(testadapter);
                        }
                    });
                    alertDialog.setPositiveButton("Cancel", null);
                    alertDialog.create().show();

                }
            } else {

            }

        }

    }

    public ArrayList populateScannedNonAuditItem(HashMap hashMapScItem, String flag,String item,String location) {
        ArrayList thisArray = new ArrayList();
        HashMap<String, String> thisData = new HashMap<>();

        thisData.put("Plant", String.valueOf(hashMapScItem.get("Plant")));
        thisData.put("ScanDate", String.valueOf(new Date().getTime()));
        thisData.put("ItemID", item);
        thisData.put("Description", String.valueOf(hashMapScItem.get("Description_Search")));
        thisData.put("OriginalLocID", String.valueOf(hashMapScItem.get("Location_ID_Search")));
        thisData.put("LocationID", location);
        thisData.put("ItemFlag", String.valueOf(hashMapScItem.get(" ")));
        thisData.put("AssetAuditID", spinnerAuditItem);
        thisData.put("AssetAuditDesc", " ");
        thisData.put("AuditFlag", String.valueOf(hashMapScItem.get(" ")));
        thisData.put("Flag", flag);
        thisData.put("ScannerID",deviceName);

        thisArray.add(thisData);
        return thisArray;
    }

    public ArrayList populateScannedItem(HashMap hashMapScItem, String flag,String item,String location) {
        ArrayList thisArray = new ArrayList();
        HashMap<String, String> thisData = new HashMap<>();

        thisData.put("Plant", String.valueOf(hashMapScItem.get("Plant")));
        thisData.put("ScanDate", String.valueOf(new Date().getTime()));
        thisData.put("ItemID", item);
        thisData.put("Description", String.valueOf(hashMapScItem.get("Description")));
        thisData.put("OriginalLocID", String.valueOf(hashMapScItem.get("OriginalLocID")));
        thisData.put("LocationID", location);
        thisData.put("ItemFlag", String.valueOf(hashMapScItem.get("ItemFlag")));
        thisData.put("AssetAuditID", String.valueOf(hashMapScItem.get("AssetAuditID")));
        thisData.put("AssetAuditDesc", String.valueOf(hashMapScItem.get("AssetAuditDesc")));
        thisData.put("AuditFlag", String.valueOf(hashMapScItem.get("AuditFlag")));
        thisData.put("Flag", flag);
        thisData.put("ScannerID",deviceName);

        thisArray.add(thisData);
        return thisArray;
    }

    public ArrayList populateUnknownScannedItem(String item,String location) {
        ArrayList thisArray = new ArrayList();
        HashMap<String, String> thisData = new HashMap<>();

        thisData.put("Plant",plant);
        thisData.put("ScanDate", String.valueOf(new Date().getTime()));
        thisData.put("ItemID", item);
        thisData.put("Description"," ");
        thisData.put("OriginalLocID", " ");
        thisData.put("LocationID", location);
        thisData.put("ItemFlag", " ");
        thisData.put("AssetAuditID", spinnerAuditItem);
        thisData.put("AssetAuditDesc", " ");
        thisData.put("AuditFlag", " ");
        thisData.put("Flag", "5");
        thisData.put("ScannerID",deviceName);

        thisArray.add(thisData);
        return thisArray;
    }

    public void doUploadAuditSqlite(String strTableName, ArrayList arrayList) {
        SqliteDBHelper sqliteDBHelper = new SqliteDBHelper(AuditActivity.this, getString(R.string.appdb_name), getFilesDir().getPath());

        if (sqliteDBHelper.checkTableExistence(strTableName)) {

            insertDataIntoSqlite(sqliteDBHelper, strTableName, arrayList);

            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(AuditActivity.this);
            alertDialog.setIcon(AuditActivity.this.getResources().getDrawable(R.drawable.ico_warn, AuditActivity.this.getTheme()));
            alertDialog.setTitle("Audit Item");
            alertDialog.setMessage("Data saved to device. Please upload the data later.");
            alertDialog.setPositiveButton("OK", null);
            alertDialog.create().show();
        } else {
            System.out.println("Please create table for audit item and insert data");
        }
    }

    public void insertDataIntoSqlite(SqliteDBHelper sqliteDBHelper, String strTableName, ArrayList arrayList){
        if (arrayList.size() > 0) {
            for (int i = 0; i < arrayList.size(); i++) {
                sqliteDBHelper.insertTableData(strTableName, setContVal((ArrayList) arrayList.get(i)));
            }
        }
    }

    public ContentValues setContVal(ArrayList thisList) {
        ContentValues contentValues = new ContentValues();
        for (int i = 0 ; i < thisList.size(); i++) {
            Set<String> keySet = ((HashMap)thisList.get(i)).keySet();
            for (String strKey: keySet) {
                if  (strKey.equals("ReceiveDate") || strKey.equals("ScanDate")) {
                    contentValues.put(strKey, "\\/Date("+String.valueOf(new Date().getTime())+")\\/");
                } else {
                    contentValues.put(strKey, String.valueOf(((HashMap) thisList.get(i)).get(strKey)));
                }
            }
        }
        return contentValues;
    }

    public void createTable(String strTableName, String tableColumnName) {
        SqliteDBHelper sqliteDBHelper = new SqliteDBHelper(AuditActivity.this,getString(R.string.appdb_name), getFilesDir().getPath());
        sqliteDBHelper.createDatabaseTable(strTableName, tableColumnName);
    }

    @Override
    public void handleResult(Result rawResult) {
        processScanData(rawResult);
        mScannerView.stopCamera();
        contentFrame.removeAllViews();

        listView.setAdapter(adapter);
        callHandlerZxying();
    }

    public void callHandlerZxying() {
        try {
            mScannerView = new ZXingScannerView(getApplicationContext());   // Programmatically initialize the scanner view
            contentFrame.addView(mScannerView);               // Set the scanner view as the content view
            mScannerView.setResultHandler(this);
            mScannerView.startCamera();
        } catch (Exception Ex) {
            Ex.printStackTrace();
            System.out.println("E:" + Ex);
        }
    }

    public void processScanData (Result rawResult) {
        boolean insert = false;

        try {

            Object obj = new JSONTokener(rawResult.toString()).nextValue();

            if (obj instanceof JSONObject) {
                JSONObject object = new JSONObject(rawResult.getText());
                item = object.getString("itemID");// object based on barcode !!
            }
            else if(obj instanceof String){
                item = rawResult.getText().trim();
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            System.out.println("serr : "+e);
        }


        boolean insertTest = false;

        if(!spinnerAuditItem.trim().isEmpty()&&!spinnerAuditLoc.trim().isEmpty()){
            if(ItemAudit.contains(item)){
                if(scannedItem.contains(item)){
                    Message.message(AuditActivity.this,"Same Item");
                    insertTest = true;
                }

                if(!insertTest){
                    AsyncCheckAuditItem asyncCheckAuditItem = new AsyncCheckAuditItem(AuditActivity.this,item);
                    asyncCheckAuditItem.execute();
                }

            }else {
                AsyncCheckNonAuditItem asyncCheckNonAuditItem = new AsyncCheckNonAuditItem(AuditActivity.this,item);
                asyncCheckNonAuditItem.execute();
            }

        }







//        if(scannedItem.contains(item)){
//            System.out.println("same item");
//            insert = true;
//        }
//
//        if(!insert){
//            scannedItem.add(item);
//        }

    }

    public HashMap checkTablePragma(String strTableName) {
        SqliteDBHelper sqliteDBHelper = new SqliteDBHelper(AuditActivity.this, getString(R.string.appdb_name), getFilesDir().getPath());
        ArrayList arrayList = sqliteDBHelper.sqlStatement("pragma table_info("+strTableName+")");
        HashMap<String, String> thisHash = new HashMap<>();
        for (int i = 0; i < arrayList.size(); i++) { thisHash.put(String.valueOf(((HashMap)arrayList.get(i)).get("name")), String.valueOf(((HashMap)arrayList.get(i)).get("name"))); }

        return thisHash;
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
                startActivity(new Intent(AuditActivity.this, MenuActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
