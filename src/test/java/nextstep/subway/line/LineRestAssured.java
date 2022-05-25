package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.util.RestUtils;

import java.util.HashMap;
import java.util.Map;

public class LineRestAssured {
    private static final String LINE_URL = "/lines";

    public static ExtractableResponse<Response> 지하철노선_생성_요청(String name, String color, long upStationId,
                                                             long downStationId,
                                                             int distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return RestUtils.post(LINE_URL, params);
    }

    public static ExtractableResponse<Response> 지하철노선_목록_조회_요청() {
        return RestUtils.get(LINE_URL);
    }

    public static ExtractableResponse<Response> 지하철노선_조회_요청(String path) {
        return RestUtils.get(path);
    }

    public static ExtractableResponse<Response> 지하철노선_수정_요청(String path, String name, String color) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        return RestUtils.put(path, params);
    }

    public static ExtractableResponse<Response> 지하철노선_삭제_요청(String 신분당선_위치) {
        return RestUtils.delete(신분당선_위치);
    }
}
