package com.dicoding.asclepius.view

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.BuildConfig
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.remote.response.ArticleResponse
import com.dicoding.asclepius.data.remote.retrofit.ApiConfig
import com.dicoding.asclepius.databinding.ActivityArticleBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ArticleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityArticleBinding
    private lateinit var articleAdapter: ArticleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionBar = supportActionBar
        actionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.zomp)))

        articleAdapter = ArticleAdapter()
        binding.rvArticle.adapter = articleAdapter

        val layoutManager = LinearLayoutManager(this)
        binding.rvArticle.layoutManager = layoutManager

        loadArticles()

        supportActionBar?.title = getString(R.string.article_activity_title)
    }

    private fun loadArticles() {
        showLoading(true)
        val client = ApiConfig.getApiService().getArticles()
        client.enqueue(object : Callback<ArticleResponse> {
            override fun onResponse(
                call: Call<ArticleResponse>,
                response: Response<ArticleResponse>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        articleAdapter.submitList(responseBody.articles)
                    }
                }
                else {
                    if (BuildConfig.DEBUG) Log.d(TAG, "onFailResponse: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<ArticleResponse>, t: Throwable) {
                if (BuildConfig.DEBUG) Log.d(TAG, "onFailResponse: ${t.message}")
                showLoading(false)
            }
        })
    }

    private fun showLoading(state: Boolean) {
        binding.progressBar.visibility = if (state) View.VISIBLE else View.GONE
    }

    companion object {
        private const val TAG = "ArticleActivity"
    }
}