package com.shamo.common.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

/**
 * ListValue校验器
 *
 * @author ringo
 * @version 1.0
 * @date 2021/7/6 15:34
 */
public class ListValueConstraintValidator implements ConstraintValidator<ListValue, Integer> {

    // 注解中标注的值的集合
    private Set<Integer> valueSet = new HashSet<>();

    /**
     * 初始化方法
     * @param constraintAnnotation 注解中的信息
     */

    @Override
    public void initialize(ListValue constraintAnnotation) {
        // 获取注解中标注的值的数组
        int[] value = constraintAnnotation.value();
        // 将注解中标注的所有值加入集合
        for (int i : value) {
            valueSet.add(i);
        }
    }

    /**
     * 判断是否校验成功
     * @param value
     * @param context
     * @return
     */
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return valueSet.contains(value);
    }


}
