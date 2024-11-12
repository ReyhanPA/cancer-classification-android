package com.dicoding.asclepius.view

import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.local.entity.HistoryEntity
import com.dicoding.asclepius.databinding.ActivityResultBinding
import kotlinx.coroutines.launch

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionBar = supportActionBar
        actionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.zomp)))

        supportActionBar?.title = getString(R.string.result_activity_title)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(application)
        val viewModel: ResultViewModel by viewModels {
            factory
        }

        // TODO: Menampilkan hasil gambar, prediksi, dan confidence score.

        val resultText = intent.getStringExtra(getString(R.string.result_text_extra))
        val inferenceTimeText = intent.getStringExtra(getString(R.string.inference_time_text_extra))
        val imageUriData = intent.data

        binding.resultImage.setImageURI(imageUriData)
        binding.resultText.text = resultText

        binding.saveButton.setOnClickListener {
            val result = HistoryEntity().apply {
                imageUri = imageUriData
                result = resultText
                inferenceTime = inferenceTimeText
            }
            lifecycleScope.launch {
                viewModel.insertHistory(result)
            }
            showToast(getString(R.string.success_save_history))
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}