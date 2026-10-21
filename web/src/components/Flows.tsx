// Components for the Profile, Addresses and Instructions flows (the flow designs).
import { ActionCardBody, instructionLines } from './ActionCard';
import { useEffect, useId, useRef, useState, type CSSProperties, type ReactNode } from 'react';
import { Asset, hasAsset, type AssetName } from '../foundations/Asset';
import patternUrl from '../foundations/chat-pattern@3x.webp';
import { Emoji, Icon } from '../foundations/Icon';
import { Text } from '../foundations/Text';
import { Announce, inertWhen, radioGroupKeyDown, rovingTabIndex, useFocusHandoff, useModal } from '../foundations/a11y';
import { AiAvatar, ProfileAvatar, type PlanTier } from './Avatar';
import { Button, IconButton } from './Button';
import { ContextMenu, type MenuItem } from './ContextMenu';
import { SegmentedChips, TextField } from './Inputs';
import './Flows.css';

// ================================================================ shared

/** Centred 3D illustration at the top of a settings detail screen (Silent Mode, WhatsApp Updates). */
export function IllustrationHeader({ asset, size = 160 }: { asset: AssetName; size?: number }) {
  return (
    <div style={{ display: 'flex', justifyContent: 'center', padding: '8px 0 16px' }}>
      <Asset name={asset} size={size} />
    </div>
  );
}

/** More-vert button that opens an anchored ContextMenu. Closes on outside click / Escape. */
export function OverflowMenu({ items, label = 'More options' }: { items: MenuItem[]; label?: string }) {
  const [open, setOpen] = useState(false);
  const ref = useRef<HTMLDivElement>(null);
  const trigger = () => ref.current?.querySelector<HTMLButtonElement>('button[aria-haspopup]');
  // Close and give focus back to the ⋮ trigger (Escape, or after choosing an item).
  const close = () => { setOpen(false); trigger()?.focus({ preventScroll: true }); };
  useEffect(() => {
    if (!open) return;
    const onDown = (e: PointerEvent) => !ref.current?.contains(e.target as Node) && setOpen(false);
    const onKey = (e: KeyboardEvent) => e.key === 'Escape' && close();
    document.addEventListener('pointerdown', onDown);
    document.addEventListener('keydown', onKey);
    return () => {
      document.removeEventListener('pointerdown', onDown);
      document.removeEventListener('keydown', onKey);
    };
  }, [open]);
  return (
    <div ref={ref} style={{ position: 'relative', zIndex: open ? 30 : undefined }}>
      <IconButton
        icon="more-vert"
        label={label}
        size={32}
        iconSize={20}
        onClick={() => setOpen((o) => !o)}
        onKeyDown={(e) => { if (e.key === 'ArrowDown' || e.key === 'ArrowUp') { e.preventDefault(); setOpen(true); } }}
        aria-haspopup="menu"
        aria-expanded={open}
      />
      {open && (
        <div className="eq-overflow">
          <ContextMenu autoFocus label={label} onClose={() => close()} items={items.map((it) => ({ ...it, onSelect: () => (close(), it.onSelect?.()) }))} minWidth={168} />
        </div>
      )}
    </div>
  );
}

/**
 * Doodle chat wallpaper — BG pattern.svg, used as an alpha mask (360 × 750 tile) and tinted with
 * `chat/wallpaper-pattern` (#737373 @ 8%) over `chat/wallpaper-bg`, so one pattern serves light and dark.
 */
export function ChatWallpaper({ children, style }: { children: ReactNode; style?: CSSProperties }) {
  return (
    <div className="eq-wallpaper" style={{ ['--eq-pattern-url' as string]: `url(${patternUrl})`, ...style }}>
      {children}
    </div>
  );
}

// ================================================================ Profile flow

export type Gender = 'male' | 'female';

/**
 * Gender picker: two cards with a faint ♂ / ♀ watermark and the avatar. Selected: green outline +
 * fill. `tier`: free shows the plain bust; paid shows the avatar on its gold background and ring.
 */
export function GenderSelector({ value, onChange, tier = 'free' }: { value: Gender; onChange?: (g: Gender) => void; tier?: PlanTier }) {
  return (
    <div role="radiogroup" aria-label="Gender" onKeyDown={radioGroupKeyDown} style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
      {(['male', 'female'] as const).map((g, i) => {
        const bust = g === 'male' ? 'avatar-male-bust' : 'avatar-female-bust';
        return (
          <button key={g} type="button" role="radio" aria-checked={value === g} tabIndex={rovingTabIndex(i, value === 'male' ? 0 : value === 'female' ? 1 : -1)} className={`eq-reset-btn eq-pressable eq-gender ${value === g ? 'is-on' : ''}`} onClick={() => onChange?.(g)}>
            <Text variant="inherit" color="none" className="eq-gender__mark" aria-hidden>{g === 'male' ? '♂' : '♀'}</Text>
            <Text variant="subheading" style={{ position: 'relative' }}>{g === 'male' ? 'Male' : 'Female'}</Text>
            <span className="eq-gender__art">
              {tier === 'paid' ? <ProfileAvatar variant={g} size={52} tier="paid" label="" /> : hasAsset(bust) ? <Asset name={bust} width={60} height={56} alt="" /> : <ProfileAvatar variant={g} size={52} label="" />}
            </span>
          </button>
        );
      })}
    </div>
  );
}

// ================================================================ Addresses flow

export type SavedAddress = { id: string; label: string; line1: string; line2?: string; city: string; pincode: string; direction?: string };

export const formatAddress = (a: SavedAddress) => [a.line1, a.line2, `${a.city}- ${a.pincode}`].filter(Boolean).join(', ');

/** Saved address card — label (subheading), address (caption Regular, wraps), overflow menu Edit / Delete. */
export function AddressCard({ label, address, onEdit, onDelete }: { label: string; address: string; onEdit?: () => void; onDelete?: () => void }) {
  return (
    <div className="eq-flow-card" style={{ display: 'flex', gap: 8, alignItems: 'flex-start' }}>
      <div style={{ flex: 1, minWidth: 0 }}>
        <Text as="div" variant="subheading">{label}</Text>
        <Text as="p" variant="captionWrap" color="secondary" style={{ marginTop: 4 }}>{address}</Text>
      </div>
      {(onEdit || onDelete) && (
        <div style={{ margin: '-6px -8px 0 0' }}>
          <OverflowMenu
            label={`${label} options`}
            items={[
              ...(onEdit ? [{ label: 'Edit', icon: 'edit' as const, onSelect: onEdit }] : []),
              ...(onDelete ? [{ label: 'Delete address', icon: 'delete' as const, danger: true, onSelect: onDelete }] : []),
            ]}
          />
        </div>
      )}
    </div>
  );
}

const LABELS = ['Home', 'Work', 'Other'] as const;

/** Edit / add address form — label chips, two address lines, city + pincode, optional guiding direction, Save. */
export function AddressForm({ initial, onSave }: { initial?: Partial<SavedAddress>; onSave?: (a: Omit<SavedAddress, 'id'>) => void }) {
  const [label, setLabel] = useState<(typeof LABELS)[number]>(((LABELS as readonly string[]).includes(initial?.label ?? '') ? initial?.label : initial?.label ? 'Other' : 'Home') as (typeof LABELS)[number]);
  const [line1, setLine1] = useState(initial?.line1 ?? '');
  const [line2, setLine2] = useState(initial?.line2 ?? '');
  const [city, setCity] = useState(initial?.city ?? '');
  const [pincode, setPincode] = useState(initial?.pincode ?? '');
  const [direction, setDirection] = useState(initial?.direction ?? '');
  const pinError = pincode && !/^\d{6}$/.test(pincode) ? 'Enter a 6-digit pincode' : undefined;
  const canSave = line1.trim() && city.trim() && /^\d{6}$/.test(pincode);
  const labelId = useId();
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 20 }}>
      <div>
        <Text as="div" id={labelId} variant="overline" color="secondary" style={{ marginBottom: 10 }}>LABEL*</Text>
        <SegmentedChips options={LABELS} value={label} onChange={setLabel} labelledBy={labelId} />
      </div>
      <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
        <Text as="div" variant="overline" color="secondary">ADDRESS</Text>
        <TextField aria-label="Address line 1" placeholder="Flat, house no., building" value={line1} onChange={setLine1} multiline />
        <TextField aria-label="Address line 2" placeholder="Street, area, landmark" value={line2} onChange={setLine2} multiline />
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
          <TextField aria-label="City" placeholder="City" value={city} onChange={setCity} align="center" />
          <TextField aria-label="Pincode" placeholder="Pincode" value={pincode} onChange={(v) => setPincode(v.replace(/\D/g, '').slice(0, 6))} align="center" inputMode="numeric" className="eq-tnum" error={pinError} />
        </div>
      </div>
      <TextField label="Guiding direction" optional value={direction} onChange={setDirection} placeholder="e.g. Near City Center Mall" />
      <Button block size="lg" disabled={!canSave} onClick={() => canSave && onSave?.({ label, line1, line2, city, pincode, direction })}>Save</Button>
    </div>
  );
}

// ================================================================ Instructions flow

/**
 * Tinted "+ Apply" pill (Preset Instruction design): brand green at 8 % with a green + and label,
 * 24dp high. Used on recommended cards and preset instructions.
 */
export function ApplyButton({ label = 'Apply', 'aria-label': ariaLabel, onClick }: { label?: string; 'aria-label'?: string; onClick?: () => void }) {
  return (
    <Button size="xs" variant="tonal" leadingIcon="add" aria-label={ariaLabel} onClick={onClick}>{label}</Button>
  );
}

/** Horizontal "Recommended for you" card (191 × 114): emoji 24, + Apply pill, title 14 semibold, description 12/16. */
export function RecommendedCard({ emoji, title, description, onAdd }: { emoji: string; title: string; description: string; onAdd?: () => void }) {
  return (
    <div className="eq-rec">
      <div className="eq-rec__top">
        <Emoji char={emoji} size={24} />
        {onAdd && <ApplyButton aria-label={`Apply ${title}`} onClick={onAdd} />}
      </div>
      <div>
        <Text as="div" variant="body2" style={{ fontWeight: 600, lineHeight: '18px' }}>{title}</Text>
        <Text as="p" variant="caption" style={{ fontWeight: 400 }}>{description}</Text>
      </div>
    </div>
  );
}

/**
 * Shown in place of recommendations when all are applied or none exist: a grey card with three soft
 * green glows, the orb + "New suggestions coming…", a 14 medium line and the 140dp robot top-right.
 */
export function SuggestionsComingCard() {
  return (
    <div className="eq-suggest">
      <span className="eq-suggest__glow" style={{ left: 290, top: 150, width: 89, height: 89 }} />
      <span className="eq-suggest__glow" style={{ left: -12, top: 167, width: 68, height: 68 }} />
      <span className="eq-suggest__glow" style={{ left: 107, top: 45, width: 130, height: 130 }} />
      <div className="eq-suggest__art"><Asset name="illustration-suggestions" size={140} /></div>
      <div style={{ position: 'relative', display: 'flex', alignItems: 'center', gap: 4, height: 24 }}>
        <AiAvatar size={18} label="" />
        <Text variant="caption" color="secondary" style={{ fontWeight: 700 }}>New suggestions coming…</Text>
      </div>
      <Text as="p" variant="body2" style={{ position: 'relative', marginTop: 13, maxWidth: 184, lineHeight: '18px' }}>More suggestions will show up here as I learn your calls</Text>
    </div>
  );
}

export type PresetTint = 'green' | 'blue' | 'orange';
export type PresetArt = 'preset-screen-protect' | 'preset-everyday-calls' | 'preset-business';

/** 3D preset art in its tinted circle (44dp circle, 33dp art). */
export function PresetIcon({ art, tint, size = 44 }: { art: AssetName; tint: PresetTint | 'lime'; size?: number }) {
  return (
    <span className="eq-preset__icon" style={{ width: size, height: size, background: `var(--eq-preset-tint-${tint})` }}>
      <Asset name={art} size={Math.round(size * 0.75)} />
    </span>
  );
}

/** "Browse presets" card (320 × 85): art in a tinted circle, title 16 semibold, description 12/16, chevron. */
export function PresetCard({ art, tint, title, description, onClick }: { art: PresetArt; tint: PresetTint; title: string; description: string; onClick?: () => void }) {
  return (
    <button type="button" className="eq-reset-btn eq-preset" onClick={onClick}>
      <PresetIcon art={art} tint={tint} />
      <div style={{ flex: 1, minWidth: 0 }}>
        <Text as="div" variant="subheading" style={{ fontWeight: 600, lineHeight: '19px' }}>{title}</Text>
        <Text as="p" variant="caption" color="secondary" style={{ fontWeight: 400, marginTop: 2 }}>{description}</Text>
      </div>
      <Icon name="chevron-right" size={22} color="var(--eq-text-placeholder)" />
    </button>
  );
}

/**
 * "Your instructions" entry (319 × 76): the robot-with-wand on the lime tint, Active Instructions
 * (14 semibold) and "N active instructions" (12 secondary), chevron.
 */
export function ActiveInstructionsRow({ count, onClick }: { count: number; onClick?: () => void }) {
  return (
    <button type="button" className="eq-reset-btn eq-preset eq-preset--entry" onClick={onClick}>
      <PresetIcon art="mascot-instructions" tint="lime" />
      <div style={{ flex: 1, minWidth: 0 }}>
        <Text as="div" variant="body2" style={{ fontWeight: 600 }}>Active Instructions</Text>
        <Text as="div" variant="caption" color="secondary" style={{ fontWeight: 400 }}>{count ? `${count} active instruction${count === 1 ? '' : 's'}` : 'Manage how your assistant handles calls'}</Text>
      </div>
      <Icon name="chevron-right" size={22} color="var(--eq-text-placeholder)" />
    </button>
  );
}

/**
 * When / Do line of an instruction: WHEN / DO 12 semibold caps in a 52dp pill that fades from the
 * tint (`gradient/when-chip`, `gradient/do-chip`) to the card colour, text 14/18 beside it.
 * Used by preset instructions and active instructions alike.
 */
export function WhenDoRow({ kind, children }: { kind: 'when' | 'do'; children: ReactNode }) {
  return (
    <div className="eq-whendo">
      <span className={`eq-whendo__pill eq-whendo__pill--${kind}`}>
        <Text variant="caption" color="secondary" style={{ fontWeight: 600 }}>{kind === 'when' ? 'WHEN' : 'DO'}</Text>
      </span>
      <Text as="p" variant="body1" color="primary" style={{ flex: 1, lineHeight: '18px' }}>{children}</Text>
    </div>
  );
}

export type Instruction = { id: string; title: string; when: string; do: string; source?: { art: PresetArt; label: string } };

/** Preset instruction with an Apply action (preset detail screen): title 16 semibold, + Apply pill, WHEN / DO. */
export function PresetInstructionCard({ instruction, applied, onApply }: { instruction: Instruction; applied?: boolean; onApply?: () => void }) {
  const handoff = useFocusHandoff<HTMLSpanElement>(!!applied);
  return (
    <div className="eq-instruction-card eq-instruction-card--preset">
      <div style={{ display: 'flex', gap: 12, alignItems: 'flex-start' }}>
        <Text as="h3" variant="subheading" style={{ flex: 1, fontWeight: 600, lineHeight: '20px' }}>{instruction.title}</Text>
        {applied ? (
          <span ref={handoff.targetRef} tabIndex={-1} className="eq-applied"><Icon name="check" size={16} /><Text variant="buttonSmall" color="none">Applied</Text></span>
        ) : (
          <ApplyButton aria-label={`Apply ${instruction.title}`} onClick={() => { handoff.markFocus(); onApply?.(); }} />
        )}
        <Announce message={applied ? `${instruction.title} applied` : ''} />
      </div>
      <WhenDoRow kind="when">{instruction.when}</WhenDoRow>
      <WhenDoRow kind="do">{instruction.do}</WhenDoRow>
    </div>
  );
}

/** Source badge on an active instruction: the preset's 3D art (16) and its name, outlined pill. */
export function SourceBadge({ art, label }: { art: PresetArt; label: string }) {
  return (
    <span className="eq-source">
      <Asset name={art} size={16} />
      <Text variant="caption" color="secondary" style={{ fontSize: 10, lineHeight: '12px', fontWeight: 600 }}>{label}</Text>
    </span>
  );
}

/**
 * Active instruction (328dp card, padding 16, gap 16): title 16/20 bold + ⋮ overflow (Edit / Delete
 * instruction), the source badge under it, then the WHEN and DO rows. No divider.
 */
export function InstructionCard({ instruction, onEdit, onDelete }: { instruction: Instruction; onEdit?: () => void; onDelete?: () => void }) {
  return (
    <div className="eq-instruction-card">
      <div style={{ display: 'flex', flexDirection: 'column', gap: 6, alignItems: 'flex-start' }}>
        <div style={{ display: 'flex', gap: 8, alignItems: 'flex-start', alignSelf: 'stretch' }}>
          <Text as="h3" variant="subheading" style={{ flex: 1, minWidth: 0, lineHeight: '20px', fontWeight: 700 }}>{instruction.title}</Text>
          {(onEdit || onDelete) && (
            <div style={{ margin: '-10px -14px 0 0' }}>
              <OverflowMenu
                label={`${instruction.title} options`}
                items={[
                  ...(onEdit ? [{ label: 'Edit', icon: 'edit' as const, onSelect: onEdit }] : []),
                  ...(onDelete ? [{ label: 'Delete instruction', icon: 'delete' as const, danger: true, onSelect: onDelete }] : []),
                ]}
              />
            </div>
          )}
        </div>
        {instruction.source && <SourceBadge art={instruction.source.art} label={instruction.source.label} />}
      </div>
      <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
        <WhenDoRow kind="when">{instruction.when}</WhenDoRow>
        <WhenDoRow kind="do">{instruction.do}</WhenDoRow>
      </div>
    </div>
  );
}

/**
 * Instruction preview inside the setup chat: the shared action-card body (note_stack icon on a white
 * circle, title, When / Do lines, time) in the action card's white 8dp frame; optional ✅ reaction.
 */
export function InstructionPreview({ title, when, action, time, confirmed }: { title: string; when: ReactNode; action: ReactNode; time?: string; confirmed?: boolean }) {
  return (
    <div className="eq-preview-wrap">
      <ActionCardBody icon="instructions" title={title} details={instructionLines(when, action)} time={time} />
      {confirmed && (
        <span className="eq-reaction" role="img" aria-label="Confirmed"><Emoji char="✅" size={18} /></span>
      )}
    </div>
  );
}

/** Full-height sheet with a title row (orb + title + close) — the "Setting up instruction" chat. */
export function FullSheet({ open, onClose, title, children, footer }: { open: boolean; onClose?: () => void; title: string; children: ReactNode; footer?: ReactNode }) {
  const modal = useModal<HTMLElement>({ open, onClose });
  return (
    <div className={`eq-fullsheet ${open ? 'is-open' : ''}`} aria-hidden={!open} {...inertWhen(!open)}>
      <div className="eq-fullsheet__scrim" onClick={onClose} />
      <section className="eq-fullsheet__panel" role="dialog" aria-label={title} {...modal}>
        <header className="eq-fullsheet__head">
          <AiAvatar size={32} label="" />
          <Text variant="subheading" style={{ flex: 1 }}>{title}</Text>
          <IconButton icon="close" label="Close" variant="neutral" size={40} iconSize={22} onClick={onClose} />
        </header>
        <ChatWallpaper style={{ flex: 1, minHeight: 0, display: 'flex', flexDirection: 'column' }}>
          <div style={{ flex: 1, minHeight: 0, overflowY: 'auto', scrollbarWidth: 'none' }}>{children}</div>
          {footer && <div style={{ padding: '8px 16px 16px' }}>{footer}</div>}
        </ChatWallpaper>
      </section>
    </div>
  );
}
