package com.iustu.identification.ui.main.camera.prenster;

import com.iustu.identification.ui.main.camera.view.IVew;
import com.serenegiant.usb.Size;

import java.util.List;

public interface IPenster {
    void attchView(IVew iVew);
    void updateCompareResult(String imageId);
    IVew getView();
    void setSupportPreviewSize(List<Size> supportPreviewSize);
}
