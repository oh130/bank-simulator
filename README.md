# 🏦 Bank Simulator

Spring Boot + React + MySQL 기반 은행 계좌/송금 시뮬레이터 포트폴리오 프로젝트.

## 📌 기능

- **회원가입 / 로그인** — JWT 기반 인증
- **계좌 개설** — 계좌번호 자동 생성, 초기 잔액 설정
- **내 계좌 목록 / 상세 조회** — 잔액 확인
- **계좌 간 송금** — 잔액 부족 시 예외 처리, 트랜잭션 원자성 보장
- **거래 내역 조회** — 입금/출금/송금/수신 구분, 최신 순 정렬

## 🛠 기술 스택

| 영역 | 기술 |
|------|------|
| Backend | Spring Boot 3.2, Java 17, Spring Security, JPA |
| Frontend | React 18, React Router v6, Axios |
| Database | MySQL 8.0 |
| 인증 | JWT (JJWT 0.12) |
| 인프라 | Docker, Docker Compose |
| Build | Gradle |

## 🏗 프로젝트 구조

```
bank-simulator/
├── backend/                          # Spring Boot 백엔드
│   ├── src/main/java/com/banksimulator/
│   │   ├── config/                   # Security, CORS 설정
│   │   ├── controller/               # REST 컨트롤러
│   │   ├── dto/                      # 요청/응답 DTO
│   │   ├── entity/                   # JPA 엔티티 (User, Account, Transaction)
│   │   ├── exception/                # 예외 클래스 + GlobalExceptionHandler
│   │   ├── repository/               # JPA 레포지토리
│   │   ├── security/                 # JWT 필터, 토큰 프로바이더
│   │   └── service/                  # 비즈니스 로직
│   ├── Dockerfile
│   └── build.gradle
├── frontend/                         # React 프론트엔드
│   ├── src/
│   │   ├── api/axios.js              # Axios 인스턴스 (인터셉터 포함)
│   │   ├── context/AuthContext.jsx   # 전역 인증 상태 관리
│   │   └── pages/                   # Login, Signup, Dashboard, AccountDetail, Transfer
│   ├── Dockerfile
│   └── package.json
├── docker-compose.yml
└── README.md
```

## 🚀 실행 방법

### 사전 요구사항

- [Docker Desktop](https://www.docker.com/products/docker-desktop/)

> Java, Node.js 별도 설치 불필요. MySQL, Spring Boot, React 모두 Docker 컨테이너로 실행.

### 실행

```bash
# 전체 시작 (MySQL + Backend + Frontend)
docker compose up -d

# 로그 확인
docker compose logs -f backend

# 전체 종료
docker compose down
```

> 최초 실행 시 이미지 빌드로 인해 10~15분 소요. 이후 재시작은 수초 내 완료.

브라우저에서 `http://localhost:3000` 접속.

### 컨테이너별 포트

| 서비스 | 포트 | 설명 |
|--------|------|------|
| frontend | 3000 | React (nginx 서빙) |
| backend | 8080 | Spring Boot API |
| mysql | 3306 | MySQL 8.0 |

---

## 📡 API 명세

모든 API는 `/api/v1/` prefix를 사용. 인증이 필요한 API는 요청 헤더에 JWT 토큰 포함.

```
Authorization: Bearer {JWT_TOKEN}
```

### 인증 (`/api/v1/auth`) — 인증 불필요

| 메서드 | 경로 | 설명 |
|--------|------|------|
| POST | `/api/v1/auth/signup` | 회원가입 |
| POST | `/api/v1/auth/login` | 로그인 (JWT 발급) |

**회원가입 요청**
```json
{
  "name": "홍길동",
  "email": "hong@example.com",
  "password": "password123"
}
```

**로그인 응답**
```json
{
  "success": true,
  "message": "로그인 성공",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "email": "hong@example.com",
    "name": "홍길동"
  }
}
```

### 계좌 (`/api/v1/accounts`) — JWT 필요

| 메서드 | 경로 | 설명 |
|--------|------|------|
| POST | `/api/v1/accounts` | 계좌 개설 |
| GET | `/api/v1/accounts` | 내 계좌 목록 조회 |
| GET | `/api/v1/accounts/{id}` | 계좌 상세 조회 |

**계좌 개설 요청**
```json
{
  "initialBalance": 100000
}
```

### 거래 (`/api/v1/transactions`) — JWT 필요

| 메서드 | 경로 | 설명 |
|--------|------|------|
| POST | `/api/v1/transactions/transfer` | 계좌 간 송금 |
| GET | `/api/v1/transactions/accounts/{accountId}` | 계좌별 거래 내역 조회 |

**송금 요청**
```json
{
  "fromAccountNumber": "100-1234567",
  "toAccountNumber": "100-7654321",
  "amount": 50000,
  "description": "용돈"
}
```

---

## 🔑 주요 설계 포인트

### JWT 인증 흐름
1. 로그인 시 서버가 JWT 토큰 발급
2. 클라이언트는 `localStorage`에 토큰 저장
3. 이후 모든 요청에 `Authorization: Bearer {token}` 헤더 첨부
4. `JwtAuthenticationFilter`가 매 요청마다 토큰 검증

### 송금 트랜잭션 처리
- `@Transactional`로 송금 전 과정을 단일 DB 트랜잭션으로 처리
- 잔액 차감 → 잔액 증가 → 거래 내역 기록이 원자적으로 실행
- 중간 오류 발생 시 전체 롤백

### 예외 처리
`GlobalExceptionHandler`로 모든 예외를 통합 처리:

| 예외 | HTTP 상태 |
|------|-----------|
| `DuplicateEmailException` | 409 Conflict |
| `ResourceNotFoundException` | 404 Not Found |
| `InsufficientBalanceException` | 400 Bad Request |
| `UnauthorizedAccessException` | 403 Forbidden |
| `BadCredentialsException` | 401 Unauthorized |
| `MethodArgumentNotValidException` | 400 (필드별 오류 메시지) |

---

## 🐳 환경 변수

`docker-compose.yml`에서 수정 가능:

| 변수 | 기본값 | 설명 |
|------|--------|------|
| `MYSQL_DATABASE` | `bank_simulator` | 데이터베이스 이름 |
| `MYSQL_USER` | `bankuser` | DB 사용자 |
| `MYSQL_PASSWORD` | `bankpass` | DB 비밀번호 |
| `JWT_SECRET` | (256bit 이상 문자열) | JWT 서명 키 |
| `JWT_EXPIRATION` | `86400000` | 토큰 만료 시간 (ms, 기본 24시간) |

---

## 📸 화면 구성

| 페이지 | 경로 | 설명 |
|--------|------|------|
| 로그인 | `/login` | 이메일/비밀번호 로그인 |
| 회원가입 | `/signup` | 이름/이메일/비밀번호 가입 |
| 대시보드 | `/dashboard` | 내 계좌 목록, 계좌 개설 |
| 계좌 상세 | `/accounts/:id` | 잔액 + 거래 내역 |
| 송금 | `/transfer` | 계좌 간 송금 |
