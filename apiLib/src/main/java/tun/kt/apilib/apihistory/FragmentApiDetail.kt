package tun.kt.apilib.apihistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_api_detail_fragment, null, false)
        initLayout(view)
        return view
    }

    fun initLayout(view: View) {
        textHeader = view.findViewById(R.id.textHeader)
        textBody = view.findViewById(R.id.textBody)
        recyclerView = view.findViewById(R.id.recyclerView)
        txtBody = view.findViewById(R.id.txtBody)
        when (type) {
            Type.INFO -> {
                textHeader?.visibility = View.GONE
                textBody?.visibility = View.GONE
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
    }

    enum class Type {
        INFO, REQUEST, RESPONSE
    }
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
        private val keyHeader: TextView
        private val valueHeader: TextView
        private val txtDot: TextView

        init {
            keyHeader = view.findViewById(R.id.keyHeaders)
            valueHeader = view.findViewById(R.id.valueHeaders)
            txtDot = view.findViewById(R.id.dot)
        }

        fun onBind(headers: Pair<String, String>) {
            keyHeader.text = headers.first
            valueHeader.text = headers.second
            if (headers.second.isEmpty()) {
                valueHeader.visibility = View.GONE
                txtDot.visibility = View.GONE
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