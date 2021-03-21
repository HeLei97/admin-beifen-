package com.module.app.service;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePayRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradePayResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.helei.api.common.ImplBase;
import com.helei.api.util.DateUtils;
import com.helei.api.util.PwdUtil;
import com.util.OrderState;
import com.util.OrderUtils;
import net.bytebuddy.implementation.bytecode.Throw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SuppressWarnings({"serial", "unchecked", "rawtypes"})
@Service("appApiService")
public class ApiService extends ImplBase {

    @Autowired
    private DbService dbService;

    public Map login(Map map) {
        try {
            List<Map> list = dbService.getUser(new HashMap() {{
                put("username", map.get("username"));
                put("password", PwdUtil.encodePwd(map.get("password").toString()));
            }});
            if (list.size() > 0) {
                if ("1".equals(list.get(0).get("loginlock"))) {
                    return dbService.getReturn_status("20010");
                } else {
                    return dbService.getReturn_status("10000", new HashMap() {{
                        put("username", list.get(0).get("loginlock"));
                        put("MC", list.get(0).get("MC"));
                        put("BM", list.get(0).get("BM"));
                    }});
                }
            } else {
                return dbService.getReturn_status("20006");
            }
        } catch (Exception e) {
            getLogger().warn(e);
            dbService.setLog(e, null, null);
            return dbService.getReturn_status("20000");
        }
    }

    public Map pay(Map map) {
        try {
            Object type = map.get("type");
            String order_id = map.get("order_id").toString();
            if (isnull(type)) {
                throw new Exception("支付方式错误");
            } else if ("alipay".equals(type)) {
                String orderCode = OrderUtils.getOrderCode(type.toString(), 1);
                if (isnull(orderCode)) {
                    throw new Exception("订单号生成失败");
                } else {
                     Map res=alipayPay("19970220",orderCode, map.get("subject").toString(), map.get("auth_code").toString(), map.get("total_amount").toString(), order_id);
                     dbService.updateOrder(new HashMap(){{
                         put("order_id",order_id);
                     }},new HashMap(){{
                         put("payment_method",type);
                         put("pay_order_no",res.get("out_trade_no"));
                         if (res.get("return_code").equals(OrderState.TRADE_SUCCESS)){
                             put("state",OrderState.TRADE_SUCCESS);
                         }else if (res.get("return_code").equals(OrderState.TRADE_ERROR)){
                             put("state",OrderState.TRADE_ERROR);
                         }
                     }});
                     return res;
                }
            } else if ("wxpay".equals(type)) {
                throw new Exception("暂时不支持微信");
            } else {
                throw new Exception("支付方式错误");
            }
        } catch (Exception e) {
            getLogger().warn(e);
            dbService.setLog(e, null, map);
            return dbService.getReturn_status("20000");
        }
    }

    /**
     * @param out_trade_no 订单号
     * @param subject      商品名称
     * @param auth_code    付款码
     * @param total_amount 金额
     * @param order_id     订单号
     * @return
     */
    public Map alipayPay(String bm,String out_trade_no, String subject, String auth_code, String total_amount, String order_id) throws Exception {
        return new HashMap() {
            {
                String trade_no="";
                try {
                    List<Map> list=dbService.getPayConfig(new HashMap(){{
                        put("bm",bm);
                    }});
                    if (list.size()<=0){
                        throw new Exception("未获取到支付配置信息");
                    }else if (!list.get(0).get("alipay_state").equals("0")){
                        throw new Exception("商户已停用");
                    }
                    AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do",
                            list.get(0).get("ali_appid").toString(), list.get(0).get("ali_privateKey").toString(), "json", "GBK",
                            list.get(0).get("alipayPublicKey").toString(), list.get(0).get("ali_signType").toString());
                    AlipayTradePayRequest request = new AlipayTradePayRequest();
                    Map data = new HashMap() {
                        {
                            put("out_trade_no", out_trade_no);
                            put("scene", "bar_code");
                            put("auth_code", auth_code);
                            put("product_code", "FACE_TO_FACE_PAYMENT");
                            put("subject", subject);
                            put("total_amount", total_amount);
                        }
                    };
                    request.setBizContent(toJson(data));
                    AlipayTradePayResponse response = alipayClient.execute(request);
                    dbService.addPayinfo(new HashMap() {{
                        put("order_id", order_id);
                        put("Request_parameters", toJson(data));
                        put("Return_parameters", response.getBody());
                    }});
                    System.out.print("支付宝扫码返回信息：" + response.getBody());
                    System.out.print(response);
                    Map respData = new HashMap<Object, Object>();
                    respData.put("out_trade_no", response.getOutTradeNo());
                    respData.put("trade_no", response.getTradeNo());
                    respData.put("buyer_logon_id", response.getBuyerLogonId());
                    respData.put("total_amount", response.getTotalAmount());
                    respData.put("receipt_amount", response.getReceiptAmount());
                    respData.put("subject", subject);
                    respData.put("buyer_user_id", response.getBuyerUserId());
                    respData.put("transaction_time", response.getGmtPayment());
                    respData.put("point_amount", response.getPointAmount());
                    if (!response.getCode().equals("10000")) {
                        respData.put("sub_code", response.getSubCode());
                        respData.put("sub_msg", response.getSubMsg());
                    }
                    respData.put("add_time", DateUtils.currentDateDefaultString());


                    if ("10000".equals(response.getCode())) {
                        put("SuccessMsg", "订单支付成功");
                        put("transaction_id", response.getTradeNo());
                        put("out_trade_no", response.getOutTradeNo());
                        put("return_code", OrderState.TRADE_SUCCESS);
                        respData.put("order_state", OrderState.TRADE_SUCCESS);
                        trade_no=response.getTradeNo();
                    } else if ("10003".equals(response.getCode())) {
                        put("SuccessMsg", "订单支付状态未知");
                        put("err_code_des", response.getMsg());
                        put("return_code", OrderState.TRADE_UNKNOWN);
                        respData.put("order_state", OrderState.TRADE_UNKNOWN);
                    } else {
                        put("SuccessMsg", "订单支付失败");
                        put("err_code_des", response.getSubMsg());
                        put("return_code", OrderState.TRADE_ERROR);
                        respData.put("order_state", OrderState.TRADE_ERROR);
                    }
                    dbService.addorderPay(respData);
                } catch (Exception e) {
                    if (isnull(trade_no)){
                        String finalTrade_no = trade_no;
                        alirefund(new HashMap(){{
                            put("bm",bm);
                            put("trade_no", finalTrade_no);
                            put("out_trade_no",out_trade_no);
                            put("refund_amount",total_amount);
                            put("refund_reason","系统异常");
                            put("order_id",order_id);
                        }});
                    }
                    put("SuccessMsg", "订单支付失败");
                    put("err_code_des", e.getMessage());
                    put("return_code", "0");
                }
            }
        };
    };

    /**
     * 支付宝退款
     * @param map
     * bm 商户编码(本系统) 必填
     * out_trade_no 商户订单号  out_trade_no或trade_no 二选一
     * trade_no trade_no out_trade_no或trade_no 二选一
     * refund_amount 退款金额 必填
     * refund_reason  退款理由 选填
     * order_id 订单号 必填
     * @return
     */
    public Map alirefund(Map map) throws Exception {
        return new HashMap() {
            {
                try {
                    //String bm,String out_trade_no,String trade_no,String refund_amount,String refund_reason,String order_id
                    if (isnull(map.get("bm"))){
                        throw new Exception("商户编码不能为空");
                    }
                    if (isnull(map.get("out_trade_no")) && isnull(map.get("trade_no"))){
                        throw new Exception("out_trade_no和trade_no不能都为空");
                    } if (isnull(map.get("refund_amount"))){
                        throw new Exception("refund_amount不能为空");
                    }
                    if (isnull(map.get("order_id"))){
                        throw new Exception("order_id不能为空");
                    }
                    List<Map> list=dbService.getPayConfig(new HashMap(){{
                        put("bm",map.get("bm"));
                    }});
                    if (list.size()<=0){
                        throw new Exception("未获取到支付配置信息");
                    }else if (!list.get(0).get("alipay_state").equals("0")){
                        throw new Exception("商户已停用");
                    }
                    //查询订单信息
                    List<Map> orderlist=dbService.getOrder(new HashMap(){{
                        put("order_id", map.get("order_id"));
                    }});
                    if (orderlist.size()<=0){
                        throw new Exception("订单信息查询失败");
                    }
                    //查询支付信息
                    List<Map> paylist=dbService.getOrderPay(new HashMap(){{
                        if (!isnull(map.get("out_trade_no"))){
                            put("out_trade_no",map.get("out_trade_no"));
                        }else {
                            put("trade_no",map.get("trade_no"));
                        }
                    }});
                    if (paylist.size()<=0){
                        throw new Exception("支付信息查询失败");
                    }
                    BigDecimal money=(BigDecimal) orderlist.get(0).get("money");
                    BigDecimal refund_fee=(BigDecimal) orderlist.get(0).get("refund_fee");
                    BigDecimal refund_amount= objectConvertBigDecimal(map.get("refund_amount"));
                    if ((money.subtract(refund_fee)).compareTo(refund_amount)<0){
                        throw new Exception("退款金额大于当前实际收款金额");
                    }
                    String fefund_id= OrderUtils.getOrderCode("alipay", Integer.parseInt(map.get("bm").toString()));
                    map.put("out_request_no",fefund_id);
                    AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do",
                            list.get(0).get("ali_appid").toString(), list.get(0).get("ali_privateKey").toString(), "json", "GBK",
                            list.get(0).get("alipayPublicKey").toString(), list.get(0).get("ali_signType").toString());
                    AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();

                    request.setBizContent(toJson(map));
                    AlipayTradeRefundResponse response = alipayClient.execute(request);
                    dbService.addPayinfo(new HashMap() {{
                        put("order_id", map.get("order_id"));
                        put("Request_parameters", toJson(map));
                        put("Return_parameters", response.getBody());
                    }});
                    System.out.print("支付宝退款返回信息：" + response.getBody());
                    if (response.isSuccess()) {
                        Map respData = new HashMap<Object, Object>();
                        respData.put("refund_reason", map.get("refund_reason"));
                        respData.put("out_trade_no", response.getOutTradeNo());
                        respData.put("trade_no", response.getTradeNo());
                        respData.put("buyer_logon_id", response.getBuyerLogonId());
                        respData.put("buyer_user_id", response.getBuyerUserId());
                        respData.put("fund_change", response.getFundChange());
                        respData.put("refund_fee", response.getRefundFee());
                        respData.put("out_request_no",fefund_id);
                        if (!isnull(response.getSendBackFee())){
                            respData.put("send_back_fee",response.getSendBackFee());
                        }
                        dbService.addrefund(respData);
                        dbService.updateOrder(
                                new HashMap(){{
                                    put("order_id",map.get("order_id"));
                                }}
                                ,new HashMap(){{
                            put("refund_fee",response.getRefundFee());
                            if ((money.subtract(refund_fee)).compareTo(refund_amount)==0){
                                put("state",OrderState.FULL_REFUND);
                            }else {
                                put("state",OrderState.PARTIAL_REFUND);
                            }
                        }});
                        put("SuccessMsg", "订单退款成功");
                        put("transaction_id", response.getTradeNo());
                        put("return_code", "1");
                        put("refund_id", response.getOutTradeNo());
                    } else {
                        put("SuccessMsg", "订单退款失败");
                        put("err_code_des", response.getSubMsg());
                        put("return_code", "0");
                    }
                } catch (Exception e) {
                    put("SuccessMsg", "订单退款失败");
                    put("err_code_des", e.getMessage());
                    put("return_code", "0");
                    throw new Exception(e.getMessage());
                }
            }
        };
    }

    @Transactional
    public Map addOrder(Map map) {
        try {
            final String orderCode = isnull(map.get("order_id")) ? OrderUtils.getOrderCode(1) : map.get("order_id").toString();
            int res = 0;
            if (isnull(map.get("order_id"))) {
                res = dbService.addOrder(new HashMap() {{
                    put("order_id", orderCode);
                    put("money", map.get("total_amount"));
                    put("order_name", map.get("subject"));
                    put("state", 0);
                }});
            } else {
                List<Map> list = dbService.getOrder(new HashMap() {{
                    put("order_id", orderCode);
                }});
                if (list.size() > 0) {
                    if (list.get(0).get("state").equals(OrderState.WAIT_BUYER_PAY)) {
                        res = dbService.updateOrder(
                                new HashMap() {{
                                    put("order_id", map.get("order_id"));
                                }}
                                , new HashMap() {{
                                    put("money", map.get("total_amount"));
                                    put("order_name", map.get("subject"));
                                    put("state", 0);
                                }});
                    } else {
                        throw new Exception("订单状态有误");
                    }
                } else {
                    map.remove("order_id");
                    addOrder(map);
                }
                dbService.deleteGoods(new HashMap() {{
                    put("order_id", map.get("order_id"));
                }});
            }
            if (res > 0) {
                int number = 0;
                if (!isnull(map.get("goods_list"))) {
                    List<Map> list = (List<Map>) map.get("goods_list");
                    for (Map m : list) {
                        number += dbService.addGoods(new HashMap() {{
                            put("order_id", orderCode);
                            put("goods_name", m.get("goods_name"));
                            put("goods_number", m.get("goods_number"));
                            put("goods_Price", m.get("goods_Price"));
                            put("state", OrderState.WAIT_BUYER_PAY);
                        }});
                    }
                }
                if (number > 0) {
                    return dbService.getReturn_status("10000", new HashMap() {
                        {
                            put("order_id", orderCode);
                        }
                    });
                } else {
                    throw new Exception("商品写入失败");
                }
            } else {
                throw new Exception("订单写入失败");
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            getLogger().warn(e);
            dbService.setLog(e, null, map);
            return dbService.getReturn_status("20000", e.getMessage());
        }
    }
}
