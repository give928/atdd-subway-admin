package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.BaseAcceptanceTest;
import nextstep.subway.station.StationRestAssured;
import nextstep.subway.util.RestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철노선 관련 기능")
class LineAcceptanceTest extends BaseAcceptanceTest {
    private static final String 신분당선 = "신분당선";
    private static final String 분당선 = "분당선";
    private static final String 빨간색 = "bg-red-600";
    private static final String 초록색 = "bg-green-600";

    private long 지하철역1_id;
    private long 지하철역2_id;

    @BeforeEach
    protected void setUp() {
        super.setUp();

        지하철역1_id = RestUtils.id_추출(StationRestAssured.지하철역_생성_요청("지하철역1"));
        지하철역2_id = RestUtils.id_추출(StationRestAssured.지하철역_생성_요청("지하철역2"));
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다.
     */
    @DisplayName("지하철노선을 생성한다.")
    @Test
    void createLine() {
        // when
        LineRestAssured.지하철노선_생성_요청(신분당선, 빨간색, 지하철역1_id, 지하철역2_id, 10);

        // then
        지하철노선_목록에서_조회됨(신분당선);
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철노선 목록을 조회한다.")
    @Test
    void getLines() {
        //given
        long 지하철역3_id = RestUtils.id_추출(StationRestAssured.지하철역_생성_요청("지하철역3"));
        LineRestAssured.지하철노선_생성_요청(신분당선, 빨간색, 지하철역1_id, 지하철역2_id, 10);
        LineRestAssured.지하철노선_생성_요청(분당선, 초록색, 지하철역1_id, 지하철역3_id, 20);

        // when
        ExtractableResponse<Response> response = LineRestAssured.지하철노선_목록_조회_요청();

        // then
        지하철노선_목록에서_응답됨(response, 신분당선, 분당선);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철노선을 조회한다.")
    @Test
    void getLine() {
        //given
        String 신분당선_위치 = 지하철노선_생성_요청해서_위치_반환(신분당선, 빨간색, 지하철역1_id, 지하철역2_id, 10);

        // when
        ExtractableResponse<Response> response = LineRestAssured.지하철노선_조회_요청(신분당선_위치);

        // then
        지하철노선_응답됨(response, 신분당선);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다.
     */
    @DisplayName("지하철노선을 수정한다.")
    @Test
    void updateLine() {
        //given
        String 신분당선_위치 = 지하철노선_생성_요청해서_위치_반환(신분당선, 빨간색, 지하철역1_id, 지하철역2_id, 10);

        // when
        ExtractableResponse<Response> response = LineRestAssured.지하철노선_수정_요청(신분당선_위치, "다른분당선", 빨간색);

        // then
        지하철노선_수정됨(response);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다.
     */
    @DisplayName("지하철노선을 삭제한다.")
    @Test
    void deleteLine() {
        //given
        String 신분당선_위치 = 지하철노선_생성_요청해서_위치_반환(신분당선, 빨간색, 지하철역1_id, 지하철역2_id, 10);

        // when
        ExtractableResponse<Response> response = LineRestAssured.지하철노선_삭제_요청(신분당선_위치);

        // then
        지하철노선_삭제됨(response);
    }

    private String 지하철노선_생성_요청해서_위치_반환(String name, String color, long upStationId, long downStationId, int distance) {
        return LineRestAssured.지하철노선_생성_요청(name, color, upStationId, downStationId, distance).header("Location");
    }

    private void 지하철노선_목록에서_조회됨(String lineName) {
        ExtractableResponse<Response> response = LineRestAssured.지하철노선_목록_조회_요청();
        지하철노선_목록에서_응답됨(response, lineName);
    }

    private void 지하철노선_목록에서_응답됨(ExtractableResponse<Response> response, String... containsLineNames) {
        assertResponseStatus(response, HttpStatus.OK);
        List<String> lineNames = RestUtils.이름_추출(response);
        assertThat(lineNames).containsExactly(containsLineNames);
    }

    private void 지하철노선_응답됨(ExtractableResponse<Response> response, String lineName) {
        assertResponseStatus(response, HttpStatus.OK);
        assertThat(response.jsonPath().getString("name")).isEqualTo(lineName);
    }

    private void 지하철노선_수정됨(ExtractableResponse<Response> response) {
        assertResponseStatus(response, HttpStatus.OK);
    }

    private void 지하철노선_삭제됨(ExtractableResponse<Response> response) {
        assertResponseStatus(response, HttpStatus.NO_CONTENT);
    }
}
