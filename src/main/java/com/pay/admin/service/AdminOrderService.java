package com.pay.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pay.admin.dto.PageResult;
import com.pay.entity.PayOrder;
import com.pay.mapper.PayOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class AdminOrderService {

    @Autowired
    private PayOrderMapper payOrderMapper;

    @Autowired
    private com.pay.service.AlipayService alipayService;

    public PageResult<PayOrder> getOrderPage(int page, int size, String outTradeNo,
                                               Integer status, String startDate, String endDate, String payType) {
        Page<PayOrder> p = new Page<>(page, size);
        LambdaQueryWrapper<PayOrder> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(outTradeNo)) {
            wrapper.eq(PayOrder::getOutTradeNo, outTradeNo);
        }
        if (status != null) {
            wrapper.eq(PayOrder::getStatus, status);
        }
        if (StringUtils.hasText(payType)) {
            wrapper.eq(PayOrder::getPayType, payType);
        }
        if (StringUtils.hasText(startDate)) {
            wrapper.ge(PayOrder::getCreateTime, LocalDate.parse(startDate).atStartOfDay());
        }
        if (StringUtils.hasText(endDate)) {
            wrapper.le(PayOrder::getCreateTime, LocalDate.parse(endDate).atTime(LocalTime.MAX));
        }
        wrapper.orderByDesc(PayOrder::getCreateTime);

        Page<PayOrder> result = payOrderMapper.selectPage(p, wrapper);

        PageResult<PayOrder> pr = new PageResult<>();
        pr.setRecords(result.getRecords());
        pr.setTotal(result.getTotal());
        pr.setPage(result.getCurrent());
        pr.setSize(result.getSize());
        return pr;
    }

    public PayOrder getOrderDetail(Long id) {
        return payOrderMapper.selectById(id);
    }

    public void refundOrder(Long id, BigDecimal refundAmount) throws Exception {
        PayOrder order = payOrderMapper.selectById(id);
        if (order == null) throw new com.pay.common.exception.BusinessException("订单不存在");
        if (order.getStatus() != 1) throw new com.pay.common.exception.BusinessException("只能退款已支付的订单");
        boolean success = alipayService.refund(order.getOutTradeNo(), refundAmount);
        if (!success) throw new com.pay.common.exception.BusinessException("退款失败");
    }

    public void closeOrder(Long id) throws Exception {
        PayOrder order = payOrderMapper.selectById(id);
        if (order == null) throw new com.pay.common.exception.BusinessException("订单不存在");
        if (order.getStatus() != 0) throw new com.pay.common.exception.BusinessException("只能关闭待支付的订单");
        boolean success = alipayService.close(order.getOutTradeNo());
        if (!success) throw new com.pay.common.exception.BusinessException("关闭失败");
    }

    public void deleteOrder(Long id) {
        PayOrder order = payOrderMapper.selectById(id);
        if (order == null) throw new com.pay.common.exception.BusinessException("订单不存在");
        if (order.getStatus() == 0) throw new com.pay.common.exception.BusinessException("请先关闭待支付订单");
        payOrderMapper.deleteById(id);
    }

    public void batchDeleteOrders(List<Long> ids) {
        payOrderMapper.deleteBatchIds(ids);
    }
}
