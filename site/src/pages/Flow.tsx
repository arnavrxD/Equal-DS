import { useState, type ReactNode } from 'react';
import { AddressesScreen, InstructionSetupDemo, InstructionsFlow, PersonalDetailsScreen, SilentModeScreen, SystemStatusFlow, WhatsAppUpdatesScreen } from '../../../web/src/screens/FlowScreens';
import { ChatScreen, ContactInfoScreen, HomeScreen, SearchScreen, YouScreen } from '../../../web/src/screens/Screens';
import { PaymentResultScreen, PaywallScreen, SubscriptionScreen } from '../../../web/src/screens/MonetizationScreens';
import { sampleAddresses } from '../../../web/src/screens/flowData';
import { sampleChats } from '../../../web/src/screens/data';
import { ChatActionScreen, ChatErrorScreen } from '../../../web/src/screens/ActionFlows';
import { AlwaysRingScreen, CallHandlingScreen, DirectCallsEmptyScreen, KnownCallNotificationScreen } from '../../../web/src/screens/CallHandlingScreens';
import { H2, Page } from '../ui/kit';
import { TraceStage, type StagePhone } from '../ui/TraceStage';
import { StageCode } from '../ui/StageCode';
import NotFound from './NotFound';

type FlowDef = { title: string; lead: string; states: StagePhone[]; steps: ReactNode[]; source: string };

const SUB_H = 1841;

// Global MYA: the saved contact Amit Nanda first, as in the design.
const momChat = { ...sampleChats[0], id: 'mom', name: 'Mom', phone: '+91-81720 33604', initials: undefined, category: 'home-services' as const };
const newHome = (extra = '') => `<HomeScreen chats={[]} promo="talk-hero" assistantDescription="Equal is managing your calls!"${extra} />`;

const myaChats = [{ ...sampleChats[2], preview: 'Amit called to remind you about…' }, ...sampleChats.filter((_, i) => i !== 2)];
const mya = (stage: string) => `<HomeScreen chats={chats} promo="meet-assistant-carousel" assistant="${stage}" />`;

const flows: Record<string, FlowDef> = {
  monetization: {
    title: 'Monetization',
    lead: 'Equal Unlimited: the free-plan touchpoints that open the paywall, the paywall itself, payment results, managing and cancelling the plan, and lapsed plans. The Android team experiments here, so every screen takes the experiment knobs (price, coupon, copy, entry touchpoint) as props.',
    source: 'Monetization Flow',
    states: [
      { key: 'home-free', label: 'Home, free plan (locked chats)', render: () => <HomeScreen tier="free" lockedFrom={4} promo="meet-assistant" />, code: '<HomeScreen tier="free" lockedFrom={4} promo="meet-assistant" />' },
      { key: 'chat-locked', label: 'Chat: Unlock All Details', render: () => <ChatScreen lock="details" />, code: '<ChatScreen lock="details" />' },
      { key: 'chat-audio', label: 'Chat: Unlock Audio', render: () => <ChatScreen lock="audio" />, code: '<ChatScreen lock="audio" />' },
      { key: 'paywall', label: 'Paywall (Try it: apply, unlock)', render: () => <PaywallScreen touchpoint="chat-list" />, code: '<PaywallScreen touchpoint="chat-list" />', height: 1272 },
      { key: 'paywall-chat', label: 'Paywall from a chat', render: () => <PaywallScreen touchpoint="chat" />, code: '<PaywallScreen touchpoint="chat" />', height: 1324 },
      { key: 'paywall-audio', label: 'Paywall from audio, coupon applied', render: () => <PaywallScreen touchpoint="audio" coupon="applied" subtitle="get" />, code: '<PaywallScreen touchpoint="audio" coupon="applied" subtitle="get" />', height: 1390 },
      { key: 'waiting', label: 'Waiting for Confirmation', render: () => <PaywallScreen overlay="waiting" />, code: '<PaywallScreen overlay="waiting" />' },
      { key: 'confirmed', label: 'Payment Confirmed', render: () => <PaywallScreen overlay="confirmed" />, code: '<PaywallScreen overlay="confirmed" />' },
      { key: 'pending', label: 'Payment Pending', render: () => <PaywallScreen overlay="pending" />, code: '<PaywallScreen overlay="pending" />' },
      { key: 'failed', label: 'Payment Failed', render: () => <PaywallScreen overlay="failed" />, code: '<PaywallScreen overlay="failed" />' },
      { key: 'help', label: 'Help Center', render: () => <PaywallScreen overlay="help" />, code: '<PaywallScreen overlay="help" />' },
      { key: 'welcome', label: 'Congratulations', render: () => <PaywallScreen overlay="welcome" />, code: '<PaywallScreen overlay="welcome" />' },
      { key: 'home-paid', label: 'Home, paid', render: () => <HomeScreen promo="meet-assistant" />, code: '<HomeScreen promo="meet-assistant" />' },
      { key: 'settings-none', label: 'You: no plan yet', render: () => <YouScreen plan="none" />, code: '<YouScreen plan="none" />', height: 1400 },
      { key: 'settings', label: 'You: Manage plan', render: () => <YouScreen plan="active" />, code: '<YouScreen plan="active" />', height: 1480 },
      { key: 'subscription', label: 'Subscription (Try it: cancel)', render: () => <SubscriptionScreen />, code: '<SubscriptionScreen />', height: SUB_H },
      { key: 'cancel-feedback', label: 'Cancel: tell us why', render: () => <SubscriptionScreen cancelStep="feedback" />, code: '<SubscriptionScreen cancelStep="feedback" />' },
      { key: 'cancel-filled', label: 'Cancel: reason written', render: () => <SubscriptionScreen cancelStep="feedback" reason="Felt it too expensive, I hope it would be under my budget" />, code: '<SubscriptionScreen cancelStep="feedback" reason="Felt it too expensive, I hope it would be under my budget" />' },
      { key: 'cancel-confirm', label: 'Cancel: are you sure?', render: () => <SubscriptionScreen cancelStep="confirm" />, code: '<SubscriptionScreen cancelStep="confirm" />' },
      { key: 'cancel-submitted', label: 'Cancel: request submitted', render: () => <SubscriptionScreen cancelStep="submitted" />, code: '<SubscriptionScreen cancelStep="submitted" />' },
      { key: 'cancelled', label: 'Cancelled: Re-Subscribe', render: () => <SubscriptionScreen state="cancelled" />, code: '<SubscriptionScreen state="cancelled" />', height: SUB_H },
      { key: 'settings-restore', label: 'You: Restore Plan', render: () => <YouScreen tier="free" plan="restore" />, code: '<YouScreen tier="free" plan="restore" />', height: 1480 },
      { key: 'settings-reactivate', label: 'You: Reactivate plan', render: () => <YouScreen plan="reactivate" />, code: '<YouScreen plan="reactivate" />', height: 1530 },
      { key: 'chat-lapsed', label: 'Chat: plan lapsed', render: () => <ChatScreen lock="lapsed" />, code: '<ChatScreen lock="lapsed" />' },
    ],
    steps: [
      <><b>Touchpoints</b> (where the paywall opens from): locked rows in the chat list (blurred preview, crown-lock), a locked conversation (Unlock All Details), locked audio (Unlock Audio), Manage plan on the You tab’s plan card, and the Reactivate banners on a lapsed plan. They sit on the revamped screens as props: <code>HomeScreen lockedFrom</code>, <code>ChatScreen lock</code> (details · audio · lapsed) and <code>YouScreen plan</code> (none · active · restore · reactivate). Pass the entry as <code>touchpoint</code> so the paywall previews what the user was trying to see.</>,
      <><b>Paywall</b>: gold hero with the crown, “Unlock Equal Unlimited”, the price under LIMITED TIME DISCOUNT, the preview card, the EQUAL25 coupon (APPLY → APPLIED drops ₹249 to ₹149), FAQ, and the sticky gold Unlock Unlimited with “Cancel Anytime. Automatically renews”.</>,
      <><b>Payment</b>: Waiting for Confirmation (auto) → Payment Confirmed (redirect in 5s) → Congratulations (redirect to home). Pending offers Contact Support; Failed offers Retry Payment; both say the refund comes within 24 hours. Support opens the Help Center sheet.</>,
      <><b>After</b>: the home header shows UNLIMITED and avatars turn gold. The You tab’s plan card has Manage plan → the subscription page (plan end date, 400k+, rating, testimonials, FAQ).</>,
      <><b>Cancel</b>: tell us why (Proceed disabled until there is text) → are you really sure (Cancel / Confirm) → request submitted (redirect in 3s). Access stays until the end date; the page then offers Re-Subscribe.</>,
      <><b>Lapsed</b>: the You tab shows Restore Plan or Reactivate plan with the amber banner; chats show “Reactivate your current Plan to keep using Equal Unlimited”.</>,
      <><b>Experiments</b> (props, not forks): <code>price</code> / <code>discountedPrice</code> (₹249, ₹149), <code>coupon</code> (none · available · applied), <code>subtitle</code> (“Unlock Summaries…” · “Get Unlimited Summaries…”), <code>touchpoint</code> (chat-list · chat · audio), <code>waitingArt</code> (coin · cash), HomeScreen <code>lockedFrom</code> (how many free chats).</>,
    ],
  },
  'system-status': {
    title: 'Temporarily unavailable',
    lead: 'What Chats shows while Equal AI is down, and how it tells you it’s back. Switch to Try it and tap “Notify me when ready” to run the whole flow.',
    source: 'Temporarily Unavailable',
    states: [
      { key: 'down', label: 'Unavailable', render: () => <SystemStatusFlow stage="unavailable" />, code: '<SystemStatusFlow stage="unavailable" />' },
      { key: 'notified', label: 'Notify requested', render: () => <SystemStatusFlow stage="notified" />, code: '<SystemStatusFlow stage="notified" />' },
      { key: 'back', label: 'Back on', render: () => <SystemStatusFlow stage="back-on" />, code: '<SystemStatusFlow stage="back-on" />' },
      { key: 'live', label: 'Live flow (Try it)', render: () => <SystemStatusFlow />, code: '<SystemStatusFlow />' },
    ],
    steps: [
      <><b>Unavailable</b>: the System status card takes the assistant toggle’s place, with the sleeping robot and an amber “Notify me when ready”. The promo card is hidden while the system is down.</>,
      <><b>Notify requested</b>: the pill becomes a green “✓ You’ll be notified soon”. Nothing else on the screen changes.</>,
      <><b>Back on</b>: a heads-up notification (“Equal AI Assistant is back on”) drops in, the toggle returns as “AI Assistant is On”, and Meet Your Assistant with “Call me” sits above Conversations.</>,
    ],
  },
  profile: {
    title: 'Profile flow',
    lead: 'Personal Details, Silent Mode and WhatsApp Updates: the nested screens behind the You tab.',
    source: 'Nested Profile Screen',
    states: [
      { key: 'personal', label: 'Personal Details', render: () => <PersonalDetailsScreen onBack={() => {}} />, code: '<PersonalDetailsScreen onBack={goBack} />' },
      { key: 'silent', label: 'Silent Mode', render: () => <SilentModeScreen onBack={() => {}} />, code: '<SilentModeScreen onBack={goBack} />' },
      { key: 'wa', label: 'WhatsApp Updates', render: () => <WhatsAppUpdatesScreen onBack={() => {}} />, code: '<WhatsAppUpdatesScreen onBack={goBack} />' },
      { key: 'wa-sheet', label: 'Turn-off confirm', render: () => <WhatsAppUpdatesScreen onBack={() => {}} showSheet />, code: '<WhatsAppUpdatesScreen onBack={goBack} showSheet />' },
    ],
    steps: [
      <><b>Personal Details</b>: the name field uses the pill shape; the helper line reads “Your AI will call you ‘Keshav’” with the first name bolded. Choosing a gender card swaps the profile avatar across the app.</>,
      <><b>Silent Mode</b>: the master toggle mutes promotional calls; the five category toggles dim and lock while it’s off. Muted Contacts opens the list of people you’ve muted.</>,
      <><b>WhatsApp Updates</b>: turning the toggle off never happens silently. It opens a confirm sheet with the WhatsApp illustration and a primary “Turn off updates”.</>,
    ],
  },
  addresses: {
    title: 'Addresses flow',
    lead: 'Empty state, the saved list with an overflow menu, the Edit Address sheet and the delete confirm.',
    source: 'Addresses',
    states: [
      { key: 'empty', label: 'Empty', render: () => <AddressesScreen onBack={() => {}} initial={[]} />, code: '<AddressesScreen onBack={goBack} initial={[]} />' },
      { key: 'list', label: 'Saved addresses', render: () => <AddressesScreen onBack={() => {}} />, code: '<AddressesScreen onBack={goBack} />' },
      { key: 'edit', label: 'Edit Address sheet', render: () => <AddressesScreen onBack={() => {}} openEdit={sampleAddresses[3].id} />, code: '<AddressesScreen onBack={goBack} openEdit={sampleAddresses[3].id} />' },
      { key: 'delete', label: 'Delete confirm', render: () => <AddressesScreen onBack={() => {}} confirmDelete={sampleAddresses[0].id} />, code: '<AddressesScreen onBack={goBack} confirmDelete={sampleAddresses[0].id} />' },
    ],
    steps: [
      <><b>Empty</b>: the illustration, “Never explain your address again”, and a pinned “Add new Address”.</>,
      <><b>List</b>: each card shows its label and address, with ⋮ → Edit / Delete address.</>,
      <><b>Edit / Add</b>: a titled sheet with LABEL* chips (the selected chip is dark), two address lines, City + Pincode (6 digits, checked), an optional guiding direction and Save.</>,
      <><b>Delete</b>: a confirm sheet with the trash illustration and a destructive Delete.</>,
    ],
  },
  instructions: {
    title: 'Instructions flow',
    lead: 'The hub, presets, the “Setting up instruction” chat and Active Instructions. Switch to Try it and apply an instruction end to end.',
    source: 'Instructions',
    states: [
      { key: 'hub', label: 'Hub', render: () => <InstructionsFlow initialActive={[]} />, code: '<InstructionsFlow initialActive={[]} />' },
      { key: 'preset', label: 'Preset: Screen & Protect', render: () => <InstructionsFlow initialActive={[]} initialView={{ preset: 'screen' }} />, code: '<InstructionsFlow initialActive={[]} initialView={{ preset: \'screen\' }} />' },
      { key: 'setup', label: 'Setup chat, no input', render: () => <InstructionSetupDemo which="decline" />, code: '<InstructionSetupDemo which="decline" />' },
      { key: 'setup-input', label: 'Setup chat, input needed', render: () => <InstructionSetupDemo which="delivery" />, code: '<InstructionSetupDemo which="delivery" />' },
      { key: 'active', label: 'Active instructions', render: () => <InstructionsFlow initialView="active" />, code: '<InstructionsFlow initialView="active" />' },
      { key: 'active-empty', label: 'Active, empty', render: () => <InstructionsFlow initialActive={[]} initialView="active" />, code: '<InstructionsFlow initialActive={[]} initialView="active" />' },
      { key: 'no-recs', label: 'No recommendations', render: () => <InstructionsFlow noRecommendations />, code: '<InstructionsFlow noRecommendations />' },
    ],
    steps: [
      <><b>Hub</b>: horizontal recommended cards (or “New suggestions coming…” once they’re all applied), an Active Instructions row, three presets and a pinned “Add New Instruction”.</>,
      <><b>Preset</b>: the description plus instruction cards with When / Do rows and + Apply, which turns into Applied.</>,
      <><b>Setup chat, no input</b>: You → Equal AI “Got it! Here’s your instruction:” → preview → ✍️ Edit / 👍 Confirm. Confirm adds ✅ and saves.</>,
      <><b>Setup chat, input needed</b>: Equal AI asks each follow-up (the landmark, then the wait time); the answers are bolded in the preview → ✏️ Change something else / 👌 That’s all.</>,
      <><b>Active</b>: cards with a source chip, WHEN / DO, and ⋮ Edit / Delete instruction; plus an empty state.</>,
    ],
  },
  'plan-renewal': {
    title: 'Plan value & renewal',
    lead: 'Before a plan ends, show what it did for the user, then make renewing one tap. Home carries the expiry strip, a call near the end brings up the What Unlimited saved you sheet, and Manage plan leads to the renewal page and the paywall.',
    source: 'Monetization value, Monetization local components',
    states: [
      { key: 'home-expiring', label: 'Home: expires in 2 days', render: () => <HomeScreen planExpiry={{ state: 'expiring', days: 2 }} promo="meet-assistant" />, code: "<HomeScreen planExpiry={{ state: 'expiring', days: 2 }} promo=\"meet-assistant\" />" },
      { key: 'home-today', label: 'Home: expires today', render: () => <HomeScreen planExpiry={{ state: 'today' }} promo="meet-assistant" />, code: "<HomeScreen planExpiry={{ state: 'today' }} promo=\"meet-assistant\" />" },
      { key: 'home-expired', label: 'Home: expired', render: () => <HomeScreen tier="free" planExpiry={{ state: 'expired', date: '30th July' }} promo="meet-assistant" />, code: "<HomeScreen tier=\"free\" planExpiry={{ state: 'expired', date: '30th July' }} promo=\"meet-assistant\" />" },
      { key: 'value-sheet', label: 'Value sheet (Try it: close)', render: () => <HomeScreen planExpiry={{ state: 'expiring', days: 3 }} valueSheet promo="meet-assistant" />, code: "<HomeScreen planExpiry={{ state: 'expiring', days: 3 }} valueSheet promo=\"meet-assistant\" />" },
      { key: 'value-sheet-flush', label: 'Value sheet, flush (experiment)', render: () => <HomeScreen planExpiry={{ state: 'expiring', days: 3 }} valueSheet="flush" promo="meet-assistant" />, code: "<HomeScreen planExpiry={{ state: 'expiring', days: 3 }} valueSheet=\"flush\" promo=\"meet-assistant\" />" },
      { key: 'value-sheet-today', label: 'Value sheet: expires today', render: () => <HomeScreen planExpiry={{ state: 'today' }} valueSheet promo="meet-assistant" />, code: "<HomeScreen planExpiry={{ state: 'today' }} valueSheet promo=\"meet-assistant\" />" },
      { key: 'value-sheet-expired', label: 'Value sheet: expired', render: () => <HomeScreen tier="free" planExpiry={{ state: 'expired', date: '30th July' }} valueSheet promo="meet-assistant" />, code: "<HomeScreen tier=\"free\" planExpiry={{ state: 'expired', date: '30th July' }} valueSheet promo=\"meet-assistant\" />" },
      { key: 'quota-left', label: 'Free: 6 of 30 calls left', render: () => <HomeScreen tier="free" quotaSheet={{ used: 24, limit: 30, resetsInDays: 4 }} promo="meet-assistant" />, code: '<HomeScreen tier="free" quotaSheet={{ used: 24, limit: 30, resetsInDays: 4 }} promo="meet-assistant" />' },
      { key: 'quota-limit', label: 'Free: limit reached', render: () => <HomeScreen tier="free" lockedFrom={4} quotaSheet={{ used: 30, limit: 30, resetsInDays: 2 }} promo="meet-assistant" />, code: '<HomeScreen tier="free" lockedFrom={4} quotaSheet={{ used: 30, limit: 30, resetsInDays: 2 }} promo="meet-assistant" />' },
      { key: 'you-active', label: 'You: plan active', render: () => <YouScreen plan="active" />, code: '<YouScreen plan="active" />', height: 1480 },
      { key: 'manage-active', label: 'Manage plan: active', render: () => <SubscriptionScreen />, code: '<SubscriptionScreen />', height: SUB_H },
      { key: 'you-expired', label: 'You: plan expired', render: () => <YouScreen tier="free" plan="expired" />, code: '<YouScreen tier="free" plan="expired" />', height: 1480 },
      { key: 'manage-expired', label: 'Manage plan: expired (Renew)', render: () => <SubscriptionScreen state="expired" />, code: '<SubscriptionScreen state="expired" />', height: SUB_H },
      { key: 'renew-paywall', label: 'Renew: paywall (Try it: apply)', render: () => <PaywallScreen touchpoint="chat-list" cta="Get Equal AI Unlimited" />, code: '<PaywallScreen touchpoint="chat-list" cta="Get Equal AI Unlimited" />', height: 1272 },
      { key: 'progress', label: 'Payment progress steps', render: () => <PaywallScreen overlay="waiting" cta="Get Equal AI Unlimited" />, code: '<PaywallScreen overlay="waiting" cta="Get Equal AI Unlimited" />' },
      { key: 'confirmed', label: 'Payment Confirmed', render: () => <PaywallScreen overlay="confirmed" cta="Get Equal AI Unlimited" />, code: '<PaywallScreen overlay="confirmed" cta="Get Equal AI Unlimited" />' },
    ],
    steps: [
      <><b>Expiry strip</b> under the home header: amber “Your Plan Expires in 2 Days”, red “Expires Today” and “Expired on 30th July”, each with Renew Now. Pass <code>planExpiry</code> to HomeScreen; nothing shows while the plan is comfortably active.</>,
      <><b>Value sheet once called</b>: after the assistant handles a call near the end of the plan, the What Unlimited saved you sheet rises over home: calls handled, three perks, the expiry chip (03 Days amber, Today / 30th July red) and the current plan, with Renew your Equal AI. Close floats above the sheet. <code>valueSheet="flush"</code> is the experiment where the card is the sheet. In dark mode the card turns black with a gold glow.</>,
      <><b>Free plan quota</b>: the same sheet for free users counts the week’s calls. Under the limit it reads “6 out of 30 Calls Left” with a gold progress bar; at the limit, “Limit Reached · 30 out of 30 (100%)”. Both show when the quota resets and Subscribe to Equal AI Unlimited at ₹149, with Get Equal AI Unlimited opening the paywall. Pass <code>quotaSheet</code> to HomeScreen.</>,
      <><b>Plan card</b>: active shows Current Plan; expired shows the red Plan Expired pill and a plain avatar. Manage plan opens the plan page either way.</>,
      <><b>Plan page</b>: Equal AI Unlimited hero, What Unlimited Saved You (calls, plan active until or expired on, price), What You Get, social proof and FAQ. The plan action is pinned: Cancel Subscription (gold outline) while active, Renew your Equal AI once expired, which opens the paywall with Get Equal AI Unlimited.</>,
      <><b>Motion</b> (local components): APPLY strikes the old price, lands the new one with the list price and “Saved ₹100”, and pops the Special Coupon Applied! badge. The gold CTA has a sheen sweep. The waiting sheet steps through Connecting To Razorpay → Waiting for Confirmation → Verifying Transaction, then Payment Confirmed with the ray burst behind the seal. Everything holds still under reduced motion.</>,
    ],
  },
  'global-mya': {
    title: 'Global MYA',
    lead: 'Tap the green orb anywhere and your assistant answers in a sheet: a greeting, quick actions and suggestions, then a request typed or spoken. The request turns into a short task conversation that ends with the result to confirm.',
    source: 'Global MYA',
    states: [
      { key: 'home', label: 'Home (Try it: tap the orb)', render: () => <HomeScreen chats={myaChats} promo="meet-assistant-carousel" />, code: '<HomeScreen chats={chats} promo="meet-assistant-carousel" />' },
      { key: 'greeting', label: 'Greeting & quick actions', render: () => <HomeScreen chats={myaChats} promo="meet-assistant-carousel" assistant="greeting" />, code: mya('greeting') },
      { key: 'voice', label: 'Voice note option', render: () => <HomeScreen chats={myaChats} promo="meet-assistant-carousel" assistant="voice" />, code: mya('voice') },
      { key: 'typing', label: 'Keyboard open', render: () => <HomeScreen chats={myaChats} promo="meet-assistant-carousel" assistant="typing" />, code: mya('typing') },
      { key: 'typed', label: 'Text typed', render: () => <HomeScreen chats={myaChats} promo="meet-assistant-carousel" assistant="typed" />, code: mya('typed') },
      { key: 'voice-prompt', label: 'Voice input prompt', render: () => <HomeScreen chats={myaChats} promo="meet-assistant-carousel" assistant="voice-prompt" />, code: mya('voice-prompt') },
      { key: 'recording', label: 'Recording', render: () => <HomeScreen chats={myaChats} promo="meet-assistant-carousel" assistant="recording" />, code: mya('recording') },
      { key: 'hold', label: 'Hold to record: slide to cancel', render: () => <HomeScreen chats={myaChats} promo="meet-assistant-carousel" assistant="hold" />, code: mya('hold') },
      { key: 'hold-cancel', label: 'Hold to record: release to cancel', render: () => <HomeScreen chats={myaChats} promo="meet-assistant-carousel" assistant="hold-cancel" />, code: mya('hold-cancel') },
      { key: 'sent', label: 'Voice note sent', render: () => <HomeScreen chats={myaChats} promo="meet-assistant-carousel" assistant="sent" />, code: mya('sent') },
      { key: 'thinking', label: 'Thinking', render: () => <HomeScreen chats={myaChats} promo="meet-assistant-carousel" assistant="thinking" />, code: mya('thinking') },
      { key: 'reminder', label: 'Reminder card', render: () => <HomeScreen chats={myaChats} promo="meet-assistant-carousel" assistant="reminder" />, code: mya('reminder') },
      { key: 'confirmed', label: 'Reminder confirmed', render: () => <HomeScreen chats={myaChats} promo="meet-assistant-carousel" assistant="confirmed" />, code: mya('confirmed') },
      { key: 'after', label: 'Home: reminder added', render: () => <HomeScreen chats={myaChats} promo="meet-assistant-carousel" assistantResult={{ chatId: 'c3', preview: 'Reminder added' }} />, code: "<HomeScreen chats={chats} assistantResult={{ chatId: 'c3', preview: 'Reminder added' }} />" },
      { key: 'chat-ai', label: 'In a chat: the AI layer (Try it)', render: () => <ChatScreen chat={sampleChats[2]} />, code: '<ChatScreen chat={amit} />' },
      { key: 'chat-task', label: 'In a chat: Replying Amit on WhatsApp', render: () => <ChatScreen chat={sampleChats[2]} aiTask="draft" />, code: '<ChatScreen chat={amit} aiTask="draft" />' },
      { key: 'chat-sending', label: 'In a chat: Opening WhatsApp', render: () => <ChatScreen chat={sampleChats[2]} aiTask="sending" />, code: '<ChatScreen chat={amit} aiTask="sending" />' },
    ],
    steps: [
      <><b>Open</b>: the green orb (the nav FAB) opens the sheet over whatever screen you’re on: orb, “Good morning, Keshav” (name in green), “What can I help you with today?”, three quick-action tiles, suggestion chips, and a message field with the mic.</>,
      <><b>Type</b>: focusing the field hides the tiles and lifts the input above the keyboard (green outline). Enter sends.</>,
      <><b>Speak</b>: the mic swaps the field for <b>Send voice note</b> with a keyboard toggle. Tap it to record: “Try saying …”, then delete, the timer with a live waveform, and send. Or press and hold: “Release to send, slide left to cancel”, and sliding left turns the waveform red (“Release to cancel”).</>,
      <><b>Task</b>: once sent, the sheet grows into a conversation titled with what the assistant is doing (“Setting up a reminder…”), with close. Your voice note (player + transcript) → <i>Thinking…</i> → the result card (here a WhatsApp reminder) with Edit / Confirm.</>,
      <><b>Inside a chat</b>: the chat’s own AI layer (“Want me to send Amit a quick reply on WhatsApp?”) works the same way. Tapping a chip or sending a message raises the task sheet, which stops below the chat header so you can still see whose chat it is. “Replying Amit on WhatsApp” shows the drafted reply with ✏️ Edit / 📤 Send and the 📤 Send · ✏️ Edit draft · 🖋️ Custom chips. Confirming shows “Opening WhatsApp…” and greys the chips out.</>,
      <><b>Done</b>: Confirm ticks the card, the sheet closes, and the chat it acted on now reads “Reminder added”. Saved contacts carry the person badge on their initials.</>,
    ],
  },
  'monetization-touchpoints': {
    title: 'Monetization touchpoints',
    lead: 'Where a free user meets Equal Unlimited. Each touchpoint is an experiment option the Android team A/B tests, built as props on the same Home, Chat and Paywall screens.',
    source: 'Plan Expired Touchpoint · Usage Cap Touchpoint (options 1 & 2) · Touchpoint: Actions on Home Page · Recordings + Transcripts Locked (with / without actions) · Component 25',
    states: [
      { key: 'expired-home', label: 'Plan expired 1: Equal AI chat', render: () => <HomeScreen tier="free" planExpired official="plan-expired" />, code: '<HomeScreen tier="free" planExpired official="plan-expired" />' },
      { key: 'expired-chat', label: 'Plan expired 1: the message', render: () => <ChatScreen official="plan-expired" />, code: '<ChatScreen official="plan-expired" />' },
      { key: 'expired-paywall', label: 'Plan expired 1: paywall', render: () => <PaywallScreen touchpoint="recordings" cta="Get Equal AI Unlimited" />, code: '<PaywallScreen touchpoint="recordings" cta="Get Equal AI Unlimited" />', height: 1323 },
      { key: 'expired-card', label: 'Plan expired 2: home card', render: () => <HomeScreen tier="free" planExpired homeNotice="plan-expired" />, code: '<HomeScreen tier="free" planExpired homeNotice="plan-expired" />' },
      { key: 'cap-chat-home', label: 'Usage cap 1: Equal AI chat', render: () => <HomeScreen tier="free" assistantOff official="usage-cap" />, code: '<HomeScreen tier="free" assistantOff official="usage-cap" />' },
      { key: 'cap-chat', label: 'Usage cap 1: the message', render: () => <ChatScreen official="usage-cap" />, code: '<ChatScreen official="usage-cap" />' },
      { key: 'cap-card', label: 'Usage cap 2: calls left', render: () => <HomeScreen tier="free" assistantOff homeNotice={{ callsLeft: 7, limit: 30 }} />, code: '<HomeScreen tier="free" assistantOff homeNotice={{ callsLeft: 7, limit: 30 }} />' },
      { key: 'actions-home', label: 'Actions locked: home (Try it)', render: () => <HomeScreen tier="free" lockedActions paywallSheet promo="meet-assistant-carousel" />, code: '<HomeScreen tier="free" lockedActions paywallSheet promo="meet-assistant-carousel" />' },
      { key: 'actions-paywall', label: 'Actions locked: paywall', render: () => <PaywallScreen touchpoint="actions" subtitle="actions" cta="Get Equal AI Unlimited" />, code: '<PaywallScreen touchpoint="actions" subtitle="actions" cta="Get Equal AI Unlimited" />', height: 1255 },
      { key: 'recordings', label: 'Recordings locked (without actions)', render: () => <ChatScreen chat={sampleChats[1]} lock="details" />, code: '<ChatScreen chat={sampleChats[1]} lock="details" />' },
      { key: 'recordings-actions', label: 'Recordings locked (with actions)', render: () => <ChatScreen chat={sampleChats[1]} lock="details" summaryActions />, code: '<ChatScreen chat={sampleChats[1]} lock="details" summaryActions />' },
      { key: 'recordings-paywall', label: 'Recordings locked: paywall', render: () => <PaywallScreen touchpoint="recordings-actions" cta="Get Equal AI Unlimited" />, code: '<PaywallScreen touchpoint="recordings-actions" cta="Get Equal AI Unlimited" />', height: 1419 },
      { key: 'phone-toggle', label: 'Toggle art: phone (Component 25)', render: () => <HomeScreen assistantArt="phone" promo="meet-assistant" />, code: '<HomeScreen assistantArt="phone" promo="meet-assistant" />' },
    ],
    steps: [
      <><b>Plan expired, option 1</b>: the red <b>PLAN EXPIRED</b> badge, and Equal AI’s own verified chat on top (“Your Plan Expired…”, 👑 Renew Now). Its message is a voice note plus Renew Equal AI Unlimited, which opens the paywall previewing a locked summary.</>,
      <><b>Plan expired, option 2</b>: no chat, a card under the header instead (👑 Your Plan Expired · Renew Now).</>,
      <><b>Usage cap</b>: the assistant switches Off at the weekly limit. Option 1 is again an Equal AI message (“Weekly call limit reached…”). Option 2 is a meter card: “7 Calls left this week · Resets Mon | Unlimited on premium” with Get Unlimited.</>,
      <><b>Actions locked</b>: action chips turn gold with the crown-lock. Tapping one opens the paywall (here the sheet) with “Unlock Unlimited Actions…” and the locked row as its preview.</>,
      <><b>Recordings locked</b>: bubbles blur, the recording washes gold under the crown-lock, and Unlock All Details sits at the bottom. With actions, Call Back / Add to Calendar stay visible under the summary and show locked in the paywall preview.</>,
      <><b>Toggle art</b> (Component 25): the phone-with-sparkles is an experiment on the toggle; the robot stays the default.</>,
    ],
  },
  'paywall-sheet': {
    title: 'Sheet paywall & payment',
    lead: 'The experiment that opens the paywall as a sheet over the screen the user was on, offers EQUAL25 when they try to leave, and shows full-screen payment results. If the user closes a pending payment, every surface says it is processing and the paywall locks.',
    source: 'Light / Dark Mode: Home Screen · Conversation Screen · All Payment States · Payment Pending: Home Screen · If User Closes the payment pending page · Dark Mode_Monetization',
    states: [
      { key: 'sheet', label: 'Sheet paywall (Try it)', render: () => <HomeScreen tier="free" paywallSheet="sheet" />, code: '<HomeScreen tier="free" paywallSheet="sheet" />' },
      { key: 'help', label: 'Help Center sheet', render: () => <HomeScreen tier="free" paywallSheet="help" />, code: '<HomeScreen tier="free" paywallSheet="help" />' },
      { key: 'exit', label: 'Leave Without Unlocking?', render: () => <HomeScreen tier="free" paywallSheet="exit" />, code: '<HomeScreen tier="free" paywallSheet="exit" />' },
      { key: 'offer', label: 'Declined: offer strip', render: () => <HomeScreen tier="free" offerStrip promo="meet-assistant-carousel" />, code: '<HomeScreen tier="free" offerStrip promo="meet-assistant-carousel" />' },
      { key: 'claimed', label: 'Claimed: EQUAL25 applied', render: () => <HomeScreen tier="free" paywallSheet="claimed" />, code: '<HomeScreen tier="free" paywallSheet="claimed" />' },
      { key: 'chat-sheet', label: 'From a chat: sheet', render: () => <ChatScreen chat={sampleChats[1]} lock="details" summaryActions paywallSheet="sheet" />, code: '<ChatScreen chat={sampleChats[1]} lock="details" summaryActions paywallSheet="sheet" />' },
      { key: 'chat-exit', label: 'From a chat: Leave / Keep 25% Off', render: () => <ChatScreen chat={sampleChats[1]} lock="details" summaryActions paywallSheet="exit" />, code: '<ChatScreen chat={sampleChats[1]} lock="details" summaryActions paywallSheet="exit" />' },
      { key: 'success', label: 'Congratulations', render: () => <PaymentResultScreen status="success" />, code: '<PaymentResultScreen status="success" />' },
      { key: 'pending', label: 'Payment Pending (Try it: Refresh)', render: () => <PaymentResultScreen status="pending" />, code: '<PaymentResultScreen status="pending" />' },
      { key: 'failed', label: 'Payment Failed (Try it: Retry)', render: () => <PaymentResultScreen status="failed" />, code: '<PaymentResultScreen status="failed" />' },
      { key: 'pending-support', label: 'Pending: Contact Support', render: () => <PaymentResultScreen status="pending" pendingAction="support" />, code: '<PaymentResultScreen status="pending" pendingAction="support" />' },
      { key: 'processing-chat', label: 'Closed pending: chat strip', render: () => <ChatScreen chat={sampleChats[1]} lock="details" summaryActions paymentProcessing />, code: '<ChatScreen chat={sampleChats[1]} lock="details" summaryActions paymentProcessing />' },
      { key: 'processing-paywall', label: 'Closed pending: paywall locked', render: () => <PaywallScreen touchpoint="recordings-actions" processing />, code: '<PaywallScreen touchpoint="recordings-actions" processing />', height: 1483 },
      { key: 'processing-home', label: 'Closed pending: home card', render: () => <HomeScreen paymentProcessing promo="meet-assistant" />, code: '<HomeScreen paymentProcessing promo="meet-assistant" />' },
    ],
    steps: [
      <><b>Sheet</b>: a compact gold card (crown, Equal AI Unlimited, “Trusted by 10 Lakh+ Indians”, ₹249, EQUAL25 · APPLY) with Unlock Equal AI Unlimited. Support opens the <b>Help Center</b> FAQ sheet, and back returns.</>,
      <><b>Exit offer</b>: tapping outside asks <b>Leave Without Unlocking?</b> “Claim 25% off with EQUAL25 first, go Unlimited for ₹149/month.” On home the buttons read Not Now / Claim 25% Off; in a chat, Leave / Keep 25% Off. Claim reopens the sheet at ₹149 with APPLIED. Not Now closes it and leaves the <b>Go unlimited for ₹149 with EQUAL25 · Claim Now</b> strip on home.</>,
      <><b>Results</b> are full screens on a tinted wash with rings. <b>Congratulations</b>, You’re Unlimited, shows Plan active till. <b>Payment Pending</b> has × and Refresh Status, or Contact Support. <b>Payment Failed</b> has ‹ and suggests another UPI app, with Retry payment and Contact Support.</>,
      <><b>Closed while pending</b>: a “Payment processing · We’ll update your order once it’s confirmed.” strip under the chat header and as a card under the home toggle. On the paywall it’s a toast above a disabled Unlock Unlimited, so nobody pays twice.</>,
      <><b>Dark mode</b>: everything follows the theme switch (black and gold paywall, deep green / brown / red result washes, lime buttons).</>,
    ],
  },
  'call-handling': {
    title: 'Call Handling',
    lead: 'You → Your assistant → Call Handling: which calls the assistant answers and who always rings you. New users are invited into it from home: two questions, one minute. The wording is an experiment, so the question screen takes a copy prop.',
    source: 'Known Calls (new users)',
    states: [
      { key: 'home-new', label: 'Home, new user', render: () => <HomeScreen chats={[]} promo="talk-hero" assistantDescription="Equal is managing your calls!" />, code: newHome() },
      { key: 'setup-prompt', label: 'Set up your calls', render: () => <HomeScreen chats={[]} promo="talk-hero" assistantDescription="Equal is managing your calls!" callSetup="prompt" />, code: newHome(' callSetup="prompt"') },
      { key: 'question', label: 'Which calls? (Try it)', render: () => <CallHandlingScreen />, code: '<CallHandlingScreen />' },
      { key: 'exclude', label: 'Exclude contacts', render: () => <CallHandlingScreen step="exclude" picked={[]} />, code: '<CallHandlingScreen step="exclude" picked={[]} />', height: 1060 },
      { key: 'exclude-picked', label: 'Exclude contacts: 2 picked', render: () => <CallHandlingScreen step="exclude" />, code: '<CallHandlingScreen step="exclude" />', height: 1060 },
      { key: 'perfect', label: 'Perfect!', render: () => <CallHandlingScreen step="perfect" />, code: '<CallHandlingScreen step="perfect" />' },
      { key: 'unknown', label: 'Only unknown numbers', render: () => <CallHandlingScreen mode="unknown" />, code: '<CallHandlingScreen mode="unknown" />' },
      { key: 'whose', label: 'Copy experiment: whose calls', render: () => <CallHandlingScreen copy="whose" mode="unknown" />, code: '<CallHandlingScreen copy="whose" mode="unknown" />' },
      { key: 'sorted', label: 'All Sorted!', render: () => <CallHandlingScreen step="sorted" mode="unknown" />, code: '<CallHandlingScreen step="sorted" mode="unknown" />' },
      { key: 'no-permission', label: 'No contacts permission', render: () => <CallHandlingScreen copy="whose" mode="unknown" contactsAllowed={false} />, code: '<CallHandlingScreen copy="whose" mode="unknown" contactsAllowed={false} />' },
      { key: 'perm-contacts', label: 'Allow contacts', render: () => <CallHandlingScreen copy="whose" step="perm-contacts" contactsAllowed={false} />, code: '<CallHandlingScreen copy="whose" step="perm-contacts" contactsAllowed={false} />' },
      { key: 'perm-history', label: 'Allow call history', render: () => <CallHandlingScreen copy="whose" step="perm-history" />, code: '<CallHandlingScreen copy="whose" step="perm-history" />' },
      { key: 'finish', label: 'Finish your setup?', render: () => <HomeScreen chats={[]} promo="talk-hero" assistantDescription="Equal is managing your calls!" callSetup="finish" />, code: newHome(' callSetup="finish"') },
      { key: 'always-ring', label: 'Always ring me (edit)', render: () => <AlwaysRingScreen />, code: '<AlwaysRingScreen onBack={goBack} />', height: 1180 },
      { key: 'no-contacts', label: 'Direct Calls: no contacts', render: () => <DirectCallsEmptyScreen />, code: '<DirectCallsEmptyScreen onBack={goBack} onDone={goBack} />' },
      { key: 'you', label: 'You: Call Handling row', render: () => <YouScreen />, code: '<YouScreen onOpen={(page) => page === "call-handling" && open("call-handling")} />', height: 1550 },
    ],
    steps: [
      <><b>Where it lives</b>: You → Your assistant → <b>Call Handling</b> (“Choose which calls I answer”). New users see the “Talk With Your Assistant” home, then the dark <b>Set up your calls</b> takeover: “Two questions. One minute.”</>,
      <><b>Question 1</b>: “Which calls should I answer? You decide. I follow.” Two radio cards: <b>All calls, except a few</b> and <b>Only unknown numbers</b>. The copy experiment (<code>copy="whose"</code>) asks “Whose calls should I answer?”, tags All calls as ⭐ Suggested, and says what each option means for your contacts.</>,
      <><b>All calls</b>: needs the contacts permission (sheet: “I need your contact list to know who is calling.”) and offers call history so frequent callers sort first. Then <b>Anyone you want to answer yourself?</b>: search, suggested people, all contacts with checkboxes and an A–Z index; the button counts the picks. Ends on <b>Perfect!</b> naming who will always ring you.</>,
      <><b>Only unknown numbers</b>: ends on <b>All Sorted!</b> (“Your contacts will ring you, like always.”). Without the permission, All calls is dimmed and a blue note offers <i>Give contacts permission ›</i>.</>,
      <><b>Later</b>: if the user stops halfway, home shows <b>Finish your setup?</b> with Finish or <i>Skip, answer everyone</i>. <b>Always ring me</b> edits the list; with no saved contacts, <b>Direct Calls</b> explains why it’s empty. The hero art on the contact screens is also a Lottie (<code>call-handling-contacts</code>).</>,
    ],
  },
  'known-calls': {
    title: 'Known-call notifications',
    lead: 'When the assistant answers someone you know, Android shows an ongoing notification that tells you who it is and what they want, with quick replies, and hands the call to you when it needs you. Opening it shows the live chat.',
    source: 'Known-call notifications',
    states: [
      { key: 'answering', label: 'Answering', render: () => <KnownCallNotificationScreen state="answering" />, code: '<CallNotification name="Mom" state="answering" />' },
      { key: 'summary', label: 'What they want', render: () => <KnownCallNotificationScreen state="summary" />, code: '<CallNotification name="Mom" state="summary" summary={liveSummary} />' },
      { key: 'expanded', label: 'Expanded: quick replies', render: () => <KnownCallNotificationScreen state="expanded" />, code: '<CallNotification name="Mom" state="expanded" replies={["Takeover", "I’ll bring them all", "I’ll call back to get more"]} onReply={reply} onAnswer={answer} />' },
      { key: 'connecting', label: 'Needs you: connecting', render: () => <KnownCallNotificationScreen state="connecting" />, code: '<CallNotification name="Mom" state="connecting" />' },
      { key: 'chat', label: 'Live chat', render: () => <ChatScreen chat={momChat} live ongoing="02:34" />, code: '<ChatScreen chat={mom} live ongoing="02:34" />' },
    ],
    steps: [
      <><b>Answering</b>: “Equal AI • Ongoing” with a live timer; the caller (their avatar) and “Your Equal AI is answering your call.”, with the Equal app badge.</>,
      <><b>Summary</b>: the line updates to what the caller wants as the call goes on.</>,
      <><b>Expanded</b>: two lines, a call button to pick up, and quick replies: <b>Takeover</b> first, then suggested answers the assistant can say for you.</>,
      <><b>Hand-off</b>: when it needs you, the replies give way to “This needs your attention, connecting this call to you”.</>,
      <><b>Open</b>: the chat shows the call live under a black <b>Ongoing call - 02:34</b> strip with Takeover in the header and the assistant’s follow-up below. Android draws the notification shell; the system wallpaper here is a coded stand-in.</>,
    ],
  },
  search: {
    title: 'Search',
    lead: 'Search from home: names, numbers, categories and keywords across calls. Results arrive in two passes, chats first and messages after, so something useful shows while the slower search finishes.',
    source: 'Search',
    states: [
      { key: 'empty', label: 'Empty', render: () => <SearchScreen keyboard state="empty" />, code: '<SearchScreen keyboard state="empty" />' },
      { key: 'offline', label: 'No internet', render: () => <SearchScreen keyboard state="offline" />, code: '<SearchScreen keyboard state="offline" />' },
      { key: 'recent', label: 'Recent searches (Try it: type)', render: () => <SearchScreen keyboard />, code: '<SearchScreen keyboard />' },
      { key: 'no-results', label: 'No results', render: () => <SearchScreen keyboard state="no-results" query="Xyz" />, code: '<SearchScreen keyboard state="no-results" query="Xyz" />' },
      { key: 'loading', label: 'Loading', render: () => <SearchScreen keyboard state="loading" query="S" />, code: '<SearchScreen keyboard state="loading" query="S" />' },
      { key: 'results', label: 'Results: chats', render: () => <SearchScreen keyboard state="results" query="S" />, code: '<SearchScreen keyboard state="results" query="S" />' },
      { key: 'messages-loading', label: 'Results: messages loading', render: () => <SearchScreen keyboard state="messages-loading" query="S" />, code: '<SearchScreen keyboard state="messages-loading" query="S" />', height: 980 },
      { key: 'messages', label: 'Results: messages included', render: () => <SearchScreen keyboard state="messages" query="Sa" />, code: '<SearchScreen keyboard state="messages" query="Sa" />', height: 980 },
    ],
    steps: [
      <><b>Open</b>: tapping “Search for names” on home opens search with the keyboard up. The bar is a green-outlined pill with back inside it.</>,
      <><b>Before typing</b>: recent searches show as avatar tiles with Clear all. With none, the robot and “Search across your calls · Look for names, phone numbers, categories, keywords”.</>,
      <><b>Typing</b>: green dots under CHATS, then matching chats (with their action chips). Messages load next under MESSAGES, each hit showing who, when, and the line with the match in <b>bold</b>.</>,
      <><b>Nothing found</b>: the thinking robot, “No results found”, and a nudge to try another name, number, category or keyword.</>,
      <><b>Offline</b>: the struck-through wifi, “No internet connection”. The same art works anywhere the network is needed.</>,
    ],
  },
  'call-back': {
    title: 'Call Back',
    lead: 'The AI layer calls someone back for you: it suggests what to ask, hands off to the phone’s dialer, and leaves an Outgoing call card in the chat.',
    source: 'Call Back',
    states: [
      { key: 'live', label: 'Chat (Try it: tap Call Back)', render: () => <ChatActionScreen action="call-back" />, code: '<ChatActionScreen action="call-back" />' },
      { key: 'ask', label: 'Calling: what to ask', render: () => <ChatActionScreen action="call-back" step="ask" />, code: '<ChatActionScreen action="call-back" step="ask" />' },
      { key: 'dialer', label: 'Dialer (system, stand-in)', render: () => <ChatActionScreen action="call-back" step="dialer" />, code: '<ChatActionScreen action="call-back" step="dialer" />' },
      { key: 'done', label: 'Back in the chat', render: () => <ChatActionScreen action="call-back" step="done" />, code: '<ChatActionScreen action="call-back" step="done" />' },
      { key: 'missed-reason', label: 'Missed call: with reason (Try it)', render: () => <ChatActionScreen action="call-back" missed="reason" />, code: '<ChatActionScreen action="call-back" missed="reason" />' },
      { key: 'missed-name', label: 'Missed call: name only', render: () => <ChatActionScreen action="call-back" missed="name" />, code: '<ChatActionScreen action="call-back" missed="name" />' },
      { key: 'missed-unknown', label: 'Missed call: caller unknown', render: () => <ChatActionScreen action="call-back" missed="unknown" />, code: '<ChatActionScreen action="call-back" missed="unknown" />' },
    ],
    steps: [
      <><b>Prompt</b>: “Want me to give a quick call back to the clinic?” with 📞 Call Back, 🗓️ Add to Calendar and 🔔 Set reminder chips.</>,
      <><b>Task sheet</b>: “Calling Dr. Mehta’s Clinic” (stops below the chat header). The assistant lists what to ask (reports, location, charges, insurance) with 📞 Call Back.</>,
      <><b>Hand-off</b>: the phone’s own dialer places the call; the system draws it, so the docs show a coded stand-in.</>,
      <><b>Result</b>: an <b>Outgoing call</b> card (“You called back Dr. Mehta’s Clinic · Bring previous medical reports”) with 📞 Call back.</>,

      <><b>From a missed call</b>: when the assistant couldn’t pick up, the thread shows a <b>missed-call card</b> instead of a summary. With a reason: the caller’s art with a missed badge, “LIKELY CALLS FOR …” and 👍/👎. With only a name: “Valluri Called”. With nothing: “Missed by the assistant”. Its Call back opens the same task.</>,
    ],
  },
  mute: {
    title: 'Mute',
    lead: 'Muting a caller from the chat: the assistant double-checks, because muting means missing their calls even when they matter.',
    source: 'Mute',
    states: [
      { key: 'live', label: 'Chat (Try it: tap Mute)', render: () => <ChatActionScreen action="mute" />, code: '<ChatActionScreen action="mute" />' },
      { key: 'confirm', label: 'Are you sure?', render: () => <ChatActionScreen action="mute" step="confirm" />, code: '<ChatActionScreen action="mute" step="confirm" />' },
      { key: 'done', label: 'Muted', render: () => <ChatActionScreen action="mute" step="done" />, code: '<ChatActionScreen action="mute" step="done" />' },
    ],
    steps: [
      <><b>Ask</b>: 🔕 Mute from the AI layer opens “Muting Amit Nanda”.</>,
      <><b>Confirm</b>: “Are you sure? You’ll not get any call from them even if it’s important” with 🔕 Mute.</>,
      <><b>Result</b>: “This caller has been muted” and a card “+91-81720 33604 muted · Calls from this caller will be muted” with 📋 Open muted caller list.</>,
    ],
  },
  'reply-whatsapp': {
    title: 'Reply on WhatsApp',
    lead: 'The assistant drafts a WhatsApp reply from the call. The user sends it as is or edits it in place, WhatsApp opens with the message entered, and the chat keeps a record.',
    source: 'Reply On WhatsApp',
    states: [
      { key: 'live', label: 'Chat (Try it)', render: () => <ChatActionScreen action="reply-whatsapp" />, code: '<ChatActionScreen action="reply-whatsapp" />' },
      { key: 'draft', label: 'Draft', render: () => <ChatActionScreen action="reply-whatsapp" step="draft" />, code: '<ChatActionScreen action="reply-whatsapp" step="draft" />' },
      { key: 'sending', label: 'Opening WhatsApp', render: () => <ChatActionScreen action="reply-whatsapp" step="sending" />, code: '<ChatActionScreen action="reply-whatsapp" step="sending" />' },
      { key: 'edit', label: 'Edit message', render: () => <ChatActionScreen action="reply-whatsapp" step="edit" />, code: '<ChatActionScreen action="reply-whatsapp" step="edit" />' },
      { key: 'edit-long', label: 'Edit: long message', render: () => <ChatActionScreen action="reply-whatsapp" step="edit-long" />, code: '<ChatActionScreen action="reply-whatsapp" step="edit-long" />' },
      { key: 'done', label: 'Confirmation', render: () => <ChatActionScreen action="reply-whatsapp" step="done" />, code: '<ChatActionScreen action="reply-whatsapp" step="done" />' },
    ],
    steps: [
      <><b>Draft</b>: “Replying Amit on WhatsApp”: You “Reply on whatsapp”, then the draft with ✏️ Edit / 📤 Send.</>,
      <><b>Edit</b>: “Edit Message” dims the chat and floats the draft above the keyboard. The bubble grows with the text (about 11 lines, then scrolls); the green check saves.</>,
      <><b>Send</b>: “send as is” → <i>Opening WhatsApp…</i>, chips grey out; WhatsApp opens with the message entered.</>,
      <><b>Confirmation</b>: “Wrote a draft for Amit” and a WhatsApp card with the message and 👀 View message.</>,
    ],
  },
  'save-contact': {
    title: 'Save Contact',
    lead: 'Saving an unknown caller as a contact from the chat, including editing the details, the contact form, and every path through the contacts permission.',
    source: 'Save Contact',
    states: [
      { key: 'live', label: 'Chat (Try it)', render: () => <ChatActionScreen action="save-contact" />, code: '<ChatActionScreen action="save-contact" />' },
      { key: 'review', label: 'Review: Edit / Save', render: () => <ChatActionScreen action="save-contact" step="review" />, code: '<ChatActionScreen action="save-contact" step="review" />' },
      { key: 'saved', label: 'Saved', render: () => <ChatActionScreen action="save-contact" step="saved" />, code: '<ChatActionScreen action="save-contact" step="saved" />' },
      { key: 'edit-ask', label: 'Edit: what should I change?', render: () => <ChatActionScreen action="save-contact" step="edit-ask" />, code: '<ChatActionScreen action="save-contact" step="edit-ask" />' },
      { key: 'form', label: 'Contact form (iOS parity)', render: () => <ChatActionScreen action="save-contact" step="form" keyboard />, code: '<ChatActionScreen action="save-contact" step="form" keyboard />' },
      { key: 'form-outlined', label: 'Contact form (WhatsApp parity)', render: () => <ChatActionScreen action="save-contact" step="form-outlined" keyboard />, code: '<ChatActionScreen action="save-contact" step="form-outlined" keyboard />' },
      { key: 'no-permission', label: 'No permission: allow', render: () => <ChatActionScreen action="save-contact" step="no-permission" />, code: '<ChatActionScreen action="save-contact" step="no-permission" />' },
      { key: 'resume', label: 'Back from Settings', render: () => <ChatActionScreen action="save-contact" step="resume" />, code: '<ChatActionScreen action="save-contact" step="resume" />' },
      { key: 'granted', label: 'Permission granted', render: () => <ChatActionScreen action="save-contact" step="granted" />, code: '<ChatActionScreen action="save-contact" step="granted" />' },
      { key: 'denied', label: 'Still no permission', render: () => <ChatActionScreen action="save-contact" step="denied" />, code: '<ChatActionScreen action="save-contact" step="denied" />' },
    ],
    steps: [
      <><b>Prompt</b>: “This number isn’t saved. Add Rahul to your contacts?” with 👤 Save contact.</>,
      <><b>Review</b>: “Saving contact”: the pre-filled card (Rahul (Electrician), number) with ✏️ Edit / 💾 Save and “Looks right? Tap Save to add them.”</>,
      <><b>Edit</b>: by voice (“What should I change?”, Hold to speak) or in the contact form. Two forms are on the table: iOS-revamp parity (grouped rows) and exact WhatsApp parity (outlined fields, Sync contact to phone).</>,
      <><b>No permission</b>: a centred dialog “Allow contacts permission · Open Settings”. Back from Settings, the chat offers ▶️ Continue saving. If granted, the pre-filled card returns with ✏️ Edit / 👍 Confirm; if not, a “Contact Permission Required” card with ⚙️ Open Settings.</>,
      <><b>Saved</b>: “Saved! Rahul is in your contacts now.” with 👤 View contact.</>,
    ],
  },
  'error-states': {
    title: 'Error states',
    lead: 'How the AI layer fails: say what happened in one line and offer the one action that fixes it.',
    source: 'Error states',
    states: [
      { key: 'generation', label: 'Generation failure', render: () => <ChatErrorScreen state="generation" />, code: '<ChatErrorScreen state="generation" onRetry={retry} />' },
      { key: 'offline-reply', label: 'No internet (in a task)', render: () => <ChatErrorScreen state="offline-reply" />, code: '<ChatErrorScreen state="offline-reply" onRetry={retry} />' },
      { key: 'voice', label: 'Voice input failure', render: () => <ChatErrorScreen state="voice" />, code: '<ChatErrorScreen state="voice" />' },
      { key: 'offline-sheet', label: 'No internet (sheet)', render: () => <ChatErrorScreen state="offline-sheet" />, code: '<ChatErrorScreen state="offline-sheet" onRetry={refresh} />' },
      { key: 'offline-screen', label: 'No internet (full screen)', render: () => <ChatErrorScreen state="offline-screen" />, code: '<ChatErrorScreen state="offline-screen" onRetry={retry} />' },
    ],
    steps: [
      <><b>Generation failure</b>: “Sorry, I couldn’t do that just now. Please try again.” with 🔄 Retry, inside the task.</>,
      <><b>Offline in a task</b>: “It looks like you’re offline. Try again once you’re back online.” with 🔄 Retry.</>,
      <><b>Voice input failure</b>: the AI layer asks again: “Sorry, I didn’t catch that. Could you hold and speak again?”</>,
      <><b>Offline sheet</b>: over the chat, the struck wifi, “No internet connection” and Refresh. <b>Full screen</b>: the same with Try again, when there is nothing to show yet.</>,
    ],
  },
  'contact-info': {
    title: 'Contact info',
    lead: 'The caller’s page: category avatar, name and number, four action tiles and the call history.',
    source: 'Contact Info',
    states: [{ key: 'contact', label: 'Contact Info', render: () => <ContactInfoScreen onBack={() => {}} />, code: '<ContactInfoScreen onBack={goBack} />' }],
    steps: [
      <><b>Header</b>: a 96dp category avatar (or initials for saved contacts), the headline name and a tabular phone number.</>,
      <><b>Actions</b>: Call, Mute (toggles to Unmute), Save and Share as 72dp tiles with the icon in <code>text/action</code>.</>,
      <><b>Call history</b>: phone_callback, phone_missed (red icon, label not red) and phone_forwarded rows inside a card.</>,
    ],
  },
};

export default function Flow({ id }: { id: string }) {
  const f = flows[id];
  const [mode, setMode] = useState<'trace' | 'try'>('try');
  if (!f) return <NotFound />;
  return (
    <Page key={id} title={f.title} lead={f.lead} toc={false}>
      <div style={{ display: 'flex', alignItems: 'center', gap: 14, margin: '28px 0 8px', flexWrap: 'wrap' }}>
        <div className="s-seg" role="group" aria-label="Stage mode">
          <button type="button" aria-pressed={mode === 'try'} onClick={() => setMode('try')}>Try it</button>
          <button type="button" aria-pressed={mode === 'trace'} onClick={() => setMode('trace')}>Trace</button>
        </div>
        <span style={{ fontSize: 13, color: 'var(--site-ink-3)' }}>{mode === 'try' ? 'Every state is live: tap, type, open the menus.' : 'Hover to name components; click to open their page.'} Design: {f.source}</span>
      </div>
      <TraceStage phones={f.states} mode={mode} scale={0.7} wrap />
      <H2>How it works</H2>
      <ol className="s-steps">{f.steps.map((s, i) => <li key={i}><span>{s}</span></li>)}</ol>
      <H2>Code</H2>
      <p className="s-p">Each state is one screen component. Pick a state to see how it’s built in React and in Jetpack Compose.</p>
      <StageCode phones={f.states} group={`flow-${id}`} />
    </Page>
  );
}
