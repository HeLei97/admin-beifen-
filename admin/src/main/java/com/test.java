package com;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.request.AlipayTradePayRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.alipay.api.response.AlipayTradePayResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.helei.api.common.ImplBase;
import com.helei.api.util.DateUtils;
import com.sdicons.json.mapper.JSONMapper;
import org.apache.ibatis.jdbc.Null;
import org.springframework.stereotype.Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Controller
public class test{

    public static void main(String[] args) {
        //alirefund();
        String cmd="net start mysql";
        try {

            try {
                runCMD(cmd);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static boolean runCMD(String cmd) throws IOException, InterruptedException {
        final String METHOD_NAME = "runCMD";

        Process p = Runtime.getRuntime().exec("cmd.exe /C " + cmd);
        //Process p = Runtime.getRuntime().exec(cmd);
        BufferedReader br = null;
        try {
            // br = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String readLine = br.readLine();
            StringBuilder builder = new StringBuilder();
            while (readLine != null) {
                readLine = br.readLine();
                builder.append(readLine);
            }
            //logger.debug(METHOD_NAME + "#readLine: " + builder.toString());

            p.waitFor();
            int i = p.exitValue();
            //logger.info(METHOD_NAME + "#exitValue = " + i);
            if (i == 0) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            //logger.error(METHOD_NAME + "#ErrMsg=" + e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            if (br != null) {
                br.close();
            }
        }
    }
    private static Map alipayPay() {
        return new HashMap() {
            {
                try {
                    AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do",
                            "2019090466868977", "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCbxjI5QyWus2T41GHQt1yLoQKWFWs7YwOUPgJ87WDxifdDYmUuhuqgZxh6UbDhTnEA9PDUEkm3tTJniM/BJyvywuEl7IdI7NblnOMEmjY2xKKA5C/cgVP3ceO3E3j+l2SztR+jptCPdCKccPi+OndNAyDV38TNtVoUpKeQlNKUuPie70PwE5iYjKiM+2zVbdQ2sQJUqOVa2onyzw8Ll2f0l8JrQTo2CrIIrM7G0RVZHBWK+vB7KnbwJAaJKpkeng4tvzBRDUjDeocLYGwHoxk9kIzFoPk/I2fwmO09Z5HMDN30u/Js1qGJoGfrfHCdP57n86NLSRS0Dc0bxOqm1HVjAgMBAAECggEATUl0ubx7Aca4Hk0hivCu1gg4xEY0Qe7KY86wZVNRegW9zI0RLph56MO9/VJytBa7MoJUyqOYN7GLJJtYmLjasUPHeY11QdVgeePxLuNkap/9EH4m0PzJsEetd7QNoCN1L0R6QjQN3l78nSngAGH5txgKPpMbjgSggQWArddo86hb3CDV65VJaY+K8lzITOgvfRDcQ+7N+L8NiaMARvRpyKw+wTwNDc4qb/uT1cdrgcJPFvFaWvDz9Ll5F5rjyt1/oBU5zIMnrSKTN5XQA4FncYXFtKCKaZmCWBo5BejbQ22L0IOfOCl+lBT+MRlh0TxqbckP7rp358LWzfnn2sn3YQKBgQDXLDzpX/DB3o9PV+30Cjmp1Y/C5j83zscuSjU3dUt/xD72lpl5ti8kw78SiZCS7ja859rMjtAS8xYATCMruHKyCPrAEsBABO3gVn+zAIxlMcYZKHLcCQNj2QtmwqJUvBoL+HIjCe06Q0tEV1jfJQwxIIES86WHy8YlrYdh6nswpwKBgQC5VL39X9VUIzKQ9wPglCbPS02UC2ifqaOC5HaeadL4noWlRcjdHHlzpjExaTiQM60AAiJNlclYo1qRTKY3N/3dmHaDD9+5KUpLXLkb1v0r4d16niuSIASmU7rdfc922SVO5hc8Wd4rrdnMM+8ELMp/3qlIouLkV/YJnen4jxyQ5QKBgFX8ApdL1HzUgDY3sAoeb+6TPfCna8nUYrn3w45W3AXBmVuW0NvXVr19T4SL3m8orE0O5dtqknGQHFGgG0nkWkkU14cbtyGeFqTYTA1vAauKmVwCUuvk2irzV+AHlTEBPfNco9emptrqe1BjC0uYlWeoUowNOy+p2ZPC5V/WgMXvAoGBALFm0bpQ2raAPq4PFTIK/p9c+LSmuw5iZrcrk/QNXjpIG6vZge1NjWBLjDk8/DzkikxAvq8/mw1yyIXfNhgTS7mrcH4CWug2Avzik/p3L76qpMPz1cc5SNohufS8sjOUpibBDyrg508uShYy/C8l5OzDqY1zBfcHz+bitw0LiI+VAoGBAJWmM6OdMCw4PvWt8q8Ksm8Zy8duKN/HOClaxuTA0vZoBvRzYpFZbc/dGMWSNzbCYElhcfVhE34FFqvMcnRSoxKy7qTiUPQ7JJKvUhjz/hfObFxiWKkveg5wwf6k4yt2rX7FIzxsaXod4CSIKnifp9AT31RTBQlUupeg5pDh7aiU", "json", "GBK",
                            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkcr4XLZAIpdcHAf6Wb8E0K/alUp9z1GShgbq114pJR1fvzUKzpdGwoY3hCep7dffulRQ3bwYmYkVS/hhDUrBDTCSaNsZkaMqhY6bhX9bWN85fd2atn7rmsOQ8dTPVw/DYEJcSOIrT0TeJK6/qxZbD0WmmP91XHui/Ose1DazHy6zmjkXb3ZCxSLgACVJk8TIlHyVlTyK5XcoZ+/rCqrvMnDlabc9JBbUaHURjCInV9GeGdWWpWG5waPssSRLWwy7rS1CTgZF0iYak0zM6xRCGU6VPWFLUe1HWAQV5hA9fRHleLG4fzo9Igw4ywF7wNs7o/9ovGdtD8rsKmox74xDswIDAQAB", "RSA2");
                    AlipayTradePayRequest request = new AlipayTradePayRequest();
                    Map data = new HashMap() {
                        {
                            put("out_trade_no", "ZFB20201224000004");
                            put("scene", "bar_code");
                            put("auth_code", "287082912930376379");
                            put("product_code", "FACE_TO_FACE_PAYMENT");
                            put("subject", "商品");
                            put("total_amount","12");
                        }
                    };
                    request.setBizContent(toJson(data));
                     AlipayTradePayResponse response = alipayClient.execute(request);
                    System.out.print("支付宝扫码返回信息：" + response.getBody());
                    System.out.print(response);
                    Map respData = new HashMap<Object, Object>();
                    respData.put("transaction_id", response.getTradeNo());
                    respData.put("result_code", response.getCode());
                    respData.put("fee_type", response.getTransCurrency());
                    //respData.put("out_trade_no", param.get("out_trade_no"));
                    respData.put("cash_fee", response.getReceiptAmount());
                    respData.put("time_end", response.getGmtPayment());
                    System.out.println(response.getGmtPayment());
                    //respData.put("appid", param.get("appid"));
                    //respData.put("total_fee", param.get("total_fee"));
                    respData.put("err_code_des", response.getSubMsg());
                    respData.put("trade_state_desc", response.getMsg());
                    respData.put("add_date", DateUtils.currentDate());
                    //respData.put("user_code", param.get("user_code"));
                    //respData.put("pay_type", param.get("pay_type"));
                    //dbService.addSminfo(respData);
                    if ("10000".equals(response.getCode())) {
                        put("SuccessMsg", "订单支付成功");
                        put("transaction_id", response.getTradeNo());
                        put("out_trade_no", response.getOutTradeNo());
                        put("return_code", "1");
                    } else if ("10003".equals(response.getCode())) {
                        put("SuccessMsg", "订单支付状态未知");
                        put("err_code_des", response.getMsg());
                        put("return_code", "2");
                    } else {
                        put("SuccessMsg", "订单支付失败");
                        put("err_code_des", response.getSubMsg());
                        put("return_code", "0");
                    }

                } catch (Exception e) {
                    put("SuccessMsg", "订单支付失败");
                    put("err_code_des", e.getMessage());
                    put("return_code", "0");
                }
            }
        };
    }
    public static String toJson(Object data) {
        try {
            return JSONMapper.toJSON(data).render(true);
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    private static Map alireverse() {
        return new HashMap() {
            {
                try {
                    AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do",
                            "2019090466868977", "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCbxjI5QyWus2T41GHQt1yLoQKWFWs7YwOUPgJ87WDxifdDYmUuhuqgZxh6UbDhTnEA9PDUEkm3tTJniM/BJyvywuEl7IdI7NblnOMEmjY2xKKA5C/cgVP3ceO3E3j+l2SztR+jptCPdCKccPi+OndNAyDV38TNtVoUpKeQlNKUuPie70PwE5iYjKiM+2zVbdQ2sQJUqOVa2onyzw8Ll2f0l8JrQTo2CrIIrM7G0RVZHBWK+vB7KnbwJAaJKpkeng4tvzBRDUjDeocLYGwHoxk9kIzFoPk/I2fwmO09Z5HMDN30u/Js1qGJoGfrfHCdP57n86NLSRS0Dc0bxOqm1HVjAgMBAAECggEATUl0ubx7Aca4Hk0hivCu1gg4xEY0Qe7KY86wZVNRegW9zI0RLph56MO9/VJytBa7MoJUyqOYN7GLJJtYmLjasUPHeY11QdVgeePxLuNkap/9EH4m0PzJsEetd7QNoCN1L0R6QjQN3l78nSngAGH5txgKPpMbjgSggQWArddo86hb3CDV65VJaY+K8lzITOgvfRDcQ+7N+L8NiaMARvRpyKw+wTwNDc4qb/uT1cdrgcJPFvFaWvDz9Ll5F5rjyt1/oBU5zIMnrSKTN5XQA4FncYXFtKCKaZmCWBo5BejbQ22L0IOfOCl+lBT+MRlh0TxqbckP7rp358LWzfnn2sn3YQKBgQDXLDzpX/DB3o9PV+30Cjmp1Y/C5j83zscuSjU3dUt/xD72lpl5ti8kw78SiZCS7ja859rMjtAS8xYATCMruHKyCPrAEsBABO3gVn+zAIxlMcYZKHLcCQNj2QtmwqJUvBoL+HIjCe06Q0tEV1jfJQwxIIES86WHy8YlrYdh6nswpwKBgQC5VL39X9VUIzKQ9wPglCbPS02UC2ifqaOC5HaeadL4noWlRcjdHHlzpjExaTiQM60AAiJNlclYo1qRTKY3N/3dmHaDD9+5KUpLXLkb1v0r4d16niuSIASmU7rdfc922SVO5hc8Wd4rrdnMM+8ELMp/3qlIouLkV/YJnen4jxyQ5QKBgFX8ApdL1HzUgDY3sAoeb+6TPfCna8nUYrn3w45W3AXBmVuW0NvXVr19T4SL3m8orE0O5dtqknGQHFGgG0nkWkkU14cbtyGeFqTYTA1vAauKmVwCUuvk2irzV+AHlTEBPfNco9emptrqe1BjC0uYlWeoUowNOy+p2ZPC5V/WgMXvAoGBALFm0bpQ2raAPq4PFTIK/p9c+LSmuw5iZrcrk/QNXjpIG6vZge1NjWBLjDk8/DzkikxAvq8/mw1yyIXfNhgTS7mrcH4CWug2Avzik/p3L76qpMPz1cc5SNohufS8sjOUpibBDyrg508uShYy/C8l5OzDqY1zBfcHz+bitw0LiI+VAoGBAJWmM6OdMCw4PvWt8q8Ksm8Zy8duKN/HOClaxuTA0vZoBvRzYpFZbc/dGMWSNzbCYElhcfVhE34FFqvMcnRSoxKy7qTiUPQ7JJKvUhjz/hfObFxiWKkveg5wwf6k4yt2rX7FIzxsaXod4CSIKnifp9AT31RTBQlUupeg5pDh7aiU", "json", "GBK",
                            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkcr4XLZAIpdcHAf6Wb8E0K/alUp9z1GShgbq114pJR1fvzUKzpdGwoY3hCep7dffulRQ3bwYmYkVS/hhDUrBDTCSaNsZkaMqhY6bhX9bWN85fd2atn7rmsOQ8dTPVw/DYEJcSOIrT0TeJK6/qxZbD0WmmP91XHui/Ose1DazHy6zmjkXb3ZCxSLgACVJk8TIlHyVlTyK5XcoZ+/rCqrvMnDlabc9JBbUaHURjCInV9GeGdWWpWG5waPssSRLWwy7rS1CTgZF0iYak0zM6xRCGU6VPWFLUe1HWAQV5hA9fRHleLG4fzo9Igw4ywF7wNs7o/9ovGdtD8rsKmox74xDswIDAQAB", "RSA2");
                    AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
                    Map data = new HashMap() {
                        {
                            put("out_trade_no", "ZFB2020122819464654595405043028726");
                        }
                    };
                    request.setBizContent(toJson(data));
                    AlipayTradeCloseResponse response = alipayClient.execute(request);
                    System.out.print("支付宝撤销订单返回信息：" + response.toString());
                    if (response.isSuccess()) {
                        data.put("msg", "已撤销订单");
                        //dbService.updateSminfoTK(data);
                        put("SuccessMsg", "撤销成功");
                        put("return_code", "1");
                    } else {
                        put("SuccessMsg", "撤销失败");
                        put("err_code_des", response.getSubMsg());
                        put("return_code", "0");
                    }
                } catch (Exception e) {
                    put("SuccessMsg", "订单撤销失败");
                    put("err_code_des", "接口异常" + e.getMessage());
                    put("return_code", "0");
                }
            }
        };
    }
    private static Map alirefund() {
        return new HashMap() {
            {
                try {
                    AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do",
                            "2019090466868977", "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCbxjI5QyWus2T41GHQt1yLoQKWFWs7YwOUPgJ87WDxifdDYmUuhuqgZxh6UbDhTnEA9PDUEkm3tTJniM/BJyvywuEl7IdI7NblnOMEmjY2xKKA5C/cgVP3ceO3E3j+l2SztR+jptCPdCKccPi+OndNAyDV38TNtVoUpKeQlNKUuPie70PwE5iYjKiM+2zVbdQ2sQJUqOVa2onyzw8Ll2f0l8JrQTo2CrIIrM7G0RVZHBWK+vB7KnbwJAaJKpkeng4tvzBRDUjDeocLYGwHoxk9kIzFoPk/I2fwmO09Z5HMDN30u/Js1qGJoGfrfHCdP57n86NLSRS0Dc0bxOqm1HVjAgMBAAECggEATUl0ubx7Aca4Hk0hivCu1gg4xEY0Qe7KY86wZVNRegW9zI0RLph56MO9/VJytBa7MoJUyqOYN7GLJJtYmLjasUPHeY11QdVgeePxLuNkap/9EH4m0PzJsEetd7QNoCN1L0R6QjQN3l78nSngAGH5txgKPpMbjgSggQWArddo86hb3CDV65VJaY+K8lzITOgvfRDcQ+7N+L8NiaMARvRpyKw+wTwNDc4qb/uT1cdrgcJPFvFaWvDz9Ll5F5rjyt1/oBU5zIMnrSKTN5XQA4FncYXFtKCKaZmCWBo5BejbQ22L0IOfOCl+lBT+MRlh0TxqbckP7rp358LWzfnn2sn3YQKBgQDXLDzpX/DB3o9PV+30Cjmp1Y/C5j83zscuSjU3dUt/xD72lpl5ti8kw78SiZCS7ja859rMjtAS8xYATCMruHKyCPrAEsBABO3gVn+zAIxlMcYZKHLcCQNj2QtmwqJUvBoL+HIjCe06Q0tEV1jfJQwxIIES86WHy8YlrYdh6nswpwKBgQC5VL39X9VUIzKQ9wPglCbPS02UC2ifqaOC5HaeadL4noWlRcjdHHlzpjExaTiQM60AAiJNlclYo1qRTKY3N/3dmHaDD9+5KUpLXLkb1v0r4d16niuSIASmU7rdfc922SVO5hc8Wd4rrdnMM+8ELMp/3qlIouLkV/YJnen4jxyQ5QKBgFX8ApdL1HzUgDY3sAoeb+6TPfCna8nUYrn3w45W3AXBmVuW0NvXVr19T4SL3m8orE0O5dtqknGQHFGgG0nkWkkU14cbtyGeFqTYTA1vAauKmVwCUuvk2irzV+AHlTEBPfNco9emptrqe1BjC0uYlWeoUowNOy+p2ZPC5V/WgMXvAoGBALFm0bpQ2raAPq4PFTIK/p9c+LSmuw5iZrcrk/QNXjpIG6vZge1NjWBLjDk8/DzkikxAvq8/mw1yyIXfNhgTS7mrcH4CWug2Avzik/p3L76qpMPz1cc5SNohufS8sjOUpibBDyrg508uShYy/C8l5OzDqY1zBfcHz+bitw0LiI+VAoGBAJWmM6OdMCw4PvWt8q8Ksm8Zy8duKN/HOClaxuTA0vZoBvRzYpFZbc/dGMWSNzbCYElhcfVhE34FFqvMcnRSoxKy7qTiUPQ7JJKvUhjz/hfObFxiWKkveg5wwf6k4yt2rX7FIzxsaXod4CSIKnifp9AT31RTBQlUupeg5pDh7aiU", "json", "GBK",
                            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkcr4XLZAIpdcHAf6Wb8E0K/alUp9z1GShgbq114pJR1fvzUKzpdGwoY3hCep7dffulRQ3bwYmYkVS/hhDUrBDTCSaNsZkaMqhY6bhX9bWN85fd2atn7rmsOQ8dTPVw/DYEJcSOIrT0TeJK6/qxZbD0WmmP91XHui/Ose1DazHy6zmjkXb3ZCxSLgACVJk8TIlHyVlTyK5XcoZ+/rCqrvMnDlabc9JBbUaHURjCInV9GeGdWWpWG5waPssSRLWwy7rS1CTgZF0iYak0zM6xRCGU6VPWFLUe1HWAQV5hA9fRHleLG4fzo9Igw4ywF7wNs7o/9ovGdtD8rsKmox74xDswIDAQAB", "RSA2");
                    AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
                    Map data = new HashMap() {
                        {
                            put("out_trade_no", "ZFB2021010119130923296011008490922");
                            //put("out_request_no", param.get("out_refund_no"));
                            put("out_request_no","");
                            put("refund_amount",
                                    "12");
                        }
                    };
                    request.setBizContent(toJson(data));
                    AlipayTradeRefundResponse response = alipayClient.execute(request);
                    System.out.print("支付宝退款返回信息：" + response.getBody());
                    if (response.isSuccess()) {
                        Map respData = new HashMap<Object, Object>();
                        respData.put("msg", "已退款");
                        respData.put("trade_type", "");
                        //respData.put("out_trade_no", param.get("out_trade_no"));
                        //respData.put("refund_id", param.get("out_trade_no"));
                        //dbService.updateSminfoTK(respData);

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
                }
            }
        };
    }
}
