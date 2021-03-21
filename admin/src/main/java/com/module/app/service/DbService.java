package com.module.app.service;

import com.helei.api.db.DbAgent;
import com.helei.api.mapper.CommonDao;
import com.helei.api.util.DateUtils;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("appDbService")
@SuppressWarnings({ "rawtypes", "serial", "unchecked" })
public class DbService extends DbAgent {
    public DbService(CommonDao dao) {
        super(dao);
    }

    public List getUser(Map map){
        String sql="SELECT * FROM "+User+" WHERE 1=1";
        for (Object key:map.keySet()){
            if ("username".equals(key)){
                sql +=" AND username='"+map.get(key)+"' or BM='"+map.get(key)+"'";
            }else {
                sql+=" AND "+key+"='"+map.get(key)+"'";
            }
        }
        sql+=" LIMIT 1";
       return querySql(sql);
    };
    public int addOrder(Map map){
        return insertTable(ORDERS,map);
    };
    public List getOrder(Map map){
        return queryTable(ORDERS,map);
    };
    public int updateOrder(Map keys,Map values){
        return updateTable(ORDERS,keys,values);
    };

    public int addGoods(Map map){
        return insertTable(GOODS,map);
    }
    public void deleteGoods(Map map){
        deleteTable(GOODS,map);
    }
    public int addorderPay(Map map){
        return insertTable(ORDER_PAY,map);
    }
    public int addPayinfo(Map map){
        return insertTable(PAY_INFO,map);
    }
    public List getPayConfig(Map map){
        return queryTable(PAY_CONFIG,map);
    }

    public int addrefund(Map map){
        return insertTable(REFUND,map);
    }
    public List<Map> getOrderPay(Map map){
        return queryTable(ORDER_PAY,map);
    }
}
