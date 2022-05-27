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
    private List<Section> values;

    protected Sections() {
        this.values = new ArrayList<>();
    }

    private Sections(List<Section> sections) {
        this.values = sections;
    }

    public static Sections of(List<Section> sections) {
        return new Sections(sections);
    }

    public List<Station> getStations() {
        return values.stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .collect(Collectors.toList());
    }

    public boolean add(Section section) {
        if (!values.isEmpty()) {
            reduceDistanceIfInnerSection(section);
        }
        return this.values.add(section);
    }

    private void reduceDistanceIfInnerSection(Section section) {
        Section linkableSection = findLinkableSection(section);
        linkableSection.reduceDistanceIfInnerSection(section);
    }

    private Section findLinkableSection(Section section) {
        List<Section> linkableSections = findLinkableSections(section);
        if (linkableSections.isEmpty()) {
            throw new IllegalArgumentException(ErrorMessages.CAN_NOT_ADD_ISOLATED_STATIONS);
        }
        if (linkableSections.size() > 1) {
            throw new IllegalArgumentException(ErrorMessages.CAN_NOT_ADD_DUPLICATED_STATIONS);
        }
        return linkableSections.get(0);
    }

    private List<Section> findLinkableSections(Section section) {
        return values.stream()
                .filter(s -> s.isLinkable(section))
                .collect(Collectors.toList());
    }

    public boolean remove(Station station) {
        validateRemove();
        List<Section> sections = findLinkedSections(station);
        Section removeSection = findRemoveSection(sections);
        mergeIfLinkedSections(sections);
        return this.values.remove(removeSection);
    }

    private void validateRemove() {
        if (values.size() <= 1) {
            throw new IllegalArgumentException(ErrorMessages.CAN_NOT_REMOVE_ONLY_ONE_SECTION);
        }
    }

    private List<Section> findLinkedSections(Station station) {
        return values.stream()
                .filter(section -> section.getUpStation().isSame(station) || section.getDownStation().isSame(station))
                .collect(Collectors.toList());
    }

    private Section findRemoveSection(List<Section> sections) {
        if (sections.isEmpty()) {
            throw new IllegalArgumentException(ErrorMessages.CAN_NOT_FIND_STATION);
        }
        return sections.get(0);
    }

    private void mergeIfLinkedSections(List<Section> sections) {
        if (sections.size() > 1) {
            Section removeSection = sections.get(0);
            Section remainSection = sections.get(1);
            remainSection.mergeIfOuterSection(removeSection);
        }
    }

    private static class ErrorMessages {
        public static final String CAN_NOT_ADD_DUPLICATED_STATIONS = "상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없습니다.";
        public static final String CAN_NOT_ADD_ISOLATED_STATIONS = "상행역과 하행역이 이미 노선에 모두 등록되어 있어서 추가할 수 없습니다.";
        public static final String CAN_NOT_FIND_STATION = "해당역으로 등록된 구간이 없습니다.";
        public static final String CAN_NOT_REMOVE_ONLY_ONE_SECTION = "구간이 하나인 노선의 마지막 구간을 제거할 수 없습니다.";

        private ErrorMessages() {
        }
    }
}
