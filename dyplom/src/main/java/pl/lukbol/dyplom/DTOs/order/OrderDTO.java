package pl.lukbol.dyplom.DTOs.order;

import pl.lukbol.dyplom.DTOs.material.MaterialDTO;

import java.util.List;

public record OrderDTO(
        Long id,
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
        List<MaterialDTO> items
) {}
