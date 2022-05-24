package nextstep.subway.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

import java.util.List;

public class LineResponse {
    private final Long id;
    private final String name;
    private final String color;
    private final List<Station> stations;

    public LineResponse(Line line) {
        id = line.getId();
        name = line.getName();
        color = line.getColor();
        stations = line.getStations();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<Station> getStations() {
        return stations;
    }
}
