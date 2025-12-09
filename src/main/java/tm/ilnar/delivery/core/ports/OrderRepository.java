package tm.ilnar.delivery.core.ports;

import tm.ilnar.delivery.core.domain.model.order.Order;
import tm.ilnar.delivery.core.domain.model.order.OrderStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {

    void save(Order order);

    Optional<Order> findById(UUID id);

    Optional<Order> findAnyByStatus(OrderStatus orderStatus);

    List<Order> findAllByStatus(OrderStatus orderStatus);
}
