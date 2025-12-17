package tm.ilnar.delivery.adapters.out.postgres;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import tm.ilnar.delivery.core.domain.model.courier.Courier;
import tm.ilnar.delivery.core.domain.model.courier.StoragePlace;
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
@ActiveProfiles("test")
class CourierRepositoryImplTest extends BasePostgresContainerTest {

    private static final Location DEFAULT_LOCATION = Location.create(5, 5).getValue();

    @Autowired
    CourierRepository sut;

    @Test
    void saveAndFindCourierById() {
        // Arrange
        Courier courier = Courier.create("Ivan", Speed.create(1).getValue(), DEFAULT_LOCATION).getValue();

        // Act
        sut.save(courier);
        var loaded = sut.findById(courier.getId());

        // Assert
        assertThat(loaded).isPresent();
        Courier loadedCourier = loaded.get();
        assertThat(loadedCourier.getId()).isNotNull();
        assertThat(loadedCourier.getName()).isEqualTo("Ivan");
        assertThat(loadedCourier.getSpeed()).isEqualTo(Speed.create(1).getValue());
        assertThat(loadedCourier.getLocation()).isEqualTo(DEFAULT_LOCATION);
        List<StoragePlace> storagePlaces = loadedCourier.getStoragePlaces();
        assertThat(storagePlaces.size()).isEqualTo(1);
        assertThat(storagePlaces.getFirst().getName()).isEqualTo("bag");
        assertThat(storagePlaces.getFirst().getTotalVolume()).isEqualTo(10);
    }

    @Test
    void findAllWithFreeStorage() {
        // Arrange
        Courier courier1 = Courier.create("Ivan", Speed.create(1).getValue(), DEFAULT_LOCATION).getValue();
        sut.save(courier1);

        Courier courier2 = Courier.create("Igor", Speed.create(2).getValue(), DEFAULT_LOCATION).getValue();
        Order order = Order.create(UUID.randomUUID(), DEFAULT_LOCATION, 5).getValue();
        courier2.takeOrder(order);
        sut.save(courier2);

        // Act
        List<Courier> allCouriersWithFreeStorage = sut.findAllWithFreeStorage();
        System.out.println(sut.findById(courier1.getId()));
        System.out.println(sut.findById(courier2.getId()));

        // Assert
        assertThat(allCouriersWithFreeStorage).hasSize(1);
        assertThat(allCouriersWithFreeStorage.getFirst().getId()).isEqualTo(courier1.getId());
        List<StoragePlace> storagePlaces =  allCouriersWithFreeStorage.getFirst().getStoragePlaces();
        assertThat(storagePlaces.size()).isEqualTo(1);
        assertThat(storagePlaces.getFirst().getName()).isEqualTo("bag");
        assertThat(storagePlaces.getFirst().getTotalVolume()).isEqualTo(10);
    }
}