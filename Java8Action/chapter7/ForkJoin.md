포크/조인 프레임워크에 대한 간략한 추가 자료



> 참고자료 (읽어보면 좋아용)

- https://homes.cs.washington.edu/~djg/teachingMaterials/grossmanSPAC_forkJoinFramework.html
- https://www.baeldung.com/java-fork-join
- <https://okky.kr/article/345720>
- http://blog.naver.com/PostView.nhn?blogId=tmondev&logNo=220945933678



# 자바 포크/조인 프레임워크

- C/C++에는 Intel의 Thread Building Blocks(TBB), C#에는 Task Parallel Library와 같이 비슷한 기능을하는 언어/라이브러리 있음

- 자바7부터 스탠다드 라이브러리에 포함되었음

- 모든 클래스가 `java.util.concurrent`에 있음. 다음과 같이 임포트

  ```java
  import java.util.concurrent.ForkJoinPool;
  import java.util.concurrent.RecursiveTask;
  ```

  

## ForkJoinPool 인스턴스 만들기

- **자바8**: `ForkJoinPool` 의 정적 메서드 `commonPool()` 사용 

- ```java
  ForkJoinPool commonPool = ForkJoinPool.commonPool();
  ```

  - 모든 `ForkJoinTask` 의 디폴트 스레드 풀에 대한 레퍼런스 제공 
  - 오라클 문서에 따르면, 미리 정의된 커먼 풀을 사용하는 게 자원 소비를 줄일 수 있음 
    - 태스크 당 분리된 스레드 풀을 만들지 않으니까 

- **자바7**: `ForkJoinPool` 만든다 

  - `new ForkJoinPool()`을 통해 풀을 만들어야 함. 그러나 딱 한번만 이걸 수행하여 스태틱 필드에 결과를 저장해야 함. 전체 프로그램이 그걸 사용할 수 있게~ 
  - `ForkJoinPool` 생산자로 커스텀 스레드 풀 만들 수 있음 

  

## ForkJoinTask<V>

- `ForkJoinPool` 안에서 수행되는 태스크를 위한 기본 타입 
- 다음 둘 중 하나 상속 
  - 리턴값 없는 태스크에는 `RecursiveAction`, 있는 태스크에는 `RecursiveTask<V>` 
  - 둘 다 태스크의 로직이 정의된 추상 메서드인 `compute()` 를 가짐 



## 잘 쓰려면?

- 가능한 한 최소한의 스레드 풀 사용 
  - 하나의 애플리케이션 혹은 시스템에 하나의 스레드 풀 쓰는 게 보통 최선 
- 디폴트 커먼 스레드 풀 사용. 특별한 튜닝 필요하지 않다면~
- 서브태스크로 분할할 때, 적절한 임계값 사용 
- `ForkJoingTask` 블록킹 피하기 