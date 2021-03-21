package com.module.common;

import com.helei.api.db.DbAgent;
import com.helei.api.mapper.CommonDao;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("commonDbService")
@SuppressWarnings({ "rawtypes", "serial", "unchecked" })
public class DbService extends DbAgent {

    public DbService(CommonDao dao) {
        super(dao);
    }
    public List<Map> getConfig(String... key){
        String sql="SELECT * FROM "+CONFIG+ " WHERE 1=1 ";
        if (key!=null) {
            sql+=" AND keyname in (";
            int i=1;
            for (String str : key) {
                if (key.length==1 || i==key.length){
                    sql+="'"+str+"'";
                }else {
                    sql+="'"+str+"',";
                }
                i++;
            };
            sql+=")";
        }
        return querySql(sql);
    }
    public List<Map> getUserinfo(Map map){
       return queryTable(User,map);
    }
}
