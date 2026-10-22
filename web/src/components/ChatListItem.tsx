import './ChatListItem.css';
import { Locked } from './Monetization';
import { useRef, useState } from 'react';
import { Text } from '../foundations/Text';
import { AiAvatar, ContactAvatar, type ContactAvatarProps } from './Avatar';
import { Icon } from '../foundations/Icon';
import { CountBadge } from './Badge';
import { ChipRow, SmallActionChip } from './ActionChip';

export type ChatAction = { label: string; /** Unicode emoji */ emoji?: string; onClick?: () => void };

export type ChatListItemProps = {
  name: string;
  preview: string;
  date: string;
  unread?: number;
  actions?: ChatAction[];
  selected?: boolean;
  avatar?: ContactAvatarProps;
  onClick?: () => void;
  onLongPress?: () => void;
  /** Monetization: free plan past its limit. The preview blurs and the crown-lock replaces the unread count. */
  locked?: boolean;
  /** Equal AI's own chat: the orb avatar and the verified tick. */
  official?: boolean;
  /** Actions touchpoint: the action chips are gold and crown-locked. */
  lockedActions?: boolean;
  onLockedAction?: () => void;
  /** Updated UI rows: missed by the assistant, the assistant is on the call ("Ongoing call" pill), or the summary is still generating. */
  status?: 'missed' | 'on-call' | 'loading';
  /** Muted from the list: a 16 notifications-off glyph after the name. */
  muted?: boolean;
};

/** Chat list row (Component ref #6). States derive from `unread` and `selected`. */
export function ChatListItem({ name, preview, date, unread = 0, actions = [], selected, avatar, onClick, onLongPress, locked, official, lockedActions, onLockedAction, status, muted }: ChatListItemProps) {
  const isUnread = unread > 0;
  const chipState = selected ? 'outlined' : isUnread ? 'unread' : 'read';
  const timer = useRef<ReturnType<typeof setTimeout>>(undefined);
  const [pressed, setPressed] = useState(false);
  return (
    <div
      role="button"
      tabIndex={0}
      aria-pressed={selected === undefined ? undefined : selected}
      className="eq-chat-row"
      onClick={onClick}
      onKeyDown={(e) => {
        // Enter or Space on the row itself (not on a chip inside it) opens the chat.
        if (e.target !== e.currentTarget || (e.key !== 'Enter' && e.key !== ' ')) return;
        e.preventDefault();
        onClick?.();
      }}
      onContextMenu={(e) => {
        if (onLongPress) {
          e.preventDefault();
          onLongPress();
        }
      }}
      onPointerDown={() => {
        setPressed(true);
        if (onLongPress) timer.current = setTimeout(onLongPress, 500);
      }}
      onPointerUp={() => { setPressed(false); clearTimeout(timer.current); }}
      onPointerLeave={() => { setPressed(false); clearTimeout(timer.current); }}
      onPointerCancel={() => { setPressed(false); clearTimeout(timer.current); }}
      style={{
        display: 'flex',
        gap: 16,
        padding: '12px 0 12px 16px',
        background: selected ? 'var(--eq-action-card-highlight)' : 'var(--eq-bg-surface)',
        // Pressed: the state/chat-pressed wash, painted over the fill and under the content.
        boxShadow: pressed && !selected ? 'inset 0 0 0 100vmax var(--eq-state-chat-pressed)' : undefined,
        cursor: 'pointer',
        transition: 'background-color var(--eq-duration-base)',
      }}
    >
      {official ? <AiAvatar size={48} /> : <ContactAvatar size={48} selected={selected} {...avatar} />}
      <div style={{ flex: 1, minWidth: 0, display: 'flex', flexDirection: 'column', gap: 0 }}>
        <div style={{ display: 'flex', alignItems: 'baseline', gap: 8, paddingRight: 16 }}>
          <span style={{ flex: 1, minWidth: 0, display: 'flex', alignItems: 'center', gap: 4 }}>
            <Text variant="subheading" truncate>{name}</Text>
            {official && <Icon name="verified" size={16} color="var(--eq-text-action)" label="Verified" />}
            {muted && <Icon name="silent-mode" size={16} color="var(--eq-text-secondary)" label="Muted" />}
          </span>
          {status === 'on-call'
            ? <span className="eq-chat-row__oncall"><Text variant="buttonSmall" color="none">Ongoing call</Text></span>
            : <Text variant="micro" color={isUnread || selected ? 'action' : 'secondary'} style={{ flexShrink: 0 }}>{date}</Text>}
        </div>
        <div style={{ display: 'flex', alignItems: 'center', gap: 8, paddingRight: 16, minHeight: 22 }}>
          {locked ? (
            <Locked inline><Text variant="body1" color="secondary">{preview}</Text></Locked>
          ) : (
            <>
              {status === 'missed' && <Icon name="call-missed" size={14} color="var(--eq-missed-call-icon)" />}
              <Text variant="body1" color={status === 'on-call' ? 'action' : 'secondary'} truncate style={{ flex: 1, fontWeight: status === 'on-call' ? 500 : undefined }}>
                {status === 'missed' ? 'Missed by assistant' : status === 'on-call' ? 'Assistant is on call' : status === 'loading' ? 'Loading Summary...' : preview}
              </Text>
              {status !== 'on-call' && <CountBadge count={unread} />}
            </>
          )}
        </div>
        {actions.length > 0 && status !== 'on-call' && status !== 'loading' && (
          <div style={{ marginTop: 8 }}>
            <ChipRow>
              {actions.map((a) => (
                <SmallActionChip
                  key={a.label}
                  label={a.label}
                  emoji={a.emoji}
                  state={chipState}
                  locked={lockedActions}
                  onClick={(e) => {
                    e.stopPropagation();
                    if (lockedActions) onLockedAction?.();
                    else a.onClick?.();
                  }}
                />
              ))}
              <span style={{ width: 4, flexShrink: 0 }} />
            </ChipRow>
          </div>
        )}
      </div>
    </div>
  );
}
