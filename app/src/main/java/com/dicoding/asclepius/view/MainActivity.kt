package com.dicoding.asclepius.view

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.io.File
import com.yalantis.ucrop.UCrop
import java.text.NumberFormat

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var imageClassifierHelper: ImageClassifierHelper

    private var currentImageUri: Uri? = null
    private var resultText: String? = null
    private var inferenceTimeText: String? = null

    private val uCropResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val resultUri = UCrop.getOutput(result.data!!)
            if (resultUri != null) {
                currentImageUri = resultUri
                showImage()
            }
        } else if (result.resultCode == UCrop.RESULT_ERROR) {
            val error = UCrop.getError(result.data!!)
            showToast(error?.message ?: getString(R.string.cropping_failed))
        }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { imageUri: Uri? ->
        if (imageUri != null) {
            val timeStamp = SystemClock.uptimeMillis()
            val destinationUri = Uri.fromFile(File(cacheDir, "image_crop-$timeStamp"))

            val uCropIntent = UCrop.of(imageUri, destinationUri)
                .withAspectRatio(1f, 1f)
                .getIntent(this)

            uCropResultLauncher.launch(uCropIntent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionBar = supportActionBar
        actionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.zomp)))

        binding.apply {
            galleryButton.setOnClickListener { startGallery() }
            analyzeButton.setOnClickListener {
                currentImageUri?.let { analyzeImage(it) } ?: showToast(getString(R.string.image_not_selected_warning))
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_history -> {
                val intent = Intent(this@MainActivity, HistoryActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_article -> {
                val intent = Intent(this@MainActivity, ArticleActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun startGallery() {
        // TODO: Mendapatkan gambar dari Gallery.
        launcherGallery.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    private fun showImage() {
        // TODO: Menampilkan gambar sesuai Gallery yang dipilih.
        binding.previewImageView.setImageURI(currentImageUri)
    }

    private fun analyzeImage(imageUri: Uri) {
        // TODO: Menganalisa gambar yang berhasil ditampilkan.
        imageClassifierHelper = ImageClassifierHelper(
            context = this,
            classifierListener = object : ImageClassifierHelper.ClassifierListener {
                override fun onError(error: String) {
                    runOnUiThread {
                        showToast(error)
                    }
                }

                override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                    runOnUiThread {
                        results?.let { it ->
                            if (it.isNotEmpty() && it[0].categories.isNotEmpty()) {
                                println(it)
                                val sortedCategories =
                                    it[0].categories.sortedByDescending { it?.score }
                                val displayResult =
                                    sortedCategories[0].label + " " + NumberFormat.getPercentInstance().format(sortedCategories[0].score).trim()
                                resultText = displayResult
                                inferenceTimeText = "$inferenceTime ms"
                                showToast(getString(R.string.image_analyzed_successfully))
                            } else {
                                resultText = ""
                                inferenceTimeText = ""
                                showToast(getString(R.string.image_analyzed_failed))
                            }
                        }
                    }
                }
            }
        )
        currentImageUri?.let { imageClassifierHelper.classifyStaticImage(imageUri) }
        moveToResult()
    }

    private fun moveToResult() {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra(getString(R.string.result_text_extra), resultText)
        intent.putExtra(getString(R.string.inference_time_text_extra), inferenceTimeText)
        intent.setData(currentImageUri)
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}