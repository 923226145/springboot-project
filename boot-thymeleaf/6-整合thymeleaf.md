本系列文章都是基于SpringBoot2.2.5.RELEASE

# 默认配置属性

Thymelea的自动配置类为`ThymeleafAutoConfiguration`，配置属性类为`ThymeleafProperties`

```java
@ConfigurationProperties(prefix = "spring.thymeleaf")
public class ThymeleafProperties {
    // 默认编码为UTF_8
	private static final Charset DEFAULT_ENCODING = StandardCharsets.UTF_8;
    // 默认HTML页面存放路径
    // 只要我们把HTML页面放在classpath:/templates/, thymeleaf就能自动渲染
    // maven会将resources下面的文件打包到classpath路径下, 
    // 所以我们一般把HTML页面写在resources/templates
	public static final String DEFAULT_PREFIX = "classpath:/templates/";
    // 默认后缀为 .html
	public static final String DEFAULT_SUFFIX = ".html";
    ......
}
```

常见的属性配置如下

```properties
# 缓存默认值true, 开发环境一般设置为false
spring.thymeleaf.cache=true
# 编码默认值为UTF-8
spring.thymeleaf.encoding=UTF-8
# HTML页面的前缀默认值为classpath:/templates/
spring.thymeleaf.prefix=classpath:/templates/
# HTML页面后缀默认值.html
spring.thymeleaf.suffix=.html
# 默认为HTML
spring.thymeleaf.mode=HTML
```

# 依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```

# 编写代码

```java
@Controller
public class UserController {
    @GetMapping("/user")
    public String user(Model model) {
        List<Map<String, Object>> userList = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("id",i);
            map.put("username", "name_" +  i);
            userList.add(map);
        }
        model.addAttribute("userList",userList);
        return "user";
    }
}
```

在resources/templates目录下新建一个user.html

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="UTF-8">
    <title>Spring MVC案例</title>
    <!-- 新 Bootstrap4 核心 CSS 文件 -->
    <link rel="stylesheet" href="https://cdn.staticfile.org/twitter-bootstrap/4.1.0/css/bootstrap.min.css">
    <!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
    <script src="https://cdn.staticfile.org/jquery/3.2.1/jquery.min.js"></script>
    <!-- popper.min.js 用于弹窗、提示、下拉菜单 -->
    <script src="https://cdn.staticfile.org/popper.js/1.12.5/umd/popper.min.js"></script>
    <!-- 最新的 Bootstrap4 核心 JavaScript 文件 -->
    <script src="https://cdn.staticfile.org/twitter-bootstrap/4.1.0/js/bootstrap.min.js"></script>
</head>
<body>
<div class="container">
    <div class="row">
        <div class="col-md-12">
            <table class="table table-bordered">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>姓名</th>
                </tr>
                </thead>
                <tbody>
                <tr th:each="user : ${userList}">
                    <td th:text="${user.id}"></td>
                    <td th:text="${user.username}"></td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>
</div>
</body>
</html>
```

user.html头部需要导入thymeleaf的名称空间

```html
<html lang="en" xmlns:th="http://www.w3.org/1999/xhtml">
```

在application.properties中设置关闭thymeleaf缓存

```properties
spring.thymeleaf.cache=false
```

# 语法

官方文档：https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html

## 获取普通变量

```html
<h1 th:text="${username}"></h1>
<h1>[[${username}]]</h1>

<script th:inline="javascript">
    var username = [[${username}]];
</script>
```

## 遍历循环

```html
<tr th:each="user : ${userList}">
    <td th:text="${user.id}"></td>
    <td th:text="${user.username}"></td>
</tr>
```

## 条件语句

```html
<tr th:each="user : ${userList}">
    <td th:text="${user.id}"></td>
    <td th:text="${user.username}"></td>
    <td th:if="${user.gender} == 1" th:text="男"></td>
    <td th:if="${user.gender} == 0" th:text="女"></td>
</tr>
或者
<tr th:each="user : ${userList}">
    <td th:text="${user.id}"></td>
    <td th:text="${user.username}"></td>
    <td th:switch="${user.gender}">
        <span th:case="0">女</span>
        <span th:case="1">男</span>
    </td>
</tr>
```

## URL

```html
<!-- Will produce '/gtvg/order/details?orderId=3' (plus rewriting) -->
<a href="details.html" th:href="@{/order/details(orderId=${o.id})}">view</a>

<!-- Will produce '/gtvg/order/3/details' (plus rewriting) -->
<a href="details.html" th:href="@{/order/{orderId}/details(orderId=${o.id})}">view</a>
```

## 模板

resources/templates/layout.html

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="UTF-8">
    <title>layout</title>
</head>
<body>
<div th:fragment="header">
    <h1>我是头部</h1>
</div>
<div th:fragment="footer">
    <h1>我是底部</h1>
</div>
</body>
</html>
```

其它页面可以复用layout.html里面的代码，用法如下：replace代表替换，insert代表插入

```html
<div th:replace="~{layout :: header}"></div>
<div>主题内容</div>
<div th:insert="~{layout :: footer}"></div>
```

## request/session属性

```html
<h1 th:text="${#request.getAttribute('key')}"></h1>
<h1 th:text="${#session.getAttribute('key')}"></h1>
```

## 时间

```java
/*
 * 如果处理时间为Java8的LocalDateTime，则使用temporals，例如
 * ${#temporals.format(date, 'yyyy-MM-dd HH:mm:ss')}
 * ======================================================================
 * See javadoc API for class org.thymeleaf.expression.Dates
 * ======================================================================
 */
/*
 * Format date with the standard locale format
 * Also works with arrays, lists or sets
 */
${#dates.format(date)}
${#dates.arrayFormat(datesArray)}
${#dates.listFormat(datesList)}
${#dates.setFormat(datesSet)}

/*
 * Format date with the ISO8601 format
 * Also works with arrays, lists or sets
 */
${#dates.formatISO(date)}
${#dates.arrayFormatISO(datesArray)}
${#dates.listFormatISO(datesList)}
${#dates.setFormatISO(datesSet)}

/*
 * Format date with the specified pattern
 * Also works with arrays, lists or sets
 */
${#dates.format(date, 'dd/MMM/yyyy HH:mm')}
${#dates.arrayFormat(datesArray, 'dd/MMM/yyyy HH:mm')}
${#dates.listFormat(datesList, 'dd/MMM/yyyy HH:mm')}
${#dates.setFormat(datesSet, 'dd/MMM/yyyy HH:mm')}

/*
 * Obtain date properties
 * Also works with arrays, lists or sets
 */
${#dates.day(date)}                    // also arrayDay(...), listDay(...), etc.
${#dates.month(date)}                  // also arrayMonth(...), listMonth(...), etc.
${#dates.monthName(date)}              // also arrayMonthName(...), listMonthName(...), etc.
${#dates.monthNameShort(date)}         // also arrayMonthNameShort(...), listMonthNameShort(...), etc.
${#dates.year(date)}                   // also arrayYear(...), listYear(...), etc.
${#dates.dayOfWeek(date)}              // also arrayDayOfWeek(...), listDayOfWeek(...), etc.
${#dates.dayOfWeekName(date)}          // also arrayDayOfWeekName(...), listDayOfWeekName(...), etc.
${#dates.dayOfWeekNameShort(date)}     // also arrayDayOfWeekNameShort(...), listDayOfWeekNameShort(...), etc.
${#dates.hour(date)}                   // also arrayHour(...), listHour(...), etc.
${#dates.minute(date)}                 // also arrayMinute(...), listMinute(...), etc.
${#dates.second(date)}                 // also arraySecond(...), listSecond(...), etc.
${#dates.millisecond(date)}            // also arrayMillisecond(...), listMillisecond(...), etc.

/*
 * Create date (java.util.Date) objects from its components
 */
${#dates.create(year,month,day)}
${#dates.create(year,month,day,hour,minute)}
${#dates.create(year,month,day,hour,minute,second)}
${#dates.create(year,month,day,hour,minute,second,millisecond)}

/*
 * Create a date (java.util.Date) object for the current date and time
 */
${#dates.createNow()}

${#dates.createNowForTimeZone()}

/*
 * Create a date (java.util.Date) object for the current date (time set to 00:00)
 */
${#dates.createToday()}

${#dates.createTodayForTimeZone()}
```

