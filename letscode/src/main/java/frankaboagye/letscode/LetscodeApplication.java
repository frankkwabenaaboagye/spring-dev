package frankaboagye.letscode;

import lombok.extern.slf4j.Slf4j;
import org.postgresql.Driver;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

@Slf4j
public class LetscodeApplication {

    public static void main(String[] args) throws Exception {
        var customerService = new DefaultCustomerService();

        var allCustomers = customerService.allCustomers();

        allCustomers.forEach(
                customer -> log.info(customer.toString())
        );

    }

}


@Slf4j
class DefaultCustomerService {

    private final DataSource dataSource;

    DefaultCustomerService() {

        var dataSource = new DriverManagerDataSource(
                "jdbc:postgresql://localhost/postgres",
                "postgres",
                "password"

        ); // not an actual connection pool

        dataSource.setDriverClassName(Driver.class.getName());

        this.dataSource = dataSource;
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
