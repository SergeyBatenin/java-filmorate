package ru.yandex.practicum.filmorate.repository.like;

public interface LikeRepository {

    void like(long filmId, long userId);

    void unlike(long filmId, long userId);
}
