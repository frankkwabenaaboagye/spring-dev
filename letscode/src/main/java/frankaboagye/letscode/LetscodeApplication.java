package frankaboagye.letscode;

import lombok.extern.slf4j.Slf4j;
import org.postgresql.Driver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

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

        // add customer
        var henry = customerService.addCustomer("Henry");
        var chad = customerService.addCustomer("Chad");


        var allCustomers = customerService.allCustomers();

        Assert.state(allCustomers.contains(henry) && allCustomers.contains(chad), "Henry and Chad not added successfully");

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

        return this.jdbcTemplate.query("select id, name from customers", this.customerRowMapper);
    }

    // let create customer
    Customer addCustomer(String name) {
        // we want the id
        var al = new ArrayList<Map<String, Object>>();
        var hm = new HashMap<String, Object>();
        hm.put("id", Long.class);
        al.add(hm);

        var keyHolder = new GeneratedKeyHolder(al);

        this.jdbcTemplate.update(
                con -> {
                    var ps = con.prepareStatement(
                            "insert into customers (name) values ( ? )",
                            Statement.RETURN_GENERATED_KEYS
                    );
                    ps.setString(1, name);
                    return ps;
                },
                keyHolder
        );
        var generatedId = keyHolder.getKeys().get("id");
        log.info("generated id is {}", generatedId.toString());
        if (generatedId instanceof Number number) {
            return findCustomerByTheId(number.intValue());
        }
        return null;
    }


    // find a particular customer
    Customer findCustomerByTheId(Integer id) {
        return this.jdbcTemplate.queryForObject(
                "select id, name from customers where id=?",
                this.customerRowMapper,
                id
        );
    }
}

record Customer(Integer id, String name) {
}
