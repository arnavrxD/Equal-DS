// Screen templates — composed ONLY from DS components. Use these as starting points for new flows.
import { useEffect, useState, type ReactNode } from 'react';
import {
  type PlanTier,
  Asset,
  NoticedCard,
  NoticedCoach,
  InstructionsPromoCard,
  EqualLogo,
  InfoNote,
  type UsageMeterCardProps,
  UsageMeterCard,
  Icon,
  AddressCard,
  AiChatPanel,
  AiSummaryCard,
  AssistantToggle,
  MeetAssistantCard,
  CarouselDots,
  SetupPrompt,
  SearchBar,
  SearchSection,
  RecentSearches,
  MessageResult,
  LoadingDots,
  SearchState as SearchStateView,
  KeyboardSpace,
  type RecentSearch,
  VoicePlayer,
  AiAvatar,
  ProcessingStrip,
  OfferStrip,
  HomeNoticeCard,
  UsageCapCard,
  AssistantNoticeCard,
  LockedRecording,
  SummaryActions,
  PermissionSheet,
  OngoingCallBar,
  SystemStatusCard,
  InviteCard,
  ProfileHero,
  AppBar,
  BottomNav,
  Button,
  CallHistoryList,
  CallHistoryRow,
  CallSummaryMessage,
  ChatBubble,
  ChatHeader,
  ChatListItem,
  ChatWallpaper,
  ContactInfoHeader,
  DayDivider,
  EmptyState,
  FeedbackCard,
  HomeHeader,
  IconButton,
  InstructionCard,
  MessageList,
  PlanCard,
  PlanBanner,
  PlanExpiryBanner,
  ValueSheet,
  type PlanExpiry,
  type Quota,
  UnlockDetailsCard,
  RowGroup,
  Screen,
  SearchField,
  SectionHeader,
  SegmentedChips,
  SelectionHeader,
  SettingsRow,
  StatusBar,
  Text,
  TextButton,
  TextField,
  ThreadEvent,
  ThreadNotice,
  WhatsNewCard,
  type AiSummaryStatus,
  type NavTab,
  type ProfileAvatarVariant,
} from '../index';
import { sampleChats, type SampleChat } from './data';
import { AssistantSheet, ChatTaskSheet, type ChatTaskStage, type MyaStage } from './AssistantSheet';
import { PaywallSheetFlow, type PaywallSheetStage } from './MonetizationScreens';
import { officialNotices } from './monetizationData';

// ---------------------------------------------------------------- Home (Chats tab)

export type HomeScreenProps = {
  chats?: SampleChat[];
  onOpenChat?: (c: SampleChat) => void;
  onTab?: (t: NavTab) => void;
  onDeleteSelected?: (ids: string[]) => void;
  onSearch?: () => void;
  showWhatsNew?: boolean;
  avatar?: ProfileAvatarVariant;
  /** The sample user is on Unlimited, so screens default to the paid (gold) avatar. */
  tier?: PlanTier;
  /** Temporarily Unavailable design: the status card replaces the assistant toggle and hides the promo. */
  systemStatus?: 'ok' | 'unavailable' | 'notified';
  onNotify?: () => void;
  /** Which promo card sits above the list. `meet-assistant-carousel`: the banner card (text left, mascot right) with carousel dots (Global MYA design). */
  promo?: 'whats-new' | 'meet-assistant' | 'meet-assistant-carousel' | 'talk-hero' | 'instructions';
  /** Monetization (free plan): rows from this index on are locked (blurred preview, crown-lock). */
  lockedFrom?: number;
  /** Tapping a locked row (the chat-list touchpoint). */
  onLockedChat?: () => void;
  /** Plan value & renewal: the strip under the header when the plan ends soon or has ended. */
  planExpiry?: Exclude<PlanExpiry, { state: 'active' }>;
  /** Show the "What Unlimited saved you" sheet (after a call, near the end of the plan). */
  valueSheet?: boolean | 'flush';
  /** Renew Now / Renew your Equal AI. */
  onRenew?: () => void;
  /** Free plan: the weekly quota sheet ("6 out of 30 Calls Left" / "Limit Reached"); its CTA calls onRenew (open the paywall). */
  quotaSheet?: Quota;
  /** Global MYA: pin the assistant sheet at a stage (docs). Otherwise the green orb opens it live. */
  assistant?: MyaStage;
  /** Global MYA done: the chat the assistant acted on shows what it did ("Reminder added"). */
  assistantResult?: { chatId: string; preview: string };
  /** Call Handling for new users: the "Set up your calls" takeover, or the "Finish your setup?" sheet. */
  callSetup?: 'prompt' | 'finish';
  onCallSetup?: () => void;
  /** Assistant toggle line (new users: "Equal is managing your calls!"). */
  assistantDescription?: string;
  /** Monetization experiments ---------------------------------------------------------------- */
  /** Red PLAN EXPIRED badge beside the logo. */
  planExpired?: boolean;
  /** The assistant toggle starts Off (usage cap reached). */
  assistantOff?: boolean;
  /** Toggle art experiment (Component 25). */
  assistantArt?: 'robot' | 'phone';
  /** Assistant Healthcheck design: assistant off with "Check assistant health" and the Fix Now strip. */
  healthAlert?: { onFix?: () => void };
  /** Meet Your Assistant → Call me (starts the in-app call, vOIP design). */
  onCallMe?: () => void;
  /** Card under the header: Plan Expired (option 2) or the weekly usage meter (Usage Cap option 2). */
  homeNotice?: 'plan-expired' | 'plan-expired-plain' | 'recordings-locked' | 'recordings-locked-plain' | { callsLeft: number; limit: number; lead?: false };
  /** Known Call Usage Cap: the known / unknown usage card under the toggle (replaces homeNotice). */
  usageMeter?: UsageMeterCardProps;
  /** P0 free home: the 👑 Upgrade pill in the top bar. */
  upgradePill?: boolean;
  /** Equal-to-Equal: the incoming or ongoing call strip under the status bar. */
  callStrip?: ReactNode;
  /** No internet: the list is replaced by the no-internet state. */
  offline?: boolean;
  /** The promo card's CTA (Try Instructions). */
  onPromo?: () => void;
  /** Equal AI's own chat on top with its notice (Plan Expired / Usage Cap option 1). */
  official?: 'plan-expired' | 'usage-cap' | 'recordings-locked';
  onOfficialChat?: () => void;
  /** Actions touchpoint: every row's action chips are crown-locked. */
  lockedActions?: boolean;
  /** "Payment processing" card under the toggle (the user closed Payment Pending). */
  paymentProcessing?: boolean;
  /** Sheet paywall experiment: `true` opens it from locked taps; a stage pins it open. */
  paywallSheet?: boolean | PaywallSheetStage;
  /** "Go unlimited for ₹149 with EQUAL25 · Claim Now" strip (shown after the exit offer is declined). */
  offerStrip?: boolean;
  /** Assistant Snooze: drive the toggle from outside (on, busy spinner, off copy); onChange gets the tapped value. */
  assistantControl?: { on: boolean; busy?: 'deactivating' | 'activating'; offDescription?: ReactNode; onChange?: (on: boolean) => void };
  /** Any overlay on top of Home (Snooze dialog, error sheets, the soft update, a heads-up notification). */
  overlay?: ReactNode;
};

export function HomeScreen({ chats = sampleChats, onOpenChat, onTab, onDeleteSelected, onSearch, showWhatsNew = true, avatar = 'male', tier = 'paid', systemStatus = 'ok', onNotify, promo = 'whats-new', lockedFrom, onLockedChat, planExpiry, valueSheet, onRenew, quotaSheet, assistant, assistantResult, callSetup, onCallSetup, assistantDescription, planExpired, assistantOff, assistantArt, onCallMe, healthAlert, homeNotice, official, onOfficialChat, lockedActions, paymentProcessing, paywallSheet, offerStrip, assistantControl, overlay, usageMeter, upgradePill, callStrip, offline, onPromo }: HomeScreenProps) {
  const [assistantOn, setAssistantOn] = useState(!assistantOff && !healthAlert);
  const [pw, setPw] = useState<PaywallSheetStage | null>(typeof paywallSheet === 'string' ? paywallSheet : null);
  useEffect(() => setPw(typeof paywallSheet === 'string' ? paywallSheet : null), [paywallSheet]);
  const [offer, setOffer] = useState(!!offerStrip);
  useEffect(() => setOffer(!!offerStrip), [offerStrip]);
  const openPaywall = () => (paywallSheet ? setPw('sheet') : onLockedChat?.());
  const notice = official ? officialNotices[official] : undefined;
  const [mya, setMya] = useState(false);
  const [result, setResult] = useState(assistantResult);
  useEffect(() => setResult(assistantResult), [assistantResult]);
  const rows = result ? chats.map((c) => (c.id === result.chatId ? { ...c, preview: result.preview, unread: 0 } : c)) : chats;
  const [sheet, setSheet] = useState(!!valueSheet || !!quotaSheet);
  useEffect(() => setSheet(!!valueSheet || !!quotaSheet), [valueSheet, quotaSheet]);
  const [selected, setSelected] = useState<string[]>([]);
  const [read, setRead] = useState<string[]>([]);
  const [whatsNew, setWhatsNew] = useState(showWhatsNew);
  const selecting = selected.length > 0;
  const toggle = (id: string) => setSelected((s) => (s.includes(id) ? s.filter((x) => x !== id) : [...s, id]));
  const unreadTotal = chats.filter((c) => c.unread > 0 && !read.includes(c.id)).length;

  return (
    <Screen
      header={
        selecting ? (
          <SelectionHeader
            statusBar
            count={selected.length}
            allSelected={selected.length === chats.length}
            onSelectAll={(all) => setSelected(all ? chats.map((c) => c.id) : [])}
            onBack={() => setSelected([])}
            onMarkRead={() => (setRead((r) => [...r, ...selected]), setSelected([]))}
            onDelete={() => onDeleteSelected?.(selected)}
          />
        ) : (
          <HomeHeader
            trailing={upgradePill ? <Button appearance="premium" emphasis="secondary" size="sm" onClick={onRenew ?? openPaywall}><span aria-hidden>👑 </span>Upgrade</Button> : undefined}
            assistantOn={assistantControl ? assistantControl.on : assistantOn}
            onAssistantChange={assistantControl ? (v) => assistantControl.onChange?.(v) : setAssistantOn}
            assistantBusy={assistantControl?.busy}
            plan={planExpired ? 'Plan Expired' : tier === 'paid' ? 'Unlimited' : null}
            planTone={planExpired ? 'expired' : 'paid'}
            assistantArt={assistantArt}
            below={paymentProcessing ? <ProcessingStrip variant="card" /> : undefined}
            status={systemStatus === 'ok' ? undefined : <SystemStatusCard state={systemStatus} onNotify={onNotify} />}
            assistantDescription={assistantDescription}
            assistantOffDescription={assistantControl?.offDescription ?? (healthAlert ? 'Check assistant health' : undefined)}
            healthAlert={healthAlert && !assistantOn ? healthAlert : undefined}
            callStrip={callStrip}
          />
        )
      }
      floating={selecting ? undefined : <BottomNav active="chats" unread={unreadTotal} onChange={onTab} avatar={avatar} tier={tier} onFabClick={() => setMya(true)} />}
      overlay={overlay ? overlay : pw ? <PaywallSheetFlow stage={pw} onClose={() => setPw(null)} onDecline={() => setOffer(true)} /> : callSetup === 'prompt' ? <SetupPrompt onStart={onCallSetup} /> : callSetup === 'finish' ? <div className="eq-status-sheet-layer"><div className="eq-status-sheet-layer__scrim" /><PermissionSheet art="call-handling-all" title="Finish your setup?" body={<>You chose all calls. One thing is left.<br />Tell me who should always ring you.</>} cta="Finish" onAllow={onCallSetup} onClose={() => {}} secondary="Skip, answer everyone" /></div> : assistant ? <AssistantSheet stage={assistant} /> : mya ? <AssistantSheet onClose={() => setMya(false)} onDone={() => (setMya(false), setResult({ chatId: 'c3', preview: 'Reminder added' }))} /> : sheet && quotaSheet ? <ValueSheet quota={quotaSheet} onClose={() => setSheet(false)} onRenew={onRenew} /> : sheet && planExpiry ? <ValueSheet expiry={planExpiry} variant={valueSheet === 'flush' ? 'flush' : 'card'} onClose={() => setSheet(false)} onRenew={onRenew} /> : undefined}
    >
      {planExpiry && !selecting && <PlanExpiryBanner expiry={planExpiry} onRenew={onRenew} />}
      {offer && !selecting && <OfferStrip onClick={() => setPw('claimed')} />}
      {usageMeter && !selecting && <div style={{ paddingTop: 12 }}><UsageMeterCard onUpgrade={onRenew ?? openPaywall} {...usageMeter} /></div>}
      {homeNotice && !usageMeter && !selecting && (homeNotice === 'plan-expired' || homeNotice === 'plan-expired-plain'
        ? <HomeNoticeCard lead={homeNotice === 'plan-expired' ? <span aria-hidden>👑</span> : undefined} title="Your Plan Expired" subtitle="Renew to get Unlimited access back" cta="Renew Now" onCta={onRenew ?? openPaywall} />
        : homeNotice === 'recordings-locked' || homeNotice === 'recordings-locked-plain'
          ? <HomeNoticeCard lead={homeNotice === 'recordings-locked' ? <Icon name="call-filled" size={16} /> : undefined} title="Call Recordings Locked" subtitle="Subscribe to get unlimited recordings" cta="Get Unlimited" onCta={onRenew ?? openPaywall} />
          : <UsageCapCard left={homeNotice.callsLeft} limit={homeNotice.limit} lead={homeNotice.lead === false ? null : undefined} onUpgrade={onRenew ?? openPaywall} />)}
      {offline && (
        <div className="eq-home-state" role="status">
          <Asset name="illustration-no-internet" size={112} alt="" />
          <Text as="h2" variant="headline" align="center" style={{ fontWeight: 600, marginTop: 16 }}>No internet connection</Text>
          <Text as="p" variant="body1" color="secondary" align="center" style={{ margin: '8px 0 0', maxWidth: 340 }}>We could not connect to a stable internet. Check your connection and try again.</Text>
        </div>
      )}
      {!offline && chats.length === 0 && promo !== 'talk-hero' && (
        <div className="eq-home-state is-empty">
          <span className="eq-home-state__art" aria-hidden>
            <Asset name="mascot-update" size={160} alt="" />
            {[[0, 72, 19], [88, 76, 14], [96, 54, 19], [64, 12, 11]].map(([x, y, sz], i) => (
              <svg key={i} className="eq-home-state__spark" style={{ left: `${(x / 171) * 100}%`, top: `${(y / 143) * 100}%` }} width={sz} height={sz} viewBox="0 0 24 24"><path d="M12 0c.6 6.4 5 11 12 12-7 1-11.4 5.6-12 12-.6-6.4-5-11-12-12C7 11 11.4 6.4 12 0Z" /></svg>
            ))}
          </span>
          <Text as="h2" variant="title" align="center" style={{ fontWeight: 600, marginTop: 8 }}>Your chats will appear here</Text>
          <Text as="p" variant="body1" color="secondary" align="center" style={{ margin: '8px 0 0', maxWidth: 300 }}>Assistant will answer calls, summarise them and help you take actions afterwards</Text>
        </div>
      )}
      {!offline && !selecting && chats.length > 0 && (
        <div style={{ padding: '12px 16px 0' }}>
          <SearchField value="" onActivate={onSearch ?? (() => {})} />
        </div>
      )}
      {whatsNew && !offline && (chats.length > 0 || promo === 'talk-hero') && !selecting && systemStatus === 'ok' && (
        <div style={{ padding: '12px 12px 4px', display: 'flex', justifyContent: 'center' }}>
          {promo === 'talk-hero' ? (
            <MeetAssistantCard layout="hero" />
          ) : promo === 'meet-assistant-carousel' ? (
            <div style={{ width: '100%' }}>
              <MeetAssistantCard layout="banner" onCall={onCallMe} onDismiss={() => setWhatsNew(false)} />
              <CarouselDots count={3} active={0} />
            </div>
          ) : promo === 'instructions' ? (
            <div style={{ width: '100%' }}>
              <InstructionsPromoCard onCta={onPromo} onDismiss={() => setWhatsNew(false)} />
              <CarouselDots count={3} active={1} />
            </div>
          ) : promo === 'meet-assistant' ? (
            <MeetAssistantCard onCall={onCallMe} onDismiss={() => setWhatsNew(false)} />
          ) : (
            <WhatsNewCard width="100%" heading="Assistant Languages" body="Checkout how your assistant speaks in 9 languages" ctaLabel="Try Languages" ctaIcon="translate" onDismiss={() => setWhatsNew(false)} />
          )}
        </div>
      )}
      <div style={{ display: 'flex', flexDirection: 'column', gap: 1, marginTop: 8, background: 'var(--eq-stroke-subtle)' }}>
        {notice && !selecting && (
          <ChatListItem official name="Equal AI" preview={notice.preview} date="Now" unread={1} actions={[{ label: notice.chip, emoji: '👑', onClick: onRenew ?? openPaywall }]} onClick={onOfficialChat} />
        )}
        {!offline && rows.map((c, i) => (
          <ChatListItem
            status={c.status}
            muted={c.muted}
            key={c.id}
            name={c.name}
            preview={c.preview}
            date={c.date}
            unread={read.includes(c.id) ? 0 : c.unread}
            actions={c.actions}
            avatar={c.initials ? { initials: c.initials } : { category: c.category }}
            selected={selected.includes(c.id)}
            locked={lockedFrom !== undefined && i >= lockedFrom}
            lockedActions={lockedActions}
            onLockedAction={openPaywall}
            onClick={() => (selecting ? toggle(c.id) : lockedFrom !== undefined && i >= lockedFrom ? openPaywall() : onOpenChat?.(c))}
            onLongPress={() => toggle(c.id)}
          />
        ))}
      </div>
    </Screen>
  );
}

// ---------------------------------------------------------------- Chat thread

export type ChatScreenProps = {
  chat?: SampleChat;
  live?: boolean;
  /**
   * Monetization touchpoints on a free plan. details: the conversation is blurred and the
   * Unlock All Details card replaces the AI panel; audio: only the recording is locked
   * (Unlock Audio); lapsed: readable, with the Reactivate banner under the header.
   */
  lock?: 'details' | 'audio' | 'lapsed';
  /** Opens the paywall (or the plan page, for lapsed). */
  onUnlock?: () => void;
  /** Known call in progress (Known-call notifications design): the "Ongoing call - 02:34" strip under the header and the assistant's follow-up. */
  ongoing?: string;
  /** Monetization experiments ---------------------------------------------------------------- */
  /** Equal AI's own conversation with its notice (Plan Expired / Usage Cap option 1). */
  official?: 'plan-expired' | 'usage-cap' | 'recordings-locked';
  /** Call Back / Add to Calendar rows under the call summary (green; the paywall shows them locked). */
  summaryActions?: boolean;
  /** "Payment processing" strip under the header. */
  paymentProcessing?: boolean;
  /** Sheet paywall experiment: `true` opens it from Unlock; a stage pins it open. */
  paywallSheet?: boolean | PaywallSheetStage;
  /** The AI layer as a task: pin the "Replying … on WhatsApp" sheet (docs). Live, sending from the AI panel opens it. */
  aiTask?: ChatTaskStage;
  onBack?: () => void;
  onInfo?: () => void;
};

export function ChatScreen({ chat = sampleChats[0], live = false, lock, onUnlock: onUnlockProp, ongoing, official, summaryActions: withActions, paymentProcessing, paywallSheet, aiTask, onBack, onInfo }: ChatScreenProps) {
  const [task, setTask] = useState<string | null>(aiTask ? 'Reply on whatsapp' : null);
  useEffect(() => setTask(aiTask ? 'Reply on whatsapp' : null), [aiTask]);
  // First name for the AI layer; titles ("Dr.") and businesses keep the full name.
  const first = chat.initials ? chat.name.split(' ')[0] : chat.name;
  const [pw, setPw] = useState<PaywallSheetStage | null>(typeof paywallSheet === 'string' ? paywallSheet : null);
  useEffect(() => setPw(typeof paywallSheet === 'string' ? paywallSheet : null), [paywallSheet]);
  const [sent, setSent] = useState<string[]>([]);
  const [panel, setPanel] = useState(true);
  const onUnlock = paywallSheet ? () => setPw('sheet') : onUnlockProp;
  if (official) {
    const n = officialNotices[official];
    return (
      <Screen header={<ChatHeader statusBar official name="Equal AI" subtitle="+91-81720 33604" onBack={onBack} onCall={() => {}} onMore={onInfo} />} overlay={pw ? <PaywallSheetFlow stage={pw} context="chat" onClose={() => setPw(null)} /> : undefined}>
        <ChatWallpaper style={{ minHeight: '100%' }}>
          <MessageList>
            <DayDivider label="Today" />
            <div style={{ display: 'flex', gap: 8, alignItems: 'flex-start' }}>
              <AiAvatar size={28} />
              <AssistantNoticeCard title={n.title} body={n.body} cta={n.cta} onCta={onUnlockProp ?? (() => setPw('sheet'))} />
            </div>
          </MessageList>
        </ChatWallpaper>
      </Screen>
    );
  }
  const locked = lock === 'details';
  const header = <ChatHeader statusBar name={chat.name} avatar={chat.initials ? { initials: chat.initials } : { category: chat.category }} subtitle={live ? 'You have a visitor' : chat.phone} live={live} onBack={onBack} onMore={onInfo} />;
  return (
    <Screen
      header={
        ongoing ? (
          <div>
            {header}
            <OngoingCallBar elapsed={ongoing} />
          </div>
        ) : paymentProcessing ? (
          <div>
            {header}
            <ProcessingStrip />
          </div>
        ) : lock === 'lapsed' ? (
          <div>
            {header}
            <PlanBanner onClick={onUnlock}>Reactivate your current Plan to keep using Equal Unlimited</PlanBanner>
          </div>
        ) : header
      }
      overlay={pw ? <PaywallSheetFlow stage={pw} context="chat" onClose={() => setPw(null)} /> : task ? <ChatTaskSheet contact={first} request={task} stage={aiTask} onClose={() => setTask(null)} /> : undefined}
      footer={
        locked ? (
          <div style={{ padding: '0 12px 12px' }}><UnlockDetailsCard onUnlock={onUnlock} /></div>
        ) : panel ? (
          <AiChatPanel
            prompt={ongoing ? 'Our appointment is all set. Want me to help with anything else?' : `Want me to send ${first} a quick reply on WhatsApp?`}
            actions={
              ongoing
                ? [
                    { label: 'Call back', emoji: '📞', onClick: () => setPanel(false) },
                    { label: 'Update Address', emoji: '✏️', onClick: () => setPanel(false) },
                  ]
                : live
                ? [
                    { label: 'Takeover', emoji: '📞' },
                    { label: 'Ask to reschedule', emoji: '🗓️', onClick: () => setPanel(false) },
                  ]
                : [
                    { label: 'Reply on WhatsApp', emoji: '💬', onClick: () => setTask('Reply on whatsapp') },
                    { label: 'Add to Calendar', emoji: '🗓️', onClick: () => setPanel(false) },
                  ]
            }
            composer={{ onSend: (t) => (ongoing || live ? setSent((s) => [...s, t]) : setTask(t)) }}
          />
        ) : undefined
      }
    >
      <ChatWallpaper style={{ minHeight: '100%' }}>
      <MessageList>
        <DayDivider label="Today" />
        <ThreadEvent label="Call Started 03:31" />
        <ChatBubble kind="outgoing" sender="Equal AI" time="03:21" locked={locked}>Hi, who is this?</ChatBubble>
        <ChatBubble kind="incoming" category="others" sender="Dr Mehta Clinic" time="03:21" locked={locked}>This is Priya from Dr. Mehta's clinic. Your appointment is confirmed for tomorrow.</ChatBubble>
        <ChatBubble kind="outgoing" sender="Equal AI" time="03:21" locked={locked}>Yes, I'll be there at 10 AM</ChatBubble>
        <ChatBubble kind="incoming" category="others" sender="Dr Mehta Clinic" time="03:22" locked={locked}>Great. Please bring your previous reports too.</ChatBubble>
        <ThreadEvent label="Call Ended 03:31" />
        <ThreadNotice>Call has ended. You are now talking to your AI assistant.</ThreadNotice>
        <ChatBubble kind="assistant" sender="Equal AI" time="03:32">Hey Keshav! Here is the summary of your call</ChatBubble>
        {locked || withActions ? (
          <CallSummaryMessage
            first={false}
            width={264}
            summary="Priya from Dr. Mehta's clinic confirmed your appointment for tomorrow at 10 AM. Bring your previous reports."
            time="03:32"
            recording={locked ? <LockedRecording onUnlock={onUnlock} /> : undefined}
            footer={withActions ? <SummaryActions actions={[{ label: 'Call back', icon: 'call-filled' }, { label: 'Add to Calendar', icon: 'calendar' }]} /> : undefined}
          />
        ) : (
          <CallSummaryMessage first={false} summary="Priya from Dr. Mehta's clinic confirmed your appointment for tomorrow at 10 AM. Bring your previous reports." time="03:32" onShare={() => {}} width={264} tag={{ emoji: '🪄', label: '2 instructions applied in this call' }} locked={lock === 'audio' ? 'audio' : undefined} onUnlock={onUnlock} />
        )}
        <div style={{ paddingLeft: 36, marginTop: 4 }}>
          <FeedbackCard />
        </div>
        {sent.map((t, i) => (
          <ChatBubble key={i} kind="outgoing" sender="You" time="now" first={i === 0}>{t}</ChatBubble>
        ))}
      </MessageList>
      </ChatWallpaper>
    </Screen>
  );
}

// ---------------------------------------------------------------- You tab (You (Settings) design)

export type YouPage = 'addresses' | 'instructions' | 'personal' | 'silent' | 'whatsapp' | 'call-handling' | 'health-check' | 'account-data';

export type YouScreenProps = {
  onTab?: (t: NavTab) => void;
  onOpen?: (page: YouPage) => void;
  name?: string;
  /** AI Summary state — the three variants in the design. */
  summary?: AiSummaryStatus;
  avatar?: ProfileAvatarVariant;
  /** Paid (default: the sample user is on Unlimited) shows the gold-ringed avatar. */
  tier?: PlanTier;
  /**
   * Monetization: the plan card's link. active: Manage plan; restore: the plan ended
   * (Restore Plan, use with tier="free"); reactivate: autopay is off (banner + Reactivate plan);
   * expired: the red "Plan Expired" pill, Manage plan opens the renewal page (use with tier="free").
   */
  /** 'none': no subscription and no plan (mint header, no plan card). */
  plan?: 'none' | 'active' | 'restore' | 'reactivate' | 'expired';
  /** The plan card link (the Manage plan touchpoint). */
  onManagePlan?: () => void;
  /** Plan Expired option 3: the plan card asks to renew (use with plan="expired"). */
  planRenew?: boolean;
  /** "Settings live here now": card (title + line) or the one-line variant, dismissible. */
  notice?: 'card' | 'line';
  /** AI Personalisation: Equal Noticed replaces the AI summary (ready, reveal, the first-time coach mark, or several cards). */
  noticed?: 'ready' | 'reveal' | 'coach' | 'multiple';
  onPersonalisation?: () => void;
};

const NOTICED_SUMMARY = (
  <>
    <p style={{ margin: 0 }}>You’re a Logistics Legend. You navigate a constant stream of courier updates from FedEx and Blue Dart while I filter out the noise from banks and real estate.</p>
    <p style={{ margin: 0 }}>Whether it is confirming a driver is waiting at the airport or managing delivery details, I keep the important pieces ready for you. We make it happen.</p>
  </>
);

export function YouScreen({ onTab, onOpen, summary = 'ready', avatar = 'male', name = 'Keshav', tier = 'paid', plan = 'active', onManagePlan, planRenew, notice, noticed, onPersonalisation }: YouScreenProps) {
  const [noticedState, setNoticedState] = useState(noticed);
  useEffect(() => setNoticedState(noticed), [noticed]);
  const [noticeOpen, setNoticeOpen] = useState(!!notice);
  useEffect(() => setNoticeOpen(!!notice), [notice]);
  const [assistantOn, setAssistantOn] = useState(true);
  // No subscription and no plan: mint header, plain avatar, no plan card, and the rows without NEW markers.
  const none = plan === 'none';
  return (
    <Screen header={none ? <StatusBar background="var(--eq-profile-hero-plain-bg)" /> : <StatusBar background="var(--eq-profile-hero-status-bg)" color="var(--eq-profile-hero-status-text)" />} floating={<BottomNav active="you" unread={2} onChange={onTab} avatar={avatar} tier={none ? 'free' : tier} />}>
      {none ? (
        <ProfileHero tone="plain" name={name} phone="+91 98789 56749" avatar={avatar} tier="free" onEdit={() => onOpen?.('personal')} />
      ) : (
      <ProfileHero name={name} phone="+91 98789 56749" avatar={avatar} tier={tier} plan={tier === 'paid' ? 'Unlimited' : null} onEdit={() => onOpen?.('personal')}>
        {noticeOpen && (
          <div style={{ marginBottom: 12 }}>
            {notice === 'line'
              ? <InfoNote title="Settings, plan and assistant controls all moved here." onDismiss={() => setNoticeOpen(false)} />
              : <InfoNote title="Settings live here now" body="Your profile, plan and assistant controls all moved into You." onDismiss={() => setNoticeOpen(false)} />}
          </div>
        )}
        <PlanCard renew={planRenew ? { onRenew: onManagePlan } : undefined} onManage={onManagePlan ?? (() => {})} status={plan === 'expired' ? 'expired' : 'current'} actionLabel={plan === 'restore' ? 'Restore Plan' : plan === 'reactivate' ? 'Reactivate plan' : 'Manage plan'} />
        {plan === 'reactivate' && <PlanBanner attached>Reactivate your current Plan to use Equal Unlimited</PlanBanner>}
      </ProfileHero>
      )}

      <SectionHeader>Tell Equal</SectionHeader>
      <div style={{ padding: '0 20px 12px' }}>
        <AssistantToggle on={assistantOn} onChange={setAssistantOn} />
      </div>
      <RowGroup>
        {!none && <SettingsRow icon="call" label="Call Handling" description="Choose which calls I answer" isNew onClick={() => onOpen?.('call-handling')} />}
        <SettingsRow icon="instructions" label="Your Instructions" description="Tell Equal what to do" isNew={!none} onClick={() => onOpen?.('instructions')} />
        <SettingsRow icon="health-check" label="Assistant Health Check" description="Check Real-Time Assistant Connectivity" onClick={() => onOpen?.('health-check')} />
        <SettingsRow icon="silent-mode" label="Silent Mode" description="Mute unwanted calls automatically" onClick={() => onOpen?.('silent')} />
        <SettingsRow icon="voice-language" label="Voice & Language" description="Choose how Equal speaks." onClick={() => {}} />
      </RowGroup>

      <SectionHeader>About you</SectionHeader>
      <div style={{ padding: '0 20px 12px', position: 'relative' }}>
        {noticedState ? (
          noticedState === 'multiple' ? (
            <>
              <div className="eq-noticed-carousel">
                <NoticedCard summary={NOTICED_SUMMARY} />
                <NoticedCard summary={<p style={{ margin: 0 }}>Most calls come in after 6 PM, and you rarely pick up while driving. I keep those short and save the details for later.</p>} />
              </div>
              <CarouselDots count={2} active={0} />
            </>
          ) : noticedState === 'ready' ? (
            <NoticedCard summary={NOTICED_SUMMARY} />
          ) : (
            <>
              {noticedState === 'coach' && <div style={{ marginBottom: 12 }}><NoticedCoach onDismiss={() => setNoticedState('reveal')} /></div>}
              <NoticedCard state="reveal" summary={NOTICED_SUMMARY} onReveal={() => setNoticedState('ready')} />
            </>
          )
        ) : (
          <AiSummaryCard
            status={none ? 'empty' : summary}
            summary="Your family calls you a lot. They always say it's urgent. They almost never say why. Everyone wants you, not me. You keep me on anyway, and let me be the one who asks."
          />
        )}
      </div>
      <RowGroup>
        {noticed && <SettingsRow icon="auto-fix" label="AI Personalisation" description="See how Equal personalises your calls" isNew onClick={onPersonalisation} />}
        <SettingsRow icon="address-pin" label="Your Addresses" description="Add addresses your assistant should know" isNew={!none} onClick={() => onOpen?.('addresses')} />
      </RowGroup>

      <SectionHeader>Settings &amp; support</SectionHeader>
      <RowGroup>
        <SettingsRow icon="whatsapp" label="WhatsApp Updates" description="Assistant and App Updates" onClick={() => onOpen?.('whatsapp')} />
        <SettingsRow icon="account-data" label="Account Data" description="Manage Your Account" onClick={() => onOpen?.('account-data')} />
        <SettingsRow icon="help-center" label="Help Center" description="Your What, Why and How" onClick={() => {}} />
      </RowGroup>

      <div style={{ padding: '12px 20px 24px' }}>
        <InviteCard onShare={() => {}} />
      </div>
    </Screen>
  );
}

// ---------------------------------------------------------------- Contact info

export function ContactInfoScreen({ chat = { ...sampleChats[2], initials: undefined, category: 'career', phone: '+91 9871746113' }, onBack }: { chat?: SampleChat; onBack?: () => void }) {
  const [muted, setMuted] = useState(false);
  return (
    <Screen header={<AppBar statusBar title="Contact Info" onBack={onBack} />}>
      <ContactInfoHeader
        name={chat.name}
        phone={chat.phone}
        avatar={chat.initials ? { initials: chat.initials } : { category: chat.category }}
        actions={[
          { icon: 'call', label: 'Call' },
          { icon: 'silent-mode', label: muted ? 'Unmute' : 'Mute', onClick: () => setMuted((m) => !m) },
          { icon: 'person-add', label: 'Save' },
          { icon: 'share', label: 'Share' },
        ]}
      />
      <SectionHeader size="label" inset={22}>Call history</SectionHeader>
      <CallHistoryList>
        <CallHistoryRow direction="incoming" meta="Today • 30 Sec." time="02:30 PM" />
        <CallHistoryRow direction="missed" meta="Yesterday • 0 Sec." time="02:30 PM" />
        <CallHistoryRow direction="incoming" meta="Today • 30 Sec." time="02:30 PM" />
        <CallHistoryRow direction="incoming" meta="Today • 30 Sec." time="02:30 PM" />
      </CallHistoryList>
      <div style={{ height: 24 }} />
    </Screen>
  );
}

// ---------------------------------------------------------------- Search

export type SearchState = 'empty' | 'offline' | 'recent' | 'no-results' | 'loading' | 'results' | 'messages-loading' | 'messages';

export const recentSearches: RecentSearch[] = [
  { label: 'Amit Nanda', category: 'career' },
  { label: 'Shankar', category: 'ride' },
  { label: 'DSR Builder', category: 'financial' },
];

const searchChats: SampleChat[] = [
  { id: 's1', name: 'Saurabh', preview: 'About a miniature painting that you asked for', date: 'Yesterday', unread: 2, actions: [{ label: 'Call back', emoji: '📞' }, { label: 'Update Address', emoji: '✏️' }], phone: '+91 98450 11223', category: 'home-services' },
  { id: 's2', name: 'Santosh Anna', preview: 'Order left with security', date: '12 July', unread: 0, actions: [], phone: '+91 99001 23456', category: 'delivery' },
  ...sampleChats,
];

const searchMessages = [
  { name: 'Santosh Anna', time: '3:02 PM', text: 'Caller said that they’ll call you in the evening' },
  { name: 'Amit Nanda', time: '3:02 PM', text: 'Same address, near Gachibowli' },
];

export type SearchScreenProps = {
  onBack?: () => void;
  onOpenChat?: (c: SampleChat) => void;
  /** Pin a state (docs). Otherwise it runs live: type → loading → chats → messages. */
  state?: SearchState;
  /** Query for pinned states. */
  query?: string;
  /** Docs only: show the system keyboard stand-in (the app never draws one). */
  keyboard?: boolean;
};

/**
 * Search (Search design): recent searches as avatar tiles, then live results in two sections:
 * chats first (they load fastest), messages after with the match in bold. Empty, no internet and
 * no results each have their own illustration.
 */
export function SearchScreen({ onBack, onOpenChat, state, query, keyboard }: SearchScreenProps) {
  const [q, setQ] = useState(query ?? '');
  const [recent, setRecent] = useState<RecentSearch[]>(recentSearches);
  const [phase, setPhase] = useState<'idle' | 'loading' | 'chats' | 'done'>('idle');
  useEffect(() => setQ(query ?? ''), [query]);
  useEffect(() => {
    if (state || !q.trim()) return setPhase('idle');
    setPhase('loading');
    const a = setTimeout(() => setPhase('chats'), 500);
    const b = setTimeout(() => setPhase('done'), 1200);
    return () => (clearTimeout(a), clearTimeout(b));
  }, [q, state]);
  const term = q.trim().toLowerCase();
  const chats = term ? searchChats.filter((c) => `${c.name} ${c.preview}`.toLowerCase().includes(term)).slice(0, 4) : [];
  const messages = term ? searchMessages.filter((m) => m.text.toLowerCase().includes(term)) : [];
  const s: SearchState =
    state ?? (!term ? (recent.length ? 'recent' : 'empty') : phase === 'loading' ? 'loading' : !chats.length && !messages.length && phase === 'done' ? 'no-results' : phase === 'chats' ? 'messages-loading' : phase === 'done' ? 'messages' : 'loading');
  const shownChats = state && term ? searchChats.filter((c) => `${c.name} ${c.preview}`.toLowerCase().includes(term)).slice(0, 2) : chats.slice(0, 2);
  const showRecentLabel = s === 'empty' || s === 'offline' || s === 'recent' || s === 'no-results';
  return (
    <Screen
      header={
        <div style={{ background: 'var(--eq-bg-primary)' }}>
          <StatusBar />
          <SearchBar value={q} onChange={setQ} onBack={onBack} autoFocus={!state} />
        </div>
      }
      footer={keyboard ? <KeyboardSpace /> : undefined}
    >
      {showRecentLabel && <SearchSection title="Recent searches" onClear={s === 'recent' ? () => setRecent([]) : undefined} />}
      {s === 'recent' && <RecentSearches items={recent} onPick={setQ} />}
      {s === 'empty' && <SearchStateView art="illustration-search" size={170} title="Search across your calls" body="Look for names, phone numbers, categories, keywords" />}
      {s === 'offline' && <SearchStateView art="illustration-no-internet" size={130} title="No internet connection" body="We could not connect to a stable internet. Check your connection and try again." />}
      {s === 'no-results' && <SearchStateView art="illustration-no-results" size={150} title="No results found" body="Couldn’t find anything matching. Try a different name, number, category or keyword" />}
      {(s === 'loading' || s === 'results' || s === 'messages-loading' || s === 'messages') && (
        <>
          <SearchSection title="Chats" onClear={s === 'messages-loading' || s === 'messages' ? () => setQ('') : undefined} />
          {s === 'loading' ? (
            <LoadingDots label="Searching chats" />
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: 1, background: 'var(--eq-stroke-subtle)' }}>
              {shownChats.map((c) => (
                <ChatListItem key={c.id} name={c.name} preview={c.preview} date={c.date} unread={c.unread} actions={c.actions} avatar={c.initials ? { initials: c.initials } : { category: c.category }} onClick={() => onOpenChat?.(c)} />
              ))}
            </div>
          )}
          {(s === 'messages-loading' || s === 'messages') && (
            <>
              <SearchSection title="Messages" />
              {s === 'messages-loading' ? (
                <LoadingDots label="Searching messages" />
              ) : (
                (state ? searchMessages.filter((m) => m.text.toLowerCase().includes(term)) : messages).map((m) => <MessageResult key={m.name + m.text} name={m.name} time={m.time} text={m.text} query={q} />)
              )}
            </>
          )}
        </>
      )}
    </Screen>
  );
}

/** Splash (Splash Screen design): full-bleed `button/primary` (lime in dark, logo black), the logo and the tagline. */
export function SplashScreen({ tagline = 'Your personal AI call assistant' }: { tagline?: string }) {
  return (
    <div className="eq-splash" role="img" aria-label={`Equal AI. ${tagline}`}>
      <StatusBar background="transparent" color="var(--eq-text-on-action)" />
      <div className="eq-splash__body" aria-hidden>
        <EqualLogo height={42} color="var(--eq-text-on-action)" />
        <Text variant="title" color="none" style={{ marginTop: 16 }}>{tagline}</Text>
      </div>
    </div>
  );
}
