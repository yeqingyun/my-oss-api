package com.gionee.oss.api.util;

import com.gionee.gnif.file.util.CalcUtil;
import com.gionee.gnif.file.util.DateUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by yeqy on 2017/5/27.
 */
public class EncryptUtil {


    public static String signature(String key, String str) throws UnsupportedEncodingException {
        return URLEncoder.encode(CalcUtil.getBase64(CalcUtil.hamcsha1(str, key)), "UTF-8");
    }

    public static String getfileInfo(String fileMd5, String fileName, String fileSize, String chunkMd5, String chunkSize) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        sb.append(DateUtil.GMTCurrentTimeStamp()).append("\n").append(URLEncoder.encode(new StringBuilder(fileMd5).append("\n").append(fileName).append("\n").append(fileSize).append("\n")
                .append(chunkMd5).append("\n").append(chunkSize).toString(),"UTF-8"));

        return sb.toString();
    }

    public static String getfileInfo(String fileMd5, String fileName, String fileSize) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        sb.append(DateUtil.GMTCurrentTimeStamp()).append("\n").append(URLEncoder.encode(new StringBuilder(fileMd5).append("\n").append(fileName).append("\n").append(fileSize).toString(),"UTF-8"));
        return sb.toString();
    }

    public static String getPolicy(String count, String expire, String fileNo) {
        StringBuilder sb = new StringBuilder();
        sb.append(count).append("\n").append(expire).append("\n").append(fileNo);
        return sb.toString();
    }

}
