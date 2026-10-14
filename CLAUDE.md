# Equal AI Design System: instructions for Claude

This repo is the Equal AI design system for **web (React)** and **Android (Jetpack Compose only)**.
It covers tokens, components, screens, flows and the docs site. Read this file before you design or
build anything here. The catalog of every component, prop, token, asset, icon and flow is in
`site/public/llms-full.txt`, which `npm run llms` regenerates. Search it first; reuse before you add.
The same catalog is an MCP server (tools `search`, `get`, `list`) at https://equal-ds.vercel.app/mcp,
or locally with `npm run mcp`. `npm run llms` also rebuilds its index (`api/_index.json`).

## The rules (these always apply)

The reason behind every rule is in `site/src/data/decisions.ts` (the site’s Rules & decisions page).
When a design and the system disagree, check it first; when a rule changes, update it there too.

**Type and colour**
- **Typography:** DM Sans only, through the 12 type tokens (`<Text variant>` / `EqualTheme.type`).
  Nothing is set in Lora or any other font. Weights: 400, 500, 600 (SemiBold) and 700.
- **Colour:** only tokens. Never put a hex value in a component.
  - **New colour:** add it to `tokens/tokens.json` with `"derived": true` and a `"why"`, give it light
    and dark values, and check text pairs for AA (4.5:1). Decide colours yourself; don't ask for
    sign-off.
- **Dark mode:** every component and screen works in light and dark. In dark, every green is lime
  (#BAFF29): accents use lime itself, green-tinted surfaces are re-hued to lime, and anything on lime
  is `text/on-action` (black), never white.

**Icons and images**
- **Action cards:** the grey body sits on a white card (never a green card), and every CTA row is
  emoji + label (📤 Send, ✏️ Edit, 👍 Confirm, 🗑️ Delete).
- **Icons:** flat icons are Material Symbols (`scripts/build-icons.mjs`, then `npm run icons`). Action
  chips use emoji, sized to the label font size.
- **Images:** the only images are supplied assets in `assets/`. Everything else is code: logos, seals,
  patterns, rings, progress bars, badges. Never crop an image; trimming transparent margins is fine.
- **Premium (monetization) surfaces:** perk icons are *filled* Material Symbols at 16 dp, painted
  with the gold radial gradient `premium-icon/start` → `premium-icon/end` (`<PremiumIcon>` /
  `PremiumIcon`). Light premium cards sit on the `premium-glow` art; dark ones use the coded
  black-gold gradient. Gold CTAs use `gradient/cta-gold`. Most monetization values (price, coupon, copy, entry point) are experiments: keep them as props.
- **Mascots:** the green mascot illustrations are generated to order. When a screen needs a new one,
  ask for it and show a placeholder.

**Android frame and chrome**
- **Frame:** 390 × 800 dp. Extend only vertically (`StagePhone.height` / `ANDROID_FRAME` on web,
  `EqualFrameWidthDp` / `EqualFrameHeightDp` in Compose).
- **Status bar:** Material 3, 52 dp. Text fields have 12 dp corners.
- **Avatars:** free users get the plain bust; paid users (Unlimited) get the gold background and ring
  (`tier: 'free' | 'paid'`). Saved contacts show their initials on the light-blue disc with the person
  badge at the bottom-right (the signifier for a known contact); unknown and business callers use
  category art.
- **The green orb** (nav FAB) opens the assistant sheet (Global MYA, `AssistantSheet`) on any screen.
  Requests become a short task conversation that ends in a result to confirm. Don't draw the system
  keyboard; just lift the input above it.
- **Call Handling** lives under You → Your assistant. Ask for a permission only at the step that
  needs it. Android draws notifications: `CallNotification` only fills the shell (who, what they want,
  Takeover first), and the wallpaper behind it in docs is coded.
- **Dark-mode You tab:** the cream-gold profile header stays light (its content renders with light
  tokens) as a panel with rounded bottom corners; the status bar sits on the gold.

- **Text inputs that can send:** show the mic while empty and the send arrow as soon as there is text.

**Engineering**
- **Stack:** business logic is Kotlin, UI is Compose, and animations are Lottie JSON (`assets/lottie/`,
  `<Lottie>` / `EqualLottie`). Respect reduced motion everywhere.
- **Experiments are props, not forks.** The Android team A/B tests monetization, so price, copy,
  coupon, entry touchpoint and similar knobs are props on the same screen.
- **Paywall experiments** (`MonetizationExperiments.tsx`): touchpoints and payment states are props on
  Home / Chat / Paywall (`planExpired`, `homeNotice`, `official`, `lockedActions`, `paywallSheet`,
  `paymentProcessing`, `PaymentResultScreen`). While a payment is processing, every surface says so
  and the paywall CTA is disabled.

## Where things live

| What | Web | Android |
|---|---|---|
| Tokens (generated) | `web/src/tokens/` | `compose/.../tokens/EqualTokens.kt` |
| Components | `web/src/components/<Name>.tsx` + `.css`, exported from `web/src/index.ts` | `compose/equal-ds/src/main/java/ai/equal/ds/components/` |
| Screens | `web/src/screens/*.tsx` | `compose/.../catalog/*.kt` |
| Assets | `assets/<name>.webp\|svg` + `web/src/foundations/Asset.tsx` | `res/drawable-xxxhdpi/ds_asset_*.webp`, `EqualAssets` |
| Docs page per component | `site/src/registry/components.tsx` | snippets in `compose/equal-ds/src/debug/java/ai/equal/ds/snippets/` |
| Flow pages | `site/src/pages/Flow.tsx` + `site/src/app/nav.ts` + `site/src/pages/Screens.tsx` | snippet group `flow-<id>` |

## Recipe: a new flow from a design (PDF, Figma frames or screenshots)

1. **Read the design fully.** Render PDFs at high resolution (`pdftoppm -r 100`) and crop each region.
   List every screen state, every component and every image.
2. **Map it to the system.** For each element, find the existing component in `llms-full.txt`. Only
   build a new component when nothing fits, or extend one with a prop.
3. **List the assets.** Split them into images to supply (3D art, illustrations, mascots) and things to
   code. Ask the designer for any images that are missing. Until they arrive, `<Asset>` shows a
   checkerboard placeholder.
4. **Add assets.**
   - PNG/WebP: a transparent file at least 2000 px into `assets/source/<name>.png`; add its display size
     to `SIZES` in `scripts/build-assets.py`; run `npm run assets`.
   - Register it in `Asset.tsx` (`assetNames` + `assetUsage`) and in `EqualAssets`.
   - SVG: `assets/<name>.svg` for web, and a VectorDrawable at `res/drawable/ds_asset_<name>.xml` for Android.
5. **Tokens:** add any new colours or gradients to `tokens/tokens.json`, then run `npm run tokens`.
6. **Web components:** add a `.tsx` + `.css` pair using only tokens, export it, and give it a docs entry in
   `site/src/registry/components.tsx`: examples with `code`, props, tokens, and do / don't. Add its
   anatomy (parts in reading order) and accessibility (Role, Name, Keyboard, Touch target, Contrast,
   State, Motion, for web and Android, plus known gaps) to `site/src/registry/a11y.json`.
7. **Screens:** add props to the revamped screens, or a new screen in `web/src/screens/`.
8. **Flow page:** add the flow to `Flow.tsx`, with states (`key`, `label`, `render`, `code`, and
   `height` when the screen is taller than 800), plus steps and the source. Add it to `nav.ts` and the
   Screens overview cards.
9. **Android:** mirror every component and screen in Compose. Add a snippet for every docs example and
   flow state: `// snippet: <doc id> / <example title>` or `// snippet: flow-<id> / <state key>`.
10. **Verify** with the commands below, and look at every state in light and dark on the site.

## Commands

```bash
npm run site          # docs site at http://localhost:5190 (tokens + snippets + llms first)
npm run tokens        # tokens.json → web + Compose
npm run icons         # icon manifest → web + Compose
npm run assets        # assets/source → web webp + Compose drawable-xxxhdpi
npm run llms          # regenerate site/public/llms*.txt and the Claude skill zip
npx tsc --noEmit      # web typecheck
node scripts/build-snippets.mjs --check   # every docs example has Compose code
cd compose && JAVA_HOME=/opt/homebrew/opt/openjdk@17 ANDROID_HOME=/opt/homebrew/share/android-commandlinetools ./gradlew :equal-ds:compileDebugKotlin :equal-ds:assembleRelease :equal-ds:lintDebug
```

Done means: typecheck clean, `--check` passes, Gradle BUILD SUCCESSFUL, and lint reports
"0 errors, 0 warnings" in `compose/equal-ds/build/reports/lint-results-debug.txt`.
