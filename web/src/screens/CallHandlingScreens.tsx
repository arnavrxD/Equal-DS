// Call Handling (Known Calls (new users) design): Your assistant → Call Handling on the You tab,
// and the setup a new user is invited into from home. Two questions: which calls the assistant
// answers, then who always rings you. Copy is an experiment, so `copy` switches the wording.
import { useState } from 'react';
import {
  AlphabetIndex,
  AppBar,
  Asset,
  Button,
  CallNotification,
  CallQuestion,
  ChoiceCard,
  ContactCheckRow,
  ContactPick,
  DeviceScreen,
  InfoNote,
  PermissionSheet,
  radioGroupKeyDown,
  Screen,
  SearchField,
  SetupDone,
  Text,
  type CallNotificationState,
} from '../index';

export type CallMode = 'all' | 'unknown';
export type CallHandlingCopy = 'which' | 'whose';

export type CallHandlingStep = 'question' | 'perm-contacts' | 'perm-history' | 'exclude' | 'perfect' | 'sorted';

const COPY = {
  which: {
    title: 'Who should Equal answer for?',
    subtitle: 'You choose. Equal follows.',
    all: { title: 'All calls except', description: 'Equal answers all your calls, except the people you choose.' },
    unknown: { title: 'Unknown numbers only', description: 'Equal only answers calls from numbers you don’t have saved.' },
    note: 'You can change this anytime.',
    cta: 'Next',
  },
  whose: {
    title: 'Whose calls should I answer?',
    subtitle: 'I’ll pick up when you can’t, and tell you what they wanted.',
    all: { title: 'All calls except', description: 'Unknown numbers, and people you know when you don’t pick up' },
    unknown: { title: 'Only unknown numbers', description: 'Numbers not saved in your phone. Your contacts always ring you.' },
    note: 'This isn’t permanent — you can change it anytime.',
    cta: 'Proceed',
  },
} as const;

export type PickContact = { id: string; initials: string; name: string; phone: string };

export const suggestedContacts: PickContact[] = [
  { id: 'mom', initials: 'SU', name: 'Mom', phone: '+91 8986461540' },
  { id: 'pappa', initials: 'PA', name: 'Pappa', phone: '+91 8986461540' },
  { id: 'vijay', initials: 'VC', name: 'Vijay Cousin', phone: '+91 8986461540' },
  { id: 'arnav', initials: 'AR', name: 'Arnav Raj', phone: '+91 8986461540' },
];
export const allContacts: PickContact[] = [
  { id: 'arnav', initials: 'AR', name: 'Arnav Raj', phone: '+91 8986461540' },
  { id: 'shubham', initials: 'SS', name: 'Shubham Singh', phone: '+91 8986461540' },
  { id: 'aditya', initials: 'AD', name: 'Aditya Rao', phone: '+91 8986461540' },
];

export type CallHandlingScreenProps = {
  /** Pin a step (docs). Otherwise it runs: question → (permissions) → exclude → Perfect!, or → All Sorted!. */
  step?: CallHandlingStep;
  copy?: CallHandlingCopy;
  /** Pre-selected answer. */
  mode?: CallMode;
  /** Contacts permission already granted (otherwise "All calls" asks for it, and `whose` copy shows the note). */
  contactsAllowed?: boolean;
  /** Pre-picked contacts on the exclude step. */
  picked?: string[];
  onBack?: () => void;
  onDone?: () => void;
};

export function CallHandlingScreen({ step, copy = 'which', mode: initialMode = 'all', contactsAllowed = true, picked: initialPicked = ['arnav', 'shubham'], onBack, onDone }: CallHandlingScreenProps) {
  const [live, setLive] = useState<CallHandlingStep>('question');
  const [mode, setMode] = useState<CallMode>(initialMode);
  const [allowed, setAllowed] = useState(contactsAllowed);
  const [picked, setPicked] = useState<string[]>(initialPicked);
  const s = step ?? live;
  const go = (n: CallHandlingStep) => step === undefined && setLive(n);
  const c = COPY[copy];
  const toggle = (id: string) => setPicked((p) => (p.includes(id) ? p.filter((x) => x !== id) : [...p, id]));

  if (s === 'perfect' || s === 'sorted') {
    const names = allContacts.filter((x) => picked.includes(x.id)).map((x) => x.name.split(' ')[0]);
    return (
      <Screen surface="primary">
        <SetupDone
          header={<AppBar statusBar title="" onBack={s === 'sorted' ? onBack ?? (() => go('question')) : undefined} surface="transparent" />}
          title={s === 'perfect' ? 'Perfect!' : 'You’re all set!'}
          body={s === 'perfect' ? <>Equal will handle the calls.{names.length ? ` ${names.slice(0, 3).join(', ')}${names.length > 3 ? ` and ${names.length - 3} more` : ''} will always ring you.` : ''}</> : <>Equal will answer unknown numbers only. Your contacts will ring you, like always.</>}
          onDone={onDone}
        />
      </Screen>
    );
  }

  if (s === 'exclude') {
    return (
      <Screen
        surface="primary"
        header={<AppBar statusBar title="Call Handling" onBack={() => go('question')} />}
        footer={
          <div style={{ padding: '12px 16px 20px', background: 'var(--eq-bg-surface)' }}>
            <Text as="div" variant="body1" color="secondary" align="center" style={{ marginBottom: 12 }}>You can change this anytime.</Text>
            <Button size="lg" block onClick={() => go('perfect')}>{picked.length ? `Select ${picked.length} contacts` : 'Select contacts'}</Button>
          </div>
        }
      >
        <CallQuestion art="call-handling-exclude" lottie="call-handling-contacts" title="Who should still ring you?" />
        <div style={{ padding: '16px 16px 0' }}><SearchField value="" placeholder="Search contacts..." /></div>
        <Text as="div" variant="subheading" className="eq-ch-section" style={{ fontWeight: 400 }}>Suggested</Text>
        <div className="eq-ch-picks">{suggestedContacts.map((x) => <ContactPick key={x.id} initials={x.initials} name={x.name} onClick={() => toggle(x.id)} />)}</div>
        <Text as="div" variant="subheading" className="eq-ch-section" style={{ fontWeight: 400 }}>All Contacts</Text>
        <div style={{ position: 'relative' }}>
          {allContacts.map((x) => <ContactCheckRow key={x.id} initials={x.initials} name={x.name} phone={x.phone} checked={picked.includes(x.id)} onToggle={() => toggle(x.id)} />)}
          <AlphabetIndex />
        </div>
      </Screen>
    );
  }

  const sheet =
    s === 'perm-contacts' ? (
      <PermissionSheet art="permission-contacts" title="Allow contacts permission" body="I need your contact list to know who is calling." cta="Allow" onAllow={() => (setAllowed(true), go('perm-history'))} onClose={() => go('question')} />
    ) : s === 'perm-history' ? (
      <PermissionSheet art="permission-call-history" title="Allow call logs permission" body="Your contact list can be long. Allow Equal AI to access your phone call logs to show the people you call the most, at the top." cta="Allow" onAllow={() => go('exclude')} onClose={() => go('exclude')} />
    ) : null;

  return (
    <Screen
      surface="primary"
      header={<AppBar statusBar title="Call Handling" onBack={onBack ?? (() => {})} />}
      footer={
        <div style={{ padding: '12px 16px 20px' }}>
          {!sheet && <Text as="div" variant="body1" color="secondary" align="center" style={{ marginBottom: 12 }}>{c.note}</Text>}
          <Button size="lg" block onClick={() => (mode === 'unknown' ? go('sorted') : allowed ? go('exclude') : go('perm-contacts'))}>{c.cta}</Button>
        </div>
      }
      overlay={sheet ? <div className="eq-status-sheet-layer"><div className="eq-status-sheet-layer__scrim" onClick={() => go('question')} />{sheet}</div> : undefined}
    >
      <CallQuestion art="call-handling-all" title={c.title} subtitle={c.subtitle} />
      <div role="radiogroup" aria-label={c.title} onKeyDown={radioGroupKeyDown} style={{ display: 'flex', flexDirection: 'column', gap: 16, padding: '24px 16px 16px' }}>
        <ChoiceCard icon="globe" title={c.all.title} description={c.all.description} suggested={copy === 'whose'} selected={mode === 'all'} disabled={copy === 'whose' && !allowed} onSelect={() => setMode('all')} tabIndex={mode === 'unknown' || (copy === 'whose' && !allowed) ? -1 : 0} />
        <ChoiceCard icon="call" title={c.unknown.title} description={c.unknown.description} selected={mode === 'unknown'} onSelect={() => setMode('unknown')} tabIndex={mode === 'unknown' || (copy === 'whose' && !allowed) ? 0 : -1} />
        {copy === 'whose' && !allowed && <InfoNote title="Contact access needed" body="Without it, the assistant cannot handle all calls." link="Give contacts permission" onLink={() => go('perm-contacts')} />}
      </div>
    </Screen>
  );
}

// ---------------------------------------------------------------- Always ring me (edit, from the You tab)

export function AlwaysRingScreen({ onBack, initial = ['arnav', 'shubham'] }: { onBack?: () => void; initial?: string[] }) {
  const [picked, setPicked] = useState<string[]>(initial);
  const chosen = suggestedContacts.slice(0, 3);
  const toggle = (id: string) => setPicked((p) => (p.includes(id) ? p.filter((x) => x !== id) : [...p, id]));
  const count = picked.length + chosen.length + 3;
  return (
    <Screen
      surface="primary"
      header={<AppBar statusBar title="Always ring me" onBack={onBack ?? (() => {})} />}
      footer={
        <div style={{ padding: '12px 16px 20px', background: 'var(--eq-bg-surface)' }}>
          <Text as="div" variant="body1" color="secondary" align="center" style={{ marginBottom: 12 }}>You can change this anytime.</Text>
          <Button size="lg" block onClick={onBack}>{count} people will ring you directly</Button>
        </div>
      }
    >
      <Text as="p" variant="title" color="secondary" align="center" style={{ fontWeight: 400, margin: 0 }}>I will never answer these people.</Text>
      <div style={{ padding: '16px 16px 0' }}><SearchField value="" placeholder="Search contacts..." /></div>
      <div className="eq-ch-picks" style={{ paddingTop: 16 }}>{chosen.map((x) => <ContactPick key={x.id} initials={x.initials} name={x.name} mark="remove" />)}</div>
      <Text as="div" variant="subheading" className="eq-ch-section" style={{ fontWeight: 400 }}>Suggested</Text>
      <div style={{ position: 'relative' }}>
        {allContacts.slice(0, 3).map((x) => <ContactCheckRow key={x.id} initials={x.initials} name={x.name} phone={x.phone} checked={picked.includes(x.id)} onToggle={() => toggle(x.id)} />)}
      </div>
      <Text as="div" variant="subheading" className="eq-ch-section" style={{ fontWeight: 400 }}>All Contacts</Text>
      <div style={{ position: 'relative' }}>
        {allContacts.map((x) => <ContactCheckRow key={`a-${x.id}`} initials={x.initials} name={x.name} phone={x.phone} checked={picked.includes(x.id)} onToggle={() => toggle(x.id)} />)}
        <AlphabetIndex />
      </div>
    </Screen>
  );
}

// ---------------------------------------------------------------- Direct Calls (no contacts)

export function DirectCallsEmptyScreen({ onBack, onDone }: { onBack?: () => void; onDone?: () => void }) {
  return (
    <Screen
      surface="primary"
      header={<AppBar statusBar title="Direct Calls" onBack={onBack ?? (() => {})} />}
      footer={
        <div style={{ padding: '12px 16px 20px', background: 'var(--eq-bg-surface)' }}>
          <Text as="div" variant="body1" color="secondary" align="center" style={{ marginBottom: 12 }}>Find this later under You, then Call Handling.</Text>
          <Button size="lg" block onClick={onDone}>Got it</Button>
        </div>
      }
    >
      <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 6, padding: '120px 28px 0' }}>
        <Asset name="illustration-no-contacts" size={140} alt="" />
        <Text as="h2" variant="headline" align="center" style={{ fontWeight: 700, marginTop: 16 }}>No contacts saved</Text>
        <Text as="p" variant="title" color="secondary" align="center" style={{ fontWeight: 400, margin: 0 }}>Save numbers in your phone first. Then tell me who should always ring you.</Text>
      </div>
    </Screen>
  );
}

// ---------------------------------------------------------------- Known-call notification (Known-call notifications design)

export function KnownCallNotificationScreen({ state }: { state: CallNotificationState }) {
  return (
    <DeviceScreen>
      <CallNotification name="Mom" state={state} />
    </DeviceScreen>
  );
}
