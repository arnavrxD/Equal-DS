// Call Handling (Known Calls (new users) design, Known-call notifications design):
// which calls the assistant answers, who always rings you, the permissions it needs, and the
// Android notification while it answers a known caller.
import type { ReactNode } from 'react';
import { Announce, useModal } from '../foundations/a11y';
import { Asset, type AssetName, type Category } from '../foundations/Asset';
import { Emoji, Icon } from '../foundations/Icon';
import type { IconName } from '../foundations/icons';
import { Text } from '../foundations/Text';
import { EqualMark } from '../foundations/EqualMark';
import { CategoryAvatar } from './Avatar';
import { Button, IconButton, TextButton } from './Button';
import { Checkbox } from './Checkbox';
import { Lottie } from './Lottie';
import './CallHandling.css';

// ---------------------------------------------------------------- question

/** Hero art (or its Lottie) + the question + the one-line promise under it. */
export function CallQuestion({ art, lottie, title, subtitle }: { art: AssetName; lottie?: string; title: string; subtitle?: string }) {
  return (
    <div className="eq-ch-question">
      {lottie ? <div style={{ margin: '-44px 0 -48px' }}><Lottie name={lottie} width={300} height={300} label="" /></div> : <Asset name={art} width={280} height={208} alt="" />}
      <Text as="h1" variant="headline" align="center" style={{ fontWeight: 700, marginTop: 8 }}>{title}</Text>
      {subtitle && <Text as="p" variant="title" color="secondary" align="center" style={{ fontWeight: 400, margin: 0 }}>{subtitle}</Text>}
    </div>
  );
}

export type ChoiceCardProps = {
  icon: IconName;
  title: string;
  description: string;
  selected?: boolean;
  /** A small "⭐ Suggested" tag beside the title. */
  suggested?: boolean;
  /** Dimmed while it can't be chosen (e.g. no contacts permission). */
  disabled?: boolean;
  onSelect?: () => void;
  /** Roving tabindex inside a radiogroup: 0 for the selected card, -1 for the rest (see `rovingTabIndex`). */
  tabIndex?: number;
};

/** A radio card: icon, title, description, radio. Selected = green outline fading to lime, tinted fill. */
export function ChoiceCard({ icon, title, description, selected, suggested, disabled, onSelect, tabIndex }: ChoiceCardProps) {
  return (
    <button type="button" role="radio" aria-checked={!!selected} disabled={disabled} tabIndex={tabIndex} className={`eq-reset-btn eq-pressable eq-ch-choice ${selected ? 'is-selected' : ''}`} onClick={onSelect}>
      <Icon name={icon} size={28} color="var(--eq-text-primary)" />
      <span style={{ flex: 1, minWidth: 0 }}>
        <span style={{ display: 'flex', alignItems: 'center', gap: 8, flexWrap: 'wrap' }}>
          <Text as="span" variant="subheading" style={{ fontWeight: 500 }}>{title}</Text>
          {suggested && <SuggestedTag />}
        </span>
        <Text as="span" variant="body1" color="secondary" style={{ display: 'block', fontWeight: 400 }}>{description}</Text>
      </span>
      <span className={`eq-ch-radio ${selected ? 'is-on' : ''}`} aria-hidden />
    </button>
  );
}

/** "⭐ Suggested" tag. */
export function SuggestedTag({ label = 'Suggested' }: { label?: string }) {
  return (
    <span className="eq-ch-suggested">
      <Emoji char="⭐" inline />
      <Text as="span" variant="caption" color="secondary" style={{ fontWeight: 400 }}>{label}</Text>
    </span>
  );
}

/** Blue info note with a link, e.g. "Contact access needed · Give contacts permission ›". */
export function InfoNote({ title, body, link, onLink, onDismiss }: { title: string; body?: string; link?: string; onLink?: () => void; /** Dismissible notice (You: "Settings live here now"): left-aligned with a close. */ onDismiss?: () => void }) {
  if (onDismiss) {
    return (
      <div className="eq-ch-info is-dismissible" role="note">
        <span style={{ display: 'flex', alignItems: 'flex-start', gap: 8 }}>
          <Icon name="info-fill" size={16} color="var(--eq-info-note-title)" style={{ marginTop: 4 }} />
          <span style={{ flex: 1, minWidth: 0 }}>
            <Text as="div" variant="subheading" style={{ fontWeight: 600 }}>{title}</Text>
            {body && <Text as="div" variant="body1" color="secondary">{body}</Text>}
          </span>
          <IconButton icon="close" label="Dismiss" size={24} iconSize={16} onClick={onDismiss} />
        </span>
      </div>
    );
  }
  return (
    <div className="eq-ch-info" role="note">
      <Text as="div" variant="subheading" align="center" style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 6 }}>
        <Icon name="info" size={18} /> {title}
      </Text>
      {body && <Text as="div" variant="body1" align="center" style={{ fontWeight: 400 }}>{body}</Text>}
      {link && (
        <div style={{ display: 'flex', justifyContent: 'center', marginTop: 4 }}>
          <TextButton onClick={onLink} trailingIcon="chevron-right" className="eq-ch-link">{link}</TextButton>
        </div>
      )}
    </div>
  );
}

// ---------------------------------------------------------------- contact picking

/** A suggested contact: initials on a white disc with a + (add) or × (remove) badge, name under it. */
export function ContactPick({ initials, name, mark = 'add', onClick }: { initials: string; name: string; mark?: 'add' | 'remove'; onClick?: () => void }) {
  return (
    <button type="button" className="eq-reset-btn eq-pressable eq-ch-pick" onClick={onClick} aria-label={`${mark === 'add' ? 'Add' : 'Remove'} ${name}`}>
      <span className="eq-ch-initials" style={{ width: 56, height: 56 }}>
        <Text as="span" variant="title" style={{ fontWeight: 500 }}>{initials}</Text>
        <span className="eq-ch-pick__mark"><Icon name={mark === 'add' ? 'add' : 'close'} size={14} /></span>
      </span>
      <Text as="span" variant="body2" align="center" style={{ fontWeight: 400 }}>{name}</Text>
    </button>
  );
}

/** A contact row with a checkbox: initials disc, name, number. */
export function ContactCheckRow({ initials, name, phone, checked, onToggle }: { initials: string; name: string; phone: string; checked: boolean; onToggle?: (v: boolean) => void }) {
  return (
    // The whole row toggles on tap; the checkbox is the keyboard / screen-reader control.
    <div className="eq-ch-row" onClick={(e) => { if (!(e.target as Element).closest('[role="checkbox"]')) onToggle?.(!checked); }} style={{ cursor: onToggle ? 'pointer' : undefined }}>
      <span className="eq-ch-initials" style={{ width: 52, height: 52 }}>
        <Text as="span" variant="title" style={{ fontWeight: 500 }}>{initials}</Text>
      </span>
      <span style={{ flex: 1, minWidth: 0 }}>
        <Text as="div" variant="title" style={{ fontWeight: 500 }} truncate>{name}</Text>
        <Text as="div" variant="body1" color="secondary" className="eq-tnum">{phone}</Text>
      </span>
      <Checkbox checked={checked} onChange={onToggle} ariaLabel={`Select ${name}`} size={26} />
    </div>
  );
}

/** A–Z fast-scroll index down the right edge of a contact list. */
export function AlphabetIndex({ onPick }: { onPick?: (letter: string) => void }) {
  return (
    <nav className="eq-ch-index" aria-label="Jump to letter">
      {'ABCDEFGHIJKLMNOPQRSTUVWXYZ'.split('').map((l) => (
        <button key={l} type="button" className="eq-reset-btn eq-ch-index__letter" aria-label={`Jump to ${l}`} onClick={() => onPick?.(l)}><Text variant="inherit" color="none">{l}</Text></button>
      ))}
    </nav>
  );
}

// ---------------------------------------------------------------- setup moments

/** Dark takeover that invites a new user into setup: art, title, subtitle, Start. */
export function SetupPrompt({ art = 'call-setup', title = 'Set up your calls', subtitle = 'Two questions. One minute.', cta = 'Start', onStart, onClose }: { art?: AssetName; title?: string; subtitle?: string; cta?: string; onStart?: () => void; /** Escape (e.g. "not now"). */ onClose?: () => void }) {
  const modal = useModal({ open: true, onClose });
  return (
    <div className="eq-ch-prompt" role="dialog" aria-label={title} {...modal}>
      <div className="eq-ch-prompt__body">
        <Asset name={art} width={240} height={169} alt="" />
        <Text as="h2" variant="headline" align="center" style={{ color: '#FFFFFF', fontWeight: 500, marginTop: 24 }}>{title}</Text>
        <Text as="p" variant="title" align="center" style={{ color: 'rgba(255,255,255,0.85)', fontWeight: 400, margin: 0 }}>{subtitle}</Text>
      </div>
      <Button size="lg" block onClick={onStart} className="eq-ch-prompt__cta">{cta}</Button>
    </div>
  );
}

/** Full-screen result after setup ("Perfect!", "All Sorted!") on the mint-to-lime wash. */
export function SetupDone({ art = 'call-handling-done', title, body, cta = 'Done', onDone, header }: { art?: AssetName; title: string; body: ReactNode; cta?: string; onDone?: () => void; /** Status bar / back, drawn on the wash. */ header?: ReactNode }) {
  return (
    <div className="eq-ch-done">
      {header}
      <div className="eq-ch-done__body">
        <Asset name={art} width={300} height={262} alt="" />
        <Text as="h1" variant="headline" align="center" style={{ fontWeight: 700, fontSize: 28, lineHeight: '34px' }}>{title}</Text>
        <Text as="p" variant="title" align="center" style={{ fontWeight: 400, margin: 0 }}>{body}</Text>
      </div>
      <div style={{ padding: '0 16px 24px' }}><Button size="lg" block onClick={onDone}>{cta}</Button></div>
    </div>
  );
}

/** Ask-once permission sheet: art, title, why, one action; close floats top-right. */
export function PermissionSheet({ art, title, body, cta, onAllow, onClose, secondary, onSecondary }: { art: AssetName; title: string; body: ReactNode; cta: string; onAllow?: () => void; onClose?: () => void; secondary?: string; onSecondary?: () => void }) {
  const modal = useModal({ open: true, onClose });
  return (
    <div className="eq-ch-sheet" role="dialog" aria-label={title} {...modal}>
      {onClose && <IconButton icon="close" label="Close" variant="neutral" size={44} onClick={onClose} className="eq-ch-sheet__close" />}
      <Asset name={art} size={110} alt="" />
      <Text as="h2" variant="headline" align="center" style={{ fontWeight: 500, marginTop: 12 }}>{title}</Text>
      <Text as="p" variant="title" color="secondary" align="center" style={{ fontWeight: 400, margin: '4px 0 0' }}>{body}</Text>
      <div style={{ width: '100%', marginTop: 24 }}><Button size="lg" block onClick={onAllow}>{cta}</Button></div>
      {secondary && <div style={{ marginTop: 14 }}><TextButton onClick={onSecondary} className="eq-ch-link">{secondary}</TextButton></div>}
    </div>
  );
}

// ---------------------------------------------------------------- notifications

export type CallNotificationState = 'answering' | 'summary' | 'expanded' | 'connecting';

export type CallNotificationProps = {
  name: string;
  category?: Category;
  /** Seconds on the call. */
  elapsed?: string;
  /** answering: "Your Equal AI is answering your call."; summary: what the caller wants (one line); expanded: two lines + call button + replies; connecting: "This needs your attention…". */
  state: CallNotificationState;
  summary?: string;
  replies?: string[];
  onReply?: (r: string) => void;
  onAnswer?: () => void;
};

/**
 * Android ongoing-call notification while the assistant answers a known caller: app line with a
 * live timer, the caller and what they want, then quick replies (Takeover first) or the hand-off line.
 */
export function CallNotification({ name, category = 'home-services', elapsed = '00:06', state, summary = 'She is asking when will you be coming to the wedding, she also asked you to bring her sweets.', replies = ['Takeover', 'I’ll bring them all', 'I’ll call back to get more'], onReply, onAnswer }: CallNotificationProps) {
  const big = state === 'expanded' || state === 'connecting';
  return (
    // Not a live region: the timer ticks every second. Screen readers hear the call once, on mount.
    <div className="eq-ch-notif" role="group" aria-label={`Equal AI is answering ${name}`}>
      <Announce onMount message={state === 'connecting' ? `${name}: this needs your attention, connecting the call to you` : `Equal AI is answering ${name}`} />
      <div className="eq-ch-notif__app">
        <Text as="span" variant="caption" color="secondary" style={{ fontWeight: 400 }}>Equal AI</Text>
        <Text variant="inherit" color="none" className="eq-ch-notif__sep" aria-hidden>•</Text>
        <Text as="span" variant="caption" color="action" style={{ fontWeight: 400 }}>Ongoing</Text>
        <Text variant="inherit" color="none" className="eq-ch-notif__timer eq-tnum"><Icon name="graphic-eq" size={12} color="var(--eq-text-action)" /> {elapsed}</Text>
        <Icon name="chevron-down" size={20} color="var(--eq-text-secondary)" style={{ marginLeft: 'auto' }} />
      </div>
      <div className="eq-ch-notif__main">
        <span style={{ position: 'relative', flexShrink: 0 }}>
          <CategoryAvatar category={category} size={40} />
          {big && <span className="eq-ch-notif__badge" aria-hidden><EqualMark width={10} color="var(--eq-text-on-action)" /></span>}
        </span>
        <span style={{ flex: 1, minWidth: 0 }}>
          <Text as="div" variant="subheading" style={{ fontWeight: 500 }}>{name}</Text>
          <Text as="div" variant="body1" className={big ? 'eq-ch-clamp2' : 'eq-ch-clamp1'} style={{ fontWeight: 400 }}>{state === 'answering' ? 'Your Equal AI is answering your call.' : summary}</Text>
        </span>
        {state === 'expanded' ? (
          <button type="button" className="eq-reset-btn eq-pressable eq-touch eq-ch-notif__call" aria-label="Answer the call" onClick={onAnswer}><Icon name="call-filled" size={20} color="var(--eq-text-action)" /></button>
        ) : state !== 'connecting' ? (
          <span className="eq-ch-notif__app-icon" aria-hidden><EqualMark width={18} color="var(--eq-text-on-action)" /><Text variant="inherit" color="none">equal</Text></span>
        ) : null}
      </div>
      {state === 'expanded' && (
        <div className="eq-ch-notif__replies">
          {replies.map((r) => <button key={r} type="button" className="eq-reset-btn eq-pressable eq-touch eq-ch-notif__reply" onClick={() => onReply?.(r)}><Text variant="inherit" color="none">{r}</Text></button>)}
        </div>
      )}
      {state === 'connecting' && <Text as="div" variant="body1" align="center" className="eq-ch-notif__handoff">This needs your attention, connecting this call to you</Text>}
    </div>
  );
}

/** A coded stand-in for the phone's home / lock screen behind a notification (no wallpaper art). */
export function DeviceScreen({ time = '2:13', children }: { time?: string; children?: ReactNode }) {
  return (
    <div className="eq-ch-device">
      <div className="eq-ch-device__bar"><Text variant="inherit" color="none" className="eq-tnum">{time}</Text><Text variant="inherit" color="none" aria-hidden>▲ ● ▮</Text></div>
      <div style={{ position: "relative", zIndex: 1, padding: "4px 8px" }}>{children}</div>
      <Text variant="inherit" color="none" as="div" className="eq-ch-device__clock eq-tnum" aria-hidden>{time}</Text>
    </div>
  );
}

/** Black strip under a chat header while the assistant is on the call: "Ongoing call - 02:34". */
export function OngoingCallBar({ elapsed }: { elapsed: string }) {
  return (
    // Not a live region (the timer ticks every second); the call is announced once.
    <div className="eq-ch-ongoing">
      <Announce onMount message="Ongoing call" />
      <Icon name="graphic-eq" size={18} />
      <Text as="span" variant="subheading" className="eq-tnum" style={{ color: 'inherit', fontWeight: 500 }}>Ongoing call - {elapsed}</Text>
    </div>
  );
}
