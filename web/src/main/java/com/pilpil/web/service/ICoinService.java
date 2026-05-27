package com.pilpil.web.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.pilpil.common.entity.po.Coin;
import com.pilpil.web.entity.dto.CoinDto;

/**
 * <p>
 * 投币表 服务类
 * </p>
 *
 * @author 
 * @since 2026-05-27
 */
public interface ICoinService extends IService<Coin> {

    void saveCoin(CoinDto coinDto);
}
