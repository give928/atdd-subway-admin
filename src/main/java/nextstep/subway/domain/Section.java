package nextstep.subway.domain;

import nextstep.subway.exception.MessageCodeException;

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
            throw new MessageCodeException("error.section.required.up_station");
        }
        if (downStation == null) {
            throw new MessageCodeException("error.section.required.down_station");
        }
        if (upStation.isSame(downStation)) {
            throw new MessageCodeException("error.section.add.same_stations");
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
            throw new IllegalArgumentException("error.section.add.exists_stations");
        }
        return sameUpStation || sameDownStation;
    }

    private boolean isOuter(Section section) {
        return upStation.isSame(section.getDownStation()) || downStation.isSame(section.getUpStation());
    }
}
