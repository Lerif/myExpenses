package com.industries.shins.myexpenses.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by saga on 05/05/17.
 */

public class DateUtils {

    public DateFormat monthFormat = new SimpleDateFormat("MM");
    public DateFormat yearFormat = new SimpleDateFormat("yyyy");
    public Date date = new Date();
    public String currentMonth = monthFormat.format(date);
    public String currentYear = yearFormat.format(date);


    public int lastDayOfMonth(int month) {
        return Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);
    }

}
