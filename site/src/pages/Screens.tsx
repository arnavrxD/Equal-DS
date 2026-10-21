import { useState } from 'react';
import { ChatScreen, ContactInfoScreen, HomeScreen, SearchScreen, YouScreen } from '../../../web/src/screens/Screens';
import { sampleChats } from '../../../web/src/screens/data';
import { Link } from '../app/router';
import { Page } from '../ui/kit';
import { Icon } from '@equal/ds';

const flows = [
  { id: 'profile', name: 'Profile', body: 'Personal details, gender, Silent Mode and WhatsApp updates.' },
  { id: 'addresses', name: 'Addresses', body: 'Empty state, list, add, edit and delete with undo.' },
  { id: 'instructions', name: 'Instructions', body: 'Hub, presets, the setup chat and active instructions.' },
  { id: 'search', name: 'Search', body: 'Recent searches, chats then messages with the match in bold, and the empty, no internet and no results states.' },
  { id: 'call-back', name: 'Call Back', body: 'The AI layer suggests what to ask, hands off to the dialer and leaves an Outgoing call card.' },
  { id: 'mute', name: 'Mute', body: 'Confirm first, then the muted card with the muted caller list.' },
  { id: 'reply-whatsapp', name: 'Reply on WhatsApp', body: 'Draft, edit in place, send, and the confirmation card.' },
  { id: 'save-contact', name: 'Save Contact', body: 'Review, edit by voice or form, and every contacts-permission path.' },
  { id: 'error-states', name: 'Error states', body: 'Generation failure, offline in a task, voice input failure, offline sheet and screen.' },
  { id: 'global-mya', name: 'Global MYA', body: 'The assistant sheet from the green orb: greeting, quick actions, typing or a voice note, and the task it turns into.' },
  { id: 'call-handling', name: 'Call Handling', body: 'Which calls the assistant answers, who always rings you, the permissions it needs, and the new-user setup.' },
  { id: 'known-calls', name: 'Known-call notifications', body: 'The ongoing Android notification while the assistant answers someone you know, and the live chat.' },
  { id: 'contact-info', name: 'Contact info', body: 'Header actions, category, call history and blocking.' },
  { id: 'monetization', name: 'Monetization', body: 'Touchpoints, the Equal Unlimited paywall, payment results, plan and cancellation.' },
  { id: 'monetization-touchpoints', name: 'Monetization touchpoints', body: 'Plan expired and usage cap options, locked actions and recordings: the experiments that lead to the paywall.' },
  { id: 'paywall-sheet', name: 'Sheet paywall & payment', body: 'The paywall as a sheet, the exit offer, full-screen payment results and the processing state.' },
  { id: 'plan-renewal', name: 'Plan value & renewal', body: 'Expiry banners, the What Unlimited saved you sheet, the expired plan and renewing.' },
  { id: 'system-status', name: 'Temporarily unavailable', body: 'System status card, Notify me, and the back-on notification.' },
  { id: 'voip', name: 'Call your assistant (vOIP)', body: 'Call me, the mic permission, the in-app call with speaker / mute / end, and the phone-call fallback.' },
];
import { TraceStage, type StagePhone } from '../ui/TraceStage';
import { StageCode } from '../ui/StageCode';

const screens: StagePhone[] = [
  { key: 'home', label: 'Chats', render: () => <HomeScreen />, code: '<HomeScreen />' },
  { key: 'live', label: 'Chat, live call', render: () => <ChatScreen live />, code: '<ChatScreen live />' },
  { key: 'after', label: 'Chat, after the call', render: () => <ChatScreen chat={sampleChats[1]} />, code: '<ChatScreen chat={sampleChats[1]} />' },
  { key: 'you-no-plan', label: 'You, no plan (free, not subscribed)', render: () => <YouScreen plan="none" />, code: '<YouScreen plan="none" />', height: 1400 },
  { key: 'you', label: 'You, summary ready', render: () => <YouScreen summary="ready" />, code: '<YouScreen summary="ready" />', height: 1480 },
  { key: 'you-loading', label: 'You, summary loading', render: () => <YouScreen summary="loading" />, code: '<YouScreen summary="loading" />', height: 1480 },
  { key: 'you-empty', label: 'You, summary empty', render: () => <YouScreen summary="empty" avatar="female" />, code: '<YouScreen summary="empty" avatar="female" />', height: 1480 },
  { key: 'contact', label: 'Contact info', render: () => <ContactInfoScreen />, code: '<ContactInfoScreen />' },
  { key: 'search', label: 'Search', render: () => <SearchScreen />, code: '<SearchScreen />' },
];

export default function Screens() {
  const [mode, setMode] = useState<'trace' | 'try'>('trace');
  return (
    <Page title="Screens" lead="Every core screen of the app, composed only from design-system components. Flows have their own pages; the whole thing is wired together in the clickable prototype." toc={false}>
      <div style={{ display: 'flex', alignItems: 'center', gap: 14, margin: '28px 0 8px', flexWrap: 'wrap' }}>
        <div className="s-seg" role="group" aria-label="Stage mode">
          <button type="button" aria-pressed={mode === 'trace'} onClick={() => setMode('trace')}>Trace</button>
          <button type="button" aria-pressed={mode === 'try'} onClick={() => setMode('try')}>Try it</button>
        </div>
        <span style={{ fontSize: 13, color: 'var(--site-ink-3)' }}>
          Source: <code>web/src/screens/Screens.tsx</code>. Want the whole app? <Link to="/prototype" style={{ color: 'var(--site-accent)' }}>Open the prototype</Link>.
        </span>
      </div>
      <TraceStage phones={screens} mode={mode} scale={0.7} wrap />
      <h2 className="s-h2">Code</h2>
      <p className="s-p">Pick a screen to see how it’s built in React and in Jetpack Compose.</p>
      <StageCode phones={screens} group="screens" />
      <h2 className="s-h2">Flows</h2>
      <p className="s-p">Each flow page shows every state side by side, with the steps between them.</p>
      <div className="s-flowgrid">
        {flows.map((f) => (
          <Link key={f.id} to={`/flows/${f.id}`} className="s-flowcard">
            <b>{f.name}</b>
            <span>{f.body}</span>
            <Icon name="arrow-forward" size={18} />
          </Link>
        ))}
      </div>
    </Page>
  );
}
