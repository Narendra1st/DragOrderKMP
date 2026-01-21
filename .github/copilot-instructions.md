# DragOrderKMP - AI Coding Assistant Instructions

## Project Overview
**DragOrderKMP** is a Kotlin Multiplatform Mobile (KMP) application for managing restaurant orders with drag-and-drop functionality. It targets Android and iOS, sharing business logic through a common module while maintaining platform-specific UIs.

### Architecture
- **shared/** - Kotlin Multiplatform library with cross-platform models
- **androidApp/** - Android app using Jetpack Compose for UI
- **iosApp/** - iOS app using SwiftUI (native development)

## Core Domain Model
Three essential data classes (in `shared/src/commonMain/kotlin/com/example/dragorderkmp/`):
- `Order` - Restaurant order with id, name, table number, line items, qty, total
- `OrderItem` - Menu item: name, price, imageUrl (emoji), qty
- Both are simple data classes shared across platforms

## State Management & ViewModels
- **OrderViewModel** (Android-specific, in `androidApp/src/main/java/com/example/dragorderkmp/android/viewmodel/`) manages:
  - Order list state via `mutableStateListOf<Order>()`
  - Selection state: `selectedOrderId`, `selectedTable`, `selectedDropArea`
  - UI overlays: `showPayPopup`, `showPopup`
  - Drag-drop areas via `orderDropAreas: Map<String, Rect>`
- Loads persisted orders on init via `OrderStorage.load()`
- State flows directly into Compose recomposition

## Persistence & Storage
- **OrderStorage** singleton (in `androidApp/src/main/java/com/example/dragorderkmp/android/storage/`) uses:
  - Android SharedPreferences for persistence
  - Gson for JSON serialization of Order lists
  - Key: "ORDERS" in "order_app" preference file
  - Manually invoked (no automatic sync) - call `OrderStorage.save(context, orders)` after mutations

## UI Architecture
- **Compose-based** screens organized by feature:
  - `DragOrderScreen.kt` - Main drag-drop interface (left: item catalog, right: order table view)
  - `OrderList.kt` - Order list display logic
  - `BillSection.kt` - Payment summary UI
  - `CommonTable.kt` - Table selection UI
  - `CreateOrderPopup.kt`, `PayPopup.kt` - Modal dialogs

### Drag-Drop Implementation
- `DragOrderScreen` captures `Rect` boundaries via `onGloballyPositioned` for drop targets
- Uses `pointerInput { awaitEachGesture { awaitFirstDown() } }` for drag detection
- Position tracking via `positionInWindow()` and `positionChange()`
- Toast notifications for user feedback (view `DragOrderScreen` lines ~150-200)

## Navigation
- Android: Compose Navigation with `NavHost`
  - Route `"home"` → DragOrderScreen
  - Route `"detail/{id}"` → PersonDetailScreen (order detail view)
- iOS: SwiftUI native (separate implementation in iosApp/)

## Dependencies & Build
- **Versions** (in `gradle/libs.versions.toml`):
  - Kotlin 1.9.20, AGP 8.1.0
  - Compose 1.5.4, Material3 1.1.2
  - AndroidX Activity Compose 1.8.0
- **Build Targets**:
  - Android: minSdk 24, compileSdk 34, JVM target 1.8
  - iOS: Framework format (isStatic=true) targeting x64, arm64, simulatorArm64
- **Gradle Features**:
  - Configuration cache enabled (gradle.properties)
  - Gradle daemon JVM args: `-Xmx2048M`
  - Official Kotlin code style enforced

## Key Developer Workflows
### Build
```bash
# Full build
./gradlew build

# Android only
./gradlew androidApp:build

# Shared module
./gradlew shared:build

# With configuration cache (faster)
./gradlew build --configuration-cache
```

### Run Android
```bash
# Install and run on emulator/device
./gradlew androidApp:installDebug
./gradlew run  # if run task configured
```

### Package
- Android: `.apk` outputs in `androidApp/build/outputs/apk/`
- iOS: Framework in `shared/build/` (consumed by Xcode)

## Code Conventions
1. **Multiplatform Patterns**:
   - Common code in `commonMain`, platform-specific in `androidMain`/`iosMain`
   - Platform interfaces in `shared/src/commonMain/kotlin/Platform.kt`, implementations in `Platform.android.kt`/`Platform.ios.kt`

2. **Compose Style**:
   - Prefer `mutableStateOf`, `mutableStateListOf`, `mutableStateMapOf` for reactive state
   - Use factory pattern for ViewModels requiring Context (see DragOrderScreen ViewModel creation)
   - Modifiers flow left-to-right; padding/sizing near end

3. **Android Specifics**:
   - SharedPreferences via context, always wrap in `getSharedPreferences("app_scope", MODE_PRIVATE)`
   - Use `viewModel { factory }` for constructor dependency injection
   - Toast notifications: `Toast.makeText(context, msg, Toast.LENGTH_SHORT).apply { setGravity(Gravity.BOTTOM, 0, 100) }.show()`

4. **Naming**:
   - ViewModels: `*ViewModel` suffix
   - UI Screens: `*Screen` suffix
   - Dialogs/Popups: `*Popup` suffix
   - Storage/Persistence: `*Storage` suffix

## Common Tasks
- **Add new menu item**: Add to `availableItems` list in `DragOrderScreen.kt` (line ~48)
- **Persist order changes**: Call `OrderStorage.save(context, vm.orders)` after mutation
- **Add new route**: Add `composable()` entry in `MainActivity` NavHost
- **Style changes**: Modify `Color(0xFF...)` values and `RoundedCornerShape` parameters in UI composables

## Debugging Tips
- Drag-drop coordinates logged via visual Rect bounds; check `onGloballyPositioned` blocks
- State mutations: Watch `OrderViewModel` mutable state fields for reference vs value semantics
- SharedPreferences: Verify `.apply()` is called (not just `.edit()`)
- Compose recomposition: Ensure state observers (vm fields) are properly captured in lambda scopes
