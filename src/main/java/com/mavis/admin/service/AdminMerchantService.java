package com.mavis.admin.service;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mavis.admin.dto.PageResult;
import com.mavis.common.exception.BusinessException;
import com.mavis.entity.Merchant;
import com.mavis.mapper.MerchantMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AdminMerchantService {

    @Autowired
    private MerchantMapper merchantMapper;

    public PageResult<Merchant> getMerchantPage(int page, int size, String name, Integer status) {
        Page<Merchant> p = new Page<>(page, size);
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(name)) {
            wrapper.like(Merchant::getName, name);
        }
        if (status != null) {
            wrapper.eq(Merchant::getStatus, status);
        }
        wrapper.orderByDesc(Merchant::getCreateTime);

        Page<Merchant> result = merchantMapper.selectPage(p, wrapper);

        PageResult<Merchant> pr = new PageResult<>();
        pr.setRecords(result.getRecords());
        pr.setTotal(result.getTotal());
        pr.setPage(result.getCurrent());
        pr.setSize(result.getSize());
        return pr;
    }

    public Merchant getMerchantDetail(Long id) {
        return merchantMapper.selectById(id);
    }

    public Merchant createMerchant(String name) {
        Merchant m = new Merchant();
        m.setName(name);
        m.setMerchantKey(IdUtil.fastSimpleUUID());
        m.setStatus(1);
        merchantMapper.insert(m);
        return m;
    }

    public Merchant updateMerchant(Long id, String name) {
        Merchant m = merchantMapper.selectById(id);
        if (m == null) throw new BusinessException("商户不存在");
        m.setName(name);
        merchantMapper.updateById(m);
        return m;
    }

    public void updateStatus(Long id, int status) {
        Merchant m = merchantMapper.selectById(id);
        if (m == null) throw new BusinessException("商户不存在");
        m.setStatus(status);
        merchantMapper.updateById(m);
    }

    public String resetKey(Long id) {
        Merchant m = merchantMapper.selectById(id);
        if (m == null) throw new BusinessException("商户不存在");
        m.setMerchantKey(IdUtil.fastSimpleUUID());
        merchantMapper.updateById(m);
        return m.getMerchantKey();
    }

    public void deleteMerchant(Long id) {
        merchantMapper.deleteById(id);
    }
}
