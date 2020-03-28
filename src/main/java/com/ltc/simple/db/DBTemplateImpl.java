package com.ltc.simple.db;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by longtc on 17/7/29.
 */
public class DBTemplateImpl implements DBTemplate {

    private DataSource mDataSource;

    public DBTemplateImpl(DataSource ds) {
        mDataSource = ds;
    }

    //封装增删改
    private int curd(String sql, Object...obs) throws SQLException {

        int r = -1;
        PreparedStatement pst = null;
        Connection con = null;

        try {
            con = mDataSource.getConnection();
            pst = con.prepareStatement(sql);
            setPreparedStatementArgs(pst, obs);
            r = pst.executeUpdate();
        } finally {
            Utils.close(pst);
            Utils.close(con);
        }

        return r;
    }

    //设置sql占位符的值
    private void setPreparedStatementArgs(PreparedStatement pst, Object[] params) throws SQLException {
        if (!Utils.isEmpty(params)) {
            final int size = params.length;
            for (int index = 0; index < size; index++) {
                pst.setObject(index + 1, params[index]);
            }
        }
    }

    @Override
    public int insert(String sql, Object... obs) throws SQLException {
        return curd(sql, obs);
    }

    @Override
    public int update(String sql, Object... obs) throws SQLException {
        return curd(sql, obs);
    }

    @Override
    public int delete(String sql, Object... obs) throws SQLException {
        return curd(sql, obs);
    }

    @Override
    public String querySingleString(String sql, Object... obj) throws SQLException {

        Connection con = null;
        PreparedStatement pst = null;
        ResultSet set = null;

        try {
            con = mDataSource.getConnection();
            pst = con.prepareStatement(sql);
            setPreparedStatementArgs(pst, obj);
            set = pst.executeQuery();
            if (set.next()) {
                return set.getString(1);
            }
        } finally {
            Utils.close(set);
            Utils.close(pst);
            Utils.close(con);
        }

        return "";
    }

    @Override
    public int querySingleInt(String sql, Object... obj) throws SQLException {
        String res = querySingleString(sql, obj);
        return Utils.isEmpty(res) ? 0 : Integer.parseInt(res);
    }

    @Override
    public long querySingleLong(String sql, Object... obj) throws SQLException {
        String res = querySingleString(sql, obj);
        return Utils.isEmpty(res) ? 0L : Long.parseLong(res);
    }

    @Override
    public float querySingleFloat(String sql, Object... obj) throws SQLException {
        String res = querySingleString(sql, obj);
        return Utils.isEmpty(res) ? 0.0f : Float.parseFloat(res);
    }

    @Override
    public <T> List<T> querySingleColumnList(String sql, Object... obj) throws SQLException {
        return query(sql, new RowMapper<T>() {
            @Override
            public T mapRow(ResultSet rs) throws SQLException {
                return (T) rs.getObject(1);
            }
        }, obj);
    }

    @Override
    public <T> T queryOne(String sql, Class<T> cls, Object... obj) throws SQLException {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet set = null;

        try {
            con = mDataSource.getConnection();
            pst = con.prepareStatement(sql);
            setPreparedStatementArgs(pst, obj);
            set = pst.executeQuery();

            Object entity = null;
            if (set.next()) {
                entity = cls.newInstance();
                setFields(entity, cls, set);
                return (T)entity;
            }
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } finally {
            Utils.close(set);
            Utils.close(pst);
            Utils.close(con);
        }

        return null;
    }

    @Override
    public List<Map<String, Object>> query(String sql, Object... obj) throws SQLException {

        Connection con = null;
        PreparedStatement pst = null;
        ResultSet set = null;

        ArrayList<Map<String, Object>> results = new ArrayList<Map<String, Object>>();

        try {
            con = mDataSource.getConnection();
            pst = con.prepareStatement(sql);
            setPreparedStatementArgs(pst, obj);
            set = pst.executeQuery();

            final String[] names = columnNames(set.getMetaData());
            Map<String, Object> map = null;
            while (set.next()) {
                map = new HashMap<String, Object>();
                for (int i = 0; i < names.length; i++) {
                    map.put(names[i], set.getObject(i+1));
                }
                results.add(map);
            }
        } finally {
            Utils.close(set);
            Utils.close(pst);
            Utils.close(con);
        }

        return results;
    }

    @Override
    public <T> List<T> query(String sql, Class<T> cls, Object... obj) throws SQLException {

        Connection con = null;
        PreparedStatement pst = null;
        ResultSet set = null;

        ArrayList<T> results = new ArrayList<T>();

        try {
            con = mDataSource.getConnection();
            pst = con.prepareStatement(sql);
            setPreparedStatementArgs(pst, obj);
            set = pst.executeQuery();

            Object entity = null;
            while(set.next()) {
                entity = cls.newInstance();
                setFields(entity, cls, set);
                results.add((T)entity);
            }
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } finally {
            Utils.close(set);
            Utils.close(pst);
            Utils.close(con);
        }

        return results;
    }

    @Override
    public <T> List<T> query(String sql, RowMapper<T> rowMapper, Object... obj) throws SQLException {

        Connection con = null;
        PreparedStatement pst = null;
        ResultSet set = null;

        ArrayList<T> results = new ArrayList<T>();

        try {
            con = mDataSource.getConnection();
            pst = con.prepareStatement(sql);
            setPreparedStatementArgs(pst, obj);
            set = pst.executeQuery();
            while (set.next()) {
                if (rowMapper != null) {
                    results.add(rowMapper.mapRow(set));
                }
            }
        } finally {
            Utils.close(set);
            Utils.close(pst);
            Utils.close(con);
        }

        return results;
    }

    private String[] columnNames(ResultSetMetaData metaData) throws SQLException {
        final int count = metaData.getColumnCount();
        final String[] names = new String[count];
        for (int i = 0; i < count; i++) {
            names[i] = metaData.getColumnName(i + 1);
        }
        return names;
    }

    private void setFields(Object obj, Class cls, ResultSet set) throws SQLException, IllegalAccessException{

        final Field[] fs = cls.getDeclaredFields();
        final String[] cs = columnNames(set.getMetaData());

        final Map<Field, TableFiled> ms = AnnotationParser.parseTableField(cls);
        final boolean empty = (ms == null || ms.isEmpty());

        final int fieldSize = fs.length;
        final int columnSize = cs.length;

        for (int i = 0; i < fieldSize; i++) {

            final Field f = fs[i];
            f.setAccessible(true);

            //通过注解匹配
            if (!empty) {
                final TableFiled tf = ms.get(f);
                if (tf != null) {
                    f.set(obj, set.getObject(tf.columnName()));
                }
            } else {//通过类属性匹配
                for (int j = 0; j < columnSize; j++) {
                    if (cs[j].equals(f.getName())) {
                        f.set(obj, set.getObject(cs[j]));
                    }
                }
            }
        }
    }
}
