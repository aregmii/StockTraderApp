# Project Name

Real time basic stock trader app built utilizing Alpaca stream APIs -
//TODO - add details

## Introduction

This is a Maven project.

## Getting Started

To run this application, you need to create your own API keys and configure the logging settings.

### Prerequisites

- Java Development Kit (JDK) 8 or higher
- Maven (for building the project)

### Configuration

#### 1. ALPACA API key configuration

1. Create a file named `application.properties` in the `src/main/resources` directory.
2. Add the following configuration with your Alpaca API credentials:
   ```properties
   alpaca.api.key=<your-alpaca-api-key>
   alpaca.secret.key=<your-alpaca-secret-key>
#### 2. Application Configuration
1. 
   ```properties
   alpaca.websocket.threadpool.size=<thread-pool-size>
   
#### 3. Logging Configuration
1. Create file logback.xml in src/main/resources
```

<configuration debug="true">
    <!-- Console Appender -->
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- File Appender -->
    <appender name="FILE" class="ch.qos.logback.core.FileAppender">
        <!-- Ensure this path is correct and accessible -->
        <file>/path/to/log/file/myapp.log</file>
        <append>true</append>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- Root Logger Configuration -->
    <root level="DEBUG">
        <appender-ref ref="STDOUT" />
        <appender-ref ref="FILE" />
    </root>

    <!-- Specific logger configuration -->
    <logger name="org.example.stocktrader" level="DEBUG"/>
</configuration>
```

### Running the Application

To build the project, run the following command in the project's root directory - 

```bash
mvn clean install

# Run the Application

mvn spring-boot:run

# To generate an executable JAR file, you can use the following command:
mvn package

#This command will create a JAR file in the `target` directory, which you can then run using:
java -jar target/<project-name>.jar

#If you need to clean the project and remove any generated files, you can use the following command:
mvn clean






