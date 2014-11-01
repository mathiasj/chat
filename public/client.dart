//import 'dart:async';
import 'dart:convert';
import 'dart:html';

class Client {
  static const Duration RECONNECT_DELAY = const Duration(milliseconds: 500);

  bool connectPending = false;
  WebSocket webSocket;

  Client() {
    connect();
  }

  void connect() {
    connectPending = false;
    webSocket = new WebSocket('ws://127.0.0.1/ws/chat');

    webSocket.onOpen.first.then((_) {
      webSocket.onMessage.listen((e) {
        handleMessage(e.data);
      });

      webSocket.onClose.first.then((_) {
        updateChatWindow("Connection disconnected to ${webSocket.url}.");
      });
    });

    webSocket.onError.first.then((_) {
      updateChatWindow("Failed to connect to ${webSocket.url}");
    });
  }

  void handleMessage(data) {
    var json = JSON.decode(data);
    var response = json['response'];
    switch (response) {
      case 'newMessage':
        updateChatWindow(json['msg']);
        break;

      case 'info':
        updateChatWindow(json['msg']);
        break;

    default:
        updateChatWindow("Invalid response: '$response'");
    }
  }

  void updateChatWindow(String str) {
    querySelector('#chatWindow').text = str;
  }
}


void main() {
  var client = new Client();
}


