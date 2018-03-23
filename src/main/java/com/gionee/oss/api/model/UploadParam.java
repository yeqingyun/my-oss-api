package com.gionee.oss.api.model;

import com.gionee.gnif.file.biz.model.Callback;

/**
 * Created by yeqy on 2017/6/12.
 */
public class UploadParam {

    private String savePath;
    private Callback callback;
    private byte[] file;

    public UploadParam(String savePath, Callback callback, byte[] file) {
        this.savePath = savePath;
        this.callback = callback;
        this.file = file;
    }

    public UploadParam() {
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }
}
