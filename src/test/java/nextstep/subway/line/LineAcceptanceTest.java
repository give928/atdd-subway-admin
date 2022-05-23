package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.BaseAcceptanceTest;
import nextstep.subway.BaseRestAssured;
import nextstep.subway.station.StationRestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철노선 관련 기능")
class LineAcceptanceTest extends BaseAcceptanceTest {
    private static final String LINE_URL = "/lines";
    private static final String 신분당선 = "신분당선";

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

        // when

        // then
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

        // when

        // then
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

        // when

        // then
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

        // when

        // then
    }
}
