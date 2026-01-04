package tm.ilnar.delivery.core.ports;

import tm.ilnar.delivery.core.domain.model.order.events.OrderCompletedDomainEvent;
import tm.ilnar.delivery.core.domain.model.order.events.OrderCreatedDomainEvent;

public interface OrdersEventProducer {

    void publish(OrderCreatedDomainEvent domainEvent);

    void publish(OrderCompletedDomainEvent event);
}
