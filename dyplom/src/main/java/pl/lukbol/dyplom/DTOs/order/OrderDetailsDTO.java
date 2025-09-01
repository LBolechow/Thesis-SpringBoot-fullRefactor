package pl.lukbol.dyplom.DTOs.order;

import pl.lukbol.dyplom.DTOs.material.MaterialDTO;

import java.util.List;

public record OrderDetailsDTO(
        Long id,
        String description,
        String clientName,
        String clientEmail,
        String phoneNumber,
        String employeeName,
        String status,
        int price,
        double duration,
        String idCode,
        List<MaterialDTO> materials
) {}
