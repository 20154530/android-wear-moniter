package com.example.cloudmoniter.controls

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import androidx.fragment.app.FragmentManager
import com.example.cloudmoniter.R
import com.example.cloudmoniter.contract.CloudResourceType
import com.example.cloudmoniter.contract.SettingListener
import com.example.cloudmoniter.databinding.CloudResNodeBinding
import com.example.cloudmoniter.databinding.CloudResNodeSettingBinding
import com.example.cloudmoniter.manager.SettingManager
import com.example.cloudmoniter.models.CloudResNodeViewModel
import com.example.cloudmoniter.views.MainFragment
import java.util.*


class CloudNodeAdapter(context: Context, arrayList: ArrayList<CloudResNodeViewModel>, manager: FragmentManager) :
    ArrayAdapter<CloudResNodeViewModel>(context, 0, arrayList) {

    private lateinit var _binding: CloudResNodeBinding
    private lateinit var _lastbinding: CloudResNodeSettingBinding
    private lateinit var _bindingdic: MutableMap<Int, CloudResNodeBinding>;
    private var _dic: MutableMap<CloudResourceType, Int> =
        EnumMap(com.example.cloudmoniter.contract.CloudResourceType::class.java);
    private lateinit var _listitemBg: GradientDrawable;
    private lateinit var _settingListener: SettingListener;

    init {
        _dic[CloudResourceType.Unknown] = Color.parseColor("#b75c29")
        _dic[CloudResourceType.KiwivmVps] = Color.parseColor("#EE7D3D")
        _dic[CloudResourceType.TencentCloudCDN] = Color.parseColor("#007acc")
        val mDrawable = context.resources.getDrawable(R.drawable.listview_bg, null)
        _listitemBg = (mDrawable as GradientDrawable)
        _bindingdic = kotlin.collections.HashMap()
    }

    @SuppressLint("ViewHolder", "SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var currentItemView = convertView
        var model: CloudResNodeViewModel? = getItem(position);
        model?.Id = position;

        if (model?.restype == CloudResourceType.Unknown) {
            currentItemView = LayoutInflater.from(context).inflate(R.layout.cloud_res_node_setting, parent, false)
            _lastbinding = CloudResNodeSettingBinding.bind(currentItemView!!)
            _lastbinding.settingBtn.setOnClickListener {
                _settingListener?.onSetting();
            }
        } else {
            currentItemView = LayoutInflater.from(context).inflate(R.layout.cloud_res_node, parent, false)

            _binding = CloudResNodeBinding.bind(currentItemView!!);
            _bindingdic[model?.Id!!] = _binding;
            _binding.infoProcess.setOnClickListener {
                if (model != null)
                    SettingManager.Singleton.UpdateOne(model, _bindingdic[model.Id!!]!!);
            }

            if (model != null) {
                var bg = _listitemBg.constantState?.newDrawable() as GradientDrawable;
                bg.setColor(_dic[model.restype]!!);
                _binding.rootgrid.background = bg
                _binding.infoName.text = model.name;
                _binding.infoProcess.percentage = model.percentage
                _binding.infoSub.text = model.subInfo;
            } else {
                _binding.rootgrid.background = ColorDrawable(Color.BLACK)
                _binding.infoName.text = "none";
                _binding.infoProcess.percentage = 0f;
                _binding.infoSub.text = "";
            }
        }

        return currentItemView;
    }

    fun SetSettingListener(listener: SettingListener) {
        _settingListener = listener;
    }
}