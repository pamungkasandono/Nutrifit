package com.udimuhaits.nutrifit.ui.getstarted

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.udimuhaits.nutrifit.R
import com.udimuhaits.nutrifit.databinding.FragmentStartedBinding
import com.udimuhaits.nutrifit.ui.login.LoginActivity

class StartedFragment : Fragment() {

    companion object {
        const val PREFS_ONBOARDING = "sharedPrefOnBoarding"
    }

    private lateinit var binding: FragmentStartedBinding
    private lateinit var sharedPreferences: SharedPreferences

    private val topAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(
            activity,
            R.anim.top_animation_onboarding
        )
    }

    private val bottomAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(
            activity,
            R.anim.bottom_animation_onboarding
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStartedBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imgStarted.startAnimation(topAnimation)
        binding.tvLetsStart.startAnimation(topAnimation)
        binding.tvDescription.startAnimation(topAnimation)
        binding.imgRoundOne.startAnimation(bottomAnimation)
        binding.imgRoundTwo.startAnimation(bottomAnimation)
        binding.btnStarted.startAnimation(bottomAnimation)

        sharedPreferences = activity?.getSharedPreferences(PREFS_ONBOARDING, Context.MODE_PRIVATE)!!

        binding.btnStarted.setOnClickListener {
            sharedPreferences.edit().apply {
                putBoolean("isStarted", true)
                apply()
            }
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }
}