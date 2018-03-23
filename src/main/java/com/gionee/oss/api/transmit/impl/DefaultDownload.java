package com.gionee.oss.api.transmit.impl;

import com.gionee.gnif.file.util.CalcUtil;
import com.gionee.gnif.file.web.message.Message;
import com.gionee.oss.api.client.impl.OssClient;
import com.gionee.oss.api.constant.DownloadParameter;
import com.gionee.oss.api.exception.TargetPathException;
import com.gionee.oss.api.transmit.Download;
import com.gionee.oss.api.util.DownloadUtil;
import com.gionee.oss.api.util.EncryptUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yeqy on 2017/5/31.
 */
public class DefaultDownload implements Download {


    @Override
    public Message download(String targetPath, Long fileNo, OssClient ossClient) throws TargetPathException, IOException {
        return DownloadUtil.download(targetPath, getDownloadParam(fileNo, ossClient), ossClient.getDownloadURL());
    }

    @Override
    public InputStream getInputStream(Long fileNo, OssClient ossClient) throws TargetPathException, IOException {
        return DownloadUtil.getInputStream(getDownloadParam(fileNo, ossClient), ossClient.getDownloadURL());
    }

    private Map getDownloadParam(Long fileNo, OssClient ossClient) throws UnsupportedEncodingException {
        Map map = new HashMap<>();
        String policy = EncryptUtil.getPolicy("0", "0", fileNo.toString());
        map.put(DownloadParameter.code.name(), ossClient.getCode());
        map.put(DownloadParameter.signature.name(), EncryptUtil.signature(ossClient.getKey(), policy));
        map.put(DownloadParameter.policy.name(), URLEncoder.encode(CalcUtil.getBase64(policy), "UTF-8"));
        return map;
    }
}
