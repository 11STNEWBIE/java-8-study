import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Ch5StudyPractice {

    public static void main(String[] args) {

        // Q1 : 4개의 단어로부터 문자들을 추출하고, 이 문자들이 고유하게 정렬된 리스트로 만들기
        String[] strArr = {"Hello", "World", "Home", "Study"};

        // TODO : 옆에 반환되는 자료구조를 써봅시다
        List<String> result = Arrays.stream(strArr)         // ???
                .map(word -> word.split(""))            // ???
                .flatMap(Arrays::stream)        // ???
                .map(String::toLowerCase)
                .distinct()     // ???
                .sorted()
                .collect(Collectors.toList());
        // output : d e h l m o r s t u w y


        // Q2, Q3, Q4 : 주어진 문제 풀기
        List<NewBie> newBies = Arrays.asList(
                new NewBie("jinwoo", "finding", 123),
                new NewBie("hyukjin", "finding", 153),
                new NewBie("jiwon-kang", "finding", 142),
                new NewBie("jiwon-kwon", "user-account", 125),
                new NewBie("dongmin", "SO", 221),
                new NewBie("kyubum", "rm", 266),
                new NewBie("juwon", "pdp", 177),
                new NewBie("hyejin", "escrow", 193)
        );



        int ans2 = 0; // Q2 TODO : 사번 160이상인 사람들의 사번의 합
        System.out.println("Q2: " + ans2);


        
        String ans3 = "";       // Q3 TODO : 가장 높은 사번의 사람 이름 출력
        System.out.println("Q3: " + ans3);


        
        long ans4 = 0;      // Q4 TODO : 부서가 finding이 아닌 사람들 카운트
        System.out.println("Q4: " + ans4);


    }
}


class NewBie {
    private String name;
    private String department;
    private int id;

    public NewBie(String name, String department, int id) {
        this.name = name;
        this.department = department;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getDepartment() {
        return department;
    }

    @Override
    public String toString() {
        return String.format("name : %s - dep : %s - id : %d", getName(), getDepartment(), 
                getId());
    }
}









/**
 * 
 * 
 *      아래에는 해답이 있습니다. 다 푸신 경우만 확인!
 * 
 * 
 * 
 * 
 * */









/*
* ANSWER
*
* Q1 : Stream<String> / Stream<String[]> / Stream<String> / Stream<String>
*
* Q2 :
*
  int ans2 = newBies.stream()
                .filter(a -> a.getId() >= 160)
                .map(NewBie::getId)
                .reduce(0, Integer::sum);
*
* ans: 857
*
*
* Q3 :
    String ans3 = newBies.stream()
                .max(Comparator.comparing(NewBie::getId))
                .map(NewBie::getName)
                .get();
*
* ans: kyubum
*
*
* Q4 :
   long ans4 = newBies.stream()        
                .filter(newbie -> !"finding".equals(newbie.getDepartment()))
                .count();
*
* ans : 5
*
* */
