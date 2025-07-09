package ru.yandex.practicum.filmorate.storage.reviews;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbc;

    @Override
    public Review create(Review review) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbc)
                .withTableName("reviews")
                .usingGeneratedKeyColumns("review_id");

        Map<String, Object> params = Map.of(
                "content", review.getContent(),
                "is_positive", review.getIsPositive(),
                "user_id", review.getUserId(),
                "film_id", review.getFilmId(),
                "useful", 0
        );

        Long id = insert.executeAndReturnKey(params).longValue();
        review.setReviewId(id);
        return review;
    }

    private void updateReviewRating(Long reviewId, Long userId, boolean newRating, int usefulDelta) {
        Boolean currentRating = getUserReviewRating(reviewId, userId);

        if (currentRating == null) {
            jdbc.update("INSERT INTO review_likes VALUES (?, ?, ?)", reviewId, userId, newRating);
            jdbc.update("UPDATE reviews SET useful = useful + ? WHERE review_id = ?", usefulDelta, reviewId);
        } else if (currentRating != newRating) {
            jdbc.update("UPDATE review_likes SET is_positive = ? WHERE review_id = ? AND user_id = ?",
                    newRating, reviewId, userId);
            jdbc.update("UPDATE reviews SET useful = useful + ? WHERE review_id = ?",
                    usefulDelta * 2, reviewId);
        }
    }

    @Override
    public Review update(Review review) {
        String sql = "UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?";
        int updatedRows = jdbc.update(sql,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());

        if (updatedRows == 0) {
            throw new NotFoundException("Отзыв с id=" + review.getReviewId() + " не найден");
        }
        return getById(review.getReviewId()).orElseThrow();
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM reviews WHERE review_id = ?";
        int deletedRows = jdbc.update(sql, id);
        if (deletedRows == 0) {
            throw new NotFoundException("Отзыв с id=" + id + " не найден");
        }
    }

    @Override
    public Optional<Review> getById(Long id) {
        String sql = "SELECT * FROM reviews WHERE review_id = ?";
        try {
            return Optional.ofNullable(jdbc.queryForObject(sql, this::mapRowToReview, id));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Review> getReviewsByFilmId(Long filmId, int count) {
        String sql = "SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?";
        return jdbc.query(sql, this::mapRowToReview, filmId, count);
    }

    @Override
    public Boolean getUserReviewRating(Long reviewId, Long userId) {
        try {
            return jdbc.queryForObject(
                    "SELECT is_positive FROM review_likes WHERE review_id = ? AND user_id = ?",
                    Boolean.class, reviewId, userId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public void addLike(Long reviewId, Long userId) {
        updateReviewRating(reviewId, userId, true, 1);
    }

    @Override
    public void addDislike(Long reviewId, Long userId) {
        updateReviewRating(reviewId, userId, false, -1);
    }

    @Override
    public void removeLike(Long reviewId, Long userId) {
        removeRating(reviewId, userId, true);
    }

    @Override
    public void removeDislike(Long reviewId, Long userId) {
        removeRating(reviewId, userId, false);
    }

    private void removeRating(Long reviewId, Long userId, boolean isLike) {
        int deleted = jdbc.update(
                "DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND is_positive = ?",
                reviewId, userId, isLike);

        if (deleted > 0) {
            int usefulDelta = isLike ? -1 : 1;
            jdbc.update("UPDATE reviews SET useful = useful + ? WHERE review_id = ?",
                    usefulDelta, reviewId);
        }
    }

    private Review mapRowToReview(ResultSet rs, int rowNum) throws SQLException {
        return Review.builder()
                .reviewId(rs.getLong("review_id"))
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("is_positive"))
                .userId(rs.getLong("user_id"))
                .filmId(rs.getLong("film_id"))
                .useful(rs.getInt("useful"))
                .build();
    }
}
