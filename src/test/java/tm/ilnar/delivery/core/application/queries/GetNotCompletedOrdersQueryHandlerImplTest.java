package tm.ilnar.delivery.core.application.queries;

import libs.errs.Error;
import libs.errs.Result;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import tm.ilnar.delivery.adapters.out.postgres.BasePostgresContainerTest;
import tm.ilnar.delivery.core.domain.model.kernel.Location;
import tm.ilnar.delivery.core.domain.model.order.Order;
import tm.ilnar.delivery.core.ports.OrderRepository;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Sql(
    scripts = "classpath:/sql/cleanup.sql",
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@SpringBootTest
@ActiveProfiles("test")
class GetNotCompletedOrdersQueryHandlerImplTest extends BasePostgresContainerTest {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    GetNotCompletedOrdersQueryHandlerImpl sut;

    @Test
    void handle() {
        // Arrange
        Order order = Order.create(UUID.randomUUID(), Location.create(7, 7).getValue(), 3).getValue();
        orderRepository.save(order);

        // Act
        Result<List<GetNotCompletedOrdersQueryResponse>, Error> result = sut.handle();

        // Assert
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue()).hasSize(1);
        GetNotCompletedOrdersQueryResponse response = result.getValue().getFirst();
        assertThat(response.id()).isEqualTo(order.getId());
        assertThat(response.location()).isEqualTo(new LocationDto(7, 7));
    }
}