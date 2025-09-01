# Tailoring Workshop Order Management Application (Refactored Version)

## Overview
This is a fully refactored version of the original tailoring workshop order management application.  
The application now follows clean code principles, incorporates best programming practices, and introduces a more robust architecture to improve maintainability, testability, and security.  

For the original version, see [My GitHub Repo](https://github.com/LBolechow/Praca-dyplomowa-SpringBoot-old).

 ### Key Improvements
- **DTO-Based API:** All endpoints now use dedicated Data Transfer Objects (DTOs) for requests and responses, ensuring clear data contracts and easier API maintenance.  
- **Enhanced Error Handling:** Centralized and structured exception handling provides consistent and informative error responses across the application.  
- **Cleaner Code & Better Practices:** Refactored services, controllers, and utilities for improved readability and maintainability.  
- **Improved Security:** Replaced standard Spring Security authorization with JWT-based authorization and token invalidation.  
- **Refactored OAuth2 Integration:**
- **Separated Constants & Magic Strings:** All reusable messages and constants moved to dedicated classes for better maintainability.
