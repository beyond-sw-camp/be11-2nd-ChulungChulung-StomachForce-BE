
<h2 align="center">먹고 모이는 공간 - 뱃살력</h2>
<p align="center">
  <img src="https://github.com/beyond-sw-camp/be11-2nd-ChulungChulung-StomachForce-FE/blob/main/src/assets/stomach.png?raw=true" width="400">
</p>



## 📌 팀원소개
<div align="center">
<table>
  <tr>
    <td align="center" width="150">
      <img src="https://github.com/beyond-sw-camp/be11-2nd-ChulungChulung-StomachForce-FE/blob/main/src/assets/%EC%8B%AC%EC%88%9C.jpeg?raw=true" width="120" height="120">
      <hr>
      <b>임성재</b><br>팀장
    </td>
    <td align="center" width="150">
      <img src="https://github.com/beyond-sw-camp/be11-2nd-ChulungChulung-StomachForce-FE/blob/main/src/assets/%EB%A7%88%EC%A7%80%EC%8B%9C%EB%AF%93%E3%84%B4.png?raw=true" width="120" height="120">
      <hr>
      <b>최영일</b><br>팀원
    </td>
    <td align="center" width="150">
      <img src="https://github.com/beyond-sw-camp/be11-2nd-ChulungChulung-StomachForce-FE/blob/main/src/assets/%EB%B0%94%ED%8A%B8%EC%8B%AC%EC%8A%A8.jpeg?raw=true" width="120" height="120">
      <hr>
      <b>최영재</b><br>팀원
    </td>
    <td align="center" width="150">
      <img src="https://github.com/beyond-sw-camp/be11-2nd-ChulungChulung-StomachForce-FE/blob/main/src/assets/%EB%B0%94%ED%8A%B8%EC%8B%AC%EC%8A%A8.png?raw=true" width="120" height="120">
      <hr>
      <b>홍성혁</b><br>팀원
    </td>
  </tr>
</table>
</div>


## 📌 목차
1. [프로젝트 소개](#프로젝트-소개)
2. [기획](#기획)
3. [기술 스택](#기술-스택)
4. [프론트엔드 기능 시연](#프론트엔드-기능-시연)
5. [프로젝트 마무리](#프로젝트-마무리)


## 프로젝트 소개

<img src="https://github.com/beyond-sw-camp/be11-2nd-ChulungChulung-StomachForce-FE/blob/main/src/assets/stomach.png?raw=true" width="40" align="left" />

### **뱃살력이란?**

맛집을 찾고 예약하는 과정이 번거롭고, 사용자 간의 소통이 단절된 기존 시스템을 개선하기 위해 **뱃살력**을 기획하게 되었습니다.

단순한 예약 플랫폼이 아닌, 외식 경험을 더욱 **기억에 남고 특별하게 만드는 커뮤니티 공간**을 제공하고자 했습니다.

사용자는 직관적인 인터페이스로 손쉽게 레스토랑을 탐색하고, 게시글을 통해 정보를 확인하며 다양한 사용자들과 소통할 수 있습니다. 이를 통해 **보다 즐거운 외식 문화**를 만들어 보고자 했습니다.

또한, 예약하는 사용자뿐만 아니라 레스토랑을 운영하는 사업자도 뱃살력을 통해 **편리한 가게 홍보 및 운영**이 가능하도록 서비스를 설계하였습니다.


###  배경 및 목적
- 레스토랑 예약과 SNS를 하나로 통합  
- 효율적이고 직관적인 맛집 탐색 및 예약 시스템  
- 실시간 공지 및 이벤트 확인으로 최신 정보 제공  
- 커뮤니티 기능을 통한 사용자 간 소통 극대화  
- 편리한 고객센터를 통한 문의 및 신고 접수
- 쉬운 메뉴 설정 및 가게 홍보

### 🔍 사용한 핵심 기술 & 코드

| 사용 기술                            | 설명                                                                                                                                              |
|------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------|
| Redis를 통한 동시성 이슈 처리        | Redis를 활용해, 여러 사용자가 동시에 좋아요를 눌러도 숫자가 꼬이지 않도록 안전하게 처리했습니다. 서버가 여러 대로 나눠져 있는 분산 환경에서도, Redis가 좋아요 수를 한 번에 정확하게 처리해줘서 **동시성 이슈가 발생하지 않고** 안정적인 서비스 운영이 가능했습니다. |
| 배타락을 통한 동시성 이슈 처리       | 예약 생성 시, 동시에 여러 요청이 들어와 **중복 예약**이 발생할 수 있는 상황을 방지하기 위해, 레포지토리 레벨에서 **배타락**을 적용하였습니다. 이를 통해 데이터 무결성을 확보하고 안정적인 예약 시스템을 구축하였습니다. |
| 다양한 로그인 방식 도입             | 사용자 편의성을 높이기 위해 **자체 회원가입/로그인** 기능 외에도, **구글 로그인 서비스**을 도입하였습니다. 이를 통해 다양한 사용자층의 접근성을 높이고, 빠른 회원가입 및 로그인 경험을 제공합니다. |
| 사용자 중심의 화면 설계 및 UX 최적화 | 다양한 사용자의 요구들 반영하여 **방대한 UI 화면을 설계**하고 구현하였습니다. 단순한 화면 배치가 아닌, 실제 사용 흐름을 고려해 **직관적인 인터페이스**와 **편리한 사용자 경험**을 제공하기 위해 반복적인 기획 및 피드백 과정을 거쳤습니다. |

각 기능의 특성과 우선순위에 따라, Redis와 배타락을 적절히 활용하여 동시성 이슈를 안정적으로 해결하였습니다.<br><br>

### 🔍 주요 기능 요약 화면
✅ **홈화면에서 유명 레스토랑과 인기 인플루언서를 확인 가능**  
<img src="https://github.com/beyond-sw-camp/be11-2nd-ChulungChulung-StomachForce-FE/blob/main/giffolder/%ED%99%88%ED%8E%98%EC%9D%B4%EC%A7%80%20%EC%B4%88%EA%B8%B0%ED%99%94%EB%A9%B4.gif?raw=true" width="600"><br><br><br>
✅ **레스토랑 리스트를 통해 원하는 식당 예약 가능**  
<img src="https://github.com/beyond-sw-camp/be11-2nd-ChulungChulung-StomachForce-FE/blob/main/giffolder/reservation_create.gif?raw=true" width="600"><br><br><br>

✅ **좋아요 및 댓글 기능으로 맛집 추천 및 공유 활성화**  
<img src="https://github.com/beyond-sw-camp/be11-2nd-ChulungChulung-StomachForce-FE/blob/main/giffolder/%EA%B2%8C%EC%8B%9C%EA%B8%80%EC%83%81%EC%84%B8_%EC%A2%8B%EC%95%84%EC%9A%94_%EB%8C%93%EA%B8%80%EC%9E%91%EC%84%B1.gif?raw=true" width="600"><br><br><br>

✅ **쉬운 레스토랑 메뉴 등록**  
<img src="https://github.com/beyond-sw-camp/be11-2nd-ChulungChulung-StomachForce-FE/blob/main/giffolder/%EB%A9%94%EB%89%B4_%EB%93%B1%EB%A1%9D_%EC%88%98%EC%A0%95_%EC%82%AD%EC%A0%9C.gif?raw=true" width="600">

## 기획
📌 **요구사항 정의**  
📌 **ERD 설계**  
📌 **화면 설계 (FIGMA)**  

🔗 [요구사항 정의서 보러가기](https://docs.google.com/spreadsheets/d/1DBmJCVfnOjsCebl_T0jYjfnuHkb_mHtqmZW0du6Pse4/edit?usp=sharing)  
🔗 [ERD 보러가기](https://www.erdcloud.com/d/wSEbrXDob5fz8JEo5)  
🔗 [화면설계서(FIGMA) 보러가기](https://www.figma.com/design/vi3vWmmWRPv8dq9VBIt2Oc/%EB%B1%83%EC%82%B4%EB%A0%A5?node-id=0-1&t=Xts1hEkIqwq3ehUJ-1)  


 ##  기술 스택  
### 📌 Backend  
<div>
  <img src="https://img.shields.io/badge/Spring-6DB33F?style=flat-square&logo=Spring&logoColor=white">
  <img src="https://img.shields.io/badge/SpringBoot-6DB33F?style=flat-square&logo=SpringBoot&logoColor=white">
</div>

### 📌 Frontend  
<div>
  <img src="https://img.shields.io/badge/CSS3-1572B6?style=flat-square&logo=CSS3&logoColor=white">
  <img src="https://img.shields.io/badge/JavaScript-F7DF1E?style=flat-square&logo=JavaScript&logoColor=black">
  <img src="https://img.shields.io/badge/Vue.js-4FC08D?style=flat-square&logo=Vue.js&logoColor=white">
</div>

### 📌 DB  
<div>
  <img src="https://img.shields.io/badge/MariaDB-003545?style=flat-square&logo=MariaDB&logoColor=white">
  <img src="https://img.shields.io/badge/Redis-DC382D?style=flat-square&logo=Redis&logoColor=white">
</div>

### 📌 Etc  
<div>
  <img src="https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=Docker&logoColor=white">
</div>

### 📌 Communication  
<div>
  <img src="https://img.shields.io/badge/Discord-5865F2?style=flat-square&logo=Discord&logoColor=white">
  <img src="https://img.shields.io/badge/GitHub-181717?style=flat-square&logo=GitHub&logoColor=white">
</div>

 ##  요구사항정의에 따른 프론트엔드 기능 시연  
<details>
  <summary>📂회원 & 관리자 </summary>
  <details>
    <summary> ▶️ 일반회원-회원가입</summary>
    <img src="https://github.com/beyond-sw-camp/be11-2nd-ChulungChulung-StomachForce-FE/blob/main/giffolder/%EC%9D%BC%EB%B0%98%ED%9A%8C%EC%9B%90_%ED%9A%8C%EC%9B%90%EA%B0%80%EC%9E%85.gif?raw=true" width="600">
  </details>
  <details>
    <summary> ▶️ 일반유저로그인</summary>
    <img src="https://github.com/beyond-sw-camp/be11-2nd-ChulungChulung-StomachForce-FE/blob/main/giffolder/%EC%9D%BC%EB%B0%98%EC%9C%A0%EC%A0%80%EB%A1%9C%EA%B7%B8%EC%9D%B8.gif?raw=true" width="600">
  </details>
  <details>
    <summary>▶️ 예약생성</summary>
    <img src="https://github.com/beyond-sw-camp/be11-2nd-ChulungChulung-StomachForce-FE/blob/main/giffolder/reservation_create.gif?raw=true" width="600">
  </details>
  <details>
    <summary>▶️ 예약내역-조회-삭제-상세화면보기</summary>
    <img src="https://github.com/beyond-sw-camp/be11-2nd-ChulungChulung-StomachForce-FE/blob/main/giffolder/%EC%98%88%EC%95%BD%EB%82%B4%EC%97%AD_%EC%A1%B0%ED%9A%8C_%EC%82%AD%EC%A0%9C_%EC%83%81%EC%84%B8%ED%99%94%EB%A9%B4%EB%B3%B4%EA%B8%B0.gif?raw=true" width="600">
  </details>
  <details>
    <summary>▶️ 게시글 신고</summary>
    <img src="https://github.com/beyond-sw-camp/be11-2nd-ChulungChulung-StomachForce-FE/blob/main/giffolder/%EA%B2%8C%EC%8B%9C%EA%B8%80_%EC%8B%A0%EA%B3%A0.gif?raw=true" width="600">
  </details>
    <details>
  <summary>▶️ 게시글상세-좋아요-댓글작성</summary>
    <img src="https://github.com/beyond-sw-camp/be11-2nd-ChulungChulung-StomachForce-FE/blob/main/giffolder/%EA%B2%8C%EC%8B%9C%EA%B8%80%EC%83%81%EC%84%B8_%EC%A2%8B%EC%95%84%EC%9A%94_%EB%8C%93%EA%B8%80%EC%9E%91%EC%84%B1.gif?raw=true" width="600">
  </details>
    <details>
  <summary>▶️ 게시글작성-댓글작성-수정-삭제</summary>
    <img src="https://github.com/beyond-sw-camp/be11-2nd-ChulungChulung-StomachForce-FE/blob/main/giffolder/%EA%B2%8C%EC%8B%9C%EA%B8%80%EC%9E%91%EC%84%B1_%EB%8C%93%EA%B8%80%EC%9E%91%EC%84%B1_%EC%88%98%EC%A0%95_%EC%82%AD%EC%A0%9C.gif?raw=true" width="600">
  </details>
    <details>
  <summary>▶️ 내정보-조회-수정</summary>
    <img src="https://github.com/beyond-sw-camp/be11-2nd-ChulungChulung-StomachForce-FE/blob/main/giffolder/%EB%82%B4%EC%A0%95%EB%B3%B4_%EC%A1%B0%ED%9A%8C_%EC%88%98%EC%A0%95.gif?raw=true" width="600">
  </details>
    <details>
  <summary>▶️ 팔로우기능구현</summary>
    <img src="https://github.com/beyond-sw-camp/be11-2nd-ChulungChulung-StomachForce-FE/blob/main/giffolder/%ED%8C%94%EB%A1%9C%EC%9A%B0%EA%B8%B0%EB%8A%A5%EA%B5%AC%ED%98%84.gif?raw=true" width="600">
  </details>
    <details>
  <summary>▶️ 차단기능구현</summary>
    <img src="https://github.com/beyond-sw-camp/be11-2nd-ChulungChulung-StomachForce-FE/blob/main/giffolder/%EC%B0%A8%EB%8B%A8%EA%B8%B0%EB%8A%A5%EA%B5%AC%ED%98%84.gif?raw=true" width="600">
  </details>
    <details>
  <summary>▶️ 회원_리뷰_회원의 리뷰 삭제</summary>
    <img src="https://github.com/beyond-sw-camp/be11-2nd-ChulungChulung-StomachForce-FE/blob/main/giffolder/%ED%9A%8C%EC%9B%90_%EB%A6%AC%EB%B7%B0_%ED%9A%8C%EC%9B%90%EC%9D%98%20%EB%A6%AC%EB%B7%B0%20%EC%82%AD%EC%A0%9C.gif?raw=true" width="600">
  </details>
    <details>
  <summary>▶️ 회원_리뷰_회원의 리뷰 작성+별점</summary>
    <img src="https://github.com/beyond-sw-camp/be11-2nd-ChulungChulung-StomachForce-FE/blob/main/giffolder/%ED%9A%8C%EC%9B%90_%EB%A6%AC%EB%B7%B0_%ED%9A%8C%EC%9B%90%EC%9D%98%20%EB%A6%AC%EB%B7%B0%20%EC%9E%91%EC%84%B1+%EB%B3%84%EC%A0%90.gif?raw=true" width="600">
  </details>
  <details>
  <summary>▶️ 회원_리뷰_회원의 리뷰 작성_예약여부확인</summary>
    <img src="https://github.com/beyond-sw-camp/be11-2nd-ChulungChulung-StomachForce-FE/blob/main/giffolder/%ED%9A%8C%EC%9B%90_%EB%A6%AC%EB%B7%B0_%ED%9A%8C%EC%9B%90%EC%9D%98%20%EB%A6%AC%EB%B7%B0%20%EC%9E%91%EC%84%B1_%EC%98%88%EC%95%BD%EC%97%AC%EB%B6%80%ED%99%95%EC%9D%B8.gif?raw=true" width="600">
  </details>
  <details>
  <summary>▶️ 회원_리뷰_회원이 리뷰 수정</summary>
    <img src="https://github.com/beyond-sw-camp/be11-2nd-ChulungChulung-StomachForce-FE/blob/main/giffolder/%ED%9A%8C%EC%9B%90_%EB%A6%AC%EB%B7%B0_%ED%9A%8C%EC%9B%90%EC%9D%B4%20%EB%A6%AC%EB%B7%B0%20%EC%88%98%EC%A0%95.gif?raw=true" width="600">
  </details>
  <details>
  <summary>▶️ 회원관리-상태수정</summary>
    <img src="https://github.com/beyond-sw-camp/be11-2nd-ChulungChulung-StomachForce-FE/blob/main/giffolder/%ED%9A%8C%EC%9B%90%EA%B4%80%EB%A6%AC_%EC%83%81%ED%83%9C%EC%88%98%EC%A0%95.gif?raw=true" width="600">
  </details>
</details><br>
<details>
  <summary>📂 레스토랑</summary>
  <details>
  <summary>▶️ 레스토랑 회원가입</summary>
  <img src="https://github.com/beyond-sw-camp/be11-2nd-ChulungChulung-StomachForce-FE/blob/main/giffolder/%EB%A0%88%EC%8A%A4%ED%86%A0%EB%9E%91_%EC%82%AC%EC%97%85%EC%9E%90%20%ED%9A%8C%EC%9B%90%EA%B0%80%EC%9E%85.gif?raw=true" width="600">
</details>
  <details>
  <summary>▶️ 레스토랑 로그인</summary>
  <img src="https://github.com/beyond-sw-camp/be11-2nd-ChulungChulung-StomachForce-FE/blob/main/giffolder/%EB%A0%88%EC%8A%A4%ED%86%A0%EB%9E%91_%EC%82%AC%EC%97%85%EC%9E%90%20%EB%A1%9C%EA%B7%B8%EC%9D%B8.gif?raw=true" width="600">
</details>
  <details>
  <summary>▶️ 레스토랑 리뷰조회</summary>
  <img src="https://github.com/beyond-sw-camp/be11-2nd-ChulungChulung-StomachForce-FE/blob/main/giffolder/%EB%A0%88%EC%8A%A4%ED%86%A0%EB%9E%91_%EB%A0%88%EC%8A%A4%ED%86%A0%EB%9E%91%20%EB%A6%AC%EB%B7%B0%20%EC%A1%B0%ED%9A%8C.gif?raw=true" width="600">
</details>
  <details>
  <summary>▶️ 레스토랑 정보 수정</summary>
  <img src="https://github.com/beyond-sw-camp/be11-2nd-ChulungChulung-StomachForce-FE/blob/main/giffolder/%EB%A0%88%EC%8A%A4%ED%86%A0%EB%9E%91_%EB%A0%88%EC%8A%A4%ED%86%A0%EB%9E%91%20%EC%A0%95%EB%B3%B4%20%EC%88%98%EC%A0%95.gif?raw=true" width="600">
</details>
  <details>
  <summary>▶️ 레스토랑 정보조회 정보</summary>
  <img src="https://github.com/beyond-sw-camp/be11-2nd-ChulungChulung-StomachForce-FE/blob/main/giffolder/%EB%A0%88%EC%8A%A4%ED%86%A0%EB%9E%91_%EB%A0%88%EC%8A%A4%ED%86%A0%EB%9E%91%20%EC%A0%95%EB%B3%B4%20%EC%A1%B0%ED%9A%8C+%EC%A0%95%EB%B3%B4.gif?raw=true" width="600">
</details>
  <details>
  <summary>▶️ 레스토랑-이름 별 레스토랑 검색</summary>
  <img src="https://github.com/beyond-sw-camp/be11-2nd-ChulungChulung-StomachForce-FE/blob/main/giffolder/%EB%A0%88%EC%8A%A4%ED%86%A0%EB%9E%91_%EC%9D%B4%EB%A6%84%20%EB%B3%84%20%EB%A0%88%EC%8A%A4%ED%86%A0%EB%9E%91%20%EA%B2%80%EC%83%89.gif?raw=true" width="600">
</details>
  <details>
  <summary>▶️ 레스토랑-주소 별 레스토랑 검색</summary>
  <img src="https://github.com/beyond-sw-camp/be11-2nd-ChulungChulung-StomachForce-FE/blob/main/giffolder/%EB%A0%88%EC%8A%A4%ED%86%A0%EB%9E%91_%EC%A3%BC%EC%86%8C%20%EB%B3%84%20%EB%A0%88%EC%8A%A4%ED%86%A0%EB%9E%91%20%EA%B2%80%EC%83%89.gif?raw=true" width="600">
</details>
  <details>
  <summary>▶️ 전체 레스토랑 조회</summary>
  <img src="https://github.com/beyond-sw-camp/be11-2nd-ChulungChulung-StomachForce-FE/blob/main/giffolder/%EB%A0%88%EC%8A%A4%ED%86%A0%EB%9E%91_%EC%A0%84%EC%B2%B4%20%EB%A0%88%EC%8A%A4%ED%86%A0%EB%9E%91%20%EC%A1%B0%ED%9A%8C.gif?raw=true" width="600">
</details>
  <details>
  <summary>▶️ 카테고리별 레스토랑 조회</summary>
  <img src="https://github.com/beyond-sw-camp/be11-2nd-ChulungChulung-StomachForce-FE/blob/main/giffolder/%EB%A0%88%EC%8A%A4%ED%86%A0%EB%9E%91_%EC%B9%B4%ED%85%8C%EA%B3%A0%EB%A6%AC%EB%B3%84%20%EB%A0%88%EC%8A%A4%ED%86%A0%EB%9E%91%20%EC%A1%B0%ED%9A%8C.gif?raw=true" width="600">
</details>
  <details>
  <summary>▶️ 메뉴등록</summary>
  <img src="https://github.com/beyond-sw-camp/be11-2nd-ChulungChulung-StomachForce-FE/blob/main/giffolder/%EB%A9%94%EB%89%B4_%EB%93%B1%EB%A1%9D_%EC%88%98%EC%A0%95_%EC%82%AD%EC%A0%9C.gif?raw=true" width="600">
</details>
</details><br>
<details>
  <summary>📂고객센터</summary>
  <details>
  <summary>▶️ 문의사항-등록-수정-삭제</summary>
  <img src="https://github.com/beyond-sw-camp/be11-2nd-ChulungChulung-StomachForce-FE/blob/main/giffolder/%EB%AC%B8%EC%9D%98%EC%82%AC%ED%95%AD_%EB%93%B1%EB%A1%9D_%EC%88%98%EC%A0%95_%EC%82%AD%EC%A0%9C.gif?raw=true" width="600">
</details>
  <details>
  <summary>▶️ 문의사항-답변-등록-수정-삭제</summary>
  <img src="https://github.com/beyond-sw-camp/be11-2nd-ChulungChulung-StomachForce-FE/blob/main/giffolder/%EB%AC%B8%EC%9D%98%EC%82%AC%ED%95%AD_%EB%8B%B5%EB%B3%80_%EB%93%B1%EB%A1%9D_%EC%88%98%EC%A0%95_%EC%82%AD%EC%A0%9C.gif?raw=true" width="600">
</details>
</details><br>
<details>
  <summary>📂 신고</summary>
  <details>
  <summary>▶️ 신고-등록-수정-삭제</summary>
  <img src="https://github.com/beyond-sw-camp/be11-2nd-ChulungChulung-StomachForce-FE/blob/main/giffolder/%EC%8B%A0%EA%B3%A0_%EB%93%B1%EB%A1%9D_%EC%88%98%EC%A0%95_%EC%82%AD%EC%A0%9C.gif?raw=true" width="600">
</details>
  <details>
  <summary>▶️ 신고-답변-등록-수정-삭제</summary>
  <img src="https://github.com/beyond-sw-camp/be11-2nd-ChulungChulung-StomachForce-FE/blob/main/giffolder/%EC%8B%A0%EA%B3%A0_%EB%8B%B5%EB%B3%80_%EB%93%B1%EB%A1%9D_%EC%88%98%EC%A0%95_%EC%82%AD%EC%A0%9C.gif?raw=true" width="600">
</details>
  <details>
  <summary>▶️ 유저-프로필-신고</summary>
  <img src="https://github.com/beyond-sw-camp/be11-2nd-ChulungChulung-StomachForce-FE/blob/main/giffolder/%EC%9C%A0%EC%A0%80_%ED%94%84%EB%A1%9C%ED%95%84_%EC%8B%A0%EA%B3%A0.gif?raw=true" width="600">
</details>
</details><br>
<details>
  <summary>📂 공지사항 & 이벤트</summary>
  <details>
  <summary>▶️ 이벤트생성</summary>
  <img src="https://github.com/beyond-sw-camp/be11-2nd-ChulungChulung-StomachForce-FE/blob/main/giffolder/event_create.gif?raw=true" width="600">
</details>
  <details>
  <summary>▶️ 이벤트수정_삭제</summary>
  <img src="https://github.com/beyond-sw-camp/be11-2nd-ChulungChulung-StomachForce-FE/blob/main/giffolder/event_update_delete.gif?raw=true" width="600">
</details>
</details>


 ##   프로젝트 마무리  
###  배운 점 & 개선할 점
<details>
  <summary>▶️조원 피드백</summary>
  
  🎵 **홍성혁**  
  - **느낀 점**  
    - 처음에 협업 분담과 일정을 정할 때, 할 수 있는 양을 정확히 작성해야 한다고 느꼈습니다.  
 - **배운 점** 
    - 프로젝트를 진행하면서 코드의 흐름을 파악하면 오류를 찾기 쉬웠고,  
      프론트에서는 `console.log()`,  
      백에서는 `println()`을 통해 주고받는 데이터를 확인하며 진행하니 코드를 작성하는 데 많은 도움이 되었습니다.
  - **아쉬운 점**  
    - 각자 코드를 짜고 합치는 과정에서 생각보다 시간이 많이 소요가 되었습니다.

🎵 **임성재**  
  - **느낀 점**  
    - 협업이 많이 어렵다는 걸 깨달았습니다.  
    - 내가 고친 코드가 다른 사람이 수정하면서 기능이 망가지는 경우가 많아 머리가 뜨끈했습니다.  
    - 소통의 중요성을 많이 깨달았고, 너무 AI에 의존하면 안 되겠다는 생각이 들었습니다.

🎵 **최영일**  
  - **배운 점**  
    - 프로젝트 초기, ERD와 요구 명세서를 작성할 때는 할 만하다고 생각했지만,  
      백엔드를 개발하면서 부족한 점이 많이 드러났습니다.  
    - 프로젝트를 진행하면서 실력이 향상되는 것을 느꼈고, 중간 프로젝트는 어느 정도 마무리할 수 있었습니다.  
    - 최종 프로젝트까지 진행하면 더욱 실력을 증진시킬 수 있을 것 같습니다.  
  - **느낀 점**  
    - 우리가 일상 속에서 사용하는 서비스들이 얼마나 많은 노력 끝에 만들어졌는지 다시금 생각해보는 계기가 되었습니다.  
  - **개선할 점**  
    - 개발 중간에 서로의 업무가 겹쳐 비효율적인 부분이 있었습니다.  
    - 다음에는 계획 단계에서 역할을 명확히 나누어 효율적으로 작업할 필요가 있습니다.  
  - **아쉬운 점**  
    - 조금만 더 열심히 했다면 더 나은 서비스를 만들 수 있었을 것 같습니다.  
    - 최종 프로젝트에서는 더욱 열심히 해야겠습니다.  

🎵 **최영재**  
  - **배운 점**  
    - 프론트와 합치는 과정에서 서버의 DTO 설계를 효율적으로 해야 `axios` 요청을 줄일 수 있다는 것을 알게 됨.  
    - 백엔드 흐름을 완벽하게 이해할 수 있었음.  
    - 요구사항을 꼼꼼히 정의하더라도, 기술적 한계나 수정 사항이 필연적으로 발생함.  
    - 엔티티를 너무 많이 만들면 뇌가 녹아버림.  
    - 서버에서 데이터를 꼼꼼하게 준비하더라도, 프론트에서 정상적으로 처리하지 못하면 무용지물.  
    - 프론트엔드 개발자는 대단함.  
    - GitHub에서 `force push`만 안 하면 어떻게든 수습 가능. 하지만, 실수를 혼자 만회하려다 더 큰 실수를 할 수 있음.  
  - **느낀 점**  
    - CRUD가 쉬울 거라 생각했지만, 규모가 커지면 관계 설정이 쉽지 않음.  
    - 주어진 시간이 길든 짧든 최대한 빨리 개발을 끝내고 여유를 부리는 것이 중요함.  
    - 백엔드에서 DTO를 대충 설정하면, 프론트와 합치는 과정에서 큰 문제가 발생함.  
    - 프론트엔드 개발자가 왜 연봉이 낮은지 이해할 수 없음.  
    - 프로젝트를 진행하며 계속 개선할 점이 보임.  
    - 팀원 간의 연결 작업을 진행할 때 신중해야 함.  
  - **개선할 점**  
    - 프로젝트 시작과 동시에 전력 질주해야 함.  
    - 부족한 실력을 우선 개선해야 함.  
  - **아쉬운 점**  
    - 프론트엔드의 어려움을 더 경험했다면, 구조 설계나 속도 조절을 더 안정적으로 할 수 있었을 것 같음.  
    - 시간이 부족해 디테일을 챙기지 못한 것이 아쉬움. 
</details>



