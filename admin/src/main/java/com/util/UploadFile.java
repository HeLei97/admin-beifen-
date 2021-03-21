package com.util;

import com.alibaba.fastjson.JSONObject;
import com.helei.api.common.ImplBase;
import net.sf.json.JSON;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class UploadFile extends ImplBase{
    /**
     * 上传文件
     * @param file
     * @param request
     * @param url 上传路径
     * @param name 文件名称
     * @param type 允许上传类型
     * @return
     */
    public Map uploadFile(MultipartFile file, HttpServletRequest request,String url,String name,String ... type) {
        Map map=new HashMap();
        String filePath = url;//上传到这个文件夹
        File file1 = new File(filePath);
        if (!file1.exists()) {
            file1.mkdirs();
        }
        boolean flag=true;
        String originalFilename = file.getOriginalFilename();
        String fileType = originalFilename.substring(originalFilename.lastIndexOf("."));
        if (type.length>0) {
            for (int i = 0; i < type.length; i++) {
                if (!type[i].equalsIgnoreCase(fileType)) {
                    flag=false;
                }else {
                    flag=true;
                    break;
                }
            }
        }
        if (!flag){
            return new HashMap(){{
                put("code",-1);
                put("msg","文件格式不允许上传");
            }};
        }
        String finalFilePath;
        if (isnull(name)) {
             finalFilePath = filePath + file.getOriginalFilename().trim();
        }else {
            finalFilePath=filePath+name+fileType;
        }
        File desFile = new File(finalFilePath);
        if (desFile.exists()) {
            desFile.delete();
        }
        try {
            file.transferTo(desFile);
            map.put("code",0);
            map.put("msg","上传成功");
            map.put("src",finalFilePath);
        } catch (Exception e) {
            map.put("code",-1);
            map.put("msg","上传失败:"+e.getMessage());
        }
        return map;
    }

    /**
     * 上传文件
     * @param file
     * @param request
     * @param url 上传路径
     * @param name 文件名称
     * @return
     */
    public Map uploadFile(MultipartFile file, HttpServletRequest request,String url,String name) {
        return uploadFile(file,request,url,name,null);
    }

    /**
     *
     * @param file
     * @param request
     * @param url 上传路径
     * @return
     */
    public Map uploadFile(MultipartFile file, HttpServletRequest request,String url) {
        return uploadFile(file,request,url,null,null);
    }
}
