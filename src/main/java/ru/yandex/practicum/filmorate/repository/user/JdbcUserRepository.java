package ru.yandex.practicum.filmorate.repository.user;

import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.SaveDataException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.BaseJdbcRepository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@Primary
public class JdbcUserRepository extends BaseJdbcRepository<User> implements UserRepository {
    public JdbcUserRepository(NamedParameterJdbcOperations jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public User create(User user) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        String sqlQuery = """
                INSERT INTO USERS (EMAIL, LOGIN, NAME, BIRTHDAY)
                VALUES (:email, :login, :name, :birthday);
                """;
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("email", user.getEmail())
                .addValue("login", user.getLogin())
                .addValue("name", user.getName())
                .addValue("birthday", user.getBirthday());
        jdbc.update(sqlQuery, params, keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);

        if (id != null) {
            user.setId(id);
        } else {
            throw new SaveDataException("Не удалось сохранить данные:" + user);
        }

        return user;
    }

    @Override
    public User update(User user) {
        String sqlQuery = """
                UPDATE USERS
                SET EMAIL = :email,
                    LOGIN = :login,
                    NAME = :name,
                    BIRTHDAY = :birthday
                WHERE USER_ID = :userId;
                """;
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", user.getId())
                .addValue("email", user.getEmail())
                .addValue("login", user.getLogin())
                .addValue("name", user.getName())
                .addValue("birthday", user.getBirthday());
        jdbc.update(sqlQuery, params);
        return user;
    }

    @Override
    public Collection<User> getAll() {
        String sqlQuery = "SELECT * FROM USERS;";
        List<User> users = jdbc.query(sqlQuery, mapper);
        return users;
    }

    @Override
    public Optional<User> getById(Long id) {
        try {
            String sqlQuery = """
                    SELECT USER_ID, EMAIL, LOGIN, NAME, BIRTHDAY
                    FROM USERS
                    WHERE USER_ID = :userId;
                    """;
            User user = jdbc.queryForObject(sqlQuery, Map.of("userId", id), mapper);
            return Optional.ofNullable(user);
        } catch (NullPointerException | EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public void delete(long userId) {
        String deleteUserQuery = "DELETE FROM USERS WHERE USER_ID = :userId;";
        jdbc.update(deleteUserQuery, Map.of("userId", userId));
    }

    @Override
    public void addFriend(long userId, long friendId) {
        String sqlQuery = """
                INSERT INTO FRIENDSHIP (USER_ID, FRIEND_ID, IS_MUTUAL)
                VALUES (:userId, :friendId, false);
                """;
        jdbc.update(sqlQuery, Map.of("userId", userId, "friendId", friendId));

        boolean isExists = checkFriendship(userId, friendId);

        if (isExists) {
            String updateMutualQuery = """
                    UPDATE FRIENDSHIP
                    SET IS_MUTUAL = TRUE
                    WHERE (USER_ID, FRIEND_ID) IN ((:userId, :friendId), (:friendId, :userId));
                    """;
            jdbc.update(updateMutualQuery, Map.of("userId", userId, "friendId", friendId));
        }
    }

    private Boolean checkFriendship(long userId, long friendId) {
        String checkMutualQuery = """
                SELECT EXISTS(
                        SELECT 1
                        FROM FRIENDSHIP
                        WHERE USER_ID = :userId
                        AND FRIEND_ID = :friendId);
                """;
        return jdbc.queryForObject(checkMutualQuery,
                Map.of("userId", friendId, "friendId", userId),
                Boolean.class);
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        String removeFriendship = """
                DELETE FROM FRIENDSHIP
                WHERE USER_ID = :userId
                AND FRIEND_ID = :friendId;
                """;
        int countUpdateRow = jdbc.update(removeFriendship, Map.of("userId", userId, "friendId", friendId));

        if (countUpdateRow != 0) {
            String updateMutualQuery = """
                    UPDATE FRIENDSHIP
                    SET IS_MUTUAL = FALSE
                    WHERE USER_ID = :userId
                    AND FRIEND_ID = :friendId;
                    """;
            jdbc.update(updateMutualQuery, Map.of("userId", friendId, "friendId", userId));
        }
    }

    @Override
    public Collection<User> getFriends(long userId) {
        String sqlQuery = """
                SELECT *
                FROM USERS
                WHERE USER_ID IN (
                        SELECT FRIEND_ID
                        FROM FRIENDSHIP
                        WHERE USER_ID = :userId);
                """;
        List<User> friends = jdbc.query(sqlQuery, Map.of("userId", userId), mapper);
        return friends;
    }

    @Override
    public Collection<User> getCommonFriends(long userId, long otherId) {
        Collection<User> userFriends = getFriends(userId);
        Collection<User> friendFriends = getFriends(otherId);

        userFriends.retainAll(friendFriends);
        return userFriends;
    }
}
