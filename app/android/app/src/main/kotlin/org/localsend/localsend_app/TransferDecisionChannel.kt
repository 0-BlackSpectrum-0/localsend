package org.localsend.localsend_app

import io.flutter.plugin.common.MethodChannel
import io.flutter.embedding.engine.FlutterEngine

object TransferDecisionChannel {
    private var channel: MethodChannel? = null

    fun init (flutterEngine: FlutterEngine) {
        channel = MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            "org.localsend.localsend_app/transfer_decision"
        )
    }

    fun send(sessionId: String, accepted: Boolean) {
        channel?.invokeMethod(
            "onTransferDecision",
            mapOf("sessionId" to sessionId, "accepted" to accepted)
        )
    }
}