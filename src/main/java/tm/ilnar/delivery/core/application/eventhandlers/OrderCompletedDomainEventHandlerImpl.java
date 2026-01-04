package tm.ilnar.delivery.core.application.eventhandlers;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import tm.ilnar.delivery.core.domain.model.order.events.OrderCompletedDomainEvent;
import tm.ilnar.delivery.core.ports.OrdersEventProducer;

@RequiredArgsConstructor
@Service
public class OrderCompletedDomainEventHandlerImpl implements OrderCompletedDomainEventHandler {

    private final OrdersEventProducer producer;

    @EventListener
    @Override
    public void handle(OrderCompletedDomainEvent event) {
        producer.publish(event);
    }
}
