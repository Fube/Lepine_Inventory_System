package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.UUID;

import static java.lang.String.format;

public class V5__Add_Roles extends BaseJavaMigration {

    private final List<String> roles = List.of(
            "MANAGER",
            "CLERK",
            "SALESPERSON"
    );

    @Override
    public void migrate(Context context) throws Exception {
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(
                new SingleConnectionDataSource(context.getConnection(), true));

        final String insertIntoRoles = "INSERT INTO lepine.roles (uuid, name) VALUES (?, ?)";
        PreparedStatement preparedStatement = jdbcTemplate.getDataSource().getConnection().prepareStatement(insertIntoRoles);
        for (String role : roles) {
            preparedStatement.setString(1, UUID.randomUUID().toString());
            preparedStatement.setString(2, role);
        }
    }
}