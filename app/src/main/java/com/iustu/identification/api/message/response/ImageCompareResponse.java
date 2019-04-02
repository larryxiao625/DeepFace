package com.iustu.identification.api.message.response;

/**
 * Created by Liu Yuchuan on 2017/11/29.
 */

public class ImageCompareResponse {
    private String rect1;
    private String rect2;
    private double score;

    public String getRect1() {
        return rect1;
    }

    public void setRect1(String rect1) {
        this.rect1 = rect1;
    }

    public String getRect2() {
        return rect2;
    }

    public void setRect2(String rect2) {
        this.rect2 = rect2;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
