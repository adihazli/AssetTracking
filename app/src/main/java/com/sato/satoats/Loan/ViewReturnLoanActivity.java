package com.sato.satoats.Loan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

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

public class ViewReturnLoanActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView recyclerViewHeader;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.LayoutManager layoutManagerHeader;

    ArrayList searchList = new ArrayList<>();
    ArrayList<ArrayList> listOfItemRow = new ArrayList<>();
    CustomAlertDialog customAlertDialog;

    Spinner spinnerReturnLoanTable,spinnerDay;
    String[] searchBY = {"","Item ID","Description","Doc. No","Location"};
    String[] days = {"Pick Days","60","30","15"};
    int SearchPos = 0,spinnerDayPos = 0;
    String searchDay = "";

    EditText returnloanViewSearchET;
    ImageView searchViewReturnLoanDoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_return_loan);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("View Return Loan Item");

        spinnerReturnLoanTable = findViewById(R.id.viewSpinnerReturnLoanView);
        ArrayAdapter sByFilter = new ArrayAdapter(ViewReturnLoanActivity.this,R.layout.spinner_custom,searchBY);
        spinnerReturnLoanTable.setAdapter(sByFilter);
        returnloanViewSearchET = findViewById(R.id.returnloanViewSearchET);

        spinnerDay = findViewById(R.id.spinnerReturnLoanDay);
        ArrayAdapter sDays = new ArrayAdapter(ViewReturnLoanActivity.this,R.layout.spinner_custom,days);
        spinnerDay.setAdapter(sDays);

        searchViewReturnLoanDoc = findViewById(R.id.searchViewReturnLoanDoc);

        layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManagerHeader = new LinearLayoutManager(getApplicationContext());
        initRecyclerViewDetails();

        //  setCustomRecycView();
        ArrayList<AdapterFiveModel> headerModel = new ArrayList<>();
        headerModel.add(new AdapterFiveModel("No","Item ID", "Item Desc.", "Doc. No", "From Loc."));
        initHeader(getApplicationContext(), headerModel);

        AsyncGetReturnLoanDtlSqlite asyncGetReturnLoanDtlSqlite = new AsyncGetReturnLoanDtlSqlite(ViewReturnLoanActivity.this);
        asyncGetReturnLoanDtlSqlite.execute();

        filterTable();
        searchTable();
        setDays();
        searchLoanList();
    }

    public void setDays(){
        spinnerDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position != 0){
                    spinnerDayPos = position;

                    switch (spinnerDayPos) {
                        case 1:
                            searchDay = days[1];
                            break;
                        case 2:
                            searchDay = days[2];
                            break;
                        case 3:
                            searchDay = days[3];
                            break;

                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void searchLoanList(){
        searchViewReturnLoanDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                AsyncGetReturnLoanDtlSqliteByDay asyncGetReturnLoanDtlSqliteByDay = new AsyncGetReturnLoanDtlSqliteByDay(ViewReturnLoanActivity.this);
//                asyncGetReturnLoanDtlSqliteByDay.execute();

                Message.message(ViewReturnLoanActivity.this,"Set table within " + searchDay + " days");
            }
        });
    }

    public void filterTable(){
        spinnerReturnLoanTable.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                        case 4:
                            searchby = searchBY[4];
                            break;
                    }
                    Message.message(ViewReturnLoanActivity.this,"Search table by " + searchby);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void searchTable(){
        returnloanViewSearchET.addTextChangedListener(new TextWatcher() {
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
                    String text = ((SingleModelItem)listOfItemRow.get(i).get(0)).getStrText().toLowerCase();
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
                    AdapterFive adapterFive = new AdapterFive(ViewReturnLoanActivity.this, searchList);
                    recyclerView.setAdapter(adapterFive);
                    adapterFive.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
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
            AdapterFive adapterFive = new AdapterFive(ViewReturnLoanActivity.this, listModel);
            recyclerView.setAdapter(adapterFive);
            adapterFive.notifyDataSetChanged();

        }
    }

    public ArrayList<SingleModelItem> populateViewData(int dataPosition, HashMap hashData){
        //Set Model Details for item
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        DynaTextModel textModelItem1 =  new DynaTextModel(ViewReturnLoanActivity.this.getResources().getColor(R.color.satoColor, getTheme()), 14);
        textModelItem1.setDrawableModel(new DynaDrawableModel(ViewReturnLoanActivity.this.getResources().getColor(R.color.satoColor, getTheme()) , Color.WHITE));
        textModelItem1.setLayoutParam(new DynaLayoutParam(130, LinearLayout.LayoutParams.MATCH_PARENT));

        DynaTextModel textModelItem =  new DynaTextModel(ViewReturnLoanActivity.this.getResources().getColor(R.color.satoColor, getTheme()), 14);
        textModelItem.setDrawableModel(new DynaDrawableModel(ViewReturnLoanActivity.this.getResources().getColor(R.color.satoColor, getTheme()) ,Color.WHITE));
        textModelItem.setLayoutParam(new DynaLayoutParam(500, LinearLayout.LayoutParams.MATCH_PARENT));
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        ArrayList<SingleModelItem> itemDataColumn = new ArrayList();
        itemDataColumn.add(new SingleModelItem("False", textModelItem1, toHide));
        itemDataColumn.add(new SingleModelItem(String.valueOf(dataPosition), textModelItem1, toShow));
        itemDataColumn.add(new SingleModelItem(String.valueOf(hashData.get("ItemID")), textModelItem, toShow));
        itemDataColumn.add(new SingleModelItem(String.valueOf(hashData.get("Description")), textModelItem, toShow));
        itemDataColumn.add(new SingleModelItem(String.valueOf(hashData.get("DocumentNo")), textModelItem, toShow));
        itemDataColumn.add(new SingleModelItem(String.valueOf(hashData.get("FromLocationID")), textModelItem, toShow));
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        return itemDataColumn;
    }

    public ArrayList getReturnLoanDTLSqlite(String strTableName) {
        ArrayList arrayList = new ArrayList();
        SqliteDBHelper sqliteDBHelper = new SqliteDBHelper(ViewReturnLoanActivity.this, getString(R.string.appdb_name), getFilesDir().getPath());

        if (sqliteDBHelper.checkTableExistence(strTableName)) {
            arrayList = sqliteDBHelper.getTableDataFromSqlite("select * from " + strTableName + " where Status = '1'");
        } else { System.out.println("Table Don't Exist"); }


        return arrayList;
    }

    public class AsyncGetReturnLoanDtlSqlite extends AsyncTask<Object, String, ArrayList> {
        Context context;
        ProgressDialog progressDialog;

        public AsyncGetReturnLoanDtlSqlite(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(ViewReturnLoanActivity.this);
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected ArrayList doInBackground(Object... objects) {
            ArrayList isSuccess = getReturnLoanDTLSqlite(CommonDBItem.SQLiteLoanItemDtl);
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
                    customAlertDialog.DialogWarningAlert(ViewReturnLoanActivity.this, "Audit ID Detail",
                            "Audit ID item not Available").show();
                }
            }
        }

    }

//    public class AsyncGetReturnLoanDtlSqliteByDay extends AsyncTask<Object, String, ArrayList> {
//        Context context;
//        ProgressDialog progressDialog;
//
//        public AsyncGetReturnLoanDtlSqliteByDay(Context context) {
//            this.context = context;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            progressDialog = new ProgressDialog(ViewReturnLoanActivity.this);
//            progressDialog.show();
//            super.onPreExecute();
//        }
//
//        @Override
//        protected ArrayList doInBackground(Object... objects) {
//            ArrayList isSuccess = getReturnLoanDTLSqliteByDay(CommonDBItem.SQLiteLoanItemDtl, searchDay);
//            System.out.println(isSuccess);
//            return isSuccess;
//        }
//
//        @Override
//        protected void onPostExecute(ArrayList arrayList) {
//            if (progressDialog.isShowing()) {
//                progressDialog.dismiss();
//                if (arrayList.size() > 0) {
//                    if (arrayList.size() > 0 && arrayList != null) {
//
//                        listOfItemRow = new ArrayList<>();
//
//                        for (int i = 0; i < arrayList.size(); i++) {
//                            int dataPosition = i + 1;
//                            listOfItemRow.add(populateViewData(dataPosition, (HashMap)arrayList.get(i)));
//                        }
//
//                        doRecycler();
//                    }
//                } else {
//                    customAlertDialog.DialogWarningAlert(ViewReturnLoanActivity.this, "Return Loan Item Detail",
//                            "Return loan item not Available").show();
//                }
//            }
//        }
//
//    }

//    public ArrayList getReturnLoanDTLSqliteByDay (String strTableName,String day) {
//        ArrayList arrayList = new ArrayList();
//        SqliteDBHelper sqliteDBHelper = new SqliteDBHelper(ViewReturnLoanActivity.this, getString(R.string.appdb_name), getFilesDir().getPath());
//
//        if (sqliteDBHelper.checkTableExistence(strTableName)) {
//            arrayList = sqliteDBHelper.getTableDataFromSqlite("select * from " + strTableName + " where Status = '1' and strftime('%Y', OutDate) = '2020'");
//        } else { System.out.println("Table Don't Exist"); }
//
//        //  System.out.println("getDoNumberSqlite : " + arrayList);
//
//        return arrayList;
//    }

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
                startActivity(new Intent(ViewReturnLoanActivity.this, MenuActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
