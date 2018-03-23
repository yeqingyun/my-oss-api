package com.gionee.oss.api.util;

import com.gionee.oss.api.exception.URLErrorException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by yeqy on 2017/5/31.
 */
@SuppressWarnings("ALL")
public class UploadUtil {

    public static String postUploadWithParam(URL url, Map<String, String> map) throws URLErrorException, IOException {
        CloseableHttpClient client = null;
        try {
            String result = null;

            //创建httpclient对象
            client = HttpClients.createDefault();
            //创建post方式请求对象
            HttpPost httpPost = new HttpPost(url.toString());

            //装填参数
            List<NameValuePair> nvps = new ArrayList<>();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            //设置参数到请求对象中
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));

            httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
            httpPost.setHeader(HttpHeaders.USER_AGENT, "gionee-oss");

            //执行请求操作，并拿到结果（同步阻塞）
            CloseableHttpResponse response = client.execute(httpPost);
            //获取结果实体
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                //按指定编码转换结果实体为String类型
                result = EntityUtils.toString(entity, "UTF-8");
            }
            EntityUtils.consume(entity);

            return result;
        } finally {
            //释放链接
            if (client != null)
                client.close();
        }


    }

    public static String chunkFileUpload(String fileName, URL url, byte[] bs, Map<String, String> map) throws URLErrorException, FileNotFoundException {

        final String remote_url = url.toString();// 第三方服务器请求地址
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String result = "";
        try {
            HttpPost httpPost = new HttpPost(remote_url);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addBinaryBody("file", bs, ContentType.MULTIPART_FORM_DATA, fileName);// 文件流
            for (Map.Entry<String, String> entry : map.entrySet()) {
                builder.addTextBody(entry.getKey(), entry.getValue());
            }

            org.apache.http.HttpEntity entity = builder.build();
            httpPost.setEntity(entity);
            CloseableHttpResponse response = httpClient.execute(httpPost);// 执行提交
            org.apache.http.HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                // 将响应内容转换为字符串
                result = EntityUtils.toString(responseEntity, Charset.forName("UTF-8"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return result;

    }


    public static String chunkFileUpload(String fileName, URL url, InputStream inputStream, Map<String, String> map) {

        final String remote_url = url.toString();// 第三方服务器请求地址
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String result = "";
        try {
            HttpPost httpPost = new HttpPost(remote_url);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addBinaryBody("file", inputStream, ContentType.MULTIPART_FORM_DATA, fileName);// 文件流
            for (Map.Entry<String, String> entry : map.entrySet()) {
                builder.addTextBody(entry.getKey(), entry.getValue());
            }

            org.apache.http.HttpEntity entity = builder.build();
            httpPost.setEntity(entity);
            CloseableHttpResponse response = httpClient.execute(httpPost);// 执行提交
            org.apache.http.HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                // 将响应内容转换为字符串
                result = EntityUtils.toString(responseEntity, Charset.forName("UTF-8"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return result;
    }


}
