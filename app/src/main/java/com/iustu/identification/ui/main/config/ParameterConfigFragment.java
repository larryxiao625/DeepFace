package com.iustu.identification.ui.main.config;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.iustu.identification.R;
import com.iustu.identification.config.ParametersConfig;
import com.iustu.identification.ui.base.BaseFragment;
import com.iustu.identification.ui.widget.seekbar.BubbleSeekBar;
import com.iustu.identification.util.PickerViewFactor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Liu Yuchuan on 2017/11/5.
 *
 * 该界面就是一些列关于人脸库的参数的设置
 * 逻辑分析：
 * 1. 初始化控件
 * 2. 保存参数到一个对像(ParametersConfig)中
 *
 * 值得注意的是BubbleSeekBar获取的百分比只能通过OnPrecessChange方法
 * 中的参数，而且其参数process的值的范围是0-1000，所以要获取具体的
 * 百分比，就要除以1000
 *
 * 修改方案：
 * 1. 修改onProgressChange
 * 2. onHide和onPause中的逻辑由Presenter实现
 */

public class ParameterConfigFragment extends BaseFragment implements BubbleSeekBar.OnProgressChangeListener{
    private ParametersConfig parametersConfig;

    private static final String FORMAT_COUNT = "%d";

    @BindView(R.id.face_bsb)
    BubbleSeekBar faceSeekBar;
    @BindView(R.id.display_count_tv)
    TextView displayCountTv;
    @BindView(R.id.save_count_tv)
    TextView saveCountTv;
    @BindView(R.id.min_face)
    EditText minFace;
    @BindView(R.id.check_factor1)
    EditText checkFactor1;
    @BindView(R.id.check_factor2)
    EditText checkFactor2;
    @BindView(R.id.check_factor3)
    EditText checkFactor3;
    @BindView(R.id.save_dpi_tv)
    TextView dpiSetTv;

    private OptionsPickerView displayCountPicker;
    private OptionsPickerView saveCountPicker;
    private OptionsPickerView dpiPicker;

    private List<Integer> displayCountList = new ArrayList<>();         // 设置显示结果数量的源
    private List<Integer> saveCountList = new ArrayList<>();          // 设置保存记录数的源
    private List<Integer> dpiWidth=new ArrayList<>();     // 设置分辨率宽度的源
    private List<Integer> dpiHeight=new ArrayList<>();       // 设置分辨率高度的源
    private List<String> dpiStringList=new ArrayList<>();       // 设置分辨率的源

    String dpi;

    @Override
    protected int postContentView() {
        return R.layout.fragment_parameter_config;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState, View view) {
        super.initView(savedInstanceState, view);
        parametersConfig = ParametersConfig.getInstance();
        faceSeekBar.setProgress((int) (parametersConfig.getThresholdValueFace() * 1000));
        faceSeekBar.setOnProgressChangeListener(this);
        displayCountTv.setText(String.valueOf(parametersConfig.getDisplayCount()));

        for(int i = 10; i <= 50; i++){
            displayCountList.add(i);
        }
        saveCountList.add(0);
        saveCountList.add(1000);
        saveCountList.add(5000);
        saveCountList.add(10000);
        dpiWidth.add(1920);
        dpiWidth.add(1280);
        dpiWidth.add(2048);
        dpiWidth.add(1600);
        dpiWidth.add(1280);
        dpiWidth.add(1280);
        dpiWidth.add(1024);
        dpiHeight.add(1080);
        dpiHeight.add(720);
        dpiHeight.add(1536);
        dpiHeight.add(1200);
        dpiHeight.add(1024);
        dpiHeight.add(960);
        dpiHeight.add(768);
        getDpiStringList();
    }

    public void getDpiStringList(){
        for(int i=0;i<dpiHeight.size();i++){
            dpiStringList.add(dpiWidth.get(i)+"x"+dpiHeight.get(i));
        }
    }
    @Override
    public void onPause() {
        if(parametersConfig != null) {
            parametersConfig.save();
        }
        super.onPause();
    }

    @Override
    public void onHide() {
        super.onHide();
        if(parametersConfig != null){
            parametersConfig.save();
        }
    }

    @Override
    public void onProgressChange(View view, int progress) {
        if(view.getId() == R.id.face_bsb){
            parametersConfig.setThresholdValueFace(progress/1000f);
        }
    }

    @OnClick(R.id.display_count_ll)
    public void setDisplayCount(){
        if(displayCountPicker == null) {
            displayCountPicker = PickerViewFactor.newPickerViewBuilder(mActivity, (options1, options2, options3, v) -> {
                parametersConfig.setDisplayCount(displayCountList.get(options1));
                displayCountTv.setText(String.format(Locale.ENGLISH, FORMAT_COUNT, displayCountList.get(options1)));
            })
                    .setSelectOptions(parametersConfig.getDisplayCount() - 10)
                    .setTitleText("显示结果数量")
                    .build();

            displayCountPicker.setPicker(displayCountList);
        }
        displayCountPicker.show();
    }

    @OnClick(R.id.save_count_ll)
    public void setSaveCount() {
        if(saveCountPicker == null) {
            saveCountPicker = PickerViewFactor.newPickerViewBuilder(mActivity, (options1, options2, options3, v) -> {
                parametersConfig.setDisplayCount(displayCountList.get(options1));
                saveCountTv.setText(String.format(Locale.ENGLISH, FORMAT_COUNT, saveCountList.get(options1)));
            })
                    .setSelectOptions(parametersConfig.getDisplayCount() - 10)
                    .setTitleText("保存记录数量")
                    .build();

            saveCountPicker.setPicker(saveCountList);
        }
        saveCountPicker.show();
    }

    // “保存”按钮的点击事件
    @OnClick(R.id.save_tv)
    public void save() {

    }
    //摄像头分辨率配置界面
    @OnClick(R.id.save_dpi_ll)
    public void setDpi(){
        if(dpiPicker==null){
            dpiPicker=PickerViewFactor.newPickerViewBuilder(mActivity,(options1, options2, options3, v) ->{
                parametersConfig.setDpiHeight(dpiHeight.get(options1));
                parametersConfig.setDpiWidth(dpiWidth.get(options1));
                parametersConfig.setDpiCount(options1);
                dpiSetTv.setText(dpiStringList.get(options1));
            } )
                    .setSelectOptions(parametersConfig.getDpiCount())
                    .setTitleText("摄像头分辨率")
                    .build();

            dpiPicker.setPicker(dpiStringList);
        }

        dpiPicker.show();
    }
}
