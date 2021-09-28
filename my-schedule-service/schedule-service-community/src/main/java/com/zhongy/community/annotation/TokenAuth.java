package com.zhongy.community.annotation;

import java.lang.annotation.*;

/**
 * 用于校验token令牌
 */
@Documented //作用域
@Inherited //可继承
@Target(ElementType.METHOD)//标明自定义注解可作用的地方，指方法
@Retention(RetentionPolicy.RUNTIME) //存活阶段，RUNRIME:存在运行期，还有jvm，class文件级别
public @interface TokenAuth {
    String username() default "name";
    //是否需要数据权限，默认为true
    boolean required() default true;
}