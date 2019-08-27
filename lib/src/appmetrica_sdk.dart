// Copyright 2019 EM ALL iT Studio. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

part of appmetrica_sdk;

/// Appmetrica SDK singleton.
class AppmetricaSdk {
  String _apiKey;
  final MethodChannel _channel;
  static AppmetricaSdk _instance;

  factory AppmetricaSdk() {
    if (_instance == null) {
      _instance = AppmetricaSdk.private(
          const MethodChannel('emallstudio.com/appmetrica_sdk'));
    }
    return _instance;
  }
  AppmetricaSdk.private(this._channel);

  /// Initializes the library in an application with given parameters.
  ///
  /// The [apiKey] is API key of the application. The API key is a unique application identifier that is issued
  /// in the AppMetrica web interface during app registration. Make sure you have entered it correctly.
  /// The [sessionTimeout] sets the session timeout in seconds. The default value is 10 (minimum allowed value).
  /// The [locationTracking] enables/disables sending location of the device. By default, sending is enabled.
  /// The [statisticsSending] enables/disables sending statistics to the AppMetrica server. By default, sending is enabled.
  Future<void> activate(
      {@required String apiKey,
        int sessionTimeout = 10,
        bool locationTracking = true,
        bool statisticsSending = true}) async {
    if (apiKey == null) {
      throw ArgumentError.notNull('apiKey');
    }
    if (sessionTimeout == null) {
      throw ArgumentError.notNull('sessionTimeout');
    }
    if (locationTracking == null) {
      throw ArgumentError.notNull('locationTracking');
    }
    if (statisticsSending == null) {
      throw ArgumentError.notNull('statisticsSending');
    }
    await _channel.invokeMethod<void>('activate', <String, dynamic>{
      'apiKey': apiKey,
      'sessionTimeout': sessionTimeout,
      'locationTracking': locationTracking,
      'statisticsSending': statisticsSending,
    });

    /// Set the API Key after activation.
    _apiKey = apiKey;
  }

  /// Sends an event with [name] and message as a set of [attributes] to the AppMetrica server.
  Future<void> reportEvent(
      {@required String name, Map<String, dynamic> attributes}) async {
    if (_apiKey == null) {
      throw 'The API key is not set';
    }
    await _channel.invokeMethod<void>('reportEvent', <String, dynamic>{
      'name': name,
      'attributes': attributes,
    });
  }

  /// Sends custom string user profile attribute with the given [key] and [value] to the AppMetrica server.
  Future<void> reportUserProfileCustomString(
      {@required String key, @required String value}) async {
    if (_apiKey == null) {
      throw 'The API key is not set';
    }
    if (key == null) {
      throw ArgumentError.notNull('key');
    }
    await _channel
        .invokeMethod<void>('reportUserProfileCustomString', <String, dynamic>{
      'key': key,
      'value': value,
    });
  }

  /// Sends custom number user profile attribute with the given [key] and [value] to the AppMetrica server.
  Future<void> reportUserProfileCustomNumber(
      {@required String key, @required double value}) async {
    if (_apiKey == null) {
      throw 'The API key is not set';
    }
    if (key == null) {
      throw ArgumentError.notNull('key');
    }
    await _channel
        .invokeMethod<void>('reportUserProfileCustomNumber', <String, dynamic>{
      'key': key,
      'value': value,
    });
  }

  /// Sends custom boolean user profile attribute with the given [key] and [value] to the AppMetrica server.
  Future<void> reportUserProfileCustomBoolean(
      {@required String key, @required bool value}) async {
    if (_apiKey == null) {
      throw 'The API key is not set';
    }
    if (key == null) {
      throw ArgumentError.notNull('key');
    }
    await _channel
        .invokeMethod<void>('reportUserProfileCustomBoolean', <String, dynamic>{
      'key': key,
      'value': value,
    });
  }

  /// Sends custom counter user profile attribute with the given [key] and [value] to the AppMetrica server.
  Future<void> reportUserProfileCustomCounter(
      {@required String key, @required double delta}) async {
    if (_apiKey == null) {
      throw 'The API key is not set';
    }
    if (key == null) {
      throw ArgumentError.notNull('key');
    }
    if (delta == null) {
      throw ArgumentError.notNull('delta');
    }
    await _channel
        .invokeMethod<void>('reportUserProfileCustomCounter', <String, dynamic>{
      'key': key,
      'delta': delta,
    });
  }

  /// Sends predefined [userName] profile attribute to the AppMetrica server.
  Future<void> reportUserProfileUserName({@required String userName}) async {
    if (_apiKey == null) {
      throw 'The API key is not set';
    }
    await _channel
        .invokeMethod<void>('reportUserProfileUserName', <String, dynamic>{
      'userName': userName,
    });
  }

  /// Sends predefined [notificationsEnabled] profile attribute to the AppMetrica server.
  Future<void> reportUserProfileNotificationsEnabled(
      {@required bool notificationsEnabled}) async {
    if (_apiKey == null) {
      throw 'The API key is not set';
    }
    await _channel.invokeMethod<void>(
        'reportUserProfileNotificationsEnabled', <String, dynamic>{
      'notificationsEnabled': notificationsEnabled,
    });
  }

  /// Disable and enable sending statistics to the AppMetrica server.
  Future<void> setStatisticsSending({@required bool statisticsSending}) async {
    if (_apiKey == null) {
      throw 'The API key is not set';
    }
    if (statisticsSending == null) {
      throw ArgumentError.notNull('statisticsSending');
    }

    await _channel.invokeMethod<void>('setStatisticsSending', <String, dynamic>{
      'statisticsSending': statisticsSending,
    });
  }

  /// Returns the current version of the AppMetrica library.
  Future<String> getLibraryVersion() async {
    if (_apiKey == null) {
      throw 'The API key is not set';
    }
    return await _channel.invokeMethod<String>('getLibraryVersion');
  }
}
