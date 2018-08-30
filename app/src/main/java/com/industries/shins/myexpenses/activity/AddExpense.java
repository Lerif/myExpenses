package com.industries.shins.myexpenses.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.industries.shins.myexpenses.R;
import com.industries.shins.myexpenses.entity.Expense;
import com.industries.shins.myexpenses.repository.ExpenseDataBase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddExpense extends AppCompatActivity {

    @BindView(R.id.add_expense_label)
    EditText label;

    @BindView(R.id.add_expense_category)
    Spinner category;

    @BindView(R.id.add_expense_cost)
    EditText cost;

    @BindView(R.id.add_expense_paid)
    CheckBox paid;

    @BindView(R.id.add_expense_date)
    Button datePaid;

    Calendar calendar = Calendar.getInstance();

    private ExpenseDataBase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        ButterKnife.bind(this);

        db = new ExpenseDataBase(AddExpense.this);

        datePaid.setText(currentDateTime());
        datePaid.setOnClickListener(datePaidHandler);
    }

    @OnClick(R.id.add_expense_confirm_button)
    public void confirmExpense(){
        // Checks if an obliged field is empty
        if(!verifyObligedFields()){
            return;
        }

        Expense saveExpense = new Expense(label.getText().toString(),
                category.getSelectedItem().toString(), Float.parseFloat(cost.getText().toString()),
                paid.isChecked(), 0, datePaid.getText().toString());
        db.saveExpense(saveExpense);
        clearAllFields();
        Toast.makeText(AddExpense.this, R.string.save_successful, Toast.LENGTH_SHORT).show();
    }


    /**
     * Verify all fields that cannot be empty
     * If there is an empty field returns false
     * If all fields are filled return true
     */
    private boolean verifyObligedFields() {
        if(label.getText().toString().isEmpty() || label.getText().toString().equals(""))
        {
            Toast.makeText(AddExpense.this, R.string.label_must_be_filled, Toast.LENGTH_SHORT).show();
            return false;
        }
        if(category.getSelectedItem().toString().isEmpty())
        {
            Toast.makeText(AddExpense.this, R.string.category_must_be_filled, Toast.LENGTH_SHORT).show();
            return false;
        }
        if(cost.getText().toString().isEmpty()){
            Toast.makeText(AddExpense.this, R.string.cost_must_be_filled, Toast.LENGTH_SHORT).show();
            return false;
        }
        if(datePaid.getText().toString().isEmpty()){
            Toast.makeText(AddExpense.this, R.string.date_due_must_be_filled, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void clearAllFields(){
        label.setText("");
        category.setSelection(0);
        cost.setText("");
        paid.setChecked(false);
        datePaid.setText(currentDateTime());

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
            new DatePickerDialog(AddExpense.this, date, calendar
                    .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show();
        }
    };

    private void updatePaidDate() {
        String myFormat = "dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        datePaid.setText(sdf.format(calendar.getTime()));
    }


    private String currentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}
