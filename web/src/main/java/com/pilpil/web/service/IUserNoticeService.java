package com.pilpil.web.service;

import com.pilpil.common.entity.dto.VideoFansMq;
import com.pilpil.common.entity.po.UserNotice;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 用户通知表 服务类
 * </p>
 *
 * @author 
 * @since 2026-05-30
 */
public interface IUserNoticeService extends IService<UserNotice> {

    void noticeFans(VideoFansMq fansMq);

    List<UserNotice> listByFromId();

    void noticeVideoComment(VideoFansMq fansMq);

    void receiveComment(VideoFansMq fansMq);

    Long counts();
}
