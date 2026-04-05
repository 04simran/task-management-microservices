A Spring Boot microservices system using Eureka, API Gateway, Auth Service, and Task Service.
Supports JWT authentication and RBAC (USER & ADMIN) for secure task management.
Services: Eureka (8761), Gateway (8080), Auth (8081), Task (8082).
All requests go through the gateway, with service discovery via Eureka.
Built with Spring Cloud, JPA, MySQL, and JWT.