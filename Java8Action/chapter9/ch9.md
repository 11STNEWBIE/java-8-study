### 9장 디폴트 메서드

#### 배경

##### 문제 상황 : 인터페이스를 바꾸고 싶을 때

- 인터페이스를 구현하는 클래스는 인터페이스에서 정의하는 메서드를 모두 구현해야 함
- 만약 인터페이스에 새로운 메서드를 추가한다면, 해당 인터페이스를 구현했던 모든 클래스의 구현이 수정되어야 함

##### 자바 8에서의 해결방법

1. 인터페이스 내부에 스태틱 메서드 사용
2. 인터페이스에서 디폴트 메서드 사용

##### 디폴트 메서드

- 구현을 포함하는 메서드
- 해당 인터페이스를 구현한 클래스는 디폴트 메서드의 구현을 그대로 상속받음

#### 9.1. 변화하는 API

##### API 버전 1

```java
public interface Resizable extends Drawable{
    public int getWidth();
    public int getHeight();
    public void setWidth(int width);
    public void setHeight(int height);
    public void setAbsoluteSize(int width, int height);
}
```

- 사용자 구현 : Resizable을 구현하는 Ellipse 클래스

##### API 버전 2

```java
public interface Resizable extends Drawable{
    public int getWidth();
    public int getHeight();
    public void setWidth(int width);
    public void setHeight(int height);
    public void setAbsoluteSize(int width, int height);
    
    public void setRelativeSize(int widthFactor, int heightFactor);
}
```

- 사용자가 겪는 문제

  1) 런타임 에러

  2) 컴파일 에러 (Ellipse를 포함하는 전체 애플리케이션 재빌드 시)

  cf) 바이너리 호환성은 유지(새로 추가된 메서드를 호출하지만 않으면 기존 클래스 파일 구현이 잘 동작한다)

  

#### 9.2. 디폴트 메서드란 무엇인가?

- 인터페이스에 메서드의 구현을 포함할 수 있는 방법
- default 키워드로 시작함
- 결과적으로 다중 상속이 가능해진다.

```java
default void setRelativeSize(int wFactor, int hFactor){
    setAbsoluteSize(getWidth() / wFactor, getHeight() / hFactor);
}
```

##### 추상 클래스와 인터페이스의 차이 

1. 클래스는 하나의 추상 클래스만 상속받을 수 있지만 인터페이스는 여러 개 구현 가능
2. 추상 클래스는 인스턴스 변수(필드)로 공통 상태를 가질 수 있찌만 인터페이스는 인스턴스 변수를 가질 수 없음

#### 9.3. 디폴트 메서드 활용 패턴 

##### 9.3.1. 선택형 메서드

- Iterator 인터페이스 : hasNext, next, remove 메서드 정의
- 하지만 사용자들이 remove 메서드를 잘 사용하지 않음
- 많은 Iterator를 정의한 클래스들에서 remove에 빈 구현을 제공함
- 하지만 디폴트 메서드를 이용하면 그럴 필요가 없어짐

##### 9.3.2. 동작 다중 상속

- 인터페이스는 한 클래스에서 여러 개 구현할 수 있으므로 *동작 다중 상속*이 가능해졌다.
  - 예시) Rotatable, Moveable, Resizable 인터페이스의 조합을 통해 게임에 필요한 다양한 클래스들 구현 가능
  - Monster : Rotatable, Moveable, Resizable
  - Sun : Moveable, Rotatable
  - 그리고 기본 클래스를 따로 구현할 필요가 없음

#### 9.4. 해석 규칙

1. 클래스가 항상 이긴다. (클래스 또는 슈퍼클래스에서 정의한 메서드 > 디폴트 메서드)
2. 1번을 제외한 상황에서는 서브인터페이스가 이긴다. (B가 A를 상속받는다면 B가 A를 이긴다)
3. 여전히 우선순위가 결정되지 않았다면 여러 인터페이스를 상속받는 클래스가 명시적으로 오버라이드 & 호출해야 한다.

##### 9.4.2. 디폴트 메서드를 제공하는 서브인터페이스가 이긴다

```java
public interface A {
    default void hello() {
        System.out.println("Hello From A");
    }
}

public interface B extends A {
    default void hello() {
        System.out.println("Hello From B");
    }
}

public class C implements B, A {
    public static void main(String args[]){
        new C().hello();
    }
}
```

```
Hello from B
```



```java
public class D implements A {
    void hello(){
        System.out.println("Hello from D");
    }
}
public class C extends D implements B, A {
    public static void main (String args[]){
        new C().hello();
    }
}
```

```
Hello from D
```



##### 9.4.3. 충돌 그리고 명시적인 문제 해결

```java
public interface A{
        default void hello() {
            System.out.println("Hello from A");
        }
    }

public interface B {
        default void hello() {
            System.out.println("Hello from B");
        }
    }

public class C implements B, A {}
```

```
Error: class C inherits unrelated defaults for hello() from types B and A

```

```java
public class C implements B, A {
       void hello(){
            B.super.hello();
        }
    }

```



##### 9.4.4. 다이아몬드 문제

```java
public interface A {
    default void hello() {
        System.out.println("Hello from A");
    }
}

public interface B extends A {}
public interface C extends A {}

public class D implements B, C {
    public static void main(String args[]){
        new D().hello();
    }
}

```

```
Hello from A

```

- B에 같은 시그너쳐의 디폴트 메서드 hello가 있다면 : B가 선택됨
- B와 C가 모두 디폴트 메서드 hello를 정의한다면: 충돌 발생
- 인터페이스 C에 *추상* 메서드 hello가 추가된다면 : 컴파일 에러  


