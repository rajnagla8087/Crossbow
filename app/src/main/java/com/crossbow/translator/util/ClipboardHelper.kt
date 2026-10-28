package com.crossbow.translator.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast

class ClipboardHelper(private val context: Context) {

    private val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager

    fun copyToClipboard(label: String, text: String, showToast: Boolean = true) {
        if (text.isBlank()) return
        val clip = ClipData.newPlainText(label, text)
        clipboard?.setPrimaryClip(clip)
        if (showToast) {
            Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
        }
    }

    fun getClipboardText(): String? {
        val clip = clipboard?.primaryClip ?: return null
        if (clip.itemCount > 0) {
            val item = clip.getItemAt(0)
            return item.text?.toString()
        }
        return null
    }
}
