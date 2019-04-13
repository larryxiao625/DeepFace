package com.iustu.identification.util;

import android.text.Editable;
import android.text.TextWatcher;

public class MyTextWatcher implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        switch (s.length())
        {
            case 0:
                break;
            case 1:
                oneChar(s);
                break;
            case 2:
                twoChar(s);
                break;
            case 3:
                threeChar(s);
                break;
            case 4:
                fourChar(s);
                break;
            case 5:
                fiveChar(s);
                break;
            default:
                s.delete(s.length()-1,s.length());
                break;
        }
    }

    private void oneChar(Editable s)
    {
        if(!s.toString().equals("0"))
            s.delete(0,1);

    }

    private void twoChar(Editable s)
    {
        if(!s.toString().endsWith("0."))
            s.delete(1,2);
    }

    /*
    处理0.x   0..
     */
    private void threeChar(Editable s)
    {
        if(s.toString().contains(".."))
            s.delete(2,3);
    }

    /*
    处理0.xx 0.x.
     */
    private void fourChar(Editable s)
    {
        if(s.toString().endsWith("."))
            s.delete(s.length()-1,s.length());
    }

    /*
    处理0.xxx  0.xx.
     */
    private void fiveChar(Editable s)
    {
        if(s.toString().endsWith("."))
            s.delete(s.length()-1,s.length());
    }
}
