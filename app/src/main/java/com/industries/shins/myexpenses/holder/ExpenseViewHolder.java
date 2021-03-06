package com.industries.shins.myexpenses.holder;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.industries.shins.myexpenses.R;
import com.industries.shins.myexpenses.activity.ExpenseCardViewDetails;

import static com.industries.shins.myexpenses.valueObject.ExpenseDataBaseConstants.EXPENSE_TABLE_COLUMN_ID;

/**
 * Created by saga on 07/03/17.
 */
public class ExpenseViewHolder extends RecyclerView.ViewHolder {

    public int id;
    public ImageView thumbnail;
    public TextView label;
    public TextView category;
    public TextView cost;
    public TextView percentage;
    public ImageView wasItPaid;
    public TextView dateDue;

    public ExpenseViewHolder(View view) {
        super(view);
        this.thumbnail = (ImageView) view.findViewById(R.id.expense_thumbnail);
        this.label = (TextView) view.findViewById(R.id.expense_label);
        this.category = (TextView) view.findViewById(R.id.expense_category);
        this.dateDue = (TextView) view.findViewById(R.id.expense_due_date);
        this.cost = (TextView) view.findViewById(R.id.expense_cost);
        this.percentage = (TextView) view.findViewById(R.id.expense_percentage);
        this.wasItPaid = (ImageView) view.findViewById(R.id.expense_was_paid_icon);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent expenseDetails = new Intent(v.getContext(), ExpenseCardViewDetails.class);
                expenseDetails.putExtra(EXPENSE_TABLE_COLUMN_ID, id);
                v.getContext().startActivity(expenseDetails);
            }
        });
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}