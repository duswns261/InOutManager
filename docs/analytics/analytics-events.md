# Analytics Events

이 문서는 InOutManager가 GA4(Firebase Analytics)로 기록하는 핵심 재고 행동 이벤트의 명세다.

이벤트는 `com.cret.inoutmanager.analytics.AnalyticsEvent`에 typed model로 정의되어 있으며, Presentation은 `AnalyticsLogger.log(event)` 계약만 참조한다. Firebase SDK 호출은 `com.cret.inoutmanager.analytics.firebase.FirebaseAnalyticsLogger`에서만 발생한다.

촬영 흐름의 Crashlytics Custom Key 기록은 별도 계약인 `com.cret.inoutmanager.reporting.ProductPhotoCaptureReporter`가 담당하며, GA4 이벤트와는 독립적으로 동작한다. Firebase Crashlytics 호출은 `com.cret.inoutmanager.reporting.firebase.FirebaseProductPhotoCaptureReporter`에서만 발생한다.

## 1. 공통 규칙

- 제품명, 위치, DB id 등 사용자 입력 원문이나 식별 정보는 어떤 event parameter에도 포함하지 않는다.
- 수량은 항상 사전 정의된 구간 문자열(`quantity_range`)로 변환해 전달한다.
- 완료(`_created`, `_completed`, `_deleted`) 이벤트는 해당 UseCase가 예외 없이 반환한 직후에만 기록한다. 취소하거나 UseCase가 예외를 던진 경로에서는 기록하지 않는다.
- 시작(`_started`)/화면(`_viewed`) 이벤트는 단일 UI 콜백 지점에서만 기록해 중복 집계를 방지한다.

## 2. 이벤트 명세

| Event | 발생 시점 | Parameter | 코드 위치 |
|---|---|---|---|
| `product_registration_started` | 입고 화면 App Bar `+` 버튼을 눌러 등록 dialog를 열기 직전 | `entry_point=inbound_app_bar` | `InventoryTopAppBar.kt`의 등록 action `onClick` → `InventoryApp.kt` → `InventoryViewModel.logProductRegistrationStarted()` |
| `product_created` | `AddProductUseCase`가 예외 없이 반환된 직후 | `has_image` (등록에 실제 첨부된 사진 유무), `quantity_range` (등록 수량 기준) | `InventoryViewModel.addProduct()` |
| `product_photo_capture_started` | 카메라 권한 확인 후 촬영 화면(`ProductCameraDialog`)이 열릴 때 | 없음 | `Dialogs.kt`의 `openCamera()` → `InventoryViewModel.logPhotoCaptureStarted()` |
| `product_photo_capture_completed` | 촬영 결과 확인 화면에서 사용자가 "사용하기"를 눌러 사진을 확정할 때 | 없음 | `ProductCameraDialog.kt`의 `onCaptureCompleted` → `InventoryViewModel.logPhotoCaptureCompleted()` |
| `product_photo_capture_failed` | 카메라 권한 거부, `ImageCapture` 실패, 또는 등록 확정 중 이미지 파일 저장/DB 실패 | `failure_reason` | `InventoryViewModel.logPhotoCaptureFailed()` |
| `outbound_started` | 출고 화면에서 상품을 선택해 출고 수량 dialog를 열기 직전 | 없음 | `InventoryApp.kt`의 `onOutboundClick` → `InventoryViewModel.logOutboundStarted()` |
| `outbound_completed` | `DecreaseProductQuantityUseCase`가 예외 없이 반환된 직후 | `quantity_range` (출고한 수량 기준) | `InventoryViewModel.decreaseQuantity()` |
| `product_deleted` | `DeleteProductUseCase`가 예외 없이 반환된 직후 | 없음 | `InventoryViewModel.deleteProduct()` |
| `inventory_screen_viewed` | 자재 현황(Status) route가 활성화될 때 | 없음 | `InventoryApp.kt`의 `LaunchedEffect(currentRoute)` → `InventoryViewModel.logInventoryScreenViewed()` |

## 3. Parameter 허용 값

### `entry_point`

| 값 | 의미 |
|---|---|
| `inbound_app_bar` | 입고 화면 App Bar의 등록 `+` 버튼에서 시작 (현재 UX에서 사용하는 유일한 진입점) |

### `has_image`

| 값 | 의미 |
|---|---|
| `true` | 등록 시 촬영한 사진이 확정되어 `Product.imagePath`와 함께 저장됨 |
| `false` | 사진 없이 등록됨 (권한 거부, 촬영 취소, 사진 제거 등 포함) |

### `failure_reason` (`product_photo_capture_failed`)

| 값 | 의미 |
|---|---|
| `permission_denied` | 카메라 런타임 권한 요청이 거부됨 |
| `capture_error` | CameraX `ImageCapture`가 촬영/저장 중 실패함 |
| `save_error` | 촬영은 성공했으나 등록 확정 중 이미지 파일 확정 또는 DB 저장이 실패함 |

### `quantity_range`

| 값 | 범위 |
|---|---|
| `zero` | 수량 0 |
| `1_10` | 수량 1 ~ 10 |
| `11_50` | 수량 11 ~ 50 |
| `51_plus` | 수량 51 이상 |

`product_created`는 등록한 수량, `outbound_completed`는 그 시점에 출고한 수량을 기준으로 구간을 계산한다 (출고 후 남은 재고 수량이 아니다).

## 4. 중복 방지

- `product_registration_started`, `outbound_started`는 각각 App Bar 등록 `+` `onClick`, 상품 선택 `onClick` 콜백 한 곳에서만 기록한다. Home 화면 이동이나 다른 재구성 경로에서는 기록하지 않는다.
- `inventory_screen_viewed`는 `currentRoute`를 key로 하는 `LaunchedEffect`에서 기록하므로, 같은 route로 유지되는 동안의 Compose 재구성에서는 재기록되지 않는다.
- 완료 이벤트는 UseCase가 실제로 반환된 뒤에만 기록되므로, 사용자가 dialog를 취소하거나 UseCase가 예외를 던지면 기록되지 않는다.
- `product_photo_capture_started`는 카메라 화면이 열릴 때마다(최초 진입, 재촬영 재진입 포함) 기록되고, `product_photo_capture_completed`는 촬영 결과를 "사용하기"로 확정할 때만 기록된다. 재촬영으로 폐기된 촬영에는 completed가 기록되지 않는다.

## 5. DebugView 검증 절차

```bash
adb shell setprop debug.firebase.analytics.app com.cret.inoutmanager
```

1. 위 명령 실행 후 debug 앱을 재실행한다.
2. Firebase Console → DebugView에서 기기를 선택한다.
3. 입고 App Bar `+` 진입 → 등록 완료 → `product_registration_started`, `product_created`(`has_image=false`) 순서로 각 1회 수신되는지 확인한다.
4. 입고 App Bar `+` 진입 → 사진 촬영 → 사용하기 → 등록 완료 → `product_registration_started`, `product_photo_capture_started`, `product_photo_capture_completed`, `product_created`(`has_image=true`) 순서로 각 1회 수신되는지 확인한다.
5. 카메라 권한을 거부한 뒤 → `product_photo_capture_failed`(`failure_reason=permission_denied`)가 수신되고, 이후 사진 없이 등록해 `product_created`(`has_image=false`)가 정상 수신되는지 확인한다.
6. 출고 화면에서 상품 선택 → 수량 입력 후 출고 확인 → `outbound_started`, `outbound_completed` 순서로 각 1회 수신되는지 확인한다.
7. 자재 현황 진입 → `inventory_screen_viewed` 수신을 확인한다. 상품을 길게 눌러 삭제를 확인하면 `product_deleted` 수신을 확인한다.
8. 각 event의 parameter 값이 위 명세와 일치하고 제품명/위치/id/파일 경로가 없는지 확인한다.
9. 등록/출고/삭제 dialog를 취소했을 때 대응 완료 event가 기록되지 않는지 확인한다.
10. 검증이 끝나면 DebugView 모드를 해제한다.

```bash
adb shell setprop debug.firebase.analytics.app .none.
```

## 6. Crashlytics Custom Key 검증 절차

촬영 흐름 상태는 GA4 이벤트와 별도로 Crashlytics Custom Key(`product_photo_capture_state`, `product_photo_failure_reason`)에도 기록된다.

1. 실기기에서 사진 촬영을 포함한 등록 흐름을 진행하다가 의도적으로 비정상 종료(강제 종료 또는 `CrashlyticsTestActivity`의 강제 crash 버튼)를 발생시킨다.
2. Firebase Console → Crashlytics → 해당 crash report의 Keys 탭에서 `product_photo_capture_state`, `product_photo_failure_reason` 값이 직전 촬영 상태와 일치하는지 확인한다.
3. 등록 dialog를 취소하거나 등록을 성공적으로 마친 뒤 새로 crash를 발생시켜, 두 key가 `idle`/`none`으로 복원되어 있는지 확인한다.
4. 두 key의 값에 제품명, 위치, 파일 경로, 예외 메시지 등 사용자 입력이나 식별 정보가 포함되지 않는지 확인한다.
