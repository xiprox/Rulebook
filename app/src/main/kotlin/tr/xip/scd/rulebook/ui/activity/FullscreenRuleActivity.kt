package tr.xip.scd.rulebook.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_fullscreen_rule.*
import tr.xip.scd.rulebook.R
import tr.xip.scd.rulebook.model.Rule
import tr.xip.scd.rulebook.util.getLanguage

class FullscreenRuleActivity : AppCompatActivity() {

    private val hideHandler = Handler()

    private val hidePart2Runnable = Runnable {
        root.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }

    private val showPart2Runnable = Runnable {
        val actionBar = supportActionBar
        actionBar?.show()
        root.visibility = View.VISIBLE
    }

    private var visibility: Boolean = false

    private val mHideRunnable = Runnable {
        hide()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen_rule)

        visibility = true

        root.setOnClickListener {
            toggle()
        }

        val rule = intent.extras.get(ARG_RULE) as Rule?

        if (rule == null) {
            finish()
        } else {
            ruleDetailsId.text = "#${rule.id}"

            root.setBackgroundColor(
                    if (rule.result.contains("+")) {
                        resources.getColor(R.color.charge_positive_dark, null)
                    } else {
                        resources.getColor(R.color.charge_negative_dark, null)
                    }
            )

            val lang = getLanguage(this)
            ruleView.text = if (lang == "ba" || lang == "bs" || lang == "hr" || lang == "sr") rule.bosnian else rule.english

            when (rule.result) {
                "404" -> result.text = "Expulsion"
                "500" -> result.text = "Discipline"
                else -> result.text = rule.result
            }

            if (getLanguage(this) == "en" && rule.note_english.trim().length != 0) {
                notes.text = rule.note_english
                notes.visibility = View.VISIBLE
            } else if (getLanguage(this) == "ba" && rule.note_bosnian.trim().length != 0) {
                notes.text = rule.note_bosnian
                notes.visibility = View.VISIBLE
            } else {
                notes.visibility = View.GONE
            }
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        delayedHide(100)
    }

    private fun toggle() {
        if (visibility) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        // Hide UI first
        visibility = false

        // Schedule a runnable to remove the status and navigation bar
        hideHandler.removeCallbacks(showPart2Runnable)
        hideHandler.postDelayed(hidePart2Runnable, 0)
    }

    @SuppressLint("InlinedApi")
    private fun show() {
        // Show the system bar
        root.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        visibility = true

        // Schedule a runnable to display UI elements
        hideHandler.removeCallbacks(hidePart2Runnable)
        hideHandler.postDelayed(showPart2Runnable, 0)
    }

    private fun delayedHide(delayMillis: Int) {
        hideHandler.removeCallbacks(mHideRunnable)
        hideHandler.postDelayed(mHideRunnable, delayMillis.toLong())
    }

    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [.AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private val AUTO_HIDE = true

        /**
         * If [.AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private val UI_ANIMATION_DELAY = 300

        val ARG_RULE = "arg_rule"
    }
}
