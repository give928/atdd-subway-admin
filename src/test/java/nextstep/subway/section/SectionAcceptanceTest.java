package nextstep.subway.section;

import nextstep.subway.BaseAcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("지하철구간 관련 기능")
class SectionAcceptanceTest extends BaseAcceptanceTest {
    /**
     * Given 지하철 구간을 등록하고
     * When 구간 사이에 동일한 상행 또는 하행역과 새로운 역으로 구간을 등록하면
     * Then 새로운 구간이 등록되고
     * Then 새로운 길이를 뺀 나머지를 새롭게 추가된 역과의 길이로 설정된다.
     */
    @DisplayName("역 사이에 새로운 역을 등록한다.")
    @Test
    void insertStation() {
        // given

        // when

        // then
    }

    /**
     * Given 지하철 구간을 등록하고
     * When 새로운 역을 상행 종점으로 등록하면
     * Then 새로운 구간이 등록되고
     * Then 새로운 역이 상행 종점역이 된다.
     */
    @DisplayName("새로운 역을 상행 종점으로 등록한다.")
    @Test
    void addUpStation() {
        // given

        // when

        // then
    }

    /**
     * Given 지하철 구간을 등록하고
     * When 새로운 역을 하행 종점으로 등록하면
     * Then 새로운 구간이 등록되고
     * Then 새로운 역이 하행 종점역이 된다.
     */
    @DisplayName("새로운 역을 하행 종점으로 등록한다.")
    @Test
    void addDownStation() {
        // given

        // when

        // then
    }

    /**
     * Given 지하철 구간을 등록하고
     * When 사이에 기존 구간 길이보다 크거나 같은 구간을 등록하면
     * Then 구간이 등록되지 않는다.
     */
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없다.")
    @Test
    void cannotInsertInvalidDistanceStation() {
        // given

        // when

        // then
    }

    /**
     * Given 지하철 구간을 등록하고
     * When 노선에 이미 등록되어 있는 상행역과 하행역으로 구간을 추가하면
     * Then 구간이 등록되지 않는다.
     */
    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다.")
    @Test
    void cannotAddDuplicateStation() {
        // given

        // when

        // then
    }

    /**
     * Given 지하철 구간을 등록하고
     * When 노선에 등록되지 않은 상행역과 하행역으로 구간을 추가하면
     * Then 구간이 등록되지 않는다.
     */
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없다.")
    @Test
    void cannotAddBothInsertedStation() {
        // given

        // when

        // then
    }
}
