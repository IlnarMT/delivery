package tm.ilnar.delivery.core.application.queries;

import libs.errs.Error;
import libs.errs.Result;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import tm.ilnar.delivery.adapters.out.postgres.BasePostgresContainerTest;
import tm.ilnar.delivery.core.domain.model.courier.Courier;
import tm.ilnar.delivery.core.domain.model.kernel.Location;
import tm.ilnar.delivery.core.domain.model.kernel.Speed;
import tm.ilnar.delivery.core.domain.model.order.Order;
import tm.ilnar.delivery.core.ports.CourierRepository;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Sql(
    scripts = "classpath:/sql/cleanup.sql",
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@SpringBootTest
class CourierWithAssignedOrderQueryHandlerImplTest extends BasePostgresContainerTest {

    @Autowired
    CourierRepository courierRepository;

    @Autowired
    CourierWithAssignedOrderQueryHandlerImpl sut;

    @Test
    void handle() {
        // Arrange
        Order order = Order.create(UUID.randomUUID(), Location.create(7, 7).getValue(), 3).getValue();
        Courier courier = Courier.create("Ivan", Speed.create(1).getValue(), Location.create(5, 5).getValue()).getValue();
        courier.takeOrder(order);
        courierRepository.save(courier);

        // Act
        Result<List<CourierWithAssignedOrderQueryResponse>, Error> result = sut.handle();

        // Assert
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue()).hasSize(1);
        CourierWithAssignedOrderQueryResponse response = result.getValue().getFirst();
        assertThat(response.id()).isEqualTo(courier.getId());
        assertThat(response.name()).isEqualTo("Ivan");
        assertThat(response.location()).isEqualTo(new LocationDto(5, 5));
    }
}