import { Emoji, FeatureBadge, LargeActionChip, SmallActionChip } from '@equal/ds';
import { CopyTag, Example, H2, Page } from '../../ui/kit';
import { FoundationCode } from '../../ui/FoundationCode';

const set = [
  ['📞', 'Call back · Takeover'],
  ['✏️', 'Update address · Change something else'],
  ['📅', 'Add to Calendar'],
  ['🗓️', 'Ask to reschedule'],
  ['💚', 'Reply on WhatsApp'],
  ['💬', 'Take a message'],
  ['⭐', 'New feature'],
  ['🪄', 'Instruction applied'],
  ['✍️', 'Edit (setup chat)'],
  ['👍', 'Confirm'],
  ['👌', "That's all"],
  ['✅', 'Confirmed reaction'],
  ['🛡️ 📞 💼', 'Preset badges'],
  ['🚫', 'Decline sales & spam'],
] as const;

export default function EmojiPage() {
  return (
    <Page title="Action emoji" lead="Actions speak in Unicode emoji: the platform’s colour emoji at a fixed dp size, so they don’t grow with the user’s font scale. They are content, not an icon system; flat UI glyphs stay Material Symbols." meta={<CopyTag k="React" value={'<SmallActionChip emoji="📞" label="Call back" />'} />}>
      <H2>The vocabulary</H2>
      <div className="s-grid" style={{ ['--min' as string]: '170px' }}>
        {set.map(([e, l]) => (
          <div key={l} className="s-cell" style={{ cursor: 'default' }}>
            <span style={{ fontSize: 30, lineHeight: 1 }}>{e}</span>
            <b>{l}</b>
          </div>
        ))}
      </div>
      <H2>In context</H2>
      <Example title="Chips and badges" width="wide" code={`<SmallActionChip label="Call back" emoji="📞" />\n<SmallActionChip label="Update Address" emoji="✏️" state="read" />\n<LargeActionChip label="Ask to reschedule" emoji="🗓️" />\n<FeatureBadge />`}>
        <div style={{ display: 'flex', flexWrap: 'wrap', gap: 12, alignItems: 'center' }}>
          <SmallActionChip label="Call back" emoji="📞" />
          <SmallActionChip label="Update Address" emoji="✏️" state="read" />
          <LargeActionChip label="Ask to reschedule" emoji="🗓️" />
          <LargeActionChip label="Edit" emoji="✍️" state="read" />
          <FeatureBadge />
          <Emoji char="✅" size={20} label="Confirmed" />
        </div>
      </Example>
      <p className="s-p">Sizes: 16dp in small chips, 18dp in large chips, 14dp in the feature badge. On Android the glyph comes from the device’s emoji font, so it will look slightly different from these renders.</p>
      <H2>Code</H2>
      <FoundationCode id="emoji" />
    </Page>
  );
}
