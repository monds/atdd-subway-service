package nextstep.subway.fares.policy;

import nextstep.subway.fares.domain.Fare;
import nextstep.subway.path.domain.Path;

import java.util.ArrayList;
import java.util.List;

public class FarePolicies {

    private final List<FarePolicy> farePolicies;

    public FarePolicies() {
        farePolicies = new ArrayList<>();
        farePolicies.add(new DistanceBasedFarePolicy());
        farePolicies.add(new SectionExtraChargeFarePolicy());
    }

    public Fare calculateFare(Path path) {
        Fare fare = new Fare();
        for (FarePolicy farePolicy : farePolicies) {
            farePolicy.calculateFare(fare, path);
        }
        return fare;
    }
}
