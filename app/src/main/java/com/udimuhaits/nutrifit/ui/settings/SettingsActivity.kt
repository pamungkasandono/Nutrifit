package com.udimuhaits.nutrifit.ui.settings

import android.content.Context
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
import com.udimuhaits.nutrifit.ui.login.LoginActivity.Companion.PREFS_LOGIN

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
            builder.setTitle(R.string.dialog_title_logout)
            builder.setMessage(R.string.logout_message)
            builder.setIcon(R.drawable.ic_logout)
            builder.setPositiveButton("Yes") { dialogInterface, which ->
                gsc.signOut().addOnCompleteListener(OnCompleteListener { task ->
                    if (task.isSuccessful) {
                        sharedPreferences =
                            requireActivity().getSharedPreferences(
                                PREFS_LOGIN,
                                Context.MODE_PRIVATE
                            )
                        sharedPreferences.edit().apply {
                            putBoolean("isLogout", true)
                            fAuth.signOut()
                            val intent = Intent(activity, LoginActivity::class.java)
                            startActivity(intent)
                            Toast.makeText(activity, "Successfully logout", Toast.LENGTH_SHORT)
                                .show()
                            activity?.finish()
                            apply()
                        }
                    }
                })
            }
            builder.setNegativeButton("No") { dialogInterface, which ->
                Toast.makeText(activity, "Logout canceled", Toast.LENGTH_SHORT).show()
            }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()
        }
    }
}