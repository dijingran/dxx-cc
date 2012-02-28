/**
 * 
 */
package org.cc.core.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 校验数字类型的最小�?�，支持Long,Integer,BigDecimal
 * @author liangzhonghua
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Min {
	double value() default 0.00;
	String message() default "不能小于%f";
}
