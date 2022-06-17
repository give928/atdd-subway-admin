package nextstep.subway.domain;

import javax.persistence.*;
import java.util.Objects;
import java.util.Optional;

@Entity
public class Station extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    protected Station() {
    }

    private Station(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Station of(String name) {
        return of(null, validateIfEmptyName(name));
    }

    public static Station of(Long id, String name) {
        return new Station(id, validateIfEmptyName(name));
    }

    private static String validateIfEmptyName(String name) {
        return Optional.ofNullable(name)
                .map(String::trim)
                .filter(s -> s.length() > 0)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessages.REQUIRED_STATION_NAME));
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isSame(Station station) {
        if (isSameStationId(station)) {
            return true;
        }
        return isSameStationName(station);
    }

    private boolean isSameStationId(Station station) {
        return getId() != null && getId().equals(station.getId());
    }

    private boolean isSameStationName(Station station) {
        return getName() != null && getName().equals(station.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Station station = (Station) o;
        return Objects.equals(getId(), station.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "Station{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    private static final class ErrorMessages {
        private static final String REQUIRED_STATION_NAME = "지하철역 이름은 필수입니다.";

        private ErrorMessages() {
        }
    }
}
