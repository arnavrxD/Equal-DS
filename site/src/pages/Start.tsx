import { CopyTag, H2, Page } from '../ui/kit';

const Code = ({ children }: { children: string }) => <pre className="s-code" style={{ borderRadius: 16, border: '1px solid var(--site-hair)', margin: '12px 0 20px' }}><code>{children}</code></pre>;

export default function Start() {
  return (
    <Page title="Using the system" lead="How to consume the design system from web and Android, and how a value travels from the token file to both platforms.">
      <H2>Run this site</H2>
      <p className="s-p">From <code>equal-ds/</code>:</p>
      <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap' }}>
        <CopyTag k="dev" value="npm run site" />
        <CopyTag k="build" value="npm run site:build" />
        <CopyTag k="preview" value="npm run site:preview" />
      </div>
      <p className="s-p" style={{ marginTop: 12 }}>The build is static (<code>site-dist/</code>) with hash URLs, so it can be hosted anywhere with no rewrites.</p>

      <H2>Web (React)</H2>
      <p className="s-p">Import the tokens and base styles once at the root, set the theme on a wrapper, then use components.</p>
      <Code>{`import './equal-ds/web/src/tokens/tokens.css';
import './equal-ds/web/src/styles/base.css';
import { ChatListItem, Button, Text } from './equal-ds/web/src';

<div data-theme="light">   {/* or "dark" */}
  <Text variant="subheading">Instructions</Text>
  <Button leadingIcon="add">Add New Instructions</Button>
</div>`}</Code>
      <p className="s-p">Colours are CSS variables (<code>var(--eq-text-action)</code>) or TS references (<code>color.textAction</code>). Type is <code>{'<Text variant="body1">'}</code> or the class <code>.eq-t-body1</code>. Icons are <code>{'<Icon name="call" />'}</code>; actions take an <code>emoji</code> prop.</p>

      <H2>Android (Jetpack Compose)</H2>
      <p className="s-p">The Android app is <b>Kotlin</b> for business logic, <b>Jetpack Compose</b> for UI and <b>Lottie</b> (JSON) for animations. Include the <code>compose/equal-ds</code> module and wrap your UI in <code>EqualTheme</code>. Components are stateless: hoist state into your ViewModel and pass values and callbacks in.</p>
      <Code>{`EqualTheme {                       // follows the system dark setting
  val colors = EqualTheme.colors      // EqualColors: textAction, bgSurface, …
  val type = EqualTheme.type          // DM Sans: body1, subheading, …
  EqualButton(text = "Save", onClick = {})
  EqualIcon(EqualIcons.Call, contentDescription = "Call", tint = colors.textAction)
}`}</Code>
      <p className="s-p">The module builds with its own Gradle wrapper: run <code>./gradlew :equal-ds:assembleRelease</code> in <code>compose/</code> (JDK 17, Android SDK 35), or open the folder in Android Studio. Debug and release AARs build with no compiler warnings, and Lint reports no issues.</p>

      <H2>From token to both platforms</H2>
      <ol className="s-steps">
        <li><span>Edit the value in <code>tokens/tokens.json</code>. Mark tokens that come from screens rather than the colour guide <code>"derived": true</code>, and check text pairs pass WCAG AA in both themes.</span></li>
        <li><span>Run <code>npm run tokens</code>. It writes <code>web/src/tokens/tokens.css</code>, <code>tokens.ts</code> and Compose <code>EqualTokens.kt</code>.</span></li>
        <li><span>Icons: add a line to <code>scripts/build-icons.mjs</code> and run <code>npm run icons</code> (web <code>icons.ts</code> + Compose <code>EqualSymbols.kt</code>).</span></li>
        <li><span>Logo: replace <code>tokens/brand/equal-logo.svg</code> with the official SVG and run <code>npm run logo</code>.</span></li>
        <li><span>Never hand-edit generated files; they carry a GENERATED header.</span></li>
      </ol>

      <H2>Rules that hold everywhere</H2>
      <p className="s-p">The short list is below. Every rule, with the reason it was decided and where it came from, is on <a href="#/reference/decisions">Rules &amp; decisions</a>.</p>
      <ol className="s-steps">
        <li><span><b>Text that doesn’t match a type token is a bug.</b> There are 12 tokens and no ad-hoc sizes.</span></li>
        <li><span><b>Green means actionable.</b> Green (lime in dark) at Medium only on touchable things or the assistant’s byline.</span></li>
        <li><span><b>Weight separates label from prose.</b> Medium is scanned, Regular is read; wrapping text is always Regular.</span></li>
        <li><span><b>Two actions at equal weight are one action.</b> Commit is a filled button, revise is a text action.</span></li>
        <li><span><b>48dp touch targets</b>, even when the visual is smaller.</span></li>
        <li><span><b>Four weights: 400, 500, 600, 700.</b> Regular for reading, Medium for labels, SemiBold where the designs use it (plan names), Bold for display lines.</span></li>
      </ol>
    </Page>
  );
}
