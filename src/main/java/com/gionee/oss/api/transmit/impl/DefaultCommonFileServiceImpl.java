package com.gionee.oss.api.transmit.impl;

import com.alibaba.fastjson.JSON;
import com.gionee.gnif.file.util.CalcUtil;
import com.gionee.gnif.file.util.DateUtil;
import com.gionee.gnif.file.web.message.Message;
import com.gionee.oss.api.client.impl.OssClient;
import com.gionee.oss.api.constant.FileTmpType;
import com.gionee.oss.api.transmit.CommonFileService;
import com.gionee.oss.api.util.EncryptUtil;
import com.gionee.oss.api.util.UploadUtil;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yeqy on 2017/6/20.
 */
public class DefaultCommonFileServiceImpl implements CommonFileService {
    @Override
    public Message delete(Long fileNo, FileTmpType fileTmpType, OssClient ossClient) throws IOException {
        Map map = new HashMap<String, String>();
        String policy = new StringBuilder(DateUtil.GMTCurrentTimeStamp()).append("\n").append(fileNo).append("\n").append((fileTmpType == FileTmpType.TMP ? 1 : 0)).toString();
        map.put("code", ossClient.getCode());
        map.put("policy", URLEncoder.encode(CalcUtil.getBase64(policy), "UTF-8"));
        map.put("signature", EncryptUtil.signature(ossClient.getKey(), policy));
        Message message = JSON.toJavaObject(JSON.parseObject(UploadUtil.postUploadWithParam(ossClient.getDeleteURL(), map)), Message.class);
        if (message.getIsSuccess()) {
            message.setMessage("删除成功");
        }
        return message;
    }
}
