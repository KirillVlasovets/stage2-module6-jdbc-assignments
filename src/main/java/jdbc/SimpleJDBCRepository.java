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

    private Connection connection = null;
    private PreparedStatement ps = null;
    private Statement st = null;

    private static final String createUserSQL = "insert into myusers (firstname, lastname, age) values (?, ?, ?)";
    private static final String updateUserSQL = "update myusers set firstname = ?, lastname = ?, age = ? where id = ?";
    private static final String deleteUser = "delete from myusers where id = ?";
    private static final String findUserByIdSQL = "select * from myusers where id = ?";
    private static final String findUserByNameSQL = "select * from myusers where firstname = ?";
    private static final String findAllUserSQL = "select * from myusers";

    public Long createUser(User user) {
        try {
            connection = CustomDataSource.getInstance().getConnection();
            PreparedStatement createUserStatement = connection.prepareStatement(createUserSQL);
            createUserStatement.setString(1, user.getFirstName());
            createUserStatement.setString(2, user.getLastName());
            createUserStatement.setInt(3, user.getAge());
            int affectedRows = createUserStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException(String.format("User was not created. %d affected rows.", affectedRows));
            }
            ResultSet resultSet = createUserStatement.getGeneratedKeys();
            if (resultSet.next()) {
                return resultSet.getLong("id");
            } else {
                throw new SQLException("User was not created. Id was not generated.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User findUserById(Long userId) {
        try {
            connection = CustomDataSource.getInstance().getConnection();
            PreparedStatement createUserStatement = connection.prepareStatement(findUserByIdSQL);
            createUserStatement.setLong(1, userId);
            ResultSet resultSet = createUserStatement.executeQuery();
            if (resultSet.next()) {
                return buildUser(resultSet);
            } else {
                throw new SQLException(String.format("User with id = %d was not found.", userId));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User findUserByName(String userName) {
        try {
            connection = CustomDataSource.getInstance().getConnection();
            PreparedStatement createUserStatement = connection.prepareStatement(findUserByNameSQL);
            createUserStatement.setString(1, userName);
            ResultSet resultSet = createUserStatement.executeQuery();
            if (resultSet.next()) {
                return buildUser(resultSet);
            } else {
                throw new SQLException(String.format("User with id = %s was not found.", userName));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<User> findAllUser() {
        try {
            connection = CustomDataSource.getInstance().getConnection();
            PreparedStatement createUserStatement = connection.prepareStatement(findAllUserSQL);
            ResultSet resultSet = createUserStatement.executeQuery();
            List<User> users = new ArrayList<>();
            while (resultSet.next()) {
                users.add(buildUser(resultSet));
            }
            return users;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User updateUser(User user) {
        try {
            connection = CustomDataSource.getInstance().getConnection();
            PreparedStatement createUserStatement = connection.prepareStatement(updateUserSQL);
            createUserStatement.setString(1, user.getFirstName());
            createUserStatement.setString(2, user.getLastName());
            createUserStatement.setInt(3, user.getAge());
            createUserStatement.setLong(4, user.getId());
            int affectedRows = createUserStatement.executeUpdate();

            if (affectedRows == 1) {
                return findUserById(user.getId());
            } else {
                throw new SQLException(String.format("User was not updated. %d affected rows.", affectedRows));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteUser(Long userId) {
        try {
            connection = CustomDataSource.getInstance().getConnection();
            PreparedStatement createUserStatement = connection.prepareStatement(deleteUser);
            createUserStatement.setLong(1, userId);
            int affectedRows = createUserStatement.executeUpdate();

            if (affectedRows != 1 || findUserById(userId) != null) {
                throw new SQLException(String.format("User was not updated. %d affected rows.", affectedRows));
            }
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
