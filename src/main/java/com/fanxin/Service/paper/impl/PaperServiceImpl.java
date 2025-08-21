package com.fanxin.Service.paper.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fanxin.Service.paper.PaperService;
import com.fanxin.entity.Paper;
import com.fanxin.entity.dto.PaperCreateDTO;
import com.fanxin.mapper.PaperMapper;
import com.fanxin.util.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class PaperServiceImpl extends ServiceImpl<PaperMapper, Paper> implements PaperService {

    @Autowired
    private PaperMapper paperMapper;

    @Override
    public void create(PaperCreateDTO paperCreateDTO) {
        Map<String,Object> claims =  ThreadLocalUtil.get();
        Long id = ((Integer) claims.get("id")).longValue();
        Paper p = new Paper();
        p.setUserId(id);
        p.setTitle(paperCreateDTO.getTitle());
        p.setAuthor(paperCreateDTO.getAuthor());
        p.setPdfPath(paperCreateDTO.getPaperPdfSaveDTO().getPdfPath());
        p.setFileSize(paperCreateDTO.getPaperPdfSaveDTO().getFileSize());
        p.setFileHash(paperCreateDTO.getPaperPdfSaveDTO().getFileHash());
        p.setCreatedAt(LocalDateTime.now());
        paperMapper.insert(p);
    }
}
