import { Callout, CodeTabs, H2, Page } from '../ui/kit';

const MCP_URL = 'https://equal-ds.vercel.app/mcp';

const mcpSetup = [
  { label: 'Claude Code', code: `claude mcp add --transport http equal-ds ${MCP_URL}` },
  { label: 'claude.ai / Claude Desktop', code: `Settings → Connectors → Add custom connector
Name: Equal DS
URL:  ${MCP_URL}` },
  { label: 'Cursor / Windsurf / others', code: `{
  "mcpServers": {
    "equal-ds": { "type": "http", "url": "${MCP_URL}" }
  }
}` },
  { label: 'Local (no network)', code: `# from the equal-ds folder
claude mcp add equal-ds -- node scripts/mcp-stdio.mjs` },
];

const prompts = [
  {
    label: 'New flow from a design',
    code: `Build the flow in @Flows/Referral.pdf with the Equal AI design system.
Read CLAUDE.md first. Before writing code, list every screen state and every image asset
(say which ones you need from me and which you will code). Reuse existing components, put new
touchpoints on the revamped Home / Chat / You screens as props, build it for web and Jetpack
Compose, add the flow page to the site, and run the checks.`,
  },
  {
    label: 'One screen from a screenshot',
    code: `Here is a new screen for the Equal AI app (attached). Build it with the design system:
map each element to an existing component, tell me what's new, then build web + Compose
and add it to the right flow page. Light and dark.`,
  },
  {
    label: 'Hand over assets',
    code: `Here are the assets for the Referral flow (attached, transparent PNGs):
referral-gift, referral-coins. Add them to the asset pipeline at the sizes the screens use,
register them for web and Compose, and replace the placeholders.`,
  },
  {
    label: 'Check a design',
    code: `Review the attached design against the Equal AI design system. List anything off-system
(colours, type, spacing, icons, components that already exist) and what you'd change, before
anything is built.`,
  },
];

const files = [
  { href: 'equal-ds-flow-skill.zip', name: 'equal-ds-flow-skill.zip', body: 'The Claude skill: workflow, rules and the full catalog. Upload it in claude.ai.' },
  { href: 'llms-full.txt', name: 'llms-full.txt', body: 'The full catalog: every component with props and React + Compose code, flows, tokens, assets and icons.' },
  { href: 'llms.txt', name: 'llms.txt', body: 'The short index in the llms.txt format, for any AI tool.' },
  { href: 'CLAUDE.md', name: 'CLAUDE.md', body: 'The rules and the new-flow recipe. Already in the repo root, where Claude Code reads it automatically.' },
];

export default function Claude() {
  return (
    <Page
      title="Use with Claude"
      lead="Give Claude this system and new flows come out on-system: the real components and tokens, the right assets, and both platforms. Pick the setup that matches what the designer has."
    >
      <H2>Three ways in</H2>
      <div className="s-claude-ways">
        <section className="s-tile">
          <div>
            <span className="s-claude-ways__tag">Best · builds and checks</span>
            <h3>Claude Code with the repo</h3>
            <p>Open the <code>equal-ds</code> folder in Claude Code (the desktop app’s Code tab or the terminal). Claude reads <code>CLAUDE.md</code> on its own, and the <code>equal-ds-flow</code> skill comes with the repo. It builds web and Compose, adds the flow page and runs the type, snippet and Gradle checks.</p>
          </div>
          <p>Needs: the <code>equal-ds</code> folder (shared drive or Git), Node, and JDK 17 + Android SDK for the Android checks.</p>
        </section>
        <section className="s-tile">
          <div>
            <span className="s-claude-ways__tag">For designers · no setup</span>
            <h3>claude.ai with the skill</h3>
            <p>Download the skill zip below. In claude.ai, open <b>Settings → Capabilities → Skills</b>, upload it and turn it on. Then attach a PDF or screenshots and ask for the flow. Claude specs it against the real components and writes the code, but can’t run the builds, so an engineer applies it in the repo.</p>
          </div>
          <p>Needs: a claude.ai plan with skills enabled.</p>
        </section>
        <section className="s-tile">
          <div>
            <span className="s-claude-ways__tag">Anywhere</span>
            <h3>Any AI tool</h3>
            <p>Attach <code>llms-full.txt</code>, or link to it once the site is hosted. It has the rules and every component, prop, token, asset, icon and flow, with code for both platforms.</p>
          </div>
          <p>Needs: nothing, but nothing gets verified either.</p>
        </section>
      </div>

      <H2>MCP server</H2>
      <p style={{ maxWidth: 760, color: "var(--site-ink-2)", lineHeight: 1.6 }}>
        Connect any MCP tool to the design system and it looks things up as it works, instead of reading one big file. Three tools:
        <b> search</b> (find components, flows, rules, tokens and assets in plain words), <b>get</b> (the full entry: imports, tokens, do / don’t, every example in React and Kotlin)
        and <b>list</b>. It serves the same catalog as <code>llms-full.txt</code> and updates with every deploy. Address: <code>{MCP_URL}</code>
      </p>
      <CodeTabs tabs={mcpSetup} />

      <H2>Downloads</H2>
      <div className="s-claude-files">
        {files.map((f) => (
          <a key={f.name} className="s-claude-file" href={f.href} download>
            <b>{f.name}</b>
            <span>{f.body}</span>
          </a>
        ))}
      </div>
      <p className="s-p">These are generated from the system itself (the registry, tokens, assets, icons and flows) by <code>npm run llms</code>, which also runs before <code>npm run site</code> and <code>npm run site:build</code>. Rebuild before sharing a copy so it matches the latest components.</p>

      <H2>Starter prompts</H2>
      <p className="s-p">Swap in the file names. With Claude Code, <code>@</code> attaches a file from the repo; in claude.ai, attach the files to the message.</p>
      <CodeTabs tabs={prompts.map((p) => ({ label: p.label, code: p.code }))} />

      <H2>What to give Claude</H2>
      <ol className="s-steps">
        <li><span><b>The design</b>: a PDF, Figma frames or screenshots, with every state: empty, loading, error, free and paid, light and dark, plus each experiment variant.</span></li>
        <li><span><b>Where it starts</b>: which revamped screen the flow opens from (Home, a chat, the You tab) and where it ends.</span></li>
        <li><span><b>The images</b>: 3D art, illustrations and mascots as transparent PNGs, 2000 px or more, uncropped, named like the asset (<code>referral-gift.png</code>). Everything else (logos, seals, patterns, badges) is coded. If an image isn’t ready, Claude ships a placeholder and lists it.</span></li>
        <li><span><b>What’s an experiment</b>: prices, copy or entry points the Android team will A/B test. These become props, not separate screens.</span></li>
      </ol>

      <H2>What you get back</H2>
      <ol className="s-steps">
        <li><span>The screens and states, built from existing components, with new components only where nothing fit.</span></li>
        <li><span>Web (React) and Android (Jetpack Compose) versions, plus a docs page per new component with live examples and code for both.</span></li>
        <li><span>A flow page on this site with every state, in light and dark.</span></li>
        <li><span>A list of assets still needed and the decisions it made where the design was unclear.</span></li>
        <li><span>The checks it ran (with Claude Code): web typecheck, every example has Compose code, the Gradle build and lint.</span></li>
      </ol>

      <Callout>
        <b>Sharing with other designers.</b> For the full setup, they need a copy of the <code>equal-ds</code> folder (a shared drive works; Git is better because changes merge). For claude.ai, send the skill zip. For a link, host the static site from <code>npm run site:build</code> (<code>site-dist/</code>); the downloads above then live at <code>/llms.txt</code> and <code>/equal-ds-flow-skill.zip</code> on that host.
      </Callout>
    </Page>
  );
}
