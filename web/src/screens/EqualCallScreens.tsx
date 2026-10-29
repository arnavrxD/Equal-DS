// Equal user to Equal user calls (Equal To Equal Call Experience design): the receiver on the lock
// screen, over an app, on the launcher and in Equal (three strip options), the caller from a chat
// (mic permission, Calling, Ringing, Connected), the minimised strip, and the Voice Call card after.
import { useEffect, useState, type ReactNode } from 'react';
import { AiChatPanel, CallLogCard, CallStrip, CallSummaryMessage, ChatBubble, ChatHeader, ChatWallpaper, DayDivider, DeviceScreen, EqualCallScreen, IncomingCallNotification, MessageList, PermissionDialog, Screen, SystemPermissionStandIn, useCallTimer, type EqualCallState } from '../index';
import { HomeScreen } from './Screens';

const RECEIVER = { name: 'Keshav Reddy', initials: 'KR' };
const CALLER_PEER = { name: 'Chaitanya Sai', initials: 'CS', phone: '+91-81720 33604' };

export type ReceiverStage =
  | 'lock-ringing' | 'connecting' | 'connected'
  | 'heads-up-app' | 'heads-up-launcher'
  | 'strip-avatar' | 'strip-icon' | 'strip-accent'
  | 'minimised';

/** Heads-up over any screen. */
function HeadsUp({ children, onAnswer, onDecline }: { children: ReactNode; onAnswer?: () => void; onDecline?: () => void }) {
  return (
    <div style={{ position: 'relative', flex: 1, display: 'flex', flexDirection: 'column' }}>
      {children}
      <div className="eq-heads-up" style={{ position: 'absolute', left: 16, right: 16, top: 68, zIndex: 50 }}>
        <IncomingCallNotification name={RECEIVER.name} initials={RECEIVER.initials} onAnswer={onAnswer} onDecline={onDecline} />
      </div>
    </div>
  );
}

/**
 * Receiver POV. Ringing (lock screen, heads-up over Equal or the launcher, or the in-app strip once the
 * heads-up is swiped away) → Connecting → Connected; Minimise shows the ongoing strip on Chats and
 * tapping it returns to the call. Speaker and mute toggle; End goes back to Chats.
 */
export function EqualCallReceiverFlow({ stage: initial = 'lock-ringing' }: { stage?: ReceiverStage }) {
  const [stage, setStage] = useState<ReceiverStage>(initial);
  const [speaker, setSpeaker] = useState(false);
  const [muted, setMuted] = useState(false);
  const [ended, setEnded] = useState(false);
  useEffect(() => { setStage(initial); setEnded(false); }, [initial]);
  const seconds = useCallTimer(stage === 'connected' || stage === 'minimised', initial === 'minimised' ? 21 : initial === 'connected' ? 1 : 0);
  useEffect(() => {
    if (stage !== 'connecting' || initial === 'connecting') return;
    const id = window.setTimeout(() => setStage('connected'), 1500);
    return () => window.clearTimeout(id);
  }, [stage, initial]);
  const answer = () => setStage('connecting');
  if (ended) return <HomeScreen />;
  if (stage === 'heads-up-app') return <HeadsUp onAnswer={answer} onDecline={() => setEnded(true)}><HomeScreen /></HeadsUp>;
  if (stage === 'heads-up-launcher') return <HeadsUp onAnswer={answer} onDecline={() => setEnded(true)}><DeviceScreen time="12:30" /></HeadsUp>;
  if (stage === 'strip-avatar' || stage === 'strip-icon' || stage === 'strip-accent') {
    const style = stage === 'strip-avatar' ? 'avatar' : stage === 'strip-icon' ? 'icon' : 'accent';
    return <HomeScreen callStrip={<CallStrip state="incoming" style={style} name={RECEIVER.name} initials={RECEIVER.initials} onAnswer={answer} />} />;
  }
  if (stage === 'minimised') {
    return <HomeScreen callStrip={<CallStrip state="ongoing" name={RECEIVER.name} seconds={seconds} muted={muted} onMute={() => setMuted((v) => !v)} onOpen={() => setStage('connected')} onEnd={() => setEnded(true)} />} />;
  }
  const state: EqualCallState = stage === 'lock-ringing' ? 'ringing' : stage === 'connecting' ? 'connecting' : 'connected';
  return (
    <EqualCallScreen
      role="receiver"
      state={state}
      {...RECEIVER}
      seconds={seconds}
      speaker={speaker}
      muted={muted}
      onAccept={answer}
      onDecline={() => setEnded(true)}
      onMinimise={() => setStage('minimised')}
      onSpeaker={() => setSpeaker((v) => !v)}
      onMute={() => setMuted((v) => !v)}
      onEnd={() => setEnded(true)}
    />
  );
}

/** The caller's chat with Chaitanya: summary, optional ongoing strip (call icon disabled) and the Voice Call card after. */
export function EqualCallChat({ ongoing, seconds = 21, afterCall, onCall, onOpenCall, onEnd }: { ongoing?: boolean; seconds?: number; afterCall?: boolean; onCall?: () => void; onOpenCall?: () => void; onEnd?: () => void }) {
  const [muted, setMuted] = useState(false);
  return (
    <Screen
      header={
        <ChatHeader
          statusBar
          name={CALLER_PEER.name}
          subtitle={CALLER_PEER.phone}
          avatar={{ initials: CALLER_PEER.initials }}
          onCall={onCall}
          callDisabled={ongoing}
          callStrip={ongoing ? <CallStrip state="ongoing" name={CALLER_PEER.name} seconds={seconds} muted={muted} onMute={() => setMuted((v) => !v)} onOpen={onOpenCall} onEnd={onEnd} /> : undefined}
        />
      }
      footer={<AiChatPanel prompt="Want me to send Chaitanya a quick reply on WhatsApp?" actions={[{ label: 'Reply on WhatsApp', emoji: '💚' }, { label: 'Add to Calendar', emoji: '📅' }, { label: 'Call back', emoji: '📞' }]} composer={{ onSend: () => {} }} />}
    >
      <ChatWallpaper style={{ minHeight: '100%' }}>
        <MessageList>
          <DayDivider label="Today" />
          <ChatBubble kind="assistant" sender="Equal AI" time="03:21">Hey Keshav! Here is the summary of your call</ChatBubble>
          <CallSummaryMessage first={false} summary="Chaitanya invited you to dinner this Sunday, 8 PM at his place. He asked you to confirm by tonight." time="03:21" onShare={() => {}} width={264} />
          {afterCall && <ChatBubble kind="outgoing" sender="You" time="03:21" bare><CallLogCard duration="1min" /></ChatBubble>}
        </MessageList>
      </ChatWallpaper>
    </Screen>
  );
}

export type CallerStage = 'chat' | 'mic-prompt' | 'mic-system' | 'calling' | 'remote-ringing' | 'connected' | 'chat-ongoing' | 'chat-after';

/**
 * Caller POV: the chat → tap call → (first time) "Let’s get you talking" → Android's dialog → Calling… →
 * Ringing… → Connected. Minimise puts the strip over the chat (call icon disabled); End leaves the
 * Voice Call card in the thread. Not now / Deny stay in the chat.
 */
export function EqualCallCallerFlow({ stage: initial = 'chat', micGranted = false }: { stage?: CallerStage; micGranted?: boolean }) {
  const [stage, setStage] = useState<CallerStage>(initial);
  const [speaker, setSpeaker] = useState(false);
  const [muted, setMuted] = useState(false);
  useEffect(() => setStage(initial), [initial]);
  const seconds = useCallTimer(stage === 'connected' || stage === 'chat-ongoing', initial === 'chat-ongoing' ? 21 : initial === 'connected' ? 1 : 0);
  useEffect(() => {
    if (stage === initial) return;
    if (stage === 'calling') { const id = window.setTimeout(() => setStage('remote-ringing'), 1500); return () => window.clearTimeout(id); }
    if (stage === 'remote-ringing') { const id = window.setTimeout(() => setStage('connected'), 2000); return () => window.clearTimeout(id); }
  }, [stage, initial]);
  if (stage === 'chat' || stage === 'chat-after' || stage === 'chat-ongoing') {
    return <EqualCallChat ongoing={stage === 'chat-ongoing'} seconds={seconds} afterCall={stage === 'chat-after'} onCall={() => setStage(micGranted ? 'calling' : 'mic-prompt')} onOpenCall={() => setStage('connected')} onEnd={() => setStage('chat-after')} />;
  }
  if (stage === 'mic-prompt' || stage === 'mic-system') {
    return (
      <div style={{ position: 'relative', flex: 1, display: 'flex', flexDirection: 'column' }}>
        <EqualCallChat />
        {stage === 'mic-prompt'
          ? <PermissionDialog art="permission-microphone" title="Let’s get you talking" body="Equal needs access to your microphone so you can talk to people on calls." cta="Allow microphone" onAction={() => setStage('mic-system')} secondary="Not now" onSecondary={() => setStage('chat')} closable={false} />
          : <SystemPermissionStandIn onAllow={() => setStage('calling')} onDeny={() => setStage('chat')} />}
      </div>
    );
  }
  const state: EqualCallState = stage === 'calling' ? 'calling' : stage === 'remote-ringing' ? 'remote-ringing' : 'connected';
  return (
    <EqualCallScreen
      role="caller"
      state={state}
      name={CALLER_PEER.name}
      initials={CALLER_PEER.initials}
      seconds={seconds}
      speaker={speaker}
      muted={muted}
      onMinimise={() => setStage('chat-ongoing')}
      onMessage={() => setStage('chat-ongoing')}
      onSpeaker={() => setSpeaker((v) => !v)}
      onMute={() => setMuted((v) => !v)}
      onEnd={() => setStage('chat-after')}
    />
  );
}
