package tm.ilnar.delivery.core.application.eventhandlers;

import tm.ilnar.delivery.core.domain.model.order.events.OrderCreatedDomainEvent;

public interface OrderCreatedDomainEventHandler {

    void handle(OrderCreatedDomainEvent event);
}
