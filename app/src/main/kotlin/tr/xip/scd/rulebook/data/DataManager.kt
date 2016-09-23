package tr.xip.scd.rulebook.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.apache.commons.io.IOUtils
import tr.xip.scd.rulebook.model.Rule
import java.util.*

object DataManager {

    fun getData(context: Context): List<Rule> {
        return Gson().fromJson(
                IOUtils.toString(context.assets.open("data/rules.json"), "UTF-8"),
                object : TypeToken<ArrayList<Rule>>() {}.type
        )
    }
}