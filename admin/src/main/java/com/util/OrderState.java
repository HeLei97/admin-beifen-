package com.util;

public class OrderState{
    //创建交易
    public final static String WAIT_BUYER_PAY = "0";
    //交易成功
    public final static String TRADE_SUCCESS = "1";
    //交易关闭
    public final static String TRADE_CLOSED = "2";
    //交易
    public final static String TRADE_REFNUD = "3";
    //交易状态未知
    public final static String TRADE_UNKNOWN = "4";
    //交易失败
    public final static String TRADE_ERROR = "5";
    //部分退款
    public final static String PARTIAL_REFUND = "6";
    //全部退款
    public final static String FULL_REFUND = "7";
}
