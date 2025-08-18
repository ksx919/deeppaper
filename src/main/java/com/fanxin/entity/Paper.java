package com.fanxin.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("paper")
public class Paper {
    @TableId
    private Long id;
    private Long userId;
    private String title;
    private String author;
    private String pdfPath;
    private Long fileSize;
    private String fileHash;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
