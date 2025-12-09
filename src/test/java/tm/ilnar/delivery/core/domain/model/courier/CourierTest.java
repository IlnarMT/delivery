package tm.ilnar.delivery.core.domain.model.courier;

import libs.errs.Error;
import libs.errs.Result;
import libs.errs.UnitResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tm.ilnar.delivery.core.domain.model.kernel.Location;
import tm.ilnar.delivery.core.domain.model.order.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class CourierTest {

    private static final Location DEFAULT_LOCATION = Location.create(5, 5).getValue();

    private static Courier createCourier() {
        return Courier.create("Ivan", 1, DEFAULT_LOCATION).getValue();
    }

    private static Order createOrder(int volume) {
        return Order.create(UUID.randomUUID(), DEFAULT_LOCATION, volume).getValue();
    }

    private static Stream<Arguments> invalidCourierCreateParams() {
        return Stream.of(
            Arguments.of(null, 1, DEFAULT_LOCATION),
            Arguments.of(" ", 1, DEFAULT_LOCATION),
            Arguments.of("Ivan", -1, DEFAULT_LOCATION),
            Arguments.of("Ivan", 1, null)
        );
    }

    @Test
    void shouldBeCorrectWhenParametersAreCorrectOnCreated() {
        //Arrange
        Location location = DEFAULT_LOCATION;

        //Act
        Result<Courier, Error> result = Courier.create("Ivan", 1, location);

        //Assert
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue().getId()).isNotNull();
        assertThat(result.getValue().getName()).isEqualTo("Ivan");
        assertThat(result.getValue().getSpeed()).isEqualTo(1);
        assertThat(result.getValue().getLocation()).isEqualTo(location);
        List<StoragePlace> storagePlaces = result.getValue().getStoragePlaces();
        assertThat(storagePlaces.size()).isEqualTo(1);
        assertThat(storagePlaces.getFirst().getName()).isEqualTo("bag");
        assertThat(storagePlaces.getFirst().getTotalVolume()).isEqualTo(10);
    }

    @ParameterizedTest
    @MethodSource("invalidCourierCreateParams")
    void shouldReturnErrorWhenParametersAreNotCorrectOnCreated(String name, int speed, Location location) {
        //Act
        Result<Courier, Error> result = Courier.create(name, speed, location);

        //Assert
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError()).isNotNull();
    }

    @Test
    void shouldAddStoragePlaceWhenParametersAreCorrectOnAddedStoragePlace() {
        //Arrange
        Courier sut = createCourier();

        //Act
        UnitResult<Error> result = sut.addStoragePlace("pocket", 5);

        //Assert
        assertThat(result.isSuccess()).isTrue();
        List<StoragePlace> storagePlaces = sut.getStoragePlaces();
        assertThat(storagePlaces).hasSize(2);
        Optional<StoragePlace> pocket = storagePlaces.stream()
            .filter(storagePlace -> storagePlace.getName().equals("pocket"))
            .findFirst();
        assertThat(pocket.isPresent()).isTrue();
        assertThat(pocket.get().getTotalVolume()).isEqualTo(5);
    }

    @Test
    void shouldBeCorrectWhenOrderIsCorrectOnCanTakeOrder() {
        // Arrange
        Courier sut = createCourier();
        Order order = createOrder(3);

        // Act
        Result<Boolean, Error> result = sut.canTakeOrder(order);

        // Assert
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue()).isTrue();
    }

    @Test
    void shouldReturnErrorWhenOrderIsNullOnCanTakeOrder() {
        // Arrange
        Courier sut = createCourier();

        // Act
        Result<Boolean, Error> result = sut.canTakeOrder(null);

        // Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isNotNull();
    }

    @Test
    void shouldTakeOrderWhenStoragePlaceIsAvailableOnTakenOrder() {
        //Arrange
        Courier sut = createCourier();

        UUID orderId = UUID.fromString("aa90689a-53ff-4571-9ffc-fdf8497bbeef");
        int orderVolume = 7;
        Order order = Order.create(orderId, DEFAULT_LOCATION, orderVolume).getValue();

        //Act
        sut.takeOrder(order);

        //Assert
        List<StoragePlace> storagePlaces = sut.getStoragePlaces();
        assertThat(storagePlaces.size()).isEqualTo(1);
        assertThat(storagePlaces.getFirst().getOrderId()).isEqualTo(orderId);
    }

    @Test
    void shouldReturnErrorWhenNoFreeStoragePlaceOnTakenOrder() {
        // Arrange
        Courier sut = createCourier();
        sut.takeOrder(createOrder(7)); // заняли bag
        Order secondOrder = createOrder(3);

        // Act
        UnitResult<Error> result = sut.takeOrder(secondOrder);

        // Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isNotNull();
        // место всё ещё занято первым заказом
        assertThat(sut.getStoragePlaces().getFirst().getOrderId()).isNotEqualTo(secondOrder.getId());
    }

    @Test
    void shouldCompleteOrderWhenOrderIsStoredOnCompletedOrder() {
        //Arrange
        Courier sut = createCourier();
        Order order = createOrder(7);
        sut.takeOrder(order);

        //Act
        UnitResult<Error> result = sut.completeOrder(order.getId());

        //Assert
        assertThat(result.isSuccess()).isTrue();
        List<StoragePlace> storagePlaces = sut.getStoragePlaces();
        assertThat(storagePlaces.size()).isEqualTo(1);
        assertThat(storagePlaces.getFirst().getOrderId()).isNull();
    }

    @Test
    void shouldCalculateTimeToLocationWhenTargetLocationIsCorrectOnCalculatedTimeToLocation() {
        //Arrange
        Courier sut = createCourier();
        Location targetLocation = Location.create(7, 7).getValue();

        //Act
        Result<Double, Error> timeResult = sut.calculateTimeToLocation(targetLocation);

        //Assert
        assertThat(timeResult.isSuccess()).isTrue();
        assertThat(timeResult.getValue()).isEqualTo(4);
    }

    @Test
    void shouldMoveToTargetWhenTargetIsReachableOnMoved() {
        // Arrange
        Courier sut = createCourier(); // speed=1, start (5,5)

        // Act
        UnitResult<Error> result = sut.move(Location.create(6, 5).getValue());

        // Assert
        assertThat(result.isSuccess()).isTrue();
        assertThat(sut.getLocation()).isEqualTo(Location.create(6, 5).getValue());
    }
}