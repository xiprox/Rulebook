package tr.xip.scd.rulebook.ui.activity

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.widget.NestedScrollView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import nl.komponents.kovenant.CancelablePromise
import nl.komponents.kovenant.android.startKovenant
import nl.komponents.kovenant.android.stopKovenant
import nl.komponents.kovenant.task
import nl.komponents.kovenant.ui.successUi
import tr.xip.scd.rulebook.R
import tr.xip.scd.rulebook.data.DataManager
import tr.xip.scd.rulebook.ext.toDp
import tr.xip.scd.rulebook.model.Rule
import tr.xip.scd.rulebook.ui.adapter.RulesAdapter
import java.util.*

class MainActivity : AppCompatActivity() {
    private var bottomSheetBehavior: BottomSheetBehavior<NestedScrollView>? = null

    private var displayedRule: Rule? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startKovenant()

        val rules = DataManager.getData(this)

        bottomSheetBehavior = BottomSheetBehavior.from(ruleDetails)
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN

        more.setOnClickListener {
            val menu = PopupMenu(this, more)
            menu.inflate(R.menu.main)
            menu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_change_lang -> {
                        // Collapse BottomSheet
                        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN

                        // Save preferences
                        if (DataManager.getLang() == DataManager.LANG_EN) {
                            DataManager.saveLang(DataManager.LANG_BS)
                        } else {
                            DataManager.saveLang(DataManager.LANG_EN)
                        }

                        // Reload content
                        recycler.adapter.notifyDataSetChanged()
                    }
                    R.id.action_about -> {
                        startActivity(Intent(this@MainActivity, AboutActivity::class.java))
                    }
                }
                true
            }
            menu.show()
        }

        val recyclerClickListener = object : RulesAdapter.OnItemClickListener {
            override fun onClick(rule: Rule) {
                loadDataIntoRuleDetails(rule)
                bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = RulesAdapter(recycler, rules, recyclerClickListener)
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

        var searchPromise = task {}

        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                val foundRules = ArrayList<Rule>()

                progress.visibility = View.VISIBLE

                (searchPromise as CancelablePromise).cancel(Exception("trash"))
                searchPromise = task {
                    for (rule in rules) {
                        if (rule.bosnian.contains(s.toString()) || rule.english.contains(s.toString())) {
                            foundRules.add(rule)
                        }
                    }
                } successUi {
                    recycler.swapAdapter(RulesAdapter(recycler, foundRules, recyclerClickListener), false)
                    progress.visibility = View.GONE
                }
            }
        })
    }

    override fun onStop() {
        super.onStop()
        stopKovenant()
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
        val lang = DataManager.getLang()

        ruleView.text = if (lang == DataManager.LANG_EN) rule.english else rule.bosnian

        when (rule.result) {
            "404" -> result.text = "Expulsion"
            "500" -> result.text = "Discipline"
            else -> result.text = rule.result
        }

        if (lang == DataManager.LANG_EN && rule.note_english.trim().length != 0) {
            notes.text = rule.note_english
            notes.visibility = View.VISIBLE
        } else if (lang == DataManager.LANG_BS && rule.note_bosnian.trim().length != 0) {
            notes.text = rule.note_bosnian
            notes.visibility = View.VISIBLE
        } else {
            notes.visibility = View.GONE
        }
    }
}
