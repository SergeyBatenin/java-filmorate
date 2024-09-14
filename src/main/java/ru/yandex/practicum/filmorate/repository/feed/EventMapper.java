package ru.yandex.practicum.filmorate.repository.feed;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class EventMapper implements RowMapper<Event> {
    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Event.builder()
                .eventId(rs.getLong("EVENT_ID"))
                .timestamp(rs.getLong("TIMESTAMP"))
                .userId(rs.getLong("USER_ID"))
                .eventType(EventType.valueOf(rs.getString("EVENT_TYPE")))
                .operation(Operation.valueOf(rs.getString("OPERATION")))
                .entityId(rs.getLong("ENTITY_ID"))
                .build();
    }
}

