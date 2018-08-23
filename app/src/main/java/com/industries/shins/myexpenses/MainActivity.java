package com.industries.shins.myexpenses;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.industries.shins.myexpenses.Utils.DateUtils;
import com.industries.shins.myexpenses.Utils.DialogMessage;
import com.industries.shins.myexpenses.activity.AddExpense;
import com.industries.shins.myexpenses.adapters.ExpenseAdapter;
import com.industries.shins.myexpenses.entity.Expense;
import com.industries.shins.myexpenses.repository.ExpenseDataBase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.industries.shins.myexpenses.valueObject.DateConstants.DAY_ONE;
import static com.industries.shins.myexpenses.valueObject.PersonalDataBaseConstants.NO_SALARY_SAVED;
import static com.industries.shins.myexpenses.valueObject.PersonalDataBaseConstants.PERSONAL_SHARED_PREFERENCES_FILE_NAME;
import static com.industries.shins.myexpenses.valueObject.PersonalDataBaseConstants.SALARY_INCOME_KEY;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.expenses_cards)
    RecyclerView mRecyclerView;      // Handler

    private RecyclerView.LayoutManager mRecyclerLayoutManager;
    private RecyclerView.Adapter mRecyclerAdapter;  // Use to display data
    private List<Expense> expenses = new ArrayList<Expense>();

    @BindView(R.id.total_expense_cost)
    TextView totalExpenseCost;

    @BindView(R.id.total_left_payment)
    TextView totalLeftPayment;

    @BindView(R.id.total_salary_left)
    TextView leftSalary;
    private ExpenseDataBase db;
    private SharedPreferences sharedPreferences;

    @BindView(R.id.from_date_button)
    Button fromDate;

    @BindView(R.id.to_date_button)
    Button untilDate;

    private Calendar fromDateCalendar = Calendar.getInstance();
    private Calendar untilDateCalendar = Calendar.getInstance();
    private DateUtils dateUtils = new DateUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        db = new ExpenseDataBase(MainActivity.this);
        mRecyclerView.setHasFixedSize(true);

        // Using a linear layout manager
        mRecyclerLayoutManager = new LinearLayoutManager(MainActivity.this);
        mRecyclerView.setLayoutManager(mRecyclerLayoutManager);


        fromDate.setOnClickListener(fromDateHandler);
        fromDate.setText(DAY_ONE + "-" + dateUtils.currentMonth + "-" + dateUtils.currentYear);

        untilDate.setOnClickListener(untilDateHandler);
        untilDate.setText(dateUtils.lastDayOfMonth(Integer.parseInt(dateUtils.currentMonth))
                + "-" + dateUtils.currentMonth + "-" + dateUtils.currentYear);

        FloatingActionButton newExpenseBtn = (FloatingActionButton) findViewById(R.id.add_expense);
        newExpenseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addExpense = new Intent(MainActivity.this, AddExpense.class);
                startActivity(addExpense);
            }
        });
    }


    View.OnClickListener fromDateHandler = new View.OnClickListener() {

        DatePickerDialog.OnDateSetListener dateFrom = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                fromDateCalendar = updateCalendar(fromDateCalendar, fromDate, year, month, dayOfMonth);
                expenses = db.getExpenseInRange(fromDateCalendar, untilDateCalendar);
                refreshCards();
            }
        };

        @Override
        public void onClick(View v) {
            new DatePickerDialog(MainActivity.this, dateFrom, fromDateCalendar
                    .get(Calendar.YEAR), fromDateCalendar.get(Calendar.MONTH),
                    fromDateCalendar.get(Calendar.DAY_OF_MONTH)).show();
        }
    };

    View.OnClickListener untilDateHandler = new View.OnClickListener() {

        DatePickerDialog.OnDateSetListener dateTo = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                untilDateCalendar = updateCalendar(untilDateCalendar, untilDate, year, month, dayOfMonth);
                expenses = db.getExpenseInRange(fromDateCalendar, untilDateCalendar);
                refreshCards();
            }
        };

        @Override
        public void onClick(View v) {
            new DatePickerDialog(MainActivity.this, dateTo, untilDateCalendar
                    .get(Calendar.YEAR), untilDateCalendar.get(Calendar.MONTH),
                    untilDateCalendar.get(Calendar.DAY_OF_MONTH)).show();
        }
    };


    @Override
    public void onResume(){
        super.onResume();

        expenses = db.getCurrentMonthExpenses();
        expenses.addAll(db.getAllPreviouslyUnpaidExpenses());

        refreshCards();
    }

    private void refreshCards(){
        double totalExpenseCost = 0;
        double totalLeftPayment = 0;
        double leftSalary;

        sharedPreferences = getSharedPreferences(PERSONAL_SHARED_PREFERENCES_FILE_NAME,
                MODE_PRIVATE);
        float totalSalary = sharedPreferences.getFloat(SALARY_INCOME_KEY, NO_SALARY_SAVED);

        for(Expense expense : expenses){
            totalExpenseCost += expense.getCost();

            // Get all non paid expenses
            totalLeftPayment += (!expense.isWasItPaid()) ? expense.getCost(): 0;
            float percentage = ((float)expense.getCost() / totalSalary) *100;
            expense.setPercentageOfTotalSalary(percentage);
        }

        leftSalary = totalSalary - totalExpenseCost;

        if(leftSalary > 0) {
            this.leftSalary.setTextColor(Color.GREEN);
        }
        else{
            this.leftSalary.setTextColor(Color.RED);
        }
        this.leftSalary.setText(R.string.currency);
        this.leftSalary.append(String.format("%.2f", leftSalary));

        this.totalLeftPayment.setText(R.string.currency);
        this.totalLeftPayment.append(String.format("%.2f", totalLeftPayment));

        this.totalExpenseCost.setText(R.string.currency);
        this.totalExpenseCost.append(String.format("%.2f ",totalExpenseCost));

        // Specifying adapter
        mRecyclerAdapter = new ExpenseAdapter(expenses, MainActivity.this);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){

            case R.id.action_change_salary:{
                DialogMessage dialogMessage = new DialogMessage();
                dialogMessage.insertSalary(R.string.salary, sharedPreferences, MainActivity.this);
                break;
            }

            case R.id.action_about:{
                DialogMessage dialogMessage = new DialogMessage();
                AlertDialog.Builder alertDialog = dialogMessage.alertText(R.string.about,
                        R.string.designByFlatIcon, MainActivity.this);
                alertDialog.show();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private Calendar updateCalendar(Calendar calendarToSet, Button buttonToUpdate, int year, int month, int dayOfMonth){
        calendarToSet.set(Calendar.YEAR, year);
        calendarToSet.set(Calendar.MONTH, month);
        calendarToSet.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        updateDateOnButton(buttonToUpdate, calendarToSet);

        return calendarToSet;
    }

    private void updateDateOnButton(Button date, Calendar dataToUpdate) {
        String myFormat = "dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        date.setText(sdf.format(dataToUpdate.getTime()));
    }
}
