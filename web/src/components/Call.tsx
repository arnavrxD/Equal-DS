// In-app calls (vOIP and Equal-to-Equal designs): the call shell shared by "Call me" with the
// assistant and Equal user to Equal user calls.
import { useEffect, useState, type ReactNode } from 'react';
import { Asset, type AssetName } from '../foundations/Asset';
import { EqualLogo } from '../foundations/EqualMark';
import { Icon } from '../foundations/Icon';
import type { IconName } from '../foundations/icons';
import { Text } from '../foundations/Text';
import { IconButton } from './Button';
import { ChatWallpaper } from './Flows';
import { StatusBar } from './StatusBar';
import './Call.css';

/** mm:ss (h:mm:ss past an hour), tabular. */
export const formatCallTime = (s: number) => {
  const h = Math.floor(s / 3600), m = Math.floor((s % 3600) / 60), sec = s % 60;
  const p = (n: number) => String(n).padStart(2, '0');
  return h ? `${h}:${p(m)}:${p(sec)}` : `${p(m)}:${p(sec)}`;
};

/** Counts seconds while `running`; freezes when it stops. */
export function useCallTimer(running: boolean, start = 0) {
  const [s, setS] = useState(start);
  useEffect(() => setS(start), [start]);
  useEffect(() => { if (!running) return; const id = window.setInterval(() => setS((v) => v + 1), 1000); return () => window.clearInterval(id); }, [running]);
  return s;
}

export type CallControlKind = 'speaker' | 'mute' | 'end' | 'accept' | 'decline' | 'message';
const CONTROL: Record<CallControlKind, { icon: IconName; label: string }> = {
  speaker: { icon: 'volume-up', label: 'Speaker' },
  mute: { icon: 'mic-off', label: 'Mute' },
  end: { icon: 'call-end', label: 'End call' },
  accept: { icon: 'call-filled', label: 'Answer' },
  decline: { icon: 'call-end', label: 'Decline' },
  message: { icon: 'message', label: 'Message' },
};

/**
 * Round in-call control (52 or 40, icon 24). Speaker and mute are switches: resting `call-control/bg`
 * with a 1px `stroke/medium` edge, on = `call-control/active-bg`. End and Decline are `button/end-call`,
 * Accept `button/primary`, Message `call-control/bg` + `stroke/subtle`. `label` adds a caption under it.
 */
export function CallControlButton({ kind, active = false, onClick, size = 52, label, ariaLabel }: { kind: CallControlKind; active?: boolean; onClick?: () => void; size?: number; /** Visible caption (`body2`, 6 below). */ label?: string; ariaLabel?: string }) {
  const c = CONTROL[kind];
  const toggle = kind === 'speaker' || kind === 'mute';
  const btn = (
    <button
      type="button"
      className={`eq-reset-btn eq-pressable eq-touch eq-call-ctl eq-call-ctl--${kind} ${active ? 'is-on' : ''}`}
      style={{ width: size, height: size }}
      role={toggle ? 'switch' : undefined}
      aria-checked={toggle ? active : undefined}
      aria-label={ariaLabel ?? c.label}
      onClick={onClick}
    >
      <Icon name={c.icon} size={24} />
    </button>
  );
  if (!label) return btn;
  return <span className="eq-call-ctl-labelled">{btn}<Text variant="body2" aria-hidden>{label}</Text></span>;
}

/** Pill of call controls (speaker, mute, end), `bg/primary` with a 1px `stroke/medium` edge, padding 12/32, gap 16. */
export function CallControlBar({ speaker, muted, onSpeaker, onMute, onEnd, controls = ['speaker', 'mute', 'end'] }: { speaker?: boolean; muted?: boolean; onSpeaker?: () => void; onMute?: () => void; onEnd?: () => void; controls?: CallControlKind[] }) {
  return (
    <div className="eq-call-bar" role="group" aria-label="Call controls">
      {controls.map((k) => <CallControlButton key={k} kind={k} active={k === 'speaker' ? speaker : k === 'mute' ? muted : false} onClick={k === 'speaker' ? onSpeaker : k === 'mute' ? onMute : onEnd} />)}
    </div>
  );
}

/** Elapsed call time: `body2` tabular, `text/secondary`. Not a live region (ticks aren't announced). */
export function CallTimer({ seconds }: { seconds: number }) {
  return <Text variant="body2" color="secondary" className="eq-tnum" aria-live="off">{formatCallTime(seconds)}</Text>;
}

/**
 * Who you're on the call with: a 200 disc (`gradient/assistant-call`) with the assistant bust (or any
 * art / initials), and up to three `button/primary` ripple rings (280 / 360 / 440 at 35 / 22 / 12 %).
 * `pulse` animates the rings outward (static under reduced motion).
 */
export function CallAvatar({ art = 'avatar-assistant-bust', initials, ripples = false, pulse = false, size = 200, label = 'Equal AI assistant', badge = false }: { art?: AssetName; initials?: string; ripples?: boolean; pulse?: boolean; size?: number; label?: string; /** Heads-up: the call-received badge (24) at the bottom-right. */ badge?: boolean }) {
  // Initials: DM Sans 600 at 40 % of the disc, on `call-avatar/bg`.
  const fs = Math.round(size * 0.4);
  return (
    <div className={`eq-call-avatar ${pulse ? 'is-pulsing' : ''}`} style={{ width: size, height: size }} role="img" aria-label={label}>
      {ripples && [1.4, 1.8, 2.2].map((k, i) => <span key={k} className={`eq-call-avatar__ring eq-call-avatar__ring--${i + 1}`} style={{ width: size * k, height: size * k }} aria-hidden />)}
      <span className={`eq-call-avatar__disc ${initials ? 'is-initials' : ''}`} aria-hidden>
        {initials ? <Text variant="headline" color="none" style={{ fontSize: fs, lineHeight: `${Math.round(fs * 1.15)}px`, fontWeight: 600 }}>{initials}</Text> : <Asset name={art} size={Math.round(size * 0.96)} alt="" />}
      </span>
      {badge && <span className="eq-call-avatar__badge" aria-hidden><Icon name="call-received" size={16} /></span>}
    </div>
  );
}

/** "Identified by" + the Equal logo in `text/secondary`: the Equal-user badge on call surfaces. One node: "Identified by Equal AI". */
export function IdentifiedByEqual({ size = 'lg' }: { size?: 'lg' | 'sm' }) {
  return (
    <span className="eq-identified" role="img" aria-label="Identified by Equal AI">
      <Text variant={size === 'lg' ? 'body2' : 'caption'} color="secondary" aria-hidden>Identified by</Text>
      <span aria-hidden style={{ display: 'inline-flex' }}><EqualLogo height={size === 'lg' ? 14 : 12} color="var(--eq-text-secondary)" /></span>
    </span>
  );
}

/** Incoming-call actions: Decline · [chevrons → Accept → "Swipe up to Accept"] · Message. Drag Accept up 86 (or tap it) to answer. */
export function SwipeToAnswer({ onAccept, onDecline, onMessage }: { onAccept?: () => void; onDecline?: () => void; onMessage?: () => void }) {
  const [drag, setDrag] = useState<{ y0: number; dy: number } | null>(null);
  const up = drag ? Math.max(0, Math.min(86, -drag.dy)) : 0;
  return (
    <div className="eq-swipe-answer">
      <CallControlButton kind="decline" label="Decline" onClick={onDecline} />
      <span className="eq-swipe-answer__accept">
        <span className="eq-swipe-answer__chevrons" aria-hidden>
          {[1, 0.7, 0.4, 0.2].map((o, i) => <span key={i} style={{ opacity: o, animationDelay: `${(3 - i) * 150}ms` }}><Icon name="chevron-up" size={20} /></span>)}
        </span>
        <span
          style={{ transform: `translateY(${-up}px)`, transition: drag ? 'none' : 'transform var(--eq-duration-base) var(--eq-easing-standard)', touchAction: 'none' }}
          onPointerDown={(e) => { (e.target as Element).setPointerCapture?.(e.pointerId); setDrag({ y0: e.clientY, dy: 0 }); }}
          onPointerMove={(e) => drag && setDrag({ ...drag, dy: e.clientY - drag.y0 })}
          onPointerUp={() => { if (up >= 86) onAccept?.(); setDrag(null); }}
          onPointerCancel={() => setDrag(null)}
        >
          <CallControlButton kind="accept" ariaLabel="Answer" onClick={() => { if (!drag || up < 4) onAccept?.(); }} />
        </span>
        <Text variant="body2" aria-hidden>Swipe up to Accept</Text>
      </span>
      <CallControlButton kind="message" label="Message" onClick={onMessage} />
    </div>
  );
}

export type EqualCallState = 'ringing' | 'connecting' | 'calling' | 'remote-ringing' | 'connected' | 'ended';

export type EqualCallScreenProps = {
  role: 'caller' | 'receiver';
  state: EqualCallState;
  name: string;
  initials: string;
  seconds?: number;
  speaker?: boolean;
  muted?: boolean;
  onAccept?: () => void;
  onDecline?: () => void;
  onMessage?: () => void;
  onMinimise?: () => void;
  onSpeaker?: () => void;
  onMute?: () => void;
  onEnd?: () => void;
  overlay?: ReactNode;
};

/**
 * Equal user to Equal user call (Equal To Equal Call Experience design), 360 × 800. Ringing and
 * Connecting: the large name + Identified by, the 200 initials disc, and Swipe to answer. Calling,
 * Ringing (caller) and Connected: the compact header (Minimise · name + status · Message for the
 * caller), the disc (ripples once connected), Connected + timer, and the control bar 40 from the bottom.
 */
export function EqualCallScreen({ role, state, name, initials, seconds = 0, speaker, muted, onAccept, onDecline, onMessage, onMinimise, onSpeaker, onMute, onEnd, overlay }: EqualCallScreenProps) {
  const large = state === 'ringing' || state === 'connecting';
  const live = state === 'connected';
  return (
    <ChatWallpaper style={{ flex: 1, minHeight: 0, display: 'flex', flexDirection: 'column', position: 'relative' }}>
      <StatusBar background="transparent" />
      {large ? (
        <div className="eq-e2e-head is-large">
          <Text as="h1" variant="headline" align="center" truncate>{name}</Text>
          <IdentifiedByEqual size="lg" />
        </div>
      ) : (
        <div className="eq-e2e-head">
          <IconButton icon="minimise" label="Minimise call" size={40} onClick={onMinimise} className="eq-e2e-subtle" />
          <span className="eq-e2e-head__title">
            <Text as="h1" variant="subheading" align="center" truncate>{name}</Text>
            {state === 'calling' || state === 'remote-ringing' ? <Text variant="body1" color="secondary" role="status">{state === 'calling' ? 'Calling…' : 'Ringing…'}</Text> : <IdentifiedByEqual size="sm" />}
          </span>
          {role === 'caller' ? <IconButton icon="message" label="Message" size={40} onClick={onMessage} className="eq-e2e-subtle" /> : <span style={{ width: 40 }} aria-hidden />}
        </div>
      )}
      <div className="eq-e2e-body" style={{ paddingTop: state === 'ringing' ? 163 : state === 'connecting' ? 139 : 116 }}>
        <CallAvatar initials={initials} ripples={live} pulse={state === 'remote-ringing'} label={name} />
        {state === 'connecting' && <span style={{ marginTop: 24 }}><Text variant="title" color="secondary" role="status">Connecting...</Text></span>}
        {live && <span style={{ marginTop: 24 }}><CallStatusLine phase="connected" muted={muted} /></span>}
        {live && <span style={{ marginTop: 4 }}><CallTimer seconds={seconds} /></span>}
        {state === 'ended' && <span style={{ marginTop: 24 }}><CallStatusLine phase="ended" /></span>}
      </div>
      {state === 'ringing' && <div className="eq-e2e-foot"><SwipeToAnswer onAccept={onAccept} onDecline={onDecline} onMessage={onMessage} /></div>}
      {!large && state !== 'ended' && <div className="eq-e2e-foot"><CallControlBar speaker={speaker} muted={muted} onSpeaker={onSpeaker} onMute={onMute} onEnd={onEnd} /></div>}
      {overlay}
    </ChatWallpaper>
  );
}

export type CallStripProps =
  | { state: 'incoming'; name: string; initials?: string; style?: 'avatar' | 'icon' | 'accent'; onAnswer?: () => void }
  | { state: 'ongoing'; name: string; seconds: number; muted?: boolean; onMute?: () => void; onOpen?: () => void; onEnd?: () => void };

/**
 * Full-width in-app call strip under the status bar (`call-strip/bg`). Incoming: one button "Answer
 * call from …" (option 1 avatar + name, 2 green icon + green name, 3 all green). Ongoing: mute ·
 * "Name - 0:21" (opens the call) · end.
 */
export function CallStrip(props: CallStripProps) {
  if (props.state === 'incoming') {
    const { name, initials = '', style = 'avatar', onAnswer } = props;
    return (
      <button type="button" className={`eq-reset-btn eq-call-strip is-incoming is-${style}`} aria-label={`Answer call from ${name}`} onClick={onAnswer}>
        {style === 'avatar' ? <CallAvatar initials={initials} size={28} label="" /> : <Icon name="call-filled" size={20} color="var(--eq-text-action)" />}
        <Text variant="body2" color={style === 'avatar' ? 'primary' : 'action'} style={{ fontWeight: style === 'accent' ? 500 : 600 }}>{name}</Text>
        {style === 'accent'
          ? <Text variant="body2" color="action">- Incoming call</Text>
          : <Text variant="body1" color="secondary">- Incoming Call</Text>}
      </button>
    );
  }
  const { name, seconds, muted, onMute, onOpen, onEnd } = props;
  return (
    <div className="eq-call-strip is-ongoing" role="group" aria-label="Ongoing call">
      <CallControlButton kind="mute" size={40} active={muted} onClick={onMute} />
      <button type="button" className="eq-reset-btn eq-touch eq-call-strip__label" aria-label={`Return to call with ${name}, ${formatCallTime(seconds)}`} onClick={onOpen}>
        <Icon name="call-filled" size={20} color="var(--eq-text-action)" />
        <Text variant="subheading" color="action" truncate>{name}</Text>
        <Text variant="body2" color="action">-</Text>
        <Text variant="subheading" color="action" className="eq-tnum">{formatCallTime(seconds).replace(/^0/, '')}</Text>
      </button>
      <CallControlButton kind="end" size={40} onClick={onEnd} />
    </div>
  );
}

/**
 * Docs stand-in for Android's CallStyle heads-up (the OS draws it on device; post
 * `NotificationCompat.CallStyle.forIncomingCall`). Always dark, as the system draws it.
 */
export function IncomingCallNotification({ name, initials, app = 'Equal AI', time = 'now', onAnswer, onDecline }: { name: string; initials: string; app?: string; time?: string; onAnswer?: () => void; onDecline?: () => void }) {
  return (
    <div className="eq-incoming-notif" data-theme="dark" role="alertdialog" aria-label={`Incoming voice call from ${name}`}>
      <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
        <CallAvatar initials={initials} size={48} badge label="" />
        <span style={{ minWidth: 0 }}>
          <span style={{ display: 'flex', alignItems: 'baseline', gap: 6 }}>
            <Text variant="subheading" color="on-dark" truncate>{name}</Text>
            <Text variant="body2" color="on-dark-secondary">• {app} • {time}</Text>
          </span>
          <Text as="div" variant="body2" color="on-dark-secondary" style={{ marginTop: 4 }}>Incoming Voice Call</Text>
        </span>
      </div>
      <div className="eq-incoming-notif__actions">
        <button type="button" className="eq-reset-btn eq-incoming-notif__btn is-decline" onClick={onDecline}><Icon name="call-end-outline" size={24} /><Text variant="subheading" color="none">DECLINE</Text></button>
        <button type="button" className="eq-reset-btn eq-incoming-notif__btn is-answer" onClick={onAnswer}><Icon name="call" size={24} /><Text variant="subheading" color="none">ANSWER</Text></button>
      </div>
    </div>
  );
}

/** A call in the thread: icon circle (40, `action-card/highlight`) + "Voice Call" + the duration, inside an outgoing bubble. */
export function CallLogCard({ duration, direction = 'outgoing', title = 'Voice Call' }: { duration: string; direction?: 'outgoing' | 'incoming'; title?: string }) {
  return (
    <span className="eq-call-log">
      <span className="eq-call-log__icon"><Icon name={direction === 'outgoing' ? 'call-made' : 'call-received'} size={24} color="var(--eq-text-action)" /></span>
      <span>
        <Text as="div" variant="body2">{title}</Text>
        <Text as="div" variant="captionWrap" color="secondary">{duration}</Text>
      </span>
    </span>
  );
}

/**
 * Call Takeover message (Personal Number call): your right-side bubble (`chat-bubble/other-bg`, the
 * "You" label 10 SemiBold `text/success` in its top corner) holding the takeover card
 * (`takeover-card/bg` + 1 inside stroke, radius 12, padding 8): a 40 `bg/surface` disc with
 * `phone-incoming` in `text/action`, the title (body2) over what happened (captionWrap, 2 apart), and
 * the time (badgeCount) in the card's bottom corner. Drop it in a `MessageList` like a `ChatBubble`.
 */
export function TakeoverCard({ time, title = 'Call taken over', body = 'You took over the call', sender = 'You' }: { time: string; title?: string; body?: string; sender?: string }) {
  return (
    <div className="eq-bubble-row is-right is-first">
      <div className="eq-takeover-msg">
        <Text variant="badgeCount" color="success" className="eq-takeover-msg__sender" style={{ fontWeight: 600 }}>{sender}</Text>
        <div className="eq-takeover-msg__card">
          <span className="eq-takeover-msg__icon"><Icon name="phone-incoming" size={24} color="var(--eq-text-action)" /></span>
          <span className="eq-takeover-msg__text">
            <Text as="div" variant="body2">{title}</Text>
            <Text as="div" variant="captionWrap" color="secondary">{body}</Text>
          </span>
          <Text variant="badgeCount" color="secondary" className="eq-takeover-msg__time">{time}</Text>
        </div>
      </div>
    </div>
  );
}

export type CallPhase = 'connecting' | 'calling' | 'ringing' | 'connected' | 'ended';
const PHASE: Record<CallPhase, string> = { connecting: 'Connecting…', calling: 'Calling…', ringing: 'Ringing…', connected: 'Connected', ended: 'Call ended' };

/** Status line: `title`; Connected in `text/action`, everything else (and "On mute") `text/secondary`. */
export function CallStatusLine({ phase, muted }: { phase: CallPhase; muted?: boolean }) {
  if (phase === 'connected' && muted) {
    return <span className="eq-call-status"><Icon name="mic-off" size={24} color="var(--eq-text-secondary)" /><Text variant="title" color="secondary">On mute</Text></span>;
  }
  return <span className="eq-call-status" role="status"><Text variant="title" color={phase === 'connected' ? 'action' : 'secondary'}>{PHASE[phase]}</Text></span>;
}

export type CallScreenProps = {
  phase: CallPhase;
  seconds?: number;
  speaker?: boolean;
  muted?: boolean;
  onSpeaker?: () => void;
  onMute?: () => void;
  onEnd?: () => void;
  onBack?: () => void;
  /** Who is on the other end: the assistant (default) or a person's initials. */
  initials?: string;
  name?: string;
  /** Under the name, e.g. "Identified by Equal". */
  subtitle?: ReactNode;
  /** Overlays (the mic permission dialog). */
  overlay?: ReactNode;
};

/**
 * The in-app call screen (vOIP design), 360 × 800: wallpaper → status bar → back + logo → call avatar
 * → status → timer → control bar 32 from the bottom. Ripples and controls show only while connected.
 */
export function CallScreen({ phase, seconds = 0, speaker, muted, onSpeaker, onMute, onEnd, onBack, initials, name, subtitle, overlay }: CallScreenProps) {
  const live = phase === 'connected';
  return (
    <ChatWallpaper style={{ flex: 1, minHeight: 0, display: 'flex', flexDirection: 'column', position: 'relative' }}>
      <StatusBar background="transparent" />
      <div className="eq-call-top">
        <IconButton icon="arrow-back" label="Back" onClick={onBack} />
        <span className="eq-call-top__logo"><EqualLogo height={24} /></span>
        <span style={{ width: 40 }} />
      </div>
      <div className="eq-call-body">
        <CallAvatar initials={initials} ripples={live} pulse={phase === 'connecting' || phase === 'ringing'} label={name ?? 'Equal AI assistant'} />
        {name && <Text as="h1" variant="headline" align="center" style={{ marginTop: 24 }}>{name}</Text>}
        {subtitle}
        <span style={{ marginTop: name ? 8 : 20 }}><CallStatusLine phase={phase} muted={muted} /></span>
        {(live || phase === 'ended') && <span style={{ marginTop: 4 }}><CallTimer seconds={seconds} /></span>}
      </div>
      {live && <div className="eq-call-foot"><CallControlBar speaker={speaker} muted={muted} onSpeaker={onSpeaker} onMute={onMute} onEnd={onEnd} /></div>}
      {overlay}
    </ChatWallpaper>
  );
}

/**
 * Docs stand-in for Android's runtime permission dialog (the real one is drawn by the OS): message
 * with the app name in bold, then full-width Allow / Deny rows.
 */
export function SystemPermissionStandIn({ icon = 'mic', app = 'Equal AI', what = 'access your microphone', onAllow, onDeny }: { icon?: IconName; app?: string; what?: string; onAllow?: () => void; onDeny?: () => void }) {
  return (
    <div className="eq-dialog-layer">
      <div className="eq-status-sheet-layer__scrim" style={{ background: 'rgba(0,0,0,0.45)' }} />
      <div className="eq-sys-perm" role="dialog" aria-label={`Allow ${app} to ${what}?`}>
        <Icon name={icon} size={24} color="var(--eq-text-primary)" />
        <Text as="p" variant="bodyLarge" align="center">Allow <b>{app}</b> to {what}.</Text>
        <div className="eq-sys-perm__rows">
          <button type="button" className="eq-reset-btn eq-sys-perm__row" onClick={onAllow}><Text variant="button" color="action">Allow</Text></button>
          <button type="button" className="eq-reset-btn eq-sys-perm__row" onClick={onDeny}><Text variant="button" color="action">Deny</Text></button>
        </div>
      </div>
    </div>
  );
}
