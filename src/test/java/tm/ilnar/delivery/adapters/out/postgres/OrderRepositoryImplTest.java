package tm.ilnar.delivery.adapters.out.postgres;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import tm.ilnar.delivery.BaseIntegrationTest;
import tm.ilnar.delivery.core.domain.model.kernel.Location;
import tm.ilnar.delivery.core.domain.model.order.Order;
import tm.ilnar.delivery.core.domain.model.order.OrderStatus;
import tm.ilnar.delivery.core.ports.OrderRepository;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Sql(
    scripts = "classpath:/sql/cleanup.sql",
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
class OrderRepositoryImplTest extends BaseIntegrationTest {

    private static final Location DEFAULT_LOCATION = Location.create(5, 5).getValue();

    @Autowired
    OrderRepository sut;

    @Test
    void saveAndFindOrderById() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        Location location = DEFAULT_LOCATION;
        Order order = Order.create(orderId, location, 5).getValue();

        // Act
        sut.save(order);
        var loaded = sut.findById(orderId);

        // Assert
        assertThat(loaded).isPresent();
        Order loadedOrder = loaded.get();
        assertThat(loadedOrder.getId()).isNotNull();
        assertThat(loadedOrder.getLocation()).isEqualTo(location);
        assertThat(loadedOrder.getVolume()).isEqualTo(5);
        assertThat(loadedOrder.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(loadedOrder.getCourierId()).isNull();
    }

    @Test
    void findAnyByStatusCreated() {
        // Arrange
        Order order = Order.create(UUID.randomUUID(), DEFAULT_LOCATION, 5).getValue();
        sut.save(order);

        // Act
        var result = sut.findAnyByStatus(OrderStatus.CREATED);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(order.getId());
    }

    @Test
    void findAllByStatusAssigned() {
        // Arrange
        Order order = Order.create(UUID.randomUUID(), DEFAULT_LOCATION, 5).getValue();
        order.assign(UUID.randomUUID());
        sut.save(order);

        // Act
        var result = sut.findAllByStatus(OrderStatus.ASSIGNED);

        // Assert
        assertThat(result).hasSize(1);
        Order loadedOrder = result.getFirst();
        assertThat(loadedOrder.getId()).isEqualTo(order.getId());
    }
}