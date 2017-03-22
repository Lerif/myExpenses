package com.industries.shins.myexpenses;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;

import com.industries.shins.myexpenses.Utils.DialogMessage;
import com.industries.shins.myexpenses.activity.AddExpense;
import com.industries.shins.myexpenses.adapters.ExpenseAdapter;
import com.industries.shins.myexpenses.entity.Expense;
import com.industries.shins.myexpenses.repository.ExpenseDataBase;

import java.util.ArrayList;
import java.util.List;

import static com.industries.shins.myexpenses.valueObject.PersonalDataBaseConstants.NO_SALARY_SAVED;
import static com.industries.shins.myexpenses.valueObject.PersonalDataBaseConstants.PERSONAL_SHARED_PREFERENCES_FILE_NAME;
import static com.industries.shins.myexpenses.valueObject.PersonalDataBaseConstants.SALARY_INCOME_KEY;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;      // Handler
    private RecyclerView.LayoutManager mRecyclerLayoutManager;
    private RecyclerView.Adapter mRecyclerAdapter;  // Use to display data
    private List<Expense> expenses = new ArrayList<Expense>();
    private TextView totalExpenseCost;
    private TextView totalLeftPayment;
    private TextView leftSalary;
    private ExpenseDataBase db;
    private  SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new ExpenseDataBase(MainActivity.this);

        mRecyclerView = (RecyclerView) findViewById(R.id.expenses_cards);
        mRecyclerView.setHasFixedSize(true);

        // Using a linear layout manager
        mRecyclerLayoutManager = new LinearLayoutManager(MainActivity.this);
        mRecyclerView.setLayoutManager(mRecyclerLayoutManager);

        totalExpenseCost = (TextView) findViewById(R.id.total_expense_cost);
        totalLeftPayment = (TextView) findViewById(R.id.total_left_payment);
        leftSalary = (TextView) findViewById(R.id.total_salary_left);

        FloatingActionButton newExpenseBtn = (FloatingActionButton) findViewById(R.id.add_expense);
        newExpenseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addExpense = new Intent(MainActivity.this, AddExpense.class);
                startActivity(addExpense);
            }
        });
    }


    @Override
    public void onResume(){
        super.onResume();

        double totalExpenseCost = 0;
        double totalLeftPayment = 0;

        sharedPreferences = getSharedPreferences(PERSONAL_SHARED_PREFERENCES_FILE_NAME,
                MODE_PRIVATE);
        float totalSalary = sharedPreferences.getFloat(SALARY_INCOME_KEY, NO_SALARY_SAVED);

        expenses = db.getAllExpenses();

        for(Expense expense : expenses){
            totalExpenseCost += expense.getCost();

            // Get all non paid expenses
            totalLeftPayment += (!expense.isWasItPaid()) ? expense.getCost(): 0;
            float percentage = ((float)expense.getCost() / totalSalary) *100;
            expense.setPercentageOfTotalSalary(percentage);
        }

        this.totalLeftPayment.setText(String.format("%.2f", totalLeftPayment));
        this.totalExpenseCost.setText(String.format("%.2f ",totalExpenseCost));
        this.leftSalary.setText(String.format("%.2f", (totalSalary - totalExpenseCost)));


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
}
