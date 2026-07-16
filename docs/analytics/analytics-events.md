# Analytics Events

이 문서는 InOutManager가 GA4(Firebase Analytics)로 기록하는 핵심 재고 행동 이벤트의 명세다.

이벤트는 `com.cret.inoutmanager.analytics.AnalyticsEvent`에 typed model로 정의되어 있으며, Presentation은 `AnalyticsLogger.log(event)` 계약만 참조한다. Firebase SDK 호출은 `com.cret.inoutmanager.analytics.firebase.FirebaseAnalyticsLogger`에서만 발생한다.

## 1. 공통 규칙

- 제품명, 위치, DB id 등 사용자 입력 원문이나 식별 정보는 어떤 event parameter에도 포함하지 않는다.
- 수량은 항상 사전 정의된 구간 문자열(`quantity_range`)로 변환해 전달한다.
- 완료(`_created`, `_completed`, `_deleted`) 이벤트는 해당 UseCase가 예외 없이 반환한 직후에만 기록한다. 취소하거나 UseCase가 예외를 던진 경로에서는 기록하지 않는다.
- 시작(`_started`)/화면(`_viewed`) 이벤트는 단일 UI 콜백 지점에서만 기록해 중복 집계를 방지한다.

## 2. 이벤트 명세

| Event | 발생 시점 | Parameter | 코드 위치 |
|---|---|---|---|
| `product_registration_started` | Inbound 화면 FAB를 눌러 등록 dialog를 열기 직전 | `entry_point=inbound_fab` | `InventoryApp.kt`의 FAB `onClick` → `InventoryViewModel.logProductRegistrationStarted()` |
| `product_created` | `AddProductUseCase`가 예외 없이 반환된 직후 | `has_image=false`, `quantity_range` (등록 수량 기준) | `InventoryViewModel.addProduct()` |
| `outbound_started` | 출고 화면에서 상품을 선택해 출고 수량 dialog를 열기 직전 | 없음 | `InventoryApp.kt`의 `onOutboundClick` → `InventoryViewModel.logOutboundStarted()` |
| `outbound_completed` | `DecreaseProductQuantityUseCase`가 예외 없이 반환된 직후 | `quantity_range` (출고한 수량 기준) | `InventoryViewModel.decreaseQuantity()` |
| `product_deleted` | `DeleteProductUseCase`가 예외 없이 반환된 직후 | 없음 | `InventoryViewModel.deleteProduct()` |
| `inventory_screen_viewed` | 자재 현황(Status) route가 활성화될 때 | 없음 | `InventoryApp.kt`의 `LaunchedEffect(currentRoute)` → `InventoryViewModel.logInventoryScreenViewed()` |

## 3. Parameter 허용 값

### `entry_point`

| 값 | 의미 |
|---|---|
| `inbound_fab` | 입고 화면의 등록 FAB에서 시작 (현재 UX에서 사용하는 유일한 진입점) |

### `has_image`

이번 Issue에서는 항상 `false`로 고정한다. 실제 이미지 첨부 기능은 Milestone 4 카메라 기능 Issue에서 `has_image=true` 경로를 추가한다.

### `quantity_range`

| 값 | 범위 |
|---|---|
| `zero` | 수량 0 |
| `1_10` | 수량 1 ~ 10 |
| `11_50` | 수량 11 ~ 50 |
| `51_plus` | 수량 51 이상 |

`product_created`는 등록한 수량, `outbound_completed`는 그 시점에 출고한 수량을 기준으로 구간을 계산한다 (출고 후 남은 재고 수량이 아니다).

## 4. 중복 방지

- `product_registration_started`, `outbound_started`는 각각 FAB `onClick`, 상품 선택 `onClick` 콜백 한 곳에서만 기록한다. Home 화면 이동이나 다른 재구성 경로에서는 기록하지 않는다.
- `inventory_screen_viewed`는 `currentRoute`를 key로 하는 `LaunchedEffect`에서 기록하므로, 같은 route로 유지되는 동안의 Compose 재구성에서는 재기록되지 않는다.
- 완료 이벤트는 UseCase가 실제로 반환된 뒤에만 기록되므로, 사용자가 dialog를 취소하거나 UseCase가 예외를 던지면 기록되지 않는다.

## 5. DebugView 검증 절차

```bash
adb shell setprop debug.firebase.analytics.app com.cret.inoutmanager
```

1. 위 명령 실행 후 debug 앱을 재실행한다.
2. Firebase Console → DebugView에서 기기를 선택한다.
3. 입고 FAB 진입 → 등록 완료 → `product_registration_started`, `product_created` 순서로 각 1회 수신되는지 확인한다.
4. 출고 화면에서 상품 선택 → 수량 입력 후 출고 확인 → `outbound_started`, `outbound_completed` 순서로 각 1회 수신되는지 확인한다.
5. 자재 현황 진입 → `inventory_screen_viewed` 수신을 확인한다. 상품을 길게 눌러 삭제를 확인하면 `product_deleted` 수신을 확인한다.
6. 각 event의 parameter 값이 위 명세와 일치하고 제품명/위치/id가 없는지 확인한다.
7. 등록/출고/삭제 dialog를 취소했을 때 대응 완료 event가 기록되지 않는지 확인한다.
8. 검증이 끝나면 DebugView 모드를 해제한다.

```bash
adb shell setprop debug.firebase.analytics.app .none.
```
