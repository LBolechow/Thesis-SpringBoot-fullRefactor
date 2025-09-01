package pl.lukbol.dyplom.DTOs.notification;

import java.util.List;

public record NotificationRequestDTO(String message, List<Long> participantIds) {
}
