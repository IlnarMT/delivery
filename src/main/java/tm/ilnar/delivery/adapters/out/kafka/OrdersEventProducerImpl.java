package tm.ilnar.delivery.adapters.out.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import queues.order.OrderEventsProto;
import tm.ilnar.delivery.core.domain.model.order.events.OrderCompletedDomainEvent;
import tm.ilnar.delivery.core.domain.model.order.events.OrderCreatedDomainEvent;
import tm.ilnar.delivery.core.ports.OrdersEventProducer;

@Component
@RequiredArgsConstructor
public class OrdersEventProducerImpl implements OrdersEventProducer {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    @Value("${app.kafka.order-events-topic}")
    private String topic;

    @Override
    public void publish(OrderCreatedDomainEvent event) {
        var integrationEvent = mapToProto(event);

        kafkaTemplate.send(
            topic,
            event.getOrderId().toString(),
            integrationEvent.toByteArray()
        );
    }

    private OrderEventsProto.OrderCreatedIntegrationEvent mapToProto(OrderCreatedDomainEvent event) {
        return OrderEventsProto.OrderCreatedIntegrationEvent.newBuilder()
            .setEventId(event.getEventId().toString())
            .setEventType(event.getClass().getSimpleName())
            .setOrderId(event.getOrderId().toString())
            .build();
    }

    @Override
    public void publish(OrderCompletedDomainEvent event) {
        var integrationEvent = mapToProto(event);

        kafkaTemplate.send(
            topic,
            event.getOrderId().toString(),
            integrationEvent.toByteArray()
        );
    }

    private OrderEventsProto.OrderCompletedIntegrationEvent mapToProto(OrderCompletedDomainEvent event) {
        return OrderEventsProto.OrderCompletedIntegrationEvent.newBuilder()
            .setEventId(event.getEventId().toString())
            .setEventType(event.getClass().getSimpleName())
            .setOrderId(event.getOrderId().toString())
            .setCourierId(event.getCourierId().toString())
            .build();
    }
}
