package ru.yandex.practicum.filmorate.repository.review;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ReviewMapper implements RowMapper<Review> {
    @Override
    public Review mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Review.builder()
                .reviewId(rs.getLong("REVIEW_ID"))
                .filmId(rs.getLong("FILM_ID"))
                .userId(rs.getLong("USER_ID"))
                .content(rs.getString("CONTENT"))
                .useful(rs.getInt("USEFUL"))
                .isPositive(rs.getBoolean("IS_POSITIVE"))
                .build();
    }
}
