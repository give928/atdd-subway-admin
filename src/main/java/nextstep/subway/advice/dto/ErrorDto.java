package nextstep.subway.advice.dto;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class ErrorDto {
    private final HttpStatus httpStatus;
    private final LocalDateTime date = LocalDateTime.now();

    public ErrorDto(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public int getStatusCode() {
        return httpStatus.value();
    }

    public String getReasonPhrase() {
        return httpStatus.getReasonPhrase();
    }

    public LocalDateTime getDate() {
        return date;
    }
}
