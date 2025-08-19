package com.fanxin.Service.minio;

import com.fanxin.config.MinioConfig;
import io.minio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import jakarta.annotation.PostConstruct;

import java.util.UUID;

@Service
public class MinioService {
    @Autowired
    private MinioConfig minioConfig;
    @Autowired
    private MinioClient minioClient;

    /**
     * 应用启动时初始化，设置现有桶为公开访问
     */
    @PostConstruct
    public void initBucket() {
        try {
            // 检查桶是否存在
            if (minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioConfig.getBucket()).build())) {
                // 桶已存在，检查是否为公开访问
                if (!isBucketPublic()){
                    setBucketPublic();
                    System.out.println("已将现有桶 [" + minioConfig.getBucket() + "] 设置为公开访问");
                }
            } else {
                // 桶不存在，创建并设置为公开
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioConfig.getBucket()).build());
                setBucketPublic();
                System.out.println("已创建新桶 [" + minioConfig.getBucket() + "] 并设置为公开访问");
            }
            System.out.println("初始化桶[" + minioConfig.getBucket() + "] 成功！");
        } catch (Exception e) {
            System.err.println("初始化MinIO桶时出错: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 上传文件到MinIO
     * @param file 要上传的文件（Spring MultipartFile）
     * @param type 文件类型 0:头像 1:论文
     * @return 文件的公开访问URL
     */
    public String uploadFile(MultipartFile file, Short type) throws Exception {
        // 生成唯一文件名（避免重名）
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        StringBuilder objectName = new StringBuilder();
        String originalNameWithoutExtension = originalFilename.substring(0, originalFilename.lastIndexOf("."));

        if (type == 0) objectName.append("avatar/");
        else if (type == 1) objectName.append("paper/");

        objectName.append(originalNameWithoutExtension)
                .append(UUID.randomUUID().toString().substring(24))
                .append(fileExtension);

        // 上传文件
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(minioConfig.getBucket())
                        .object(objectName.toString())
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build());

        // 返回公开访问URL（直接拼接，无需预签名）
        return getPublicUrl(objectName.toString());
    }

    /**
     * 删除文件
     * @param objectName 文件在MinIO中的唯一标识
     */
    public void deleteFile(String objectName) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(minioConfig.getBucket())
                        .object(objectName)
                        .build());
    }

    /**
     * 设置存储桶为公开读取权限
     */
    private void setBucketPublic() throws Exception {
        // 设置桶策略为公开读取
        String policy = "{\n" +
                "  \"Version\": \"2012-10-17\",\n" +
                "  \"Statement\": [\n" +
                "    {\n" +
                "      \"Effect\": \"Allow\",\n" +
                "      \"Principal\": {\n" +
                "        \"AWS\": \"*\"\n" +
                "      },\n" +
                "      \"Action\": \"s3:GetObject\",\n" +
                "      \"Resource\": \"arn:aws:s3:::" + minioConfig.getBucket() + "/*\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        minioClient.setBucketPolicy(
                SetBucketPolicyArgs.builder()
                        .bucket(minioConfig.getBucket())
                        .config(policy)
                        .build());
    }

    /**
     * 构建公开访问URL
     * @param objectName 对象名称
     * @return 公开访问URL
     */
    private String buildPublicUrl(String objectName) {
        // 直接拼接公开访问URL
        // 格式: http://域名:端口/桶名/对象名
        String url = minioConfig.getUrl();
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        return url + "/" + minioConfig.getBucket() + "/" + objectName;
    }

    /**
     * 根据对象名称获取公开访问URL
     * @param objectName 对象名称
     * @return 公开访问URL
     */
    public String getPublicUrl(String objectName) {
        return buildPublicUrl(objectName);
    }

    /**
     * 检查存储桶是否为公开访问
     */
    private boolean isBucketPublic() {
        try {
            String policy = minioClient.getBucketPolicy(
                    GetBucketPolicyArgs.builder()
                            .bucket(minioConfig.getBucket())
                            .build());

            // 简单检查策略中是否包含允许公开读取的设置
            return policy != null && policy.contains("Allow") && policy.contains("s3:GetObject");
        } catch (Exception e) {
            // 如果获取策略失败，则认为不是公开的
            return false;
        }
    }
}