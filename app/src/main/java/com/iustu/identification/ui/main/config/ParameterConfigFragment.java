package com.iustu.identification.ui.main.config;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iustu.identification.R;
import com.iustu.identification.bean.Province;
import com.iustu.identification.config.ParametersConfig;
import com.iustu.identification.ui.base.BaseFragment;
import com.iustu.identification.ui.widget.SexChooser;
import com.iustu.identification.ui.widget.seekbar.BubbleSeekBar;
import com.iustu.identification.util.GetJsonDataUtil;
import com.iustu.identification.util.PickerViewFactor;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Liu Yuchuan on 2017/11/5.
 */

public class ParameterConfigFragment extends BaseFragment implements BubbleSeekBar.OnProgressChangeListener, SexChooser.OnSexChosenListener{
    private ParametersConfig parametersConfig;

    private static final String FORMAT_AGE_RANGE = "%d-%d";

    @BindView(R.id.face_bsb)
    BubbleSeekBar faceSeekBar;
    @BindView(R.id.paper_bsb)
    BubbleSeekBar paperSeekBar;
    @BindView(R.id.display_count_tv)
    TextView displayCountTv;
    @BindView(R.id.sex_chooser)
    SexChooser sexChooser;
    @BindView(R.id.age_range_tv)
    TextView ageRangeTv;
    @BindView(R.id.config_location_tv)
    TextView locationTv;

    private OptionsPickerView displayCountPicker;
    private OptionsPickerView ageRangePicker;
    private OptionsPickerView cityPicker;

    private List<String> provinceList = new ArrayList<>();
    private List<List<String>> cityList = new ArrayList<>();
    private List<List<List<String>>> areaList = new ArrayList<>();

    private List<Integer> countList = new ArrayList<>();

    @Override
    protected int postContentView() {
        return R.layout.fragment_parameter_config;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState, View view) {
        super.initView(savedInstanceState, view);
        parametersConfig = ParametersConfig.getInstance();
        faceSeekBar.setProgress((int) (parametersConfig.getThresholdValueFace() * 1000));
        paperSeekBar.setProgress((int) (parametersConfig.getThresholdValuePaper() * 1000));
        faceSeekBar.setOnProgressChangeListener(this);
        paperSeekBar.setOnProgressChangeListener(this);
        displayCountTv.setText(String.valueOf(parametersConfig.getDisplayCount()));
        sexChooser.setChoose(parametersConfig.getSex());
        sexChooser.setOnSexChosenListener(this);
        int min = parametersConfig.getAgeMin();
        int max = parametersConfig.getAgeMax();
        if(min >= 0 && max <= 100){
            ageRangeTv.setText(String.format(Locale.ENGLISH,FORMAT_AGE_RANGE, min, max));
        }
        locationTv.setText(parametersConfig.getLocation());
        countList.clear();
        countList.add(1);
        countList.add(5);
        countList.add(10);
        countList.add(50);
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
        }else {
            parametersConfig.setThresholdValuePaper(progress/1000f);
        }
    }

    @OnClick(R.id.display_count_ll)
    public void setDisplayCount(){
        if(displayCountPicker == null) {
            displayCountPicker = PickerViewFactor.newPickerViewBuilder(mActivity, (options1, options2, options3, v) -> {
                parametersConfig.setDisplayCount(countList.get(options1));
                displayCountTv.setText(String.valueOf(countList.get(options1)));
            })
                    .setSelectOptions(parametersConfig.getDisplayCount() - 10)
                    .setTitleText("显示结果数量")
                    .build();
            displayCountPicker.setPicker(countList);
        }

        displayCountPicker.show();
    }

    @OnClick(R.id.age_range_ll)
    public void setAgeRange(){
        if(ageRangePicker == null){
            ageRangePicker = PickerViewFactor.newPickerViewBuilder(mActivity, (options1, options2, options3, v) -> {
                int max = options1 + options2 + 1;
                parametersConfig.setAgeMin(options1);
                parametersConfig.setAgeMax(max);
                ageRangeTv.setText(String.format(Locale.ENGLISH, FORMAT_AGE_RANGE, options1, max));
            })
                    .setTitleText("选择年龄区间")
                    .setSelectOptions(parametersConfig.getAgeMin(), parametersConfig.getAgeMax() - parametersConfig.getAgeMin() - 1)
                    .build();

            ArrayList options1 = new ArrayList<>();
            for(int i = 0; i <= 99; i++){
                options1.add(i);
            }
            ArrayList<ArrayList<Integer>> options2 = new ArrayList<>();
            for(int i = 0; i <= 99; i++){
                ArrayList<Integer> arrayList = new ArrayList<>();
                for(int j = i + 1; j <= 100; j++){
                    arrayList.add(j);
                }
                options2.add(arrayList);
            }

            ageRangePicker.setPicker(options1, options2);
        }

        ageRangePicker.show();
    }

    @OnClick(R.id.location_ll)
    public void setLocation(){
        final String noLimit = "无限制";
        if(cityPicker == null){
            cityPicker = PickerViewFactor.newPickerViewBuilder(mActivity, (options1, options2, options3, v) -> {
                String text = noLimit;
                if(options1 != 0){
                    text = provinceList.get(options1);
                    if(options2 != 0) {
                        text += "-" + cityList.get(options1).get(options2);
                        if(options3 != 0){
                            text += "-" + areaList.get(options1).get(options2).get(options3);
                        }
                    }
                }
                parametersConfig.setLocation(text);
                locationTv.setText(text);
            })
                    .setTitleText("选择籍贯")
                    .build();
            Type type = new TypeToken<ArrayList<Province>>(){}.getType();
            List<Province> provinces;
            provinces = new Gson().fromJson(GetJsonDataUtil.getJson(mActivity, "province.json"),type);
            provinceList.add(noLimit);
            cityList.add(new ArrayList<>());
            cityList.get(0).add(noLimit);
            areaList.add(new ArrayList<>());
            areaList.get(0).add(new ArrayList<>());
            areaList.get(0).get(0).add(noLimit);
            String s;
            for(int i = 0; i < provinces.size(); i++){
                Province province = provinces.get(i);
                provinceList.add(province.getName());
                cityList.add(new ArrayList<>());
                cityList.get(i + 1).add(noLimit);
                areaList.add(new ArrayList<>());
                areaList.get(i + 1).add(new ArrayList<>());
                areaList.get(i + 1).get(0).add(noLimit);
                for(int j = 0; j < province.getCity().size(); j++){
                    Province.City city = province.getCity().get(j);
                    cityList.get(i + 1).add(city.getName());
                    areaList.get(i + 1).add(new ArrayList<>());
                    areaList.get(i + 1).get(j + 1).add(noLimit);
                    for(int k = 0; k < city.getArea().size(); k++){
                        areaList.get(i + 1).get(j + 1).add(city.getArea().get(k));
                    }
                }
            }
            cityPicker.setPicker(provinceList, cityList, areaList);
        }
        cityPicker.show();
    }

    @Override
    public void onChooseSex(View view, int sex) {
        parametersConfig.setSex(sex);
    }
}
