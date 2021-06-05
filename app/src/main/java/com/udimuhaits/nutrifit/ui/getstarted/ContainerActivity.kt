package com.udimuhaits.nutrifit.ui.getstarted

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.udimuhaits.nutrifit.R

class ContainerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)

        val mFragmentManager = supportFragmentManager
        val mIntroductionFragment = IntroductionFragment()
        val introductionFragment =
            mFragmentManager.findFragmentByTag(IntroductionFragment::class.java.simpleName)

        if (introductionFragment !is IntroductionFragment) {
            mFragmentManager
                .beginTransaction()
                .add(
                    R.id.frame_container,
                    mIntroductionFragment,
                    IntroductionFragment::class.java.simpleName
                )
                .commit()
        }
    }
}