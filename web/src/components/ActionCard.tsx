import { Fragment, type ReactNode } from 'react';
import { Asset, type AssetName, type Category } from '../foundations/Asset';
import { Emoji, Icon } from '../foundations/Icon';
import type { IconName } from '../foundations/icons';
import { CategoryAvatar } from './Avatar';
import { Text } from '../foundations/Text';
import { TextButton } from './Button';

/** Every action-card CTA carries an emoji (✏️ Edit, 📤 Send, 👍 Confirm, 🗑️ Delete). */
export type ActionCardAction = { label: string; emoji: string; onClick?: () => void; danger?: boolean };

export type ActionCardLine = { text: ReactNode; tone?: 'primary' | 'secondary' };

export type ActionCardBodyProps = {
  /**
   * What sits in the 40dp circle (precedence: category → icon → source):
   * - `category`: the caller's 3D category avatar, for a person or business (e.g. Dr. Mehta's Clinic)
   * - `icon`: a Material Symbol in `text/action` on a white circle (instructions use `instructions` = note_stack)
   * - `source`: `'whatsapp'` (coded logo) or any image asset
   */
  category?: Category;
  icon?: IconName;
  source?: 'whatsapp' | AssetName;
  title: ReactNode;
  /** Quoted message (body1, text/secondary). */
  quote?: ReactNode;
  /** Detail lines (body1, default text/secondary). Instructions use When (primary) / Do (secondary). */
  details?: ActionCardLine[];
  time?: string;
};

/**
 * The quote block shared by every action card and by the instruction preview in the setup chat:
 * `bg/primary`, radius 12, padding 8; 40dp circle → 12 → title `body2`, lines `body1`, time `micro`.
 */
export function ActionCardBody({ category, icon, source = 'whatsapp', title, quote, details = [], time }: ActionCardBodyProps) {
  return (
    <div style={{ background: 'var(--eq-bg-primary)', borderRadius: 12, padding: 8, display: 'flex', gap: 12 }}>
      {category ? (
        <CategoryAvatar category={category} size={40} />
      ) : (
        <span style={{ width: 40, height: 40, borderRadius: '50%', background: 'var(--eq-bg-surface)', display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
          {icon ? (
            <Icon name={icon} size={22} color="var(--eq-text-action)" />
          ) : source === 'whatsapp' ? (
            <Icon name="whatsapp" size={22} color="var(--eq-brand-whatsapp)" label="WhatsApp" />
          ) : (
            <Asset name={source} size={22} />
          )}
        </span>
      )}
      <div style={{ flex: 1, minWidth: 0 }}>
        <Text as="div" variant="body2">{title}</Text>
        {quote && <Text as="div" variant="body1" color="secondary">{quote}</Text>}
        {details.map((d, i) => (
          <Text key={i} as="div" variant="body1" color={d.tone ?? 'secondary'}>{d.text}</Text>
        ))}
        {time && <Text as="div" variant="micro" color="secondary" align="right" style={{ marginTop: 2 }}>{time}</Text>}
      </div>
    </div>
  );
}

/** When / Do lines for an instruction, in the shared body format. */
export const instructionLines = (when: ReactNode, action: ReactNode): ActionCardLine[] => [
  { text: <>When: {when}</>, tone: 'primary' },
  { text: <>Do: {action}</>, tone: 'secondary' },
];

export type ActionCardProps = ActionCardBodyProps & {
  actions?: ActionCardAction[];
  width?: number | string;
};

/**
 * Action card: something the assistant did or proposes (Component ref #14). A white card: the grey
 * body sits in an 8dp frame (never on a green card), and each action is a full-width green text row
 * (44dp) under a full-bleed divider, always led by its emoji. All actions have the same weight, the
 * first one ("Confirm", "Send") included: never a filled button.
 */
export function ActionCard({ actions = [], width = 268, ...body }: ActionCardProps) {
  return (
    <div style={{ width, maxWidth: '100%', background: 'var(--eq-bg-surface)', borderRadius: 16, overflow: 'hidden' }}>
      <div style={{ padding: 8 }}>
        <ActionCardBody {...body} />
      </div>
      {actions.map((a) => (
        <Fragment key={a.label}>
          <hr className="eq-divider" />
          <TextButton tone={a.danger ? 'danger' : 'action'} onClick={a.onClick} style={{ display: 'flex', width: '100%', minHeight: 44, justifyContent: 'center', alignItems: 'center', gap: 6 }}>
            <span style={{ display: 'inline-flex', alignItems: 'center', gap: 6 }}><Emoji char={a.emoji} size={14} />{a.label}</span>
          </TextButton>
        </Fragment>
      ))}
    </div>
  );
}
