package tm.ilnar.delivery.core.application.queries;

import java.util.UUID;

public record CourierWithAssignedOrderQueryResponse(UUID id, String name, LocationDto location) {
}
