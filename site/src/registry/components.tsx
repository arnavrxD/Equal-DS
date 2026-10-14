// Component registry for the Equal AI design-system docs site.
// One ComponentDoc per page. Every example renders the real @equal/ds components; `code` is the matching JSX.
import { useState, type CSSProperties, type ReactNode } from 'react';
import { PaywallPreview } from '../../../web/src/screens/MonetizationScreens';
import { ChatTaskSheet } from '../../../web/src/screens/AssistantSheet';
import { paywallFaq, testimonials } from '../../../web/src/screens/monetizationData';
import {
  AiTurn,
  MissedCallCard,
  HoldToSpeakBar,
  EditBubble,
  NewContactForm,
  OfflineSheet,
  OfflineScreen,
  SearchBar,
  SearchSection,
  RecentSearches,
  MessageResult,
  LoadingDots,
  SearchState,
  AssistantReplyCard,
  PaymentResult,
  ProcessingStrip,
  PaywallSheet,
  ExitOfferDialog,
  OfferStrip,
  HomeNoticeCard,
  UsageCapCard,
  AssistantNoticeCard,
  SummaryActions,
  LockedRecording,
  ChoiceCard,
  InfoNote,
  ContactPick,
  ContactCheckRow,
  PermissionSheet,
  CallNotification,
  OngoingCallBar,
  AssistantSheetShell,
  AssistantGreeting,
  AssistantActions,
  AssistantInput,
  VoiceRecorder,
  AssistantTaskHeader,
  AssistantThinking,
  AssistantReplies,
  CarouselDots,
  PlanExpiryBanner,
  ValueCard,
  QuotaCard,
  ValueSheet,
  ExpiryChip,
  FeatureList,
  DiscountBadge,
  Asset,
  ActionCard,
  AnimatedCrown,
  CancelSheet,
  Text,
  CouponTicket,
  DividerLabel,
  FaqList,
  HelpCenterSheet,
  LockBadge,
  Locked,
  PaymentStatusSheet,
  PaywallFooter,
  PaywallHero,
  PlanBanner,
  PriceTag,
  RatingPill,
  StatBlock,
  TestimonialCarousel,
  UnlockAudioPill,
  UnlockDetailsCard,
  Lottie,
  ActionCardBody,
  instructionLines,
  AddressCard,
  AddressForm,
  AiAvatar,
  AiChatPanel,
  AiSuggestion,
  AiSummaryCard,
  AppBar,
  AssistantToggle,
  MeetAssistantCard,
  SystemNotification,
  SystemStatusCard,
  BottomNav,
  BottomSheet,
  Button,
  CallHistoryList,
  CallHistoryRow,
  CallSummary,
  CategoryAvatar,
  ChatBubble,
  ChatHeader,
  ChatListItem,
  ChatWallpaper,
  Checkbox,
  ChipRow,
  Composer,
  ConfirmSheetContent,
  ContactAvatar,
  ContactInfoHeader,
  ContextMenu,
  CountBadge,
  DayDivider,
  Dialog,
  EmptyState,
  EqualLogo,
  EqualMark,
  EqualOrb,
  Fab,
  FeatureBadge,
  FeedbackCard,
  FullSheet,
  GenderSelector,
  HomeHeader,
  IconButton,
  IllustrationHeader,
  InstructionCard,
  InstructionPreview,
  InviteCard,
  LargeActionChip,
  Mascot,
  MessageList,
  NewBadge,
  OverflowMenu,
  PhoneFrame,
  PlanBadge,
  PlanCard,
  PresetCard,
  PresetInstructionCard,
  ProfileAvatar,
  ProfileHeader,
  ProfileHero,
  RecommendedCard,
  RowGroup,
  SearchField,
  SectionHeader,
  SegmentedChips,
  SelectionHeader,
  SettingsRow,
  SmallActionChip,
  Snackbar,
  StatusBar,
  SuggestionsComingCard,
  Switch,
  TakeoverPill,
  TextButton,
  TextField,
  ThreadEvent,
  ThreadNotice,
  ToggleRow,
  TopBar,
  VoicePlayer,
  WhatsNewCard,
  WhenDoRow,
  formatAddress,
  type Gender,
  type NavTab,
} from '@equal/ds';
import { sampleChats } from '../../../web/src/screens/data';
import { instructionLibrary, presets, recommendations, sampleAddresses } from '../../../web/src/screens/flowData';
import type { ComponentDoc, PropDoc } from './types';

// ---------------------------------------------------------------- helpers

const req = (name: string, type: string, description: string): PropDoc => ({ name, type, description, required: true });
const opt = (name: string, type: string, def: string | undefined, description: string): PropDoc => (def === undefined ? { name, type, description } : { name, type, default: def, description });

const col = (gap = 12, extra?: CSSProperties): CSSProperties => ({ display: 'flex', flexDirection: 'column', gap, ...extra });
const row = (gap = 12, extra?: CSSProperties): CSSProperties => ({ display: 'flex', alignItems: 'center', flexWrap: 'wrap', gap, ...extra });

/** Example wrapper: 16dp padding, full canvas width; full-width components fill it, fixed-size ones stay centred. */
const pad16: CSSProperties = { padding: 16, width: '100%', boxSizing: 'border-box', display: 'flex', flexDirection: 'column', alignItems: 'center' };

function Col({ gap, children, style }: { gap?: number; children: ReactNode; style?: CSSProperties }) {
  return <div style={col(gap, style)}>{children}</div>;
}
function Row({ gap, children, style }: { gap?: number; children: ReactNode; style?: CSSProperties }) {
  return <div style={row(gap, style)}>{children}</div>;
}
/** Page-background strip used to show surface components the way they sit in the app. */
function OnPrimary({ children, pad = 0 }: { children: ReactNode; pad?: number | string }) {
  return <div style={{ background: 'var(--eq-bg-primary)', padding: pad, width: '100%', boxSizing: 'border-box' }}>{children}</div>;
}
/** Hairline-separated list, like the chat list. */
function Hairlines({ children }: { children: ReactNode }) {
  return <div style={{ display: 'flex', flexDirection: 'column', gap: 1, background: 'var(--eq-stroke-subtle)', width: '100%' }}>{children}</div>;
}

const noop = () => {};
const [arnav, mehta, amit] = sampleChats;

// ---------------------------------------------------------------- stateful demos

function PriceDropDemo() {
  const [n, setN] = useState(0);
  return (
    <div style={{ width: '100%', padding: 16, boxSizing: 'border-box', display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 12 }}>
      <PriceTag key={n} price={149} was={249} badge="Special Coupon Applied!" animate />
      <Button size="sm" variant="secondary" onClick={() => setN((x) => x + 1)}>Replay</Button>
    </div>
  );
}

function SwitchDemo() {
  const [on, setOn] = useState(true);
  return <Switch checked={on} onChange={setOn} label="AI Assistant" />;
}

function CheckboxDemo() {
  const [all, setAll] = useState(false);
  const [a, setA] = useState(true);
  return (
    <Col gap={4}>
      <Checkbox checked={all} onChange={setAll} label="Select All" />
      <Checkbox checked={a} onChange={setA} label="Loan Offers" labelVariant="body1" />
    </Col>
  );
}

function NameFieldDemo() {
  const [name, setName] = useState('Keshav');
  const first = name.trim().split(/\s+/)[0] || 'you';
  return <TextField label="Name" value={name} onChange={setName} placeholder="Your name" helper={`Your AI will call you '${first}'`} />;
}

function PincodeFieldDemo() {
  const [pin, setPin] = useState('5000');
  const error = pin && !/^\d{6}$/.test(pin) ? 'Enter a 6-digit pincode' : undefined;
  return <TextField aria-label="Pincode" placeholder="Pincode" value={pin} onChange={(v) => setPin(v.replace(/\D/g, '').slice(0, 6))} inputMode="numeric" className="eq-tnum" align="center" error={error} />;
}

function DirectionFieldDemo() {
  const [v, setV] = useState('');
  return <TextField label="Guiding direction" optional value={v} onChange={setV} placeholder="e.g. Near City Center Mall" />;
}

function AddressLinesDemo() {
  const [l1, setL1] = useState('House no. 4, Tony Street');
  const [l2, setL2] = useState('');
  return (
    <Col gap={10}>
      <TextField aria-label="Address line 1" placeholder="Flat, house no., building" value={l1} onChange={setL1} multiline />
      <TextField aria-label="Address line 2" placeholder="Street, area, landmark" value={l2} onChange={setL2} multiline />
    </Col>
  );
}

function SearchDemo({ initial = '' }: { initial?: string }) {
  const [q, setQ] = useState(initial);
  return <SearchField value={q} onChange={setQ} />;
}

function SearchResultsDemo() {
  const [q, setQ] = useState('Dr');
  const results = sampleChats.filter((c) => c.name.toLowerCase().includes(q.trim().toLowerCase()));
  return (
    <Col gap={12} style={{ width: '100%', minWidth: 0 }}>
      <div style={{ padding: '0 16px' }}>
        <SearchField value={q} onChange={setQ} />
      </div>
      {q && results.length === 0 ? (
        <EmptyState title="No chats found" body={`Nothing matches "${q}". Try a name or number.`} />
      ) : (
        <Hairlines>
          {results.slice(0, 3).map((c) => (
            <ChatListItem key={c.id} name={c.name} preview={c.preview} date={c.date} unread={c.unread} avatar={c.initials ? { initials: c.initials } : { category: c.category }} />
          ))}
        </Hairlines>
      )}
    </Col>
  );
}

const ADDRESS_LABELS = ['Home', 'Work', 'Other'] as const;
function SegmentedDemo() {
  const [label, setLabel] = useState<(typeof ADDRESS_LABELS)[number]>('Home');
  return <SegmentedChips options={ADDRESS_LABELS} value={label} onChange={setLabel} />;
}

const SPEED_OPTIONS = ['Slow', 'Normal', 'Fast'] as const;
function SegmentedSpeedDemo() {
  const [v, setV] = useState<(typeof SPEED_OPTIONS)[number]>('Normal');
  return <SegmentedChips options={SPEED_OPTIONS} value={v} onChange={setV} />;
}

function ComposerChatDemo() {
  const [sent, setSent] = useState<string[]>([]);
  return (
    <Col gap={12}>
      <MessageList>
        <ChatBubble kind="assistant" sender="Equal AI" time="03:21">Want me to send Amit a quick reply on WhatsApp?</ChatBubble>
        {sent.map((t, i) => (
          <ChatBubble key={i} kind="outgoing" sender="You" time="03:22" first={i === 0}>{t}</ChatBubble>
        ))}
      </MessageList>
      <div style={{ padding: '0 16px' }}>
        <Composer onSend={(t) => setSent((s) => [...s, t])} />
      </div>
    </Col>
  );
}

function FeedbackDemo() {
  const [rating, setRating] = useState(0);
  const [shared, setShared] = useState<number | null>(null);
  return (
    <Col gap={12}>
      <FeedbackCard rating={rating} onRate={setRating} onShare={(r) => setShared(r)} />
      {shared !== null && <Snackbar message={shared ? `Thanks! You rated the call ${shared}/5` : 'Pick a rating first'} />}
    </Col>
  );
}

function AssistantToggleDemo() {
  const [on, setOn] = useState(true);
  return <AssistantToggle on={on} onChange={setOn} />;
}

function HomeHeaderDemo() {
  const [on, setOn] = useState(true);
  return <HomeHeader assistantOn={on} onAssistantChange={setOn} />;
}

function BottomNavDemo() {
  const [tab, setTab] = useState<NavTab>('chats');
  return (
    <OnPrimary pad="24px 0">
      <div style={{ display: 'flex', justifyContent: 'center' }}>
        <BottomNav active={tab} onChange={setTab} unread={7} />
      </div>
    </OnPrimary>
  );
}

function SelectionDemo() {
  const [selected, setSelected] = useState<string[]>([arnav.id]);
  const shown = sampleChats.slice(0, 3);
  const all = selected.length === shown.length;
  const toggle = (id: string) => setSelected((s) => (s.includes(id) ? s.filter((x) => x !== id) : [...s, id]));
  return (
    <div style={{ width: '100%', minWidth: 0 }}>
      <SelectionHeader count={selected.length} allSelected={all} onSelectAll={(v) => setSelected(v ? shown.map((c) => c.id) : [])} onBack={() => setSelected([])} onMarkRead={noop} onDelete={noop} />
      <Hairlines>
        {shown.map((c) => (
          <ChatListItem key={c.id} name={c.name} preview={c.preview} date={c.date} unread={c.unread} actions={c.actions} selected={selected.includes(c.id)} avatar={c.initials ? { initials: c.initials } : { category: c.category }} onClick={() => toggle(c.id)} />
        ))}
      </Hairlines>
    </div>
  );
}

function ChatListLongPressDemo() {
  const [selected, setSelected] = useState(false);
  return (
    <ChatListItem
      name={mehta.name}
      preview={mehta.preview}
      date={mehta.date}
      unread={mehta.unread}
      actions={mehta.actions}
      avatar={{ category: mehta.category }}
      selected={selected}
      onLongPress={() => setSelected(true)}
      onClick={() => selected && setSelected(false)}
    />
  );
}

function ToggleRowsDemo() {
  const [mute, setMute] = useState(true);
  const [learn, setLearn] = useState(false);
  return (
    <OnPrimary pad="0 0 16px">
      <SectionHeader>Your assistant</SectionHeader>
      <RowGroup>
        <ToggleRow label="Mute Promotional Calls" wrap description="Equal AI Assistant can identify Promotional Calls and automatically mute them" checked={mute} onChange={setMute} />
        <ToggleRow label="Allow AI to learn from calls" wrap description="When this is on, assistant pays attention to how your calls go so it can handle them more like you would." checked={learn} onChange={setLearn} />
      </RowGroup>
    </OnPrimary>
  );
}

function SheetDemo() {
  const [open, setOpen] = useState<'delete' | 'suggest' | null>(null);
  return (
    <PhoneFrame height={560}>
      <div style={{ padding: 24, display: 'flex', flexDirection: 'column', gap: 12 }}>
        <Button variant="danger" leadingIcon="delete" onClick={() => setOpen('delete')}>Delete chat</Button>
        <Button onClick={() => setOpen('suggest')}>Open AI suggestion</Button>
      </div>
      <BottomSheet open={open !== null} onClose={() => setOpen(null)} closable={open === 'delete'}>
        {open === 'suggest' ? (
          <Col gap={20}>
            <AiSuggestion>Want me to send Amit a quick reply on WhatsApp?</AiSuggestion>
            <ChipRow wrap>
              <LargeActionChip emoji="💬" label="Reply on WhatsApp" onClick={() => setOpen(null)} />
              <LargeActionChip emoji="✍️" label="Edit draft" state="read" />
            </ChipRow>
          </Col>
        ) : (
          <ConfirmSheetContent
            title={<>Delete chat with<br />Amit Nanda</>}
            subject="(+91 9871746113)"
            body="All past conversations will be deleted. This action cannot be undone."
            confirmLabel="Delete"
            destructive
            onConfirm={() => setOpen(null)}
          />
        )}
      </BottomSheet>
    </PhoneFrame>
  );
}

function FullSheetDemo() {
  const [open, setOpen] = useState(false);
  const ins = instructionLibrary.takeMessage;
  return (
    <PhoneFrame height={560}>
      <div style={{ padding: 24 }}>
        <Button block leadingIcon="add" onClick={() => setOpen(true)}>Add New Instruction</Button>
      </div>
      <FullSheet
        open={open}
        onClose={() => setOpen(false)}
        title="Setting up instruction"
        footer={
          <ChipRow gap={8}>
            <LargeActionChip emoji="✍️" label="Edit" state="read" />
            <LargeActionChip emoji="👍" label="Confirm" onClick={() => setOpen(false)} />
          </ChipRow>
        }
      >
        <div style={{ padding: '16px 0' }}>
          <MessageList>
            <ChatBubble kind="outgoing" sender="You" time="03:21">Take a message &amp; Callback request</ChatBubble>
            <ChatBubble kind="assistant" sender="Equal AI" time="03:21">Got it! Here's your instruction:</ChatBubble>
          </MessageList>
          <div style={{ padding: '8px 16px 0 52px' }}>
            <InstructionPreview title={ins.title} when={ins.when} action={ins.do} time="03:21" />
          </div>
        </div>
      </FullSheet>
    </PhoneFrame>
  );
}

function DialogDemo() {
  const [open, setOpen] = useState(false);
  const [toast, setToast] = useState<string | null>(null);
  return (
    <PhoneFrame height={560}>
      <div style={{ padding: 24, display: 'flex', flexDirection: 'column', gap: 16, flex: 1 }}>
        <Button onClick={() => (setToast(null), setOpen(true))}>Turn off Call Learning</Button>
        <span style={{ flex: 1 }} />
        {toast && <Snackbar message={toast} actionLabel="Undo" onAction={() => setToast(null)} />}
      </div>
      <Dialog
        open={open}
        title="Turn off Call Learning?"
        subject="Call learning"
        body="Your assistant will stop learning from new calls. What it already knows stays until you clear it."
        confirmLabel="Turn off"
        onCancel={() => setOpen(false)}
        onConfirm={() => (setOpen(false), setToast('Call learning turned off'))}
      />
    </PhoneFrame>
  );
}

function GenderDemo() {
  const [g, setG] = useState<Gender>('male');
  return (
    <Col gap={20}>
      <GenderSelector value={g} onChange={setG} />
      <Row gap={12}>
        <ProfileAvatar variant={g} size={56} />
        <span className="eq-t-body1" style={{ color: 'var(--eq-text-secondary)' }}>Your avatar updates across the app.</span>
      </Row>
    </Col>
  );
}

function AddressListDemo() {
  const [list, setList] = useState(sampleAddresses.slice(0, 2));
  return (
    <Col gap={12}>
      {list.map((a) => (
        <AddressCard key={a.id} label={a.label} address={formatAddress(a)} onEdit={noop} onDelete={() => setList((l) => l.filter((x) => x.id !== a.id))} />
      ))}
      {list.length === 0 && <EmptyState illustration="illustration-addresses-empty" title="Never explain your address again" body="Save frequently used addresses, and your assistant will use them to handle delivery, pickup calls, etc. more naturally." />}
    </Col>
  );
}

function AddressFormDemo() {
  const [saved, setSaved] = useState<string | null>(null);
  return (
    <Col gap={16}>
      <AddressForm initial={sampleAddresses[0]} onSave={(a) => setSaved(`${a.label} saved`)} />
      {saved && <Snackbar message={saved} />}
    </Col>
  );
}

function PresetApplyDemo() {
  const preset = presets[0];
  const [applied, setApplied] = useState<string[]>([]);
  return (
    <Col gap={12}>
      {preset.instructions.slice(0, 2).map((i) => (
        <PresetInstructionCard key={i.id} instruction={i} applied={applied.includes(i.id)} onApply={() => setApplied((a) => [...a, i.id])} />
      ))}
    </Col>
  );
}

function RecommendedDemo() {
  const [added, setAdded] = useState<string[]>([]);
  const left = recommendations.filter((r) => !added.includes(r.id));
  return left.length ? (
    <div className="eq-hscroll" style={{ display: 'flex', gap: 10 }}>
      {left.map((r) => (
        <RecommendedCard key={r.id} emoji={r.emoji} title={r.title} description={r.description} onAdd={() => setAdded((a) => [...a, r.id])} />
      ))}
    </div>
  ) : (
    <SuggestionsComingCard />
  );
}

function PreviewConfirmDemo() {
  const [confirmed, setConfirmed] = useState(false);
  const ins = recommendations[0].instruction;
  return (
    <ChatWallpaper style={{ padding: '16px 0' }}>
      <MessageList>
        <ChatBubble kind="assistant" sender="Equal AI" time="03:21">Got it! Here's your instruction:</ChatBubble>
      </MessageList>
      <div style={{ padding: '8px 16px 0 52px' }}>
        <InstructionPreview title={ins.title} when={ins.when} action={ins.do} time="03:21" confirmed={confirmed} />
      </div>
      {!confirmed && (
        <div style={{ padding: '16px 16px 0' }}>
          <ChipRow gap={8}>
            <LargeActionChip emoji="✍️" label="Edit" state="read" />
            <LargeActionChip emoji="👍" label="Confirm" onClick={() => setConfirmed(true)} />
          </ChipRow>
        </div>
      )}
    </ChatWallpaper>
  );
}

function PlanStatesDemo() {
  return (
    <Col gap={16}>
      <PlanCard onManage={noop} />
      <PlanCard status="expired" onManage={noop} />
    </Col>
  );
}

// ---------------------------------------------------------------- shared prop docs

const htmlButtonNote = 'All other native <button> attributes (onClick, disabled, aria-*, …) are passed through.';

// ================================================================ registry

export const componentDocs: ComponentDoc[] = [
  // ============================================================ Actions
  {
    id: 'button',
    name: 'Button',
    group: 'Actions',
    summary: 'Filled, text and icon buttons: use a filled Button for the one commit action on a surface, a TextButton for revise or secondary actions, and an IconButton for icon-only controls like back, close and call.',
    components: ['Button', 'TextButton', 'IconButton'],
    importLine: "import { Button, TextButton, IconButton } from '@equal/ds';",
    compose: ['EqualButton', 'EqualTextButton', 'EqualIconButton'],
    spec: '§19 Buttons',
    tokens: ['button/primary', 'button/secondary', 'button/danger', 'button/disabled', 'text/on-action', 'text/on-danger', 'text/on-action-disabled', 'text/action', 'text/danger', 'bg/surface', 'shadow/raised', 'shadow/focusRing'],
    examples: [
      {
        title: 'Variants',
        note: 'Primary is the default commit. Secondary and danger are for the rare surfaces that need them.',
        width: 'wide',
        render: () => (
          <Row gap={12}>
            <Button>Save</Button>
            <Button variant="secondary">Try Voices</Button>
            <Button variant="danger" leadingIcon="delete">Delete</Button>
            <Button disabled>Save</Button>
          </Row>
        ),
        code: `<Button>Save</Button>
<Button variant="secondary">Try Voices</Button>
<Button variant="danger" leadingIcon="delete">Delete</Button>
<Button disabled>Save</Button>`,
      },
      {
        title: 'Sizes',
        note: 'sm 24 · md 36 · lg 45. `block` fills the container; sheets use block lg.',
        width: 'wide',
        render: () => (
          <Col gap={16}>
            <Row gap={12}>
              <Button size="sm" leadingIcon="add">Apply</Button>
              <Button size="md">Save</Button>
              <Button size="lg">Save</Button>
              <Button shape="rounded">Save</Button>
            </Row>
            <div style={{ maxWidth: 328 }}>
              <Button block size="lg" leadingIcon="add">Add New Instruction</Button>
            </div>
          </Col>
        ),
        code: `<Button size="sm" leadingIcon="add">Apply</Button>
<Button size="md">Save</Button>
<Button size="lg">Save</Button>
<Button shape="rounded">Save</Button>

<Button block size="lg" leadingIcon="add">Add New Instruction</Button>`,
      },
      {
        title: 'Text and icon buttons',
        width: 'wide',
        render: () => (
          <Col gap={16}>
            <Row gap={20}>
              <TextButton>Edit</TextButton>
              <TextButton leadingIcon="share">Share</TextButton>
              <TextButton trailingIcon="chevron-right">Share Feedback</TextButton>
              <TextButton tone="neutral">Cancel</TextButton>
              <TextButton tone="danger">Delete address</TextButton>
              <TextButton size="small">Clear all</TextButton>
            </Row>
            <Row gap={12}>
              <IconButton icon="arrow-back" label="Back" />
              <IconButton icon="call" label="Call" />
              <IconButton icon="more-vert" label="More options" />
              <IconButton icon="delete" label="Delete" variant="danger-ghost" />
              <IconButton icon="close" label="Close" variant="neutral" />
              <IconButton icon="check" label="Confirm" variant="primary" />
            </Row>
          </Col>
        ),
        code: `<TextButton>Edit</TextButton>
<TextButton leadingIcon="share">Share</TextButton>
<TextButton trailingIcon="chevron-right">Share Feedback</TextButton>
<TextButton tone="neutral">Cancel</TextButton>
<TextButton tone="danger">Delete address</TextButton>
<TextButton size="small">Clear all</TextButton>

<IconButton icon="arrow-back" label="Back" />
<IconButton icon="delete" label="Delete" variant="danger-ghost" />
<IconButton icon="close" label="Close" variant="neutral" />
<IconButton icon="check" label="Confirm" variant="primary" />`,
      },
      {
        title: 'Commit + revise',
        note: 'Rule 4: two actions at equal weight are one action. Revise is text, commit is filled.',
        surface: 'surface',
        render: () => (
          <div style={{ display: 'flex', justifyContent: 'flex-end', alignItems: 'center', gap: 20, padding: 24 }}>
            <TextButton tone="neutral">Cancel</TextButton>
            <Button>Turn off</Button>
          </div>
        ),
        code: `<TextButton tone="neutral">Cancel</TextButton>
<Button>Turn off</Button>`,
      },
    ],
    props: [
      {
        component: 'Button',
        props: [
          req('children', 'ReactNode', 'Label, sentence case.'),
          opt('variant', "'primary' | 'secondary' | 'danger'", "'primary'", 'Fill colour: button/primary, button/secondary or button/danger.'),
          opt('size', "'sm' | 'md' | 'lg'", "'md'", 'Height 24 / 36 / 45. sm uses the buttonSmall token and keeps a 48dp touch target.'),
          opt('block', 'boolean', undefined, 'Full width.'),
          opt('leadingIcon', 'IconName', undefined, 'Icon before the label (14 on sm, 18 otherwise).'),
          opt('trailingIcon', 'IconName', undefined, 'Icon after the label.'),
          opt('shape', "'pill' | 'rounded'", "'pill'", "Fully rounded pill, or 8dp 'rounded' (typography specimens)."),
          opt('disabled', 'boolean', undefined, 'button/disabled fill, text/on-action-disabled label.'),
          opt('type', "'button' | 'submit' | 'reset'", "'button'", htmlButtonNote),
        ],
      },
      {
        component: 'TextButton',
        props: [
          req('children', 'ReactNode', 'Label.'),
          opt('size', "'button' | 'small'", "'button'", "'button' 14sp, or 'small' 12sp for dense containers."),
          opt('tone', "'action' | 'danger' | 'neutral'", "'action'", 'text/action (green = actionable), text/danger, or neutral for Cancel.'),
          opt('leadingIcon', 'IconName', undefined, 'Icon before the label.'),
          opt('trailingIcon', 'IconName', undefined, 'Icon after the label.'),
          opt('type', "'button' | 'submit' | 'reset'", "'button'", htmlButtonNote),
        ],
      },
      {
        component: 'IconButton',
        props: [
          req('icon', 'IconName', 'Material Symbols icon from the DS manifest.'),
          req('label', 'string', 'Accessible label; also used as the tooltip.'),
          opt('variant', "'neutral' | 'primary' | 'ghost' | 'danger-ghost'", "'ghost'", 'ghost = bare target; neutral = white raised circle (close); primary = green circle (confirm); danger-ghost = red icon (delete).'),
          opt('size', 'number', '40 ghost · 42 primary · 44 neutral', 'Circle size in dp.'),
          opt('iconSize', 'number', '24 ghost · 22 filled', 'Icon size in dp.'),
          opt('type', "'button' | 'submit' | 'reset'", "'button'", htmlButtonNote),
        ],
      },
    ],
    dos: [
      'Use exactly one filled Button per surface for the commit action (Save, Delete, Turn off); pair it with a TextButton for revise or cancel.',
      'Write labels in sentence case ("Add new Address", "Share Feedback" as shipped) and keep the same verb from control to confirmation: Turn off → "Call learning turned off".',
      'Use the danger variant only for destructive commits, with the trash icon (the delete sheet does this for you).',
      'Always pass a `label` to IconButton; it is the accessible name and the tooltip.',
    ],
    donts: [
      "Don't put two filled buttons side by side; two actions at equal weight are one action.",
      "Don't colour non-actionable text green; text/action is reserved for things you can tap.",
      "Don't shrink the touch target: small buttons keep a 48dp hit area even when the visual is 24dp.",
    ],
  },
  {
    id: 'action-chips',
    name: 'Action chips',
    group: 'Actions',
    summary: 'Emoji-led glass chips for the next step after a call: small chips in the chat list, large chips inside a conversation or the AI panel.',
    components: ['SmallActionChip', 'LargeActionChip', 'ChipRow'],
    importLine: "import { SmallActionChip, LargeActionChip, ChipRow } from '@equal/ds';",
    compose: ['SmallActionChip', 'LargeActionChip'],
    spec: '§9 Small Action Chip · §10 Large Action Chip',
    tokens: ['gradient/chip-small-unread', 'gradient/chip-small-read', 'gradient/chip-large', 'action/stroke/unread', 'stroke/subtle', 'shadow/glass', 'text/action', 'text/secondary', 'text/primary'],
    examples: [
      {
        title: 'Small chip states',
        note: 'unread (green glass) · read (grey glass) · outlined (selected row).',
        render: () => (
          <Col gap={12} style={{ padding: 16 }}>
            <Row gap={8}>
              <SmallActionChip emoji="📞" label="Call Back" />
              <SmallActionChip emoji="✏️" label="Update Address" />
            </Row>
            <Row gap={8}>
              <SmallActionChip emoji="📞" label="Call Back" state="read" />
              <SmallActionChip emoji="🗓️" label="Add to Calendar" state="read" />
            </Row>
            <Row gap={8}>
              <SmallActionChip emoji="📞" label="Call Back" state="outlined" />
              <SmallActionChip emoji="🗓️" label="Add to Calendar" state="outlined" chevron={false} />
            </Row>
          </Col>
        ),
        code: `<SmallActionChip emoji="📞" label="Call Back" />
<SmallActionChip emoji="✏️" label="Update Address" />
<SmallActionChip emoji="📞" label="Call Back" state="read" />
<SmallActionChip emoji="📞" label="Call Back" state="outlined" />
<SmallActionChip emoji="🗓️" label="Add to Calendar" state="outlined" chevron={false} />`,
      },
      {
        title: 'Large chips',
        note: 'Inside a chat. The suggested action is unread; the alternative (Edit) is read.',
        render: () => (
          <Col gap={12} style={{ padding: 16 }}>
            <ChipRow gap={8}>
              <LargeActionChip emoji="📞" label="Takeover" />
              <LargeActionChip emoji="🗓️" label="Ask to reschedule" />
            </ChipRow>
            <ChipRow gap={8}>
              <LargeActionChip emoji="✍️" label="Edit" state="read" />
              <LargeActionChip emoji="👍" label="Confirm" />
            </ChipRow>
          </Col>
        ),
        code: `<ChipRow gap={8}>
  <LargeActionChip emoji="📞" label="Takeover" />
  <LargeActionChip emoji="🗓️" label="Ask to reschedule" />
</ChipRow>
<ChipRow gap={8}>
  <LargeActionChip emoji="✍️" label="Edit" state="read" />
  <LargeActionChip emoji="👍" label="Confirm" />
</ChipRow>`,
      },
      {
        title: 'In a chat row',
        note: 'ChipRow scrolls horizontally and clips at the right edge.',
        surface: 'surface',
        render: () => (
          <div style={{ padding: '12px 0 12px 16px' }}>
            <ChipRow>
              {arnav.actions.map((a) => (
                <SmallActionChip key={a.label} emoji={a.emoji} label={a.label} />
              ))}
            </ChipRow>
          </div>
        ),
        code: `<ChipRow>
  <SmallActionChip emoji="📞" label="Call Back" />
  <SmallActionChip emoji="✏️" label="Update Address" />
  <SmallActionChip emoji="🗓️" label="Add to Calendar" />
</ChipRow>`,
      },
    ],
    props: [
      {
        component: 'SmallActionChip',
        props: [
          req('label', 'string', 'Chip label (buttonSmall token).'),
          opt('emoji', 'string', undefined, 'Unicode emoji shown at 16dp: 📞 Call back, ✏️ Update address, 🗓️ Add to calendar.'),
          opt('state', "'unread' | 'read' | 'outlined'", "'unread'", 'unread = green glass, read = grey glass, outlined = the selected-row state.'),
          opt('chevron', 'boolean', 'true', 'Trailing chevron-right (14dp).'),
          opt('type', "'button' | 'submit' | 'reset'", "'button'", htmlButtonNote),
        ],
      },
      {
        component: 'LargeActionChip',
        props: [
          req('label', 'string', 'Chip label (button token).'),
          opt('emoji', 'string', undefined, 'Unicode emoji shown at 18dp.'),
          opt('state', "'unread' | 'read'", "'unread'", 'unread = green glass (the suggested action); read = grey glass (the alternative, e.g. Edit).'),
          opt('type', "'button' | 'submit' | 'reset'", "'button'", htmlButtonNote),
        ],
      },
      {
        component: 'ChipRow',
        props: [
          req('children', 'ReactNode', 'The chips.'),
          opt('gap', 'number', '12', 'Gap between chips in dp.'),
          opt('wrap', 'boolean', 'false', 'Wrap onto new lines instead of scrolling horizontally.'),
        ],
      },
    ],
    dos: [
      'Lead every action with a Unicode emoji (📞 Call Back, ✏️ Update Address, 🗓️ Add to Calendar); actions use emoji, not Material icons.',
      'Use small chips outside chats (the chat list) and large chips inside a conversation or the AI panel.',
      'Let ChatListItem pick the small-chip state: unread rows get unread chips, read rows read chips, a selected row outlined chips.',
      'Mark the suggested next step unread and the alternative read (👍 Confirm vs ✍️ Edit).',
    ],
    donts: [
      "Don't swap the emoji for a Material icon or drop it on some chips in a row.",
      "Don't wrap the chat-list chip row; it scrolls and clips at the right edge.",
      "Don't use a chip for a destructive action; that belongs in a confirm sheet.",
    ],
  },

  // ============================================================ Inputs
  {
    id: 'switch',
    name: 'Switch',
    group: 'Inputs',
    summary: 'A controlled on/off toggle (40 × 24 track) for settings that take effect immediately, such as the assistant or promotional-call muting.',
    components: ['Switch'],
    importLine: "import { Switch } from '@equal/ds';",
    compose: ['EqualSwitch'],
    spec: '§3 Assistant Availability Toggle · Switch',
    tokens: ['button/primary', 'button/disabled', 'brand/white', 'shadow/raised'],
    examples: [
      {
        title: 'Interactive',
        render: () => (
          <div style={{ padding: 24 }}>
            <SwitchDemo />
          </div>
        ),
        code: `const [on, setOn] = useState(true);

<Switch checked={on} onChange={setOn} label="AI Assistant" />`,
      },
      {
        title: 'States',
        render: () => (
          <Row gap={20} style={{ padding: 24 }}>
            <Switch checked label="On" />
            <Switch checked={false} label="Off" />
            <Switch checked disabled label="On, disabled" />
            <Switch checked={false} disabled label="Off, disabled" />
          </Row>
        ),
        code: `<Switch checked label="On" />
<Switch checked={false} label="Off" />
<Switch checked disabled label="On, disabled" />
<Switch checked={false} disabled label="Off, disabled" />`,
      },
      {
        title: 'In context',
        note: 'Most switches live in a ToggleRow or the Assistant toggle card.',
        render: () => <ToggleRowsDemo />,
        code: `<RowGroup>
  <ToggleRow label="Mute Promotional Calls" wrap
    description="Equal AI Assistant can identify Promotional Calls and automatically mute them"
    checked={mute} onChange={setMute} />
</RowGroup>`,
      },
    ],
    props: [
      {
        component: 'Switch',
        props: [
          req('checked', 'boolean', 'Controlled state.'),
          opt('onChange', '(checked: boolean) => void', undefined, 'Called with the next value on tap.'),
          opt('disabled', 'boolean', undefined, 'Disables the control.'),
          req('label', 'string', 'Accessible label (aria-label). Not rendered visually.'),
        ],
      },
    ],
    dos: [
      'Use a Switch when the change applies instantly (Mute Promotional Calls, Get updates on WhatsApp).',
      'Put the visible label in the row beside it and pass the same text as `label`.',
      'Confirm with a sheet when turning something off has consequences, as WhatsApp Updates does ("Turn off WhatsApp updates?").',
    ],
    donts: [
      "Don't use a Switch for a choice that needs a Save button; use Checkbox or SegmentedChips.",
      "Don't recolour the track; on is button/primary and off is button/disabled in both themes.",
    ],
  },
  {
    id: 'checkbox',
    name: 'Checkbox',
    group: 'Inputs',
    summary: 'A coded checkbox with an optional label, used for multi-select such as "Select All" in chat-list selection mode.',
    components: ['Checkbox'],
    importLine: "import { Checkbox } from '@equal/ds';",
    compose: ['EqualCheckbox'],
    spec: '§5 Chat Header · Selection mode',
    tokens: ['button/primary', 'text/secondary', 'text/primary'],
    examples: [
      {
        title: 'Interactive',
        render: () => (
          <div style={pad16}>
            <CheckboxDemo />
          </div>
        ),
        code: `const [all, setAll] = useState(false);

<Checkbox checked={all} onChange={setAll} label="Select All" />
<Checkbox checked={a} onChange={setA} label="Loan Offers" labelVariant="body1" />`,
      },
      {
        title: 'Sizes and bare',
        render: () => (
          <Row gap={20} style={{ padding: 16 }}>
            <Checkbox checked size={18} />
            <Checkbox checked={false} size={18} />
            <Checkbox checked />
            <Checkbox checked={false} />
          </Row>
        ),
        code: `<Checkbox checked size={18} />
<Checkbox checked={false} size={18} />
<Checkbox checked />
<Checkbox checked={false} />`,
      },
      {
        title: 'In the selection header',
        render: () => <SelectionDemo />,
        code: `<SelectionHeader count={selected.length} allSelected={all}
  onSelectAll={(v) => setSelected(v ? ids : [])} />`,
      },
    ],
    props: [
      {
        component: 'Checkbox',
        props: [
          req('checked', 'boolean', 'Controlled state. Checked uses button/primary, unchecked text/secondary.'),
          opt('onChange', '(checked: boolean) => void', undefined, 'Called with the next value on tap.'),
          opt('label', 'string', undefined, 'Label beside the box (text/primary).'),
          opt('size', 'number', '22', 'Box size in dp.'),
          opt('labelVariant', "'bodyLarge' | 'body1'", "'bodyLarge'", 'Type token for the label.'),
        ],
      },
    ],
    dos: [
      'Use for multi-select lists; the whole label is the tap target.',
      'Use bodyLarge labels in headers ("Select All") and body1 in dense lists.',
    ],
    donts: [
      "Don't use a checkbox for an instant on/off setting; that is a Switch.",
      "Don't replace the coded box with an image; checkboxes are coded, never images.",
    ],
  },
  {
    id: 'text-field',
    name: 'Text field',
    group: 'Inputs',
    summary: 'A borderless white form field with an uppercase overline label, bodyLarge value, helper and error text, used in the Personal Details and Address forms.',
    components: ['TextField'],
    importLine: "import { TextField } from '@equal/ds';",
    compose: ['EqualTextField'],
    spec: 'Surfaces from the Typography screen inventory · Text Field',
    tokens: ['bg/surface', 'stroke/medium', 'action/stroke/unread', 'shadow/focusRing', 'text/primary', 'text/placeholder', 'text/tertiary', 'text/secondary', 'text/danger'],
    examples: [
      {
        title: 'Name',
        note: 'Personal Details: the helper echoes what the assistant will call you.',
        render: () => (
          <div style={pad16}>
            <NameFieldDemo />
          </div>
        ),
        code: `const [name, setName] = useState('Keshav');

<TextField label="Name" value={name} onChange={setName}
  placeholder="Your name" helper="Your AI will call you 'Keshav'" />`,
      },
      {
        title: 'Optional label',
        render: () => (
          <div style={pad16}>
            <DirectionFieldDemo />
          </div>
        ),
        code: `<TextField label="Guiding direction" optional value={v} onChange={setV}
  placeholder="e.g. Near City Center Mall" />`,
      },
      {
        title: 'Multiline and grouped',
        note: 'Fields that share one ADDRESS label omit `label` and pass aria-label instead.',
        render: () => (
          <div style={pad16}>
            <AddressLinesDemo />
          </div>
        ),
        code: `<TextField aria-label="Address line 1" placeholder="Flat, house no., building"
  value={l1} onChange={setL1} multiline />
<TextField aria-label="Address line 2" placeholder="Street, area, landmark"
  value={l2} onChange={setL2} multiline />`,
      },
      {
        title: 'Error',
        render: () => (
          <div style={{ padding: 16, width: 180 }}>
            <PincodeFieldDemo />
          </div>
        ),
        code: `<TextField aria-label="Pincode" placeholder="Pincode" value={pin}
  onChange={(v) => setPin(v.replace(/\\D/g, '').slice(0, 6))}
  inputMode="numeric" className="eq-tnum" align="center"
  error={/^\\d{6}$/.test(pin) ? undefined : 'Enter a 6-digit pincode'} />`,
      },
    ],
    props: [
      {
        component: 'TextField',
        props: [
          opt('label', 'string', undefined, 'Overline label, uppercased in the string. Omit for fields in a group that shares one label.'),
          req('value', 'string', 'Controlled value.'),
          opt('onChange', '(v: string) => void', undefined, 'Called with the new string.'),
          opt('optional', 'boolean', undefined, 'Adds a caption Regular "(Optional)" after the label.'),
          opt('required', 'boolean', undefined, 'Appends * to the label (e.g. LABEL*).'),
          opt('helper', 'ReactNode', undefined, 'Helper text below (captionWrap, text/secondary).'),
          opt('error', 'string', undefined, 'Error text; replaces the helper and turns it text/danger.'),
          opt('multiline', 'boolean', undefined, 'Renders a 2-row textarea.'),
          opt('shape', "'rounded' | 'pill'", "'rounded'", "pill = fully rounded (Name); rounded = 12dp (address lines)."),
          opt('align', "'start' | 'center'", "'start'", 'Text alignment (City / Pincode are centred).'),
          opt('…input attributes', "InputHTMLAttributes (minus onChange, value, size)", undefined, 'placeholder, inputMode, aria-label, className, autoFocus… are passed to the <input>.'),
        ],
      },
    ],
    dos: [
      'Write the overline label in the string\'s natural case; the component uppercases it (rule 5).',
      'Use `pill` for single short values (Name) and `rounded` for address lines.',
      'Add `className="eq-tnum"` on numeric fields; postal codes use tabular figures (rule 6).',
    ],
    donts: [
      "Don't add a border in light mode; fields are borderless white on bg/primary.",
      "Don't put instructions in the placeholder that the user needs after typing; use `helper`.",
      "Don't show both helper and error; the error replaces the helper.",
    ],
  },
  {
    id: 'search-field',
    name: 'Search field',
    group: 'Inputs',
    summary: 'A pill search input with a leading search icon and a clear button, used to filter chats.',
    components: ['SearchField'],
    importLine: "import { SearchField } from '@equal/ds';",
    compose: ['SearchField'],
    spec: 'Surfaces from the Typography screen inventory · Search Field',
    tokens: ['bg/surface', 'text/tertiary', 'text/primary', 'text/placeholder'],
    examples: [
      {
        title: 'Empty',
        render: () => (
          <div style={pad16}>
            <SearchDemo />
          </div>
        ),
        code: `const [q, setQ] = useState('');

<SearchField value={q} onChange={setQ} />`,
      },
      {
        title: 'With a query',
        note: 'The clear (X) button appears once there is text.',
        render: () => (
          <div style={pad16}>
            <SearchDemo initial="Amit" />
          </div>
        ),
        code: `<SearchField value="Amit" onChange={setQ} />`,
      },
      {
        title: 'Filtering the chat list',
        note: 'Try a name that is not there to see the empty state.',
        render: () => <SearchResultsDemo />,
        code: `<SearchField value={q} onChange={setQ} />
{results.length === 0
  ? <EmptyState title="No chats found" body={\`Nothing matches "\${q}". Try a name or number.\`} />
  : results.map((c) => <ChatListItem key={c.id} {...c} />)}`,
      },
    ],
    props: [
      {
        component: 'SearchField',
        props: [
          req('value', 'string', 'Controlled query.'),
          opt('onChange', '(v: string) => void', undefined, "Called on input and with '' when Clear is tapped."),
          opt('placeholder', 'string', "'Search chats'", 'Placeholder, also the aria-label.'),
          opt('autoFocus', 'boolean', undefined, 'Focus on mount.'),
        ],
      },
    ],
    dos: [
      'Say what is being searched in the placeholder ("Search chats").',
      'Show an EmptyState with the query in it when nothing matches.',
    ],
    donts: [
      "Don't add a separate search button; results filter as you type.",
      "Don't use it as a general form field; use TextField.",
    ],
  },
  {
    id: 'segmented-chips',
    name: 'Segmented chips',
    group: 'Inputs',
    summary: 'A single-select row of pill chips for a short, fixed set of options, like the Home / Work / Other address label.',
    components: ['SegmentedChips'],
    importLine: "import { SegmentedChips } from '@equal/ds';",
    compose: ['SegmentedChips'],
    spec: 'Surfaces from the Typography screen inventory · Segmented Label Chips',
    tokens: ['chip/selected-bg', 'chip/selected-text', 'stroke/medium', 'text/secondary'],
    examples: [
      {
        title: 'Address label',
        render: () => (
          <div style={pad16}>
            <SegmentedDemo />
          </div>
        ),
        code: `const LABELS = ['Home', 'Work', 'Other'] as const;
const [label, setLabel] = useState<(typeof LABELS)[number]>('Home');

<SegmentedChips options={LABELS} value={label} onChange={setLabel} />`,
      },
      {
        title: 'Any string union',
        note: 'The component is generic over the option type.',
        render: () => (
          <div style={pad16}>
            <SegmentedSpeedDemo />
          </div>
        ),
        code: `const SPEEDS = ['Slow', 'Normal', 'Fast'] as const;

<SegmentedChips options={SPEEDS} value={v} onChange={setV} />`,
      },
    ],
    props: [
      {
        component: 'SegmentedChips',
        props: [
          req('options', 'readonly T[]', 'Option labels (T extends string); shown as-is, in the button token.'),
          req('value', 'T', 'The selected option.'),
          opt('onChange', '(v: T) => void', undefined, 'Called with the tapped option.'),
        ],
      },
    ],
    dos: [
      'Use for two to four short options where exactly one is always selected.',
      'Declare options `as const` so value and onChange are typed to the union.',
    ],
    donts: [
      "Don't use it for multi-select; use checkboxes.",
      "Don't use it for long labels that would wrap; the chips wrap as a row, not the text.",
    ],
  },
  {
    id: 'composer',
    name: 'Composer',
    group: 'Inputs',
    summary: 'The message input bar with a send button that stays disabled until there is text, used at the bottom of a chat and in the AI panel.',
    components: ['Composer'],
    importLine: "import { Composer } from '@equal/ds';",
    compose: ['VoiceInputBar'],
    spec: '§15 Voice Input Bar (Composer)',
    tokens: ['bg/surface', 'stroke/subtle', 'action/stroke/unread', 'shadow/focusRing', 'gradient/send-disabled', 'gradient/fab', 'shadow/fab', 'text/tertiary', 'text/primary'],
    examples: [
      {
        title: 'Interactive',
        note: 'Type and press Enter or the send button. Uncontrolled: it clears itself after send.',
        render: () => <ComposerChatDemo />,
        code: `<Composer onSend={(text) => setSent((s) => [...s, text])} />`,
      },
      {
        title: 'States',
        note: 'Empty (send disabled) · focused · with text.',
        render: () => (
          <Col gap={16} style={{ padding: 16, width: '100%', boxSizing: 'border-box' }}>
            <Composer />
            <Composer forceFocus />
            <Composer value="Sure, I'll be there!" onChange={noop} />
          </Col>
        ),
        code: `<Composer />
<Composer forceFocus />
<Composer value="Sure, I'll be there!" onChange={setText} />`,
      },
    ],
    props: [
      {
        component: 'Composer',
        props: [
          opt('value', 'string', undefined, 'Controlled text. Omit to let the composer manage its own state.'),
          opt('onChange', '(v: string) => void', undefined, 'Called on input (and with "" after send). Required to make `value` editable.'),
          opt('onSend', '(v: string) => void', undefined, 'Called with the trimmed text on submit. Send is disabled while the text is blank.'),
          opt('placeholder', 'string', "'Type a message...'", 'Placeholder text.'),
          opt('forceFocus', 'boolean', undefined, 'Force the focused look (for docs).'),
          opt('autoFocus', 'boolean', undefined, 'Focus the input on mount.'),
        ],
      },
    ],
    dos: [
      'Pin the composer to the bottom of the chat or AI panel.',
      'Leave send disabled on empty input; the component does this for you.',
    ],
    donts: [
      "Don't add attachment or emoji buttons inside the bar; the spec is input + send only.",
      "Don't change the send glyph; it is the coded send icon rotated −35°.",
    ],
  },

  // ============================================================ Display
  {
    id: 'badges',
    name: 'Badges',
    group: 'Display',
    summary: 'Small uppercase markers: NEW on settings rows, unread counts, the NEW FEATURE pill on announcements, and the gold UNLIMITED plan marker.',
    components: ['NewBadge', 'CountBadge', 'FeatureBadge', 'PlanBadge'],
    importLine: "import { NewBadge, CountBadge, FeatureBadge, PlanBadge } from '@equal/ds';",
    compose: ['NewBadge', 'CountBadge', 'FeatureBadge', 'PlanBadge'],
    spec: '§20 Badge',
    tokens: ['button/primary', 'text/on-action', 'bg/surface', 'badge/new-feature-stroke', 'text/secondary', 'gradient/gold', 'text/on-dark'],
    examples: [
      {
        title: 'All badges',
        width: 'wide',
        render: () => (
          <Row gap={16} style={{ padding: 8 }}>
            <NewBadge />
            <NewBadge tone="soft" />
            <CountBadge count={5} />
            <CountBadge count={12} />
            <CountBadge count={140} />
            <FeatureBadge />
            <PlanBadge />
          </Row>
        ),
        code: `<NewBadge />
<NewBadge tone="soft" />
<CountBadge count={5} />
<CountBadge count={12} />
<CountBadge count={140} />   {/* renders 99+ */}
<FeatureBadge />
<PlanBadge />`,
      },
      {
        title: 'In context',
        render: () => (
          <Col gap={0}>
            <TopBar />
            <RowGroup inset={0}>
              <SettingsRow icon="instructions" label="Instructions" description="Tell assistant how you'd like it to help" isNew />
            </RowGroup>
            <ChatListItem name={arnav.name} preview={arnav.preview} date={arnav.date} unread={arnav.unread} avatar={{ category: arnav.category }} />
          </Col>
        ),
        code: `<TopBar />                                  {/* PlanBadge */}
<SettingsRow icon="instructions" label="Instructions" isNew
  description="Tell assistant how you'd like it to help" />   {/* NewBadge */}
<ChatListItem name="Arnav Raj" preview="You have a visitor"
  date="Yesterday" unread={5} />             {/* CountBadge */}`,
      },
    ],
    props: [
      {
        component: 'NewBadge',
        props: [
          opt('label', 'string', "'New'", 'Uppercased in the string.'),
          opt('tone', "'solid' | 'soft'", "'solid'", 'solid = button/primary fill with text/on-action; soft = action-card/highlight fill with text/action.'),
        ],
      },
      {
        component: 'CountBadge',
        props: [
          req('count', 'number', 'Unread count. Renders nothing at 0; above 99 shows 99+.'),
          opt('size', 'number', '20', 'Circle size / min width in dp (the bottom nav uses 16).'),
          opt('ring', 'boolean', 'false', 'Adds the 1.5dp bg/surface ring used when overlapping an icon.'),
        ],
      },
      {
        component: 'FeatureBadge',
        props: [opt('label', 'string', "'New feature'", 'Label after the ⭐ emoji, uppercased.')],
      },
      {
        component: 'PlanBadge',
        props: [opt('label', 'string', "'Unlimited'", 'Plan name, uppercased, on the gold gradient.')],
      },
    ],
    dos: [
      'Keep badge text to one or two words; badges never wrap.',
      'Pass normal-case strings; the components uppercase badge text in the string (rule 5).',
      'Use CountBadge for unread messages only; it hides itself at zero.',
    ],
    donts: [
      "Don't use NewBadge for counts or statuses; it means \"new\" and nothing else.",
      "Don't recolour PlanBadge; the gold gradient marks the plan everywhere (top bar, profile).",
    ],
  },
  {
    id: 'avatars',
    name: 'Avatars',
    group: 'Display',
    summary: 'Every face in the app: caller category art or initials for contacts, the Equal orb for the assistant, the ringed 3D profile avatar for the user, and the robot mascot.',
    components: ['ContactAvatar', 'CategoryAvatar', 'AiAvatar', 'EqualOrb', 'ProfileAvatar', 'Mascot'],
    importLine: "import { ContactAvatar, CategoryAvatar, AiAvatar, EqualOrb, ProfileAvatar, Mascot } from '@equal/ds';",
    compose: ['ContactAvatar', 'CategoryAvatar', 'InitialsAvatar', 'AiAvatar', 'EqualLogoOrb', 'ProfileAvatar', 'EqualMascot'],
    spec: 'Icons, emoji, categories, logo · §12 AI Assistant Avatar',
    tokens: ['action-card/highlight', 'text/action', 'button/primary', 'text/on-action', 'bg/surface'],
    examples: [
      {
        title: 'Profile avatar: free vs paid',
        note: '`tier="paid"` adds the gold background and scalloped gold ring (You tab, bottom nav, profile header, gender cards).',
        render: () => (
          <Row gap={24}>
            <ProfileAvatar variant="male" size={70} tier="free" />
            <ProfileAvatar variant="male" size={70} tier="paid" />
            <ProfileAvatar variant="female" size={70} tier="free" />
            <ProfileAvatar variant="female" size={70} tier="paid" />
          </Row>
        ),
        code: `<ProfileAvatar variant="male" size={70} tier="free" />
<ProfileAvatar variant="male" size={70} tier="paid" />`,
      },
      {
        title: 'Caller categories',
        note: 'The circle is part of the image. `na` (the sparkle) is the unknown-caller default.',
        width: 'wide',
        render: () => (
          <Row gap={12}>
            {(['na', 'career', 'delivery', 'education', 'financial', 'gate-alert', 'home-services', 'order-confirmation', 'otp', 'others', 'others-1', 'political', 'real-estate', 'ride', 'services', 'travel'] as const).map((c) => (
              <CategoryAvatar key={c} category={c} size={48} />
            ))}
          </Row>
        ),
        code: `<CategoryAvatar category="na" />
<CategoryAvatar category="delivery" />
<CategoryAvatar category="real-estate" size={48} />`,
      },
      {
        title: 'Contact avatar',
        note: 'Category for unknown and business callers; initials on a tinted disc (one of six hues, picked per contact) with the person badge for saved contacts (the badge is the signifier for a known contact); a check in selection mode.',
        render: () => (
          <Row gap={16} style={{ padding: 16 }}>
            <ContactAvatar />
            <ContactAvatar category="others" />
            <ContactAvatar initials="AN" />
            <ContactAvatar initials="AN" selected />
            <ContactAvatar category="career" size={36} />
          </Row>
        ),
        code: `<ContactAvatar />
<ContactAvatar category="others" />
<ContactAvatar initials="AN" />
<ContactAvatar initials="AN" selected />
<ContactAvatar category="career" size={36} />`,
      },
      {
        title: 'Saved-contact colours',
        note: 'Six hues at the same saturation and lightness, each with initials that pass AA. The colour is picked from the initials, so the same contact always gets the same one; pass tone to choose.',
        render: () => (
          <Row gap={14} style={{ padding: 16, flexWrap: 'wrap' }}>
            {(['blue', 'green', 'purple', 'orange', 'pink', 'teal'] as const).map((t, i) => <ContactAvatar key={t} initials={['AN', 'AR', 'KS', 'PR', 'VJ', 'MS'][i]} tone={t} />)}
          </Row>
        ),
        code: `<ContactAvatar initials="AN" />              // tone picked from the initials
<ContactAvatar initials="AR" tone="green" />  // blue · green · purple · orange · pink · teal`,
      },
      {
        title: 'Assistant, user and mascot',
        render: () => (
          <Row gap={20} style={{ padding: 16, alignItems: 'flex-end' }}>
            <AiAvatar size={24} />
            <AiAvatar size={36} />
            <AiAvatar size={48} />
            <EqualOrb size={58} />
            <ProfileAvatar size={70} />
            <ProfileAvatar variant="female" size={70} />
            <Mascot size={40} />
            <Mascot size={40} dimmed />
          </Row>
        ),
        code: `<AiAvatar size={24} />
<AiAvatar size={36} />
<AiAvatar size={48} />
<EqualOrb size={58} />
<ProfileAvatar size={70} />
<ProfileAvatar variant="female" size={70} />
<Mascot size={40} />
<Mascot size={40} dimmed />`,
      },
    ],
    props: [
      {
        component: 'ContactAvatar',
        props: [
          opt('size', 'number', '48', 'Diameter in dp (list 48, header 36, contact info 96).'),
          opt('category', 'Category', "'na'", 'Caller category art. `na` = unknown caller (the sparkle).'),
          opt('initials', 'string', undefined, 'Initials for saved contacts; overrides category. First two letters, uppercased.'),
          opt('selected', 'boolean', undefined, 'Green check badge bottom-right (selection mode).'),
        ],
      },
      {
        component: 'CategoryAvatar',
        props: [
          opt('category', 'Category', "'na'", 'One of the 16 supplied categories.'),
          opt('size', 'number', '48', 'Diameter in dp.'),
        ],
      },
      {
        component: 'AiAvatar',
        props: [opt('size', '24 | 36 | 48 | number', '36', 'Orb size. The spec sizes are 24 / 36 / 48.')],
      },
      {
        component: 'EqualOrb',
        props: [
          opt('size', 'number', '36', 'Size of the opaque orb; the glow overflows the box (image drawn at size / 0.8).'),
          opt('label', 'string', "'Equal AI'", 'Accessible label.'),
        ],
      },
      {
        component: 'ProfileAvatar',
        props: [
          opt('variant', "'male' | 'female'", "'male'", 'Which supplied 3D character.'),
          opt('size', 'number', '72', 'Width in dp. Height is ~1.12 × width because the head rises above the ring.'),
          opt('label', 'string', "'Your profile'", 'Alt text.'),
        ],
      },
      {
        component: 'Mascot',
        props: [
          opt('size', 'number', '40', 'The robot fills size × size; the file keeps its padding.'),
          opt('dimmed', 'boolean', 'false', 'Greyscale at 70% opacity (assistant off).'),
          opt('label', 'string', "'Equal assistant'", 'Accessible label.'),
        ],
      },
    ],
    dos: [
      'Default unknown callers to `na` and incoming bubbles to `others` (the red telephone).',
      'Size ProfileAvatar by width only; its height follows so the head can rise above the ring.',
      'Use AiAvatar at 24 / 36 / 48 only.',
    ],
    donts: [
      'Never crop category art or the profile avatar; the circle and ring are part of the image.',
      "Don't draw your own green circle for the assistant; the orb swaps light (#00B140) and dark (lime) files by theme.",
      "Don't use initials for business callers; they get their category art.",
    ],
  },

  // ============================================================ Navigation
  {
    id: 'status-bar',
    name: 'Status bar',
    group: 'Navigation',
    summary: 'The Material 3 Android status bar (52dp: Roboto time; wifi, triangle signal and an upright battery) for mocks, flows and screen demos.',
    components: ['StatusBar'],
    importLine: "import { StatusBar } from '@equal/ds';",
    compose: ['StatusBar'],
    spec: '§1 Status Bar',
    tokens: ['bg/primary', 'text/primary'],
    examples: [
      {
        title: 'Default',
        render: () => <div style={{ width: 390 }}><StatusBar /></div>,
        code: `<StatusBar />`,
      },
      {
        title: 'On a white header',
        note: 'Match the background to the surface below it.',
        render: () => (
          <Col gap={0} style={{ width: '100%' }}>
            <StatusBar time="9:41" background="var(--eq-bg-surface)" />
            <AppBar title="Contact Info" surface="surface" onBack={noop} />
          </Col>
        ),
        code: `<StatusBar time="9:41" background="var(--eq-bg-surface)" />
<AppBar title="Contact Info" surface="surface" onBack={goBack} />`,
      },
    ],
    props: [
      {
        component: 'StatusBar',
        props: [
          opt('time', 'string', "'12:30'", 'Clock text (body2, tabular).'),
          opt('background', 'string', "'var(--eq-bg-primary)'", 'Any CSS colour or token var; match the header below.'),
        ],
      },
    ],
    dos: [
      'Use it only in mocks and flow demos; the app draws edge-to-edge and pads with `WindowInsets.statusBars`.',
      'Match `background` to the surface beneath (bg/surface under white headers).',
    ],
    donts: ["Don't add carrier names or extra icons; the Material 3 bar is time + wifi + signal + battery."],
  },
  {
    id: 'top-bar',
    name: 'Top bar',
    group: 'Navigation',
    summary: 'The Chats home bar: the vector Equal AI logo with the plan marker, plus the logo and mark on their own.',
    components: ['TopBar', 'EqualLogo', 'EqualMark'],
    importLine: "import { TopBar, EqualLogo, EqualMark } from '@equal/ds';",
    compose: ['TopBar', 'EqualLogo', 'EqualMark'],
    spec: '§2 Top Bar (Logo + Availability Status)',
    tokens: ['text/action', 'gradient/gold', 'text/on-dark'],
    examples: [
      {
        title: 'Default',
        render: () => <TopBar />,
        code: `<TopBar />`,
      },
      {
        title: 'Without a plan, with a trailing action',
        render: () => <TopBar plan={null} trailing={<IconButton icon="search" label="Search chats" />} />,
        code: `<TopBar plan={null} trailing={<IconButton icon="search" label="Search chats" />} />`,
      },
      {
        title: 'Logo and mark',
        render: () => (
          <Row gap={24} style={{ padding: 16 }}>
            <EqualLogo height={20} />
            <EqualLogo height={20} variant="wordmark" />
            <EqualMark width={23} />
          </Row>
        ),
        code: `<EqualLogo height={20} />
<EqualLogo height={20} variant="wordmark" />
<EqualMark width={23} />`,
      },
    ],
    props: [
      {
        component: 'TopBar',
        props: [
          opt('plan', 'string | null', "'Unlimited'", 'Plan marker label. null hides the PlanBadge.'),
          opt('trailing', 'ReactNode', undefined, 'Right-aligned content.'),
        ],
      },
      {
        component: 'EqualLogo',
        props: [
          opt('height', 'number', '20', 'Rendered height in dp; keeps the official 113 × 24 proportions.'),
          opt('color', 'string', "'var(--eq-text-action)'", 'Fill colour.'),
          opt('variant', "'full' | 'wordmark'", "'full'", "'full' = mark + wordmark, 'wordmark' = \"equal ai\" only."),
        ],
      },
      {
        component: 'EqualMark',
        props: [
          opt('width', 'number', '23', 'Width in dp.'),
          opt('color', 'string', "'var(--eq-text-action)'", 'Fill colour.'),
          opt('title', 'string', undefined, 'Accessible title. Without it the mark is decorative (aria-hidden).'),
        ],
      },
    ],
    dos: [
      'Use the vector logo in text/action so it is #00B140 in light and lime in dark.',
      'Keep the logo 20dp high in the top bar.',
    ],
    donts: [
      "Don't use a raster logo; the logo is coded as vector paths.",
      "Don't stretch or recolour the lockup outside text/action.",
    ],
  },
  {
    id: 'app-bar',
    name: 'App bar',
    group: 'Navigation',
    summary: 'The standard screen header with back, a subheading title and trailing actions, plus the uppercase section header that labels groups of rows.',
    components: ['AppBar', 'SectionHeader'],
    importLine: "import { AppBar, SectionHeader } from '@equal/ds';",
    compose: ['EqualAppBar', 'SectionHeader'],
    spec: 'Surfaces from the Typography screen inventory · App Bar, Section Header',
    tokens: ['bg/primary', 'bg/surface', 'shadow/header', 'text/primary', 'text/tertiary'],
    examples: [
      {
        title: 'Centred (flows)',
        render: () => <AppBar title="Personal Details" onBack={noop} />,
        code: `<AppBar title="Personal Details" onBack={goBack} />`,
      },
      {
        title: 'Start-aligned, elevated, with trailing',
        render: () => <AppBar title="Active Instructions" align="start" surface="surface" elevated onBack={noop} trailing={<TextButton leadingIcon="add">Add</TextButton>} />,
        code: `<AppBar title="Active Instructions" align="start" surface="surface" elevated
  onBack={goBack} trailing={<TextButton leadingIcon="add">Add</TextButton>} />`,
      },
      {
        title: 'Section headers',
        render: () => (
          <OnPrimary>
            <SectionHeader>Your assistant</SectionHeader>
            <SectionHeader qualifier="(Optional)">Guiding direction</SectionHeader>
            <SectionHeader action={<TextButton size="small">Clear all</TextButton>}>Recommended for you</SectionHeader>
          </OnPrimary>
        ),
        code: `<SectionHeader>Your assistant</SectionHeader>
<SectionHeader qualifier="(Optional)">Guiding direction</SectionHeader>
<SectionHeader action={<TextButton size="small">Clear all</TextButton>}>
  Recommended for you
</SectionHeader>`,
      },
    ],
    props: [
      {
        component: 'AppBar',
        props: [
          req('title', 'string', 'Screen title (subheading).'),
          opt('onBack', '() => void', undefined, 'Shows the back arrow when set.'),
          opt('trailing', 'ReactNode', undefined, 'Right-side actions.'),
          opt('statusBar', 'boolean', 'false', 'Renders a StatusBar above, in the same colour.'),
          opt('surface', "'primary' | 'surface'", "'primary'", 'Background: bg/primary or bg/surface.'),
          opt('elevated', 'boolean', undefined, 'Adds shadow/header.'),
          opt('align', "'center' | 'start'", "'center'", 'Centred title at 56dp (flows) or start-aligned at 64dp.'),
        ],
      },
      {
        component: 'SectionHeader',
        props: [
          req('children', 'string', 'Header text; uppercased in the string (overline, text/tertiary).'),
          opt('action', 'ReactNode', undefined, 'Trailing action, e.g. a small TextButton.'),
          opt('qualifier', 'string', undefined, 'Caption Regular text after the label, e.g. "(Optional)" or a count.'),
          opt('inset', 'number', '20', 'Side padding in dp (You tab: 20).'),
        ],
      },
    ],
    dos: [
      'Centre the title in flow screens (Personal Details, Your Addresses, Contact Info).',
      'Keep SectionHeader side inset equal to the RowGroup inset below it (20 on the You tab).',
    ],
    donts: [
      "Don't write section headers in capitals in source; pass normal text and let the component uppercase it.",
      "Don't put a filled Button in the app bar; trailing actions are icon or text buttons.",
    ],
  },
  {
    id: 'bottom-nav',
    name: 'Bottom nav',
    group: 'Navigation',
    summary: 'The floating two-tab pill (Chats, You) with an unread count, beside the Equal orb FAB that opens the assistant.',
    components: ['BottomNav', 'Fab'],
    importLine: "import { BottomNav, Fab } from '@equal/ds';",
    compose: ['BottomNavBar', 'EqualFab'],
    spec: '§4 Tab Bar (Bottom Navigation)',
    tokens: ['bg/surface', 'shadow/nav', 'nav/selected-bg', 'text/action', 'text/tertiary', 'text/secondary', 'button/primary', 'text/on-action'],
    examples: [
      {
        title: 'Interactive',
        render: () => <BottomNavDemo />,
        code: `const [tab, setTab] = useState<NavTab>('chats');

<BottomNav active={tab} onChange={setTab} unread={7} />`,
      },
      {
        title: 'You active, female avatar, no FAB',
        render: () => (
          <OnPrimary pad="24px 0">
            <div style={{ display: 'flex', justifyContent: 'center' }}>
              <BottomNav active="you" avatar="female" showFab={false} />
            </div>
          </OnPrimary>
        ),
        code: `<BottomNav active="you" avatar="female" showFab={false} />`,
      },
      {
        title: 'FAB on its own',
        render: () => (
          <div style={{ display: 'flex', justifyContent: 'center', padding: 24 }}>
            <Fab />
          </div>
        ),
        code: `<Fab onClick={openAssistant} />`,
      },
    ],
    props: [
      {
        component: 'BottomNav',
        props: [
          req('active', "'chats' | 'you'", 'The selected tab (NavTab).'),
          opt('onChange', '(tab: NavTab) => void', undefined, 'Called when a tab is tapped.'),
          opt('unread', 'number', '0', 'Unread count badge on the Chats icon (hidden at 0).'),
          opt('showFab', 'boolean', 'true', 'Show the assistant FAB beside the pill.'),
          opt('onFabClick', '() => void', undefined, 'FAB tap handler.'),
          opt('avatar', "'male' | 'female'", "'male'", 'Which ProfileAvatar the You tab shows.'),
        ],
      },
      {
        component: 'Fab',
        props: [
          opt('onClick', '() => void', undefined, 'Tap handler.'),
          opt('label', 'string', "'Talk to Equal AI'", 'Accessible label.'),
          opt('size', 'number', '58', 'Orb size in dp.'),
        ],
      },
    ],
    dos: [
      'Float the nav over content and leave ~104dp bottom padding so the last row is reachable.',
      'Pass the user\'s gender as `avatar` so the You tab matches the profile avatar.',
    ],
    donts: [
      "Don't add a third tab; the pill is Chats and You.",
      "Don't replace the FAB orb with an icon; the orb carries its own glow.",
    ],
  },
  {
    id: 'chat-header',
    name: 'Chat header',
    group: 'Navigation',
    summary: 'Headers for the chat surfaces: the in-conversation header (with the Takeover pill on a live call), the chat-list selection header, and the home header with the assistant toggle.',
    components: ['ChatHeader', 'TakeoverPill', 'SelectionHeader', 'HomeHeader'],
    importLine: "import { ChatHeader, TakeoverPill, SelectionHeader, HomeHeader } from '@equal/ds';",
    compose: ['ChatHeader', 'TakeoverPill', 'SelectionChatHeader', 'HomeHeader'],
    spec: '§5 Chat Header (In-Conversation)',
    tokens: ['bg/surface', 'bg/primary', 'shadow/header', 'gradient/takeover', 'action/stroke/unread', 'shadow/glass', 'text/action', 'text/primary', 'text/tertiary', 'button/danger'],
    examples: [
      {
        title: 'Default',
        render: () => <ChatHeader name={amit.name} subtitle={amit.phone} avatar={{ initials: 'AN' }} onBack={noop} onCall={noop} onMore={noop} />,
        code: `<ChatHeader name="Amit Nanda" subtitle="+91 98717 46113"
  avatar={{ initials: 'AN' }} onBack={goBack} onCall={call} onMore={openInfo} />`,
      },
      {
        title: 'Live call',
        note: 'Takeover replaces both the call and more-options buttons.',
        render: () => <ChatHeader name={arnav.name} subtitle="You have a visitor" live onBack={noop} onTakeover={noop} />,
        code: `<ChatHeader name="Arnav Raj" subtitle="You have a visitor" live
  onBack={goBack} onTakeover={takeover} />`,
      },
      {
        title: 'Selection mode',
        note: 'Tap rows to select; Select All toggles every row.',
        render: () => <SelectionDemo />,
        code: `<SelectionHeader count={1} allSelected={false}
  onSelectAll={selectAll} onBack={exit} onMarkRead={markRead} onDelete={confirmDelete} />`,
      },
      {
        title: 'Home',
        render: () => <HomeHeaderDemo />,
        code: `const [on, setOn] = useState(true);

<HomeHeader assistantOn={on} onAssistantChange={setOn} />`,
      },
      {
        title: 'Takeover pill',
        render: () => (
          <div style={pad16}>
            <TakeoverPill />
          </div>
        ),
        code: `<TakeoverPill onClick={takeover} />`,
      },
    ],
    props: [
      {
        component: 'ChatHeader',
        props: [
          req('name', 'string', 'Contact name (subheading, truncates).'),
          opt('subtitle', 'string', undefined, 'Phone number (tabular automatically when it is digits) or a status line ("You have a visitor").'),
          opt('avatar', 'ContactAvatarProps', undefined, 'Category or initials for the 36dp avatar.'),
          opt('onBack', '() => void', undefined, 'Back arrow handler.'),
          opt('onCall', '() => void', undefined, 'Call button handler.'),
          opt('onMore', '() => void', undefined, 'More-options handler.'),
          opt('onTakeover', '() => void', undefined, 'Takeover pill handler (live calls).'),
          opt('live', 'boolean', undefined, 'Live call: shows the Takeover pill instead of call and more-options.'),
          opt('statusBar', 'boolean', 'false', 'Renders a StatusBar above on bg/surface.'),
        ],
      },
      {
        component: 'TakeoverPill',
        props: [
          opt('onClick', '() => void', undefined, 'Tap handler.'),
          opt('label', 'string', "'Takeover'", 'Pill label.'),
        ],
      },
      {
        component: 'SelectionHeader',
        props: [
          req('count', 'number', 'Number selected; shown as "N Selected".'),
          req('allSelected', 'boolean', 'State of the Select All checkbox.'),
          opt('onSelectAll', '(all: boolean) => void', undefined, 'Select All toggled.'),
          opt('onBack', '() => void', undefined, 'Exit selection.'),
          opt('onMarkRead', '() => void', undefined, 'Mark read pill.'),
          opt('onDelete', '() => void', undefined, 'Trash icon (danger).'),
          opt('statusBar', 'boolean', 'false', 'Renders a StatusBar above.'),
        ],
      },
      {
        component: 'HomeHeader',
        props: [
          req('assistantOn', 'boolean', 'Assistant toggle state.'),
          opt('onAssistantChange', '(on: boolean) => void', undefined, 'Toggle handler.'),
          opt('plan', 'string | null', "'Unlimited'", 'Plan marker in the top bar; null hides it.'),
          opt('statusBar', 'boolean', 'true', 'Renders the StatusBar.'),
          opt('trailing', 'ReactNode', undefined, 'Top bar trailing content.'),
        ],
      },
    ],
    dos: [
      'Show the phone number as the subtitle normally and a status line ("You have a visitor") during a live call.',
      'During a live call show only the Takeover pill; the call and more buttons go away.',
      'Route selection-mode Delete through the delete confirm sheet.',
    ],
    donts: [
      "Don't show Takeover alongside the call button; that is two actions for the same intent.",
      "Don't truncate the phone number with custom styles; it is tabular and one line.",
    ],
  },

  // ============================================================ Chat
  {
    id: 'chat-list-item',
    name: 'Chat list item',
    group: 'Chat',
    summary: 'A row in the Chats list with avatar, name, date, preview, unread count and the small action chips; states come from `unread` and `selected`.',
    components: ['ChatListItem'],
    importLine: "import { ChatListItem } from '@equal/ds';",
    compose: ['ChatListItem'],
    spec: '§6 Chat List Item',
    tokens: ['bg/surface', 'action-card/highlight', 'text/primary', 'text/secondary', 'text/action', 'text/tertiary', 'button/primary', 'text/on-action'],
    examples: [
      {
        title: 'Unread and read',
        render: () => (
          <Hairlines>
            <ChatListItem name={arnav.name} preview={arnav.preview} date={arnav.date} unread={arnav.unread} actions={arnav.actions} avatar={{ category: arnav.category }} />
            <ChatListItem name={amit.name} preview={amit.preview} date={amit.date} actions={amit.actions} avatar={{ initials: 'AN' }} />
          </Hairlines>
        ),
        code: `<ChatListItem name="Arnav Raj" preview="You have a visitor" date="Yesterday"
  unread={5} avatar={{ category: 'na' }}
  actions={[
    { label: 'Call Back', emoji: '📞' },
    { label: 'Update Address', emoji: '✏️' },
    { label: 'Add to Calendar', emoji: '🗓️' },
  ]} />
<ChatListItem name="Amit Nanda" preview="Count me in for Sunday 8 PM 👍" date="12 July"
  avatar={{ initials: 'AN' }} actions={[{ label: 'Call Back', emoji: '📞' }]} />`,
      },
      {
        title: 'Long-press to select',
        note: 'Long-press (or right-click) the row; tap to deselect.',
        render: () => <ChatListLongPressDemo />,
        code: `<ChatListItem name="Dr Mehta Clinic" preview="Appointment confirmed for tomorrow, 10 AM"
  date="03:21" unread={2} avatar={{ category: 'others' }}
  actions={[{ label: 'Add to Calendar', emoji: '🗓️' }, { label: 'Call Back', emoji: '📞' }]}
  selected={selected} onLongPress={() => setSelected(true)} />`,
      },
      {
        title: 'The list',
        render: () => (
          <Hairlines>
            {sampleChats.slice(2, 6).map((c) => (
              <ChatListItem key={c.id} name={c.name} preview={c.preview} date={c.date} unread={c.unread} actions={c.actions} avatar={c.initials ? { initials: c.initials } : { category: c.category }} />
            ))}
          </Hairlines>
        ),
        code: `{chats.map((c) => (
  <ChatListItem key={c.id} name={c.name} preview={c.preview} date={c.date}
    unread={c.unread} actions={c.actions}
    avatar={c.initials ? { initials: c.initials } : { category: c.category }} />
))}`,
      },
    ],
    props: [
      {
        component: 'ChatListItem',
        props: [
          req('name', 'string', 'Contact name (subheading, one line).'),
          req('preview', 'string', 'Last message or summary (body1, one line).'),
          req('date', 'string', 'Date or time (micro); text/action when unread or selected.'),
          opt('unread', 'number', '0', 'Unread count. > 0 makes the row unread and shows the CountBadge.'),
          opt('actions', 'ChatAction[]', '[]', '{ label, emoji?, onClick? }[] rendered as SmallActionChips. Chip taps do not trigger the row.'),
          opt('selected', 'boolean', undefined, 'Selection state: highlight fill, check on the avatar, outlined chips.'),
          opt('avatar', 'ContactAvatarProps', undefined, 'Category or initials.'),
          opt('onClick', '() => void', undefined, 'Row tap (also Enter).'),
          opt('onLongPress', '() => void', undefined, 'Fires after a 500ms press or on right-click.'),
        ],
      },
    ],
    dos: [
      'Give unknown callers their category art and saved contacts their initials.',
      'Offer the next step as chips on the row (📞 Call Back, 🗓️ Add to Calendar) so it is one tap from the list.',
      'Use long-press to enter selection mode.',
    ],
    donts: [
      "Don't set chip states yourself; the row derives them from unread and selected.",
      "Don't let the preview wrap; it is one line with an ellipsis.",
    ],
  },
  {
    id: 'chat-bubble',
    name: 'Chat bubble',
    group: 'Chat',
    summary: 'Message bubbles for a call thread: outgoing (Equal AI or you on the call), incoming (the caller) and assistant (Equal AI talking to you), stacked with MessageList.',
    components: ['ChatBubble', 'MessageList'],
    importLine: "import { ChatBubble, MessageList } from '@equal/ds';",
    compose: ['ChatBubble'],
    spec: '§11 Chat Bubbles',
    tokens: ['chat-bubble/user-bg', 'chat-bubble/user-title', 'chat-bubble/other-bg', 'chat-bubble/contact-title', 'bg/surface', 'text/action', 'text/primary', 'text/tertiary'],
    examples: [
      {
        title: 'A call thread',
        render: () => (
          <div style={{ padding: '16px 0' }}>
            <MessageList>
              <ChatBubble kind="outgoing" sender="Equal AI" time="03:21">Hi, who is this?</ChatBubble>
              <ChatBubble kind="incoming" sender="Dr Mehta Clinic" time="03:21">This is Priya from Dr. Mehta's clinic. Your appointment is confirmed for tomorrow.</ChatBubble>
              <ChatBubble kind="outgoing" sender="You" time="03:21">Yes, I'll be there at 10 AM</ChatBubble>
              <ChatBubble kind="incoming" sender="Dr Mehta Clinic" time="03:21">Great. Please bring your previous reports too.</ChatBubble>
              <ChatBubble kind="assistant" sender="Equal AI" time="03:21">Hey Keshav! Here is the summary of your call</ChatBubble>
            </MessageList>
          </div>
        ),
        code: `<MessageList>
  <ChatBubble kind="outgoing" sender="Equal AI" time="03:21">Hi, who is this?</ChatBubble>
  <ChatBubble kind="incoming" sender="Dr Mehta Clinic" time="03:21">
    This is Priya from Dr. Mehta's clinic. Your appointment is confirmed for tomorrow.
  </ChatBubble>
  <ChatBubble kind="outgoing" sender="You" time="03:21">Yes, I'll be there at 10 AM</ChatBubble>
  <ChatBubble kind="assistant" sender="Equal AI" time="03:21">
    Hey Keshav! Here is the summary of your call
  </ChatBubble>
</MessageList>`,
      },
      {
        title: 'Tails and grouping',
        note: 'Follow-ups set first={false}: 4dp gap, no avatar, no sender line.',
        render: () => (
          <div style={{ padding: '16px 0' }}>
            <MessageList>
              <ChatBubble kind="incoming" category="real-estate" sender="Vijay · NoBroker" time="03:21" tail>I have an offer on a 2BHK flat in Sector 12.</ChatBubble>
              <ChatBubble kind="incoming" category="real-estate" first={false} time="03:21">Are you free for a site visit this weekend?</ChatBubble>
              <ChatBubble kind="outgoing" sender="Equal AI" time="03:22" tail>Thanks, but Keshav is not interested right now.</ChatBubble>
            </MessageList>
          </div>
        ),
        code: `<ChatBubble kind="incoming" category="real-estate" sender="Vijay · NoBroker" time="03:21" tail>
  I have an offer on a 2BHK flat in Sector 12.
</ChatBubble>
<ChatBubble kind="incoming" category="real-estate" first={false} time="03:21">
  Are you free for a site visit this weekend?
</ChatBubble>
<ChatBubble kind="outgoing" sender="Equal AI" time="03:22" tail>
  Thanks, but Keshav is not interested right now.
</ChatBubble>`,
      },
      {
        title: 'Bare content',
        note: '`bare` removes padding for custom content such as a call summary.',
        render: () => (
          <div style={{ padding: '16px 0' }}>
            <MessageList>
              <ChatBubble kind="assistant" bare>
                <CallSummary noRecording summary="Delivery agent will arrive between 4 and 5 PM." time="03:21" width={240} />
              </ChatBubble>
            </MessageList>
          </div>
        ),
        code: `<ChatBubble kind="assistant" bare>
  <CallSummary noRecording summary="Delivery agent will arrive between 4 and 5 PM." time="03:21" />
</ChatBubble>`,
      },
    ],
    props: [
      {
        component: 'ChatBubble',
        props: [
          req('kind', "'outgoing' | 'incoming' | 'assistant'", 'outgoing = your side of the call (Equal AI speaking for you, or You), right; incoming = the caller, left; assistant = Equal AI talking to you, left.'),
          opt('sender', 'string', undefined, 'Sender line (body2), shown on the first bubble of a group.'),
          opt('time', 'string', undefined, 'Timestamp (micro, right-aligned).'),
          req('children', 'ReactNode', 'Message text (body1), or custom content with `bare`.'),
          opt('first', 'boolean', 'true', 'First bubble of a group: gets the sender line, avatar and (with tail) the tail.'),
          opt('tail', 'boolean', 'false', 'Draws the tail on the first bubble.'),
          opt('category', 'Category', "'others'", 'Caller category for the incoming avatar.'),
          opt('bare', 'boolean', undefined, 'Removes inner padding, sender and time for custom content.'),
          opt('maxWidth', 'number', '264', 'Max bubble width in dp (from the 360-wide designs; ≈ 68% of the 390 frame).'),
        ],
      },
      {
        component: 'MessageList',
        props: [req('children', 'ReactNode', 'Bubbles and thread indicators; applies the 4 / 12dp group rhythm.')],
      },
    ],
    dos: [
      'Use `outgoing` with sender "Equal AI" when the assistant speaks for the user on a call, and "You" after a takeover.',
      'Use `assistant` only when Equal AI is talking to the user, not to the caller.',
      'Set first={false} on consecutive bubbles from the same sender.',
    ],
    donts: [
      "Don't mix up outgoing and assistant; the green byline means Equal AI is addressing you.",
      "Don't put custom cards inside a padded bubble; use `bare`.",
    ],
  },
  {
    id: 'call-summary',
    name: 'Call summary',
    group: 'Chat',
    summary: 'The post-call card in a thread: a recording player with a coded waveform and speed pill, the assistant\'s summary, and an optional share footer or "Instruction Applied" tag.',
    components: ['CallSummary', 'VoicePlayer'],
    importLine: "import { CallSummary, VoicePlayer } from '@equal/ds';",
    compose: ['CallSummaryBubble', 'VoiceNotePlayer'],
    spec: '§11 Chat Bubbles · Voice note + Call Summary bubble',
    tokens: ['bg/surface', 'voice/player-bg', 'stroke/subtle', 'waveform/bar', 'waveform/bar-played', 'playback/pill-bg', 'text/on-dark', 'text/tertiary', 'text/primary', 'text/action'],
    examples: [
      {
        title: 'With recording and share',
        note: 'Play to watch the waveform fill; the speed pill cycles 1x → 1.2x → 1.5x → 2x.',
        render: () => (
          <div style={{ padding: 16, display: 'flex', gap: 8 }}>
            <span style={{ width: 28, display: 'flex', justifyContent: 'center' }}><AiAvatar size={24} /></span>
            <CallSummary summary="Amit invited you to dinner this Sunday, 8 PM at his place. He asked you to confirm by tonight." time="03:21" onShare={noop} />
          </div>
        ),
        code: `<CallSummary
  summary="Amit invited you to dinner this Sunday, 8 PM at his place. He asked you to confirm by tonight."
  time="03:21" onShare={share} />`,
      },
      {
        title: 'Instruction applied',
        render: () => (
          <div style={pad16}>
            <CallSummary summary="Priya from Dr. Mehta's clinic confirmed your appointment for tomorrow at 10 AM. Bring your previous reports." time="03:32" width={264} tag={{ emoji: '🪄', label: 'Instruction Applied' }} />
          </div>
        ),
        code: `<CallSummary
  summary="Priya from Dr. Mehta's clinic confirmed your appointment for tomorrow at 10 AM. Bring your previous reports."
  time="03:32" tag={{ emoji: '🪄', label: 'Instruction Applied' }} />`,
      },
      {
        title: 'Summary only',
        render: () => (
          <div style={pad16}>
            <CallSummary noRecording summary="Vijay from NoBroker called with an offer on a 2BHK flat in Sector 12. You said you were not interested and ended the call." time="03:21" />
          </div>
        ),
        code: `<CallSummary noRecording
  summary="Vijay from NoBroker called with an offer on a 2BHK flat in Sector 12. You said you were not interested and ended the call."
  time="03:21" />`,
      },
      {
        title: 'Voice player',
        render: () => (
          <div style={{ padding: 16, width: 268 }}>
            <VoicePlayer duration={94} seed={7} />
          </div>
        ),
        code: `<VoicePlayer duration={94} seed={7} />`,
      },
    ],
    props: [
      {
        component: 'CallSummary',
        props: [
          req('summary', 'ReactNode', 'The summary text (body1).'),
          opt('duration', 'number', '157', 'Recording length in seconds.'),
          opt('time', 'string', undefined, 'Timestamp (micro).'),
          opt('label', 'string', "'Call Summary'", 'Caption label above the summary.'),
          opt('onShare', '() => void', undefined, 'Adds the divider + Share footer.'),
          opt('noRecording', 'boolean', undefined, 'Hide the recording player.'),
          opt('width', 'number', '268', 'Card width in dp.'),
          opt('tag', '{ emoji?: string; label: string }', undefined, "Pill left of the timestamp, e.g. { emoji: '🪄', label: 'Instruction Applied' }."),
          opt('recording', 'ReactNode', undefined, 'Replaces the recording player, e.g. `<LockedRecording>` (gold, crown-locked) on the free plan.'),
          opt('footer', 'ReactNode', undefined, 'Rows under the summary, e.g. `<SummaryActions>` (Call Back · Add to Calendar).'),
        ],
      },
      {
        component: 'VoicePlayer',
        props: [
          req('duration', 'number', 'Recording length in seconds.'),
          opt('position', 'number', undefined, 'Controlled progress (0–duration). Omit to let the player simulate playback.'),
          opt('seed', 'number', '42', 'Seed for the deterministic waveform, so the same recording always draws the same bars.'),
          opt('bars', 'number', '28', 'Number of waveform bars.'),
        ],
      },
    ],
    dos: [
      'Write the summary as the outcome for the user ("Amit invited you to dinner… confirm by tonight"), not a transcript.',
      'Tag the card 🪄 "Instruction Applied" when one of the user\'s instructions handled the call.',
      'Keep the same `seed` for a recording so its waveform is stable.',
    ],
    donts: [
      "Don't use an image for the waveform; it is coded.",
      "Don't show the player for calls without a recording; use `noRecording`.",
    ],
  },
  {
    id: 'missed-call',
    name: 'Missed call card',
    group: 'Chat',
    summary: 'The thread card for a call the assistant couldn’t pick up. One component, three cases: we know why the caller usually calls, we know only their name, or we know nothing. Every case ends in Call back.',
    components: ['MissedCallCard'],
    importLine: "import { MissedCallCard } from '@equal/ds';",
    compose: ['MissedCallCard'],
    tokens: ['bg/surface', 'missed-call/bg', 'missed-call/stroke', 'missed-call/icon-bg', 'missed-call/icon', 'text/primary', 'text/secondary', 'text/tertiary', 'text/action', 'stroke/subtle'],
    examples: [
      {
        title: 'With a reason',
        note: 'The caller’s art carries the missed badge; LIKELY CALLS FOR bolds the key words. 👍 / 👎 tell us whether the reason was right.',
        render: () => {
          const [fb, setFb] = useState<'up' | 'down' | null>(null);
          return (
            <div style={pad16}>
              <MissedCallCard caller="Kuldeep Solanki" category="real-estate" reason={<>Promote <b>L&amp;T Realty residential</b> projects.</>} time="03:21" onCallBack={noop} feedback={fb} onFeedback={setFb} />
            </div>
          );
        },
        code: `<MissedCallCard
  caller="Kuldeep Solanki" category="real-estate"
  reason={<>Promote <b>L&amp;T Realty residential</b> projects.</>}
  time="03:21" onCallBack={callBack}
  feedback={feedback} onFeedback={setFeedback} />`,
      },
      {
        title: 'Caller unknown',
        render: () => (
          <div style={pad16}>
            <MissedCallCard time="3:31 PM" onCallBack={noop} />
          </div>
        ),
        code: `<MissedCallCard time="3:31 PM" onCallBack={callBack} />`,
      },
      {
        title: 'Name only',
        render: () => (
          <div style={pad16}>
            <MissedCallCard caller="Valluri" time="3:31 PM" onCallBack={noop} />
          </div>
        ),
        code: `<MissedCallCard caller="Valluri" time="3:31 PM" onCallBack={callBack} />`,
      },
    ],
    props: [
      {
        component: 'MissedCallCard',
        props: [
          opt('caller', 'string', undefined, 'Caller name: “{caller} Called”. Leave it out when we couldn’t tell who called (“Missed by the assistant”).'),
          opt('reason', 'ReactNode', undefined, 'Why this caller usually calls, under LIKELY CALLS FOR. Bold the key words. Moves the time below it.'),
          opt('category', 'Category', undefined, 'Caller art with the missed badge (with a reason). Otherwise the red missed-call disc.'),
          opt('time', 'string', undefined, 'Timestamp.'),
          opt('onCallBack', '() => void', undefined, 'The Call back row. Opens the Call Back task.'),
          opt('callBackLabel', 'string', "'Call back'", 'CTA label.'),
          opt('feedback', "'up' | 'down' | null", 'null', 'Controlled thumbs state.'),
          opt('onFeedback', '(v) => void', undefined, 'Shows 👍 / 👎 under the card. Tapping the selected one clears it.'),
          opt('width', 'number | string', '320', 'Card width.'),
        ],
      },
    ],
    dos: [
      'Pick the case from what we know: reason + art, then name only, then unknown. Never invent a reason.',
      'Show thumbs only with a reason, since they rate the reason.',
      'Call back opens the same Call Back task as everywhere else (Flows → Call Back).',
    ],
    donts: [
      'Don’t colour the whole card red; the warm tint says “missed”, not “error”.',
      'Don’t show caller art without a reason; the disc keeps the three cases distinct.',
    ],
  },
  {
    id: 'call-status',
    name: 'Call status',
    group: 'Chat',
    summary: 'Centred thread markers: the day divider, call start/end events, and a notice when the thread switches to the assistant.',
    components: ['DayDivider', 'ThreadEvent', 'ThreadNotice'],
    importLine: "import { DayDivider, ThreadEvent, ThreadNotice } from '@equal/ds';",
    compose: ['DayDivider', 'ThreadEvent', 'ThreadNotice'],
    spec: '§21 Call Status Indicators',
    tokens: ['bg/surface', 'stroke/subtle', 'text/primary', 'text/secondary', 'notice/bg'],
    examples: [
      {
        title: 'All indicators',
        render: () => (
          <div style={{ padding: '16px 0' }}>
            <MessageList>
              <DayDivider label="Today" />
              <ThreadEvent label="Call Started 03:31" />
              <ThreadEvent label="Call Ended 03:31" />
              <ThreadNotice>Call has ended. You are now talking to your AI assistant.</ThreadNotice>
            </MessageList>
          </div>
        ),
        code: `<MessageList>
  <DayDivider label="Today" />
  <ThreadEvent label="Call Started 03:31" />
  <ThreadEvent label="Call Ended 03:31" />
  <ThreadNotice>Call has ended. You are now talking to your AI assistant.</ThreadNotice>
</MessageList>`,
      },
      {
        title: 'In a thread',
        render: () => (
          <ChatWallpaper style={{ padding: '16px 0' }}>
            <MessageList>
              <DayDivider label="12 July" />
              <ThreadEvent label="Call Started 18:02" />
              <ChatBubble kind="outgoing" sender="Equal AI" time="18:02">Hi, who is this?</ChatBubble>
              <ChatBubble kind="incoming" category="na" sender="Amit Nanda" time="18:02">It's Amit. Dinner at mine this Sunday, 8 PM?</ChatBubble>
              <ThreadEvent label="Call Ended 18:04" />
              <ThreadNotice>Call has ended. You are now talking to your AI assistant.</ThreadNotice>
            </MessageList>
          </ChatWallpaper>
        ),
        code: `<ChatWallpaper>
  <MessageList>
    <DayDivider label="12 July" />
    <ThreadEvent label="Call Started 18:02" />
    <ChatBubble kind="outgoing" sender="Equal AI" time="18:02">Hi, who is this?</ChatBubble>
    …
    <ThreadEvent label="Call Ended 18:04" />
    <ThreadNotice>Call has ended. You are now talking to your AI assistant.</ThreadNotice>
  </MessageList>
</ChatWallpaper>`,
      },
    ],
    props: [
      { component: 'DayDivider', props: [req('label', 'string', '"Today", "12 July".')] },
      { component: 'ThreadEvent', props: [req('label', 'string', '"Call Started 03:31", "Call Ended 03:31".')] },
      { component: 'ThreadNotice', props: [req('children', 'ReactNode', 'Notice text (captionWrap, centred, max width 240).')] },
    ],
    dos: [
      'Bracket every call with Call Started / Call Ended events.',
      'Use a ThreadNotice when the conversation changes hands ("You are now talking to your AI assistant").',
    ],
    donts: [
      "Don't use a ThreadNotice for ordinary messages; it is for system state changes only.",
      "Don't style these as bubbles; they are centred pills without avatars.",
    ],
  },
  {
    id: 'ai-panel',
    name: 'AI panel',
    group: 'Chat',
    summary: 'The assistant\'s bottom panel inside a conversation: a suggestion from the orb, large action chips and a composer; AiSuggestion is the avatar-plus-prompt line on its own.',
    components: ['AiChatPanel', 'AiSuggestion'],
    importLine: "import { AiChatPanel, AiSuggestion } from '@equal/ds';",
    compose: ['AiChatPanel', 'AiSuggestion'],
    spec: '§12b AI Chat — Within other conversation · §16 AI Suggestion',
    tokens: ['bg/surface', 'text/secondary', 'text/primary', 'gradient/chip-large', 'action/stroke/unread'],
    examples: [
      {
        title: 'After a call',
        render: () => (
          <div style={{ paddingTop: 40, background: 'var(--eq-bg-primary)' }}>
            <AiChatPanel
              prompt="Want me to send Amit a quick reply on WhatsApp?"
              actions={[
                { label: 'Reply on WhatsApp', emoji: '💬' },
                { label: 'Add to Calendar', emoji: '🗓️' },
              ]}
            />
          </div>
        ),
        code: `<AiChatPanel
  prompt="Want me to send Amit a quick reply on WhatsApp?"
  actions={[
    { label: 'Reply on WhatsApp', emoji: '💬' },
    { label: 'Add to Calendar', emoji: '🗓️' },
  ]}
  composer={{ onSend: send }} />`,
      },
      {
        title: 'During a live call, no composer',
        render: () => (
          <div style={{ paddingTop: 40, background: 'var(--eq-bg-primary)' }}>
            <AiChatPanel
              prompt="Arnav is at the gate with a parcel. Want to take over?"
              actions={[
                { label: 'Takeover', emoji: '📞' },
                { label: 'Ask to reschedule', emoji: '🗓️' },
              ]}
              composer={false}
            />
          </div>
        ),
        code: `<AiChatPanel
  prompt="Arnav is at the gate with a parcel. Want to take over?"
  actions={[
    { label: 'Takeover', emoji: '📞' },
    { label: 'Ask to reschedule', emoji: '🗓️' },
  ]}
  composer={false} />`,
      },
      {
        title: 'AI suggestion',
        render: () => (
          <Col gap={16} style={{ padding: 16 }}>
            <AiSuggestion>Want me to send Amit a quick reply on WhatsApp?</AiSuggestion>
            <AiSuggestion tone="secondary">I can also add Sunday dinner to your calendar.</AiSuggestion>
          </Col>
        ),
        code: `<AiSuggestion>Want me to send Amit a quick reply on WhatsApp?</AiSuggestion>
<AiSuggestion tone="secondary">I can also add Sunday dinner to your calendar.</AiSuggestion>`,
      },
    ],
    props: [
      {
        component: 'AiChatPanel',
        props: [
          req('prompt', 'ReactNode', "The assistant's suggestion (body1, text/secondary)."),
          opt('actions', '{ label: string; emoji?: string; onClick?: () => void }[]', '[]', 'LargeActionChips under the prompt.'),
          opt('composer', 'ComposerProps | false', '{}', 'Props for the built-in Composer, or false to hide it.'),
        ],
      },
      {
        component: 'AiSuggestion',
        props: [
          req('children', 'ReactNode', 'Suggestion text (body1, wraps).'),
          opt('tone', "'primary' | 'secondary'", "'primary'", 'Text colour.'),
        ],
      },
    ],
    dos: [
      'Phrase the prompt as a question the user can answer with one chip ("Want me to send Amit a quick reply on WhatsApp?").',
      'Offer at most two or three emoji chips, the most likely one first.',
      'Hide the composer (composer={false}) when only a quick decision makes sense, like during a live call.',
    ],
    donts: [
      "Don't put a filled Button in the panel; the actions are large chips.",
      "Don't write the prompt in green; the assistant's words are text/secondary here.",
    ],
  },
  {
    id: 'chat-wallpaper',
    name: 'Chat wallpaper',
    group: 'Chat',
    summary: 'The doodle pattern behind chat threads and the instruction setup chat, tinted by token so one tile serves light and dark.',
    components: ['ChatWallpaper'],
    importLine: "import { ChatWallpaper } from '@equal/ds';",
    compose: ['ChatWallpaper'],
    spec: 'Flows · Chat wallpaper',
    tokens: ['chat/wallpaper-bg', 'chat/wallpaper-pattern'],
    examples: [
      {
        title: 'Behind a thread',
        render: () => (
          <ChatWallpaper style={{ padding: '16px 0', minHeight: 320 }}>
            <MessageList>
              <DayDivider label="Today" />
              <ChatBubble kind="outgoing" sender="Equal AI" time="03:21">Hi, who is this?</ChatBubble>
              <ChatBubble kind="incoming" sender="Dr Mehta Clinic" time="03:21">This is Priya from Dr. Mehta's clinic. Your appointment is confirmed for tomorrow.</ChatBubble>
            </MessageList>
          </ChatWallpaper>
        ),
        code: `<ChatWallpaper style={{ minHeight: '100%' }}>
  <MessageList>
    <DayDivider label="Today" />
    <ChatBubble kind="outgoing" sender="Equal AI" time="03:21">Hi, who is this?</ChatBubble>
    …
  </MessageList>
</ChatWallpaper>`,
      },
    ],
    props: [
      {
        component: 'ChatWallpaper',
        props: [
          req('children', 'ReactNode', 'Thread content.'),
          opt('style', 'CSSProperties', undefined, 'Merged onto the wrapper (e.g. minHeight, padding, flex).'),
        ],
      },
    ],
    dos: [
      'Use it behind chat threads and the "Setting up instruction" chat only.',
      'Give it minHeight: 100% so the pattern fills short threads.',
    ],
    donts: [
      "Don't put the wallpaper behind lists or settings screens.",
      "Don't tint the pattern yourself; it reads chat/wallpaper-pattern in both themes.",
    ],
  },

  // ============================================================ Cards
  {
    id: 'assistant-toggle',
    name: 'Assistant toggle',
    group: 'Cards',
    summary: 'The card at the top of Chats and the You tab that turns the AI assistant on or off, with the mascot dimming when it is off.',
    components: ['AssistantToggle'],
    importLine: "import { AssistantToggle } from '@equal/ds';",
    compose: ['AssistantToggleCard'],
    spec: '§3 Assistant Availability Toggle',
    tokens: ['gradient/assistant-toggle', 'bg/surface', 'stroke/subtle', 'text/primary', 'text/secondary', 'button/primary', 'button/disabled'],
    examples: [
      {
        title: 'Interactive',
        render: () => (
          <div style={{ padding: 12 }}>
            <AssistantToggleDemo />
          </div>
        ),
        code: `const [on, setOn] = useState(true);

<AssistantToggle on={on} onChange={setOn} />`,
      },
      {
        title: 'On and off',
        render: () => (
          <Col gap={12} style={{ padding: 12 }}>
            <AssistantToggle on />
            <AssistantToggle on={false} />
          </Col>
        ),
        code: `<AssistantToggle on />
<AssistantToggle on={false} />`,
      },
    ],
    props: [
      {
        component: 'AssistantToggle',
        props: [
          req('on', 'boolean', 'Assistant state. Off: bg/surface with a stroke, dimmed mascot, "Unknown calls will ring normally".'),
          opt('onChange', '(on: boolean) => void', undefined, 'Switch handler.'),
          opt('title', 'string', "'AI Assistant is On' / 'AI Assistant is Off'", 'Overrides the state title.'),
          opt('description', 'string', "'Equal is managing your unknown calls!'", 'Description when on. The off description is fixed.'),
        ],
      },
    ],
    dos: [
      'Put it directly under the top bar on Chats (HomeHeader does this) and first under YOUR ASSISTANT on the You tab.',
      'Let the default copy explain the state: on manages unknown calls, off lets them ring normally.',
    ],
    donts: [
      "Don't crop or replace the mascot; it dims itself when off.",
      "Don't add a Save button; the change applies immediately.",
    ],
  },
  {
    id: 'feedback-card',
    name: 'Feedback card',
    group: 'Cards',
    summary: 'The post-call card that asks the user to rate the call from one to five stars and share feedback.',
    components: ['FeedbackCard'],
    importLine: "import { FeedbackCard } from '@equal/ds';",
    compose: ['FeedbackCard'],
    spec: '§13 Feedback Card',
    tokens: ['bg/surface', 'rating/star-filled', 'rating/star-empty', 'text/placeholder', 'stroke/subtle', 'text/action'],
    examples: [
      {
        title: 'Interactive',
        note: 'Hover to preview, tap to rate, then Share Feedback.',
        render: () => (
          <div style={pad16}>
            <FeedbackDemo />
          </div>
        ),
        code: `const [rating, setRating] = useState(0);

<FeedbackCard rating={rating} onRate={setRating} onShare={(r) => submit(r)} />`,
      },
      {
        title: 'Rated',
        render: () => (
          <div style={pad16}>
            <FeedbackCard rating={4} />
          </div>
        ),
        code: `<FeedbackCard rating={4} />`,
      },
    ],
    props: [
      {
        component: 'FeedbackCard',
        props: [
          opt('question', 'string', "'How would you rate the call?'", 'Question (body1).'),
          opt('rating', 'number', undefined, 'Controlled rating 0–5. Omit to let the card keep its own.'),
          opt('onRate', '(rating: number) => void', undefined, 'Called when a star is tapped.'),
          opt('onShare', '(rating: number) => void', undefined, 'Share Feedback tapped, with the current rating.'),
          opt('width', 'number', '246', 'Card width in dp.'),
        ],
      },
    ],
    dos: [
      'Show it once, right after the call summary, indented to line up with the summary bubble.',
      'Keep the question short and about the call itself.',
    ],
    donts: [
      "Don't add a text field to the card; Share Feedback opens the next step.",
      "Don't use emoji or images for the stars; they are coded.",
    ],
  },
  {
    id: 'action-card',
    name: 'Action card',
    group: 'Cards',
    summary: 'A card for something the assistant did or proposes (a WhatsApp reply, an appointment, a saved rule), with stacked actions underneath.',
    components: ['ActionCard', 'ActionCardBody'],
    importLine: "import { ActionCard, ActionCardBody, instructionLines } from '@equal/ds';",
    compose: ['ActionCard'],
    spec: '§14 Action Card',
    tokens: ['bg/surface', 'bg/primary', 'stroke/subtle', 'text/primary', 'text/secondary', 'text/tertiary', 'text/action', 'button/primary'],
    examples: [
      {
        title: 'WhatsApp reply',
        render: () => (
          <div style={pad16}>
            <ActionCard title="WhatsApp · Amit Nanda" quote="“Count me in for Sunday 8 PM 👍”" time="03:21" actions={[{ label: 'Send', emoji: '📤' }, { label: 'Edit reply', emoji: '✏️' }]} />
          </div>
        ),
        code: `<ActionCard source="whatsapp" title="WhatsApp · Amit Nanda"
  quote="“Count me in for Sunday 8 PM 👍”" time="03:21"
  actions={[{ label: 'Send', emoji: '📤' }, { label: 'Edit reply', emoji: '✏️' }]} />`,
      },
      {
        title: 'Appointment: Confirm + Edit',
        note: 'Every action is a green text row at the same weight, led by its emoji. Confirm is not a filled button.',
        render: () => (
          <div style={pad16}>
            <ActionCard
              category="others"
              title="Dr. Mehta's Clinic Appointment"
              details={[{ text: 'Tomorrow, 9:00 AM' }, { text: 'Bring previous medical reports', tone: 'tertiary' }]}
              time="03:21"
              actions={[{ label: 'Confirm', emoji: '👍' }, { label: 'Edit', emoji: '✏️' }]}
            />
          </div>
        ),
        code: `<ActionCard category="others" title="Dr. Mehta's Clinic Appointment"
  details={[
    { text: 'Tomorrow, 9:00 AM' },
    { text: 'Bring previous medical reports', tone: 'tertiary' },
  ]}
  time="03:21"
  actions={[{ label: 'Confirm', emoji: '👍' }, { label: 'Edit', emoji: '✏️' }]} />`,
      },
      {
        title: 'Instruction saved',
        note: 'Instructions use the note_stack icon on a white circle and When / Do lines, never a 3D category avatar.',
        render: () => (
          <div style={pad16}>
            <ActionCard icon="instructions" title="Instruction saved" details={instructionLines('Delivery agents call', 'Ask them to leave parcels with security')} time="03:21" actions={[{ label: 'Edit rule', emoji: '✏️' }]} />
          </div>
        ),
        code: `<ActionCard icon="instructions" title="Instruction saved"
  details={instructionLines('Delivery agents call', 'Ask them to leave parcels with security')}
  time="03:21" actions={[{ label: 'Edit rule', emoji: '✏️' }]} />`,
      },
      {
        title: 'Instruction (body only)',
        note: 'The same body the setup chat shows before “What would you like to update in this?”.',
        render: () => (
          <div style={{ padding: 16, width: 300, boxSizing: 'border-box' }}>
            <ActionCardBody icon="instructions" title="Instructions" details={instructionLines('Bank calls come', 'Tell callers I’ll call back after 6 PM')} time="03:21" />
          </div>
        ),
        code: `<ActionCardBody icon="instructions" title="Instructions"
  details={instructionLines('Bank calls come', 'Tell callers I’ll call back after 6 PM')}
  time="03:21" />`,
        surface: 'surface',
      },
    ],
    props: [
      {
        component: 'ActionCard',
        props: [
          opt('source', "'whatsapp' | AssetName", "'whatsapp'", 'Source logo inside the 40dp circle. `whatsapp` is the coded logo (simple-icons glyph in `brand/whatsapp`); any image asset also works.'),
          opt('category', 'Category', undefined, 'Caller’s 3D category avatar (a person or business). Wins over `icon` and `source`.'),
          opt('icon', 'IconName', undefined, 'Material Symbol in `text/action` on a white circle. Instructions use `instructions` (note_stack). Wins over `source`.'),
          req('title', 'ReactNode', 'Title (body2), e.g. "WhatsApp · Amit Nanda".'),
          opt('quote', 'ReactNode', undefined, 'Quoted message (body1, text/secondary).'),
          opt('details', "{ text: ReactNode; tone?: 'primary' | 'secondary' | 'tertiary' }[]", '[]', 'Detail lines (body1). For instructions use `instructionLines(when, do)`: When in primary, Do in secondary.'),
          opt('time', 'string', undefined, 'Timestamp (micro, right-aligned).'),
          opt('actions', 'ActionCardAction[]', '[]', '{ label, emoji, onClick?, danger? }[]. Each renders as a full-width green text row (44dp) under a divider, led by its emoji (required); danger turns it red.'),
          opt('width', 'number | string', '268', 'Card width.'),
        ],
      },
    ],
    dos: [
      'Put the positive action first (Confirm, Send), then the revise (Edit). Both are text rows.',
      'Name the source in the title ("WhatsApp · Amit Nanda") so the user knows where the action goes.',
      'Lead every action with its emoji (📤 Send, ✏️ Edit, 👍 Confirm, 🗑️ Delete).',
    ],
    donts: [
      "Don't use a filled button inside an action card, not even for Confirm.",
      "Don't use a 3D category avatar for an instruction; use the note_stack icon with When / Do lines.",
      "Don't put the card on a green card; the grey body always sits on a white card.",
      "Don't use flat icons in action labels; the rows are emoji + text.",
    ],
  },
  {
    id: 'whats-new-card',
    name: "What's New card",
    group: 'Cards',
    summary: 'A dismissible announcement card with the NEW FEATURE badge, a heading, body copy and one CTA. Built in three layers: a flat fill, a blurred circle in the bottom-right corner, and the illustration on top of the circle.',
    components: ['WhatsNewCard'],
    importLine: "import { WhatsNewCard } from '@equal/ds';",
    compose: ['WhatsNewCard'],
    spec: "§22 What's New Card",
    tokens: ['whats-new/bg', 'whats-new/glow', 'badge/new-feature-stroke', 'text/action', 'text/secondary', 'text/placeholder', 'button/primary'],
    examples: [
      {
        title: 'Default',
        render: () => (
          <div style={{ padding: 16, width: 390, boxSizing: 'border-box' }}>
            <WhatsNewCard width="100%" heading="Language Preference" body="Set your preferred language and choose how your assistant sounds" ctaLabel="Try Voices" ctaIcon="translate" onDismiss={noop} />
          </div>
        ),
        code: `<WhatsNewCard width="100%" heading="Language Preference"
  body="Set your preferred language and choose how your assistant sounds"
  ctaLabel="Try Voices" ctaIcon="translate" onCta={openVoices} onDismiss={dismiss} />`,
      },
      {
        title: 'Custom badge and icon, not dismissible',
        render: () => (
          <div style={pad16}>
            <WhatsNewCard width="100%" badge="Just added" heading="Instructions" body="Tell your assistant how to handle deliveries, sales calls and more." ctaLabel="Add instruction" ctaIcon="add" />
          </div>
        ),
        code: `<WhatsNewCard width="100%" badge="Just added" heading="Instructions"
  body="Tell your assistant how to handle deliveries, sales calls and more."
  ctaLabel="Add instruction" ctaIcon="add" />`,
      },
    ],
    props: [
      {
        component: 'WhatsNewCard',
        props: [
          req('heading', 'ReactNode', 'Heading (title token, text/action).'),
          req('body', 'ReactNode', 'Body (body1, text/secondary), about two lines.'),
          req('ctaLabel', 'string', 'Filled button label.'),
          opt('ctaIcon', 'IconName', "'star'", 'Leading icon on the CTA.'),
          opt('onCta', '() => void', undefined, 'CTA handler.'),
          opt('onDismiss', '() => void', undefined, 'Shows the close (X) button when set.'),
          opt('badge', 'string', "'New feature'", 'FeatureBadge label.'),
          opt('width', 'number | string', '322', 'Card width (max 100%). Min height 172.'),
        ],
      },
    ],
    dos: [
      'Keep the body to about two lines and one CTA that goes straight to the feature.',
      'Always offer onDismiss on the Chats list so the card can go away.',
    ],
    donts: [
      "Don't stack more than one What's New card at a time.",
      "Don't add a second button; the card has a single commit.",
    ],
  },

  {
    id: 'system-status-card',
    name: 'System status card',
    group: 'Cards',
    summary: 'Takes the assistant toggle’s place on Chats while Equal AI is down: a SYSTEM STATUS pill, the sleeping robot, and a “Notify me when ready” pill that turns into a confirmation.',
    components: ['SystemStatusCard'],
    importLine: "import { SystemStatusCard } from '@equal/ds';",
    compose: ['SystemStatusCard'],
    spec: 'Flows: Temporarily Unavailable',
    tokens: ['gradient/status-card', 'component/status-card/pill-bg', 'component/status-card/button', 'component/status-card/button-text', 'text/primary', 'text/secondary', 'text/action'],
    examples: [
      {
        title: 'Unavailable',
        render: () => <div style={{ padding: 16, width: 390, boxSizing: 'border-box' }}><SystemStatusCard onNotify={noop} /></div>,
        code: `<SystemStatusCard onNotify={subscribeToRecovery} />`,
      },
      {
        title: 'Notify requested',
        note: 'After the tap the pill is replaced by a green confirmation. It is not a button any more.',
        render: () => <div style={{ padding: 16, width: 390, boxSizing: 'border-box' }}><SystemStatusCard state="notified" /></div>,
        code: `<SystemStatusCard state="notified" />`,
      },
      {
        title: 'In the Chats header',
        render: () => <div style={{ width: 390 }}><HomeHeader assistantOn statusBar={false} status={<SystemStatusCard onNotify={noop} />} /></div>,
        code: `<HomeHeader assistantOn={on} status={down ? <SystemStatusCard state={notify} onNotify={subscribe} /> : undefined} />`,
      },
    ],
    props: [
      {
        component: 'SystemStatusCard',
        props: [
          opt('state', "'unavailable' | 'notified'", "'unavailable'", 'Shows the Notify pill, or the “You’ll be notified soon” confirmation.'),
          opt('onNotify', '() => void', undefined, 'Tap on “Notify me when ready”. Flip `state` to notified when it succeeds.'),
          opt('title', 'ReactNode', "'Temporarily Unavailable'", 'Heading, 18/24 bold.'),
          opt('body', 'ReactNode', '“We’re already working on a fix…”', 'Two short lines, caption, text/secondary.'),
          opt('label', 'string', "'System status'", 'Pill text (rendered uppercase).'),
        ],
      },
    ],
    dos: [
      'Put it where the assistant toggle sits (HomeHeader `status`), so the outage reads as the assistant’s state.',
      'Hide promo cards (What’s New, Meet Your Assistant) while the card is showing.',
    ],
    donts: [
      'Don’t show it for a single failed call; it means the whole service is down.',
      'Don’t keep the Notify pill tappable after the user has subscribed.',
    ],
  },
  {
    id: 'meet-assistant-card',
    name: 'Meet Your Assistant card',
    group: 'Cards',
    summary: 'Introduces the voice assistant with its persona avatar and a “Call me” pill. Compact on Chats once the assistant is back; banner as a standalone promo.',
    components: ['MeetAssistantCard', 'CallMeButton'],
    importLine: "import { MeetAssistantCard } from '@equal/ds';",
    compose: ['MeetAssistantCard'],
    spec: 'Flows: Temporarily Unavailable',
    tokens: ['gradient/meet-assistant', 'gradient/call-me', 'text/primary', 'text/action', 'text/secondary', 'text/on-action', 'text/placeholder'],
    examples: [
      {
        title: 'Compact (Chats)',
        render: () => <div style={pad16}><MeetAssistantCard width={328} onCall={noop} onDismiss={noop} /></div>,
        code: `<MeetAssistantCard onCall={startAssistantCall} onDismiss={dismiss} />`,
      },
      {
        title: 'Banner',
        render: () => <div style={pad16}><MeetAssistantCard layout="banner" width={340} onCall={noop} onDismiss={noop} /></div>,
        code: `<MeetAssistantCard layout="banner" onCall={startAssistantCall} onDismiss={dismiss} />`,
      },
    ],
    props: [
      {
        component: 'MeetAssistantCard',
        props: [
          opt('layout', "'compact' | 'banner'", "'compact'", 'Compact: avatar left, title + Call me. Banner: green bold title, body, avatar right.'),
          opt('title', 'ReactNode', "'Meet Your Assistant'", 'Heading.'),
          opt('body', 'ReactNode', '“Try talking to your assistant…”', 'Banner only.'),
          opt('ctaLabel', 'string', "'Call me'", 'Pill label, with the call-sparkle icon.'),
          opt('onCall', '() => void', undefined, 'Starts a call with the assistant.'),
          opt('onDismiss', '() => void', undefined, 'Shows the close (X) when set.'),
          opt('width', 'number | string', "'100%'", 'Card width (max 100%).'),
        ],
      },
    ],
    dos: ['Use the compact layout in the Chats promo slot, the same slot as What’s New.', 'Keep “Call me” as the only action.'],
    donts: ['Don’t show it together with a What’s New card.', 'Don’t crop or recolour the avatar-assistant art; the orb badge is part of the file.'],
  },
  {
    id: 'system-notification',
    name: 'System notification',
    group: 'Overlays',
    summary: 'The Android heads-up notification Equal AI posts, drawn over the status bar in mocks and flows (e.g. “Equal AI Assistant is back on”).',
    components: ['SystemNotification'],
    importLine: "import { SystemNotification } from '@equal/ds';",
    compose: ['SystemNotificationPreview'],
    spec: 'Flows: Temporarily Unavailable',
    tokens: ['component/notification/bg', 'text/primary', 'text/secondary', 'text/placeholder'],
    examples: [
      {
        title: 'Back on',
        note: 'On a real device the OS draws this from a NotificationCompat post; the component is for mocks and flows.',
        render: () => <div style={{ padding: 16, width: 390, boxSizing: 'border-box' }}><SystemNotification title="Equal AI Assistant is back on" body="Equal AI is now handling all your unknown calls." /></div>,
        code: `<SystemNotification title="Equal AI Assistant is back on"
  body="Equal AI is now handling all your unknown calls." />`,
      },
    ],
    props: [
      {
        component: 'SystemNotification',
        props: [
          req('title', 'ReactNode', 'Bold first line.'),
          req('body', 'ReactNode', 'Second line, text/secondary.'),
          opt('app', 'string', "'Equal AI'", 'App name in the header row.'),
          opt('time', 'string', "'Now'", 'Timestamp after the dot.'),
          opt('width', 'number | string', "'100%'", 'Width.'),
        ],
      },
    ],
    dos: ['Keep the title under about 32 characters, so it fits on one line on a 360dp phone.'],
    donts: ['Don’t use it as an in-app toast; it stands for the OS notification.'],
  },

  {
    id: 'lottie',
    name: 'Lottie animation',
    group: 'Display',
    summary: 'Plays illustrative animations shipped as Lottie JSON: mascot moments, celebrations, typing. The same file plays on web and Android.',
    components: ['Lottie'],
    importLine: "import { Lottie } from '@equal/ds';",
    compose: ['EqualLottie'],
    spec: 'Motion · Animations',
    tokens: ['text/action'],
    examples: [
      {
        title: 'Typing dots',
        note: 'Sample file `assets/lottie/typing-dots.json`, tinted with `text/action` so it turns lime in dark.',
        render: () => <Lottie name="typing-dots" width={120} height={40} tint="var(--eq-text-action)" label="Equal AI is typing" />,
        code: `// assets/lottie/typing-dots.json
<Lottie name="typing-dots" width={120} height={40} tint="var(--eq-text-action)" label="Equal AI is typing" />`,
      },
      {
        title: 'Original colours, play once',
        note: 'Without `tint` the file plays in its own colours. Reduced motion shows the first frame.',
        render: () => <Lottie name="typing-dots" width={120} height={40} loop={false} />,
        code: `<Lottie name="typing-dots" width={120} height={40} loop={false} />`,
      },
    ],
    props: [
      {
        component: 'Lottie',
        props: [
          opt('name', 'string', undefined, 'File in `assets/lottie/` without `.json` (picked up automatically).'),
          opt('animationData', 'object', undefined, 'Or the parsed JSON.'),
          opt('width / height', 'number', '120 / 120', 'Box in dp.'),
          opt('loop', 'boolean', 'true', 'Repeat forever.'),
          opt('autoplay', 'boolean', 'true', 'Start on mount.'),
          opt('tint', 'string', undefined, 'CSS colour replacing every fill and stroke (single-colour animations).'),
          opt('label', 'string', undefined, 'Accessible name; omit for decorative animations.'),
        ],
      },
    ],
    dos: [
      'Use Lottie for illustrative motion; keep micro-interactions (press, toggles, sheets) in code on the motion tokens.',
      'Export from After Effects with Bodymovin, shapes only (no images or text layers), 60 fps, under ~100 KB.',
      'Tint single-colour animations with `text/action` so they follow the theme.',
    ],
    donts: [
      "Don't rebuild an animation by hand in code when a Lottie file exists.",
      "Don't autoplay looping animations behind text people are reading.",
    ],
  },

  // ============================================================ Monetization
  {
    id: 'paywall',
    name: 'Paywall',
    group: 'Monetization',
    summary: 'The Equal Unlimited paywall: gold hero with the 3D crown, the price, a preview of what is locked (it changes with the touchpoint), and the sticky gold CTA.',
    components: ['PaywallHero', 'PriceTag', 'LockedPreviewCard', 'PaywallFooter', 'PaywallPreview', 'AnimatedCrown', 'LockedSummaryPreview'],
    importLine: "import { PaywallHero, PriceTag, LockedPreviewCard, PaywallFooter, AnimatedCrown } from '@equal/ds';",
    compose: ['PaywallHero', 'PriceTag', 'LockedPreviewCard', 'PaywallFooter', 'AnimatedCrown'],
    spec: 'Monetization Flow · Unlock Equal Unlimited',
    tokens: ['gradient/paywall-hero', 'gradient/gold-text', 'gradient/cta-gold', 'text/gold', 'paywall/card-bg', 'paywall/card-stroke', 'paywall/divider'],
    examples: [
      {
        title: 'Hero with price and preview',
        note: 'DM Sans only: "Unlock Equal" 22 Regular over "Unlimited" 38 Bold (the design used Lora).',
        width: 390,
        render: () => (
          <div style={{ width: '100%' }}>
            <PaywallHero>
              <PriceTag price={249} />
              <PaywallPreview touchpoint="chat-list" />
            </PaywallHero>
          </div>
        ),
        code: `<PaywallHero onBack={back} onSupport={openHelp}>
  <PriceTag price={249} />
  <LockedPreviewCard>…what's locked…</LockedPreviewCard>
</PaywallHero>`,
      },
      {
        title: 'Preview by touchpoint',
        note: 'The card shows what the user tapped: locked chat rows, a locked conversation, or locked audio.',
        width: 390,
        render: () => (
          <div style={pad16}>
            <div style={{ display: 'flex', flexDirection: 'column', gap: 12, width: '100%' }}>
              <PaywallPreview touchpoint="chat-list" />
              <PaywallPreview touchpoint="chat" />
              <PaywallPreview touchpoint="audio" />
            </div>
          </div>
        ),
        code: `<PaywallPreview touchpoint="chat-list" />
<PaywallPreview touchpoint="chat" />
<PaywallPreview touchpoint="audio" />`,
      },
      {
        title: 'Animated crown',
        note: 'A slow turntable sway with a float, standing in for the rotating-crown video. Static under reduced motion.',
        render: () => <Row gap={32}><AnimatedCrown size={64} /><AnimatedCrown size={100} /><AnimatedCrown size={64} animated={false} /></Row>,
        code: `<AnimatedCrown size={64} />
<AnimatedCrown size={64} animated={false} />`,
      },
      {
        title: 'Sticky footer',
        width: 390,
        render: () => <div style={{ width: '100%' }}><PaywallFooter onClick={noop} /></div>,
        code: `<PaywallFooter cta="Unlock Unlimited" note="Cancel Anytime. Automatically renews" onClick={buy} />`,
      },
    ],
    props: [
      { component: 'PaywallHero', props: [opt('title', 'string', "'Unlock Equal'", 'Regular first line.'), opt('titleStrong', 'string', "'Unlimited'", 'Bold display line.'), opt('subtitle', 'ReactNode', 'Unlock Summaries…', 'Experiment: copy variant.'), opt('onBack / onSupport', '() => void', undefined, 'Header actions; Support opens the Help Center sheet.'), opt('children', 'ReactNode', undefined, 'Price, preview, coupon.')] },
      { component: 'PriceTag', props: [req('price', 'number | string', 'Shown price (drops when the coupon applies).'), opt('label', 'string | null', "'Limited time discount'", 'Gold caps label with 🎊.'), opt('currency', 'string', "'₹'", 'Prefix.')] },
      { component: 'PaywallFooter', props: [opt('cta', 'string', "'Unlock Unlimited'", 'Gold block button.'), opt('note', 'string | null', "'Cancel Anytime. Automatically renews'", 'Caption under it.'), opt('onClick', '() => void', undefined, 'Starts payment.'), opt('variant', "'gold' | 'gold-outline' | 'primary'", "'gold'", 'gold-outline for Cancel Subscription on the plan page.'), opt('disabled', 'boolean', undefined, 'While a payment is processing.'), opt('flat', 'boolean', undefined, 'No surface or shadow, inside the sheet paywall.'), opt('above', 'ReactNode', undefined, 'Above the button, e.g. the processing toast.')] },
    ],
    dos: ['Pick the preview from the touchpoint the user came from.', 'Keep one gold CTA; it is the only commit on the page.'],
    donts: ["Don't use a serif for the display lines; the system is DM Sans only.", "Don't hide the auto-renew note."],
  },
  {
    id: 'coupon-ticket',
    name: 'Coupon ticket',
    group: 'Monetization',
    summary: 'A notched ticket with the coupon code, what it saves, APPLY / APPLIED and the big discount line.',
    components: ['CouponTicket'],
    importLine: "import { CouponTicket } from '@equal/ds';",
    compose: ['CouponTicket'],
    spec: 'Monetization Flow · EQUAL25',
    tokens: ['gradient/coupon', 'paywall/card-stroke', 'paywall/divider', 'text/gold', 'gradient/gold-text'],
    examples: [
      { title: 'Available', render: () => <div style={pad16}><div style={{ width: '100%' }}><CouponTicket code="EQUAL25" description="Save Flat ₹100 with this Code" discount="25% OFF" onApply={noop} /></div></div>, code: `<CouponTicket code="EQUAL25" description="Save Flat ₹100 with this Code" discount="25% OFF" onApply={apply} />` },
      { title: 'Applied', render: () => <div style={pad16}><div style={{ width: '100%' }}><CouponTicket code="EQUAL25" description="Save Flat ₹100 with this Code" discount="25% OFF" applied /></div></div>, code: `<CouponTicket code="EQUAL25" description="Save Flat ₹100 with this Code" discount="25% OFF" applied />` },
    ],
    props: [{ component: 'CouponTicket', props: [req('code', 'string', 'Coupon code.'), req('description', 'ReactNode', 'What it saves.'), req('discount', 'string', 'Big gold line, e.g. "25% OFF".'), opt('applied', 'boolean', undefined, 'Shows APPLIED; the price above drops.'), opt('onApply', '() => void', undefined, 'APPLY tap.')] }],
    dos: ['Drop the price in PriceTag when the coupon applies (₹249 → ₹149).'],
    donts: ["Don't show more than one coupon at a time."],
  },
  {
    id: 'locked-content',
    name: 'Locked content',
    group: 'Monetization',
    summary: 'How free users see what Unlimited unlocks: blurred previews with the crown-lock badge, the Unlock All Details card, and the Unlock Audio pill. Each is a paywall touchpoint.',
    components: ['Locked', 'LockBadge', 'UnlockDetailsCard', 'UnlockAudioPill'],
    importLine: "import { Locked, LockBadge, UnlockDetailsCard, UnlockAudioPill, ChatListItem, ChatBubble, CallSummary } from '@equal/ds';",
    compose: ['LockedContent', 'LockBadge', 'UnlockDetailsCard', 'UnlockAudioPill'],
    spec: 'Monetization Flow · touchpoints',
    tokens: ['locked/badge-bg', 'gradient/status-gold', 'button/primary'],
    examples: [
      {
        title: 'Locked chat rows',
        note: '`locked` blurs the preview and swaps the unread count for the crown-lock. Tapping opens the paywall.',
        width: 390,
        render: () => (
          <Hairlines>
            <ChatListItem name="Amit Nanda" preview="Amit is done with onboarding." date="Yesterday" avatar={{ category: 'career' }} />
            <ChatListItem name="Vijay CA" preview="Your KYC is pending" date="1 May" avatar={{ category: 'financial' }} locked />
            <ChatListItem name="Vijay OLA" preview="On the gate, need approval." date="1 Jul" avatar={{ category: 'ride' }} locked />
          </Hairlines>
        ),
        code: `<ChatListItem name="Vijay CA" preview="Your KYC is pending" date="1 May"
  avatar={{ category: 'financial' }} locked onClick={openPaywall} />`,
      },
      {
        title: 'Locked bubbles and audio',
        width: 390,
        render: () => (
          <OnPrimary pad="16px 8px">
            <MessageList>
              <ChatBubble kind="outgoing" locked>Hello, who is this?</ChatBubble>
              <ChatBubble kind="incoming" category="real-estate" locked>We have a 2BHK in Sector 12 with good offer</ChatBubble>
              <ChatBubble kind="assistant" bare>
                <CallSummary summary="Hey Keshav! Here is the summary of your call." locked="audio" onUnlock={noop} width={264} />
              </ChatBubble>
            </MessageList>
          </OnPrimary>
        ),
        code: `<ChatBubble kind="incoming" locked>…</ChatBubble>
<CallSummary summary="…" locked="audio" onUnlock={openPaywall} />`,
      },
      {
        title: 'Unlock All Details',
        note: 'Docked over the bottom of a locked conversation.',
        render: () => <div style={pad16}><div style={{ width: '100%' }}><UnlockDetailsCard onUnlock={noop} /></div></div>,
        code: `<UnlockDetailsCard onUnlock={openPaywall} />`,
      },
      {
        title: 'Badge and wrapper',
        render: () => (
          <Row gap={24}>
            <LockBadge />
            <Locked><Text variant="body1">Hey this is vijay calling from magicbrick</Text></Locked>
            <UnlockAudioPill onClick={noop} />
          </Row>
        ),
        code: `<LockBadge />
<Locked><Text variant="body1">…</Text></Locked>
<UnlockAudioPill onClick={openPaywall} />`,
      },
    ],
    props: [
      { component: 'Locked', props: [req('children', 'ReactNode', 'Real content, blurred.'), opt('badge', 'boolean', 'true', 'Crown-lock badge.'), opt('inline', 'boolean', undefined, 'One line (chat-list previews).')] },
      { component: 'UnlockDetailsCard', props: [opt('title / body / cta', 'string', 'Unlock All Details…', 'Copy.'), opt('onUnlock', '() => void', undefined, 'Opens the paywall.')] },
      { component: 'ChatListItem · ChatBubble · CallSummary', props: [opt('locked', "boolean · boolean · 'audio' | 'all'", undefined, 'Locked state on the existing components.'), opt('onUnlock', '() => void', undefined, 'CallSummary: Unlock Audio tap.')] },
    ],
    dos: ['Blur the real content so the user sees there is something there.', 'Route every locked tap to the paywall with its touchpoint.'],
    donts: ["Don't lock the caller's name or the time; only the content.", "Don't show the crown-lock to paid users."],
  },
  {
    id: 'payment-status',
    name: 'Payment status',
    group: 'Monetization',
    summary: 'The result sheets after the user pays: Waiting for Confirmation, Payment Confirmed, Pending, Failed and Congratulations. All share the ring StatusCard.',
    components: ['PaymentStatusSheet', 'StatusCard', 'StatusSheet', 'RedirectNote', 'Sunburst'],
    importLine: "import { PaymentStatusSheet, StatusCard, StatusSheet, RedirectNote } from '@equal/ds';",
    compose: ['PaymentStatusSheet', 'StatusCard', 'StatusSheet', 'RedirectNote'],
    spec: 'Monetization Flow · payment results',
    tokens: ['gradient/status-neutral', 'gradient/status-success', 'gradient/status-warning', 'gradient/status-danger', 'gradient/status-gold', 'scrim'],
    examples: (['waiting', 'confirmed', 'pending', 'failed', 'welcome'] as const).map((s) => ({
      title: { waiting: 'Waiting for Confirmation', confirmed: 'Payment Confirmed', pending: 'Payment Pending', failed: 'Payment Failed', welcome: 'Congratulations' }[s],
      render: () => <div style={pad16}><div style={{ width: '100%' }}><PaymentStatusSheet status={s} mode="inline" onRetry={noop} onContactSupport={noop} /></div></div>,
      code: `<PaymentStatusSheet status="${s}"${s === 'failed' ? ' onRetry={retry}' : s === 'pending' ? ' onContactSupport={openHelp}' : s === 'confirmed' || s === 'welcome' ? ' onRedirect={goHome}' : ''} />`,
    })),
    props: [
      { component: 'PaymentStatusSheet', props: [req('status', "'waiting' | 'confirmed' | 'pending' | 'failed' | 'welcome'", 'Result.'), opt('cardLast4', 'string', "'2652'", 'In the refund line.'), opt('onRetry / onContactSupport / onRedirect', '() => void', undefined, 'Actions.'), opt('waitingArt', "'status-waiting' | 'status-waiting-cash'", "'status-waiting'", 'Experiment: coin or cash art.'), opt('mode', "'overlay' | 'inline'", "'overlay'", 'Inline for docs.')] },
      { component: 'StatusCard', props: [req('tone', "'neutral' | 'success' | 'warning' | 'danger' | 'gold'", 'Tint.'), req('art', 'AssetName', '3D art (100dp).'), req('strong', 'string', 'Bold line 28/32.'), opt('light', 'string', undefined, 'Regular line 22/26.'), opt('strongFirst', 'boolean', 'true', 'Order of the lines.')] },
    ],
    dos: ['Say what happens to the money on pending and failed.', 'Auto-redirect after success, with the countdown visible.'],
    donts: ["Don't add a close button; each result has one next step."],
  },
  {
    id: 'help-center-sheet',
    name: 'Help Center sheet',
    group: 'Monetization',
    summary: 'Opened from Support on the paywall (or Contact Support after a pending payment): call or mail the team.',
    components: ['HelpCenterSheet', 'SheetHeading', 'HelpContactRow'],
    importLine: "import { HelpCenterSheet } from '@equal/ds';",
    compose: ['HelpCenterSheet'],
    spec: 'Monetization Flow · Help Center',
    tokens: ['bg/surface', 'stroke/subtle', 'action-card/highlight', 'text/action'],
    examples: [{ title: 'Default', render: () => <div style={pad16}><div style={{ width: '100%' }}><HelpCenterSheet mode="inline" /></div></div>, code: `<HelpCenterSheet phone="+91 9876 543210" email="hello@equal.in" onCall={call} onMail={mail} />` }],
    props: [{ component: 'HelpCenterSheet', props: [opt('phone / email', 'string', '+91 9876 543210 / hello@equal.in', 'Contact rows.'), opt('onCall / onMail', '() => void', undefined, 'Row taps.')] }],
    dos: ['Keep it to the two contact routes.'],
    donts: ["Don't send people to a web page from here."],
  },
  {
    id: 'faq',
    name: 'FAQ',
    group: 'Monetization',
    summary: 'The FREQUENTLY ASKED QUESTIONS accordion on the paywall and the subscription page, and the hairline section label it sits under.',
    components: ['FaqList', 'FaqEntry', 'DividerLabel'],
    importLine: "import { FaqList, DividerLabel } from '@equal/ds';",
    compose: ['FaqList', 'DividerLabel'],
    spec: 'Monetization Flow · FAQ',
    tokens: ['bg/surface', 'stroke/subtle', 'paywall/divider', 'text/secondary'],
    examples: [
      { title: 'Accordion', render: () => <div style={pad16}><div style={{ width: '100%' }}><FaqList items={paywallFaq} defaultOpen={[3]} /></div></div>, code: `<FaqList items={faq} defaultOpen={[3]} />` },
      { title: 'Section label', render: () => <div style={pad16}><div style={{ width: '100%' }}><DividerLabel>Why people choose</DividerLabel></div></div>, code: `<DividerLabel>Why people choose</DividerLabel>` },
    ],
    props: [{ component: 'FaqList', props: [req('items', '{ q: string; a: ReactNode }[]', 'Questions.'), opt('title', 'string | null', "'Frequently asked questions'", 'Label above.'), opt('defaultOpen', 'number[]', '[]', 'Expanded rows.')] }],
    dos: ['Answer cancellation and refunds plainly.'],
    donts: ["Don't put the FAQ above the CTA."],
  },
  {
    id: 'plan-banner',
    name: 'Plan banner',
    group: 'Monetization',
    summary: 'The amber "Reactivate your current Plan…" strip for lapsed plans, under the plan card in Settings or at the top of a chat; plus the plan card link label (Manage plan / Restore Plan / Reactivate plan).',
    components: ['PlanBanner'],
    importLine: "import { PlanBanner, PlanCard } from '@equal/ds';",
    compose: ['PlanBanner'],
    spec: 'Monetization Flow · lapsed plan',
    tokens: ['plan-banner/bg', 'plan-banner/text'],
    examples: [
      { title: 'Under the plan card', render: () => <div style={pad16}><div style={{ width: '100%' }}><PlanCard onManage={noop} actionLabel="Reactivate plan" /><PlanBanner attached>Reactivate your current Plan to use Equal Unlimited</PlanBanner></div></div>, code: `<PlanCard onManage={reactivate} actionLabel="Reactivate plan" />
<PlanBanner attached>Reactivate your current Plan to use Equal Unlimited</PlanBanner>` },
      { title: 'At the top of a chat', width: 390, render: () => <div style={{ width: '100%' }}><PlanBanner onClick={noop}>Reactivate your current Plan to keep using Equal Unlimited</PlanBanner></div>, code: `<PlanBanner onClick={reactivate}>Reactivate your current Plan to keep using Equal Unlimited</PlanBanner>` },
    ],
    props: [{ component: 'PlanBanner', props: [req('children', 'ReactNode', 'Message.'), opt('attached', 'boolean', undefined, 'Hangs under the plan card.'), opt('onClick', '() => void', undefined, 'Makes the strip tappable.')] }],
    dos: ['Use it only when the plan has lapsed or autopay is off.'],
    donts: ["Don't use red; it is a nudge, not an error."],
  },
  {
    id: 'plan-expiry-banner',
    name: 'Plan expiry banner',
    group: 'Monetization',
    summary: 'The strip under the home header as a plan runs out: amber while days remain, red on the last day and after it ends, always with Renew Now.',
    components: ['PlanExpiryBanner'],
    importLine: "import { PlanExpiryBanner } from '@equal/ds';",
    compose: ['PlanExpiryBanner'],
    spec: 'Monetization value · Home Screen',
    tokens: ['expiry-banner/warning-bg', 'expiry-banner/warning-text', 'expiry-banner/danger-bg', 'expiry-banner/danger-text'],
    examples: [
      {
        title: 'States',
        width: 390,
        render: () => (
          <div style={{ width: '100%', display: 'flex', flexDirection: 'column', gap: 12 }}>
            <PlanExpiryBanner expiry={{ state: 'expiring', days: 2 }} onRenew={noop} />
            <PlanExpiryBanner expiry={{ state: 'today' }} onRenew={noop} />
            <PlanExpiryBanner expiry={{ state: 'expired', date: '30th July' }} onRenew={noop} />
          </div>
        ),
        code: `<PlanExpiryBanner expiry={{ state: 'expiring', days: 2 }} onRenew={renew} />
<PlanExpiryBanner expiry={{ state: 'today' }} onRenew={renew} />
<PlanExpiryBanner expiry={{ state: 'expired', date: '30th July' }} onRenew={renew} />`,
      },
    ],
    props: [{ component: 'PlanExpiryBanner', props: [req('expiry', "{ state: 'expiring', days } | { state: 'today' } | { state: 'expired', date }", 'Where the plan stands; picks the tone and copy.'), opt('onRenew', '() => void', undefined, 'Renew Now.')] }],
    dos: ['Show it full width directly under the home header.', 'Use amber while days remain, red from the last day.'],
    donts: ["Don't show it while the plan is comfortably active.", "Don't stack it with the Reactivate plan banner."],
  },
  {
    id: 'value-card',
    name: 'Value card & sheet',
    group: 'Monetization',
    summary: 'What Unlimited saved you: calls handled, the perks, the expiry chip and the current plan. Inside the sheet that rises after a call near the end of a plan, and on the plan page (circuit pattern, no perks). For free users the QuotaCard counts the week’s calls instead.',
    components: ['ValueCard', 'QuotaCard', 'ValueSheet', 'ExpiryChip', 'PremiumIcon'],
    importLine: "import { ValueCard, QuotaCard, ValueSheet, ExpiryChip, PremiumIcon } from '@equal/ds';",
    compose: ['ValueCard', 'QuotaCard', 'ValueSheet', 'ExpiryChip', 'PremiumIcon'],
    spec: 'Monetization value · Value Bottom Sheet Once Called',
    tokens: ['premium-icon/start', 'premium-icon/end', 'gradient/value-card', 'value-card/stroke', 'value-card/perks-bg', 'value-card/ring', 'expiry-chip/warning-bg', 'expiry-chip/danger-bg', 'expiry-chip/active-bg'],
    examples: [
      { title: 'Sheet', width: 390, render: () => <div style={{ width: '100%' }}><ValueSheet mode="inline" expiry={{ state: 'expiring', days: 3 }} onRenew={noop} onClose={noop} /></div>, code: `<ValueSheet expiry={{ state: 'expiring', days: 3 }} onRenew={renew} onClose={close} />` },
      { title: 'Flush sheet (experiment)', width: 390, render: () => <div style={{ width: '100%' }}><ValueSheet mode="inline" variant="flush" expiry={{ state: 'today' }} onRenew={noop} onClose={noop} /></div>, code: `<ValueSheet variant="flush" expiry={{ state: 'today' }} onRenew={renew} onClose={close} />` },
      { title: 'On the plan page', width: 390, render: () => <div style={pad16}><div style={{ width: '100%' }}><ValueCard calls={20} title={null} perks={null} expiry={{ state: 'expired', date: '30th July' }} /></div></div>, code: `<DividerLabel>What Unlimited Saved You</DividerLabel>
<ValueCard calls={20} title={null} perks={null} expiry={{ state: 'expired', date: '30th July' }} />` },
      { title: 'Quota: calls left', width: 390, render: () => <div style={pad16}><div style={{ width: '100%' }}><QuotaCard quota={{ used: 24, limit: 30, resetsInDays: 4 }} /></div></div>, code: `<QuotaCard quota={{ used: 24, limit: 30, resetsInDays: 4 }} />` },
      { title: 'Quota: limit reached', width: 390, render: () => <div style={pad16}><div style={{ width: '100%' }}><QuotaCard quota={{ used: 30, limit: 30, resetsInDays: 2 }} /></div></div>, code: `<QuotaCard quota={{ used: 30, limit: 30, resetsInDays: 2 }} />
// in the sheet: <ValueSheet quota={{ used: 30, limit: 30, resetsInDays: 2 }} onRenew={openPaywall} onClose={close} />` },
      {
        title: 'Expiry chips',
        render: () => (
          <div style={{ display: 'flex', gap: 10, flexWrap: 'wrap' }}>
            <ExpiryChip tone="active">25th Jun ’26</ExpiryChip>
            <ExpiryChip tone="warning">03 Days</ExpiryChip>
            <ExpiryChip tone="danger">Today</ExpiryChip>
            <ExpiryChip tone="danger">30th July</ExpiryChip>
          </div>
        ),
        code: `<ExpiryChip tone="active">25th Jun ’26</ExpiryChip>
<ExpiryChip tone="warning">03 Days</ExpiryChip>
<ExpiryChip tone="danger">Today</ExpiryChip>`,
      },
    ],
    props: [
      { component: 'ValueCard', props: [req('calls', 'number', 'Calls the assistant handled.'), req('expiry', 'PlanExpiry', "active (until) · expiring (days) · today · expired (date)."), opt('title', 'string | null', "'What Unlimited saved you'", 'In-card heading; null on the plan page.'), opt('perks', 'ValuePerk[] | null', 'defaultValuePerks', 'The three rows; null hides them.'), opt('price', 'number', '39', 'Current price.'), opt('was', 'number', '49', 'Struck list price.'), opt('pattern', "'rings' | 'circuit'", 'circuit when perks is null', 'Rings (sheet) or the gold circuit lines (plan page).')] },
      { component: 'PremiumIcon', props: [req('name', 'IconName', 'A filled Material Symbol (the -fill icons, timer, person, call-log).'), opt('size', 'number', '16', 'dp.')] },
      { component: 'QuotaCard', props: [req('quota', '{ used: number; limit: number; resetsInDays: number }', 'This week; used ≥ limit shows Limit Reached.'), opt('perks', 'ValuePerk[] | null', 'quotaPerks', 'What Unlimited adds.'), opt('price', 'number', '149', 'Price.'), opt('was', 'number', '249', 'Struck list price.')] },
      { component: 'ValueSheet', props: [opt('expiry', 'PlanExpiry', undefined, 'Paid plan state.'), opt('quota', 'Quota', undefined, 'Free plan: show the QuotaCard (CTA Get Equal AI Unlimited).'), opt('calls', 'number', '20', 'Calls handled.'), opt('variant', "'card' | 'flush'", "'card'", 'Inset card or the card as the sheet (experiment).'), opt('cta', 'string', "'Renew your Equal AI'", 'Gold CTA.'), opt('onRenew', '() => void', undefined, 'CTA.'), opt('onClose', '() => void', undefined, 'The floating close and the scrim.')] },
    ],
    dos: ['Show the sheet once, after the assistant handles a call in the last days of a plan.', 'Lead with the number; the user should see what they get before the ask.', 'Perk icons are filled Material Symbols at 16 dp in the premium gold gradient (PremiumIcon), never outlined or flat-tinted.', 'Light cards sit on the premium-glow art; dark cards use the coded black-gold gradient.'],
    donts: ["Don't show the sheet on every call or app open.", "Don't use it for users who never subscribed; they get the paywall."],
  },
  {
    id: 'feature-list',
    name: 'Feature list',
    group: 'Monetization',
    summary: 'What You Get on the plan page: one card per Unlimited feature with its 3D gold icon (24/7 Unlimited Call Screening, Unlimited Recordings, Unlimited Summaries).',
    components: ['FeatureList'],
    importLine: "import { FeatureList, unlimitedFeatures } from '@equal/ds';",
    compose: ['FeatureList'],
    spec: 'Monetization value · Manage Plan',
    tokens: ['stroke/subtle', 'bg/surface'],
    examples: [{ title: 'What You Get', width: 390, render: () => <div style={pad16}><div style={{ width: '100%' }}><FeatureList /></div></div>, code: `<DividerLabel>What You Get</DividerLabel>
<FeatureList items={unlimitedFeatures} />` }],
    props: [{ component: 'FeatureList', props: [opt('items', '{ art: AssetName; label: string }[]', 'unlimitedFeatures', 'Features, top to bottom; art is a 3D feature-* asset.')] }],
    dos: ['Keep labels to one line.', 'Use the 3D feature-* art; add a new asset for a new feature.'],
    donts: ["Don't swap in flat Material icons here.", "Don't list more than five; the page already has FAQ and proof."],
  },
  {
    id: 'discount-badge',
    name: 'Discount badge & price drop',
    group: 'Monetization',
    summary: 'The green 25% OFF seal with its label, and the price drop it rides on: APPLY strikes the old price, the new price lands beside the list price with Saved ₹100, then the badge pops.',
    components: ['DiscountBadge', 'OffSeal', 'PriceTag'],
    importLine: "import { DiscountBadge, PriceTag } from '@equal/ds';",
    compose: ['DiscountBadge', 'PriceTag'],
    spec: 'Monetization local components',
    tokens: ['text/brand', 'gradient/gold-text'],
    examples: [
      { title: 'Badges', render: () => <div style={{ display: 'flex', gap: 12, flexWrap: 'wrap' }}><DiscountBadge /><DiscountBadge label="Special Coupon Applied!" /></div>, code: `<DiscountBadge />
<DiscountBadge label="Special Coupon Applied!" />` },
      { title: 'Price drop', width: 390, render: () => <PriceDropDemo />, code: `<PriceTag price={149} was={249} badge="Special Coupon Applied!" animate={justApplied} />` },
    ],
    props: [
      { component: 'DiscountBadge', props: [opt('label', 'string', "'Discounted!!'", 'Green label.'), opt('off', 'string', "'25%'", 'Seal text.'), opt('animate', 'boolean', undefined, 'Pop in.')] },
      { component: 'PriceTag', props: [req('price', 'number', 'Price now.'), opt('was', 'number', undefined, 'Struck list price; adds /Monthly and Saved ₹.'), opt('badge', 'string', undefined, 'A DiscountBadge under the price.'), opt('animate', 'boolean', undefined, 'Run the strike → land → pop sequence (still under reduced motion).')] },
    ],
    dos: ['Animate only when the user caused the change (APPLY).'],
    donts: ["Don't animate a price that was already discounted when the page opened."],
  },
  {
    id: 'assistant-sheet',
    name: 'Assistant sheet (Global MYA)',
    group: 'Assistant',
    summary: 'The sheet the green orb opens on any screen: greeting, quick-action tiles, suggestion chips and a text or voice input. A request turns it into a short task conversation (voice note → Thinking… → result card → Edit / Confirm).',
    components: ['AssistantSheetShell', 'AssistantGreeting', 'AssistantActions', 'AssistantInput', 'VoiceRecorder', 'TrySaying', 'AssistantTaskHeader', 'AssistantThinking', 'AssistantReplies', 'CarouselDots'],
    importLine: "import { AssistantSheetShell, AssistantGreeting, AssistantActions, AssistantInput, VoiceRecorder, TrySaying, AssistantTaskHeader, AssistantThinking, AssistantReplies, CarouselDots } from '@equal/ds';",
    compose: ['AssistantSheetShell', 'AssistantGreeting', 'AssistantActions', 'AssistantInput', 'VoiceRecorder', 'TrySaying', 'AssistantTaskHeader', 'AssistantThinking', 'AssistantReplies', 'CarouselDots'],
    spec: 'Global MYA',
    tokens: ['component/sheet/bg', 'gradient/voice-note', 'text/action', 'action/stroke/unread', 'text/danger', 'component/chat/wallpaper-bg'],
    examples: [
      {
        title: 'Greeting & quick actions',
        width: 390,
        render: () => (
          <div style={{ width: '100%' }}>
            <AssistantSheetShell mode="inline" footer={<AssistantInput mode="text" />}>
              <AssistantGreeting name="Keshav" />
              <AssistantActions items={[{ label: 'Talk to Equal', icon: 'equal' }, { label: 'Talk to Equal', icon: 'auto-fix' }, { label: 'Make a call', icon: 'call-filled' }]} />
              <div className="eq-mya-chips"><LargeActionChip emoji="🗓️" label="Reply Amit on WhatsApp" /><LargeActionChip emoji="🔔" label="Set reminder" /></div>
            </AssistantSheetShell>
          </div>
        ),
        code: `<AssistantSheetShell footer={<AssistantInput mode="text" onMic={toVoice} onSubmit={send} />} onScrim={close}>
  <AssistantGreeting name="Keshav" />
  <AssistantActions items={quickActions} />
  <ChipRow><LargeActionChip emoji="🗓️" label="Reply Amit on WhatsApp" /> …</ChipRow>
</AssistantSheetShell>`,
      },
      {
        title: 'Input: text and voice',
        width: 390,
        render: () => (
          <div style={{ width: '100%', padding: 16, boxSizing: 'border-box', display: 'flex', flexDirection: 'column', gap: 16, background: 'var(--eq-sheet-bg)' }}>
            <AssistantInput mode="text" />
            <AssistantInput mode="text" value="Set a remainder for appointment" focused />
            <AssistantInput mode="voice" />
          </div>
        ),
        code: `<AssistantInput mode="text" value={text} onChange={setText} onMic={() => setMode('voice')} onSubmit={send} />
<AssistantInput mode="voice" onKeyboard={() => setMode('text')} onVoiceNote={record} onVoiceHold={holdToRecord} />`,
      },
      {
        title: 'Recorder states',
        width: 390,
        render: () => (
          <div style={{ width: '100%', padding: 16, boxSizing: 'border-box', display: 'flex', flexDirection: 'column', gap: 18, background: 'var(--eq-sheet-bg)' }}>
            <VoiceRecorder state="idle" />
            <VoiceRecorder state="recording" seconds={4} />
            <VoiceRecorder state="hold" />
            <VoiceRecorder state="cancel" />
          </div>
        ),
        code: `<VoiceRecorder state="idle" onDelete={discard} onSend={send} />
<VoiceRecorder state="recording" seconds={elapsed} onDelete={discard} onSend={send} />
<VoiceRecorder state="hold" />    // press and hold: release to send, slide left to cancel
<VoiceRecorder state="cancel" />  // slid left: release to cancel`,
      },
      {
        title: 'Task conversation',
        width: 390,
        render: () => (
          <div style={{ width: '100%', background: 'var(--eq-chat-wallpaper-bg)' }}>
            <AssistantTaskHeader title="Setting up a reminder for appointment at Dr. Mehta’s" onClose={noop} />
            <div style={{ padding: 16, display: 'flex', flexDirection: 'column', gap: 16 }}>
              <AssistantThinking />
              <AssistantReplies onEdit={noop} onConfirm={noop} />
            </div>
          </div>
        ),
        code: `<AssistantTaskHeader title="Setting up a reminder for appointment at Dr. Mehta’s" onClose={close} />
<AssistantThinking />
<AssistantReplies onEdit={edit} onConfirm={confirm} />`,
      },
      { title: 'Carousel dots', render: () => <div style={{ width: 200 }}><CarouselDots count={3} active={0} /></div>, code: `<CarouselDots count={3} active={0} />` },
      {
        title: 'Reply card',
        note: 'The assistant’s drafted answer inside a task. Every action row is emoji + label.',
        width: 390,
        render: () => <div style={{ padding: 16, width: '100%', boxSizing: 'border-box', background: 'var(--eq-chat-wallpaper-bg)' }}><AssistantReplyCard time="03:21" actions={[{ label: 'Edit', emoji: '✏️' }, { label: 'Send', emoji: '📤' }]}>{'Sure! Here’s a draft for Amit:\n\nHi Amit! Count me in for Sunday 8 PM. See you there 👍'}</AssistantReplyCard></div>,
        code: `<AssistantReplyCard time="03:21" actions={[{ label: 'Edit', emoji: '✏️', onClick: edit }, { label: 'Send', emoji: '📤', onClick: send }]}>
  {draft}
</AssistantReplyCard>`,
      },
      {
        title: 'Chat task sheet',
        note: 'From a chat’s AI layer: the sheet stops below the chat header so the user sees which chat it belongs to.',
        width: 390,
        render: () => <div style={{ width: '100%', height: 640, display: 'flex' }}><ChatTaskSheet mode="inline" contact="Amit" request="Reply on whatsapp" /></div>,
        code: `<ChatScreen chat={amit} />               // tap Reply on WhatsApp or send a message
<ChatTaskSheet contact="Amit" request={text} onClose={close} onSend={openWhatsApp} />`,
      },
    ],
    props: [
      { component: 'AssistantInput', props: [req('mode', "'text' | 'voice'", 'Field + mic, or keyboard toggle + Send voice note.'), opt('value', 'string', "''", 'Text.'), opt('focused', 'boolean', undefined, 'Green outline (keyboard open).'), opt('onMic', '() => void', undefined, 'Text → voice.'), opt('onKeyboard', '() => void', undefined, 'Voice → text.'), opt('onVoiceNote', '() => void', undefined, 'Tap to record.'), opt('onVoiceHold', '() => void', undefined, 'Press and hold to record.'), opt('onSubmit', '(text: string) => void', undefined, 'Send.')] },
      { component: 'VoiceRecorder', props: [req('state', "'idle' | 'recording' | 'hold' | 'cancel'", 'Bar state.'), opt('seconds', 'number', '0', 'Elapsed.'), opt('onDelete', '() => void', undefined, 'Discard.'), opt('onSend', '() => void', undefined, 'Send.')] },
      { component: 'AssistantGreeting', props: [req('name', 'string', 'First name, in text/action.'), opt('greeting', 'string', "'Good morning'", 'Use greetingFor(hour).')] },
      { component: 'AssistantActions', props: [req('items', "{ label; icon: IconName | 'equal'; onClick? }[]", 'Three tiles.')] },
      { component: 'AssistantSheetShell', props: [opt('task', 'boolean', undefined, 'The taller conversation sheet (wallpaper body).'), opt('header', 'ReactNode', undefined, 'AssistantTaskHeader in task mode.'), opt('footer', 'ReactNode', undefined, 'Input / recorder.'), opt('onScrim', '() => void', undefined, 'Tap outside closes.')] },
    ],
    dos: ['Open it from the green orb on every screen; it is the same sheet everywhere.', 'Title the task by what the assistant is doing, not what the user said.', 'End with a result to confirm (Edit / Confirm), then close and show the outcome where it lives (the chat row).'],
    donts: ["Don't draw the keyboard; it's the system's. Only lift the input above it.", "Don't keep the tiles while typing; the keyboard needs the room."],
  },
  {
    id: 'call-handling',
    name: 'Call Handling',
    group: 'Flows',
    summary: 'Pieces of the Call Handling setup and the known-call notification: radio choice cards, contact pickers with an A–Z index, the setup takeover, permission sheets, the done wash, the Android ongoing-call notification and the ongoing-call strip.',
    components: ['CallQuestion', 'ChoiceCard', 'SuggestedTag', 'InfoNote', 'ContactPick', 'ContactCheckRow', 'AlphabetIndex', 'SetupPrompt', 'SetupDone', 'PermissionSheet', 'CallNotification', 'OngoingCallBar'],
    importLine: "import { CallQuestion, ChoiceCard, InfoNote, ContactPick, ContactCheckRow, AlphabetIndex, SetupPrompt, SetupDone, PermissionSheet, CallNotification, OngoingCallBar } from '@equal/ds';",
    compose: ['CallQuestion', 'ChoiceCard', 'SuggestedTag', 'InfoNote', 'ContactPick', 'ContactCheckRow', 'AlphabetIndex', 'SetupPrompt', 'SetupDone', 'PermissionSheet', 'CallNotification', 'OngoingCallBar'],
    spec: 'Known Calls (new users) · Known-call notifications',
    tokens: ['choice/selected-bg', 'gradient/choice-stroke', 'info-note/bg', 'initials-disc/bg', 'gradient/setup-done', 'gradient/setup-prompt', 'ongoing-bar/bg', 'ongoing-bar/text'],
    examples: [
      {
        title: 'Choice cards',
        width: 390,
        render: () => (
          <div style={{ width: '100%', padding: 16, boxSizing: 'border-box', display: 'flex', flexDirection: 'column', gap: 16 }}>
            <ChoiceCard icon="globe" title="All calls, except a few" description="I answer everyone. You pick who I skip." />
            <ChoiceCard icon="call" title="Only unknown numbers" description="I answer numbers that are not saved in your phone." selected />
            <ChoiceCard icon="globe" title="All calls except" description="Unknown numbers, and people you know when you don’t pick up" suggested disabled />
            <InfoNote title="Contact access needed" body="Without it, the assistant can’t handle all calls." link="Give contacts permission" />
          </div>
        ),
        code: `<div role="radiogroup">
  <ChoiceCard icon="globe" title="All calls, except a few" description="I answer everyone. You pick who I skip." selected={mode === 'all'} onSelect={() => setMode('all')} />
  <ChoiceCard icon="call" title="Only unknown numbers" description="I answer numbers that are not saved in your phone." selected={mode === 'unknown'} onSelect={() => setMode('unknown')} />
</div>
<InfoNote title="Contact access needed" body="Without it, the assistant can’t handle all calls." link="Give contacts permission" onLink={askContacts} />`,
      },
      {
        title: 'Contact pickers',
        width: 390,
        render: () => (
          <div style={{ width: '100%', position: 'relative' }}>
            <div className="eq-ch-picks"><ContactPick initials="SU" name="Mom" /><ContactPick initials="PA" name="Pappa" /><ContactPick initials="VC" name="Vijay Cousin" mark="remove" /></div>
            <div style={{ position: 'relative' }}>
              <ContactCheckRow initials="AR" name="Arnav Raj" phone="+91 8986461540" checked />
              <ContactCheckRow initials="SS" name="Shubham Singh" phone="+91 8986461540" checked={false} />
            </div>
          </div>
        ),
        code: `<ContactPick initials="SU" name="Mom" onClick={add} />
<ContactPick initials="VC" name="Vijay Cousin" mark="remove" onClick={remove} />
<ContactCheckRow initials="AR" name="Arnav Raj" phone="+91 8986461540" checked={picked} onToggle={toggle} />
<AlphabetIndex onPick={jumpTo} />`,
      },
      {
        title: 'Permission sheet',
        width: 390,
        render: () => <div style={{ width: '100%' }}><PermissionSheet art="permission-contacts" title="Allow contacts permission" body="I need your contact list to know who is calling." cta="Allow" onClose={noop} /></div>,
        code: `<PermissionSheet art="permission-contacts" title="Allow contacts permission" body="I need your contact list to know who is calling." cta="Allow" onAllow={requestContacts} onClose={close} />`,
      },
      {
        title: 'Call notification',
        width: 390,
        render: () => (
          <div style={{ width: '100%', padding: 12, boxSizing: 'border-box', display: 'flex', flexDirection: 'column', gap: 12, background: '#142540' }}>
            <CallNotification name="Mom" state="answering" />
            <CallNotification name="Mom" state="expanded" />
            <CallNotification name="Mom" state="connecting" />
          </div>
        ),
        code: `<CallNotification name="Mom" state="answering" />
<CallNotification name="Mom" state="expanded" summary={liveSummary} replies={['Takeover', 'I’ll bring them all', 'I’ll call back to get more']} onReply={reply} onAnswer={answer} />
<CallNotification name="Mom" state="connecting" />`,
      },
      { title: 'Ongoing call bar', width: 390, render: () => <div style={{ width: '100%' }}><OngoingCallBar elapsed="02:34" /></div>, code: `<OngoingCallBar elapsed="02:34" />` },
    ],
    props: [
      { component: 'ChoiceCard', props: [req('icon', 'IconName', 'Leading icon.'), req('title', 'string', 'Option.'), req('description', 'string', 'What it means.'), opt('selected', 'boolean', undefined, 'Green outline + tint.'), opt('suggested', 'boolean', undefined, '⭐ Suggested tag.'), opt('disabled', 'boolean', undefined, 'Dimmed (e.g. no permission).'), opt('onSelect', '() => void', undefined, 'Choose.')] },
      { component: 'CallNotification', props: [req('name', 'string', 'Caller.'), req('state', "'answering' | 'summary' | 'expanded' | 'connecting'", 'Stage of the call.'), opt('category', 'Category', "'home-services'", 'Avatar art.'), opt('elapsed', 'string', "'00:06'", 'Timer.'), opt('summary', 'string', undefined, 'What the caller wants.'), opt('replies', 'string[]', undefined, 'Quick replies (expanded).')] },
      { component: 'PermissionSheet', props: [req('art', 'AssetName', '3D art.'), req('title', 'string', 'Ask.'), req('body', 'ReactNode', 'Why.'), req('cta', 'string', 'Action.'), opt('secondary', 'string', undefined, 'Underlined text action (e.g. Skip, answer everyone).'), opt('onClose', '() => void', undefined, 'Floating close.')] },
    ],
    dos: ['Ask for a permission only at the step that needs it, and say why in one line.', 'Keep the notification to who, what they want and one tap to act; Takeover always comes first.'],
    donts: ["Don't style the notification beyond Android's shell; the system draws it.", "Don't make the user pick contacts before they've chosen All calls."],
  },
  {
    id: 'monetization-experiments',
    name: 'Paywall experiments',
    group: 'Monetization',
    summary: 'The pieces the Android team A/B tests around the paywall: full-screen payment results, the processing strip, the sheet paywall with its coupon row, Help Center and exit offer, the offer strip, home notice and usage cards, Equal AI’s own notice, crown-locked actions and recordings.',
    components: ['PaymentResult', 'ProcessingStrip', 'PaywallSheet', 'CouponRow', 'HelpFaqSheet', 'ExitOfferDialog', 'OfferStrip', 'HomeNoticeCard', 'UsageCapCard', 'AssistantNoticeCard', 'SummaryActions', 'LockedRecording'],
    importLine: "import { PaymentResult, ProcessingStrip, PaywallSheet, CouponRow, HelpFaqSheet, ExitOfferDialog, OfferStrip, HomeNoticeCard, UsageCapCard, AssistantNoticeCard, SummaryActions, LockedRecording } from '@equal/ds';",
    compose: ['PaymentResult', 'ProcessingStrip', 'PaywallSheet', 'CouponRow', 'HelpFaqSheet', 'ExitOfferDialog', 'OfferStrip', 'HomeNoticeCard', 'UsageCapCard', 'AssistantNoticeCard', 'SummaryActions', 'LockedRecording'],
    spec: 'All Payment States · Payment Pending · Home / Conversation Screen (light & dark) · Plan Expired & Usage Cap touchpoints · Recordings + Transcripts Locked · Actions on Home Page',
    tokens: ['gradient/result-success', 'gradient/result-pending', 'gradient/result-failed', 'result/ring', 'processing/bg', 'processing/ring', 'offer-strip/bg', 'plan-badge/expired-bg'],
    examples: [
      {
        title: 'Payment results',
        width: 'wide',
        render: () => (
          <div style={{ display: 'flex', gap: 12, padding: 12, flexWrap: 'wrap', justifyContent: 'center' }}>
            {(['success', 'pending', 'failed'] as const).map((st) => <div key={st} style={{ width: 240, height: 440, borderRadius: 20, overflow: 'hidden', border: '1px solid var(--eq-stroke-subtle)' }}><PaymentResult status={st} /></div>)}
          </div>
        ),
        code: `<PaymentResult status="success" activeTill="10th Sept 2026" />
<PaymentResult status="pending" onClose={close} onRefresh={refresh} />   // pendingAction="support" → Contact Support
<PaymentResult status="failed" onBack={back} onRetry={retry} onContactSupport={support} />`,
      },
      {
        title: 'Payment processing',
        width: 390,
        render: () => <div style={{ width: '100%', display: 'flex', flexDirection: 'column', gap: 12, padding: '12px 0' }}><ProcessingStrip /><ProcessingStrip variant="card" /><div style={{ padding: '0 12px' }}><ProcessingStrip variant="toast" /></div></div>,
        code: `<ProcessingStrip />                 // under a chat header
<ProcessingStrip variant="card" />  // under the home toggle
<ProcessingStrip variant="toast" /> // above the disabled paywall CTA`,
      },
      {
        title: 'Sheet paywall',
        width: 390,
        render: () => <div style={{ width: '100%' }}><PaywallSheet mode="inline" /></div>,
        code: `<PaywallSheet applied={applied} onApply={apply} onSupport={openHelp} onScrim={askToLeave} onUnlock={pay} />`,
      },
      {
        title: 'Exit offer and offer strip',
        width: 390,
        render: () => <div style={{ width: '100%', display: 'flex', flexDirection: 'column', gap: 16, padding: '16px 0' }}><div style={{ padding: '0 16px' }}><ExitOfferDialog mode="inline" /></div><OfferStrip /></div>,
        code: `<ExitOfferDialog onDismiss={leave} onClaim={claim} />          // chat: dismissLabel="Leave" claimLabel="Keep 25% Off"
<OfferStrip onClick={reopenWithCoupon} />`,
      },
      {
        title: 'Home cards',
        width: 390,
        render: () => <div style={{ width: '100%', paddingBottom: 12 }}><HomeNoticeCard lead={<span aria-hidden>👑</span>} title="Your Plan Expired" subtitle="Renew to get Unlimited access back" cta="Renew Now" /><UsageCapCard left={7} limit={30} /></div>,
        code: `<HomeNoticeCard lead="👑" title="Your Plan Expired" subtitle="Renew to get Unlimited access back" cta="Renew Now" onCta={renew} />
<UsageCapCard left={7} limit={30} resets="Mon" onUpgrade={openPaywall} />`,
      },
      {
        title: 'Equal AI notice',
        width: 390,
        render: () => <div style={{ padding: 16, background: 'var(--eq-chat-wallpaper-bg)', width: '100%', boxSizing: 'border-box' }}><AssistantNoticeCard title="Your Plan Expired" body="Renew to unlock unlimited recordings, call summaries, transcripts, and unlimited call screening." cta="Renew Equal AI Unlimited" /></div>,
        code: `<AssistantNoticeCard title="Your Plan Expired" body="Renew to unlock unlimited recordings, …" cta="Renew Equal AI Unlimited" onCta={openPaywall} />`,
      },
      {
        title: 'Locked recording and actions',
        width: 390,
        render: () => (
          <div style={{ padding: 16, width: '100%', boxSizing: 'border-box', display: 'flex', flexDirection: 'column', gap: 12 }}>
            <div className="eq-summary"><div style={{ padding: 8 }}><LockedRecording /></div><SummaryActions locked actions={[{ label: 'Call Back', icon: 'call-filled' }, { label: 'Add to Calendar', icon: 'calendar' }]} /></div>
            <SmallActionChip label="Call Back" emoji="📞" locked />
          </div>
        ),
        code: `<LockedRecording onUnlock={openPaywall} />
<SummaryActions locked actions={[{ label: 'Call Back', icon: 'call-filled' }, { label: 'Add to Calendar', icon: 'calendar' }]} onLocked={openPaywall} />
<SmallActionChip label="Call Back" emoji="📞" locked />   // ChatListItem lockedActions`,
      },
    ],
    props: [
      { component: 'PaymentResult', props: [req('status', "'success' | 'pending' | 'failed'", 'Which page.'), opt('activeTill', 'string', "'10th Sept 2026'", 'Success date.'), opt('pendingAction', "'refresh' | 'support'", "'refresh'", 'Pending CTA.'), opt('header', 'ReactNode', undefined, 'Status bar on the wash.')] },
      { component: 'PaywallSheet', props: [opt('price', 'number', '249', 'List price.'), opt('discountedPrice', 'number', '149', 'With EQUAL25.'), opt('applied', 'boolean', undefined, 'Coupon applied.'), opt('processing', 'boolean', undefined, 'Toast + disabled CTA.'), opt('cta', 'string', "'Unlock Equal AI Unlimited'", 'CTA copy (experiment).'), opt('mode', "'overlay' | 'inline'", "'overlay'", 'Docs: inline.')] },
      { component: 'ExitOfferDialog', props: [opt('dismissLabel', 'string', "'Not Now'", 'Leave in chats.'), opt('claimLabel', 'string', "'Claim 25% Off'", 'Keep 25% Off in chats.')] },
      { component: 'ProcessingStrip', props: [opt('variant', "'strip' | 'card' | 'toast'", "'strip'", 'Where it sits.')] },
    ],
    dos: ['Pass experiments as props on the same screen (Home, Chat, Paywall); never fork a screen per variant.', 'While a payment is processing, show it everywhere it matters and disable the paywall CTA.'],
    donts: ["Don't show the exit offer more than once per session.", "Don't lock the caller's name, the time or the summary label; only content and actions."],
  },
  {
    id: 'search',
    name: 'Search',
    group: 'Inputs',
    summary: 'The search screen’s pieces: the outlined search bar with back inside, section labels with Clear all, recent searches as avatar tiles, message hits with the match in bold, loading dots and the three illustration states.',
    components: ['SearchBar', 'SearchSection', 'RecentSearches', 'MessageResult', 'Highlight', 'LoadingDots', 'SearchState'],
    importLine: "import { SearchBar, SearchSection, RecentSearches, MessageResult, LoadingDots, SearchState } from '@equal/ds';",
    compose: ['SearchBar', 'SearchSection', 'RecentSearches', 'MessageResult', 'LoadingDots', 'SearchState'],
    spec: 'Search',
    tokens: ['button/primary', 'bg/surface', 'text/tertiary', 'text/primary'],
    examples: [
      { title: 'Search bar', width: 390, render: () => <div style={{ width: '100%' }}><SearchBar value="" /><SearchBar value="Sa" /></div>, code: `<SearchBar value={q} onChange={setQ} onBack={goBack} autoFocus />` },
      { title: 'Recent searches', width: 390, render: () => <div style={{ width: '100%' }}><SearchSection title="Recent searches" onClear={noop} /><RecentSearches items={[{ label: 'Amit Nanda', category: 'career' }, { label: 'Shankar', category: 'ride' }, { label: 'DSR Builder', category: 'financial' }]} /></div>, code: `<SearchSection title="Recent searches" onClear={clearRecent} />
<RecentSearches items={recent} onPick={setQ} />` },
      { title: 'Message results', width: 390, render: () => <div style={{ width: '100%' }}><SearchSection title="Messages" /><MessageResult name="Santosh Anna" time="3:02 PM" text="Caller said that they’ll call you in the evening" query="Sa" /><MessageResult name="Amit Nanda" time="3:02 PM" text="Same address, near Gachibowli" query="Sa" /></div>, code: `<MessageResult name="Santosh Anna" time="3:02 PM" text={line} query={q} onClick={openMessage} />` },
      { title: 'States', width: 'wide', render: () => (
        <div style={{ display: 'flex', gap: 12, flexWrap: 'wrap', justifyContent: 'center', padding: 12 }}>
          <div style={{ width: 240 }}><SearchState art="illustration-search" size={120} title="Search across your calls" body="Look for names, phone numbers, categories, keywords" /></div>
          <div style={{ width: 240 }}><SearchState art="illustration-no-internet" size={100} title="No internet connection" body="We could not connect to a stable internet. Check your connection and try again." /></div>
          <div style={{ width: 240 }}><SearchState art="illustration-no-results" size={110} title="No results found" body="Couldn’t find anything matching. Try a different name, number, category or keyword" /></div>
          <div style={{ width: 240 }}><LoadingDots /></div>
        </div>
      ), code: `<SearchState art="illustration-search" title="Search across your calls" body="…" />
<SearchState art="illustration-no-internet" title="No internet connection" body="…" />
<SearchState art="illustration-no-results" title="No results found" body="…" />
<LoadingDots />` },
    ],
    props: [
      { component: 'SearchBar', props: [req('value', 'string', 'Query.'), opt('onChange', '(v: string) => void', undefined, 'Typing.'), opt('onBack', '() => void', undefined, 'Back arrow inside the bar.'), opt('placeholder', 'string', "'Search for names'", 'Hint.')] },
      { component: 'MessageResult', props: [req('name', 'string', 'Who.'), req('time', 'string', 'When.'), req('text', 'string', 'The message line.'), req('query', 'string', 'Bolded wherever it appears.')] },
      { component: 'SearchState', props: [req('art', 'AssetName', 'Illustration.'), req('title', 'string', 'Headline.'), req('body', 'string', 'One line of help.'), opt('size', 'number', '160', 'Art size.')] },
    ],
    dos: ['Show chats first and messages after; chats come back fastest.', 'Bold the match in message hits so people see why each one showed up.'],
    donts: ["Don't show a spinner for the whole screen; load each section in place with the dots.", "Don't reuse the no-results art for errors; offline has its own."],
  },
  {
    id: 'chat-actions',
    name: 'Chat actions',
    group: 'Assistant',
    summary: 'What the AI layer uses to carry out a task in a chat and to fail well: the assistant’s turn, Hold to speak, the edit-in-place bubble, the new contact form (two parity options), the permission dialog and the offline sheet and screen.',
    components: ['AiTurn', 'HoldToSpeakBar', 'EditBubble', 'NewContactForm', 'PermissionDialog', 'OfflineSheet', 'OfflineScreen', 'DialerStandIn'],
    importLine: "import { AiTurn, HoldToSpeakBar, EditBubble, NewContactForm, PermissionDialog, OfflineSheet, OfflineScreen } from '@equal/ds';",
    compose: ['AiTurn', 'HoldToSpeakBar', 'EditBubble', 'NewContactForm', 'PermissionDialog', 'OfflineSheet', 'OfflineScreen', 'DialerStandIn'],
    spec: 'Flows/Call Back · Mute · Reply On WhatsApp · Save Contact · Error states',
    tokens: ['button/primary', 'bg/surface', 'gradient/offline-screen', 'action/stroke/unread'],
    examples: [
      { title: 'Assistant turn', width: 390, render: () => <div style={{ padding: 16, width: '100%', boxSizing: 'border-box', background: 'var(--eq-chat-wallpaper-bg)' }}><AiTurn actions={[{ label: 'Mute', emoji: '🔕' }]}>Are you sure? You’ll not get any call from them even if it’s important</AiTurn></div>, code: `<AiTurn actions={[{ label: 'Mute', emoji: '🔕', onClick: mute }]}>Are you sure? …</AiTurn>` },
      { title: 'Hold to speak', width: 390, render: () => <div style={{ padding: 16, width: '100%', boxSizing: 'border-box', background: 'var(--eq-sheet-bg)' }}><HoldToSpeakBar /></div>, code: `<HoldToSpeakBar onHold={record} onKeyboard={showKeyboard} />` },
      { title: 'Edit bubble', width: 390, render: () => <div style={{ width: '100%', background: 'rgba(0,0,0,0.35)' }}><EditBubble value="Hi Amit! Count me in for Sunday 8 PM. See you there 👍" /></div>, code: `<EditBubble value={draft} onChange={setDraft} onDone={save} autoFocus />` },
      { title: 'New contact: iOS parity', width: 390, render: () => <div style={{ width: '100%' }}><NewContactForm variant="grouped" value={{ first: 'Rahul (Electrician)', phone: '+91 98717 46113' }} /></div>, code: `<NewContactForm variant="grouped" value={contact} onClose={close} onSave={save} />` },
      { title: 'New contact: WhatsApp parity', width: 390, render: () => <div style={{ width: '100%', height: 520, display: 'flex' }}><NewContactForm variant="outlined" value={{ first: 'Rahul (Electrician)', phone: '+91 98717 46113', sync: true }} /></div>, code: `<NewContactForm variant="outlined" value={contact} onSave={save} onSync={setSync} />` },
      { title: 'Offline', width: 'wide', render: () => (
        <div style={{ display: 'flex', gap: 12, padding: 12, flexWrap: 'wrap', justifyContent: 'center' }}>
          <div style={{ width: 300 }}><OfflineSheet mode="inline" /></div>
          <div style={{ width: 260, height: 440, borderRadius: 20, overflow: 'hidden', border: '1px solid var(--eq-stroke-subtle)' }}><OfflineScreen /></div>
        </div>
      ), code: `<OfflineSheet onRefresh={refresh} />   // over a chat
<OfflineScreen onRetry={retry} />    // nothing to show yet` },
    ],
    props: [
      { component: 'NewContactForm', props: [req('value', '{ first; last?; phone; email?; sync? }', 'Pre-filled from the call.'), opt('variant', "'grouped' | 'outlined'", "'grouped'", 'iOS-revamp parity or exact WhatsApp parity.'), opt('onSave', '() => void', undefined, 'Save.')] },
      { component: 'EditBubble', props: [req('value', 'string', 'The draft.'), opt('onChange', '(v: string) => void', undefined, 'Typing.'), opt('onDone', '() => void', undefined, 'Green check.')] },
      { component: 'AiTurn', props: [req('children', 'ReactNode', 'What the assistant says.'), opt('actions', '{ label; emoji; onClick? }[]', undefined, 'Action rows, emoji first.')] },
    ],
    dos: ['Confirm before anything the user can’t easily undo (mute, send, save).', 'Leave the result in the chat as an action card, so the chat is the record of what the assistant did.', 'On failure, say what happened in one line and offer the one action that fixes it.'],
    donts: ["Don't draw the dialer, Settings or WhatsApp; they belong to the phone. Hand off and show the result when the user comes back.", "Don't lose the user’s place: after Settings, offer to continue where they stopped."],
  },
  {
    id: 'social-proof',
    name: 'Social proof',
    group: 'Monetization',
    summary: 'The subscription page proof block: a big gold stat, the rating pill and a testimonial carousel.',
    components: ['SocialProof', 'StatBlock', 'RatingPill', 'TestimonialCarousel'],
    importLine: "import { StatBlock, RatingPill, TestimonialCarousel } from '@equal/ds';",
    compose: ['StatBlock', 'RatingPill', 'TestimonialCarousel'],
    spec: 'Monetization Flow · Why people choose',
    tokens: ['gradient/gold-text', 'text/gold', 'paywall/card-stroke'],
    examples: [
      { title: 'Stat and rating', render: () => <Col gap={16} style={{ alignItems: 'center' }}><StatBlock value="400k+" label="People never miss a call" /><RatingPill rating={4.5} /></Col>, code: `<StatBlock value="400k+" label="People never miss a call" />
<RatingPill rating={4.5} />` },
      { title: 'Testimonials', width: 390, render: () => <div style={{ width: '100%', padding: '16px 0' }}><TestimonialCarousel items={testimonials.map((t, i) => ({ ...t, avatar: <Asset name={(['avatar-testimonial-1', 'avatar-testimonial-2', 'avatar-testimonial-3'] as const)[i % 3]} size={36} alt="" /> }))} /></div>, code: `<TestimonialCarousel items={[{ quote, name, avatar: <ProfileAvatar variant="female" size={36} /> }, …]} />` },
    ],
    props: [
      { component: 'StatBlock', props: [req('value', 'string', '"400k+".'), req('label', 'string', 'Under it.')] },
      { component: 'RatingPill', props: [req('rating', 'number', '4.5'), opt('outOf', 'number', '5', 'Scale.')] },
      { component: 'TestimonialCarousel', props: [req('items', '{ quote; name; avatar? }[]', 'Cards (240 wide), with page dots.')] },
    ],
    dos: ['Use real numbers and real reviews only.'],
    donts: ["Don't invent testimonials for production."],
  },
  {
    id: 'cancel-sheet',
    name: 'Cancellation',
    group: 'Monetization',
    summary: 'Three steps to cancel: tell us why (Proceed stays disabled until there is text), confirm, and the request submitted with access kept until the end date.',
    components: ['CancelSheet'],
    importLine: "import { CancelSheet } from '@equal/ds';",
    compose: ['CancelSheet'],
    spec: 'Monetization Flow · cancellation',
    tokens: ['gradient/status-neutral', 'button/primary', 'stroke/subtle'],
    examples: [
      { title: 'Feedback', render: () => <div style={pad16}><div style={{ width: '100%' }}><CancelSheet step="feedback" mode="inline" /></div></div>, code: `<CancelSheet step="feedback" reason={reason} onReasonChange={setReason} onProceed={next} />` },
      { title: 'Feedback filled', render: () => <div style={pad16}><div style={{ width: '100%' }}><CancelSheet step="feedback" mode="inline" reason="Felt it too expensive, I hope it would be under my budget" onReasonChange={noop} /></div></div>, code: `<CancelSheet step="feedback" reason="Felt it too expensive…" onReasonChange={setReason} onProceed={next} />` },
      { title: 'Are you really sure?', render: () => <div style={pad16}><div style={{ width: '100%' }}><CancelSheet step="confirm" mode="inline" /></div></div>, code: `<CancelSheet step="confirm" accessUntil="15 June 2026" onKeep={close} onConfirm={cancel} />` },
      { title: 'Request submitted', render: () => <div style={pad16}><div style={{ width: '100%' }}><CancelSheet step="submitted" mode="inline" /></div></div>, code: `<CancelSheet step="submitted" accessUntil="15 June 2026" onRedirect={goHome} />` },
    ],
    props: [{ component: 'CancelSheet', props: [req('step', "'feedback' | 'confirm' | 'submitted'", 'Step.'), opt('accessUntil', 'string', "'15 June 2026'", 'Access end date.'), opt('reason / onReasonChange', 'string / (v) => void', undefined, 'Feedback text (controlled or not).'), opt('onProceed / onKeep / onConfirm / onRedirect', '() => void', undefined, 'Step actions.')] }],
    dos: ['Say how long access lasts at every step.'],
    donts: ["Don't make Proceed tappable before the user writes a reason."],
  },

  // ============================================================ Overlays
  {
    id: 'bottom-sheet',
    name: 'Bottom sheet',
    group: 'Overlays',
    summary: 'Sheets that rise over the current screen: the standard sheet with a floating close or title row, the centred confirm content for destructive actions, and the full-height sheet for the instruction setup chat.',
    components: ['BottomSheet', 'ConfirmSheetContent', 'FullSheet'],
    importLine: "import { BottomSheet, ConfirmSheetContent, FullSheet } from '@equal/ds';",
    compose: ['EqualBottomSheet', 'ConfirmSheetContent', 'FullSheet'],
    spec: '§17 Bottom Sheet',
    tokens: ['scrim', 'sheet/bg', 'bg/surface', 'shadow/raised', 'text/secondary', 'text/primary', 'button/danger', 'text/on-danger', 'chat/wallpaper-bg'],
    examples: [
      {
        title: 'Delete confirm (inline)',
        render: () => (
          <div style={{ paddingTop: 40, background: 'var(--eq-scrim)' }}>
            <BottomSheet open mode="inline">
              <ConfirmSheetContent
                title={<>Delete chat with<br />Amit Nanda</>}
                subject="(+91 9871746113)"
                body="All past conversations will be deleted. This action cannot be undone."
                confirmLabel="Delete"
                destructive
              />
            </BottomSheet>
          </div>
        ),
        code: `<BottomSheet open mode="inline">
  <ConfirmSheetContent
    title={<>Delete chat with<br />Amit Nanda</>}
    subject="(+91 9871746113)"
    body="All past conversations will be deleted. This action cannot be undone."
    confirmLabel="Delete"
    destructive />
</BottomSheet>`,
      },
      {
        title: 'Titled sheet (inline)',
        note: 'A title row puts close on the left and replaces the floating close.',
        render: () => (
          <div style={{ paddingTop: 40, background: 'var(--eq-scrim)' }}>
            <BottomSheet open mode="inline" title="Turn off WhatsApp updates?">
              <Col gap={16} style={{ alignItems: 'center', textAlign: 'center' }}>
                <IllustrationHeader asset="illustration-whatsapp" size={120} />
                <span className="eq-t-body1" style={{ color: 'var(--eq-text-secondary)' }}>You'll stop getting call alerts and account updates on WhatsApp.</span>
                <Button block size="lg">Turn off updates</Button>
              </Col>
            </BottomSheet>
          </div>
        ),
        code: `<BottomSheet open mode="inline" title="Turn off WhatsApp updates?">
  <IllustrationHeader asset="illustration-whatsapp" size={120} />
  …
  <Button block size="lg">Turn off updates</Button>
</BottomSheet>`,
      },
      {
        title: 'Overlay in a phone',
        note: 'Overlay mode covers the nearest positioned ancestor. Escape or the scrim closes it.',
        render: () => <SheetDemo />,
        code: `<PhoneFrame height={560}>
  <Button variant="danger" leadingIcon="delete" onClick={() => setOpen(true)}>Delete chat</Button>
  <BottomSheet open={open} onClose={() => setOpen(false)}>
    <ConfirmSheetContent title={<>Delete chat with<br />Amit Nanda</>}
      subject="(+91 9871746113)" confirmLabel="Delete" destructive
      onConfirm={deleteChat} />
  </BottomSheet>
</PhoneFrame>`,
      },
      {
        title: 'Full sheet',
        note: 'The "Setting up instruction" chat: orb + title + close, on the chat wallpaper.',
        render: () => <FullSheetDemo />,
        code: `<FullSheet open={open} onClose={() => setOpen(false)} title="Setting up instruction"
  footer={
    <ChipRow gap={8}>
      <LargeActionChip emoji="✍️" label="Edit" state="read" />
      <LargeActionChip emoji="👍" label="Confirm" onClick={confirm} />
    </ChipRow>
  }>
  <MessageList>…</MessageList>
</FullSheet>`,
      },
    ],
    props: [
      {
        component: 'BottomSheet',
        props: [
          req('open', 'boolean', 'Visibility (overlay mode).'),
          opt('onClose', '() => void', undefined, 'Close button, scrim tap and Escape.'),
          req('children', 'ReactNode', 'Sheet content.'),
          opt('closable', 'boolean', 'true', 'Show the floating close button (ignored when `title` is set).'),
          opt('mode', "'overlay' | 'inline'", "'overlay'", "'overlay' covers the nearest positioned ancestor (the PhoneFrame); 'inline' renders statically, for docs."),
          opt('label', 'string', "'Sheet'", 'aria-label when there is no title.'),
          opt('title', 'string', undefined, 'Title row: close (X) on the left, centred title.'),
        ],
      },
      {
        component: 'ConfirmSheetContent',
        props: [
          opt('illustration', 'AssetName', "'illustration-trash'", '72dp illustration on top.'),
          req('title', 'ReactNode', 'Title (title token, centred, wraps).'),
          opt('subject', 'ReactNode', undefined, 'The thing affected, e.g. the phone number (tabular).'),
          opt('body', 'ReactNode', undefined, 'Consequence text (body1, text/secondary).'),
          req('confirmLabel', 'string', 'Full-width lg button label.'),
          opt('confirmIcon', 'IconName', "'delete' when destructive", 'Leading icon on the button.'),
          opt('destructive', 'boolean', undefined, 'Danger button with the trash icon.'),
          opt('onConfirm', '() => void', undefined, 'Button handler.'),
        ],
      },
      {
        component: 'FullSheet',
        props: [
          req('open', 'boolean', 'Visibility.'),
          opt('onClose', '() => void', undefined, 'Close button, scrim tap and Escape.'),
          req('title', 'string', 'Title beside the orb.'),
          req('children', 'ReactNode', 'Scrolling content over the chat wallpaper.'),
          opt('footer', 'ReactNode', undefined, 'Pinned footer (chips or a composer).'),
        ],
      },
    ],
    dos: [
      'Name the thing being deleted in the title and put its number in `subject` ("Delete chat with Amit Nanda", "(+91 9871746113)").',
      'State the consequence in the body ("This action cannot be undone.").',
      'Render overlays inside a positioned container such as PhoneFrame; use mode="inline" in docs.',
    ],
    donts: [
      "Don't put a Cancel button next to the confirm; the close button and scrim are the revise path.",
      "Don't use a sheet for a quick yes/no that needs no context; use a Dialog.",
      "Don't show both a title row and the floating close.",
    ],
  },
  {
    id: 'dialog',
    name: 'Dialog',
    group: 'Overlays',
    summary: 'A centred decision dialog with a text cancel and one filled commit, plus the inverse-surface Snackbar that confirms an action afterwards.',
    components: ['Dialog', 'Snackbar'],
    importLine: "import { Dialog, Snackbar } from '@equal/ds';",
    compose: ['EqualDialog', 'EqualDialogContent', 'EqualSnackbar'],
    spec: 'Surfaces from the Typography screen inventory · Dialog, Snackbar/Toast',
    tokens: ['bg/surface', 'shadow/nav', 'scrim', 'text/primary', 'text/secondary', 'text/tertiary', 'button/primary', 'button/danger', 'button/secondary'],
    examples: [
      {
        title: 'Inline',
        render: () => (
          <div style={{ padding: 24, display: 'flex', justifyContent: 'center' }}>
            <Dialog open mode="inline" title="Turn off Call Learning?" subject="Call learning" body="Your assistant will stop learning from new calls. What it already knows stays until you clear it." confirmLabel="Turn off" />
          </div>
        ),
        code: `<Dialog open mode="inline" title="Turn off Call Learning?" subject="Call learning"
  body="Your assistant will stop learning from new calls. What it already knows stays until you clear it."
  confirmLabel="Turn off" />`,
      },
      {
        title: 'Destructive',
        render: () => (
          <div style={{ padding: 24, display: 'flex', justifyContent: 'center' }}>
            <Dialog open mode="inline" destructive title="Delete address?" subject="Work" body="Your assistant will no longer share this address with delivery partners." confirmLabel="Delete" />
          </div>
        ),
        code: `<Dialog open mode="inline" destructive title="Delete address?" subject="Work"
  body="Your assistant will no longer share this address with delivery partners."
  confirmLabel="Delete" />`,
      },
      {
        title: 'Overlay + snackbar',
        render: () => <DialogDemo />,
        code: `<Dialog open={open} title="Turn off Call Learning?" confirmLabel="Turn off"
  onCancel={() => setOpen(false)}
  onConfirm={() => { setOpen(false); setToast('Call learning turned off'); }} />

{toast && <Snackbar message={toast} actionLabel="Undo" onAction={undo} />}`,
      },
      {
        title: 'Snackbars',
        render: () => (
          <Col gap={12} style={{ padding: 16 }}>
            <Snackbar message="Contact saved" />
            <Snackbar message="Chat deleted" actionLabel="Undo" onAction={noop} />
          </Col>
        ),
        code: `<Snackbar message="Contact saved" />
<Snackbar message="Chat deleted" actionLabel="Undo" onAction={undo} />`,
      },
    ],
    props: [
      {
        component: 'Dialog',
        props: [
          req('open', 'boolean', 'Visibility in overlay mode (inline always renders).'),
          req('title', 'string', 'Question (title token).'),
          opt('subject', 'string', undefined, 'What it applies to (caption, text/tertiary).'),
          opt('body', 'ReactNode', undefined, 'Consequence (body1, text/secondary).'),
          req('confirmLabel', 'string', 'Filled commit label.'),
          opt('cancelLabel', 'string', "'Cancel'", 'Neutral text action label.'),
          opt('destructive', 'boolean', undefined, 'Danger fill on the commit.'),
          opt('onConfirm', '() => void', undefined, 'Commit handler.'),
          opt('onCancel', '() => void', undefined, 'Cancel and scrim tap.'),
          opt('mode', "'overlay' | 'inline'", "'overlay'", 'overlay covers the nearest positioned ancestor; inline renders statically for docs.'),
        ],
      },
      {
        component: 'Snackbar',
        props: [
          req('message', 'string', 'Confirmation text (body1).'),
          opt('actionLabel', 'string', undefined, 'Optional text action (e.g. Undo).'),
          opt('onAction', '() => void', undefined, 'Action handler.'),
        ],
      },
    ],
    dos: [
      'Commit is filled and cancel is a text action, right-aligned (rule 4).',
      'Reuse the verb from the control: Turn off → "Turn off" button → "Call learning turned off" snackbar.',
      'Offer Undo on the snackbar for reversible deletes.',
    ],
    donts: [
      "Don't write \"OK\" / \"Yes\" as the commit; name the action.",
      "Don't stack a snackbar on an open dialog.",
    ],
  },
  {
    id: 'context-menu',
    name: 'Context menu',
    group: 'Overlays',
    summary: 'The long-press menu of icon + label items, and OverflowMenu, the ⋮ button that opens one anchored to a card.',
    components: ['ContextMenu', 'OverflowMenu'],
    importLine: "import { ContextMenu, OverflowMenu } from '@equal/ds';",
    compose: ['ContextMenu', 'ContextMenuPopup', 'OverflowMenu'],
    spec: '§18 Context Menu (Long-press)',
    tokens: ['bg/surface', 'shadow/card', 'stroke/subtle', 'text/primary', 'text/tertiary', 'text/danger'],
    examples: [
      {
        title: 'Chat long-press',
        render: () => (
          <div style={pad16}>
            <ContextMenu
              items={[
                { label: 'Mark as read', icon: 'done-all' },
                { label: 'Call back', icon: 'call' },
                { label: 'Delete chat', icon: 'delete', danger: true },
              ]}
            />
          </div>
        ),
        code: `<ContextMenu items={[
  { label: 'Mark as read', icon: 'done-all' },
  { label: 'Call back', icon: 'call' },
  { label: 'Delete chat', icon: 'delete', danger: true },
]} />`,
      },
      {
        title: 'Overflow menu',
        note: 'Click ⋮. Closes on outside click or Escape.',
        render: () => (
          <div style={{ padding: 16, minHeight: 160, display: 'flex', justifyContent: 'flex-end' }}>
            <OverflowMenu
              items={[
                { label: 'Edit', icon: 'edit' },
                { label: 'Delete instruction', icon: 'delete', danger: true },
              ]}
            />
          </div>
        ),
        code: `<OverflowMenu items={[
  { label: 'Edit', icon: 'edit', onSelect: edit },
  { label: 'Delete instruction', icon: 'delete', danger: true, onSelect: confirmDelete },
]} />`,
      },
    ],
    props: [
      {
        component: 'ContextMenu',
        props: [
          req('items', 'MenuItem[]', '{ label, icon?, danger?, onSelect? }[]. danger turns label and icon text/danger.'),
          opt('minWidth', 'number', '131', 'Minimum width in dp.'),
        ],
      },
      {
        component: 'OverflowMenu',
        props: [
          req('items', 'MenuItem[]', 'Menu items; the menu closes before onSelect runs.'),
          opt('label', 'string', "'More options'", 'Accessible label of the ⋮ button.'),
        ],
      },
    ],
    dos: [
      'Put the destructive item last and mark it `danger`.',
      'Name the object in destructive labels ("Delete instruction", "Delete address").',
    ],
    donts: [
      "Don't put more than four or five items in a menu.",
      "Don't delete straight from the menu; open the confirm sheet.",
    ],
  },

  // ============================================================ Lists & rows
  {
    id: 'settings-row',
    name: 'Settings row',
    group: 'Lists & rows',
    summary: 'The dense settings row (icon, subheading label, caption description, chevron or switch), grouped into rounded cards with RowGroup.',
    components: ['SettingsRow', 'ToggleRow', 'RowGroup'],
    importLine: "import { SettingsRow, ToggleRow, RowGroup } from '@equal/ds';",
    compose: ['SettingsRow', 'ToggleRow', 'RowGroup'],
    spec: 'You tab · Settings row',
    tokens: ['bg/surface', 'stroke/subtle', 'text/primary', 'text/tertiary', 'text/placeholder', 'button/primary'],
    examples: [
      {
        title: 'You tab group',
        render: () => (
          <OnPrimary pad="0 0 16px">
            <SectionHeader>Your assistant</SectionHeader>
            <RowGroup>
              <SettingsRow icon="instructions" label="Instructions" description="Tell assistant how you'd like it to help" isNew onClick={noop} />
              <SettingsRow icon="health-check" label="Assistant Health Check" description="Check Real-Time Assistant Connectivity" onClick={noop} />
              <SettingsRow icon="silent-mode" label="Silent Mode" description="Mute promotional calls" onClick={noop} />
              <SettingsRow icon="voice-language" label="Voice & Language" description="How your assistant sounds" onClick={noop} />
            </RowGroup>
          </OnPrimary>
        ),
        code: `<SectionHeader>Your assistant</SectionHeader>
<RowGroup>
  <SettingsRow icon="instructions" label="Instructions" isNew
    description="Tell assistant how you'd like it to help" onClick={open} />
  <SettingsRow icon="health-check" label="Assistant Health Check"
    description="Check Real-Time Assistant Connectivity" onClick={open} />
  …
</RowGroup>`,
      },
      {
        title: 'Toggle rows',
        note: 'Wrapping descriptions switch to captionWrap (Regular).',
        render: () => <ToggleRowsDemo />,
        code: `<RowGroup>
  <ToggleRow label="Mute Promotional Calls" wrap
    description="Equal AI Assistant can identify Promotional Calls and automatically mute them"
    checked={mute} onChange={setMute} />
</RowGroup>`,
      },
      {
        title: 'Trailing variants',
        render: () => (
          <OnPrimary pad="16px 0">
            <RowGroup>
              <SettingsRow label="Active Instructions" description="3 Active" onClick={noop} />
              <SettingsRow label="Muted Contacts" trailing={<span className="eq-t-body1 eq-tnum" style={{ color: 'var(--eq-text-tertiary)' }}>4</span>} onClick={noop} />
              <SettingsRow label="Version" description="2.4.0" trailing="none" />
            </RowGroup>
          </OnPrimary>
        ),
        code: `<SettingsRow label="Active Instructions" description="3 Active" onClick={open} />
<SettingsRow label="Muted Contacts" trailing={<span>4</span>} onClick={open} />
<SettingsRow label="Version" description="2.4.0" trailing="none" />`,
      },
    ],
    props: [
      {
        component: 'SettingsRow',
        props: [
          req('label', 'string', 'Row label (subheading).'),
          opt('description', 'ReactNode', undefined, 'Secondary line (caption, text/tertiary).'),
          opt('wrap', 'boolean', undefined, 'Let the description wrap; switches it to captionWrap (Regular, 18 line height).'),
          opt('icon', 'IconName', undefined, 'Leading 24dp icon.'),
          opt('isNew', 'boolean', undefined, 'NEW badge after the label.'),
          opt('trailing', "'chevron' | 'none' | ReactNode", "'chevron'", 'Chevron, nothing, or custom content.'),
          opt('onClick', '() => void', undefined, 'Makes the row a button.'),
        ],
      },
      {
        component: 'ToggleRow',
        props: [
          req('checked', 'boolean', 'Switch state.'),
          opt('onChange', '(v: boolean) => void', undefined, 'Switch handler.'),
          opt('…row', "Omit<SettingsRowProps, 'trailing' | 'onClick'>", undefined, 'label, description, wrap, icon and isNew as on SettingsRow. The label doubles as the switch\'s accessible name.'),
        ],
      },
      {
        component: 'RowGroup',
        props: [
          req('children', 'ReactNode', 'Rows, separated by dividers.'),
          opt('inset', 'number', '20', 'Side margin in dp (You tab: 20).'),
        ],
      },
    ],
    dos: [
      'Group related rows under a SectionHeader with matching inset.',
      'Use `wrap` for descriptions longer than one line so they switch to Regular (rule 3).',
      'Use ToggleRow for instant settings and a chevron row for anything that opens a screen.',
    ],
    donts: [
      "Don't mix a chevron and a switch on one row.",
      "Don't truncate a description that matters; set `wrap`.",
    ],
  },
  {
    id: 'call-history',
    name: 'Call history',
    group: 'Lists & rows',
    summary: 'The Contact Info call-history card (radius 16; outlined in dark) and its rows: direction icon, call type, meta line and time.',
    components: ['CallHistoryRow', 'CallHistoryList'],
    importLine: "import { CallHistoryList, CallHistoryRow } from '@equal/ds';",
    compose: ['CallHistoryList', 'CallHistoryRow'],
    spec: 'Contact Info · Call history',
    tokens: ['bg/surface', 'stroke/subtle', 'stroke/medium', 'text/primary', 'text/secondary', 'text/tertiary', 'text/danger'],
    examples: [
      {
        title: 'Directions',
        render: () => (
          <OnPrimary pad="0 0 16px">
            <SectionHeader size="label" inset={22}>Call history</SectionHeader>
            <CallHistoryList>
              <CallHistoryRow direction="incoming" meta="Today • 30 Sec." time="02:30 PM" />
              <CallHistoryRow direction="missed" meta="Yesterday" time="11:04 AM" />
              <CallHistoryRow direction="outgoing" meta="12 July • 2 Min." time="06:15 PM" />
            </CallHistoryList>
          </OnPrimary>
        ),
        code: `<SectionHeader size="label" inset={22}>Call history</SectionHeader>
<CallHistoryList>
  <CallHistoryRow direction="incoming" meta="Today • 30 Sec." time="02:30 PM" />
  <CallHistoryRow direction="missed" meta="Yesterday" time="11:04 AM" />
  <CallHistoryRow direction="outgoing" meta="12 July • 2 Min." time="06:15 PM" />
</CallHistoryList>`,
      },
      {
        title: 'With trailing',
        render: () => (
          <OnPrimary pad="16px 0">
            <CallHistoryList>
              <CallHistoryRow direction="incoming" meta="Today • 2 Min." time="03:21 PM" trailing={<IconButton icon="play" label="Play recording" size={32} iconSize={20} />} />
            </CallHistoryList>
          </OnPrimary>
        ),
        code: `<CallHistoryRow direction="incoming" meta="Today • 2 Min." time="03:21 PM"
  trailing={<IconButton icon="play" label="Play recording" size={32} iconSize={20} />} />`,
      },
    ],
    props: [
      {
        component: 'CallHistoryRow',
        props: [
          req('direction', "'incoming' | 'outgoing' | 'missed'", 'Sets the icon and label (Incoming Call / Outgoing Call / Missed Call). Only the missed icon is red.'),
          req('meta', 'string', 'Meta line (11sp Regular, text/tertiary), e.g. "Today • 30 Sec.".'),
          req('time', 'string', 'Time (body1, text/secondary).'),
          opt('trailing', 'ReactNode', undefined, 'Content after the time.'),
        ],
      },
    ],
    dos: [
      'Separate date and duration in `meta` with " • " ("Today • 30 Sec.").',
      'Group rows in a CallHistoryList under a `SectionHeader size="label"` CALL HISTORY header (inset 22).',
    ],
    donts: [
      "Don't colour the \"Missed Call\" label red; only the icon uses text/danger.",
      "Don't pass your own label; the direction sets it.",
    ],
  },
  {
    id: 'empty-state',
    name: 'Empty state',
    group: 'Lists & rows',
    summary: 'A centred illustration, title, body and optional action for empty lists and no-results, plus IllustrationHeader for the art at the top of settings detail screens.',
    components: ['EmptyState', 'IllustrationHeader'],
    importLine: "import { EmptyState, IllustrationHeader } from '@equal/ds';",
    compose: ['EmptyState', 'IllustrationHeader'],
    spec: 'Surfaces from the Typography screen inventory · Empty / Error State',
    tokens: ['text/primary', 'text/tertiary', 'button/primary'],
    examples: [
      {
        title: 'Addresses empty',
        render: () => <EmptyState illustration="illustration-addresses-empty" title="Never explain your address again" body="Save frequently used addresses, and your assistant will use them to handle delivery, pickup calls, etc. more naturally." actionLabel="Add new Address" onAction={noop} />,
        code: `<EmptyState illustration="illustration-addresses-empty"
  title="Never explain your address again"
  body="Save frequently used addresses, and your assistant will use them to handle delivery, pickup calls, etc. more naturally."
  actionLabel="Add new Address" onAction={addAddress} />`,
      },
      {
        title: 'No results',
        render: () => <EmptyState title="No chats found" body={'Nothing matches "Priya". Try a name or number.'} />,
        code: `<EmptyState title="No chats found" body={\`Nothing matches "\${q}". Try a name or number.\`} />`,
      },
      {
        title: 'Illustration header',
        render: () => (
          <OnPrimary pad="0 0 16px">
            <IllustrationHeader asset="illustration-silent-mode" />
            <RowGroup>
              <ToggleRow label="Mute Promotional Calls" wrap description="Equal AI Assistant can identify Promotional Calls and automatically mute them" checked onChange={noop} />
            </RowGroup>
          </OnPrimary>
        ),
        code: `<IllustrationHeader asset="illustration-silent-mode" />
<RowGroup>
  <ToggleRow label="Mute Promotional Calls" … />
</RowGroup>`,
      },
    ],
    props: [
      {
        component: 'EmptyState',
        props: [
          req('title', 'string', 'Title (subheading).'),
          opt('body', 'string', undefined, 'Body (captionWrap, text/tertiary, max 260dp).'),
          opt('illustration', 'AssetName', undefined, '96dp illustration.'),
          opt('actionLabel', 'string', undefined, 'Shows a filled Button.'),
          opt('onAction', '() => void', undefined, 'Button handler.'),
        ],
      },
      {
        component: 'IllustrationHeader',
        props: [
          req('asset', 'AssetName', 'Illustration asset, e.g. illustration-silent-mode, illustration-whatsapp.'),
          opt('size', 'number', '160', 'Square size in dp.'),
        ],
      },
    ],
    dos: [
      'Say what the feature does for the user in the title ("Never explain your address again").',
      'Put the query back in no-results copy and suggest what to try.',
    ],
    donts: [
      "Don't crop or recolour the 3D illustrations.",
      "Don't show an action when the list fills from elsewhere; if the add button is pinned below, leave it off.",
    ],
  },

  // ============================================================ You tab
  {
    id: 'profile-hero',
    name: 'Profile hero',
    group: 'You tab',
    summary: 'The gold header at the top of the You tab (avatar, name, plan marker, edit, phone) that hosts the plan card, plus the compact ProfileHeader.',
    components: ['ProfileHero', 'ProfileHeader'],
    importLine: "import { ProfileHero, ProfileHeader } from '@equal/ds';",
    compose: ['ProfileHero', 'ProfileHeader'],
    spec: 'You tab · Profile hero',
    tokens: ['gradient/profile-hero', 'gradient/gold', 'text/primary', 'text/secondary'],
    examples: [
      {
        title: 'You tab hero with plan card',
        render: () => (
          <ProfileHero name="Keshav" phone="+91 98789 56749" onEdit={noop}>
            <PlanCard onManage={noop} />
          </ProfileHero>
        ),
        code: `<ProfileHero name="Keshav" phone="+91 98789 56749" onEdit={editProfile}>
  <PlanCard onManage={managePlan} />
</ProfileHero>`,
      },
      {
        title: 'Female avatar, no plan',
        render: () => <ProfileHero name="Priya Sharma" phone="+91 99100 22114" avatar="female" plan={null} onEdit={noop} />,
        code: `<ProfileHero name="Priya Sharma" phone="+91 99100 22114" avatar="female" plan={null} onEdit={editProfile} />`,
      },
      {
        title: 'Compact header',
        render: () => <ProfileHeader name="Keshav" phone="+91 98789 56749" />,
        code: `<ProfileHeader name="Keshav" phone="+91 98789 56749" />`,
      },
    ],
    props: [
      {
        component: 'ProfileHero',
        props: [
          req('name', 'string', 'Name (headline, truncates).'),
          req('phone', 'string', 'Phone (body1, tabular).'),
          opt('avatar', "'male' | 'female'", "'male'", 'ProfileAvatar variant (70dp).'),
          opt('plan', 'string | null', "'Unlimited'", 'PlanBadge label; null hides it.'),
          opt('onEdit', '() => void', undefined, 'Shows the edit-profile button.'),
          opt('children', 'ReactNode', undefined, 'Rendered at the bottom of the hero; the plan card goes here.'),
        ],
      },
      {
        component: 'ProfileHeader',
        props: [
          req('name', 'string', 'Name (headline).'),
          req('phone', 'string', 'Phone (body1, tabular).'),
          opt('plan', 'string | null', "'Unlimited'", 'PlanBadge label; null hides it.'),
          opt('avatar', "'male' | 'female' | false", "'male'", 'ProfileAvatar variant (56dp), or false for none.'),
        ],
      },
    ],
    dos: [
      'Put the PlanCard inside the hero as children so it sits on the gold.',
      'Drive `avatar` from the gender chosen in Personal Details.',
    ],
    donts: [
      "Don't add more actions to the hero row; edit is the only one.",
      "Don't format the phone number without tabular figures.",
    ],
  },
  {
    id: 'plan-card',
    name: 'Plan card',
    group: 'You tab',
    summary: "The dark card showing the user's current plan (Equal AI UNLIMITED, ₹149/Monthly), with a Manage plan action and an expired state.",
    components: ['PlanCard', 'PlanPattern'],
    importLine: "import { PlanCard } from '@equal/ds';",
    compose: ['PlanCard'],
    spec: 'You tab · Plan card',
    tokens: ['plan-card/bg', 'plan-card/pill-stroke', 'plan-card/pattern', 'text/on-dark', 'text/on-dark-secondary', 'text/accent-gold', 'text/danger'],
    examples: [
      {
        title: 'Current and expired',
        render: () => (
          <div style={pad16}>
            <PlanStatesDemo />
          </div>
        ),
        code: `<PlanCard onManage={managePlan} />
<PlanCard status="expired" onManage={managePlan} />`,
      },
      {
        title: 'Custom tier',
        render: () => (
          <div style={pad16}>
            <PlanCard tier="LITE" price="₹49/Monthly" />
          </div>
        ),
        code: `<PlanCard tier="LITE" price="₹49/Monthly" />`,
      },
    ],
    props: [
      {
        component: 'PlanCard',
        props: [
          opt('product', 'string', "'Equal AI'", 'Product name (title, on-dark).'),
          opt('tier', 'string', "'UNLIMITED'", 'Tier, in text/accent-gold. Pass it uppercase.'),
          opt('price', 'string', "'₹149/Monthly'", 'Price (caption Regular, tabular).'),
          opt('status', "'current' | 'expired'", "'current'", 'Pill reads "Current Plan" or "Plan expired" (text/danger).'),
          opt('onManage', '() => void', undefined, 'Shows the "Manage plan" action.'),
        ],
      },
    ],
    dos: [
      'Show the price with the period ("₹149/Monthly"); it renders in tabular figures.',
      'Use the expired state with Manage plan so renewing is one tap.',
    ],
    donts: [
      "Don't replace the coded gold pattern with an image.",
      "Don't place the plan card on a white surface; it belongs in the gold hero.",
    ],
  },
  {
    id: 'ai-summary-card',
    name: 'AI summary card',
    group: 'You tab',
    summary: 'The "Your AI Summary" card, prose the assistant wrote about the user, with empty, loading and ready states.',
    components: ['AiSummaryCard'],
    importLine: "import { AiSummaryCard } from '@equal/ds';",
    compose: ['AiSummaryCard'],
    spec: 'You tab · AI Summary card',
    tokens: ['gradient/ai-summary', 'skeleton', 'text/primary', 'text/secondary', 'text/tertiary'],
    examples: [
      {
        title: 'Ready',
        render: () => (
          <div style={pad16}>
            <AiSummaryCard summary="Your family calls you a lot. They always say it's urgent. They almost never say why." />
          </div>
        ),
        code: `<AiSummaryCard summary="Your family calls you a lot. They always say it's urgent. They almost never say why." />`,
      },
      {
        title: 'Empty',
        render: () => (
          <div style={pad16}>
            <AiSummaryCard />
          </div>
        ),
        code: `<AiSummaryCard />`,
      },
      {
        title: 'Loading',
        render: () => (
          <div style={pad16}>
            <AiSummaryCard status="loading" lastUpdated="8:06 AM" />
          </div>
        ),
        code: `<AiSummaryCard status="loading" lastUpdated="8:06 AM" />`,
      },
    ],
    props: [
      {
        component: 'AiSummaryCard',
        props: [
          opt('status', "'empty' | 'loading' | 'ready'", "'ready' if summary is set, else 'empty'", 'Which state to show.'),
          opt('summary', 'ReactNode', undefined, 'The summary prose (bodyLarge).'),
          opt('footer', 'string', "'Last Updated: {lastUpdated}' when loading, else 'Usually ready after your first few calls'", 'Footer line.'),
          opt('lastUpdated', 'string', "'8:06 AM'", 'Time shown in the loading footer.'),
          opt('label', 'string', "'Your AI Summary'", 'Header label next to the orb.'),
        ],
      },
    ],
    dos: [
      'Write the summary in the assistant\'s voice, second person, short sentences.',
      'Use the loading state while the summary refreshes rather than hiding the card.',
    ],
    donts: [
      "Don't show a stale summary with a spinner; use `status=\"loading\"`.",
      "Don't use bullet lists; the summary is prose.",
    ],
  },
  {
    id: 'invite-card',
    name: 'Invite card',
    group: 'You tab',
    summary: 'The mint "Invite a Friend" pill at the bottom of the You tab, with the high-five art rising above it and a share button.',
    components: ['InviteCard'],
    importLine: "import { InviteCard } from '@equal/ds';",
    compose: ['InviteCard'],
    spec: 'You tab · Invite card',
    tokens: ['invite/bg', 'invite/stroke', 'bg/surface', 'text/primary', 'text/secondary'],
    examples: [
      {
        title: 'Default',
        render: () => (
          <OnPrimary pad="48px 20px 24px">
            <InviteCard onShare={noop} />
          </OnPrimary>
        ),
        code: `<InviteCard onShare={shareInvite} />`,
      },
      {
        title: 'Custom copy',
        render: () => (
          <OnPrimary pad="48px 20px 24px">
            <InviteCard title="Give a month free" subtitle="Share Equal AI with family" onShare={noop} />
          </OnPrimary>
        ),
        code: `<InviteCard title="Give a month free" subtitle="Share Equal AI with family" onShare={shareInvite} />`,
      },
    ],
    props: [
      {
        component: 'InviteCard',
        props: [
          opt('title', 'string', "'Invite a Friend'", 'Title (subheading).'),
          opt('subtitle', 'string', "'Invite Friends To Join'", 'Subtitle (caption Regular).'),
          opt('onShare', '() => void', undefined, 'Share button handler.'),
          opt('artRotation', 'number', '0', 'Extra rotation in degrees. The supplied art already carries the −6.39° tilt.'),
        ],
      },
    ],
    dos: [
      'Leave ~29dp of space above the card for the art to rise into.',
      'Keep it the last item on the You tab, above the bottom nav.',
    ],
    donts: [
      "Don't rotate the art again; the tilt is baked into the file.",
      "Don't crop the high-five illustration to fit the card.",
    ],
  },
  {
    id: 'contact-info-header',
    name: 'Contact info header',
    group: 'You tab',
    summary: 'The top of the Contact Info screen: a 96dp avatar, name, phone and a row of action tiles (Call, Mute, Save, Share).',
    components: ['ContactInfoHeader'],
    importLine: "import { ContactInfoHeader } from '@equal/ds';",
    compose: ['ContactInfoHeader'],
    spec: 'Contact Info (Contact Info)',
    tokens: ['bg/surface', 'stroke/subtle', 'text/action', 'text/primary', 'text/secondary'],
    examples: [
      {
        title: 'Saved contact',
        render: () => (
          <ContactInfoHeader
            name="Amit Nanda"
            phone="+91 98717 46113"
            avatar={{ initials: 'AN' }}
            actions={[
              { icon: 'call', label: 'Call' },
              { icon: 'silent-mode', label: 'Mute' },
              { icon: 'person-add', label: 'Save' },
              { icon: 'share', label: 'Share' },
            ]}
          />
        ),
        code: `<ContactInfoHeader name="Amit Nanda" phone="+91 98717 46113" avatar={{ initials: 'AN' }}
  actions={[
    { icon: 'call', label: 'Call' },
    { icon: 'silent-mode', label: 'Mute' },
    { icon: 'person-add', label: 'Save' },
    { icon: 'share', label: 'Share' },
  ]} />`,
      },
      {
        title: 'Unknown caller, with history',
        render: () => (
          <OnPrimary pad="0 0 16px">
            <ContactInfoHeader
              name={arnav.name}
              phone={arnav.phone}
              avatar={{ category: 'career' }}
              actions={[
                { icon: 'call', label: 'Call' },
                { icon: 'silent-mode', label: 'Mute' },
                { icon: 'person-add', label: 'Save' },
                { icon: 'share', label: 'Share' },
              ]}
            />
            <SectionHeader size="label" inset={22}>Call history</SectionHeader>
            <CallHistoryList>
              <CallHistoryRow direction="incoming" meta="Today • 30 Sec." time="02:30 PM" />
              <CallHistoryRow direction="missed" meta="Yesterday" time="11:04 AM" />
            </CallHistoryList>
          </OnPrimary>
        ),
        code: `<ContactInfoHeader name="Arnav Raj" phone="+91 98717 46113"
  avatar={{ category: 'career' }} actions={actions} />
<SectionHeader size="label" inset={22}>Call history</SectionHeader>
<CallHistoryList>
  <CallHistoryRow direction="incoming" meta="Today • 30 Sec." time="02:30 PM" />
  <CallHistoryRow direction="missed" meta="Yesterday" time="11:04 AM" />
</CallHistoryList>`,
      },
    ],
    props: [
      {
        component: 'ContactInfoHeader',
        props: [
          req('name', 'string', 'Name (headline).'),
          req('phone', 'string', 'Phone (bodyLarge, tabular).'),
          opt('avatar', 'ContactAvatarProps', undefined, 'Category or initials for the 96dp avatar.'),
          opt('actions', 'QuickAction[]', '[]', '{ icon, label, onClick? }[] shown as equal-width tiles.'),
        ],
      },
    ],
    dos: [
      'Use the four tiles Call · Mute · Save · Share, with Material Symbols icons in text/action.',
      'Flip the Mute tile label to Unmute when the contact is muted.',
    ],
    donts: [
      "Don't add more than four tiles; they share one row.",
      "Don't use emoji on the tiles; these are icon tiles, not action chips.",
    ],
  },

  // ============================================================ Flows
  {
    id: 'gender-selector',
    name: 'Gender selector',
    group: 'Flows',
    summary: 'The two-card Male / Female picker in Personal Details; the choice sets the profile avatar across the app.',
    components: ['GenderSelector'],
    importLine: "import { GenderSelector } from '@equal/ds';",
    compose: ['GenderSelector'],
    spec: 'Flows · Profile',
    tokens: ['bg/surface', 'button/primary', 'gradient/gender-selected', 'text/primary'],
    examples: [
      {
        title: 'Free vs paid',
        note: 'Free users get the plain bust; paid users get the avatar on its gold background and scalloped ring.',
        width: 390,
        render: () => (
          <div style={{ padding: 16, width: 390, boxSizing: 'border-box', display: 'flex', flexDirection: 'column', gap: 16 }}>
            <GenderSelector value="male" tier="free" onChange={noop} />
            <GenderSelector value="male" tier="paid" onChange={noop} />
          </div>
        ),
        code: `<GenderSelector value={gender} onChange={setGender} tier="free" />
<GenderSelector value={gender} onChange={setGender} tier="paid" />`,
      },
      {
        title: 'Interactive',
        render: () => (
          <div style={pad16}>
            <GenderDemo />
          </div>
        ),
        code: `const [gender, setGender] = useState<Gender>('male');

<GenderSelector value={gender} onChange={setGender} />
<ProfileAvatar variant={gender} size={56} />`,
      },
      {
        title: 'In Personal Details',
        render: () => (
          <OnPrimary pad="0 16px 16px">
            <AppBar title="Personal Details" onBack={noop} />
            <Col gap={20}>
              <NameFieldDemo />
              <div>
                <SectionHeader inset={0}>Gender</SectionHeader>
                <GenderSelector value="female" />
              </div>
            </Col>
          </OnPrimary>
        ),
        code: `<AppBar title="Personal Details" onBack={goBack} />
<TextField label="Name" value={name} onChange={setName}
  helper="Your AI will call you 'Keshav'" />
<SectionHeader inset={0}>Gender</SectionHeader>
<GenderSelector value={gender} onChange={setGender} />`,
      },
    ],
    props: [
      {
        component: 'GenderSelector',
        props: [
          req('value', "'male' | 'female'", 'The selected gender (Gender).'),
          opt('onChange', '(g: Gender) => void', undefined, 'Called when a card is tapped.'),
        ],
      },
    ],
    dos: [
      'Update the profile avatar (hero and bottom nav) as soon as the user picks.',
      'Place it under a GENDER overline, below the Name field.',
    ],
    donts: [
      "Don't crop the bust art; if it is missing the card falls back to the ringed avatar.",
      "Don't add a third card or a text input here.",
    ],
  },
  {
    id: 'address',
    name: 'Address',
    group: 'Flows',
    summary: 'Saved address cards with an Edit / Delete overflow, and the add/edit form (label chips, address lines, city, pincode, guiding direction, Save).',
    components: ['AddressCard', 'AddressForm'],
    importLine: "import { AddressCard, AddressForm, formatAddress } from '@equal/ds';",
    compose: ['AddressCard', 'AddressForm'],
    spec: 'Flows · Addresses',
    tokens: ['bg/surface', 'text/primary', 'text/secondary', 'text/tertiary', 'text/danger', 'chip/selected-bg', 'button/primary'],
    examples: [
      {
        title: 'Saved addresses',
        note: 'Open ⋮ → Delete address to remove one; the empty state appears when none are left.',
        render: () => (
          <OnPrimary pad="16px 20px">
            <AddressListDemo />
          </OnPrimary>
        ),
        code: `{addresses.map((a) => (
  <AddressCard key={a.id} label={a.label} address={formatAddress(a)}
    onEdit={() => edit(a)} onDelete={() => confirmDelete(a)} />
))}`,
      },
      {
        title: 'Long address',
        render: () => (
          <OnPrimary pad="16px 20px">
            <AddressCard label={sampleAddresses[3].label} address={formatAddress(sampleAddresses[3])} onEdit={noop} />
          </OnPrimary>
        ),
        code: `<AddressCard label="Work"
  address="Flat 4B, Block C, Skyline Towers, Phase 2, HITECH city, MG Road, Opposite City Center Mall, Banjara Hills, Hyderabad- 500034"
  onEdit={edit} />`,
      },
      {
        title: 'Edit address form',
        note: 'Save stays disabled until line 1, city and a 6-digit pincode are filled.',
        render: () => (
          <OnPrimary pad="16px">
            <AddressFormDemo />
          </OnPrimary>
        ),
        code: `<AddressForm
  initial={{ label: 'Home', line1: 'House no. 4, Tony Street', line2: 'Financial District', city: 'Hyderabad', pincode: '500032' }}
  onSave={(address) => save(address)} />`,
      },
    ],
    props: [
      {
        component: 'AddressCard',
        props: [
          req('label', 'string', 'Label (subheading): Home, Work, or a custom name.'),
          req('address', 'string', 'Full address (captionWrap). Build it with formatAddress(savedAddress).'),
          opt('onEdit', '() => void', undefined, 'Adds "Edit" to the overflow menu.'),
          opt('onDelete', '() => void', undefined, 'Adds "Delete address" (danger) to the overflow menu.'),
        ],
      },
      {
        component: 'AddressForm',
        props: [
          opt('initial', 'Partial<SavedAddress>', undefined, 'Prefill for editing. A label outside Home / Work selects Other.'),
          opt('onSave', "(a: Omit<SavedAddress, 'id'>) => void", undefined, 'Called on Save with { label, line1, line2, city, pincode, direction }.'),
        ],
      },
    ],
    dos: [
      'Use formatAddress so every card reads "line1, line2, City- pincode".',
      'Open the form in a titled BottomSheet ("Edit Address" / "Add Address").',
      'Route Delete address through the trash confirm sheet.',
    ],
    donts: [
      "Don't show Edit as a separate button; it lives in the ⋮ menu.",
      "Don't accept a pincode that isn't six digits; the form blocks Save.",
    ],
  },
  {
    id: 'instruction-cards',
    name: 'Instruction cards',
    group: 'Flows',
    summary: 'The pieces of the Instructions flow: recommended and preset cards on the hub, preset and active instruction cards with When / Do rows, and the preview shown in the setup chat.',
    components: ['RecommendedCard', 'SuggestionsComingCard', 'PresetCard', 'PresetInstructionCard', 'InstructionCard', 'WhenDoRow', 'InstructionPreview'],
    importLine: "import { RecommendedCard, SuggestionsComingCard, PresetCard, PresetInstructionCard, InstructionCard, WhenDoRow, InstructionPreview } from '@equal/ds';",
    compose: ['RecommendedCard', 'SuggestionsComingCard', 'PresetCard', 'PresetInstructionCard', 'InstructionCard', 'WhenDoRow', 'InstructionPreview'],
    spec: 'Flows · Instructions · Instruction Card',
    tokens: ['bg/surface', 'chip/when-bg', 'chip/do-bg', 'preset/tint-red', 'preset/tint-green', 'preset/tint-orange', 'gradient/suggestion-card', 'text/primary', 'text/secondary', 'text/tertiary', 'text/action', 'button/primary'],
    examples: [
      {
        title: 'Recommended for you',
        note: 'Tap + to add; once both are added the "New suggestions coming…" card takes their place.',
        render: () => (
          <OnPrimary pad="16px">
            <RecommendedDemo />
          </OnPrimary>
        ),
        code: `<RecommendedCard emoji="🚫" title="Decline sales & spam"
  description="Politely declines sales and insurance calls." onAdd={add} />
<RecommendedCard emoji="💬" title="Take a message"
  description="Takes their name, number and message." onAdd={add} />

{allApplied && <SuggestionsComingCard />}`,
      },
      {
        title: 'Browse presets',
        width: 'wide',
        render: () => (
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(160px, 1fr))', gap: 12 }}>
            {presets.map((p) => (
              <PresetCard key={p.id} emoji={p.emoji} tint={p.tint} title={p.title} description={p.description} onClick={noop} />
            ))}
          </div>
        ),
        code: `<PresetCard emoji="🛡️" tint="red" title="Screen & Protect"
  description="Decline spam, hold the line on loan and payments call, close wrong call" onClick={open} />
<PresetCard emoji="📞" tint="green" title="Handle everyday calls"
  description="Take message, guide deliveries, relay to family, start in your language" onClick={open} />
<PresetCard emoji="💼" tint="orange" title="Run my business"
  description="Capture enquiries, orders, bookings, job, and supplier calls" onClick={open} />`,
      },
      {
        title: 'Preset instructions',
        note: 'Apply turns into Applied.',
        render: () => (
          <OnPrimary pad="16px">
            <PresetApplyDemo />
          </OnPrimary>
        ),
        code: `<PresetInstructionCard instruction={instructionLibrary.declineSales}
  applied={applied} onApply={apply} />`,
      },
      {
        title: 'Active instructions',
        note: 'Outlined card: bold title + ⋮, the source pill, a divider, then WHEN / DO in fading pills.',
        width: 390,
        render: () => (
          <OnPrimary pad="16px">
            <Col gap={8}>
              <InstructionCard instruction={{ ...instructionLibrary.declineSales, source: { emoji: '✨', label: 'Recommended for you' } }} onEdit={noop} onDelete={noop} />
              <InstructionCard instruction={{ ...instructionLibrary.loanEmi, source: { emoji: '💼', label: 'Run my business' } }} onEdit={noop} onDelete={noop} />
              <InstructionCard instruction={{ ...instructionLibrary.difficult, source: { emoji: '📞', label: 'Handle everyday calls' } }} onEdit={noop} onDelete={noop} />
            </Col>
          </OnPrimary>
        ),
        code: `<InstructionCard instruction={{
  id: 'decline-sales',
  title: 'Decline sales, promo spam calls',
  source: { emoji: '✨', label: 'Recommended for you' },
  when: 'The caller is selling loan, insurance, credit card, product, or another unsolicited offer',
  do: 'Politely say I am not interested and end the conversation without discussing the offer',
}} onEdit={edit} onDelete={confirmDelete} />`,
      },
      {
        title: 'Preview in the setup chat',
        note: 'Tap 👍 Confirm to add the ✅ reaction.',
        render: () => <PreviewConfirmDemo />,
        code: `<InstructionPreview title="Decline sales calls"
  when="Caller is selling a loan, insurance or product"
  action="Politely decline and end the conversation"
  time="03:21" confirmed={confirmed} />`,
      },
    ],
    props: [
      {
        component: 'RecommendedCard',
        props: [
          req('emoji', 'string', 'Leading emoji (26dp).'),
          req('title', 'string', 'Title (subheading).'),
          req('description', 'string', 'One-line description (body1).'),
          opt('onAdd', '() => void', undefined, 'Shows the + button.'),
        ],
      },
      { component: 'SuggestionsComingCard', props: [] },
      {
        component: 'PresetCard',
        props: [
          req('emoji', 'string', 'Emoji in the tinted circle.'),
          req('tint', "'red' | 'green' | 'orange'", 'Circle tint (PresetTint).'),
          req('title', 'string', 'Title (subheading).'),
          req('description', 'string', 'Description (captionWrap).'),
          opt('onClick', '() => void', undefined, 'Opens the preset.'),
        ],
      },
      {
        component: 'PresetInstructionCard',
        props: [
          req('instruction', 'Instruction', '{ id, title, when, do, source? }.'),
          opt('applied', 'boolean', undefined, 'Shows "Applied" instead of the Apply button.'),
          opt('onApply', '() => void', undefined, 'Apply handler.'),
        ],
      },
      {
        component: 'InstructionCard',
        props: [
          req('instruction', 'Instruction', '{ id, title, when, do, source? }. source renders as an emoji chip under the title.'),
          opt('onEdit', '() => void', undefined, 'Adds "Edit" to the overflow menu.'),
          opt('onDelete', '() => void', undefined, 'Adds "Delete instruction" (danger) to the overflow menu.'),
        ],
      },
      {
        component: 'WhenDoRow',
        props: [
          req('kind', "'when' | 'do'", 'Which chip to show.'),
          req('children', 'ReactNode', 'The condition or action (body1).'),
          opt('upper', 'boolean', 'false', 'Uppercase overline chip (active instructions) instead of sentence case (presets).'),
        ],
      },
      {
        component: 'InstructionPreview',
        props: [
          req('title', 'string', 'Instruction title (body2).'),
          req('when', 'ReactNode', 'Condition, after "When:".'),
          req('action', 'ReactNode', 'Action, after "Do:". Bold the user\'s answers for input-required instructions.'),
          opt('time', 'string', undefined, 'Timestamp (micro).'),
          opt('confirmed', 'boolean', undefined, 'Adds the ✅ reaction.'),
        ],
      },
    ],
    dos: [
      'Write When as the situation and Do as the assistant\'s behaviour, in plain first person ("say I am not interested").',
      'Use sentence-case When / Do on presets and uppercase WHEN / DO on active instructions (`upper`).',
      'Confirm with 👍 Confirm (unread) next to ✍️ Edit (read) under the preview.',
      'Show SuggestionsComingCard once every recommendation is applied.',
    ],
    donts: [
      "Don't delete an instruction straight from the ⋮ menu; confirm first.",
      "Don't use Material icons on preset or recommended cards; they lead with emoji.",
      "Don't put two filled buttons on a preset instruction; Apply is the only commit.",
    ],
  },
];
