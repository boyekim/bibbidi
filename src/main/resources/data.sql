-- Demo account
-- email: demo@bibbidi.local
-- password: demo1234

INSERT INTO users (email, password, name)
VALUES
    ('demo@bibbidi.local', '$2y$10$0/2uv3PvgDz73AwAyUaxH.5wdcGCd5DDP/s.FOIeRtnBJmxUxWl12', '보예'),
    ('partner@bibbidi.local', '$2y$10$0/2uv3PvgDz73AwAyUaxH.5wdcGCd5DDP/s.FOIeRtnBJmxUxWl12', '지훈');

INSERT INTO wedding_profile (owner_id, name, partner_name, wedding_date, total_budget)
VALUES (
    (SELECT id FROM users WHERE email = 'demo@bibbidi.local'),
    '보예와 지훈의 결혼식',
    '지훈',
    '2026-10-24',
    45000000
);

INSERT INTO wedding_member (wedding_profile_id, user_id, role, status, joined_at)
VALUES
    (
        (SELECT id FROM wedding_profile WHERE name = '보예와 지훈의 결혼식'),
        (SELECT id FROM users WHERE email = 'demo@bibbidi.local'),
        'OWNER',
        'ACTIVE',
        '2026-07-01 10:00:00'
    ),
    (
        (SELECT id FROM wedding_profile WHERE name = '보예와 지훈의 결혼식'),
        (SELECT id FROM users WHERE email = 'partner@bibbidi.local'),
        'PARTNER',
        'ACTIVE',
        '2026-07-01 10:05:00'
    );

INSERT INTO wedding_invitation (
    wedding_profile_id,
    invited_email,
    role,
    token,
    status,
    invited_at,
    expires_at,
    accepted_at,
    accepted_user_id
)
VALUES
    (
        (SELECT id FROM wedding_profile WHERE name = '보예와 지훈의 결혼식'),
        'planner@example.com',
        'PLANNER',
        'demo-planner-invitation-token',
        'PENDING',
        '2026-07-05 14:00:00',
        '2026-07-19 14:00:00',
        NULL,
        NULL
    );

INSERT INTO vendor_card (
    wedding_profile_id,
    category,
    name,
    status,
    current_selected,
    contract_date,
    total_amount,
    deposit_amount,
    balance_amount,
    balance_due_date,
    memo
)
VALUES
    (
        (SELECT id FROM wedding_profile WHERE name = '보예와 지훈의 결혼식'),
        'WEDDING_HALL',
        '라루체 컨벤션',
        'CONTRACTED',
        TRUE,
        '2026-06-28',
        24000000,
        3000000,
        21000000,
        '2026-09-24',
        '토요일 오후 예식. 보증 인원 200명, 생화 장식 포함.'
    ),
    (
        (SELECT id FROM wedding_profile WHERE name = '보예와 지훈의 결혼식'),
        'STUDIO',
        '온더무드 스튜디오',
        'IN_PROGRESS',
        TRUE,
        NULL,
        2200000,
        300000,
        1900000,
        '2026-08-20',
        '야외 컷 가능 여부 확인 필요. 원본 파일 포함 옵션 검토 중.'
    ),
    (
        (SELECT id FROM wedding_profile WHERE name = '보예와 지훈의 결혼식'),
        'DRESS',
        '모먼트 드레스',
        'CANDIDATE',
        FALSE,
        NULL,
        1800000,
        NULL,
        NULL,
        NULL,
        '1차 피팅 완료. 머메이드 라인은 예쁘지만 추가금이 큼.'
    ),
    (
        (SELECT id FROM wedding_profile WHERE name = '보예와 지훈의 결혼식'),
        'DRESS',
        '아뜰리에 루미에르',
        'CANDIDATE',
        TRUE,
        NULL,
        2100000,
        NULL,
        NULL,
        NULL,
        '2차 피팅 예약 완료. 실크 드레스 후보가 가장 마음에 듦.'
    ),
    (
        (SELECT id FROM wedding_profile WHERE name = '보예와 지훈의 결혼식'),
        'MAKEUP',
        '블룸 메이크업',
        'SCHEDULED',
        TRUE,
        NULL,
        950000,
        100000,
        850000,
        '2026-10-10',
        '혼주 메이크업 2인 추가 견적 확인 필요.'
    );

INSERT INTO vendor_option (vendor_card_id, name, extra_cost, memo, status)
VALUES
    (
        (SELECT id FROM vendor_card WHERE name = '라루체 컨벤션'),
        '채플홀 2시간 대관',
        0,
        '기본 패키지 포함',
        'SELECTED'
    ),
    (
        (SELECT id FROM vendor_card WHERE name = '라루체 컨벤션'),
        '생화 업그레이드',
        1200000,
        '버진로드와 포토테이블 생화 추가',
        'CANDIDATE'
    ),
    (
        (SELECT id FROM vendor_card WHERE name = '온더무드 스튜디오'),
        '원본 전체 파일',
        330000,
        '셀렉 전 전체 원본 제공',
        'SELECTED'
    ),
    (
        (SELECT id FROM vendor_card WHERE name = '아뜰리에 루미에르'),
        '본식 드레스 1벌',
        0,
        '실크 A라인 후보',
        'CANDIDATE'
    ),
    (
        (SELECT id FROM vendor_card WHERE name = '블룸 메이크업'),
        '혼주 메이크업 2인',
        300000,
        '양가 어머님 포함',
        'CANDIDATE'
    );

INSERT INTO payment_schedule (vendor_card_id, label, amount, due_date, paid, memo)
VALUES
    (
        (SELECT id FROM vendor_card WHERE name = '라루체 컨벤션'),
        '계약금',
        3000000,
        '2026-06-28',
        TRUE,
        '계약 당일 입금 완료'
    ),
    (
        (SELECT id FROM vendor_card WHERE name = '라루체 컨벤션'),
        '잔금',
        21000000,
        '2026-09-24',
        FALSE,
        '예식 30일 전 입금'
    ),
    (
        (SELECT id FROM vendor_card WHERE name = '온더무드 스튜디오'),
        '예약금',
        300000,
        '2026-07-15',
        FALSE,
        '촬영일 확정 후 입금'
    ),
    (
        (SELECT id FROM vendor_card WHERE name = '블룸 메이크업'),
        '예약금',
        100000,
        '2026-07-20',
        FALSE,
        '계좌 문자 수신 대기'
    );

INSERT INTO vendor_event (vendor_card_id, title, event_at, memo)
VALUES
    (
        (SELECT id FROM vendor_card WHERE name = '온더무드 스튜디오'),
        '스튜디오 상담',
        '2026-07-18 15:00:00',
        '샘플 앨범과 야외 촬영 가능 여부 확인'
    ),
    (
        (SELECT id FROM vendor_card WHERE name = '아뜰리에 루미에르'),
        '드레스 2차 피팅',
        '2026-07-27 11:00:00',
        '베일과 볼레로 조합도 같이 보기'
    ),
    (
        (SELECT id FROM vendor_card WHERE name = '블룸 메이크업'),
        '메이크업 리허설',
        '2026-08-09 13:30:00',
        '헤어 변형 2회 가능 여부 문의'
    );

INSERT INTO wedding_todo (wedding_profile_id, vendor_card_id, title, due_date, completed, memo)
VALUES
    (
        (SELECT id FROM wedding_profile WHERE name = '보예와 지훈의 결혼식'),
        (SELECT id FROM vendor_card WHERE name = '라루체 컨벤션'),
        '식대 최종 견적서 받기',
        '2026-07-22',
        FALSE,
        '보증 인원 200명 기준'
    ),
    (
        (SELECT id FROM wedding_profile WHERE name = '보예와 지훈의 결혼식'),
        (SELECT id FROM vendor_card WHERE name = '온더무드 스튜디오'),
        '촬영 컨셉 레퍼런스 정리',
        '2026-07-17',
        FALSE,
        '캐주얼 컷, 한복 컷 포함'
    ),
    (
        (SELECT id FROM wedding_profile WHERE name = '보예와 지훈의 결혼식'),
        NULL,
        '청첩장 문구 초안 작성',
        '2026-08-01',
        TRUE,
        '부모님 성함 표기 확인 완료'
    ),
    (
        (SELECT id FROM wedding_profile WHERE name = '보예와 지훈의 결혼식'),
        (SELECT id FROM vendor_card WHERE name = '블룸 메이크업'),
        '혼주 메이크업 추가 견적 확인',
        '2026-07-25',
        FALSE,
        '2인 기준 금액과 시작 시간 확인'
    );

INSERT INTO draft_vendor_card (
    wedding_profile_id,
    category,
    name,
    status,
    contract_date,
    total_amount,
    deposit_amount,
    balance_amount,
    balance_due_date,
    memo,
    source_message
)
VALUES
    (
        (SELECT id FROM wedding_profile WHERE name = '보예와 지훈의 결혼식'),
        'MAKEUP',
        '멜로우 메이크업',
        'NEEDS_COORDINATION',
        NULL,
        880000,
        100000,
        780000,
        '2026-10-01',
        '예약 가능 시간과 출장비 확인 필요',
        '멜로우 메이크업은 본식 88만원, 예약금 10만원이고 출장비는 지역별로 다르대.'
    );

INSERT INTO memo_analysis_suggestion (
    vendor_card_id,
    kind,
    payload_json,
    evidence_text,
    status,
    created_at,
    resolved_at
)
VALUES
    (
        (SELECT id FROM vendor_card WHERE name = '라루체 컨벤션'),
        'PAYMENT',
        '{"label":"잔금","amount":21000000,"dueDate":"2026-09-24"}',
        '예식 30일 전 잔금 입금',
        'PENDING',
        '2026-07-06 09:20:00',
        NULL
    ),
    (
        (SELECT id FROM vendor_card WHERE name = '온더무드 스튜디오'),
        'TODO',
        '{"title":"원본 파일 포함 여부 확정","dueDate":"2026-07-18"}',
        '원본 파일 포함 옵션 검토 중',
        'PENDING',
        '2026-07-06 09:25:00',
        NULL
    );

INSERT INTO vendor_change_history (
    wedding_profile_id,
    category,
    previous_card_id,
    new_card_id,
    previous_vendor_name,
    new_vendor_name,
    reason,
    changed_at
)
VALUES
    (
        (SELECT id FROM wedding_profile WHERE name = '보예와 지훈의 결혼식'),
        'DRESS',
        (SELECT id FROM vendor_card WHERE name = '모먼트 드레스'),
        (SELECT id FROM vendor_card WHERE name = '아뜰리에 루미에르'),
        '모먼트 드레스',
        '아뜰리에 루미에르',
        '실크 드레스 라인업과 피팅 일정이 더 잘 맞음',
        '2026-07-08 18:30:00'
    );
