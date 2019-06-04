package com.zyy.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * @Author:zhouyuyang
 * @Description:
 * @Date: Created in 11:58 2019/5/27
 */
@RestController
@Api(tags = "文件上传接口")
public class FileUploadController {

    @ApiOperation(value = "上传",notes = "文件上传")
    //@ApiImplicitParam(paramType = "form" , dataType = "File", name = "file",value = "文件" ,required = true)
    @PostMapping("/upload")
    public String upload(@ApiParam(value = "上传的文件",required = true) MultipartFile file, HttpServletRequest req) throws Exception {
        String path =  "/uploadFile/";
        String realPath = req.getSession().getServletContext().getRealPath(path);
        String format = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        File folder = new File(realPath + format);
        if (!folder.isDirectory()){
            folder.mkdirs();
        }
        String oldName = file.getOriginalFilename();
        String newName = UUID.randomUUID().toString()+oldName.substring(oldName.lastIndexOf("."),oldName.length());
        try {
            file.transferTo(new File(folder, newName));
            //req.getScheme()获取协议名称   req.getServerName()获取服务器的名字;
            String filePath = req.getScheme() + "://" + req.getServerName() + ":" +req.getServerPort() + path + format + "/" + newName;
            return filePath;
        }catch (IOException e){
            e.printStackTrace();
        }
        return "上传失败!";
    }

}
