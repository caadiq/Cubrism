# Cubrism


## :bookmark: 목차
+ [개요](#pushpin-개요)
+ [API Docs](#abacus-api-docs)
+ [적용 기술](#screwdriver-적용-기술)
+ [시스템 구조도](#gear-시스템-구조도)
+ [팀](#family_man_woman_boy_boy-팀)

</br>

## :pushpin: 개요
자격증은 개인의 전문성과 능력을 나타내는 중요한 지표에 해당한다. 따라서 우리는 자격증 취득을 위해 공부하는 현대인들로 하여금 정보 취득이 용이하도록, 관련 자료를 한데 모아 그 편의성을 높인 모바일 어플리케이션을 제작하였다. Cubrism은 Cube와 -ism을 결합한 단어로써 큐브와 같은 구조적이고 다면적인 기능을 통해 사용자들은 ‘관심 자격증’을 설정하여 본인이 취득하고자 하는 자격 정보를 간편하게 열람할 수 있으며, 스터디 그룹에 참가하여 목표를 세우고 함께 공부함으로써 동기부여를 높일 수 있다. 또한 Q&A 게시판을 통해 타 사용자들과 지식을 공유하고, 일정 관리 기능을 활용하여 계획적인 학습 환경을 조성하는 등 다양한 측면에서 유용한 도움을 얻을 수 있다.

</br>

## :abacus: API Docs
### 회원
---
<details>
<summary>회원가입</summary>

| HTTP | Path  |
| --- | --- |
| <code>POST</code> | /auth/signup |

#### 요청
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| email | String | 이메일 |
| password | String | 비밀번호 |
| nickname | String | 닉네임 |

```json
{
    "email": "test@test.com",
    "password": "password1!",
    "nickname": "nickname"
}
```

#### 응답
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| message | String | 결과 메시지 |
```json
{
    "message": "회원가입에 성공했습니다."
}
```
</details>

<details>
<summary>로그인</summary>

| HTTP | Path  |
| --- | --- |
| <code>POST</code> | /auth/signin |

#### 요청
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| email | String | 이메일 |
| password | String | 비밀번호 |
| fcmToken | String | FCM (Firebase Cloud Messaging) 토큰 |

```json
{
    "email": "test@test.com",
    "password": "password1!",
    "fcmToken": ""
}
```
#### 응답
##### user
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| email | String | 이메일 |
| nickname | String | 닉네임 |
| profileImage | String | 프로필 사진 Url |
| provider | String | 소셜 로그인 [Google, Kakao, null(이메일)] |

##### token
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| accessToken | String | JWT Access Token |
| refreshToken | String | JWT Refresh Token |

```json
{
    "user": {
        "email": "test@test.com",
        "nickname": "닉네임",
        "profileImage": "",
        "provider": null
    },
    "token": {
        "accessToken": "",
        "refreshToken": ""
    }
}
```
</details>

<details>
<summary>구글 로그인</summary>

| HTTP | Path  |
| --- | --- |
| <code>POST</code> | /auth/signin/google |

#### 요청
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| token | String | 구글 인증 토큰 |
| fcmToken | String | FCM (Firebase Cloud Messaging) 토큰 |

```json
{
    "token": "",
    "fcmToken": ""
}
```
#### 응답
##### user
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| email | String | 이메일 |
| nickname | String | 닉네임 |
| profileImage | String | 프로필 사진 Url |
| provider | String | 소셜 로그인 [Google, Kakao, null(이메일)] |

##### token
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| accessToken | String | JWT Access Token |
| refreshToken | String | JWT Refresh Token |

```json
{
    "user": {
        "email": "test@test.com",
        "nickname": "닉네임",
        "profileImage": "",
        "provider": "google"
    },
    "token": {
        "accessToken": "",
        "refreshToken": ""
    }
}
```
</details>

<details>
<summary>카카오 로그인</summary>

| HTTP | Path  |
| --- | --- |
| <code>POST</code> | /auth/signin/kakao |

#### 요청
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| token | String | 카카오 인증 토큰 |
| fcmToken | String | FCM (Firebase Cloud Messaging) 토큰 |

```json
{
    "token": "",
    "fcmToken": ""
}
```
#### 응답
##### user
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| email | String | 이메일 |
| nickname | String | 닉네임 |
| profileImage | String | 프로필 사진 Url |
| provider | String | 소셜 로그인 [Google, Kakao, null(이메일)] |

##### token
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| accessToken | String | JWT Access Token |
| refreshToken | String | JWT Refresh Token |

```json
{
    "user": {
        "email": "test@test.com",
        "nickname": "닉네임",
        "profileImage": "",
        "provider": "kakao"
    },
    "token": {
        "accessToken": "",
        "refreshToken": ""
    }
}
```
</details>

<details>
<summary>로그아웃</summary>

| HTTP | Path  |
| --- | --- |
| <code>POST</code> | /auth/logout |

#### 응답
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| message | String | 결과 메시지 |
```json
{
    "message": "로그아웃 완료"
}
```
</details>

<details>
<summary>로그인 유저 정보</summary>

| HTTP | Path  |
| --- | --- |
| <code>GET</code> | /auth/users |

#### 요청
##### 헤더
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| Authorization | String | JWT Access Token |

```
Authorization: Bearer Token
```
#### 응답
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| email | String | 이메일 |
| nickname | String | 닉네임 |
| profileImage | String | 프로필 사진 Url |
| provider | String | 소셜 로그인 [Google, Kakao, null(이메일)] |
```json
{
    "email": "test@test.com",
    "nickname": "닉네임",
    "profileImage": "",
    "provider": null
}
```
</details>

<details>
<summary>회원 정보 수정</summary>

| HTTP | Path  |
| --- | --- |
| <code>PUT</code> | /auth/users |

#### 요청
##### 헤더
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| Authorization | String | JWT Access Token |

```
Authorization: Bearer Token
```
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| nickname | String | 닉네임 |
| imageUrl | String | 프로필 사진 Url |
| isImageChange | Boolean | 프로필 사진 변경 여부 |

```json
{
    "nickname": "닉네임",
    "imageUrl": "",
    "isImageChange": true
}
```
#### 응답
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| email | String | 이메일 |
| nickname | String | 닉네임 |
| profileImage | String | 프로필 사진 Url |
| provider | String | 소셜 로그인 [Google, Kakao, null(이메일)] |
```json
{
    "email": "test@test.com",
    "nickname": "닉네임",
    "profileImage": "",
    "provider": null
}
```
</details>

<details>
<summary>회원 탈퇴</summary>

| HTTP | Path  |
| --- | --- |
| <code>DELETE</code> | /auth/users |

#### 요청
##### 헤더
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| Authorization | String | JWT Access Token |

```
Authorization: Bearer Token
```

#### 응답
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| message | String | 결과 메시지 |
```json
{
    "message": "회원 탈퇴가 완료되었습니다."
}
```
</details>

<details>
<summary>이메일 인증 번호 요청</summary>

| HTTP | Path  |
| --- | --- |
| <code>POST</code> | /auth/signup/email/request |

#### 요청
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| email | String | 이메일 |

```json
{
    "email": "test@test.com"
}
```
#### 응답
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| message | String | 결과 메시지 |
```json
{
    "message": "이메일 전송 완료"
}
```
</details>

<details>
<summary>이메일 인증 번호 인증</summary>

| HTTP | Path  |
| --- | --- |
| <code>POST</code> | /auth/signup/email/verify |

#### 요청
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| email | String | 이메일 |
| code | String | 인증 코드 |

```json
{
    "email": "test@test.com",
    "code": "000000"
}
```
#### 응답
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| message | String | 결과 메시지 |
```json
{
    "message": "이메일 인증 완료"
}
```
</details>

<details>
<summary>비밀번호 초기화 요청</summary>

| HTTP | Path  |
| --- | --- |
| <code>POST</code> | /auth/users/password |

#### 요청
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| email | String | 이메일 |

```json
{
    "email": "test@test.com"
}
```
#### 응답
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| message | String | 결과 메시지 |
```json
{
    "message": "이메일을 전송했습니다."
}
```
</details>

<details>
<summary>Access Token 재발급</summary>

| HTTP | Path  |
| --- | --- |
| <code>POST</code> | /auth/token/access |

#### 요청
##### 헤더
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| AccessToken | String | JWT Access Token |
| RefreshToken | String | JWT Refresh Token |

```
AccessToken: Bearer Token
RefreshToken: Token
```
#### 응답
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| accessToken | String | JWT Access Token |
| refreshToken | String | JWT Refresh Token |
```json
{
    "accessToken": "",
    "refreshToken": null
}
```
</details>

<details>
<summary>Refresh Token 재발급</summary>

| HTTP | Path  |
| --- | --- |
| <code>POST</code> | /auth/token/refresh |

#### 요청
##### 헤더
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| Authorization | String | JWT Access Token |

```
Authorization: Bearer Token
```

#### 응답
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| accessToken | String | JWT Access Token |
| refreshToken | String | JWT Refresh Token |
```json
{
    "accessToken": null,
    "refreshToken": ""
}
```
</details>

</br>

### 자격증
---
<details>
<summary>전체 자격증 목록</summary>

| HTTP | Path  |
| --- | --- |
| <code>GET</code> | /qualification/list/all |

#### 응답
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| code | String | 자격증 코드 |
| name | String | 자격증 이름 |
```json
[
    {
        "code": "1320",
        "name": "정보처리기사"
    },
    ···
]
```
</details>

<details>
    
<summary>대직무분야명 목록</summary>

| HTTP | Path  |
| --- | --- |
| <code>GET</code> | /qualification/list/majorfield |

#### 응답
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| majorFieldName | String | 대직무분야명 |
| iconUrl | String | 아이콘 Url |
```json
[
    {
        "majorFieldName": "정보통신",
        "iconUrl": ""
    },
    ···
]
```
</details>

<details>
<summary>중직무분야명 목록</summary>

| HTTP | Path  |
| --- | --- |
| <code>GET</code> | /qualification/list/middlefield |

#### 요청
##### 파라미터
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| field | String | 대직무분야명 |

```
field: 정보통신
```

#### 응답
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| middleFieldName | String | 중직무분야명 |
| code | String | 자격증 코드 |
| name | String | 자격증 이름 |
```json
[
    {
        "middleFieldName": "정보기술",
        "code": "1320",
        "name": "정보처리기사"
    },
    ···
]
```
</details>

<details>
<summary>자격증 세부정보</summary>

| HTTP | Path  |
| --- | --- |
| <code>GET</code> | /qualification/details |

#### 요청
##### 파라미터
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| code | String | 자격증 코드 |

```
field: 정보통신
```

#### 응답
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| name | String | 자격증 이름 |
| code | String | 자격증 코드 |
| tendency | String | 출제 경향 |
| acquisition | String | 취득 방법 |

##### schedule (시험 일정)
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| category | String | 구분 |
| writtenApp | String | 필기원서접수 |
| writtenExam | String | 필기시험 |
| writtenExamResult | String | 필기합격발표 |
| practicalApp | String | 실기원서접수 |
| practicalExam | String | 실기시험 |
| practicalExamResult | String | 최종합격발표 |

##### fee (수수료)
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| writtenFee | Int | 필기 수수료 |
| practicalFee | Int | 실기 수수료 |

##### standard (출제 기준)
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| filePath | String | 파일 경로 |
| fileName | String | 파일 이름 |

##### question (공개 문제)
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| filePath | String | 파일 경로 |
| fileName | String | 파일 이름 |

##### books (추천 도서)
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| title | String | 책 제목 |
| authors | String | 저자 |
| publisher | String | 출판사 |
| date | LocalDate | 출간일 |
| price | Int | 정가 |
| sale_price | Int | 판매가 |
| thumbnail | String | 책 표지 |
| url | String | 링크 |

```json
{
    "code": "1320",
    "name": "정보처리기사",
    "schedule": [
        {
            "category": "2024년 정기 기사 1회",
            "writtenApp": "2024.01.23~2024.01.26",
            "writtenExam": "2024.02.15~2024.03.07",
            "writtenExamResult": "2024.03.13",
            "practicalApp": "2024.03.26~2024.03.29",
            "practicalExam": "2024.04.27~2024.05.17",
            "practicalExamResult": "2024.06.18"
        },
        ···
    ],
    "fee": {
        "writtenFee": 19400,
        "practicalFee": 22600
    },
    "tendency": "<실기시험 출제 경향>\n정보시스템 등의 개발 요구 사항을 이해하여 각 업무에 맞는 소프트웨어의 기능에 관한 설계, 구현 및 테스트를 수행에 필요한\n1. 현행 시스템 분석 및 요구사항 확인(소프트웨어 공학 기술의 요구사항 분석 기법 활용)···",
    "standard": [
        {
            "filePath": "bbs/Q006/Q006_2204043",
            "fileName": "정보처리기사 출제기준(2020.1.1.~2022.12.31).hwp"
        },
        ···
    ],
    "question": [],
    "acquisition": "① 시 행 처 : 한국산업인력공단\n② 관련학과 : 모든 학과 응시가능\n③ 시험과목\n- 필기 1. 소프트웨어설계 2. 소프트웨어개발···",
    "books": [
        {
            "title": "정보처리기사 필기 한권으로 끝내기",
            "authors": "메인에듀 정보기술연구소, 김대영",
            "publisher": "메인에듀",
            "date": "2024-04-02",
            "price": 29000,
            "sale_price": 26100,
            "thumbnail": "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F6614487%3Ftimestamp%3D20240427153255",
            "url": "https://search.daum.net/search?w=bookpage&bookId=6614487&q=%EC%A0%95%EB%B3%B4%EC%B2%98%EB%A6%AC%EA%B8%B0%EC%82%AC+%ED%95%84%EA%B8%B0+%ED%95%9C%EA%B6%8C%EC%9C%BC%EB%A1%9C+%EB%81%9D%EB%82%B4%EA%B8%B0"
        },
        ···
    ]
}
```
</details>

<br>

### 게시판
---
<details>
<summary>게시글 목록</summary>

| HTTP | Path  |
| --- | --- |
| <code>GET</code> | /posts |

#### 요청
##### 파라미터
| 이름 | 타입 | 설명 | 필수 |
| --- | --- | --- | --- |
| page | Int | 페이지 | X (기본값 0) |
| limit | Int | 한 번에 불러올 게시글 수 (0~50) | X (기본값 20) |
| search-query | String | 검색어 | X |
| board-id | Int | 게시판 번호 | O |

```
page: 0
limit: 20
search-query: 정보처리기사
board-id: 1
```
#### 응답
##### page (페이지)
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| previousPage | Int | 이전 페이지 |
| currentPage | Int | 현재 페이지 |
| nextPage | Int | 다음 페이지 |

##### postList (게시글 목록)
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| postId | Int | 게시글 번호 |
| category | String | 카테고리 |
| nickname | String | 작성자 닉네임 |
| imageUrl | String | 첫 번째 사진 Url |
| title | String | 제목 |
| content | String | 내용 |
| createdDate | String | 작성일 |
| commentCount | Int | 댓글 수 |
```json
{
    "page": {
        "previousPage": null,
        "currentPage": 0,
        "nextPage": 1
    },
    "postList": [
        {
            "postId": 208,
            "category": "정보처리기사",
            "nickname": "nickname",
            "imageUrl": "",
            "title": "정처기 필기는 언제인가요?",
            "content": "2차때 신청하려고 하는데, 신청 기간은 언제인가요?",
            "createdDate": "2일 전",
            "commentCount": 0
        },
        ···
    ]
}
```
</details>

<details>
<summary>내 게시글 목록</summary>

| HTTP | Path  |
| --- | --- |
| <code>GET</code> | /posts/my |

#### 요청
##### 헤더
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| Authorization | String | JWT Access Token |

```
Authorization: Bearer Token
```
##### 파라미터
| 이름 | 타입 | 설명 | 필수 |
| --- | --- | --- | --- |
| page | Int | 페이지 | X (기본값 0) |
| limit | Int | 한 번에 불러올 게시글 수 (0~50) | X (기본값 20) |

```
page: 0
limit: 20
```
#### 응답
##### page (페이지)
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| previousPage | Int | 이전 페이지 |
| currentPage | Int | 현재 페이지 |
| nextPage | Int | 다음 페이지 |

##### postList (게시글 목록)
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| postId | Int | 게시글 번호 |
| category | String | 카테고리 |
| nickname | String | 작성자 닉네임 |
| profileImage | String | 프로필 사진 Url |
| images | String[] | 사진 Url 목록 |
| title | String | 제목 |
| content | String | 내용 |
| createdDate | String | 작성일 |
| commentCount | Int | 댓글 수 |
```json
{
    "page": {
        "previousPage": null,
        "currentPage": 0,
        "nextPage": 1
    },
    "postList": [
        {
            "postId": 208,
            "category": "정보처리기사",
            "nickname": "nickname",
            "profileImage": "",
            "images": [
                "",
                "",
                ""
            ],
            "title": "정처기 필기는 언제인가요?",
            "content": "2차때 신청하려고 하는데, 신청 기간은 언제인가요?",
            "createdDate": "5.21 16:25",
            "commentCount": 0
        },
        ···
    ]
}
```
</details>

<details>
<summary>관심 자격증 게시글 목록</summary>

| HTTP | Path  |
| --- | --- |
| <code>GET</code> | /posts/favorites |

#### 요청
##### 헤더
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| Authorization | String | JWT Access Token |

```
Authorization: Bearer Token
```
##### 파라미터
| 이름 | 타입 | 설명 | 필수 |
| --- | --- | --- | --- |
| page | Int | 페이지 | X (기본값 0) |
| limit | Int | 한 번에 불러올 게시글 수 (0~50) | X (기본값 20) |
| board-id | Int | 게시판 번호 | O |

```
page: 0
limit: 20
board-id: 1
```
#### 응답
##### page (페이지)
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| previousPage | Int | 이전 페이지 |
| currentPage | Int | 현재 페이지 |
| nextPage | Int | 다음 페이지 |

##### postList (게시글 목록)
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| postId | Int | 게시글 번호 |
| category | String | 카테고리 |
| nickname | String | 작성자 닉네임 |
| imageUrl | String | 첫 번째 사진 Url |
| title | String | 제목 |
| content | String | 내용 |
| createdDate | String | 작성일 |
| commentCount | Int | 댓글 수 |
```json
{
    "page": {
        "previousPage": null,
        "currentPage": 0,
        "nextPage": 1
    },
    "postList": [
        {
            "postId": 208,
            "category": "정보처리기사",
            "nickname": "nickname",
            "imageUrl": "",
            "title": "정처기 필기는 언제인가요?",
            "content": "2차때 신청하려고 하는데, 신청 기간은 언제인가요?",
            "createdDate": "2일 전",
            "commentCount": 0
        },
        ···
    ]
}
```
</details>

<details>
<summary>게시글 보기</summary>

| HTTP | Path  |
| --- | --- |
| <code>GET</code> | /post/{postId} |

#### 응답
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| postId | Int | 게시글 번호 |
| boardName | String | 게시판 이름 |
| category | String | 카테고리 |
| nickname | String | 작성자 닉네임 |
| profileImageUrl | String | 프로필 사진 Url |
| email | String | 작성자 이메일 |
| title | String | 제목 |
| content | String | 내용 |
| createdDate | LocalDateTime | 작성 날짜 |
| modifiedDate | LocalDateTime | 수정 날짜 |
| images | String[] | 사진 목록 |
| aiComment | String | GPT-4o 답변 |

##### comments
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| commentId | Int | 댓글 번호 |
| replyTo | Int | 대댓글 대상 번호 |
| replyToNickname | String | 대댓글 대상 닉네임 |
| nickname | String | 작성자 닉네임 |
| email | String | 작성자 이메일 |
| content | String | 댓글 내용 |
| createdDate | LocalDateTime | 작성 날짜 |
| profileImageUrl | String | 프로필 사진 Url |
| isUpdated | Boolean | 댓글 수정 여부 |
```json
{
    "postId": 208,
    "boardName": "QnA",
    "category": "정보처리기사",
    "nickname": "nickname",
    "profileImageUrl": "",
    "email": "test@test.com",
    "title": "정처기 필기는 언제인가요?",
    "content": "2차때 신청하려고 하는데, 신청 기간은 언제인가요?",
    "createdDate": "2024-05-21T16:25:28",
    "modifiedDate": "2024-05-21T16:25:34",
    "images": [
        "",
        "",
        ""
    ],
    "comments": [
        {
            "commentId": 64,
            "replyTo": 38,
            "replyToNickname": "nickname",
            "nickname": "nickname2",
            "email": "test2@test.com",
            "content": "정보처리기사는 이 앱의 자격증 정보 목록에서 확인하세요",
            "createdDate": "2024-05-22T11:22:01",
            "profileImageUrl": "",
            "isUpdated": false
        },
        ···
    ],
    "aiComment": ""
}    
```
</details>

<details>
<summary>게시글 작성</summary>

| HTTP | Path  |
| --- | --- |
| <code>POST</code> | /post |

#### 요청
##### 헤더
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| Authorization | String | JWT Access Token |

```
Authorization: Bearer Token
```
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| boardId | Int | 게시판 번호 |
| title | String | 제목 |
| content | String | 내용 |
| category | String | 카테고리 |
| images | String[] | 사진 목록 |
```json
{
    "boardId": 1,
    "title": "정처기 필기는 언제인가요?",
    "content": "2차때 신청하려고 하는데, 신청 기간은 언제인가요?",
    "category": "정보처리기사",
    "images": [
        "",
        "",
        ""
    ]
}
```

#### 응답
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| message | String | 결과 메시지 |
```json
{
    "message": "게시글을 작성했습니다."
}
```
</details>

<details>
<summary>게시글 삭제</summary>

| HTTP | Path  |
| --- | --- |
| <code>DELETE</code> | /post/{postId} |

#### 요청
##### 헤더
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| Authorization | String | JWT Access Token |

```
Authorization: Bearer Token
```

#### 응답
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| message | String | 결과 메시지 |
```json
{
    "message": "게시글을 삭제했습니다."
}
```
</details>

<details>
<summary>게시글 수정</summary>

| HTTP | Path  |
| --- | --- |
| <code>PUT</code> | /post/{postId} |

#### 요청
##### 헤더
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| Authorization | String | JWT Access Token |

```
Authorization: Bearer Token
```
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| title | String | 제목 |
| content | String | 내용 |
| category | String | 카테고리 |
| images | String[] | 추가할 사진 목록 |
| removedImages | String[] | 삭제할 사진 목록 |
```json
{
    "title": "정처기 필기는 언제인가요?",
    "content": "2차때 신청하려고 하는데, 신청 기간은 언제인가요?",
    "category": "정보처리기사",
    "images": [
        "",
        "",
        ""
    ],
    "removedImages": [
        "",
        "",
        ""
    ]
}

```

#### 응답
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| message | String | 결과 메시지 |
```json
{
    "message": "게시글을 수정했습니다."
}
```
</details>

<details>
<summary>댓글 작성</summary>

| HTTP | Path  |
| --- | --- |
| <code>POST</code> | /comment |

#### 요청
##### 헤더
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| Authorization | String | JWT Access Token |

```
Authorization: Bearer Token
```
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| postId | Int | 게시글 번호 |
| content | String | 내용 |
```json
{
    "postId": 208,
    "content": "정보처리기사는 이 앱의 자격증 정보 목록에서 확인하세요"
}

```

#### 응답
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| message | String | 결과 메시지 |
```json
{
    "message": "댓글을 추가했습니다."
}
```
</details>

<details>
<summary>댓글 삭제</summary>

| HTTP | Path  |
| --- | --- |
| <code>DELETE</code> | /comment/{commentId} |

#### 요청
##### 헤더
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| Authorization | String | JWT Access Token |

```
Authorization: Bearer Token
```

#### 응답
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| message | String | 결과 메시지 |
```json
{
    "message": "댓글을 삭제했습니다."
}
```
</details>

<details>
<summary>댓글 수정</summary>

| HTTP | Path  |
| --- | --- |
| <code>PUT</code> | /comment/{commentId} |

#### 요청
##### 헤더
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| Authorization | String | JWT Access Token |

```
Authorization: Bearer Token
```
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| content | String | 내용 |
```json
{
    "content": "아니면 큐넷에서 확인하셔도 돼요"
}

```

#### 응답
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| message | String | 결과 메시지 |
```json
{
    "message": "댓글을 수정했습니다."
}
```
</details>

<details>
<summary>대댓글 작성</summary>

| HTTP | Path  |
| --- | --- |
| <code>POST</code> | /reply |

#### 요청
##### 헤더
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| Authorization | String | JWT Access Token |

```
Authorization: Bearer Token
```
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| postId | Int | 게시글 번호 |
| commentId | Int | 대댓글 대상 번호 |
| content | String | 내용 |
```json
{
    "postId": 208,
    "commentId": 64,
    "content": "감사합니다"
}

```

#### 응답
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| message | String | 결과 메시지 |
```json
{
    "message": "대댓글을 작성했습니다."
}
```
</details>

<br>

### 일정
---
<details>
<summary>일정 추가</summary>

| HTTP | Path  |
| --- | --- |
| <code>POST</code> | /schedule |

#### 요청
##### 헤더
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| Authorization | String | JWT Access Token |

```
Authorization: Bearer Token
```
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| startDate | LocalDateTime | 시작 날짜 |
| endDate | LocalDateTime | 종료 날짜 |
| isAllDay | Boolean | 종일 여부 |
| title | String | 제목 |
| content | String | 내용 |

```json
{
    "startDate": "2024-05-24T09:00:00",
    "endDate": "2024-05-24T12:00:00",
    "isAllDay": false,
    "title": "정보처리기사 필기 시험 준비",
    "content": "기출문제 풀어보기"
}
```
#### 응답
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| message | String | 결과 메시지 |

```json
{
    "message": "일정이 추가되었습니다."
}
```
</details>

<details>
<summary>일정 삭제</summary>

| HTTP | Path  |
| --- | --- |
| <code>DELETE</code> | /schedule/{scheduleId} |

#### 요청
##### 헤더
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| Authorization | String | JWT Access Token |

```
Authorization: Bearer Token
```

#### 응답
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| message | String | 결과 메시지 |

```json
{
    "message": "일정을 삭제했습니다."
}
```
</details>

<details>
<summary>일정 수정</summary>

| HTTP | Path  |
| --- | --- |
| <code>PUT</code> | /schedule/{scheduleId} |

#### 요청
##### 헤더
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| Authorization | String | JWT Access Token |

```
Authorization: Bearer Token
```
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| startDate | LocalDateTime | 시작 날짜 |
| endDate | LocalDateTime | 종료 날짜 |
| isAllDay | Boolean | 종일 여부 |
| title | String | 제목 |
| content | String | 내용 |

```json
{
    "startDate": "2024-05-24T00:00:00",
    "endDate": "2024-05-24T00:00:00",
    "isAllDay": true,
    "title": "정보처리기사 필기 시험 준비",
    "content": "기출문제 풀어보기"
}
```
#### 응답
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| message | String | 결과 메시지 |

```json
{
    "message": "일정을 수정했습니다."
}
```
</details>

<details>
<summary>일정 목록</summary>

| HTTP | Path  |
| --- | --- |
| <code>GET</code> | /schedules |

#### 요청
##### 헤더
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| Authorization | String | JWT Access Token |

```
Authorization: Bearer Token
```
##### 파라미터
| 이름 | 타입 | 설명 | 필수 |
| --- | --- | --- | --- |
| year | Int | 년 | X (기본값 null) |
| month | Int | 월 | X (기본값 null) |

```
year: 2024
month: 5
```
#### 응답
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| scheduleId | Int | 일정 번호 |
| startDate | LocalDateTime | 시작 날짜 |
| endDate | LocalDateTime | 종료 날짜 |
| title | String | 제목 |
| content | String | 내용 |
| allDay | Boolean | 종일 여부 |

```json
[
    {
        "scheduleId": 102,
        "startDate": "2024-05-24T00:00:00",
        "endDate": "2024-05-24T00:00:00",
        "title": "정보처리기사 필기 시험 준비",
        "content": "기출문제 풀어보기",
        "allDay": true
    },
    ···
]
```
</details>

<br>

### 스터디 그룹
---
<details>
<summary>스터디 그룹 생성</summary>

| HTTP | Path  |
| --- | --- |
| <code>POST</code> | /studygroup |

#### 요청
##### 헤더
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| Authorization | String | JWT Access Token |
```
Authorization: Bearer Token
```
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| groupName | String | 스터디 그룹 이름 |
| groupDescription | String | 스터디 그룹 소개 |
| maxMembers | Int | 최대 인원수 |
| tags | String[] | 태그 목록 |
```json
{
    "groupName": "정보처리기사 같이 따실분 구해요~",
    "groupDescription": "정보처리기사 자격증 준비 같이 하실 분들 구합니다!",
    "maxMembers": 4,
    "tags": [
        "정보처리기사",
        "열공"
    ]
}
```
#### 응답
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| message | String | 결과 메시지 |
```json
{
    "message": "스터디 그룹을 생성했습니다."
}
```
</details>

<details>
<summary>스터디 그룹 삭제</summary>

| HTTP | Path  |
| --- | --- |
| <code>DELETE</code> | /studygroup/{groupId} |

#### 요청
##### 헤더
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| Authorization | String | JWT Access Token |
```
Authorization: Bearer Token
```
#### 응답
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| message | String | 결과 메시지 |
```json
{
    "message": "스터디 그룹을 삭제했습니다."
}
```
</details>

<details>
<summary>스터디 그룹 목록</summary>

| HTTP | Path  |
| --- | --- |
| <code>GET</code> | /studygroups |

#### 요청
##### 파라미터
| 이름 | 타입 | 설명 | 필수 |
| --- | --- | --- | --- |
| page | Int | 페이지 | X (기본값 0) |
| limit | Int | 한 번에 불러올 게시글 수 (0~50) | X (기본값 20) |
| recruiting | Boolean | 모집 완료 포함 (true: 포함, false: 제외) | X (기본값 true) |

```
page: 0
limit: 20
recruiting: true
```
#### 응답
##### page (페이지)
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| previousPage | Int | 이전 페이지 |
| currentPage | Int | 현재 페이지 |
| nextPage | Int | 다음 페이지 |

##### studyGroupList (스터디 그룹 목록)
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| studyGroupId | Int | 스터디 그룹 번호 |
| groupName | String | 스터디 그룹 이름 |
| groupDescription | String | 스터디 그룹 소개 |
| currentMembers | Int | 현재 인원수 |
| maxMembers | Int | 최대 인원수 |
| tags | String[] | 태그 목록 |
| recruiting | Boolean | 모집 여부 (true: 모집 중, false: 모집 완료) |
```json
{
    "page": {
        "previousPage": null,
        "currentPage": 0,
        "nextPage": 1
    },
     "studyGroupList": [
        {
            "studyGroupId": 57,
            "groupName": "정보처리기사 같이 따실분 구해요~",
            "groupDescription": "정보처리기사 자격증 준비 같이 하실 분들 구합니다!",
            "currentMembers": 2,
            "maxMembers": 4,
            "tags": [
                "정보처리기사",
                "열공"
            ],
            "recruiting": true
        },
        ···
    ]
}
```
</details>

<details>
<summary>내가 가입한 스터디 그룹 목록</summary>

| HTTP | Path  |
| --- | --- |
| <code>GET</code> | /studygroups/my |

#### 요청
##### 헤더
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| Authorization | String | JWT Access Token |
```
Authorization: Bearer Token
```
##### 파라미터
| 이름 | 타입 | 설명 | 필수 |
| --- | --- | --- | --- |
| page | Int | 페이지 | X (기본값 0) |
| limit | Int | 한 번에 불러올 게시글 수 (0~50) | X (기본값 20) |

```
page: 0
limit: 20
```
#### 응답
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| studyGroupId | Int | 스터디 그룹 번호 |
| groupName | String | 스터디 그룹 이름 |
| groupDescription | String | 스터디 그룹 소개 |
| currentMembers | Int | 현재 인원수 |
| maxMembers | Int | 최대 인원수 |
| tags | String[] | 태그 목록 |
| recruiting | Boolean | 모집 여부 (true: 모집 중, false: 모집 완료) |
```json
[
    {
        "studyGroupId": 57,
        "groupName": "정보처리기사 같이 따실분 구해요~",
        "groupDescription": "정보처리기사 자격증 준비 같이 하실 분들 구합니다!",
        "currentMembers": 2,
        "maxMembers": 4,
        "tags": [
            "정보처리기사",
            "열공"
        ],
        "recruiting": true
    },
    ···
]
```
</details>

<details>
<summary>스터디 그룹 정보</summary>

| HTTP | Path  |
| --- | --- |
| <code>GET</code> | /studygroup/{groupId} |

#### 응답
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| studyGroupId | Int | 스터디 그룹 번호 |
| groupName | String | 스터디 그룹 이름 |
| groupDescription | String | 스터디 그룹 소개 |
| groupAdmin | String | 스터디 그룹 관리자 닉네임 |
| currentMembers | Int | 현재 인원수 |
| maxMembers | Int | 최대 인원수 |
| tags | String[] | 태그 목록 |
| members | String[] | 스터디 그룹 멤버 이메일 목록 |
| recruiting | Boolean | 모집 여부 (true: 모집 중, false: 모집 완료) |
```json
{
    "studyGroupId": 57,
    "groupName": "정보처리기사 같이 따실분 구해요~",
    "groupDescription": "정보처리기사 자격증 준비 같이 하실 분들 구합니다!",
    "groupAdmin": "nickname",
    "adminProfileImage": "",
    "currentMembers": 2,
    "maxMembers": 4,
    "tags": [
        "정보처리기사",
        "열공"
    ],
    "members": [
        "test@test.com"
    ],
    "recruiting": true
}
```
</details>

<details>
<summary>스터디 그룹 가입 신청</summary>

| HTTP | Path  |
| --- | --- |
| <code>POST</code> | /studygroup/join/request/{groupId} |

#### 요청
##### 헤더
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| Authorization | String | JWT Access Token |
```
Authorization: Bearer Token
```
#### 응답
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| message | String | 결과 메시지 |
```json
{
    "message": "스터디 그룹 가입을 요청했습니다."
}
```
</details>

<details>
<summary>스터디 그룹 가입 신청 취소</summary>

| HTTP | Path  |
| --- | --- |
| <code>DELETE</code> | /studygroup/join/request/{memberId} |

#### 요청
##### 헤더
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| Authorization | String | JWT Access Token |
```
Authorization: Bearer Token
```
#### 응답
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| message | String | 결과 메시지 |
```json
{
    "message": "스터디 그룹 가입 신청을 취소했습니다."
}
```
</details>

<details>
<summary>스터디 그룹 가입 신청 목록</summary>

| HTTP | Path  |
| --- | --- |
| <code>GET</code> | /studygroup/join/requests |

#### 요청
##### 헤더
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| Authorization | String | JWT Access Token |
```
Authorization: Bearer Token
```
#### 응답
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| memberId | UUID | 가입 신청 번호 |
| groupName | String | 스터디 그룹 이름 |
| groupDescription | String | 스터디 그룹 소개 |
| tags | String[] | 태그 목록 |
| groupAdmin | String | 스터디 그룹 관리자 닉네임 |
| groupAdminProfileImage | String | 스터디 그룹 관리자 프로필 사진 Url |
| requestDate | LocalDateTime | 신청 날짜 |
```json
[
    {
        "memberId": "f71e4cac-1b5a-4099-85ff-3573d862976b",
        "groupName": "정보처리기사 같이 따실분 구해요~",
        "groupDescription": "정보처리기사 자격증 준비 같이 하실 분들 구합니다!",
        "tags": [
            "정보처리기사",
            "열공"
        ],
        "groupAdmin": "nickname",
        "groupAdminProfileImage": "",
        "requestDate": "2024-05-23T19:10:31"
    },
    ···
]
```
</details>

<details>
<summary>스터디 그룹 가입 승인</summary>

| HTTP | Path  |
| --- | --- |
| <code>PUT</code> | /studygroup/join/receive/{memberId} |

#### 요청
##### 헤더
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| Authorization | String | JWT Access Token |
```
Authorization: Bearer Token
```
#### 응답
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| message | String | 결과 메시지 |
```json
{
    "message": "스터디 그룹 가입을 승인했습니다."
}
```
</details>

<details>
<summary>스터디 그룹 가입 거절</summary>

| HTTP | Path  |
| --- | --- |
| <code>DELETE</code> | /studygroup/join/receive/{memberId} |

#### 요청
##### 헤더
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| Authorization | String | JWT Access Token |
```
Authorization: Bearer Token
```
#### 응답
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| message | String | 결과 메시지 |
```json
{
    "message": "스터디 그룹 가입을 거절했습니다."
}
```
</details>

<details>
<summary>해당 스터디 그룹 가입 신청 목록</summary>

| HTTP | Path  |
| --- | --- |
| <code>GET</code> | /studygroup/join/receives/{groupId} |

#### 요청
##### 헤더
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| Authorization | String | JWT Access Token |
```
Authorization: Bearer Token
```
#### 응답
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| memberId | UUID | 가입 신청 번호 |
| groupName | String | 스터디 그룹 이름 |
| userName | String | 가입 신청한 유저 닉네임 |
| userImage | String | 가입 신청한 유저 프로필 사진 Url |
| requestDate | LocalDateTime | 신청 날짜 |
```json
[
    {
        "memberId": "f71e4cac-1b5a-4099-85ff-3573d862976b",
        "groupName": "정보처리기사 같이 따실분 구해요~",
        "userName": "nickname",
        "userImage": "",
        "requestDate": "2024-05-23T19:10:31"
    },
    ···
]
```
</details>

<details>
<summary>스터디 그룹 목표 추가</summary>

| HTTP | Path  |
| --- | --- |
| <code>POST</code> | /studygroup/goal |

#### 요청
##### 헤더
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| Authorization | String | JWT Access Token |
```
Authorization: Bearer Token
```
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| studyGroupId | Int | 스터디 그룹 번호 |
| goalName | String | 목표 이름 |
```json
{
    "studyGroupId": 57,
    "goalName": "필기 시험 합격"
}
```
#### 응답
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| message | String | 결과 메시지 |
```json
{
    "message": "목표를 추가했습니다."
}
```
</details>

<details>
<summary>스터디 그룹 목표 삭제</summary>

| HTTP | Path  |
| --- | --- |
| <code>DELETE</code> | /studygroup/goal/{goalId} |

#### 요청
##### 헤더
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| Authorization | String | JWT Access Token |
```
Authorization: Bearer Token
```
#### 응답
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| message | String | 결과 메시지 |
```json
{
    "message": "목표를 삭제했습니다."
}
```
</details>

<details>
<summary>스터디 그룹 목표 목록</summary>

| HTTP | Path  |
| --- | --- |
| <code>GET</code> | /studygroup/{groupId}/goals |

#### 요청
##### 헤더
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| Authorization | String | JWT Access Token |
```
Authorization: Bearer Token
```
#### 응답
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| goalId | Int | 목표 번호 |
| goalName | String | 목표 이름 |
```json
[
    {
        "goalId": 1,
        "goalName": "필기 시험 합격"
    },
    ···
]
```
</details>

<details>
<summary>스터디 그룹 목표 달성 인증</summary>

| HTTP | Path  |
| --- | --- |
| <code>POST</code> | /studygroup/goal/submit |

#### 요청
##### 헤더
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| Authorization | String | JWT Access Token |
```
Authorization: Bearer Token
```
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| goalId | Int | 목표 번호 |
| groupId | Int | 스터디 그룹 번호 |
| content | String | 인증 내용 |
| imageUrl | String | 인증 사진 Url |
```json
{
    "goalId": 1,
    "groupId": 57,
    "content": "필기 시험 합격했습니다!",
    "imageUrl": ""
}
```
#### 응답
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| message | String | 결과 메시지 |
```json
{
    "message": "목표를 달성했습니다."
}
```
</details>

<details>
<summary>스터디 그룹 목표 달성 목록</summary>

| HTTP | Path  |
| --- | --- |
| <code>GET</code> | /studygroup/goal/submits/{groupId} |

#### 요청
##### 헤더
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| Authorization | String | JWT Access Token |
```
Authorization: Bearer Token
```
#### 응답
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| userGoalId | Int | 유저 목표 번호 |
| nickname | String | 제출자 닉네임 |
| profileImageUrl | String | 제출자 프로필 사진 Url |
| content | String | 인증 내용 |
| imageUrl | String | 인증 사진 Url |
| submittedAt | LocalDateTime | 제출 날짜 |
| goalName | String | 목표 이름 |
```json
[
    {
        "userGoalId": 1,
        "nickname": "테스트",
        "profileImageUrl": "",
        "content": "필기 시험 합격했습니다!",
        "imageUrl": "",
        "submittedAt": "2024-05-23T19:18:17",
        "goalName": "필기 시험 합격"
    }
]
```
</details>

<details>
<summary>스터디 그룹 목표 달성 승인</summary>

| HTTP | Path  |
| --- | --- |
| <code>PUT</code> | /studygroup/goal/submit/{userGoalId} |

#### 요청
##### 헤더
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| Authorization | String | JWT Access Token |
```
Authorization: Bearer Token
```
#### 응답
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| message | String | 결과 메시지 |
```json
{
    "message": "목표 달성을 승인했습니다."
}
```
</details>

</details>

<details>
<summary>스터디 그룹 목표 달성 거절</summary>

| HTTP | Path  |
| --- | --- |
| <code>DELETE</code> | /studygroup/goal/submit/{userGoalId} |

#### 요청
##### 헤더
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| Authorization | String | JWT Access Token |
```
Authorization: Bearer Token
```
#### 응답
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| message | String | 결과 메시지 |
```json
{
    "message": "목표 달성을 거절했습니다."
}
```
</details>

<details>
<summary>스터디 그룹 D-Day 설정</summary>

| HTTP | Path  |
| --- | --- |
| <code>POST</code> | /studygroup/dday |

#### 요청
##### 헤더
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| Authorization | String | JWT Access Token |
```
Authorization: Bearer Token
```
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| groupId | Int | 스터디 그룹 번호 |
| title | String | D-Day 제목 |
| day | DateTime | D-Day 날짜 |
```json
{
    "groupId": 57,
    "title": "정보처리기사 자격증 따기",
    "day": "2024-12-31"
}
```
#### 응답
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| message | String | 결과 메시지 |
```json
{
    "message": "D-Day를 설정했습니다."
}
```
</details>

<details>
<summary>스터디 그룹 D-Day 정보</summary>

| HTTP | Path  |
| --- | --- |
| <code>GET</code> | /studygroup/{groupId}/dday |

#### 요청
##### 헤더
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| Authorization | String | JWT Access Token |
```
Authorization: Bearer Token
```
#### 응답
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| groupId | Int | 스터디 그룹 번호 |
| title | String | D-Day 제목 |
| day | DateTime | D-Day 날짜 |
```json
{
    "groupId": 57,
    "title": "정보처리기사 자격증 따기",
    "day": "2024-12-31"
}
```
</details>

<details>
<summary>스터디 그룹 정보</summary>

| HTTP | Path  |
| --- | --- |
| <code>GET</code> | /studygroup/{groupId}/enter |

#### 요청
##### 헤더
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| Authorization | String | JWT Access Token |
```
Authorization: Bearer Token
```
#### 응답
##### 본문
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| groupName | String | 스터디 그룹 이름 |

##### members (멤버 목록)
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| nickname | String | 닉네임 |
| email | String | 이메일 |
| profileImage | String | 프로필 사진 Url |
| admin | Boolean | 관리자 여부 |

##### UserGoal (목표 개인 진행 상황)
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| completionPercentage | Double | 목표 달성률 |

##### goals (목표 목록)
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| goalId | Int | 목표 번호 |
| goalName | String | 목표 이름 |
| completed | Boolean | 목표 달성 여부 |
| submitted | Boolean | 목표 달성 인증 제출 여부 |

##### day
| 이름 | 타입 | 설명 |
| --- | --- | --- |
| groupId | Int | 스터디 그룹 번호 |
| title | String | D-Day 제목 |
| day | DateTime | D-Day 날짜 |
```json
{
    "groupName": "정보처리기사 같이 따실분 구해요~",
    "members": [
        {
            "nickname": "nickname",
            "email": "test@test.com",
            "profileImage": "",
            "admin": true,
            "userGoal": {
                "goals": [
                    {
                        "goalId": 1,
                        "goalName": "정보처리기사 자격증 따기",
                        "completed": false,
                        "submitted": false
                    },
                    ···
                ],
                "completionPercentage": 0
            }
        },
        ···
    ],
    "day": {
        "groupId": 57,
        "title": "정보처리기사 자격증 따기",
        "day": "2024-12-31"
    }
}
```
</details>

<br>

## :screwdriver: 적용 기술
<ul>
  <li>Language: <img src="https://img.shields.io/badge/java-000000?style=for-the-badge&logo=openjdk&logoColor=white"> <img src="https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=HTML5&logoColor=white"></li>
  <li>Database: <img src="https://img.shields.io/badge/redis-DC382D?style=for-the-badge&logo=redis&logoColor=white"> <img src="https://img.shields.io/badge/mariadb-003545?style=for-the-badge&logo=mariadb&logoColor=white"> </li>
  <li>Framework: <img src="https://img.shields.io/badge/spring boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"> <img src="https://img.shields.io/badge/spring security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white"></li>
  <li> tool: <img src="https://img.shields.io/badge/intellij idea-000000?style=for-the-badge&logo=intellijidea&logoColor=white"></li>
</ul>

</br>

## :gear: 시스템 구조도
![시스템 구조도](https://raw.githubusercontent.com/caadiq/Cubrism/master/image/%EC%8B%9C%EC%8A%A4%ED%85%9C%20%EA%B5%AC%EC%84%B1%EB%8F%84.png)

</br>

## :family_man_woman_boy_boy: 팀
<table>
  <tr>
    <th colspan="2">프론트엔드</th>
    <th colspan="2">백엔드</th>
  </tr>
  <tr>
    <td align="center">
      <img src="https://avatars.githubusercontent.com/u/149460377?v=4" alt="황윤구" style="width:150px;height:150px;">
    </td>
    <td align="center">
      <img src="https://avatars.githubusercontent.com/u/149464663?v=4" alt="안해연" style="width:150px;height:150px;">
    </td>
    <td align="center">
      <img src="https://avatars.githubusercontent.com/u/10990331?v=4" alt="반명우" style="width:150px;height:150px;">
    </td>
    <td align="center">
      <img src="https://avatars.githubusercontent.com/u/74907427?v=4" alt="김승상" style="width:150px;height:150px;">
    </td>
  </tr>
  <tr>
    <td align="center">
      <a href="https://github.com/hyg0527">황윤구</a>
    </td>
    <td align="center">
      <a href="https://github.com/haeyeon623">안해연</a>
    </td>
    <td align="center">
      <a href="https://github.com/caadiq">반명우</a>
    </td>
    <td align="center">
      <a href="https://github.com/seungsang2000">김승상</a>
    </td>
  </tr>
</table>
