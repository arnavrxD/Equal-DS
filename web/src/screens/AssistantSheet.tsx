// Global MYA (Global MYA design): the assistant sheet that opens from the green orb, as a live flow.
// greeting → mic → voice → Send voice note → recording → send → sent → Thinking… → reminder card
// → Confirm → confirmed → closes (the chat row then reads "Reminder added").
import { useEffect, useState } from 'react';
import {
  Composer,
  AssistantReplyCard,
  AiAvatar,
  ActionCardBody,
  AssistantActions,
  AssistantGreeting,
  AssistantInput,
  AssistantReplies,
  AssistantSheetShell,
  AssistantTaskHeader,
  AssistantThinking,
  ChatBubble,
  ChatWallpaper,
  KeyboardSpace,
  LargeActionChip,
  MessageList,
  TrySaying,
  VoicePlayer,
  VoiceRecorder,
  type AssistantAction,
} from '../index';

export type MyaStage =
  | 'greeting'
  | 'voice'
  | 'typing'
  | 'typed'
  | 'voice-prompt'
  | 'recording'
  | 'hold'
  | 'hold-cancel'
  | 'sent'
  | 'thinking'
  | 'reminder'
  | 'confirmed';

export const myaActions: AssistantAction[] = [
  { label: 'Talk to Equal', icon: 'equal' },
  { label: 'Set Instruction', icon: 'auto-fix' },
  { label: 'Make a Call', icon: 'call-filled' },
];

const REQUEST = 'Set up a remainder for appointment at Dr. Mehta’s';

export type AssistantSheetProps = {
  name?: string;
  /** Pin a stage (docs); otherwise the sheet runs live from `greeting`. */
  stage?: MyaStage;
  onClose?: () => void;
  /** Live flow finished (reminder confirmed). */
  onDone?: () => void;
  mode?: 'overlay' | 'inline';
};

export function AssistantSheet({ name = 'Keshav', stage, onClose, onDone, mode = 'overlay' }: AssistantSheetProps) {
  const [live, setLive] = useState<MyaStage>('greeting');
  const [text, setText] = useState('');
  const [seconds, setSeconds] = useState(0);
  const s = stage ?? live;
  const go = (next: MyaStage) => stage === undefined && setLive(next);

  // Live timing: recording counts up; sent → thinking → reminder; confirmed → done.
  useEffect(() => {
    if (stage !== undefined) return;
    if (s === 'voice-prompt') {
      const t = setTimeout(() => (setSeconds(0), setLive('recording')), 900);
      return () => clearTimeout(t);
    }
    if (s === 'recording') {
      const t = setInterval(() => setSeconds((x) => x + 1), 1000);
      return () => clearInterval(t);
    }
    if (s === 'sent') { const t = setTimeout(() => setLive('thinking'), 900); return () => clearTimeout(t); }
    if (s === 'thinking') { const t = setTimeout(() => setLive('reminder'), 1500); return () => clearTimeout(t); }
    if (s === 'confirmed') { const t = setTimeout(() => onDone?.(), 1600); return () => clearTimeout(t); }
  }, [s, stage, onDone]);

  const chips = (
    <div className="eq-mya-chips">
      <LargeActionChip emoji="🗓️" label="Reply Amit on WhatsApp" onClick={() => go('typed')} />
      <LargeActionChip emoji="🔔" label="Set reminder" onClick={() => (setText('Set a remainder for appointment'), go('typed'))} />
    </div>
  );

  // ---------------------------------------------------------------- task conversation
  if (s === 'sent' || s === 'thinking' || s === 'reminder' || s === 'confirmed') {
    const voice = text === '';
    return (
      <AssistantSheetShell
        task
        mode={mode}
        onScrim={onClose}
        header={<AssistantTaskHeader title="Setting up a reminder for appointment at Dr. Mehta’s" onClose={onClose} />}
        footer={
          <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
            {s === 'reminder' && <AssistantReplies onEdit={() => (setText(REQUEST), go('typing'))} onConfirm={() => go('confirmed')} />}
            <AssistantInput mode="voice" onKeyboard={() => go('typing')} onVoiceNote={() => go('voice-prompt')} />
          </div>
        }
      >
        <ChatWallpaper style={{ minHeight: '100%' }}>
          <MessageList>
            <ChatBubble kind="outgoing" sender="You" time="03:21">
              {voice && <div style={{ margin: '4px 0 8px' }}><VoicePlayer duration={157} tone="sent" /></div>}
              {voice ? REQUEST : text}
            </ChatBubble>
            {s === 'thinking' && <AssistantThinking />}
            {(s === 'reminder' || s === 'confirmed') && (
              <>
                <ChatBubble kind="assistant" sender="Equal AI" time="03:21">Got it! Here’s your remainder:</ChatBubble>
                <div style={{ position: 'relative', marginLeft: 36, maxWidth: 300, background: 'var(--eq-bg-surface)', borderRadius: 16, padding: 8 }}>
                  <ActionCardBody source="whatsapp" title="Reminder · Dr. Mehta’s Clinic" details={[{ text: 'Tomorrow, 9:00 AM' }, { text: 'Bring previous medical reports' }]} time="03:21" />
                  {s === 'confirmed' && <span className="eq-mya-done" role="img" aria-label="Confirmed">✅</span>}
                </div>
                {s === 'confirmed' && <ChatBubble kind="outgoing" sender="You" time="03:21">Confirm</ChatBubble>}
              </>
            )}
          </MessageList>
        </ChatWallpaper>
      </AssistantSheetShell>
    );
  }

  // ---------------------------------------------------------------- recording
  if (s === 'voice-prompt' || s === 'recording' || s === 'hold' || s === 'hold-cancel') {
    return (
      <AssistantSheetShell
        mode={mode}
        onScrim={onClose}
        footer={
          <VoiceRecorder
            state={s === 'voice-prompt' ? 'idle' : s === 'recording' ? 'recording' : s === 'hold' ? 'hold' : 'cancel'}
            seconds={stage === undefined ? seconds : s === 'recording' ? 4 : 0}
            onDelete={() => go('voice')}
            onSend={() => (setText(''), go('sent'))}
          />
        }
      >
        <AssistantGreeting name={name} />
        <TrySaying example="Remind me to call Amit at 6" />
      </AssistantSheetShell>
    );
  }

  // ---------------------------------------------------------------- greeting / typing
  const typing = s === 'typing' || s === 'typed';
  const value = stage !== undefined ? (s === 'typed' ? 'Set a remainder for appointment' : '') : text;
  return (
    <AssistantSheetShell
      mode={mode}
      onScrim={onClose}
      footer={
        <>
          <AssistantInput
            mode={s === 'voice' ? 'voice' : 'text'}
            value={value}
            focused={typing}
            onChange={setText}
            onFocus={() => go('typing')}
            onSubmit={() => go('sent')}
            onMic={() => go('voice')}
            onKeyboard={() => go('greeting')}
            onVoiceNote={() => go('voice-prompt')}
            onVoiceHold={() => go('hold')}
          />
          {typing && <div style={{ margin: '16px -16px -20px' }}><KeyboardSpace /></div>}
        </>
      }
    >
      <AssistantGreeting name={name} />
      {!typing && <AssistantActions items={myaActions.map((a) => ({ ...a, onClick: () => go('voice') }))} />}
      {chips}
    </AssistantSheetShell>
  );
}

// ---------------------------------------------------------------- the AI layer inside a chat

export type ChatTaskStage = 'draft' | 'sending';

export type ChatTaskSheetProps = {
  /** First name of the person in the chat (the title says who the task is for). */
  contact: string;
  /** What the user asked (a chip they tapped or what they typed). */
  request: string;
  /** Pin a stage (docs); otherwise draft → (Send / "send as is") → sending (Opening WhatsApp…). */
  stage?: ChatTaskStage;
  onClose?: () => void;
  /** Hand-off once sending starts (open WhatsApp). */
  onSend?: () => void;
  mode?: 'overlay' | 'inline';
};

/**
 * A chat's AI layer turned into a task: the sheet rises over the conversation but stops below its
 * header, so the user can still see which chat it belongs to. Request → the drafted reply (✏️ Edit /
 * 📤 Send) → the user confirms → "Opening WhatsApp…" while the chips grey out.
 */
export function ChatTaskSheet({ contact, request, stage, onClose, onSend, mode = 'overlay' }: ChatTaskSheetProps) {
  const [live, setLive] = useState<ChatTaskStage>('draft');
  const [said, setSaid] = useState<string | null>(null);
  const s = stage ?? live;
  const confirm = stage !== undefined ? 'send as is' : said;
  const send = (text: string) => {
    if (stage !== undefined) return;
    setSaid(text);
    setLive('sending');
    onSend?.();
  };
  const sending = s === 'sending';
  return (
    <AssistantSheetShell
      task
      inChat
      mode={mode}
      onScrim={onClose}
      header={<AssistantTaskHeader title={`Replying ${contact} on WhatsApp`} onClose={onClose} />}
      footer={
        <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
          <div className="eq-mya-chips" style={{ padding: 0 }}>
            <LargeActionChip emoji="📤" label="Send" disabled={sending} onClick={() => send('Send')} />
            <LargeActionChip emoji="✏️" label="Edit draft" disabled={sending} />
            <LargeActionChip emoji="🖋️" label="Custom" disabled={sending} />
          </div>
          <Composer onSend={(t) => send(t)} />
        </div>
      }
    >
      <ChatWallpaper style={{ minHeight: '100%' }}>
        <MessageList>
          <ChatBubble kind="outgoing" sender="You" time="03:21">{request}</ChatBubble>
          <div style={{ display: 'flex', gap: 8, alignItems: 'flex-start' }}>
            <AiAvatar size={28} />
            <AssistantReplyCard
              time="03:21"
              actions={[
                { label: 'Edit', emoji: '✏️' },
                { label: 'Send', emoji: '📤', onClick: () => send('Send') },
              ]}
            >
              {`Sure! Here’s a draft for ${contact}:\n\nHi ${contact}! Count me in for Sunday 8 PM. See you there 👍`}
            </AssistantReplyCard>
          </div>
          {sending && (
            <>
              <ChatBubble kind="outgoing" sender="You" time="03:21">{confirm ?? 'send as is'}</ChatBubble>
              <AssistantThinking label="Opening WhatsApp" />
            </>
          )}
        </MessageList>
      </ChatWallpaper>
    </AssistantSheetShell>
  );
}
