---
trigger: always_on
---

# MASTER UI/UX RULES: SIPeKa (School PKK Business App)
**Target Agent**: Antigravity (Jetpack Compose Code Generator)
**Core Vibe**: Modern Retail-Tech (Gojek/GoBiz/Apple Wallet), Premium, Bento-box layout.
**Strict Protocol**: ZERO AI SLOP. Do not hallucinate gradients, weird 3D shadows, or complex nested scroll logic.

When generating ANY Jetpack Compose screen for this project, you MUST strictly adhere to the following 5 constraints:

## 1. SPATIAL GRID ENFORCER (The 8pt Rule)
- You are strictly forbidden from using arbitrary padding or spacing (e.g., 5.dp, 10.dp, 15.dp).
- ALL `padding`, `Arrangement.spacedBy()`, and `size` values MUST be multiples of 8 (`8.dp`, `16.dp`, `24.dp`, `32.dp`).
- Exception: For micro-spacing inside a small component, `4.dp` is permitted.

## 2. SHAPE & FLAT ELEVATION (Bento-Box Protocol)
- Use Material 3 standard components but with HIGH corner radii.
- All `Card`, `Button`, and `OutlinedTextField` components MUST use `RoundedCornerShape(16.dp)` or `RoundedCornerShape(24.dp)`. NO sharp corners (0.dp) unless it's a BottomSheet touching the screen edge.
- **ZERO AI SLOP SHADOWS:** Do NOT use the custom `.shadow()` modifier with extreme blur or colored shadows. Use standard `CardDefaults.cardElevation(defaultElevation = 2.dp)` for a clean, premium flat look. Backgrounds must be solid.

## 3. STRICT COLOR TOKENS
- NEVER hardcode hex colors (e.g., `Color(0xFF...)`) in the UI screen files.
- You must ONLY use the exact tokens defined in the SIPeKa Design System (`Color.kt`):
  - App/Screen Background: `BgLightCanvas`
  - Headers & Primary Text: `BgDarkEspresso`
  - Primary Action Buttons: `BtnDarkChocolate`
  - Card Backgrounds: `CardCreamWhite`
  - Toggles/Active Chips: `BgWarmTan`
  - FABs & Alerts: `VibrantOrange`
  - Success/Laba: `GreenSuccess`

## 4. UNIFIED SCROLL & EDGE-TO-EDGE SAFETY (Crucial Architecture)
- **NO NESTED SCROLLING:** NEVER nest a `LazyColumn` inside a `Column` that uses `.verticalScroll()`. Every scrollable screen MUST use a single root `LazyColumn` or `LazyVerticalGrid`. Elements like Headers, Search Bars, and Tabs must be items within that root Lazy container (e.g., using `item { }` or `GridItemSpan`).
- **Camera Cutout Safety:** Always apply `Modifier.statusBarsPadding()` to the topmost components (Headers, Search Bars) so they are not hidden behind the device camera notch.
- **Thumbzone & Nav Bar Safety:** For any screen containing a `FloatingActionButton` or `BottomNavigationBar`, you MUST apply `contentPadding = PaddingValues(bottom = 120.dp)` to the root LazyList so the last items can be scrolled completely into view.

## 5. TYPOGRAPHY & READABILITY
- Do not make text overly small. Minimum description size is `14.sp`.
- Use `FontWeight.Bold` prominently for Product Names, Prices, and Section Titles.
- For "Empty States", provide a clean placeholder `Column` with `Arrangement.Center` and a prompt to use a `LottieAnimation` instead of a blank screen.