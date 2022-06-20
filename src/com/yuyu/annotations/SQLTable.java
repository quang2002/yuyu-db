package com.yuyu.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(java.lang.annotation.ElementType.TYPE)
public @interface SQLTable {

    public String table();
}
