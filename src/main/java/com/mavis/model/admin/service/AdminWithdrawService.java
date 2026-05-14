package com.mavis.model.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mavis.common.exception.BusinessException;
import com.mavis.entity.Merchant;
import com.mavis.entity.MerchantAccount;
import com.mavis.entity.MerchantWithdraw;
import com.mavis.mapper.MerchantAccountMapper;
import com.mavis.mapper.MerchantMapper;
import com.mavis.mapper.MerchantWithdrawMapper;
import com.mavis.model.admin.dto.WithdrawDetailVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminWithdrawService {

    @Autowired
    private MerchantWithdrawMapper merchantWithdrawMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private MerchantAccountMapper merchantAccountMapper;

    public Page<MerchantWithdraw> getWithdrawPage(int page, int size, Long merchantId, String merchantName, Integer status) {
        Page<MerchantWithdraw> p = new Page<>(page, size);
        LambdaQueryWrapper<MerchantWithdraw> wrapper = new LambdaQueryWrapper<>();

        if (merchantId != null) {
            wrapper.eq(MerchantWithdraw::getMerchantId, merchantId);
        }

        if (merchantName != null && StringUtils.isNotBlank(merchantName)) {
            List<Merchant> merchants = merchantMapper.selectList(
                    new LambdaQueryWrapper<Merchant>().like(Merchant::getName, merchantName)
            );
            if (!merchants.isEmpty()) {
                List<Long> merchantIds = new ArrayList<>();
                for (Merchant m : merchants) {
                    merchantIds.add(m.getId());
                }
                wrapper.in(MerchantWithdraw::getMerchantId, merchantIds);
            } else {
                wrapper.eq(MerchantWithdraw::getMerchantId, -1L);
            }
        }

        if (status != null) {
            wrapper.eq(MerchantWithdraw::getStatus, status);
        }

        wrapper.orderByDesc(MerchantWithdraw::getCreateTime);
        return merchantWithdrawMapper.selectPage(p, wrapper);
    }

    public MerchantWithdraw getWithdrawDetail(Long id) {
        MerchantWithdraw withdraw = merchantWithdrawMapper.selectById(id);
        if (withdraw == null) {
            throw new BusinessException("提现记录不存在");
        }
        return withdraw;
    }

    public WithdrawDetailVO getWithdrawDetailVO(Long id) {
        MerchantWithdraw withdraw = merchantWithdrawMapper.selectById(id);
        if (withdraw == null) {
            throw new BusinessException("提现记录不存在");
        }

        Merchant merchant = merchantMapper.selectById(withdraw.getMerchantId());

        WithdrawDetailVO vo = new WithdrawDetailVO();
        BeanUtils.copyProperties(withdraw, vo);
        if (merchant != null) {
            vo.setMerchantName(merchant.getName());
            vo.setAlipayAccount(merchant.getAlipayAccount());
            vo.setNickName(merchant.getNickName());
            vo.setPhone(merchant.getPhone());
        }

        return vo;
    }

    public void approve(Long id) {
        MerchantWithdraw withdraw = merchantWithdrawMapper.selectById(id);
        if (withdraw == null) {
            throw new BusinessException("提现记录不存在");
        }
        if (withdraw.getStatus() != 0) {
            throw new BusinessException("只能处理待处理的提现任务");
        }

        // 更新状态为已提现
        withdraw.setStatus(1);
        withdraw.setUpdateTime(LocalDateTime.now());
        merchantWithdrawMapper.updateById(withdraw);

        // 冻结金额减少（提现成功，冻结金额扣减）
        MerchantAccount account = merchantAccountMapper.selectOne(
                new LambdaQueryWrapper<MerchantAccount>()
                        .eq(MerchantAccount::getMerchantId, withdraw.getMerchantId())
        );
        if (account != null) {
            account.setFrozenBalance(account.getFrozenBalance().subtract(withdraw.getAmount()));
            merchantAccountMapper.updateById(account);
        }
    }

    public void reject(Long id, String reason) {
        if (reason == null || StringUtils.isBlank(reason)) {
            throw new IllegalArgumentException("请填写拒绝理由");
        }

        MerchantWithdraw withdraw = merchantWithdrawMapper.selectById(id);
        if (withdraw == null) {
            throw new BusinessException("提现记录不存在");
        }
        if (withdraw.getStatus() != 0) {
            throw new BusinessException("只能处理待处理的提现任务");
        }

        // 更新状态为已拒绝
        withdraw.setStatus(2);
        withdraw.setRemark(reason);
        withdraw.setUpdateTime(LocalDateTime.now());
        merchantWithdrawMapper.updateById(withdraw);

        // 冻结金额释放回可用余额
        MerchantAccount account = merchantAccountMapper.selectOne(
                new LambdaQueryWrapper<MerchantAccount>()
                        .eq(MerchantAccount::getMerchantId, withdraw.getMerchantId())
        );
        if (account != null) {
            account.setAvailableBalance(account.getAvailableBalance().add(withdraw.getAmount()));
            account.setFrozenBalance(account.getFrozenBalance().subtract(withdraw.getAmount()));
            merchantAccountMapper.updateById(account);
        }
    }
}
