package nextstep.subway.exception;

public class MessageCodeException extends RuntimeException {
    private final String code;
    private final Object[] args;

    public MessageCodeException(String code) {
        this(code, null);
    }

    public MessageCodeException(String code, Object[] args) {
        this.code = code;
        this.args = args;
    }

    public String getCode() {
        return code;
    }

    public Object[] getArgs() {
        return args;
    }
}
