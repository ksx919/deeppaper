package com.fanxin.Service.minio;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fanxin.config.MinioConfig;
import com.fanxin.entity.Paper;
import com.fanxin.entity.dto.PaperPdfSaveDTO;
import com.fanxin.mapper.PaperMapper;
import com.fanxin.util.ThreadLocalUtil;
import io.minio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.UUID;

import static com.fanxin.common.ServiceExceptionUtil.exception;
import static com.fanxin.enums.PaperErrorCodeConstants.PAPER_UPLOAD_FAILED;

@Service
public class MinioService {
    @Autowired
    private MinioConfig minioConfig;
    @Autowired
    private MinioClient minioClient;
    @Autowired
    private PaperMapper paperMapper;

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
     * 上传头像文件到MinIO存储服务
     *
     * @param file 要上传的头像文件，类型为MultipartFile
     * @return 返回上传文件的公开访问URL
     * @throws Exception 上传过程中可能抛出的异常
     */
    public String uploadAvatarFile(MultipartFile file) throws Exception {
        Map<String,Object> claims = ThreadLocalUtil.get();
        String avatarUrl = (String) claims.get("avatar");
        deleteFileByPublicUrl(avatarUrl);
        // 生成唯一文件名（避免重名）
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        StringBuilder objectName = new StringBuilder();
        String originalNameWithoutExtension = originalFilename.substring(0, originalFilename.lastIndexOf("."));

        objectName.append("avatar/");

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
     * 上传论文文件到MinIO存储服务
     * 如果检测到用户已上传过同名文件，则返回已存在的文件信息
     * 否则上传新文件并返回文件信息
     *
     * @param file 要上传的论文文件，类型为MultipartFile
     * @return PaperPdfSaveDTO 包含文件哈希值、访问路径和文件大小的信息
     */
    public PaperPdfSaveDTO uploadPaperFile(MultipartFile file) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        Long id = ((Integer) claims.get("id")).longValue();

        // 获取文件原始信息
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        Long fileSize = file.getSize();

        // 生成唯一文件名（避免MinIO中对象名冲突）
        StringBuilder objectName = new StringBuilder();
        String originalNameWithoutExtension = originalFilename.substring(0, originalFilename.lastIndexOf("."));

        objectName.append("paper/");
        objectName.append(originalNameWithoutExtension)
                .append(UUID.randomUUID().toString().substring(24))
                .append(fileExtension);

        // 生成文件哈希值用于检测重复上传
        String hash = generateFileHash(id, originalFilename);

        // 检查是否已存在相同用户上传的同名文件
        LambdaQueryWrapper<Paper> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Paper::getUserId, id).eq(Paper::getFileHash, hash);

        Paper existingPaper = paperMapper.selectOne(wrapper);
        if (existingPaper != null) {
            // 如果存在重复文件，返回已存在的文件信息
            PaperPdfSaveDTO paperPdfSaveDTO = new PaperPdfSaveDTO();
            paperPdfSaveDTO.setFileHash(hash);
            paperPdfSaveDTO.setPdfPath(existingPaper.getPdfPath());
            paperPdfSaveDTO.setFileSize(existingPaper.getFileSize());
            return paperPdfSaveDTO;
        }

        // 上传文件到MinIO
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioConfig.getBucket())
                            .object(objectName.toString())
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());
        } catch (Exception e) {
            throw exception(PAPER_UPLOAD_FAILED);
        }

        // 构建返回对象
        PaperPdfSaveDTO paperPdfSaveDTO = new PaperPdfSaveDTO();
        paperPdfSaveDTO.setFileHash(hash);
        paperPdfSaveDTO.setPdfPath(getPublicUrl(objectName.toString()));
        paperPdfSaveDTO.setFileSize(fileSize);

        return paperPdfSaveDTO;
    }



    /**
     * 生成文件哈希值，用于检测重复上传
     * @param userId 用户ID
     * @param originalFilename 原始文件名
     * @return 哈希值
     */
    private String generateFileHash(Long userId, String originalFilename) {
        String input = userId + "_" + originalFilename;
        return String.valueOf(input.hashCode());
    }

    /**
     * 根据公开访问URL删除文件
     *
     * @param publicUrl 文件的公开访问URL
     */
    public void deleteFileByPublicUrl(String publicUrl) throws Exception {
        // 从URL中提取对象名
        // URL格式: http://域名:端口/桶名/对象名
        String url = minioConfig.getUrl();
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        // 构造需要被替换的前缀
        String prefix = url + "/" + minioConfig.getBucket() + "/";

        // 提取对象名
        String objectName = publicUrl.replace(prefix, "");

        // 删除文件
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