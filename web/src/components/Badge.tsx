import { Emoji } from '../foundations/Icon';
import { Text } from '../foundations/Text';
import './Badge.css';

/** Text badge — one-word state (NEW). Uppercased in the string. Never wraps, max two words. */
export function NewBadge({ label = 'New', tone = 'solid' }: { label?: string; tone?: 'solid' | 'soft' }) {
  return <Text variant="badge" color="none" className={`eq-badge eq-badge--text eq-badge--${tone}`}>{label.toUpperCase()}</Text>;
}

/** Count badge — unread count. Tabular, −0.026em. 99+ collapses to 99+. */
export function CountBadge({ count, size = 20, ring = false }: { count: number; size?: number; ring?: boolean }) {
  if (count <= 0) return null;
  const text = count > 99 ? '99+' : String(count);
  return (
    <Text variant="badgeCount" color="none" className={`eq-badge eq-badge--count ${ring ? 'eq-badge--ring' : ''}`} style={{ minWidth: size, height: size }}>
      <span aria-hidden>{text}</span>
      <span className="eq-sr">{count} unread</span>
    </Text>
  );
}

/** NEW FEATURE pill with the star asset. */
export function FeatureBadge({ label = 'New feature' }: { label?: string }) {
  return (
    <span className="eq-badge eq-badge--feature">
      <Emoji char="⭐" size={11} />
      <Text variant="badge" color="none">{label.toUpperCase()}</Text>
    </span>
  );
}

/** Plan marker (UNLIMITED) — gold gradient. */
/** Plan marker beside the logo. `expired` is the red PLAN EXPIRED badge (Plan Expired touchpoint). */
export function PlanBadge({ label = 'Unlimited', tone = 'paid' }: { label?: string; tone?: 'paid' | 'expired' }) {
  return <Text variant="badge" color="none" className={`eq-badge eq-badge--plan ${tone === 'expired' ? 'eq-badge--expired' : ''}`}>{label.toUpperCase()}</Text>;
}
