package nextstep.subway.line.domain;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("지하철노선 관련 기능")
class LineTest {
    public static Stream<Arguments> thrownByEmptyNameOrColorParameter() {
        return Stream.of(Arguments.of(null, "bg-red-600"), Arguments.of("", "bg-red-600"),
                         Arguments.of(" ", "bg-red-600"),
                         Arguments.of("신분당선", null), Arguments.of(null, ""), Arguments.of(null, " "));
    }

    @DisplayName("지하철노선을 생성한다.")
    @Test
    void createLine() {
        // given
        Station upStation = Station.of(1L, "지하철역");
        Station downStation = Station.of(2L, "새로운지하철역");

        // when
        Line line = Line.of("신분당선", "bg-red-600", upStation, downStation, 10);

        // then
        assertThat(line.getStations()).containsExactly(upStation, downStation);
    }

    @DisplayName("이름, 컬러 둘중 값이 없으면 IllegalArgumentException 이 발생한다.")
    @ParameterizedTest(name = "{displayName} name=\"{0}\", color=\"{1}\"")
    @MethodSource(value = "thrownByEmptyNameOrColorParameter")
    void thrownByHasNotNameOrColor(String name, String color) {
        // given
        Station upStation = Station.of(1L, "지하철역");
        Station downStation = Station.of(2L, "새로운지하철역");

        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> Line.of(name, color, upStation, downStation, 1);

        // then
        assertThatThrownBy(throwingCallable).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("지하철노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        Station upStation = Station.of(1L, "지하철역");
        Station downStation = Station.of(2L, "새로운지하철역");
        Line line = Line.of("신분당선", "bg-red-600", upStation, downStation, 10);

        // when
        Line updatedLine = line.update("분당선", "bg-green-600");

        // then
        assertThat(updatedLine.getName()).isEqualTo("분당선");
        assertThat(updatedLine.getColor()).isEqualTo("bg-green-600");
    }
}
