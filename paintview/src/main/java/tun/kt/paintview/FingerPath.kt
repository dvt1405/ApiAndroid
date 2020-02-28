package tun.kt.paintview

import android.graphics.Path

data class FingerPath(
    var color: Int,
    var emboss: Boolean,
    var blur: Boolean,
    var strokeWidth: Float,
    var path: Path
)