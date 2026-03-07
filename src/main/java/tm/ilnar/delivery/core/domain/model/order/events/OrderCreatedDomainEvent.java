package tm.ilnar.delivery.core.domain.model.order.events;

import libs.ddd.DomainEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tm.ilnar.delivery.core.domain.model.order.Order;

import java.util.UUID;

@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
@Getter
public class OrderCreatedDomainEvent extends DomainEvent {

    private final UUID orderId;

    public OrderCreatedDomainEvent(Order order) {
        this.orderId = order.getId();
    }
}
