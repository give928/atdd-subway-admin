package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.BaseAcceptanceTest;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.SectionRepository;
import nextstep.subway.line.LineRestAssured;
import nextstep.subway.station.StationRestAssured;
import nextstep.subway.util.RestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철구간 관련 기능")
class SectionAcceptanceTest extends BaseAcceptanceTest {
    private static final int DEFAULT_DISTANCE = 10;

    @Autowired
    private SectionRepository sectionRepository;

    private String SECTION_URL;

    private long 노선_id;
    private long 상행역_id;
    private long 하행역_id;

    @BeforeEach
    protected void setUp() {
        super.setUp();

        상행역_id = RestUtils.id_추출(StationRestAssured.지하철역_생성_요청("상행역"));
        하행역_id = RestUtils.id_추출(StationRestAssured.지하철역_생성_요청("하행역"));
        노선_id = RestUtils.id_추출(LineRestAssured.지하철노선_생성_요청("신분당선", "bg-red-600", 상행역_id, 하행역_id, DEFAULT_DISTANCE));

        SECTION_URL = String.format("/lines/%d/sections", 노선_id);
    }

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
        long 중간역_id = RestUtils.id_추출(StationRestAssured.지하철역_생성_요청("중간역"));

        // when
        ExtractableResponse<Response> response = 지하철구간_등록_요청(상행역_id, 중간역_id, 9);

        // then
        지하철구간_등록됨(response);

        // then
        지하철구간_길이_검증(1, 9);
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
        long 새로운상행역_id = RestUtils.id_추출(StationRestAssured.지하철역_생성_요청("새로운상행역"));

        // when
        ExtractableResponse<Response> response = 지하철구간_등록_요청(새로운상행역_id, 상행역_id, DEFAULT_DISTANCE);

        // then
        지하철구간_등록됨(response);

        // then
        상행_종점역_검증(새로운상행역_id, 상행역_id, 하행역_id);
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
        long 새로운하행역_id = RestUtils.id_추출(StationRestAssured.지하철역_생성_요청("새로운하행역"));

        // when
        ExtractableResponse<Response> response = 지하철구간_등록_요청(하행역_id, 새로운하행역_id, DEFAULT_DISTANCE);

        // then
        지하철구간_등록됨(response);

        // then
        하행_종점역_검증(상행역_id, 하행역_id, 새로운하행역_id);
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
        long 중간역_id = RestUtils.id_추출(StationRestAssured.지하철역_생성_요청("중간역"));

        // when
        ExtractableResponse<Response> response = 지하철구간_등록_요청(상행역_id, 중간역_id, DEFAULT_DISTANCE);

        // then
        지하철구간_등록_안됨(response);
    }

    /**
     * Given 지하철 구간을 등록하고
     * When 노선에 이미 등록되어 있는 상행역과 하행역으로 구간을 추가하면
     * Then 구간이 등록되지 않는다.
     */
    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다.")
    @Test
    void cannotAddBothInsertedStation() {
        // given
        long 새로운상행역_id = RestUtils.id_추출(StationRestAssured.지하철역_생성_요청("새로운상행역"));
        지하철구간_등록_요청(새로운상행역_id, 상행역_id, DEFAULT_DISTANCE);

        // when
        ExtractableResponse<Response> response = 지하철구간_등록_요청(새로운상행역_id, 하행역_id, 20);

        // then
        지하철구간_등록_안됨(response);
    }

    /**
     * Given 지하철 구간을 등록하고
     * When 노선에 등록되지 않은 상행역과 하행역으로 구간을 추가하면
     * Then 구간이 등록되지 않는다.
     */
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없다.")
    @Test
    void cannotAddNotContainsStation() {
        // given
        long 새로운상행역_id = RestUtils.id_추출(StationRestAssured.지하철역_생성_요청("새로운상행역"));
        long 새로운하행역_id = RestUtils.id_추출(StationRestAssured.지하철역_생성_요청("새로운하행역"));

        // when
        ExtractableResponse<Response> response = 지하철구간_등록_요청(새로운상행역_id, 새로운하행역_id, DEFAULT_DISTANCE);

        // then
        지하철구간_등록_안됨(response);
    }

    /**
     * Given 지하철 구간을 3개 등록하고
     * When 종점을 제거하면
     * Then 구간이 제거되고
     * Then 이전 구간의 역이 종점이 된다.
     */
    @DisplayName("종점역을 제거한다.")
    @Test
    void deleteLastStation() {
        // given
        long 새로운상행역_id = RestUtils.id_추출(StationRestAssured.지하철역_생성_요청("새로운상행역"));
        long 새로운하행역_id = RestUtils.id_추출(StationRestAssured.지하철역_생성_요청("새로운하행역"));
        지하철구간_등록_요청(새로운상행역_id, 상행역_id, 20);
        지하철구간_등록_요청(새로운하행역_id, 하행역_id, 30);

        // when
        ExtractableResponse<Response> response = 지하철구간_삭제_요청(새로운상행역_id);

        // then
        지하철구간_삭제됨(response);

        // then
        지하철구간_종점역_삭제_검증(새로운상행역_id, 상행역_id);
    }

    /**
     * Given 지하철 구간을 3개 등록하고
     * When 중간역을 제거하면
     * Then 구간이 제거되고
     * Then 제거된 구간의 남은 역이 이전 구간의 역으로 재배치 되고 거리는 두 구간의 합이 된다.
     */
    @DisplayName("중간역을 제거한다.")
    @Test
    void deleteMiddleStation() {
        // given
        long 새로운상행역_id = RestUtils.id_추출(StationRestAssured.지하철역_생성_요청("새로운상행역"));
        long 새로운하행역_id = RestUtils.id_추출(StationRestAssured.지하철역_생성_요청("새로운하행역"));
        지하철구간_등록_요청(새로운상행역_id, 상행역_id, 20);
        지하철구간_등록_요청(새로운하행역_id, 하행역_id, 30);

        // when
        ExtractableResponse<Response> response = 지하철구간_삭제_요청(상행역_id);

        // then
        지하철구간_삭제됨(response);

        // then
        지하철구간_중간역_삭제_검증(상행역_id, 새로운상행역_id, 하행역_id, 30);
    }

    /**
     * When 등록되어 있지 않은 역을 제거하면
     * Then 제거되지 않는다.
     */
    @DisplayName("등록되지 않은 역은 제거되지 않는다.")
    @Test
    void deleteNotExistsStation() {
        // when
        ExtractableResponse<Response> response = 지하철구간_삭제_요청(0);

        // then
        지하철구간_삭제_안됨(response);
    }

    /**
     * Given 지하철 구간을 1개 등록하고
     * When 역을 제거하면
     * Then 제거되지 않는다.
     */
    @DisplayName("노선에 1개의 구간만 있으면 제거되지 않는다.")
    @Test
    void deleteOnlyOneSectionStation() {
        // when
        ExtractableResponse<Response> response = 지하철구간_삭제_요청(상행역_id);

        // then
        지하철구간_삭제_안됨(response);
    }

    private ExtractableResponse<Response> 지하철구간_등록_요청(long upStationId, long downStationId, int distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return RestUtils.post(SECTION_URL, params);
    }

    private void 지하철구간_등록됨(ExtractableResponse<Response> response) {
        assertResponseStatus(response, HttpStatus.OK);
    }

    private void 지하철구간_등록_안됨(ExtractableResponse<Response> response) {
        assertResponseStatus(response, HttpStatus.BAD_REQUEST);
    }

    private void 지하철구간_길이_검증(int distance1, int distance2) {
        List<Section> sections = sectionRepository.findAll();
        assertThat(sections.get(0).getDistance()).isEqualTo(distance1);
        assertThat(sections.get(1).getDistance()).isEqualTo(distance2);
    }

    private void 상행_종점역_검증(long upStationId, long middleStationId, long downStationId) {
        List<Section> sections = sectionRepository.findAll();
        assertThat(sections.get(0).getDownStation().getId()).isEqualTo(downStationId);
        assertThat(sections.get(0).getUpStation().getId()).isEqualTo(middleStationId);
        assertThat(sections.get(1).getDownStation().getId()).isEqualTo(middleStationId);
        assertThat(sections.get(1).getUpStation().getId()).isEqualTo(upStationId);
    }

    private void 하행_종점역_검증(long upStationId, long middleStationId, long downStationId) {
        List<Section> sections = sectionRepository.findAll();
        assertThat(sections.get(0).getDownStation().getId()).isEqualTo(middleStationId);
        assertThat(sections.get(0).getUpStation().getId()).isEqualTo(upStationId);
        assertThat(sections.get(1).getDownStation().getId()).isEqualTo(downStationId);
        assertThat(sections.get(1).getUpStation().getId()).isEqualTo(middleStationId);
    }

    private ExtractableResponse<Response> 지하철구간_삭제_요청(long stationId) {
        return RestUtils.delete(SECTION_URL + "?stationId=" + stationId);
    }

    private void 지하철구간_삭제됨(ExtractableResponse<Response> response) {
        assertResponseStatus(response, HttpStatus.OK);
    }

    private void 지하철구간_삭제_안됨(ExtractableResponse<Response> response) {
        assertResponseStatus(response, HttpStatus.BAD_REQUEST);
    }

    private void 지하철구간_종점역_삭제_검증(long deleteStationId, long lastUpStationId) {
        List<Section> sections = sectionRepository.findAll();
        assertThat(sections).hasSize(2)
                .allMatch(section -> section.getUpStation().getId() != deleteStationId && section.getDownStation().getId() != deleteStationId)
                .allMatch(section -> section.getDownStation().getId() != lastUpStationId)
                .anyMatch(section -> section.getUpStation().getId() == lastUpStationId);
    }

    private void 지하철구간_중간역_삭제_검증(long deleteStationId, long upStationId, long downStationId, int distance) {
        List<Section> sections = sectionRepository.findAll();
        assertThat(sections).hasSize(2)
                .allMatch(section -> section.getUpStation().getId() != deleteStationId && section.getDownStation().getId() != deleteStationId)
                .anyMatch(section -> section.getUpStation().getId() == upStationId && section.getDownStation().getId() == downStationId && section.getDistance() == distance);
    }
}
