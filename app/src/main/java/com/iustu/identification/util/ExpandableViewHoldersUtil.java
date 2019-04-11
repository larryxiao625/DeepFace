package com.iustu.identification.util;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.iustu.identification.R;
import com.iustu.identification.bean.ParameterConfig;
import com.iustu.identification.ui.main.camera.adapter.CompareItemAdapter;

public class ExpandableViewHoldersUtil {

    /**
     * 图标旋转方法
     * @param imageView 传入的图像
     * @param from 开始角度
     * @param to 结束角度
     */
    public static void rotateExpandIcon(ImageView imageView,float from,float to){
        ValueAnimator animator=ValueAnimator.ofFloat(from,to);
        animator.setDuration(500);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(v-> imageView.setRotation((Float) v.getAnimatedValue()));
        animator.start();
    }

    /**
     * 展开列表方法
     * @param holder holder
     * @param start 开始高度
     * @param end 结束高度
     * @param isAnimate 是否使用动画
     */
    public static void expandHolder(CompareItemAdapter.Holder holder,int start,int end,boolean isAnimate){
        LinearLayout moreInfo=holder.itemView.findViewById(R.id.more_info_ll);
        if(isAnimate) {
            updateHeight(holder.itemView,start,end);
        }else{
            moreInfo.setVisibility(View.VISIBLE);
            moreInfo.setAlpha(1);
        }
    }

    /**
     * 关闭列表方法 参数同上
     */
    public static void collapseHolder(CompareItemAdapter.Holder holder,int start,int end,boolean isAnimate){
        LinearLayout moreInfo=holder.itemView.findViewById(R.id.more_info_ll);
        if(isAnimate){
            ObjectAnimator objectAnimator=ObjectAnimator.ofFloat(moreInfo, View.ALPHA,0);
            objectAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    moreInfo.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            objectAnimator.setDuration(200);
            objectAnimator.start();
            updateHeight(holder.itemView,start,end);
        }else {
            moreInfo.setVisibility(View.GONE);
            moreInfo.setAlpha(0);
        }
    }

    /**
     * 展开高度方法
     */
    public static void updateHeight(View itemView,int start,int end){
        LinearLayout moreInfo=itemView.findViewById(R.id.more_info_ll);
        ValueAnimator valueAnimator=ValueAnimator.ofInt(start,end);
        moreInfo.setVisibility(View.VISIBLE);
        valueAnimator.addUpdateListener(l->{
            ViewGroup.LayoutParams lp=moreInfo.getLayoutParams();
            Log.d("CameraHeight", String.valueOf(itemView.getLayoutParams().height));
            lp.height= (int) l.getAnimatedValue();
            if(start<end&&(int)l.getAnimatedValue()==end){
                ObjectAnimator objectAnimator=ObjectAnimator.ofFloat(moreInfo, View.ALPHA,1);
                objectAnimator.setDuration(200);
                objectAnimator.start();
            }
            moreInfo.setLayoutParams(lp);
        });
        valueAnimator.start();
    }


}
