package nextstep.subway.domain;

import nextstep.subway.exception.MessageCodeException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
public class Sections {
    public static final int MINIMUM_SECTION_SIZE = 1;

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
            throw new MessageCodeException("error.section.add.not_exists_stations");
        }
        if (linkableSections.size() > 1) {
            throw new MessageCodeException("error.section.add.exists_stations");
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
        if (values.size() <= MINIMUM_SECTION_SIZE) {
            throw new MessageCodeException("error.section.remove.only_one_section");
        }
    }

    private List<Section> findLinkedSections(Station station) {
        return values.stream()
                .filter(section -> section.getUpStation().isSame(station) || section.getDownStation().isSame(station))
                .collect(Collectors.toList());
    }

    private Section findRemoveSection(List<Section> sections) {
        if (sections.isEmpty()) {
            throw new MessageCodeException("error.section.remove.not_found_station");
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
}
