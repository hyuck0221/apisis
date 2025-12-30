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

## 🔌 MCP (Model Context Protocol) Integration

Apisis provides an MCP server, allowing AI models like Claude to discover and utilize our APIs in real-time.

### How to Connect (Claude Desktop)

1. Open your Claude Desktop configuration file (`claude_desktop_config.json`).
   - macOS: `~/Library/Application Support/Claude/claude_desktop_config.json`
   - Windows: `%APPDATA%\Claude\claude_desktop_config.json`
2. Add the following entry to the `mcpServers` object:

```json
{
  "mcpServers": {
    "apisis": {
      "url": "https://apisis.dev/mcp/sse"
    }
  }
}
```

3. Restart Claude Desktop. You should see the 🔌 icon, indicating that the Apisis tools are ready for use.

---

## 🔐 API Usage & Security Guidelines

All API requests to Apisis require authentication.

### Authentication
- **Header**: `X-API-Key`
- **Value**: Your unique API Key generated from the Apisis Dashboard.

### Security Best Practices
1. **No Hardcoding**: Never write your API Key directly in the source code.
2. **Use Environment Variables**: Manage your keys using `.env` files or system environment variables. Ensure `.env` is added to your `.gitignore`.
3. **Prevent Client-side Exposure**: Using API Keys in frontend code (browsers) exposes them to users. Always proxy requests through a backend or use them for server-to-server communication.
4. **Configuration Safety**: Your `claude_desktop_config.json` is personal. Do not share it or commit it to public repositories.

---

## 🛠 Tech Stack


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

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

Made with ❤️ by the APIsis Team
