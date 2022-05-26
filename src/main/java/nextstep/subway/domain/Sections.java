package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> values = new ArrayList<>();

    protected Sections() {
    }

    public Sections(List<Section> sections) {
        this.values = sections;
    }

    public boolean add(Section section) {
        if (!values.isEmpty()) {
            reduceLinkSection(section);
        }
        return this.values.add(section);
    }

    private void reduceLinkSection(Section section) {
        Section linkSection = findLinkSection(section);
        linkSection.reduceDistance(section);
    }

    private Section findLinkSection(Section section) {
        List<Section> linkSections = findLinkSections(section);
        if (linkSections.isEmpty()) {
            throw new IllegalArgumentException(ErrorMessages.CAN_NOT_ADD_ISOLATED_STATIONS);
        }
        if (linkSections.size() > 1) {
            throw new IllegalArgumentException(ErrorMessages.CAN_NOT_ADD_DUPLICATED_STATIONS);
        }
        return linkSections.get(0);
    }

    private List<Section> findLinkSections(Section section) {
        return values.stream()
                .filter(s -> s.isLinkable(section))
                .collect(Collectors.toList());
    }

    public List<Station> getStations() {
        return values.stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "Sections{" +
                "values=" + values +
                '}';
    }

    private static class ErrorMessages {
        public static final String CAN_NOT_ADD_DUPLICATED_STATIONS = "상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없습니다.";
        public static final String CAN_NOT_ADD_ISOLATED_STATIONS = "상행역과 하행역이 이미 노선에 모두 등록되어 있어서 추가할 수 없습니다.";

        private ErrorMessages() {
        }
    }
}
