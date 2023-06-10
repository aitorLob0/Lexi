package com.example.lexiapp.ui.games.letsread

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.lexiapp.R
import com.example.lexiapp.databinding.ActivityResultsLetsReadBinding
import com.example.lexiapp.domain.model.Patient
import com.example.lexiapp.ui.patienthome.PatientHomeFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResultActivity() : AppCompatActivity() {

    private lateinit var binding: ActivityResultsLetsReadBinding
    private val vM: DifferenceViewModel by viewModels()

    private lateinit var resultTextView: TextView
    private lateinit var homeBtn: Button
    private lateinit var originalText: String
    private lateinit var results: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityResultsLetsReadBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        resultTextView = binding.tvTitle
        homeBtn = binding.btnHome

        setButtonHome()

        getExtras()
        getResults(originalText, results)
        manageResult()



    }

    private fun setButtonHome() {
        homeBtn.setOnClickListener {
            goToFragment(PatientHomeFragment())
        }
    }

    private fun goToFragment(destinationFragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.flMainActivity, destinationFragment)
            .commit()
    }

    private fun getExtras() {
        originalText = intent.getStringExtra("originalText").toString()
        results = intent.getStringExtra("results").toString()
    }

    private fun getResults(originalText: String, results: String) {
        vM.getDifference(originalText = originalText, results = results)
    }

    private fun manageResult() {
        vM.difference.observe(this) {
            val result = vM.convertToText()
            if (result == "Correct")
                resultTextView.text = "¡Tu respuesta es correcta!"
            else resultTextView.text = HtmlCompat.fromHtml(result, HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
    }

}