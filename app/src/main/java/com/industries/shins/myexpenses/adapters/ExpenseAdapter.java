package com.industries.shins.myexpenses.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.industries.shins.myexpenses.R;
import com.industries.shins.myexpenses.entity.Expense;
import com.industries.shins.myexpenses.holder.ExpenseViewHolder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.industries.shins.myexpenses.valueObject.DateConstants.DATE_FORMAT_DD_MM_YYYY;
import static com.industries.shins.myexpenses.valueObject.DateConstants.DUE_DATE;
import static com.industries.shins.myexpenses.valueObject.DateConstants.THREE_DAYS_IN_MILLISECONDS;

/**
 * Created by saga on 06/03/17.
 */
public class ExpenseAdapter extends RecyclerView.Adapter{

    private List<Expense> expenses;
    private Context context;

    public ExpenseAdapter(List<Expense> expenses, Context context){
        this.expenses = expenses;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position){
        ExpenseViewHolder expenseViewHolder = (ExpenseViewHolder) holder;
        Expense expense = expenses.get(position);

        expenseViewHolder.setId(expense.getId());
        expenseViewHolder.label.setText(expense.getLabel());
        expenseViewHolder.category.setText(expense.getCategory());
        expenseViewHolder.dateDue.setText(DUE_DATE + expense.getDueDate());
        expenseViewHolder.cost.setText(String.format("%.2f ",expense.getCost()));
        expenseViewHolder.percentage.setText(String.format("%.2f",expense.getPercentageOfTotalSalary()));
        expenseViewHolder.percentage.append("%");


        // To compare dates parsing string to date format
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_DD_MM_YYYY);
        try {
            Date dateDue = sdf.parse(expense.getDueDate());
            Date dateToday = sdf.parse(currentDateTime());

            if(expense.isWasItPaid()){
                expenseViewHolder.wasItPaid.setImageResource(R.drawable.paid);
            }
            else if((dateDue.getTime() - dateToday.getTime()) < THREE_DAYS_IN_MILLISECONDS){
                expenseViewHolder.wasItPaid.setImageResource(R.drawable.warning_paid);
            }
            else{
                expenseViewHolder.wasItPaid.setImageResource(R.drawable.not_paid);
            }
        }catch (ParseException e) {
            e.printStackTrace();
            expenseViewHolder.wasItPaid.setImageResource(R.drawable.unknow_stauts);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder (ViewGroup parent, int viewType){
        View view = LayoutInflater.from(context).inflate(R.layout.expense_card_view, parent, false);
        ExpenseViewHolder expenseViewHolder = new ExpenseViewHolder(view);
        return expenseViewHolder;
    }

    @Override
    public int getItemCount(){
        try {
            return expenses.size();
        }catch (NullPointerException e){
            return 0;
        }
    }

    private String currentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}