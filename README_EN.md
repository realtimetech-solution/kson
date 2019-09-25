# Kson
> This project is a library developed by REALTIMETECH Solution Division 2.

## 1. What is Kson?

Kson is a new data format compatible with the Json standards. It solves the ambiguous number type problem of Json and supports the following data types.
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
   } : "Now, you can use object like a key!",

   ["A", "B"] : "And, array!"
}
```
The other data is compatible with the Json standards.

In addition, Kson has a powerful Object Serializer and Deserializer (190% faster than Gson). Moreover, it supports Thread-Safe Pooling through KsonPool.

## 2. Usage

### 2.1. Get Started

#### 2.1.1. Gradle
```
allprojects {
   repositories {
      maven { url 'https://jitpack.io' }
   }
}

dependencies {
   implementation 'com.github.realtimetech-solution:kson:[Version]'
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
   <version>[Version]</version>
</dependency>
```

#### 2.1.3. Jar
```
Please refer to the releases part of this repository.
```

### 2.2. Usage

#### 2.2.1. Create KsonContext
All Parse, Serialize, Deserialize are available through KsonContext by default.

##### 2.2.1.1. Create Through Builder
```
KsonBuilder ksonBuilder = new KsonBuilder();

KsonContext ksonContext = ksonBuilder.build();
```

##### 2.2.1.2. General Creation
```
KsonContext ksonContext = new KsonContext();
```

##### 2.2.1.3. Create Through Thread-Safe Pool
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
String jsonString = ksonValue.toJsonString(); //Usage of standard Json format.
String ksonString = ksonValue.toKsonString(); //Usage of extended Kson format.
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

## 3. License and Afterword

Kson uses [Apache License 2.0](./LICENSE.txt). Please, leave your feedback if you have any suggestions!

```
JeongHwan, Park
+821032735003
parkjeonghwan@realtimetech.co.kr
```
