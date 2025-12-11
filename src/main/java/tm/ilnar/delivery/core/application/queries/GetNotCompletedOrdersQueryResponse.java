package tm.ilnar.delivery.core.application.queries;

import java.util.UUID;

public record GetNotCompletedOrdersQueryResponse(UUID id, LocationDto location) {
}
