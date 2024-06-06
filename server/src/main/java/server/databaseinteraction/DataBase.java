package server.databaseinteraction;

import common.data.*;
import common.exceptions.DataBaseNotUpdatedException;
import common.exceptions.UserNotFoundException;
import server.utility.ResponseOutputter;

import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.Properties;

import static server.App.logger;

public class DataBase {
    private Connection connection;

    public DataBase(String login, String password) throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://localhost/studs";
            Properties props = new Properties();
            props.setProperty("user", login);
            props.setProperty("password", password);
            props.setProperty("ssl", "false");
            connection = DriverManager.getConnection(url, props);
            logger.severe("Connection established");
        } catch (ClassNotFoundException e) {
            logger.severe("Problem with DB driver");
        }
    }

    public int insert(User user) throws SQLException, DataBaseNotUpdatedException {
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

    public int insert(Coordinates coords) throws SQLException, DataBaseNotUpdatedException {
        String query = "insert into coordinates (x, y) values (?, ?) returning coordinates_id";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setFloat(1, coords.getX());
        preparedStatement.setDouble(2, coords.getY());

        try {
            if (preparedStatement.execute()) {
                ResultSet rs = preparedStatement.getResultSet();
                if (rs.next()) {
                    return rs.getInt("coordinates_id");
                }
            }
            throw new DataBaseNotUpdatedException("The coordinates object was not added");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new DataBaseNotUpdatedException("The coordinates object was not added");
        }
    }

    public int removeById(int id) throws SQLException, DataBaseNotUpdatedException {
        String query = "delete from spacemarine where spacemarine_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, id);
        try {
            if(preparedStatement.execute()) {
                return preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.print(e.getMessage());
            throw new DataBaseNotUpdatedException("Error while deleting object");
        }
        throw new DataBaseNotUpdatedException("Id is invalid or couldn't delete the object");
    }

    public int removeByHeight(long height) throws SQLException, DataBaseNotUpdatedException {
        String query = "delete from spacemarine where height > ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setLong(1, height);
        try {
            if(preparedStatement.execute()) {
                return preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.print(e.getMessage());
            throw new DataBaseNotUpdatedException("Error while deleting object");
        }
        throw new DataBaseNotUpdatedException("Id is invalid or couldn't delete the object");
    }

    //public int removeGreaterThan

    public int insert(Chapter chapter) throws SQLException, DataBaseNotUpdatedException {
        String query = "insert into chapter (chapter_name, world) values (?, ?) returning chapter_id";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, chapter.getName());
        preparedStatement.setString(2, chapter.getWorld());

        try {
            if (preparedStatement.execute()) {
                ResultSet rs = preparedStatement.getResultSet();
                if (rs.next()) {
                    return rs.getInt("chapter_id");
                }
            }
            throw new DataBaseNotUpdatedException("The chapter object was not added");
        } catch (SQLException e) {
            System.out.print(e.getMessage());
            throw new DataBaseNotUpdatedException("The chapter object was not added");
        }
    }

    public int insert(SpaceMarine spaceMarine, int userID) throws SQLException, DataBaseNotUpdatedException {
        int coordsID = insert(spaceMarine.getCoordinates());
        int chapterID = insert(spaceMarine.getChapter());
        String query = "insert into spacemarine (name, coordinates, creationdate, health, heartcount, height," +
                " meleeweapon, chapter, owner) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?) returning spacemarine_id";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, spaceMarine.getName());
        preparedStatement.setInt(2, coordsID);
        preparedStatement.setTimestamp(3, Timestamp.valueOf(spaceMarine.getCreationDate().toLocalDateTime()));
        preparedStatement.setInt(4, spaceMarine.getHealth());
        preparedStatement.setLong(5, spaceMarine.getHeartCount());
        preparedStatement.setLong(6, spaceMarine.getHeight());
        preparedStatement.setString(7, spaceMarine.getMeleeWeapon().toString());
        preparedStatement.setInt(8, chapterID);
        preparedStatement.setInt(9, userID);
        try {
            if (preparedStatement.execute()) {
                ResultSet rs = preparedStatement.getResultSet();
                if (rs.next()) {
                    return rs.getInt("spacemarine_id");
                }
            }
            throw new DataBaseNotUpdatedException("The Organization object was not added");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            throw new DataBaseNotUpdatedException("The Organization object was not added");
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
        throw new UserNotFoundException("No user with id = " + id);
    }

    public Chapter selectChapter(int id) throws SQLException, UserNotFoundException {
        String query = "select * from chapter where chapter_id = " +
                "(select chapter from spacemarine where spacemarine_id=?)";
        PreparedStatement preparedStatement = connection
                .prepareStatement(query);
        preparedStatement.setInt(1, id);
        if (preparedStatement.execute()) {
            ResultSet rs = preparedStatement.getResultSet();
            if (rs.next()) {
                String chapter_name = rs.getString("chapter_name");
                String world = rs.getString("world");
                return new Chapter(chapter_name, world);
            }
        }
        throw new UserNotFoundException("No chapter with id = " + id);
    }

    public Coordinates selectCoordinates(int id) throws SQLException, UserNotFoundException {
        PreparedStatement preparedStatement = connection.prepareStatement("select * from coordinates where coordinates_id=?");
        preparedStatement.setInt(1, id);
        if (preparedStatement.execute()) {
            ResultSet rs = preparedStatement.getResultSet();
            if (rs.next()) {
                float x = rs.getFloat("x");
                double y = rs.getDouble("y");
                return new Coordinates(x, y);
            }
        }
        throw new UserNotFoundException("No chapter with id = " + id);
    }

    public int selectOwner(long id) throws SQLException, UserNotFoundException {
        String query = "select owner from spacemarine where spacemarine_id=?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setLong(1, id);
        if (preparedStatement.execute()) {
            ResultSet rs = preparedStatement.getResultSet();
            if (rs.next()) {
                return rs.getInt("owner");
            }
        }
        throw new UserNotFoundException("No owner with id = " + id);
    }

    public void updateCoordinates(long id, Coordinates coordinates) throws SQLException {
        String query = "update coordinates set x= "+ coordinates.getX() + ", y=" + coordinates.getY() + " where coordinates_id= " +
                "(select coordinates from spacemarine where spacemarine_id=?);";
        ResponseOutputter.appendLn(query);
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setLong(1, id);
        preparedStatement.execute();
    }

    public void updateChapter(long id, Chapter chapter) throws SQLException {
        String name = chapter.getName();
        String world = chapter.getWorld();
        String query = "update chapter set chapter_name='" + name +
                "', world='" + world + "' where chapter_id = (select chapter from spacemarine where spacemarine_id=?);";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setLong(1, id);
        preparedStatement.execute();
    }


    public void updateSpaceMarine(long id, SpaceMarine spaceMarine) throws SQLException {
        updateChapter(id, spaceMarine.getChapter());
        updateCoordinates(id, spaceMarine.getCoordinates());
        String name = spaceMarine.getName();
        String meleeweapon = spaceMarine.getMeleeWeapon().toString();
        String query = "update spacemarine set name='" + name +"', " +
                "health=?, heartcount=?, height=?, meleeweapon='" + meleeweapon +"' where spacemarine_id=?";
        ResponseOutputter.appendLn(query);
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, spaceMarine.getHealth());
        preparedStatement.setLong(2, spaceMarine.getHeartCount());
        preparedStatement.setLong(3, spaceMarine.getHeight());
        preparedStatement.setLong(4, id);
        preparedStatement.execute();
    }

    public SpaceMarine SelectSpacemarine(long id) throws SQLException, DataBaseNotUpdatedException {
        String query = "select * from spacemarine inner " +
                "join coordinates on coordinates.coordinates_id = spacemarine.coordinates " +
                "join chapter on chapter.chapter_id = spacemarine.chapter where spacemarine_id=?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setLong(1, id);
        if(preparedStatement.execute()) {
            ResultSet rs = preparedStatement.getResultSet();
            if (rs.next()) {
                String name = rs.getString("name");
                Coordinates coordinates = new Coordinates(rs.getFloat("x"), rs.getDouble("y"));
                ZonedDateTime time = rs.getTimestamp("creationdate")
                        .toInstant()
                        .atZone(ZoneId.of("Europe/Moscow"));
                Integer health = rs.getInt("health");
                long heartCount = rs.getLong("heartcount");
                long height = rs.getLong("height");
                int userID = rs.getInt("owner");
                MeleeWeapon meleeweapon = MeleeWeapon.valueOf(rs.getString("meleeweapon"));
                Chapter chapter = new Chapter(rs.getString("chapter_name"), rs.getString("world"));
                SpaceMarine spaceMarine = new SpaceMarine(id,
                        name,
                        coordinates,
                        time,
                        health,
                        heartCount,
                        height,
                        meleeweapon,
                        chapter, userID);
                return spaceMarine;
            }
        }
        throw new DataBaseNotUpdatedException("Error while updating collection");
    }


    public String selectUsername(int id) throws SQLException, UserNotFoundException {
        String query = "select username from users where id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, id);
        if(preparedStatement.execute()) {
            ResultSet rs = preparedStatement.getResultSet();
            if(rs.next()) {
                return rs.getString("username");
            }
        }
        throw new UserNotFoundException("No owner with id = " + id);
    }


    public LinkedList<SpaceMarine> selectCollection() throws SQLException, DataBaseNotUpdatedException {
        String query = "select * from spacemarine inner " +
                "join coordinates on coordinates.coordinates_id = spacemarine.coordinates " +
                "join chapter on chapter.chapter_id = spacemarine.chapter";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        if(preparedStatement.execute()) {
            ResultSet rs = preparedStatement.getResultSet();
            LinkedList<SpaceMarine> collection = new LinkedList<>();
            while (rs.next()) {
                long id = rs.getLong("spacemarine_id");
                String name = rs.getString("name");
                Coordinates coordinates = new Coordinates(rs.getFloat("x"), rs.getDouble("y"));
                ZonedDateTime time = rs.getTimestamp("creationdate")
                        .toInstant()
                        .atZone(ZoneId.of("Europe/Moscow"));
                Integer health = rs.getInt("health");
                long heartCount = rs.getLong("heartcount");
                long height = rs.getLong("height");
                int userID = rs.getInt("owner");
                MeleeWeapon meleeweapon = MeleeWeapon.valueOf(rs.getString("meleeweapon"));
                Chapter chapter = new Chapter(rs.getString("chapter_name"), rs.getString("world"));
                SpaceMarine spaceMarine = new SpaceMarine(id,
                        name,
                        coordinates,
                        time,
                        health,
                        heartCount,
                        height,
                        meleeweapon,
                        chapter, userID);
                collection.add(spaceMarine);

            }
            return collection;
        }
        throw new DataBaseNotUpdatedException("Error while updating collection");
    }


}
