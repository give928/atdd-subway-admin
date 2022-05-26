package nextstep.subway.section.domain;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("지하철구간 컬렉션 관련 기능")
class SectionsTest {
    private Sections sections;
    private Section 상행중간구간;
    private Section 중간하행구간;
    private Station 상행역;
    private Station 중간역;
    private Station 하행역;
    private Station 새로운역;

    @BeforeEach
    void setUp() {
        상행역 = Station.of(1L, "상행역");
        중간역 = Station.of(2L, "중간역");
        하행역 = Station.of(3L, "하행역");
        새로운역 = Station.of(4L, "새로운역");
        상행중간구간 = Section.of(상행역, 중간역, 10);
        중간하행구간 = Section.of(중간역, 하행역, 10);
        sections = Sections.of(new ArrayList<>(Arrays.asList(상행중간구간, 중간하행구간)));
    }

    @DisplayName("역 사이에 새로운 역 구간을 추가하면, 새로운 구간이 추가되고, 기존 구간의 거리가 새로운 길이를 뺀 나머지로 변경된다.")
    @ParameterizedTest(name = "{displayName}")
    @CsvSource({"상행역, 새로운역, '기존상행역과 중간역 사이에 새로운역을 추가'",
            "새로운역, 하행역, '기존하행역과 중간역 사이에 새로운역을 추가'"})
    void insertSection(Station 추가상행역, Station 추가하행역, String message) {
        // when
        boolean result = sections.add(Section.of(추가상행역, 추가하행역, 9));

        // then
        assertThat(result).isTrue();

        // then
        assertThat((추가상행역.isSame(상행역) ? 상행중간구간 : 중간하행구간).getDistance()).isEqualTo(1);
    }

    @DisplayName("새로운 역을 상행 종점으로 등록한다.")
    @Test
    void addUpSection() {
        // when
        boolean result = sections.add(Section.of(새로운역, 상행역, 9));

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("새로운 역을 하행 종점으로 등록한다.")
    @Test
    void addDownSection() {
        // when
        boolean result = sections.add(Section.of(하행역, 새로운역, 9));

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 추가할 수 없다.")
    @ParameterizedTest(name = "{displayName}({2})")
    @CsvSource({"상행역, 새로운역, 기존상행역과 중간역 사이에 새로운역을 추가", "새로운역, 하행역, 기존하행역과 중간역 사이에 새로운역을 추가"})
    void thrownByOverflowDistance(Station 추가상행역, Station 추가하행역, String message) {
        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> sections.add(Section.of(추가상행역, 추가하행역, 10));

        // then
        assertThatThrownBy(throwingCallable).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다.")
    @ParameterizedTest(name = "{displayName}({2})")
    @CsvSource({"상행역, 중간역, 1구간 동일한 역으로 추가", "상행역, 하행역, 2구간 동일한 역으로 추가"})
    void thrownByDuplicatedStations(Station 추가상행역, Station 추가하행역, String message) {
        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> sections.add(Section.of(추가상행역, 추가하행역, 5));

        // then
        assertThatThrownBy(throwingCallable).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없다.")
    @Test
    void thrownByIsolatedStations() {
        // given
        Station 새로운상행역 = Station.of(10L, "새로운상행역");
        Station 새로운하행역 = Station.of(11L, "새로운하행역");

        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> sections.add(Section.of(새로운상행역, 새로운하행역, 5));

        // then
        assertThatThrownBy(throwingCallable).isInstanceOf(IllegalArgumentException.class);
    }
}
