package com.iustu.identification.ui.main.batch.mvp;

public interface BatchView {
    void setProgress(int p);
    void setProgressTV(int successCount, int errCount);
    void changeSubmitable();
}
