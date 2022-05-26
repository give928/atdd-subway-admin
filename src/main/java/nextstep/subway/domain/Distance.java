package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Distance {
    private static final int MIN_DISTANCE = 1;

    @Column(name = "distance", nullable = false)
    private int value;

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

    public int reduce(int distance) {
        int updateValue = this.value - distance;
        if (updateValue < MIN_DISTANCE) {
            throw new IllegalArgumentException(ErrorMessages.CAN_NOT_ADD_OVERFLOW_DISTANCE);
        }
        this.value = updateValue;
        return updateValue;
    }

    public int get() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Distance distance = (Distance) o;
        return value == distance.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    private static final class ErrorMessages {
        private static final String REQUIRED_GREATER_THAN_OR_EQUAL_TO_SECTION_DISTANCE = "지하철구간의 거리는 %d 이상만 가능합니다.";
        private static final String CAN_NOT_ADD_OVERFLOW_DISTANCE = "기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없습니다.";

        private ErrorMessages() {
        }
    }
}
