package com.industries.shins.myexpenses.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.industries.shins.myexpenses.entity.Expense;

import java.util.ArrayList;
import java.util.List;

import static com.industries.shins.myexpenses.valueObject.ExpenseDataBaseConstants.CREATE_TABLE;
import static com.industries.shins.myexpenses.valueObject.ExpenseDataBaseConstants.DATABASE_NAME;
import static com.industries.shins.myexpenses.valueObject.ExpenseDataBaseConstants.DATABASE_VERSION;
import static com.industries.shins.myexpenses.valueObject.ExpenseDataBaseConstants.EXPENSE_TABLE_COLUMN_CATEGORY;
import static com.industries.shins.myexpenses.valueObject.ExpenseDataBaseConstants.EXPENSE_TABLE_COLUMN_COST;
import static com.industries.shins.myexpenses.valueObject.ExpenseDataBaseConstants.EXPENSE_TABLE_COLUMN_DUE_DATE;
import static com.industries.shins.myexpenses.valueObject.ExpenseDataBaseConstants.EXPENSE_TABLE_COLUMN_ID;
import static com.industries.shins.myexpenses.valueObject.ExpenseDataBaseConstants.EXPENSE_TABLE_COLUMN_LABEL;
import static com.industries.shins.myexpenses.valueObject.ExpenseDataBaseConstants.EXPENSE_TABLE_COLUMN_PAID;
import static com.industries.shins.myexpenses.valueObject.ExpenseDataBaseConstants.EXPENSE_TABLE_NAME;
import static com.industries.shins.myexpenses.valueObject.ExpenseDataBaseConstants.EXPENSE_ID_MATCH;
import static com.industries.shins.myexpenses.valueObject.ExpenseDataBaseConstants.GET_ALL_EXPENSES_RAW_QUERY;
import static com.industries.shins.myexpenses.valueObject.ExpenseDataBaseConstants.GET_EXPENSE_WICH_ID_IS_EQUAL_RAW_QUERY;
import static com.industries.shins.myexpenses.valueObject.ExpenseDataBaseConstants.SQL_ERROR;
import static com.industries.shins.myexpenses.valueObject.ExpenseDataBaseConstants.UPGRADE_TABLE;

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
                expense = new Expense(null, null, 0, false, 0, null);
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

}
