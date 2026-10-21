// Every rule the system follows and why it was decided. Shown on /reference/decisions and
// included in llms-full.txt, so people and Claude get the reasoning, not just the rule.
export type Decision = {
  rule: string;
  why: string;
  /** Where it came from: a design file, a product call, accessibility, engineering. */
  source: string;
  /** Where to look in the system. */
  where?: string;
  /** An A/B experiment value: the current default, expected to change. */
  experiment?: boolean;
};

export type DecisionGroup = { id: string; title: string; intro: string; items: Decision[] };

export const decisions: DecisionGroup[] = [
  {
    id: 'principles',
    title: 'Principles',
    intro: 'The few ideas everything else follows from.',
    items: [
      { rule: 'One token file feeds web and Android.', why: 'If a value lives in two places it drifts. tokens.json generates the CSS, the TS and the Kotlin, so a change lands on both platforms at once and nobody hand-copies hex values.', source: 'Engineering', where: 'tokens/tokens.json · npm run tokens' },
      { rule: 'Experiments are props on one screen, never forks.', why: 'The Android team A/B tests monetization constantly (price, copy, entry point, layout). A forked screen per variant would multiply bugs and fall out of sync; a prop keeps every variant on the same, tested code.', source: 'Product: Android runs monetization experiments', where: 'HomeScreen / ChatScreen / PaywallScreen props' },
      { rule: 'Only supplied art is an image; everything else is code.', why: 'Coded logos, seals, rings, badges and progress bars stay sharp at any size, follow light and dark automatically and weigh nothing. Images are reserved for 3D art and illustrations nobody can draw in code.', source: 'Design direction', where: 'assets/ · Asset.tsx · EqualAssets' },
      { rule: 'Never crop supplied art.', why: 'Designers compose the transparent padding on purpose (glow, shadow, breathing room). Cropping changes the art; only fully transparent margins are trimmed when an asset is built.', source: 'Design direction' },
      { rule: 'Colours that aren’t in the colour guide are marked “derived”.', why: 'The colour guide doesn’t cover every surface the screens use. Marking derived tokens keeps it clear which values came from the guide and which were measured from screens, so design can review them in one place.', source: 'Design: colour guide gaps', where: 'Foundations → Colour (derived pill)' },
    ],
  },
  {
    id: 'colour-type',
    title: 'Colour & type',
    intro: 'How colour and text are chosen, and the accessibility floor under them.',
    items: [
      { rule: 'Nothing comes from the Figma “Experiments” page.', why: 'It holds explorations that are not decided. Colours, radii, type and components are taken only from the other pages (Updated UI, Monetization, Global MYA, Preset Instruction…).', source: 'Product owner', where: 'Figma: 📱 EQAI Android' },
      { rule: 'Colours come from the Figma library’s “Colours” set: text is black at 80 / 60 / 40 / 20%.', why: 'The app file binds its colours to a shared library with two sets of the same names; the newer “Colours” set (alpha black and white) is the one the revamped screens use. Primary text is black 80%, secondary 60%, placeholder 40%, disabled 20%; strokes are black 5 / 8 / 32%; the page is #FAFAFA and the dark surface #1F1F1F.', source: 'Figma: 📱 EQAI Android · library “Colours”', where: 'text/* · stroke/* · bg/*' },
      { rule: 'A colour at x% is the colour-guide colour at 100% with x% opacity.', why: 'Figma paints the same brand and neutral colours at different opacities (green at 36% for unread strokes, lime at 8% for unread fills). Writing them as “{brand.green} @ 36%” keeps one source colour per hue, so a brand change flows to every tint.', source: 'Product owner', where: 'tokens.json values like {brand.green} @ 36%' },
      { rule: 'One-off greys snap to the text colours; readable text never drops below 4.5:1.', why: 'Figma has about 20 hand-picked greys (#3C3C3C names, #858484 previews, #9F9F9F dates…). Snapping them to primary / secondary keeps new screens to four text colours. Black 40% is 2.85:1 on white, so it is only for placeholders and disabled text; dates and captions use secondary (60%, 5.7:1). Figma colours that fail AA for text are darkened in the same hue and the reason is written on the token.', source: 'Product owner + accessibility', where: 'text/primary · text/secondary · text/placeholder' },
      { rule: 'Every colour token says what it is for and is used somewhere.', why: 'Tokens that no Figma screen or component uses were deleted in the rebuild. The Colour page lists each token’s purpose and the components that use it, so an unused or duplicate colour is visible at once.', source: 'Figma rebuild', where: 'Colour page · token-usage.json' },
      { rule: 'Green (lime in dark) only for what you can touch.', why: 'If green also decorates, users can’t tell what is tappable. Reserving it for actions and the assistant’s byline makes every green thing a promise that it does something.', source: 'Typography v3 colour roles', where: 'text/action · button/primary' },
      { rule: 'In dark mode every green is lime (#BAFF29).', why: 'Brand green goes muddy on near-black; lime is the brand’s dark-mode accent. Accents (buttons, icons, strokes, the FAB, the WhatsApp glyph) use lime itself, and green-tinted dark surfaces are re-hued to lime at the same darkness. Anything drawn on lime is dark (text/on-action), never white.', source: 'Design rule', where: 'tokens.json dark values · text/on-action' },
      { rule: 'Every text pair passes WCAG AA (4.5:1).', why: 'Many users read the app outdoors on mid-range phones at low brightness. When a mock’s colour falls short (e.g. the saved-contact blue at 4.15:1), the token is nudged until it passes and the reason is written on the token.', source: 'Accessibility', where: 'tokens.json “why” fields' },
      { rule: 'Everything is set in DM Sans. Nothing is in Lora.', why: 'One family keeps every screen consistent and the app light to ship. Some design files use Lora for display lines; in the product those lines are DM Sans.', source: 'Product: “Make everything DM Sans”' },
      { rule: 'Four DM Sans weights: 400, 500, 600 and 700.', why: 'Regular is for reading, Medium for labels, SemiBold where the designs use it (plan names such as “Equal AI Unlimited”), Bold for display lines. All four are loaded on web and Android, so nothing is synthesised.', source: 'Design files', where: 'tokens.css · EqualFonts.kt' },
      { rule: 'Text that doesn’t match a type token is a bug.', why: 'Twelve tokens cover every role. Ad-hoc sizes creep in one screen at a time until nothing lines up; holding to the tokens keeps rhythm across screens.', source: 'Typography v3' },
      { rule: 'Medium is scanned, Regular is read; wrapping text is always Regular.', why: 'Weight separates labels from prose. Paragraphs in Medium are tiring to read and flatten the hierarchy.', source: 'Typography v3' },
      { rule: 'Emoji are sized to the label they sit in.', why: 'Emoji render larger than letters at the same font size, so oversized emoji pull the eye away from the words. Matching the label size keeps chips calm.', source: 'Design review: emoji looked too big', where: 'ActionChip · ActionCard' },
    ],
  },
  {
    id: 'android',
    title: 'Android frame & chrome',
    intro: 'Rules that come from designing for Android first.',
    items: [
      { rule: 'Design at 390 × 800 dp and extend only vertically.', why: 'It is the team’s working Android frame. Long screens grow taller rather than wider, so layouts are always checked at real phone width.', source: 'Product: team frame', where: 'ANDROID_FRAME · EqualFrameWidthDp' },
      { rule: 'The status bar and every icon follow Material 3.', why: 'The app runs inside Android’s system UI. A Material 3 status bar (52 dp) and Material Symbols for all flat icons mean screens sit naturally next to the real system UI and every icon shares one line weight.', source: 'Product: Material 3', where: 'StatusBar · npm run icons' },
      { rule: 'Text fields have 12 dp corners; search and assistant message fields are fully rounded.', why: 'Form fields read as editable boxes. Search and the assistant composer are conversational entry points, so they use the pill shape the app uses for chat input.', source: 'Design: text field 12px; assistant fields fully rounded' },
      { rule: '48 dp touch targets, even when the visual is smaller.', why: 'Smaller targets cause mis-taps, especially one-handed. Where a mock draws a 36 dp button, the touch area is still 48 dp.', source: 'Accessibility / Material' },
      { rule: 'Business logic in Kotlin, UI in Compose, animations in Lottie.', why: 'That is how the Android team builds. Components are stateless Compose; illustrative motion is Lottie JSON played by Lottie, never re-built by hand.', source: 'Engineering: Android stack', where: 'compose/equal-ds · assets/lottie' },
      { rule: 'Respect reduced motion everywhere.', why: 'Some users get motion sickness or find animation distracting. Every animation in the system holds still when the phone asks for reduced motion.', source: 'Accessibility' },
    ],
  },
  {
    id: 'identity',
    title: 'People & identity',
    intro: 'How the user, contacts and the assistant are shown.',
    items: [
      { rule: 'Free users get the plain avatar; paid users get the gold background and ring.', why: 'Unlimited should feel like a status the user can see on their own face. Keeping free plain makes the upgrade visible without nagging.', source: 'Product', where: 'ProfileAvatar tier' },
      { rule: 'Saved contacts show initials on a tinted disc with a person badge.', why: 'The badge is the signifier for “someone you know”. The disc colour varies per contact (blue, green, purple, orange, pink, teal) at the same saturation and lightness, so a list isn’t one flat colour, and every pair keeps the initials readable (AA). The same initials always get the same colour.', source: 'Design: “AN with a mark”; colour can vary at the same saturation', where: 'ContactAvatar tone · contactTone()' },
      { rule: 'Unknown and business callers use 3D category art.', why: 'The category tells the user at a glance why someone called (delivery, bank, real estate) before reading a word.', source: 'Design' },
      { rule: 'The robot is the default “AI Assistant is On” art; the phone-with-sparkles is an experiment.', why: 'Design confirmed the robot as final. The phone stays available as a prop so the team can test it.', source: 'Product decision', where: 'AssistantToggle art' },
      { rule: 'Green mascots are generated to order.', why: 'Mascot moments are custom for each screen. When a new one is needed, the system shows a placeholder and asks for the art rather than reusing a mismatched pose.', source: 'Design' },
      { rule: 'The logo comes from the official SVG.', why: 'A traced logo drifts from the brand. The official vector generates both platforms (npm run logo).', source: 'Brand', where: 'tokens/brand/equal-logo.svg' },
      { rule: 'In the AI layer, saved contacts are addressed by first name, others by full name.', why: '“Replying Amit” reads naturally for a friend, but splitting a business name gives nonsense like “Replying Dr.”.', source: 'Engineering (copy)' },
    ],
  },
  {
    id: 'components',
    title: 'Components',
    intro: 'Component-level rules that came from design reviews.',
    items: [
      { rule: 'Build from atoms: atoms → molecules → organisms → screens.', why: 'Every visible leaf (text, icon, emoji, image, coded art) is rendered by an atom (Text, Icon, Emoji, Asset, Button…). Molecules combine atoms for one job (a chip, a row), organisms are whole sections (a chat row, a paywall). Trace walks this chain, so a designer can point at any pixel and find the atom, then step up with ↑ to the molecule and section.', source: 'Product owner', where: 'Trace · site/src/registry/levels.ts' },
      { rule: 'Corner radii are whole numbers from one scale.', why: 'The Figma scale is 2, 4, 6, 8, 12, 16, 18, 20, 24, 28, 32, 36, 50, 54, 80, 100 and full. Decimal radii in Figma (5.76, 1.33, 10.27…) come from scaled component instances or circles; use the whole number the master component uses, and “full” for circles and pills.', source: 'Figma audit', where: 'radius tokens' },
      { rule: 'Action cards: grey body on a white card, never a green card.', why: 'A green wrapper made confirmations look like warnings and broke consistency with the rest of the thread. Every action card has the same frame; the content says what happened.', source: 'Design review', where: 'ActionCard' },
      { rule: 'Every action-card CTA has an emoji (📤 Send, ✏️ Edit, 👍 Confirm, 🗑️ Delete).', why: 'Emoji make the action recognisable at a glance and match the chip language used everywhere else in chat.', source: 'Design review', where: 'ActionCardAction.emoji (required)' },
      { rule: 'All actions in a card have the same weight; Confirm is not a filled button.', why: 'Two actions at equal weight let the user choose; a filled Confirm pushes them. The commit goes first, the revise second, both as text rows.', source: 'Typography v3 / handoff' },
      { rule: 'Instructions use the note_stack icon with When / Do lines, never a category avatar.', why: 'An instruction is a rule the user set, not a caller. The icon and When / Do structure make that obvious.', source: 'Design review' },
      { rule: 'Flat icons are Material Symbols (Material 3); actions use emoji.', why: 'One icon family keeps line weight consistent. Emoji on actions add warmth and are easier to recognise in a crowded chat.', source: 'Design direction', where: 'npm run icons' },
      { rule: 'Premium (monetization) perk icons are filled, 16 dp, in the gold radial gradient.', why: 'Filled gold marks a paid perk apart from everyday UI icons, which are outlined.', source: 'Design spec', where: 'PremiumIcon' },
    ],
  },
  {
    id: 'monetization',
    title: 'Monetization',
    intro: 'Pricing, the paywall and how free users meet Unlimited. Most of this area is experiments the Android team A/B tests, so values marked Experiment are today’s defaults, not fixed rules; they are props, so a test changes a value without forking a screen.',
    items: [
      { rule: 'Unlimited is ₹249; with EQUAL25 it is ₹149.', why: 'Today’s default, confirmed by product; price and coupon are experiment props (price, discountedPrice). The coupon row says “Save Flat ₹100”, and the price drop animates only when the user taps APPLY.', source: 'Product decision', experiment: true },
      { rule: 'The plan value card shows ₹39 with ₹49 struck through.', why: 'The value designs show both orders; ₹39 / ₹49 was confirmed.', source: 'Product decision', experiment: true },
      { rule: '🎊 LIMITED TIME DISCOUNT stays an emoji, not art.', why: 'Confirmed by design: it is copy, not an illustration.', source: 'Design decision', experiment: true },
      { rule: 'Gold is only for subscription: paywall, plan, renewal, payment and anything else about Equal AI Unlimited.', why: 'Gold means “premium”. Keeping it to subscription surfaces makes it a reliable signal and keeps the rest of the app green. Its white-on-gold contrast at the sheen is an accepted brand decision, like brand green.', source: 'Product owner', where: 'Button variant gold · gradient/cta-gold · gradient/gold · text/gold' },
      { rule: 'Every piece of text is DM Sans, including system mocks.', why: 'One family everywhere. Even the mock status bar clock, the dialer stand-in and the call-handling device bar use DM Sans, not Roboto or the system font.', source: 'Product owner', where: 'StatusBar · DialerStandIn · CallHandling' },
      { rule: 'Lock content, never identity.', why: 'Free users still see who called and when; only the content (bubbles, recording, actions) is blurred or crown-locked. Hiding the caller would make the app useless, not tempting.', source: 'Product', where: 'Locked · LockedRecording · SummaryActions' },
      { rule: 'While a payment is processing, every surface says so and the paywall button is disabled.', why: 'UPI confirmations can take minutes. Without this, users who close the pending page pay twice.', source: 'Design: If User Closes the payment pending page', where: 'ProcessingStrip' },
      { rule: 'The exit offer appears once per session.', why: 'Offering the coupon when someone leaves converts; offering it every time trains people to leave first.', source: 'System guidance (not yet enforced in code)', where: 'ExitOfferDialog', experiment: true },
      { rule: 'Show what Unlimited did before asking for a renewal.', why: 'A user who sees “20 calls handled” renews for the value they already got, not on a promise.', source: 'Design: Monetization value', where: 'ValueCard · ValueSheet', experiment: true },
      { rule: 'Keep the existing bottom navigation (Chats, You).', why: 'One mock showed a Home tab; product confirmed the navigation stays as it is.', source: 'Product decision' },
    ],
  },
  {
    id: 'assistant',
    title: 'Assistant & calls',
    intro: 'Global MYA, Call Handling and known-call notifications.',
    items: [
      { rule: 'The green orb opens the same assistant sheet on every screen.', why: 'One consistent entry point means users learn it once.', source: 'Design: Global MYA', where: 'AssistantSheet' },
      { rule: 'Inside a chat, the task sheet stops below the chat header.', why: 'The user must still see whose chat they are acting in, so a reply never goes to the wrong person.', source: 'Design: chat AI layer', where: 'ChatTaskSheet' },
      { rule: 'Tasks end in a result the user confirms (Edit / Confirm), then the outcome shows where it lives.', why: 'The assistant acts on the user’s behalf; confirming first keeps them in control, and showing the result in the chat row closes the loop.', source: 'Design: Global MYA' },
      { rule: 'Confirm before actions that are hard to undo (mute, send, save).', why: 'The assistant acts for the user. A one-line check (“Are you sure? You’ll not get any call from them…”) prevents the mistake that costs the most.', source: 'Design: Mute, Reply on WhatsApp, Save Contact', where: 'ChatActionScreen' },
      { rule: 'Every finished action leaves an action card in the chat.', why: 'The chat is the record of what the assistant did (Outgoing call, Muted, WhatsApp message, Contact saved), and each card links to where the result lives.', source: 'Design: action flows', where: 'ActionCard' },
      { rule: 'Hand off to the phone for the dialer, Settings and WhatsApp; never redraw them.', why: 'The system owns those screens. The app hands off, then picks up where the user left off (“You stopped between saving this contact. Want to continue?”).', source: 'Design: Call Back, Save Contact' },
      { rule: 'Errors say what happened in one line and offer one fix.', why: 'Retry for a failed generation, Refresh or Try again when offline, “hold and speak again” for voice. One clear next step beats an error code.', source: 'Design: Error states', where: 'ChatErrorScreen · OfflineSheet · OfflineScreen' },
      { rule: 'The contact form has two candidate designs (iOS-revamp parity, WhatsApp parity).', why: 'The team hasn’t chosen; both are props on one form so either can ship.', source: 'Design: Save Contact', where: 'NewContactForm variant', experiment: true },
      { rule: 'A missed call shows only what we know: the reason (with caller art and 👍/👎), else the name, else “Missed by the assistant”.', why: 'Inventing a reason would mislead the call-back decision. The thumbs rate the reason, so they only appear with one. The warm tint says “missed”, not “error”.', source: 'Design: missed-call card', where: 'MissedCallCard' },
      { rule: 'Never draw the system keyboard in the app.', why: 'Android owns the keyboard. The docs show a stand-in image; the app only lifts its input above the keyboard.', source: 'Engineering' },
      { rule: 'Ask for a permission only at the step that needs it, and say why in one line.', why: 'Permissions asked up front get denied. Asked in context (“I need your contact list to know who is calling”), they get granted.', source: 'Design: Known Calls flow', where: 'PermissionSheet' },
      { rule: 'Notifications: who, what they want, and Takeover first.', why: 'A notification is glanced at in a second. Takeover is the action that matters most when the assistant is on a call with someone you know.', source: 'Design: Notifications - Known Calls', where: 'CallNotification' },
      { rule: 'Silent Mode is titled “Silent Mode” everywhere.', why: 'The dark mock said “Call Preferences”; design confirmed “Silent Mode” to match the You-tab row.', source: 'Design decision' },
      { rule: 'Auto-rickshaw, paper bag and delivery rider are not caller categories.', why: 'They appeared in a design as decoration; product confirmed they shouldn’t become categories.', source: 'Product decision' },
    ],
  },
];
