import { PhoneFrame } from '@equal/ds';
import { EqualPrototype } from '../../../web/src/screens/Prototype';
import { Page } from '../ui/kit';

const tryThis = [
  'Tap a chat to open it; the header’s ⋮ opens Contact info.',
  'Long-press (or right-click) a chat to select it → trash → confirm sheet → Undo.',
  'Switch to You: the edit pencil opens Personal Details, and choosing Female changes your avatar everywhere.',
  'You → Instructions: apply a preset or a recommendation and finish the setup chat.',
  'You → Assistant Health Check: Start Health Check, grant the permissions, fix the carrier, Back To Home.',
  'You → Account Data → Call Learning & Consent (turning learning off asks first).',
  'You → Silent Mode, Your Addresses and WhatsApp Updates.',
  'The search icon on Chats filters by name.',
];

export default function Prototype() {
  return (
    <Page title="Clickable prototype" lead="The whole app wired together with a simple route stack, sheets and toasts: the pattern to copy when you build a new flow." toc={false}>
      <div style={{ display: 'grid', gridTemplateColumns: 'minmax(0, 380px) minmax(0, 1fr)', gap: 48, marginTop: 36, alignItems: 'start' }} className="s-proto">
        <div style={{ position: 'sticky', top: 'calc(var(--site-top) + 24px)' }}>
          <PhoneFrame>
            <EqualPrototype />
          </PhoneFrame>
        </div>
        <div>
          <h2 className="s-h3" style={{ marginTop: 0 }}>Things to try</h2>
          <ol className="s-steps">{tryThis.map((t) => <li key={t}><span>{t}</span></li>)}</ol>
          <h2 className="s-h3">Building a new flow</h2>
          <ol className="s-steps">
            <li><span>Compose each screen from DS components in <code>web/src/screens/</code>, the way <code>Screens.tsx</code> and <code>FlowScreens.tsx</code> do.</span></li>
            <li><span>Wire navigation like <code>Prototype.tsx</code>: a route stack, with sheets and toasts as overlays inside the phone frame.</span></li>
            <li><span>Add a page to this site in <code>site/src/pages/Flow.tsx</code> so every state is reviewable in light and dark.</span></li>
          </ol>
        </div>
      </div>
    </Page>
  );
}
