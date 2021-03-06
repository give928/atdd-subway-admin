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
        return Stream.of(Arguments.of(null, Station.of(1L, "지하철역")), Arguments.of(Station.of(1L, "지하철역"), null),
                         Arguments.of(null, null), Arguments.of(Station.of(1L, "지하철역"), Station.of(1L, "지하철역")));
    }

    @DisplayName("지하철구간을 생성한다.")
    @Test
    void createSection() {
        // given
        Station upStation = Station.of(1L, "상행역");
        Station downStation = Station.of(2L, "하행역");

        // when
        Section section = Section.of(upStation, downStation, 1);

        // then
        assertThat(section.getUpStation()).isEqualTo(upStation);
        assertThat(section.getDownStation()).isEqualTo(downStation);
    }

    @DisplayName("지하철역이 null 이거나 같은 역이면 IllegalArgumentException 이 발생한다.")
    @ParameterizedTest(name = "{displayName} upStation={0}, downStation={1}")
    @MethodSource(value = "thrownByNullOrSameStationParameter")
    void thrownByNullOrSameStation(Station upStation, Station downStation) {
        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> Section.of(upStation, downStation, 1);

        // then
        assertThatThrownBy(throwingCallable).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("길이가 1미만이면 IllegalArgumentException 이 발생한다.")
    @Test
    void thrownByNotPositiveDistance() {
        // given
        Station upStation = Station.of(1L, "상행역");
        Station downStation = Station.of(2L, "하행역");

        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> Section.of(upStation , downStation, 0);

        // then
        assertThatThrownBy(throwingCallable).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상행 종점 구간이 추가 가능한지 확인한다.")
    @Test
    void isUpLinkable() {
        // given
        Station upStation = Station.of(1L, "상행역");
        Station downStation = Station.of(2L, "하행역");
        Section section = Section.of(upStation, downStation, 5);

        Station linkStation = Station.of(3L, "새로운상행역");

        // when
        boolean actual = section.isLinkable(Section.of(linkStation, upStation, 5));

        // then
        assertThat(actual).isTrue();
    }

    @DisplayName("하행 종점 구간이 추가 가능한지 확인한다.")
    @Test
    void isDownLinkable() {
        // given
        Station upStation = Station.of(1L, "상행역");
        Station downStation = Station.of(2L, "하행역");
        Section section = Section.of(upStation, downStation, 5);

        Station linkStation = Station.of(3L, "새로운하행역");

        // when
        boolean actual = section.isLinkable(Section.of(downStation, linkStation, 5));

        // then
        assertThat(actual).isTrue();
    }

    @DisplayName("역 사이에 새로운 구간이 추가 가능한지 확인한다.")
    @Test
    void isInnerLinkable() {
        // given
        Station upStation = Station.of(1L, "상행역");
        Station downStation = Station.of(2L, "하행역");
        Section section = Section.of(upStation, downStation, 5);

        Station linkStation = Station.of(3L, "중간역");

        // when
        boolean actual = section.isLinkable(Section.of(upStation, linkStation, 4));

        // then
        assertThat(actual).isTrue();
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다.")
    @Test
    void thrownByDuplicatedStations() {
        // given
        Station upStation = Station.of(1L, "상행역");
        Station downStation = Station.of(2L, "하행역");
        Section section = Section.of(upStation, downStation, 5);

        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> section.isLinkable(
                Section.of(upStation, downStation, 4));

        // then
        assertThatThrownBy(throwingCallable).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("역 사이에 새로운 역 구간을 추가하면 기존 구간의 거리가 새로운 길이를 뺀 나머지로 변경된다.")
    @Test
    void reduceDistanceIfInnerSection() {
        // given
        Station upStation = Station.of(1L, "상행역");
        Station downStation = Station.of(2L, "하행역");
        Section linkSection = Section.of(upStation, downStation, 5);
        Section section = Section.of(upStation, Station.of(3L, "중간역"), 4);

        // when
        linkSection.reduceDistanceIfInnerSection(section);

        // then
        assertThat(linkSection.getDistance()).isEqualTo(1);
        assertThat(section.getDistance()).isEqualTo(4);
    }

    @DisplayName("기존 구간의 거리보다 새로운 길이가 길거나 같으면 예외가 발생한다.")
    @Test
    void thrownByOverflowDistance() {
        // given
        Station upStation = Station.of(1L, "상행역");
        Station downStation = Station.of(2L, "하행역");
        Section linkSection = Section.of(upStation, downStation, 5);
        Section section = Section.of(upStation, Station.of(3L, "중간역"), 5);

        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> linkSection.reduceDistanceIfInnerSection(section);

        // then
        assertThatThrownBy(throwingCallable).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("연결된 두 구간을 합치면 상행역과 하행역을 재배치하고 구간의 길이를 합한다.")
    @Test
    void mergeIfOuterSection() {
        // given
        Station upStation = Station.of(1L, "상행역");
        Station middleStation = Station.of(2L, "중간역");
        Station downStation = Station.of(3L, "하행역");
        Section section = Section.of(upStation, middleStation, 5);
        Section removeSection = Section.of(middleStation, downStation, 4);

        // when
        section.mergeIfOuterSection(removeSection);

        // then
        assertThat(section.getUpStation()).isEqualTo(upStation);
        assertThat(section.getDownStation()).isEqualTo(downStation);
        assertThat(section.getDistance()).isEqualTo(9);
    }

    @DisplayName("연결되지 않은 두 구간을 합치면 변경되지 않는다.")
    @Test
    void mergeIfNotLinkedSections() {
        // given
        Station upStation = Station.of(1L, "상행역");
        Station downStation = Station.of(2L, "하행역");
        Station otherUpStation = Station.of(3L, "다른상행역");
        Station otherDownStation = Station.of(4L, "다른하행역");
        Section section = Section.of(upStation, downStation, 5);
        Section removeSection = Section.of(otherUpStation, otherDownStation, 4);

        // when
        section.mergeIfOuterSection(removeSection);

        // then
        assertThat(section.getUpStation()).isEqualTo(upStation);
        assertThat(section.getDownStation()).isEqualTo(downStation);
        assertThat(section.getDistance()).isEqualTo(5);
        assertThat(removeSection.getUpStation()).isEqualTo(otherUpStation);
        assertThat(removeSection.getDownStation()).isEqualTo(otherDownStation);
        assertThat(removeSection.getDistance()).isEqualTo(4);
    }
}
