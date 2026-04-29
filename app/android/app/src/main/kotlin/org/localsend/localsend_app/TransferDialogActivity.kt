package org.localsend.localsend_app

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle

class TransferDialogActivity: Activity() {
    companion object {
        fun launch(context: Context, sessionId: String, senderName: String, fileCount: Int, totalSize: String){
            val intent = Intent(context, TransferDialogActivity::class.java).apply{
                putExtra("sessionId", sessionId)
                putExtra("senderName", senderName)
                putExtra("fileCount", fileCount)
                putExtra("totalSize", totalSize)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK

            }
            context.startActivity(intent)
        }
    }
    override fun onCreate (savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionId = intent.getStringExtra("sessionId") ?: return finish()
        val senderName = intent.getStringExtra("senderName") ?: "Unknown"
        val fileCount = intent.getIntExtra("fileCount", 0)
        val totalSize = intent.getStringExtra("totalSize") ?: ""

        AlertDialog.Builder(this)
            .setTitle("Incoming Transfer")
            .setMessage("$senderName wants to send you $fileCount file(s) \nSize: $totalSize")
            .setCancelable(false)
            .setPositiveButton("Accept") {_, _->
                TransferDecisionChannel.send(sessionId, true)
                finish()
            }
            .setNegativeButton("Decline") {_, _->
                TransferDecisionChannel.send(sessionId, false)
                finish()
            }
            .show()
    }
}
