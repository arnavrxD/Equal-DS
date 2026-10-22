// Clickable prototype that wires the screen templates together — the pattern to copy when building a new flow.
import { useState } from 'react';
import { BottomSheet, ConfirmSheetContent, Snackbar } from '../index';
import { sampleChats, type SampleChat } from './data';
import { ChatScreen, ContactInfoScreen, HomeScreen, SearchScreen, YouScreen } from './Screens';
import { CallHandlingScreen } from './CallHandlingScreens';
import { HealthCheckFlow } from './HealthCheckScreens';
import { AccountDataFlow } from './SettingsScreens';
import { AddressesScreen, InstructionsFlow, PersonalDetailsScreen, SilentModeScreen, WhatsAppUpdatesScreen } from './FlowScreens';
import type { Gender } from '../components/Flows';

type Route =
  | { name: 'home' }
  | { name: 'you' }
  | { name: 'chat'; chat: SampleChat }
  | { name: 'contact'; chat: SampleChat }
  | { name: 'addresses' }
  | { name: 'instructions' }
  | { name: 'personal' }
  | { name: 'silent' }
  | { name: 'call-handling' }
  | { name: 'whatsapp' }
  | { name: 'search' }
  | { name: 'health-check' }
  | { name: 'account-data' };

export function EqualPrototype() {
  const [stack, setStack] = useState<Route[]>([{ name: 'home' }]);
  const [chats, setChats] = useState(sampleChats);
  const [pendingDelete, setPendingDelete] = useState<string[] | null>(null);
  const [toast, setToast] = useState<string | null>(null);
  const [profile, setProfile] = useState<{ name: string; gender: Gender }>({ name: 'Keshav Reddy', gender: 'male' });
  const route = stack[stack.length - 1];
  const push = (r: Route) => setStack((s) => [...s, r]);
  const back = () => setStack((s) => (s.length > 1 ? s.slice(0, -1) : s));
  const tab = (t: 'chats' | 'you') => setStack([{ name: t === 'chats' ? 'home' : 'you' }]);
  const flash = (m: string) => {
    setToast(m);
    setTimeout(() => setToast(null), 2200);
  };

  const target = pendingDelete && chats.find((c) => c.id === pendingDelete[0]);

  return (
    <>
      {route.name === 'home' && <HomeScreen key={chats.length} chats={chats} onOpenChat={(c) => push({ name: 'chat', chat: c })} onTab={tab} onDeleteSelected={setPendingDelete} onSearch={() => push({ name: 'search' })} avatar={profile.gender} />}
      {route.name === 'you' && <YouScreen onTab={tab} onOpen={(p) => push({ name: p })} avatar={profile.gender} name={profile.name.split(' ')[0]} />}
      {route.name === 'chat' && <ChatScreen chat={route.chat} live={route.chat.id === 'c1'} onBack={back} onInfo={() => push({ name: 'contact', chat: route.chat })} />}
      {route.name === 'contact' && <ContactInfoScreen chat={route.chat} onBack={back} />}
      {route.name === 'addresses' && <AddressesScreen onBack={back} />}
      {route.name === 'instructions' && <InstructionsFlow onBack={back} />}
      {route.name === 'personal' && <PersonalDetailsScreen name={profile.name} gender={profile.gender} onBack={back} onChange={setProfile} />}
      {route.name === 'silent' && <SilentModeScreen onBack={back} />}
      {route.name === 'call-handling' && <CallHandlingScreen onBack={back} onDone={back} />}
      {route.name === 'whatsapp' && <WhatsAppUpdatesScreen onBack={back} />}
      {route.name === 'health-check' && <HealthCheckFlow onBack={back} onHome={() => tab('chats')} />}
      {route.name === 'account-data' && <AccountDataFlow onBack={back} />}
      {route.name === 'search' && <SearchScreen onBack={back} onOpenChat={(c) => push({ name: 'chat', chat: c })} />}

      <BottomSheet open={!!pendingDelete} onClose={() => setPendingDelete(null)}>
        <ConfirmSheetContent
          title={
            pendingDelete && pendingDelete.length > 1 ? (
              `Delete ${pendingDelete.length} chats`
            ) : (
              <>
                Delete chat with
                <br />
                {target?.name}
              </>
            )
          }
          subject={pendingDelete && pendingDelete.length === 1 ? `(${target?.phone})` : undefined}
          body="All past conversations will be deleted. This action cannot be undone."
          confirmLabel="Delete"
          destructive
          onConfirm={() => {
            const n = pendingDelete?.length ?? 0;
            setChats((cs) => cs.filter((c) => !pendingDelete?.includes(c.id)));
            setPendingDelete(null);
            flash(n > 1 ? `${n} chats deleted` : 'Chat deleted');
          }}
        />
      </BottomSheet>

      {toast && (
        <div style={{ position: 'absolute', left: 16, right: 16, bottom: 110, zIndex: 70 }}>
          <Snackbar message={toast} actionLabel="Undo" onAction={() => (setChats(sampleChats), setToast(null))} />
        </div>
      )}
    </>
  );
}
