package com.industries.shins.myexpenses.valueObject;

/**
 * Created by saga on 09/03/17.
 */

public class ExpenseDataBaseConstants {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "expense_database";
    public static final String EXPENSE_TABLE_NAME = "expense";
    public static final String EXPENSE_TABLE_COLUMN_ID = "id";
    public static final String EXPENSE_TABLE_COLUMN_LABEL = "label";
    public static final String EXPENSE_TABLE_COLUMN_CATEGORY = "category";
    public static final String EXPENSE_TABLE_COLUMN_COST = "cost";
    public static final String EXPENSE_TABLE_COLUMN_PAID = "paid";
    public static final String EXPENSE_TABLE_COLUMN_DUE_DATE = "due_date";
    public static final String EXPENSE_ID_MATCH = EXPENSE_TABLE_COLUMN_ID +" = ?";

    public static final String CREATE_TABLE = "CREATE TABLE " +
            EXPENSE_TABLE_NAME + " (" +
            EXPENSE_TABLE_COLUMN_ID + " INTEGER PRIMARY KEY," +
            EXPENSE_TABLE_COLUMN_LABEL + " VARCHAR(255)," +
            EXPENSE_TABLE_COLUMN_CATEGORY + " VARCHAR(255), " +
            EXPENSE_TABLE_COLUMN_COST + " FLOAT," +
            EXPENSE_TABLE_COLUMN_PAID + " BOOLEAN," +
            EXPENSE_TABLE_COLUMN_DUE_DATE + " DATETIME " + ")";

    public static final String UPGRADE_TABLE = "DROPE TABLE IF EXISTS " + EXPENSE_TABLE_NAME;

    public static final String GET_ALL_EXPENSES_RAW_QUERY = "SELECT * FROM " + EXPENSE_TABLE_NAME;

    public static final String GET_EXPENSE_WICH_ID_IS_EQUAL_RAW_QUERY = "SELECT * FROM "
            + EXPENSE_TABLE_NAME
            + " WHERE " + EXPENSE_TABLE_COLUMN_ID + " = " ;

    public static final String GET_ALL_UNPAID_EXPENSES_RAW_QUERY = "SELECT * FROM " +
            EXPENSE_TABLE_NAME + " WHERE " + EXPENSE_TABLE_COLUMN_PAID + " = " + 0 + " AND " +
            EXPENSE_TABLE_COLUMN_DUE_DATE + " > ";

    public static final int GET_MONTH = 1;
    public static final int GET_YEAR = 2;

    public static final String SQL_ERROR = "SQL error: ";



}
