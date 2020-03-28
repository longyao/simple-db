package com.ltc.simple.db;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: longyao
 * Date: 13-1-27
 * Time: 上午10:16
 * To change this template use File | Settings | File Templates.
 */
public interface RowMapper<T> {
    public T mapRow(ResultSet rs) throws SQLException;
}
