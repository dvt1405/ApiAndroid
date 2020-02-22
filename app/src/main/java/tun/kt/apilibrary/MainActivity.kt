package tun.kt.apilibrary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import tun.kt.apilib.apihistory.BaseActivity

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
