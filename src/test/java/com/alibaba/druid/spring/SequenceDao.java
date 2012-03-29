package com.alibaba.druid.spring;

import java.util.HashMap;
import java.util.Map;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

public class SequenceDao extends SqlMapClientDaoSupport implements ISequenceDao {

    public boolean compareAndSet(String name, int value, int expect) {

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("name", name);
        parameters.put("value", value);
        parameters.put("expect", expect);

        int updateCount = getSqlMapClientTemplate().update("Sequence.compareAndSet", parameters);

        return updateCount == 1;
    }

    public int getValue(String name) {
        return (Integer) getSqlMapClientTemplate().queryForObject("Sequence.getValue", name);
    }

    public int getValueForUpdate(String name) {
        return (Integer) getSqlMapClientTemplate().queryForObject("Sequence.getValueForUpdate", name);
    }
}
