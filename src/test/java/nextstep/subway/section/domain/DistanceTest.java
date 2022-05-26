package nextstep.subway.section.domain;

import nextstep.subway.domain.Distance;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("지하철구간 거리 관련 기능")
class DistanceTest {
    @DisplayName("거리 원시 값을 포장한다.")
    @Test
    void createDistance() {
        // when
        Distance distance = Distance.of(1);

        // then
        assertThat(distance).isEqualTo(Distance.of(1));
    }

    @DisplayName("1보다 작은 값은 생성할 수 없다.")
    @Test
    void thrownByNotPositiveValue() {
        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> Distance.of(0);

        // then
        assertThatThrownBy(throwingCallable).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("거리를 줄인다.")
    @Test
    void reduce() {
        // given
        Distance distance = Distance.of(10);

        // when
        int result = distance.reduce(9);

        // then
        assertThat(result).isEqualTo(1);
    }

    @DisplayName("줄인 거리가 0보다 작거나 같으면 예외가 발생한다.")
    @Test
    void thrownByNotPositiveDistance() {
        // given
        Distance distance = Distance.of(10);

        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> distance.reduce(10);

        // then
        assertThatThrownBy(throwingCallable).isInstanceOf(IllegalArgumentException.class);
    }
}
