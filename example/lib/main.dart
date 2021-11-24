import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'package:appmetrica_sdk/appmetrica_sdk.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  // Initializing the AppMetrica SDK
  await AppmetricaSdk().activate(
    apiKey: 'db2206ed-c61a-43aa-b95c-6912f60bd25e',
  );

  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String? _libraryVersion = 'Unknown';

  @override
  void initState() {
    super.initState();
    initLibrarayVersionState();
  }

  // Platform messages are asynchronous, so we initialize in an async method
  Future<void> initLibrarayVersionState() async {
    String? libraryVersion;
    // Platform messages may fail, so we use a try/catch PlatformException
    try {
      libraryVersion = await AppmetricaSdk().getLibraryVersion();
    } on PlatformException {
      libraryVersion = 'Failed to get library version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance
    if (!mounted) {
      return;
    }

    setState(() {
      _libraryVersion = libraryVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: _body(),
      ),
    );
  }

  Widget _body() {
    return Container(
      padding: const EdgeInsets.all(8),
      child: _center(),
    );
  }

  Center _center() {
    return Center(
      child: Column(
        children: <Widget>[
          _appMetricaVersion(),
          _customEventButton(),
          _customEventWithParametersButton(),
          _userProfileAttributesButton(),
          _referralUrlButton(),
        ],
      ),
    );
  }

  Widget _referralUrlButton() {
    return ElevatedButton(
      child: const Text('Send referral URL for this installation'),
      onPressed: () {
        // Sets referral URL for this installation. This might
        // be required to track some specific traffic
        // sources like Facebook
        AppmetricaSdk().reportReferralUrl('fb123456789://example.com/test');
      },
    );
  }

  Widget _userProfileAttributesButton() {
    return ElevatedButton(
      child: const Text('Send user profile attributes'),
      onPressed: () {
        // Sending profile custom string attribute
        AppmetricaSdk().reportUserProfileCustomString(
          key: 'string_attribute',
          value: 'string',
        );

        // Sending profile custom number attribute
        AppmetricaSdk().reportUserProfileCustomNumber(
          key: 'number_attribute',
          value: 55,
        );

        // Sending profile custom boolean attribute
        AppmetricaSdk().reportUserProfileCustomBoolean(
          key: 'boolean_attribute',
          value: true,
        );

        // Sending profile custom attribute of the counter type
        AppmetricaSdk().reportUserProfileCustomCounter(
          key: 'counter_attribute',
          delta: 1,
        );

        // Sets the ID of the user profile. Required for predefined
        // profile attributes like Name or Notifications enabled
        AppmetricaSdk().setUserProfileID('id');

        // Sending profile predefined user name attribute
        AppmetricaSdk().reportUserProfileUserName('John');

        // Sending profile predefined NotificationsEnabled attribute
        AppmetricaSdk().reportUserProfileNotificationsEnabled(
          notificationsEnabled: true,
        );
      },
    );
  }

  Widget _customEventWithParametersButton() {
    return ElevatedButton(
      child: const Text('Send a custom event with nested parameters'),
      onPressed: () {
        // Sending a custom event with nested parameters
        AppmetricaSdk().reportEvent(
          name: 'Current app statistics',
          attributes: <String, dynamic>{
            'Application': 'com.company.myapp.awesomeapp',
            'Audience': 1000000000,
            'Product price in €': 10000000.99,
            'nested map': <String, dynamic>{
              'strategies': 'Age of Empires',
            },
          },
        );
      },
    );
  }

  Widget _appMetricaVersion() {
    return Container(
      child: Text('AppMetrica SDK Library version: $_libraryVersion\n'),
    );
  }

  Widget _customEventButton() {
    return ElevatedButton(
      child: const Text('Send a custom event'),
      onPressed: () {
        // Sending a custom event without nested parameters
        AppmetricaSdk().reportEvent(name: 'Updates installed');
      },
    );
  }
}
