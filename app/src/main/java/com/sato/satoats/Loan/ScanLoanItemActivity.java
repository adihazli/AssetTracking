package com.sato.satoats.Loan;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sato.satoats.MenuActivity;
import com.sato.satoats.R;
import com.sato.satoats.Transfer.LocationTransferActivity;
import com.sato.satoats.Utilities.CommonDBItem;
import com.sato.satoats.Utilities.Message;
import com.sato.satoats.Utilities.SqliteDBHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanLoanItemActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    private ZXingScannerView mScannerView;
    FrameLayout contentFrame;

    TextView loanIDTV;
    ImageView searchLoan;

    String deviceName,plant,item,strDocID =" ",strLocation = " ";

    Button saveLoanBtn;

    ArrayAdapter docIDAdapter,adapter;

    ListView scannedLoanItem;

    ArrayList<String> itemLoan= new ArrayList<>();
    ArrayList<String> scannedItem;
    ArrayList loanItem = new ArrayList<>();

    Button btnTest;
    EditText etTest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_loan_item);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Loan Item");

        loanIDTV = findViewById(R.id.loanItemID);
        searchLoan = findViewById(R.id.searchLoan);
        saveLoanBtn = findViewById(R.id.saveLoanBtn);

        sharedPreferences = this.getSharedPreferences(getString(R.string.MyPref), Context.MODE_PRIVATE);
        deviceName = sharedPreferences.getString(getString(R.string.DeviceName), "");
        plant = sharedPreferences.getString(getString(R.string.plant), "");

        scannedItem = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,scannedItem);

        btnTest = findViewById(R.id.buttonTest);
        etTest = findViewById(R.id.editTest);

        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strDocID = "123";
                String itemTest =  etTest.getText().toString();
                System.out.println(itemTest);

                boolean insertTest = false;

                if(!strDocID.trim().isEmpty()){

                    System.out.println("check itemLoan : " + itemTest);
                    if(scannedItem.contains(itemTest)){
                        Message.message(ScanLoanItemActivity.this,"Same Item");
                        insertTest = true;
                    }

                    if(!insertTest){
                        item = itemTest;

                    }


                }else{
                    System.out.println("Select Document ID to scan item");
                }

            }
        });

    }

    public ArrayList<String> getItemDataFromMSTItem(String strTableName,String item){

        ArrayList list = new ArrayList();
        SqliteDBHelper sqliteDBHelper = new SqliteDBHelper(ScanLoanItemActivity.this, getString(R.string.appdb_name), getFilesDir().getPath());

        if (sqliteDBHelper.checkTableExistence(strTableName)) {
            list = sqliteDBHelper.getTableDataFromSqlite("select * from " + strTableName +" where Item_ID_Search ='"+item+"'");
        } else {
            System.out.println("Table Don't Exist");
        }

        return list;
    }

    public ArrayList populateScannedItem(HashMap hashMapScItem, String item,String docID) {
        ArrayList thisArray = new ArrayList();
        HashMap<String, String> thisData = new HashMap<>();

        // String query = "UPDATE MSTItem SET LocationID = '" + loc  + "',CostCenter = '" + ccenter  + "' WHERE [ItemID] = '" + itemID + "' " ;

        thisData.put("Plant", String.valueOf(hashMapScItem.get("Plant")));
        thisData.put("ScanDate", String.valueOf(new Date().getTime()));
        thisData.put("ItemID", item);
        thisData.put("Description", String.valueOf(hashMapScItem.get("Description_Search")));
        thisData.put("OriginalLocID", String.valueOf(hashMapScItem.get("Location_ID_Search")));
        thisData.put("DocumentNo", docID);
        thisData.put("ScannerID",deviceName);

        thisArray.add(thisData);
        return thisArray;
    }

    public class AsyncGetLoanItemData extends AsyncTask<Object, String, ArrayList> {
        Context context;
        String item;
        ProgressDialog progressDialog;

        public AsyncGetLoanItemData(Context context,String item) {
            this.context = context;
            this.item = item;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(ScanLoanItemActivity.this);
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
                        loanItem.add(populateScannedItem((HashMap)arrayList.get(x),item,strDocID));
                        scannedItem.add(item);
                        scannedLoanItem.setAdapter(adapter);
                    }

                }
                else {
                    System.out.println("Unknown Item");

                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ScanLoanItemActivity.this);
                    alertDialog.setIcon(ScanLoanItemActivity.this.getResources().getDrawable(R.drawable.ico_warn, ScanLoanItemActivity.this.getTheme()));
                    alertDialog.setTitle("Unknown Item");
                    alertDialog.setMessage("Unknown Item. Please scan valid item");
                    alertDialog.setPositiveButton("Cancel", null);
                    alertDialog.create().show();

                }
            } else {

            }

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
                startActivity(new Intent(ScanLoanItemActivity.this, MenuActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
