import type { ReactNode } from 'react';
import { Icon } from '../foundations/Icon';
import { Text } from '../foundations/Text';
import { AiAvatar, ContactAvatar, type ContactAvatarProps } from './Avatar';
import { IconButton } from './Button';
import { Checkbox } from './Checkbox';
import { AssistantToggle } from './AssistantToggle';
import { StatusBar } from './StatusBar';
import { TopBar } from './TopBar';
import './ChatHeader.css';

export type ChatHeaderProps = {
  name: string;
  /** Phone number (tabular) or status line ("You have a visitor"). */
  subtitle?: string;
  avatar?: ContactAvatarProps;
  onBack?: () => void;
  onCall?: () => void;
  onMore?: () => void;
  /** Live call — the Takeover pill replaces the call and more-options buttons. */
  onTakeover?: () => void;
  live?: boolean;
  statusBar?: boolean;
  /** Equal AI's own chat: the orb instead of a contact avatar. */
  official?: boolean;
  /** Equal-to-Equal: a call strip between the status bar and the row (e.g. the ongoing call). */
  callStrip?: ReactNode;
  /** A call is live: the call icon is disabled (`text/action-disabled`). */
  callDisabled?: boolean;
};

/** In-conversation header (Component ref #5). */
export function ChatHeader({ name, subtitle, avatar, onBack, onCall, onMore, onTakeover, live, statusBar = false, official, callStrip, callDisabled }: ChatHeaderProps) {
  return (
    <header className="eq-chat-header">
      {statusBar && <StatusBar background={callStrip ? 'var(--eq-call-strip-bg)' : 'var(--eq-bg-surface)'} />}
      {callStrip}
      <div className="eq-chat-header__row">
        <IconButton icon="arrow-back" label="Back" onClick={onBack} size={36} />
        {official ? <AiAvatar size={36} /> : <ContactAvatar size={36} {...avatar} />}
        <div style={{ flex: 1, minWidth: 0, marginLeft: 6 }}>
          <Text as="h1" variant="subheading" truncate>{name}</Text>
          {subtitle && (
            <Text as="div" variant="caption" color="secondary" truncate className={/^[+\d\s()-]+$/.test(subtitle) ? 'eq-tnum' : undefined} style={{ fontWeight: 400 }}>
              {subtitle}
            </Text>
          )}
        </div>
        {/* Live call: Takeover replaces both the call and the more-options buttons. */}
        {live ? (
          <TakeoverPill onClick={onTakeover} />
        ) : (
          <>
            <IconButton icon="call" label="Call" onClick={onCall} size={36} disabled={callDisabled} style={callDisabled ? { color: 'var(--eq-text-action-disabled)' } : undefined} />
            <IconButton icon="more-vert" label="More options" onClick={onMore} size={36} />
          </>
        )}
      </div>
    </header>
  );
}

/** Glass "Takeover" pill for a live call. */
export function TakeoverPill({ onClick, label = 'Takeover' }: { onClick?: () => void; label?: string }) {
  return (
    <button type="button" onClick={onClick} className="eq-reset-btn eq-pressable eq-touch eq-takeover">
      <Icon name="call-filled" size={18} />
      <Text variant="button" color="none">{label}</Text>
    </button>
  );
}

export type SelectionHeaderProps = {
  count: number;
  allSelected: boolean;
  onSelectAll?: (all: boolean) => void;
  onBack?: () => void;
  onMarkRead?: () => void;
  onDelete?: () => void;
  statusBar?: boolean;
};

/** Selection-mode app bar for the chat list (Component ref #5, last variant). */
export function SelectionHeader({ count, allSelected, onSelectAll, onBack, onMarkRead, onDelete, statusBar = false }: SelectionHeaderProps) {
  return (
    <header className="eq-chat-header eq-chat-header--flat">
      {statusBar && <StatusBar background="var(--eq-bg-surface)" />}
      <div className="eq-chat-header__row">
        <IconButton icon="arrow-back" label="Exit selection" onClick={onBack} />
        <Text variant="subheading" role="status" style={{ flex: 1, marginLeft: 8 }} className="eq-tnum">{`${count} Selected`}</Text>
        <button type="button" className="eq-reset-btn eq-pressable eq-touch eq-markread" onClick={onMarkRead}>
          <Icon name="done-all" size={16} color="var(--eq-text-action)" />
          <Text variant="buttonSmall" color="none">Mark read</Text>
        </button>
        <IconButton icon="delete" label="Delete" variant="danger-ghost" onClick={onDelete} />
      </div>
      <div style={{ padding: '0 16px 4px' }}>
        <Checkbox checked={allSelected} onChange={onSelectAll} label="Select All" />
      </div>
    </header>
  );
}

export type HomeHeaderProps = {
  assistantOn: boolean;
  onAssistantChange?: (on: boolean) => void;
  plan?: string | null;
  statusBar?: boolean;
  trailing?: ReactNode;
  /** Replaces the assistant toggle, e.g. a SystemStatusCard while Equal AI is down. */
  status?: ReactNode;
  /** The toggle's line (default "Equal is managing your unknown calls!"). */
  assistantDescription?: string;
  planTone?: 'paid' | 'expired';
  /** Under the toggle, e.g. the Payment processing card. */
  below?: ReactNode;
  /** Toggle art experiment (Component 25): the robot (default) or the phone with sparkles. */
  assistantArt?: 'robot' | 'phone';
  /** Off line under the toggle title, and the Healthcheck strip. */
  assistantOffDescription?: ReactNode;
  /** Assistant Snooze: spinner in place of the switch while (de)activating. */
  assistantBusy?: 'deactivating' | 'activating';
  healthAlert?: { onFix?: () => void };
  /** Equal-to-Equal: the incoming or ongoing call strip under the status bar. */
  callStrip?: ReactNode;
};

/** Home header: status bar + logo/plan + assistant toggle, on bg/primary with bottom shadow. */
export function HomeHeader({ assistantOn, onAssistantChange, plan = 'Unlimited', statusBar = true, trailing, status, assistantDescription, planTone, below, assistantArt, assistantOffDescription, healthAlert, assistantBusy, callStrip }: HomeHeaderProps) {
  return (
    <header className="eq-home-header">
      {statusBar && <StatusBar background={callStrip ? 'var(--eq-call-strip-bg)' : undefined} />}
      {callStrip}
      <TopBar plan={plan} trailing={trailing} planTone={planTone} />
      <div style={{ padding: '0 12px 12px' }}>
        {status ?? <AssistantToggle on={assistantOn} onChange={onAssistantChange} description={assistantDescription} art={assistantArt} offDescription={assistantOffDescription} healthAlert={healthAlert} busy={assistantBusy} />}
      </div>
      {below}
    </header>
  );
}
