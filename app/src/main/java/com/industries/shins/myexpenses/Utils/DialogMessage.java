package com.industries.shins.myexpenses.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.industries.shins.myexpenses.R;

import static com.industries.shins.myexpenses.valueObject.PersonalDataBaseConstants.PERSONAL_SHARED_PREFERENCES_FILE_NAME;
import static com.industries.shins.myexpenses.valueObject.PersonalDataBaseConstants.SALARY_INCOME_KEY;

/**
 * Created by saga on 19/03/17.
 */

public class DialogMessage extends Activity {

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

    public void insertSalary(int title, final SharedPreferences sp, final Context context){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_salary);
        dialog.setTitle(title);

        Button buttonOk = (Button) dialog.findViewById(R.id.dialog_salary_ok);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                EditText income_new =(EditText)dialog.findViewById(R.id.dialog_salary);
                SharedPreferences.Editor editor = sp.edit();
                try {
                    editor.putFloat(SALARY_INCOME_KEY, Float.parseFloat(income_new.getText().toString()));
                    editor.commit();
                } catch (NumberFormatException e){
                    Toast.makeText(context, R.string.salary_not_saved, Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(context, R.string.salary_saved, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        Button buttonCancel = (Button) dialog.findViewById(R.id.dialog_salary_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


}
