package tm.ilnar.delivery.core.application.eventhandlers;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import tm.ilnar.delivery.core.domain.model.order.events.OrderCreatedDomainEvent;
import tm.ilnar.delivery.core.ports.OrdersEventProducer;

@RequiredArgsConstructor
@Service
public class OrderCreatedDomainEventHandlerImpl {

    private final OrdersEventProducer producer;

    @EventListener
    public void handle(OrderCreatedDomainEvent event) {
        producer.publish(event);
    }
}
