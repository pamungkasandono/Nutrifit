package com.udimuhaits.nutrifit.ui.getstarted

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.udimuhaits.nutrifit.databinding.FragmentStartedBinding
import com.udimuhaits.nutrifit.ui.login.LoginActivity

class StartedFragment : Fragment() {

    private lateinit var binding: FragmentStartedBinding
    private lateinit var sharedPreferences: SharedPreferences

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

        sharedPreferences = activity?.getSharedPreferences("sharedPrefStarted", Context.MODE_PRIVATE)!!

        binding.btnStarted.setOnClickListener {
                sharedPreferences.edit().apply {
                    putBoolean("alreadyGettingStarted", true)
                    apply()
            }
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}