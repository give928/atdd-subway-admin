package nextstep.subway.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

public class LineSaveRequest {
    private String name;
    private String color;
    private long upStationId;
    private long downStationId;
    private int distance;

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public long getUpStationId() {
        return upStationId;
    }

    public long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }

    public Line toLine(Station upStation, Station downStation) {
        return new Line(name, color, upStation, downStation, distance);
    }
}
