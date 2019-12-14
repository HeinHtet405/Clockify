package com.koekoetech.clockify.helpers;

import android.text.TextUtils;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Hein Htet Aung on 20/11/19.
 */
@SuppressWarnings("WeakerAccess")
public class DateHelper {

    private static final String TAG = "DateHelper";

    public static String getTodayDate() {
        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat(MyConstant.PATTERN_DMY_SERVER_DASH, Locale.US);
        return df.format(c);
    }

    public static Date getDateFromString(String strDate, String dateFormat) {
        return getDateFromString(strDate, dateFormat, Locale.US);
    }

    public static String formatDate(Date date, String updateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(updateFormat, Locale.US);
        return sdf.format(date);
    }

    public static Date getDateFromString(String strDate, String dateFormat, Locale locale) {
        Log.d(TAG, "getDateFromString() called with: strDate = [" + strDate + "], dateFormat = [" + dateFormat + "], locale = [" + locale + "]");
        if (TextUtils.isEmpty(strDate) || TextUtils.isEmpty(dateFormat)) {
            return null;
        }

        if (locale == null) {
            locale = Locale.US;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, locale);
        try {
            String cleanedDate = strDate.replace("T", " ");
            if (cleanedDate.contains(".")) {
                cleanedDate = cleanedDate.substring(0, cleanedDate.indexOf("."));
            }
            Log.d(TAG, "getDateFromString: Remove T " + cleanedDate);
            return sdf.parse(cleanedDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String serverFormatToLocal(String dateString) {
        DateFormat df = new SimpleDateFormat(MyConstant.PATTERN_SERVER, Locale.US);
        Date result;
        try {
            String formatString = dateString.replace("Z", "+0000");
            result = df.parse(formatString);
            SimpleDateFormat sdf = new SimpleDateFormat(MyConstant.PATTERN_DMY_DASH, Locale.US);
            sdf.setTimeZone(TimeZone.getDefault());
            assert result != null;
            return sdf.format(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String serverFormatToLocalTime(String dateString) {
        DateFormat df = new SimpleDateFormat(MyConstant.PATTERN_SERVER, Locale.US);
        Date result;
        try {
            String formatString = dateString.replace("Z", "+0000");
            result = df.parse(formatString);
            SimpleDateFormat sdf = new SimpleDateFormat(MyConstant.PATTERN_HMS_DASH, Locale.US);
            sdf.setTimeZone(TimeZone.getDefault());
            assert result != null;
            return sdf.format(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String localTimeToServerTime(String time) {
        SimpleDateFormat df = new SimpleDateFormat(MyConstant.PATTERN_HMS_DASH, Locale.US);
        try {
            Date date = df.parse(time);
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            assert date != null;
            return df.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
