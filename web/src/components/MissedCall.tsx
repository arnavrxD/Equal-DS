// Missed-call card: the thread card for a call the assistant couldn't pick up. Three cases, one
// component: we know why they call (reason + caller art), we know only the name, or we know nothing.
import type { ReactNode } from 'react';
import { Asset, type Category } from '../foundations/Asset';
import { Icon } from '../foundations/Icon';
import { Text } from '../foundations/Text';
import './MissedCall.css';

export type CallFeedback = 'up' | 'down' | null;

export type MissedCallCardProps = {
  /** Caller name. Leave it out when we couldn't tell who called. */
  caller?: string;
  /** Why this caller usually calls (bold the key words). Shown under USUALLY CALLS FOR. */
  reason?: ReactNode;
  /** Caller art shown with the missed badge. Only when there is a reason; otherwise the red disc. */
  category?: Category;
  time?: string;
  onCallBack?: () => void;
  callBackLabel?: string;
  /** Thumbs row under the card (was this reason right?). Shown when `onFeedback` is set. */
  feedback?: CallFeedback;
  onFeedback?: (value: CallFeedback) => void;
  width?: number | string;
};

/** Thread card for a call the assistant missed: who, why they usually call, Call back, and thumbs. */
export function MissedCallCard({ caller, reason, category, time, onCallBack, callBackLabel = 'Call back', feedback = null, onFeedback, width = 320 }: MissedCallCardProps) {
  const title = caller ? `${caller} Called` : 'Missed Call';
  const body = 'Missed by Assistant';
  const art = reason && category;
  return (
    <div className="eq-missed" style={{ width }}>
      <div className="eq-missed__card">
        <div className="eq-missed__head">
          {art ? (
            <span className="eq-missed__art">
              <Asset name={`category-${category}`} size={48} alt="" />
              <span className="eq-missed__badge"><Icon name="phone-missed-fill" size={12} color="var(--eq-missed-call-icon)" /></span>
            </span>
          ) : (
            <span className="eq-missed__disc"><Icon name="phone-missed-fill" size={24} color="var(--eq-missed-call-icon)" /></span>
          )}
          <span className="eq-missed__text">
            <Text as="div" variant="subheading" truncate>{title}</Text>
            <Text as="div" variant="body2" color="secondary" style={{ fontWeight: 400 }}>{body}</Text>
            {!reason && time && <Text as="div" variant="micro" color="secondary" align="right" className="eq-tnum" style={{ marginTop: 2 }}>{time}</Text>}
          </span>
        </div>
        {reason && (
          <div className="eq-missed__reason">
            <Text as="div" variant="overline" color="secondary">Usually calls for</Text>
            <Text as="div" variant="body1" color="secondary" style={{ fontWeight: 400 }}>{reason}</Text>
            {time && <Text as="div" variant="micro" align="right" className="eq-tnum">{time}</Text>}
          </div>
        )}
        <button type="button" className="eq-reset-btn eq-pressable eq-missed__cta" onClick={onCallBack}>
          <Icon name="call" size={20} />
          <Text variant="subheading" color="none">{callBackLabel}</Text>
        </button>
      </div>
      {onFeedback && (
        <div className="eq-missed__feedback" role="group" aria-label="Was this helpful?">
          {(['up', 'down'] as const).map((v) => (
            <button key={v} type="button" aria-pressed={feedback === v} aria-label={v === 'up' ? 'Helpful' : 'Not helpful'} className={`eq-reset-btn eq-pressable eq-touch eq-missed__thumb ${feedback === v ? 'is-on' : ''}`} onClick={() => onFeedback(feedback === v ? null : v)}>
              <Icon name={feedback === v ? `thumb-${v}-fill` : `thumb-${v}`} size={20} />
            </button>
          ))}
        </div>
      )}
    </div>
  );
}
