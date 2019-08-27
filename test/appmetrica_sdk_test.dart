import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:appmetrica_sdk/appmetrica_sdk.dart';

void main() {
  const MethodChannel channel = MethodChannel('emallstudio.com/appmetrica_sdk');

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  /*test('getPlatformVersion', () async {
    expect(await AppmetricaSdk.platformVersion, '42');
  });*/
}
