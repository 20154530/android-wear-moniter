package com.example.cloudmoniter.views

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.cloudmoniter.contract.IUpdater
import com.example.cloudmoniter.contract.SettingListener
import com.example.cloudmoniter.controls.CloudNodeAdapter
import com.example.cloudmoniter.databinding.MainFragmentBinding
import com.example.cloudmoniter.manager.SettingManager


class MainFragment(settingListener: SettingListener) : Fragment() {

    private lateinit var _binding: MainFragmentBinding
    private lateinit var _adapter: CloudNodeAdapter;
    private lateinit var _handler: Handler;
    private var _settinglistener: SettingListener = settingListener;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = MainFragmentBinding.inflate(inflater)
        _handler = Handler(Looper.getMainLooper());
        val configmanager = SettingManager.Singleton;

        configmanager.LoadConfig();

        _adapter = CloudNodeAdapter(inflater.context, configmanager.ResourceConfigs, parentFragmentManager)
        configmanager.SetAdapter(_adapter);
        _binding.resList.adapter = _adapter;

        _adapter.SetSettingListener(_settinglistener)
        val mainupdater = MainUpdater(_binding.root, _adapter)
        _binding.root.setOnRefreshListener {
            configmanager.UpdateAll(mainupdater)
        }
        return _binding.root;
    }

    internal class MainUpdater(layout: SwipeRefreshLayout, adapter: CloudNodeAdapter) : IUpdater {
        private var _updater: SwipeRefreshLayout = layout;
        private var _adapter: CloudNodeAdapter = adapter;

        override var isUpdating: Boolean
            get() = _updater.isRefreshing;
            set(value) {
                _updater.isRefreshing = value;
                val h: Handler = Handler(_updater.context.mainLooper);
                if (!value) {
                    h.post {
                        _adapter.notifyDataSetChanged();
                    }
                }
            }
    }


}
