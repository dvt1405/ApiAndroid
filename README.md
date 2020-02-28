# PaintView
PaintView: Help you to drawing 
# Download
Download [the lastest version](https://bintray.com/beta/#/dvt1405/com.kt.checkApi/PaintView?tab=overview) via maven and gradle
##### Newest vertion = 1.0.0-beta
In your build.gradle app module
```gradle
def newestVerion = '1.1.5'
dependencies {
    implementation 'com.kt:paint-view:1.0.0-beta
```
# Usage
  ## Demo when use retrofit
  ### Step 1
  Add interceptor to your OkHttpClient
  ##### XML
```XML
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    xmlns:hedgehog="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">
    <tun.kt.paintview.PaintView
        android:id="@+id/paintView"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:backgroundBoardColor="#fff"
        app:brushColor="#FF7D27"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />
</androidx.constraintlayout.widget.ConstraintLayout>
            
```
### Step 2: Use in your activity
  ##### Kotlin

```kotlin
class WelcomeActivity : AppCompatActivity() {
    private var paintView: PaintView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            paintView = findViewById(R.id.paintView)
            // set attributes programming
            paintView?.brushSize = 10f
            paintView?.brushColor = Color.GREEN
            paintView?.backgroundBoardColor = Color.WHITE
        }
}
```
  ##### Java
```java
public class WelcomeActivity extends AppCompatActivity {
    private PaintView paintView;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        // set attributes programming
        paintView = findViewById(R.id.paintView);
        paintView.setBrushSize(10f);
        paintView.setBrushColor(Color.GREEN);
        paintView.setBackgroundBoardColor(Color.WHITE);
    }
    
...
}
```
