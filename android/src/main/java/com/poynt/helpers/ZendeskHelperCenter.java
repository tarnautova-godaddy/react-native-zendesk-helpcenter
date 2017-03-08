package com.poynt.helpers;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.zendesk.logger.Logger;
import com.zendesk.sdk.model.access.AnonymousIdentity;
import com.zendesk.sdk.network.impl.ZendeskConfig;
import com.zendesk.sdk.support.SupportActivity;

import java.util.HashMap;
import java.util.Map;

import static android.R.attr.duration;
import static android.R.id.message;
import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class ZendeskHelperCenter extends ReactContextBaseJavaModule {

  private static final String DURATION_SHORT_KEY = "SHORT";
  private static final String DURATION_LONG_KEY = "LONG";

  public ZendeskHelperCenter(ReactApplicationContext reactContext) {
    super(reactContext);
  }

  @Override
  public String getName() {
    return "ZendeskHelpCenter";
  }

  @Override
  public Map<String, Object> getConstants() {
    final Map<String, Object> constants = new HashMap<>();
    constants.put(DURATION_SHORT_KEY, Toast.LENGTH_SHORT);
    constants.put(DURATION_LONG_KEY, Toast.LENGTH_LONG);
    return constants;
  }

  @ReactMethod
  public void toast(String message, int duration) {
    Toast.makeText(getReactApplicationContext(), message, duration).show();
  }



  @ReactMethod
  public void show(ReadableMap options) {

    initializeZendesk(options);

  }

  private void initializeZendesk(ReadableMap options) {
    if(!options.hasKey("url")||!options.hasKey("appId") || !options.hasKey("clientId") || !options.hasKey("name") || !options.hasKey("email") ){
      this.toast("Config params must include url, appId, clientId, name, & email",Toast.LENGTH_LONG);
      return;
    }


    String url = options.getString("url");
    String appId = options.getString("appId");
    String clientId = options.getString("clientId");
    String nameId = options.getString("name");
    String email = options.getString("email");
    Context ctx = getReactApplicationContext();
    // Initialize the Support SDK with your Zendesk Support subdomain, mobile SDK app ID, and client ID.
    // Get these details from your Zendesk Support dashboard: Admin -> Channels -> Mobile SDK
    ZendeskConfig.INSTANCE.init(ctx, url, appId, clientId);
    // Authenticate anonymously as a Zendesk Support user
    ZendeskConfig.INSTANCE.setIdentity(
            new AnonymousIdentity.Builder()
                    .withNameIdentifier(nameId)
                    .withEmailIdentifier(email)
                    .build()
    );

    Activity activity = getCurrentActivity();
    if(activity != null){
      new SupportActivity.Builder().show(activity);
    }
  }

}