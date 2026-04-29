package org.localsend.localsend_app

import io.flutter.plugin.common.MethodChannel

object TransferDecisionChannel {
    private var channel: MethodChannel? = null


    fun setChannel(channel: MethodChannel){
        this.channel = channel
    }

    fun send(sessionId: String, accepted: Boolean) {
        channel?.invokeMethod(
            "onTransferDecision",
            mapOf("sessionId" to sessionId, "accepted" to accepted)
        )
    }
}