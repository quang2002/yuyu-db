package com.yuyu.jdbc;

import java.lang.reflect.Field;
import java.sql.ResultSet;

import org.json.JSONObject;

import com.yuyu.annotations.SQLColumn;

public abstract class JSONEntity extends JSONObject {

    public void loadProps(ResultSet rs) throws Exception {
        for (Field field : getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(SQLColumn.class)) {
                continue;
            }

            field.setAccessible(true);
            Object value = rs.getObject(field.getAnnotation(SQLColumn.class).column());
            value = rs.wasNull() ? null : value;

            field.set(this, value);
            updateProp(field.getName(), value);
        }
    }

    public void updateProps() {
        for (Field field : this.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                put(field.getName(), field.get(this));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void updateProp(String propName, Object value) {
        try {
            put(propName, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
