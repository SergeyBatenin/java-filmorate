package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

/**
 * UserEvent.
 */
@Data
@Builder
public class Event {
    private Long eventId;
    @NotNull
    private Long timestamp;
    @NotNull
    private Long userId;
    @NotBlank
    private EventType eventType;
    @NotBlank
    private Operation operation;
    @NotNull
    private Long entityId;
}
