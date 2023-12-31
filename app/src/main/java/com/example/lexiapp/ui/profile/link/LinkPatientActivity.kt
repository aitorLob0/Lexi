package com.example.lexiapp.ui.profile.link

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import com.example.lexiapp.databinding.ActivityLinkPatientBinding
import com.example.lexiapp.ui.profile.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LinkPatientActivity : AppCompatActivity() {
    lateinit var binding: ActivityLinkPatientBinding
    private val vM: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLinkPatientBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        blockActivityCapture()
        setQr()
        setObservers()
        vM.startTimer()
    }

    private fun blockActivityCapture() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
    }

    private fun setQr(){
        binding.ivQr.setImageBitmap(vM.getQR())
    }

    private fun setObservers(){
        vM.isLinked.observe(this){ isLink ->
            if(isLink != null && isLink){
                Toast.makeText(this, "Se vinculó con su profesional", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        vM.timeLeftInMillis.observe(this) { timeLeftInMillis ->
            binding.txtTimerCount.text = "${(timeLeftInMillis / 1000).toString()}s"
        }

        vM.isTimerRunning.observe(this) { isRunning ->
            if (!isRunning) {
                finish()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        vM.startTimer()
    }

    override fun onPause() {
        super.onPause()
        vM.cleared()
    }
}