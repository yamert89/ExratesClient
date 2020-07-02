package ru.exrates.mobile.view.prefs

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import ru.exrates.mobile.MyApp
import ru.exrates.mobile.R
import ru.exrates.mobile.view.ExratesActivity

class SettingsActivity : ExratesActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.settings,
                SettingsFragment(app)
            )
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

}
class SettingsFragment(private val app: MyApp) : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onDisplayPreferenceDialog(preference: Preference?) {
        val dialogFragment = NotificationPreferenceDialogFragment.newInstance(app, preference?.key ?: "1")
        dialogFragment.setTargetFragment(this, 0);
        dialogFragment.show(parentFragmentManager, "settings" )
    }
}