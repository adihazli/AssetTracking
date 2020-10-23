package com.sato.satoats;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.sato.satoats.Utilities.CommonDBItem;
import com.sato.satoats.Utilities.CommonFunction;
import com.sato.satoats.Utilities.CustomAlertDialog;
import com.sato.satoats.Utilities.DecryptEncrypted;
import com.sato.satoats.Utilities.LogoutDialog;
import com.sato.satoats.Utilities.SqliteDBHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    boolean testInternet;
    //MssqlConnection conn;
    private String[] permissions = { Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int PERMISSION_REQUEST_CODE = 200;

    Button btnLogin;
    EditText userNameET,pwdET;
    SharedPreferences sharedPreferences;
    ImageView settingIV;
    String userName,userPassword;

    CustomAlertDialog customAlertDialog;
    CommonFunction commonFunction = new CommonFunction();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences(getString(R.string.MyPref), Context.MODE_PRIVATE);


        btnLogin = findViewById(R.id.btnLogin);
        settingIV = findViewById(R.id.settingIV);
        userNameET = findViewById(R.id.txtUsername);
        pwdET = findViewById(R.id.txtPassword);

        userNameET.setText("admin");
        userNameET.setSelection(userNameET.getText().length());
        pwdET.setText("123456");
        pwdET.setSelection(pwdET.getText().length());

        buttonLoginTap();
        settingIVTap();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(arePermissionsEnabled()){
//                    permissions granted, continue flow normally
            }else{
                requestMultiplePermissions();
            }
        }

    }

    public void buttonLoginTap() {

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customAlertDialog = new CustomAlertDialog();

                userName = userNameET.getText().toString().trim();
                userPassword = pwdET.getText().toString().trim();
                if (doNetworkReceiver()) {
                    if (!commonFunction.CheckEmptyOrNull(userNameET.getText().toString()) || !commonFunction.CheckEmptyOrNull(pwdET.getText().toString())) {
                        customAlertDialog.DialogWarningAlert(MainActivity.this, "Warning", "Username or Password cannot empty").show();
                    } else {
                        String doLoginResult = doLoginUsingSqliteSelect(CommonDBItem.SQLiteUserData,userName,userPassword);
                        if (doLoginResult.trim().equals("00")) {
                              startActivity(new Intent(MainActivity.this, MenuActivity.class));
                            finish();
                        } else {
                            customAlertDialog.DialogWarningAlert(MainActivity.this, "Warning", doLoginResult).show();
                        }

//                        asyncDoLoginResult asyncDoLoginResult = new asyncDoLoginResult(LoginActivity.this);
//                        asyncDoLoginResult.execute();

                    }
                }
            }
        });
    }

    public void settingIVTap(){
        settingIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
            }
        });
    }

    //check network receiver function
    public boolean checkNetworkReceiver() {
        boolean isConnected = false;
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager.getAllNetworks().length > 0) {
            for (Network strNetwork :connManager.getAllNetworks()) {
                isConnected = connManager.getNetworkInfo(strNetwork).isConnected();
            }
        }
        return isConnected;
    }

    public Boolean doNetworkReceiver () {
        Boolean isConnected = checkNetworkReceiver();
        if (!isConnected) {
            customAlertDialog.DialogWarningAlert(MainActivity.this, "Warning", "Your network provider is Disable\nPlease turn on your network provider.").show();
        }
        return isConnected;
    }

    public String doLoginUsingSqliteSelect(String strTableName, String strUserName, String strPassword) {
        SqliteDBHelper sqliteDBHelper = new SqliteDBHelper(MainActivity.this, getString(R.string.appdb_name), getFilesDir().getPath());
        String isSuccess = "00";
        if (sqliteDBHelper.checkTableExistence(strTableName)) {
            DecryptEncrypted decryptEncrypted = new DecryptEncrypted();
            ArrayList arrayList = sqliteDBHelper.getTableDataFromSqlite("select * from " + strTableName
                    +" where USERNM ='"+strUserName+"' and PWD = '"+decryptEncrypted.DecryptMD5FirstMethod(strPassword.trim()).toUpperCase()+"'");
            System.out.println("this Encrypt : " + decryptEncrypted.DecryptMD5FirstMethod(strPassword.trim()));
            System.out.println("arrayList : " + arrayList);

            if (arrayList.size() > 0 ) {
                for (int i = 0; i < arrayList.size(); i++) {
                    if (String.valueOf(((HashMap)arrayList.get(i)).get("USERNM")).trim().equals(strUserName)
                            && String.valueOf(((HashMap)arrayList.get(i)).get("PWD")).trim().equals(decryptEncrypted.DecryptMD5FirstMethod(strPassword.trim()).toUpperCase())) {

//                        if (String.valueOf(((HashMap)arrayList.get(i)).get("ActiveYN")).trim().equals("N")) {
//                            isSuccess = "User Don't Active. Please active your user";
//                            return isSuccess;
//                        }

                        System.out.println("User ID :" + String.valueOf(((HashMap)arrayList.get(i)).get("USERID")).trim());
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(getString(R.string.username), strUserName);
                        editor.putString(getString(R.string.password), strPassword);
                        editor.putString(getString(R.string.userID),  String.valueOf(((HashMap)arrayList.get(i)).get("USERID")).trim());
                        editor.putString(getString(R.string.plant),  String.valueOf(((HashMap)arrayList.get(i)).get("Plant")).trim());

                        editor.apply();
                        editor.commit();

                        break;

                    } else {
                        isSuccess = "User Don't Exist";
                    }
                }
            } else {
                isSuccess = "Username and password not available";
            }

        } else {
            isSuccess = "Please Sync you phone with database.";
        }

        System.out.println("isSuccess : " + isSuccess);
        return isSuccess;
    }



    //////////////////////////////////Request Permission//////////////////////////////////////////////////////////////
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestMultiplePermissions(){
        List<String> remainingPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                remainingPermissions.add(permission);
            }
        }
        requestPermissions(remainingPermissions.toArray(new String[remainingPermissions.size()]), 101);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean arePermissionsEnabled(){
        for(String permission : permissions){
            if(checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 101){
            for(int i=0;i<grantResults.length;i++){
                if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                    if(shouldShowRequestPermissionRationale(permissions[i])){
                        new AlertDialog.Builder(this)
                                .setMessage("You need to allow the permission for the apps to work properly.")
                                .setPositiveButton("Allow", (dialog, which) -> requestMultiplePermissions())
                                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                                .create()
                                .show();
                    }
                    return;
                }
            }
            //all is good, continue flow
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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

}
