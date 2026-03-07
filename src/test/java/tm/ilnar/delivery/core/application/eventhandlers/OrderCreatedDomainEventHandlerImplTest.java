package tm.ilnar.delivery.core.application.eventhandlers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import tm.ilnar.delivery.BaseIntegrationTest;
import tm.ilnar.delivery.core.domain.model.kernel.Location;
import tm.ilnar.delivery.core.domain.model.order.Order;
import tm.ilnar.delivery.core.domain.model.order.events.OrderCreatedDomainEvent;

import java.util.UUID;

import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class OrderCreatedDomainEventHandlerImplTest extends BaseIntegrationTest {

    @Autowired
    ApplicationEventPublisher publisher;

    @MockitoSpyBean
    OrderCreatedDomainEventHandlerImpl listener;

    @Test
    void listener_receives_event() {
        UUID orderId = UUID.randomUUID();
        Location location = Location.create(5, 5).getValue();
        Order order = Order.create(orderId, location, 5).getValue();
        OrderCreatedDomainEvent event = new OrderCreatedDomainEvent(order);

        publisher.publishEvent(event);

        verify(listener, timeout(1000)).handle(event);
        verifyNoMoreInteractions(listener);
    }
}