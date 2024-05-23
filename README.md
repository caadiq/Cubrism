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
##### 파라미터
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
##### 파라미터
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
##### 파라미터
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
##### 파라미터
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
##### 파라미터
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
