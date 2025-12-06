package tm.ilnar.delivery.core.domain.model.order;

import libs.errs.Error;
import libs.errs.Result;
import libs.errs.UnitResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tm.ilnar.delivery.core.domain.model.kernel.Location;

import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTest {

    private static final Location DEFAULT_LOCATION = Location.create(5, 5).getValue();
    private static final int DEFAULT_VOLUME = 7;

    private static Order createOrder() {
        return Order.create(UUID.randomUUID(), DEFAULT_LOCATION, DEFAULT_VOLUME).getValue();
    }

    static Stream<Arguments> invalidOderCreateParams() {
        Location location = Location.create(5, 5).getValue();
        return Stream.of(
            Arguments.of(null, DEFAULT_LOCATION, DEFAULT_VOLUME),
            Arguments.of(UUID.randomUUID(), null, DEFAULT_VOLUME),
            Arguments.of(UUID.randomUUID(), DEFAULT_LOCATION, -1)
        );
    }

    @Test
    void shouldBeCorrectWhenParametersAreCorrectOnCreated() {
        //Arrange
        UUID orderId = UUID.randomUUID();
        Location location = DEFAULT_LOCATION;
        int volume = DEFAULT_VOLUME;

        //Act
        Result<Order, Error> result = Order.create(orderId, location, volume);

        //Assert
        assertThat(result.isSuccess()).isTrue();
        Order order = result.getValue();
        assertThat(order.getId()).isNotNull();
        assertThat(order.getLocation()).isEqualTo(location);
        assertThat(order.getVolume()).isEqualTo(volume);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(order.getCourierId()).isNull();
    }

    @ParameterizedTest
    @MethodSource("invalidOderCreateParams")
    void shouldReturnErrorWhenParametersAreNotCorrectOnCreated(UUID orderId, Location location, int volume) {
        //Act
        Result<Order, Error> result = Order.create(orderId, location, volume);

        //Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isNotNull();
    }

    @Test
    void shouldAssignCourierWhenCourierIdIsCorrectOnAssigned() {
        //Arrange
        Order sut = createOrder();
        UUID courierId = UUID.randomUUID();

        //Act
        UnitResult<Error> result = sut.assign(courierId);

        //Assert
        assertThat(result.isSuccess()).isTrue();
        assertThat(sut.getCourierId()).isEqualTo(courierId);
        assertThat(sut.getStatus()).isEqualTo(OrderStatus.ASSIGNED);
    }

    @Test
    void shouldReturnErrorWhenCourierIdIsNullOnAssigned() {
        // Arrange
        Order sut = createOrder();

        // Act
        UnitResult<Error> result = sut.assign(null);

        // Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isNotNull();
        assertThat(sut.getCourierId()).isNull();
        assertThat(sut.getStatus()).isEqualTo(OrderStatus.CREATED);
    }

    @Test
    void shouldCompleteOrderWhenOrderWasAssignedOnCompleted() {
        //Arrange
        Order sut = createOrder();
        UUID courierId = UUID.randomUUID();
        sut.assign(courierId);

        //Act
        UnitResult<Error> result = sut.complete();

        //Assert
        assertThat(result.isSuccess()).isTrue();
        assertThat(sut.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    void shouldReturnErrorWhenOrderWasNotAssignedOnCompleted() {
        // Arrange
        Order sut = createOrder();

        // Act
        UnitResult<Error> result = sut.complete();

        // Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isNotNull();
        assertThat(sut.getStatus()).isEqualTo(OrderStatus.CREATED);
    }

}