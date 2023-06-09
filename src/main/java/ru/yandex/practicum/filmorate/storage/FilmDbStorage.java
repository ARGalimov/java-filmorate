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
import ru.yandex.practicum.filmorate.exception.NoDataException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;

@Component
@Qualifier("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final Logger log = LoggerFactory.getLogger(FilmDbStorage.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film create(Film film) {
        validateFilm(film);
        String sqlQuery = "INSERT INTO FILMS (NAME, DESCRIPTION, RELEASE_DATE, DURATION, ID_RATING) " +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"ID"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        Integer filmId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        insertFilmGenre(filmId, getGenreIdList(film.getGenres()));
        log.info("Создан фильм с идентефикатором {}", filmId);
        return getById(filmId);
    }

    @Override
    public void delete(Film film) {
        deleteFromFilmLikes(film);
        deleteFromFilmGenre(film);
        deleteFromFilm(film);
        log.info("Удалён фильм с идентефикатором {}", film.getId());
    }

    @Override
    public Film update(Film film) {
        SqlRowSet userRS = jdbcTemplate.queryForRowSet("SELECT * FROM FILMS WHERE ID = ?", film.getId());
        if (userRS.next()) {
            String sqlQuery = "UPDATE FILMS set NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, " +
                    "ID_RATING = ? where ID = ?";
            jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                    film.getMpa().getId(), film.getId());
            updateFilmGenre(film);
            updateFilmUsersLikes(film);
            log.info("Изменён фильм {}", film.getId());
            return getById(film.getId());
        } else throw new NoDataException("Нет фильма с таким id");

    }

    @Override
    public List<Film> getFilmList() {
        List<Film> films = new ArrayList<>();
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * FROM FILMS");
        while (filmRows.next()) {
            Film film = makeFilm(filmRows);
            log.info("Найден фильм: {} {}", film.getId(), film.getName());
            films.add(film);
        }
        return films;
    }

    @Override
    public Film getById(Integer id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * FROM FILMS WHERE ID = ?", id);
        if (filmRows.next()) {
            Film film = makeFilm(filmRows);
            log.info("Найден фильм: {} {}", film.getId(), film.getName());
            return film;
        } else {
            log.info("Фильм с идентификатором {} не найден.", id);
            throw new NoDataException("Не найден фильм с таким номером");
        }
    }

    private void updateFilmUsersLikes(Film film) {
        deleteFromFilmLikes(film);
        String sqlQuery = "INSERT INTO FILM_LIKES (ID_FILM, ID_USER) VALUES (?, ?)";
        for (Integer userId : film.getLikes()) {
            jdbcTemplate.update(sqlQuery, film.getId(), userId);
        }
    }

    private void updateFilmGenre(Film film) {
        deleteFromFilmGenre(film);
        String sqlQuery = "INSERT INTO FILM_GENRE (ID_FILM, ID_GENRE) VALUES (?, ?)";
        Set<Genre> genres = new HashSet<>(film.getGenres());
        for (Genre genre : genres) {
            jdbcTemplate.update(sqlQuery, film.getId(), genre.getId());
        }
    }

    private void deleteFromFilm(Film film) {
        String sqlQuery = "DELETE FROM FILMS WHERE ID = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
    }

    private void deleteFromFilmGenre(Film film) {
        String sqlQuery = "DELETE FROM FILM_GENRE WHERE ID_FILM = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
    }

    private void deleteFromFilmLikes(Film film) {
        String sqlQuery = "DELETE FROM FILM_LIKES WHERE ID_FILM = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
    }

    private Film makeFilm(SqlRowSet rs) {
        Integer filmId = rs.getInt("ID");
        String name = rs.getString("NAME");
        String description = rs.getString("DESCRIPTION");
        LocalDate releaseDate = Objects.requireNonNull(rs.getDate("RELEASE_DATE")).toLocalDate();
        Integer duration = rs.getInt("DURATION");
        Set<Integer> likes = getLikes(filmId);
        List<Genre> genre = getGenres(filmId);
        MPA mpa = getMPA(rs.getInt("ID_RATING"));
        return new Film(filmId, name, description, releaseDate, duration, likes, genre, mpa);
    }

    private MPA makeMPA(SqlRowSet rs) {
        Integer ratingId = rs.getInt("ID");
        String ratingName = rs.getString("NAME");
        return new MPA(ratingId, ratingName);
    }

    private Genre makeGenre(SqlRowSet rs) {
        Integer genreId = rs.getInt("ID");
        String genreName = rs.getString("NAME");
        return new Genre(genreId, genreName);
    }

    private void insertFilmGenre(Integer filmId, Set<Integer> genreIdList) {
        String sqlQuery = "INSERT INTO FILM_GENRE (ID_FILM, ID_GENRE) VALUES (?, ?)";
        for (Integer id : genreIdList) {
            jdbcTemplate.update(sqlQuery, filmId, id);
        }
    }

    private Set<Integer> getLikes(Integer filmId) {
        String sql = "SELECT ID_USER FROM FILM_LIKES WHERE ID_FILM = ?";
        List<Integer> likes = jdbcTemplate.queryForList(sql, Integer.class, filmId);
        return new HashSet<>(likes);
    }

    private List<Genre> getGenres(Integer filmId) {
        SqlRowSet genreRows =
                jdbcTemplate.queryForRowSet("SELECT g.ID, g.NAME FROM FILM_GENRE AS f " +
                        "JOIN GENRE AS g ON g.ID = f.ID_GENRE" +
                        " WHERE ID_FILM = ?" +
                        " ORDER BY g.ID", filmId);
        List<Genre> genres = new ArrayList<>();
        while (genreRows.next()) {
            Genre genre = makeGenre(genreRows);
            genres.add(genre);
        }
        return genres;
    }

    private MPA getMPA(Integer id) {
        SqlRowSet ratingRows =
                jdbcTemplate.queryForRowSet("SELECT * FROM RATING WHERE ID = ?", id);
        if (ratingRows.next()) {
            return makeMPA(ratingRows);
        } else
            throw new NoDataException("Не найден рейтинг с таким номером");
    }

    private Set<Integer> getGenreIdList(List<Genre> genreList) {
        Set<Integer> genreIdList = new HashSet<>();
        if (Objects.isNull(genreList)) {
            return genreIdList;
        } else if (genreList.size() != 0) {
            for (Genre genre : genreList) {
                genreIdList.add(genre.getId());
            }
        }
        return genreIdList;
    }

    private void validateFilm(Film film) throws ValidationException {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Название не может быть пустым!");
            throw new ValidationException("Название не может быть пустым!");
        }

        if (film.getDescription() != null && film.getDescription().length() > 200) {
            log.error("Максимальная длина описания — 200 символов!");
            throw new ValidationException("Максимальная длина описания — 200 символов!");
        }

        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            log.error("Дата релиза — не раньше 28 декабря 1895 года!");
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года!");
        }

        if (film.getDuration() != null && film.getDuration() <= 0) {
            log.error("Продолжительность фильма должна быть положительной!");
            throw new ValidationException("Продолжительность фильма должна быть положительной!");
        }
    }
}