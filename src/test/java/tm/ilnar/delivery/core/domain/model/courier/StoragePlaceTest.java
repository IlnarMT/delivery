package tm.ilnar.delivery.core.domain.model.courier;

import libs.errs.Error;
import libs.errs.Result;
import libs.errs.UnitResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class StoragePlaceTest {

    static Stream<Arguments> invalidStoragePlaceCreateParams() {
        return Stream.of(
            Arguments.of(null, 10),
            Arguments.of(" ", 10),
            Arguments.of("bagpack", 0),
            Arguments.of("bagpack", -1)
        );
    }

    @Test
    void shouldBeCorrectWhenParametersAreCorrectOnCreated() {
        //Arrange

        //Act
        Result<StoragePlace, Error> result = StoragePlace.create("backpack ", 10);

        //Assert
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue().getId()).isNotNull();
        assertThat(result.getValue().getName()).isEqualTo("backpack");
        assertThat(result.getValue().getTotalVolume()).isEqualTo(10);
    }

    @ParameterizedTest
    @MethodSource("invalidStoragePlaceCreateParams")
    void shouldReturnErrorWhenParametersAreNotCorrectOnCreated(String name, int totalVolume) {
        //Arrange

        //Act
        Result<StoragePlace, Error> result = StoragePlace.create(name, totalVolume);

        //Assert
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError()).isNotNull();
    }

    @Test
    void shouldBeNotOccupiedWhenStoragePlaceCreated() {
        //Arrange

        //Act
        Result<StoragePlace, Error> result = StoragePlace.create("backpack ", 10);

        //Assert
        assertThat(result.getValue().isOccupied()).isFalse();
    }

    @Test
    void shouldBeOccupiedWhenOrderStoredOnStore() {
        //Arrange
        StoragePlace sut = StoragePlace.create("backpack ", 10).getValue();
        sut.store(UUID.randomUUID(), 9);

        //Act
        boolean result = sut.isOccupied();

        //Assert
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnSuccessWhenVolumeLessOrEqualTotalVolumeOnCanStore() {
        //Arrange
        StoragePlace sut = StoragePlace.create("backpack ", 10).getValue();

        //Act
        UnitResult<Error> result = sut.canStore(9);

        assertThat(result.isSuccess()).isTrue();
    }

    @Test
    void shouldReturnErrorWhenVolumeIsLessOrEqualZeroOnCanStore() {
        //Arrange
        StoragePlace sut = StoragePlace.create("backpack ", 10).getValue();

        //Act
        UnitResult<Error> result = sut.canStore(0);

        assertThat(result.isFailure()).isTrue();
    }

    @Test
    void shouldReturnErrorWhenVolumeIsGreaterThanTotalVolumeOnCanStore() {
        //Arrange
        StoragePlace sut = StoragePlace.create("backpack ", 10).getValue();

        //Act
        UnitResult<Error> result = sut.canStore(11);

        assertThat(result.isFailure()).isTrue();
    }

    @Test
    void shouldReturnErrorWhenStoragePlaceIsOccupiedOnCanStore() {
        //Arrange
        StoragePlace sut = StoragePlace.create("backpack ", 10).getValue();
        sut.store(UUID.randomUUID(), 9);

        //Act
        UnitResult<Error> result = sut.canStore(9);

        assertThat(result.isFailure()).isTrue();
    }

    @Test
    void shouldReturnSuccessAndSetOrderIdWhenParametersAreCorrectOnStore() {
        //Arrange
        StoragePlace sut = StoragePlace.create("backpack ", 10).getValue();
        UUID orderId = UUID.randomUUID();

        //Act
        UnitResult<Error> result = sut.store(orderId, 9);

        assertThat(result.isSuccess()).isTrue();
        assertThat(sut.getOrderId()).isEqualTo(orderId);
    }

    @Test
    void shouldReturnErrorAndNotChangeOrderIdWhenOrderIdIsNullOnStore() {
        //Arrange
        StoragePlace sut = StoragePlace.create("backpack ", 10).getValue();

        //Act
        UnitResult<Error> result = sut.store(null, 9);

        assertThat(result.isFailure()).isTrue();
        assertThat(sut.getOrderId()).isNull();
    }

    @Test
    void shouldClearOrderIdAndReturnSuccessWhenOrderIsStoredOnClear() {
        //Arrange
        StoragePlace sut = StoragePlace.create("backpack ", 10).getValue();
        sut.store(UUID.randomUUID(), 9);

        //Act
        UnitResult<Error> result = sut.clear();

        assertThat(result.isSuccess()).isTrue();
        assertThat(sut.getOrderId()).isNull();
    }
}