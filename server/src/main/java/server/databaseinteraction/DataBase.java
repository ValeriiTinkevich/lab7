package server.databaseinteraction;

import common.data.User;
import common.exceptions.DataBaseNotUpdatedException;
import common.exceptions.UserNotFoundException;

import java.sql.*;
import java.util.Properties;

import static server.App.logger;

public class DataBase {
    private Connection connection;

    public DataBase(String login, String password) throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://localhost/studs";
            Properties props = new Properties();
            props.setProperty("user", "postgres");
            props.setProperty("password", "admin123");
            props.setProperty("ssl", "false");
            connection = DriverManager.getConnection(url, props);
            logger.severe("Connection established");
        } catch (ClassNotFoundException e) {
            logger.severe("Problem with DB driver");
        }
    }

    public int insert (User user) throws SQLException, DataBaseNotUpdatedException {
        PreparedStatement preparedStatement =
                connection.prepareStatement("insert into users (username, password) values (?, ?) returning id");
                preparedStatement.setString(1, user.getUsername());
                preparedStatement.setString(2, user.getPassword());

                try {
                    if (preparedStatement.execute()) {
                        ResultSet rs = preparedStatement.getResultSet();
                        if (rs.next()) {
                            return rs.getInt("id");
                        }
                    }
                    throw new DataBaseNotUpdatedException("The user object was not added");
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                    throw new DataBaseNotUpdatedException("The user object was not added");
                }
    }

    public int selectUserID(String login, String password) throws SQLException, UserNotFoundException {
        PreparedStatement preparedStatement =
                connection.prepareStatement("select * from users where username=? and password=?");
        preparedStatement.setString(1, login);
        preparedStatement.setString(2, password);
        if (preparedStatement.execute()) {
            ResultSet rs = preparedStatement.getResultSet();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        throw new UserNotFoundException("User not found");
    }

    public User selectUser(int id) throws SQLException, UserNotFoundException {
        PreparedStatement preparedStatement = connection.prepareStatement("select * from users where id=?");
        preparedStatement.setInt(1, id);
        if (preparedStatement.execute()) {
            ResultSet rs = preparedStatement.getResultSet();
            if (rs.next()) {
                String login = rs.getString("login");
                String password = rs.getString("password");
                return new User(login, password);
            }
        }

        throw new UserNotFoundException("No пользователя with id = " + id);
    }
}
