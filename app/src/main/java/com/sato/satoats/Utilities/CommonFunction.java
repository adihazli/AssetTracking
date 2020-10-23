package com.sato.satoats.Utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.sato.satoats.MainActivity;
import com.sato.satoats.R;

import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;

public class CommonFunction {
    Context context;
//
//    public void saveListInSP(Context context,ArrayList<String> list, String key) {
//
//        SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.MyPref),Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = prefs.edit();
//        Gson gson = new Gson();
//        String json = gson.toJson(list);
//        editor.putString(key, json);
//        editor.apply();
//
//    }
//
//    public ArrayList<String> getListFromSP(Context context,String key)
//    {
//        SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.MyPref),Context.MODE_PRIVATE);
//        Gson gson = new Gson();
//        String json = prefs.getString(key, null);
//        Type type = new TypeToken<ArrayList<String>>() {}.getType();
//        return gson.fromJson(json, type);
//
//    }
//
    public static void message(Context context, String message,int toastLenght) {

        if ( toastLenght == 0){
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }else if (toastLenght == 1) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }

    }


    public Boolean CheckStringIsNumber(String strData) {
        Boolean isSucces = false;

        isSucces = strData.matches("^(?:(?:\\-{1})?\\d+(?:\\.{1}\\d+)?)$");

        return isSucces;
    }

    public Boolean CheckEmptyOrNull(String strData) {
        Boolean isSuccess = false;
        if (!strData.isEmpty() && strData.trim().length() > 0) {
            isSuccess = true;
            return isSuccess;
        }
        return isSuccess;
    }

    public Integer ConvertStringToNumber(String strData){
        Integer thisInt;
        if (CheckStringIsNumber(strData)) {
            thisInt = Integer.parseInt(strData);
            return thisInt;
        }
        return null;
    }





}
