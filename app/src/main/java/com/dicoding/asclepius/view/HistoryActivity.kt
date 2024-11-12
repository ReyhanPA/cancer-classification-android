package com.dicoding.asclepius.view

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionBar = supportActionBar
        actionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.zomp)))

        historyAdapter = HistoryAdapter()
        binding.rvHistory.adapter = historyAdapter

        val layoutManager = LinearLayoutManager(this)
        binding.rvHistory.layoutManager = layoutManager

        val factory: ViewModelFactory = ViewModelFactory.getInstance(application)
        val viewModel: HistoryViewModel by viewModels {
            factory
        }

        viewModel.getAllHistory().observe(this) {
            historyAdapter.submitList(it)
        }

        supportActionBar?.title = getString(R.string.history_activity_title)
    }
}