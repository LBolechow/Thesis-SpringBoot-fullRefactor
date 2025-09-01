package pl.lukbol.dyplom.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.lukbol.dyplom.DTOs.response.ApiResponseDTO;
import pl.lukbol.dyplom.DTOs.user.*;
import pl.lukbol.dyplom.services.UserService;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin(maxAge = 3600)
public class UserController {
    private final UserService userService;


    @PostMapping("/add")
    public ResponseEntity<ApiResponseDTO> addUser(
            @RequestBody AddUserDTO addUserDTO) {
        ApiResponseDTO responseDTO = userService.addUser(addUserDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/users/findByRole")
    @ResponseBody
    public ResponseEntity<List<UserDTO>> getUsersByRoles() {
        List<UserDTO> response = userService.getUsersByRoles();
        return ResponseEntity.ok(response);
    }
/*
    @GetMapping("/panel_administratora")
    public ModelAndView displayAllUsers(Authentication authentication,
                                        @RequestParam(name = "page", defaultValue = "0") int page,
                                        @RequestParam(name = "size", defaultValue = "10") int size) {
        return userService.getAllUsers(page, size);
    }
*/
    @PostMapping(value = "/register", consumes = {"*/*"})
    public ResponseEntity<ApiResponseDTO> registerUser(@RequestBody RegisterRequestDTO registerRequestDTO) {
        ApiResponseDTO response = userService.registerUser(registerRequestDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get_message")
    @ResponseBody
    public String getMessageFromSession(HttpServletRequest request) {
        return (String) request.getSession().getAttribute("message");
    }

    @GetMapping("/user")
    public ResponseEntity<UserDTO> user(Authentication authentication) {
        UserDTO userDTO = userService.getUserByEmail(authentication);
        return ResponseEntity.ok(userDTO);
    }

    @PostMapping("/profile/apply")
    public ResponseEntity<ApiResponseDTO> changeProfile(
            Authentication authentication,
            @RequestBody UpdateProfileRequestDTO updateProfileRequestDTO
    ) {
        ApiResponseDTO response = userService.changeProfile(authentication, updateProfileRequestDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/employees-and-admins")
    public ResponseEntity<List<UserDTO>> getEmployeesAndAdmins(Authentication authentication) {
        List<UserDTO> users = userService.getEmployeesAndAdmins(authentication);
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponseDTO> deleteUser(@PathVariable Long id) {
        ApiResponseDTO response = userService.deleteUser(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/users/update/{id}")
    public ResponseEntity<ApiResponseDTO> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest updateUserRequest) {

        ApiResponseDTO response= userService.updateUser(id, updateUserRequest);
            return ResponseEntity.ok(response);

    }

    @GetMapping("/search-users")
    public ResponseEntity<List<UserDTO>> searchUsers(
            @RequestParam("category") String category,
            @RequestParam("searchText") String searchText) {

        List<UserDTO> users = userService.searchUsers(category, searchText);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/user/employees-and-admins")
    public List<String> getEmployeeNames() {
        return userService.getEmployeeNames();
    }

    @PostMapping("/send-new-password")
    public ResponseEntity<ApiResponseDTO> sendNewPassword(@RequestBody Map<String, String> payload) {
        ApiResponseDTO response = userService.sendNewPassword(payload);
        return ResponseEntity.ok(response);
    }

}
