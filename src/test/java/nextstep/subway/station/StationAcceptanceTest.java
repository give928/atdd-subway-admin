package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.BaseAcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
class StationAcceptanceTest extends BaseAcceptanceTest {
    private static final String 강남역 = "강남역";
    private static final String 역삼역 = "역삼역";

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> response = StationRestAssured.지하철역_생성_요청(강남역);

        // then
        지하철역_생성됨(response);

        // then
        지하철역_조회됨(강남역);
    }

    /**
     * Given 지하철역을 생성하고
     * When 기존에 존재하는 지하철역 이름으로 지하철역을 생성하면
     * Then 지하철역 생성이 안된다
     */
    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        StationRestAssured.지하철역_생성_요청(강남역);

        // when
        ExtractableResponse<Response> response = StationRestAssured.지하철역_생성_요청(강남역);

        // then
        지하철역_생성_안됨(response);
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역 목록을 조회한다.")
    @Test
    void getStations() {
        // given
        StationRestAssured.지하철역_생성_요청(강남역);
        StationRestAssured.지하철역_생성_요청(역삼역);

        // when
        ExtractableResponse<Response> response = StationRestAssured.지하철역_목록_조회_요청();

        // then
        지하철역_응답_됨(response, 강남역, 역삼역);
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        String path = StationRestAssured.지하철역_생성_요청(강남역).header("Location");

        // when
        StationRestAssured.지하철역_삭제_요청(path);

        // then
        지하철역_조회_안됨(강남역);
    }

    private void 지하철역_생성됨(ExtractableResponse<Response> response) {
        assertResponseStatus(response, HttpStatus.CREATED);
    }

    private void 지하철역_생성_안됨(ExtractableResponse<Response> response) {
        assertResponseStatus(response, HttpStatus.BAD_REQUEST);
    }

    private void 지하철역_조회됨(String stationName) {
        ExtractableResponse<Response> response = StationRestAssured.지하철역_목록_조회_요청();
        지하철역_응답_됨(response, stationName);
    }

    private void 지하철역_응답_됨(ExtractableResponse<Response> response, String... containsStationNames) {
        assertResponseStatus(response, HttpStatus.OK);
        List<String> stationNames = 이름추출(response);
        assertThat(stationNames).containsAnyOf(containsStationNames);
    }

    private void 지하철역_조회_안됨(String stationName) {
        List<String> stationNames = 이름추출(StationRestAssured.지하철역_목록_조회_요청());
        assertThat(stationNames).doesNotContain(stationName).isEmpty();
    }
}
