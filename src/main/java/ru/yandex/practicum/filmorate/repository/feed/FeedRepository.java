package ru.yandex.practicum.filmorate.repository.feed;

import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;

public interface FeedRepository {
    void saveEvent(long userId, Operation operation, EventType eventType, long entityId);
}
