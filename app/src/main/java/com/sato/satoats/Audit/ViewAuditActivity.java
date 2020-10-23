package com.sato.satoats.Audit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.sato.satoats.LayoutAdapter.AdapterFive;
import com.sato.satoats.LayoutAdapter.AdapterFiveHeader;
import com.sato.satoats.LayoutAdapter.AdapterFiveModel;
import com.sato.satoats.MenuActivity;
import com.sato.satoats.R;
import com.sato.satoats.RecyclerViewModelClass.DynaDrawableModel;
import com.sato.satoats.RecyclerViewModelClass.DynaLayoutParam;
import com.sato.satoats.RecyclerViewModelClass.DynaTextModel;
import com.sato.satoats.RecyclerViewModelClass.SingleModelItem;
import com.sato.satoats.Utilities.CommonDBItem;
import com.sato.satoats.Utilities.CustomAlertDialog;
import com.sato.satoats.Utilities.Message;
import com.sato.satoats.Utilities.SqliteDBHelper;

import java.util.ArrayList;
import java.util.HashMap;

import static com.sato.satoats.RecyclerViewModelClass.SingleModelItem.toHide;
import static com.sato.satoats.RecyclerViewModelClass.SingleModelItem.toShow;

public class ViewAuditActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView recyclerViewHeader;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.LayoutManager layoutManagerHeader;

    ArrayList<ArrayList> listOfItemRow = new ArrayList<>();
    ArrayList searchList = new ArrayList<>();
    ArrayList<String> auditID = new ArrayList<>();
    ArrayList<String> auditYear = new ArrayList<>();

    ImageView searchAuditIDIV;
    CustomAlertDialog customAlertDialog;

    ArrayAdapter adapter,adapterYear;
    Spinner SpinerView,spinner;
    String[] searchBY = {"","Item ID","Description","Status"};

    EditText auditViewSearchET;
    Integer SearchPos = 0,spinnerYearPos = 0,spinnerAuditPos = 0;
    String spinnerAuditItem = " ",setAuditID = "";

    TextView txtAuditID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_audit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("View Audit Report");

        customAlertDialog = new CustomAlertDialog();

        txtAuditID =findViewById(R.id.txtViewAuditID);
        searchAuditIDIV = findViewById(R.id.searchViewAuditID);
        auditViewSearchET = findViewById(R.id.auditViewSearchET);

        SpinerView = findViewById(R.id.viewSpinnerAuditView);
        ArrayAdapter sByFilter = new ArrayAdapter(ViewAuditActivity.this,R.layout.spinner_custom,searchBY);
        SpinerView.setAdapter(sByFilter);

        layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManagerHeader = new LinearLayoutManager(getApplicationContext());
        initRecyclerViewDetails();

        //  setCustomRecycView();
        ArrayList<AdapterFiveModel> headerModel = new ArrayList<>();
        headerModel.add(new AdapterFiveModel("No","Item ID", "Item Desc.", "Location ID", "Status"));
        initHeader(getApplicationContext(), headerModel);

        auditYear = getAuditYear(CommonDBItem.SQLiteAuditHr);
        auditID.add("Please Select Audit ID");





        searchAuditList();
        searchTable();
        filterTable();

    }

    public void filterTable(){
        SpinerView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position != 0){
                    SearchPos = position;
                    String searchby = "";
                    switch (SearchPos) {
                        case 1:
                            searchby = searchBY[1];
                            break;
                        case 2:
                            searchby = searchBY[2];
                            break;
                        case 3:
                            searchby = searchBY[3];
                            break;
                    }
                    Message.message(ViewAuditActivity.this,"Search table by " + searchby);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void searchTable(){
        auditViewSearchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                s = s.toString().toLowerCase();
                System.out.println(s);
                ArrayList thisList = listOfItemRow.get(0);
                searchList.clear();
                for(int i = 0;i<listOfItemRow.size();i++){
                    String text = ((SingleModelItem)listOfItemRow.get(i).get(3)).getStrText().toLowerCase();
                    System.out.println(text);
                    if(SearchPos != 0) {
                        switch (SearchPos) {
                            case 1:
                                //by item ID
                                text = ((SingleModelItem)listOfItemRow.get(i).get(2)).getStrText().toLowerCase();
                                break;
                            case 2:
                                //by item desc
                                text = ((SingleModelItem)listOfItemRow.get(i).get(3)).getStrText().toLowerCase();
                                break;
                            case 3:
                                //by status
                                text = ((SingleModelItem)listOfItemRow.get(i).get(5)).getStrText().toLowerCase();
                                break;

                        }
                    }
                    if(text.contains(s)){

                        ArrayList<AdapterFiveModel> listModel = new ArrayList<>();

                        searchList.add(new AdapterFiveModel(((SingleModelItem)listOfItemRow.get(i).get(1)).getStrText(), ((SingleModelItem)listOfItemRow.get(i).get(2)).getStrText(),
                                    ((SingleModelItem)listOfItemRow.get(i).get(3)).getStrText(), ((SingleModelItem)listOfItemRow.get(i).get(4)).getStrText(),
                                    ((SingleModelItem)listOfItemRow.get(i).get(5)).getStrText()));

                    }

                    System.out.println(searchList.size());
                    AdapterFive adapterFive = new AdapterFive(ViewAuditActivity.this, searchList);
                    recyclerView.setAdapter(adapterFive);
                    adapterFive.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void searchAuditList(){
       searchAuditIDIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(ViewAuditActivity.this);

                dialog.setContentView(R.layout.row_spinner);
                dialog.setCancelable(true);

                spinner = dialog.findViewById(R.id.spinnerAudit);
                AsyncSetAuditIDSpinner asyncSetAuditIDSpinner = new AsyncSetAuditIDSpinner(ViewAuditActivity.this);
                asyncSetAuditIDSpinner.execute();

                Button button = (Button) dialog.findViewById(R.id.button1);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        spinnerAuditItem = auditID.get(position);
                        spinnerAuditPos = position;

                        System.out.println(spinnerAuditItem);
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
                        if(spinnerAuditPos != 0) {
                            AsyncGetAuditDtlSqlite asyncGetAuditDtlSqlite = new AsyncGetAuditDtlSqlite(ViewAuditActivity.this);
                            asyncGetAuditDtlSqlite.execute();

                            txtAuditID.setText("Audit ID : " + spinnerAuditItem);
                        }
                        dialog.dismiss();
                    }
                });
                dialog.show();

            }
        });
    }

    public void initRecyclerViewDetails() {
        recyclerView =  findViewById(R.id.bodyData);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerViewHeader = findViewById(R.id.headerData);
        recyclerViewHeader.setHasFixedSize(true);
        recyclerViewHeader.setLayoutManager(layoutManagerHeader);
        recyclerViewHeader.setItemAnimator(new DefaultItemAnimator());
    }

    public void initHeader(Context context, ArrayList arrayList) {
        //System.out.println(arrayList);
        AdapterFiveHeader adapterFiveHeader = new AdapterFiveHeader(context, arrayList);
        recyclerViewHeader.setAdapter(adapterFiveHeader);
        adapterFiveHeader.notifyDataSetChanged();
    }

    public void doRecycler() {
        ArrayList thisList = listOfItemRow.get(0);
        if (listOfItemRow.size() > 0) {
            ArrayList<AdapterFiveModel> listModel = new ArrayList<>();
            for (int i = 0; i < listOfItemRow.size(); i++) {

                listModel.add(new AdapterFiveModel(((SingleModelItem)listOfItemRow.get(i).get(1)).getStrText(), ((SingleModelItem)listOfItemRow.get(i).get(2)).getStrText(),
                        ((SingleModelItem)listOfItemRow.get(i).get(3)).getStrText(), ((SingleModelItem)listOfItemRow.get(i).get(4)).getStrText(),
                        ((SingleModelItem)listOfItemRow.get(i).get(5)).getStrText()));
            }

            //  System.out.println(listModel.get(0).getLblText1());
            AdapterFive adapterFive = new AdapterFive(ViewAuditActivity.this, listModel);
            recyclerView.setAdapter(adapterFive);
            adapterFive.notifyDataSetChanged();

        }
    }

    public class AsyncGetAuditDtlSqlite extends AsyncTask<Object, String, ArrayList> {
        Context context;
        ProgressDialog progressDialog;

        public AsyncGetAuditDtlSqlite(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(ViewAuditActivity.this);
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected ArrayList doInBackground(Object... objects) {
            ArrayList isSuccess = getAuditDTLSqlite(CommonDBItem.SQLiteAuditDTl,spinnerAuditItem);
            System.out.println(isSuccess);
            return isSuccess;
        }

        @Override
        protected void onPostExecute(ArrayList arrayList) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
                if (arrayList.size() > 0) {
                    if (arrayList.size() > 0 && arrayList != null) {

                        listOfItemRow = new ArrayList<>();

                        for (int i = 0; i < arrayList.size(); i++) {
                            int dataPosition = i + 1;
                            listOfItemRow.add(populateViewData(dataPosition, (HashMap)arrayList.get(i)));
                        }

                        doRecycler();
                    }
                } else {
                    customAlertDialog.DialogWarningAlert(ViewAuditActivity.this, "Audit ID Detail",
                            "Audit ID item not Available").show();
                }
            }
        }

    }

    public ArrayList getAuditDTLSqlite(String strTableName, String auditID) {
        ArrayList arrayList = new ArrayList();
        SqliteDBHelper sqliteDBHelper = new SqliteDBHelper(ViewAuditActivity.this, getString(R.string.appdb_name), getFilesDir().getPath());

        if (sqliteDBHelper.checkTableExistence(strTableName)) {
            arrayList = sqliteDBHelper.getTableDataFromSqlite("select * from " + strTableName + " where AssetAuditID = '"+auditID+"'");
        } else { System.out.println("Table Don't Exist"); }

        //  System.out.println("getDoNumberSqlite : " + arrayList);

        return arrayList;
    }

    public ArrayList<SingleModelItem> populateViewData(int dataPosition, HashMap hashData){
        //Set Model Details for item
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        DynaTextModel textModelItem1 =  new DynaTextModel(ViewAuditActivity.this.getResources().getColor(R.color.satoColor, getTheme()), 14);
        textModelItem1.setDrawableModel(new DynaDrawableModel(ViewAuditActivity.this.getResources().getColor(R.color.satoColor, getTheme()) , Color.WHITE));
        textModelItem1.setLayoutParam(new DynaLayoutParam(130, LinearLayout.LayoutParams.MATCH_PARENT));

        DynaTextModel textModelItem =  new DynaTextModel(ViewAuditActivity.this.getResources().getColor(R.color.satoColor, getTheme()), 14);
        textModelItem.setDrawableModel(new DynaDrawableModel(ViewAuditActivity.this.getResources().getColor(R.color.satoColor, getTheme()) ,Color.WHITE));
        textModelItem.setLayoutParam(new DynaLayoutParam(500, LinearLayout.LayoutParams.MATCH_PARENT));
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        ArrayList<SingleModelItem> itemDataColumn = new ArrayList();
        itemDataColumn.add(new SingleModelItem("False", textModelItem1, toHide));
        itemDataColumn.add(new SingleModelItem(String.valueOf(dataPosition), textModelItem1, toShow));
        itemDataColumn.add(new SingleModelItem(String.valueOf(hashData.get("ItemID")), textModelItem, toShow));
        itemDataColumn.add(new SingleModelItem(String.valueOf(hashData.get("Description")), textModelItem, toShow));
        itemDataColumn.add(new SingleModelItem(String.valueOf(hashData.get("LocationID")), textModelItem, toShow));
        itemDataColumn.add(new SingleModelItem(String.valueOf(hashData.get("AuditFlag")), textModelItem, toShow));
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        return itemDataColumn;
    }

    public ArrayList<String> getAuditYear(String strTableName){
        ArrayList<String> yearList = new ArrayList();
        ArrayList list = new ArrayList();
        yearList.add("Please Select Year.");
        SqliteDBHelper sqliteDBHelper = new SqliteDBHelper(ViewAuditActivity.this, getString(R.string.appdb_name), getFilesDir().getPath());

        if (sqliteDBHelper.checkTableExistence(strTableName)) {
            list = sqliteDBHelper.getTableDataFromSqlite("select strftime('%Y', CDATE) as CDATE from " + strTableName);
        } else {
            System.out.println("Table Don't Exist");
        }

        for(int i =0;i < list.size();i++){
            if(!yearList.contains(((HashMap)list.get(i)).get("CDATE"))){
               // System.out.println("Hash item : "+list.get(i)+" = "+ ((HashMap)list.get(i)).get("CDATE"));
                yearList.add( String.valueOf(((HashMap)list.get(i)).get("CDATE")));
            }
        }

        return yearList;
    }

    public ArrayList<String> getAuditID(String strTableName){
        ArrayList<String> audit = new ArrayList<>();
        ArrayList list = new ArrayList();
        auditID.clear();
        audit.add("Please Select Audit ID");
        SqliteDBHelper sqliteDBHelper = new SqliteDBHelper(ViewAuditActivity.this, getString(R.string.appdb_name), getFilesDir().getPath());

        if (sqliteDBHelper.checkTableExistence(strTableName)) {
            list = sqliteDBHelper.getTableDataFromSqlite("select Asset_Audit_ID_Search from " + strTableName +" order by CDATE desc");
        } else {
            System.out.println("Table Don't Exist");
        }

        System.out.println("this list :" +list);
        for(int i =0;i < list.size();i++){
            if(!audit.contains(((HashMap)list.get(i)).get("Asset_Audit_ID_Search"))){
                System.out.println("Audit item : "+list.get(i)+" = "+ ((HashMap)list.get(i)).get("Asset_Audit_ID_Search"));

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
            adapter = new ArrayAdapter(ViewAuditActivity.this,R.layout.spinner_custom,auditID);
            spinner.setAdapter(adapter);

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
                startActivity(new Intent(ViewAuditActivity.this, MenuActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
