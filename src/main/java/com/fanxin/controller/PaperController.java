package com.fanxin.controller;

import com.fanxin.Service.minio.MinioService;
import com.fanxin.Service.paper.PaperService;
import com.fanxin.common.CommonResult;
import com.fanxin.entity.Paper;
import com.fanxin.entity.dto.PaperCreateDTO;
import com.fanxin.entity.dto.PaperPdfSaveDTO;
import com.fanxin.util.ThreadLocalUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

import static com.fanxin.enums.PaperErrorCodeConstants.*;

@RestController
@RequestMapping("/paper")
@Tag(name = "论文管理", description = "论文管理")
public class PaperController {

    @Autowired
    private PaperService paperService;
    @Autowired
    private MinioService minioService;

    @PostMapping("/create")
    @Operation(summary = "创建论文", description = "创建论文")
    public CommonResult<Boolean> createPaper(@io.swagger.v3.oas.annotations.parameters.
            RequestBody(description = "论文信息", required = true, content = @Content(schema = @Schema(implementation = Paper.class)))
                                               @RequestBody PaperCreateDTO paperCreateDTO) {
        paperService.create(paperCreateDTO);
        return CommonResult.success();
    }

    @GetMapping("/get")
    @Operation(summary = "获取论文详情", description = "获取论文详情")
    public CommonResult<Paper> getPaper(@Parameter(name = "id", description = "论文id", required = true) @RequestParam String id) {
        return CommonResult.success(paperService.getById(id));
    }

    @PostMapping("/upload")
    @Operation(summary = "上传论文", description = "上传论文")
    public CommonResult<PaperPdfSaveDTO> uploadPaper(@Parameter(name = "file", description = "论文文件", required = true) @RequestParam MultipartFile file) {
        return CommonResult.success(minioService.uploadPaperFile(file));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除论文", description = "删除论文")
    public CommonResult<Boolean> deletePaper(@Parameter(name = "id", description = "论文id", required = true) @RequestParam String id) {
        Paper paper = paperService.getById(id);
        if (paper == null) {
            return CommonResult.error(PAPER_NOT_EXIST);
        }
        Map<String,Object> claims = ThreadLocalUtil.get();
        Long userId = ((Integer) claims.get("id")).longValue();
        if (!paper.getUserId().equals(userId)) {
            return CommonResult.error(PAPER_NO_PERMISSION_DELETE);
        }
        try {
            minioService.deleteFileByPublicUrl(paper.getPdfPath());
        } catch (Exception e) {
            return CommonResult.error(PAPER_DELETE_FAILED);
        }
        return CommonResult.success(paperService.removeById(id));
    }
}
