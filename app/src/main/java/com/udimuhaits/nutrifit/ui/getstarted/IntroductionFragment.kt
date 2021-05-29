package com.udimuhaits.nutrifit.ui.getstarted

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.udimuhaits.nutrifit.R
import com.udimuhaits.nutrifit.databinding.FragmentIntroductionBinding

class IntroductionFragment : Fragment() {

    private lateinit var binding: FragmentIntroductionBinding

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
        binding = FragmentIntroductionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imgHealthy.startAnimation(topAnimation)
        binding.tvLiveHealthy.startAnimation(topAnimation)
        binding.tvDescription.startAnimation(topAnimation)
        binding.imgRoundOne.startAnimation(bottomAnimation)
        binding.imgRoundTwo.startAnimation(bottomAnimation)
        binding.btnNext.startAnimation(bottomAnimation)

        binding.btnNext.setOnClickListener {
            val mFragmentManager = fragmentManager
            val mStartedFragment = StartedFragment()
            mFragmentManager?.beginTransaction()?.apply {
                replace(
                    R.id.frame_container,
                    mStartedFragment,
                    StartedFragment::class.java.simpleName
                )
                addToBackStack(null)
                commit()
            }
        }
    }
}