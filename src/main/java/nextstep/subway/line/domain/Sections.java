package nextstep.subway.line.domain;

import lombok.Getter;
import nextstep.subway.line.application.AddLineException;
import nextstep.subway.line.application.RemoveLineException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.Stations;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Embeddable
public class Sections implements Iterable<Section> {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private final List<Section> sections;

    public Sections() {
        sections = new ArrayList<>();
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void add(Section section) {
        Stations stations = getStationsRelativeOrder();
        boolean isUpStationExisted = stations.contains(section.getUpStation());
        boolean isDownStationExisted = stations.contains(section.getDownStation());

        checkLineStationAddable(stations, isUpStationExisted, isDownStationExisted);

        if (isUpStationExisted || isDownStationExisted) {
            updateSection(section.getUpStation(), section.getDownStation(), section.getDistance());
        }

        sections.add(section);
    }

    public Stations getStationsRelativeOrder() {
        if (sections.isEmpty()) {
            return Stations.emptyStations();
        }

        Stations stations = new Stations();
        Station downStation = findUpStation();

        while (downStation != null) {
            stations.add(downStation);
            Station finalDownStation = downStation;
            downStation = findSection(section -> section.matchUpStation(finalDownStation))
                    .map(Section::getDownStation)
                    .orElse(null);
        }

        return stations;
    }

    public Stations getStations() {
        if (sections.isEmpty()) {
            return Stations.emptyStations();
        }

        return sections.stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .distinct()
                .collect(Collectors.collectingAndThen(Collectors.toList(), Stations::new));
    }

    public void removeSection(Station station) {
        checkSectionSizeOneOrZero();

        Optional<Section> upLineStation = findSection(section -> section.matchUpStation(station));
        Optional<Section> downLineStation = findSection(section -> section.matchDownStation(station));

        if (upLineStation.isPresent() && downLineStation.isPresent()) {
            sections.add(Section.combine(upLineStation.get(), downLineStation.get()));
        }

        upLineStation.ifPresent(sections::remove);
        downLineStation.ifPresent(sections::remove);
    }

    public boolean containsStation(Station station) {
        return getStations().contains(station);
    }

    private void updateSection(Station upStation, Station downStation, int distance) {
        for (Section section : sections) {
            UpdateSectionType.valueOf(section, upStation, downStation)
                    .updateSection(section, upStation, downStation, distance);
        }
    }

    private void checkLineStationAddable(Stations stations,
                                         boolean isUpStationExisted,
                                         boolean isDownStationExisted) {
        if (isUpStationExisted && isDownStationExisted) {
            throw new AddLineException("이미 등록된 구간 입니다.");
        }

        if (!stations.isEmpty() && !isUpStationExisted && !isDownStationExisted) {
            throw new AddLineException("등록할 수 없는 구간 입니다.");
        }
    }

    private void checkSectionSizeOneOrZero() {
        if (sections.size() <= 1) {
            throw new RemoveLineException("Cannot delete a single section");
        }
    }

    private Station findUpStation() {
        List<Station> downStations = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());

        return findSection(section -> !downStations.contains(section.getUpStation()))
                .map(Section::getUpStation)
                .orElseThrow(IllegalStateException::new);
    }

    private Optional<Section> findSection(Predicate<Section> sectionPredicate) {
        return sections.stream()
                .filter(sectionPredicate)
                .findFirst();
    }

    @Override
    public Iterator<Section> iterator() {
        return sections.iterator();
    }

    @Override
    public void forEach(Consumer<? super Section> action) {
        sections.forEach(action);
    }
}
