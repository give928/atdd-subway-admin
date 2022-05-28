package nextstep.subway.advice.dto;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class ErrorDto {
    private final HttpStatus httpStatus;
    private final String message;
    private final LocalDateTime timestamp = LocalDateTime.now();

    public ErrorDto(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public int getStatusCode() {
        return httpStatus.value();
    }

    public String getReasonPhrase() {
        return httpStatus.getReasonPhrase();
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
