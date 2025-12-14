package tm.ilnar.delivery.core.domain.services;

import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.Result;
import libs.errs.UnitResult;
import org.junit.jupiter.api.Test;
import tm.ilnar.delivery.core.domain.model.courier.Courier;
import tm.ilnar.delivery.core.domain.model.kernel.Location;
import tm.ilnar.delivery.core.domain.model.kernel.Speed;
import tm.ilnar.delivery.core.domain.model.order.Order;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OrderDispatcherImplTest {

    private final OrderDispatcher sut = new OrderDispatcherImpl();

    @Test
    void shouldReturnFailureWhenOrderIsNull() {
        // Arrange
        List<Courier> couriers = List.of(createCourierAt(1, 1));

        // Act
        Result<Courier, Error> result = sut.dispatch(null, couriers);

        // Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isEqualTo(GeneralErrors.valueIsRequired("order"));
    }

    @Test
    void shouldReturnFailureWhenCouriersIsNull() {
        // Arrange
        Order order = createCreatedOrder(UUID.randomUUID(), createLocation(10, 10), 1);

        // Act
        Result<Courier, Error> result = sut.dispatch(order, null);

        // Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isEqualTo(GeneralErrors.valueIsRequired("couriers"));
    }

    @Test
    void shouldReturnFailureWhenCouriersIsEmpty() {
        // Arrange
        Order order = createCreatedOrder(UUID.randomUUID(), createLocation(10, 10), 1);

        // Act
        Result<Courier, Error> result = sut.dispatch(order, List.of());

        // Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isEqualTo(OrderDispatcherImpl.Errors.couriersMustNotBeEmpty());
    }

    @Test
    void shouldReturnFailureWhenOrderStatusIsNotCreated() {
        // Arrange
        Order order = createCreatedOrder(UUID.randomUUID(), createLocation(10, 10), 1);
        Courier courier = createCourierAt(1, 1);

        UnitResult<Error> assign = order.assign(courier.getId());
        assertThat(assign.isSuccess()).isTrue();

        // Act
        Result<Courier, Error> result = sut.dispatch(order, List.of(courier));

        // Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isEqualTo(OrderDispatcherImpl.Errors.orderMustBeCreated());
    }

    @Test
    void shouldReturnFailureWhenNoEligibleCourierFound() {
        // Arrange
        // делаем заказ объёмом больше, чем может взять курьер (например, bag=10 -> берём 11)
        Order order = createCreatedOrder(UUID.randomUUID(), createLocation(10, 10), 11);
        Courier c1 = createCourierAt(1, 1);
        Courier c2 = createCourierAt(5, 5);

        // Act
        Result<Courier, Error> result = sut.dispatch(order, List.of(c1, c2));

        // Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isEqualTo(OrderDispatcherImpl.Errors.noEligibleCourierFound());
    }

    @Test
    void shouldReturnWinnerCourierWhenFastestCourierFound() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        Order order = createCreatedOrder(orderId, createLocation(10, 10), 5);

        Courier far = createCourierAt(1, 1);
        Courier near = createCourierAt(9, 10);

        // Act
        Result<Courier, Error> result = sut.dispatch(order, List.of(far, near));

        // Assert
        assertThat(result.isSuccess()).isTrue();
        Courier courier = result.getValue();
        assertThat(courier).isEqualTo(near);
        assertThat(courier.getStoragePlaces())
            .anyMatch(sp -> orderId.equals(sp.getOrderId()));
        assertThat(order.getCourierId()).isEqualTo(courier.getId());
    }

    private static Location createLocation(int x, int y) {
        return Location.create(x, y).getValue();
    }

    private static Order createCreatedOrder(UUID id, Location location, int volume) {
        return Order.create(id, location, volume).getValue();
    }

    private static Courier createCourierAt(int x, int y) {
        Location location = createLocation(x, y);
        return Courier.create("Ivan", Speed.create(1).getValue(), location).getValue();
    }
}