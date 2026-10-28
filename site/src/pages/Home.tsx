import { useEffect } from 'react';
import {
  AiChatPanel,
  AiSummaryCard,
  aliases,
  ANDROID_FRAME,
  AssistantToggle,
  Button,
  CallSummary,
  ChatBubble,
  ChatListItem,
  ChatWallpaper,
  ContactInfoHeader,
  CountBadge,
  EqualLogo,
  FeatureBadge,
  FeedbackCard,
  Icon,
  iconNames,
  InstructionCard,
  LargeActionChip,
  MessageList,
  NewBadge,
  PhoneFrame,
  PlanBadge,
  PlanCard,
  RowGroup,
  SettingsRow,
  SmallActionChip,
  Switch,
  Text,
  ThreadNotice,
  typography,
} from '@equal/ds';
import { HomeScreen, YouScreen } from '../../../web/src/screens/Screens';
import { InstructionsFlow } from '../../../web/src/screens/FlowScreens';
import { instructionLibrary } from '../../../web/src/screens/flowData';
import { componentDocs } from '../registry/components';
import { Link } from '../app/router';

// React 18 has no `inert` typing; the empty string sets the attribute.
const inert = { inert: '' } as object;

const Tile = ({ span, children, label, to, tall }: { span: number; children: React.ReactNode; label: string; to: string; tall?: boolean }) => (
  <Link to={to} className={`s-bento__tile ${tall ? 'is-tall' : ''}`} style={{ gridColumn: `span ${span}` }} aria-label={`${label} component`}>
    <div className="s-bento__stage" aria-hidden {...inert}>
      {children}
    </div>
    <span className="s-bento__label">{label}<Icon name="arrow-forward" size={16} /></span>
  </Link>
);

const Band = ({ title, body, to, cta, children, flip }: { title: string; body: string; to: string; cta: string; children: React.ReactNode; flip?: boolean }) => (
  <section className={`s-feature ${flip ? 'is-flip' : ''}`}>
    <div className="s-feature__copy">
      <h2>{title}</h2>
      <p>{body}</p>
      <Link to={to} className="s-arrowlink">{cta} <Icon name="arrow-forward" size={18} /></Link>
    </div>
    <div className="s-feature__visual">{children}</div>
  </section>
);

const Mini = ({ scale, children, height = 800 }: { scale: number; children: React.ReactNode; height?: number }) => (
  <div style={{ width: ANDROID_FRAME.width * scale, height: height * scale, flexShrink: 0 }} aria-hidden {...inert}>
    <div style={{ width: ANDROID_FRAME.width, height, transform: `scale(${scale})`, transformOrigin: 'top left' }}>
      <PhoneFrame height={height}>{children}</PhoneFrame>
    </div>
  </div>
);

export default function Home() {
  useEffect(() => {
    document.title = 'Equal AI Design System';
  }, []);
  const compCount = componentDocs.reduce((n, d) => n + d.components.length, 0);
  const tokenCount = aliases.length + Object.keys(typography).length;
  const previews = ['button', 'action-chips', 'switch', 'badges', 'chat-bubble', 'segmented-chips'];

  return (
    <div className="s-home2">
      <section className="s-hero" aria-labelledby="hero-title">
        <div className="s-hero__logo"><EqualLogo height={84} /></div>
        <h1 id="hero-title" className="s-hero__title">The design system behind Equal AI, from <b>token to screen</b></h1>
        <div className="s-hero__ctas">
          <Link to="/start" className="s-btn s-btn--primary">Get started</Link>
          <Link to="/components" className="s-btn">Browse components</Link>
          <Link to="/start/claude" className="s-btn">Use with Claude</Link>
        </div>
        <p className="s-hero__meta">
          DM Sans · Material Symbols · one token file for <u>React</u> and <u>Jetpack Compose</u>
        </p>
      </section>

      <section className="s-bento" aria-label="Live components">
        <Tile span={4} label="Assistant toggle · Chat list item" to="/components/chat-list-item" tall>
          <div style={{ width: 340, display: 'flex', flexDirection: 'column', gap: 12 }}>
            <AssistantToggle on />
            <div style={{ borderRadius: 16, overflow: 'hidden', display: 'flex', flexDirection: 'column', gap: 1, background: 'var(--eq-stroke-subtle)' }}>
              <ChatListItem name="Arnav Raj" preview="You have a visitor" date="Yesterday" unread={5} actions={[{ label: 'Call back', emoji: '📞' }, { label: 'Update Address', emoji: '✏️' }]} />
              <ChatListItem name="HDFC Bank" preview="Pre-approved loan offer. You said not interested" date="08 July" avatar={{ category: 'financial' }} actions={[{ label: 'Call back', emoji: '📞' }]} />
            </div>
          </div>
        </Tile>
        <Tile span={4} label="Chat bubbles on the wallpaper" to="/components/chat-bubble" tall>
          <div style={{ width: 340, borderRadius: 18, overflow: 'hidden' }}>
            <ChatWallpaper>
              <MessageList>
                <ChatBubble kind="outgoing" sender="Equal AI" time="03:21">Hi, who is this?</ChatBubble>
                <ChatBubble kind="incoming" sender="Dr Mehta Clinic" time="03:21">This is Priya from Dr. Mehta’s clinic. Your appointment is confirmed for tomorrow.</ChatBubble>
                <ChatBubble kind="outgoing" sender="Equal AI" time="03:21">Yes, I’ll be there at 10 AM</ChatBubble>
                <ThreadNotice>Call has ended. You are now talking to your AI assistant.</ThreadNotice>
              </MessageList>
            </ChatWallpaper>
          </div>
        </Tile>
        <Tile span={4} label="Plan card · Badges" to="/components/plan-card">
          <div style={{ width: 340, display: 'flex', flexDirection: 'column', gap: 16 }}>
            <PlanCard onManage={() => {}} />
            <div style={{ display: 'flex', gap: 10, alignItems: 'center', flexWrap: 'wrap' }}>
              <NewBadge /><CountBadge count={2} /><PlanBadge /><FeatureBadge />
            </div>
          </div>
        </Tile>
        <Tile span={4} label="Feedback card" to="/components/feedback-card">
          <FeedbackCard rating={4} width={300} />
        </Tile>
        <Tile span={5} label="Call summary" to="/components/call-summary">
          <CallSummary summary="Amit invited you to dinner this Sunday, 8 PM at his place. He asked you to confirm by tonight." time="03:21" tag={{ emoji: '🪄', label: '2 instructions applied in this call' }} onShare={() => {}} width={320} />
        </Tile>
        <Tile span={3} label="Buttons · Switch" to="/components/button">
          <div style={{ display: 'flex', flexDirection: 'column', gap: 12, alignItems: 'flex-start' }}>
            <Button leadingIcon="add">Add instruction</Button>
            <Button variant="secondary">Try Voices</Button>
            <Button variant="danger" leadingIcon="delete">Delete</Button>
            <div style={{ display: 'flex', gap: 12 }}><Switch checked label="On" /><Switch checked={false} label="Off" /></div>
          </div>
        </Tile>
        <Tile span={6} label="AI chat panel" to="/components/ai-panel">
          <div style={{ width: 380 }}>
            <AiChatPanel prompt="Want me to send Amit a quick reply on WhatsApp?" actions={[{ label: 'Reply on WhatsApp', emoji: '💚' }, { label: 'Add to Calendar', emoji: '📅' }]} />
          </div>
        </Tile>
        <Tile span={6} label="Instruction card" to="/components/instruction-cards">
          <div style={{ width: 380 }}>
            <InstructionCard instruction={instructionLibrary.declineSales} />
          </div>
        </Tile>
        <Tile span={4} label="AI summary card" to="/components/ai-summary-card">
          <div style={{ width: 330 }}>
            <AiSummaryCard summary="Your family calls you a lot. They always say it’s urgent. They almost never say why." />
          </div>
        </Tile>
        <Tile span={4} label="Settings rows" to="/components/settings-row">
          <div style={{ width: 340 }}>
            <RowGroup inset={0}>
              <SettingsRow icon="instructions" label="Your Instructions" description="Tell Equal what to do" isNew />
              <SettingsRow icon="health-check" label="Assistant Health Check" description="Check Real-Time Assistant Connectivity" />
              <SettingsRow icon="silent-mode" label="Silent Mode" description="Mute unwanted calls automatically" />
            </RowGroup>
          </div>
        </Tile>
        <Tile span={4} label="Contact info header" to="/components/contact-info-header">
          <div style={{ width: 340 }}>
            <ContactInfoHeader name="Amit Nanda" phone="+91 9871746113" avatar={{ category: 'career' }} actions={[{ icon: 'call', label: 'Call' }, { icon: 'silent-mode', label: 'Mute' }, { icon: 'person-add', label: 'Save' }, { icon: 'share', label: 'Share' }]} />
          </div>
        </Tile>
      </section>

      <Band title="Foundations you can copy" body={`${tokenCount} tokens for colour and type, a dp spacing scale, ${iconNames.length} Material Symbols, and the supplied brand art. Every value is one click from its CSS, TypeScript and Kotlin name.`} to="/foundations/colour" cta="Explore foundations">
        <div className="s-found2">
          <div className="s-found2__swatches" aria-hidden>
            {['--eq-button-primary', '--eq-button-secondary', '--eq-text-primary', '--eq-chat-bubble-user-bg', '--eq-action-card-highlight', '--eq-button-danger'].map((t) => (
              <span key={t} style={{ background: `var(${t})` }} />
            ))}
          </div>
          <div className="s-found2__type" aria-hidden>
            <div style={{ fontSize: 56, lineHeight: 1, fontWeight: 500, letterSpacing: '-0.04em' }}>Aa</div>
            <div>
              <Text as="div" variant="subheading">Assistant Health Check</Text>
              <Text as="div" variant="caption" color="secondary" style={{ fontWeight: 400 }}>Check Real-Time Assistant Connectivity</Text>
            </div>
          </div>
          <div className="s-found2__icons" aria-hidden>
            {(['call', 'instructions', 'health-check', 'silent-mode', 'address-pin', 'help-center', 'share', 'phone-missed', 'whatsapp', 'account-data'] as const).map((n) => <Icon key={n} name={n} size={24} />)}
          </div>
        </div>
      </Band>

      <Band flip title={`${compCount} components, all live`} body="Every example is the real component from web/src, in light and dark, with props, tokens, the Compose equivalent and the rules for using it." to="/components" cta="Browse components">
        <div className="s-mini-grid">
          {previews.map((id) => {
            const d = componentDocs.find((x) => x.id === id);
            if (!d) return null;
            return (
              <Link key={id} to={`/components/${id}`} className="s-mini">
                <div className="s-mini__stage" aria-hidden {...inert}>{d.examples[0]?.render()}</div>
                <span>{d.name}</span>
              </Link>
            );
          })}
        </div>
      </Band>

      <Band title="Real screens to start from" body="Core screens composed only from system components, with every state of the Profile, Addresses, Instructions and Contact Info flows. Hover a screen to name its parts." to="/screens" cta="See screens and flows">
        <div className="s-phones-row">
          <Mini scale={0.5}><HomeScreen /></Mini>
          <Mini scale={0.5}><YouScreen summary="ready" /></Mini>
          <Mini scale={0.5}><InstructionsFlow initialActive={[instructionLibrary.loanEmi, instructionLibrary.difficult]} /></Mini>
        </div>
      </Band>

      <Band flip title="One source, two platforms" body="Values live in tokens/tokens.json. npm run tokens writes the CSS, the TypeScript and the Compose theme, so web and Android can’t drift apart." to="/start" cta="Set up web or Compose">
        <div className="s-pipeline s-pipeline--stack">
          <div className="s-pipe">
            <b>tokens/tokens.json</b>
            {'"text/action": { "light": "{brand.green}", "dark": "{brand.lime}" }'}
          </div>
          <div className="s-pipe"><b>CSS</b>--eq-text-action: #00B140;</div>
          <div className="s-pipe"><b>TypeScript</b>color.textAction</div>
          <div className="s-pipe"><b>Kotlin</b>EqualTheme.colors.textAction</div>
        </div>
      </Band>

      <section className="s-closer">
        <h2>Build the next Equal AI flow</h2>
        <p>Start from the foundations, pick the components, and wire screens the way the prototype does.</p>
        <div className="s-hero__ctas">
          <Link to="/start" className="s-btn s-btn--primary">Get started</Link>
          <Link to="/prototype" className="s-btn">Open the playground</Link>
        </div>
        <div className="s-closer__chips" aria-hidden {...inert}>
          <SmallActionChip label="Call back" emoji="📞" />
          <LargeActionChip label="Ask to reschedule" emoji="🗓️" />
        </div>
      </section>

      <footer className="s-foot2">
        <div className="s-foot2__brand">
          <EqualLogo height={22} />
          <p>The Equal AI design system. Tokens, components, screens and flows, rendered live from the source.</p>
        </div>
        <nav className="s-foot2__cols" aria-label="Footer">
          <div><b>Docs</b><Link to="/start">Getting started</Link><Link to="/foundations/colour">Colour</Link><Link to="/foundations/typography">Typography</Link><Link to="/foundations/icons">Icons</Link></div>
          <div><b>Components</b><Link to="/components">Overview</Link><Link to="/components/button">Button</Link><Link to="/components/chat-list-item">Chat list item</Link><Link to="/components/bottom-sheet">Bottom sheet</Link></div>
          <div><b>Screens</b><Link to="/screens">Overview</Link><Link to="/flows/instructions">Instructions</Link><Link to="/flows/addresses">Addresses</Link><Link to="/prototype">Playground</Link></div>
          <div><b>Reference</b><Link to="/patterns">Patterns</Link><Link to="/reference/conflicts">Spec conflicts</Link><Link to="/reference/assets">Image assets</Link></div>
        </nav>
      </footer>
    </div>
  );
}
