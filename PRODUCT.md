# Product

<!-- impeccable:product-schema 1 -->

## Platform

web

## Stack

Product (Android): Kotlin for business logic, Jetpack Compose for UI, Lottie JSON for animations.

This repo: React 18 + TypeScript + Vite. The documentation site lives in `site/` and imports the design system's real components, tokens and screens from `web/src`. It builds to static files. Storybook has been retired; this site replaces it.

## Users

Equal's internal product team: designers, Android (Jetpack Compose) and web engineers, and PMs who build flows for the Equal AI app. They come to look something up in the middle of a task: which token, which component, what spacing, what copy rule. Then they copy code or a spec and go back to their editor or Figma.

## Product Purpose

The single reference for the revamped Equal AI app's design system: colour primitives and alias tokens (light and dark), the 12-token DM Sans type scale, spacing, radius, elevation, Material Symbols icons, action emoji, caller category icons, logo and orb, mascot, avatars, the chat wallpaper, every component, and the full screens and flows. Success means a team member finds the right answer in seconds, trusts it because every example is the real shipped component, and never has to ask a designer "which green?"

## Positioning

Every example on the site is the real component from `web/src`, rendered live in light and dark, and backed by one token source (`tokens/tokens.json`) that also generates the Compose theme. The site cannot drift from the code, and web and Android cannot drift from each other.

## Operating Context

- Source designs in `/Users/arnavraj/Downloads/DS` (colour guides, typography specs, dev handoff, You tab, flows) are the design authority. `SPEC.md` is the component contract.
- Users switch between the site, Figma, Android Studio and a code editor. Copy-paste of token names, CSS variables, TS and Kotlin identifiers is a core job.
- Android screens are designed in a 360 × 800dp frame (mdpi, 1px = 1dp = 1sp). Start every screen at 360 × 800 and extend only the vertical side when content needs it; never change the width. Code: `ANDROID_FRAME` (web) and `EqualFrameWidthDp` / `EqualFrameHeightDp` (Compose). The designs are drawn at 360 wide, so their dp values (paddings, component sizes) carry over unchanged.

## Capabilities and Constraints

- Monetization is experimented on in Android (multiple paywall touchpoints, prices, coupons, copy). The system models every experiment knob as a prop on the shared components and screens, never as a forked component.

- Tokens: `tokens/tokens.json` → `npm run tokens` generates `web/src/tokens/tokens.css|ts` and Compose `EqualTokens.kt`. Tokens marked `derived` come from the screens rather than the colour guide; their values are final and text pairs pass WCAG AA.
- Icons: `scripts/build-icons.mjs` generates web + Compose icons from Material Symbols (Outlined 400) and simple-icons.
- Logo: the official SVG (`tokens/brand/equal-logo.svg`); `npm run logo` writes it into code for both platforms.
- Dark mode now follows the dark screens in the flow designs (background #141414, dark dividers), not the original alias sheet.
- Every image asset is supplied; the WhatsApp logo is coded from the simple-icons glyph.
- Android stack (confirmed with the developers): **business logic in Kotlin, UI in Jetpack Compose, animations as Lottie (JSON)**. Every component ships a Compose equivalent from the same tokens; micro-interactions (press, toggles, sheets) are code (Compose `animate*` / web CSS) on the motion tokens, while illustrative animations (mascot moments, celebrations) are Lottie files.

## Brand Commitments

- Equal AI brand green #00B140 and lime #BAFF29 (green in light, lime in dark); black / white anchors.
- DM Sans only (400 / 500 / 700; never 600). No serif anywhere, even where a source design uses Lora. The logo, the orb (green in light, lime in dark), the robot mascot and the 3D category icons are supplied art and must never be cropped or redrawn.
- Flat icons are Material Symbols; actions use Unicode emoji.

## Evidence on Hand

The real components, screens, flows and clickable prototype in `web/src`; supplied assets in `equal-ds/assets` and `tokens/brand`; the spec-conflict log (Storybook's Reference page, moving to the site). There are no customer quotes, metrics or case studies, and none may be invented.

## Product Principles

1. The code is the documentation: never show a picture of a component when the real one can render.
2. Answer first: every page leads with the thing people copy (the token, the prop, the spec).
3. Show both themes, always; dark mode is a first-class citizen.
4. Say where values come from: derived tokens and design conflicts are visible, not hidden.

## Accessibility & Inclusion

WCAG 2.2 AA for the site itself: keyboard navigation, visible focus, reduced-motion support, and contrast checked in both themes.
