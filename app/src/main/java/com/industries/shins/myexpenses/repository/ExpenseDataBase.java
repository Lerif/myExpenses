package com.industries.shins.myexpenses.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.industries.shins.myexpenses.Utils.DateUtils;
import com.industries.shins.myexpenses.entity.Expense;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import static com.industries.shins.myexpenses.valueObject.ExpenseDataBaseConstants.CREATE_TABLE;
import static com.industries.shins.myexpenses.valueObject.ExpenseDataBaseConstants.DATABASE_NAME;
import static com.industries.shins.myexpenses.valueObject.ExpenseDataBaseConstants.DATABASE_VERSION;
import static com.industries.shins.myexpenses.valueObject.ExpenseDataBaseConstants.EXPENSE_ID_MATCH;
import static com.industries.shins.myexpenses.valueObject.ExpenseDataBaseConstants.EXPENSE_TABLE_COLUMN_CATEGORY;
import static com.industries.shins.myexpenses.valueObject.ExpenseDataBaseConstants.EXPENSE_TABLE_COLUMN_COST;
import static com.industries.shins.myexpenses.valueObject.ExpenseDataBaseConstants.EXPENSE_TABLE_COLUMN_DUE_DATE;
import static com.industries.shins.myexpenses.valueObject.ExpenseDataBaseConstants.EXPENSE_TABLE_COLUMN_ID;
import static com.industries.shins.myexpenses.valueObject.ExpenseDataBaseConstants.EXPENSE_TABLE_COLUMN_LABEL;
import static com.industries.shins.myexpenses.valueObject.ExpenseDataBaseConstants.EXPENSE_TABLE_COLUMN_PAID;
import static com.industries.shins.myexpenses.valueObject.ExpenseDataBaseConstants.EXPENSE_TABLE_NAME;
import static com.industries.shins.myexpenses.valueObject.ExpenseDataBaseConstants.GET_ALL_EXPENSES_RAW_QUERY;
import static com.industries.shins.myexpenses.valueObject.ExpenseDataBaseConstants.GET_DAY;
import static com.industries.shins.myexpenses.valueObject.ExpenseDataBaseConstants.GET_EXPENSE_WICH_ID_IS_EQUAL_RAW_QUERY;
import static com.industries.shins.myexpenses.valueObject.ExpenseDataBaseConstants.GET_MONTH;
import static com.industries.shins.myexpenses.valueObject.ExpenseDataBaseConstants.GET_YEAR;
import static com.industries.shins.myexpenses.valueObject.ExpenseDataBaseConstants.SQL_ERROR;
import static com.industries.shins.myexpenses.valueObject.ExpenseDataBaseConstants.UPGRADE_TABLE;
import static com.industries.shins.myexpenses.valueObject.PersonalDataBaseConstants.PERCENTAGE_UNDEFINED;
import static com.industries.shins.myexpenses.valueObject.PersonalDataBaseConstants.ZERO_COST;

/**
 * Created by saga on 09/03/17.
 */

public class ExpenseDataBase extends SQLiteOpenHelper {

    public ExpenseDataBase(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL(UPGRADE_TABLE);
        this.onCreate(db);
        db.close();
    }

    public long saveExpense(Expense expense){
        SQLiteDatabase db = this.getWritableDatabase();

        // Setting values into content to save in ExpenseDataBase
        ContentValues cv = new ContentValues();
        cv.put(EXPENSE_TABLE_COLUMN_LABEL, expense.getLabel());
        cv.put(EXPENSE_TABLE_COLUMN_CATEGORY, expense.getCategory());
        cv.put(EXPENSE_TABLE_COLUMN_COST, expense.getCost());
        cv.put(EXPENSE_TABLE_COLUMN_PAID, expense.isWasItPaid());
        cv.put(EXPENSE_TABLE_COLUMN_DUE_DATE, expense.getDueDate());

        long id = db.insert(EXPENSE_TABLE_NAME, null, cv);

        db.close();
        return id;
    }

    public int updateExpense(Expense expense, int id){

        int numberOfRowsAffected;
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(EXPENSE_TABLE_COLUMN_LABEL, expense.getLabel());
        cv.put(EXPENSE_TABLE_COLUMN_CATEGORY, expense.getCategory());
        cv.put(EXPENSE_TABLE_COLUMN_COST, expense.getCost());
        cv.put(EXPENSE_TABLE_COLUMN_PAID, expense.isWasItPaid());
        cv.put(EXPENSE_TABLE_COLUMN_DUE_DATE, expense.getDueDate());

        numberOfRowsAffected = db.update(EXPENSE_TABLE_NAME, cv, EXPENSE_ID_MATCH,
                new String[]{ String.valueOf(id) });
        db.close();
        return numberOfRowsAffected;

    }

    public void removeExpense(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(EXPENSE_TABLE_NAME, EXPENSE_ID_MATCH, new String[]{ String.valueOf(id) });
        db.close();
    }

    public Expense getExpenseWithId(int id){

        Expense expense = new Expense(null, null, 0, false, 0, null);
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(GET_EXPENSE_WICH_ID_IS_EQUAL_RAW_QUERY + id, null);

        try{
            cursor.moveToFirst();

            do{
                expense.setLabel(cursor.getString(cursor.getColumnIndex(EXPENSE_TABLE_COLUMN_LABEL)));
                expense.setCategory(cursor.getString(cursor.getColumnIndex(EXPENSE_TABLE_COLUMN_CATEGORY)));
                expense.setCost(cursor.getDouble(cursor.getColumnIndex(EXPENSE_TABLE_COLUMN_COST)));
                if(cursor.getInt(cursor.getColumnIndex(EXPENSE_TABLE_COLUMN_PAID)) > 0){
                    expense.setWasItPaid(true);
                }
                else {
                    expense.setWasItPaid(false);
                }
                expense.setDueDate(cursor.getString(cursor.getColumnIndex(EXPENSE_TABLE_COLUMN_DUE_DATE)));

            }while(cursor.moveToNext());
        } catch (Exception e){
            Log.e(SQL_ERROR, e.toString());
            return new Expense(null, null, 0, false, 0, null);
        } finally {
            cursor.close();
            db.close();
        }

        return expense;
    }

    public List<Expense> getAllExpenses(){
        List<Expense> expenses = new ArrayList<Expense>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(GET_ALL_EXPENSES_RAW_QUERY, null);

        try{
            Expense expense ;
            cursor.moveToFirst();

            do{
                // Not doing new everytime will make all the expenses be same
                //  This happens because the expense added will still be the same as the one
                // being filled, until a new expense is generated
                expense = new Expense(null, null, ZERO_COST, false, PERCENTAGE_UNDEFINED, null);
                expense.setId(cursor.getInt(cursor.getColumnIndex(EXPENSE_TABLE_COLUMN_ID)));
                expense.setLabel(cursor.getString(cursor.getColumnIndex(EXPENSE_TABLE_COLUMN_LABEL)));
                expense.setCategory(cursor.getString(cursor.getColumnIndex(EXPENSE_TABLE_COLUMN_CATEGORY)));
                expense.setCost(cursor.getDouble(cursor.getColumnIndex(EXPENSE_TABLE_COLUMN_COST)));
                if(cursor.getInt(cursor.getColumnIndex(EXPENSE_TABLE_COLUMN_PAID)) > 0){
                    expense.setWasItPaid(true);
                }
                else {
                    expense.setWasItPaid(false);
                }
                expense.setDueDate(cursor.getString(cursor.getColumnIndex(EXPENSE_TABLE_COLUMN_DUE_DATE)));

                expenses.add(expense);
            }while(cursor.moveToNext());
        } catch (Exception e){
            Log.e(SQL_ERROR, e.toString());
            return new ArrayList<Expense>();
        } finally {
            cursor.close();
            db.close();
        }
        return expenses;
    }

    /**
     * Get all unpaid expenses from past months
     */
    public List<Expense> getAllPreviouslyUnpaidExpenses(){
        List<Expense> unpaidExpenses = new ArrayList<Expense>();
        DateUtils dateUtils = new DateUtils();

        for(Expense expense : getAllExpenses()){

            // Breaking date string from date base
            String[] expenseDate = expense.getDueDate().split("-");

            // Checking if there is any unpaid expenses from previous months
            if((Integer.parseInt(expenseDate[GET_MONTH]) < Integer.parseInt(dateUtils.currentMonth))
                    && (Integer.parseInt(expenseDate[GET_YEAR]) <= Integer.parseInt(dateUtils.currentYear))
                    && (!expense.isWasItPaid())){
                unpaidExpenses.add(expense);
            }
        }


        return unpaidExpenses;
    }

    public List<Expense> getCurrentMonthExpenses(){
        List<Expense> monthExpenses = new ArrayList<Expense>();
        DateUtils dateUtils = new DateUtils();

        for(Expense expense : getAllExpenses()){

            // Breaking date string from date base
            String[] expenseDate = expense.getDueDate().split("-");

            // Checking if expense is of current month and year
            if((Integer.parseInt(expenseDate[GET_MONTH]) >= Integer.parseInt(dateUtils.currentMonth))
                    && (Integer.parseInt(expenseDate[GET_YEAR]) >= Integer.parseInt(dateUtils.currentYear))){
                monthExpenses.add(expense);
            }
        }

        return  monthExpenses;
    }

    public List<Expense> getExpensesStartingFrom(Calendar fromDate){
        List<Expense> rangeExpenses = new ArrayList<Expense>();

        for(Expense expense : getAllExpenses()){

            // Breaking date string from expense in data base
            String[] expenseDateInDB = expense.getDueDate().split("-");

            // Checking if expense year is greater than searched year
            if((Integer.parseInt(expenseDateInDB[GET_YEAR]) > (fromDate.get(Calendar.YEAR)))){
                rangeExpenses.add(expense);
            }
            // Checking if expense is from same year, and if month is greater than searched month
            // +1 because calendar month starts at 0
            else if((Integer.parseInt(expenseDateInDB[GET_YEAR]) == (fromDate.get(Calendar.YEAR)))
                    && (Integer.parseInt(expenseDateInDB[GET_MONTH]) > (fromDate.get(Calendar.MONTH) + 1))){
                rangeExpenses.add(expense);

            }
            // Checking if expense is from same year and month, and if day is greater than searched day
            // +1 because calendar month starts at 0
            else if ((Integer.parseInt(expenseDateInDB[GET_YEAR]) == (fromDate.get(Calendar.YEAR)))
                    && (Integer.parseInt(expenseDateInDB[GET_MONTH]) == (fromDate.get(Calendar.MONTH) + 1))
                    && (Integer.parseInt(expenseDateInDB[GET_DAY]) >= (fromDate.get(Calendar.DAY_OF_MONTH)))){
                rangeExpenses.add(expense);
            }

        }


        return rangeExpenses;
    }

    public List<Expense> getExpensesUntil(Calendar untilDate){
        List<Expense> rangeExpenses = new ArrayList<Expense>();

        for(Expense expense : getAllExpenses()){
            // Breaking date string from expense in date base
            String[] expenseDateInDB = expense.getDueDate().split("-");

            // Checking if expense year is before than searched year
            if((Integer.parseInt(expenseDateInDB[GET_YEAR]) > (untilDate.get(Calendar.YEAR)))){
                rangeExpenses.add(expense);
            }
            // Checking if expense is from same year, and if month is before than searched month
            // +1 because calendar month starts at 0
            else if((Integer.parseInt(expenseDateInDB[GET_YEAR]) == (untilDate.get(Calendar.YEAR)))
                    && (Integer.parseInt(expenseDateInDB[GET_MONTH]) < (untilDate.get(Calendar.MONTH) + 1))){
                rangeExpenses.add(expense);

            }
            // Checking if expense is from same year and month, and if day is before than searched day
            // +1 because calendar month starts at 0
            else if ((Integer.parseInt(expenseDateInDB[GET_YEAR]) == (untilDate.get(Calendar.YEAR)))
                    && (Integer.parseInt(expenseDateInDB[GET_MONTH]) == (untilDate.get(Calendar.MONTH) + 1))
                    && (Integer.parseInt(expenseDateInDB[GET_DAY]) <= (untilDate.get(Calendar.DAY_OF_MONTH)))){
                rangeExpenses.add(expense);
            }
        }

        return rangeExpenses;
    }

    public List<Expense> getExpenseInRange(Calendar fromDate, Calendar toDate){
        List<Expense> expensesFromDate = getExpensesStartingFrom(fromDate);
        List<Expense> expensesToDate = getExpensesUntil(toDate);

        return commonExpense(expensesFromDate, expensesToDate);
    }

    private List<Expense> commonExpense(List<Expense> expensesPrimary, List<Expense> expensesSecondary){
        List<Expense> result = new ArrayList<>();

        for(Expense expenseOne : expensesPrimary){
            for(Expense expenseTwo : expensesSecondary){
                if(expenseOne.getId() == expenseTwo.getId()) {
                    result.add(expenseOne);
                }
            }
        }

        return result;
    }


}
