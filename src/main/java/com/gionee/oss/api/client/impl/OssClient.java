package com.gionee.oss.api.client.impl;

import com.gionee.gnif.file.web.message.Message;
import com.gionee.oss.api.client.Oss;
import com.gionee.oss.api.constant.FileTmpType;
import com.gionee.oss.api.exception.FileErrorException;
import com.gionee.oss.api.exception.TargetPathException;
import com.gionee.oss.api.exception.URLErrorException;
import com.gionee.oss.api.model.UploadParam;
import com.gionee.oss.api.transmit.CommonFileService;
import com.gionee.oss.api.transmit.Download;
import com.gionee.oss.api.transmit.Upload;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;


/**
 * Created by yeqy on 2017/5/27.
 */
public class OssClient implements Oss {

    private final String commonUpload = "common_upload.html";
    private final String uploadUrl = "upload.html";
    private final String downloadUrl = "download.html";
    private final String deleteUrl = "file_delete.html";
    private String key;
    private String code;
    private String serverUrl;
    private Upload upload;
    private Download download;
    private CommonFileService commonFileService;
    private URL commonUploadURL;
    private URL uploadURL;
    private URL downloadURL;
    private URL deleteURL;


    public OssClient(String key, String code, String serverUrl) throws MalformedURLException {
        this.key = key;
        this.code = code;
        this.serverUrl = serverUrl;
    }

    public OssClient() {
    }

    public void initURL() throws MalformedURLException {
        commonUploadURL = new URL(serverUrl + commonUpload);
        uploadURL = new URL(serverUrl + uploadUrl);
        downloadURL = new URL(serverUrl + downloadUrl);
        deleteURL = new URL(serverUrl + deleteUrl);
    }

    /**
     * 上传(文件默认分块大小50M)
     *
     * @param inputStream
     * @return
     */
    public Message upload(InputStream inputStream, String fileName) throws URLErrorException, FileErrorException, IOException, NoSuchAlgorithmException {
        return upload.upload(this, inputStream, fileName);
    }

    public Message upload(InputStream inputStream, String fileName, UploadParam param) throws URLErrorException, FileErrorException, IOException, NoSuchAlgorithmException {
        return upload.upload(this, inputStream, fileName, param);
    }

    /**
     * 上传(文件默认分块大小50M)
     *
     * @param file 上传文件对象
     * @return
     */
    public Message upload(File file) throws URLErrorException, FileErrorException, IOException, NoSuchAlgorithmException {
        return upload.upload(this, new FileInputStream(file), file.getName());
    }

    public Message upload(File file, UploadParam param) throws URLErrorException, FileErrorException, IOException, NoSuchAlgorithmException {
        return upload.upload(this, new FileInputStream(file), file.getName(), param);
    }

    /**
     * 文件下载
     *
     * @param targetPath 文件存放路径
     * @param fileNo     文件Id
     * @return Message attribute 中返回文件路径
     */
    public Message download(String targetPath, Long fileNo) throws TargetPathException, IOException {
        return download.download(targetPath, fileNo, this);
    }

    /**
     * 文件下载
     *
     * @param fileNo 文件Id
     * @return 返回文件输出流
     */
    public InputStream getInputStream(Long fileNo) throws TargetPathException, IOException {
        return download.getInputStream(fileNo, this);
    }

    @Override
    public Message deleteFile(Long fileNo, FileTmpType fileTmpType) throws IOException {
        return commonFileService.delete(fileNo, fileTmpType, this);
    }

    public URL getCommonUploadURL() {
        return commonUploadURL;
    }

    public URL getUploadURL() {
        return uploadURL;
    }

    public URL getDownloadURL() {
        return downloadURL;
    }

    public void setUpload(Upload upload) {
        this.upload = upload;
    }

    public void setDownload(Download download) {
        this.download = download;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public URL getDeleteURL() {
        return deleteURL;
    }

    public void setCommonFileService(CommonFileService fileService) {
        this.commonFileService = fileService;
    }
}
