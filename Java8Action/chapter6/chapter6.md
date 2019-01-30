# chapter 6 : 스트림으로 데이터 수집

*이 장의 목표*

“ Collectors 클래스로 Collection을 만들고 사용하기 ”

“ 데이터 스트림을 하나의 값으로 reduce(스트림 합치기) 하기 ”

“ 특별한 Reducing 요약 연산 ”

“ 데이터의 그룹화(groupingBy)와 분할(프레디케이트) ”

“ 자신만의 Custom Collector 개발 ”

**reduce 연산(하나로 수렴)을 통해 스트림을 다양한 형태로 collect 하는 방법에 대한 내용을 담고 있는 챕터 입니다.**

---

### 이전 챕터들과의 차이점

* filter, map과 같은 중간 연산은 Stream을 반환하며 파이프라인을 계속해서 구성한다. 따라서 Stream의 요소를 소비하지 않는다.
* 하지만, count, reduce 같은 최종 연산은 **Stream의 요소들을 소비**해서 결과를 도출한다.
* 앞선 Chapter에서는 최종 연산 collect를 사용했다. 하지만 우리는 **항상 toList만 사용**하여 Stream 요소를 리스트로만 변환했다.
* Chapter 6은 collect에 **다양한 요소 누적 방식을 파라미터로 받아**서 Stream을 최종 결과로 도출하는 **reducing 연산을 수행**할 수 있음을 설명한다.

#### 통화별로 트랜잭션을 그룹화한 코드

![image](https://user-images.githubusercontent.com/14870706/51952555-498c8000-247c-11e9-82ae-3215a954199a.png)

* 기존 명령형 버전 코드는 다음과 같이 직접 객체에 ask 해서 데이터를 가지고 온 뒤, 로직을 짜는 형태다. 따라서 로직이 복잡하고 가독성이 떨어진다.

![image](https://user-images.githubusercontent.com/14870706/51952571-54dfab80-247c-11e9-87a8-6c8dbac619cb.png)

* 함수형 버전 코드는 다음과 같이 **Tell Don't Ask** 를 잘 따르는 형태다. 객체에 메시지만 전달하고 데이터를 요청하지 않는다. 모든 로직은 해당 객체의 스트림에서 처리된다. 가독성이 뛰어나다.

> Stream에 toList를 사용하는 대신 더 범용적인 **Collector** 파라미터를 collect 메서드에 전달함으로써 원하는 연산을 간결하게 구현할 수 있게 되었다. 또한 가독성도 뛰어나졌다. 그렇다면 **Collector**란 무엇인가?

## Collector

> **Collector의 사전적 의미** :  수집가; 징수원

자바 8에서 Collector는 Stream 요소들을 어떻게  분류하고 모을지(reducing)를 담당하는 인터페이스이다. 즉, Collector 인터페이스를 implement하고 구현하여 스트림의 요소를 어떤 식으로 도출할지 지정한다.

![image](https://user-images.githubusercontent.com/14870706/51952586-62953100-247c-11e9-9188-aad15895baeb.png)

* 예를 들어, 아까 본 groupingBy는 **Collectors**라는 클래스에 미리 구현된 **‘팩토리 메서드 함수’**다. 특정 키값을 기준으로 Map을 도출하는 역할을 하고 Collector를 반환한다.

*이제부터 우리가 알아볼 **Collectors** 클래스에서 기본적으로 제공하는 메서드의 기능*

* 스트림 요소를 하나의 값으로 reduce하고 요약
* 스트림 요소들의 그룹화
* 스트림 요소들의 분할 (이것도 결국 bool 그룹화)

---

### 첫번째, Reducing과 요약

1. 스트림값에서 최댓값 최솟값 찾기

![image](https://user-images.githubusercontent.com/14870706/51952607-80629600-247c-11e9-9bd1-f9f31216ec41.png)

2. 요약 연산 (필드의 합계나 평균 등)

![image](https://user-images.githubusercontent.com/14870706/51952619-8b1d2b00-247c-11e9-9e2f-c23326c06c10.png)

![image](https://user-images.githubusercontent.com/14870706/51952628-95d7c000-247c-11e9-9ac6-c9f4bc1bfea2.png)

![image](https://user-images.githubusercontent.com/14870706/51952645-a5ef9f80-247c-11e9-8be6-be3b1e06b350.png)

* 두 개 이상의 연산(ex 합계, 평균 동시에 필요)을 한번에 수행하는 역할

3. 문자열 연결

![image](https://user-images.githubusercontent.com/14870706/51952657-af790780-247c-11e9-9dc7-25177e43edd1.png)



> 이러한 모든 Collector는 reducing 팩토리 메서드로도 정의할 수 있다. (가독성이나 편리성 측면에서 권장하지 않는다)

![image](https://user-images.githubusercontent.com/14870706/51949993-31176800-2472-11e9-8ef6-0f9312729edd.png)

* 첫 번째 인수 : reducing 연산의 시작값. 스트림에 인수가 없을 때는 반환값.
* 두 번째 인수 : 요리를 칼로리 정수로 변환하는 변환함수.
* 세 번째 인수 : 어떠한 연산을 할건지를 명시하는 BinaryOperator.



![image](https://user-images.githubusercontent.com/14870706/51950049-5906cb80-2472-11e9-9862-131a2c8dcb08.png)

![image](https://user-images.githubusercontent.com/14870706/51950071-6623ba80-2472-11e9-9fe0-a8a8c518e072.png)

**구현 방법에는 여러가지가 있다. 상황에 맞는 최적의 해법을 선택하여 가독성과 성능을 만족시키자.**



> **퀴즈 : Reducing으로 문자열 연결하기**

joining Collector를 reducing Collector로 올바르게 바꾼 코드를 모두 선택하시오.

![image](https://user-images.githubusercontent.com/14870706/51950122-894e6a00-2472-11e9-915c-ed79c135a1fc.png)

**정답** : 1, 3

**2번이 오답인 이유** : reducing은 **BinaryOperator<T>**, 즉 **BiFunction<T, T, T>**를 인수로 받음. 제네릭에 따라서 3가지 형식이 다 통일되어야함. 하지만 2번은 menu와 String 두가지 형식이다.**그냥 reducing으로 joining을 구현할 수 있다는걸 보여주는 예제다. 실무에선 joining 쓰자.**



### 두번째, 그룹화

1. **타입별로 그룹화하기**

![image](https://user-images.githubusercontent.com/14870706/51950213-e5b18980-2472-11e9-9245-47e0c1d975d2.png)

2. **더 복잡한 기준으로 분류하기**

![image](https://user-images.githubusercontent.com/14870706/51950232-04178500-2473-11e9-875d-71e696ff8e67.png)

3. **다수준으로 그룹화하기**

![image](https://user-images.githubusercontent.com/14870706/51950251-1691be80-2473-11e9-8ea9-d17a045fe4bc.png)

4. **서브그룹으로 데이터 수집**

![image](https://user-images.githubusercontent.com/14870706/51950270-24474400-2473-11e9-9477-00134613ce59.png)

5. **Collector 결과를 다른 형식에 적용하기**

![image](https://user-images.githubusercontent.com/14870706/51950297-46d95d00-2473-11e9-9c8b-be17bd496cb9.png)

​				의미없는 Optional이 붙어서 반환될 경우, 이렇게 변환시킨 형태로 반환할 수 있다.

![image](https://user-images.githubusercontent.com/14870706/51950520-32499480-2474-11e9-8e85-4af8886fa915.png)

### 세번째, 분할

> 분할은 프레디케이트(true, false 반환함수)를 분류 함수로 사용하는 특수한 그룹화다

![image](https://user-images.githubusercontent.com/14870706/51950588-6c1a9b00-2474-11e9-8c3b-db5cd2b8da57.png)

**의문점** : filter를 사용해서도 간단히 원하는 내용을 얻을 수 있는데 왜 사용해야하는가?

**답변** : 참, 거짓 두가지 요소의 스트림 리스트를 모두 유지한다는 것이 장점이다.

![image](https://user-images.githubusercontent.com/14870706/51950604-7dfc3e00-2474-11e9-9957-d9515a84ffa2.png)

**위처럼 참이냐 거짓이냐로 쉽게 그룹화를 할 수 있다. filter는 말그대로 필터링하여 보여주는 것이고, 분할은 데이터를 분할하는 용도로 사용하는 것이다.**

---

> 지금까지 살펴본 모든 **Collector**는 Collector 인터페이스를 구현한다. Collector 인터페이스를 자세히 살펴보자.

### Collector 인터페이스란?

: reducing 연산(Collector)를 어떻게 구현할지 제공하는 메서드 집합으로 구성된 인터페이스. Collector 인터페이스를 직접 구현하면 문제를 더 효율적으로 해결할 수 있다.

![image](https://user-images.githubusercontent.com/14870706/51950660-b4d25400-2474-11e9-885e-1fa3badc5dee.png)

![image](https://user-images.githubusercontent.com/14870706/51950682-cd426e80-2474-11e9-8ad3-509c14902364.png)

**구현해야될 메서드**

1. supplier
2. accumulator
3. finisher
4. combiner
5. characteristics

![image](https://user-images.githubusercontent.com/14870706/51950716-ec410080-2474-11e9-9358-b4692c1a3eb2.png)



#### Characterristics 메서드

* **UNORDERED :** reducing 결과는 스트림 요소의 방문 순서나 누적 순서에 영향을 받지 않는다.
* **CONCURRENT :** 다중 스레드에서 accumulator 함수를 동시에 호출할 수 있으며 이 컬렉터는 스트림의 병렬 reducing을 수행할 수 있다. UNORDERED를 함께 설정하지 않았다면 데이터 소스가 정렬되어 있지 않은 상황에서만 병렬 reducing을 수행할 수 있다.
* **IDENTITY_FINISH :** finisher 메서드가 반환하는 함수는 단순히 identity를 적용할 뿐 이므로 이를 생략할 수 있게 한다. reducing 과정의 최종 결과로 누적자 객체를 바로 사용할 수 있다.

![image](https://user-images.githubusercontent.com/14870706/51950660-b4d25400-2474-11e9-885e-1fa3badc5dee.png)

\- accumulator를 통해 누적하는 데에 사용한 리스트가 최종 결과이므로 **IDENTITY_FINISH**

**-** 리스트의 순서는 상관 없으므로  **UNORDERED**

**-** UNORDERED해서 병렬 처리가 가능하므로 **CONCURRENT**

