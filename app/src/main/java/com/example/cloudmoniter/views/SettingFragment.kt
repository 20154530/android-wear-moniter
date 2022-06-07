package com.example.cloudmoniter.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.wear.widget.SwipeDismissFrameLayout
import com.example.cloudmoniter.R
import com.example.cloudmoniter.databinding.FragmentSettingBinding
import com.example.cloudmoniter.manager.SettingManager
import javax.security.auth.callback.Callback

class SettingFragment : Fragment() {

    private lateinit var _binding: FragmentSettingBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSettingBinding.inflate(layoutInflater);
        val configmanager = SettingManager.Singleton;

        _binding.root.addCallback(object : SwipeDismissFrameLayout.Callback() {

            override fun onDismissed(layout: SwipeDismissFrameLayout?) {
                var trans = parentFragmentManager.beginTransaction();
                trans.hide(this@SettingFragment);
                trans.commitNow()
            }
        })

        _binding.btnsubmit.setOnClickListener {
            var config = _binding.resurl.text.toString();
            configmanager.UseConfig(config, context!!);
            var trans = parentFragmentManager.beginTransaction();
            trans.hide(this@SettingFragment);
            trans.commitNow()
        }

        _binding.root.dismissMinDragWidthRatio = .8f;
        return _binding.root;
    }

}

