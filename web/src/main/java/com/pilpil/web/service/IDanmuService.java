package com.pilpil.web.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.pilpil.comment.entity.po.Danmu;
import com.pilpil.web.entity.dto.DanmuDto;
import com.pilpil.web.entity.dto.queryDanmuDto;
import com.pilpil.web.entity.vo.DanmuVo;

import java.util.List;

/**
 * <p>
 * 弹幕表 服务类
 * </p>
 *
 * @author 
 * @since 2026-05-25
 */
public interface IDanmuService extends IService<Danmu> {

    void saveDanmu(DanmuDto danmuDto);

    List<DanmuVo> listDanmu(queryDanmuDto queryDanmuDto);

    void deleteDanmu(Integer id);
}
