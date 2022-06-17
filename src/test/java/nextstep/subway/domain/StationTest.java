package nextstep.subway.domain;

import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("지하철역 관련 기능")
class StationTest {
    public static Stream<String> thrownByHasNotNameParameter() {
        return Stream.of(null, "", " ");
    }

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createSection() {
        // when
        Station station = new Station(1L, "지하철역");

        // then
        assertThat(station).isEqualTo(new Station(1L, "지하철역"));
    }

    @DisplayName("이름에 값이 없으면 IllegalArgumentException 이 발생한다.")
    @ParameterizedTest(name = "{displayName} name=\"{0}\"")
    @MethodSource(value = "thrownByHasNotNameParameter")
    void thrownByHasNotName(String name) {
        // given

        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> new Station(name);

        // then
        assertThatThrownBy(throwingCallable).isInstanceOf(IllegalArgumentException.class);
    }
}
