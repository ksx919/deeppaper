package com.fanxin.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "论文创建信息")
public class PaperCreateDTO {
    @Schema(description = "论文标题")
    private String title;
    @Schema(description = "论文作者")
    private String author;
    @Schema(description = "论文pdf位置")
    private PaperPdfSaveDTO paperPdfSaveDTO;
}
