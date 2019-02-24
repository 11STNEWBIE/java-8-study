# 챕터 10 : null 대신 Optional

#### *이 장의 내용*

* *null 레퍼런스의 문제점과 null을 멀리해야 하는 이유*
* *null 대신 Optional: null로부터 안전한 도메인 모델 재구현하기*
* *Optional 활용 : null 확인 코드 제거하기*
* *Optional에 저장된 값을 확인하는 방법*
* *값이 없을 수도 있는 상황을 고려하는 프로그래밍*

---

> **null 때문에 어떤 문제가 발생할 수 있을까?**

### 1. **값이 없는 상황**

```java
pulbic class Person {
    private Car car;
    public Car getCar() { return car; }
}

public class Car {
    private Insurance insurance;
    public Insurance getInsurance() { return insurance; }
}

public class insurance {
    private String name;
    public String getName() { return name; }
}
```

다음 코드에서는 어떤 문제가 발생할까?

```java
public String getCarInsuranceName(Person person) {
    return person.getCar().getInsurance().getName();
}
```

코드는 이상이 없어보이지만 차를 소유하지 않은 사람도 많다. getCar를 하면 Null을 반환한다.  그러면 런타임에 NullpointException이 발생하면서 프로그램 실행이 중단된다.

1. **보수적인 자세로 NullPointerException 줄이기**

   * null 체크 코드를 추가해서 예외 문제를 해결한다.
   * null로 부터 안전하려면 깊은 의심을 해야한다.
   * 들여쓰기 수준이 증가한다.
   * 들여쓰기를 없애기 위해서 메서드에 여러개의 출구를 만드는 방법도 있다.

   > 앞의 코드는 쉽게 에러를 일으킬 수 있다. 만약 누군가가 null일 수 있다는 사실을 깜빡 잊었다면 어떤 일이 일어날까?
   >
   > null로 값이 없다는 사실을 표현하는 것은 좋은 방법이 아니다. 따라서 값이 있거나 없음을 표현할 수 있는 좋은 방법이 필요하다.

2. **null 때문에 발생하는 문제**

   1. 에러의 근원이다.
   2. 코드를 어지럽힌다.
   3. 아무 의미가 없다.
   4. 자바 철학에 위배된다.
      * 자바는 개발자로부터 모든 포인터를 숨겼다. 하지만 예외가 있는데 그게 바로 null 포인터다.
   5. 형식 시스템에 구멍을 만든다.
      * null은 무형식이며 정보를 포함하고 있지 않으므로 모든 레퍼런스 형식에 Null을 할당할 수 있다. 이런 식으로 null이 할당되기 시작하면서 시스템의 다른 부분으로 null이 퍼졌을 때 애초에 null이 어떤 의미로 사용되었는지 알 수 없다.

3. **다른 언어는 null 대신 무엇을 사용할까?**

   1. Groovy 같은 언어에서는 **안전 내비게이션 연산자**(?.) 를 도입해서 null 문제를 해결했다.

      ```groovy
      def carInsuranceName = person?.car?.insurance?.name
      ```

      호출 체인에 null인 레퍼런스가 있으면 결과로 null이 반환된다.

   2. 하스켈, 스칼라 등의 함수형 언어는 아예 다른 관점에서 null 문제를 접근한다. 

      1. 하스켈은 선택형값을 저장할 수 있는 Maybe라는 형식을 제공한다.
      2. Maybe는 주어진 형식의 값을 갖거나 아니면 아무 값도 갖지 않을 수 있다.
      3. null 레퍼런스 개념은 자연스럽게 사라진다.
      4. 스칼라도 T 형식의 값을 갖거나 아무 값고 갖지 않을 수 있는 Option[T]라는 구조를 제공한다.

### Optional 클래스 소개

> 자바 8은 하스켈과 스칼라의 영향을 받아서 java.util.Optional<T>라는 새로운 클래스를 제공한다. Optional은 선택형값을 캡슐화하는 클래스다. 값이 있으면 Optional 클래스는 값을 감싼다. 값이 없으면 Optional.empty 메서드로 Optional을 반환한다.

```java
pulbic class Person {
    private Optional<Car> car; // 차를 소유했을 수도 안했을 수도 있음
    public Optional<Car> getCar() { return car; }
}

public class Car {
    private Optional<Insurance> insurance; // 보험에 가입되어 있을 수도 없을 수도.
    public Optional<Insurance> getInsurance() { return insurance; }
}

public class insurance {
    private String name;
    public String getName() { return name; }
}
```

* Optional 클래스를 사용함으로써 모델의 의미가 더욱 명확해졌다. (있을수도 없을수도 있는 것인지)
* Optional을 이용하면 값이 없는 상황이 우리 데이터에 문제가 있는 것인지 아니면 알고리즘의 버그인지 명확하게 구분할 수 있다.
* 모든 null 레퍼런스를 Optional로 대치하는 것은 바람직하지 않다. Optional의 역할은 더 이해하기 쉬운 API를 설계하도록 돕는 것이다.

### Optional 적용 패턴

> Optional 형식을 이용하면 도메인 모델의 의미를 더 명확하게 만들 수 있었으며 null 레퍼런스 대신 값이 없는 상황을 표현할 수 있음을 확인했다. 이제는 활용 방법에 대해 알아보도록 하겠다.

#### 1. Optional 객체 만들기

1. 정적 팩토리 메서드를 통해 빈 Optional 객체를 만들 수 있다. 

   `Optional<Car> car = Optional.empty()`

2. null이 아닌 값으로 Optional 만들기
   * 정적 팩토리 메서드 Optional.of로 null이 아닌 값을 포함하는 Optional을 만들 수 있다.
     * `Optional<Car> car = Optional.of(car);`
3. null 값으로 Optional 만들기
   * 정적 팩토리 메서드 Optional.ofNullable로 null 값을 저장할 수 있는 Optional을 만들 수 있다.
     * `Optional<Car> car = Optional.ofNullable(car)`

#### 2. 맵으로 Optional의 값을 추출하고 변환하기

> 보통 객체의 정보를 추출할 때는 Optional을 사용할 때가 많다. 예를 들어 보험회사의 이름을 추출한다고 가정하자. 다음 코드처럼 이름 정보에 접근하기 전에 insurance가 null인지 확인해야 한다.

```java
String name = null;
if(insurance != null){
    name = insurance.getName();
}
```

이런 유형의 패턴에 사용할 수 있도록 Optional은 map 메서드를 지원한다. 

```java
Optional<Insurance> optInsurance = Optional.ofNullable(insurance);
Optional<String> name = optInsurance.map(Insurance::getName);
```

여러 메서드를 안전하게 호출하는데, 이 코드를 어떻게 활용할 수 있을지에 대해서 알아보도록 하겠다.

flatMap이라는 Optional의 또 다른 메서드를 살펴보도록 하자.



#### 3. flatMap으로 Optional 객체 연결

> 스트림의 flatMap은 함수를 인수로 받아서 다른 스트림을 반환하는 메서드였다. 보통 인수로 받은 함수를 스트림의 각 요소에 적용하면 스트림의 스트림이 만들어진다. 하지만 flatMap은 인수로 받은 함수를 적용해서 생성된 각각의 스트림에서 콘텐츠만 남긴다. 즉, 함수를 적용해서 새성된 모든 스트림이 하나의 스트림으로 병합되어 평준화된다. 우리도 이차원 Optional 을 일차원 Optional로 평준화해야 한다.

* Optional로 자동차의 보험회사 이름 찾기

  ```java
  public String getCarInsuranceName(Optional<Person> person) {
      return person.flatMap(Person::getCar) // Optional<Car>
      			.flatMap(Car::getInsurance) // Optional<Insurance>
      			.map(Insurance::getName) // Optional<String>
      			.orElse("Unknown"); // 보험회사 이름 출력 (널이면 언노운)
  }
  ```

#### 4. 디폴트 액션과 Optional 언랩

> Optional이 비어있을 때 디폴트 값을 제공할 수 있는 orElse 메서드로 값을 읽을 수 있다. Optional 클래스는 Optional 인스턴스에서 값을 읽을 수 있는 다양한 인스턴스 메서드를 제공한다.

* `get()` 은 값을 읽는 가장 간단한 메서드면서 동시에 가장 안전하지 않은 메서드다. 해당 값이 있으면 반환하고 없으면 `NoSuchElementException을` 발생시킨다. 따라서 Optional에 반드시 값이 있다고 확신할 수 있는 상황 아니면 get을 사용하지 않는게 바람직하다.
* `orElse(T other)` 를 사용하면 값이 없을 때 디폴트 값을 제공할 수 있다.
* `orElseGet(Supplier<? extends T> other)` 는 `orElse` 메서드에 대응하는 게으른 버전의 메서드다. 
  * Optional에 값이 없을 때만 Supplier가 실행된다.
  * 디폴트 메서드를 만드는 데 시간이 걸리거나(효율성 때문에) Optional이 비어있을 때만 디폴트값을 생성하고 싶다면 이걸 사용해야한다.
* `orElseThrow` 는 Optional이 비어있을 때 예외를 발생시킨다는 점에서 get 메서드와 비슷하다. 하지만 이 메서드는 발생시킬 예외의 종류를 선택할 수 있다.
* `ifPresent(Consumer<? super T> consumer)`를 이용하면 값이 존재할 때 인수로 넘겨준 동작을 실행할 수 있다. 값이 없으면 아무 일도 일어나지 않는다.

#### 5. 두 Optional 합치기

> Person과 Car 정보를 이용해서 가장 저렴한 보험료를 제공하는 보험회사를 찾는 몇몇 복잡한 비즈니스 로직을 구현한 외부 서비스가 있다고 가정하자.

```java
public Insurance findCheapestInsurance(Person person, Car car) {
    // 다양한 보험회사가 제공하는 서비스 조회
    // 모든 결과 데이터 비교
    return cheapestCompany;
}
```

이제 두 Optional을 인수로 받아서 Optional<Insurance>를 반환하는 null 안전 버전의 메서드를 구현해보자.

```java
public Optional<Insurance> nullSafeFindCheapestInsurance(Optional<Person> person, 																		Optional<Car> car) {
    if(person.isPresent() && car.isPresent()) {
        return Optional.of(findCheapestInsurance(person.get(), car.get()));
    } else {
        return Optional.empty();
    }
}
```

안타깝게도 구현 코드는 null 확인 코드와 크게 다른점이 없다. 

Optional 클래스에서 제공하는 기능을 이용해서 이 코드를 더 자연스럽게 개선해보자.

```java
public Optional<Insurance> nullSafeFindCheapestInsurance(Optional<Person> person, 																		Optional<Car> car) {
    return person.flatMap(p -> car.map(c -> findCheapestInsurance(p, c)));
}
```



#### 6. 필터로 특정값 거르기

> 종종 객체의 메서드를 호출해서 어떤 프로퍼티를 확인해야 할 때가 있다. 예를 들어 보험회사 이름이 'CambridgeInsurance'인지 확인해야 한다고 가정하자. 이 작업을 안전하게 수행하려면 Insurance 객체가 null인지 여부를 확인한 다음에 getName 메서드를 호출해야 한다.

```java
Insurance insurance = ...;
if(insurance != null && "CambridgeInsurance".equals(insurance.getName())) {
    System.out.println("ok");
}
```

Optional 객체에 filter 메서드를 이용해서 다음과 같이 코드를 재구현할 수 있다.

```java
Optional<Insurance> optInsurance = ...;
optInsurance.filter(insurance ->
								"CambridgeInsurance".equals(Insurance.getName()))
			.ifPresent(x -> System.out.println("ok"));
```

*tip* : Optional은 최대 한 개의 요소를 포함할 수 있는 스트림과 같다.



### Optional을 사용한 실용 예제

> Optional을 잘 활용하려면 기존의 알고리즘과는 다른 관점에서 접근해야 한다.

#### 1. 잠재적으로 null이 될 수 있는 대상을 Optional로 감싸기

```java
Object value = map.get("key");

-->

Optional<Object> value = Optional.ofNullable(map.get("key"));
```



2번 예외와 Optional과 응용부분은 잘 모르겠다.



#### 내가 생각하는 Spring Boot를 쓰면서 Optional 잘 활용하는법

```java
public class UserController {

	@Autowired
	UserService userService;
	
    @GetMapping
    public String list(Model model){
        model.setAttribute("users", userService.listUsers(?));
        return "/users/list";
    }
}



public class UserService {
    
    @Autowired
    UserRepository userRepository;
    
    public String listUsers(?) {
        return userRepository.findByUserId(?).orElseThrow(EntityNotFoundException::new);
    }
}



public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserId(Long userId); 
}
```

