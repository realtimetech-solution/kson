<p align="right">
  <b>Languages</b><br>
  <a href="README.md">Korean</a> |
  <a href="README_EN.md">English</a>
</p>


# Kson
> 이 프로젝트는 (주)리얼타임테크 솔루션 2팀에서 개발된 라이브러리입니다.

## 1. Kson란?

### 1.1. 지원 기능

>
>- #### Gson 보다 190% 빠른 성능
>      더 빠른 Object Serialize, Deserialize, Json Parse를 경험하세요!
>
>- #### 확장된 Number 및 Key Types 지원 (Optional)
>      모호한 숫자 데이터를 명시하고, Dict의 Key로서 Array와 Dict를 이용해보세요!
>
>- #### 자동 Interface 및 Abstract Class 필드 기록 (Optional)
>      Object Serialize, Deserialize 단계의 문제인 Interface, Abstract Class 형태의 필드를 마음껏 이용하세요!
>
>- #### Primary Object 설정 기능 (Optional)
>      여러 객체에서 반복적으로 포함하는 Object를 Primary Key를 지정해서 하나로 묶어보세요! 
>


### 1.2. 데이터 형상

Kson은 Json 표준을 호환하는 새로운 데이터 포맷입니다, 기존 Json의 모호한 정수 타입 문제를 해결한 구현체로서 아래와 같은 데이터 형태를 지원합니다.

```
{
   "longValueLower"    : 100l,
   "floatValueLower"   : 100.2f,
   "doubleValueLower"  : 100.2d,

   "longValueUpper"    : 100L,
   "floatValueUpper"   : 100.2F,
   "doubleValueUpper"  : 100.2D,

   "automaticInteger"  : 20,
   "automaticDecimal"  : 10.1

   {
     "A": "B"
   } : "Now, you can use object like key!",

   ["A", "B"] : "And, array!"
}
```

<<<<<<< HEAD

=======
>>>>>>> 424b6e4... Update README.md
## 2. 사용법

### 2.1. 받아오기

#### 2.1.1. Gradle
```
allprojects {
   repositories {
      maven { url 'https://jitpack.io' }
   }
}

dependencies {
   implementation 'com.github.realtimetech-solution:kson:[버전]'
}
```

#### 2.1.2. Maven
```
<repositories>
   <repository>
      <id>jitpack.io</id>
      <url>https://jitpack.io</url>
   </repository>
</repositories>

<dependency>
   <groupId>com.github.realtimetech-solution</groupId>
   <artifactId>kson</artifactId>
   <version>[버전]</version>
</dependency>
```

#### 2.1.3. Jar
```
이 저장소의 릴리즈 페이지를 참고해주세요.
```

### 2.2. 사용법

#### 2.2.1. KsonContext 생성
기본적으로 모든 Parse, Serialize, Deserialize는 KsonContext 객체를 통해서 사용 가능합니다.

##### 2.2.1.1. Builder를 통해서 생성
```
KsonBuilder ksonBuilder = new KsonBuilder();

KsonContext ksonContext = ksonBuilder.build();
```

##### 2.2.1.2. 일반적인 생성
```
KsonContext ksonContext = new KsonContext();
```

##### 2.2.1.3. Thread-Safe Pool를 이용한 생성
```
KsonBuilder ksonBuilder = new KsonBuilder();
KsonPool ksonPool = new KsonPool(ksonBuilder);

KsonContext ksonContext = ksonPool.get();
```

#### 2.2.2. String to JsonValue
```
KsonContext ksonContext = new KsonContext();

String jsonString = "{...}";
JsonValue JsonValue = ksonContext.fromString(jsonString);
```

#### 2.2.3. JsonValue to String
```
JsonValue JsonValue = ...;
String jsonString = JsonValue.toJsonString(); //일반적인 Json 포맷을 이용합니다.
String ksonString = JsonValue.toKsonString(); //확장된 Kson 포맷을 이용합니다.
```

#### 2.2.4. Object to JsonValue
```
KsonContext ksonContext = new KsonContext();

Person personObject = someObject;
JsonValue JsonValue = ksonContext.fromObject(personObject);
```

#### 2.2.5. JsonValue to Object
```
KsonContext ksonContext = new KsonContext();

JsonValue JsonValue = ...;
Person personObject = ksonContext.toObject(Person.class, JsonValue);
```

#### 2.2.6. String to (JsonValue) to Object
```
KsonContext ksonContext = new KsonContext();

String jsonString = "{...}";
Person personObject = ksonContext.toObject(Person.class, jsonString);
```

## 3. 라이센스 및 남기는 말

Kson는 [Apache License 2.0](./LICENSE.txt) 라이센스를 이용합니다, 여러분의 적극적인 이슈, 기능 피드백을 기대합니다.

```
JeongHwan, Park
+821032735003
parkjeonghwan@realtimetech.co.kr
```
