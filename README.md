<h1>🔗Connet : Connect Network</h1>
<p>
  <img src='https://github.com/user-attachments/assets/bbaf9bc7-0469-47fd-8116-79203291d049' width="100"/></img>
  
  모든 아이디어와 대화를 하나로, GPT와 연결된 새로운 커뮤니티 Connet</br>
  프리픽스 '$' 입력만으로 GPT 기능을 손쉽게 사용할 수 있으며, Discord와 Slack의 협업 기능을 제공합니다. </br>
  Connet 서비스 👉 http://woohyeon.duckdns.org/login 👈
</p>
</br>
<h2 id="technologies">🛠️ 기술</h2>

| Category | Stack |
| --- | --- |
| Language | Java |
| Framework | Spring Boot |
| Library | Spring Data JPA, Query DSL |
| Database | My SQL |
| Infra | Docker |

</br>
<h2>💻주요 화면 및 기능</h2>

### 1. 그룹 채팅
- 오픈 그룹, 프라이빗 그룹 등 다양한 조건으로 그룹을 생성할 수 있습니다.
- 오픈 그룹을 입장하거나 프라이빗 그룹을 초대 받아 그룹을 입장할 수 있습니다.
- 그룹장은 그룹을 수정하거나 채널, 탭을 생성, 수정할 권한이 주어집니다.

### 2. 친구 & 1:1 채팅
- 친구 요청, 차단 등의 기능을 제공합니다.
- 1:1 채팅 기능을 제공합니다. 차단된 유저에게는 채팅을 전송할 수 없습니다.

### 3. Gpt 서비스
- 기존 GPT 채팅 서비스를 Connet에서 동일하게 사용할 수 있습니다.

### 4. 부가 기능
- 채팅에 이모지를 등록하고 조회할 수 있습니다.
- 읽지 않은 채팅은 목록을 통해 표시됩니다.
- 유저들의 실시간 접속 정보를 확인할 수 있습니다.
</br>

<h2>🛠️구현 사항</h2>

### 1. 도메인 모델 패턴 적용
비즈니스 로직을 객체 지향적으로 구현하여 시스템의 복잡성을 관리하고 유지보수성을 향상시켰습니다.

### 2. 애그리게이트 구성
관련된 엔티티와 값 객체를 하나의 애그리게이트로 묶어 관리함으로써 데이터의 일관성과 무결성을 유지하였습니다.
각 애그리게이트는 하나의 루트 엔티티를 가지며, 이 루트 엔티티를 통해서만 하위 엔티티에 접근하고 조작할 수 있도록 구현하였습니다. 

### 3. Event publish-subscribe 구조
서비스에서 다른 어그리게이트에 관한 서비스 로직이 필요한 경우 Event를 통해 핸들링할 수 있는 구조를 채택하였습니다.
pub-sub구조를 통해 서비스간 결합도를 낮추어 OCP원칙을 지키고자 하였고, 서비스를 독립적으로 테스트할 수 있도록 구현하였습니다.
</br>

<h2>🤔고민할 요소</h2>
- 강한 일관성을 가진 도메인의 집합을 어그리게이트로 구성하였습니다. 하지만 어그리게이트는 루트 엔티티를 통해서만 접근할 수 있고 하위 엔티티를 직접적으로 접근할 수 없습니다.
하위 엔티티를 대량으로 수정하기 위해 루트 엔티티에서 하위 엔티티를 수정하고 dirty check를 통해 update하였습니다. 수정해야하는 하위엔티티가 n개라면 update 쿼리가 n개 발생합니다.
상황에 따라 일관성을 포기하고 성능을 위해 직접적으로 어그리게이트에 수정을 가해도 되는지 고민이 많이 되었습니다. </br></br>
- 엔티티를 보수적으로 사용하기 위해 database에서 조회를 할때 dto를 통해 반환하였고 필요에 따라 fetch join을 활용하였습니다. 또한 엔티티를 파라미터로 넘기는 것에 있어 신중하게 구현하였습니다. 이를 통해 예상치 못한 lazy loading, dirty check를 통한 변경을 방지할 수 있었지만 JPA의 이점 또한 상쇄되는것 같았습니다. JPA의 기능들을 어디까지 사용해도 좋을지 trade-off에 대해 생각하고 개선해야 합니다. 또한 ORM기술이 아닌 SQl Mapper를 사용해도 괜찮을것 같다는 생각이 들었습니다.
</br>

<h2>📄문서</h2>

| 종류   | URL                   |
|--------|----------------------------------------------|
| API 명세서 | https://thene.notion.site/API-1808113bb5c38076ac93dac21587e209?pvs=73 |
| E-R 다이어그램   | https://thene.notion.site/E-R-1808113bb5c3807cbcc9ca6092287e30?pvs=73 |
</br>

</br>
<h2>📺최종 결과물</h2>


https://github.com/user-attachments/assets/8915bc49-a034-43df-a60f-890b5a583460

제한된 업로드 크기 제한으로 특정 기능만 시연하였습니다.

</br>

<h2>👥팀원 및 일정</h2>
2024.10.08 ~

| 이름   | 역할      | GitHub 프로필 링크                             |
|--------|-----------|----------------------------------------------|
| 정우현 | 팀장, FE | [@woohyeonDev](https://github.com/woohyeonDev) |
| Hoon   | FE        | [@simba-HOON](https://github.com/simba-HOON) |
| 윤소은 | FE        | [@Dubabbi](https://github.com/Dubabbi)       |
| 곽유나 | BE        | [@kyoona](https://github.com/kyoona)         |
