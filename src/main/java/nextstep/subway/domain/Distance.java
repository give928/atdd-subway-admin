package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    private static final int MIN_DISTANCE = 1;

    @Column(name = "distance", nullable = false)
    private final int value;

    protected Distance() {
        this(0);
    }

    private Distance(int value) {
        this.value = value;
    }

    public static Distance of(int value) {
        validateDistance(value);
        return new Distance(value);
    }

    private static void validateDistance(int value) {
        if (value < MIN_DISTANCE) {
            throw new IllegalArgumentException(
                    String.format(ErrorMessages.REQUIRED_GREATER_THAN_OR_EQUAL_TO_SECTION_DISTANCE, MIN_DISTANCE));
        }
    }

    public int get() {
        return value;
    }

    private static final class ErrorMessages {
        private static final String REQUIRED_GREATER_THAN_OR_EQUAL_TO_SECTION_DISTANCE = "지하철구간의 거리는 %d 이상만 가능합니다.";

        private ErrorMessages() {
        }
    }
}
