import { Locked } from './Monetization';
import { useLayoutEffect, useRef, useState, type ReactNode } from 'react';
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
  /**
   * First bubble of a group: avatar, sender line, and the square top-outer corner (top-left on the
   * left side, top-right on the right; the other corners stay 12). Follow-ups round all four.
   */
  first?: boolean;
  /** Caller category for the incoming avatar. Defaults to `others`. */
  category?: Category;
  /** Custom content — removes inner padding (e.g. voice note / summary). */
  bare?: boolean;
  maxWidth?: number;
  /** Monetization: blur the message and show the crown-lock badge. */
  locked?: boolean;
};

export function ChatBubble({ kind, sender, time, children, first = true, category = 'others', bare, maxWidth = 264, locked }: ChatBubbleProps) {
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
      <div className={`eq-bubble eq-bubble--${kind} ${first ? 'is-lead' : ''} ${bare ? 'is-bare' : ''}`} style={{ maxWidth }}>
        {srSender && <span className="eq-sr">{srSender}: </span>}
        {showsSender && (
          <Text as="div" variant="body2" color="inherit" style={{ color: senderColor }}>
            {sender}
          </Text>
        )}
        {bare ? children : time ? (
          <BubbleBody time={time}>{locked ? <Locked><Text as="div" variant="body1">{children}</Text></Locked> : <Text as="div" variant="body1">{children}</Text>}</BubbleBody>
        ) : locked ? <Locked><Text as="div" variant="body1">{children}</Text></Locked> : <Text as="div" variant="body1">{children}</Text>}
      </div>
    </div>
  );
}

/**
 * Message + time. A one-line message keeps the time on the same row, set a little below the text;
 * a longer message wraps it to its own row, right-aligned (flex-wrap decides, so it follows the text).
 */
function BubbleBody({ time, children }: { time: string; children: ReactNode }) {
  const textRef = useRef<HTMLDivElement>(null);
  const timeRef = useRef<HTMLDivElement>(null);
  const [inline, setInline] = useState(true);
  useLayoutEffect(() => {
    const t = textRef.current, m = timeRef.current;
    if (!t || !m) return;
    const check = () => setInline(m.offsetTop < t.offsetTop + t.offsetHeight - 2);
    check();
    const ro = new ResizeObserver(check);
    ro.observe(t);
    return () => ro.disconnect();
  }, []);
  return (
    <div className={`eq-bubble__body ${inline ? 'is-inline' : ''}`}>
      <div ref={textRef} className="eq-bubble__text">{children}</div>
      <div ref={timeRef} className="eq-bubble__time"><Text variant="micro" color="secondary">{time}</Text></div>
    </div>
  );
}

/** Vertical stack of messages: 8 between messages on the same side, 12 when the side changes. */
export function MessageList({ children, label = 'Messages' }: { children: ReactNode; /** Accessible name of the log. */ label?: string }) {
  // role="log": new messages in a live thread are announced politely.
  return <div className="eq-message-list" role="log" aria-label={label}>{children}</div>;
}
