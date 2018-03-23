package com.gionee.oss.api.transmit;

import com.gionee.gnif.file.web.message.Message;
import com.gionee.oss.api.client.impl.OssClient;
import com.gionee.oss.api.constant.FileTmpType;

import java.io.IOException;

/**
 * Created by yeqy on 2017/6/20.
 */
public interface CommonFileService {

    Message delete(Long fileNo, FileTmpType fileTmpType, OssClient ossClient) throws IOException;
}
