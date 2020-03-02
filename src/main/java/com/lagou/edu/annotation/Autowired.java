package com.lagou.edu.annotation;

import java.lang.annotation.*;

@Target({ElementType.CONSTRUCTOR,ElementType.FIELD,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Autowired {

    boolean value() default true;
}
