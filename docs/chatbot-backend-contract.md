# 챗봇 백엔드 연동 계약

## 역할 분리

- 프론트 BFF는 LLM 의도 분류와 슬롯 추출을 담당한다.
- 백엔드는 임시 카드, 채팅 내역, 확정 카드, 납부 일정, 업체 일정의 정합성을 담당한다.
- 업체와 변경 이력 질의는 구조화된 DB 데이터를 사용한다. 별도 Markdown 문서 기반 질의응답은 사용하지 않는다.
- 계약서 이미지는 저장하지 않는다. 채팅 내역에는 파일명만 기록한다.

## 처리 흐름

1. BFF가 사용자 발화 또는 계약서에서 업체와 일정 정보를 추출한다.
2. `POST /api/temp-cards`로 임시 카드를 만들고, 이어지는 슬롯 입력은 `PUT /api/temp-cards/{id}`로 갱신한다.
3. `POST /api/chat/messages`로 사용자와 assistant의 렌더링 항목을 기록한다.
4. 사용자가 확정하면 `POST /api/temp-cards/{id}/confirm`을 호출한다.
5. 백엔드는 카드, 납부 일정, 업체 일정, 변경 이력을 한 트랜잭션에서 저장하고 확정 답변도 채팅 내역에 기록한다.
6. 재진입 시 `GET /api/chat/history`로 대화를 복원한다.

## 임시 카드 생성과 갱신

`POST /api/temp-cards`는 `201 Created`, `PUT /api/temp-cards/{id}`는 `200 OK`를 반환한다.

```json
{
  "category": "hall",
  "name": "그랜드 힐 컨벤션",
  "status": "scheduled",
  "contractDate": null,
  "totalAmount": null,
  "depositAmount": null,
  "balanceAmount": null,
  "balanceDueDate": null,
  "memo": null,
  "sourceMessage": "8월 3일 오후 2시 30분에 그랜드 힐 투어 가",
  "scheduleDate": "2026-08-03",
  "scheduleTime": "14:30",
  "scheduleTitle": "그랜드 힐 투어"
}
```

응답은 확정 API 경로에 사용할 식별자를 반환한다.

```json
{ "id": 42 }
```

카테고리는 `hall`, `studio`, `dress`, `makeup`과 대문자 enum 별칭을 모두 수용한다. 상태는 프론트 값인 `drafting`, `candidate`, `scheduled`, `contracted`, `coordinating`과 백엔드 enum 별칭을 모두 수용한다.

## 채팅 내역 기록

`POST /api/chat/messages`는 최대 20개 항목을 한 번에 기록하고 `201 Created`를 반환한다.

```json
{
  "items": [
    { "role": "user", "kind": "text", "payload": { "text": "웨딩홀 계약했어" } },
    { "role": "assistant", "kind": "temp-card", "payload": { "tempCard": { "id": "42" } } },
    { "role": "assistant", "kind": "choices", "payload": { "choices": ["이대로 등록", "취소"] } }
  ]
}
```

허용 조합은 다음과 같다.

- user: `text`, `image`
- assistant: `text`, `temp-card`, `choices`
- `image` payload는 `{ "fileName": "계약서.png" }`만 허용한다.

## 프론트 BFF 연결 지점

백엔드 API 구현과 별개로 BFF의 현재 인메모리 `chatHistory`와 읽기 전용 `ChatSession`은 위 API 호출로 교체해야 한다. 이번 백엔드 작업에서는 프론트 저장소를 수정하지 않는다.
