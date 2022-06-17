package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.BaseRestAssured;

import java.util.HashMap;
import java.util.Map;

public final class StationRestAssured extends BaseRestAssured {
    public static final String STATION_URL = "/stations";

    private StationRestAssured() {
    }

    public static ExtractableResponse<Response> 지하철역_생성_요청(String stationName) {
        Map<String, String> params = new HashMap<>();
        params.put("name", stationName);

        return post(STATION_URL, params);
    }

    static ExtractableResponse<Response> 지하철역_목록_조회_요청() {
        return get(StationRestAssured.STATION_URL);
    }

    static ExtractableResponse<Response> 지하철역_삭제_요청(String path) {
        return delete(path);
    }
}
