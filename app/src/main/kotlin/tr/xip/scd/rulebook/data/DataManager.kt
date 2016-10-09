package tr.xip.scd.rulebook.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.apache.commons.io.IOUtils
import tr.xip.scd.rulebook.model.Rule
import java.util.*

object DataManager {
    private val PREF_LANG = "lang"

    val LANG_EN = "en"
    val LANG_BS = "bs"

    private var sharedPref: SharedPreferences? = null

    fun init(context: Context) {
        sharedPref = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
    }

    fun getData(context: Context): List<Rule> {
        return Gson().fromJson(
                IOUtils.toString(context.assets.open("data/rules.json"), "UTF-8"),
                object : TypeToken<ArrayList<Rule>>() {}.type
        )
    }

    fun getLang(): String {
        return sharedPref?.getString(PREF_LANG, LANG_EN) ?: LANG_EN
    }

    fun saveLang(lang: String) {
        sharedPref?.edit()?.putString(PREF_LANG, lang)?.commit()
    }
}