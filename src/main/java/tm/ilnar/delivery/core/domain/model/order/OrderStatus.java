package tm.ilnar.delivery.core.domain.model.order;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum OrderStatus {

    CREATED("CREATED"),
    ASSIGNED("ASSIGNED"),
    COMPLETED("COMPLETED");

    private final String value;

    public static OrderStatus fromValue(String value) {
        return OrderStatus.valueOf(value.toUpperCase());
    }

    public String toValue() {
        return value.toLowerCase();
    }
}
