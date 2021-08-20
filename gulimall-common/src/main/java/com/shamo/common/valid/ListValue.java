package com.shamo.common.valid;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 自定义注解
 *
 * @author ringo
 * @version 1.0
 * @date 2021/7/6 15:18
 */
@Documented
@Constraint(validatedBy = {ListValueConstraintValidator.class}) // 指定校验器
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
public @interface ListValue {
    // 错误信息，指定信息配置文件
    String message() default "{com.shamo.common.valid.ListValue.message}";

    // 分组信息
    Class<?>[] groups() default {};

    // 自定义负载信息
    Class<? extends Payload>[] payload() default {};

    int[] value() default {};


}
