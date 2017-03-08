# React Native Android Zendesk Helpcenter
##Installation
1. `cd path/to/project`
1. `npm install --save `
2. add to settings

```
include ':app'

include ':react-native-android-zendesk-helpcenter'
project(':react-native-android-zendesk-helpcenter').projectDir = new File(settingsDir, '../node_modules/react-native-android-zendesk-helpcenter/android')

```

4. add to `app/build.gradle`:

```
apply plugin: "com.android.application"

import com.android.build.OutputFile
apply from: "../../node_modules/react-native/react.gradle"
def enableSeparateBuildPerCPUArchitecture = false
def enableProguardInReleaseBuilds = false

//ZENDESK add [begin]:
repositories {
    maven { url 'https://zendesk.jfrog.io/zendesk/repo' }
}
//ZENDESK add [end]:

android {
    compileSdkVersion 24 //<------- ZENDESK adjust
    buildToolsVersion "23.0.1"

    defaultConfig {
        applicationId "com.test2"
        minSdkVersion 16
        targetSdkVersion 22
        versionCode 1
        versionName "1.0"
        ndk {
            abiFilters "armeabi-v7a", "x86"
        }
    }
    splits {
        abi {
            reset()
            enable enableSeparateBuildPerCPUArchitecture
            universalApk false  // If true, also generate a universal APK
            include "armeabi-v7a", "x86"
        }
    }
    buildTypes {
        release {
            minifyEnabled enableProguardInReleaseBuilds
            proguardFiles getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro"
        }
    }
    // applicationVariants are e.g. debug, release
    applicationVariants.all { variant ->
        variant.outputs.each { output ->
            // For each separate APK per architecture, set a unique version code as described here:
            // http://tools.android.com/tech-docs/new-build-system/user-guide/apk-splits
            def versionCodes = ["armeabi-v7a":1, "x86":2]
            def abi = output.getFilter(OutputFile.ABI)
            if (abi != null) {  // null for the universal-debug, universal-release variants
                output.versionCodeOverride =
                        versionCodes.get(abi) * 1048576 + defaultConfig.versionCode
            }
        }
    }
}

dependencies {
    compile fileTree(dir: "libs", include: ["*.jar"])
    compile "com.android.support:appcompat-v7:23.0.1"
    compile "com.facebook.react:react-native:+"  // From node_modules

    //ZENDESK add [begin]:
    compile (group: 'com.zendesk', name: 'sdk', version: '1.9.0.1'){
        exclude group: 'com.squareup.okhttp3', module: 'okhttp'
    }
    compile project(':react-native-android-zendesk-helpcenter')
    //ZENDESK add [end]:    
}

// Run this once to be able to run the application with BUCK
// puts all compile dependencies into folder libs for BUCK to use
task copyDownloadableDepsToLibs(type: Copy) {
    from configurations.compile
    into 'libs'
}

```    

5. add to MainApplication of your project

```
public class MainApplication extends Application implements ReactApplication {

  private final ReactNativeHost mReactNativeHost = new ReactNativeHost(this) {
    @Override
    public boolean getUseDeveloperSupport() {
      return BuildConfig.DEBUG;
    }

    @Override
    protected List<ReactPackage> getPackages() {
      return Arrays.<ReactPackage>asList(
          new MainReactPackage(), 
              new ZendeskHelper()
      );
    }
  };

  @Override
  public ReactNativeHost getReactNativeHost() {
    return mReactNativeHost;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    SoLoader.init(this, /* native exopackage */ false);
  }
}
```

##USAGE:

You must pass an intializing object, user name, and email into the `show` method. Configs can be created/found [here](https://poynt.zendesk.com/agent/admin/mobile_sdk):

```
var options = {
      url:"https://poynt.zendesk.com", 
      appId:"yourAppId", 
      clientId:"yourClientId",
      name:"Eric M",
      email:"eric@poynt.co"
    }
ZendeskHelpCenter.show(options)
```


####Working example
```
/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import {
  AppRegistry,
  StyleSheet,
  Text,
  TouchableOpacity,
  View
} from 'react-native';

import ZendeskHelpCenter from 'react-native-android-zendesk-helpcenter'

export default class test2 extends Component {
  onTap(){
      
    var options = {
      url:"https://poynt.zendesk.com", 
      appId:"XXXX", 
      clientId:"XXXX",
      name:"Eric M",
      email:"eric@poynt.co"
    }
    ZendeskHelpCenter.show(options)    
  }

  render() {
    return (
      <View style={styles.container}>
        <TouchableOpacity
        onPress={this.onTap.bind(this)}
        >
          <Text style={styles.welcome}>
          Welcome to React Native!
        </Text>
        </TouchableOpacity>

        <Text style={styles.instructions}>
          To get started, edit index.android.js
        </Text>
        <Text style={styles.instructions}>
          Double tap R on your keyboard to reload,{'\n'}
          Shake or press menu button for dev menu
        </Text>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
});

AppRegistry.registerComponent('test2', () => test2);
```

##TODO
1. THEMEING!!!