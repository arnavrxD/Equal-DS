import type { ReactNode } from 'react';
import { Text } from '../foundations/Text';
import { AiAvatar } from './Avatar';
import { ChipRow, LargeActionChip } from './ActionChip';
import { Composer, type ComposerProps } from './Composer';
import patternUrl from '../foundations/chat-pattern@3x.webp';
import './Flows.css';

/** Assistant suggestion line — avatar + prompt (Component ref #16). */
export function AiSuggestion({ children, tone = 'primary' }: { children: ReactNode; tone?: 'primary' | 'secondary' }) {
  return (
    <div style={{ display: 'flex', gap: 12, alignItems: 'flex-start' }}>
      <AiAvatar size={36} />
      <Text as="div" variant="body1" color={tone}>{children}</Text>
    </div>
  );
}

export type AiChatPanelProps = {
  prompt: ReactNode;
  actions?: { label: string; emoji?: string; onClick?: () => void }[];
  composer?: ComposerProps | false;
};

/** AI chat — within another conversation. Bottom panel with suggestion, large chips and composer (Component ref #12b). */
export function AiChatPanel({ prompt, actions = [], composer = {} }: AiChatPanelProps) {
  // The chat wallpaper runs on behind the panel, so its rounded top corners show the pattern, not the page.
  return (
    <div className="eq-wallpaper" style={{ ['--eq-pattern-url' as string]: `url(${patternUrl})` }}>
    <section style={{ background: 'var(--eq-bg-surface)', borderRadius: '24px 24px 0 0', padding: '20px 16px 16px', boxShadow: '0 -4px 16px rgba(0,0,0,0.06)' }}>
      <AiSuggestion tone="secondary">{prompt}</AiSuggestion>
      {actions.length > 0 && (
        <div style={{ marginTop: 20 }}>
          <ChipRow gap={6}>
            {actions.map((a) => (
              <LargeActionChip key={a.label} label={a.label} emoji={a.emoji} onClick={a.onClick} />
            ))}
          </ChipRow>
        </div>
      )}
      {composer !== false && (
        <div style={{ marginTop: 16 }}>
          <Composer {...composer} />
        </div>
      )}
    </section>
    </div>
  );
}
