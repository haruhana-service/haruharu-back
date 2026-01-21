# FCM + PWA 알림 구현 계획

## 개요
Firebase Cloud Messaging(FCM)을 이용한 푸시 알림 구현 계획서

- **프론트엔드**: PWA (Progressive Web App)
- **백엔드**: Spring Boot 3.5.7 + Java 21

---

## 1. Firebase 프로젝트 설정

### 1.1 Firebase Console 작업
- [ ] Firebase Console에서 프로젝트 생성
- [ ] Cloud Messaging 활성화
- [ ] 서비스 계정 키(JSON) 발급 (백엔드용)
- [ ] 웹 앱 등록 후 설정값 획득 (프론트용)

### 1.2 필요한 키/설정값
| 용도 | 파일/값 |
|------|---------|
| 백엔드 | `firebase-service-account.json` (서비스 계정 키) |
| 프론트엔드 | `apiKey`, `authDomain`, `projectId`, `messagingSenderId`, `appId` |

---

## 2. 백엔드 구현 (Spring Boot)

### 2.1 의존성 추가
```gradle
// build.gradle
implementation 'com.google.firebase:firebase-admin:9.2.0'
```

### 2.2 구현 태스크
- [ ] `FirebaseConfig.java` - Firebase 초기화 설정 클래스
- [ ] `Notification` 엔티티 필드 정의
- [ ] FCM 토큰 저장 (Member 테이블에 컬럼 추가 또는 별도 테이블)
- [ ] `FcmService.java` - 푸시 알림 발송 서비스
- [ ] `NotificationService.java` - 알림 CRUD 로직
- [ ] `NotificationController.java` - API 엔드포인트
- [ ] 기존 스케줄러/이벤트 핸들러와 통합

### 2.3 API 엔드포인트
| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/api/fcm/token` | FCM 토큰 등록 |
| DELETE | `/api/fcm/token` | FCM 토큰 삭제 |
| GET | `/api/notifications` | 알림 목록 조회 |
| PATCH | `/api/notifications/{id}/read` | 알림 읽음 처리 |
| PATCH | `/api/notifications/read-all` | 전체 읽음 처리 |

### 2.4 Notification 엔티티 설계
```java
public class Notification extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String body;

    @Column(nullable = false)
    private boolean isRead = false;

    private LocalDateTime sentAt;
}
```

### 2.5 NotificationType Enum
```java
public enum NotificationType {
    DAILY_PROBLEM,      // 오늘의 문제 생성 알림
    STREAK_REMINDER,    // 스트릭 유지 독려 알림
    SUBMISSION_SUCCESS, // 문제 풀이 완료 알림
    STREAK_ACHIEVED     // 스트릭 달성 축하 알림
}
```

### 2.6 알림 발송 시점
| 트리거 | 알림 타입 | 설명 |
|--------|-----------|------|
| `ProblemScheduler` (23:55) | DAILY_PROBLEM | 내일 문제 생성 완료 알림 |
| `StreakScheduler` (저녁) | STREAK_REMINDER | 오늘 문제 풀이 독려 |
| `SubmissionEventHandler` | SUBMISSION_SUCCESS | 문제 풀이 완료 축하 |
| `SubmissionEventHandler` | STREAK_ACHIEVED | 특정 스트릭 달성 시 |

---

## 3. 프론트엔드 구현 (PWA)

### 3.1 의존성 설치
```bash
npm install firebase
```

### 3.2 구현 태스크
- [ ] `firebase.js` - Firebase 초기화
- [ ] `firebase-messaging-sw.js` - Service Worker (백그라운드 알림)
- [ ] 알림 권한 요청 UI
- [ ] FCM 토큰 발급 및 백엔드 등록
- [ ] 포그라운드 알림 처리 (`onMessage`)
- [ ] 알림 목록 UI

### 3.3 Firebase 초기화 예시
```javascript
// firebase.js
import { initializeApp } from 'firebase/app';
import { getMessaging, getToken, onMessage } from 'firebase/messaging';

const firebaseConfig = {
  apiKey: "...",
  authDomain: "...",
  projectId: "...",
  messagingSenderId: "...",
  appId: "..."
};

const app = initializeApp(firebaseConfig);
const messaging = getMessaging(app);
```

### 3.4 Service Worker 예시
```javascript
// public/firebase-messaging-sw.js
importScripts('https://www.gstatic.com/firebasejs/10.7.0/firebase-app-compat.js');
importScripts('https://www.gstatic.com/firebasejs/10.7.0/firebase-messaging-compat.js');

firebase.initializeApp({
  apiKey: "...",
  authDomain: "...",
  projectId: "...",
  messagingSenderId: "...",
  appId: "..."
});

const messaging = firebase.messaging();

messaging.onBackgroundMessage((payload) => {
  const { title, body } = payload.notification;
  self.registration.showNotification(title, { body });
});
```

---

## 4. FCM 토큰 저장 전략

### 옵션 A: Member 테이블에 컬럼 추가 (권장)
```java
// Member.java에 추가
private String fcmToken;
```
- 장점: 간단함, 추가 조인 불필요
- 단점: 멀티 디바이스 지원 어려움
- PWA 특성상 단일 디바이스가 대부분이므로 권장

### 옵션 B: 별도 FcmToken 테이블
```java
@Entity
public class FcmToken {
    @Id @GeneratedValue
    private Long id;
    private Long memberId;
    private String token;
    private String deviceInfo;
    private LocalDateTime createdAt;
}
```
- 장점: 멀티 디바이스 지원
- 단점: 추가 테이블, 조인 필요

---

## 5. 보안 고려사항

- [ ] 서비스 계정 키는 절대 Git에 커밋하지 않음 (`.gitignore` 추가)
- [ ] 환경 변수 또는 Secret Manager 사용
- [ ] FCM 토큰은 사용자별로 검증
- [ ] 알림 조회/수정 시 본인 확인

---

## 6. 구현 순서

```
Phase 1: 기반 설정
├── 1. Firebase 프로젝트 생성 및 설정
├── 2. 백엔드 의존성 추가
├── 3. FirebaseConfig 설정 클래스 구현
└── 4. Notification 엔티티 완성

Phase 2: 백엔드 핵심 기능
├── 5. FCM 토큰 저장 로직 (Member 테이블)
├── 6. FcmService 구현 (푸시 발송)
├── 7. NotificationService 구현
└── 8. NotificationController API 구현

Phase 3: 이벤트 통합
├── 9. ProblemScheduler에 알림 발송 추가
├── 10. StreakScheduler에 알림 발송 추가
└── 11. SubmissionEventHandler에 알림 발송 추가

Phase 4: 프론트엔드
├── 12. Firebase SDK 설정
├── 13. Service Worker 구현
├── 14. 알림 권한 요청 및 토큰 등록
└── 15. 알림 UI 구현
```

---

## 7. 참고 자료

- [Firebase Admin SDK 문서](https://firebase.google.com/docs/admin/setup)
- [FCM HTTP v1 API](https://firebase.google.com/docs/cloud-messaging/send-message)
- [Web Push with FCM](https://firebase.google.com/docs/cloud-messaging/js/client)
