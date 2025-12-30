# APIsis

> **데이터 갈증을 해소하는 API의 안식처**

[English Documentation](README.md)

APIsis는 다양한 분야의 API를 제공하는 오픈소스 API 서버입니다. 유틸리티 API, 데이터 처리 엔드포인트, 특수 서비스 등 필요한 모든 API를 한 곳에서 제공합니다.

## 🌟 주요 특징

- **모듈화된 아키텍처**: 각 API 도메인이 독립적으로 구성되어 유지보수가 용이합니다
- **즉시 사용 가능**: [apisis.dev](https://apisis.dev)에서 바로 사용하거나 로컬에서 실행할 수 있습니다
- **오픈소스**: 자유롭게 기여하고 확장할 수 있습니다
- **Spring Boot 기반**: Spring Boot 4.0.0과 Kotlin으로 구축된 견고한 성능
- **실시간 지원**: WebSocket 통합으로 실시간 기능 제공
- **기본 보안**: Spring Security 통합

## 🚀 빠른 시작

### 방법 1: 호스팅 서비스 사용

[apisis.dev](https://apisis.dev)에 접속하여 즉시 API를 사용할 수 있습니다.

### 방법 2: 로컬 환경에서 실행

#### 사전 요구사항

- Java 25 (또는 더 나은 호환성을 위해 Java 24)
- MariaDB

#### 설치 방법

1. 저장소 클론:
```bash
git clone https://github.com/yourusername/apisis.git
cd apisis
```

2. `src/main/resources/application.properties`에서 데이터베이스 설정:
```properties
spring.application.name=apisis
# 여기에 데이터베이스 설정을 추가하세요
```

3. 프로젝트 빌드:
```bash
./gradlew build
```

4. 애플리케이션 실행:
```bash
./gradlew bootRun
```

서버가 `http://localhost:8080`에서 시작됩니다.

## 📚 API 문서

API 문서는 다음 주소에서 확인할 수 있습니다:
- 프로덕션: [apisis.dev/docs](https://apisis.dev/docs)
- 로컬: `http://localhost:8080/docs` (로컬 실행 시)

## 🏗️ 아키텍처

APIsis는 각 API 도메인이 독립적인 모듈로 구성된 모듈화 아키텍처를 따릅니다:

```
com.hshim.apisis/
├── api/
│   ├── {기능-이름}/
│   │   ├── entity/        # JPA 엔티티
│   │   ├── repository/    # 데이터 접근 계층
│   │   ├── service/       # 비즈니스 로직
│   │   ├── model/         # DTO 및 요청/응답 모델
│   │   └── controller/    # REST 엔드포인트
│   └── {다른-기능}/
│       └── ...
└── ApisisApplication.kt
```

이 구조는 다음을 보장합니다:
- 명확한 관심사 분리
- 쉬운 유지보수 및 테스트
- 기능의 독립적인 개발
- API 도메인 간 최소한의 결합

## 🛠️ 개발

### 테스트 실행

```bash
# 모든 테스트 실행
./gradlew test

# 특정 테스트 클래스 실행
./gradlew test --tests com.hshim.apisis.ApisisApplicationTests
```

### 프로덕션 빌드

```bash
./gradlew clean build
```

### 새로운 API 추가하기

1. `src/main/kotlin/com/hshim/apisis/api/{새로운-api-이름}` 아래에 새 패키지 생성
2. 표준 구조를 따라 구성:
   ```
   {새로운-api-이름}/
   ├── entity/
   ├── repository/
   ├── service/
   ├── model/
   └── controller/
   ```
3. Spring Boot 모범 사례를 따라 API 로직 구현
4. `src/test/kotlin/com/hshim/apisis/api/{새로운-api-이름}`에 테스트 추가
5. API 엔드포인트 문서화

## 🔌 MCP (Model Context Protocol) 연동

Apisis는 AI 모델(Claude 등)이 실시간으로 API 명세를 읽고 활용할 수 있도록 MCP 서버 기능을 제공합니다.

### Claude Desktop 연동 방법 (원격)

1. Claude Desktop 설정 파일(`claude_desktop_config.json`)을 엽니다.
   - macOS: `~/Library/Application Support/Claude/claude_desktop_config.json`
   - Windows: `%APPDATA%\Claude\claude_desktop_config.json`
2. `mcpServers` 항목에 아래 내용을 추가합니다.

```json
{
  "mcpServers": {
    "apisis": {
      "url": "https://apisis.dev/mcp/sse"
    }
  }
}
```

3. Claude Desktop을 재시작하면 우측 하단에 🔌 아이콘이 나타나며 Apisis API 도구를 사용할 수 있습니다.

---

## 🔐 API 사용법 및 보안 가이드

Apisis의 모든 API 호출에는 인증이 필요합니다.

### 인증 방법
- **Header**: `X-API-Key`
- **Value**: Apisis 서비스 내 마이페이지에서 발급받은 API Key

### 보안 권장사항
1. **코드 내 하드코딩 금지**: API Key를 소스 코드에 직접 적지 마세요.
2. **환경 변수 사용**: `.env` 파일이나 시스템 환경 변수를 통해 키를 관리하고, `.env` 파일은 절대 Git에 포함시키지 마세요 (`.gitignore` 확인).
3. **클라이언트 측 노출 주의**: 브라우저(Frontend) 코드에서 API Key를 직접 사용하면 사용자에게 노출됩니다. 반드시 Backend를 거쳐 호출하거나 서버 간 통신(Server-to-Server)에만 사용하세요.
4. **연동 설정 주의**: Claude Desktop 설정 파일(`claude_desktop_config.json`)은 개인적인 설정이므로 타인에게 공유하거나 공개된 저장소에 올리지 않도록 주의하세요.

---

## 🛠 기술 스택

- **언어**: Kotlin 2.2.21
- **프레임워크**: Spring Boot 4.0.0
- **데이터베이스**: MariaDB
- **ORM**: Spring Data JPA
- **보안**: Spring Security
- **실시간**: WebSocket
- **빌드 도구**: Gradle

## 📮 문의 및 지원

- 웹사이트: [apisis.dev](https://apisis.dev)
- 이슈: [GitHub Issues](https://github.com/yourusername/apisis/issues)

---

## 📝 라이선스

이 프로젝트는 MIT 라이선스에 따라 라이선스가 부여됩니다. 자세한 내용은 LICENSE 파일을 참조하세요.

APIsis 팀이 ❤️를 담아 만들었습니다
