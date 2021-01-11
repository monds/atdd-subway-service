package nextstep.subway.path.application;

import lombok.RequiredArgsConstructor;
import nextstep.subway.line.application.SectionRepository;
import nextstep.subway.line.domain.Sections;
import nextstep.subway.path.domain.Path;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.Stations;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class PathService {
    private final SectionRepository sectionRepository;
    private final StationService stationService;

    public PathResponse findShortestPath(Long sourceStationId, Long targetStationId) {
        Station sourceStation = stationService.findStationById(sourceStationId);
        Station targetStation = stationService.findStationById(targetStationId);
        checkStationsAreNotSame(sourceStation, targetStation);

        Sections sections = new Sections(sectionRepository.findAll());
        Stations stations = sections.getStations();
        checkStationIsInSections(sourceStation, stations);
        checkStationIsInSections(targetStation, stations);

        Path shortestPath = ShortestPathFinder.findShortestPath(sections, stations, sourceStation, targetStation);

        return PathResponse.of(shortestPath);
    }

    private void checkStationsAreNotSame(Station sourceStation, Station targetStation) {
        if (sourceStation == targetStation) {
            throw new PathFindException("source station and target station are the same");
        }
    }

    private void checkStationIsInSections(Station sourceStation, Stations stations) {
        if (!stations.contains(sourceStation)) {
            throw new PathFindException("the station is not in the sections: " + sourceStation);
        }
    }
}
