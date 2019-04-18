package com.iustu.identification.ui.main.camera.view;

import com.iustu.identification.entity.CompareRecord;

public interface IVew {

    void showShortMsg(String rea);
    void updateSingleResult(CompareRecord compareRecord);
    void updateCapture(String capturePic);
}
