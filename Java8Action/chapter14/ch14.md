## 14장 : 함수형 프로그래밍 기법

### 14.1 함수는 모든 곳에 존재한다

- 일급 함수 (first-class function)

  - 일반값처럼 취급할 수 있는 함수

  - 함수를 마치 일반 값처럼 사용해서 인수로 전달하거나, 결과로 반환받거나, 자료구조에 저장할 수 있음

  - 메서드 레퍼런스

  - ```java
    Function<String, Integer> strToInt = Integer::parseInt;
    ```

    

#### 14.1.1 고차원 함수

- 고차원 함수

  - 하나 이상의 함수를 인수로 받음
  - 함수를 결과로 반환

- 자바 8의 예시

  ```java
  Comparator<Apple> c = comparing(Apple::getWeight);
  ```

  ```java
  Function<String, String> transformationPipeline =
      		addHeader.andThen(Letter::checkSpelling)
      			     .andThen(Letter::addFooter);
  ```

  

#### 14.1.2 커링

- 커링

  - 함수를 모듈화하고 코드를 재사용하는 데 도움을 주는 기법
  - (이론적으로) x와 y라는 두 인수를 받는 함수 f를 한 개의 인수를 받는 g라는 함수로 대체하는 기법

- 섭씨 -> 화씨를 변환하는 패턴을 메서드로 표현할 때

  - 변환하려는 값 x, 변환 요소 f, 기준치 조정 요소 b를 모두 인수로 갖는 함수 만들기

  - ```java
    static double converter(double x, double f, double b) {
        return x * f + b;
    }
    ```

  - 두 개의 인수를 갖는 변환 함수를 생산하는 factory를 정의하기

    ```java
    static DoubleUnaryOperator curriedConverter(double f, double b) {
        return (double x) -> x * f + b;
    }
    ```

    

### 14.2 영속 자료구조

- 함수형 메서드에서는 전역 자료구조나 인수로 전달된 구조를 갱신할 수 없다 (참조 투명성 위배, 인수를 결과로 단순하게 매핑할 수 있는 능력 상실)
- 자료구조를 고칠 수 없는 상황에서도 자료구조로 프로그램 구현하기 가능한가?

#### 14.2.1 파괴적인 갱신과 함수형

- 단방향 연결 리스트로 구현된 TrainJounrey

  ```java
  class TrainJourney {
      public int price;
      public TrainJourney onward;
      public TrainJourney(int p, TrainJourney t) {
          price = p;
          onward = t;
      }
      
      static TrainJourney link(TrainJourney a, TrainJourney b) {
          if(a == null) return b;
          TrainJourney t = a;
          while(t.ownard != null){
              t = t.onward;
          }
          t.onward = b;
          return a;
      }
  }
  
  
  ```

- firstJourney (X -> Y), secondJourney (Y -> Z)

- 기존 방법

  - link(firstJourney, secondJourney) 호출시 firstJourney가 secondJourney를 포함하며 파괴적 갱신(firstJourney를 변경시킴)이 일어남
  - firstJourney는 본래 의도와 달리 X->Z의 여정이 됨
  - 결과적으로 이러한 부작용을 발생시키지 않도록 주석이나 문서로 코드 사용법에 대해 부가 설명이 필요해짐 (그리고 유지보수의 고통)

- 함수형에서는

  - 계산 결과를 표현할 자료구조가 필요하면 기존의 자료구조를 갱신하지 않도록 새로운 자료구조를 만들어야 함

  - ```
    static TrainJourney append(TrainJourney a, TrainJourney b) {
        return a==null ? b : new TrainJourney(a.price, append(a.onward, b);
    }
    ```

  - 

#### 14.2.2 트리를 사용한 다른 예제

- 기존의 트리를 갱신한다.

#### 14.2.3 함수형 접근법 사용

- 인수로 가능한 많은 정보를 받아서
- 새로운 트리를 만들어 반환한다.

- 함수형 프로그래밍에서 요구하는 단 한 가지 조건
  - **결과 자료구조를 바꾸지 말라!**

### 14.3 스트림과 게으른 평가

- 스트림은 단 한 번만 소비할 수 있다는 제약이 있어 재귀적으로 정의 불가

#### 14.3.1 자기 정의 스트림

- 소수 스트림

  - 소수로 나눌 수 있는 모든 수는 제외할 수 있다.

    - 1. 소수를 선택할 숫자 스트림 준비

         ```java
         static Intstream numbers() {
             return IntStream.iterate(2, n -> n+1);
         }
         ```

      2. 스트림에서 첫 번째 수(head) 가져옴

         ```java
         static int head(IntStream numbers) {
             return numbers.findFirst().getAsInt();
         }
         
         ```

      3. 스트림의 꼬리에서 가져온 수로 나누어떨어지는 모든 수를 걸러 제외시킴

         ```java
         static IntStream tail(IntStream numbers) {
             return numbers.skip(1);
         }
         
         ```

      4. 남은 숫자만 포함하는 새로운 스트림에서 소수 찾기 (1번부터 반복)

         ```java
         static IntStream primes(IntStream numbers) {
             int head = head(numbers);
             return IntStream.concat(
             	IntStream.of(head),
             	primes(tail(numbers).filter(n -> n % head != 0))
             	);
         }
         
         ```

  - 단계 4의 코드를 실행하면 IllegalStateException 발생
    - 스트림을 머리와 꼬리로 분리하는 두 개의 최종연산을 사용했기 때문 (최종연산 호출시 스트림은 완전 소비된다)
  - 또한 IntStream.concat은 두 개의 스트림 인스턴스를 인수로 받음
    - 무한 재귀
  - 게으른 평가 lazy evaluation
    - 소수를 처리할 필요가 있을 때에만 스트림을 실제로 평가

#### 14.3.2 게으른 리스트 만들기

- 게으른 스트림
  - 스트림에 일련의 연산을 적용하면 연산이 수행되지 않고 일단 저장
  - 최종 연산을 적용해서 실제 계산을 해야 하는 상황에서만 실제 연산이 이루어짐
  - Supplier<T> 활용
- 성능
  - 게으른 실행으로 인한 오버헤드가 더 커질 수도 있다
  - 효율성이 떨어진다면 전통적인 방식을 사용하자

### 14.4 패턴 매칭

- 자바에서는 지원하지 않는다.

#### 14.4.1 방문자 디자인 패턴

### 14.5 기타 정보

#### 14.5.1 캐싱 또는 기억화

- 기억화 memorization
  - 매번 새로 탐색하고 계산을 반복해서 수행하면 계산 비용이 너무 비싸니
  - 메소드에 레퍼로 캐시를 추가하는 기법

#### 14.5.3 콤비네이터

- 함수를 조합하는 기능

  ```java
  static <A,B,C> Function<A,C> compose(Function<B,C> g, Function<A,B> f) {
      return x -> g.apply(f.apply(x));
  }
  
  ```

  

