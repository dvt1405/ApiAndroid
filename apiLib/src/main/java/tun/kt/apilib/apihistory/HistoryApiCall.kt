package tun.kt.apilib.apihistory

import androidx.lifecycle.MutableLiveData
import kotlin.collections.ArrayList

class HistoryApiCall private constructor() {
    var listApiDetails: ArrayList<ApiDetails> = arrayListOf()
    var wasUpdated: MutableLiveData<Boolean> = MutableLiveData(true)

    companion object {
        var callNum: Int = 0
        private var INSTANCE: HistoryApiCall? = null
        fun getInstance(): HistoryApiCall {
            if (INSTANCE == null) {
                INSTANCE =
                    HistoryApiCall()
            }
            return INSTANCE!!
        }
    }
}