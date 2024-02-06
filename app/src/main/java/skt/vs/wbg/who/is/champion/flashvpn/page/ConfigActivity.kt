package skt.vs.wbg.who.`is`.champion.flashvpn.page

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import skt.vs.wbg.who.`is`.champion.flashvpn.R
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseActivityFlash
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseAd
import skt.vs.wbg.who.`is`.champion.flashvpn.databinding.ListLayoutBinding
import skt.vs.wbg.who.`is`.champion.flashvpn.page.VPNDataHelper.getImage
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.DataHelp.putPointYep
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.ConnectListViewModel

class ConfigActivity : BaseActivityFlash<ListLayoutBinding>() {
    override var conetcntLayoutId: Int
        get() = R.layout.list_layout
        set(value) {}

    private val listViewModel: ConnectListViewModel by viewModels()
    private var isConnect = false
    private var isHaveData = false
    var dataList = mutableListOf<LocaleProfile>()
    var recentlyList = mutableListOf<LocaleProfile>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BaseAd.getBackInstance().advertisementLoadingFlash(this)
        isConnect = intent.getBooleanExtra("IS_CONNECT", false)
        isHaveData = intent.getBooleanExtra("IS_HAVE_DATA", false)
        listViewModel.init(this, isConnect)
        mBinding.back.setOnClickListener {
            listViewModel.showEndScAd(this)
        }
        initAllServiceAdapter()
        initRecentlyServiceAdapter()
    }

    private fun initAllServiceAdapter() {
        if (isHaveData) {
            mBinding.tvRecently.visibility = View.VISIBLE
            mBinding.llRecently.visibility = View.VISIBLE
            mBinding.tvService.visibility = View.VISIBLE
            mBinding.locationList.visibility = View.VISIBLE
            mBinding.tvNoData.visibility = View.GONE
        } else {
            mBinding.tvRecently.visibility = View.GONE
            mBinding.llRecently.visibility = View.GONE
            mBinding.tvService.visibility = View.GONE
            mBinding.locationList.visibility = View.GONE
            mBinding.tvNoData.visibility = View.VISIBLE
        }
        dataList = VPNDataHelper.getAllVpnListData()
        val lm = LinearLayoutManager(this)
        val adapter = LocationsAdapter(dataList, listViewModel)
        mBinding.locationList.layoutManager = lm
        mBinding.locationList.adapter = adapter
    }

    private fun initRecentlyServiceAdapter() {
        recentlyList = VPNDataHelper.getRecentlyList()
        if (recentlyList.size > 0) {
            mBinding.rvRecently.visibility = View.VISIBLE
            mBinding.tvNoRecently.visibility = View.GONE
        } else {
            mBinding.rvRecently.visibility = View.GONE
            mBinding.tvNoRecently.visibility = View.VISIBLE
        }
        val lm = LinearLayoutManager(this)
        val adapter = LocationsAdapter(recentlyList, listViewModel)
        mBinding.rvRecently.layoutManager = lm
        mBinding.rvRecently.adapter = adapter
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            listViewModel.showEndScAd(this)
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        "f23".putPointYep(this)
    }
}

class LocationsAdapter(
    private val dataList: MutableList<LocaleProfile>,
    private val listViewModel: ConnectListViewModel
) :
    RecyclerView.Adapter<AdapterViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_config, parent, false)
        return AdapterViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: AdapterViewHolder, @SuppressLint("RecyclerView") position: Int
    ) {
        val name = holder.itemView.findViewById<AppCompatTextView>(R.id.itemLocationName)
        val image = holder.itemView.findViewById<AppCompatImageView>(R.id.itemLocationImage)
        val check = holder.itemView.findViewById<AppCompatImageView>(R.id.itemLocationCheckImage)
        if (position == 0) {
            name.text = "Fast Server"
        } else {
            name.text = dataList[position].name + "-" + dataList[position].city
        }
        Glide.with(holder.itemView.context)
            .load(getImage(dataList[position].name)).into(image)
        if (listViewModel.isConnected && VPNDataHelper.nodeIndex == position) {
            check.setImageResource(R.drawable.flash_checked)
        } else {
            check.setImageResource(R.drawable.flash_unchecked)
        }
        holder.itemView.setOnClickListener {
            listViewModel.onItemClick(position)
        }
    }


}

class AdapterViewHolder(view: View) : RecyclerView.ViewHolder(view)