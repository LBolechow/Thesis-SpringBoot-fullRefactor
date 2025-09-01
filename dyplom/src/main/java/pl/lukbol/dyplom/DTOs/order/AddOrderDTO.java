package pl.lukbol.dyplom.DTOs.order;

import java.util.List;

public record AddOrderDTO(
        String description,
        String clientName,
        String email,
        String phoneNumber,
        double hours,
        String startDate,
        String endDate,
        String selectedUser,
        int price,
        String status,
        List<String> items
) {}
