# Meeting Log 3

### 4장 스트림 소개 - 토론

* **혁진님** : Stream과 ParallelStream의 차이점이 무엇인가?
* **동민님** : Stream은 한 파이프라인의 동작이 끝나면 그 다음 데이터를 처리하는 싱글 스레드 형식이고, ParallelStream은 병렬프로세싱을 해서 Stream 데이터들이 CPU 코어에 맞게 동시 다발적으로 파이프라인에 들어가서 가공되고 처리되게 된다. 하지만 stream으로 제대로 된 코드를 짰으면 parallelStream에서도 잘 동작해야 맞다.
* **혁진님** : 책의 예제나 실제 사용되는 것들을 보면 대부분이 stream이고 parallelStream은 보이지 않는다. 이 책의 초반부터 강조하는게 자바 8부터 병렬프로세싱 코딩이 가능해졌다는건데 이렇게 쓸거면 그 장점이 무의미해지지 않는가? 이럴거면 왜 쓰는건가?
* **동민님, 진우님** : 팀원분께서도 같은 말을 하셨다. 무작정 다 Stream, Lambda를 사용할게 아니라 적재적소에 써야한다. stream으로 쓰면 나중에 데이터가 커졌을 때 parallelStream으로 쉽게 바꿔서 대응할 수 있다.
* **주원님** : 여태 쓰면서 그다지 성능상의 장점은 느껴보지 못했다. 
* **동민님** : 하둡으로 빅데이터 다룰 때 써보면 확연한 차이를 느낄 수 있다.



### 5장 스트림 활용 - 토론

* **혁진님** : 154p 맨 위 예제를 시각화한 자료가 없어서 이해하기 어려웠다. 한번 그려보면 좋을 것 같다.

  ```java
  words.stream()
  	.map(word -> word.split(""))
  	.map(Array::stream)
  	.distinct()
  	.collect(toList());
  ```

* **진우님, 주원님** : 순차적으로 그려보겠다. 반환형으로 보면, `Stream<String>`이 첫줄에 의해 반환되고, 두번째 줄의 `map`에 의해 `Stream<String[]>`가 반환되고, 세번째 줄의 `map`에 의해 `Stream<Stream<String>>` 가 반환되고, distinct에 의해 `Stream<Stream<String>>`이 그대로 반환되고, `collect(toList())`에 의해 Stream 한겹이 벗겨지고 List가 붙는다. 결과적으로 `List<Stream<String>>` 이 반환된다. *잘못된 결과가 나온다*

* **동민님** : parallelStream이랑 stream의 차이점을 방금 검색해봤는데, stream은 한 사이클이 끝나야 다음 데이터가 들어간다.

* 혁진님 : 그럼 일반적인 for문이랑 똑같지 않나?

* 진우님 : for 문은 어느부분에서 걸리면 끝나지만, stream은 끝까지 다 수행한다.

*[외부 참고 사이트 - parallel Stream관련](http://blog.naver.com/PostView.nhn?blogId=tmondev&logNo=220945933678)*