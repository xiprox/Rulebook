package tr.xip.scd.rulebook

import android.app.Application
import tr.xip.scd.rulebook.data.DataManager

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        DataManager.init(this)
    }
}