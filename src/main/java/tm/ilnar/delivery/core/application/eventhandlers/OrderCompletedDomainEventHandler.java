package tm.ilnar.delivery.core.application.eventhandlers;

import tm.ilnar.delivery.core.domain.model.order.events.OrderCompletedDomainEvent;

public interface OrderCompletedDomainEventHandler {

    void handle(OrderCompletedDomainEvent event);
}
