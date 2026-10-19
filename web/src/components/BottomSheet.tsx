import type { ReactNode } from 'react';
import { inertWhen, useModal } from '../foundations/a11y';
import { Asset, type AssetName } from '../foundations/Asset';
import { Text } from '../foundations/Text';
import type { IconName } from '../foundations/icons';
import { Button, IconButton } from './Button';
import './BottomSheet.css';

export type BottomSheetProps = {
  open: boolean;
  onClose?: () => void;
  children: ReactNode;
  /** Show the floating close button. */
  closable?: boolean;
  /**
   * 'overlay' (default) covers the nearest positioned ancestor (the PhoneFrame in flows).
   * 'inline' renders the sheet statically, for docs.
   */
  mode?: 'overlay' | 'inline';
  label?: string;
  /** Title row: close (X) on the left, centred title — replaces the floating close. */
  title?: string;
};

/** Bottom sheet (Component ref #17): scrim, 32dp top radius, floating close. */
export function BottomSheet({ open, onClose, children, closable = true, mode = 'overlay', label = 'Sheet', title }: BottomSheetProps) {
  const modal = useModal({ open, onClose, enabled: mode === 'overlay' });

  const sheet = (
    <div className={`eq-sheet ${mode === 'inline' ? 'is-inline' : ''} ${title ? 'has-title' : ''}`} role="dialog" aria-label={title ?? label} {...modal}>
      {title && (
        <div className="eq-sheet__title">
          <IconButton icon="close" label="Close" onClick={onClose} />
          <Text as="h2" variant="subheading" align="center">{title}</Text>
          <span />
        </div>
      )}
      {closable && !title && (
        <IconButton icon="close" label="Close" variant="neutral" className="eq-sheet__close" onClick={onClose} />
      )}
      {children}
    </div>
  );
  if (mode === 'inline') return sheet;
  return (
    <div className={`eq-sheet-layer ${open ? 'is-open' : ''}`} aria-hidden={!open} {...inertWhen(!open)}>
      <div className="eq-sheet-scrim" onClick={onClose} />
      {sheet}
    </div>
  );
}

export type ConfirmSheetProps = {
  illustration?: AssetName;
  title: ReactNode;
  subject?: ReactNode;
  body?: ReactNode;
  confirmLabel: string;
  confirmIcon?: IconName;
  destructive?: boolean;
  onConfirm?: () => void;
};

/** Centred confirmation content — e.g. "Delete chat with Amit Nanda". Put inside <BottomSheet>. */
export function ConfirmSheetContent({ illustration = 'illustration-trash', title, subject, body, confirmLabel, confirmIcon, destructive, onConfirm }: ConfirmSheetProps) {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', textAlign: 'center', paddingTop: 4 }}>
      <Asset name={illustration} size={72} />
      <Text as="h2" variant="title" style={{ marginTop: 20 }}>{title}</Text>
      {subject && <Text as="div" variant="body1" color="secondary" className="eq-tnum">{subject}</Text>}
      {body && <Text as="p" variant="body1" color="secondary" style={{ marginTop: 12 }}>{body}</Text>}
      <div style={{ marginTop: 28, width: '100%' }}>
        <Button block size="lg" variant={destructive ? 'danger' : 'primary'} leadingIcon={confirmIcon ?? (destructive ? 'delete' : undefined)} onClick={onConfirm}>
          {confirmLabel}
        </Button>
      </div>
    </div>
  );
}
