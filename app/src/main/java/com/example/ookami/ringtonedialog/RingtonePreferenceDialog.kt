package com.example.ookami.ringtonedialog

import android.app.Dialog
import android.content.ContentResolver
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceManager
import android.widget.SimpleAdapter

class RingtonePreferenceDialog : DialogFragment() {
    private var ringtones: List<Ringtone> = emptyList()
    private var currentUri: Uri? = null
    private var playingRingtone: android.media.Ringtone? = null

    private val prefKey: String
        get() = arguments?.getString(ARG_PREF_KEY) ?: throw IllegalArgumentException("ARG_PREF_KEY not set")

    private val adapter by lazy {
        SimpleAdapter(
                context,
                ringtones.map { mapOf("title" to it.title) },
                android.R.layout.simple_list_item_single_choice,
                arrayOf("title"),
                intArrayOf(android.R.id.text1)
        )
    }

    private data class Ringtone(val title: String, val uri: Uri?)

    private fun getAndroidRingtones(): List<Ringtone> {
        val ringtoneManager = RingtoneManager(context)
        val cursor = ringtoneManager.cursor
        return (0 until cursor.count).map {
            cursor.moveToPosition(it)
            Ringtone(
                    title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX),
                    uri = ringtoneManager.getRingtoneUri(it)
            )
        }
    }

    private fun getResourceRingtones(): List<Ringtone> = listOf(
            Ringtone(
                    title = "Sample ringtone",
                    uri = Uri.parse("${ContentResolver.SCHEME_ANDROID_RESOURCE}://${context!!.packageName}/raw/sample")
            )
    )

    private fun getCurrentRingtoneUri(): Uri? = PreferenceManager
            .getDefaultSharedPreferences(context)
            .getString(prefKey, null)
            ?.let { Uri.parse(it) }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        ringtones = getResourceRingtones() + getAndroidRingtones()
        currentUri = getCurrentRingtoneUri()
        val currentPosition = ringtones.indexOfFirst { currentUri == it.uri }
        return AlertDialog.Builder(context!!)
                .setPositiveButton(android.R.string.ok) { _, _ -> saveCurrentUri() }
                .setNegativeButton(android.R.string.cancel) { _, _ -> dialog.dismiss() }
                .setSingleChoiceItems(adapter, currentPosition) { _, which ->
                    playingRingtone?.stop()
                    ringtones[which].also {
                        currentUri = it.uri
                        playingRingtone = it.uri?.let { RingtoneManager.getRingtone(context, it) }
                        playingRingtone?.play()
                    }
                }
                .create()
    }

    override fun onPause() {
        super.onPause()
        playingRingtone?.stop()
    }

    private fun saveCurrentUri() {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(prefKey, currentUri?.toString())
                .apply()
    }

    companion object {
        private const val ARG_PREF_KEY = "ARG_PREF_KEY"

        fun create(prefKey: String): RingtonePreferenceDialog {
            val fragment = RingtonePreferenceDialog()
            fragment.arguments = Bundle().apply {
                putString(ARG_PREF_KEY, prefKey)
            }
            return fragment
        }


    }
}

fun Preference.connectRingtoneDialog(fragmentManager: FragmentManager?) = setOnPreferenceClickListener {
    RingtonePreferenceDialog.create(key).apply {
        fragmentManager?.let { show(it, "SOUND") }
    }
    true
}
