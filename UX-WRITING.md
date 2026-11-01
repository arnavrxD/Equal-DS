# Equal AI UX writing guide

Every string we write for Equal AI follows this guide: app UI, notifications, the assistant's lines,
paywall copy, Figma designs and the docs site.

**Where it comes from.** The voice is read from the production copy in Statsig (Android, synced
28 and 30 Oct 2026). Where Statsig is consistent, the guide writes down what it already does. Where
Statsig is split (casing, the agent's name, bare "Yes" buttons), the guide makes the call and marks it
**Decided**. Production strings that break a rule are listed at the end, to fix in Statsig.

**Statsig still wins in code.** When a screen shows a Statsig key, the design system mirrors the
production value exactly, even if it breaks this guide. Fix it in Statsig first, then re-sync. This
guide governs every new string and every proposed change.

## 1. Voice

Equal sounds like a capable friend who handles your calls: plain, warm, calm about money, never pushy.

| We are | So we write | Not |
|---|---|---|
| Direct | Your payment didn’t go through. | Oops! Something went wrong with your transaction. |
| Reassuring | You keep full access until your renewal date. | Your subscription will be terminated. |
| On your side | Before you cancel | We’re sad to see you cancel |
| Concrete | Unlimited call recordings | Supercharge your calling experience |
| In control (you) | You choose. Equal follows. | I decide which calls to take. |
| Indian, plainly | Trusted by 10 Lakh+ Indians | Trusted by millions worldwide |

## 2. Who is speaking

- **Equal AI** is the app, the brand and the plan: “Update Equal AI”, “Equal AI Unlimited”.
- **Equal** is who acts in a sentence: “Equal answers all your calls, except the people you choose.”
- **assistant** is a lowercase common noun that describes what Equal is: “Equal, Arnav’s AI assistant”.
  It is never a name or a label on its own. **Decided** (Statsig uses Assistant, Equal and Equal AI
  for the same thing).
- **App UI talks about Equal in the third person.** Write “Equal will answer unknown numbers only”,
  not “I will answer…”.
- **Equal says “I” only when Equal itself is speaking:** in its chat bubbles, in the assistant sheet
  and on a call. Example: “I’ve learned a little more about you.”
- **The user is “you”.** Their things are “your”: Your Instructions, Your Addresses, your number.
- **On every call, Equal says it is an AI first:** “Hi Amit, this is Equal, Arnav’s AI assistant.”
  Equal never sounds like it is the user, and no setting turns this off.

## 3. Words we use

| Use | Not | Note |
|---|---|---|
| Get | Unlock | “Get Equal AI Unlimited”. The plan is something you get, not something we locked. |
| call handling | call screening | What Equal does on a call. **Decided** (Statsig has both). |
| call logs | call history | “Allow call logs permission” |
| unwanted calls | promotional calls, spam (as a label) | “Mute Unwanted Calls” |
| Exclude | Add (for removing someone from Equal) | Pick the verb by its result for the user. |
| Reset | Delete | For what Equal has learned. Delete is only for data that really goes away (account). |
| Takeover | Join, Barge in | The button that puts you on a live call. A proper name, so it keeps its capital. |
| Call back | Callback, Call Back | |
| summary, recording, transcript | notes, audio, log | Name the thing, not the tech. |
| plan | subscription, membership (in UI) | “Keep my plan”, “Your plan is cancelled” |
| weekly limit | quota, cap | “You’ve used your free calls this week” |
| Missed call | Failed call | Never “call failed”: name what happened. |

## 4. Case

- **Sentence case for everything**: titles, buttons, chips, tabs, list labels, section headers,
  notifications. **Decided** (Statsig is split about evenly; sentence case is the Android convention
  and reads as a person talking).
  - Write: “Save contact”, “Weekly limit reached”, “No addresses yet”, “Cancel your plan?”
- **Capitals only for proper nouns**: Equal, Equal AI, Equal AI Unlimited, Takeover, UPI, PhonePe,
  Google Pay, Paytm, Razorpay, people’s names, places.
- **Never type in capitals.** Section labels are set in the `overline` style, which uppercases them.
  The string stays sentence case: write “Plan active until” and the style shows PLAN ACTIVE UNTIL.

## 5. Punctuation

- **No full stop** on titles, buttons, chips, labels or one-line list rows.
- **Full stop** on body text, subtitles and helper text that are full sentences, including the last
  one.
- **Question marks** stay on questions, including titles: “Who should still ring you?”
- **Exclamation marks:** at most one, only on a success title (“You’re all set!”). Never on buttons,
  errors or money screens. “Got it” has none.
- **No em dash or en dash.** Use a comma, a colon or a full stop. Ranges use “to”: “5 to 7 working
  days”.
- **Curly apostrophes and quotes:** ’ “ ”. Never ' or ".
- **Ellipsis (…) only for something in progress**: “We’re confirming your payment…”. Never to trail
  off.
- **Middle dot (·)** only to join short trust facts on one line: “Cancel anytime · UPI secure · No
  hidden charges”.
- **Comma before a name you are addressing:** “How would you rate your call, {first_name}?”

## 6. Numbers, money and time

- **Digits, always:** 3 days, 10 AM, 24/7. Never “three days”.
- **Indian system:** 10 Lakh+, 1 crore. Never 1M or 400k+.
- **Rupees:** ₹499 (no space, no “.00”, no “Rs”). Show a per-month price as “₹99/month”.
- **Time:** 9 PM, 11:02. Dates: 12 Nov, or 12 Nov 2026 when the year matters.
- **Never hard-code a person:** use `{first_name}`.

## 7. Buttons and actions

- **Start with a verb and say the result**, in 1 to 3 words: Call back, Use Equal’s number, Keep my
  plan, Allow, Try again.
- **Never a bare “Yes”, “No”, “OK” or “Confirm”.** Name the action: “Cancel plan” beside “Keep my
  plan”. **Decided** (Statsig has “Yes”).
- **The safe choice is never the hidden one.** On a cancel or reset, the option that keeps things as
  they are is written out in full.
- **One label per intent across the app.** If it is “Call back” in one place, it is “Call back”
  everywhere.

## 8. Moments that need care

**Money (paywall, payment, renewal, cancel)**
- **Every screen that mentions a charge shows** the amount, the date, and one way out.
- **Lead with what the user gets:** “Unlimited call recordings” and “AI call summaries &
  transcripts”, not feature jargon.
- **Say what happens to their money** in plain words, with a timeline: “Any amount deducted will be
  refunded automatically in 5 to 7 working days.”
- **No guilt and no pressure:** no “Are you really sure?”, no countdowns we can’t back up. Never
  invent numbers or testimonials.

**Loss and reset**
- **Say what goes and what stays:** “Equal keeps what it’s learned, but won’t learn from future
  calls.”
- **Close with the way back:** “You can change this anytime.”

**Errors and failures**
- **Write three parts in this order:** what happened, what happens next, what you can do. Example:
  “Couldn’t use your number. Your carrier blocked the merge, so Amit hasn’t been called yet.” with the
  buttons “Try again” and “Use Equal’s number”.
- **Never stop at “Something went wrong”** or “Call failed”.
- **Never blame the user.**
- **Nothing fails silently.** If Equal falls back to another way (Equal’s number, a retry), it says
  so.

**Permissions**
- **Ask only at the step that needs it.**
- **Say why, in the user’s favour:** “Allow Equal AI to access your call logs to show the people you
  call the most, at the top.”
- **The button is “Allow”.** The way out is “Not now”.

**Empty states**
- **Title:** “No addresses yet”.
- **Then one line on what the user can do:** “Add your first instruction to control how calls are
  handled.”

## 9. Length

- **Titles:** up to about 6 words.
- **Subtitles:** one sentence, up to about 20 words.
- **Buttons:** 1 to 3 words.
- **Chips:** 1 to 3 words.
- **Notifications:** who, then what they want, in under 60 characters for the first line.
- **If a button label wraps on a 360 dp screen**, shorten the label. Never shrink the text.

## 10. Spelling

- **Indian English (British spelling):** personalise, cancelled, colour, enrol, centre.
- **Product names as their owners write them:** PhonePe, Google Pay, Paytm, WhatsApp, UPI.

## Checklist for every string

1. Is Equal named correctly (Equal AI = app/plan, Equal = actor, assistant = lowercase description)?
2. Is it sentence case, with capitals only on proper nouns?
3. Is the punctuation right for its type? No dashes, curly quotes, no stray “!”.
4. Does every button start with a verb and name its result?
5. On money, loss or error screens: does it say what happens next and give a way out?
6. Are numbers in digits, rupees as ₹, Indian units, and no hard-coded names?
7. Does it fit at 360 dp without wrapping a button?

## Production strings that break this guide (fix in Statsig)

The design system keeps these as they are in production until the Statsig key changes.

| In production | Rule | Proposed |
|---|---|---|
| Your calls quota reset in | 3, grammar | Your free calls reset in |
| Checkout how your assistant speaks in 9 languages | grammar | Check out how Equal speaks in 9 languages. |
| setup auto-pay with any UPI app | grammar | set up AutoPay with any UPI app |
| How would you rate your call Keshav? | 5, 6 | How would you rate your call, {first_name}? |
| Cancel your Plan? / Your plan is Cancelled | 4 | Cancel your plan? / Your plan is cancelled |
| FREQUENTLY ASKED QUESTIONS, PLAN ACTIVE UNTIL | 4 | Frequently asked questions (overline style) |
| Yes (cancel confirm) | 7 | Cancel plan |
| Missed by Assistant | 2, 4 | Missed by Equal |
| Your Assistant handled all {limit} of your calls this week | 2 | Equal handled all {limit} of your calls this week. |
| Talk to your assistant | 2 | Talk to Equal |
| Got it / Got it! (both live) | 5 | Got it |
| Your Equal AI is answering your call | 2, 5 | Equal is answering your call. |
| UPI only — pay with any UPI app | 5 | UPI only: pay with any UPI app like PhonePe, Google Pay or Paytm. |
| 5 - 7 working days | 5 | 5 to 7 working days |
| Unlimited call screening (FAQ, plan includes) | 3 | Unlimited AI call handling |
| Title Case labels (Save Contact, Mute Caller, Set Reminder, Weekly Limit Reached, No Addresses Yet, Delete Account, Manage Your Account, Assistant Languages, Your Instructions, Add New Address) | 4 | Same words in sentence case |
