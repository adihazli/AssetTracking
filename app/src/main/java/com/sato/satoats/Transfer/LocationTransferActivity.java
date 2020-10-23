package com.sato.satoats.Transfer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.zxing.Result;
import com.sato.satoats.Audit.AuditActivity;
import com.sato.satoats.Loan.ScanReturnLoanActivity;
import com.sato.satoats.MenuActivity;
import com.sato.satoats.R;
import com.sato.satoats.Utilities.CommonDBItem;
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

public class LocationTransferActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    SharedPreferences sharedPreferences;
    private ZXingScannerView mScannerView;
    FrameLayout contentFrame;

    ImageView searchLocTransfer;
    Spinner spinnerCostCentre,spinnerLocationTransfer;
    TextView costCenterTV,transferLocTV;

    String deviceName,plant,item,strCostCentre =" ",strLocation = " ";
    Integer posCostCentre,posLoc;

    ArrayAdapter costCentreAdapter,locAdapter,adapter;

    ListView scannedLocTransferItem;
    ArrayList<String> transferLocation = new ArrayList<>();
    ArrayList<String> transferCostCentre = new ArrayList<>();
    ArrayList<String> ItemTransfer= new ArrayList<>();
    ArrayList<String> scannedItem;
    ArrayList transferItem = new ArrayList<>();

    Boolean saveList = false;
    Button saveLocTransferBtn;

    Button btnTest;
    EditText etTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_transfer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Location Transfer");

        sharedPreferences = this.getSharedPreferences(getString(R.string.MyPref), Context.MODE_PRIVATE);
        deviceName = sharedPreferences.getString(getString(R.string.DeviceName), "");
        plant = sharedPreferences.getString(getString(R.string.plant), "");

        searchLocTransfer = findViewById(R.id.searchLocTransfer);
        costCenterTV = findViewById(R.id.costCenterTV);
        transferLocTV = findViewById(R.id.transferLocTV);
        scannedLocTransferItem = findViewById(R.id.scannedLocTransferItem);
        saveLocTransferBtn = findViewById(R.id.saveLocTransferBtn);
        scannedItem = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,scannedItem);

        searchCostCentre();
        saveLocTransferBtnPressed();

        btnTest = findViewById(R.id.btnTest);
        etTest = findViewById(R.id.etTest);

        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               String itemTest =  etTest.getText().toString();
                System.out.println(itemTest);

                boolean insertTest = false;

                if(!strCostCentre.trim().isEmpty()&&!strLocation.trim().isEmpty()){

                        System.out.println("check itemtransfer : " + itemTest);
                        if(scannedItem.contains(itemTest)){
                            Message.message(LocationTransferActivity.this,"Same Item");
                            insertTest = true;
                        }

                        if(!insertTest){
                            item = itemTest;
                            AsyncGetTransferItemData asyncGetTransferItemData = new AsyncGetTransferItemData(LocationTransferActivity.this,item);
                            asyncGetTransferItemData.execute();
                        }


                }else{
                    System.out.println("Select cost center & location to transfer");
                }

            }
        });
    }

    void saveLocTransferBtnPressed(){
        saveLocTransferBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(strCostCentre.trim().isEmpty()||strLocation.trim().isEmpty()){
                    System.out.println();
                    Message.message(LocationTransferActivity.this,"Please select Cost Centre & Location");
                    if(transferItem.size()<0){
                        Message.message(LocationTransferActivity.this,"Please select item to transfer");
                    }
                }else{
                    if(transferItem.size()<0){
                        Message.message(LocationTransferActivity.this,"Please select item to transfer");
                    } else{
                        if(!saveList) {
                            System.out.println("Item save!");
                            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(LocationTransferActivity.this);
                            alertDialog.setIcon(LocationTransferActivity.this.getResources().getDrawable(R.drawable.ico_warn, LocationTransferActivity.this.getTheme()));
                            alertDialog.setTitle("Save Audit Item");
                            alertDialog.setMessage("Are you sure your want to save scanned audit data");
                            alertDialog.setNegativeButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    saveList = true;
                                    doUploadTransferSqlite(CommonDBItem.SQLiteUploadTransfer, transferItem);
                                }
                            });

                            alertDialog.setPositiveButton("Cancel", null);
                            alertDialog.create().show();
                        }
                    }
                }

            }
        });
    }

    public void searchCostCentre(){
        searchLocTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(LocationTransferActivity.this);

                dialog.setContentView(R.layout.row_spinner_loc_transfer);
                dialog.setCancelable(true);

                spinnerCostCentre = dialog.findViewById(R.id.spinnerCostCentre);
                spinnerLocationTransfer = dialog.findViewById(R.id.spinnerLocationTransfer);

                AsyncSetAuditLocationSpinner asyncSetAuditLocationSpinner = new AsyncSetAuditLocationSpinner(LocationTransferActivity.this);
                asyncSetAuditLocationSpinner.execute();

                AsyncSetCostCentreSpinner asyncSetCostCentreSpinner = new AsyncSetCostCentreSpinner(LocationTransferActivity.this);
                asyncSetCostCentreSpinner.execute();


                Button button = (Button) dialog.findViewById(R.id.buttonSelect);

                spinnerCostCentre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        strCostCentre = transferCostCentre.get(position);
                        posCostCentre = position;

                        System.out.println(strCostCentre+" : "+posCostCentre);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                spinnerLocationTransfer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        strLocation = transferLocation.get(position);
                        posLoc = position;

                        System.out.println(strLocation+" : "+ posLoc);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(posLoc != 0 && posCostCentre != 0) {

                            costCenterTV.setText("Cost Center : "+strCostCentre);
                            transferLocTV.setText("Location : "+strLocation);

                            dialog.dismiss();
                        }else{
                            Message.message(LocationTransferActivity.this,"Please select Cost Centre & Location");
                        }
                    }
                });

                dialog.show();

            }
        });
    }

    public class AsyncSetCostCentreSpinner extends AsyncTask<Object, String, ArrayList> {
        Context context;
        ProgressDialog progressDialog;

        public AsyncSetCostCentreSpinner(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList doInBackground(Object... objects) {
            ArrayList isSuccess = getTransCostCentre(CommonDBItem.SQLiteMSTCosCentre);
            return isSuccess;
        }

        @Override
        protected void onPostExecute(ArrayList arrayList) {
            transferCostCentre = arrayList;
            costCentreAdapter = new ArrayAdapter(LocationTransferActivity.this,R.layout.spinner_custom,transferCostCentre);
            spinnerCostCentre.setAdapter(costCentreAdapter);

        }

    }

    public ArrayList<String> getTransCostCentre(String strTableName){
        ArrayList<String> location = new ArrayList<>();
        ArrayList list = new ArrayList();
        location.clear();
        location.add("Please Select Cost Centre");
        SqliteDBHelper sqliteDBHelper = new SqliteDBHelper(LocationTransferActivity.this, getString(R.string.appdb_name), getFilesDir().getPath());

        if (sqliteDBHelper.checkTableExistence(strTableName)) {
            list = sqliteDBHelper.getTableDataFromSqlite("select CostCentreID from " + strTableName);
        } else {
            System.out.println("Table Don't Exist");
        }


        for(int i =0;i < list.size();i++){
            if(!location.contains(((HashMap)list.get(i)).get("CostCentreID"))){
                location.add( String.valueOf(((HashMap)list.get(i)).get("CostCentreID")));
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
            ArrayList isSuccess = getTransLoc(CommonDBItem.SQLiteMSTLocation);
            return isSuccess;
        }

        @Override
        protected void onPostExecute(ArrayList arrayList) {
            transferLocation = arrayList;
            locAdapter = new ArrayAdapter(LocationTransferActivity.this,R.layout.spinner_custom,transferLocation);
            spinnerLocationTransfer.setAdapter(locAdapter);

        }

    }

    public ArrayList<String> getTransLoc(String strTableName){
        ArrayList<String> location = new ArrayList<>();
        ArrayList list = new ArrayList();
        location.clear();
        location.add("Please Select Location");
        SqliteDBHelper sqliteDBHelper = new SqliteDBHelper(LocationTransferActivity.this, getString(R.string.appdb_name), getFilesDir().getPath());

        if (sqliteDBHelper.checkTableExistence(strTableName)) {
            list = sqliteDBHelper.getTableDataFromSqlite("select Location_ID_Search from " + strTableName);
        } else {
            System.out.println("Table Don't Exist");
        }


        for(int i =0;i < list.size();i++){
            if(!location.contains(((HashMap)list.get(i)).get("Location_ID_Search"))){
                location.add( String.valueOf(((HashMap)list.get(i)).get("Location_ID_Search")));
            }
        }

        return location;
    }

    @Override
    public void handleResult(Result rawResult) {
        processScanData(rawResult);
        mScannerView.stopCamera();
        contentFrame.removeAllViews();

        scannedLocTransferItem.setAdapter(adapter);
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

        if(!strCostCentre.trim().isEmpty()&&!strLocation.trim().isEmpty()){
            if(ItemTransfer.contains(item)){
                if(scannedItem.contains(item)){
                    Message.message(LocationTransferActivity.this,"Same Item");
                    insertTest = true;
                }

                if(!insertTest){
                    AsyncGetTransferItemData asyncGetTransferItemData = new AsyncGetTransferItemData(LocationTransferActivity.this,item);
                    asyncGetTransferItemData.execute();
                }

            }else {

            }

        }
    }

    public ArrayList<String> getItemDataFromMSTItem(String strTableName,String item){

        ArrayList list = new ArrayList();
        SqliteDBHelper sqliteDBHelper = new SqliteDBHelper(LocationTransferActivity.this, getString(R.string.appdb_name), getFilesDir().getPath());

        if (sqliteDBHelper.checkTableExistence(strTableName)) {
            list = sqliteDBHelper.getTableDataFromSqlite("select * from " + strTableName +" where Item_ID_Search ='"+item+"'");
        } else {
            System.out.println("Table Don't Exist");
        }

        return list;
    }

    public class AsyncGetTransferItemData extends AsyncTask<Object, String, ArrayList> {
        Context context;
        String item;
        ProgressDialog progressDialog;

        public AsyncGetTransferItemData(Context context,String item) {
            this.context = context;
            this.item = item;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(LocationTransferActivity.this);
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
                        final int x = i;
                        transferItem.add(populateScannedItem((HashMap)arrayList.get(x),item,strCostCentre,strLocation));
                        scannedItem.add(item);
                        scannedLocTransferItem.setAdapter(adapter);
                    }

                }
                else {
                    System.out.println("Unknown Item");

                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(LocationTransferActivity.this);
                    alertDialog.setIcon(LocationTransferActivity.this.getResources().getDrawable(R.drawable.ico_warn, LocationTransferActivity.this.getTheme()));
                    alertDialog.setTitle("Unknown Item");
                    alertDialog.setMessage("Unknown Item. Please scan valid item");
                    alertDialog.setPositiveButton("Cancel", null);
                    alertDialog.create().show();

                }
            } else {

            }

        }

    }

    public ArrayList populateScannedItem(HashMap hashMapScItem, String item,String costCentre,String location) {
        ArrayList thisArray = new ArrayList();
        HashMap<String, String> thisData = new HashMap<>();

       // String query = "UPDATE MSTItem SET LocationID = '" + loc  + "',CostCenter = '" + ccenter  + "' WHERE [ItemID] = '" + itemID + "' " ;

        thisData.put("Plant", String.valueOf(hashMapScItem.get("Plant")));
        thisData.put("ScanDate", String.valueOf(new Date().getTime()));
        thisData.put("ItemID", item);
        thisData.put("Description", String.valueOf(hashMapScItem.get("Description_Search")));
        thisData.put("OriginalLocID", String.valueOf(hashMapScItem.get("Location_ID_Search")));
        thisData.put("LocationID", location);
        thisData.put("CostCenter", costCentre);
        thisData.put("ScannerID",deviceName);

        thisArray.add(thisData);
        return thisArray;
    }

    public void doUploadTransferSqlite(String strTableName, ArrayList arrayList) {
        SqliteDBHelper sqliteDBHelper = new SqliteDBHelper(LocationTransferActivity.this, getString(R.string.appdb_name), getFilesDir().getPath());

        if (sqliteDBHelper.checkTableExistence(strTableName)) {

            insertDataIntoSqlite(sqliteDBHelper, strTableName, arrayList);

            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(LocationTransferActivity.this);
            alertDialog.setIcon(LocationTransferActivity.this.getResources().getDrawable(R.drawable.ico_warn, LocationTransferActivity.this.getTheme()));
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
                startActivity(new Intent(LocationTransferActivity.this, MenuActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
