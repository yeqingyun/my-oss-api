package com.gionee.oss.api.web;

import com.gionee.gnif.file.util.CalcUtil;
import com.gionee.gnif.file.util.DateUtil;
import com.gionee.oss.api.util.EncryptUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yeqy on 2017/6/14.
 */
@SuppressWarnings("Duplicates")
@Controller
public class FileServiceSignatureController {
    @Value("${oss.key}")
    private String key;

    @RequestMapping(method = {RequestMethod.GET}, value = "getFileUploadSignature.json")
    @ResponseBody
    public Map getSignatureAndFileInfo(String info) {
        try {
            if (info == null || info.length() == 0) {
                return null;
            }
            Map<String, String> map = new HashMap<>();
            String prefix = DateUtil.GMTCurrentTimeStamp() + "\n";
            map.put("signature", EncryptUtil.signature(key, prefix + info));
            map.put("fileInfo", URLEncoder.encode(CalcUtil.getBase64(prefix + info), "UTF-8"));
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @RequestMapping(method = {RequestMethod.GET}, value = "getSignatureAndPolicy.json")
    @ResponseBody
    public Map getSignatureAndPolicy(String info) {
        try {
            if (info == null || info.length() == 0) {
                return null;
            }
            int idxBr = info.indexOf("\\n");
            StringBuilder result = new StringBuilder();
            if (info.split("\n").length <= 1 && idxBr >= 0) {
                result.append(info.substring(0, idxBr)).append("\n");
                String tmp = info.substring(idxBr + 2);
                if ((idxBr = tmp.indexOf("\\n")) > 0) {
                    result.append(tmp.substring(0, idxBr)).append("\n").append(tmp.substring(idxBr + 2));
                } else {
                    result.append(tmp.substring(0, idxBr));
                }
            }

            info = result.toString();
            Map<String, String> map = new HashMap<>();
            map.put("signature", EncryptUtil.signature(key, info));
            map.put("policy", URLEncoder.encode(CalcUtil.getBase64(info), "UTF-8"));
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
