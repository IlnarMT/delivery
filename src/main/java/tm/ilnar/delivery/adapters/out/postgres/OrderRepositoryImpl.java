package tm.ilnar.delivery.adapters.out.postgres;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import tm.ilnar.delivery.core.domain.model.order.Order;
import tm.ilnar.delivery.core.domain.model.order.OrderStatus;
import tm.ilnar.delivery.core.ports.OrderRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;

    @Override
    public void save(Order order) {
        orderJpaRepository.save(order);
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return orderJpaRepository.findById(id);
    }

    @Override
    public Optional<Order> findAnyByStatus(OrderStatus orderStatus) {
        return orderJpaRepository.findAnyByStatus(orderStatus.getValue());
    }

    @Override
    public List<Order> findAllByStatus(OrderStatus orderStatus) {
        return orderJpaRepository.findAllByStatus(orderStatus.getValue());
    }
}
