package com.zhongy.controller;

import com.zhongy.file.FastDFSFile;
import com.zhongy.user.feign.UserFeign;
import com.zhongy.util.FastDFSUtil;
import entity.Result;
import entity.StatusCode;
import org.csource.fastdfs.StorageServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/upload")
public class FileUploadController {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 文件上传
     */
    @PostMapping
    public Result upload(@RequestParam(value = "file")MultipartFile file) throws Exception{
        //调用工具类将文件上传到FastDFS中
        FastDFSFile fastDFSFile = new FastDFSFile(
                file.getOriginalFilename(), //文件名 1.jpg
                file.getBytes(), //文件字节数组
                StringUtils.getFilenameExtension(file.getOriginalFilename()) // 获取文件扩展名
        );
        String[] uploads = FastDFSUtil.upload(fastDFSFile);

        //拼接访问地址 url = http://192.168.211.201/group1/......
        String url = FastDFSUtil.getTrackerInfo() + ":8001/"+ uploads[0] + "/" + uploads[1];
        redisTemplate.boundHashOps("uploadFile").put(url, url);
        return new Result(true, StatusCode.OK, "上传成功！", url);
    }
}
