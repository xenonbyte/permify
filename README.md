# permify

[中文说明](README-zh.md)

## Overview
`permify` is an Android permission request library that simplifies runtime permission handling with a clean and intuitive API. It provides an easy way to request permissions and handle different permission scenarios, including rationale UI and permanently denied permissions.

## Features
- Simple API for requesting permissions
- Supports both `Activity` and `Fragment`
- Callback for permission results
- Custom rationale UI support

## Usage

### 1. Simple Permission Request
```java
PermissionRequest.with(activity)
    .addPermissions(Manifest.permission.READ_SMS)
    .onResult((success, grantPerms, denyPerms, denyForeverPerms) -> {
        String msg = success ? "SMS permission granted" : "SMS permission denied";
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    })
    .request();
```

### 2. Permission Request with Rationale UI
If a permission requires an explanation before requesting, you can use `PermissionRationale`.

```java
PermissionRequest.with(fragment)
    .addPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
    .onResult(new PermissionRationale() {
        @Override
        public void showRationaleUI(@NonNull Context context, @NonNull PermissionRationaleHandler callback) {
            // Show a rationale dialog to explain why this permission is needed
            new AlertDialog.Builder(context)
                    .setMessage("This permission is required to provide location services.")
                    .setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            callback.onDenied();
                        }
                    })
                    .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            callback.onAccepted();
                        }
                    })
                    .show();
        }

        @Override
        public void onResult(boolean success, @NonNull List<String> grantPerms, @NonNull List<String> denyPerms, @NonNull List<String> denyForeverPerms) {
            String msg = success ? "Location permission granted" : "Location permission denied";
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    })
    .request();
```

## Download
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.xenonbyte:permify:1.0.0'
}
```

## License

Copyright [2025] [xubo]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

