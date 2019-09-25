# Kson
> 이 프로젝트는 (주)리얼타임테크 솔루션 2팀에서 개발된 라이브러리입니다.

## 1. Kson란?

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
이외 데이터는 Json 표준과 호환됩니다.

또한, Kson은 강력한 Object Serializer와 Deserializer를 가지고 있습니다. (Gson대비 190% 빠름) 이는, KsonPool를 통해 Thread-Safe한 Pooling을 지원합니다.

자세한 내용은 KsonContext 클래스를 참조해주세요.

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

#### 2.2.2. String to KsonValue
```
KsonContext ksonContext = new KsonContext();

String jsonString = "{...}";
KsonValue ksonValue = ksonContext.fromString(jsonString);
```

#### 2.2.3. KsonValue to String
```
KsonValue ksonValue = ...;
String jsonString = ksonValue.toJsonString(); //일반적인 Json 포맷을 이용합니다.
String ksonString = ksonValue.toKsonString(); //확장된 Kson 포맷을 이용합니다.
```

#### 2.2.4. Object to KsonValue
```
KsonContext ksonContext = new KsonContext();

Person personObject = someObject;
KsonValue ksonValue = ksonContext.fromObject(personObject);
```

#### 2.2.5. KsonValue to Object
```
KsonContext ksonContext = new KsonContext();

KsonValue ksonValue = ...;
Person personObject = ksonContext.toObject(Person.class, ksonValue);
```

#### 2.2.6. String to (KsonValue) to Object
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
