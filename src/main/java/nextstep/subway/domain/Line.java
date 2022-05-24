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

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = validateIfEmptyName(name);
        this.color = validateIfEmptyColor(color);
        addSection(new Section(upStation, downStation, distance));
    }

    private String validateIfEmptyName(String name) {
        return Optional.ofNullable(name)
                .map(String::trim)
                .filter(s -> s.length() > 0)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessages.REQUIRED_LINE_NAME));
    }

    private String validateIfEmptyColor(String color) {
        return Optional.ofNullable(color)
                .map(String::trim)
                .filter(s -> s.length() > 0)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessages.REQUIRED_LINE_COLOR));
    }

    public void addSection(Section section) {
        this.sections.add(section);
        section.updateLine(this);
    }

    public Line update(String name, String color) {
        this.name = validateIfEmptyName(name);
        this.color = validateIfEmptyColor(color);
        return this;
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
