package ru.yandex.practicum.filmorate.storage;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NoDataException;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.ArrayList;
import java.util.List;

@Component
@Qualifier("MPADbStorage")
public class MPADbStorage implements MPAStorage {

    private final Logger log = LoggerFactory.getLogger(UserDbStorage.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MPADbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<MPA> getAllRatings() {
        List<MPA> filmRatings = new ArrayList<>();
        SqlRowSet ratingRows = jdbcTemplate.queryForRowSet("SELECT * FROM RATING ORDER BY ID");
        while (ratingRows.next()) {
            MPA mpa = makeMPA(ratingRows);
            filmRatings.add(mpa);
        }
        return filmRatings;
    }

    @Override
    public MPA getRatingById(Integer id) {
        SqlRowSet ratingRows = jdbcTemplate.queryForRowSet("SELECT * FROM RATING WHERE ID = ?", id);
        if (ratingRows.next()) {
            return makeMPA(ratingRows);
        } else
            throw new NoDataException("Не найден рейтинг с таким номером");
    }


    private MPA makeMPA(SqlRowSet rs) {
        Integer ratingId = rs.getInt("ID");
        String ratingName = rs.getString("NAME");
        return new MPA(ratingId, ratingName);
    }
}