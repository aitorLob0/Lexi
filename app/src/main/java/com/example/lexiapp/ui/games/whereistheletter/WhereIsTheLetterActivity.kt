package com.example.lexiapp.ui.games.whereistheletter

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.lexiapp.R
import com.example.lexiapp.databinding.ActivityWhereIsTheLetterBinding
import com.example.lexiapp.ui.games.whereistheletter.result.NegativeResultWhereIsTheLetterActivity
import com.example.lexiapp.ui.games.whereistheletter.result.PositiveResultWhereIsTheLetterActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class WhereIsTheLetterActivity : AppCompatActivity() {

    private val vM: WhereIsTheLetterViewModel by viewModels()

    //private lateinit var observerWord:
    private lateinit var binding: ActivityWhereIsTheLetterBinding

    private lateinit var textToSpeech: TextToSpeech

    private var positions = mutableMapOf<Int, Button>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWhereIsTheLetterBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        vM.onOmitWord()
        setTextToSpeech()
        setWordObserver()
        progressBarOn()
        setListeners()
    }

    private fun setTextToSpeech(){
        val language = Locale("es", "US")

        textToSpeech = TextToSpeech(this) {
            if (it != TextToSpeech.ERROR) {
                textToSpeech.language = language
                textToSpeech.setSpeechRate(0.6f)
            }
        }
    }

    private fun setWordObserver() {
        vM.basicWord.observe(this) {
            if (it.isNullOrEmpty()) {
                progressBarOn()
            } else {
                if (vM.visibleWord.value != it){
                    setValues()
                }
                progressBarOff()
                binding.btnOtherWord.isClickable = true
            }
        }
        vM.letter.observe(this) {
            binding.txtVariableLetter.text = it.uppercase()
        }
    }

    private fun progressBarOn() {
        lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE
            binding.txtWord.visibility = View.GONE
            binding.txtVariableWord.visibility = View.GONE
            binding.iconVolume.visibility = View.GONE
            binding.txtFindLetter.visibility = View.GONE
        }
    }

    private fun progressBarOff() {
        lifecycleScope.launch {
            binding.progressBar.visibility = View.GONE
            binding.txtWord.visibility = View.VISIBLE
            binding.txtVariableWord.visibility = View.VISIBLE
            binding.iconVolume.visibility = View.VISIBLE
            binding.txtFindLetter.visibility = View.VISIBLE
        }
    }

    private fun listenerOtherWord() {
        binding.btnOtherWord.setOnClickListener {
            vM.setVisibleWord("")
            if (!vM.basicWord.hasObservers()) setWordObserver()
            binding.btnOtherWord.isClickable = false
            vM.onPositionDeselected()
            progressBarOn()
            deleteBtnLetter()
            vM.onOmitWord()
        }
    }

    private fun setListeners() {
        listenerResult()
        listenerOtherWord()
        btnBack()
        btnReadWord()
        btnHelp()
    }

    private fun listenerResult() {
        binding.btnResult.setOnClickListener {
            if (!vM.basicWord.hasObservers()) {
                Toast.makeText(this, "Debe clickear el boton 'Otra palabra' ", Toast.LENGTH_SHORT)
                    .show()
            } else {
                isAnyLetterSelected()
            }
        }
    }

    private fun isAnyLetterSelected() {
        if (vM.isAnyLetterSelected()) {
            vM.onSubmitAnswer()
            validateLetterSelected()
        } else {
            Toast.makeText(this, "Debe elegir una letra", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateLetterSelected() {
        if (vM.correctAnswerSubmitted.value) {
            startActivity(Intent(this, PositiveResultWhereIsTheLetterActivity::class.java))
        } else {
            val intent = Intent(this, NegativeResultWhereIsTheLetterActivity::class.java)
            intent.putExtra("word", vM.getWord())
            intent.putExtra("answerPosition", vM.getSelectedPosition())
            intent.putExtra("correctPosition", vM.getCorrectPosition())
            startActivity(intent)
        }
        finish()
    }

    private fun deleteBtnLetter() {
        if (positions.isNotEmpty()) {
            binding.containerWord.removeAllViews()
        }
        positions.clear()
    }

    private fun resetAllBtns() {
        positions.keys.forEach {
            setStyle(it, SetStyleButton.NOT_SELECTED)
        }
    }

    private fun saveBtnPosition(btn: Button, pos: Int) {
        positions[pos] = btn
    }

    private fun onLetterSelected(pos: Int) {
        resetAllBtns()
        Log.v("select_position_in_activity_before", "position: $pos")
        if (vM.isItSelected(pos)) {
            vM.onPositionDeselected()
            Log.v(
                "select_position_in_activity_after_true",
                "position: ${vM.selectedPosition.value}"
            )
            return
        }
        vM.onPositionSelected(pos)
        Log.v("select_position_in_activity_after_false", "position: ${vM.selectedPosition.value}")
        setStyle(pos, SetStyleButton.SELECTED)
    }

    private fun setValues() {
        val word = vM.basicWord.value!!
        vM.setVisibleWord(word)
        Log.v("init_word_in_activity", "response word: $word")
        Log.v("init_word_in_activity", "response word: ${vM.basicWord.value!!}")
        binding.txtVariableWord.text = word
        for (letter in word.withIndex()) {
            Log.v(
                "position_btn_in_activity",
                "position: ${letter.index} ${letter.value.uppercase()}"
            )
            createWordButton(
                letter.value.uppercase(),
                { this.onLetterSelected(letter.index) }) { btn ->
                saveBtnPosition(
                    pos = letter.index,
                    btn = btn
                )
            }
        }
        positions.keys.forEach { Log.v("position_in_activity", "position: $it ${positions[it]}") }
    }

    private fun createWordButton(
        letter: String,
        listener: () -> Unit,
        saveBtn: (Button) -> Unit
    ) {
        val btnLetter = Button(this)
        saveBtn(btnLetter)
        btnLetter.text = letter
        btnLetter.textSize = 38F
        val params = LinearLayout.LayoutParams(
            100, 120
        )
        params.setMargins(8, 8, 8, 8)
        params.gravity = Gravity.CENTER
        btnLetter.layoutParams = params
        btnLetter.typeface = Typeface.DEFAULT_BOLD
        btnLetter.background =
            ContextCompat.getDrawable(applicationContext, R.drawable.btn_letter_not_select)
        btnLetter.setOnClickListener {
            listener()
        }
        binding.containerWord.addView(btnLetter)
    }

    private fun setStyle(pos: Int, type: SetStyleButton) {
        when (type) {
            SetStyleButton.NOT_SELECTED -> {
                positions[pos]!!.background =
                    ContextCompat.getDrawable(applicationContext, R.drawable.btn_letter_not_select)
                positions[pos]!!.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.black
                    )
                )
            }
            SetStyleButton.SELECTED -> {
                positions[pos]!!.background =
                    ContextCompat.getDrawable(applicationContext, R.drawable.btn_letter_select)
                positions[pos]!!.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.white
                    )
                )
            }
        }
    }

    private fun btnBack() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun btnReadWord() {
        binding.iconVolume.setOnClickListener {

            if (this.textToSpeech.isSpeaking) {
                this.textToSpeech.stop()
            }

            if (binding.txtVariableWord.text.isNotEmpty()) {
                this.textToSpeech
                    .speak(
                        binding.txtVariableWord.text.toString(),
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        null
                    )
            }
        }

    }

    private fun btnHelp() {
        binding.btnHelp.setOnClickListener {
            setAlertBuilderToGoToYoutube()
        }
    }

    private fun setAlertBuilderToGoToYoutube() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Tutorial de Palabra Correcta")
            .setMessage("¿Desea salir de LEXI e ir a YouTube?")
            .setPositiveButton("SI"){dialog, _ ->
                try{
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(TUTORIAL_LINK)
                    intent.setPackage("com.google.android.youtube")
                    startActivity(intent)
                }catch (e:Exception){
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(TUTORIAL_LINK)))
                }

            }
            .setNegativeButton("NO") { dialog, _ ->
                dialog.dismiss()
            }
            .show()

    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeech.shutdown()
    }

    companion object{
        private const val TUTORIAL_LINK = "https://www.youtube.com/shorts/rA4dQl9czl0"
    }
}

enum class SetStyleButton {
    SELECTED,
    NOT_SELECTED
}