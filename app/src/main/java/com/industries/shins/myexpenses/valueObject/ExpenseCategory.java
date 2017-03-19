package com.industries.shins.myexpenses.valueObject;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by saga on 06/03/17.
 */
public class ExpenseCategory {

    public static final String INVESTMENTS = "Investments";
    public static final String BILLS = "Bills";
    public static final String ENJOY = "Enjoy";
    public static final String VEHICLE = "Vehicle";

    @StringDef({INVESTMENTS, BILLS, ENJOY, VEHICLE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Categories{}

}
