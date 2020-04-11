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
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>3.2.0</version>
</dependency>
<!--测试依赖-->
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

# MyBatis Plus设置
# 开启自动驼峰命名规则
mybatis-plus.configuration.map-underscore-to-camel-case=true
# 设置xml文件位置
mybatis-plus.mapper-locations=classpath*:/mapper/**/*.xml
# 打印SQL语句
# mybatis.configuration.log-impl=org.apache.ibatis.logging.stdout.StdOutImpl
# 设置mapper的日志级别, 将SQL打印
logging.level.com.lizhencheng.mp.mapper=debug
```

 在 Spring Boot 启动类中添加 @MapperScan 注解，扫描 Mapper 文件夹： 

```java
@SpringBootApplication
@MapperScan(value = {"com.lizhencheng.mp.mapper"})
public class MybatisPlusApplication {
    public static void main(String[] args) {
        SpringApplication.run(MybatisPlusApplication.class, args);
    }
}
```

因为在查询的时候需要用到分页插件，所以还需要设置分页插件，新建MybatisPlusConfig类 

```java
@Configuration
public class MybatisPlusConfig {
    /**
     * 分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        // paginationInterceptor.setLimit(你的最大单页限制数量，默认 500 条，小于 0 如 -1 不受限制);
        return paginationInterceptor;
    }
}
```

新建实体类Student

```java
@TableName("student")
public class Student {
    // 主键id,如果是自增id，需要使用这个注解
    @TableId(type = IdType.AUTO)
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
    省略get、set方法
}

```

 创建好实体类后，在数据库中创建实体类所对应的数据表 

```sql
CREATE TABLE `student`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `age` int(11) NULL DEFAULT NULL,
  `city_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `class_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `gender` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `student_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;
```

# Mapper类

```java
public interface StudentMapper extends BaseMapper<Student> {

}
```

可以发现，我们自己创建的StudentMapper什么东西都没有写，只是继承了`com.baomidou.mybatisplus.core.mapper.BaseMapper`接口，查看`BaseMapper`接口的源码可以发现，`BaseMapper`接口为我们提供了很多现成接口供我们使用。

## Mapper CRUD 接口

> 说明:
>
> - 通用 CRUD 封装[BaseMapper](https://gitee.com/baomidou/mybatis-plus/blob/3.0/mybatis-plus-core/src/main/java/com/baomidou/mybatisplus/core/mapper/BaseMapper.java)接口，为 `Mybatis-Plus` 启动时自动解析实体表关系映射转换为 `Mybatis` 内部对象注入容器
> - 泛型 `T` 为任意实体对象
> - 参数 `Serializable` 为任意类型主键 `Mybatis-Plus` 不推荐使用复合主键约定每一张表都有自己的唯一 `id` 主键
> - 对象 `Wrapper` 为 [条件构造器](https://mp.baomidou.com/guide/wrapper.html)


```java
public interface BaseMapper<T> extends Mapper<T> {
    // 插入一条记录
    int insert(T entity);
    // 删除（根据ID 批量删除）
    int deleteById(Serializable id);
    // 根据 columnMap 条件，删除记录
    int deleteByMap(@Param("cm") Map<String, Object> columnMap);
    // 根据 entity 条件，删除记录
    int delete(@Param("ew") Wrapper<T> wrapper);
    // 删除（根据ID 批量删除）
    int deleteBatchIds(@Param("coll") Collection<? extends Serializable> idList);
    // 根据 ID 修改
    int updateById(@Param("et") T entity);
    // 根据 whereEntity 条件，更新记录
    int update(@Param("et") T entity, @Param("ew") Wrapper<T> updateWrapper);
    // 根据 ID 查询
    T selectById(Serializable id);
    // 查询（根据ID 批量查询）
    List<T> selectBatchIds(@Param("coll") Collection<? extends Serializable> idList);
    // 查询（根据 columnMap 条件）
    List<T> selectByMap(@Param("cm") Map<String, Object> columnMap);
    // 根据 entity 条件，查询一条记录
    T selectOne(@Param("ew") Wrapper<T> queryWrapper);
    // 根据 Wrapper 条件，查询总记录数
    Integer selectCount(@Param("ew") Wrapper<T> queryWrapper);
    // 根据 entity 条件，查询全部记录
    List<T> selectList(@Param("ew") Wrapper<T> queryWrapper);
    // 查询（根据 columnMap 条件）
    List<Map<String, Object>> selectMaps(@Param("ew") Wrapper<T> queryWrapper);
    // 根据 Wrapper 条件，查询全部记录。注意： 只返回第一个字段的值
    List<Object> selectObjs(@Param("ew") Wrapper<T> queryWrapper);
    // 根据 entity 条件，查询全部记录（并翻页）
    IPage<T> selectPage(IPage<T> page, @Param("ew") Wrapper<T> queryWrapper);
    // 根据 Wrapper 条件，查询全部记录（并翻页）
    IPage<Map<String, Object>> selectMapsPage(IPage<T> page, @Param("ew") Wrapper<T> queryWrapper);
}
```

## 测试

在测试类中进行功能测试

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class MybatisPlusMapperTests {

    @Autowired
    private StudentMapper studentMapper;

    /* ################################# 新增 ################################# */
    // 插入一条记录
    @Test
    public void insert() {
        Student student = new Student();
        student.setCityName("南宁");
        student.setStudentName("王麻子");
        //SQL语句：INSERT INTO student ( student_name, city_name ) VALUES ( ?, ? )
        studentMapper.insert(student);
    }
    /* ################################# 删除 ################################# */
    // 根据 ID 删除
    @Test
    public void deleteById() {
        // SQL语句：DELETE FROM student WHERE id = ?
        studentMapper.deleteById(2L);
    }

    // 根据 columnMap 条件，删除记录
    @Test
    public void deleteByMap() {
        Map<String, Object> columnMap = new HashMap<>();
        columnMap.put("city_name", "桂林");
        columnMap.put("class_name", "计网153");
        // SQL语句：DELETE FROM student WHERE city_name = ? AND class_name = ?
        studentMapper.deleteByMap(columnMap);
    }

    // 根据 entity 条件，删除记录
    @Test
    public void delete() {
        Student student = new Student();
        student.setCityName("南宁");
        student.setStudentName("zhangsan");
        // SQL语句：DELETE FROM student WHERE student_name=? AND city_name=?
        studentMapper.delete(new QueryWrapper<>(student));
        // 通过SQL语句可以发现，条件语句只包含了实体类不为空的属性
    }

    // 删除（根据ID 批量删除）
    @Test
    public void deleteBatchIds() {
        List<Long> ids = new ArrayList<>();
        ids.add(4L);
        ids.add(5L);
        // SQL语句：DELETE FROM student WHERE id IN ( ? , ? )
        studentMapper.deleteBatchIds(ids);
    }
    /* ################################# 修改 ################################# */
    // 根据 ID 修改
    @Test
    public void updateById() {
        Student student = new Student();
        student.setId(1L);
        student.setCityName("深圳");
        student.setAge(16);
        // SQL语句：UPDATE student SET city_name=?, age=? WHERE id=?
        studentMapper.updateById(student);
    }

    // 根据 whereEntity 条件，更新记录
    @Test
    public void update() {
        Student entity = new Student(); // 修改内容
        entity.setAge(20);
        Student whereEntity = new Student(); // 修改条件
        whereEntity.setCityName("桂林");
        whereEntity.setClassName("计科152");
        // SQL语句：UPDATE student SET age=? WHERE class_name=? AND city_name=?
        studentMapper.update(entity, new UpdateWrapper<>(whereEntity));
    }
    /* ################################# 查询 ################################# */
    // 根据 ID 查询
    @Test
    public void selectById() {
        // SQL语句：SELECT id,student_name,gender,class_name,age,city_name,create_time,update_time FROM student WHERE id=?
        Student student = studentMapper.selectById(1L);
        System.out.println(student.toString());
    }

    // 查询（根据ID 批量查询）
    @Test
    public void selectBatchIds() {
        // idList 主键ID列表(不能为 null 以及 empty)
        List<Long> idList = new ArrayList<>();
        idList.add(1L);
        idList.add(2L);
        // SQL语句：SELECT id,student_name,gender,class_name,age,city_name,create_time,update_time FROM student WHERE id IN ( ? , ? )
        List<Student> studentList = studentMapper.selectBatchIds(idList);
        studentList.forEach(System.out::println);
    }

    // 查询（根据 columnMap 条件）
    @Test
    public void selectByMap() {
        Map<String, Object> columnMap = new HashMap<>();
        columnMap.put("city_name", "桂林");
        columnMap.put("class_name", "计科152");
        // SQL语句：SELECT id,student_name,gender,class_name,age,city_name,create_time,update_time FROM student WHERE city_name = ? AND class_name = ?
        List<Student> studentList = studentMapper.selectByMap(columnMap);
        studentList.forEach(System.out::println);
    }

    // 根据 Wrapper 条件，查询总记录数
    @Test
    public void selectCount() {
        Student student = new Student(); // 查询条件实体类
        student.setCityName("桂林");
        // SQL语句：SELECT COUNT( 1 ) FROM student WHERE city_name=?
        Integer count = studentMapper.selectCount(new QueryWrapper<>(student));
        System.out.println(count);
    }

    // 根据 entity 条件，查询全部记录
    @Test
    public void selectList() {
        Student entity = new Student(); // 查询条件实体类
        entity.setCityName("桂林");
        entity.setClassName("计科152");
        // SQL语句：SELECT id,student_name,gender,class_name,age,city_name,create_time,update_time FROM student WHERE class_name=? AND city_name=?
        List<Student> studentList = studentMapper.selectList(new QueryWrapper<>(entity));
        studentList.forEach(System.out::println);
    }

    // 根据 entity 条件，查询全部记录并分页（使用分页功能一定要设置PaginationInterceptor插件）
    @Test
    public void selectPage() {
        Student entity = new Student(); // 查询条件实体类
        entity.setCityName("桂林");
        entity.setClassName("计科152");
        // SQL语句：
        // SELECT id,student_name,gender,class_name,age,city_name,create_time,update_time
        // FROM student WHERE class_name=? AND city_name=? LIMIT ?,?
        Page<Student> studentPage = new Page<>(1,1);
        studentMapper.selectPage(studentPage,new QueryWrapper<>(entity));
        List<Student> records = studentPage.getRecords();// 分页对象记录列表
        System.out.println("总数：" + studentPage.getTotal());
        System.out.println("当前页：" + studentPage.getCurrent());
        System.out.println("当前分页总页数：" + studentPage.getPages());
        System.out.println("每页显示条数，默认 10：" + studentPage.getSize());
    }
}
```

# Service类

 新建StudentService接口 

```java
public interface StudentService extends IService<Student> {

}
```

 新建StudentServiceImpl类 

```java
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {

}
```
## Service CRUD 接口
> 说明:
>
> - 通用 Service CRUD 封装[IService](https://gitee.com/baomidou/mybatis-plus/blob/3.0/mybatis-plus-extension/src/main/java/com/baomidou/mybatisplus/extension/service/IService.java)接口，进一步封装 CRUD 采用 `get 查询单行`、 `remove 删除`、`list 查询集合` 、`page 分页` 前缀命名方式区分 `Mapper` 层避免混淆，
> - 泛型 `T` 为任意实体对象
> - 建议如果存在自定义通用 Service 方法的可能，请创建自己的 `IBaseService` 继承 `Mybatis-Plus` 提供的基类
> - 对象 `Wrapper` 为 [条件构造器](https://mp.baomidou.com/guide/wrapper.html)

`IService`接口有如下一些方法

### Save

```java
// 插入一条记录（选择字段，策略插入）
boolean save(T entity);
// 插入（批量）
boolean saveBatch(Collection<T> entityList);
// 插入（批量）
boolean saveBatch(Collection<T> entityList, int batchSize);
```

**参数说明**

|     类型      |   参数名   |     描述     |
| :-----------: | :--------: | :----------: |
|       T       |   entity   |   实体对象   |
| Collection<T> | entityList | 实体对象集合 |
|      int      | batchSize  | 插入批次数量 |

### SaveOrUpdate

```java
// TableId 注解存在更新记录，否插入一条记录
boolean saveOrUpdate(T entity);
// 根据updateWrapper尝试更新，否继续执行saveOrUpdate(T)方法
boolean saveOrUpdate(T entity, Wrapper<T> updateWrapper);
// 批量修改插入
boolean saveOrUpdateBatch(Collection<T> entityList);
// 批量修改插入
boolean saveOrUpdateBatch(Collection<T> entityList, int batchSize);
```

**参数说明**

|     类型      |    参数名     |               描述               |
| :-----------: | :-----------: | :------------------------------: |
|       T       |    entity     |             实体对象             |
|  Wrapper<T>   | updateWrapper | 实体对象封装操作类 UpdateWrapper |
| Collection<T> |  entityList   |           实体对象集合           |
|      int      |   batchSize   |           插入批次数量           |

### Remove

```java
// 根据 entity 条件，删除记录
boolean remove(Wrapper<T> queryWrapper);
// 根据 ID 删除
boolean removeById(Serializable id);
// 根据 columnMap 条件，删除记录
boolean removeByMap(Map<String, Object> columnMap);
// 删除（根据ID 批量删除）
boolean removeByIds(Collection<? extends Serializable> idList);
```

**参数说明**

|                类型                |    参数名    |          描述           |
| :--------------------------------: | :----------: | :---------------------: |
|             Wrapper<T>             | queryWrapper | 实体包装类 QueryWrapper |
|            Serializable            |      id      |         主键ID          |
|        Map<String, Object>         |  columnMap   |     表字段 map 对象     |
| Collection<? extends Serializable> |    idList    |       主键ID列表        |

### Update

```java
// 根据 UpdateWrapper 条件，更新记录 需要设置sqlset
boolean update(Wrapper<T> updateWrapper);
// 根据 whereEntity 条件，更新记录
boolean update(T entity, Wrapper<T> updateWrapper);
// 根据 ID 选择修改
boolean updateById(T entity);
// 根据ID 批量更新
boolean updateBatchById(Collection<T> entityList);
// 根据ID 批量更新
boolean updateBatchById(Collection<T> entityList, int batchSize);
```

**参数说明**

|     类型      |    参数名     |               描述               |
| :-----------: | :-----------: | :------------------------------: |
|  Wrapper<T>   | updateWrapper | 实体对象封装操作类 UpdateWrapper |
|       T       |    entity     |             实体对象             |
| Collection<T> |  entityList   |           实体对象集合           |
|      int      |   batchSize   |           更新批次数量           |

### Get

```java
// 根据 ID 查询
T getById(Serializable id);
// 根据 Wrapper，查询一条记录。结果集，如果是多个会抛出异常，随机取一条加上限制条件 wrapper.last("LIMIT 1")
T getOne(Wrapper<T> queryWrapper);
// 根据 Wrapper，查询一条记录
T getOne(Wrapper<T> queryWrapper, boolean throwEx);
// 根据 Wrapper，查询一条记录
Map<String, Object> getMap(Wrapper<T> queryWrapper);
// 根据 Wrapper，查询一条记录
<V> V getObj(Wrapper<T> queryWrapper, Function<? super Object, V> mapper);
```

**参数说明**

|            类型             |    参数名    |              描述               |
| :-------------------------: | :----------: | :-----------------------------: |
|        Serializable         |      id      |             主键ID              |
|         Wrapper<T>          | queryWrapper | 实体对象封装操作类 QueryWrapper |
|           boolean           |   throwEx    |   有多个 result 是否抛出异常    |
|              T              |    entity    |            实体对象             |
| Function<? super Object, V> |    mapper    |            转换函数             |

### List

```java
// 查询所有
List<T> list();
// 查询列表
List<T> list(Wrapper<T> queryWrapper);
// 查询（根据ID 批量查询）
Collection<T> listByIds(Collection<? extends Serializable> idList);
// 查询（根据 columnMap 条件）
Collection<T> listByMap(Map<String, Object> columnMap);
// 查询所有列表
List<Map<String, Object>> listMaps();
// 查询列表
List<Map<String, Object>> listMaps(Wrapper<T> queryWrapper);
// 查询全部记录
List<Object> listObjs();
// 查询全部记录
<V> List<V> listObjs(Function<? super Object, V> mapper);
// 根据 Wrapper 条件，查询全部记录
List<Object> listObjs(Wrapper<T> queryWrapper);
// 根据 Wrapper 条件，查询全部记录
<V> List<V> listObjs(Wrapper<T> queryWrapper, Function<? super Object, V> mapper);
```

**参数说明**

|                类型                |    参数名    |              描述               |
| :--------------------------------: | :----------: | :-----------------------------: |
|             Wrapper<T>             | queryWrapper | 实体对象封装操作类 QueryWrapper |
| Collection<? extends Serializable> |    idList    |           主键ID列表            |
|        Map<?String, Object>        |  columnMap   |         表字段 map 对象         |
|    Function<? super Object, V>     |    mapper    |            转换函数             |

### Page

```java
// 无条件翻页查询
IPage<T> page(IPage<T> page);
// 翻页查询
IPage<T> page(IPage<T> page, Wrapper<T> queryWrapper);
// 无条件翻页查询
IPage<Map<String, Object>> pageMaps(IPage<T> page);
// 翻页查询
IPage<Map<String, Object>> pageMaps(IPage<T> page, Wrapper<T> queryWrapper);
```

**参数说明**

|    类型    |    参数名    |              描述               |
| :--------: | :----------: | :-----------------------------: |
|  IPage<T>  |     page     |            翻页对象             |
| Wrapper<T> | queryWrapper | 实体对象封装操作类 QueryWrapper |

### Count

```java
// 查询总记录数
int count();
// 根据 Wrapper 条件，查询总记录数
int count(Wrapper<T> queryWrapper);
```

**参数说明**

|    类型    |    参数名    |              描述               |
| :--------: | :----------: | :-----------------------------: |
| Wrapper<T> | queryWrapper | 实体对象封装操作类 QueryWrapper |

### Chain

**query**

```java
// 链式查询 普通
QueryChainWrapper<T> query();
// 链式查询 lambda 式。注意：不支持 Kotlin
LambdaQueryChainWrapper<T> lambdaQuery(); 

// 示例：
query().eq("column", value).one();
lambdaQuery().eq(Entity::getId, value).list();
```

**update**

```java
// 链式更改 普通
UpdateChainWrapper<T> update();
// 链式更改 lambda 式。注意：不支持 Kotlin 
LambdaUpdateChainWrapper<T> lambdaUpdate();

// 示例：
update().eq("column", value).remove();
lambdaUpdate().eq(Entity::getId, value).update(entity);
```

## 测试

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class MybatisPlusServiceTests {

    @Autowired
    private StudentService studentService;

    /* ################################# 新增 ################################# */
    // 插入一条记录
    @Test
    public void insert() {
        Student student = new Student();
        student.setCityName("南宁");
        student.setStudentName("王麻子");
        //SQL语句：INSERT INTO student ( student_name, city_name ) VALUES ( ?, ? )
        studentService.save(student);
    }
}
```

