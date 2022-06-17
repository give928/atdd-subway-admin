package nextstep.subway.domain;

import nextstep.subway.exception.MessageCodeException;

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
        return new Distance(validateIfDistance(value));
    }

    private static int validateIfDistance(int value) {
        if (value < MIN_DISTANCE) {
            throw new MessageCodeException("error.section.add.minimum_distance", new Object[] {MIN_DISTANCE});
        }
        return value;
    }

    public int reduce(int distance) {
        int updateValue = this.value - distance;
        if (updateValue < MIN_DISTANCE) {
            throw new MessageCodeException("error.section.add.overflow_distance");
        }
        this.value = updateValue;
        return this.value;
    }

    public int extend(int distance) {
        this.value += distance;
        return this.value;
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
}
