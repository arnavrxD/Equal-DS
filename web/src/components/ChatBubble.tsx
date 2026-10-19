import { Locked } from './Monetization';
import type { ReactNode } from 'react';
import type { Category } from '../foundations/Asset';
import { Text } from '../foundations/Text';
import { AiAvatar, CategoryAvatar } from './Avatar';
import './ChatBubble.css';

/**
 * outgoing  — the user's side of a call (Equal AI speaking for the user, or "You"). Right, user-bg.
 * incoming  — the caller / contact. Left, white, amber sender line, contact avatar.
 * assistant — Equal AI talking to the user. Left, surface, green byline, AI avatar.
 */
export type BubbleKind = 'outgoing' | 'incoming' | 'assistant';

export type ChatBubbleProps = {
  kind: BubbleKind;
  sender?: string;
  time?: string;
  children: ReactNode;
  /** First bubble of a group gets the tail, sender line and avatar. */
  first?: boolean;
  tail?: boolean;
  /** Caller category for the incoming avatar. Defaults to `others`. */
  category?: Category;
  /** Custom content — removes inner padding (e.g. voice note / summary). */
  bare?: boolean;
  maxWidth?: number;
  /** Monetization: blur the message and show the crown-lock badge. */
  locked?: boolean;
};

export function ChatBubble({ kind, sender, time, children, first = true, tail = false, category = 'others', bare, maxWidth = 264, locked }: ChatBubbleProps) {
  const left = kind !== 'outgoing';
  const senderColor = kind === 'outgoing' ? 'var(--eq-chat-bubble-user-title)' : kind === 'incoming' ? 'var(--eq-chat-bubble-contact-title)' : 'var(--eq-text-action)';
  const avatar = left ? (
    <span className="eq-bubble__avatar">{first ? kind === 'assistant' ? <AiAvatar size={24} /> : <CategoryAvatar category={category} size={28} /> : null}</span>
  ) : null;
  // Follow-ups (and bare bubbles) draw no sender line; screen readers still hear who sent them.
  const showsSender = !bare && first && !!sender;
  const srSender = showsSender ? undefined : sender ?? (kind === 'assistant' ? 'Equal AI' : undefined);
  return (
    <div className={`eq-bubble-row ${left ? 'is-left' : 'is-right'} ${first ? 'is-first' : ''}`}>
      {avatar}
      <div className={`eq-bubble eq-bubble--${kind} ${tail && first ? 'has-tail' : ''} ${bare ? 'is-bare' : ''}`} style={{ maxWidth }}>
        {srSender && <span className="eq-sr">{srSender}: </span>}
        {showsSender && (
          <Text as="div" variant="body2" color="inherit" style={{ color: senderColor }}>
            {sender}
          </Text>
        )}
        {bare ? children : locked ? <Locked><Text as="div" variant="body1">{children}</Text></Locked> : <Text as="div" variant="body1">{children}</Text>}
        {!bare && time && (
          <Text as="div" variant="micro" color="secondary" align="right" style={{ marginTop: 2 }}>
            {time}
          </Text>
        )}
      </div>
    </div>
  );
}

/** Vertical stack of messages with the correct 4 / 12dp group rhythm. */
export function MessageList({ children, label = 'Messages' }: { children: ReactNode; /** Accessible name of the log. */ label?: string }) {
  // role="log": new messages in a live thread are announced politely.
  return <div className="eq-message-list" role="log" aria-label={label}>{children}</div>;
}
