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

export type CallControlKind = 'speaker' | 'mute' | 'end';
const CONTROL: Record<CallControlKind, { icon: IconName; label: string }> = {
  speaker: { icon: 'volume-up', label: 'Speaker' },
  mute: { icon: 'mic-off', label: 'Mute' },
  end: { icon: 'call-end', label: 'End call' },
};

/**
 * Round in-call control (52, icon 24). Speaker and mute are switches: resting `call-control/bg` with a
 * 1px `stroke/medium` edge, on = `call-control/active-bg`. End is `button/end-call`.
 */
export function CallControlButton({ kind, active = false, onClick, size = 52 }: { kind: CallControlKind; active?: boolean; onClick?: () => void; size?: number }) {
  const c = CONTROL[kind];
  const toggle = kind !== 'end';
  return (
    <button
      type="button"
      className={`eq-reset-btn eq-pressable eq-touch eq-call-ctl eq-call-ctl--${kind} ${active ? 'is-on' : ''}`}
      style={{ width: size, height: size }}
      role={toggle ? 'switch' : undefined}
      aria-checked={toggle ? active : undefined}
      aria-label={c.label}
      onClick={onClick}
    >
      <Icon name={c.icon} size={24} />
    </button>
  );
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
export function CallAvatar({ art = 'avatar-assistant-bust', initials, ripples = false, pulse = false, size = 200, label = 'Equal AI assistant' }: { art?: AssetName; initials?: string; ripples?: boolean; pulse?: boolean; size?: number; label?: string }) {
  return (
    <div className={`eq-call-avatar ${pulse ? 'is-pulsing' : ''}`} style={{ width: size, height: size }} role="img" aria-label={label}>
      {ripples && [1.4, 1.8, 2.2].map((k, i) => <span key={k} className={`eq-call-avatar__ring eq-call-avatar__ring--${i + 1}`} style={{ width: size * k, height: size * k }} aria-hidden />)}
      <span className={`eq-call-avatar__disc ${initials ? 'is-initials' : ''}`} aria-hidden>
        {initials ? <Text variant="headline" color="none" style={{ fontSize: 56, lineHeight: '64px' }}>{initials}</Text> : <Asset name={art} size={Math.round(size * 0.96)} alt="" />}
      </span>
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
 * The in-app call screen (vOIP design), 390 × 800: wallpaper → status bar → back + logo → call avatar
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
