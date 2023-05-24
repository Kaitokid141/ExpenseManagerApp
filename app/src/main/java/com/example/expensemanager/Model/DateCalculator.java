package com.example.expensemanager.Model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateCalculator {
    public static long calculateDaysDifference(String date1, String date2){
        DateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy");
        long diferenceDays = 0;
        try{
            Date startDate = dateFormat.parse(date1);
            Date endDate = dateFormat.parse(date2);
            long differencelnMillis = endDate.getTime() - startDate.getTime();
            diferenceDays = TimeUnit.DAYS.convert(differencelnMillis, TimeUnit.MILLISECONDS);
        }catch(ParseException e){
            e.printStackTrace();
        }
        return diferenceDays;
    }
    public static boolean checkDays(String date, String date1, String date2){
        boolean check = false;
        if (calculateDaysDifference(date1, date) <= calculateDaysDifference(date1, date2) && calculateDaysDifference(date1, date) >= 0)
            check = true;
        return check;
    }
}
