package com.mavis.model.merchant.controller;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mavis.entity.Merchant;
import com.mavis.entity.MerchantWithdraw;
import com.mavis.model.admin.dto.Result;
import com.mavis.model.merchant.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/merchant/withdraw")
public class MerchantWithdrawController {

    @Autowired
    private MerchantService merchantService;

    /**
     * 提交提现任务
     */
    @PostMapping
    public Result<MerchantWithdraw> withdraw(@RequestBody Map<String, String> body) {
        Long userId = getCurrentUserId();
        Merchant merchant = merchantService.getMerchantInfoByUserId(userId);

        String amountStr = body.get("amount");
        if (amountStr == null || StringUtils.isBlank(amountStr)) {
            throw new IllegalArgumentException("提现金额不能为空");
        }
        BigDecimal amount = new BigDecimal(amountStr);

        MerchantWithdraw withdraw = merchantService.withdraw(merchant.getId(), amount);
        return Result.ok(withdraw);
    }

    /**
     * 查询提现记录
     */
    @GetMapping("/records")
    public Result<Map<String, Object>> getWithdrawRecords(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = getCurrentUserId();
        Merchant merchant = merchantService.getMerchantInfoByUserId(userId);

        Page<MerchantWithdraw> result = merchantService.getWithdrawRecords(merchant.getId(), page, size);

        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getRecords());
        data.put("total", result.getTotal());
        data.put("page", page);
        data.put("size", size);

        return Result.ok(data);
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (Long) auth.getPrincipal();
    }
}
