# PaintView
This library help you check your api history which you use in your app
# Download
Download [the lastest version](https://bintray.com/beta/#/dvt1405/com.kt.checkApi/CheckAPIAndroid?tab=overview) via maven and gradle
##### Newest vertion = 1.1.5
In your build.gradle app module
```gradle
def newestVerion = '1.1.5'
dependencies {
    implementation 'com.kt:api:$newestVerion'
}
```
# Usage
  ## Demo when use retrofit
  ### Step 1
  Add interceptor to your OkHttpClient
  ##### Kotlin
```kotlin
val interceptor = CustomInterceptor()
interceptor.level = CustomInterceptor.Level.BODY
val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
Retrofit.Builder()
            .baseUrl(Constants.DEVICE_BASE_URL[env])
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(API::class.java)
            
```
  ##### Java

```java
CustomInterceptor interceptor = new CustomInterceptor();
interceptor.setLevel(CustomInterceptor.Level.BODY);
OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build();
// create retrofit interface
API retrofit = new Retrofit.Builder()
            .baseUrl(Constants.DEVICE_BASE_URL[env])
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(API::class.java);
            
```

### Step 2: Extend BaseActivity
  ##### Kotlin

```kotlin
class WelcomeActivity : BaseActivity() {
...
}
```
  ##### Java
```java
public class WelcomeActivity extends BaseActivity {
...
}
```


