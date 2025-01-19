<h1>Connet : Connect Network🔗</h1>
<h1 style="font-weight: bold">Connet : Connect Network✨</h1>
<p>
  약속을 지키는 그날까지 <b>"약속을 편리하게 관리하고, 재미있게 지각을 방지하자”</b></br></br>
  본 서비스는 (구)https://github.com/kyoona/AiKu_backend 를 개선하여 서비스 런칭을 목표로 하고 있습니다.</br>
  기존 서버는 단일 서버로 구성되었지만 MSA를 목표로 다시 구현중에 있습니다.
</p>
<p align="center">기간 | 2024.08.05 ~ 진행중</p>
<p align="center">팀원 | 곽유나, 최원탁</p>

<p align="center">
 <a href="">API 명세서</a> • 
  <a href="">E-R 다이어그램</a> • 
  <a href="">Entity 설계</a> • 
</p>
<br/>

<h2 id="technologies">🛠️ 기술</h2>

| Category | Stack |
| --- | --- |
| Language | Java |
| Framework | Spring Boot |
| Library | Spring Data JPA, Spring Cloud Gateway, Query DSL |
| Database | H2 |
| Infra | AWS, Kafka, Docker, Kubernetes |
| Cloud Service | Firebase Messaging |

</br>
<h2>💻주요 화면 및 기능</h2>

### 1. 그룹 생성 및 그룹 내 약속 생성
- 그룹을 생성하고 카카오톡 url 공유를 통해 사용자를 초대할 수 있습니다.
- 그룹 내 약속을 생성할 수 있고 사용자는 자유롭게 약속에 참가할 수 있습니다.
- 약속 참가자는 무료 참가자인 '깍두기'와 유료 참가자인 '일반 참가자'로 나뉩니다.
- '일반 참가자'는 약속 시간의 30분 전까지 '꼴찌 고르기' 베팅을 할 수 있습니다.

### 2. 맵 & 실시간 위치 공유
- 약속 시간 30분 전 알림과 함께 맵 기능이 열립니다.
- 맵에서 참가자들의 실시간 위치를 확인할 수 있습니다.
- '일반 참가자'끼리 포인트를 걸고 '레이싱'게임을 진행할 수 있습니다.
- 참가자 모두가 약속 장소에 도착하거나, 약속 시간 30분 후에 알림과 함께 맵이 종료됩니다.

### 3. 결과 분석
- 맵이 종료된 후 도착 순위와 베팅 결과를 확인할 수 있습니다.
- 그룹 내 모든 약속 결과(지각 순위, 베팅 승률 등)를 분석한 결과를 확인할 수 있습니다.

### 4. 포인트 상점
- 사용자는 결제를 통해 현금을 서비스 내 머니인 '아쿠'로 전환할 수 있습니다.
- 스케줄을 통해서 얻은 '아쿠'로 모바일 상품권을 구매할 수 있습니다.

<h2>🌊진행 과정</h2>

### 1. 이벤트 스토밍
이벤트 스토밍을 통해 서비스 기획과 로직에 대해 이해관계를 일치시키는 과정을 거쳤습니다.
1. 이벤트를 탐색한다.
2. 타임라인에 맞게 이벤트를 정리한다.
3. 이벤트를 발생시키는 커멘드를 탐색한다.
4. 이벤트에 해당하는 정책을 추가한다.
5. 외부 시스템을 보강한다.
6. 어그리거트를 탐색한다.
7. 바운디드 컨텍스트의 경계를 찾는다.
<img width="791" alt="스크린샷 2024-10-02 오전 1 42 22" src="https://github.com/user-attachments/assets/07875df6-7639-43ba-a4cf-4859af1f117d">

상세히 확대하면 다음과 같습니다.
![Group 2](https://github.com/user-attachments/assets/98a1b5de-6ff5-4d44-9f8b-99132ecc0ce9)

### 2. 바운디드 컨텍스트 분리
1. 서로 연관된 어그리거트를 찾고 경계를 구분한다.
2. 어그리거트 루트를 지정한다.

![Group 3](https://github.com/user-attachments/assets/bfa06bc3-6e59-4ce6-9727-c11824c805d1)

### 3. MSA 설계를 한다.
