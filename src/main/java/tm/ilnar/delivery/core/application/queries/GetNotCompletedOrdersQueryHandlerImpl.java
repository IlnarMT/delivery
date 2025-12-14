package tm.ilnar.delivery.core.application.queries;

import libs.errs.Error;
import libs.errs.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import tm.ilnar.delivery.core.domain.model.order.OrderStatus;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetNotCompletedOrdersQueryHandlerImpl implements GetNotCompletedOrdersQueryHandler {

    private static final String SQL = """
                SELECT o.id AS id,
                       o.location_x AS x,
                       o.location_y AS y
                FROM orders o
                WHERE o.status IN (:statuses)
                """;

    private static final RowMapper<GetNotCompletedOrdersQueryResponse> ROW_MAPPER =
        (rs, rowNum) -> new GetNotCompletedOrdersQueryResponse(
            rs.getObject("id", java.util.UUID.class),
            new LocationDto(
                rs.getInt("x"),
                rs.getInt("y")
            )
        );

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Result<List<GetNotCompletedOrdersQueryResponse>, Error> handle() {

        List<String> statuses = List.of(
            OrderStatus.CREATED.getValue(), OrderStatus.ASSIGNED.getValue()
        );

        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("statuses", statuses);

        List<GetNotCompletedOrdersQueryResponse> result = jdbcTemplate.query(SQL, params, ROW_MAPPER);
        return Result.success(result);
    }
}
