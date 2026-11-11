package com.zjjxy.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class AfterSaleTools {

    // 工具1：查询订单
    @Tool(description = "根据订单编号查询订单信息，返回订单金额和状态（PAID已支付/SHIPPED已发货/COMPLETED已完成）。" +
            "查询订单、退款、查询物流前都应该先调用它进行确认订单是否存在。")
    public String queryOrder(@ToolParam(description = "订单号，如:A1001") String orderNo) {
        return switch (orderNo) {
            case "A1001" -> "订单A1001：金额99.9元，状态PAID【已支付】";
            case "A1002" -> "订单A1002：金额199.9元，状态SHIPPED【已发货】";
            case "A1003" -> "订单A1003：金额299元，状态PAID【已支付】";
            default -> "订单" + orderNo + "不存在";
        };
    }

    // 工具2：查询物流
    @Tool(description = "查询订单的物流进度，返回包裹当前位置。仅当用户查询物流、快递、到哪儿了时调用。")
    public String queryLogistics(@ToolParam(description = "订单号，如A1001") String orderNo) {
        return switch (orderNo) {
            case "A1001" -> "订单A1001物流：正在仓库打包中";
            case "A1002" -> "订单A1002物流：已到杭州转运中心，预计明天送达";
            default -> "订单" + orderNo + "暂无物流信息";
        };
    }

    // 工具3：退款
    @Tool(description = "对指定订单发起退款。退款前必须先调用queryOrder确认订单存在且状态为PAID（已支付）。" +
            "若状态为SHIPPED或COMPLETED则不能退款。")
    public String refund(
            @ToolParam(description = "订单号") String orderNo,
            @ToolParam(description = "退款金额，须和订单金额一致") double amount) {
        return "退款成功：订单" + orderNo + "，金额" + amount + "元";
    }

    // 工具4：发送通知
    @Tool(description = "给用户发送售后结果通知。退款或查询完成后调用，通知用户处理结果。")
    public String sendMessage(@ToolParam(description = "通知内容") String content) {
        return "已推送通知至用户：" + content;
    }
}