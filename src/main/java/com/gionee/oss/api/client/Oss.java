package com.gionee.oss.api.client;

import com.gionee.gnif.file.web.message.Message;
import com.gionee.oss.api.constant.FileTmpType;
import com.gionee.oss.api.exception.FileErrorException;
import com.gionee.oss.api.exception.TargetPathException;
import com.gionee.oss.api.exception.URLErrorException;
import com.gionee.oss.api.model.UploadParam;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;

/**
 * Created by yeqy on 2017/5/27.
 */
public interface Oss {
    Message upload(InputStream inputStream, String fileName) throws URLErrorException, FileErrorException, IOException, NoSuchAlgorithmException;

    Message upload(InputStream inputStream, String fileName, UploadParam param) throws URLErrorException, FileErrorException, IOException, NoSuchAlgorithmException;

    Message upload(File file) throws URLErrorException, FileErrorException, IOException, NoSuchAlgorithmException;

    Message upload(File file, UploadParam param) throws URLErrorException, FileErrorException, IOException, NoSuchAlgorithmException;

    Message download(String targetPath, Long fileNo) throws TargetPathException, IOException;

    InputStream getInputStream(Long fileNo) throws TargetPathException, IOException;

    Message deleteFile(Long fileNo, FileTmpType fileTmpType) throws IOException;

}
