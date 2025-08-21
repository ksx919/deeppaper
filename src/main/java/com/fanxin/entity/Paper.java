package com.fanxin.entity;

import com.baomidou.mybatisplus.annotation.TableField;
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

    @TableField(select = false)
    private Long fileSize;
    @TableField(select = false)
    private String fileHash;
    @TableField(select = false)
    private LocalDateTime createdAt;
    @TableField(select = false)
    private LocalDateTime updatedAt;
}
