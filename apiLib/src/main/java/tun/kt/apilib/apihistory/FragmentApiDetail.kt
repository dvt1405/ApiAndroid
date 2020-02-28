package tun.kt.apilib.apihistory

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import tun.kt.apilib.R

class FragmentDialog(private val api: ApiDetails, private val type: Enum<Type>) : Fragment() {
    companion object {
        const val URL = "Url"
        const val METHOD = "Method"
        const val TIME = "Connect time"
        const val NULL = "Null"

    }

    private var textHeader: TextView? = null
    private var textBody: TextView? = null
    private var recyclerView: RecyclerView? = null
    private var txtBody: TextView? = null
    private var btnCopy: TextView? = null
    private var clipboardManager: ClipboardManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Constants.isPhone = resources.displayMetrics.densityDpi < 600

        var view: View? = null
        view = inflater.inflate(R.layout.dialog_api_detail_fragment, null, false)
        initLayout(view)
        when (type) {
            Type.INFO -> {

            }
            Type.REQUEST, Type.RESPONSE -> {

            }
        }
        return view!!
    }

    private fun initLayout(view: View) {
        clipboardManager =
            view.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        textHeader = view.findViewById(R.id.textHeader)
        textBody = view.findViewById(R.id.textBody)
        recyclerView = view.findViewById(R.id.recyclerView)
        txtBody = view.findViewById(R.id.txtBody)
        btnCopy = view.findViewById(R.id.btnCopy)
        when (type) {
            Type.INFO -> {
                textHeader?.visibility = View.GONE
                textBody?.visibility = View.GONE
                btnCopy?.visibility = View.GONE
                val map = mutableMapOf<String, String>()
                map[URL] = api.request.url.toString()
                map[METHOD] = api.request.method
                map[TIME] = "${api.tookTimes} ns"
                map.putAll(api.headerRequest)
                map.putAll(api.headerResponse)
                val adapter = Adapter(map)
                recyclerView?.layoutManager = LinearLayoutManager(activity!!)
                recyclerView?.adapter = adapter
            }
            Type.REQUEST -> {
                textHeader?.visibility = View.VISIBLE
                textBody?.visibility = View.VISIBLE
                val adapter = Adapter(api.headerRequest)
                recyclerView?.layoutManager = LinearLayoutManager(activity!!)
                recyclerView?.adapter = adapter
                txtBody?.text = NULL
                api.bodyRequest?.let {
                    txtBody?.text = it
                }
            }
            Type.RESPONSE -> {
                textHeader?.visibility = View.VISIBLE
                textBody?.visibility = View.VISIBLE
                val adapter = Adapter(api.headerResponse)
                recyclerView?.layoutManager = LinearLayoutManager(activity!!)
                recyclerView?.adapter = adapter
                txtBody?.text = NULL
                api.bodyResponse?.let {
                    txtBody?.text = it
                }
            }
        }
        btnCopy?.setOnClickListener {
            view.context.clipToClipboard(txtBody?.text!!)
        }
    }

    enum class Type {
        INFO, REQUEST, RESPONSE
    }

    class Adapter(private val headers: Map<String, String>) :
        RecyclerView.Adapter<Adapter.ViewHolder>() {
        private val listHeader = arrayListOf<Pair<String, String>>()

        init {
            if (headers.isEmpty()) {
                listHeader.add(Pair("Null", ""))
            }
            headers.forEach {
                listHeader.add(Pair(it.key, it.value))
            }
        }

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            private val keyHeader: TextView = view.findViewById(R.id.keyHeaders)
            private val valueHeader: TextView = view.findViewById(R.id.valueHeaders)
            private val txtDot: TextView? = view.findViewById(R.id.dot)

            fun onBind(headers: Pair<String, String>) {
                keyHeader.text = headers.first
                valueHeader.text = headers.second
                if (Constants.isPhone!!) {
                    keyHeader.text = "[${headers.first}]"
                }
                if (headers.second.isEmpty()) {
                    valueHeader.visibility = View.GONE
                    txtDot?.visibility = View.GONE
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.dialog_api_detail_header_item,
                parent,
                false
            )
        )

        override fun getItemCount(): Int = listHeader.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) =
            holder.onBind(listHeader.get(position))

    }

    fun Context.clipToClipboard(text: CharSequence) {
        val clip = ClipData.newPlainText("label", text)
        clipboardManager?.setPrimaryClip(clip)
        Toast.makeText(this, "Copy success", Toast.LENGTH_SHORT).show()
    }
}

