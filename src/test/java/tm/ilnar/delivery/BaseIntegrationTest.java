package tm.ilnar.delivery;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import tm.ilnar.delivery.adapters.out.postgres.BasePostgresContainerTest;
import tm.ilnar.delivery.core.ports.OrdersEventProducer;

@SpringBootTest
@ActiveProfiles("test")
public abstract class BaseIntegrationTest extends BasePostgresContainerTest {

    @MockitoBean
    private OrdersEventProducer ordersEventProducer;
}
