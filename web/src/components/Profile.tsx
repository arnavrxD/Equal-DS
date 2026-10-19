import type { ReactNode } from 'react';
import { Icon } from '../foundations/Icon';
import type { IconName } from '../foundations/icons';
import { Text } from '../foundations/Text';
import { ContactAvatar, ProfileAvatar, type ContactAvatarProps, type PlanTier, type ProfileAvatarVariant } from './Avatar';
import { PlanBadge } from './Badge';
import './Profile.css';

/** Compact profile header — avatar + headline name + plan marker + phone. The You tab uses <ProfileHero>. */
export function ProfileHeader({ name, phone, plan = 'Unlimited', avatar = 'male', tier = plan ? 'paid' : 'free' }: { name: string; phone: string; plan?: string | null; avatar?: ProfileAvatarVariant | false; tier?: PlanTier }) {
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: 16, padding: '16px' }}>
      {avatar && <ProfileAvatar variant={avatar} size={56} tier={tier} />}
      <div style={{ minWidth: 0 }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
          <Text as="h1" variant="headline" truncate>{name}</Text>
          {plan && <PlanBadge label={plan} />}
        </div>
        <Text as="div" variant="body1" color="secondary" className="eq-tnum">{phone}</Text>
      </div>
    </div>
  );
}

export type QuickAction = { icon: IconName; label: string; onClick?: () => void };

/** Contact info header — 96dp category / initials avatar, headline name, phone, row of action tiles. */
/**
 * Contact Info header (Contact Info design, 1:1): category avatar 96 → 10 → name 22/28 Bold → phone
 * `bodyLarge` `text/secondary` → 24 → action tiles (70 × 72, radius 8, gap 12; dark: 1dp
 * `stroke/subtle` border), icon 24 `text/action` + label `body1`. Side padding 22.
 */
export function ContactInfoHeader({ name, phone, avatar, actions = [] }: { name: string; phone: string; avatar?: ContactAvatarProps; actions?: QuickAction[] }) {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', width: '100%', boxSizing: 'border-box', padding: '8px 22px 0', textAlign: 'center' }}>
      <ContactAvatar size={96} {...avatar} />
      <Text as="h1" variant="headline" style={{ marginTop: 10, fontSize: 22, lineHeight: '28px', fontWeight: 700 }}>{name}</Text>
      <Text as="div" variant="bodyLarge" color="secondary" className="eq-tnum">{phone}</Text>
      {actions.length > 0 && (
        <div style={{ display: 'grid', gridTemplateColumns: `repeat(${actions.length}, 1fr)`, gap: 12, width: '100%', marginTop: 24 }}>
          {actions.map((a) => (
            <button key={a.label} type="button" className="eq-reset-btn eq-pressable eq-action-tile" onClick={a.onClick}>
              <Icon name={a.icon} size={24} color="var(--eq-text-action)" />
              <Text variant="body1">{a.label}</Text>
            </button>
          ))}
        </div>
      )}
    </div>
  );
}

export type CallDirection = 'incoming' | 'outgoing' | 'missed';
export type CallHistoryRowProps = { direction: CallDirection; meta: string; time: string; trailing?: ReactNode };

const callLabel = { incoming: 'Incoming Call', outgoing: 'Outgoing Call', missed: 'Missed Call' } as const;
const callIcon = { incoming: 'phone-incoming', outgoing: 'phone-outgoing', missed: 'phone-missed' } as const;

/**
 * Call history row (Contact Info design): 63 high, padding 0 21 0 22. Direction icon 24 (missed in
 * `text/danger`) → 10 → title `bodyLarge` + meta `micro` regular `text/secondary`; time `body1`
 * `text/secondary` on the right.
 */
export function CallHistoryRow({ direction, meta, time, trailing }: CallHistoryRowProps) {
  const missed = direction === 'missed';
  return (
    <div role="listitem" style={{ display: 'flex', alignItems: 'center', gap: 10, padding: '0 21px 0 22px', minHeight: 63, background: 'var(--eq-bg-surface)' }}>
      <Icon name={callIcon[direction]} size={24} color={missed ? 'var(--eq-text-danger)' : 'var(--eq-text-primary)'} />
      <div style={{ flex: 1, minWidth: 0 }}>
        <Text as="div" variant="bodyLarge" style={{ lineHeight: '20px' }}>{callLabel[direction]}</Text>
        <Text as="div" variant="micro" color="secondary" style={{ fontWeight: 400, fontSize: 11, lineHeight: '14px' }}>{meta}</Text>
      </div>
      <Text variant="body1" color="secondary" className="eq-tnum">{time}</Text>
      {trailing}
    </div>
  );
}

/** The call-history card: radius 16, `bg/surface`, dividers between rows; dark adds a 1dp `stroke/subtle` border. */
export function CallHistoryList({ children, inset = 22 }: { children: ReactNode; inset?: number }) {
  return <div role="list" aria-label="Call history" className="eq-row-group eq-call-history" style={{ margin: `0 ${inset}px`, alignSelf: 'stretch', borderRadius: 16, overflow: 'hidden', background: 'var(--eq-bg-surface)', display: 'flex', flexDirection: 'column' }}>{children}</div>;
}
