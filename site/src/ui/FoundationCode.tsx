import composeSnippets from '../registry/compose-snippets.json';
import { CodeTabs } from './kit';

const REACT: Record<string, string> = {
  colour: `import '@equal/ds/web/src/tokens/tokens.css';
import { Text } from '@equal/ds';

// Set the theme once on a wrapper; every alias token switches with it.
<div data-theme="dark">
  <div style={{ background: 'var(--eq-bg-surface)', borderRadius: 'var(--eq-radius-lg)', padding: 16 }}>
    <Text variant="subheading" color="action">Equal AI</Text>
  </div>
  <div style={{ width: 64, height: 64, background: 'var(--eq-gradient-fab)' }} />
</div>`,
  typography: `import { Text } from '@equal/ds';

<Text as="h1" variant="headline">Amit Nanda</Text>
<Text variant="subheading">Assistant Health Check</Text>
<Text variant="caption" color="secondary">Check Real-Time Assistant Connectivity</Text>
<Text variant="overline" color="secondary">CALL HISTORY</Text>`,
  layout: `<div
  style={{
    background: 'var(--eq-bg-surface)',
    borderRadius: 'var(--eq-radius-lg)',
    boxShadow: 'var(--eq-shadow-card)',
    padding: 'var(--eq-space-12) var(--eq-space-16)',
  }}
>
  <Text variant="body1">Card: radius lg, padding 16 / 12, shadow card</Text>
</div>`,
  icons: `import { Icon } from '@equal/ds';

<Icon name="call" color="var(--eq-text-action)" label="Call" />
<Icon name="instructions" />
<Icon name="silent-mode" size={20} color="var(--eq-text-secondary)" label="Mute" />`,
  emoji: `import { Emoji, SmallActionChip } from '@equal/ds';

// Actions use Unicode emoji, never icon images
<Emoji char="📞" size={16} />
<SmallActionChip label="Call back" emoji="📞" />`,
  brand: `import { CategoryAvatar, ChatWallpaper, EqualLogo, EqualOrb, Mascot, ProfileAvatar } from '@equal/ds';

<EqualLogo height={20} />{/* green in light, lime in dark */}
<EqualOrb size={36} />
<Mascot size={40} />
<CategoryAvatar category="delivery" size={48} />
<ProfileAvatar variant="female" size={48} />
<ChatWallpaper>…messages…</ChatWallpaper>`,
};

const CSS: Partial<Record<string, string>> = {
  colour: `.card {
  background: var(--eq-bg-surface);   /* #FFFFFF light · #222222 dark */
  color: var(--eq-text-primary);
  border: 1px solid var(--eq-stroke-subtle);
}
.cta { background: var(--eq-button-primary); color: var(--eq-text-on-action); }`,
  typography: `/* Each type token is a class: .eq-t-<token> */
<h3 class="eq-t-subheading">Assistant Health Check</h3>
<p class="eq-t-caption">Check Real-Time Assistant Connectivity</p>`,
  layout: `.card {
  padding: var(--eq-space-12) var(--eq-space-16);
  border-radius: var(--eq-radius-lg);
  box-shadow: var(--eq-shadow-card);
  transition: transform var(--eq-duration-base);
}`,
};

/** Code for a Foundations page: React, CSS where it applies, and the compiled Compose snippet. */
export function FoundationCode({ id }: { id: keyof typeof REACT | string }) {
  const compose = (composeSnippets as Record<string, Record<string, string>>).foundations?.[id];
  return (
    <CodeTabs
      tabs={[
        { label: 'React', code: REACT[id] ?? '', lang: 'tsx' },
        ...(CSS[id] ? [{ label: 'CSS', code: CSS[id]!, lang: 'css' as const }] : []),
        ...(compose ? [{ label: 'Compose', code: compose, lang: 'kotlin' as const }] : []),
      ]}
    />
  );
}
