# ApiAndroid
This library help you check your api history which you use in your app
# Download
Download [the lastest version](https://bintray.com/beta/#/dvt1405/com.kt.checkApi/CheckAPIAndroid?tab=overview) via maven and gradle

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
    implementation 'com.kt:api:1.1.1'
}
```
# Usage
  ## Demo when use retrofit
  ### Step 1
  Add interceptor to your OkHttpClient
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
### Step 2: Extend BaseActivity
```kotlin
class WelcomeActivity : BaseActivity() {
...
}
```
### Step 3: Install your app to device and shake your app to show dialog api history:

