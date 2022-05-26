package nextstep.subway.line.domain;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
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
    private static final Station upStation = Station.of(1L, "상행역");
    private static final Station downStation = Station.of(2L, "하행역");

    public static Stream<Arguments> thrownByEmptyNameOrColorParameter() {
        return Stream.of(Arguments.of(null, "bg-red-600"), Arguments.of("", "bg-red-600"),
                         Arguments.of(" ", "bg-red-600"),
                         Arguments.of("신분당선", null), Arguments.of(null, ""), Arguments.of(null, " "));
    }

    public static Stream<Arguments> addSectionParameter() {
        return Stream.of(Arguments.of(Section.of(Station.of(3L, "새로운상행역"), upStation, 10), "새로운상행역"),
                         Arguments.of(Section.of(upStation, Station.of(3L, "중간역"), 9), "중간역"),
                         Arguments.of(Section.of(downStation, Station.of(3L, "새로운하행역"), 10), "새로운하행역"));
    }

    @DisplayName("지하철노선을 생성한다.")
    @Test
    void createLine() {
        // when
        Line line = makeLine();

        // then
        assertThat(line.getStations()).containsExactly(upStation, downStation);
    }

    @DisplayName("이름, 컬러 둘중 값이 없으면 IllegalArgumentException 이 발생한다.")
    @ParameterizedTest(name = "{displayName} name=\"{0}\", color=\"{1}\"")
    @MethodSource(value = "thrownByEmptyNameOrColorParameter")
    void thrownByHasNotNameOrColor(String name, String color) {
        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> Line.of(name, color, upStation, downStation, 1);

        // then
        assertThatThrownBy(throwingCallable).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("지하철노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        Line line = makeLine();

        // when
        Line updatedLine = line.update("분당선", "bg-green-600");

        // then
        assertThat(updatedLine.getName()).isEqualTo("분당선");
        assertThat(updatedLine.getColor()).isEqualTo("bg-green-600");
    }

    @DisplayName("구간을 추가한다.")
    @ParameterizedTest(name = "{1} {displayName}")
    @MethodSource(value = "addSectionParameter")
    void addSection(Section section, String description) {
        // given
        Line line = makeLine();

        // when
        boolean result = line.addSection(section);

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("지하철역을 제거한다.")
    @ParameterizedTest(name = "{1} {displayName}")
    @MethodSource(value = "addSectionParameter")
    void removeSection(Section section, String description) {
        // given
        Line line = makeLine();
        line.addSection(section);

        // when
        boolean result = line.removeSection(section.getUpStation());

        // then
        assertThat(result).isTrue();
    }

    private Line makeLine() {
        return Line.of("신분당선", "bg-red-600", upStation, downStation, 10);
    }
}
