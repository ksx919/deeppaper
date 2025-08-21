package com.fanxin.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "论文PDF数据传输对象")
public class PaperPdfSaveDTO {
    private String pdfPath;

    private Long fileSize;

    private String fileHash;
}
