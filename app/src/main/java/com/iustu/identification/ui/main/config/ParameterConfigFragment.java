package com.iustu.identification.ui.main.config;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.OptionsPickerView;
import com.iustu.identification.R;
import com.iustu.identification.bean.ParameterConfig;
import com.iustu.identification.bean.PreviewSizeConfig;
import com.iustu.identification.ui.base.BaseFragment;
import com.iustu.identification.ui.widget.seekbar.BubbleSeekBar;
import com.iustu.identification.util.DataCache;
import com.iustu.identification.util.MyTextWatcher;
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
    private ParameterConfig config;

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
    @BindView(R.id.threshold_quantity)
    EditText quantity;
    @BindView(R.id.min_eyes_distance)
    EditText minEyesDistance;
    @BindView(R.id.alarm)
    RadioGroup radioGroup;
    RadioButton radioButton;

    private OptionsPickerView displayCountPicker;
    private OptionsPickerView saveCountPicker;
    private OptionsPickerView dpiPicker;
    private PreviewSizeConfig previewSizeConfig;

    private List<Integer> displayCountList = new ArrayList<>();         // 设置显示结果数量的源
    private List<Integer> saveCountList = new ArrayList<>();          // 设置保存记录数的源
    private List<Integer> dpiWidth=new ArrayList<>();     // 设置分辨率宽度的源
    private List<Integer> dpiHeight=new ArrayList<>();       // 设置分辨率高度的源
    private List<String> dpiStringList=new ArrayList<>();       // 设置分辨率的源

    @Override
    protected int postContentView() {
        return R.layout.fragment_parameter_config;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState, View view) {
        super.initView(savedInstanceState, view);
        config = DataCache.getParameterConfig();
        faceSeekBar.setOnProgressChangeListener(this);
        for(int i = 10; i <= 50; i++){
            displayCountList.add(i);
        }
        saveCountList.add(0);
        saveCountList.add(1000);
        saveCountList.add(5000);
        saveCountList.add(10000);
        //config=ParameterConfig.getFromSP();
        previewSizeConfig=PreviewSizeConfig.getFramSp();
        dpiWidth=previewSizeConfig.getPreviewWidth();
        dpiHeight=previewSizeConfig.getPreviewHeight();
        switch (config.getAlarmType()) {
            case ParameterConfig.ONLYMP3:
                radioButton = (RadioButton)view.findViewById(R.id.alarm_only_mp3);
                break;
            case ParameterConfig.ONLYSHAKE:
                radioButton = (RadioButton)view.findViewById(R.id.alarm_only_shake);
                break;
            case ParameterConfig.MP3ANDSHAKE:
                radioButton = (RadioButton)view.findViewById(R.id.alarm_mp3_and_shake);
                break;
        }
        radioButton.setChecked(true);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                switch (checkedId) {
                    case R.id.alarm_only_mp3:
                        config.setAlarmType(ParameterConfig.ONLYMP3);
                        break;
                    case R.id.alarm_only_shake:
                        config.setAlarmType(ParameterConfig.ONLYSHAKE);
                        break;
                    case R.id.alarm_mp3_and_shake:
                        config.setAlarmType(ParameterConfig.MP3ANDSHAKE);
                        break;
                }
        });
        getDpiStringList();
        initData();
    }

    // 将从DataCache获取的数据加载到空间上
    public void initData() {
        config = DataCache.getParameterConfig();
        faceSeekBar.setProgress((int)(config.getFactor() * 1000));
        minFace.setText(config.getMin_size() + "");
        checkFactor1.setText(config.getThreshold1() + "");
        checkFactor2.setText(config.getThreshold2() + "");
        checkFactor3.setText(config.getThreshold3() + "");
        minEyesDistance.setText(config.getMinEyesDistance() + "");
        checkFactor1.addTextChangedListener(new MyTextWatcher());
        checkFactor2.addTextChangedListener(new MyTextWatcher());
        checkFactor3.addTextChangedListener(new MyTextWatcher());
        quantity.addTextChangedListener(new MyTextWatcher());
        displayCountTv.setText(config.getDisplayCount() + "");
        saveCountTv.setText(config.getSaveCount() + "");
        dpiSetTv.setText(config.getDpiWidth()+"*"+config.getDpiHeight());
        quantity.setText(config.getThresholdQuanity() + "");
    }

    public void getDpiStringList(){
        for(int i=0;i<dpiHeight.size();i++){
            dpiStringList.add(dpiWidth.get(i)+"x"+dpiHeight.get(i));
        }
    }
    @Override
    public void onPause() {
        if(config != null) {
            if (!checkFactor1.getText().toString().isEmpty())
                config.setThreshold1(Float.valueOf(checkFactor1.getText().toString()));
            if (!checkFactor2.getText().toString().isEmpty())
                config.setThreshold2(Float.valueOf(checkFactor2.getText().toString()));
            if (!checkFactor3.getText().toString().isEmpty())
                config.setThreshold3(Float.valueOf(checkFactor3.getText().toString()));
            if (!quantity.getText().toString().isEmpty())
                config.setThresholdQuanity(Float.valueOf(quantity.getText().toString()));
            if(!minFace.getText().toString().isEmpty()) {
                config.setMin_size(Integer.valueOf(minFace.getText().toString()));
            }
            if (!minEyesDistance.getText().toString().isEmpty())
                config.setMinEyesDistance(Integer.valueOf(minEyesDistance.getText().toString()));
            config.save();
            super.onPause();
        }
    }

    @Override
    public void onHide() {
        super.onHide();
        if(config != null){
            config.save();
        }
    }

    @Override
    public void onProgressChange(View view, int progress) {
        if(view.getId() == R.id.face_bsb){
            config.setFactor(progress/1000f);
        }
    }

    @OnClick(R.id.display_count_ll)
    public void setDisplayCount(){
        if(displayCountPicker == null) {
            displayCountPicker = PickerViewFactor.newPickerViewBuilder(mActivity, (options1, options2, options3, v) -> {
                config.setDisplayCount(displayCountList.get(options1));
                config.setDiaplayPosition(options1);
                displayCountTv.setText(String.format(Locale.ENGLISH, FORMAT_COUNT, displayCountList.get(options1)));
            })
                    .setSelectOptions(config.getDisplayCount())
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
                config.setSaveCount(saveCountList.get(options1));
                config.setSavePosition(options1);
                saveCountTv.setText(String.format(Locale.ENGLISH, FORMAT_COUNT, saveCountList.get(options1)));
            })
                    .setSelectOptions(config.getSavePosition())
                    .setTitleText("保存记录数量")
                    .build();

            saveCountPicker.setPicker(saveCountList);
        }
        saveCountPicker.show();
    }

    //摄像头分辨率配置界面
    @OnClick(R.id.save_dpi_ll)
    public void setDpi(){
        if(dpiPicker==null){
            dpiPicker=PickerViewFactor.newPickerViewBuilder(mActivity,(options1, options2, options3, v) ->{
                config.setDpiHeight(dpiHeight.get(options1));
                config.setDpiWidth(dpiWidth.get(options1));
                config.setDpiCount(options1);
                dpiSetTv.setText(dpiStringList.get(options1));
            } )
                    .setSelectOptions(0)
                    .setTitleText("摄像头分辨率")
                    .build();

            dpiPicker.setPicker(dpiStringList);
        }
        dpiPicker.show();
    }
}
