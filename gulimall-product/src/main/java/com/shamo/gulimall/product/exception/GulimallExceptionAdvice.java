package com.shamo.gulimall.product.exception;

import com.shamo.common.exception.BizCodeEnum;
import com.shamo.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ringo
 * @version 1.0
 * @date 2021/7/6 11:20
 */
// 异常处理类返回JSON，basePackages注明可以处理哪些包下的Controller
@RestControllerAdvice(basePackages = {"com.shamo.gulimall.product.controller"})
@Slf4j // 日志
public class GulimallExceptionAdvice {

    /**
     * 校验异常的处理方法
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    public R handleValidException(MethodArgumentNotValidException e){
        // 获取异常中的校验结果
        BindingResult bindingResult = e.getBindingResult();
        // 存储校验异常的集合
        Map<String, String> errorMap = new HashMap<>();

        bindingResult.getFieldErrors().forEach(error -> {
            String field = error.getField();
            String message = error.getDefaultMessage();
            errorMap.put(field, message);
        });
        // 日志消息提示
        log.error("校验出现异常，异常消息{}，异常类型{}", e.getMessage(), e.getClass());
        return R.error(BizCodeEnum.VALID_EXCEPTION.getCode(), BizCodeEnum.VALID_EXCEPTION.getMsg()).put("data", errorMap);
    }

    /**
     * 默认的异常处理
     *
     * @return
     */
    @ExceptionHandler(value = Throwable.class)
    public R handleException(Throwable throwable){
        log.error("位置异常{}，异常类型{}", throwable.getMessage(), throwable.getClass());
        return R.error(BizCodeEnum.UNKNOW_EXCEPTION.getCode(), BizCodeEnum.UNKNOW_EXCEPTION.getMsg());
    }
}
