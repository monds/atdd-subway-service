package nextstep.subway.path.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.domain.Station;

import java.util.List;

@Getter
@AllArgsConstructor
public class Path {

    private final List<Station> stations;
    private final int distance;
}
