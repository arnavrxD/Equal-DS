// Flow screens — Profile (Personal Details, Silent Mode, WhatsApp Updates), Addresses, Instructions.
// Composed only from DS components. Source: the flow designs.
import { Fragment, useEffect, useRef, useState, type ReactNode } from 'react';
import {
  prefersReducedMotion,
  AddressCard,
  AddressForm,
  AppBar,
  BottomSheet,
  Button,
  ChatBubble,
  ChipRow,
  Composer,
  ConfirmSheetContent,
  EmptyState,
  FullSheet,
  GenderSelector,
  IllustrationHeader,
  InstructionCard,
  InstructionPreview,
  LargeActionChip,
  MessageList,
  PresetCard,
  PresetInstructionCard,
  RecommendedCard,
  RowGroup,
  Screen,
  SectionHeader,
  SettingsRow,
  SuggestionsComingCard,
  SystemNotification,
  Text,
  TextButton,
  TextField,
  ToggleRow,
  formatAddress,
  type Gender,
  type Instruction,
  type SavedAddress,
  type PlanTier,
} from '../index';
import { HomeScreen } from './Screens';
import { followUps, instructionLibrary, presets, recommendations, sampleAddresses, silentCategories, type Preset } from './flowData';

const header = (title: string, onBack?: () => void, trailing?: ReactNode) => <AppBar statusBar title={title} onBack={onBack} trailing={trailing} />;

// ================================================================ Profile flow

export function PersonalDetailsScreen({ name: initialName = 'Keshav Reddy', gender: initialGender = 'male', tier = 'paid', onBack, onChange }: {
  tier?: PlanTier; name?: string; gender?: Gender; onBack?: () => void; onChange?: (v: { name: string; gender: Gender }) => void }) {
  const [name, setName] = useState(initialName);
  const [gender, setGender] = useState<Gender>(initialGender);
  const first = name.trim().split(/\s+/)[0] || 'you';
  return (
    <Screen header={header('Personal Details', onBack)}>
      <div style={{ padding: '8px 20px 24px', display: 'flex', flexDirection: 'column', gap: 28 }}>
        <TextField
          label="Name"
          value={name}
          onChange={(v) => (setName(v), onChange?.({ name: v, gender }))}
          helper={<>Your AI will call you <b style={{ color: 'var(--eq-text-primary)', fontWeight: 500 }}>‘{first}’</b></>}
        />
        <div>
          <Text as="div" variant="overline" color="secondary" style={{ marginBottom: 10 }}>GENDER</Text>
          <GenderSelector value={gender} tier={tier} onChange={(g) => (setGender(g), onChange?.({ name, gender: g }))} />
        </div>
      </div>
    </Screen>
  );
}

export function SilentModeScreen({ onBack, title = 'Silent Mode' }: { onBack?: () => void; title?: string }) {
  const [muteAll, setMuteAll] = useState(true);
  const [cats, setCats] = useState<Record<string, boolean>>(Object.fromEntries(silentCategories.map((c) => [c.id, true])));
  return (
    <Screen header={header(title, onBack)}>
      <IllustrationHeader asset="illustration-silent-mode" />
      <RowGroup>
        <ToggleRow label="Mute Promotional Calls" wrap description="Equal AI Assistant can identify Promotional Calls and automatically mute them" checked={muteAll} onChange={setMuteAll} />
      </RowGroup>
      <SectionHeader>Categories</SectionHeader>
      <div style={{ opacity: muteAll ? 1 : 0.45, pointerEvents: muteAll ? 'auto' : 'none', transition: 'opacity var(--eq-duration-base)' }} aria-disabled={!muteAll}>
        <RowGroup>
          {silentCategories.map((c) => (
            <ToggleRow key={c.id} label={c.label} description={c.description} checked={muteAll && cats[c.id]} onChange={(v) => setCats((s) => ({ ...s, [c.id]: v }))} />
          ))}
        </RowGroup>
      </div>
      <div style={{ padding: '20px 0 24px' }}>
        <RowGroup>
          <SettingsRow icon="silent-mode" label="Muted Contacts" description="People you’ve muted" onClick={() => {}} />
        </RowGroup>
      </div>
    </Screen>
  );
}

export function WhatsAppUpdatesScreen({ onBack, initialOn = true, showSheet = false }: { onBack?: () => void; initialOn?: boolean; showSheet?: boolean }) {
  const [on, setOn] = useState(initialOn);
  const [confirm, setConfirm] = useState(showSheet);
  return (
    <Screen
      header={header('WhatsApp Updates', onBack)}
      overlay={
        <BottomSheet open={confirm} onClose={() => setConfirm(false)} label="Turn off WhatsApp updates">
          <ConfirmSheetContent
            illustration="illustration-whatsapp"
            title="Turn off WhatsApp updates?"
            body="Important updates and alerts will no longer reach you on WhatsApp"
            confirmLabel="Turn off updates"
            onConfirm={() => (setOn(false), setConfirm(false))}
          />
        </BottomSheet>
      }
    >
      <IllustrationHeader asset="illustration-whatsapp" />
      <RowGroup>
        <ToggleRow
          label="Get updates on WhatsApp"
          wrap
          description={
            <>
              We’ll share call alerts, account updates, and helpful tips right when you need them.
              <span style={{ display: 'block', marginTop: 12 }}>No spam. Only what matters.</span>
            </>
          }
          checked={on}
          onChange={(v) => (v ? setOn(true) : setConfirm(true))}
        />
      </RowGroup>
    </Screen>
  );
}

// ================================================================ Addresses flow

export function AddressesScreen({ onBack, initial = sampleAddresses, openEdit, confirmDelete }: { onBack?: () => void; initial?: SavedAddress[]; openEdit?: string | 'new'; confirmDelete?: string }) {
  const [list, setList] = useState(initial);
  const [editing, setEditing] = useState<string | 'new' | null>(openEdit ?? null);
  const [deleting, setDeleting] = useState<string | null>(confirmDelete ?? null);
  const current = editing && editing !== 'new' ? list.find((a) => a.id === editing) : undefined;
  return (
    <Screen
      header={header('Addresses', onBack)}
      footer={<div style={{ padding: 16 }}><Button block size="lg" leadingIcon="add" onClick={() => setEditing('new')}>Add new Address</Button></div>}
      overlay={
        <>
          <BottomSheet open={!!editing} onClose={() => setEditing(null)} title={editing === 'new' ? 'Add Address' : 'Edit Address'}>
            {editing && (
              <AddressForm
                key={editing}
                initial={current}
                onSave={(a) => {
                  setList((l) => (editing === 'new' ? [...l, { ...a, id: `a${Date.now()}` }] : l.map((x) => (x.id === editing ? { ...x, ...a } : x))));
                  setEditing(null);
                }}
              />
            )}
          </BottomSheet>
          <BottomSheet open={!!deleting} onClose={() => setDeleting(null)} label="Delete address">
            <ConfirmSheetContent
              title="Delete Address"
              body="Are you sure you want to delete this address? This action cannot be undone."
              confirmLabel="Delete"
              destructive
              onConfirm={() => (setList((l) => l.filter((a) => a.id !== deleting)), setDeleting(null))}
            />
          </BottomSheet>
        </>
      }
    >
      {list.length === 0 ? (
        <div style={{ paddingTop: 72 }}>
          <EmptyState illustration="illustration-addresses-empty" title="Never explain your address again" body="Save frequently used addresses, and your assistant will use them to handle delivery, pickup calls, etc. more naturally." />
        </div>
      ) : (
        <div style={{ padding: '8px 20px 16px', display: 'flex', flexDirection: 'column', gap: 12 }}>
          {list.map((a) => (
            <AddressCard key={a.id} label={a.label} address={formatAddress(a)} onEdit={() => setEditing(a.id)} onDelete={() => setDeleting(a.id)} />
          ))}
        </div>
      )}
    </Screen>
  );
}

// ================================================================ Instructions flow

type ChatMsg =
  | { kind: 'you'; text: string }
  | { kind: 'ai'; text: string }
  | { kind: 'preview'; instruction: Instruction; confirmed?: boolean };

type Setup = { instruction: Instruction; ask: string; needsInput: boolean };

/** Bold the {values} the user supplied inside the Do line. */
function fill(template: string, answers: Record<string, string>): ReactNode {
  const parts = template.split(/(\{\w+\})/);
  return parts.map((p, i) => {
    const m = /^\{(\w+)\}$/.exec(p);
    return m ? <b key={i} style={{ fontWeight: 500 }}>{answers[m[1]] ?? '…'}</b> : <Fragment key={i}>{p}</Fragment>;
  });
}
const plain = (template: string, answers: Record<string, string>) => template.replace(/\{(\w+)\}/g, (_, k) => answers[k] ?? '');

/**
 * "Setting up instruction" chat. No-input instructions go straight to the preview with Edit / Confirm.
 * Instructions with follow-ups ask each question first; the preview then offers Change something else / That's all.
 */
export function InstructionSetupSheet({ setup, open, onClose, onDone }: { setup: Setup | null; open: boolean; onClose: () => void; onDone: (i: Instruction) => void }) {
  const [msgs, setMsgs] = useState<ChatMsg[]>([]);
  const [answers, setAnswers] = useState<Record<string, string>>({});
  const [step, setStep] = useState(0);
  const [editing, setEditing] = useState(false);
  const end = useRef<HTMLDivElement>(null);
  const qs = setup ? followUps[setup.instruction.id] ?? [] : [];
  const asking = setup?.needsInput && step < qs.length;

  useEffect(() => {
    if (!setup || !open) return;
    setAnswers({});
    setStep(0);
    setEditing(false);
    const first: ChatMsg[] = [{ kind: 'you', text: setup.ask }];
    const fq = followUps[setup.instruction.id];
    setMsgs(setup.needsInput && fq?.length ? [...first, { kind: 'ai', text: fq[0].question }] : [...first, { kind: 'ai', text: 'Got it! Here’s your instruction:' }, { kind: 'preview', instruction: setup.instruction }]);
  }, [setup, open]);
  useEffect(() => {
    end.current?.scrollIntoView({ block: 'end', behavior: prefersReducedMotion() ? 'auto' : 'smooth' });
  }, [msgs]);

  const final = (a: Record<string, string>): Instruction => (setup ? { ...setup.instruction, do: plain(setup.instruction.do, a) } : instructionLibrary.declineSales);

  const send = (text: string) => {
    if (!setup) return;
    if (asking) {
      const q = qs[step];
      const next = { ...answers, [q.key]: text };
      setAnswers(next);
      const more = step + 1 < qs.length;
      setMsgs((m) => [...m, { kind: 'you', text }, ...(more ? [{ kind: 'ai' as const, text: qs[step + 1].question }] : [{ kind: 'ai' as const, text: 'Got it! Here’s your instruction:' }, { kind: 'preview' as const, instruction: setup.instruction }])]);
      setStep(step + 1);
      return;
    }
    // Free-text edit of the instruction
    setMsgs((m) => [...m, { kind: 'you', text }, { kind: 'ai', text: 'Got it! Here’s your instruction:' }, { kind: 'preview', instruction: { ...setup.instruction, do: text } }]);
    setEditing(false);
  };

  const confirm = () => {
    const last = [...msgs].reverse().find((m) => m.kind === 'preview') as Extract<ChatMsg, { kind: 'preview' }> | undefined;
    setMsgs((m) => [...m.map((x) => (x === last ? { ...x, confirmed: true } : x)), { kind: 'you', text: setup?.needsInput ? 'That’s all' : 'Confirm' }]);
    const done = last ? { ...last.instruction, do: plain(last.instruction.do, answers) } : final(answers);
    setTimeout(() => onDone(done), 700);
  };

  const askEdit = () => {
    setEditing(true);
    setMsgs((m) => [...m, { kind: 'you', text: setup?.needsInput ? 'Change something else' : 'Edit' }, { kind: 'ai', text: 'What would you like to update in this?' }]);
  };

  const chips = asking ? null : editing ? null : setup?.needsInput ? (
    <>
      <LargeActionChip emoji="✏️" label="Change something else" state="read" onClick={askEdit} />
      <LargeActionChip emoji="👌" label="That's all" onClick={confirm} />
    </>
  ) : (
    <>
      <LargeActionChip emoji="✍️" label="Edit" state="read" onClick={askEdit} />
      <LargeActionChip emoji="👍" label="Confirm" onClick={confirm} />
    </>
  );

  return (
    <FullSheet
      open={open}
      onClose={onClose}
      title="Setting up instruction"
      footer={
        <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
          {chips && <ChipRow gap={10}>{chips}</ChipRow>}
          <Composer onSend={send} placeholder={asking ? 'Type your answer…' : 'Type a message...'} />
        </div>
      }
    >
      <MessageList>
        {msgs.map((m, i) =>
          m.kind === 'you' ? (
            <ChatBubble key={i} kind="outgoing" sender="You" time="03:21">{m.text}</ChatBubble>
          ) : m.kind === 'ai' ? (
            <ChatBubble key={i} kind="assistant" sender="Equal AI" time="03:21">{m.text}</ChatBubble>
          ) : (
            <div key={i} style={{ paddingLeft: 36, marginBottom: m.confirmed ? 16 : 0 }}>
              <InstructionPreview title={m.instruction.title} when={m.instruction.when} action={fill(m.instruction.do, answers)} time="03:21" confirmed={m.confirmed} />
            </div>
          ),
        )}
        <div ref={end} />
      </MessageList>
    </FullSheet>
  );
}

export type InstructionsView = 'hub' | 'active' | { preset: string };

/** The whole Instructions flow: hub → preset → apply (setup chat), and Active Instructions with edit / delete. */
export function InstructionsFlow({ onBack, initialActive = [instructionLibrary.declineSales, instructionLibrary.loanEmi, instructionLibrary.difficult, instructionLibrary.takeMessage], initialView = 'hub', noRecommendations = false }: { onBack?: () => void; initialActive?: Instruction[]; initialView?: InstructionsView; noRecommendations?: boolean }) {
  const [view, setView] = useState<InstructionsView>(initialView);
  const [active, setActive] = useState<Instruction[]>(initialActive);
  const [setup, setSetup] = useState<Setup | null>(null);
  const [toast, setToast] = useState<string | null>(null);
  const isActive = (id: string) => active.some((a) => a.id === id);
  const recs = noRecommendations ? [] : recommendations.filter((r) => !isActive(r.instruction.id));

  const startSetup = (instruction: Instruction, ask = instruction.title) => setSetup({ instruction, ask, needsInput: !!followUps[instruction.id] });
  const flash = (t: string) => (setToast(t), setTimeout(() => setToast(null), 2000));

  const sheet = (
    <InstructionSetupSheet
      setup={setup}
      open={!!setup}
      onClose={() => setSetup(null)}
      onDone={(i) => {
        setActive((a) => [...a.filter((x) => x.id !== i.id), i]);
        setSetup(null);
        flash('Instruction added');
      }}
    />
  );
  const overlay = (
    <>
      {sheet}
      {toast && (
        <div style={{ position: 'absolute', left: 16, right: 16, bottom: 96, zIndex: 70 }} role="status">
          <div style={{ padding: '12px 16px', borderRadius: 12, background: 'var(--eq-text-primary)' }}>
            <span className="eq-t-body1" style={{ color: 'var(--eq-bg-surface)' }}>{toast}</span>
          </div>
        </div>
      )}
    </>
  );
  const addButton = <div style={{ padding: 16 }}><Button block size="lg" leadingIcon="add" onClick={() => startSetup({ id: `custom-${Date.now()}`, title: 'New instruction', when: 'Calls like this come in', do: 'Tell me what to do', source: undefined }, 'Add a new instruction')}>Add New Instruction</Button></div>;

  if (typeof view === 'object') {
    const p = presets.find((x) => x.id === view.preset) as Preset;
    return (
      <Screen header={header(p.title, () => setView('hub'))} overlay={overlay}>
        <Text as="p" variant="body1" color="secondary" style={{ padding: '0 20px 12px' }}>{p.description}</Text>
        <div style={{ padding: '0 20px 24px', display: 'flex', flexDirection: 'column', gap: 12 }}>
          {p.instructions.map((ins) => (
            <PresetInstructionCard key={ins.id} instruction={ins} applied={isActive(ins.id)} onApply={() => startSetup({ ...ins, source: { emoji: p.emoji, label: p.title } })} />
          ))}
        </div>
      </Screen>
    );
  }

  if (view === 'active') {
    return (
      <Screen
        header={header('Active Instructions', () => setView('hub'), active.length ? <TextButton leadingIcon="add" onClick={() => startSetup({ id: `custom-${Date.now()}`, title: 'New instruction', when: 'Calls like this come in', do: 'Tell me what to do' }, 'Add a new instruction')}>Add</TextButton> : undefined)}
        footer={active.length ? undefined : addButton}
        overlay={overlay}
      >
        {active.length === 0 ? (
          <div style={{ paddingTop: 96 }}>
            <EmptyState illustration="illustration-instructions-empty" title="No instructions yet" body="Create your first instruction to tell your assistant how to handle calls" />
          </div>
        ) : (
          <>
            <SectionHeader>{`Active instructions - ${active.length}`}</SectionHeader>
            <div style={{ padding: '0 20px 24px', display: 'flex', flexDirection: 'column', gap: 12 }}>
              {active.map((ins) => (
                <InstructionCard key={ins.id} instruction={ins} onEdit={() => startSetup(ins, `Edit: ${ins.title}`)} onDelete={() => (setActive((a) => a.filter((x) => x.id !== ins.id)), flash('Instruction deleted'))} />
              ))}
            </div>
          </>
        )}
      </Screen>
    );
  }

  return (
    <Screen header={header('Instructions', onBack)} footer={addButton} overlay={overlay}>
      <SectionHeader>Recommended for you</SectionHeader>
      {recs.length ? (
        <div className="eq-hscroll" style={{ gap: 10, padding: '0 20px 4px' }}>
          {recs.map((r) => (
            <RecommendedCard key={r.id} emoji={r.emoji} title={r.title} description={r.description} onAdd={() => startSetup(r.instruction, r.ask)} />
          ))}
          <span style={{ width: 10, flexShrink: 0 }} />
        </div>
      ) : (
        <div style={{ padding: '0 20px' }}>
          <SuggestionsComingCard />
        </div>
      )}
      <SectionHeader>Your instructions</SectionHeader>
      <RowGroup>
        <SettingsRow icon="instructions" iconBg="var(--eq-preset-tint-lime)" label="Active Instructions" description={active.length ? `${active.length} Active` : 'Manage how your assistant handles calls'} onClick={() => setView('active')} />
      </RowGroup>
      <SectionHeader>Browse presets</SectionHeader>
      <div style={{ padding: '0 20px 24px', display: 'flex', flexDirection: 'column', gap: 12 }}>
        {presets.map((p) => (
          <PresetCard key={p.id} emoji={p.emoji} tint={p.tint} title={p.title} description={p.description} onClick={() => setView({ preset: p.id })} />
        ))}
      </div>
    </Screen>
  );
}

/** Static render of the setup chat at a given state — for docs and design review. */
export function InstructionSetupDemo({ which = 'decline' }: { which?: 'decline' | 'delivery' }) {
  const setup: Setup = which === 'decline' ? { instruction: recommendations[0].instruction, ask: recommendations[0].ask, needsInput: false } : { instruction: instructionLibrary.delivery, ask: 'Handle delivery and courier calls', needsInput: true };
  return (
    <Screen header={header('Instructions')}>
      <div />
      <InstructionSetupSheet setup={setup} open onClose={() => {}} onDone={() => {}} />
    </Screen>
  );
}

// ---------------------------------------------------------------- Temporarily Unavailable (Temporarily Unavailable design)

export type SystemStatusStage = 'unavailable' | 'notified' | 'back-on';

/**
 * Chats while Equal AI is down, then back. `unavailable` → tap "Notify me when ready" → `notified`;
 * when the system recovers, a heads-up notification lands and Meet Your Assistant replaces the promo.
 * Pass `stage` to pin a state; leave it out to run the flow (the recovery fires `recoverAfter` ms after notify).
 */
export function SystemStatusFlow({ stage, recoverAfter = 2500 }: { stage?: SystemStatusStage; recoverAfter?: number }) {
  const [live, setLive] = useState<SystemStatusStage>('unavailable');
  const current = stage ?? live;
  const [banner, setBanner] = useState(true);
  useEffect(() => {
    if (stage || live !== 'notified') return;
    const t = setTimeout(() => (setLive('back-on'), setBanner(true)), recoverAfter);
    return () => clearTimeout(t);
  }, [stage, live, recoverAfter]);
  useEffect(() => {
    if (stage || live !== 'back-on') return;
    const t = setTimeout(() => setBanner(false), 4000);
    return () => clearTimeout(t);
  }, [stage, live]);
  const showBanner = current === 'back-on' && (stage ? true : banner);
  return (
    <div style={{ position: 'relative', height: '100%' }}>
      <HomeScreen
        key={current === 'back-on' ? 'on' : 'off'}
        systemStatus={current === 'back-on' ? 'ok' : current}
        onNotify={() => setLive('notified')}
        promo="meet-assistant"
      />
      {showBanner && (
        <div className="eq-heads-up" style={{ position: 'absolute', top: 30, left: 6, right: 6, zIndex: 20 }}>
          <SystemNotification title="Equal AI Assistant is back on" body="Equal AI is now handling all your unknown calls." />
        </div>
      )}
    </div>
  );
}
