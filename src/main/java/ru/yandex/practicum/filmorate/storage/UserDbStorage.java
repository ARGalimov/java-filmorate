package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exception.NoDataException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.*;

@Component
@Qualifier("UserDbStorage")
public class UserDbStorage implements UserStorage {
    private final Logger log = LoggerFactory.getLogger(UserDbStorage.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User create(User user) {
        validateUser(user);
        String sqlQuery = "INSERT INTO USERS (EMAIL, LOGIN, NAME, BIRTHDAY) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"ID"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        Integer userId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        log.info("Создан пользователь с идентефикатором {}", userId);
        return getById(userId);
    }

    @Override
    public User update(User user) {
        SqlRowSet userRS = jdbcTemplate.queryForRowSet("SELECT * FROM USERS WHERE ID = ?", user.getId());
        if (userRS.next()) {
            String sqlQuery = "UPDATE USERS set EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ? where ID = ?";
            jdbcTemplate.update(sqlQuery, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
            updateFriends(user);
            log.info("Изменён пользователь с идентефикатором {}", user.getId());
            return user;
        } else throw new NoDataException("Нет пользователя с таким id");
    }

    @Override
    public void delete(User user) {
        deleteFromFilmLikes(user);
        deleteFromFriends(user);
        deleteFromUsers(user);
        log.info("Удалён пользователь с идентефикатором {}", user.getId());
    }

    @Override
    public User getById(Integer id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM USERS WHERE ID = ?", id);
        if (userRows.next()) {
            User user = makeUser(userRows);
            log.info("Найден пользователь: {} {}", user.getId(), user.getName());
            return user;
        } else {
            log.info("Пользователь с идентификатором {} не найден.", id);
            throw new NoDataException("Нет пользователя с таким id");
        }
    }

    @Override
    public List<User> getUserList() {
        List<User> users = new ArrayList<>();
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM USERS");
        while (userRows.next()) {
            User user = makeUser(userRows);
            log.info("Найден пользователь: {} {}", user.getId(), user.getName());
            users.add(user);
        }
        return users;
    }

    private User makeUser(SqlRowSet rs) {
        Integer userId = rs.getInt("ID");
        String email = rs.getString("EMAIL");
        String login = rs.getString("LOGIN");
        String name = rs.getString("NAME");
        LocalDate birthday = Objects.requireNonNull(rs.getDate("BIRTHDAY")).toLocalDate();
        HashMap<Integer, Boolean> friends = getFriends(userId);
        return new User(userId, email, login, name, birthday, friends);
    }

    private void updateFriends(User user) {
        deleteFromFriends(user);
        String sqlQuery = "INSERT INTO FRIENDS (ID_USER, ID_FRIEND, IS_APPROVE) VALUES (?, ?, ?)";
        for (Map.Entry<Integer, Boolean> entry : user.getFriends().entrySet()) {
            jdbcTemplate.update(sqlQuery, user.getId(), entry.getKey(), entry.getValue());
        }
    }

    private HashMap<Integer, Boolean> getFriends(Integer id) {
        SqlRowSet friendsRows1 = jdbcTemplate.queryForRowSet("SELECT ID_FRIEND, IS_APPROVE FROM FRIENDS " +
                "WHERE ID_USER = ?", id);
        HashMap<Integer, Boolean> friends = new HashMap<>(getMapOfFriends(friendsRows1));
        return friends;
    }

    private HashMap<Integer, Boolean> getMapOfFriends(SqlRowSet rs) {
        HashMap<Integer, Boolean> friends = new HashMap<>();
        while (rs.next()) {
            Integer userId = rs.getInt(1);
            Boolean isApprove = rs.getBoolean(2);
            friends.put(userId, isApprove);
        }
        return friends;
    }

    private void deleteFromUsers(User user) {
        String sqlQuery = "DELETE FROM USERS WHERE ID = ?";
        jdbcTemplate.update(sqlQuery, user.getId());
    }

    private void deleteFromFriends(User user) {
        String sqlQuery = "DELETE FROM FRIENDS WHERE ID_USER = ? OR ID_FRIEND = ?";
        jdbcTemplate.update(sqlQuery, user.getId(), user.getId());
    }

    private void deleteFromFilmLikes(User user) {
        String sqlQuery = "DELETE FROM FILM_LIKES WHERE ID_USER = ?";
        jdbcTemplate.update(sqlQuery, user.getId());
    }

    private void validateUser(User user) throws ValidationException {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("Электронная почта не может быть пустой и должна содержать символ @!");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @!");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || StringUtils.containsWhitespace(user.getLogin())) {
            log.error("Логин не может быть пустым и содержать пробелы!");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы!");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.warn("Имя для отображения может быть пустым — в таком случае будет использован логин!");
            user.setName(user.getLogin());
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения не может быть в будущем!");
            throw new ValidationException("Дата рождения не может быть в будущем!");
        }
    }
}