# APIsis

> **The Oasis of APIs, Quenching Data Thirst**

[한국어 문서](README_ko.md)

APIsis is an open-source API server that provides a comprehensive collection of APIs across various domains. Whether you need utility APIs, data processing endpoints, or specialized services, APIsis serves as your one-stop solution for diverse API needs.

## 🌟 Features

- **Modular Architecture**: Each API domain is self-contained and independently maintainable
- **Ready to Use**: Access immediately at [apisis.dev](https://apisis.dev) or run locally
- **Open Source**: Contribute and extend with your own APIs
- **Spring Boot Powered**: Built with Spring Boot 4.0.0 and Kotlin for robust performance
- **Real-time Support**: WebSocket integration for real-time features
- **Secure by Default**: Spring Security integration

## 🚀 Quick Start

### Option 1: Use the Hosted Service

Visit [apisis.dev](https://apisis.dev) to start using the APIs immediately.

### Option 2: Run Locally

#### Prerequisites

- Java 25 (or Java 24 for better compatibility)
- MariaDB

#### Installation

1. Clone the repository:
```bash
git clone https://github.com/yourusername/apisis.git
cd apisis
```

2. Configure your database in `src/main/resources/application.properties`:
```properties
spring.application.name=apisis
# Add your database configuration here
```

3. Build the project:
```bash
./gradlew build
```

4. Run the application:
```bash
./gradlew bootRun
```

The server will start at `http://localhost:8080`

## 📚 API Documentation

API documentation is available at:
- Production: [apisis.dev/docs](https://apisis.dev/docs)
- Local: `http://localhost:8080/docs` (when running locally)

## 🏗️ Architecture

APIsis follows a modular architecture where each API domain is organized as an independent module:

```
com.hshim.apisis/
├── api/
│   ├── {feature-name}/
│   │   ├── entity/        # JPA entities
│   │   ├── repository/    # Data access layer
│   │   ├── service/       # Business logic
│   │   ├── model/         # DTOs and request/response models
│   │   └── controller/    # REST endpoints
│   └── {another-feature}/
│       └── ...
└── ApisisApplication.kt
```

This structure ensures:
- Clear separation of concerns
- Easy maintenance and testing
- Independent development of features
- Minimal coupling between API domains

## 🛠️ Development

### Running Tests

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests com.hshim.apisis.ApisisApplicationTests
```

### Building for Production

```bash
./gradlew clean build
```

### Adding a New API

1. Create a new package under `src/main/kotlin/com/hshim/apisis/api/{your-api-name}`
2. Follow the standard structure:
   ```
   {your-api-name}/
   ├── entity/
   ├── repository/
   ├── service/
   ├── model/
   └── controller/
   ```
3. Implement your API logic following Spring Boot best practices
4. Add tests in `src/test/kotlin/com/hshim/apisis/api/{your-api-name}`
5. Document your API endpoints

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-api`)
3. Commit your changes (`git commit -m 'Add some amazing API'`)
4. Push to the branch (`git push origin feature/amazing-api`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🔧 Tech Stack

- **Language**: Kotlin 2.2.21
- **Framework**: Spring Boot 4.0.0
- **Database**: MariaDB
- **ORM**: Spring Data JPA
- **Security**: Spring Security
- **Real-time**: WebSocket
- **Build Tool**: Gradle

## 📮 Contact & Support

- Website: [apisis.dev](https://apisis.dev)
- Issues: [GitHub Issues](https://github.com/yourusername/apisis/issues)

---

Made with ❤️ by the APIsis Team
