package net.hwyz.iov.cloud.otd.tms.service.facade.handler;

import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.framework.common.bean.AjaxResult;
import net.hwyz.iov.cloud.otd.tms.service.common.exception.TmsBusinessException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * TMS 业务异常处理（203xxx 错误码透出，OTD-TMS-DSN-CR-001 §1.6）
 *
 * @author hwyz_leo
 */
@Slf4j
@RestControllerAdvice
public class TmsExceptionHandler {

    /**
     * 业务异常转可识别错误码
     *
     * @param e 业务异常
     * @return 错误结果
     */
    @ExceptionHandler(TmsBusinessException.class)
    public AjaxResult handleBusinessException(TmsBusinessException e) {
        log.warn("TMS 业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return AjaxResult.error(e.getCode() + " " + e.getMessage());
    }
}
