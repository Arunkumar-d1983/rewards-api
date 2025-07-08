# Customer Rewards API

### Project Overview
This is a **Spring Boot**-based RESTful API for a **Retail Rewards Program**. It calculates customer reward points based on purchase transactions using a tiered reward system:

### Reward Rules
- **2 points** per dollar spent **over $100**
- **1 point** per dollar spent **between $50 and $100**
- **0 points** for purchases **≤ $50**
> Example:  
> $120 purchase → (120 - 100) × 2 + (100 - 50) × 1 = **90 points**

## Technology Stack
| Tool               | Version                 |
| ------------------ | ----------------------- |
| Java               | 8                       |
| Spring Boot        | 2.7.0                   |
| Maven              | Build Tool              |
| SLF4J              | Logging                 |
| JUnit              | Testing                 |
| Jakarta Validation | Input validation        |

## Getting Started

#### Prerequisites:
- Ensure that JDK 1.8 or higher is installed on your machine.If Java is not installed or the version is lower than 1.8, you need to install JDK 1.8 
    * Visit the Oracle JDK download page: [Oracle JDK Download](https://www.oracle.com/java/technologies/downloads/#java8).
    * After the installation is complete, verify the Java version again using the `java -version` command to ensure that JDK 1.8
- Verify that you have Apache Maven installed. You can check by running `mvn -version` in your command line.
- Install Postman or any other API testing tool.

#### Clone the Repository
```bash
git clone https://github.com/Arunkumar-d1983/rewards-api.git
cd rewards-api
```

#### The service supports:
- Reward calculation for the past 3 months
- Monthly reward breakdown
- Customer and transaction management
- Async processing for performance
- Input validation and centralized exception handling
- Java 8 compatible

#### Build the application:
- Open a command and navigate to the root directory of the mbean application project.
- Run the following command to build the application.
  
Build and Run :

Build and Run (Option 1 – via Maven)
```
mvn spring-boot:run
```
(OR)

Build and Run (Option 2 – via JAR)
```
mvn clean package

After the build is successful and the JAR file is generated, navigate to the target
directory and run the following command to execute it.

cd target
java -jar customer-rewards-api-0.0.1-SNAPSHOT
```
#### Running Tests
To verify that the application works correctly and passes all tests, run:
``` 
mvn verify
```

## Design Overview
➤ Design Details:
| Component            | Description                                                 |
| -------------------- | ----------------------------------------------------------- |
| `RewardController`   | REST controller exposing `/api/rewards` endpoint.           |
| `RewardService`      | Business logic for filtering and calculating reward points. |
| `RewardCalculator`   | Utility to calculate reward points from transaction amount. |
| `Customer`           | Represents a customer and their transaction history.        |
| `Transaction`        | Represents each customer transaction.                       |
| `MonthlyReward`      | Represents monthly breakdown of points earned.              |
| `RewardResponse`     | DTO for reward response with transaction details.           |
| `CustomerRepository` | In-memory store for managing customer records               |

➤ Points Calculation Logic:
   1. point for each dollar between $50 and $100
   2. points for each dollar above $100
   3. No points for amounts ≤ $50

> Example : $120 purchase = (120-100)*2 + (100-50)*1 = **90 points**

➤ Reward Points Calculation Flow:
   1. Accept input customer data and transaction list
   2. Filter transactions within a date range (default = last 3 months).
   3. For each transaction, compute reward points using the tiered logic
   4. Group points by month (Year-Month format)
   5. Return a response including monthly and total reward points


## API Endpoint
### 1.POST /api/rewards/customers

Description: Add a new customer

### Example URL :
``` POST http://localhost:8080/api/rewards/customers ```

### Request Body :
```json
{
  "customerName": "Arunkumar",
  "customerId": 1001,
  "transactions": [
    {
      "transactionId": 1,
      "transactionDate": "2025-05-01",
      "amount": 120.0
    },
    {
      "transactionId": 2,
      "transactionDate": "2025-06-15",
      "amount": 90.0
    }
  ]
}
```
### Sample Response (201 Created) :
```json
{
    "customerName": "Arunkumar",
    "customerId": 1001,
    "transactions": [
        {
            "transactionId": 1,
            "transactionDate": "2025-05-01",
            "amount": 120.0,
            "points": 90
        },
        {
            "transactionId": 2,
            "transactionDate": "2025-06-15",
            "amount": 90.0,
            "points": 40
        }
    ]
}
```
### 2.POST /api/rewards/customers/{customerId}/transactions

Description: Add a new transaction to an existing customer.

### Example URL :
``` POST http://localhost:8080/api/rewards/customers/1001/transactions ```

### Request Body : 
```json
{
  "transactionId": 3,
  "transactionDate": "2025-07-02",
  "amount": 75.0
}
```
### Sample Response (201 Created) :
```json
{
    "customerName": "Arunkumar",
    "customerId": 1001,
    "transactions": [
        {
            "transactionId": 1,
            "transactionDate": "2025-05-01",
            "amount": 120.0,
            "points": 90
        },
        {
            "transactionId": 2,
            "transactionDate": "2025-06-15",
            "amount": 90.0,
            "points": 40
        },
        {
            "transactionId": 3,
            "transactionDate": "2025-07-02",
            "amount": 75.0,
            "points": 25
        }
    ]
}
```
### 3.GET /api/rewards/customers/customerRewards/{customerId}

Description: Returns reward points for a specific customer over the last 3 months.

Optional Query Parameters:
 - startDate=yyyy-MM-dd
 - endDate=yyyy-MM-dd

### Example URL :
``` GET http://localhost:8080/api/rewards/customerRewards/1001?startDate=2025-05-01&endDate=2025-07-07 ```
### Sample Response :
```json
{
    "customerName": "Arunkumar",
    "customerId": 1001,
    "transactions": [
        {
            "transactionId": 1,
            "transactionDate": "2025-05-01",
            "amount": 120.0,
            "points": 90
        },
        {
            "transactionId": 2,
            "transactionDate": "2025-06-15",
            "amount": 90.0,
            "points": 40
        },
        {
            "transactionId": 3,
            "transactionDate": "2025-07-02",
            "amount": 75.0,
            "points": 25
        }
    ],
    "monthlyRewards": [
        {
            "year": 2025,
            "month": "MAY",
            "points": 90
        },
        {
            "year": 2025,
            "month": "JUNE",
            "points": 40
        },
        {
            "year": 2025,
            "month": "JULY",
            "points": 25
        }
    ],
    "totalPoints": 155
}
```

### Root Cause: Points Logic
| Transaction ID  | Date       | Amount | Points Calculation Description | Points |
| --------------- | ---------- | ------ | ------------------------------ | ------ |
| 1               | 2025-05-01 | 120.0  | (120 − 100) × 2 = 40 + 50 = 90 | 90     |
| 2               | 2025-06-15 | 90.0   | ($90 - $50) × 1 = 40           | 40     |
| 3               | 2025-07-02 | 75.0   | (75 − 50) × 1 = 25             | 25     |


## Logging Configuration

Logging is configured in application.properties:

### application.properties
```
logging.level.root=INFO
logging.level.com.rewards=INFO
logging.pattern.console=
logging.file.name=logs/rewards-app.log
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} - %msg%n
```

- Console logs are disabled.
- All logs are written to logs/rewards-app.log
- Debug logs include transaction, reward calculation, and service flow.

All logs will be written to logs/rewards-app.log. Console output is disabled.

### Notes
- If startDate or endDate is omitted in the rewards API, it defaults to the last 3 months from today.
- All inputs are validated using Jakarta Bean Validation.
- Centralized error handling ensures descriptive error responses.