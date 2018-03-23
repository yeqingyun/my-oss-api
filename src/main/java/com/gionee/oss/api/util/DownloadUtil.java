package com.gionee.oss.api.util;

import com.alibaba.fastjson.JSON;
import com.gionee.gnif.file.util.FileUtil;
import com.gionee.gnif.file.web.message.Message;
import com.gionee.oss.api.exception.TargetPathException;
import org.apache.http.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yeqy on 2017/6/1.
 */
public class DownloadUtil {

    public static Message download(String targetPath, Map<String, String> param, URL url) throws TargetPathException, IOException {
        CloseableHttpClient client = null;
        BufferedInputStream is = null;
        BufferedOutputStream fileOut = null;
        byte[] buffer;
        try {
            client = HttpClients.createDefault();
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : param.entrySet()) {
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            HttpGet httpget = new HttpGet(url.toString() + "?" + sb.substring(0, sb.length()));
            HttpResponse response = client.execute(httpget);
            HttpEntity entity = response.getEntity();

            if (response.getFirstHeader(HttpHeaders.CONTENT_TYPE).toString().toLowerCase().indexOf("text/html") > 0) {//如果返回是JSON Message
                return JSON.toJavaObject(JSON.parseObject(EntityUtils.toString(entity, "UTF-8")), Message.class);
            }


            is = new BufferedInputStream(entity.getContent());

            FileUtil.makeDir(targetPath);
            File file = new File(targetPath);
            if (!file.isDirectory()) {
                throw new TargetPathException();
            }
            final String fileName = getFileName(response);
            String filePath;
            if (targetPath.endsWith(File.pathSeparator)) {
                filePath = targetPath + fileName;
            } else {
                filePath = targetPath + File.separator + fileName;
            }

            File targetFile = new File(filePath);
            final String[] names = fileName.split("\\.");
            if (targetFile.exists()) {
                File[] files = file.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        if (name.contains(names[0]) && name.contains("." + names[1])) {
                            return true;
                        }
                        return false;
                    }
                });

                if (files.length == 1) {
                    if (names.length == 2) {
                        StringBuilder nameBuilder = new StringBuilder();
                        nameBuilder.append(targetPath).append(File.separator).append(names[0]).append("(1)").append(".").append(names[1]);
                        filePath = nameBuilder.toString();
                    } else {
                        filePath = fileName + "(1)";
                    }
                } else {
                    Arrays.sort(files, new Comparator() {
                        @Override
                        public int compare(Object o1, Object o2) {
                            File f1 = (File) o1;
                            File f2 = (File) o2;
                            if (names.length == 2) {
                                String numStr1 = f1.getName().replace(names[0], "").replace(names[1], "").replace("(", "").replace(")", "").replace(".", "").trim();
                                String numStr2 = f2.getName().replace(names[0], "").replace(names[1], "").replace("(", "").replace(")", "").replace(".", "").trim();
                                Integer num1 = 0;
                                Integer num2 = 0;
                                if (numStr1.length() > 0) {
                                    num1 = Integer.valueOf(numStr1);
                                }
                                if (numStr2.length() > 0) {
                                    num2 = Integer.valueOf(numStr2);
                                }
                                return num1 - num2;
                            } else {
                                String numStr1 = f1.getName().replace(fileName, "").replace("(", "").replace(")", "").trim();
                                String numStr2 = f2.getName().replace(fileName, "").replace("(", "").replace(")", "").trim();
                                Integer num1 = 0;
                                Integer num2 = 0;
                                if (numStr1.length() > 0) {
                                    num1 = Integer.valueOf(numStr1);
                                }
                                if (numStr2.length() > 0) {
                                    num2 = Integer.valueOf(numStr2);
                                }
                                return num1 - num2;
                            }

                        }

                        @Override
                        public boolean equals(Object obj) {
                            return false;
                        }
                    });


                    if (names.length == 2) {
                        int num = Integer.valueOf(files[files.length - 1].getName().replace(names[0], "").replace(names[1], "").replace(".", "").replace("(", "").replace(")", ""));
                        StringBuilder nameBuilder = new StringBuilder();
                        nameBuilder.append(targetPath).append(File.separator).append(names[0]).append("(").append(num + 1).append(")").append(".").append(names[1]);
                        filePath = nameBuilder.toString();
                    } else {
                        int num = Integer.valueOf(files[files.length - 1].getName().replace(fileName, "").replace("(", "").replace(")", ""));
                        StringBuilder nameBuilder = new StringBuilder();
                        nameBuilder.append(targetPath).append(File.separator).append(fileName).append("(").append(num + 1).append(")");
                        filePath = nameBuilder.toString();
                    }

                }

            }

            fileOut = new BufferedOutputStream(new FileOutputStream(filePath));

            if (is.available() <= 1048576) {//小于1M
                buffer = new byte[1024];
            } else if (is.available() <= 10485760) {//小于10M
                buffer = new byte[5120];
            } else {//小于100M
                buffer = new byte[51200];
            }
            int ch;
            while ((ch = is.read(buffer)) != -1) {
                fileOut.write(buffer, 0, ch);
            }
            fileOut.flush();


            Message message = new Message(true, true, true, true, "下载成功");
            Map<String, Object> map = new HashMap();
            map.put("path", filePath);
            message.setAttributes(map);
            return message;
        } finally {
            try {
                buffer = null;
                if (is != null)
                    is.close();
                if (fileOut != null)
                    fileOut.close();
                if (client != null)
                    client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static InputStream getInputStream(Map<String, String> param, URL url) throws TargetPathException, IOException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : param.entrySet()) {
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            HttpGet httpget = new HttpGet(url.toString() + "?" + sb.substring(0, sb.length()));
            HttpResponse response = client.execute(httpget);
            HttpEntity entity = response.getEntity();

            if (response.getFirstHeader(HttpHeaders.CONTENT_TYPE).toString().toLowerCase().indexOf("text/html") > 0) {//如果返回是JSON Message
                throw new RuntimeException(JSON.toJavaObject(JSON.parseObject(EntityUtils.toString(entity, "UTF-8")), Message.class).getMessage());
            }
            return entity.getContent();
        }
    }


    public static String getFileName(HttpResponse response) {
        Header contentHeader = response.getFirstHeader("Content-Disposition");
        String filename = null;
        if (contentHeader != null) {
            HeaderElement[] values = contentHeader.getElements();
            if (values.length == 1) {
                NameValuePair param = values[0].getParameterByName("filename");
                if (param != null) {
                    try {
                        filename = URLDecoder.decode(param.getValue(), "UTF-8");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return filename;
    }
}
