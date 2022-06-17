package nextstep.subway.advice;

import nextstep.subway.advice.dto.ErrorDto;
import nextstep.subway.exception.MessageCodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;

@RestControllerAdvice(basePackages = {"nextstep.subway.ui"})
public class ApiExceptionAdvice {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final MessageSource messageSource;

    public ApiExceptionAdvice(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(MessageCodeException.class)
    public ResponseEntity<ErrorDto> handleMessageCodeException(MessageCodeException e) {
        String message = getMessage(e.getCode(), e.getArgs());
        log.error("handleMessageCodeException: {}", message, e);
        return ResponseEntity.badRequest().body(new ErrorDto(message));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorDto> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error("handleDataIntegrityViolationException", e);
        return ResponseEntity.badRequest().body(new ErrorDto(getErrorMessage(e)));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorDto> handleException(Exception e) {
        log.error("handleException", e);
        return ResponseEntity.internalServerError().body(new ErrorDto(getErrorMessage(e)));
    }

    private String getMessage(String code, Object[] args) {
        try {
            String message = messageSource.getMessage(code, args, Locale.getDefault());
            if (StringUtils.hasText(message)) {
                return message;
            }
        } catch (NoSuchMessageException ex) {
            log.info("Not found message code: {}", code);
        }
        return code;
    }

    private String getErrorMessage(Exception e) {
        try {
            String message = messageSource.getMessage(String.format("error.%s", e.getClass().getName()), null,
                                                      Locale.getDefault());
            if (StringUtils.hasText(message)) {
                return message;
            }
        } catch (NoSuchMessageException ex) {
            log.info("Not found message code: {}", e.getClass().getName());
        }
        return messageSource.getMessage("error.java.lang.Exception", null, Locale.getDefault());
    }
}
