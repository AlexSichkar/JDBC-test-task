package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import mate.jdbc.lib.Dao;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

public class ManufacturerDaoImpl implements ManufacturerDao {
    @Override
    public Manufacturer create(Manufacturer manufacturer) {
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement createStatement =
                         connection.prepareStatement(
                             "INSERT INTO manufacturers(name, country) values(?,?);",   //ToDo add query to variable
                             Statement.RETURN_GENERATED_KEYS)) {
            createStatement.setString(1, manufacturer.getName());
            createStatement.setString(2, manufacturer.getCountry());
            ResultSet generatedKeys = createStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getLong(1); //ToDo use getObject not getLong like in line 71
                manufacturer.setId(id);
            }
            createStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error");    //ToDo create custom exception and use informative message
        }
        return manufacturer;    //
    }

    @Override
    public Manufacturer get(Long id) {  //ToDo return Optional<Manufacturer>
        Manufacturer manufacturer = new Manufacturer();
        String getManufacturerRequest = "SELECT * FROM manufacturers WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getStatement =
                         connection.prepareStatement(getManufacturerRequest)) {
            getStatement.setLong(1, id);
            ResultSet resultSet = getStatement.executeQuery();
            if (resultSet.next()) {
                Long idOfManufacturer = resultSet.getLong("id");    //ToDo rename variable manufacturerId should be better
                String name = resultSet.getString("name");
                String country = resultSet.getString("country");
                manufacturer = new Manufacturer(idOfManufacturer, name, country);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error");    //ToDo use informative message
        }
        return manufacturer;
    }

    @Override
    public List<Manufacturer> getAll() throws RuntimeException {
        List<Manufacturer> manufacturerList = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 Statement getAllStatement = connection.createStatement()) {        //ToDo use PreparedStatement its faster than Statement
            ResultSet resultSet = getAllStatement
                    .executeQuery(
                        "SELECT manufacturers.id, manufacturers.name, manufacturers.country"    //ToDo add query to variable and use only column name(manufacturers.name --> name)
                        + "  FROM manufacturers WHERE is_deleted = 0"); //ToDo is_deleted better change to boolean
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String country = resultSet.getString("country");
                Long id = resultSet.getObject("id", Long.class);
                Manufacturer manufacturer = new Manufacturer(id, name, country);
                manufacturerList.add(manufacturer);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error");    //ToDo use informative message
        }
        return manufacturerList;
    }

    @Override
    public void delete(Long id) {   //ToDo return boolean
        String deleteRequest = "UPDATE manufacturers SET is_deleted = 1 where id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement deleteStatement =
                         connection.prepareStatement(
                                 deleteRequest, Statement.RETURN_GENERATED_KEYS)) { //ToDo use RETURN_GENERATED_KEYS only in create()
            deleteStatement.setLong(1, id);
            deleteStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error");    //ToDo use informative message
        }
    }

    @Override
    public Manufacturer update(Manufacturer manufacturer) {
        String updateRequest = "UPDATE manufacturers SET name = ?, country = ? WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement updateStatement =
                        connection.prepareStatement(
                                updateRequest, Statement.RETURN_GENERATED_KEYS)) {
            updateStatement.setString(1, manufacturer.getName());
            updateStatement.setString(2, manufacturer.getCountry());
            updateStatement.setLong(3, manufacturer.getId());
            updateStatement.executeUpdate();
            return new Manufacturer();  //ToDo append fields
        } catch (SQLException e) {
            throw new RuntimeException("Error");    //ToDo use informative message
        }
    }
}
