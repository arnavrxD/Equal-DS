// Updated UI: Call Learning & Consent, Account & Data, and the Instructions promo + its new-feature sheet.
import { useState } from 'react';
import { AppBar, Asset, BottomSheet, Button, CarouselDots, FeatureIntroSheet, InstructionCard, LearningCard, RowGroup, Screen, SectionHeader, SettingsRow, Text, ToggleRow, TrustStrip, learningCards, trustStats } from '../index';
import { HomeScreen } from './Screens';

/** Call Learning & Consent: the learning toggle, what it learns (carousel), and the turn-off sheet. */
export function CallLearningScreen({ on: initial = true, sheet: sheetOpen = false, onBack }: { on?: boolean; sheet?: boolean; onBack?: () => void }) {
  const [on, setOn] = useState(initial);
  const [sheet, setSheet] = useState(sheetOpen);
  const [slide, setSlide] = useState(0);
  return (
    <Screen
      header={<AppBar statusBar title="Call Learning & Consent" align="start" onBack={onBack ?? (() => {})} />}
      overlay={sheet ? (
        <BottomSheet open label="Turn off Call Learning?" onClose={() => setSheet(false)}>
          <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', textAlign: 'center' }}>
            <Asset name="mascot-robot" size={96} alt="" />
            <Text as="h2" variant="title" style={{ fontWeight: 600, marginTop: 12 }}>Turn off Call Learning?</Text>
            <Text as="p" variant="body1" color="secondary" style={{ margin: '8px 0 24px' }}>The assistant won’t learn your preferences, recognise frequent callers, or adapt to how you handle conversations.</Text>
            <Button size="lg" block onClick={() => { setOn(false); setSheet(false); }}>Turn off</Button>
          </div>
        </BottomSheet>
      ) : undefined}
    >
      <div style={{ padding: '16px 16px 0' }}>
        <div className="eq-card-surface" style={{ borderRadius: 16, background: 'var(--eq-bg-surface)', overflow: 'hidden' }}>
          <ToggleRow
            checked={on}
            onChange={(v) => (v ? setOn(true) : setSheet(true))}
            label="Allow AI to learn from calls"
            wrap
            description="When this is on, assistant pays attention to how your calls go so it can handle them more like you would, remembering your preferences, your addresses, and the people who matter. This is only ever used to make your experience better. Nothing is sold, and nothing is shared with anyone."
          />
        </div>
      </div>
      <div style={{ marginTop: 24 }}><SectionHeader inset={16}>YOUR ASSISTANT</SectionHeader></div>
      <div className="eq-learning-carousel" style={{ marginTop: 8 }} onScroll={(e) => setSlide(Math.round(e.currentTarget.scrollLeft / 332))}>
        {learningCards.map((c) => <LearningCard key={c.title} {...c} />)}
      </div>
      <CarouselDots count={learningCards.length} active={slide} />
    </Screen>
  );
}

/** Account & Data: the shield, the trust strip, Privacy & Security, Legal and Account rows. */
export function AccountDataScreen({ onBack, onCallLearning }: { onBack?: () => void; onCallLearning?: () => void }) {
  return (
    <Screen header={<AppBar statusBar title="Account & Data" align="start" onBack={onBack ?? (() => {})} />}>
      <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', padding: '16px 20px 0' }}>
        <Asset name="illustration-shield-lock" size={140} alt="" />
        <Text as="h1" variant="headline" align="center" style={{ fontWeight: 600, marginTop: 8 }}>Trust is the foundation of everything we do at <Text as="span" variant="inherit" color="action">Equal AI</Text></Text>
        <div style={{ alignSelf: 'stretch', marginTop: 16 }}><TrustStrip stats={trustStats} /></div>
      </div>
      <div style={{ marginTop: 24 }}><SectionHeader>PRIVACY & SECURITY</SectionHeader></div>
      <RowGroup>
        <SettingsRow icon="privacy" label="Privacy Policy" description="How your data is used and protected" />
        <SettingsRow icon="key" label="Call Learning & Consent" description="Manage data sharing" onClick={onCallLearning} />
      </RowGroup>
      <div style={{ marginTop: 24 }}><SectionHeader>LEGAL</SectionHeader></div>
      <RowGroup>
        <SettingsRow icon="article" label="Terms & Conditions" description="Understand the rules and terms" />
      </RowGroup>
      <div style={{ marginTop: 24 }}><SectionHeader>ACCOUNT</SectionHeader></div>
      <RowGroup>
        <SettingsRow icon="logout" label="Logout" description="Sign out of your account securely" />
        <SettingsRow icon="delete" iconBg="var(--eq-expiry-chip-danger-bg)" label="Delete account" description="Permanently remove your account and data" />
      </RowGroup>
      <div style={{ height: 32 }} />
    </Screen>
  );
}

/** Account & Data → Call Learning & Consent, live. */
export function AccountDataFlow() {
  const [page, setPage] = useState<'account' | 'learning'>('account');
  return page === 'account' ? <AccountDataScreen onCallLearning={() => setPage('learning')} /> : <CallLearningScreen onBack={() => setPage('account')} />;
}

const loanInstruction = { id: 'loan', title: 'Loan offers', when: 'I get a loan offer call', do: 'Say I’m not interested and ask them to remove my number' };

/** Home with the Teach Your Assistant promo; Try Instructions opens the new-feature sheet. */
export function InstructionsPromoHome({ sheet: initial = false }: { sheet?: boolean }) {
  const [sheet, setSheet] = useState(initial);
  return (
    <HomeScreen
      promo="instructions"
      onPromo={() => setSheet(true)}
      overlay={sheet ? (
        <FeatureIntroSheet
          hero={<div style={{ width: '100%', maxWidth: 320 }}><InstructionCard instruction={loanInstruction} /></div>}
          title="Get every call handled your way with Instructions"
          bullets={[
            { icon: 'edit', text: 'Tell your assistant what to do on any call, in your own words' },
            { icon: 'touch', text: 'Start in one tap with presets for spam, loan and delivery calls' },
            { icon: 'call-log', text: 'See which instruction was used in every call summary' },
          ]}
          cta="Try Instructions"
          secondary="Not now"
          onCta={() => setSheet(false)}
          onSecondary={() => setSheet(false)}
          onClose={() => setSheet(false)}
        />
      ) : undefined}
    />
  );
}
