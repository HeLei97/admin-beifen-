package com.module.app.controller;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.request.AlipayTradeCreateRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeCreateResponse;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.helei.api.common.ImplBase;
import com.helei.api.util.ReqToMapUtil;
import com.module.app.service.ApiService;
import com.util.ControllerBase;
import com.util.OrderState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"unchecked", "rawtypes", "serial"})
@RequestMapping(value = "/app")
@RestController
public class APPRestController extends ImplBase {
    @Autowired
    private ApiService apiService;
    @RequestMapping("login")
    public Map getCode(HttpServletRequest req, Model model) {
        Map map = ReqToMapUtil.getParameterMap(req);
       return apiService.login(map);
    }

    @RequestMapping("pay")
    public Map pay(HttpServletRequest req, Model model) {
        Map map = ReqToMapUtil.getParameterMap(req);
        return apiService.pay(map);
    }

    /**
     * 写入订单数据
     * @param jsonObject json数据包含List
     * @return Map
     */
    @RequestMapping(value ="addOrder",method = RequestMethod.POST,consumes = "application/json")
    @ResponseBody
    public Map addOrder(@RequestBody String jsonObject) {
        System.out.print(jsonObject);
        return apiService.addOrder(jsonToMap(jsonObject));
    }
    @RequestMapping("test")
    public Map test(HttpServletRequest req, Model model) {
        Map map = ReqToMapUtil.getParameterMap(req);
        try {
            return apiService.alirefund(map);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    @RequestMapping("wappay")
    public void wappay(HttpServletResponse respore) {
        /**********************/
        // SDK 公共请求类，包含公共请求参数，以及封装了签名与验签，开发者无需关注签名与验签
        //调用RSA签名方式
        AlipayClient client = new DefaultAlipayClient("https://openapi.alipaydev.com/gateway.do", "2016092400589324", "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCAlKaknMnnZAHctse3w8KQoNmBy/1AS0JWudgeIruNh7eDY8/B/uCunQBkOYfi607fUrw1y8KZDMoOofy7OikNP6S8IJ1Xv9RnmgwI0/ux6oZ26RQwRzwzibAFg++QAfwQSLuhc3Zv0SHzsmNG7cHBRLQGwWq3vNq6hJwRO2Q8ZuJmOgPqhf0M6fj/ORa1wL4ki8ElfFQ3OjI1KLxCMnem0pjNEhqYfiWOpDZJ0ms/wgTMboVTYSl2K5NDvvtwWzS8v3vJjdNstfIK+lAleORQDEzLDbaJ6vycT9M1pzD9YNjilXbqLLIwhNdix51o0SPsDBGtJ0JTuoFuFmI5wMT9AgMBAAECggEAeoEmZB+dngACZi8fCU5RyUn28VtQ5jjN86MM7WlaF/X+OgiWoNYdaz/aZ6MIfIKEmvFQsFB6DyfIOzjKefdntV+1rCfGwRvbWvnCDYcqftBbXF+lBHpaOSAtvJN/Y00AwuMJIUHIzbALEQwc2i8cMu8qNmWBL52XJ63ApApZ7dEcyE5gmHfZjgKlBJowhWFLhh/y59rwnM9I3QKGusqB2VYdeP1Hx0fGKSp/ekCYTN7nMkV3dXTDsT93n2AadnkkfJwKxyoVZSQcyelPTAH5uAwRXjocXPZTDjWRc9uQe5/0UX+VheTuyOOvCDGCLI0SPZuyUyiLMG70+qol/nnzAQKBgQDJDfT4reoB1JbmXZyxcSABfoJEmGt2MmAeanytWIKT/ohII9gvqR7FH3chUEfG9f2zS5HyVS6AIemFfBBx7Xm6JiocHl8eoUNdnfpYxFKTxo0yEMuYLcvDb6mv9GbeCg3Dc5sFOC47c043ycSO00Xdyu5+mZQKQk7LNM8EqPfjOQKBgQCjuFOSA7uwrPE4MJZOgcqePu8cjDjQq6ukjQ+OeyXRNVQHUzb2bdEBLkNiCLo5LOgWABJbtr5IvieUwTBIzb9KJxAlCvhF74VVS3yRDlvJfknEQjsOW4VDMUVp1WLPsLT9jK266gFlhz+DLW1qfwvzh32+Lf/sXOIxtQjpneeb5QKBgByfYgqtJ61PCJSPKymPw1CzI/SEBnhzP3/VBR7ghOZwzBIJGFPgrcK+cgVKGo3Wo2GNFp/7RH11VntsaCLv/e5kVIZgRIZlyuNfBC7y9BaZo6xG+9UUX/fYw72K+8IMiF0U7jLWBq7sAWyrytHSfhXPOBjJ4Gt9eR4wzHALZg7xAoGBAIR36U6UrLHYTNkdHxDonawK2efsYTAeJyRean7rj37GOnYu06Ly2hCtgSH6U8tdVXAvoKsKos4y5c3Lvqi5qRRzpPGD/byAgA6ypFiuGIBhKYid2jDUxm/GlxZ57JiFrvwUbqF1I6olk7Ms94Iax+3E7G4LxC5YPUDg7RQxiqwtAoGAPNORpV3lx0I2i1nL9hgnwg4Hu+QxYvStW1uOn8khwPwKv3PjHdSXUhebtHy4Cj3d9udr/3yysSlKUoa/QkSTh3tQqMzvnjpXLTw4Fdyqa0TnVKcwywZLGwMW6ysJF7Z/siCqJpKQMV6XEOeyix9M7/dmWv13RnZMN9WduejRGZk=",
                "json", "GBK", "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAg9oiF0zGsGttMvhmTopy0rFJqci+2cf1GXp9FT99geWYor/07vQ67G32eAOgy2ChNQj8WL28Pv4a9kXk8CqEvO1FhTeHPP03MG1KPxUVoo+2cCkQPaoVcuQhdpBdq7jQN3WIwJPkk1t1IBKDgGavU0MHpCIqf74EoSchCl08Te6XkHB5yoixWL2csbDrA6ajpm+9IhzrXrpte45Ul4rymp+5ajP6XOUdVDVMoexRXgDi5aKWHPqmnP2hyXRcAlRebuH10WLr2BYlcJ8n41oFd5CeiwTmQGNeh6qTY1EgaPqC17RTYmOQvdGe0JjVvdiPqcCqVDa56vAsqnzcy4VUqwIDAQAB","RSA2");
        // 商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = new String("2000000001");
        // 订单名称，必填
        String subject = new String("商品");
        System.out.println(subject);
        // 付款金额，必填
        String total_amount=new String("0.01");
        // 商品描述，可空
        String body = new String("");
        // 超时时间 可空
        String timeout_express="2m";
        // 销售产品码 必填
        String product_code="QUICK_WAP_WAY";
        /**********************/
        // SDK 公共请求类，包含公共请求参数，以及封装了签名与验签，开发者无需关注签名与验签
        //调用RSA签名方式
        //AlipayClient client = new DefaultAlipayClient(AlipayConfig.URL, AlipayConfig.APPID, AlipayConfig.RSA_PRIVATE_KEY, AlipayConfig.FORMAT, AlipayConfig.CHARSET, AlipayConfig.ALIPAY_PUBLIC_KEY,AlipayConfig.SIGNTYPE);
        AlipayTradeWapPayRequest alipay_request=new AlipayTradeWapPayRequest();

        // 封装请求支付信息
        AlipayTradeWapPayModel model=new AlipayTradeWapPayModel();
        model.setOutTradeNo(out_trade_no);
        model.setSubject(subject);
        model.setTotalAmount(total_amount);
        model.setBody(body);
        model.setTimeoutExpress(timeout_express);
        model.setProductCode(product_code);
        alipay_request.setBizModel(model);
        // 设置异步通知地址
        alipay_request.setNotifyUrl("http://www.heleitest.top/index");
        // 设置同步地址
        alipay_request.setReturnUrl("http://www.heleitest.top/index");

        // form表单生产
        String form = "";
        try {
            // 调用SDK生成表单
            form = client.pageExecute(alipay_request).getBody();
            System.out.print(form);
            respore.setContentType("text/html;charset=GBK");
            respore.getWriter().write(form);//直接将完整的表单html输出到页面
            respore.getWriter().flush();
            respore.getWriter().close();
        } catch (AlipayApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
