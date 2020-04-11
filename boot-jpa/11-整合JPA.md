本系列文章都是基于SpringBoot2.2.5.RELEASE

# 依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.12</version>
</dependency>
```

# 配置文件

```properties
# 数据库属性
spring.datasource.url=jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT%2B8&useSSL=false
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# 使SQL语句打印在控制台
spring.jpa.show-sql=true
# 打印SQL中的参数
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=trace
# 格式化SQL语句
spring.jpa.properties.hibernate.format_sql=true
```

# 新建实体类

```java
@Entity
@Table(name = "student")
public class Student {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY) // 设置数据库中id自增
    private Long id;
    // 学生姓名
    private String studentName;
    // 学生姓名
    private String gender;
    // 班级名称
    private String className;
    // 学生年龄
    private Integer age;
    // 学生所在城市
    private String cityName;
    // 创建时间
    private Date createTime;
    // 更新时间
    private Date updateTime;
    // 省略get、set方法
}
```

# 新建持久层类

```java
public interface StudentRepository extends JpaRepository<Student, Long> {

}
```

StudentRepository 继承了JpaRepository，查看JpaRepository的源码可以发现，JpaRepository为我们提供了很多现成接口供我们使用。 

# 测试

 使用JpaRepository提供的方法操作数据库，使用测试类进行测试，如下所示 

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class JpaTests {

    @Autowired
    private StudentRepository studentRepository;

    // 新增
    @Test
    public void save() {
        Student student = new Student();
        student.setCityName("深圳");
        student.setStudentName("小李");
        studentRepository.save(student);
        System.out.println(student);
    }

    // 删除
    @Test
    public void deleteById() {
        // 通过id删除，如果id不存在，将会抛异常
        // 删除之前可以通过studentRepository.existsById(10L)来判断id是否存在，如果存在，则删除
        studentRepository.deleteById(10L);
    }

    // 修改
    @Test
    public void update() {
        // 通过id查询
        Student result = studentRepository.findById(9L).orElse(null); // 当查询结果不存在时则返回null
        result.setAge(9);
        studentRepository.save(result);
    }

    // 简单查询：查询所有记录
    @Test
    public void findAll() {
        List<Student> studentList = studentRepository.findAll();
        studentList.stream().forEach(s -> System.out.println(s.toString()));
    }

    // 简单查询：分页查询
    @Test
    public void findPage1() {
        Page<Student> studentPage = studentRepository.findAll(PageRequest.of(0,2));

        List<Student> studentList = studentPage.getContent();
        studentList.stream().forEach(s -> System.out.println(s.toString()));

        System.out.println("【TotalPages】"  + studentPage.getTotalPages());
        System.out.println("【totalElements】"  + studentPage.getTotalElements());
        System.out.println("【Number】"  + studentPage.getNumber());
        System.out.println("【Size】"  + studentPage.getSize());
        System.out.println("【NumberOfElements】"  + studentPage.getNumberOfElements());
    }

    // 简单查询：分页查询+排序（要么升序，要么降序）
    @Test
    public void findPage2() {

        Page<Student> studentPage = studentRepository.findAll(PageRequest.of(0,2,Sort.Direction.ASC,"age"));

        List<Student> studentList = studentPage.getContent();
        studentList.stream().forEach(s -> System.out.println(s.toString()));

        System.out.println("【TotalPages】"  + studentPage.getTotalPages());
        System.out.println("【totalElements】"  + studentPage.getTotalElements());
        System.out.println("【Number】"  + studentPage.getNumber());
        System.out.println("【Size】"  + studentPage.getSize());
        System.out.println("【NumberOfElements】"  + studentPage.getNumberOfElements());
    }

    // 简单查询：分页查询+排序（既有升序，又有降序）
    @Test
    public void findPage3() {

        Sort sort = Sort.by(Sort.Direction.DESC,"age"); // 年龄降序
        sort = sort.and(Sort.by(Sort.Direction.ASC,"className")); // 班级升序

        Page<Student> studentPage = studentRepository.findAll(PageRequest.of(0,2,sort));

        List<Student> studentList = studentPage.getContent();
        studentList.stream().forEach(s -> System.out.println(s.toString()));

        System.out.println("【TotalPages】"  + studentPage.getTotalPages());
        System.out.println("【totalElements】"  + studentPage.getTotalElements());
        System.out.println("【Number】"  + studentPage.getNumber());
        System.out.println("【Size】"  + studentPage.getSize());
        System.out.println("【NumberOfElements】"  + studentPage.getNumberOfElements());
    }
}
```

# 扩展查询

虽然JpaRepository为我们提供了很多操作方法，但是很多我们想要的查询功能还是没有。然而JPA 提供了非常优雅的方式来解决。 

## 根据实体类属性来查询

例如通过实体类的studentName属性进行查询，在StudentRepository接口中声明如下方法即可

```java
Student findByStudentName(String studentName);
```

还可以进行多个属性进行查询，例如根据实体类的studentName和cityName进行查询，在StudentRepository接口中声明如下方法即可

```java
Student findByStudentNameAndCityName(String studentName, String cityName);
```

根据实体类的属性进行分页查询

```java
Page<Student> findByClassName(String className, Pageable pageable);
```

## 动态查询

 JPA为我们提供了Example类实现动态查询， 具体用法看如下测试方法 

```java
// JPA动态查询
@Test
public void test1() {
    Student student = new Student();
    student.setCityName("南宁");
    student.setClassName("计科152");
    Example<Student> example = Example.of(student);
    // SQL语句 select * from student where city_name = '南宁' and class_name = '计科152'
    List<Student> studentList = studentRepository.findAll(example);
    studentList.forEach(s -> System.out.println(s.toString()));
}
@Test
public void test2() {
    Student student = new Student();
    student.setCityName("南宁");
    student.setClassName("计科152");

    Example<Student> example = Example.of(student);
    // SQL语句 select * from student where city_name = '南宁' and class_name = '计科152' order by age desc limit 0,2
    Page<Student> studentPage = studentRepository.findAll(example, PageRequest.of(0,2, Sort.Direction.DESC,"age"));

    List<Student> studentList = studentPage.getContent();
    studentList.stream().forEach(s -> System.out.println(s.toString()));

    System.out.println("【TotalPages】"  + studentPage.getTotalPages());
    System.out.println("【totalElements】"  + studentPage.getTotalElements());
    System.out.println("【Number】"  + studentPage.getNumber());
    System.out.println("【Size】"  + studentPage.getSize());
    System.out.println("【NumberOfElements】"  + studentPage.getNumberOfElements());
}
@Test
public void test3() {
    Student student = new Student();
    student.setCityName("南宁");
    student.setClassName("计科");

    // 设置属性的查询规则，
    // 有ignoreCase()，caseSensitive()，contains()，endsWith()，startsWith()，exact()，storeDefaultMatching()，regex()
    ExampleMatcher matcher = ExampleMatcher.matching()
        .withMatcher("className",startsWith());

    Example<Student> example = Example.of(student, matcher);

    List<Student> studentList = studentRepository.findAll(example);
    studentList.forEach(s -> System.out.println(s.toString()));
}
```

# 创建时间、更新时间自动赋值

在实体类上添加 `@EntityListeners(AuditingEntityListener.class)`注解，在创建时间字段上添加 `@CreatedDate`注解，在更新时间字段上添加 `@LastModifiedDate`，时间类型可以为：DateTime、Date、Long、long、JDK8日期和时间类型。然后还需要在启动类加上`@EnableJpaAuditing`注解。

