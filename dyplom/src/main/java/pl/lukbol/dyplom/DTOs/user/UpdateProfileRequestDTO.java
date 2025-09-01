package pl.lukbol.dyplom.DTOs.user;

public record UpdateProfileRequestDTO(
        String username,
        String password,
        String repeatPassword
) {
}
