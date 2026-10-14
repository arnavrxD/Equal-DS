// Chat action flows (Flows/Call Back, Mute, Reply On WhatsApp, Save Contact, Error states).
// One engine: a chat with its AI layer; tapping an action raises the task sheet (it stops below the
// chat header), the assistant asks or drafts, the user confirms, and the result lands in the chat as
// an action card. Every step can be pinned for docs; otherwise the flow runs live.
import { useLayoutEffect, useRef, useState, type ReactNode } from 'react';
import {
  ActionCard,
  AiChatPanel,
  AiTurn,
  AssistantSheetShell,
  AssistantTaskHeader,
  AssistantThinking,
  CallSummary,
  ChatBubble,
  ChatHeader,
  ChatWallpaper,
  Composer,
  DialerStandIn,
  EditBubble,
  FeedbackCard,
  HoldToSpeakBar,
  KeyboardSpace,
  LargeActionChip,
  MessageList,
  NewContactForm,
  OfflineScreen,
  OfflineSheet,
  PermissionDialog,
  Screen,
  StatusBar,
  SummaryActions,
  Text,
  ThreadEvent,
  VoicePlayer,
  AppBar,
  Icon,
  MissedCallCard,
  type CallFeedback,
  type Category,
} from '../index';

export type ChatAction = 'call-back' | 'mute' | 'reply-whatsapp' | 'save-contact';

/** Call Back can start from a missed call: we know why they call, only their name, or nothing. */
export type MissedCase = 'reason' | 'name' | 'unknown';

export type CallBackStep = 'chat' | 'ask' | 'dialer' | 'done';
export type MuteStep = 'chat' | 'confirm' | 'done';
export type WhatsAppStep = 'chat' | 'draft' | 'sending' | 'edit' | 'edit-long' | 'done';
export type SaveStep = 'chat' | 'review' | 'edit-ask' | 'form' | 'form-outlined' | 'saved' | 'no-permission' | 'resume' | 'granted' | 'denied';
export type ErrorState = 'generation' | 'offline-reply' | 'voice' | 'offline-sheet' | 'offline-screen';

type Contact = { name: string; first: string; phone: string; category: Category; company: string };

const CONTACTS: Record<ChatAction, Contact> = {
  'call-back': { name: 'Dr. Mehta Clinic', first: 'Dr. Mehta’s Clinic', phone: '+91 98717 46113', category: 'others', company: 'Dr. Mehta’s Clinic' },
  mute: { name: 'Amit Nanda', first: 'Amit', phone: '+91-81720 33604', category: 'na', company: 'Amit' },
  'reply-whatsapp': { name: 'Amit Nanda', first: 'Amit', phone: '+91-81720 33604', category: 'na', company: 'Amit' },
  'save-contact': { name: 'Rahul', first: 'Rahul', phone: '+91 98717 46113', category: 'na', company: 'Rahul' },
};

const SUMMARY: Record<ChatAction, string> = {
  'call-back': 'Vijay from NoBroker called with an offer on a 2BHK flat in Sector 12. You said you were not interested and ended the call.',
  mute: 'Amit invited you to dinner this Sunday, 8 PM at his place. He asked you to confirm by tonight.',
  'reply-whatsapp': 'Amit invited you to dinner this Sunday, 8 PM at his place. He asked you to confirm by tonight.',
  'save-contact': 'Vijay from NoBroker called with an offer on a 2BHK flat in Sector 12. You said you were not interested and ended the call.',
};

const DRAFT = 'Hi Amit! Count me in for Sunday 8 PM. See you there 👍';

/** The call history every action starts from: summary (recording, text, Call Back) and the rating card. */
function CallHistory({ action }: { action: ChatAction }) {
  return (
    <>
      <ThreadEvent label="Call Ended 4:37 PM" />
      <ChatBubble kind="assistant" sender="Equal AI" time="03:21" tail>Hey Keshav! Here is the summary of your call</ChatBubble>
      <div style={{ paddingLeft: 36 }}>
        <CallSummary summary={SUMMARY[action]} time="03:21" footer={<SummaryActions actions={[{ label: 'Call Back', icon: 'call-filled' }]} />} />
      </div>
      <div style={{ paddingLeft: 36, marginTop: 4 }}><FeedbackCard /></div>
    </>
  );
}

const PROMPT: Record<ChatAction, string> = {
  'call-back': 'Want me to give a quick call back to the clinic?',
  mute: 'Want me to send Amit a quick reply on WhatsApp?',
  'reply-whatsapp': 'Want me to send Amit a quick reply on WhatsApp?',
  'save-contact': 'This number isn’t saved. Add Rahul to your contacts?',
};

function Chips({ items, disabled }: { items: [string, string, (() => void)?][]; disabled?: boolean }) {
  return (
    <div className="eq-mya-chips" style={{ padding: 0 }}>
      {items.map(([emoji, label, onClick]) => <LargeActionChip key={label} emoji={emoji} label={label} disabled={disabled} onClick={onClick} />)}
    </div>
  );
}

function TaskSheet({ title, children, footer, onClose }: { title: string; children: ReactNode; footer: ReactNode; onClose?: () => void }) {
  // Like any chat, the task opens scrolled to its latest message.
  const end = useRef<HTMLSpanElement>(null);
  useLayoutEffect(() => {
    const body = end.current?.closest('.eq-mya-sheet__body');
    if (body) body.scrollTop = body.scrollHeight;
  });
  return (
    <AssistantSheetShell task inChat onScrim={onClose} header={<AssistantTaskHeader title={title} onClose={onClose} />} footer={footer}>
      <ChatWallpaper style={{ minHeight: '100%' }}><MessageList>{children}<span ref={end} /></MessageList></ChatWallpaper>
    </AssistantSheetShell>
  );
}

export type ChatActionScreenProps = {
  action: ChatAction;
  /** Pin a step (docs). Otherwise it runs live from `chat`. */
  step?: string;
  /** Docs: show the system keyboard stand-in where the user is typing. */
  keyboard?: boolean;
  /** Call Back only: the chat starts from a missed-call card instead of a call summary. */
  missed?: MissedCase;
  onBack?: () => void;
};

const MISSED: Record<MissedCase, Contact> = {
  reason: { name: 'Kuldeep Solanki', first: 'Kuldeep', phone: '+91 98450 22781', category: 'real-estate', company: 'Kuldeep Solanki' },
  name: { name: 'Valluri', first: 'Valluri', phone: '+91 99001 38452', category: 'na', company: 'Valluri' },
  unknown: { name: '+91 80 4681 0750', first: 'them', phone: '', category: 'na', company: 'this number' },
};

/** The thread when the assistant missed the call: the missed-call card (Call back) and, with a reason, thumbs. */
function MissedThread({ missed, onCallBack }: { missed: MissedCase; onCallBack?: () => void }) {
  const [feedback, setFeedback] = useState<CallFeedback>(null);
  return (
    <>
      <ThreadEvent label="Missed Call 3:31 PM" />
      <div style={{ paddingLeft: 36 }}>
        {missed === 'reason' ? (
          <MissedCallCard caller="Kuldeep Solanki" category="real-estate" reason={<>Promote <b>L&amp;T Realty residential</b> projects.</>} time="03:21" onCallBack={onCallBack} feedback={feedback} onFeedback={setFeedback} width={290} />
        ) : (
          <MissedCallCard caller={missed === 'name' ? 'Valluri' : undefined} time="3:31 PM" onCallBack={onCallBack} width={290} />
        )}
      </div>
    </>
  );
}

/** A chat whose AI layer carries out one action end to end. */
export function ChatActionScreen({ action, step, keyboard, missed, onBack }: ChatActionScreenProps) {
  const [live, setLive] = useState('chat');
  const [text, setText] = useState(DRAFT);
  const s = step ?? live;
  const go = (n: string) => step === undefined && setLive(n);
  const c = missed && action === 'call-back' ? MISSED[missed] : CONTACTS[action];
  const history = missed && action === 'call-back' ? <MissedThread missed={missed} onCallBack={() => go('ask')} /> : <CallHistory action={action} />;
  const close = () => go('chat');

  // ---------------------------------------------------------------- the result in the chat
  const results: ReactNode[] = [];
  if (action === 'call-back' && s === 'done') results.push(
    <AiTurn key="r" width={290} actions={[{ label: 'Call back', emoji: '📞' }]}>
      <ActionCardInline icon="call" title="Outgoing call" lines={missed ? [`You called back ${c.company}`] : [`You called back ${c.company}`, 'Bring previous medical reports']} />
    </AiTurn>,
  );
  if (action === 'mute' && s === 'done') results.push(
    <AiTurn key="a" width={220}>This caller has been muted</AiTurn>,
    <div key="b" style={{ paddingLeft: 36 }}><ActionCard icon="silent-mode" title={`${c.phone} muted`} details={[{ text: 'Calls from this caller will be muted' }]} actions={[{ label: 'Open muted caller list', emoji: '📋' }]} width={280} /></div>,
  );
  if (action === 'reply-whatsapp' && s === 'done') results.push(
    <AiTurn key="a" width={200}>Wrote a draft for Amit</AiTurn>,
    <div key="b" style={{ paddingLeft: 36 }}><ActionCard source="whatsapp" title="WhatsApp · Amit Nanda" quote={`“${text.length > 34 ? 'Count me in for Sunday 8 PM 👍' : text}”`} actions={[{ label: 'View message', emoji: '👀' }]} width={280} /></div>,
  );
  if (action === 'save-contact' && s === 'saved') results.push(
    <AiTurn key="a" width={260}>Saved! Rahul is in your contacts now.</AiTurn>,
    <div key="b" style={{ paddingLeft: 36 }}><ActionCard icon="person" title="Rahul (Electrician)" details={[{ text: c.phone }]} actions={[{ label: 'View contact', emoji: '👤' }]} width={280} /></div>,
  );
  if (action === 'save-contact' && s === 'resume') results.push(
    <AiTurn key="a" width={260} actions={[{ label: 'Continue saving', emoji: '▶️', onClick: () => go('granted') }]}>You stopped between saving this contact. Want to continue?</AiTurn>,
  );

  // ---------------------------------------------------------------- the AI layer's sheet
  let overlay: ReactNode;
  const sendChips: [string, string, (() => void)?][] = [['📤', 'Send'], ['✏️', 'Edit draft'], ['🖋️', 'Custom']];
  if (action === 'call-back' && s === 'ask') overlay = (
    <TaskSheet title={`Calling ${c.company}`} onClose={close} footer={<><Chips items={sendChips} /><div style={{ marginTop: 12 }}><Composer /></div></>}>
      <ChatBubble kind="outgoing" sender="You" time="03:21" tail>Call Back</ChatBubble>
      <AiTurn actions={[{ label: 'Call Back', emoji: '📞', onClick: () => go('dialer') }]}>
        {missed === 'reason' ? 'Kuldeep usually calls to promote L&T Realty residential projects. Want to call back anyway?' : missed ? 'I couldn’t pick this call, so I don’t know what it was about. Call back to find out?' : 'Try asking the following:\n\n•  what reports to bring\n•  confirm location or parking\n•  consultation charge\n•  insurance coverage'}
      </AiTurn>
    </TaskSheet>
  );
  if (action === 'mute' && s === 'confirm') overlay = (
    <TaskSheet title={`Muting ${c.name}`} onClose={close} footer={<><Chips items={sendChips} /><div style={{ marginTop: 12 }}><Composer /></div></>}>
      <ChatBubble kind="outgoing" sender="You" time="03:21" tail>Mute</ChatBubble>
      <AiTurn actions={[{ label: 'Mute', emoji: '🔕', onClick: () => go('done') }]}>Are you sure? You’ll not get any call from them even if it’s important</AiTurn>
    </TaskSheet>
  );
  if (action === 'reply-whatsapp' && (s === 'draft' || s === 'sending')) overlay = (
    <TaskSheet title="Replying Amit on WhatsApp" onClose={close} footer={<><Chips items={sendChips} disabled={s === 'sending'} /><div style={{ marginTop: 12 }}><Composer /></div></>}>
      <ChatBubble kind="outgoing" sender="You" time="03:21" tail>Reply on whatsapp</ChatBubble>
      <AiTurn actions={[{ label: 'Edit', emoji: '✏️', onClick: () => go('edit') }, { label: 'Send', emoji: '📤', onClick: () => (go('sending'), setTimeout(() => go('done'), 1400)) }]}>{`Sure! Here’s a draft for Amit:\n\n${text}`}</AiTurn>
      {s === 'sending' && <><ChatBubble kind="outgoing" sender="You" time="03:21" tail>send as is</ChatBubble><AssistantThinking label="Opening WhatsApp" /></>}
    </TaskSheet>
  );
  const contactCard = (actions: { label: string; emoji: string; onClick?: () => void }[]) => (
    <div style={{ paddingLeft: 36 }}><ActionCard icon="person" title="Rahul (Electrician)" details={[{ text: c.phone }]} actions={actions} width={280} /></div>
  );
  if (action === 'save-contact' && (s === 'review' || s === 'edit-ask' || s === 'granted' || s === 'denied' || s === 'no-permission')) {
    const voice = s === 'edit-ask';
    overlay = (
      <TaskSheet title="Saving contact" onClose={close} footer={<><Chips items={sendChips} /><div style={{ marginTop: 12 }}>{voice ? <HoldToSpeakBar /> : <Composer />}</div></>}>
        {s === 'review' || s === 'edit-ask' ? (
          <>
            <ChatBubble kind="outgoing" sender="You" time="03:21" tail>Save contact</ChatBubble>
            <AiTurn width={110}>Here you go</AiTurn>
            {contactCard([{ label: 'Edit', emoji: '✏️', onClick: () => go('edit-ask') }, { label: 'Save', emoji: '💾', onClick: () => go('saved') }])}
            <AiTurn width={220}>{'Looks right?\nTap Save to add them.'}</AiTurn>
            {voice && <><ChatBubble kind="outgoing" sender="You" time="03:21" tail>Edit</ChatBubble><AiTurn width={200}>What should I change?</AiTurn></>}
          </>
        ) : s === 'granted' ? (
          <>
            <ChatBubble kind="outgoing" sender="You" time="03:21" tail>Continue Saving</ChatBubble>
            <AiTurn width={240}>Here’s the contact, pre-filled from the call:</AiTurn>
            {contactCard([{ label: 'Edit', emoji: '✏️', onClick: () => go('form') }, { label: 'Confirm', emoji: '👍', onClick: () => go('saved') }])}
            <AiTurn width={260}>Does this look right? Tap Confirm to save, or let me know what to change</AiTurn>
          </>
        ) : (
          <>
            <ChatBubble kind="outgoing" sender="You" time="03:21" tail>{s === 'denied' ? 'Continue Saving' : 'Save contact'}</ChatBubble>
            <AiTurn width={260}>To save a contact, I need contact permission on your phone</AiTurn>
            <div style={{ paddingLeft: 36 }}><ActionCard icon="person-add" title="Contact Permission Required" details={[{ text: 'Allow Equal AI to create and manage known contacts' }]} actions={[{ label: 'Open Settings', emoji: '⚙️', onClick: () => go('resume') }]} width={280} /></div>
          </>
        )}
      </TaskSheet>
    );
    if (s === 'no-permission') overlay = <>{overlay}<PermissionDialog title="Allow contacts permission" body="I need your contacts permission to save contacts into your phone" cta="Open Settings" onAction={() => go('resume')} onClose={close} /></>;
  }

  // ---------------------------------------------------------------- full-screen steps
  if (action === 'call-back' && s === 'dialer') return <Screen surface="primary"><DialerStandIn number={missed ? c.phone || c.name : '+91 80 4681 0750'} onEnd={() => go('done')} /></Screen>;
  if (action === 'save-contact' && (s === 'form' || s === 'form-outlined')) {
    return (
      <Screen surface="primary" header={<div style={{ background: 'var(--eq-bg-primary)' }}><ChatHeader statusBar name="+91-99536 72604" avatar={{ category: 'na' }} onBack={close} /></div>} footer={keyboard ? <KeyboardSpace /> : undefined}>
        <NewContactForm variant={s === 'form' ? 'grouped' : 'outlined'} value={{ first: 'Rahul (Electrician)', phone: '+91 98717 46113', sync: s === 'form-outlined' }} onClose={close} onSave={() => go('saved')} />
      </Screen>
    );
  }
  if (action === 'reply-whatsapp' && (s === 'edit' || s === 'edit-long')) {
    const val = s === 'edit-long' && step !== undefined ? Array.from({ length: 11 }, () => 'Hi Amit! Count me in for Sunday 8').join(' ') : text;
    return (
      <Screen surface="primary" header={<AppBar statusBar title="Edit Message" align="start" onBack={() => go('draft')} />} footer={<><EditBubble value={val} onChange={setText} onDone={() => go('draft')} autoFocus={step === undefined} /><KeyboardSpace /></>}>
        <div style={{ position: 'relative', minHeight: '100%' }}>
          <ChatWallpaper style={{ minHeight: '100%' }}><MessageList>{history}</MessageList></ChatWallpaper>
          <span className="eq-edit-dim" aria-hidden />
        </div>
      </Screen>
    );
  }

  // ---------------------------------------------------------------- the chat
  const chips: Record<ChatAction, [string, string, (() => void)?][]> = {
    'call-back': [['📞', 'Call Back', () => go('ask')], ['🗓️', 'Add to Calendar'], ['🔔', 'Set reminder']],
    mute: [['🔕', 'Mute', () => go('confirm')], ['💬', 'Reply on WhatsApp'], ['🗓️', 'Add to Calendar']],
    'reply-whatsapp': [['💬', 'Reply on WhatsApp', () => go('draft')], ['🗓️', 'Add to Calendar']],
    'save-contact': [['👤', 'Save contact', () => go(step === undefined ? 'review' : 'review')], ['🗓️', 'Add to Calendar']],
  };
  return (
    <Screen
      header={<ChatHeader statusBar name={c.name} subtitle={c.phone} avatar={{ category: c.category }} onBack={onBack} onCall={() => {}} onMore={() => {}} />}
      footer={<AiChatPanel prompt={missed && action === 'call-back' ? 'The assistant missed this call. Want to call back?' : PROMPT[action]} actions={chips[action].map(([emoji, label, onClick]) => ({ emoji, label, onClick }))} composer={{}} />}
      overlay={overlay}
    >
      <ChatWallpaper style={{ minHeight: '100%' }}>
        <MessageList>
          {history}
          {results}
          <ScrollToEnd />
        </MessageList>
      </ChatWallpaper>
    </Screen>
  );
}

/** Scrolls its nearest scrolling ancestor to the end, so a chat opens at its latest message. */
function ScrollToEnd() {
  const ref = useRef<HTMLSpanElement>(null);
  useLayoutEffect(() => {
    let el = ref.current?.parentElement ?? null;
    while (el && !/(auto|scroll)/.test(getComputedStyle(el).overflowY)) el = el.parentElement;
    if (el) el.scrollTop = el.scrollHeight;
  });
  return <span ref={ref} />;
}

/** The grey body of an action card used inside an assistant turn (Outgoing call). */
function ActionCardInline({ icon, title, lines }: { icon: 'call'; title: string; lines: string[] }) {
  return (
    <span style={{ display: 'flex', gap: 12, padding: 8, marginTop: 4, borderRadius: 12, background: 'var(--eq-bg-primary)' }}>
      <span style={{ width: 40, height: 40, borderRadius: '50%', background: 'var(--eq-bg-surface)', display: 'inline-flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0, color: 'var(--eq-text-action)' }}>
        <Icon name="call-filled" size={20} />
      </span>
      <span>
        <Text as="span" variant="body2" style={{ display: 'block' }}>{title}</Text>
        {lines.map((l, i) => <Text key={l} as="span" variant="caption" color={i ? 'secondary' : 'primary'} style={{ display: 'block', fontWeight: 400 }}>{l}</Text>)}
      </span>
    </span>
  );
}

// ---------------------------------------------------------------- error states

/** How the AI layer fails: in a task (Retry), offline (Retry / Refresh / Try again), or on voice input. */
export function ChatErrorScreen({ state, onRetry }: { state: ErrorState; onRetry?: () => void }) {
  if (state === 'offline-screen') return <Screen surface="primary" header={<StatusBar background="transparent" />}><OfflineScreen onRetry={onRetry} /></Screen>;
  const unknown = { name: '+91-99536 72604', phone: '', category: 'na' as const };
  if (state === 'generation' || state === 'offline-reply') {
    const voiceChips: [string, string][] = [['✅', 'Add event'], ['✏️', 'Edit details'], ['🖋️', 'Custom']];
    return (
      <Screen header={<ChatHeader statusBar name={unknown.name} avatar={{ category: 'na' }} onCall={() => {}} onMore={() => {}} />} overlay={
        <TaskSheet title="Mute promotional calls from +91-99536 72604" footer={<><Chips items={voiceChips} /><div style={{ marginTop: 12 }}><HoldToSpeakBar /></div></>}>
          {state === 'generation' && <ChatBubble kind="outgoing" sender="You" time="03:21" tail>Mute</ChatBubble>}
          <AiTurn width={260} actions={[{ label: 'Retry', emoji: '🔄', onClick: onRetry }]}>
            {state === 'generation' ? 'Sorry, I couldn’t do that just now. Please try again.' : 'It looks like you’re offline.\nTry again once you’re back online.'}
          </AiTurn>
        </TaskSheet>
      }>
        <ChatWallpaper style={{ minHeight: '100%' }}><MessageList><ThreadEvent label="Call Ended 4:37 PM" /><ChatBubble kind="assistant" sender="Equal AI" time="03:21" tail>Hey Keshav! Here is the summary of your call</ChatBubble></MessageList></ChatWallpaper>
      </Screen>
    );
  }
  return (
    <Screen
      header={<ChatHeader statusBar name="Rahul" subtitle="+91 98717 46113" avatar={{ category: 'na' }} onCall={() => {}} onMore={() => {}} />}
      footer={state === 'voice' ? <AiChatPanel prompt="Sorry, I didn’t catch that. Could you hold and speak again?" actions={[{ label: 'Save contact', emoji: '👤' }, { label: 'Add to Calendar', emoji: '🗓️' }]} /> : undefined}
      overlay={state === 'offline-sheet' ? <OfflineSheet onRefresh={onRetry} /> : undefined}
    >
      <ChatWallpaper style={{ minHeight: '100%' }}>
        <MessageList>
          <CallHistory action="save-contact" />
          <AiTurn width={260}>Saved! Rahul is in your contacts now.</AiTurn>
          <div style={{ paddingLeft: 36 }}><ActionCard icon="person" title="Rahul (Electrician)" details={[{ text: '+91 98717 46113' }]} actions={[{ label: 'View contact', emoji: '👤' }]} width={280} /></div>
          <ScrollToEnd />
        </MessageList>
      </ChatWallpaper>
    </Screen>
  );
}
