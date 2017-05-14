# ControlView

ControlView is an alternative to various pickers or using radio buttons/switches. 
It is similar to the UISegmentedControl for iOS. The default styles are raised, flat and flat with border.


### This library is still in BETA, it's API could be changed and might not be fully stable
### If you find any bugs/problems, please raise an issue.


### Features:
- Select between different control options
- Multiple different default styles, or specify custom attributes yourself

### Getting started:

This library is hosted on Jitpack.io, which means to use it you will have to add the following to your root `build.gradle` file.
The minSDK for ControlView is 15.

```gradle
allprojects {
	repositories {
		...
		maven { url "https://jitpack.io" }
	}
}
```

And then you will need to add the following dependency to your applications `build.gradle` file.

```gradle
dependencies {
	compile 'com.github.shalskar:ControlView:v0.1'
}
```

### Usage:

Usage is straightforward, simply add a ControlView to your XML file

```xml
<com.controlview.shalskar.controlview.ControlView
    android:id="@+id/controlview"
    style="@style/ControlView"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"/>
```

And then you can set the control options and listener programmatically

```java
ControlView controlView = (ControlView) findViewById(R.id.controlview);
controlView.setControlOptions(new ArrayList<>(Arrays.asList("Item 1", "Item2")));
controlView.setOnControlOptionSelectedListener(new ControlView.OnControlOptionSelectedListener() {
    @Override
    public void onControlOptionSelected(int position, @NonNull String controlOption) {
        // Do your stuff here
    }
});
```

#### Theming:

ControlView will be themed using your colorAccent by default, however you can change this programatically:

```java
ControlView controlView = (ControlView) findViewById(R.id.controlview);
controlView.setBaseColour(myBaseColour);
controlView.setAccentColour(myAccentColour);
```

Or in xml:

```xml
<com.controlview.shalskar.controlview.ControlView
    android:id="@+id/controlview"
    style="@style/ControlView"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    controlView:baseColor="#123456"
    controlView:accentColor="#654321"/>
```

Or by creating your own style:

```xml
<style name="CustomControlView" parent="ControlVieW">
    <item name="baseColor">#123456</item>
    <item name="accentColor">#654321</item>
</style>

...

<com.controlview.shalskar.controlview.ControlView
    android:id="@+id/controlview"
    style="@style/CustomControlView"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"/>
```

Other customisable attributes include:

```xml
<com.controlview.shalskar.controlview.ControlView
    android:id="@+id/controlview"
    style="@style/ControlView"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    controlView:baseColor="#123456"
    controlView:accentColor="#654321"
    controlView:selectedTextColor="#000000"
    controlView:unselectedTextColor="#ffffff"
    controlView:isRaised="false"
    controlView:hasBorder="true"/>
```

### License

```
Copyright 2016 Vincent Te Tau

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
