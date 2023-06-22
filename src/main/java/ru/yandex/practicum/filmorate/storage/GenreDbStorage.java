package ru.yandex.practicum.filmorate.storage;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NoDataException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.List;

@Component
@Qualifier("GenreDbStorage")
public class GenreDbStorage implements GenreStorage {

    private final Logger log = LoggerFactory.getLogger(UserDbStorage.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> getAllGenres() {
        List<Genre> filmGenres = new ArrayList<>();
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("SELECT * FROM GENRE ORDER BY ID");
        while (genreRows.next()) {
            Genre genre = makeGenre(genreRows);
            filmGenres.add(genre);
        }
        return filmGenres;
    }

    @Override
    public Genre getGenreById(Integer id) {
        SqlRowSet genreRows =
                jdbcTemplate.queryForRowSet("SELECT * FROM GENRE WHERE ID = ? ORDER BY ID", id);
        if (genreRows.next()) {
            return makeGenre(genreRows);
        } else
            throw new NoDataException("Не найден жанр с таким номером");
    }

    private Genre makeGenre(SqlRowSet rs) {
        Integer genreId = rs.getInt("ID");
        String genreName = rs.getString("NAME");
        return new Genre(genreId, genreName);
    }
}