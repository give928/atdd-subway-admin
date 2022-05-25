package nextstep.subway.domain;

import javax.persistence.*;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(name = "uk_section", columnNames = {"line_id", "up_station_id", "down_station_id"})})
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id", nullable = false, foreignKey = @ForeignKey(name = "fk_section_up_station"))
    private Station upStation;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id", nullable = false, foreignKey = @ForeignKey(name = "fk_section_down_station"))
    private Station downStation;

    @Embedded
    private Distance distance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", nullable = false, foreignKey = @ForeignKey(name = "fk_section_line"))
    private Line line;

    protected Section() {
    }

    public Section(Station upStation, Station downStation, int distance) {
        validateStation(upStation, downStation);
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = Distance.of(distance);
    }

    private void validateStation(Station upStation, Station downStation) {
        if (upStation == null) {
            throw new IllegalArgumentException(ErrorMessages.REQUIRED_SECTION_UP_STATION);
        }
        if (downStation == null) {
            throw new IllegalArgumentException(ErrorMessages.REQUIRED_SECTION_DOWN_STATION);
        }
        if (upStation.isSameStation(downStation)) {
            throw new IllegalArgumentException(ErrorMessages.REQUIRED_DIFFERENT_SECTION_STATIONS);
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
        return distance.get();
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
                ", distance=" + distance.get() +
                ", line.id=" + line.getId() +
                '}';
    }

    private static final class ErrorMessages {
        private static final String REQUIRED_SECTION_UP_STATION = "지하철구간의 상행 지하철역은 필수입니다.";
        private static final String REQUIRED_SECTION_DOWN_STATION = "지하철구간의 하행 지하철역은 필수입니다.";
        private static final String REQUIRED_DIFFERENT_SECTION_STATIONS = "지하철구간의 상행 지하철역과 하행 지하철역을 하나의 역으로 지정할 수 없습니다.";

        private ErrorMessages() {
        }
    }
}
