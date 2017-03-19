package com.industries.shins.myexpenses.Utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.industries.shins.myexpenses.R;

/**
 * Created by saga on 19/03/17.
 */

public class DialogMessage {

    public AlertDialog.Builder alertText(int title, int text, Context context) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                context);

        alertDialog
                .setTitle(title)
                .setMessage(text)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        return alertDialog;
    }

    public AlertDialog.Builder alertSalary(int title, int text, Context context) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                context);

        alertDialog
                .setTitle(title)
                .setMessage(text)
                .setView(R.layout.dialog_salary)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        return alertDialog;
    }
}
