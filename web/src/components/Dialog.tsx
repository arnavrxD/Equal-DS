import { useId, type ReactNode } from 'react';
import { useModal } from '../foundations/a11y';
import { Asset, type AssetName } from '../foundations/Asset';
import { Icon } from '../foundations/Icon';
import { Text } from '../foundations/Text';
import { Button, TextButton } from './Button';

export type DialogProps = {
  open: boolean;
  title: string;
  subject?: string;
  body?: ReactNode;
  confirmLabel: string;
  cancelLabel?: string;
  destructive?: boolean;
  onConfirm?: () => void;
  onCancel?: () => void;
  /** Content between the body and the actions, e.g. the Snooze RadioGroup. */
  children?: ReactNode;
  /** Commit button disabled (Snooze: until an option is selected). */
  confirmDisabled?: boolean;
  /** overlay covers nearest positioned ancestor; inline renders statically for docs. */
  mode?: 'overlay' | 'inline';
};

/** Decision dialog — title token, body1, text revise + filled commit (rule 4). */
export function Dialog({ open, title, subject, body, confirmLabel, cancelLabel = 'Cancel', destructive, onConfirm, onCancel, mode = 'overlay', children, confirmDisabled }: DialogProps) {
  const modal = useModal({ open, onClose: onCancel, enabled: mode === 'overlay' });
  const bodyId = useId();
  const box = (
    <div role="alertdialog" aria-label={title} aria-describedby={body ? bodyId : undefined} {...modal} style={{ width: 312, maxWidth: 'calc(100% - 48px)', background: 'var(--eq-bg-surface)', borderRadius: 12, padding: 24, boxShadow: 'var(--eq-shadow-nav)' }}>
      <Text as="h2" variant="title">{title}</Text>
      {subject && <Text as="div" variant="caption" color="secondary" style={{ marginTop: 2 }}>{subject}</Text>}
      {body && <Text as="p" id={bodyId} variant="body1" color="secondary" style={{ marginTop: children ? 8 : 12 }}>{body}</Text>}
      {children && <div style={{ marginTop: 20 }}>{children}</div>}
      <div style={{ display: 'flex', justifyContent: 'flex-end', alignItems: 'center', gap: 20, marginTop: 24 }}>
        <TextButton tone="neutral" onClick={onCancel}>{cancelLabel}</TextButton>
        <Button variant={destructive ? 'danger' : 'primary'} disabled={confirmDisabled} onClick={onConfirm}>{confirmLabel}</Button>
      </div>
    </div>
  );
  if (mode === 'inline') return box;
  if (!open) return null;
  return (
    <div style={{ position: 'absolute', inset: 0, zIndex: 60, display: 'flex', alignItems: 'center', justifyContent: 'center', background: 'var(--eq-bg-overlay)' }} onClick={onCancel}>
      <div onClick={(e) => e.stopPropagation()} style={{ display: 'contents' }}>{box}</div>
    </div>
  );
}

/** Snackbar / toast — inverse surface. */
export function Snackbar({ message, actionLabel, onAction, busy, onClose }: { message: string; actionLabel?: string; onAction?: () => void; /** A spinner before the message ("Deleting Conversation"). */ busy?: boolean; /** A trailing × ("Add to favourites"). */ onClose?: () => void }) {
  return (
    <div role="status" style={{ display: 'flex', alignItems: 'center', gap: 12, padding: '12px 16px', borderRadius: 12, background: 'var(--eq-text-primary)', boxShadow: 'var(--eq-shadow-nav)' }}>
      {busy && <Icon name="progress-activity" size={20} className="eq-spin" color="var(--eq-bg-surface)" />}
      <Text variant="body1" color="none" style={{ flex: 1, color: 'var(--eq-bg-surface)' }}>{message}</Text>
      {actionLabel && (
        <button type="button" className="eq-reset-btn eq-touch eq-t-button" style={{ color: 'var(--eq-button-secondary)' }} onClick={onAction}>
          <Text variant="inherit" color="none">{actionLabel}</Text>
        </button>
      )}
      {onClose && (
        <button type="button" className="eq-reset-btn eq-touch" aria-label="Dismiss" onClick={onClose} style={{ display: 'inline-flex', color: 'var(--eq-bg-surface)' }}>
          <Icon name="close" size={20} />
        </button>
      )}
    </div>
  );
}

/** Empty / error state — illustration slot, subheading title, captionWrap body, optional action. */
export function EmptyState({ title, body, illustration, actionLabel, onAction, announce }: { title: string; body?: string; illustration?: AssetName; actionLabel?: string; onAction?: () => void; /** It replaces a list after a search or filter: announce it (role="status"). */ announce?: boolean }) {
  return (
    <div role={announce ? 'status' : undefined} style={{ width: '100%', boxSizing: 'border-box', display: 'flex', flexDirection: 'column', alignItems: 'center', textAlign: 'center', padding: '32px 32px' }}>
      {illustration && <Asset name={illustration} size={96} />}
      <Text as="h2" variant="subheading" style={{ marginTop: illustration ? 16 : 0 }}>{title}</Text>
      {body && <Text as="p" variant="captionWrap" color="secondary" style={{ marginTop: 4, maxWidth: 260 }}>{body}</Text>}
      {actionLabel && (
        <div style={{ marginTop: 20 }}>
          <Button onClick={onAction}>{actionLabel}</Button>
        </div>
      )}
    </div>
  );
}
