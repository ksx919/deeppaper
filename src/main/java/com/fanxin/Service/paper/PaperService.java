package com.fanxin.Service.paper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fanxin.entity.Paper;
import com.fanxin.entity.dto.PaperCreateDTO;

public interface PaperService extends IService<Paper> {
    void create(PaperCreateDTO paperCreateDTO);
}
