package pl.lukbol.dyplom.DTOs.user;


public record UserDTO(
        Long id,
        String name,
        String email,
        boolean enabled,
        String role
) {
}
