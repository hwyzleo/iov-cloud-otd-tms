package net.hwyz.iov.cloud.otd.tms.service.common.exception;

import lombok.Getter;

/**
 * TMS 业务异常（携带 203xxx 错误码）
 *
 * @author hwyz_leo
 */
@Getter
public class TmsBusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private final String code;

    public TmsBusinessException(TmsErrorCode errorCode, String detail) {
        super(detail == null || detail.isBlank() ? errorCode.getMessage() : detail);
        this.code = errorCode.getCode();
    }

    public TmsBusinessException(TmsErrorCode errorCode) {
        this(errorCode, errorCode.getMessage());
    }
}
