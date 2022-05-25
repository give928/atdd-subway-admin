package nextstep.subway.section.domain;

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

@DisplayName("지하철구간 관련 기능")
class SectionTest {
    public static Stream<Arguments> thrownByNullOrSameStationParameter() {
        return Stream.of(Arguments.of(null, new Station(1L, "지하철역")), Arguments.of(new Station(1L, "지하철역"), null),
                         Arguments.of(null, null), Arguments.of(new Station(1L, "지하철역"), new Station(1L, "지하철역")));
    }

    @DisplayName("지하철구간을 생성한다.")
    @Test
    void createSection() {
        // given
        Station upStation = new Station(1L, "지하철역");
        Station downStation = new Station(2L, "새로운지하철역");

        // when
        Section section = new Section(upStation, downStation, 1);

        // then
        assertThat(section.getUpStation()).isEqualTo(upStation);
        assertThat(section.getDownStation()).isEqualTo(downStation);
    }

    @DisplayName("지하철역이 null 이거나 같은 역이면 IllegalArgumentException 이 발생한다.")
    @ParameterizedTest(name = "{displayName} upStation={0}, downStation={1}")
    @MethodSource(value = "thrownByNullOrSameStationParameter")
    void thrownByNullOrSameStation(Station upStation, Station downStation) {
        // given

        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> new Section(upStation, downStation, 1);

        // then
        assertThatThrownBy(throwingCallable).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("길이가 1미만이면 IllegalArgumentException 이 발생한다.")
    @Test
    void thrownByNotPositiveDistance() {
        // given
        Station upStation = new Station(1L, "지하철역");
        Station downStation = new Station(2L, "새로운지하철역");

        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> new Section(upStation , downStation, 0);

        // then
        assertThatThrownBy(throwingCallable).isInstanceOf(IllegalArgumentException.class);
    }
}
