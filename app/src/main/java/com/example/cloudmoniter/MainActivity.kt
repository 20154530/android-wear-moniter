package com.example.cloudmoniter

import android.animation.Keyframe
import android.animation.PropertyValuesHolder
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.animation.LinearInterpolator
import android.view.animation.PathInterpolator
import androidx.fragment.app.FragmentActivity
import com.example.cloudmoniter.contract.SettingListener
import com.example.cloudmoniter.databinding.ActivityMainBinding
import com.example.cloudmoniter.manager.SettingManager
import com.example.cloudmoniter.views.MainFragment
import com.example.cloudmoniter.views.SettingFragment

class MainActivity : FragmentActivity(), SettingListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainFragment: MainFragment;
    private lateinit var settingFragment: SettingFragment;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        SettingManager.Singleton.Init(applicationContext)
        mainFragment = MainFragment(this)
        settingFragment = SettingFragment()

        showMainFragment();
    }

    private fun showMainFragment() {
        val maintrans = supportFragmentManager.beginTransaction();
        maintrans.add(binding.mainhost.id, mainFragment);
        maintrans.add(binding.mainhost.id, settingFragment);
        maintrans.hide(settingFragment);
        maintrans.commit();
    }

    override fun onSetting() {
        val maintrans = supportFragmentManager.beginTransaction();
        maintrans.show(settingFragment);
        maintrans.commit();
    }

}