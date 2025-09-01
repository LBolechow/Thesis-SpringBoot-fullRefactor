package pl.lukbol.dyplom.utilities;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import pl.lukbol.dyplom.DTOs.order.OrderDetailsDTO;
import pl.lukbol.dyplom.common.Messages;
import pl.lukbol.dyplom.DTOs.material.MaterialDTO;
import pl.lukbol.dyplom.DTOs.date.DateRange;
import pl.lukbol.dyplom.DTOs.order.AvailabilityDTO;
import pl.lukbol.dyplom.DTOs.order.EditOrderDTO;
import pl.lukbol.dyplom.DTOs.order.OrderDTO;
import pl.lukbol.dyplom.classes.Material;
import pl.lukbol.dyplom.classes.Notification;
import pl.lukbol.dyplom.classes.Order;
import pl.lukbol.dyplom.classes.User;
import pl.lukbol.dyplom.exceptions.ApplicationException;
import pl.lukbol.dyplom.repositories.MaterialRepository;
import pl.lukbol.dyplom.repositories.OrderRepository;
import pl.lukbol.dyplom.repositories.UserRepository;

import java.text.ParseException;
import java.time.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderUtils {

    public enum UserRoles {
        ROLE_ADMIN, ROLE_EMPLOYEE, ROLE_CLIENT
    }
    public static final ZoneId WARSAW_ZONE = ZoneId.of("Europe/Warsaw");
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    private final MaterialRepository materialRepository;


    public boolean isWorkingDay(Calendar calendar) {
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY;
    }

    public List<User> findAvailableUsersWithEndDateTime(Date taskStartDateTime, Date taskEndDateTime, int durationMinutes) {
        List<String> roleNamesToSearch = Arrays.asList(UserRoles.ROLE_EMPLOYEE.name(), UserRoles.ROLE_ADMIN.name());

        List<User> availableUsers = new ArrayList<>();
        List<User> allUsers = userRepository.findAll();

        for (User user : allUsers) {
            boolean isAvailable = true;

            if (user.getRole() != null && roleNamesToSearch.contains(user.getRole().getName())) {

                List<Order> userOrders = orderRepository
                        .findByEmployeeNameAndEndDateAfterAndStartDateBefore(
                                user.getName(), taskStartDateTime, taskEndDateTime
                        );

                for (Order order : userOrders) {
                    Date orderStartDate = order.getStartDate();
                    Date orderEndDate = order.getEndDate();

                    if ((taskEndDateTime.after(orderStartDate) && taskEndDateTime.before(orderEndDate)) ||
                            (taskStartDateTime.after(orderStartDate) && taskStartDateTime.before(orderEndDate)) ||
                            (taskStartDateTime.before(orderStartDate) && taskEndDateTime.after(orderEndDate))) {
                        isAvailable = false;
                        break;
                    }
                }

                if (isAvailable) {
                    availableUsers.add(user);
                }
            }
        }

        return availableUsers;
    }

    public List<User> findAvailableUserWithEndDateTime(Long employeeId, Date taskStartDateTime, Date taskEndDateTime, int durationMinutes) {
        List<String> roleNamesToSearch = Arrays.asList(UserRoles.ROLE_EMPLOYEE.name(), UserRoles.ROLE_ADMIN.name());

        List<User> availableUsers = new ArrayList<>();

        Optional<User> optionalUser = userRepository.findById(employeeId);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            boolean isAvailable = true;

            if (user.getRole() != null && roleNamesToSearch.contains(user.getRole().getName())) {

                List<Order> userOrders = orderRepository
                        .findByEmployeeNameAndEndDateAfterAndStartDateBefore(
                                user.getName(), taskStartDateTime, taskEndDateTime
                        );

                for (Order order : userOrders) {
                    Date orderStartDate = order.getStartDate();
                    Date orderEndDate = order.getEndDate();

                    if ((taskEndDateTime.after(orderStartDate) && taskEndDateTime.before(orderEndDate)) ||
                            (taskStartDateTime.after(orderStartDate) && taskStartDateTime.before(orderEndDate)) ||
                            (taskStartDateTime.before(orderStartDate) && taskEndDateTime.after(orderEndDate))) {
                        isAvailable = false;
                        break;
                    }
                }

                if (isAvailable) {
                    availableUsers.add(user);
                }
            }
        }

        return availableUsers;
    }

    public boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals(UserRoles.ROLE_ADMIN.name()));
    }

    public DateRange convertToDateRange(LocalDate fromDate, LocalDate toDate) {
        LocalDateTime startDateTime = LocalDateTime.of(fromDate, LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(toDate, LocalTime.MAX);

        return new DateRange(
                Date.from(startDateTime.atZone(ZoneId.systemDefault()).toInstant()),
                Date.from(endDateTime.atZone(ZoneId.systemDefault()).toInstant())
        );
    }

    public List<Order> filterInProgressOrders(List<Order> orders) {
        return orders.stream()
                .filter(order -> "In progress".equals(order.getStatus()))
                .collect(Collectors.toList());
    }

    public List<User> findAvailableUsersWithoutEmployee(Long orderId, Date taskStartDateTime, Date taskEndDateTime, int durationMinutes) {
        List<String> roleNamesToSearch = Arrays.asList(UserRoles.ROLE_EMPLOYEE.name(), UserRoles.ROLE_ADMIN.name());
        List<User> availableUsers = new ArrayList<>();

        Optional<Order> optionalOrder = orderRepository.findById(orderId);

        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            String employeeNameOnOrder = order.getEmployeeName();

            List<User> allUsersExceptEmployee = userRepository.findAllByNameNot(employeeNameOnOrder);

            for (User user : allUsersExceptEmployee) {
                boolean isAvailable = true;

                if (user.getRole() != null && roleNamesToSearch.contains(user.getRole().getName())) {

                    List<Order> userOrders = orderRepository.findByEmployeeNameAndEndDateAfterAndStartDateBefore(
                            user.getName(), taskStartDateTime, taskEndDateTime
                    );

                    for (Order userOrder : userOrders) {
                        Date orderStartDate = userOrder.getStartDate();
                        Date orderEndDate = userOrder.getEndDate();

                        if ((taskEndDateTime.after(orderStartDate) && taskEndDateTime.before(orderEndDate)) ||
                                (taskStartDateTime.after(orderStartDate) && taskStartDateTime.before(orderEndDate)) ||
                                (taskStartDateTime.before(orderStartDate) && taskEndDateTime.after(orderEndDate))) {
                            isAvailable = false;
                            break;
                        }
                    }

                    if (isAvailable) {
                        availableUsers.add(user);
                    }
                }
            }
        }

        return availableUsers;
    }

    public User findUserByName(String name) {
        return userRepository.findByNameContainingIgnoreCase(name).get(0);
    }

    public void updateOrderFields(Order order, EditOrderDTO request) {
        User user = findUserByName(request.selectedUser());

        order.setEmployeeName(user != null ? user.getName() : null);
        order.setDescription(request.description());
        order.setClientName(request.clientName());
        order.setClientEmail(request.email());
        order.setPhoneNumber(request.phoneNumber());

        try {
            order.setStartDate(DateUtils.parseDate(request.startDate(), "yyyy-MM-dd"));
            order.setEndDate(DateUtils.parseDate(request.endDate(), "yyyy-MM-dd"));
        } catch (ParseException e) {
            throw new ApplicationException.InvalidDateException("Nieprawidłowy format daty: " + e.getMessage());
        }

        order.setDuration(request.hours());
        order.setPrice(request.price());
        order.setStatus(request.status());
    }
    public void addNotificationAboutNewOrder(User user) {
        List<Notification> notifications = new ArrayList<>(user.getNotifications());
        notifications.add(new Notification(Messages.NEW_ORDER_NOTIF, new Date(), user, "System"));
        user.setNotifications(notifications);
        userRepository.save(user);
    }

    public List<Material> createMaterialsForOrder(List<String> items, Order order) {
        return items.stream()
                .map(item -> new Material(item, order, false))
                .collect(Collectors.toList());
    }


    public List<Order> findOrdersForAdmin(DateRange dateRange) {
        return orderRepository.findByEndDateBetween(dateRange.start(), dateRange.end());
    }

    public List<Order> findOrdersForUser(String username, DateRange dateRange) {
        return orderRepository.findByEmployeeNameAndEndDateBetween(username, dateRange.start(), dateRange.end());
    }


    public OrderDTO toOrderDTO(Order order) {
        return new OrderDTO(
                order.getId(),
                order.getDescription(),
                order.getClientName(),
                order.getClientEmail(),
                order.getPhoneNumber(),
                order.getDuration(),
                order.getStartDate().toString(),
                order.getEndDate().toString(),
                order.getEmployeeName(),
                order.getPrice(),
                order.getStatus(),
                order.getMaterials().stream()
                        .map(m -> new MaterialDTO(m.getId(), m.getItem(), m.isChecked()))
                        .toList()
        );
    }
    public OrderDetailsDTO buildOrderDetailsDTO(Order order) {
        List<MaterialDTO> materials = order.getMaterials().stream()
                .map(m -> new MaterialDTO(m.getId(), m.getItem(), m.isChecked()))
                .toList();

        return new OrderDetailsDTO(
                order.getId(),
                order.getDescription(),
                order.getClientName(),
                order.getClientEmail(),
                order.getPhoneNumber(),
                order.getEmployeeName(),
                order.getStatus(),
                order.getPrice(),
                order.getDuration(),
                order.getIdCode(),
                materials
        );
    }

    public AvailabilityDTO findNextAvailableSlot(
            Date startDate,
            double durationHours,
            Function<Calendar, List<User>> availableUsersProvider) {

        Calendar currentDateTime = Calendar.getInstance();
        currentDateTime.setTime(startDate);

        final int WORKDAY_END_HOUR = 16;
        final int START_HOUR = 8;
        final int MAX_DAYS_LOOKAHEAD = 30;
        int daysChecked = 0;

        if (currentDateTime.get(Calendar.HOUR_OF_DAY) >= WORKDAY_END_HOUR) {
            currentDateTime.add(Calendar.DAY_OF_MONTH, 1);
            currentDateTime.set(Calendar.HOUR_OF_DAY, START_HOUR);
            currentDateTime.set(Calendar.MINUTE, 0);
            currentDateTime.set(Calendar.SECOND, 0);
        }

        while (daysChecked < MAX_DAYS_LOOKAHEAD) {
            if (isWorkingDay(currentDateTime)) {
                Calendar endDateTime = (Calendar) currentDateTime.clone();
                int durationMinutes = (int) (durationHours * 60);
                endDateTime.add(Calendar.MINUTE, durationMinutes);

                if (endDateTime.get(Calendar.HOUR_OF_DAY) > WORKDAY_END_HOUR) {
                    currentDateTime.add(Calendar.DAY_OF_MONTH, 1);
                    currentDateTime.set(Calendar.HOUR_OF_DAY, START_HOUR);
                    currentDateTime.set(Calendar.MINUTE, 0);
                    currentDateTime.set(Calendar.SECOND, 0);
                    daysChecked++;
                    continue;
                }

                List<User> availableUsers = availableUsersProvider.apply(currentDateTime);

                if (!availableUsers.isEmpty()) {
                    User suggestedUser = availableUsers.get(0);
                    return new AvailabilityDTO(
                            true,
                            "Termin dostępny",
                            DateUtils.formatDateTime(currentDateTime),
                            DateUtils.formatDateTime(endDateTime),
                            suggestedUser.getName()
                    );
                }
            }

            currentDateTime.add(Calendar.DAY_OF_MONTH, 1);
            currentDateTime.set(Calendar.HOUR_OF_DAY, START_HOUR);
            currentDateTime.set(Calendar.MINUTE, 0);
            currentDateTime.set(Calendar.SECOND, 0);
            daysChecked++;
        }

        return new AvailabilityDTO(
                false,
                "Brak dostępnych pracowników w ramach dni roboczych.",
                null,
                null,
                null
        );
    }
    public void updateOrderMaterials(Order order, List<String> items) {
        materialRepository.deleteAllByOrder(order);
        List<Material> materials = createMaterialsForOrder(items, order);
        order.setMaterials(materials);
    }



}
