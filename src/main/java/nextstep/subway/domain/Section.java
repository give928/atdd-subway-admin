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

    private Section(Station upStation, Station downStation, Distance distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section of(Station upStation, Station downStation, int distance) {
        validateStation(upStation, downStation);
        return new Section(upStation, downStation, Distance.of(distance));
    }

    private static void validateStation(Station upStation, Station downStation) {
        if (upStation == null) {
            throw new IllegalArgumentException(ErrorMessages.REQUIRED_SECTION_UP_STATION);
        }
        if (downStation == null) {
            throw new IllegalArgumentException(ErrorMessages.REQUIRED_SECTION_DOWN_STATION);
        }
        if (upStation.isSame(downStation)) {
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

    public void reduceDistanceIfInnerSection(Section section) {
        if (isInner(section)) {
            this.distance.reduce(section.getDistance());
        }
    }

    public void mergeIfOuterSection(Section section) {
        if (isOuter(section)) {
            mergeStation(section);
            mergeDistance(section);
        }
    }

    private void mergeStation(Section section) {
        boolean changedDownStation = changeDownStation(section);
        if (!changedDownStation) {
            changeUpStation(section);
        }
    }

    private boolean changeDownStation(Section section) {
        if (downStation.isSame(section.getUpStation())) {
            downStation = section.getDownStation();
            return true;
        }
        return false;
    }

    private boolean changeUpStation(Section section) {
        if (upStation.isSame(section.getDownStation())) {
            upStation = section.getUpStation();
            return true;
        }
        return false;
    }

    private void mergeDistance(Section section) {
        this.distance.extend(section.getDistance());
    }

    public int getDistance() {
        return distance.get();
    }

    public Line getLine() {
        return line;
    }

    public boolean isLinkable(Section section) {
        if (isInner(section)) {
            return true;
        }
        return isOuter(section);
    }

    private boolean isInner(Section section) {
        boolean sameUpStation = upStation.isSame(section.getUpStation());
        boolean sameDownStation = downStation.isSame(section.getDownStation());
        if (sameUpStation && sameDownStation) {
            throw new IllegalArgumentException(ErrorMessages.CAN_NOT_ADD_DUPLICATED_STATIONS);
        }
        return sameUpStation || sameDownStation;
    }

    private boolean isOuter(Section section) {
        return upStation.isSame(section.getDownStation()) || downStation.isSame(section.getUpStation());
    }

    private static final class ErrorMessages {
        private static final String REQUIRED_SECTION_UP_STATION = "지하철구간의 상행 지하철역은 필수입니다.";
        private static final String REQUIRED_SECTION_DOWN_STATION = "지하철구간의 하행 지하철역은 필수입니다.";
        private static final String REQUIRED_DIFFERENT_SECTION_STATIONS = "지하철구간의 상행 지하철역과 하행 지하철역을 하나의 역으로 지정할 수 없습니다.";
        private static final String CAN_NOT_ADD_DUPLICATED_STATIONS = "상행역과 하행역이 이미 노선에 모두 등록되어 있어서 추가할 수 없습니다.";

        private ErrorMessages() {
        }
    }
}
