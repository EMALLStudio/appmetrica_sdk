// Copyright 2019 EM ALL iT Studio. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.emallstudio.appmetrica_sdk;

import androidx.annotation.NonNull;

import android.util.Log;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import android.util.SparseArray;

import java.util.Currency;
import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.view.FlutterView;

import com.yandex.metrica.Revenue;
import com.yandex.metrica.YandexMetrica;
import com.yandex.metrica.YandexMetricaConfig;
import com.yandex.metrica.profile.Attribute;
import com.yandex.metrica.profile.StringAttribute;
import com.yandex.metrica.profile.UserProfile;
import com.yandex.metrica.profile.UserProfileUpdate;

/** AppmetricaSdkPlugin */
public class AppmetricaSdkPlugin implements MethodCallHandler, FlutterPlugin {
    private static final String TAG = "AppmetricaSdkPlugin";
    private MethodChannel methodChannel;
    private Context context;
    private Application application;

    /** Plugin registration for v1 embedder. */
    public static void registerWith(Registrar registrar) {
        final AppmetricaSdkPlugin instance = new AppmetricaSdkPlugin();
        instance.onAttachedToEngine(registrar.context(), registrar.messenger());
    }

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
      onAttachedToEngine(flutterPluginBinding.getApplicationContext(), flutterPluginBinding.getBinaryMessenger());
    }

    private void onAttachedToEngine(Context applicationContext, BinaryMessenger binaryMessenger) {
      application = (Application) applicationContext;
      context = applicationContext;
      methodChannel = new MethodChannel(binaryMessenger, "emallstudio.com/appmetrica_sdk");
      methodChannel.setMethodCallHandler(this);
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        methodChannel.setMethodCallHandler(null);
        methodChannel = null;
        context = null;
        application = null;
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        switch (call.method) {
            case "activate":
                handleActivate(call, result);
                break;
            case "reportEvent":
                handleReportEvent(call, result);
                break;
            case "reportUserProfileCustomString":
                handleReportUserProfileCustomString(call, result);
                break;
            case "reportUserProfileCustomNumber":
                handleReportUserProfileCustomNumber(call, result);
                break;
            case "reportUserProfileCustomBoolean":
                handleReportUserProfileCustomBoolean(call, result);
                break;
            case "reportUserProfileCustomCounter":
                handleReportUserProfileCustomCounter(call, result);
                break;
            case "reportUserProfileUserName":
                handleReportUserProfileUserName(call, result);
                break;
            case "reportUserProfileNotificationsEnabled":
                handleReportUserProfileNotificationsEnabled(call, result);
                break;
            case "setStatisticsSending":
                handleSetStatisticsSending(call, result);
                break;
            case "getLibraryVersion":
                handleGetLibraryVersion(call, result);
                break;
            case "setUserProfileID":
                handleSetUserProfileID(call, result);
                break;
            case "sendEventsBuffer":
                handleSendEventsBuffer(call, result);
                break;
            case "reportReferralUrl":
                handleReportReferralUrl(call, result);
                break;
	        case "reportRevenueWithoutValidation":
		        handleReportRevenueWithoutValidation(call, result);
		        break;
	        case "reportRevenueWithValidation":
		        handleReportRevenueWithValidation(call, result);
		        break;
            default:
              result.notImplemented();
              break;
          }
    }

    private void handleActivate(MethodCall call, Result result) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> arguments = (Map<String, Object>) call.arguments;
            // Get activation parameters.
            final String apiKey = (String) arguments.get("apiKey");
            final int sessionTimeout = (int) arguments.get("sessionTimeout");
            final boolean locationTracking = (boolean) arguments.get("locationTracking");
            final boolean statisticsSending = (boolean) arguments.get("statisticsSending");
            final boolean crashReporting = (boolean) arguments.get("crashReporting");
            final int maxReportsInDatabaseCount = (int) arguments.get("maxReportsInDatabaseCount");
            // Creating an extended library configuration.
            YandexMetricaConfig config = YandexMetricaConfig.newConfigBuilder(apiKey)
                    .withLogs()
                    .withSessionTimeout(sessionTimeout)
                    .withLocationTracking(locationTracking)
                    .withStatisticsSending(statisticsSending)
                    .withCrashReporting(crashReporting)
                    .withMaxReportsInDatabaseCount(maxReportsInDatabaseCount)
                    .build();
            // Initializing the AppMetrica SDK.
            YandexMetrica.activate(context, config);
            // Automatic tracking of user activity.
            YandexMetrica.enableActivityAutoTracking(application);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            result.error("Error performing activation", e.getMessage(), null);
        }
        result.success(null);
    }

    private void handleReportEvent(MethodCall call, Result result) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> arguments = (Map<String, Object>) call.arguments;
            final String eventName = (String) arguments.get("name");
            @SuppressWarnings("unchecked")
            Map<String, Object> attributes = (Map<String, Object>) arguments.get("attributes");
            if (attributes == null) {
                YandexMetrica.reportEvent(eventName);
            } else {
                YandexMetrica.reportEvent(eventName, attributes);
            }

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            result.error("Error reporing event", e.getMessage(), null);
        }

        result.success(null);
    }

    private void handleReportUserProfileCustomString(MethodCall call, Result result) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> arguments = (Map<String, Object>) call.arguments;
            final String key = (String) arguments.get("key");
            final String value = (String) arguments.get("value");
            UserProfile.Builder profileBuilder = UserProfile.newBuilder();
            if (value != null) {
                profileBuilder.apply(Attribute.customString(key).withValue(value));
            } else {
                profileBuilder.apply(Attribute.customString(key).withValueReset());
            }
            YandexMetrica.reportUserProfile(profileBuilder.build());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            result.error("Error reporing user profile custom string", e.getMessage(), null);
        }

        result.success(null);
    }

    private void handleReportUserProfileCustomNumber(MethodCall call, Result result) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> arguments = (Map<String, Object>) call.arguments;
            final String key = (String) arguments.get("key");
            UserProfile.Builder profileBuilder = UserProfile.newBuilder();
            if (arguments.get("value") != null) {
                final double value = (double) arguments.get("value");
                profileBuilder.apply(Attribute.customNumber(key).withValue(value));
            } else {
                profileBuilder.apply(Attribute.customNumber(key).withValueReset());
            }
            YandexMetrica.reportUserProfile(profileBuilder.build());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            result.error("Error reporing user profile custom number", e.getMessage(), null);
        }

        result.success(null);
    }

    private void handleReportUserProfileCustomBoolean(MethodCall call, Result result) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> arguments = (Map<String, Object>) call.arguments;
            final String key = (String) arguments.get("key");
            UserProfile.Builder profileBuilder = UserProfile.newBuilder();
            if (arguments.get("value") != null) {
                final boolean value = (boolean) arguments.get("value");
                profileBuilder.apply(Attribute.customBoolean(key).withValue(value));
            } else {
                profileBuilder.apply(Attribute.customBoolean(key).withValueReset());
            }
            YandexMetrica.reportUserProfile(profileBuilder.build());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            result.error("Error reporing user profile custom boolean", e.getMessage(), null);
        }

        result.success(null);
    }

    private void handleReportUserProfileCustomCounter(MethodCall call, Result result) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> arguments = (Map<String, Object>) call.arguments;
            final String key = (String) arguments.get("key");
            final double delta = (double) arguments.get("delta");
            UserProfile.Builder profileBuilder = UserProfile.newBuilder();
            profileBuilder.apply(Attribute.customCounter(key).withDelta(delta));
            YandexMetrica.reportUserProfile(profileBuilder.build());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            result.error("Error reporing user profile custom counter", e.getMessage(), null);
        }

        result.success(null);
    }

    private void handleReportUserProfileUserName(MethodCall call, Result result) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> arguments = (Map<String, Object>) call.arguments;
            UserProfile.Builder profileBuilder = UserProfile.newBuilder();
            if (arguments.get("userName") != null) {
                final String userName = (String) arguments.get("userName");
                profileBuilder.apply(Attribute.name().withValue(userName));
            } else {
                profileBuilder.apply(Attribute.name().withValueReset());
            }
            YandexMetrica.reportUserProfile(profileBuilder.build());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            result.error("Error reporing user profile user name", e.getMessage(), null);
        }

        result.success(null);
    }

    private void handleReportUserProfileNotificationsEnabled(MethodCall call, Result result) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> arguments = (Map<String, Object>) call.arguments;
            UserProfile.Builder profileBuilder = UserProfile.newBuilder();
            if (arguments.get("notificationsEnabled") != null) {
                final boolean notificationsEnabled = (boolean) arguments.get("notificationsEnabled");
                profileBuilder.apply(Attribute.notificationsEnabled().withValue(notificationsEnabled));
            } else {
                profileBuilder.apply(Attribute.notificationsEnabled().withValueReset());
            }
            YandexMetrica.reportUserProfile(profileBuilder.build());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            result.error("Error reporing user profile user name", e.getMessage(), null);
        }

        result.success(null);
    }

    private void handleSetStatisticsSending(MethodCall call, Result result) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> arguments = (Map<String, Object>) call.arguments;
            final boolean statisticsSending = (boolean) arguments.get("statisticsSending");
            YandexMetrica.setStatisticsSending(context, statisticsSending);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            result.error("Error enable sending statistics", e.getMessage(), null);
        }

        result.success(null);
    }

    private void handleGetLibraryVersion(MethodCall call, Result result) {
        try {
            result.success(YandexMetrica.getLibraryVersion());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            result.error("Error enable sending statistics", e.getMessage(), null);
        }
    }

    private void handleSetUserProfileID(MethodCall call, Result result) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> arguments = (Map<String, Object>) call.arguments;
            final String userProfileID = (String) arguments.get("userProfileID");
            YandexMetrica.setUserProfileID(userProfileID);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            result.error("Error sets the ID of the user profile", e.getMessage(), null);
        }

        result.success(null);
    }

    private void handleSendEventsBuffer(MethodCall call, Result result) {
        try {
            YandexMetrica.sendEventsBuffer();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            result.error("Error sending stored events from the buffer", e.getMessage(), null);

        }

        result.success(null);
    }

    private void handleReportReferralUrl(MethodCall call, Result result) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> arguments = (Map<String, Object>) call.arguments;
            final String referral = (String) arguments.get("referral");
            YandexMetrica.reportReferralUrl(referral);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            result.error("Error sets the ID of the user profile", e.getMessage(), null);
        }

        result.success(null);
    }    
	
	private void handleReportRevenueWithoutValidation(MethodCall call, Result result) {
		try {
			@SuppressWarnings("unchecked") Map<String, Object> arguments =
					(Map<String, Object>) call.arguments;
			final Long priceMicros = (Long) arguments.get("priceMicros");
			final String currency = (String) arguments.get("currency");
			final String productId = (String) arguments.get("productId");
			final int quantity = (int) arguments.get("quantity");
            final String payload = (String) arguments.get("payload");
			// Creating the Revenue instance.
			Revenue revenue = Revenue.newBuilderWithMicros(priceMicros,
			                                               Currency.getInstance(currency))
			                         .withProductID(productId)
			                         .withQuantity(quantity)
			                         // To group purchases, pass the OrderID parameter in the
			                         // .withPayload(String payload) method.
			                         .withPayload(payload)
			                         .build();
			YandexMetrica.reportRevenue(revenue);
		} catch(Exception e) {
			Log.e(TAG, e.getMessage(), e);
			result.error("Error report revenue", e.getMessage(), null);
		}
		result.success(null);
	}
	
	private void handleReportRevenueWithValidation(MethodCall call, Result result) {
		try {
			@SuppressWarnings("unchecked") Map<String, Object> arguments =
					(Map<String, Object>) call.arguments;
			final Long priceMicros = (Long) arguments.get("priceMicros");
			final String currency = (String) arguments.get("currency");
			final String productId = (String) arguments.get("productId");
			final int quantity = (int) arguments.get("quantity");
            final String payload = (String) arguments.get("payload");
			final String originalJSON = (String) arguments.get("originalJSON");
			final String signature = (String) arguments.get("signature");
			// Creating the Revenue.Receipt instance.
			// It is used for checking purchases in Google Play.
			Revenue.Receipt revenueReceipt = Revenue.Receipt.newBuilder()
			                                                .withData(originalJSON)
			                                                .withSignature(signature)
			                                                .build();
			// Creating the Revenue instance.
			Revenue revenue = Revenue.newBuilderWithMicros(priceMicros,
			                                               Currency.getInstance(currency))
			                         .withProductID(productId)
			                         .withQuantity(quantity)
			                         .withReceipt(revenueReceipt)
			                         .withPayload(payload)
			                         .build();
			// Sending the Revenue instance.
			YandexMetrica.reportRevenue(revenue);
		} catch(Exception e) {
			Log.e(TAG, e.getMessage(), e);
			result.error("Error report revenue", e.getMessage(), null);
		}
		result.success(null);
	}

}
