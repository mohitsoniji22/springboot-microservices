# Microservices Architecture with Spring Boot

## Microservices Documentation

- [Order Service]
- [Payment Service]
- [Eureka Service Registry]
- [Cloud API Gateway]
- [Cloud Config Server]
- [Security Service]
- [Notification Service]

## Overview
This repository hosts a microservices architecture application designed to enhance inter-service communication using REST templates and Feign clients. The application is structured around several key technologies:

- **Eureka Service Registry**: Facilitates the registration of applications for dynamic service discovery.
- **API Gateway**: Routes requests and provides a single entry point to the system, securing endpoints through OAuth2.
- **ELK Stack**: Enables centralized logging for monitoring and troubleshooting with Elasticsearch, Logstash, and Kibana.
- **Spring Cloud Config Server**: Manages shared configurations across multiple microservice environments from a central place.
- **Spring Security 6**: Implements robust security using JWT for authentication and authorization processes.
- **Inter-Service Communication**: Utilizes web clients and Kafka for efficient communication between services.

![Architecture](Architecture.jpg)

## Service Startup Order
Ensure the services are started in the following order for proper registration and configuration:
1. `SERVICE-REGISTRY`
2. `CONFIG-SERVER`
3. `GATEWAY-SERVICE`
4. `SECURITY-SERVICE`
5. `ORDER-SERVICE`
6. `PAYMENT-SERVICE`
7. `NOTIFICATION-SERVICE`

## Running the microservices
To run all microservices using Docker Compose, execute the following command in the terminal:
```bash
docker-compose up --build
   ```

## Testing the Application

### 1. Register Users
- **Register user rakey**:
  ```http
  POST http://localhost:8080/auth/register
  {
      "username": "mohitsoniji22",
      "password": "pwd2",
      "email": "mohitsoniji22@gmail.com"
  }
  ```
- **Register user tarun**:
  ```http
  POST http://localhost:8080/auth/register
  {
    "username": "lavlavi",
    "password": "pwd3",
    "email": "lavlavi@gmail.com"
  }
  ```

### 2. Get Token for Tarun
- **Request**:
  ```http
  GET http://localhost:8080/auth/getToken
  {
      "username": "mohitsoniji22",
      "password": "pwd2"
  }
- **Response**:
  ```http
  {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0YXJ1biIsImlhdCI6MTcyNTU2NDE0NiwiZXhwIjoxNzI1NTY1MzQ2fQ.Vqb63DMj5t2PIcdmEuweROc4sAFZOb2KYS7323ounNY",
    "token": "60b2dcf2-dd14-4924-b8f5-d32d958c7048"
  }
  ```

### 3. Validate Token
- **Request**:
  ```http
  GET http://localhost:8080/auth/validateToken?token=eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0YXJ1biIsImlhdCI6MTcyNTU2NDE0NiwiZXhwIjoxNzI1NTY1MzQ2fQ.Vqb63DMj5t2PIcdmEuweROc4sAFZOb2KYS7323ounNY
  ```

- **Response**:
  ``` http
  {
    "message": "Token is valid"
  }
  ```
### 4. Create Book Order with Bearer Token
- **Including Bearer Token**:
  To include the Bearer Token in your request, go to the Headers tab of your API tool (like Postman), select 'Authorization', choose 'Bearer Token', and paste the JWT token received from the previous step.

- **Request**:
  ```http
  POST http://localhost:8080/order/bookOrder
  {
    "name": "Laptop1",
    "quantity": 3,
    "amount": "100000"
  }
  ```
- **Response**:
  ```http
  {
    "order": {
        "id": 2,
        "qty": 3,
        "productName": "Laptop1",
        "amount": 100000,
        "status": "SUCCESS",
        "username": "mohitsoniji22",
        "paymentId": 2,
        "createdAt": "2026-01-23T07:12:06.117961928Z"
    },
    "transactionId": "d9217854-5169-4a14-8e22-762401e3224d",
    "message": "payment processing successful and order placed 2"
}
  ```

### 5. Retrieve Payment Details with Bearer Token
- **Including Bearer Token**:
  To include the Bearer Token in your request, go to the Headers tab of your API tool (like Postman), select 'Authorization', choose 'Bearer Token', and paste the JWT token received from the previous steps.

- **Request**:
  ```http
  GET http://localhost:8080/payment/2
  ```
- **Response**:
  ```http
  [
    {
        "paymentId": 2,
        "paymentStatus": "SUCCESS",
        "transactionId": "d9217854-5169-4a14-8e22-762401e3224d",
        "orderId": 2,
        "amount": 100000.00
    }
  ]
  ```

## Note
Tokens must be refreshed within their valid time frames; JWT tokens are valid for 20 minutes, and refresh tokens are valid for 100 minutes. If the refresh time is exceeded, the process must restart from user registration.

## Logging
Once your services are running, you can monitor and view centralized logs in Kibana. Here's an example of how logs appear in the Kibana dashboard:

![Logging](images/6.%20Logging.jpg)
