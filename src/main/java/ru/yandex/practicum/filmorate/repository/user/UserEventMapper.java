package ru.yandex.practicum.filmorate.repository.user;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.UserEvent;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserEventMapper implements RowMapper<UserEvent> {
    @Override
    public UserEvent mapRow(ResultSet rs, int rowNum) throws SQLException {
        return UserEvent.builder()
                .eventId(rs.getLong("EVENT_ID"))
                .timestamp(rs.getLong("TIMESTAMP"))
                .userId(rs.getLong("USER_ID"))
                .eventType(EventType.valueOf(rs.getString("EVENT_TYPE")))
                .operation(Operation.valueOf(rs.getString("OPERATION")))
                .entityId(rs.getLong("ENTITY_ID"))
                .build();
    }
}

