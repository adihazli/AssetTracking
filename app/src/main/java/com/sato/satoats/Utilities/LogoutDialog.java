package com.sato.satoats.Utilities;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.sato.satoats.MainActivity;


public class LogoutDialog {
    public static void logOut(Context context){
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setMessage("Are you sure you want to Log Out?");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                 context.startActivity(new Intent(context, MainActivity.class));
                ((Activity)context).finish();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Message.message(context,"Cancel Log Out");
            }
        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();

    }

}
