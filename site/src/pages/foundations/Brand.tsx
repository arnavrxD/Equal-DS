import { Asset, AssistantToggle, categories, categoryLabel, CategoryAvatar, ChatBubble, ChatWallpaper, EqualLogo, EqualMark, EqualOrb, Fab, Mascot, MessageList, ProfileAvatar } from '@equal/ds';
import { CopyTag, Example, H2, Page } from '../../ui/kit';
import { FoundationCode } from '../../ui/FoundationCode';

const MASCOTS = [
  { name: 'mascot-robot', use: 'AI Assistant is On card (40dp)' },
  { name: 'mascot-sleeping', use: 'System status card, Temporarily Unavailable (104dp)' },
  { name: 'whats-new-illustration', use: 'What’s New: Language Preference (125dp)' },
  { name: 'illustration-addresses-empty', use: 'Addresses empty state (200dp)' },
  { name: 'illustration-instructions-empty', use: 'Active Instructions empty state (200dp)' },
  { name: 'illustration-suggestions', use: '“New suggestions coming…” card (140dp)' },
  { name: 'mascot-instructions', use: 'Instructions hub: Active Instructions row (40dp on lime)' },
  { name: 'preset-screen-protect', use: 'Preset: Screen & Protect (33dp; 16dp badge)' },
  { name: 'preset-everyday-calls', use: 'Preset: Handle everyday calls (33dp; 16dp badge)' },
  { name: 'preset-business', use: 'Preset: Run my business (33dp; 16dp badge)' },
] as const;

export default function Brand() {
  return (
    <Page title="Brand & art" lead="The supplied artwork: logo, orb, mascot, avatars, caller categories and the chat wallpaper. Art is never cropped or redrawn; components scale it so the subject fills its box.">
      <H2>Logo</H2>
      <p className="s-p">The official vector logo (<code>tokens/brand/equal-logo.svg</code>, 113 × 24). Colour is <code>text/action</code>: #00B140 in light, lime in dark. If the artwork changes, replace the SVG and run <code>npm run logo</code>.</p>
      <Example title="Lockup, wordmark and mark" width="wide" code={`<EqualLogo height={32} />\n<EqualLogo height={20} variant="wordmark" />\n<EqualMark width={30} />`}>
        <div style={{ display: 'flex', flexWrap: 'wrap', alignItems: 'center', gap: 40 }}>
          <EqualLogo height={36} />
          <EqualLogo height={22} variant="wordmark" />
          <EqualMark width={34} />
        </div>
      </Example>

      <H2>Orb</H2>
      <p className="s-p">Supplied art: the green orb in light, the lime orb in dark, swapped automatically. The opaque orb is the centre 80% of the file, and its glow deliberately overflows the layout box, so <code>size</code> is the orb itself. It’s used for the AI avatar and the FAB.</p>
      <Example title="Sizes" width="wide" code={`<EqualOrb size={36} />\n<Fab />`}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 36, flexWrap: 'wrap' }}>
          {[24, 36, 48, 72].map((s) => <EqualOrb key={s} size={s} />)}
          <Fab />
        </div>
      </Example>

      <H2>Mascot</H2>
      <p className="s-p">The robot on the “AI Assistant is On” card. The file keeps its transparent padding; <code>{'<Mascot>'}</code> scales it so the robot fills the box, and turns it greyscale when the assistant is off.</p>
      <Example title="Mascot in the assistant card" width={360} code={`<Mascot size={36} />\n<AssistantToggle on />`}>
        <div style={{ width: '100%', display: 'flex', flexDirection: 'column', gap: 16, alignItems: 'center' }}>
          <div style={{ display: 'flex', gap: 28, alignItems: 'flex-end' }}>
            <Mascot size={36} />
            <Mascot size={64} />
            <Mascot size={36} dimmed />
          </div>
          <AssistantToggle on />
        </div>
      </Example>

      <H2>Mascot illustrations</H2>
      <p className="s-p">Every green robot in the product is a custom-generated illustration, made for its moment. They aren’t drawn in code: each one is an image asset in <code>assets/</code>, used through <code>{'<Asset name="…" />'}</code> (web) or <code>EqualAssets</code> (Compose). When a new screen needs the robot, generate a new one from the brief below rather than reusing a pose that doesn’t fit.</p>
      <div className="s-mascots">
        {MASCOTS.map((m) => (
          <figure key={m.name} className="s-mascots__item">
            <div className="s-mascots__art"><Asset name={m.name} width={112} height={112} alt="" /></div>
            <figcaption>
              <CopyTag k="Asset" value={m.name} />
              <span>{m.use}</span>
            </figcaption>
          </figure>
        ))}
      </div>
      <h3 className="s-h3">Generating a new one</h3>
      <ul className="s-list">
        <li><b>Character</b>: the same robot every time. Rounded-cube head in Equal green (glossy, soft-plastic), off-white rounded face screen, black dot or arc eyes, small ear discs with lime caps, a thin stem antenna with a lime ball.</li>
        <li><b>Style</b>: 3D clay / soft plastic, studio lighting from the top-left, soft contact shadow, no outlines, no text baked in (copy stays in the UI).</li>
        <li><b>Mood</b>: the face carries the state: happy (Instructions, Language), surprised (empty states), sleeping (Temporarily Unavailable). Props tell the story (map + parcel for addresses, open book for instructions, script letters for language).</li>
        <li><b>Accents</b>: sparkles and outline speech bubbles in light green (#98E2B6-ish); one warm accent at most (a red pin, an amber glow).</li>
        <li><b>Export</b>: PNG with a transparent background, at least 2000px on the long side, subject centred with a little breathing room, never cropped. Drop it into <code>equal-ds/assets/source/</code> and run <code>npm run assets</code>: it writes the WebP for web and the <code>drawable-xxxhdpi</code> copy for Compose at 4× the display size (add the size to <code>scripts/build-assets.py</code>).</li>
        <li><b>Name</b>: <code>illustration-&lt;moment&gt;</code> (or <code>mascot-&lt;state&gt;</code> for the bare robot), and add it to <code>assetNames</code> / <code>EqualAssets</code> with its display size.</li>
      </ul>

      <H2>Profile avatars</H2>
      <p className="s-p">Two tiers, set by the user’s plan: <b>free</b> users get the plain bust; <b>paid</b> users get the avatar on its gold background and scalloped gold ring. Pass <code>tier</code> to <code>ProfileAvatar</code>, <code>GenderSelector</code>, <code>ProfileHero</code>, <code>ProfileHeader</code> and <code>BottomNav</code> (Compose: <code>PlanTier.Free</code> / <code>PlanTier.Paid</code>).</p>
      <p className="s-p">A 3D character in a gold scalloped ring; the head rises above the ring by design. <code>size</code> is the width, and choosing a gender on Personal Details swaps it app-wide.</p>
      <Example title="Male and female" width="wide" code={`<ProfileAvatar variant="male" size={70} />\n<ProfileAvatar variant="female" size={70} />`}>
        <div style={{ display: 'flex', alignItems: 'flex-end', gap: 28, flexWrap: 'wrap' }}>
          {[24, 48, 70, 110].map((s) => <ProfileAvatar key={`m${s}`} variant="male" size={s} />)}
          {[24, 48, 70, 110].map((s) => <ProfileAvatar key={`f${s}`} variant="female" size={s} />)}
        </div>
      </Example>

      <H2>Caller categories</H2>
      <p className="s-p">The contact avatar on the Chats page, the chat header and incoming bubbles. Each image carries its own coloured circle. Unknown callers are <code>na</code> (the sparkle); <code>others</code> is the red telephone and the default for incoming bubbles.</p>
      <div className="s-grid" style={{ ['--min' as string]: '120px' }}>
        {categories.map((c) => (
          <div key={c} className="s-cell" style={{ cursor: 'default' }}>
            <CategoryAvatar category={c} size={60} />
            <b>{categoryLabel[c]}</b>
            <CopyTag value={`<ContactAvatar category="${c}" />`} label={c} />
          </div>
        ))}
      </div>

      <H2>Chat wallpaper</H2>
      <p className="s-p">The supplied doodle pattern, rendered as a tinted alpha mask on a 360 × 750 tile, so one file serves light and dark. It sits behind chat threads and the instruction setup chat.</p>
      <Example title="Thread on the wallpaper" width={360} code={`<ChatWallpaper>\n  <MessageList>…</MessageList>\n</ChatWallpaper>`}>
        <div style={{ width: 360, borderRadius: 16, overflow: 'hidden' }}>
          <ChatWallpaper style={{ height: 300 }}>
            <MessageList>
              <ChatBubble kind="outgoing" sender="Equal AI" time="03:21">Hi, who is this?</ChatBubble>
              <ChatBubble kind="incoming" sender="Dr Mehta Clinic" time="03:21">This is Priya from Dr. Mehta’s clinic. Your appointment is confirmed for tomorrow.</ChatBubble>
            </MessageList>
          </ChatWallpaper>
        </div>
      </Example>

      <H2>Still to supply</H2>
      <p className="s-p">These slots render a checkerboard at their exact size until the file is added to <code>equal-ds/assets/</code> (web) and <code>res/drawable</code> (Android). See Image assets for the full list.</p>
      <div style={{ display: 'flex', gap: 16, flexWrap: 'wrap' }}>
        {(['illustration-trash', 'illustration-silent-mode', 'illustration-whatsapp', 'illustration-addresses-empty'] as const).map((n) => (
          <div key={n} className="s-cell" style={{ cursor: 'default', width: 150 }}>
            <Asset name={n} size={72} radius={10} />
            <span>{n}</span>
          </div>
        ))}
      </div>
      <H2>Code</H2>
      <FoundationCode id="brand" />
    </Page>
  );
}
