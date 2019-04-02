package com.iustu.identification.util;

import android.graphics.Typeface;
import android.widget.TextView;

import com.iustu.identification.App;

/**
 * Created by Liu Yuchuan on 2017/11/13.
 */

public class IconFontUtil {
    private static final Typeface ICONS = Typeface.createFromAsset(App.getContext().getAssets(), "fonts/icons.ttf");
    private static final Typeface MYICONS = Typeface.createFromAsset(App.getContext().getAssets(), "fonts/myicons.ttf");
    public static final String OFF = "\ue607";
    public static final String HISTORY = "\ue699";
    public static final String ALTER = "\ue615";
    public static final String USERNAME = "\ue75a";
    public static final String DELETE = "\ue638";
    public static final String UNSELECT_SINGLE = "\ue609";
    public static final String DELETE_SELECT = "\ue622";
    public static final String CAMERA_SWITCH = "\ue61e";
    public static final String SELECT_SINGLE = "\ue605";
    public static final String SELECT_ALL = "\ue649";
    public static final String FOLDER = "\ue67f";
    public static final String CALEDNAR = "\ue606";
    public static final String UNLOCK = "\ue61d";
    public static final String MORE = "\ue60f";
    public static final String PHOTO = "\ue787";
    public static final String TAKE_PHOTO = "\ue634";
    public static final String ADD = "\ue6bb";
    public static final String SAVE = "\ue66e";
    public static final String ARROW_RIGHT = "\ue504";
    public static final String SELECT_ALL_SQUAD = "\ue61f";
    public static final String ARROW_LEFT = "\ue503";
    public static final String UNSELECT_SQUAD = "\ue851";
    public static final String MEMBER_NEW = "\ue6ba";
    public static final String LOCK = "\ue62a";
    public static final String EMPTY_CIRCLE = "\ue631";
    public static final String PEOPLE_IMPORT = "\ue607";     // 批量导入的图标, &#xe607一定要改成\ue607

    private Typeface mTypeFace;
    private IconFontUtil(Typeface typeface){
        mTypeFace = typeface;
    }

    private static IconFontUtil DEFAULT;

    public static IconFontUtil getDefault(){
        if(DEFAULT == null){
            DEFAULT = new IconFontUtil(ICONS);
        }
        return DEFAULT;
    }

    // 专门为了添加批量导入的图标
    public static IconFontUtil getMyDefault() {
        return new IconFontUtil(MYICONS);
    }

    public static IconFontUtil createFromAsset(String path){
        return new IconFontUtil(Typeface.createFromAsset(App.getContext().getAssets(), path));
    }

    public void changeTypeFace(TextView tv){
        tv.setTypeface(mTypeFace);
    }

    public void setText(TextView tv, String content, boolean change){
        if(change) {
            tv.setTypeface(mTypeFace);
        }
        tv.setText(content);
    }

    public void setText(TextView tv, String content){
        setText(tv, content, true);
    }
}
