package tun.kt.apilibrary

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.hedgehog.ratingbar.RatingBar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tun.kt.apilib.apihistory.BaseActivity
import tun.kt.apilib.apihistory.DialogApiHistory
import tun.kt.paintview.PaintView

class MainActivity : BaseActivity() {
    private var textView: TextView? = null
    private var ratingBar: RatingBar? = null
    private var paintView: PaintView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView = findViewById(R.id.hello)
        ratingBar = findViewById(R.id.ratingBar)
        paintView = findViewById(R.id.paintView)
        Log.e("ratingbar",if(ratingBar==null) "null" else "NoNUll")
        ratingBar?.halfStar(false)
        ratingBar?.setOnRatingChangeListener {
            paintView?.brushSize = it
        }
        paintView?.brushSize = 10f
        textView?.setOnClickListener {
//            DialogApiHistory.getInstance(this).show()
            paintView?.brushColor = Color.GREEN
            paintView?.backgroundBoardColor = Color.GREEN
            paintView?.emboss()
        }
        try {
            RetrofitService().service.listRepos("dvt1405")?.enqueue(object : Callback<List<String>?> {
                override fun onFailure(call: Call<List<String>?>, t: Throwable) {
                    Log.e("Error", t.message, t)
                }

                override fun onResponse(call: Call<List<String>?>, response: Response<List<String>?>) {
                    Log.e("Response", response.code().toString())
                }
            })
        } catch (e: Exception) {
        }

    }
}