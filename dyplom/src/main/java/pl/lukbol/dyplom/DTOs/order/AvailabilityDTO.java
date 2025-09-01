package pl.lukbol.dyplom.DTOs.order;

public record AvailabilityDTO(
        boolean available,
        String message,
        String startDateTime,
        String endDateTime,
        String userName
) {}

