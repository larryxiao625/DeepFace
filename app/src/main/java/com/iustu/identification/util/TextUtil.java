package com.iustu.identification.util;

import android.os.Build;
import android.provider.ContactsContract;
import android.text.Html;
import android.text.Spanned;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.text.Html.FROM_HTML_MODE_LEGACY;

public class TextUtil {
    private static final SimpleDateFormat format2=new SimpleDateFormat("yyyy-MM-dd",Locale.CHINA);
    private static final SimpleDateFormat format3 = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINESE);
    private static final SimpleDateFormat MESSAGE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE);
    private TextUtil(){}

    public static Date fromDateString(String text){
        Date date = null;
        try {
            date = format.parse(text);
        }catch (ParseException e){
            e.printStackTrace();
        }

        return date;
    }

    public static String getDateString(Date date){
        return format.format(date);
    }

    public static String format(String format, Object...os){
        return String.format(Locale.CHINESE, format, os);
    }

    public static String dateMessage(Date date){
        return MESSAGE_FORMAT.format(date);
    }

    public static Spanned fromHtml(String source){
        if(Build.VERSION.SDK_INT < 24){
            return Html.fromHtml(source);
        }else {
            return Html.fromHtml(source, FROM_HTML_MODE_LEGACY);
        }
    }

    public static void enableEdit(boolean enable, EditText...editTexts){
        for (EditText editText : editTexts) {
            editText.setFocusable(enable);
            editText.setFocusableInTouchMode(enable);
        }
    }
    public static String getDateString2(Date date){
        return format2.format(date);
    }

    public static String getHourString(Date date) {return format3.format(date);}

}
