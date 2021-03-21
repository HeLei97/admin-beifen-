package com.service;


import com.helei.api.db.DbAgent;
import com.helei.api.mapper.CommonDao;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("jobDbService")
@SuppressWarnings({ "rawtypes", "serial", "unchecked" })
public class DbService extends DbAgent {

    public DbService(CommonDao dao) {
        super(dao);
    }

    public List getConfig(){
        return queryTable(CONFIG);
    }
}
