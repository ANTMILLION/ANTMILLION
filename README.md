# 앤트밀리언 (ANTMILLION)
> **"개미들의 무지성 추종(Ant-Mill)을 멈추고, 합리적인 투자자로 거듭나는 공간"**

## 1. 프로젝트 개요 및 기획 배경
### 🐜 앤트밀(Ant-Mill) 현상이란?
개미 무리가 개별 판단 없이 집단의 움직임을 추종하다가 끝없이 원을 그리며 도는 현상.

### 📉 기획 배경
* **불확실한 금융 시장**: 집단 추종, 감정적 투자 증가와 손실 누적으로 인한 개인 투자자 시장 이탈.
* **해결책**: 실시간 데이터 기반의 의사결정 지원 시스템을 통해 개인 투자자의 장기적이고 합리적인 투자 유도.

---

## 2. 주요 서비스 기능

### 🚦 집단 추종 방지 및 신호 제공
* **투자자 비율 실시간 제공**: 종목별 전체 투자자의 매도/매수 비율을 시각화하여 군집 심리 파악 지원.
* **외인/기관 신호등**: 외국인과 기관의 매수세를 신호등 형식(빨강/노랑/초록)으로 제공하여 수급 현황 직관화.

### 📈 모의투자 시스템
* **실시간 모의투자**: 한국투자증권 API 연동을 통한 실시간 주식 모의투자 환경 구축.
* **주문 및 체결 처리**: 시장가/지정가 주문 접수부터 실제 데이터 기반의 체결 로직까지 전 과정 구현.

### ⚠️ 심리 경고 및 가이드
* **Rule-based 경고 시스템**: 매매 전후, 사전에 설정된 원칙에 따라 감정적 매매를 억제하는 경고 메시지 송출.

### 🎮 게이미피케이션
* **개미 랭크 시스템**: 경제 퀴즈 풀이, 증권 뉴스 읽기 등 미션을 통해 투자 지식을 습득하고 등급을 올리는 동기부여 제공.

---

## 3. 기술 스택

### 💻 Backend
- **Framework**: Spring Framework 5.3.27 (Legacy)
- **Build Tool**: Maven
- **Server**: Apache Tomcat 9.0
- **Database Access**: MyBatis 3.5
- **Security**: Spring Security 5.8.11, JWT (JJWT 0.11.5)
- **Real-time**: Spring WebSocket
- **Utilities**: Bucket4j, Redisson, Lombok, JavaMailSender, Jackson

### 🎨 Frontend
- **View**: JSP 2.3, JSTL 1.2
- **Script/Style**: JavaScript, jQuery, CSS 3

### 🗄️ Database & Cache
- **RDBMS**: MySQL 8.0
- **Cache**: Redis

### 🛠️ Tools & Infrastructure
- **IDE**: IntelliJ IDEA, STS
- **Language**: Java 11
- **Collaboration**: Notion, Slack, Figma

---

## 4. 서비스 화면

| 메인 페이지 | 종목 리스트 페이지 | 종목 상세 페이지 |
| :---: | :---: | :---: |
| <img src="https://github.com/user-attachments/assets/982d7051-9fa5-440b-bb1d-1c686370f749" width="300"/> | <img src="https://github.com/user-attachments/assets/982d7051-9fa5-440b-bb1d-1c686370f749" width="300"/> | <img src="https://github.com/user-attachments/assets/982d7051-9fa5-440b-bb1d-1c686370f749" width="300"/> | 

| 마이 페이지 | 미션 페이지 | 로그인 페이지 |
| :---: | :---: | :---: |
| <img src="https://github.com/user-attachments/assets/982d7051-9fa5-440b-bb1d-1c686370f749" width="300"/> | <img src="https://github.com/user-attachments/assets/982d7051-9fa5-440b-bb1d-1c686370f749" width="300"/> | <img src="https://github.com/user-attachments/assets/982d7051-9fa5-440b-bb1d-1c686370f749" width="300"/> |
