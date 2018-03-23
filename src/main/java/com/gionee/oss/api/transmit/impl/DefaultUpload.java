package com.gionee.oss.api.transmit.impl;

import com.alibaba.fastjson.JSON;
import com.gionee.gnif.file.constant.UploadStep;
import com.gionee.gnif.file.util.CalcUtil;
import com.gionee.gnif.file.util.FileUtil;
import com.gionee.gnif.file.web.message.Message;
import com.gionee.oss.api.client.impl.OssClient;
import com.gionee.oss.api.constant.UploadParameter;
import com.gionee.oss.api.exception.FileErrorException;
import com.gionee.oss.api.exception.URLErrorException;
import com.gionee.oss.api.model.UploadParam;
import com.gionee.oss.api.transmit.Upload;
import com.gionee.oss.api.util.EncryptUtil;
import com.gionee.oss.api.util.UploadUtil;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yeqy on 2017/5/31.
 */
@SuppressWarnings("Duplicates")
public class DefaultUpload implements Upload {

    private Long chunkSize = 52428800l;//默认50M

    public Message upload(OssClient ossClient, InputStream inputStream, String fileName) throws URLErrorException, FileErrorException, IOException, NoSuchAlgorithmException {
        return upload(ossClient, inputStream, fileName, null);
    }


    @Override
    public Message upload(OssClient ossClient, InputStream inputStream, String fileName, UploadParam param) throws URLErrorException, FileErrorException, IOException, NoSuchAlgorithmException {
        if (inputStream == null) {
            throw new FileErrorException();
        }
        long length = inputStream.available();
        if (length <= 0) {
            throw new FileErrorException();
        }
        inputStream = new BufferedInputStream(inputStream);

        if (length <= chunkSize) {//小于分片大小
            URL url = ossClient.getCommonUploadURL();
            InputStream bakOne = null;
            InputStream bakTwo = null;
            ByteArrayOutputStream outputStream = null;
            try {
                //inputStream 重用
                outputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len;
                while ((len = inputStream.read(buffer)) > -1) {
                    outputStream.write(buffer, 0, len);
                }
                outputStream.flush();

                bakOne = new BufferedInputStream(new ByteArrayInputStream(outputStream.toByteArray()));
                bakTwo = new BufferedInputStream(new ByteArrayInputStream(outputStream.toByteArray()));


                return commonUpload(FileUtil.getFileMd5(bakOne), fileName, String.valueOf(length), url, ossClient, bakTwo, param);
            } finally {//释放资源
                try {

                    if (bakOne != null)
                        bakOne.close();
                    if (bakTwo != null)
                        bakTwo.close();
                    if (outputStream != null)
                        outputStream.close();

                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            //第一步:验证上传权限，验证文件重复 传入code,signature,fileInfo,step = 1
            URL url = ossClient.getUploadURL();
            ByteArrayOutputStream outputStream = null;
            InputStream bakOne = null;
            InputStream bakTwo = null;
            byte[] bs;
            try {
                outputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024 * 1024];
                int len;
                while ((len = inputStream.read(buffer)) > -1) {
                    outputStream.write(buffer, 0, len);
                }
                outputStream.flush();

                bakOne = new BufferedInputStream(new ByteArrayInputStream(outputStream.toByteArray()));
                bakTwo = new BufferedInputStream(new ByteArrayInputStream(outputStream.toByteArray()));


                String fileMd5 = FileUtil.getFileMd5(bakOne);
                Message step1Msg = stepOne(fileMd5, fileName, String.valueOf(length), url, ossClient);

                if (!step1Msg.getIsSuccess() || !step1Msg.getNotRepeat()) {
                    return step1Msg;
                }


                int chunkLength;
                int order = 1;
                bs = new byte[chunkSize.intValue()];
                while ((chunkLength = bakTwo.read(bs)) != -1) {//分块,每块chunkLength大小

                    //循环第二步:判断文件块重复,不需要签名加密 传入文件块md5,文件块大小,文件块序号,step = 2
                    String chunkMd5 = FileUtil.getFileMd5(bs, 0, chunkLength);

                    Message step2Msg = stepTwo(fileName, chunkMd5, url, String.valueOf(chunkLength), String.valueOf(order));

                    if (!step2Msg.getIsSuccess()) {//出错，停止上传
                        bs = null;
                        return step2Msg;
                    }
                    if (!step2Msg.getNotRepeat())//如果文件块，则不上传
                        continue;


                    //否则执行循环第三步:上传文件，传入code,signature,fileInfo,step = 3
                    byte[] tmp = bs;
                    if (chunkLength < bs.length) {
                        tmp = Arrays.copyOfRange(bs, 0, chunkLength);
                    }

                    Message step3Msg = stepThree(fileMd5, fileName, String.valueOf(length), chunkMd5, String.valueOf(chunkLength), order, url, ossClient, tmp);
                    if (!step3Msg.getIsSuccess()) {//上传失败，中断上传
                        bs = null;
                        return step3Msg;
                    }

                    order++;

                }


                //第四步:合并文件，不需要加密签名，传入文件名,文件md5,文件大小，step = 4
                Message step4Msg = stepFour(fileMd5, fileName, String.valueOf(length), url, ossClient, param);
                return step4Msg;
            } finally {//释放资源
                bs = null;
                try {
                    if (bakOne != null)
                        bakOne.close();
                    if (bakTwo != null)
                        bakTwo.close();
                    if (outputStream != null)
                        outputStream.close();

                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }


    public Message commonUpload(String fileMd5, String fileName, String fileSize, URL url, OssClient ossClient, InputStream in, UploadParam uploadParam) throws FileNotFoundException, UnsupportedEncodingException {
        String fileInfo = EncryptUtil.getfileInfo(
                fileMd5, fileName, fileSize
        );
        Map<String, String> param = new HashMap<>();
        param.put(UploadParameter.code.name(), ossClient.getCode());
        param.put(UploadParameter.fileInfo.name(), URLEncoder.encode(CalcUtil.getBase64(fileInfo), "UTF-8"));
        param.put(UploadParameter.signature.name(), EncryptUtil.signature(ossClient.getKey(), fileInfo));
        if (uploadParam != null) {//上传附加条件不为空
            if (uploadParam.getCallback() != null) {
                param.put(UploadParameter.call.name(), URLEncoder.encode(CalcUtil.getBase64(
                        JSON.toJSONString(uploadParam.getCallback())
                ), "UTF-8"));
            }
            if (uploadParam.getSavePath() != null && uploadParam.getSavePath().length() > 0) {
                param.put(UploadParameter.savepath.name(), uploadParam.getSavePath());
            }
        }
        return JSON.toJavaObject(JSON.parseObject(UploadUtil.chunkFileUpload(fileName, url, in, param)), Message.class);
    }

    public Message stepOne(String fileMd5, String fileName, String fileSize, URL url, OssClient ossClient) throws IOException {

        String fileInfo = EncryptUtil.getfileInfo(
                fileMd5, fileName, fileSize
        );
        Map<String, String> step1Param = new HashMap<>();
        step1Param.put(UploadParameter.step.name(), UploadStep.STEP_ONE.getStep().toString());
        step1Param.put(UploadParameter.code.name(), ossClient.getCode());
        step1Param.put(UploadParameter.fileInfo.name(), URLEncoder.encode(CalcUtil.getBase64(fileInfo), "UTF-8"));
        step1Param.put(UploadParameter.signature.name(), EncryptUtil.signature(ossClient.getKey(), fileInfo));
        return JSON.toJavaObject(JSON.parseObject(UploadUtil.postUploadWithParam(url, step1Param)), Message.class);

    }

    public Message stepTwo(String fileName, String fileMd5, URL url, String chunkSize, String order) throws IOException {

        Map<String, String> step2Param = new HashMap<>();
        step2Param.put(UploadParameter.step.name(), UploadStep.STEP_TWO.getStep().toString());
        step2Param.put(UploadParameter.fileName.name(), fileName);
        step2Param.put(UploadParameter.fileMd5.name(), fileMd5);
        step2Param.put(UploadParameter.chunkOrder.name(), order);
        step2Param.put(UploadParameter.fileSize.name(), chunkSize);
        return JSON.toJavaObject(JSON.parseObject(UploadUtil.postUploadWithParam(url, step2Param)), Message.class);
    }

    public Message stepThree(String fileMd5, String fileName, String fileSize, String chunkMd5, String chunkSize, int order, URL url, OssClient ossClient, byte[] bs) throws FileNotFoundException, UnsupportedEncodingException {
        String fileInfo = EncryptUtil.getfileInfo(
                fileMd5, fileName, fileSize, chunkMd5, chunkSize
        );
        Map<String, String> step3Param = new HashMap<>();
        step3Param.put(UploadParameter.step.name(), UploadStep.STEP_THREE.getStep().toString());
        step3Param.put(UploadParameter.code.name(), ossClient.getCode());
        step3Param.put(UploadParameter.chunk.name(), String.valueOf(order));
        step3Param.put(UploadParameter.fileInfo.name(), URLEncoder.encode(CalcUtil.getBase64(fileInfo), "UTF-8"));
        step3Param.put(UploadParameter.signature.name(), EncryptUtil.signature(ossClient.getKey(), fileInfo));
        return JSON.toJavaObject(JSON.parseObject(UploadUtil.chunkFileUpload(fileName, url, bs, step3Param)), Message.class);
    }

    public Message stepFour(String fileMd5, String fileName, String fileSize, URL url, OssClient ossClient, UploadParam param) throws IOException {

        Map<String, String> step4Param = new HashMap<>();
        step4Param.put(UploadParameter.step.name(), UploadStep.STEP_FOUR.getStep().toString());
        step4Param.put(UploadParameter.code.name(), ossClient.getCode());
        step4Param.put(UploadParameter.fileName.name(), fileName);
        step4Param.put(UploadParameter.fileMd5.name(), fileMd5);
        step4Param.put(UploadParameter.fileSize.name(), fileSize);
        if (param != null) {//上传附加条件不为空
            if (param.getCallback() != null) {
                step4Param.put(UploadParameter.call.name(), URLEncoder.encode(CalcUtil.getBase64(
                        JSON.toJSONString(param.getCallback())
                ), "UTF-8"));
            }
            if (param.getSavePath() != null && param.getSavePath().length() > 0) {
                step4Param.put(UploadParameter.savepath.name(), param.getSavePath());
            }
        }
        return JSON.toJavaObject(JSON.parseObject(UploadUtil.postUploadWithParam(url, step4Param)), Message.class);
    }

    public void setChunkSize(Long chunkSize) {
        this.chunkSize = chunkSize;
    }
}
