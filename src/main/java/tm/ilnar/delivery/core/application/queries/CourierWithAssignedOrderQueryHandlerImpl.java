package tm.ilnar.delivery.core.application.queries;

import libs.errs.Error;
import libs.errs.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourierWithAssignedOrderQueryHandlerImpl implements CourierWithAssignedOrderQueryHandler {

    private static final String SQL = """
                SELECT DISTINCT
                        c.id AS id,
                       c.name AS name,
                       c.location_x AS x,
                       c.location_y AS y
                FROM courier c
                    INNER JOIN storage_place sp ON c.id = sp.courier_id
                WHERE sp.order_id is not null
                """;

    private static final RowMapper<CourierWithAssignedOrderQueryResponse> ROW_MAPPER =
        (rs, rowNum) -> new CourierWithAssignedOrderQueryResponse(
            rs.getObject("id", java.util.UUID.class),
            rs.getString("name"),
            new LocationDto(
                rs.getInt("x"),
                rs.getInt("y")
            )
        );

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Result<List<CourierWithAssignedOrderQueryResponse>, Error> handle() {
        List<CourierWithAssignedOrderQueryResponse> result = jdbcTemplate.query(SQL, ROW_MAPPER);
        return Result.success(result);
    }
}
