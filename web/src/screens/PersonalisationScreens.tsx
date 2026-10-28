// AI Personalisation (About Me / Equal Noticed design): the Home FYI sheet, the You tab card, the
// global AI Personalization screen, the caller context in Contact Info, and the stop / delete sheets.
import { useState, type ReactNode } from 'react';
import {
  AppBar, Asset, AvatarStack, BottomSheet, Button, CallerContextCard, CallHistoryList, CallHistoryRow, CarouselDots, ChatPreviewCard, ContactInfoHeader,
  EqualOrb, Icon, InfoNote, InfoRows, KeepLoseList, RowGroup, Screen, SectionHeader, SettingsRow, Text, TraitOrbHero, type CallerContextState,
} from '../index';
import { HomeScreen, YouScreen } from './Screens';

/** Home with the "Equal is now getting to know your callers" FYI sheet. */
export function PersonalisationFyiHome({ open: initial = true }: { open?: boolean }) {
  const [open, setOpen] = useState(initial);
  return (
    <HomeScreen
      showWhatsNew={false}
      overlay={open ? (
        <BottomSheet open label="Your Equal experience is now more personal." onClose={() => setOpen(false)}>
          <TraitOrbHero />
          <Text as="h2" variant="headline" align="center" style={{ fontWeight: 700 }}>Your Equal experience is now more personal.</Text>
          <Text as="p" variant="body1" color="secondary" align="center" style={{ margin: '8px 0 16px' }}>Equal learns from your calls, while you choose what shapes your future calls.</Text>
          <InfoRows rows={[
            { icon: 'auto-fix', title: 'It builds up over time', body: 'It gets a little sharper with every call. Nothing changes overnight.' },
            { icon: 'privacy', title: 'Ignores sensitive topics', body: 'Health, finances, legal, family, religion never used.' },
            { icon: 'silent-mode', title: 'Off for anyone you choose', body: 'Switch it off from a caller’s profile, or in Settings.' },
          ]} />
          <div style={{ marginTop: 20 }}><Button size="lg" block onClick={() => setOpen(false)}>Got it</Button></div>
        </BottomSheet>
      ) : undefined}
    />
  );
}

const pickups = [
  { emoji: '📍', title: 'Your places', body: 'Deliveries, pickups and more, without you repeating yourself.', lines: [{ from: 'caller' as const, text: 'I am at the main gate. Which tower?' }, { from: 'assistant' as const, text: 'Come in from Gate 2, Tower B. Take service lift, not the main one' }, { from: 'caller' as const, text: 'Got it.' }] },
  { emoji: '👪', title: 'Your people', body: 'Calls handled differently for family, friends, colleagues and more.', lines: [{ from: 'caller' as const, text: 'Is he free? Papa’s appointment got moved to 4.' }, { from: 'assistant' as const, text: 'He’s in a meeting. Tell me and I’ll get it to him now. Which hospital?' }, { from: 'caller' as const, text: 'Fortis, Bannerghatta.' }] },
  { emoji: '🔔', title: 'Your preferences', body: 'The calls you want to hear, and the ones you don’t, handled your way.', lines: [{ from: 'caller' as const, text: 'Calling from your bank regarding your id verification.' }, { from: 'assistant' as const, text: 'Got it. Please hold on while I connect you to him.' }, { from: 'caller' as const, text: 'Sure.' }] },
];

function DeleteSheet({ caller, onClose }: { caller?: string; onClose: () => void }) {
  const first = caller?.split(' ')[0];
  return (
    <BottomSheet open label={caller ? `Reset what Equal learned about ${first}?` : 'Reset everything Equal has learned?'} onClose={onClose}>
      <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', textAlign: 'center' }}>
        <Asset name="illustration-trash" size={72} alt="" />
        <Text as="h2" variant="title" style={{ fontWeight: 700, marginTop: 16 }}>{caller ? `Reset what Equal learned about ${first}?` : 'Reset everything Equal has learned?'}</Text>
        <Text as="p" variant="body1" color="secondary" style={{ margin: '8px 0 16px' }}>{caller ? 'Clear what Equal has learned so far and start fresh with future calls.' : 'Clear what Equal has learned about all your callers and about you, and start fresh with future calls.'}</Text>
      </div>
      {caller
        ? <KeepLoseList stays={['Who’s calling and their number']} goes={['Why they usually call', 'How you’d want the call answered', 'Which language to answer in']} />
        : <KeepLoseList stays={['Your callers and their numbers', 'Who you’ve excluded from personalisation']} goes={['What Equal learned about each caller', 'What Equal learned about you']} />}
      <Text as="p" variant="bodyLarge" color="secondary" style={{ margin: '16px 0 20px' }}>{caller ? 'Equal can start learning about them again if learning is turned on.' : 'Equal can start learning again from your next calls.'}</Text>
      <Button size="lg" block appearance="critical" leadingIcon="delete" onClick={onClose}>{caller ? 'Reset learning' : 'Reset all learnings'}</Button>
    </BottomSheet>
  );
}

/** AI Personalization: what Equal picks up (carousel), what it skips, excluded contacts and Delete all learning. */
export function PersonalisationScreen({ sheet: initial = false, onBack }: { sheet?: boolean; onBack?: () => void }) {
  const [sheet, setSheet] = useState(initial);
  const [slide, setSlide] = useState(0);
  return (
    <Screen header={<AppBar statusBar title="Your Equal Experience" onBack={onBack ?? (() => {})} />} overlay={sheet ? <DeleteSheet onClose={() => setSheet(false)} /> : undefined}>
      <div style={{ padding: '16px 16px 0' }}>
        <Text as="h1" variant="headline" style={{ fontWeight: 700 }}>Your <Text as="span" variant="inherit" color="action">Equal</Text> experience, made more personal.</Text>
        <Text as="p" variant="bodyLarge" color="secondary" style={{ margin: '8px 0 0' }}>Equal learns from all your calls, with you choosing what shapes future calls.</Text>
      </div>
      <div style={{ marginTop: 24 }}><SectionHeader inset={16}>What becomes more personal</SectionHeader></div>
      <div className="eq-learning-carousel" style={{ marginTop: 8 }} onScroll={(e) => setSlide(Math.round(e.currentTarget.scrollLeft / 324))}>
        {pickups.map((p) => (
          <article key={p.title} className="eq-learning-card is-green" style={{ width: 312, background: 'var(--eq-bg-surface)' }}>
            <span className="eq-learning-card__tile" aria-hidden style={{ width: 40, height: 40, borderRadius: 12, background: 'var(--eq-bg-primary)' }}><Text variant="title" color="none">{p.emoji}</Text></span>
            <Text as="h3" variant="body1" style={{ fontWeight: 600, marginTop: 8 }}>{p.title}</Text>
            <Text as="p" variant="caption" color="secondary" style={{ margin: '2px 0 12px', fontWeight: 400 }}>{p.body}</Text>
            <ChatPreviewCard lines={p.lines} />
          </article>
        ))}
      </div>
      <CarouselDots count={pickups.length} active={slide} />
      <div style={{ padding: '16px 16px 0' }}>
        <InfoNote title="Sensitive details" body="Equal doesn’t learn from sensitive details like OTPs, card numbers, UPI PINs and government IDs." />
      </div>
      <div style={{ marginTop: 24 }}><SectionHeader inset={16}>What Equal doesn’t learn</SectionHeader></div>
      <RowGroup inset={16}>
        <SettingsRow
          icon="person"
          wrap
          label="Contacts you exclude"
          description={<>Equal still answers their calls, but won’t use your interactions with them to personalise future calls.<span style={{ display: 'flex', alignItems: 'center', gap: 8, marginTop: 8 }}><AvatarStack initials={['AS', 'AM', 'BC']} /><Text variant="caption">Aarav, Anjali, Brian</Text></span></>}
        />
      </RowGroup>
      <div style={{ marginTop: 24 }}><SectionHeader inset={16}>Want to start fresh?</SectionHeader></div>
      <RowGroup inset={16}>
        <SettingsRow icon="delete" iconBg="var(--eq-expiry-chip-danger-bg)" label="Reset all learnings" description="Start fresh with future calls." onClick={() => setSheet(true)} />
      </RowGroup>
      <div style={{ height: 32 }} />
    </Screen>
  );
}

/** You → AI Personalisation → the screen, live. */
export function PersonalisationFlow() {
  const [page, setPage] = useState<'you' | 'screen'>('you');
  return page === 'you' ? <YouScreen noticed="ready" onPersonalisation={() => setPage('screen')} /> : <PersonalisationScreen onBack={() => setPage('you')} />;
}

const AMIT = 'Amit manages your flat in Koramangala. He calls about rent and maintenance, almost always after 7 PM, and prefers a quick call to a message. He speaks Hindi with you but switches to English for anything involving paperwork, and he likes a heads-up a day before any visit.';

function StopSheet({ name, onStop, onClose }: { name: string; onStop: () => void; onClose: () => void }) {
  return (
    <BottomSheet open label={`Stop learning about ${name}?`} onClose={onClose}>
      <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', textAlign: 'center' }}>
        <span style={{ position: 'relative', display: 'inline-flex' }}><EqualOrb size={48} label="" /><span style={{ position: 'absolute', inset: 0, display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'var(--eq-text-on-action)' }}><Icon name="stop-circle" size={24} /></span></span>
        <Text as="h2" variant="title" style={{ fontWeight: 700, marginTop: 12 }}>Stop learning about {name}?</Text>
        <Text as="p" variant="bodyLarge" color="secondary" style={{ margin: '8px 0 16px' }}>Equal keeps what it’s learned, but won’t learn from future calls.</Text>
      </div>
      <KeepLoseList stays={['Everything Equal knows today']} goes={['New information from future calls']} />
      <Text as="p" variant="bodyLarge" color="secondary" style={{ margin: '16px 0 20px' }}>You can turn this back on anytime.</Text>
      <Button size="lg" block leadingIcon="stop-circle" onClick={onStop}>Stop learning</Button>
    </BottomSheet>
  );
}

/** Contact Info with the caller context card; its menu opens the stop and delete sheets. */
export function CallerContextScreen({ state: initial = 'filled', sheet: initialSheet }: { state?: CallerContextState; sheet?: 'stop' | 'delete' }) {
  const [state, setState] = useState<CallerContextState>(initial);
  const [sheet, setSheet] = useState(initialSheet);
  const name = 'Amit Nanda';
  const overlay: ReactNode = sheet === 'stop'
    ? <StopSheet name={name} onStop={() => { setState('paused'); setSheet(undefined); }} onClose={() => setSheet(undefined)} />
    : sheet === 'delete' ? <DeleteSheet caller={name} onClose={() => setSheet(undefined)} /> : undefined;
  return (
    <Screen header={<AppBar statusBar title="Contact Info" onBack={() => {}} />} overlay={overlay}>
      <ContactInfoHeader
        name={name}
        phone="+91 9871746113"
        avatar={{ initials: 'AN' }}
        actions={[{ icon: 'call', label: 'Call' }, { icon: 'silent-mode', label: 'Mute' }, { icon: 'person-add', label: 'Save' }, { icon: 'share', label: 'Share' }]}
      />
      <div style={{ padding: '0 16px 16px' }}>
        <CallerContextCard name={name} state={state} summary={AMIT} onStop={() => setSheet('stop')} onDelete={() => setSheet('delete')} onResume={() => setState('filled')} />
      </div>
      <SectionHeader size="label" inset={22}>Call history</SectionHeader>
      <CallHistoryList>
        <CallHistoryRow direction="incoming" meta="Today • 30 Sec." time="02:30 PM" />
        <CallHistoryRow direction="missed" meta="Yesterday • 0 Sec." time="02:30 PM" />
      </CallHistoryList>
      <div style={{ height: 24 }} />
    </Screen>
  );
}
