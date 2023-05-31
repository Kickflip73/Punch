package com.yrmjhtdjxh.punch.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectResult;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.net.URL;
import java.util.Date;

/**
 * @author ：GO FOR IT
 * @description：
 * @date ：2021/4/13 21:28
 */
@Slf4j
public class OssUtil {

    // Endpoint以杭州为例，其它Region请按实际情况填写。
    final static String endpoint = "https://oss-cn-shanghai.aliyuncs.com";
    // 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录RAM控制台创建RAM账号。
    final static String accessKeyId = "LTAI5t5s7eucFkHhxsaf3dVK";
    final static String accessKeySecret = "gcE53C6OYCtlao0a9Pf09xakfyjjsR";
    final static String bucketName = "shanghai-bucket-image";
    // <yourObjectName>上传文件到OSS时需要指定包含文件后缀在内的完整路径，例如abc/efg/123.jpg。
    private static OSS ossClient;

    public static void upImageToOSS(String targetName, InputStream inputStream){
        ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        boolean objectExist = ossClient.doesObjectExist(bucketName, targetName);
        if (objectExist){
            log.info("{}文件存在，删除再上传",targetName);
            ossClient.deleteObject(bucketName, targetName);
        }
        String style = "image/auto-orient,1/interlace,1/resize,m_lfit,w_100/quality,q_50";
        ossClient.putObject(bucketName,  targetName,inputStream);
        Date date = new Date(System.currentTimeMillis() + 3600 * 1000);

        URL url = ossClient.generatePresignedUrl(bucketName, targetName, date);
        System.out.println(url);
        // 关闭OSSClient。
        ossClient.shutdown();
    }
}
