package tun.kt.apilib.apihistory

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity


abstract class BaseActivity : AppCompatActivity(), SensorEventListener {
    private var sensorService: SensorManager? = null
    private var sensor: Sensor? = null
    private var dialogApiHistory: DialogApiHistory? = null
    private var mLastTime = System.currentTimeMillis()
    private var mLastForce = 0L
    private var mLastX = -1.0f
    private var mLastY = -1.0f
    private var mLastZ = -1.0f
    private var mShakeCount = 0
    private var mLastShake = 0L

    companion object {
        private const val FORCE_THRESHOLD = 1050
        private const val TIME_THRESHOLD = 100
        private const val SHAKE_TIMEOUT = 500
        private const val SHAKE_DURATION = 1000
        private const val SHAKE_COUNT = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialogApiHistory = DialogApiHistory.getInstance(this)
        sensorService = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorService?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        dialogApiHistory?.alertDialog?.setOnDismissListener {
            if (sensor != null) {
                sensorService?.registerListener(
                    this,
                    sensor,
                    SensorManager.SENSOR_DELAY_NORMAL
                )

            }
        }
        dialogApiHistory?.alertDialog?.setOnShowListener {
            sensorService?.unregisterListener(this, sensor)
        }
    }

//    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
//        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
//                or View.SYSTEM_UI_FLAG_IMMERSIVE)
//        window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
//            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
//                window.decorView.systemUiVisibility = (
//                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
//            }
//        }
//        return super.onCreateView(name, context, attrs)
//    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        val now = System.currentTimeMillis()
        val dataX = event!!.values?.get(0)!!
        val dataY = event.values?.get(1)!!
        val dataZ = event.values?.get(2)!!
        if (now - mLastForce > SHAKE_TIMEOUT) {
            mShakeCount = 0
        }
        if (now - mLastTime > TIME_THRESHOLD) {
            val diff: Long = now - mLastTime
            val speed: Float = Math.abs(
                dataX + dataY + dataZ - mLastX - mLastY - mLastZ
            ) / diff * 10000
            if (speed > FORCE_THRESHOLD) {
                if (++mShakeCount >= SHAKE_COUNT && now - mLastShake > SHAKE_DURATION) {
                    mLastShake = now
                    mShakeCount = 0
                    sensorService?.unregisterListener(this, sensor)
                    dialogApiHistory?.show()
                }
                mLastForce = now
            }
            mLastTime = now
            mLastX = event.values?.get(0)!!
            mLastY = event.values?.get(1)!!
            mLastZ = event.values?.get(2)!!
        }

    }

    override fun onResume() {
        super.onResume()
        dialogApiHistory = DialogApiHistory.getInstance(this)
        dialogApiHistory?.dismiss()
        dialogApiHistory?.alertDialog?.setOnShowListener {
            sensorService?.unregisterListener(this, sensor)
        }
        dialogApiHistory?.alertDialog?.setOnDismissListener {
            if (sensor != null) {
                sensorService?.registerListener(
                    this,
                    sensor,
                    SensorManager.SENSOR_DELAY_NORMAL
                )

            }
        }
        if (sensor != null) {
            sensorService?.registerListener(
                this,
                sensor,
                Sensor.TYPE_ACCELEROMETER
            )
        }

    }

    override fun onPause() {
        super.onPause()
        dialogApiHistory?.dismiss()
        sensorService?.unregisterListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        dialogApiHistory?.dismiss()
    }

}