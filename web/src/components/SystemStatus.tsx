import type { CSSProperties, ReactNode } from 'react';
import { Asset } from '../foundations/Asset';
import { Icon } from '../foundations/Icon';
import { Text } from '../foundations/Text';
import { EqualOrb } from './Avatar';
import { IconButton } from './Button';

// Temporarily Unavailable design: the system-status card on Chats, the heads-up notification when
// the assistant comes back, and the Meet Your Assistant card that follows it.

const pill: CSSProperties = {
  display: 'inline-flex',
  alignItems: 'center',
  gap: 6,
  height: 32,
  padding: '0 14px 0 12px',
  borderRadius: 999,
  border: 0,
  font: 'inherit',
  cursor: 'pointer',
  whiteSpace: 'nowrap',
};

export type SystemStatusCardProps = {
  /** `unavailable` shows the Notify pill; `notified` confirms it (Temporarily Unavailable design, states 1 and 2). */
  state?: 'unavailable' | 'notified';
  onNotify?: () => void;
  title?: ReactNode;
  body?: ReactNode;
  label?: string;
};

/** "System status" card shown in place of the assistant toggle while Equal AI is down. */
export function SystemStatusCard({
  state = 'unavailable',
  onNotify,
  title = 'Temporarily Unavailable',
  body = 'We’re already working on a fix. Thanks for your patience.',
  label = 'System status',
}: SystemStatusCardProps) {
  return (
    <div role="status" style={{ position: 'relative', borderRadius: 20, padding: 16, background: 'var(--eq-gradient-status-card)', minHeight: 140, overflow: 'hidden' }}>
      <span className="eq-t-overline" style={{ display: 'inline-flex', alignItems: 'center', height: 20, padding: '0 8px', borderRadius: 999, background: 'var(--eq-status-card-pill-bg)', color: 'var(--eq-text-secondary)', fontSize: 10, lineHeight: '20px' }}>
        {label}
      </span>
      <Text as="h3" variant="subheading" style={{ position: 'relative', zIndex: 1, marginTop: 8, fontSize: 18, lineHeight: '24px', fontWeight: 700, whiteSpace: 'nowrap' }}>{title}</Text>
      <Text as="p" variant="caption" color="secondary" style={{ marginTop: 2, fontWeight: 400, maxWidth: 190 }}>{body}</Text>
      <div style={{ marginTop: 14, minHeight: 32, display: 'flex', alignItems: 'center' }}>
        {state === 'unavailable' ? (
          <button type="button" onClick={onNotify} className="eq-t-button-small" style={{ ...pill, background: 'var(--eq-status-card-button)', color: 'var(--eq-status-card-button-text)', fontWeight: 700 }}>
            <Icon name="notify-me" size={16} />
            Notify me when ready
          </button>
        ) : (
          <Text as="span" variant="buttonSmall" color="action" style={{ display: 'inline-flex', alignItems: 'center', gap: 6, fontWeight: 700 }}>
            <Icon name="check" size={16} />
            You’ll be notified soon
          </Text>
        )}
      </div>
      <Asset name="mascot-sleeping" size={104} alt="" style={{ position: 'absolute', right: 0, top: '50%', transform: 'translateY(-46%)' }} />
    </div>
  );
}

export type SystemNotificationProps = {
  app?: string;
  time?: string;
  title: ReactNode;
  body: ReactNode;
  width?: number | string;
};

/** Android heads-up notification, drawn over the status bar (Temporarily Unavailable design, state 3). */
export function SystemNotification({ app = 'Equal AI', time = 'Now', title, body, width = '100%' }: SystemNotificationProps) {
  return (
    <div role="alert" style={{ width, boxSizing: 'border-box', borderRadius: 20, padding: '12px 16px 16px', background: 'var(--eq-notification-bg)', boxShadow: '0 8px 28px rgba(0,0,0,0.14), 0 1px 3px rgba(0,0,0,0.08)' }}>
      <Text as="div" variant="micro" style={{ display: 'flex', alignItems: 'center', gap: 6, fontSize: 10 }}>
        {app}
        <span aria-hidden style={{ width: 3, height: 3, borderRadius: '50%', background: 'var(--eq-text-placeholder)' }} />
        {time}
      </Text>
      <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginTop: 10 }}>
        <EqualOrb size={32} />
        <div style={{ minWidth: 0 }}>
          <Text as="div" variant="caption" style={{ fontWeight: 700 }}>{title}</Text>
          <Text as="div" variant="caption" color="secondary" style={{ fontWeight: 400 }}>{body}</Text>
        </div>
      </div>
    </div>
  );
}

export type MeetAssistantCardProps = {
  /** `compact` sits on Chats after the assistant is back; `banner` is the standalone promo; `hero` fills an empty home for a new user (Known Calls Flow). */
  layout?: 'compact' | 'banner' | 'hero';
  title?: ReactNode;
  body?: ReactNode;
  ctaLabel?: string;
  onCall?: () => void;
  onDismiss?: () => void;
  width?: number | string;
};

/** The lime "📞 Call me" pill on Meet Your Assistant (and the system-status back-on card). */
export function CallMeButton({ label, onClick, block }: { label: string; onClick?: () => void; block?: boolean }) {
  return (
    <button type="button" onClick={onClick} className="eq-t-button" style={{ ...pill, height: block ? 52 : 40, width: block ? '100%' : undefined, justifyContent: 'center', padding: '0 22px 0 18px', gap: 8, background: 'var(--eq-gradient-call-me)', color: 'var(--eq-text-on-action)', boxShadow: '0 6px 16px color-mix(in srgb, var(--eq-button-primary) 28%, transparent)' }}>
      <Icon name="call-sparkle" size={18} />
      {label}
    </button>
  );
}

/** Promo card that introduces the voice assistant (Temporarily Unavailable design + Meet Your Assistant banner). */
export function MeetAssistantCard({
  layout = 'compact',
  title = 'Meet Your Assistant',
  body = 'Try talking to your assistant about what all it can do',
  ctaLabel = 'Call me',
  onCall,
  onDismiss,
  width = '100%',
}: MeetAssistantCardProps) {
  const close = onDismiss && (
    <IconButton icon="close" label="Dismiss" onClick={onDismiss} size={32} iconSize={20} style={{ position: 'absolute', top: 6, right: 6, color: 'var(--eq-text-placeholder)' }} />
  );
  if (layout === 'hero') {
    return (
      <div style={{ position: 'relative', width, maxWidth: '100%', boxSizing: 'border-box', borderRadius: 24, padding: '40px 24px 28px', background: 'var(--eq-gradient-meet-assistant)', display: 'flex', flexDirection: 'column', alignItems: 'center', textAlign: 'center' }}>
        <Asset name="avatar-assistant" width={170} height={190} alt="Your assistant" />
        <Text as="h3" variant="headline" color="action" align="center" style={{ fontWeight: 700, marginTop: 28 }}>{title}</Text>
        <Text as="p" variant="subheading" align="center" style={{ fontWeight: 400, marginTop: 6 }}>{body}</Text>
        <div style={{ marginTop: 32, width: '100%' }}><CallMeButton label={ctaLabel} onClick={onCall} block /></div>
        {close}
      </div>
    );
  }
  if (layout === 'banner') {
    return (
      <div style={{ position: 'relative', width, maxWidth: '100%', boxSizing: 'border-box', borderRadius: 20, padding: '20px 16px 20px 18px', background: 'var(--eq-gradient-meet-assistant)', display: 'flex', alignItems: 'center', gap: 12 }}>
        <div style={{ flex: 1, minWidth: 0 }}>
          <Text as="h3" variant="title" color="action" style={{ fontWeight: 700 }}>{title}</Text>
          <Text as="p" variant="body1" color="secondary" style={{ marginTop: 2 }}>{body}</Text>
          <div style={{ marginTop: 16 }}><CallMeButton label={ctaLabel} onClick={onCall} /></div>
        </div>
        <Asset name="avatar-assistant" width={100} height={112} alt="Your assistant" style={{ alignSelf: 'flex-end', marginTop: 24, marginBottom: -6 }} />
        {close}
      </div>
    );
  }
  return (
    <div style={{ position: 'relative', width, maxWidth: '100%', boxSizing: 'border-box', borderRadius: 16, padding: '14px 16px', background: 'var(--eq-gradient-meet-assistant)', display: 'flex', alignItems: 'center', gap: 18 }}>
      <Asset name="avatar-assistant" width={84} height={94} alt="Your assistant" />
      <div style={{ minWidth: 0 }}>
        <Text as="h3" variant="subheading">{title}</Text>
        <div style={{ marginTop: 10 }}><CallMeButton label={ctaLabel} onClick={onCall} /></div>
      </div>
      {close}
    </div>
  );
}
