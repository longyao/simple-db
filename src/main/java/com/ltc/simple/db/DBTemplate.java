package com.ltc.simple.db;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by longtc on 17/4/18.
 */
public interface DBTemplate {

    public int insert(String sql, Object... obs) throws SQLException;

    public int update(String sql, Object... obs) throws SQLException;

    public int delete(String sql, Object... obs) throws SQLException;

    public String querySingleString(String sql, Object...obj) throws SQLException;

    //查询只有一个int值
    public int querySingleInt(String sql, Object...obj) throws SQLException;

    //查询只有一个long值
    public long querySingleLong(String sql, Object...obj) throws SQLException;

    //查询只有一个float值
    public float querySingleFloat(String sql, Object...obj) throws SQLException;

    //查询一个列的集合
    public <T> List<T> querySingleColumnList(String sql, Object...obj) throws SQLException;

    public <T> T queryOne(String sql, Class<T> cls, Object...obj) throws SQLException;

    public List<Map<String, Object>> query(String sql, Object... obj) throws SQLException;

    public <T> List<T> query(String sql, Class<T> cls, Object... obj) throws SQLException;

    public <T> List<T> query(String sql, RowMapper<T> rowMapper, Object... obj) throws SQLException;
}
