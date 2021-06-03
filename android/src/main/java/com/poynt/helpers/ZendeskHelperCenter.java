package com.poynt.helpers;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;

import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.zendesk.sdk.model.access.AnonymousIdentity;
import com.zendesk.sdk.network.impl.ZendeskConfig;
import com.zendesk.sdk.support.SupportActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static android.R.attr.duration;
import static android.R.id.message;
import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class ZendeskHelperCenter extends ReactContextBaseJavaModule {
    private static final String TAG = "ZendeskHelpCenter";
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

    private static JSONObject convertMapToJson(ReadableMap readableMap) throws JSONException {
        JSONObject object = new JSONObject();
        ReadableMapKeySetIterator iterator = readableMap.keySetIterator();
        while (iterator.hasNextKey()) {
            String key = iterator.nextKey();
            switch (readableMap.getType(key)) {
                case Null:
                    object.put(key, JSONObject.NULL);
                    break;
                case Boolean:
                    object.put(key, readableMap.getBoolean(key));
                    break;
                case Number:
                    object.put(key, readableMap.getDouble(key));
                    break;
                case String:
                    object.put(key, readableMap.getString(key));
                    break;
                case Map:
                    object.put(key, convertMapToJson(readableMap.getMap(key)));
                    break;
                case Array:
                    object.put(key, convertArrayToJson(readableMap.getArray(key)));
                    break;
            }
        }
        return object;
    }

    private static JSONArray convertArrayToJson(ReadableArray readableArray) throws JSONException {
        JSONArray array = new JSONArray();
        for (int i = 0; i < readableArray.size(); i++) {
            switch (readableArray.getType(i)) {
                case Null:
                    break;
                case Boolean:
                    array.put(readableArray.getBoolean(i));
                    break;
                case Number:
                    array.put(readableArray.getDouble(i));
                    break;
                case String:
                    array.put(readableArray.getString(i));
                    break;
                case Map:
                    array.put(convertMapToJson(readableArray.getMap(i)));
                    break;
                case Array:
                    array.put(convertArrayToJson(readableArray.getArray(i)));
                    break;
            }
        }
        return array;
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

        //create the support activity
        SupportActivity.Builder builder = new SupportActivity.Builder();

        //parse options
        try {
            JSONObject jsonObject = convertMapToJson(options);
            JSONArray categoriesArray = jsonObject.getJSONArray("withArticlesForCategoryIds");
            long[] data = new long[categoriesArray.length()];
            for(int i=0;i<categoriesArray.length();i++){
                long cat = categoriesArray.getLong(i);
                data[i] = cat;
            }
            builder.withArticlesForCategoryIds(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
            builder.show(activity);
        }
    }

}
