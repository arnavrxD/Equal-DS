// Search (Search design): the search bar, recent searches, result sections, message hits with the
// match in bold, and the empty / no internet / no results / loading states.
import type { ReactNode } from 'react';
import { Asset, type AssetName, type Category } from '../foundations/Asset';
import { Icon } from '../foundations/Icon';
import { Text } from '../foundations/Text';
import { CategoryAvatar, ContactAvatar } from './Avatar';
import './Search.css';

/** Pill search bar with the back arrow inside and a green outline while active. */
export function SearchBar({ value, onChange, onBack, placeholder = 'Search for names', autoFocus }: { value: string; onChange?: (v: string) => void; onBack?: () => void; placeholder?: string; autoFocus?: boolean }) {
  return (
    <div className="eq-searchbar">
      <button type="button" className="eq-reset-btn eq-touch eq-searchbar__back" aria-label="Back" onClick={onBack}>
        <Icon name="arrow-back" size={22} />
      </button>
      <input className="eq-t-body-large" value={value} placeholder={placeholder} aria-label={placeholder} autoFocus={autoFocus} onChange={(e) => onChange?.(e.target.value)} />
      {value && (
        <button type="button" className="eq-reset-btn eq-touch eq-searchbar__clear" aria-label="Clear search" onClick={() => onChange?.('')}>
          <Icon name="close" size={18} />
        </button>
      )}
    </div>
  );
}

/** Section label (overline) with an optional "Clear all" pill on the right. */
export function SearchSection({ title, onClear }: { title: string; onClear?: () => void }) {
  return (
    <div className="eq-search-section">
      <Text as="h2" variant="overline" color="secondary">{title}</Text>
      {onClear && <button type="button" className="eq-reset-btn eq-touch eq-search-section__clear eq-t-caption" onClick={onClear}><Text variant="inherit" color="none">Clear all</Text></button>}
    </div>
  );
}

export type RecentSearch = { label: string; category?: Category; initials?: string };

/** Recent searches as avatar tiles (name truncated under the avatar). */
export function RecentSearches({ items, onPick }: { items: RecentSearch[]; onPick?: (label: string) => void }) {
  return (
    <div className="eq-recent">
      {items.map((r) => (
        <button key={r.label} type="button" className="eq-reset-btn eq-pressable eq-recent__item" onClick={() => onPick?.(r.label)}>
          {r.initials ? <ContactAvatar size={52} initials={r.initials} /> : <CategoryAvatar category={r.category ?? 'na'} size={52} label="" />}
          <Text as="span" variant="caption" truncate style={{ fontWeight: 400, maxWidth: 72 }}>{r.label}</Text>
        </button>
      ))}
    </div>
  );
}

/** Wraps every case-insensitive occurrence of `query` in bold. */
export function Highlight({ text, query }: { text: string; query: string }) {
  const q = query.trim();
  if (!q) return <>{text}</>;
  const parts: ReactNode[] = [];
  const lower = text.toLowerCase();
  const needle = q.toLowerCase();
  let i = 0;
  let k = 0;
  for (let at = lower.indexOf(needle); at !== -1; at = lower.indexOf(needle, i)) {
    if (at > i) parts.push(text.slice(i, at));
    parts.push(<Text key={k++} as="b" variant="inherit" color="none" className="eq-highlight">{text.slice(at, at + q.length)}</Text>);
    i = at + q.length;
  }
  if (i < text.length) parts.push(text.slice(i));
  return <>{parts}</>;
}

/** A message hit: who, when, and the line with the match in bold. */
export function MessageResult({ name, time, text, query, onClick }: { name: string; time: string; text: string; query: string; onClick?: () => void }) {
  return (
    <button type="button" className="eq-reset-btn eq-pressable eq-message-result" onClick={onClick}>
      <span className="eq-message-result__top">
        <Text as="span" variant="subheading" truncate>{name}</Text>
        <Text as="span" variant="micro" color="secondary" className="eq-tnum">{time}</Text>
      </span>
      <Text as="span" variant="body1" truncate style={{ fontWeight: 400 }}><Highlight text={text} query={query} /></Text>
    </button>
  );
}

/** Three green dots that pulse while results load. */
export function LoadingDots({ label = 'Loading' }: { label?: string }) {
  return (
    <div className="eq-loading-dots" role="status" aria-label={label}>
      <span /><span /><span />
    </div>
  );
}

/** Centered illustration state (search empty, no internet, no results). */
export function SearchState({ art, size = 160, title, body }: { art: AssetName; size?: number; title: string; body: string }) {
  return (
    <div className="eq-search-state">
      <Asset name={art} size={size} alt="" />
      <Text as="h2" variant="title" align="center" style={{ fontWeight: 500, marginTop: 12 }}>{title}</Text>
      <Text as="p" variant="body1" color="secondary" align="center" style={{ fontWeight: 400, margin: '4px 0 0', maxWidth: 280 }}>{body}</Text>
    </div>
  );
}
