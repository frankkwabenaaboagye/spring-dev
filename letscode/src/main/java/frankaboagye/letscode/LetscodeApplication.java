package frankaboagye.letscode;

import lombok.extern.slf4j.Slf4j;
import org.postgresql.Driver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

@Slf4j
public class LetscodeApplication {

    public static void main(String[] args) throws Exception {

        var dataSource = new DriverManagerDataSource(
                "jdbc:postgresql://localhost/postgres",
                "postgres",
                "password"

        ); // not an actual connection pool

        dataSource.setDriverClassName(Driver.class.getName());

        var template = new JdbcTemplate(dataSource);
        template.afterPropertiesSet();


        var customerService = new DefaultCustomerService(template);

        var allCustomers = customerService.allCustomers();

        allCustomers.forEach(
                customer -> log.info(customer.toString())
        );

    }

}


@Slf4j
class DefaultCustomerService {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Customer> customerRowMapper = (rs, rowNum) -> new Customer(rs.getInt("id"), rs.getString("name"));

    DefaultCustomerService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    // let return all the customer data

    Collection<Customer> allCustomers() throws Exception {

        return  this.jdbcTemplate.query("select * from customers", this.customerRowMapper);

    }
}

record Customer(Integer id, String name) {
}
