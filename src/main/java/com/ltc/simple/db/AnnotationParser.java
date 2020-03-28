package com.ltc.simple.db;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/7/4.
 */
public class AnnotationParser {

    public static Map<Field, TableFiled> parseTableField(Class cls) {

        final Map<Field, TableFiled> map = new HashMap<Field, TableFiled>();
        final Field[] fields = cls.getFields();

        final int size = fields == null ? 0 : fields.length;

        for (int i = 0; i < size; i++) {
            final Field fd = fields[i];
            if (fd.isAnnotationPresent(TableFiled.class)) {
                final TableFiled df = fd.getAnnotation(TableFiled.class);
                map.put(fd, df);
            }
        }

        return map;
    }
}
