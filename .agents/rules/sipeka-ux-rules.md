---
trigger: always_on
---

# MASTER UX & INTERACTION RULES: SIPeKa (School PKK Business App)
**Target Agent**: Antigravity (Jetpack Compose Code Generator)
**Core Goal**: Premium tactile feel, foolproof interactions, smooth transitions, and high usability.

When implementing logic and interactions in Jetpack Compose, you MUST adhere to these 5 UX constraints:

## 1. TOUCH TARGETS & TACTILE FEEDBACK (Ergonomics)
- **Minimum Tap Size:** ALL clickable elements (Icons, Buttons, Chips) MUST have a minimum touch target size of `48.dp` x `48.dp`. Use `Modifier.defaultMinSize()` if the visual element is smaller.
- **Ripples & Clicks:** Do NOT disable the default Material ripple effect unless specifically asked. Ensure `Modifier.clickable` is placed at the highest appropriate level of the component (e.g., on the `Card` itself, not just the text inside it).
- **Debounce / Anti-Spam:** For critical actions (Checkout, Pay, Delete), ensure the button is disabled or shows a loading state immediately after the first click to prevent double-submission.

## 2. SMART KEYBOARD & INPUT HANDLING (Forms UX)
- **Keyboard Visibility:** ALL screens containing `TextField` must include `Modifier.imePadding()` on the root layout or use a scrollable state so the UI pushes up naturally and the input field is never hidden by the soft keyboard.
- **Contextual Keyboards:** 
  - For Prices, NISN/NIP, and Quantities: ALWAYS use `keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)`.
  - For Passwords: Use `KeyboardType.Password` with visual transformation.
- **IME Actions:** Use `ImeAction.Next` for the first fields and `ImeAction.Done` for the final field to allow fast, seamless form filling.

## 3. PERCEIVED PERFORMANCE & SKELETON LOADERS
- **Zero Blocking Spinners:** Avoid using full-screen, blocking `CircularProgressIndicator` that freezes the user's view. 
- **Skeleton/Shimmer Loading:** For fetching lists (Products, Queue, Analytics), structure the code to support a Shimmer/Skeleton effect. Display blank, greyed-out rounded rectangles holding the shape of the data while `isLoading == true`.
- **Async Images:** When using `AsyncImage` (e.g., Coil), ALWAYS provide a `placeholder` (a light grey rounded box) and an `error` state (a broken-image vector) to prevent layout jumping when images load.

## 4. GRACEFUL EMPTY STATES (Anti-Blank Screens)
- **No Raw Data:** Never show a completely blank screen or raw JSON/Exception text when a list is empty or fails to load.
- **Actionable Empty States:** If a LazyList is empty, display a centralized `Column` containing:
  1. A Lottie Animation container (prepare the code for `LottieAnimation`).
  2. Clear, empathetic text (e.g., "Belum ada pesanan masuk hari ini.").
  3. A Call-To-Action (CTA) button to recover or explore (e.g., "Kembali ke Beranda" or "Muat Ulang").

## 5. MICRO-INTERACTIONS & SUCCESS STATES
- **BottomSheet over Dialogs:** For quick actions (choosing payment method, viewing order details), prefer `ModalBottomSheet` over standard `AlertDialog`. Bottom sheets feel more modern, reachable (thumb-friendly), and less intrusive.
- **Feedback States:** When a Kasir completes an order, provide a brief visual success state (e.g., a green checkmark or a Snackbar toast) before removing the item from the queue.