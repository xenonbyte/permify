# permify

`permify` 是一个 Android 权限请求库，提供简单、灵活的 API 以处理运行时权限和特殊权限请求

## 特性

- **链式 API**：便捷地添加权限并设置回调
- **自动权限管理**：处理权限请求及结果
- **支持权限说明 UI**：可在权限请求前弹出自定义提示对话框
- **兼容性强**：支持 `Activity` and `Fragment`
- **支持特殊权限**:  

  - `SYSTEM_ALERT_WINDOW`  

  - `WRITE_SETTINGS`  

  - `MANAGE_EXTERNAL_STORAGE`  

  - `REQUEST_INSTALL_PACKAGES`  

## 快速开始

### 申请权限

```java
PermissionRequest.with(activity)
        .addPermissions(Manifest.permission.CAMERA)
        .onResult((success, granted, denied, deniedForever) -> {
            if (success) {
                Toast.makeText(context, "相机权限授予成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "相机权限请求失败", Toast.LENGTH_SHORT).show();
            }
        })
        .request();
```

### 使用权限说明 UI（PermissionRationale）

如果需要在请求权限前向用户解释权限用途，可以使用 `PermissionRationale`：

```java
PermissionRequest.with(fragment)
        .addPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
        .onResult(new PermissionRationale() {
            @Override
            public void showRationaleUI(@NonNull Context context, @NonNull PermissionRationaleHandler handler) {
                new AlertDialog.Builder(context)
                        .setMessage("需要位置权限以提供更好的服务")
                        .setNegativeButton("拒绝", (dialog, which) -> handler.onDenied())
                        .setPositiveButton("允许", (dialog, which) -> handler.onAccepted())
                        .show();
            }

            @Override
            public void onResult(boolean success, @NonNull List<String> granted, @NonNull List<String> denied, @NonNull List<String> deniedForever) {
                if (success) {
                    Toast.makeText(context, "位置权限授予成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "位置权限请求失败", Toast.LENGTH_SHORT).show();
                }
            }
        })
        .request();
```

## Download

在 `build.gradle` 中添加：

```gradle
dependencies {
    implementation 'com.github.xenonbyte:permify:1.0.1'
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

