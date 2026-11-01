---
name: equal-ds-flow
description: Design or build an Equal AI app screen or flow with the Equal AI design system (React web + Jetpack Compose Android). Use when someone shares a design (PDF, Figma frame, screenshot) or describes a new Equal AI flow, screen, component or asset and wants it built, specced or checked against the system.
---

# Build with the Equal AI design system

You are building with the Equal AI design system: tokens, components, screens and flows, for web
(React) and Android (Jetpack Compose only). Everything you make must look like it shipped from the
same team.

## 1. Load the system

- **Inside the `equal-ds` repo** (Claude Code): read `CLAUDE.md` (rules + recipe) and search
  `site/public/llms-full.txt` (the catalog). Build in the repo and run the verification commands.
- **Anywhere else** (claude.ai, another tool): read `RULES.md` and `CATALOG.md` next to this file.
  You can't run the build, so write code against the catalog's real component names and props, and
  hand the work over as a file-by-file change list for someone to apply in the repo.

## 2. Read the design before writing anything

- Render every page at high resolution and crop each region. Note every state: empty, loading,
  error, free vs paid, light vs dark, and each experiment variant.
- Write down the screen list, the components on each screen, and the images.

## 3. Map to the system

For every element, name the existing component (search the catalog by role, not by look). Extend
with a prop before creating a component. New touchpoints go on the revamped Home / Chat / You
screens as props; never rebuild an older layout.

## 4. Assets: say what you need

Split images into:
- **Supplied art** (3D icons, illustrations, the green mascot, photos). Ask for each one by its
  catalog-style name (e.g. `feature-call-screening`): transparent PNG, 2000 px or more, never cropped.
  Use a placeholder until it arrives.
- **Coded** (logos, seals, patterns, rings, progress, badges, gradients). Build these; don't ask for images.
- **Animation**: Lottie JSON.

## 5. Build to the rules

- DM Sans only, token colours only (new colours go in `tokens/tokens.json`, marked derived and AA-checked).
- 360 × 800 dp Android frame that grows only vertically, Material 3 status bar, 12 dp field corners.
- Material Symbols for flat icons, emoji on action chips (emoji size = label size).
- Free = plain avatar, paid = gold ring.
- Experiments are props.
- Every string follows `UX-WRITING.md` (run its checklist); Statsig production keys are mirrored exactly and logged in its “fix in Statsig” table.
- Light and dark, reduced motion.
- Web: a component `.tsx` + `.css`, a docs entry with examples and code, and the flow on the site.
- Android: a matching Compose component and screen, plus a snippet for every example and flow state.

## 6. Finish

Report:
- the screens and states built;
- the components added or extended;
- the assets still needed;
- any decisions you made where the design was unclear or inconsistent (e.g. two prices that disagree);
- verification results.

Never claim a check you didn't run.
