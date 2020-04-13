package com.zyy.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.CreateBucketRequest;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.OSSObject;
import org.springframework.util.Assert;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.Objects;

/**
 * @author zhouyuyang_vendor
 */
public class OssUtil {

    // Endpoint以杭州为例，其它Region请按实际情况填写。
    private static final String endpoint = "http://oss-cn-hangzhou.aliyuncs.com";
    // 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
    private static final String accessKeyId = "<yourAccessKeyId>";
    private static final String accessKeySecret = "<yourAccessKeySecret>";

    private static OSS ossClient;

    static {
        init();
    }

    public static void init(){
        // 创建OSSClient实例。
        ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }

    /**
     * 创建存储空间
     * @param bucketName
     */
    public static void createBucket(String bucketName){
        Assert.notNull(bucketName, "bucketName Can not be empty");
        if (!exists(bucketName)){
            // 创建CreateBucketRequest对象。
            CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);

            // 如果创建存储空间的同时需要指定存储类型以及数据容灾类型, 可以参考以下代码。
            // 此处以设置存储空间的存储类型为标准存储为例。
            // createBucketRequest.setStorageClass(StorageClass.Standard);
            // 默认情况下，数据容灾类型为本地冗余存储，即DataRedundancyType.LRS。如果需要设置数据容灾类型为同城冗余存储，请替换为DataRedundancyType.ZRS。
            // createBucketRequest.setDataRedundancyType(DataRedundancyType.ZRS)

            // 创建存储空间。
            ossClient.createBucket(createBucketRequest);
        }
    }

    /**
     * 存储空间是否存在
     * @param bucketName
     * @return
     */
    public static boolean exists(String bucketName){
        Assert.notNull(bucketName, "bucketName Can not be empty");
        return ossClient.doesBucketExist(bucketName);
    }


    /**
     * 删除存储空间
     * @param bucketName
     * @return
     */
    public static void deleteBucket(String bucketName){
        Assert.notNull(bucketName, "bucketName Can not be empty");
        // 删除存储空间。
        ossClient.deleteBucket(bucketName);
    }

    /**
     * 上传字节数组文件
     * @param bucketName
     * @param objectName
     * @param bytes
     */
    public static void upload(String bucketName, String objectName, byte[] bytes){
        check(bucketName, objectName);
        // 上传Byte数组。
        ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(bytes));
    }

    /**
     * 上传文件
     * @param bucketName
     * @param objectName
     * @param file
     */
    public static void upload(String bucketName, String objectName, File file){
        check(bucketName, objectName);
        ossClient.putObject(bucketName, objectName, file);
    }

    /**
     * 上传网络文件
     * @param bucketName
     * @param objectName
     * @param url
     */
    public static void upload(String bucketName, String objectName, String url) throws IOException {
        check(bucketName, objectName);
        InputStream inputStream = new URL(url).openStream();
        ossClient.putObject(bucketName, objectName, inputStream);
    }

    /**
     * 下载文件到本地
     * @param bucketName
     * @param objectName
     * @param localPath
     */
    public static void download(String bucketName, String objectName, String localPath){
        if (!exists(bucketName)){
            throw new IllegalArgumentException("bucket does not exist");
        }
        // 下载OSS文件到本地文件。如果指定的本地文件存在会覆盖，不存在则新建。
        ossClient.getObject(new GetObjectRequest(bucketName, objectName), new File(localPath));
    }


    /**
     * 创建模拟文件夹
     * @param bucketName 存储空间
     * @param folder   模拟文件夹名如"qj_nanjing/"
     * @return  文件夹名
     */
    public static String createFolder(String bucketName, String folder){
        //文件夹名
        final String keySuffixWithSlash = folder;
        //判断文件夹是否存在，不存在则创建
        if(!ossClient.doesObjectExist(bucketName, keySuffixWithSlash)){
            //创建文件夹
            ossClient.putObject(bucketName, keySuffixWithSlash, new ByteArrayInputStream(new byte[0]));
            //得到文件夹名
            OSSObject object = ossClient.getObject(bucketName, keySuffixWithSlash);
            return object.getKey();
        }
        return keySuffixWithSlash;
    }

    /**
     * 根据key删除OSS服务器上的文件
     * @param bucketName  存储空间
     * @param folder  模拟文件夹名 如"qj_nanjing/"
     * @param key Bucket下的文件的路径名+文件名 如："upload/cake.jpg"
     */
    public static void deleteFile(String bucketName, String folder, String key){
        if (!exists(bucketName)){
            throw new IllegalArgumentException("bucket does not exist");
        }
        ossClient.deleteObject(bucketName, folder + key);
    }


    /**
     * 获得url链接
     *
     * @param key
     * @return
     */
    public String getUrl(String bucketName, String key) {
        // 设置URL过期时间为10年 3600l* 1000*24*365*10
        Date expiration = new Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 365 * 10);
        // 生成URL
        URL url = ossClient.generatePresignedUrl(bucketName, key, expiration);
        if (Objects.nonNull(url)){
            return url.toString();
        }
        return null;
    }


    private static void check(String bucketName, String objectName){
        Assert.notNull(bucketName, "bucketName Can not be empty");
        Assert.notNull(objectName, "objectName Can not be empty");
        if (!exists(bucketName)){
            createBucket(bucketName);
        }
    }



    public static void shutdown(){
        // 关闭OSSClient。
        if (Objects.nonNull(ossClient)){
            ossClient.shutdown();
        }
    }
}
