// Assistant Snooze + Force Update designs: the duration dialog, the status sheet (errors and the
// soft update), the troubleshooting list, and the update art.
import { type ReactNode } from 'react';
import { useModal } from '../foundations/a11y';
import { Asset, type AssetName } from '../foundations/Asset';
import { Icon } from '../foundations/Icon';
import type { IconName } from '../foundations/icons';
import { Text } from '../foundations/Text';
import { Button, IconButton, TextButton } from './Button';
import { Dialog } from './Dialog';
import { RadioGroup } from './Radio';
import { StatusBar } from './StatusBar';
import './Snooze.css';

export type SnoozeDuration = '15m' | '1h' | '6h' | 'rest-of-day' | 'manual';

export const snoozeOptions: { value: SnoozeDuration; label: string }[] = [
  { value: '15m', label: '15 Minutes' },
  { value: '1h', label: '1 Hour' },
  { value: '6h', label: '6 Hours' },
  { value: 'rest-of-day', label: 'Rest of the day' },
  { value: 'manual', label: 'Until I turn it back on' },
];

export type SnoozeDurationDialogProps = {
  open?: boolean;
  /** Nothing is preselected (null): OK stays disabled until a choice is made. */
  value: SnoozeDuration | null;
  onChange?: (v: SnoozeDuration) => void;
  onConfirm?: (v: SnoozeDuration) => void;
  onCancel?: () => void;
  confirmLabel?: string;
  mode?: 'overlay' | 'inline';
};

/** "Want to deactivate assistant?": a Dialog with the duration RadioGroup; OK is disabled until a choice is made. */
export function SnoozeDurationDialog({ open = true, value, onChange, onConfirm, onCancel, confirmLabel = 'OK', mode = 'overlay' }: SnoozeDurationDialogProps) {
  return (
    <Dialog
      open={open}
      mode={mode}
      title="Want to deactivate assistant?"
      body="Choose how long the assistant stays off"
      confirmLabel={confirmLabel}
      confirmDisabled={value === null}
      onConfirm={() => value && onConfirm?.(value)}
      onCancel={onCancel}
    >
      <RadioGroup label="Choose how long the assistant stays off" value={value} onChange={(v) => onChange?.(v)} options={snoozeOptions} size="m" gap={20} />
    </Dialog>
  );
}

export type TroubleshootItem = { icon: IconName; label: string };

/** Information-only list of fixes (Assistant Activation Failed): centred icon + label rows with inset dividers. */
export function TroubleshootList({ items }: { items: TroubleshootItem[] }) {
  return (
    <ul className="eq-troubleshoot" role="list">
      {items.map((it) => (
        <li key={it.label} className="eq-troubleshoot__row">
          <Icon name={it.icon} size={18} color="var(--eq-text-secondary)" />
          <Text variant="body1">{it.label}</Text>
        </li>
      ))}
    </ul>
  );
}

/** The update robot with the coded upload badge and a soft glow behind it. */
export function UpdateArt({ size = 160 }: { size?: number }) {
  return (
    <span className="eq-update-art" style={{ width: size, height: size }} aria-hidden>
      <span className="eq-update-art__glow" />
      <Asset name="mascot-update" size={size} alt="" />
      <span className="eq-update-art__badge" style={{ width: Math.round(size * 0.225), height: Math.round(size * 0.225) }}>
        <Icon name="send" size={Math.round(size * 0.1125)} color="var(--eq-text-on-action)" />
      </span>
    </span>
  );
}

export type AlertSheetProps = {
  /** An asset, or any node (e.g. `<UpdateArt size={144} />`); it straddles the sheet's top edge. */
  art: AssetName | ReactNode;
  artSize?: number;
  title: string;
  body: ReactNode;
  cta: string;
  ctaIcon?: IconName;
  onCta?: () => void;
  secondary?: string;
  secondaryIcon?: IconName;
  /** Underlined secondary ("Maybe Later"). */
  secondaryUnderline?: boolean;
  onSecondary?: () => void;
  onClose?: () => void;
  /** Between the body and the CTA, e.g. a TroubleshootList. */
  children?: ReactNode;
};

/** Bottom sheet with the art straddling its top edge: errors (Snooze) and the soft update. */
export function AlertSheet({ art, artSize = 96, title, body, cta, ctaIcon, onCta, secondary, secondaryIcon, secondaryUnderline, onSecondary, onClose, children }: AlertSheetProps) {
  const modal = useModal({ open: true, onClose });
  return (
    <div className="eq-alert-sheet" role="dialog" aria-label={title} {...modal} style={{ paddingTop: artSize / 2 + 8 }}>
      <div className="eq-alert-sheet__art" style={{ width: artSize, height: artSize, top: -artSize / 2 }}>
        {typeof art === 'string' ? <Asset name={art as AssetName} size={artSize} alt="" /> : art}
      </div>
      {onClose && <IconButton icon="close" label="Close" variant="neutral" size={44} onClick={onClose} className="eq-alert-sheet__close" />}
      <Text as="h2" variant="title" align="center">{title}</Text>
      <Text as="p" variant="body1" color="secondary" align="center" style={{ margin: '4px 0 0', maxWidth: 292 }}>{body}</Text>
      {children && <div style={{ alignSelf: 'stretch', display: 'flex', justifyContent: 'center', marginTop: 20 }}>{children}</div>}
      <div style={{ alignSelf: 'stretch', marginTop: 28 }}>
        <Button size="lg" block leadingIcon={ctaIcon} onClick={onCta}>{cta}</Button>
      </div>
      {secondary && (
        <div style={{ marginTop: 12 }}>
          <TextButton tone="neutral" leadingIcon={secondaryIcon} size={secondaryIcon ? 'small' : 'button'} onClick={onSecondary} className={secondaryUnderline ? 'eq-alert-sheet__link' : undefined}>{secondary}</TextButton>
        </div>
      )}
    </div>
  );
}

/** Snooze error sheets and the soft update, keyed as on the flow pages. */
export type AlertSheetKind = 'airplane' | 'snooze-failed' | 'offline' | 'activation-failed' | 'update-available';

export const alertSheets: Record<AlertSheetKind, Omit<AlertSheetProps, 'onCta' | 'onSecondary' | 'onClose'>> = {
  airplane: { art: 'illustration-airplane', artSize: 116, title: 'Airplane mode is on', body: 'We couldn’t turn your assistant off because your phone is in airplane mode. Please turn it off and try again.', cta: 'Go to settings', secondary: 'Try Again' },
  'snooze-failed': { art: 'illustration-warning', artSize: 88, title: 'Assistant snooze failed', body: 'We were unable to turn off your assistant. Please try again.', cta: 'Try again' },
  offline: { art: 'illustration-no-internet', artSize: 96, title: 'No internet connection', body: 'Wi-Fi or mobile data required to turn off your assistant. Check your connection and try again.', cta: 'Go to settings', secondary: 'Try Again' },
  'activation-failed': { art: 'illustration-error-sad', artSize: 88, title: 'Assistant Activation Failed', body: 'We couldn’t turn your assistant on right now. We can help, try these.', cta: 'Try now', secondary: 'Contact Support', secondaryIcon: 'phone-in-talk' },
  'update-available': { art: <UpdateArt size={144} />, artSize: 144, title: 'A new version is here', body: 'This update brings new improvements to how Equal AI handles your calls.', cta: 'Update Now', ctaIcon: 'download', secondary: 'Maybe Later', secondaryUnderline: true },
};

export const activationFixes: TroubleshootItem[] = [
  { icon: 'flight', label: 'Toggle Airplane Mode' },
  { icon: 'cell-tower', label: 'Reset Network Connection' },
  { icon: 'power-settings', label: 'Restart your Phone' },
];

/**
 * Hard update (installed version below the minimum): full screen, no close; Back exits the app.
 * Art + title + body are centred above the pinned CTA.
 */
export function ForceUpdateScreen({ onUpdate, time = '12:00' }: { onUpdate?: () => void; time?: string }) {
  return (
    <div className="eq-force-update" role="dialog" aria-label="Update Equal AI App">
      <StatusBar time={time} />
      <div className="eq-force-update__body">
        <UpdateArt size={160} />
        <Text as="h1" variant="headline" align="center" style={{ fontWeight: 700, marginTop: 16 }}>Update Equal AI App</Text>
        <Text as="p" variant="body1" color="secondary" align="center" style={{ margin: '8px 0 0', maxWidth: 300 }}>This version is no longer supported. Update now to keep using Equal AI.</Text>
      </div>
      <div style={{ padding: '0 24px 24px' }}>
        <Button size="lg" block leadingIcon="download" onClick={onUpdate}>Update Equal AI</Button>
      </div>
    </div>
  );
}
