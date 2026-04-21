package com.androidircx.nulvex.quickcapture

import android.app.ActivityManager
import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.androidircx.nulvex.R
import com.androidircx.nulvex.VaultServiceLocator
import com.androidircx.nulvex.security.executePanicWipeAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PanicWipeConfirmActivity : AppCompatActivity() {
    private var confirmed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState?.getBoolean(KEY_CONFIRMED, false) == true) {
            confirmed = true
            return
        }
        showConfirmDialog()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_CONFIRMED, confirmed)
    }

    private fun showConfirmDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.widget_panic_wipe_confirm_title)
            .setMessage(R.string.widget_panic_wipe_confirm_message)
            .setCancelable(true)
            .setNegativeButton(R.string.widget_panic_wipe_confirm_cancel) { _, _ ->
                finish()
            }
            .setPositiveButton(R.string.widget_panic_wipe_confirm_erase) { _, _ ->
                confirmed = true
                runPanicWipe()
            }
            .setOnCancelListener { finish() }
            .show()
    }

    private fun runPanicWipe() {
        lifecycleScope.launch(Dispatchers.IO) {
            val success = runCatching {
                executePanicWipeAll(
                    panicWipeService = VaultServiceLocator.panicWipeService(),
                    appPreferences = VaultServiceLocator.appPreferences(),
                    reminderScheduler = VaultServiceLocator.noteReminderScheduler(),
                    securityEventStore = VaultServiceLocator.securityEventStore()
                )
            }.isSuccess
            withContext(Dispatchers.Main) {
                if (!success) {
                    Toast.makeText(
                        this@PanicWipeConfirmActivity,
                        getString(R.string.widget_panic_wipe_failed),
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                    return@withContext
                }
                disableWidgetProvidersForReset()
                requestFreshStartReset()
            }
        }
    }

    private fun disableWidgetProvidersForReset() {
        val components = listOf(
            ComponentName(this, NulvexPanicWipeWidgetProvider::class.java),
            ComponentName(this, NulvexQuickCaptureWidgetProvider::class.java)
        )
        components.forEach { component ->
            runCatching {
                packageManager.setComponentEnabledSetting(
                    component,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP
                )
            }
        }
    }

    private fun requestFreshStartReset() {
        val manager = getSystemService(ActivityManager::class.java)
        val requested = manager?.clearApplicationUserData() == true
        if (!requested) {
            Toast.makeText(this, getString(R.string.widget_panic_wipe_failed), Toast.LENGTH_LONG).show()
            finish()
        }
    }

    companion object {
        private const val KEY_CONFIRMED = "confirmed"
    }
}
