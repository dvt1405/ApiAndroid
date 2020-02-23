# ApiAndroid
This library help you check your api history which you use in your app
# Download
Download [the lastest version](https://bintray.com/beta/#/dvt1405/com.kt.checkApi/CheckAPIAndroid?tab=overview) via maven and gradle
##### Newest vertion = 1.1.2
In your build.gradle project
```bash
allprojects {
    repositories {
        ...
        maven{
            url  "https://dl.bintray.com/dvt1405/com.kt.checkApi"
        }
    }
}
```
In your build.gradle app module
```bash
dependencies {
    implementation 'com.kt:api:1.1.2'
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
customInterceptor.setLevel(CustomInterceptor.Level.BODY);
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
### Step 3: Install your app to device and shake your app to show dialog api history:
#### List api
![Untitled-1](https://user-images.githubusercontent.com/38560833/75093206-56fa3800-55b2-11ea-832d-7369bf2ada1c.png)
#### Info
![Untitled-2](https://user-images.githubusercontent.com/38560833/75093209-582b6500-55b2-11ea-8c01-31c198462aac.png)
#### Request
![Untitled-3](https://user-images.githubusercontent.com/38560833/75093210-58c3fb80-55b2-11ea-9c2f-b2e0a0b54381.png)
#### Response
![Untitled-4](https://user-images.githubusercontent.com/38560833/75093212-595c9200-55b2-11ea-89ec-acfa9686be9b.png)

