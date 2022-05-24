package nextstep.subway.domain;

import javax.persistence.*;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(name = "uk_section", columnNames = {"line_id", "up_station_id", "down_station_id"})})
public class Section extends BaseEntity {
    private static final int MIN_DISTANCE = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id", nullable = false, foreignKey = @ForeignKey(name = "fk_section_up_station"))
    private Station upStation;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id", nullable = false, foreignKey = @ForeignKey(name = "fk_section_down_station"))
    private Station downStation;

    @Column(nullable = false)
    private int distance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", nullable = false, foreignKey = @ForeignKey(name = "fk_section_line"))
    private Line line;

    protected Section() {
    }

    public Section(Station upStation, Station downStation, int distance) {
        validateStation(upStation, downStation);
        this.upStation = upStation;
        this.downStation = downStation;
        validateDistance(distance);
        this.distance = distance;
    }

    private void validateStation(Station upStation, Station downStation) {
        if (upStation == null) {
            throw new IllegalArgumentException(ErrorMessages.REQUIRED_SECTION_UP_STATION);
        }
        if (downStation == null) {
            throw new IllegalArgumentException(ErrorMessages.REQUIRED_SECTION_DOWN_STATION);
        }
        if (upStation.equals(downStation)) {
            throw new IllegalArgumentException(ErrorMessages.REQUIRED_DIFFERENT_SECTION_STATIONS);
        }
    }

    private void validateDistance(int distance) {
        if (distance < MIN_DISTANCE) {
            throw new IllegalArgumentException(
                    String.format(ErrorMessages.REQUIRED_GREATER_THAN_OR_EQUAL_TO_SECTION_DISTANCE, MIN_DISTANCE));
        }
    }

    public void updateLine(Line line) {
        this.line = line;
    }

    public Long getId() {
        return id;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
    }

    public Line getLine() {
        return line;
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", upStation=" + upStation +
                ", downStation=" + downStation +
                ", distance=" + distance +
                ", line.id=" + line.getId() +
                '}';
    }

    private static final class ErrorMessages {
        private static final String REQUIRED_GREATER_THAN_OR_EQUAL_TO_SECTION_DISTANCE = "지하철구간의 거리는 %d 이상만 가능합니다.";
        private static final String REQUIRED_SECTION_UP_STATION = "지하철구간의 상행 지하철역은 필수입니다.";
        private static final String REQUIRED_SECTION_DOWN_STATION = "지하철구간의 하행 지하철역은 필수입니다.";
        private static final String REQUIRED_DIFFERENT_SECTION_STATIONS = "지하철구간의 상행 지하철역과 하행 지하철역을 하나의 역으로 지정할 수 없습니다.";

        private ErrorMessages() {
        }
    }
}
