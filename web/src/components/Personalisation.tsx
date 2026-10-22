// AI Personalisation (About Me / Equal Noticed design): the FYI sheet hero, the Equal Noticed card
// (ready, reveal, coach mark), the caller context card, What stays / What goes, the avatar stack and
// the mini chat preview.
import { useState, type ReactNode } from 'react';
import { Icon } from '../foundations/Icon';
import type { IconName } from '../foundations/icons';
import { Text } from '../foundations/Text';
import { EqualOrb } from './Avatar';
import { Button, IconButton } from './Button';
import { ContextMenu, type MenuItem } from './ContextMenu';
import './Personalisation.css';

/** The 80 orb with floating trait pills (FYI sheet hero). Decorative. */
export function TraitOrbHero({ traits = ['Greets friends by name', 'Says you’ll call back at 7', 'Replies in Hinglish'] }: { traits?: string[] }) {
  return (
    <div className="eq-trait-hero" aria-hidden>
      <span className="eq-trait-hero__orb"><EqualOrb size={80} label="" /></span>
      {traits.slice(0, 3).map((t, i) => <span key={t} className={`eq-trait-hero__pill is-${i + 1}`}><Text variant="caption">{t}</Text></span>)}
    </div>
  );
}

/** An icon row in a white card: 24 icon, subheading 600 title, caption body. */
export function InfoRows({ rows }: { rows: { icon: IconName; title: string; body: string }[] }) {
  return (
    <ul className="eq-info-rows">
      {rows.map((r) => (
        <li key={r.title}>
          <Icon name={r.icon} size={24} color="var(--eq-text-action)" />
          <span>
            <Text as="div" variant="subheading" style={{ fontWeight: 600 }}>{r.title}</Text>
            <Text as="div" variant="caption" color="secondary" style={{ fontWeight: 400 }}>{r.body}</Text>
          </span>
        </li>
      ))}
    </ul>
  );
}

export type NoticedState = 'ready' | 'reveal';

/**
 * Equal Noticed (You tab, ABOUT YOU): ready shows the orb + "Equal Noticed...", share, the summary,
 * thumbs and "Updates in 21 days"; reveal covers it with `gradient/noticed-reveal` and "Tap to reveal".
 */
export function NoticedCard({ state = 'ready', summary, updatesIn = '21 days', onReveal, onShare }: { state?: NoticedState; summary: ReactNode; updatesIn?: string; onReveal?: () => void; onShare?: () => void }) {
  const [vote, setVote] = useState<'up' | 'down' | null>(null);
  if (state === 'reveal') {
    return (
      <button type="button" className="eq-reset-btn eq-noticed is-reveal" onClick={onReveal}>
        <span className="eq-noticed__sparkles" aria-hidden />
        <Text as="div" variant="title" color="none" style={{ fontWeight: 600 }}>Updated summary is ready</Text>
        <Text as="div" variant="bodyLarge" color="none" style={{ fontWeight: 500, marginTop: 4 }}>Tap to reveal</Text>
      </button>
    );
  }
  return (
    <section className="eq-noticed" aria-label="Equal Noticed">
      <div className="eq-noticed__head">
        <EqualOrb size={18} label="" />
        <Text variant="caption" style={{ fontWeight: 700, flex: 1 }}>Equal Noticed...</Text>
        <IconButton icon="share" label="Share" size={32} iconSize={20} onClick={onShare} />
      </div>
      <div className="eq-noticed__body"><Text as="div" variant="body1">{summary}</Text></div>
      <div className="eq-noticed__foot">
        <IconButton icon={vote === 'up' ? 'thumb-up-fill' : 'thumb-up'} label="Helpful" size={32} iconSize={20} aria-pressed={vote === 'up'} onClick={() => setVote(vote === 'up' ? null : 'up')} />
        <IconButton icon={vote === 'down' ? 'thumb-down-fill' : 'thumb-down'} label="Not helpful" size={32} iconSize={20} aria-pressed={vote === 'down'} onClick={() => setVote(vote === 'down' ? null : 'down')} />
        <Text variant="caption" color="secondary" style={{ marginLeft: 'auto', fontWeight: 400 }}>Updates in {updatesIn}</Text>
      </div>
    </section>
  );
}

/** First-time coach mark pointing at the reveal card: title, line and Got it. */
export function NoticedCoach({ onDismiss }: { onDismiss?: () => void }) {
  return (
    <div className="eq-coach" role="dialog" aria-label="I’m getting to know you!">
      <Text as="div" variant="subheading" style={{ fontWeight: 600 }}>I’m getting to know you!</Text>
      <Text as="p" variant="body1" color="secondary" style={{ margin: '2px 0 0' }}>The more I learn, the better I can step in and handle your calls.</Text>
      <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: 8 }}><Button size="xs" onClick={onDismiss}>Got it</Button></div>
      <span className="eq-coach__tail" aria-hidden />
    </div>
  );
}

/** What stays / What goes: green checks for what is kept, grey crosses for what is removed. */
export function KeepLoseList({ stays, goes }: { stays: string[]; goes: string[] }) {
  return (
    <div className="eq-keep-lose">
      <Text as="h3" variant="body1" style={{ fontWeight: 600 }}>What stays</Text>
      <ul>{stays.map((t) => <li key={t}><Icon name="check" size={16} color="var(--eq-text-action)" /><Text variant="body1">{t}</Text></li>)}</ul>
      <Text as="h3" variant="body1" style={{ fontWeight: 600, marginTop: 12 }}>What goes</Text>
      <ul>{goes.map((t) => <li key={t}><Icon name="close" size={16} color="var(--eq-text-secondary)" /><Text variant="body1">{t}</Text></li>)}</ul>
    </div>
  );
}

/** Overlapping 16 initials discs (excluded contacts). Decorative; the names sit next to it. */
export function AvatarStack({ initials }: { initials: string[] }) {
  return <span className="eq-avatar-stack" aria-hidden>{initials.map((i) => <span key={i}><Text variant="badge" color="none">{i}</Text></span>)}</span>;
}

/** A small, inert chat preview in a card (What Equal picks up). */
export function ChatPreviewCard({ lines }: { lines: { from: 'caller' | 'assistant'; text: string }[] }) {
  return (
    <div className="eq-chat-preview" aria-hidden>
      <span className="eq-chat-preview__pill"><Text variant="badge" color="none">Ongoing call</Text></span>
      {lines.map((l, i) => <span key={i} className={`eq-chat-preview__bubble is-${l.from}`}><Text variant="caption" style={{ fontWeight: 400 }}>{l.text}</Text></span>)}
    </div>
  );
}

export type CallerContextState = 'empty' | 'learning' | 'filled' | 'paused';

/**
 * Caller context in Contact Info: "Equal's Context for <name>" with Personalising… and a menu
 * (Share · Stop personalising · Delete all learning); empty, learning, filled (3 lines + Read more,
 * thumbs) or paused (grey, Resume personalising).
 */
export function CallerContextCard({ name, state, summary, onStop, onDelete, onResume, onShare }: { name: string; state: CallerContextState; summary?: string; onStop?: () => void; onDelete?: () => void; onResume?: () => void; onShare?: () => void }) {
  const [menu, setMenu] = useState(false);
  const [more, setMore] = useState(false);
  const items: MenuItem[] = [
    { label: 'Share', icon: 'share', onSelect: () => { setMenu(false); onShare?.(); } },
    { label: 'Stop personalising', icon: 'stop-circle', onSelect: () => { setMenu(false); onStop?.(); } },
    { label: 'Delete all learning', icon: 'delete', danger: true, onSelect: () => { setMenu(false); onDelete?.(); } },
  ];
  return (
    <section className={`eq-caller-context${state === 'paused' ? ' is-paused' : ''}`} aria-label={`Equal’s context for ${name}`}>
      <div className="eq-caller-context__head">
        <EqualOrb size={18} label="" />
        <Text variant="caption" style={{ fontWeight: 600, flex: 1, minWidth: 0 }} truncate>{state === 'learning' ? 'Learning more with every new call...' : `Equal’s Context for ${name}`}</Text>
        {state !== 'paused' && <Text variant="overline" color="action" style={{ fontWeight: 600, textTransform: 'none' }}>Personalising...</Text>}
        <span style={{ position: 'relative' }}>
          <IconButton icon="more-vert" label="More options" size={32} iconSize={20} aria-expanded={menu} onClick={() => setMenu((v) => !v)} />
          {menu && <div style={{ position: 'absolute', right: 0, top: 36, zIndex: 5 }}><ContextMenu items={items} minWidth={200} autoFocus onClose={() => setMenu(false)} label="Personalisation" /></div>}
        </span>
      </div>
      {state === 'empty' || state === 'learning' ? (
        <Text as="p" variant="captionWrap" color="secondary" align="center" style={{ margin: '12px 0 4px', fontStyle: 'italic' }}>A summary appears here after a few more calls with {name.split(' ')[0]}.</Text>
      ) : (
        <>
          <Text as="p" variant="body1" color="secondary" className={more ? undefined : 'eq-clamp-3'} style={{ margin: '8px 0 0' }}>{summary}</Text>
          {!more && <button type="button" className="eq-reset-btn eq-touch eq-caller-context__more" onClick={() => setMore(true)}><Text variant="buttonSmall" color="action">Read more</Text><Icon name="chevron-down" size={16} color="var(--eq-text-action)" /></button>}
          <div className="eq-caller-context__foot">
            <IconButton icon="thumb-up" label="Helpful" size={32} iconSize={20} />
            <IconButton icon="thumb-down" label="Not helpful" size={32} iconSize={20} />
            {state === 'paused' && <span style={{ marginLeft: 'auto' }}><Button size="xs" onClick={onResume}>Resume personalising</Button></span>}
          </div>
        </>
      )}
    </section>
  );
}
