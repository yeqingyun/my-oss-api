package com.gionee.oss.api.transmit;

import com.gionee.gnif.file.web.message.Message;
import com.gionee.oss.api.client.impl.OssClient;
import com.gionee.oss.api.exception.URLErrorException;
import com.gionee.oss.api.model.UploadParam;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;

/**
 * Created by yeqy on 2017/5/31.
 */
public interface Upload {

    Message upload(OssClient ossClient, InputStream inputStream, String fileName) throws URLErrorException, IOException, NoSuchAlgorithmException;


    Message upload(OssClient ossClient, InputStream inputStream, String fileName, UploadParam param) throws URLErrorException, IOException, NoSuchAlgorithmException;

}
