package tm.ilnar.delivery.core.domain.model.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {

    CREATED("created"),
    ASSIGNED("assigned"),
    COMPLETED("completed");

    private final String value;

    public static OrderStatus fromValue(String value) {
        return OrderStatus.valueOf(value);
    }
}
