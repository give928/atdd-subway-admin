package nextstep.subway.domain;

import nextstep.subway.exception.MessageCodeException;

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
                .orElseThrow(() -> new MessageCodeException("error.line.required.name"));
    }

    private static String validateIfEmptyColor(String color) {
        return Optional.ofNullable(color)
                .map(String::trim)
                .filter(s -> s.length() > 0)
                .orElseThrow(() -> new MessageCodeException("error.line.required.color"));
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
}
