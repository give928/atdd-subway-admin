package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.BaseAcceptanceTest;
import nextstep.subway.BaseRestAssured;
import nextstep.subway.station.StationRestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철노선 관련 기능")
class LineAcceptanceTest extends BaseAcceptanceTest {
    private static final String LINE_URL = "/lines";
    private static final String 신분당선 = "신분당선";
    private static final String 분당선 = "분당선";

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다.
     */
    @DisplayName("지하철노선을 생성한다.")
    @Test
    void createLine() {
        // given
        long 지하철역1_id = id추출(StationRestAssured.지하철역_생성_요청("지하철역1"));
        long 지하철역2_id = id추출(StationRestAssured.지하철역_생성_요청("지하철역2"));

        Map<String, Object> params = new HashMap<>();
        params.put("name", 신분당선);
        params.put("color", "bg-red-600");
        params.put("upStationId", 지하철역1_id);
        params.put("downStationId", 지하철역2_id);
        params.put("distance", 10);

        // when
        BaseRestAssured.post(LINE_URL, params);

        // then
        ExtractableResponse<Response> response = BaseRestAssured.get(LINE_URL);
        List<String> lineNames = response.jsonPath().getList("name", String.class);
        assertThat(lineNames).containsExactly(신분당선);
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
        long 지하철역1_id = id추출(StationRestAssured.지하철역_생성_요청("지하철역1"));
        long 지하철역2_id = id추출(StationRestAssured.지하철역_생성_요청("지하철역2"));

        Map<String, Object> params1 = new HashMap<>();
        params1.put("name", 신분당선);
        params1.put("color", "bg-red-600");
        params1.put("upStationId", 지하철역1_id);
        params1.put("downStationId", 지하철역2_id);
        params1.put("distance", 10);

        BaseRestAssured.post(LINE_URL, params1);

        long 지하철역3_id = id추출(StationRestAssured.지하철역_생성_요청("지하철역3"));
        long 지하철역4_id = id추출(StationRestAssured.지하철역_생성_요청("지하철역4"));

        Map<String, Object> params2 = new HashMap<>();
        params2.put("name", 분당선);
        params2.put("color", "bg-green-600");
        params2.put("upStationId", 지하철역3_id);
        params2.put("downStationId", 지하철역4_id);
        params2.put("distance", 20);

        BaseRestAssured.post(LINE_URL, params2);

        // when
        ExtractableResponse<Response> response = BaseRestAssured.get(LINE_URL);

        // then
        assertResponseStatus(response, HttpStatus.OK);
        List<String> lineNames = 이름추출(response);
        assertThat(lineNames).containsExactly(신분당선, 분당선);
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
        long 지하철역1_id = id추출(StationRestAssured.지하철역_생성_요청("지하철역1"));
        long 지하철역2_id = id추출(StationRestAssured.지하철역_생성_요청("지하철역2"));

        Map<String, Object> params = new HashMap<>();
        params.put("name", 신분당선);
        params.put("color", "bg-red-600");
        params.put("upStationId", 지하철역1_id);
        params.put("downStationId", 지하철역2_id);
        params.put("distance", 10);

        String path = BaseRestAssured.post(LINE_URL, params).header("Location");

        // when
        ExtractableResponse<Response> response = BaseRestAssured.get(path);

        // then
        assertResponseStatus(response, HttpStatus.OK);
        assertThat(response.jsonPath().getString("name")).isEqualTo(신분당선);
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
        long 지하철역1_id = id추출(StationRestAssured.지하철역_생성_요청("지하철역1"));
        long 지하철역2_id = id추출(StationRestAssured.지하철역_생성_요청("지하철역2"));

        Map<String, Object> params = new HashMap<>();
        params.put("name", 신분당선);
        params.put("color", "bg-red-600");
        params.put("upStationId", 지하철역1_id);
        params.put("downStationId", 지하철역2_id);
        params.put("distance", 10);

        String path = BaseRestAssured.post(LINE_URL, params).header("Location");

        // when
        params = new HashMap<>();
        params.put("name", "다른분당선");
        params.put("color", "bg-red-600");
        ExtractableResponse<Response> response = BaseRestAssured.put(path, params);

        // then
        assertResponseStatus(response, HttpStatus.OK);
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
        long 지하철역1_id = id추출(StationRestAssured.지하철역_생성_요청("지하철역1"));
        long 지하철역2_id = id추출(StationRestAssured.지하철역_생성_요청("지하철역2"));

        Map<String, Object> params = new HashMap<>();
        params.put("name", 신분당선);
        params.put("color", "bg-red-600");
        params.put("upStationId", 지하철역1_id);
        params.put("downStationId", 지하철역2_id);
        params.put("distance", 10);

        String path = BaseRestAssured.post(LINE_URL, params).header("Location");

        // when
        ExtractableResponse<Response> response = BaseRestAssured.delete(path);

        // then
        assertResponseStatus(response, HttpStatus.NO_CONTENT);
    }
}
