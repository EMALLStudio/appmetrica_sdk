// Copyright 2019 EM ALL iT Studio. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.
package com.emallstudio.appmetrica_sdk

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.yandex.metrica.DeferredDeeplinkListener
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import com.yandex.metrica.profile.Attribute
import com.yandex.metrica.profile.UserProfile
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterPluginBinding
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler

class AppMetricaSdkPlugin : FlutterPlugin, MethodCallHandler, ActivityAware {
    private var activity: FlutterActivity? = null
    private var binaryMessenger: BinaryMessenger? = null
    private var channel: MethodChannel? = null

    private val deferredDeeplinkListener = object : DeferredDeeplinkListener {
        override fun onDeeplinkLoaded(link: String) {
            val channel = channel ?: return

            Handler(Looper.getMainLooper()).post {
                channel.invokeMethod("onDeferredDeeplinkLoaded", link)
            }
        }

        override fun onError(error: DeferredDeeplinkListener.Error, referrer: String?) {
            val channel = channel ?: return

            Handler(Looper.getMainLooper()).post {
                channel.invokeMethod(
                    "onDeferredDeeplinkError",
                    mapOf(
                        "name" to error.name,
                        "description" to error.description,
                        "referrer" to referrer
                    ),
                )
            }
        }
    }

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPluginBinding) {
        binaryMessenger = flutterPluginBinding.binaryMessenger
    }

    override fun onDetachedFromEngine(binding: FlutterPluginBinding) {
        binaryMessenger = null
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        if (binaryMessenger == null) return

        activity = binding.activity as FlutterActivity
        channel = MethodChannel(binaryMessenger, "emallstudio.com/appmetrica_sdk")
        channel?.setMethodCallHandler(this)
    }

    override fun onDetachedFromActivityForConfigChanges() {
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    }

    override fun onDetachedFromActivity() {
        activity = null
        channel?.setMethodCallHandler(null)
        channel = null
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "activate" -> handleActivate(call, result)
            "reportEvent" -> handleReportEvent(call, result)
            "reportUserProfileCustomString" -> handleReportUserProfileCustomString(call, result)
            "reportUserProfileCustomNumber" -> handleReportUserProfileCustomNumber(call, result)
            "reportUserProfileCustomBoolean" -> handleReportUserProfileCustomBoolean(call, result)
            "reportUserProfileCustomCounter" -> handleReportUserProfileCustomCounter(call, result)
            "reportUserProfileUserName" -> handleReportUserProfileUserName(call, result)
            "reportUserProfileNotificationsEnabled" -> handleReportUserProfileNotificationsEnabled(
                call,
                result,
            )
            "setStatisticsSending" -> handleSetStatisticsSending(call, result)
            "getLibraryVersion" -> handleGetLibraryVersion(result)
            "setUserProfileID" -> handleSetUserProfileID(call, result)
            "sendEventsBuffer" -> handleSendEventsBuffer(result)
            "reportReferralUrl" -> handleReportReferralUrl(call, result)
            "reportAppOpen" -> handleReportAppOpen(call, result)
            "requestDeferredDeeplink" -> handleRequestDeferredDeeplinkParameters(result)
            else -> result.notImplemented()
        }
    }

    private fun handleActivate(call: MethodCall, result: MethodChannel.Result) {
        val activity = activity
        if (activity == null) {
            result.error(
                "Error performing activation",
                "Activity is null",
                null,
            )
            return
        }

        try {
            val apiKey: String = call.argument("apiKey")!!
            val sessionTimeout: Int = call.argument("sessionTimeout")!!
            val locationTracking: Boolean = call.argument("locationTracking")!!
            val statisticsSending: Boolean = call.argument("statisticsSending")!!
            val crashReporting: Boolean = call.argument("crashReporting")!!
            val maxReportsInDatabaseCount: Int = call.argument("maxReportsInDatabaseCount")!!

            val config = YandexMetricaConfig.newConfigBuilder(apiKey)
                .withLogs()
                .withSessionTimeout(sessionTimeout)
                .withLocationTracking(locationTracking)
                .withStatisticsSending(statisticsSending)
                .withCrashReporting(crashReporting)
                .withMaxReportsInDatabaseCount(maxReportsInDatabaseCount)
                .build()

            YandexMetrica.activate(activity, config)

            YandexMetrica.enableActivityAutoTracking(activity.application)
        } catch (e: Exception) {
            Log.e(TAG, "Error performing activation", e)
            result.error("Error performing activation", e.message, null)
        }
        result.success(null)
    }

    private fun handleReportEvent(call: MethodCall, result: MethodChannel.Result) {
        try {
            val eventName: String = call.argument("name")!!
            val attributes: Map<String, Any>? = call.argument("attributes")
            YandexMetrica.reportEvent(eventName, attributes)
        } catch (e: Exception) {
            Log.e(TAG, "Error reporting event", e)
            result.error("Error reporting event", e.message, null)
        }
        result.success(null)
    }

    private fun handleReportUserProfileCustomString(
        call: MethodCall,
        result: MethodChannel.Result,
    ) {
        try {
            val key: String = call.argument("key")!!
            val value: String? = call.argument("value")
            val profileBuilder = UserProfile.newBuilder()
            if (value == null) {
                profileBuilder.apply(Attribute.customString(key).withValueReset())
            } else {
                profileBuilder.apply(Attribute.customString(key).withValue(value))
            }
            YandexMetrica.reportUserProfile(profileBuilder.build())
        } catch (e: Exception) {
            Log.e(TAG, "Error reporting user profile custom string", e)
            result.error("Error reporting user profile custom string", e.message, null)
        }
        result.success(null)
    }

    private fun handleReportUserProfileCustomNumber(
        call: MethodCall,
        result: MethodChannel.Result,
    ) {
        try {
            val key: String = call.argument("key")!!
            val value: Double? = call.argument("value")
            val profileBuilder = UserProfile.newBuilder()
            if (value == null) {
                profileBuilder.apply(Attribute.customNumber(key).withValueReset())
            } else {
                profileBuilder.apply(Attribute.customNumber(key).withValue(value))
            }
            YandexMetrica.reportUserProfile(profileBuilder.build())
        } catch (e: Exception) {
            Log.e(TAG, "Error reporting user profile custom number", e)
            result.error("Error reporting user profile custom number", e.message, null)
        }
        result.success(null)
    }

    private fun handleReportUserProfileCustomBoolean(
        call: MethodCall,
        result: MethodChannel.Result,
    ) {
        try {
            val key: String = call.argument("key")!!
            val value: Boolean? = call.argument("value")
            val profileBuilder = UserProfile.newBuilder()
            if (value == null) {
                profileBuilder.apply(Attribute.customBoolean(key).withValueReset())
            } else {
                profileBuilder.apply(Attribute.customBoolean(key).withValue(value))
            }
            YandexMetrica.reportUserProfile(profileBuilder.build())
        } catch (e: Exception) {
            Log.e(TAG, "Error reporting user profile custom boolean", e)
            result.error("Error reporting user profile custom boolean", e.message, null)
        }
        result.success(null)
    }

    private fun handleReportUserProfileCustomCounter(
        call: MethodCall,
        result: MethodChannel.Result
    ) {
        try {
            val key: String = call.argument("key")!!
            val delta: Double = call.argument("delta")!!
            val profileBuilder = UserProfile.newBuilder()
            profileBuilder.apply(Attribute.customCounter(key).withDelta(delta))
            YandexMetrica.reportUserProfile(profileBuilder.build())
        } catch (e: Exception) {
            Log.e(TAG, "Error reporting user profile custom counter", e)
            result.error("Error reporting user profile custom counter", e.message, null)
        }
        result.success(null)
    }

    private fun handleReportUserProfileUserName(call: MethodCall, result: MethodChannel.Result) {
        try {
            val userName: String? = call.argument("userName")
            val profileBuilder = UserProfile.newBuilder()
            if (userName == null) {
                profileBuilder.apply(Attribute.name().withValueReset())
            } else {
                profileBuilder.apply(Attribute.name().withValue(userName))
            }
            YandexMetrica.reportUserProfile(profileBuilder.build())
        } catch (e: Exception) {
            Log.e(TAG, "Error reporting user profile user name", e)
            result.error("Error reporting user profile user name", e.message, null)
        }
        result.success(null)
    }

    private fun handleReportUserProfileNotificationsEnabled(
        call: MethodCall,
        result: MethodChannel.Result,
    ) {
        try {
            val notificationsEnabled: Boolean? = call.argument("notificationsEnabled")
            val profileBuilder = UserProfile.newBuilder()
            if (notificationsEnabled == null) {
                profileBuilder.apply(Attribute.notificationsEnabled().withValueReset())
            } else {
                profileBuilder.apply(
                    Attribute.notificationsEnabled().withValue(notificationsEnabled)
                )
            }
            YandexMetrica.reportUserProfile(profileBuilder.build())
        } catch (e: Exception) {
            Log.e(TAG, "Error reporting user profile user name", e)
            result.error("Error reporting user profile user name", e.message, null)
        }
        result.success(null)
    }

    private fun handleSetStatisticsSending(call: MethodCall, result: MethodChannel.Result) {
        val activity = activity
        if (activity == null) {
            result.error(
                "Error enable sending statistics",
                "Activity is null",
                null,
            )
            return
        }

        try {
            val statisticsSending: Boolean = call.argument("statisticsSending")!!
            YandexMetrica.setStatisticsSending(activity, statisticsSending)
        } catch (e: Exception) {
            Log.e(TAG, "Error enable sending statistics", e)
            result.error("Error enable sending statistics", e.message, null)
        }
        result.success(null)
    }

    private fun handleGetLibraryVersion(result: MethodChannel.Result) {
        try {
            result.success(YandexMetrica.getLibraryVersion())
        } catch (e: Exception) {
            Log.e(TAG, "Error enable sending statistics", e)
            result.error("Error enable sending statistics", e.message, null)
        }
    }

    private fun handleSetUserProfileID(call: MethodCall, result: MethodChannel.Result) {
        try {
            val userProfileID: String? = call.argument("userProfileID")
            YandexMetrica.setUserProfileID(userProfileID)
        } catch (e: Exception) {
            Log.e(TAG, "Error sets the ID of the user profile", e)
            result.error("Error sets the ID of the user profile", e.message, null)
        }
        result.success(null)
    }

    private fun handleSendEventsBuffer(result: MethodChannel.Result) {
        try {
            YandexMetrica.sendEventsBuffer()
        } catch (e: Exception) {
            Log.e(TAG, "Error sending stored events from the buffer", e)
            result.error("Error sending stored events from the buffer", e.message, null)
        }
        result.success(null)
    }

    private fun handleReportReferralUrl(call: MethodCall, result: MethodChannel.Result) {
        try {
            val referral: String = call.argument("referral")!!
            YandexMetrica.reportReferralUrl(referral)
        } catch (e: Exception) {
            Log.e(TAG, "Error sets the ID of the user profile", e)
            result.error("Error sets the ID of the user profile", e.message, null)
        }
        result.success(null)
    }

    private fun handleReportAppOpen(call: MethodCall, result: MethodChannel.Result) {
        try {
            val deeplink: String = call.argument("deeplink")!!
            YandexMetrica.reportAppOpen(deeplink)
        } catch (e: Exception) {
            Log.e(TAG, "Error report app open", e)
            result.error("Error report app open", e.message, null)
        }
        result.success(null)
    }

    private fun handleRequestDeferredDeeplinkParameters(result: MethodChannel.Result) {
        try {
            YandexMetrica.requestDeferredDeeplink(deferredDeeplinkListener)
        } catch (e: Exception) {
            Log.e(TAG, "Error report app open", e)
            result.error("Error report app open", e.message, null)
        }
        result.success(null)
    }

    private companion object {
        private val TAG = AppMetricaSdkPlugin::class.simpleName
    }
}