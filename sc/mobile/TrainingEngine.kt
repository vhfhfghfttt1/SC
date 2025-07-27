
package com.snooker.coach

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import android.speech.tts.TextToSpeech
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream
import java.util.Locale

class TrainingEngine(
    private val context: Context,
    private val textView: TextView,
    private val onComplete: () -> Unit
) {
    private lateinit var jsonSteps: JSONArray
    private var currentIndex = 0
    private val handler = Handler(Looper.getMainLooper())
    private var tts: TextToSpeech? = null

    init {
        tts = TextToSpeech(context) {
            if (it == TextToSpeech.SUCCESS) {
                tts?.language = Locale("he")
            }
        }
    }

    fun loadTrainingFromAssets(filename: String = "training_flow.json") {
        val inputStream: InputStream = context.assets.open(filename)
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val jsonObj = JSONObject(jsonString)
        jsonSteps = jsonObj.getJSONArray("שלבים")
    }

    fun start() {
        if (!::jsonSteps.isInitialized) return
        currentIndex = 0
        nextStep()
    }

    private fun nextStep() {
        if (currentIndex >= jsonSteps.length()) {
            speak("האימון הסתיים. כל הכבוד!")
            onComplete()
            return
        }

        val step = jsonSteps.getJSONObject(currentIndex)
        val instruction = step.getString("הנחיה")

        textView.text = instruction
        speak(instruction)

        // simulate sensor pass/fail (for now always pass after delay)
        handler.postDelayed({
            val feedback = step.getJSONObject("משוב").getString("בהצלחה")
            textView.text = feedback
            speak(feedback)
            currentIndex++
            handler.postDelayed({ nextStep() }, 2000)
        }, 3000)
    }

    fun stop() {
        handler.removeCallbacksAndMessages(null)
        tts?.shutdown()
    }

    private fun speak(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }
}
