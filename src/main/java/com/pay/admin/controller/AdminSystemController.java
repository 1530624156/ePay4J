package com.pay.admin.controller;

import com.pay.admin.dto.Result;
import com.pay.admin.service.AdminSystemService;
import com.pay.entity.SystemConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/system")
public class AdminSystemController {

    @Autowired
    private AdminSystemService adminSystemService;

    @GetMapping("/config")
    public Result<List<SystemConfig>> getConfig() {
        return Result.ok(adminSystemService.getAllConfigs());
    }

    @PutMapping("/config")
    public Result<?> updateConfig(@RequestBody List<SystemConfig> configs) {
        adminSystemService.batchUpdate(configs);
        return Result.ok();
    }
}
