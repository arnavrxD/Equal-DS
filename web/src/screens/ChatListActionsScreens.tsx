// Chat-list bulk actions (Updated UI): long-press to select, the single-select header (mark read,
// mute, delete, more), the overflow menu, delete / mute / unmute sheets, and the toasts.
import { useEffect, useState } from 'react';
import { BottomNav, BottomSheet, ChatListItem, ConfirmSheetContent, ContextMenu, HomeHeader, Screen, SelectionHeader, Snackbar } from '../index';
import { sampleChats } from './data';

export type ChatListActionsStage = 'list' | 'one' | 'many' | 'menu' | 'delete' | 'deleting' | 'mute' | 'unmute' | 'muted' | 'favourite';

/**
 * Chats with selection. Long-press a row → one selected (mark read · mute · delete · more); tap more
 * rows → the multi-select header. Delete and mute confirm in a sheet; deleting and favourites show a
 * toast. A muted row shows the bell-off glyph.
 */
export function ChatListActions({ stage: initial = 'list' }: { stage?: ChatListActionsStage }) {
  const [stage, setStage] = useState<ChatListActionsStage>(initial);
  const [selected, setSelected] = useState<string[]>(initial === 'list' || initial === 'muted' ? [] : initial === 'many' ? ['c1', 'c2', 'c3'] : ['c3']);
  const [muted, setMuted] = useState<string[]>(initial === 'muted' || initial === 'unmute' ? ['c3'] : []);
  const [gone, setGone] = useState<string[]>([]);
  useEffect(() => {
    if (stage !== 'deleting' && stage !== 'favourite') return;
    if (initial === stage) return;
    const id = window.setTimeout(() => setStage('list'), 2000);
    return () => window.clearTimeout(id);
  }, [stage, initial]);
  const chats = sampleChats.filter((c) => !gone.includes(c.id));
  const target = chats.find((c) => c.id === selected[0]) ?? sampleChats[2];
  const clear = () => { setSelected([]); setStage('list'); };
  const toggle = (id: string) => {
    const next = selected.includes(id) ? selected.filter((x) => x !== id) : [...selected, id];
    setSelected(next);
    setStage(next.length === 0 ? 'list' : next.length === 1 ? 'one' : 'many');
  };
  const selecting = selected.length > 0 && stage !== 'deleting' && stage !== 'favourite' && stage !== 'list' && stage !== 'muted';
  const header = !selecting ? (
    <HomeHeader assistantOn onAssistantChange={() => {}} />
  ) : selected.length === 1 ? (
    <SelectionHeader
      statusBar
      single
      count={1}
      allSelected={false}
      muted={muted.includes(target.id)}
      onBack={clear}
      onMarkRead={clear}
      onMute={() => setStage(muted.includes(target.id) ? 'unmute' : 'mute')}
      onDelete={() => setStage('delete')}
      onMore={() => setStage(stage === 'menu' ? 'one' : 'menu')}
      menu={stage === 'menu' ? (
        <ContextMenu
          label="Chat options"
          minWidth={164}
          autoFocus
          onClose={() => setStage('one')}
          items={[
            { label: 'Mark as read', onSelect: clear },
            { label: 'Add to favourite', onSelect: () => { setSelected([]); setStage('favourite'); } },
            { label: 'Share contact', onSelect: clear },
          ]}
        />
      ) : undefined}
    />
  ) : (
    <SelectionHeader statusBar count={selected.length} allSelected={selected.length === chats.length} onSelectAll={(all) => setSelected(all ? chats.map((c) => c.id) : [])} onBack={clear} onMarkRead={clear} onDelete={() => setStage('delete')} />
  );
  const sheet = stage === 'delete' ? (
    <BottomSheet open label="Delete chat" onClose={() => setStage(selected.length > 1 ? 'many' : 'one')}>
      <ConfirmSheetContent title={selected.length > 1 ? `Delete ${selected.length} chats` : `Delete chat with ${target.name}`} body="This removes the conversation and its call summaries from this phone." confirmLabel="Delete" destructive onConfirm={() => { setGone((g) => [...g, ...selected]); setSelected([]); setStage('deleting'); }} />
    </BottomSheet>
  ) : stage === 'mute' || stage === 'unmute' ? (
    <BottomSheet open label={stage === 'mute' ? `Mute ${target.name}` : `Unmute ${target.name}`} onClose={() => setStage('one')}>
      <ConfirmSheetContent
        illustration="mascot-sleeping"
        title={`${stage === 'mute' ? 'Mute' : 'Unmute'} ${target.name}`}
        subject={`(${target.phone})`}
        body={stage === 'mute' ? 'Their calls won’t ring. Your assistant will still answer and keep you posted.' : 'Are you sure? You’ll start receiving calls from them again'}
        confirmLabel={stage === 'mute' ? 'Mute' : 'Unmute'}
        onConfirm={() => { setMuted((m) => (stage === 'mute' ? [...m, target.id] : m.filter((x) => x !== target.id))); clear(); }}
      />
    </BottomSheet>
  ) : undefined;
  const toast = stage === 'deleting' ? <Snackbar busy message="Deleting Conversation" /> : stage === 'favourite' ? <Snackbar message="Add to favourites" onClose={() => setStage('list')} /> : undefined;
  return (
    <Screen
      header={header}
      floating={selecting ? undefined : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: 12, alignItems: 'stretch' }}>
          {toast && <div style={{ padding: '0 16px' }}>{toast}</div>}
          <BottomNav active="chats" unread={2} />
        </div>
      )}
      overlay={sheet}
    >
      <div style={{ display: 'flex', flexDirection: 'column', gap: 1, marginTop: 8, background: 'var(--eq-stroke-subtle)' }}>
        {chats.map((c) => (
          <ChatListItem
            key={c.id}
            name={c.name}
            preview={c.preview}
            date={c.date}
            unread={c.unread}
            actions={c.actions}
            muted={muted.includes(c.id)}
            avatar={c.initials ? { initials: c.initials } : { category: c.category }}
            selected={selecting ? selected.includes(c.id) : undefined}
            onClick={() => (selecting ? toggle(c.id) : undefined)}
            onLongPress={() => toggle(c.id)}
          />
        ))}
      </div>
      <div style={{ height: 120 }} />
    </Screen>
  );
}
