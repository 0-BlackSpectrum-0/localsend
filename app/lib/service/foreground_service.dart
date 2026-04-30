import 'package:flutter/services.dart';

class ForegroundService {
  static const _channel = MethodChannel('org.localsend.localsend_app/localsend');

  static const _decisionChannel = MethodChannel('org.localsend.localsend_app/transfer_decision');

  static Future<void> start() async {
    await _channel.invokeMethod('startForegroundService');
  }

  static Future<void> stop() async {
    await _channel.invokeMethod('stopForegroundService');
  }

  static Future<void> showTransferDialog({
    required String sessionId,
    required String senderName,
    required int fileCount,
    required String totalSize,
  }) async {
    await _channel.invokeMethod('showTransferDialog', {
      'sessionId': sessionId,
      'senderName': senderName,
      'fileCount': fileCount,
      'totalSize': totalSize,
    });
  }

  static void listenForDecisions(void Function(String sessionId, bool accepted) onDecision) {
    _decisionChannel.setMethodCallHandler((call) async {
      if (call.method == 'onTransferDecision') {
        final sessionId = call.arguments['sessionId'] as String;
        final accepted = call.arguments['accepted'] as bool;
        onDecision(sessionId, accepted);
      }
    });
  }
}
