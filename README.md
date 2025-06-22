# Rewards API

### Project Overview
This is a Spring Boot-based RESTful API for a Retail Rewards Program. It calculates customer reward points based on purchase transactions using a tiered reward system:

- 2 points per dollar spent over $100
- 1 point per dollar spent between $50–$100
- No points for purchases ≤ $50

#### Clone the Repository
``` git clone https://github.com/Arunkumar-d1983/rewards-api.git ```

#### Prerequisites:

- Ensure that JDK 1.8 or higher is installed on your machine.If Java is not installed or the version is lower than 1.8, you need to install JDK 1.8 
    * Visit the Oracle JDK download page: [Oracle JDK Download](https://www.oracle.com/java/technologies/downloads/#java8).
    * After the installation is complete, verify the Java version again using the `java -version` command to ensure that JDK 1.8
- Verify that you have Apache Maven installed. You can check by running `mvn -version` in your command line.
- Install Postman or any other API testing tool.

#### The API supports:
- Dynamic time-frame filtering (start, end dates)
- Monthly breakdown of rewards
- Async reward calculation
- Input validation & centralized exception handling
- Java 8 compatible

#### Technology Stack:

| Tool               | Version                 |
| ------------------ | ----------------------- |
| Java               | 8                       |
| Spring Boot        | 2.7.0                   |
| Maven              | Build Tool              |
| SLF4J              | Logging                 |
| JUnit              | Testing                 |
| Jakarta Validation | Input validation        |


#### Build the application:

- Open a command and navigate to the root directory of the mbean application project.
- Run the following command to build the application.
  
Build and Run :
```
mvn spring-boot:run
```
(OR)

```
mvn clean package

After the build is successful and the JAR file is generated, navigate to the target
directory and run the following command to execute it.

java -jar customer-rewards-api-0.0.1-SNAPSHOT
```

## Design Overview
➤ Design Details:
| Component          | Description                                                 |
| ------------------ | ----------------------------------------------------------- |
| `RewardController` | REST controller exposing `/api/rewards` endpoint.           |
| `RewardService`    | Business logic for filtering and calculating reward points. |
| `RewardCalculator` | Utility class for computing points from amount.             |
| `RewardResponse`   | DTO for returning detailed monthly and total reward data.   |
| `Transaction`      | Represents each customer transaction.                       |
| `Customer`         | Represents a customer and their transaction history.        |

➤ Points Calculation Logic:
   1. point for each dollar between $50 and $100
   2. points for each dollar above $100
   3. No points for amounts ≤ $50

> Example : $120 purchase = (120-100)*2 + (100-50)*1 = **90 points**

## Example
Transactions:
- $75 on 2025-05-15 → 25 pts
- $120 on 2025-06-01 → 90 pts

➤ API Flow:
   1. Accept Customer data with transaction list
   2. Filter transactions in the time frame (start to end, default 3 months)
   3. Calculate monthly and total points
   4. Return enhanced reward response


## API Endpoint
### 1. POST /api/rewards

Description: Calculates reward points for a given customer.

### Example Request:
``` POST http://localhost:8080/api/rewards?start=2025-04-01&end=2025-06-20 ```

### Request Body:
```json
{
  "customerName": "Arunkumar",
  "customerId": 1001,
  "transactions": [
    {
      "transactionId": 1,
      "transactionDate": "2025-04-15",
      "amount": 60.0
    },
    {
      "transactionId": 2,
      "transactionDate": "2025-05-20",
      "amount": 75.0
    },
    {
      "transactionId": 3,
      "transactionDate": "2025-06-01",
      "amount": 120.0
    },
    {
      "transactionId": 4,
      "transactionDate": "2025-06-10",
      "amount": 45.0
    },
    {
      "transactionId": 5,
      "transactionDate": "2025-06-15",
      "amount": 90.0
    }
  ]
}
```
### Sample Response :
```json
{
    "customerName": "Arunkumar",
    "customerId": 1001,
    "monthlyPoints": {
        "2025-05": 25,
        "2025-04": 10,
        "2025-06": 130
    },
    "totalPoints": 165,
    "transactions": [
        {
            "transactionId": 1,
            "transactionDate": "2025-04-15",
            "amount": 60.0,
            "points": 10
        },
        {
            "transactionId": 2,
            "transactionDate": "2025-05-20",
            "amount": 75.0,
            "points": 25
        },
        {
            "transactionId": 3,
            "transactionDate": "2025-06-01",
            "amount": 120.0,
            "points": 90
        },
        {
            "transactionId": 5,
            "transactionDate": "2025-06-15",
            "amount": 90.0,
            "points": 40
        }
    ]
}
```
### Root Cause: Points Logic
| Transaction ID  | Date       | Amount | Points Calculation Description | Points |
| --------------- | ---------- | ------ | ------------------------------ | ------ |
| 1               | 2025-04-15 | 60.0   | (60 − 50) × 1 = 10             | 10     |
| 2               | 2025-05-20 | 75.0   | (75 − 50) × 1 = 25             | 25     |
| 3               | 2025-06-01 | 120.0  | (120 − 100) × 2 = 40 + 50 = 90 | 90     |
| 5               | 2025-06-15 | 90.0   | ($90 - $50) × 1 = 40           | 40     |

### 2. POST /api/rewards/bulk
### Example Request:
``` http://localhost:8080/api/rewards/bulk?start=2025-04-01&end=2025-06-20 ```

### Request Body: 
```json
[
  {
    "customerName": "Arunkumar",
    "customerId": 1001,
    "transactions": [
      { "transactionId": 1, "transactionDate": "2025-04-15", "amount": 60.0 },
      { "transactionId": 2, "transactionDate": "2025-05-20", "amount": 75.0 }
    ]
  },
  {
    "customerName": "Kannan",
    "customerId": 1002,
    "transactions": [
      { "transactionId": 3, "transactionDate": "2025-06-01", "amount": 120.0 },
      { "transactionId": 4, "transactionDate": "2025-06-10", "amount": 45.0 },
      { "transactionId": 5, "transactionDate": "2025-06-15", "amount": 90.0 }
    ]
  }
]
```
### Sample Response :
```json
{
    "customers": [
        {
            "customerName": "Arunkumar",
            "customerId": 1001,
            "monthlyPoints": {
                "2025-05": 25,
                "2025-04": 10
            },
            "totalPoints": 35,
            "transactions": [
                {
                    "transactionId": 1,
                    "transactionDate": "2025-04-15",
                    "amount": 60.0,
                    "points": 10
                },
                {
                    "transactionId": 2,
                    "transactionDate": "2025-05-20",
                    "amount": 75.0,
                    "points": 25
                }
            ]
        },
        {
            "customerName": "Kannan",
            "customerId": 1002,
            "monthlyPoints": {
                "2025-06": 130
            },
            "totalPoints": 130,
            "transactions": [
                {
                    "transactionId": 3,
                    "transactionDate": "2025-06-01",
                    "amount": 120.0,
                    "points": 90
                },
                {
                    "transactionId": 5,
                    "transactionDate": "2025-06-15",
                    "amount": 90.0,
                    "points": 40
                }
            ]
        }
    ]
}
```
## Logging Setup
### application.properties
```
logging.level.root=INFO
logging.level.com.rewards=INFO
logging.pattern.console=
logging.file.name=logs/rewards-app.log
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} - %msg%n
```

All logs will be written to logs/rewards-app.log. Console output is disabled.
