package jdbc;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimpleJDBCRepository {

    private PreparedStatement ps = null;
    private Statement st = null;

    private static final String CREATE_USER_SQL = "insert into myusers (firstname, lastname, age) values (?, ?, ?);";
    private static final String UPDATE_USER_SQL = "update myusers set firstname = ?, lastname = ?, age = ? where id = ?;";
    private static final String DELETE_USER = "delete from myusers where id = ?;";
    private static final String FIND_USER_BY_ID_SQL = "select * from myusers where id = ?;";
    private static final String FIND_USER_BY_NAME_SQL = "select * from myusers where firstname = ?;";
    private static final String FIND_ALL_USER_SQL = "select * from myusers;";

    public Long createUser(User user) {
        long id = 1L;
        try (Connection connection = CustomDataSource.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(CREATE_USER_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setObject(1, user.getFirstName());
            statement.setObject(2, user.getLastName());
            statement.setObject(3, user.getAge());
            statement.execute();
            ResultSet generatedKeys = statement.getGeneratedKeys();

            if (generatedKeys.next()) {
                id = generatedKeys.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return id;
    }

    public User findUserById(Long userId) {
        User user = null;
        try (Connection connection = CustomDataSource.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_USER_BY_ID_SQL)) {
            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                user = buildUser(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    public User findUserByName(String userName) {
        User user = null;
        try (Connection connection = CustomDataSource.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_USER_BY_NAME_SQL)) {
            statement.setString(1, userName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                user = buildUser(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    public List<User> findAllUser() {
        List<User> users = new ArrayList<>();
        try (Connection connection = CustomDataSource.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_USER_SQL)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                users.add(buildUser(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }

    public User updateUser(User user) {
        try (Connection connection = CustomDataSource.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_USER_SQL)) {
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setInt(3, user.getAge());
            statement.setLong(4, user.getId());
            int affectedRows = statement.executeUpdate();

            if (affectedRows == 1) {
                return findUserById(user.getId());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void deleteUser(Long userId) {
        try (Connection connection = CustomDataSource.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_USER)) {
            statement.setLong(1, userId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private User buildUser(ResultSet set) {
        try {
            return User.builder()
                    .id(set.getLong("id"))
                    .firstName(set.getString("firstname"))
                    .lastName(set.getString("lastname"))
                    .age(set.getInt("age"))
                    .build();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
