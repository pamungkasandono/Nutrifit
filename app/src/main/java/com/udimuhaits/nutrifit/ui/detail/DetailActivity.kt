package com.udimuhaits.nutrifit.ui.detail

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.udimuhaits.nutrifit.databinding.ActivityDetailBinding


class DetailActivity : AppCompatActivity() {
    private lateinit var detailBinding: ActivityDetailBinding

    companion object {
        const val QUERY = "QUERY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailBinding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(detailBinding.root)

        val detailAdapter = DetailAdapter()

        val viewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[DetailViewModel::class.java]

//        detailBinding.btnSearch.setOnClickListener {
        val query = intent.extras?.getString(QUERY)
//            // data dari viewModel di kirim ke adapter
//        }
//
        if (query != null) {
            Log.d("asdasd", query)
            viewModel.getListFood(query).observe(this) {
                detailAdapter.setData(it)
                detailAdapter.notifyDataSetChanged()
            }
        }
//        viewModel.modelResponseCN.observe(this, {
//            detailAdapter.setData(it)
//            detailAdapter.notifyDataSetChanged()
//        })


        with(detailBinding.rvNutrition) {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = detailAdapter
        }

    }
}