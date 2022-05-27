package nextstep.subway.advice.dto;

import org.springframework.http.HttpStatus;

public class ErrorMessageDto  extends ErrorDto {
    private final String message;

    public ErrorMessageDto(HttpStatus httpStatus, String message) {
        super(httpStatus);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
