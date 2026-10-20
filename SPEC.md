# Equal AI Design System — Component Spec

The contract both implementations follow (`web/` React and `compose/` Jetpack Compose).
**Inline emoji** (chips, badges, pills) are set at the label's font size, never larger; standalone emoji icons (preset circles, recommended cards) keep their own size.

All units are **dp** (mdpi: 1px = 1dp = 1sp). **Frame: design every Android screen at 390 × 800dp and extend only vertically when content needs it** (`ANDROID_FRAME` on web, `EqualFrameWidthDp` / `EqualFrameHeightDp` in Compose). The designs were drawn at 360 wide; their dp values carry over unchanged, and full-width elements simply fill the wider frame. Text sizes are **sp** and always come from a type token.
Colour names are alias tokens (`bg/surface`, `text/secondary`, …). Names marked ⓓ are derived tokens: final values taken from the screens and component references because the colour guide doesn’t define them (`"derived": true` in `tokens/tokens.json`). Every text pair passes WCAG AA in both themes.
Measurements were taken from *Component References design* (rendered at 2×, halved).

## Global rules (from Typography v3 final)

1. Text that does not match a token is a bug, not a variant.
2. **Green means actionable.** Brand green at Medium is only for touchable things or the assistant's own byline.
3. **Weight separates label from prose.** Medium is scanned, Regular is read. Wrapping text is always Regular (`caption` → `captionWrap` once it wraps).
4. **Two actions at equal weight are one action.** Commit is a filled button, revise is a text action.
5. Overline and Badge text are uppercased **in the string**, not by the renderer. Buttons are sentence case.
6. Tabular figures on: `micro`, `badgeCount`, phone numbers, playback pill, plan price, postal codes.
7. Non-Latin strings → letterSpacing 0em.
8. Touch target is always ≥ 48dp even when the visual is smaller.
9. Semibold (600) is never used. 700 is reserved.

## Icons, emoji, categories, logo

- **Flat icons = Material Symbols** (Outlined, weight 400; `-fill` for filled) + WhatsApp brand glyph (simple-icons). One manifest, `scripts/build-icons.mjs`, generates web `icons.ts` and Compose `EqualSymbols.kt` with identical paths.
- **Profile avatars** (supplied): `avatar-male`, `avatar-female`; gold scalloped ring, head rises above it. Size by width; height = width × 1.12 (never cropped). Used on the You hero (70) and bottom-nav You (24).
- **Actions use Unicode emoji** (📞 Call back / Takeover, ✏️ Update address, 🗓️ Add to calendar / Reschedule, ⭐ New feature), sized in dp with the platform emoji font.
- **Category avatars** (supplied images; the circle is part of the image): na (unknown caller, the sparkle), career, delivery, education, financial, gate-alert, home-services, order-confirmation, otp, others (red telephone, default for incoming bubbles), others-1 (card), political, real-estate, ride, services, travel.
- **Full logo** (supplied → vector): mark + "equal ai" wordmark traced to SVG paths (2000 × 428; mark x 0–488). Colour `text/action`.
- **Mascot** (supplied): robot on the Assistant card. Drawn so the robot's bounding box (597,799 2915×2907 in the 4096×4430 canvas) fills the slot.
- **Logo orb** (supplied): light file = #00B140 orb, dark file = lime orb. The opaque orb is the centre 80% of the file and the glow overflows, so render the image at size / 0.8. Used for the AI avatar (24/36/48) and the FAB (58).

## Image assets

All slots are supplied. The action-card WhatsApp logo is coded (simple-icons glyph, `brand/whatsapp` #25D366), not an image.

Supplied since: `avatar-male-bust` · `avatar-female-bust` (gender cards, 60 × 56), `whats-new-illustration` (Language Preference: robot with scripts, transparent, 125dp at right 4 / top 35), `illustration-trash`, `illustration-silent-mode`, `illustration-whatsapp`, `illustration-addresses-empty`, `illustration-instructions-empty`, `illustration-suggestions`, `mascot-sleeping` (system status card, 104dp) and `avatar-assistant` (Meet Your Assistant, 84 × 94 / 100 × 112; the orb badge is part of the file).

Coded (never images): the logo (as vector paths), waveform, stars, checkboxes, switches, gradients.

---

## 1. Status Bar
Material 3 status bar (mocks and flows only; the app draws edge-to-edge and pads content with `WindowInsets.statusBars`). Height 52, horizontal padding 24, background `bg/primary` (overridable). Left: time "12:30" in DM Sans Medium 14/20 (all text is DM Sans, even in system mocks), tracking 0.1, tabular, `text/primary`. Right: Material Symbols `wifi`, `signal_cellular_4_bar` (filled triangle) and an upright `battery_full`, 18dp, gap 4, `text/primary`.

## 2. Top Bar (Logo + Availability Status)
Height 56, padding horizontal 16. Row: full vector logo 20dp high (≈94dp wide, `text/action`) + gap 8 + **UNLIMITED plan pill**: height 16, padding h 6, radius 4, fill `gradient/gold` ⓓ, text `badge` "UNLIMITED" `text/on-dark` ⓓ.

## 3. Assistant Availability Toggle
Card 336 × 64 (fills width with 12 side margin), radius 20, fill `gradient/assistant-toggle` ⓓ, padding 12. Row: `Mascot` 40 (dimmed/greyscale when off) → gap 12 → column [title `subheading` `text/primary` "AI Assistant is On", description `caption` Regular `text/secondary` "Equal is managing your unknown calls!"] → spacer → **Switch**.
Off state: title "AI Assistant is Off", fill `bg/surface` with 1dp `stroke/subtle`.

### Switch
Track 40 × 24, radius full. On: `button/primary`. Off: `button/disabled`. Thumb 18 circle `brand/white`, inset 3, shadow `raised`. Animate thumb 200ms standard.

## 4. Tab Bar (Bottom Navigation)
Floating pill container 185 × 75 (padding 8, radius full, fill `bg/surface`, shadow `nav`). Two items, each 99 × 52 capsule (radius full).
Item: icon 24 (Chats: chat-bubble filled when selected / outline when not; You: ProfileAvatar 24 wide) above label `caption` (Medium). Gap 2.
Selected: capsule fill `nav/selected-bg` ⓓ, label `text/action`. Unselected: no fill, label `text/secondary` ⓓ, icon `text/secondary`.
Count badge on icon: 16 circle, `button/primary` fill, `badgeCount` `text/on-action`, offset top-right (x +10, y −6), 1.5dp `bg/surface` ring.
**FAB** (beside the pill, gap 16): the logo orb at 58dp (light/dark asset, glow built in).

## 5. Chat Header (In-Conversation)
Height 64, fill `bg/surface`, shadow `header`, padding h 12. Row: back-arrow icon button (48 target, 24 icon `text/primary`) → gap 4 → category avatar 36 (default `na`) → gap 12 → column [name `subheading` `text/primary`, subtitle `caption` Regular `text/secondary` (phone number — tabular) or status text] → spacer → trailing actions.
Variants:
- **Default**: trailing = phone icon button + more-vert icon button (24 icons, `text/primary`).
- **Live call / visitor**: trailing = **Takeover pill only** (no call, no more-options): height 34, padding h 14, radius full, fill `gradient/takeover` ⓓ, 1dp `action/stroke/unread`, shadow `glass`; phone icon 18 `text/action` + gap 6 + `button` `text/action`.
- **Home**: status bar + Top bar + Assistant toggle stacked in `bg/primary`, bottom shadow `header`.
- **Selection mode**: back arrow + "1 Selected" `subheading` → spacer → **Mark read** pill (height 26, padding h 10, radius full, fill `bg/surface`, 1dp `action/stroke/unread`, double-check icon 16 `text/action` + gap 6 + `buttonSmall` `text/primary`) → gap 12 → trash icon 24 `button/danger`. Second row (height 40): checkbox 18 + gap 12 + "Select All" `bodyLarge` `text/primary`.

## 6. Chat List Item
Width fill, padding 16 (top 12, bottom 12), fill `bg/surface`. Row: category avatar 48 (default `na`; initials for saved contacts) → gap 16 → column (fill).
Column row 1: name `subheading` `text/primary` (1 line, ellipsis) + spacer + date `micro` (`text/action` when unread, else `text/secondary`).
Row 2: preview `body1` `text/secondary` (1 line, ellipsis) + spacer + unread count (20 circle, `button/primary`, `badgeCount` `text/on-action`) if > 0.
Row 3 (gap 8): horizontal scroll of **Small action chips**, gap 12, overflow clips at right edge.
States: `unread` (chips use unread style), `read` (chips use read style), `selected` (row fill `action-card/highlight`, avatar gets 18 green check badge bottom-right with 2dp `bg/surface` ring, chips outlined only).

## 9. Small Action Chip (outside chats)
Height 30, padding h 10 (left 8), radius full. Content: emoji 12 (= label size) → gap 6 → label `buttonSmall` → gap 4 → chevron-right 14.
- **Unread**: fill `gradient/chip-small-unread` ⓓ, 1dp `action/stroke/unread`, shadow `glass`, label `text/action`.
- **Read**: fill `gradient/chip-small-read` ⓓ, 1dp `stroke/subtle`, shadow `glass`, label `text/secondary`.
- **Outlined** (selected row): transparent fill, 1dp `action/stroke/unread`, label `text/secondary`.
Touch target 48 high (padding outside the visual).

## 10. Large Action Chip (post-call, inside chats)
Height 44, padding h 16, radius full, fill `gradient/chip-large` ⓓ, 1dp `action/stroke/unread`. Content: emoji 14 (= label size) → gap 10 → label `button` `text/action`. Examples: Takeover 📞, Ask to reschedule 🗓️.

## 11. Chat Bubbles
Max width 264 (≈ 68% of the 390 frame). Radius 12. Padding 8 top, 10 h, 6 bottom. Contents: sender line `body2` → message `body1` `text/primary` → timestamp `micro` `text/secondary`, right-aligned (gap 2).
- **Outgoing (user / Equal AI speaking on the call)**: right aligned, fill `chat-bubble/user-bg`, sender colour `chat-bubble/user-title`. Sender = "Equal AI" or "You".
- **Incoming (contact)**: left aligned, 28 category avatar (default `others`, the red telephone) with gap 8, fill `chat-bubble/other-bg` ⓓ, sender colour `chat-bubble/contact-title` ⓓ.
- **Assistant (Equal AI talking to the user)**: left aligned, 24 AI avatar, fill `bg/surface`, sender `text/action`.
- `tail`: optional 8dp tail on the first bubble of a group (top-outer corner, radius 0 on that corner + small curved notch).
- Consecutive bubbles from the same sender: gap 4, avatar only on the first, sender line only on the first.

### Voice note + Call Summary bubble
Width 268. Fill `bg/surface`, radius 16. Inner **Voice player** box: margin 8, radius 12, 1dp `stroke/subtle`, padding 10. Row: play icon 22 `text/secondary` → gap 8 → waveform (fill; bars 2dp wide, gap 2, heights 4–16, `waveform/bar` ⓓ, played part `waveform/bar-played` ⓓ) → gap 8 → speed pill (height 22, padding h 8, radius full, fill `playback/pill-bg` ⓓ, `caption` `text/on-dark` ⓓ, tabular "1x/1.2x/1.5x/2x", tap cycles). Below (gap 4, indent 26): elapsed & total `micro` `text/secondary` at the two ends of the waveform.
Then padding 8 h: "Call Summary" `caption` Medium `text/secondary` → summary `body1` `text/primary` → timestamp `micro`.
Optional footer: 1dp `stroke/subtle` divider, 44 high centred button: share icon 18 + gap 6 + "Share" `button` `text/action`.

## 12. AI Assistant Avatar
The logo orb asset (light = #00B140, dark = lime). Sizes 24 / 36 / 48.

## 12b. AI Chat — Within other conversation (bottom panel)
Panel fill `bg/surface`, radius top 24, padding 20 h, 20 top, 16 bottom. Row: AI avatar 36 → gap 12 → prompt `body1` `text/secondary`. Gap 24. Row of large action chips (gap 12, wrap). Gap 20. Composer (§15).

## 13. Feedback Card
Width 246, fill `bg/surface`, radius 12. Padding 10 h, 12 top. Question `body1` `text/primary`. Gap 10. Five stars 28, gap 8 (filled `rating/star-filled` ⓓ, empty `rating/star-empty` ⓓ), tap to rate. Divider `stroke/subtle`. Footer 36 high centred: "Share Feedback" `button` `text/action` + chevron 16.

## 14. Action Card
Width 268, fill `bg/surface`, radius 16, padding 8. Inner quote block (`ActionCardBody`, shared with the setup-chat instruction preview): fill `bg/primary`, radius 12, padding 8. Row: 40dp circle → gap 12 → column [title `body2` `text/primary`, quote / lines `body1`, timestamp `micro` `text/secondary` right-aligned]. The circle holds one of:
- **Caller category** (3D avatar, its own coloured circle): a person or business, e.g. "Dr. Mehta's Clinic Appointment" with "Tomorrow, 9:00 AM" (`text/secondary`) and "Bring previous medical reports" (`text/secondary`).
- **Material Symbol** in `text/action` on a `bg/surface` circle. Instructions always use `instructions` (note_stack) with two lines: "When: …" `text/primary` and "Do: …" `text/secondary`. Never a 3D category avatar.
- **Source logo**: the coded WhatsApp logo 22 (`brand/whatsapp`), e.g. "WhatsApp · Amit Nanda" + quote.
Actions below: stacked rows 44 high, each preceded by a 1dp `stroke/subtle` divider (full card width, bleeding to the edges), label `button` `text/action` centred (`text/danger` for destructive). Every action is a text row at equal weight, the first one ("Confirm", "Send") included: never a filled button.
Highlight variant: card fill `action-card/highlight` (confirmations, e.g. "Instruction saved" + When / Do + "Edit rule").

## 15. Voice Input Bar (Composer)
Row: input (fill, height 45, radius full, fill `bg/surface`, 1dp `stroke/subtle`, padding h 20, placeholder `body1` `text/secondary`, value `body1` `text/primary`) → gap 10 → send button 45 circle.
- **Empty**: send fill `gradient/send-disabled` ⓓ, icon white, disabled.
- **Focused/typing**: input 1dp `action/stroke/unread` + shadow `focusRing`; send fill `gradient/fab`, shadow `fab`, icon white.

## 16. AI Suggestion (in bottom sheet)
Row: AI avatar 36 → gap 12 → text `body1` `text/primary` (wraps).

## 17. Bottom Sheet
Scrim `scrim` ⓓ. Sheet fill `sheet/bg` ⓓ, radius top 32, padding 24 h, 24 top, 24 bottom (+ nav inset). Close button top-right (inset 12): 44 circle `bg/surface`, shadow `raised`, close icon 22 `text/secondary`.
Confirm-sheet content (centred): illustration asset 72 → gap 20 → title `title` `text/primary` (centred, wraps) → phone `body1` `text/secondary` (tabular) → gap 12 → body `body1` `text/secondary` → gap 28 → full-width button.
Destructive button: height 45, radius full, fill `button/danger`, trash icon 18 + gap 6 + `button` `text/on-danger` ⓓ.

## 18. Context Menu (Long-press)
Width 131 (min), fill `bg/surface`, radius 12, shadow `card`. Items 40 high, padding h 12, icon 16 `text/secondary` + gap 10 + label `body1` `text/primary`. 1dp `stroke/subtle` divider between items. Destructive item: label & icon `text/danger`.

## 19. Buttons
- **Filled** (primary / secondary / danger / disabled): radius full. Sizes: `sm` height 24 padding h 14 `buttonSmall`; `md` height 36 padding h 20 `button`; `lg` height 45 padding h 24 `button`; `block` full width. Fill `button/primary` + `text/on-action`; secondary `button/secondary` + `text/primary` (light) ; danger `button/danger` + `text/on-danger`; disabled `button/disabled` + `text/on-action-disabled`. Optional leading/trailing icon 16 (gap 6).
- **Text**: no fill, `button` `text/action` (danger → `text/danger`).
- **Icon button**: circle 44. `neutral` = `bg/surface` + shadow `raised` + icon `text/secondary` (close). `primary` = `button/primary` + icon `text/on-action` (confirm check), size 42.
- Pressed: scale 0.97 + 8% black overlay. Focus: `focusRing`.

## 20. Badge
- **Text** (NEW): height 16, padding h 6, radius 4, fill `button/primary`, `badge` `text/on-action`.
- **Count**: 20 circle (min width 20, padding h 5 for 2 digits), fill `button/primary`, `badgeCount` `text/on-action`.
- **Feature** (NEW FEATURE): height 24, padding h 10 (left 8), radius full, fill `bg/surface`, 1dp `badge/new-feature-stroke` ⓓ, ⭐ emoji 11 + gap 6 + `badge` `text/secondary`.
- **Plan** (UNLIMITED): see §2.

## 21. Call Status Indicators
- **Day divider** ("Today"): height 22, padding h 14, radius 8, fill `bg/surface`, 1dp `stroke/subtle`, `micro` `text/primary`.
- **Thread event** ("Call Started 03:31", "Call Ended 03:31"): same pill, `micro` `text/secondary`.
- **Thread notice**: max width 232, padding 8 h 16, radius 8, fill `notice/bg` ⓓ, `captionWrap` `text/secondary` centred.

## 22. What's New Card
Width 322, radius 24, fill `gradient/whats-new` ⓓ, padding 16. Top row: Feature badge + spacer + close icon 22 `text/placeholder`. Body row: column [heading `title` `text/action` → gap 4 → body `body1` `text/secondary` (2 lines) → gap 12 → filled `md` button with star icon] + gap 12 + image slot 94 × 100 (asset `whats-new-illustration`, radius 8).

---

## Surfaces from the Typography screen inventory (derived — no visual reference)

- **App Bar**: height 64, back arrow + title `subheading` + trailing actions. Selection variant = §5.
- **Section Header**: `overline` `text/secondary`, padding 16 h, 24 top, 8 bottom. Optional trailing `buttonSmall` action (e.g. Clear all).
- **Settings Row**: min height 64, padding 16 h 12 v. Optional leading icon 24. Column [label `subheading` + optional NEW badge (gap 8), description `caption` (1 line Medium → `captionWrap` Regular when multi-line) `text/secondary`] + chevron-right 20 `text/secondary` or Switch.
- **Toggle Row**: Settings row with a trailing Switch.
- **AI Summary Card**: fill `bg/surface`, radius 16, padding 16. Label "Your AI Summary" `caption` `text/action` → body `bodyLarge` `text/primary` (empty: `text/secondary`) → footer `caption` Regular `text/secondary` right.
- **Plan Card**: fill `plan-card/bg` ⓓ, radius 16, padding 16. "Current Plan" status pill (height 24, padding h 10, 1dp `plan-card/pill-stroke`, `caption` `text/on-dark`; expired → `text/danger`) → product name `title` `text/on-dark` + "UNLIMITED" `text/accent-gold` → price `caption` Regular `text/on-dark-secondary` (tabular) → optional "Manage plan" `buttonSmall` `text/action`.
- **Profile Header**: name `headline` + plan badge (gap 8), phone `body1` `text/secondary` (tabular).
- **Instruction Card**: fill `bg/surface`, radius 16, padding 16. "When" chip (height 22, padding h 8, radius 6, fill `chip/when-bg` ⓓ, `caption` `text/secondary`) → condition `body2` → gap 12 → "Do" chip → action `body2`. Overflow menu (Edit / Delete instruction `buttonSmall`).
- **Address Card**: label `subheading` → value `caption` Regular `text/secondary` (wraps → captionWrap). Trailing Edit text button.
- **Text Field**: overline label (+ `caption` Regular "(Optional)"), input height 48, radius 12, 1dp `stroke/medium`, focus 1dp `action/stroke/unread` + focusRing, value `bodyLarge` `text/primary`, placeholder `text/placeholder`, helper `caption` Regular `text/secondary`, error `text/danger`.
- **Search Field**: height 48, radius full, fill `bg/surface`, search icon 20 `text/secondary`, `bodyLarge`.
- **Segmented Label Chips** (Home / Work / Other): height 36, padding h 16, radius full; selected fill `button/primary` + `text/on-action`; unselected 1dp `stroke/medium` + `text/secondary`; `button` token.
- **Dialog**: width 312, radius 24, padding 24, fill `bg/surface`. Title `title` → subject `caption` `text/secondary` → body `body1` `text/secondary` → actions right-aligned: text (revise/cancel) + filled (commit) or danger.
- **Contact Info Header**: avatar 72 → name `headline` → phone `bodyLarge` `text/secondary` (tabular) → row of icon buttons (48 circle `bg/primary`, icon 22, label `caption` `text/secondary` below): Call, Mute, Save, Share.
- **Call History Row**: direction icon 20 → column [type `body2` "Incoming call" (missed: `text/danger`), meta `micro` Regular `text/secondary` "Today • 30 Sec."] → time `micro` `text/secondary`.
- **Empty / Error State**: illustration slot 96 → title `subheading` → body `caption` Regular (wrap) `text/secondary` → optional button.
- **Snackbar/Toast**: fill `text/primary`, text `body1` `bg/surface`, radius 12, padding 12 h 16, optional text action `text/action` (dark: lime).

---

## You tab (You (Settings) design)

Screen order: StatusBar (bg/surface) → **Profile hero** → YOUR ASSISTANT → Assistant toggle + row group (Instructions `instructions` NEW · Assistant Health Check `health-check` · Silent Mode `silent-mode` · Voice & Language `voice-language`) → ABOUT YOU → **AI Summary card** → row group (Your Addresses `address-pin` NEW) → SETTINGS & SUPPORT → row group (WhatsApp Updates `whatsapp` · Account Data `account-data` · Help Center `help-center`) → **Invite card** → floating BottomNav (You active). Side inset 20 for cards and section headers.

- **Profile hero**: fill `gradient/profile-hero` ⓓ + 2 faint white arcs; padding 20 top / 12 sides. Row (inner padding 8): ProfileAvatar 70 → 12 → [name `headline` + PlanBadge + edit icon button (`edit-profile` 18 in a 28 target); phone `body1` text/secondary tabular]. Plan card sits 16 below, inside the hero.
- **Plan card**: radius 20, `plan-card/bg` ⓓ, padding 14/16/16. "Current Plan" pill: h24, radius 8, 1dp `plan-card/pill-stroke` ⓓ (gold), `caption` on-dark. "Manage plan": `buttonSmall` on-dark, underlined. Product `title` on-dark + tier `text/accent-gold`; price `caption` Regular on-dark-secondary. Coded gold line pattern (`plan-card/pattern` ⓓ) on the right 60%.
- **Settings row** (You tab): leading Symbols icon 24 `text/primary`; gap 12; label `subheading`; NEW = solid badge; description `caption` Regular tertiary (one line); chevron 22 `text/placeholder`.
- **AI Summary card**: radius 20, `gradient/ai-summary` ⓓ, padding 16, min-height 212. Header: orb 28 + "Your AI Summary" `caption` text/secondary. States: **empty** (centred "I'm still getting to know you..." `bodyLarge` Medium), **loading** (5 × 17dp skeleton bars, gap 4, `skeleton` ⓓ, pulsing; footer "Last Updated: 8:06 AM"), **ready** (`bodyLarge` primary). Footer `caption` Regular tertiary, right-aligned.
- **Invite card**: min-height 68, radius 24, `invite/bg` ⓓ + 1dp `invite/stroke` ⓓ, padding 10/6/10/108. `illustration-high-five` (supplied) at 113 × 112, flush left, top −29dp, so it rises above the card and hangs below it. The file already carries the design's −6.39° tilt (verified against the card export), so no extra rotation. Title `subheading`, subtitle `caption` Regular secondary. Share: 56×40 pill, bg/surface, `share` 22 secondary.

---

## Flows (the flow designs) — source of truth is `web/src/screens/FlowScreens.tsx` + `web/src/components/Flows.tsx`

**Shared updates**: AppBar title centred (56dp); large chips have `read` / `unread` states with `text/primary` labels; the selected segmented chip is dark (`chip/selected-bg` ⓓ); text fields are borderless white with 12dp corners (Name and address lines alike); the call-summary player is filled (`voice/player-bg` ⓓ) and can carry a 🪄 "Instruction Applied" tag; rating stars are outlined when empty; bottom sheets take an optional title row (X left, centred title).

**Profile** — *Personal Details*: NAME (text field, 12dp corners, + helper "Your AI will call you '<first name>'"), GENDER (two 120dp cards; selected = 2dp green border + `gradient/gender-selected` ⓓ; faint ♂/♀ watermark; bust art). Choosing a gender updates the profile avatar app-wide. *Silent Mode*: illustration, "Mute Promotional Calls" toggle; CATEGORIES group (Loan Offers, Credit Cards, Job Offers, Real Estate, Others), dimmed and disabled when muting is off; Muted Contacts row. *WhatsApp Updates*: illustration, "Get updates on WhatsApp" toggle (two-paragraph description); switching it off opens a confirm sheet ("Turn off WhatsApp updates?" → primary "Turn off updates").

**Addresses** — Empty state (illustration, "Never explain your address again") → list of address cards (label, address, ⋮ Edit / Delete address) → *Edit / Add Address* titled sheet (LABEL* chips, ADDRESS two lines + City / Pincode, GUIDING DIRECTION (Optional), Save) → *Delete Address* confirm sheet (trash illustration, destructive Delete). "Add new Address" is pinned to the bottom.

**Instructions** — *Hub*: RECOMMENDED FOR YOU (horizontal 188×114 cards: emoji, + add) or the "New suggestions coming…" card once all are applied; YOUR INSTRUCTIONS row (Active Instructions · N Active); BROWSE PRESETS (🛡️ Screen & Protect, 📞 Handle everyday calls, 💼 Run my business; emoji in a tinted circle); pinned "Add New Instruction". *Preset*: description plus instruction cards (title, + Apply / Applied, When / Do rows). *Active Instructions*: header "+ Add"; "ACTIVE INSTRUCTIONS - N"; cards with a source chip, WHEN / DO, ⋮ Edit / Delete instruction; empty state (illustration, "No instructions yet"). *Setting up instruction* (full-height sheet, orb + title + close, chat wallpaper): **no input** → You: request → Equal AI: "Got it! Here's your instruction:" → preview card → chips ✍️ Edit (read) / 👍 Confirm (unread); Confirm adds ✅ to the preview and a "Confirm" bubble, then saves. **Input required** → Equal AI asks each follow-up (e.g. landmark, wait time), the user answers in the composer, then the preview appears with the answers in bold → chips ✏️ Change something else / 👌 That's all.
**In chat**: the call summary shows 🪄 "Instruction Applied"; the chat sits on the doodle wallpaper; the assistant panel offers 💬 Reply on WhatsApp / 🗓️ Add to Calendar after a call.

**Chat wallpaper** (supplied, BG pattern.svg): 360 × 750 tile, repeated. Rendered as an alpha mask tinted `chat/wallpaper-pattern` (#737373 @ 8%) over `chat/wallpaper-bg`, in both modes. Used behind chat threads and the instruction setup chat.

## Contact Info (Contact Info design)
Centred app bar "Contact Info". Header: ContactAvatar 96 (category, e.g. career) → name `headline` → phone `bodyLarge` secondary (tabular) → 4 action tiles in a row (72dp tall, radius 12, bg/surface, 1dp stroke/subtle in dark; icon 24 `text/action`; label `body1`): Call · Mute (`silent-mode`) · Save (`person-add`) · Share. CALL HISTORY → row group: Material Symbols `phone_callback` (incoming) / `phone_missed` (missed, `text/danger`) / `phone_forwarded` (outgoing); label `body2` primary ("Missed Call" is not red); meta `micro` Regular secondary; time `micro` secondary.

---

## Flow · Temporarily Unavailable (Temporarily Unavailable design)
Values marked ⓓ are derived (light from the design; dark matched to the dark screens).
- **SystemStatusCard** replaces the assistant toggle in the Chats header (`HomeHeader status`) while the service is down; promo cards hide. Radius 20, padding 16, min-height 140, fill `gradient/status-card` ⓓ (120°, #FEEBC8 → #FDF2DB → #FBF9F4). "SYSTEM STATUS" pill: height 20, padding h 8, `status-card/pill-bg` ⓓ, overline 10sp `text/secondary`. Title 18/24 Bold `text/primary`, one line. Body caption regular `text/secondary`, max 190 wide. Sleeping mascot 104dp, right-aligned, vertically centred.
  - `unavailable`: amber pill 32 high, padding 12/14, `status-card/button` ⓓ #FAAF39, bell `notify-me` 16 + "Notify me when ready" buttonSmall Bold `status-card/button-text` ⓓ (#3D2600: dark brown, because white on the amber is only 1.87:1).
  - `notified`: `check` 16 + "You’ll be notified soon" buttonSmall Bold `text/action` (not tappable).
- **SystemNotification** (web) / **SystemNotificationPreview** (Compose): heads-up card for mocks, radius 20, `notification/bg` ⓓ, padding 12/16/16. "Equal AI · Now" 10sp; orb 32 + title caption Bold + body caption regular `text/secondary`. In the app, post a real notification with the same copy.
- **MeetAssistantCard**: fill `gradient/meet-assistant` ⓓ. Compact (Chats promo slot): radius 16, padding 14/16, avatar 84 × 94, gap 18, title subheading, "Call me" pill. Banner: radius 20, title `title` Bold `text/action`, body1 `text/secondary`, avatar 100 × 112 bottom-right. "Call me": height 40, `gradient/call-me` ⓓ (green; lime in dark), icon `call-sparkle` 18 + button `text/on-action`, green glow shadow. Close (X) 20 `text/placeholder`, top-right.
- Sequence: unavailable → tap Notify → notified → on recovery the notification drops in (320ms, reduced-motion: none), the toggle returns as "AI Assistant is On", and Meet Your Assistant fills the promo slot.

---

## Flow · Monetization (Monetization Flow design)
**Type: DM Sans only.** The design sets display lines in Lora; the system uses DM Sans everywhere: display pairs are 22/26 Regular + 38/42 Bold ("Unlock Equal" / "Unlimited") or 22/26 Regular + 28/32 Bold (status cards). Never add a serif.

**Touchpoints** (each opens the paywall with `touchpoint` so its preview card shows what was locked): chat list rows past the free limit (`ChatListItem locked`: blurred preview, crown-lock replaces the unread count; `HomeScreen lockedFrom`), a locked conversation (`ChatBubble locked` + `UnlockDetailsCard` docked at the bottom), locked audio (`CallSummary locked="audio"` → 🔒 Unlock Audio pill), Manage plan (Settings plan card), and lapsed plans (`PlanBanner` in Settings and at the top of chats).

**Experiments** (the Android team tests these, so they are props, never forks): price / discountedPrice (₹249 / ₹199), coupon (none · available · applied), subtitle copy, touchpoint preview, waiting art (coin · cash), home `headerVariant` (welcome: gear + "Welcome Keshav 👋"; avatar: avatar + name top-right) and `lockedFrom`.

- **PaywallHero**: `gradient/paywall-hero` ⓓ + soft concentric white rings; back + Support (help-fill 20 + body2); crown 64; title pair; subtitle body1 `text/secondary`. **PriceTag**: 🎊 LIMITED TIME DISCOUNT 🎊 caption caps `text/gold` ⓓ; price 44/48 Bold `gradient/gold-text` ⓓ between `paywall/divider` hairlines. **LockedPreviewCard**: radius 20, padding 8, `paywall/card-bg` + 1dp `paywall/card-stroke`; inner `bg/surface` radius 14.
- **CouponTicket**: radius 20, `gradient/coupon`, two 9dp notches at 58%; code subheading Bold `text/gold`; description caption; APPLY / APPLIED buttonSmall Bold `text/gold`; dashed rule with the % seal 28; discount 30/36 Bold gold. Applying drops the price.
- **FaqList**: "FREQUENTLY ASKED QUESTIONS" `DividerLabel`; one `bg/surface` card radius 16, rows padding 14/16, bodyLarge question + chevron, body1 answer.
- **PaywallFooter**: sticky, padding 12/16/16, `bg/surface`, lg block `Button variant="gold"` (`gradient/cta-gold` ⓓ, white Bold, 5.1:1) + "Cancel Anytime. Automatically renews" caption.
- **StatusCard**: radius 20, 1dp `stroke/subtle`, `gradient/status-{neutral|success|warning|danger|gold}` ⓓ with rings; 3D art 100; title pair; body1 `text/secondary`. **StatusSheet**: scrim + `bg/surface` panel radius 24 top, padding 12/12/20, no close. **PaymentStatusSheet**: waiting (coin or cash; auto) · confirmed (RedirectNote 5s) · pending (Contact Support) · failed (Retry Payment) · welcome (crown, redirect to home). Pending/failed body: "If money was deducted from card (**2652), refund will be initiated within 24hours". **RedirectNote**: body2 `text/action` "Redirecting … in 00:5 Secs".
- **HelpCenterSheet**: title, body, Call Us / Mail Us rows (radius 14, 1dp stroke) with 52×36 `action-card/highlight` icon pills.
- **UnlockDetailsCard**: radius 20, `gradient/status-gold` + rings, crown 56, "Unlock All Details" 22/26 Bold, body1, lg Button with lock. **UnlockAudioPill**: 32 high, `button/primary`, lock 14 + buttonSmall Bold. **LockBadge**: crown-lock art (or crown + coded padlock until supplied), 16–18dp on `locked/badge-bg`.
- **PlanBanner**: `plan-banner/bg` ⓓ, caption `plan-banner/text` ⓓ (6.0:1) with info-fill 16; `attached` hangs under the plan card. **PlanCard** link: Manage plan · Restore Plan · Reactivate plan.
- **Subscription page**: hero ("Equal / Unlimited"), PLAN ACTIVE UNTILL + date, "Why people choose / EQUAL UNLIMITED" (`text/gold`), **StatBlock** 52/56 gold, **RatingPill** (star 20, radius 10), **TestimonialCarousel** (240-wide cards, dots `text/gold`), FAQ, outline "Cancel Subscription" (`Button variant="outline"`) or gold Re-Subscribe once cancelled.
- **CancelSheet**: feedback (sad face; textarea radius 12; Proceed disabled until text) → confirm (Cancel outline / Confirm) → submitted (thumbs up; redirect 3s). Access stays until the end date.
- **Settings v2**: gold header, transparent "← Settings", paid avatar + UNLIMITED, plan card, **SettingsArtRow** (3D icon 32 → 16 → subheading + caption), Invite a Friend.
