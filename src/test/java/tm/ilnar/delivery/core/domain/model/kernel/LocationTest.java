package tm.ilnar.delivery.core.domain.model.kernel;

import libs.errs.Error;
import libs.errs.Result;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class LocationTest {

    @Test
    void shouldBeCorrectWhenParametersAreCorrectOnCreated() {
        //Arrange

        //Act
        Result<Location, Error> result = Location.create(1, 2);

        //Assert
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue().getX()).isEqualTo(1);
        assertThat(result.getValue().getY()).isEqualTo(2);
    }

    @ParameterizedTest
    @CsvSource({
        "-1, -1",
        "-1, 1",
        "1, -1",
        "0, 0",
        "0, 5",
        "5, 0",
        "5, 11",
        "11, 5",
        "11, 11"
    })
    void shouldReturnErrorWhenParametersAreNotCorrectOnCreated(int x, int y) {
        //Arrange

        //Act
        Result<Location, Error> result = Location.create(x, y);

        //Assert
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError()).isNotNull();
    }

    @ParameterizedTest
    @CsvSource({
            "5, 5, 0",
            "9, 3, 6",
            "2, 9, 7"
        }
    )
    void shouldCalculateDistanceTo(int x , int y, int expectedDistance) {
        //Arrange
        var first = Location.create(5, 5).getValue();
        var second = Location.create(x, y).getValue();

        //Act
        int result = first.distanceTo(second);

        //Assert
        assertThat(result).isEqualTo(expectedDistance);
    }

    @Test
    void shouldBeEqualWhenAllPropertiesAreEqual() {
        //Arrange
        var first = Location.create(2, 4).getValue();
        var second = Location.create(2, 4).getValue();

        //Act
        boolean result = first.equals(second);

        //Assert
        assertThat(result).isTrue();
    }

    @Test
    void shouldNotBeEqualWhenAllPropertiesAreEqual() {
        //Arrange
        Result<Location, Error> first = Location.create(2, 4);
        Result<Location, Error> second = Location.create(3, 4);

        //Act
        boolean result = first.equals(second);

        //Assert
        assertThat(result).isFalse();
    }
}