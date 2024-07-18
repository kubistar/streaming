# 정산 프로젝트 ( streaming)
[**📚 Notion**]() |
**june 2024 ~ july 2024**  

<img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=for-the-badge&logo=Spring Boot&logoColor=white"> <img src="https://img.shields.io/badge/Spring Cloud-6DB33F?style=for-the-badge&logo= &logoColor=white"> 
<img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=MySQL&logoColor=white">
<img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=Docker&logoColor=white">
<img src="https://img.shields.io/badge/GitHub Actions-2088FF?style=for-the-badge&logo=GitHub Actions&logoColor=white">
<br>

## 🌱 프로젝트 소개
- 대량의 영상 시청기록에 대한 통계 및 정산 Batch 작업

<br>

## 🛠️ 주요 기능
1. 통계 및 정산 기능  
 
3. [관련 API 기능](#관련-API-기능)

<br>

## 🚀 아키텍처
![무제 001]()
<br>

## 📚 기술적 의사결정


<br>



<br>

## 🐞 트러블 슈팅



## 🛠 기능구현 요약


  ```
  resilience4j.circuitbreaker:
  instances:
    adFeignClient:
      registerHealthIndicator: true
      slidingWindowSize: 100
      minimumNumberOfCalls: 10
      permittedNumberOfCallsInHalfOpenState: 3
      automaticTransitionFromOpenToHalfOpenEnabled: true
      waitDurationInOpenState: 10s
      failureRateThreshold: 50
      eventConsumerBufferSize: 10
  ```

  </details>


#### 관련 API 기능
- 일간, 주간, 월간 조회수 및 재생시간 Top5 조회
- 일간, 주간, 월간 사용자 정산내역 조회
