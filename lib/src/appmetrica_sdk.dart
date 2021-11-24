// Copyright 2019 EM ALL iT Studio. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

part of appmetrica_sdk;

/// Called if it is possible to get a delayed deeplink
typedef OnDeferredDeeplinkLoaded = Future<dynamic> Function(String link);

/// Called if an error occurs while trying to get the parameters of a pending deeplink
/// [name] - name of error
/// [description] - description of error
/// [referrer] - Google Play Install Referrer if PARSE_ERROR
typedef OnDeferredDeeplinkError = Future<dynamic> Function({
  required String name,
  required String description,
  String? referrer,
});

/// Appmetrica SDK singleton
class AppmetricaSdk {
  String? _apiKey;
  OnDeferredDeeplinkLoaded? _onDeferredDeeplinkLoaded;
  OnDeferredDeeplinkError? _onDeferredDeeplinkError;

  final MethodChannel _channel;
  static AppmetricaSdk? _instance;

  factory AppmetricaSdk() {
    if (_instance == null) {
      _instance = AppmetricaSdk.private(
        const MethodChannel('emallstudio.com/appmetrica_sdk'),
      );
    }
    return _instance!;
  }

  AppmetricaSdk.private(this._channel);

  Future<dynamic> _handleMethod(MethodCall call) async {
    switch (call.method) {
      case 'onDeferredDeeplinkLoaded':
        return _onDeferredDeeplinkLoaded?.call(call.arguments as String);
      case 'onDeferredDeeplinkError':
        final data = call.arguments.cast<dynamic, String?>();
        return _onDeferredDeeplinkError?.call(
          name: data['name'],
          description: data['description'],
          referrer: data['referrer'],
        );
    }
  }

  /// Initializes the library in an application with given parameters.
  ///
  /// The [apiKey] is API key of the application. The API key is a unique application identifier that is issued
  /// in the AppMetrica web interface during app registration. Make sure you have entered it correctly.
  /// The [sessionTimeout] sets the session timeout in seconds. The default value is 10 (minimum allowed value).
  /// The [locationTracking] enables/disables sending location of the device. By default, sending is enabled.
  /// The [statisticsSending] enables/disables sending statistics to the AppMetrica server. By default, sending is enabled.
  /// The [crashReporting] indicating that sending app crashes is enabled. By default, sending is enabled.
  /// Android only. The [maxReportsInDatabaseCount] is maximum number of events that can be stored in the database on the phone before being sent to AppMetrica. If there are more events, old records will begin to be deleted. The default value is 1000 (allowed values from 100 to 10000).
  Future<void> activate({
    required String apiKey,
    int sessionTimeout = 10,
    bool locationTracking = true,
    bool statisticsSending = true,
    bool crashReporting = true,
    int maxReportsInDatabaseCount = 1000,
  }) async {
    await _channel.invokeMethod<void>('activate', <String, dynamic>{
      'apiKey': apiKey,
      'sessionTimeout': sessionTimeout,
      'locationTracking': locationTracking,
      'statisticsSending': statisticsSending,
      'crashReporting': crashReporting,
      'maxReportsInDatabaseCount': maxReportsInDatabaseCount,
    });

    // Set the API Key after activation
    _apiKey = apiKey;
  }

  /// Sends an event with [name] and message as a set of [attributes] to the AppMetrica server.
  Future<void> reportEvent({
    required String name,
    Map<String, dynamic>? attributes,
  }) async {
    if (_apiKey == null) {
      throw 'The API key is not set';
    }
    await _channel.invokeMethod<void>('reportEvent', <String, dynamic>{
      'name': name,
      'attributes': attributes,
    });
  }

  /// Sends custom string user profile attribute with the given [key] and [value] to the AppMetrica server.
  Future<void> reportUserProfileCustomString({
    required String key,
    String? value,
  }) async {
    if (_apiKey == null) {
      throw 'The API key is not set';
    }
    await _channel.invokeMethod<void>(
      'reportUserProfileCustomString',
      <String, dynamic>{
        'key': key,
        'value': value,
      },
    );
  }

  /// Sends custom number user profile attribute with the given [key] and [value] to the AppMetrica server.
  Future<void> reportUserProfileCustomNumber({
    required String key,
    double? value,
  }) async {
    if (_apiKey == null) {
      throw 'The API key is not set';
    }
    await _channel.invokeMethod<void>(
      'reportUserProfileCustomNumber',
      <String, dynamic>{
        'key': key,
        'value': value,
      },
    );
  }

  /// Sends custom boolean user profile attribute with the given [key] and [value] to the AppMetrica server.
  Future<void> reportUserProfileCustomBoolean({
    required String key,
    bool? value,
  }) async {
    if (_apiKey == null) {
      throw 'The API key is not set';
    }
    await _channel.invokeMethod<void>(
      'reportUserProfileCustomBoolean',
      <String, dynamic>{
        'key': key,
        'value': value,
      },
    );
  }

  /// Sends custom counter user profile attribute with the given [key] and [value] to the AppMetrica server.
  Future<void> reportUserProfileCustomCounter({
    required String key,
    required double delta,
  }) async {
    if (_apiKey == null) {
      throw 'The API key is not set';
    }
    await _channel.invokeMethod<void>(
      'reportUserProfileCustomCounter',
      <String, dynamic>{
        'key': key,
        'delta': delta,
      },
    );
  }

  /// Sends predefined [userName] profile attribute to the AppMetrica server.
  Future<void> reportUserProfileUserName([String? userName]) async {
    if (_apiKey == null) {
      throw 'The API key is not set';
    }
    await _channel.invokeMethod<void>(
      'reportUserProfileUserName',
      <String, dynamic>{
        'userName': userName,
      },
    );
  }

  /// Sends predefined [notificationsEnabled] profile attribute to the AppMetrica server.
  Future<void> reportUserProfileNotificationsEnabled({
    bool? notificationsEnabled,
  }) async {
    if (_apiKey == null) {
      throw 'The API key is not set';
    }
    await _channel.invokeMethod<void>(
      'reportUserProfileNotificationsEnabled',
      <String, dynamic>{
        'notificationsEnabled': notificationsEnabled,
      },
    );
  }

  /// Disable and enable sending statistics to the AppMetrica server.
  Future<void> setStatisticsSending(bool statisticsSending) async {
    if (_apiKey == null) {
      throw 'The API key is not set';
    }

    await _channel.invokeMethod<void>(
      'setStatisticsSending',
      <String, dynamic>{
        'statisticsSending': statisticsSending,
      },
    );
  }

  /// Returns the current version of the AppMetrica library.
  Future<String?> getLibraryVersion() async {
    if (_apiKey == null) {
      throw 'The API key is not set';
    }
    return await _channel.invokeMethod<String>('getLibraryVersion');
  }

  /// Sets the ID of the user profile.
  /// Required for predefined profile attributes like Name or Notifications enabled.
  Future<void> setUserProfileID(String? userProfileID) async {
    if (_apiKey == null) {
      throw 'The API key is not set';
    }
    await _channel.invokeMethod<void>(
      'setUserProfileID',
      <String, dynamic>{
        'userProfileID': userProfileID,
      },
    );
  }

  /// Sends stored events from the buffer.
  /// AppMetrica SDK does not send an event immediately after it occurred.
  /// The library stores event data in the buffer. The sendEventsBuffer() method
  /// sends data from the buffer and flushes it. Use the method to force sending
  /// stored events after passing important checkpoints of user scenarios.
  Future<void> sendEventsBuffer() async {
    if (_apiKey == null) {
      throw 'The API key is not set';
    }
    await _channel.invokeMethod<String>('sendEventsBuffer');
    return;
  }

  /// Sets referral URL for this installation. This might be required to track
  /// some specific traffic sources like Facebook.
  /// Referral URL reporting is no longer available
  /// but many agencies use this report
  Future<void> reportReferralUrl(String referral) async {
    if (_apiKey == null) {
      throw 'The API key is not set';
    }
    await _channel.invokeMethod<String>(
      'reportReferralUrl',
      <String, dynamic>{
        'referral': referral,
      },
    );
    return;
  }

  /// Sending a message about opening an application using deeplink
  Future<void> reportAppOpen(String deeplink) async {
    if (_apiKey == null) {
      throw 'The API key is not set';
    }

    await _channel.invokeMethod<String>(
      'reportAppOpen',
      <String, dynamic>{
        'deeplink': deeplink,
      },
    );
    return;
  }

  /// Requests a deferred deeplink
  /// Android only
  Future<void> requestDeferredDeeplink({
    OnDeferredDeeplinkLoaded? onDeferredDeeplinkLoaded,
    OnDeferredDeeplinkError? onDeferredDeeplinkError,
  }) async {
    if (_apiKey == null) {
      throw 'The API key is not set';
    }

    if (!Platform.isAndroid) return;

    _onDeferredDeeplinkLoaded = onDeferredDeeplinkLoaded;
    _onDeferredDeeplinkError = onDeferredDeeplinkError;
    _channel.setMethodCallHandler(_handleMethod);

    await _channel.invokeMethod<String>('requestDeferredDeeplink');
    return;
  }
}
