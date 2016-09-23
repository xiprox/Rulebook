package tr.xip.scd.rulebook.ui.activity

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.widget.NestedScrollView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import tr.xip.scd.rulebook.R
import tr.xip.scd.rulebook.data.DataManager
import tr.xip.scd.rulebook.ext.toDp
import tr.xip.scd.rulebook.model.Rule
import tr.xip.scd.rulebook.ui.adapter.RulesAdapter
import tr.xip.scd.rulebook.util.getLanguage

class MainActivity : AppCompatActivity() {
    private var bottomSheetBehavior: BottomSheetBehavior<NestedScrollView>? = null

    private var displayedRule: Rule? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomSheetBehavior = BottomSheetBehavior.from(ruleDetails)
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = RulesAdapter(recycler, DataManager.getData(this), object : RulesAdapter.OnItemClickListener {
            override fun onClick(rule: Rule) {
                loadDataIntoRuleDetails(rule)
                bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
            }
        })

        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            var offset = 0
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                offset += dy

                if (offset > 0) {
                    divider.visibility = View.GONE
                    shadow.visibility = View.VISIBLE
                } else {
                    divider.visibility = View.VISIBLE
                    shadow.visibility = View.GONE
                }

                val percentage = offset.toDp(this@MainActivity) / 30f
                val alpha = Math.min(percentage, 1f)
                shadow.alpha = alpha
            }
        })

        fullscreen.setOnClickListener {
            if (displayedRule != null) {
                val intent = Intent(this, FullscreenRuleActivity::class.java)
                intent.putExtra(FullscreenRuleActivity.ARG_RULE, displayedRule)
                startActivity(intent)
            }
        }
    }

    override fun onBackPressed() {
        if (bottomSheetBehavior?.state != BottomSheetBehavior.STATE_HIDDEN) {
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
        } else {
            super.onBackPressed()
        }
    }

    private fun loadDataIntoRuleDetails(rule: Rule) {
        displayedRule = rule

        ruleDetailsId.text = "#${rule.id}"

        header.setBackgroundColor(
                if (rule.result.contains("+")) {
                    resources.getColor(R.color.charge_positive, null)
                } else {
                    resources.getColor(R.color.charge_negative, null)
                }
        )

        ruleView.setBackgroundColor(
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
