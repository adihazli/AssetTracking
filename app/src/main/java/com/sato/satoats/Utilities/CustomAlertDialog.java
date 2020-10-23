package com.sato.satoats.Utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import com.sato.satoats.R;


public class CustomAlertDialog {
    Context mContext;
    Class<?> mMainContext;
    SharedPreferences sharedPreferences;

    public CustomAlertDialog() {
    }

    public AlertDialog DialogChangeActivity(final Context context, final Class<?> mainContext, String strTitle, String strMessage) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(strTitle);
        alertDialog.setMessage(strMessage);

        alertDialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                context.startActivity(new Intent(context, mainContext));
                ((Activity)context).finish();

                sharedPreferences = context.getSharedPreferences(context.getString(R.string.MyPref), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString(context.getString(R.string.username), "");
                editor.putString(context.getString(R.string.password), "");
                editor.putString(context.getString(R.string.userID),  "");

                editor.apply();
                editor.commit();
            }
        });

        alertDialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        return alertDialog.create();
    }

    public AlertDialog DialogWarningAlert(Context context, String strTitle, String strMsg) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setIcon(context.getResources().getDrawable(R.drawable.ico_warn, context.getTheme()));
        alertDialog.setTitle(strTitle);
        alertDialog.setMessage(strMsg);
        alertDialog.setNegativeButton("OK", null);
        return alertDialog.create();
    }

}
