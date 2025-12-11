package tm.ilnar.delivery.adapters.out.postgres.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import tm.ilnar.delivery.core.domain.model.order.OrderStatus;

@Converter(autoApply = true)
public class OrderStatusConverter implements AttributeConverter<OrderStatus, String> {

    @Override
    public String convertToDatabaseColumn(OrderStatus attribute) {
        if (attribute == null) {
            throw new IllegalStateException("OrderStatus is null");
        }
        return attribute.getValue();
    }

    @Override
    public OrderStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            throw new IllegalStateException("OrderStatus value is null");
        }
        return OrderStatus.fromValue(dbData);
    }
}
