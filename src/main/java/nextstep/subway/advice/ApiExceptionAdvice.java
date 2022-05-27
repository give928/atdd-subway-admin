package nextstep.subway.advice;

import nextstep.subway.advice.dto.ErrorDto;
import nextstep.subway.advice.dto.ErrorMessageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = {"nextstep.subway.ui"})
public class ApiExceptionAdvice {
    private static final String ERROR_MESSAGE_INTERNAL_SERVER_ERROR = "서버 내부 오류";

    private final Logger log = LoggerFactory.getLogger(getClass());

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorDto handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("handleIllegalArgumentException", e);
        return handleCustomException(e);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorDto handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error("handleDataIntegrityViolationException", e);
        return handleCustomException(e);
    }

    private ErrorDto handleCustomException(RuntimeException e) {
        if (StringUtils.hasText(e.getMessage())) {
            return new ErrorMessageDto(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return handleRuntimeException(e);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public ErrorDto handleRuntimeException(RuntimeException e) {
        log.error("handleRuntimeException", e);
        return handleException(e);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorDto handleException(Exception e) {
        log.error("handleException", e);
        return new ErrorMessageDto(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE_INTERNAL_SERVER_ERROR);
    }
}
