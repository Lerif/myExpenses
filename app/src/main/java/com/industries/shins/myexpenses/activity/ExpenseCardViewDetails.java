package com.industries.shins.myexpenses.activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.industries.shins.myexpenses.MainActivity;
import com.industries.shins.myexpenses.R;
import com.industries.shins.myexpenses.entity.Expense;
import com.industries.shins.myexpenses.repository.ExpenseDataBase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.industries.shins.myexpenses.valueObject.ExpenseDataBaseConstants.EXPENSE_TABLE_COLUMN_ID;

public class ExpenseCardViewDetails extends AppCompatActivity {

    private static final String LOG_CLASS = "ExpenseDetail";

    private Button addExpense;
    private Button deleteExpense;
    private EditText label;
    private Spinner category;
    private EditText cost;
    private CheckBox paid;
    private Button dateDue;
    private Calendar calendar = Calendar.getInstance();
    private ExpenseDataBase db;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_card_view_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bundle = getIntent().getExtras();
        db = new ExpenseDataBase(ExpenseCardViewDetails.this);

        addExpense = (Button) findViewById(R.id.detail_expense_confirm_button);
        addExpense.setOnClickListener(updateExpenseHandler);

        deleteExpense = (Button) findViewById(R.id.detail_expense_delete_button);
        deleteExpense.setOnClickListener(deleteExpenseHandler);

        // Getting all fields
        label = (EditText) findViewById(R.id.detail_expense_label);
        category = (Spinner) findViewById(R.id.detail_expense_category);
        cost = (EditText)findViewById(R.id.detail_expense_cost);
        paid = (CheckBox)findViewById(R.id.detail_expense_paid);

        dateDue = (Button) findViewById(R.id.detail_expense_date);
        dateDue.setOnClickListener(datePaidHandler);

        setAllFields();
    }


    /*
    * Reads from db the details of the card to fill in all the information*/
    private void setAllFields(){
        Expense retrivedExpense = db.getExpenseWithId(bundle.getInt(EXPENSE_TABLE_COLUMN_ID));
        label.setText(retrivedExpense.getLabel());
        //category.setSelection();
        cost.setText(String.format("%.2f ",retrivedExpense.getCost()));
        paid.setChecked(retrivedExpense.isWasItPaid());
        dateDue.setText(retrivedExpense.getDueDate());

    }

    View.OnClickListener updateExpenseHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Checks if an obliged field is empty
            if(!verifyObligedFields()){
                return;
            }

            Expense saveExpense = new Expense(label.getText().toString(),
                    category.getSelectedItem().toString(), Float.parseFloat(cost.getText().toString()),
                    paid.isChecked(), 0, dateDue.getText().toString());
            db.updateExpense(saveExpense, bundle.getInt(EXPENSE_TABLE_COLUMN_ID));
            Intent mainIntent = new Intent(ExpenseCardViewDetails.this, MainActivity.class);
            startActivity(mainIntent);
            Toast.makeText(ExpenseCardViewDetails.this, R.string.update_successful, Toast.LENGTH_SHORT).show();
        }
    };

    View.OnClickListener deleteExpenseHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder alertDelete = alertDeleteExpense();
            alertDelete.create();
            alertDelete.show();
        }
    };

    private AlertDialog.Builder alertDeleteExpense() {
        AlertDialog.Builder alertDelete = new AlertDialog.Builder(
                ExpenseCardViewDetails.this);

        alertDelete
                .setTitle(R.string.delete_expense_alert_dialog_title)
                .setMessage(R.string.operation_cannot_be_undone)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.removeExpense(bundle.getInt(EXPENSE_TABLE_COLUMN_ID));
                        Toast.makeText(ExpenseCardViewDetails.this, R.string.expense_deleted,
                                Toast.LENGTH_SHORT).show();

                        Intent mainIntent = new Intent(ExpenseCardViewDetails.this,
                                MainActivity.class);
                        startActivity(mainIntent);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        return alertDelete;
    }

    /**
     * Verify all fields that cannot be empty
     * If there is an emprty field returns false
     * If all fields are filled return true
     */
    private boolean verifyObligedFields() {
        if(label.getText().toString().isEmpty() || label.getText().toString().equals(""))
        {
            Toast.makeText(ExpenseCardViewDetails.this, R.string.label_must_be_filled, Toast.LENGTH_SHORT).show();
            return false;
        }
        if(category.getSelectedItem().toString().isEmpty())
        {
            Toast.makeText(ExpenseCardViewDetails.this, R.string.category_must_be_filled, Toast.LENGTH_SHORT).show();
            return false;
        }
        if(cost.getText().toString().isEmpty()){
            Toast.makeText(ExpenseCardViewDetails.this, R.string.cost_must_be_filled, Toast.LENGTH_SHORT).show();
            return false;
        }
        if(dateDue.getText().toString().isEmpty()){
            Toast.makeText(ExpenseCardViewDetails.this, R.string.date_due_must_be_filled, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    View.OnClickListener datePaidHandler = new View.OnClickListener() {
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updatePaidDate();
            }
        };

        @Override
        public void onClick(View v) {
            new DatePickerDialog(ExpenseCardViewDetails.this, date, calendar
                    .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show();
        }
    };

    private void updatePaidDate() {
        String myFormat = "dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        dateDue.setText(sdf.format(calendar.getTime()));
    }

}
