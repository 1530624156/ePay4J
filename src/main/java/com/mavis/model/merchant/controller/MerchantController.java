package com.mavis.model.merchant.controller;

import com.mavis.model.admin.dto.Result;
import com.mavis.entity.Merchant;
import com.mavis.entity.MerchantAccount;
import com.mavis.model.merchant.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/merchant")
public class MerchantController {

    @Autowired
    private MerchantService merchantService;

    /**
     * 获取当前商户用户信息
     */
    @GetMapping("/info")
    public Result<Map<String, Object>> getInfo() {
        Long userId = getCurrentUserId();
        Merchant merchant = merchantService.getMerchantInfoByUserId(userId);

        Map<String, Object> data = new HashMap<>();
        data.put("id", merchant.getId());
        data.put("name", merchant.getName());
        data.put("alipayAccount", merchant.getAlipayAccount());
        data.put("nickName", merchant.getNickName());
        data.put("phone", merchant.getPhone());

        return Result.ok(data);
    }

    /**
     * 获取当前商户账户信息（余额）
     */
    @GetMapping("/account")
    public Result<MerchantAccount> getAccount() {
        Long userId = getCurrentUserId();
        Merchant merchant = merchantService.getMerchantInfoByUserId(userId);
        MerchantAccount account = merchantService.getMerchantAccount(merchant.getId());
        return Result.ok(account);
    }

    /**
     * 修改商户信息
     */
    @PutMapping("/info")
    public Result<Merchant> updateInfo(@RequestBody Map<String, String> body) {
        Long userId = getCurrentUserId();
        Merchant merchant = merchantService.getMerchantInfoByUserId(userId);

        String alipayAccount = body.get("alipayAccount");
        String nickName = body.get("nickName");
        String phone = body.get("phone");

        Merchant updated = merchantService.updateMerchant(merchant.getId(), alipayAccount, nickName, phone);
        return Result.ok(updated);
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<?> changePassword(@RequestBody Map<String, String> body) {
        Long userId = getCurrentUserId();
        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");

        merchantService.changePassword(userId, oldPassword, newPassword);
        return Result.ok();
    }

    /**
     * 获取商户密钥信息
     */
    @GetMapping("/credentials")
    public Result<Map<String, Object>> getCredentials() {
        Long userId = getCurrentUserId();
        Merchant merchant = merchantService.getMerchantInfoByUserId(userId);

        Map<String, Object> data = new HashMap<>();
        data.put("pid", merchant.getId());
        data.put("key", merchant.getMerchantKey());
        data.put("name", merchant.getName());

        return Result.ok(data);
    }

    /**
     * 重置商户密钥
     */
    @PostMapping("/reset-key")
    public Result<Map<String, Object>> resetKey() {
        Long userId = getCurrentUserId();
        Merchant merchant = merchantService.getMerchantInfoByUserId(userId);

        String newKey = merchantService.resetKey(merchant.getId());

        Map<String, Object> data = new HashMap<>();
        data.put("pid", merchant.getId());
        data.put("key", newKey);

        return Result.ok(data);
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (Long) auth.getPrincipal();
    }
}
