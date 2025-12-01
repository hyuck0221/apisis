# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**APIsis** - "The Oasis of APIs, Quenching Data Thirst"

APIsis is an open-source API server providing a wide range of APIs across various domains. The project is designed to be used immediately at https://apisis.dev or can be built and run locally.

This is a Spring Boot 4.0.0 application built with Kotlin 2.2.21, targeting Java 25. The project includes:
- Spring Data JPA with MariaDB
- Spring Security
- Spring Web MVC
- WebSocket support

## Build & Development Commands

### Build
```bash
./gradlew build
```

### Run Application
```bash
./gradlew bootRun
```

### Testing
```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests com.hshim.apisis.ApisisApplicationTests

# Run specific test method
./gradlew test --tests com.hshim.apisis.ApisisApplicationTests.contextLoads
```

### Clean Build
```bash
./gradlew clean build
```

## Architecture Notes

### Package Structure
The project follows a modular API structure where each API domain is isolated as a mini-module:

```
com.hshim.apisis/
├── api/
│   ├── {feature-name}/
│   │   ├── entity/        # JPA entities
│   │   ├── repository/    # Spring Data repositories
│   │   ├── service/       # Business logic
│   │   ├── model/         # DTOs, request/response models
│   │   └── controller/    # REST controllers
│   └── {another-feature}/
│       └── ...
└── ApisisApplication.kt   # Main application entry point
```

Each API domain is self-contained with its own entity, repository, service, model, and controller layers, making it easy to understand, maintain, and extend individual features independently.

### Adding New APIs
When implementing a new API domain:
1. Create a new package under `com.hshim.apisis.api.{feature-name}`
2. Follow the standard structure: entity → repository → service → model → controller
3. Keep dependencies between API domains minimal to maintain modularity
4. Document the API endpoints and usage in the feature's package

### Kotlin Configuration
- Uses strict JSR-305 nullability checking (`-Xjsr305=strict`)
- Default annotation target set to param-property for better constructor properties handling
- JPA entities, MappedSuperclass, and Embeddable classes are configured to be open (required for JPA proxying)

### Database
- Runtime database: MariaDB (driver included as runtime dependency)
- JPA/Hibernate for ORM
- Entities must be annotated with `@Entity` and will automatically be open classes

### Security
Spring Security is included but not yet configured. Default behavior will apply until custom security configuration is added.

### WebSocket
WebSocket support is included for real-time bidirectional communication features.

## Important Considerations

### Java Version Compatibility
The build is configured for Java 25, but note that as of Kotlin 2.2.21, full Java 25 support may be limited. If you encounter compatibility issues, consider using Java 24 (as noted in HELP.md).

### Entity Classes
When creating JPA entities in Kotlin, the `allOpen` plugin is configured to automatically make Entity, MappedSuperclass, and Embeddable classes open. No need to manually add the `open` keyword.
