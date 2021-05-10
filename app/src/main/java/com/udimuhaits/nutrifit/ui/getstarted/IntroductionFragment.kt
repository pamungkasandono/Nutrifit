package com.udimuhaits.nutrifit.ui.getstarted

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.udimuhaits.nutrifit.R
import com.udimuhaits.nutrifit.databinding.FragmentIntroductionBinding

class IntroductionFragment : Fragment() {

    private lateinit var binding: FragmentIntroductionBinding

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
        binding.btnNext.setOnClickListener {
            val mFragmentManager = fragmentManager
            val mStartedFragment = StartedFragment()
            mFragmentManager?.beginTransaction()?.apply {
                replace(R.id.frame_container, mStartedFragment, StartedFragment::class.java.simpleName)
                addToBackStack(null)
                commit()
            }
        }
    }
}