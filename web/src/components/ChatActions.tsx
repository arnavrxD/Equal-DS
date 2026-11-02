// Chat action flows (Flows/Call Back, Mute, Reply On WhatsApp, Save Contact, Error states): the pieces
// the AI layer uses to carry out a task in a chat and to fail gracefully.
import { useEffect, useRef, type ReactNode } from 'react';
import { useModal } from '../foundations/a11y';
import { Asset, type AssetName } from '../foundations/Asset';
import { Icon } from '../foundations/Icon';
import { Text } from '../foundations/Text';
import { AiAvatar } from './Avatar';
import { Button, IconButton } from './Button';
import { Switch } from './Switch';
import { AssistantReplyCard, type ReplyAction } from './Assistant';
import './ChatActions.css';
import './ChatBubble.css';

/** The assistant's turn in a task: orb + reply card (text, time, emoji action rows). */
export function AiTurn({ children, actions, time = '03:21', width = 280 }: { children: ReactNode; actions?: ReplyAction[]; time?: string; width?: number }) {
  return (
    <div className="eq-bubble-row is-left">
      <AiAvatar size={28} />
      <AssistantReplyCard time={time} actions={actions} width={width}>{children}</AssistantReplyCard>
    </div>
  );
}

/** Voice-first input in a task sheet: "🎤 Hold to speak" with a keyboard toggle. */
export function HoldToSpeakBar({ onHold, onKeyboard }: { onHold?: () => void; onKeyboard?: () => void }) {
  return (
    <div className="eq-hold-bar">
      <button type="button" className="eq-reset-btn eq-pressable eq-hold-bar__speak" onClick={onHold}>
        <Icon name="mic-fill" size={20} />
        <Text variant="button" color="none">Hold to speak</Text>
      </button>
      <button type="button" className="eq-reset-btn eq-pressable eq-hold-bar__kbd" aria-label="Type instead" onClick={onKeyboard}>
        <Icon name="keyboard" size={22} color="var(--eq-text-secondary)" />
      </button>
    </div>
  );
}

/**
 * Edit a drafted message in place: the bubble floats above the keyboard, grows with the text (to
 * about 11 lines, then scrolls) and the green check saves it. The chat behind it dims.
 */
export function EditBubble({ value, onChange, onDone, autoFocus }: { value: string; onChange?: (v: string) => void; onDone?: () => void; autoFocus?: boolean }) {
  const ref = useRef<HTMLTextAreaElement>(null);
  useEffect(() => {
    const el = ref.current;
    if (!el) return;
    el.style.height = 'auto';
    el.style.height = `${Math.min(el.scrollHeight, 300)}px`;
  }, [value]);
  return (
    <div className="eq-edit-bubble">
      <textarea ref={ref} className="eq-t-body-large" value={value} rows={1} aria-label="Edit message" autoFocus={autoFocus} onChange={(e) => onChange?.(e.target.value)} />
      <button type="button" className="eq-reset-btn eq-pressable eq-edit-bubble__done" aria-label="Done" onClick={onDone}>
        <Icon name="check" size={24} />
      </button>
    </div>
  );
}

export type NewContactValue = { first: string; last?: string; phone: string; email?: string; sync?: boolean };

/**
 * New contact form, two parity variants the team is choosing between. `grouped`: iOS-revamp style
 * grouped rows with Save in the header. `outlined`: WhatsApp-style outlined fields with a Save button.
 */
export function NewContactForm({ value, variant = 'grouped', onClose, onSave, onSync }: { value: NewContactValue; variant?: 'grouped' | 'outlined'; onClose?: () => void; onSave?: () => void; onSync?: (on: boolean) => void }) {
  const header = (
    <div className="eq-contact-form__bar">
      <IconButton icon="close" label="Close" onClick={onClose} />
      <Text as="h2" variant="title" style={{ fontWeight: 500, flex: 1, textAlign: 'center' }}>New contact</Text>
      <Button size="xs" onClick={onSave}>Save</Button>
    </div>
  );
  if (variant === 'grouped') {
    return (
      <div className="eq-contact-form is-grouped">
        {header}
        <div className="eq-contact-form__group">
          <div className="eq-contact-form__row"><Text as="span" variant="subheading" style={{ fontWeight: 400 }}>{value.first}</Text></div>
          <div className="eq-contact-form__row"><Text as="span" variant="subheading" color="secondary" style={{ fontWeight: 400 }}>{value.last || 'Last name'}</Text></div>
        </div>
        <div className="eq-contact-form__group">
          {([['Phone', 'India'], ['Mobile', value.phone], ['Email', value.email || 'Add email']] as const).map(([k, v], i) => (
            <div key={k} className="eq-contact-form__pair">
              <Text as="span" variant="subheading" style={{ fontWeight: 400 }}>{k}</Text>
              <span>
                <Text as="span" variant="subheading" color={i === 2 && !value.email ? 'secondary' : 'primary'} className="eq-tnum" style={{ fontWeight: 400 }}>{v}</Text>
                {i === 1 && <Text as="div" variant="micro" color="secondary">This phone number is on WhatsApp</Text>}
              </span>
            </div>
          ))}
        </div>
        <div className="eq-contact-form__group eq-contact-form__sync">
          <Text as="span" variant="subheading" style={{ fontWeight: 400 }}>Sync contact to phone</Text>
          <Switch checked={!!value.sync} onChange={onSync} label="Sync contact to phone" />
        </div>
      </div>
    );
  }
  return (
    <div className="eq-contact-form is-outlined">
      {header}
      <div className="eq-contact-form__fields">
        <div className="eq-contact-form__line"><Icon name="person" size={22} color="var(--eq-text-secondary)" /><label className="eq-ofield"><Text variant="inherit" color="none">First name</Text><input defaultValue={value.first} /></label></div>
        <div className="eq-contact-form__line"><span style={{ width: 22 }} /><label className="eq-ofield"><Text variant="inherit" color="none">Last name</Text><input defaultValue={value.last} /></label></div>
        <div className="eq-contact-form__line">
          <Icon name="call" size={22} color="var(--eq-text-secondary)" />
          <label className="eq-ofield" style={{ flex: '0 0 92px' }}><Text variant="inherit" color="none">Country</Text><input defaultValue="IN +91" /></label>
          <label className="eq-ofield"><Text variant="inherit" color="none">Phone</Text><input className="eq-tnum" defaultValue={value.phone.replace('+91 ', '')} /></label>
        </div>
        <Text as="div" variant="micro" color="secondary" style={{ marginLeft: 38 }}>This phone number is on WhatsApp</Text>
        <div className="eq-contact-form__line"><span style={{ width: 22 }} /><label className="eq-ofield"><Text variant="inherit" color="none">Email ID</Text><input defaultValue={value.email} /></label></div>
        <div className="eq-contact-form__line" style={{ alignItems: 'flex-start' }}>
          <Icon name="refresh" size={22} color="var(--eq-text-secondary)" />
          <span style={{ flex: 1 }}>
            <Text as="div" variant="subheading" style={{ fontWeight: 400 }}>Sync contact to phone</Text>
            <Text as="div" variant="caption" color="secondary" style={{ fontWeight: 400 }}>Only contacts with a phone number can be synced</Text>
          </span>
          <Switch checked={value.sync ?? true} onChange={onSync} label="Sync contact to phone" />
        </div>
      </div>
      <div style={{ padding: '0 16px 20px', marginTop: 'auto' }}><Button size="lg" block onClick={onSave}>Save</Button></div>
    </div>
  );
}

/** Centered permission dialog over a dimmed chat (no permission yet): art, title, why, one action, close. */
export function PermissionDialog({ art = 'permission-contacts', title, body, cta, onAction, onClose, secondary, onSecondary, closable = true }: { art?: AssetName; title: string; body: ReactNode; cta: string; onAction?: () => void; onClose?: () => void; /** Second action beside the CTA, e.g. "Not now" (vOIP mic prompt). */ secondary?: string; onSecondary?: () => void; /** Show the close button (the vOIP mic prompt has none). */ closable?: boolean }) {
  const modal = useModal({ open: true, onClose });
  return (
    <div className="eq-dialog-layer">
      <div className="eq-status-sheet-layer__scrim" style={{ background: 'rgba(0,0,0,0.45)' }} onClick={onClose} />
      <div className="eq-permission-dialog" role="dialog" aria-label={title} {...modal}>
        {closable && <IconButton icon="close" label="Close" variant="neutral" size={36} iconSize={18} onClick={onClose} className="eq-permission-dialog__close" />}
        <Asset name={art} size={84} alt="" />
        <Text as="h2" variant="title" align="center" style={{ fontWeight: 500, marginTop: 12 }}>{title}</Text>
        <Text as="p" variant="body1" color="secondary" align="center" style={{ fontWeight: 400, margin: '4px 0 16px' }}>{body}</Text>
        {secondary ? (
          <div style={{ display: 'flex', gap: 12, width: '100%' }}>
            <Button size="md" emphasis="secondary" onClick={onSecondary ?? onClose}>{secondary}</Button>
            <span style={{ flex: 1, display: 'flex' }}><Button size="md" block onClick={onAction}>{cta}</Button></span>
          </div>
        ) : (
          <Button size="lg" block onClick={onAction}>{cta}</Button>
        )}
      </div>
    </div>
  );
}

/** No-internet sheet over a chat, with Refresh. */
export function OfflineSheet({ onRefresh, mode = 'overlay', onClose }: { onRefresh?: () => void; mode?: 'overlay' | 'inline'; /** Escape and the scrim. */ onClose?: () => void }) {
  const modal = useModal({ open: true, onClose, enabled: mode === 'overlay' });
  const panel = (
    <div className="eq-offline-sheet" role="alertdialog" aria-label="No internet connection" {...modal}>
      <Asset name="illustration-no-internet" size={64} alt="" />
      <Text as="h2" variant="title" align="center" style={{ fontWeight: 500, marginTop: 8 }}>No internet connection</Text>
      <Text as="p" variant="body2" color="secondary" align="center" style={{ fontWeight: 400, margin: '2px 0 16px' }}>We could not connect to a stable internet. Check your connection and try again.</Text>
      <Button size="lg" block onClick={onRefresh}>Refresh</Button>
    </div>
  );
  if (mode === 'inline') return panel;
  return (
    <div className="eq-status-sheet-layer">
      <div className="eq-status-sheet-layer__scrim" onClick={onClose} />
      {panel}
    </div>
  );
}

/** Full-screen no-internet state (nothing to show yet), with Try again pinned at the bottom. */
export function OfflineScreen({ onRetry }: { onRetry?: () => void }) {
  return (
    <div className="eq-offline-screen">
      <div className="eq-offline-screen__body">
        <Asset name="illustration-no-internet" size={120} alt="" />
        <Text as="h2" variant="title" align="center" style={{ fontWeight: 500, marginTop: 12 }}>No internet connection</Text>
        <Text as="p" variant="body1" color="secondary" align="center" style={{ fontWeight: 400, margin: '4px 0 0' }}>We could not connect to a stable internet. Check your connection and try again.</Text>
      </div>
      <div style={{ padding: '0 16px 24px' }}><Button size="lg" block onClick={onRetry}>Try again</Button></div>
    </div>
  );
}

/** Coded stand-in for the phone's dialer during Call Back (the OS draws the real one). */
export function DialerStandIn({ number, place = 'Bangalore, Karnataka', carrier = 'airtel', status, onEnd }: { number: string; place?: string; carrier?: string; /** Replaces “Calling via {carrier}”, e.g. a connected call’s timer. */ status?: string; onEnd?: () => void }) {
  return (
    <div className="eq-dialer" role="group" aria-label={`Calling ${number}`}>
      <div className="eq-dialer__top">
        <Text as="div" variant="subheading" align="center" style={{ color: 'rgba(255,255,255,0.8)', fontWeight: 400 }}>{status ?? <>Calling via <Text variant="inherit" color="none" style={{ color: '#8AB4F8' }}>{carrier}</Text></>}</Text>
        <Text variant="inherit" color="none" as="div" className="eq-dialer__number eq-tnum">{number}</Text>
        <Text as="div" variant="subheading" align="center" style={{ color: 'rgba(255,255,255,0.8)', fontWeight: 400 }}>{place}</Text>
      </div>
      <div className="eq-dialer__keys">
        {[['Keypad', 'menu'], ['Mute', 'mic'], ['Phone', 'call'], ['More', 'more-vert']].map(([l, i]) => (
          <span key={l}><span className="eq-dialer__key"><Icon name={i as 'mic'} size={24} color="#FFFFFF" /></span><Text variant="inherit" color="none">{l}</Text></span>
        ))}
      </div>
      <button type="button" className="eq-reset-btn eq-dialer__end" aria-label="End call" onClick={onEnd}><Icon name="call-filled" size={30} color="#2B0000" rotate={135} /></button>
    </div>
  );
}
