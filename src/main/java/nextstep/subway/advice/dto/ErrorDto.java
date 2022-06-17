package nextstep.subway.advice.dto;

import java.time.LocalDateTime;

public class ErrorDto {
    private final String message;
    private final LocalDateTime timestamp = LocalDateTime.now();

    public ErrorDto(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
