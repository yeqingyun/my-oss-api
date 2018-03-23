package com.gionee.client;

import com.gionee.gnif.file.web.message.Message;
import com.gionee.oss.api.client.Oss;
import com.gionee.oss.api.constant.FileTmpType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by yeqy on 2017/6/9.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:domain-context.xml"})
public class OssClientTest {

    @Autowired
    private Oss ossClient;

    @Test
    public void fileUpload() throws Exception {
        Message message1 = ossClient.upload(new File("E:/Activiti实战.pdf"));
        System.out.println(message1);
    }

    @Test
    public void inputStreamUpload() throws Exception {
        File file = new File("D:/test.jpg");
        Message message = ossClient.upload(new FileInputStream(file), file.getName());
        System.out.println(message);
    }

    @Test
    public void chunkFileUpload() throws Exception {
        File file = new File("E:/download/Activiti实战.pdf");
        Message message = ossClient.upload(file);
        System.out.println(message);
    }

    @Test
    public void chunkInputStreamUpload() throws Exception {
        File file = new File("E:/download/Activiti实战.pdf");
        Message message = ossClient.upload(new FileInputStream(file), file.getName());
        System.out.println(message);
    }


    @Test
    public void download() throws Exception {
        Message message = ossClient.download("D:/imgs", 696l);
        System.out.println(message);
    }

    @Test
    public void delete() throws Exception {
        Message message = ossClient.deleteFile(1457l, FileTmpType.UNTMP);
        System.out.println(message);
    }


}