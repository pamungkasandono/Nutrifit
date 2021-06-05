package com.udimuhaits.nutrifit.ui.settings

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.udimuhaits.nutrifit.R
import com.udimuhaits.nutrifit.ui.form.FormUpdateActivity
import com.udimuhaits.nutrifit.ui.login.LoginActivity

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        private lateinit var fAuth: FirebaseAuth
        private lateinit var gsc: GoogleSignInClient
        private lateinit var sharedPreferences: SharedPreferences

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            fAuth = FirebaseAuth.getInstance()
            gsc = GoogleSignIn.getClient(requireContext(), GoogleSignInOptions.DEFAULT_SIGN_IN)

            val updateProfile = findPreference<Preference>("update_profile")
            updateProfile?.setOnPreferenceClickListener {
                val intent = Intent(activity, FormUpdateActivity::class.java)
                startActivity(intent)
                true
            }

            val changeLanguage = findPreference<Preference>("change_language")
            changeLanguage?.setOnPreferenceClickListener {
                val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(intent)
                true
            }

            val logOut = findPreference<Preference>("logout")
            logOut?.setOnPreferenceClickListener {
                showAlertDialogLogout()
                true
            }
        }

        private fun showAlertDialogLogout() {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(R.string.logout_title)
            builder.setMessage(R.string.logout_message)
            builder.setIcon(R.drawable.ic_logout)
            builder.setPositiveButton(R.string.yes) { dialogInterface, which ->
                gsc.signOut().addOnCompleteListener(OnCompleteListener { task ->
                    if (task.isSuccessful) {
                        sharedPreferences =
                            requireActivity().getSharedPreferences(
                                LoginActivity.PREFS_STARTED,
                                MODE_PRIVATE
                            )
                        sharedPreferences.edit().apply {
                            putBoolean("isLogin", false)
                            putBoolean("isSave", false)
                            fAuth.signOut()
                            val intent = Intent(
                                activity,
                                LoginActivity::class.java
                            ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(intent)
                            activity?.finish()
                            Toast.makeText(
                                activity,
                                getString(R.string.success_logout),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            apply()
                        }
                    }
                })
            }
            builder.setNegativeButton(R.string.no) { dialogInterface, which ->
                Toast.makeText(activity, getString(R.string.cancel_logout), Toast.LENGTH_SHORT)
                    .show()
            }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()
        }
    }
}