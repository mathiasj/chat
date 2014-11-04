//import 'dart:async';
import 'dart:convert';
import 'dart:html';

class Client {
  static const Duration RECONNECT_DELAY = const Duration(milliseconds: 500);

  bool connectPending = false;
  WebSocket webSocket;

  Client() {
    // disable submit on return in form
    querySelector('#chatForm').onSubmit.listen((Event e) {
      submitSay();
      e.preventDefault();
    });

    connect();
    var sayButton = querySelector('#sayButton');
    sayButton.onClick.listen((e) {
      submitSay();
    });
  }

  void submitSay() {
    var sayTextElm = querySelector('#sayText');
    say(sayTextElm.value);
    sayTextElm.value = '';
    sayTextElm.focus();
  }

  void connect() {
    connectPending = false;
    webSocket = new WebSocket('ws://localhost:9000/ws/chat');

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
      case 'msg':
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
    var history = querySelector('#chatWindow').innerHtml;
    querySelector('#chatWindow').innerHtml = history
      + "<div class=\"msg\">" + str + "</div>";
  }

  void say(String input) {
    if (input.isEmpty) return;

    var request = {
        'request': 'newMessage',
        'msg': input
    };
    webSocket.send(JSON.encode(request));
  }
}

void main() {
  var client = new Client();
}


