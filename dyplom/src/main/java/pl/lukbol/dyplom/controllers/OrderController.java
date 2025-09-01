package pl.lukbol.dyplom.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.lukbol.dyplom.DTOs.date.CurrentDateDTO;
import pl.lukbol.dyplom.DTOs.order.*;
import pl.lukbol.dyplom.DTOs.response.ApiResponseDTO;
import pl.lukbol.dyplom.classes.Order;
import pl.lukbol.dyplom.repositories.OrderRepository;
import pl.lukbol.dyplom.services.OrderService;
import pl.lukbol.dyplom.utilities.AuthenticationUtils;
import pl.lukbol.dyplom.utilities.OrderUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderRepository orderRepository;

    private final OrderService orderService;

    private final OrderUtils orderUtils;


    @GetMapping("/currentDate")
    public ResponseEntity<CurrentDateDTO> getCurrentDate() {
        return ResponseEntity.ok(orderService.getCurrentDate());
    }


    @GetMapping("/index")
    public ResponseEntity<List<OrderDTO>> showUserOrders(Authentication authentication) {
        String userEmail = AuthenticationUtils.checkmail(authentication.getPrincipal());
        List<OrderDTO> userOrders = orderService.getUserOrders(userEmail);
        return ResponseEntity.ok(userOrders);
    }

    @GetMapping(value = "/order/getDailyOrders")
    @ResponseBody
    public ResponseEntity<List<OrderDTO>> getDailyOrders(
            Authentication authentication,
            @RequestBody OrderRequestByDateDTO orderRequestDTO
    ) {

            List<OrderDTO> ordersData = orderService.getDailyOrders(authentication, orderRequestDTO);
            return ResponseEntity.ok(ordersData);

    }

    @PostMapping(value = "/order/add", consumes = "application/json")
    public ResponseEntity<ApiResponseDTO> addOrder(@RequestBody AddOrderDTO addOrderDTO) {
        ApiResponseDTO response = orderService.addOrder(addOrderDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/order/getOrderDetails/{id}")
    public ResponseEntity<OrderDetailsDTO> getOrderDetails(@PathVariable Long id) {
        OrderDetailsDTO orderDetails = orderService.getOrderDetails(id);
        return ResponseEntity.ok(orderDetails);
    }

    @PostMapping(value = "/order/edit/{id}", consumes = "application/json")
    @ResponseBody
    public ResponseEntity<ApiResponseDTO> editOrder(@PathVariable Long id, @RequestBody EditOrderDTO request) {
        ApiResponseDTO response = orderService.editOrder(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/order/checkAvailability")
    public ResponseEntity<AvailabilityDTO> checkAvailability(
            @RequestParam double durationHours,
            @RequestParam(required = false, defaultValue = "8") int startHour) {

        AvailabilityDTO availability = orderService.checkAvailability(durationHours, startHour);
        return ResponseEntity.ok(availability);
    }

    @GetMapping("/order/checkAvailabilityNextDay/{orderId}")
    public ResponseEntity<AvailabilityDTO> checkAvailabilityNextDay(
            @PathVariable Long orderId,
            @RequestParam double durationHours) {

        AvailabilityDTO availability = orderService.checkAvailabilityNextDay(orderId, durationHours);
        return ResponseEntity.ok(availability);
    }

    @GetMapping("/order/otherEmployee/{orderId}")
    public ResponseEntity<AvailabilityDTO> checkAvailabilityOtherEmployee(
            @PathVariable Long orderId,
            @RequestParam double durationHours) {

        AvailabilityDTO availability = orderService.checkAvailabilityOtherEmployee(orderId, durationHours);
        return ResponseEntity.ok(availability);
    }

    @DeleteMapping("/order/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable Long id) {

        orderService.deleteOrder(id);
    }

    @GetMapping("/order/search")
    public ResponseEntity<List<OrderDTO>> searchOrdersByStartDateBetweenWithMaterials(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            Authentication authentication) {

        List<OrderDTO> filteredOrders = orderService.searchOrdersByStartDateBetweenWithMaterials(fromDate, toDate, authentication);

        return ResponseEntity.ok(filteredOrders);
    }

    @PatchMapping("/material/{materialId}")
    public ResponseEntity<ApiResponseDTO> updateMaterialCheckedState(
            @PathVariable Long materialId,
            @RequestBody Map<String, Boolean> requestBody) {

        boolean checked = requestBody.getOrDefault("checked", false);

        ApiResponseDTO response = orderService.updateMaterialCheckedState(materialId, checked);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/order/checkOrder/{idCode}")
    public ResponseEntity<OrderDetailsDTO> getOrderForClient(@PathVariable String idCode) {
        Order order = orderRepository.findByIdCode(idCode);
        if (order != null) {
            OrderDetailsDTO dto = orderUtils.buildOrderDetailsDTO(order);
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}
