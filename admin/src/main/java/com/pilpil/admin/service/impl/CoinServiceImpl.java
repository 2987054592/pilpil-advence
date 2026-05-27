package com.pilpil.admin.service.impl;


import com.pilpil.admin.mapper.CoinMapper;
import com.pilpil.admin.service.ICoinService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pilpil.common.entity.po.Coin;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 投币表 服务实现类
 * </p>
 *
 * @author 
 * @since 2026-05-27
 */
@Service
public class CoinServiceImpl extends ServiceImpl<CoinMapper, Coin> implements ICoinService {

}
