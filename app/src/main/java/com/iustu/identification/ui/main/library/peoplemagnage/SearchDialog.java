package com.iustu.identification.ui.main.library.peoplemagnage;

import android.app.DialogFragment;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.iustu.identification.R;
import com.iustu.identification.ui.main.library.peoplemagnage.mvp.PersionPresenter;
import com.iustu.identification.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;

public class SearchDialog extends DialogFragment {

    private String libName;

    private EditText searchName;
    private EditText searchIdentity;
    private Button search;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        libName = getArguments().getString("libName");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_people_manage_search, container, false);
        search = (Button)view.findViewById(R.id.start_search_submmit_bt);
        searchIdentity = (EditText)view.findViewById(R.id.search_identity_et);
        searchName = (EditText)view.findViewById(R.id.search_name_et);
        search.setOnClickListener( v -> {
            startSearch();
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Window dialogWindow = getDialog().getWindow();
        //dialogWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#68030919")));
        dialogWindow.setBackgroundDrawableResource(R.drawable.out_line);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        getDialog().setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        params.alpha = 0.9f;
        dialogWindow.setAttributes(params);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        dialogWindow.setLayout((int)(dm.widthPixels * 0.84), (int)(dm.heightPixels * 0.31));
    }

    // “查询”按钮的点击事件
    private void startSearch() {
        String name = searchName.getText().toString().trim();
        String identity = searchIdentity.getText().toString().trim();
        if ((name.equals("") || name == null) && (identity.equals("") || identity == null)){
            ToastUtil.show("姓名和身份证号码至少填写一个");
            return;
        }
        String searchString = "";
        if (!name.equals("") && name != null)
            searchString += "name = '" + name + "'";
        if (!identity.equals("") && identity != null) {
            if (searchString.equals(""))
                searchString += "identity = '" + identity +"'";
            else
                searchString += "and identity = '" + identity + "'";
        }
        Log.d("search", "startSearch: " + searchString);
        PersionPresenter.searchPerson(libName, searchString, this);
    }

    // 将查询结果返回给Fragment
    public void postBack(int index) {
        if (index == -1) {
            ToastUtil.show("查无此人");
            return;
        } else {
            Log.d("search", "postBack: " + index);
            int n = index % 3;
            int page = index / 3;
            if (n > 0)
                page ++;
            Log.d("search", "postBack: " + page);
            EventBus.getDefault().post(page);
            getDialog().dismiss();
        }
    }

    public void showErrorMessage(String message) {
        ToastUtil.showLong(message);
    }
}
