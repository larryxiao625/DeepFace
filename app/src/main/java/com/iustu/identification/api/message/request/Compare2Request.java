package com.iustu.identification.api.message.request;

/**
 * Created by Liu Yuchuan on 2017/12/7.
 */

public class Compare2Request {
    private Image image1;
    private Image image2;

    public Image getImage1() {
        return image1;
    }

    public void setImage1(Image image1) {
        this.image1 = image1;
    }

    public Image getImage2() {
        return image2;
    }

    public void setImage2(Image image2) {
        this.image2 = image2;
    }

    public static class Image{
        private String rect;
        private String image;

        public String getRect() {
            return rect;
        }

        public void setRect(String rect) {
            this.rect = rect;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }
}
