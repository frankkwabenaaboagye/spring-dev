package frankaboagye.letscode;

import lombok.extern.slf4j.Slf4j;
import org.postgresql.Driver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
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

    DefaultCustomerService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    // let return all the customer data

    Collection<Customer> allCustomers() throws Exception {

        var listOfCustomers = new ArrayList<Customer>();

        try {
            try (var connection = this.dataSource.getConnection()) {

                try (var stmt = connection.createStatement()) {

                    try (var resultSet = stmt.executeQuery("select * from customers");) {

                        while (resultSet.next()) {
                            var name = resultSet.getString("name");
                            var id = resultSet.getInt("id");
                            listOfCustomers.add(new Customer(id, name));

                        }

                    }

                }
            }
        } catch (Exception e) {
            log.error("Something went wrong - figure out", e);

        }

        return listOfCustomers;


    }
}

record Customer(Integer id, String name) {
}
