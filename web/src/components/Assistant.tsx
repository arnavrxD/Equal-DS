// Global MYA (Global MYA design): the assistant sheet that opens from the green orb (FAB) on any screen.
// Greeting + quick actions, suggestion chips, text or voice input, hold-to-record, and the short
// task conversation it turns into (voice note → Thinking… → result card → Edit / Confirm).
import { useEffect, useState, type ReactNode } from 'react';
import patternUrl from '../foundations/chat-pattern@3x.webp';
import './Flows.css';
import { useModal } from '../foundations/a11y';
import { Asset } from '../foundations/Asset';
import { Emoji, Icon } from '../foundations/Icon';
import type { IconName } from '../foundations/icons';
import { Text } from '../foundations/Text';
import { EqualMark } from '../foundations/EqualMark';
import { EqualOrb } from './Avatar';
import { IconButton } from './Button';
import './Assistant.css';

// ---------------------------------------------------------------- greeting

/** Orb + "Good morning, Keshav" (name in text/action) + "What can I help you with today?". */
export function AssistantGreeting({ name, greeting = 'Good morning', prompt = 'What can I help you with today?' }: { name: string; greeting?: string; prompt?: string }) {
  return (
    <div className="eq-mya-greeting">
      <EqualOrb size={56} label="" />
      <Text as="h2" variant="headline" align="center" style={{ fontWeight: 700, marginTop: 12 }}>
        {greeting}, <Text variant="inherit" color="action">{name}</Text>
      </Text>
      <Text as="p" variant="body1" color="secondary" align="center" style={{ margin: 0 }}>{prompt}</Text>
    </div>
  );
}

/** Time-of-day greeting for the sheet. */
export const greetingFor = (hour: number) => (hour < 12 ? 'Good morning' : hour < 17 ? 'Good afternoon' : 'Good evening');

// ---------------------------------------------------------------- quick actions

export type AssistantAction = { label: string; icon: IconName | 'equal'; onClick?: () => void };

/** Three white tiles under the greeting (icon in text/action, label below). */
export function AssistantActions({ items }: { items: AssistantAction[] }) {
  return (
    <div className="eq-mya-actions">
      {items.map((a, i) => (
        <button key={`${i}-${a.label}`} type="button" className="eq-reset-btn eq-pressable eq-mya-action" onClick={a.onClick}>
          {a.icon === 'equal' ? <EqualMark width={30} /> : <Icon name={a.icon} size={30} color="var(--eq-text-action)" />}
          <Text as="span" variant="body2" align="center" style={{ fontWeight: 400 }}>{a.label}</Text>
        </button>
      ))}
    </div>
  );
}

/** "Try saying" + an example request (shown while recording). */
export function TrySaying({ example }: { example: string }) {
  return (
    <div className="eq-mya-try">
      <Text as="div" variant="title" color="secondary" align="center" style={{ fontWeight: 400 }}>Try saying</Text>
      <Text as="div" variant="title" color="secondary" align="center" style={{ fontWeight: 400, fontSize: 22, lineHeight: '28px' }}>“{example}”</Text>
    </div>
  );
}

// ---------------------------------------------------------------- input

export type AssistantInputMode = 'text' | 'voice';

export type AssistantInputProps = {
  /** text: field + mic; voice: keyboard toggle + Send voice note. */
  mode: AssistantInputMode;
  value?: string;
  onChange?: (v: string) => void;
  onSubmit?: (v: string) => void;
  onFocus?: () => void;
  /** text mode: the mic switches to voice. */
  onMic?: () => void;
  /** voice mode: back to the keyboard. */
  onKeyboard?: () => void;
  /** voice mode: tap to record; press and hold for hold-to-record. */
  onVoiceNote?: () => void;
  onVoiceHold?: () => void;
  /** Focused look (green outline), e.g. while the keyboard is open. */
  focused?: boolean;
  placeholder?: string;
};

/** The assistant sheet's input row: a message field with the mic, or Send voice note with a keyboard toggle. */
export function AssistantInput({ mode, value = '', onChange, onSubmit, onFocus, onMic, onKeyboard, onVoiceNote, onVoiceHold, focused, placeholder = 'Type a message...' }: AssistantInputProps) {
  if (mode === 'voice') {
    let timer: ReturnType<typeof setTimeout> | undefined;
    return (
      <div className="eq-mya-input">
        <button type="button" className="eq-reset-btn eq-pressable eq-mya-round" aria-label="Type instead" onClick={onKeyboard}>
          <Icon name="keyboard" size={26} color="var(--eq-text-secondary)" />
        </button>
        <button
          type="button"
          className="eq-reset-btn eq-pressable eq-mya-voice-note"
          onClick={onVoiceNote}
          onPointerDown={onVoiceHold ? () => (timer = setTimeout(onVoiceHold, 450)) : undefined}
          onPointerUp={() => timer && clearTimeout(timer)}
        >
          <Icon name="mic-fill" size={22} />
          <Text variant="subheading" color="none" style={{ fontWeight: 700 }}>Send Voice Note</Text>
        </button>
      </div>
    );
  }
  return (
    <form className="eq-mya-input" onSubmit={(e) => (e.preventDefault(), value.trim() && onSubmit?.(value.trim()))}>
      <input
        className={`eq-mya-field eq-t-body-large ${focused ? 'is-focus' : ''}`}
        value={value}
        placeholder={placeholder}
        aria-label="Message your assistant"
        onChange={(e) => onChange?.(e.target.value)}
        onFocus={onFocus}
      />
      {/* Mic while empty; as soon as there is text it becomes Send. */}
      {value.trim() ? (
        <button type="submit" className="eq-reset-btn eq-pressable eq-mya-mic" aria-label="Send">
          <Icon name="send" size={22} />
        </button>
      ) : (
        <button type="button" className="eq-reset-btn eq-pressable eq-mya-mic" aria-label="Voice note" onClick={onMic}>
          <Icon name="mic-fill" size={24} />
        </button>
      )}
    </form>
  );
}

// ---------------------------------------------------------------- recorder

export type RecorderState = 'idle' | 'recording' | 'hold' | 'cancel';

function useRecordingBars(active: boolean, count = 22) {
  const [t, setT] = useState(0);
  useEffect(() => {
    if (!active || window.matchMedia?.('(prefers-reduced-motion: reduce)').matches) return;
    const id = setInterval(() => setT((x) => x + 1), 120);
    return () => clearInterval(id);
  }, [active]);
  return Array.from({ length: count }, (_, i) => 4 + Math.round(10 * Math.abs(Math.sin(i * 1.7 + t * 0.6) * Math.cos(i * 0.45 + t * 0.2))));
}

const mmss = (s: number) => `${Math.floor(s / 60)}:${String(Math.floor(s % 60)).padStart(2, '0')}`;

/** Coded art: the live recording waveform (bar heights in dp); dots while idle, red while cancelling. */
export function RecordingWaveform({ bars, state }: { bars: number[]; state: RecorderState }) {
  return (
    <span className={`eq-mya-wave ${state === 'cancel' ? 'is-cancel' : ''}`} aria-hidden>
      {state === 'idle' ? <span className="eq-mya-wave__dots" /> : bars.map((h, i) => <span key={i} style={{ height: h }} />)}
    </span>
  );
}

/**
 * Voice recorder bar. idle / recording: delete, timer + waveform, send. hold (press-and-hold):
 * "Release to send, slide left to cancel"; cancel (slid left): "Release to cancel", red waveform.
 */
export function VoiceRecorder({ state, seconds = 0, onDelete, onSend }: { state: RecorderState; seconds?: number; onDelete?: () => void; onSend?: () => void }) {
  const live = state !== 'idle';
  const bars = useRecordingBars(live);
  const wave = <RecordingWaveform bars={bars} state={state} />;
  if (state === 'hold' || state === 'cancel') {
    return (
      <div className="eq-mya-hold">
        <Text as="div" role="status" variant="caption" color="secondary" align="center" style={{ fontWeight: 400 }}>{state === 'cancel' ? 'Release to cancel' : 'Release to send, slide left to cancel'}</Text>
        <div className="eq-mya-pill">
          <Icon name="slide-left" size={18} color="var(--eq-text-secondary)" />
          <Text as="span" variant="body1" color="secondary" style={{ flex: 1 }}>Slide left to cancel</Text>
          {wave}
        </div>
      </div>
    );
  }
  return (
    <div className="eq-mya-input">
      <button type="button" className="eq-reset-btn eq-pressable eq-mya-round" aria-label="Delete recording" onClick={onDelete}>
        <Icon name="delete" size={24} color="var(--eq-text-danger)" />
      </button>
      <div className="eq-mya-pill" role="timer" aria-label={`Recording ${mmss(seconds)}`}>
        <Text variant="body1" color="primary" className="eq-tnum">{mmss(seconds)}</Text>
        {wave}
      </div>
      <button type="button" className="eq-reset-btn eq-pressable eq-mya-mic" aria-label="Send voice note" onClick={onSend}>
        <Icon name="navigation" size={26} />
      </button>
    </div>
  );
}

// ---------------------------------------------------------------- conversation

/** Header once the request becomes a task: orb + what the assistant is doing + close. */
export function AssistantTaskHeader({ title, onClose }: { title: ReactNode; onClose?: () => void }) {
  return (
    <div className="eq-mya-task">
      <EqualOrb size={32} label="" />
      <Text as="div" variant="subheading" style={{ flex: 1, minWidth: 0, fontWeight: 400 }}>{title}</Text>
      <IconButton icon="close" label="Close" variant="neutral" size={40} onClick={onClose} className="eq-mya-task__close" />
    </div>
  );
}

/** The assistant working: small orb + "Thinking…" (italic, dots pulse). */
export function AssistantThinking({ label = 'Thinking' }: { label?: string }) {
  return (
    <div className="eq-mya-thinking" role="status">
      <EqualOrb size={28} label="" />
      <Text as="span" variant="subheading" color="secondary" style={{ fontStyle: 'italic', fontWeight: 400 }}>
        {label}<span className="eq-mya-dots" aria-hidden><span>.</span><span>.</span><span>.</span></span>
      </Text>
    </div>
  );
}

/** Two reply pills under a result: revise (Edit, neutral) and commit (Confirm, highlighted). */
export function AssistantReplies({ onEdit, onConfirm, editLabel = 'Edit', confirmLabel = 'Confirm' }: { onEdit?: () => void; onConfirm?: () => void; editLabel?: string; confirmLabel?: string }) {
  return (
    <div className="eq-mya-replies">
      <button type="button" className="eq-reset-btn eq-pressable eq-mya-reply" onClick={onEdit}>
        <Emoji char="✍️" inline /> <Text variant="subheading" color="none" style={{ fontWeight: 400 }}>{editLabel}</Text>
      </button>
      <button type="button" className="eq-reset-btn eq-pressable eq-mya-reply is-commit" onClick={onConfirm}>
        <Emoji char="👍" inline /> <Text variant="subheading" color="none" style={{ fontWeight: 400 }}>{confirmLabel}</Text>
      </button>
    </div>
  );
}

/**
 * The system keyboard for docs and prototypes (asset `system-keyboard`, Gboard). The app never draws a
 * keyboard; on device the input just sits above the IME.
 */
export function KeyboardSpace() {
  return <div className="eq-mya-keyboard" aria-hidden><Asset name="system-keyboard" width={390} height={300} alt="" style={{ width: '100%', height: 'auto' }} /></div>;
}

// ---------------------------------------------------------------- sheet shell

/** The sheet itself: dimmed screen behind, radius 32 top, `sheet/bg`. `task` makes it the taller conversation sheet. */
export function AssistantSheetShell({ children, footer, header, task, inChat, mode = 'overlay', onScrim, onClose }: { children?: ReactNode; footer?: ReactNode; header?: ReactNode; task?: boolean; /** Opened from a chat's AI layer: the sheet stops below the chat header so the user still sees which chat it's in. */ inChat?: boolean; mode?: 'overlay' | 'inline'; onScrim?: () => void; /** Escape. Defaults to `onScrim`. */ onClose?: () => void }) {
  const modal = useModal({ open: true, onClose: onClose ?? onScrim, enabled: mode === 'overlay' });
  const panel = (
    <div className={`eq-mya-sheet ${task ? 'is-task' : ''} ${inChat ? 'is-in-chat' : ''} ${mode === 'inline' ? 'is-inline' : ''}`} role="dialog" aria-label="Your assistant" {...modal}>
      {header}
      <div className="eq-mya-sheet__body">{children}</div>
      {/* Task sheet: the chat wallpaper runs on behind the chips and the input, to the bottom edge. */}
      {footer && <div className={`eq-mya-sheet__footer ${task ? 'eq-wallpaper' : ''}`} style={task ? { ['--eq-pattern-url' as string]: `url(${patternUrl})` } : undefined}>{footer}</div>}
    </div>
  );
  if (mode === 'inline') return panel;
  return (
    <div className="eq-status-sheet-layer">
      <div className="eq-status-sheet-layer__scrim" onClick={onScrim} />
      {panel}
    </div>
  );
}

export type ReplyAction = { label: string; emoji: string; onClick?: () => void };

/**
 * The assistant's answer as a card in a task conversation: "Equal AI", the text (e.g. a drafted
 * WhatsApp reply), the time, then full-width green action rows led by their emoji (✏️ Edit, 📤 Send).
 */
export function AssistantReplyCard({ children, time, actions = [], sender = 'Equal AI', width = 300 }: { children: ReactNode; time?: string; actions?: ReplyAction[]; sender?: string; width?: number | string }) {
  return (
    <div className="eq-mya-reply-card" style={{ width, maxWidth: '100%' }}>
      <div style={{ padding: '10px 12px 6px' }}>
        <Text as="div" variant="body2" color="action">{sender}</Text>
        <Text as="div" variant="body1" style={{ fontWeight: 400, whiteSpace: 'pre-line' }}>{children}</Text>
        {time && <Text as="div" variant="micro" color="secondary" align="right">{time}</Text>}
      </div>
      {actions.map((a) => (
        <button key={a.label} type="button" className="eq-reset-btn eq-pressable eq-mya-reply-card__action" onClick={a.onClick}>
          <Emoji char={a.emoji} inline />
          <Text variant="button" color="none">{a.label}</Text>
        </button>
      ))}
    </div>
  );
}

/** Page dots under a promo carousel (active dot larger, in the action-stroke green). */
export function CarouselDots({ count, active }: { count: number; active: number }) {
  return (
    <div className="eq-carousel-dots" role="img" aria-label={`Card ${active + 1} of ${count}`}>
      {Array.from({ length: count }, (_, i) => <span key={i} className={i === active ? 'is-on' : ''} />)}
    </div>
  );
}
