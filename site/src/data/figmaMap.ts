// Figma → code: every generically named Figma component and what it is in the system. Read from
// the Figma file (every page except Experiments and 🎨 Foundations); the file itself is not changed.

export type FigmaMapRow = {
  /** The name in the Figma file. */
  figma: string;
  /** Its variants or a hint to find it. */
  where: string;
  /** What it is. */
  is: string;
  /** The code component (web · Compose), or a gap. */
  code: string;
  /** The docs page id, when there is one. */
  doc?: string;
  gap?: boolean;
};

export const figmaMap: FigmaMapRow[] = [
  { figma: 'Component 25', where: 'Monetization, Healthcheck, Force Update · 13 uses', is: 'Assistant toggle, On (phone art)', code: 'AssistantToggle art="phone" · AssistantToggleCard(art = Phone)', doc: 'assistant-toggle' },
  { figma: 'Component 24', where: 'Snooze, Force Update · "Equal will be back Today 6:45pm"', is: 'Assistant toggle, Off / snoozed / busy', code: 'AssistantToggle offDescription · busy', doc: 'assistant-toggle' },
  { figma: 'Component 26', where: 'Monetization, Healthcheck, Snooze, Force Update · 26 uses', is: 'The green block CTA', code: 'Button size="lg" block · EqualButton(size = Block)', doc: 'button' },
  { figma: 'Buton · Button Glow', where: 'Monetization', is: 'Gold CTA and its glow', code: 'Button appearance="premium" glow · EqualButton(appearance = Premium, glow = true)', doc: 'button' },
  { figma: 'Component 46', where: 'Snooze, Updated UI, Known Calls · "Call me →"', is: 'The Call me pill', code: 'CallMeButton (in MeetAssistantCard)', doc: 'meet-assistant-card' },
  { figma: 'Component 249 (Your AI Summary)', where: 'Updated UI, 27 Aug · 345 × 221', is: 'AI summary card on You', code: 'AiSummaryCard · NoticedCard', doc: 'personalisation' },
  { figma: 'Component 248', where: 'Updated UI · "Your family calls you a lot…"', is: 'AI summary, ready', code: 'AiSummaryCard status="ready"' },
  { figma: 'Component 249 (bars, 93 × 16)', where: 'Global MYA · variants Component 238 / 239', is: 'Voice recording waveform', code: 'RecordingWaveform · VoiceWaveform' },
  { figma: 'Frame 2085664171', where: 'Updated UI, Known Calls · 21 × 25, three bars', is: 'Live-audio equaliser glyph', code: 'Icon "graphic-eq" (OngoingCallBar, CallNotification)' },
  { figma: 'Component 241 · 242 · 243', where: 'Global MYA · "Thinking…", "Understanding your request…", "Getting things ready…"', is: 'Assistant thinking line', code: 'AssistantThinking label={…}' },
  { figma: 'Group 2085663987', where: 'Healthcheck · 176 uses · "Checking"', is: 'Health check status label', code: 'HealthStatusLabel · HealthStatusLabel()', doc: 'health-check' },
  { figma: 'Frame 2085664294', where: 'Healthcheck · 51 × 51, six variants', is: 'Permission status ring', code: 'StatusRing · StatusRing()', doc: 'health-check' },
  { figma: 'Component 1', where: 'Force Update · 20 × 20, variant=1…8', is: 'Spinner frames', code: 'Spinner · EqualSpinner', doc: 'snooze' },
  { figma: 'Component 55 · Component 57 · Frame (Lock)', where: 'Monetization · 20–24', is: 'Crown-lock badge', code: 'LockBadge · Asset "crown-lock"', doc: 'locked-content' },
  { figma: 'Component 252', where: '27 Aug · ₹ + 0–9 glyph strips', is: 'Price odometer', code: 'PriceTag (the price drop) · PriceDealCard', doc: 'paywall' },
  { figma: 'Frame 2085663275', where: 'Monetization · "Share Contact", "Unmute"', is: 'Long-press menu', code: 'ContextMenu · ContextMenu()', doc: 'context-menu' },
  { figma: 'Badge · Variant2 / Default', where: 'Updated UI, Known Calls, Local Components · "NEW", "NEW FEATURE"', is: 'New badge (solid / soft)', code: 'NewBadge tone="solid" | "soft"', doc: 'badges' },
  { figma: 'Text DNA (Frame 2147228927)', where: 'Local Components · "Search for names / numbers / categories"', is: 'Search placeholder variants', code: 'SearchField placeholder', doc: 'search-field' },
  { figma: 'Component / Bar', where: 'Updated UI, Global MYA · Gboard toolbar', is: 'System keyboard toolbar', code: 'Not drawn: the system draws the keyboard (CLAUDE.md rule)' },
  { figma: 'Component 14', where: 'Monetization · 248 × 42, 0–5 stars', is: 'Star rating', code: 'Not in code: feedback uses thumbs (FeedbackCard). Needs design: keep stars or thumbs?', gap: true },
  { figma: 'Component 234', where: 'Known Calls · "Welcome Keshav! Let me show you how I can take your…"', is: 'New-user greeting card', code: 'Not in code: the Known Calls flow opens with MeetAssistantCard layout="hero" instead.', gap: true },
];

/** What changed between the Figma file and the system, by rule. */
export const figmaRebuild = [
  { what: 'Fonts', file: 'Geist (price digits), Lora (paywall headlines), PP Cirka (payment status), Roboto (status-bar clock), Inter / SF Pro / Poppins / Noto in places', code: 'DM Sans only, through the type tokens. Weights 400 / 500 / 600 / 700; button labels SemiBold 16 / 12.' },
  { what: 'Frames', file: '360 × 800, plus scaled 393 × 852 (Snooze, Fore Update, vOIP, Healthcheck) and 0.92× chat copies', code: 'Every screen rebuilt 1:1 on 360 × 800 (extends only vertically), which removes the fractional sizes.' },
  { what: 'Type sizes', file: '12.82, 10.99, 11.51, 14.39, 15.35, 16.44, 22.27 … from the scaled copies', code: 'Snapped to the scale: 10, 11, 12, 14, 16, 20, 24 (44 for prices).' },
  { what: 'Radii', file: '7, 13, 13.74, 14.5, 15, 18.87, 22.53, 30, 34, 42, 44 …', code: 'Whole numbers from 2, 4, 6, 8, 12, 16, 18, 20, 24, 28, 32, 36, 50, 54, 80, 100, full.' },
  { what: 'Greys', file: 'About 20 one-off greys (#111, #282828, #545454, #858484 …)', code: 'Text black 80 / 60 / 40 / 20 %, strokes 5 / 8 / 32 %.' },
  { what: 'Buttons', file: 'Button (48 variants), Buton, Button Glow', code: 'One Button: appearance × emphasis × xs / sm / md / lg (24 / 32 / 40 / 48).' },
  { what: 'Off-system accents', file: '#E11D48 end call, #006C27 / #69FFA0 usage, #00518C info, #FFC927 iOS paywall …', code: 'Named derived tokens with a why (button/end-call, usage-meter/*, info-note/title, paywall/*).' },
];
