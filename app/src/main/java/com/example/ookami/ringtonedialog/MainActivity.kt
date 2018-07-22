package com.example.ookami.ringtonedialog

import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceManager
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.playRingtone).setOnClickListener {
            val ringtone = PreferenceManager.getDefaultSharedPreferences(this)
                    .getString("ringtone", null)
                    ?.let { RingtoneManager.getRingtone(this, Uri.parse(it)) }
            if (ringtone == null) {
                Toast.makeText(this, "Select ringtone in settings", Toast.LENGTH_SHORT).show()
            } else {
                ringtone.play()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        R.id.settings -> {
            startSettingsActivity()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun startSettingsActivity() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }
}
