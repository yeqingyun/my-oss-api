package com.gionee.oss.api.transmit;


import com.gionee.gnif.file.web.message.Message;
import com.gionee.oss.api.client.impl.OssClient;
import com.gionee.oss.api.exception.TargetPathException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by yeqy on 2017/5/31.
 */
public interface Download {
    Message download(String targetPath, Long fileNo, OssClient ossClient) throws TargetPathException, IOException;

    InputStream getInputStream(Long fileNo, OssClient ossClient) throws TargetPathException, IOException;
}
