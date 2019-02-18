# 리팩토링, 테스트, 디버깅

## 8.1 리팩토링

- **리팩토링?**

​    - 코드를 보다 쉽게 이해할 수 있도록 만드는 작업

- 가독성 : 좀 더 간결하게
- 확장성 : 좀 더 유연하게

### 8.1.1 코드 가독성 개선

- 코드 가독성이 좋다?
  - 어떤 코드를 다른 사람도 쉽게 이해할 수 있음
- 코드의 가독성이 높아지면 다른 사람이 유지보수하기 쉬워짐  
- **in JAVA 8**
  - 람다식으로 코드의 길이를 줄일 수 있음
  - 메소드 레퍼런스, 스트림 API로 **코드의 의도**를 주석을 사용하지 않고 표현 가능

### 8.1.2 익명 클래스 >> 람다

- 코드 길이는 당연히 줄어드니까 좋은데...

- 익명 클래스에서 사용한 this, super는 람다의 this, super와 다름

  - 익명 클래스의 this는 **자기 자신**
  - 람다의 this는 **람다를 감싸는 클래스**

- 익명 클래스는 shadow variable 사용 가능

  ```java
  int a = 10;
  Runnable r1 = () -> {
      int a = 2;
      System.out.println(a);
  } // compile error
  
  Runnable r2 = new Runnable() {
      @Override
      public void run() {
          int a = 2;
          System.out.println(a);
      }
  }
  ```

  

- 콘텍스트 오버로딩에 따른 모호함을 초래할 수 있음

  - 명시적 형 변환을 통해서 해결 가능

    

위와 같은 문제를 잘 생각하고 바꾸도록 합시다.



### 8.1.3 람다 >> 메소드 레퍼런스

#### ex1

```java
Map<CaloricLevel, List<Dish>> dishesByCaloricLevel = menu.stream()
                        .collect(
                            groupingBy(dish -> {
                                if (dish.getCalories() <= 400) return CaloricLevel.DIET;
                                else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
                                else return CaloricLevel.FAT;
                            })
                        )
```

```java
Map<CaloricLevel, List<Dish>> dishesByCaloricLevel = menu.stream().collect(groupingBy(Dish::getCaloricLevel));
```

#### ex2

```java
int totalCalories = menu.stream().map(Dish::getCalories)
                                 .reduce(0, (c1, c2) -> c1 + c2);
```

```java
int totalCarories = menu.stream().collect(summingInt(Dish::getCalories));
```

### 8.1.4 명령형 >> 스트림

- **이론적으로는** 반복자를 이용한 기존의 모든 컬렉션 처리 코드를 스트림 API로 바꿔야 함

```java
List<String> dishNames = new ArrayList<>();

for (Dish dish: menu) {
        if(dish.getCalories() > 300) {
            dishNames.add(dish.getName());
        }
}
```

```java
menu.parallelStream()
    .filter(dish -> dish.getCalories() > 300)
    .map(Dish::getName)
    .collect(toList());

```

### 8.1.5 코드 유연성 개선

## 8.2 디자인 패턴 리팩토링

### 전략 패턴

```java
public interface ValidationStrategy {
    boolean execute(String s);
}

public class IsAllLowerCase implements ValidationStrategy {
    public boolean execute(String s) {
        return s.matches("[a-z]+");
    }
}

public class IsNumeric implements ValidationStrategy {

    public boolean execute(String s) {
        return s.matches("d+");
    }
}

public class Validator {
    private final ValidationStrategy strategy;

    public Validator(ValidationStrategy v) {
        this.strategy = v;
    }
    public boolean validate (String s) {
        return strategy.execute(s);
    }

}

Validator numericValidator = new Validator(new IsNumeric());
boolean b = numericValidator.validate("aaaa");

```

```java
Validator numericValidator = new Validator((String s) -> s.matches("[a-z]+"));
boolean b = numericValidator.validate("aaaa");

```

## 8.3 테스트

- 람다 표현식 자체를 테스트할 수 없음 (익명함수라 테스트 케이스 내부에서 호출 불가능)
- 람다 표현식은 함수형 인터페이스의 인스턴스를 생성, 그러므로 생성된 인스턴스의 동작으로 테스트 가능
- 람다를 사용하는 메소드의 동작을 테스트
- 복잡한 람다식은 개별 메소드로 분할해서 테스트 (메소드 레퍼런스)

## 8.4 디버깅

### 8.4.1 스택 트레이스 확인

- 람다 표현식은 이름이 없으므로 스택 트레이스가 많이... 복잡함...
- 메소드 레퍼런스를 사용해서 스택 트레이스에는 표현X
- 방법 없음...

### 8.4.2 정보 로깅

- 스트림의 파이프라인 연산을 디버깅하고 싶을 때
  - peek
