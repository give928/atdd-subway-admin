package nextstep.subway.domain;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String color;

    @Embedded
    private final Sections sections = new Sections();

    protected Line() {
    }

    private Line(String name, String color, Section section) {
        this.name = name;
        this.color = color;
        addSection(section);
    }

    public static Line of(String name, String color, Station upStation, Station downStation, int distance) {
        return new Line(validateIfEmptyName(name), validateIfEmptyColor(color), Section.of(upStation, downStation, distance));
    }

    private static String validateIfEmptyName(String name) {
        return Optional.ofNullable(name)
                .map(String::trim)
                .filter(s -> s.length() > 0)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessages.REQUIRED_LINE_NAME));
    }

    private static String validateIfEmptyColor(String color) {
        return Optional.ofNullable(color)
                .map(String::trim)
                .filter(s -> s.length() > 0)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessages.REQUIRED_LINE_COLOR));
    }

    public boolean addSection(Section section) {
        section.updateLine(this);
        return this.sections.add(section);
    }

    public Line update(String name, String color) {
        this.name = validateIfEmptyName(name);
        this.color = validateIfEmptyColor(color);
        return this;
    }

    public boolean removeSection(Station station) {
        return sections.remove(station);
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
        return sections.getStations();
    }

    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", sections=" + sections +
                '}';
    }

    private static final class ErrorMessages {
        private static final String REQUIRED_LINE_NAME = "지하철노선 이름은 필수입니다.";
        private static final String REQUIRED_LINE_COLOR = "지하철노선 색상은 필수입니다.";

        private ErrorMessages() {
        }
    }
}
