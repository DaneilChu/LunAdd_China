package com.infinity.lunadd.di.scope;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by DanielChu on 2017/6/7.
 * <p>
 * 控制Activity的生命周期
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface ActivityScope {


}
