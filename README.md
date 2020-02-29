# PaintView
PaintView: Help you to drawing 
# Download
Download [the lastest version](https://bintray.com/beta/#/dvt1405/com.kt.checkApi/PaintView?tab=overview) via maven and gradle
##### Newest vertion = 1.0.0
In your build.gradle app module
```gradle
def newestVerion = '1.1.0'
dependencies {
    implementation 'com.kt:paint-view:$newestVersion
```
# Attributes
| Name          | Format        |   Default    | Description |
| ------------- |:-------------:| :------------:| :-----------|
| strokeWidth | dimension | 2dp | brush size
| brushSize | dimension | 2dp |brush size
| brushColor | color | #4D4B4C | color of brush
| backgroundBoardColor | color | Color.WHITE | set background paint view
| emboss | boolean | false | set maskfilter: emboss
| blur | boolean | false | set maskfilter: blur
| paintStyle | enum | STROKE | set paint style: FILL or STROKE or FILL_AND_STROKE
| paintStrokeJoint | enum | ROUND | paintStrokeJoint: MITER or ROUND or BEVEL
| strokeCap | enum | ROUND| paintStrokeCap: BUTT or ROUND or SQUARE
|isAntiAlias | boolean | false | 
isDither|boolean|false|
# Variables
| Name          | Type        |   Description    |
| ------------- |:-------------:| :------------:|
|bitmapPaint | Paint |get/set bitmap to draw
|drawPath | FingerPath|get/set paths draw
|brushColor | Int (Color int) | get/set
|brushSize|Float(dp)|get/set: brusdhSize = 2(dp)|
|backgroundBoardColor|Int (Color int)|get/set
|isEmptyBrush | boolean| get
|picture|ByteArray|get: capture your view
|pictureBitmap|Bitmap|get: capture your view
|pictureBase64|String(Base64)|get: capture your view

# Methods
| Name          | Return        |   Usage    | Params | Description 
| ------------- |:-------------:| :------------:| :------------: |:------------: |
|clear| Void| clear your view|none
|undo | Void | undo|none
|redo | Void | redo|none
|getPicture(type: Type, otherBackgroundColor: Int)|Any|capture your view | type: Type(BITMAP or BASE64 or BYTE_ARRAY)|Cast to each type befor use (bitmap or String or bytearray)


# Usage
  ## Demo 
  ### Step 1
  Add view to your layout xml file
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
        app:strokeWidth="10dp"
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
