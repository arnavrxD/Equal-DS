import { useState } from 'react';
import { BottomSheet, CallSummary, ChatBubble, ChatListItem, ConfirmSheetContent, DayDivider, Dialog, FeedbackCard, MessageList, PhoneFrame, SelectionHeader, ThreadEvent, ThreadNotice, Button, Snackbar, ChatWallpaper } from '@equal/ds';
import { Example, H2, Page } from '../ui/kit';

function SheetDemo() {
  const [open, setOpen] = useState(false);
  const [toast, setToast] = useState(false);
  return (
    <PhoneFrame height={600}>
      <div style={{ padding: 20, display: 'flex', flexDirection: 'column', gap: 12 }}>
        <Button variant="danger" leadingIcon="delete" onClick={() => setOpen(true)}>Delete chat</Button>
        {toast && <Snackbar message="Chat deleted" actionLabel="Undo" onAction={() => setToast(false)} />}
      </div>
      <BottomSheet open={open} onClose={() => setOpen(false)}>
        <ConfirmSheetContent title={<>Delete chat with<br />Amit Nanda</>} subject="(+91 9871746113)" body="All past conversations will be deleted. This action cannot be undone." confirmLabel="Delete" destructive onConfirm={() => (setOpen(false), setToast(true))} />
      </BottomSheet>
    </PhoneFrame>
  );
}

function SelectionDemo() {
  const [sel, setSel] = useState<string[]>(['a']);
  const rows = [
    { id: 'a', name: 'Arnav Raj', preview: 'You have a visitor', date: 'Yesterday', unread: 5 },
    { id: 'b', name: 'Dr Mehta Clinic', preview: 'Appointment confirmed for tomorrow, 10 AM', date: '03:21', unread: 0 },
  ];
  const toggle = (id: string) => setSel((s) => (s.includes(id) ? s.filter((x) => x !== id) : [...s, id]));
  return (
    <div style={{ width: 390, background: 'var(--eq-bg-primary)' }}>
      <SelectionHeader count={sel.length} allSelected={sel.length === rows.length} onSelectAll={(all) => setSel(all ? rows.map((r) => r.id) : [])} onBack={() => setSel([])} />
      <div style={{ display: 'flex', flexDirection: 'column', gap: 1, background: 'var(--eq-stroke-subtle)' }}>
        {rows.map((r) => (
          <ChatListItem key={r.id} name={r.name} preview={r.preview} date={r.date} unread={r.unread} selected={sel.includes(r.id)} actions={[{ label: 'Call Back', emoji: '📞' }]} onClick={() => toggle(r.id)} onLongPress={() => toggle(r.id)} />
        ))}
      </div>
    </div>
  );
}

export default function Patterns() {
  return (
    <Page title="Patterns" lead="How components combine: the rhythm of a call thread, selection in the chat list, and the confirm-then-undo pattern for destructive actions.">
      <H2>A call thread</H2>
      <p className="s-p">Day divider → call events → outgoing (Equal AI on your behalf) and incoming (the caller) bubbles → the notice that the call ended → the assistant’s summary with the recording → the rating card. Consecutive bubbles from one sender group at 4dp; a new sender opens at 12dp with a tail.</p>
      <Example title="Thread on the chat wallpaper" width={390} code={`<ChatWallpaper>\n  <MessageList>\n    <DayDivider label="Today" />\n    <ThreadEvent label="Call Started 03:31" />\n    <ChatBubble kind="outgoing" sender="Equal AI" tail>…</ChatBubble>\n    <ChatBubble kind="incoming" sender="Dr Mehta Clinic" tail>…</ChatBubble>\n    <ThreadNotice>Call has ended…</ThreadNotice>\n    <CallSummary summary="…" tag={{ emoji: '🪄', label: '2 instructions applied in this call' }} />\n  </MessageList>\n</ChatWallpaper>`}>
        <div style={{ width: 390, borderRadius: 16, overflow: 'hidden' }}>
          <ChatWallpaper>
            <MessageList>
              <DayDivider label="Today" />
              <ThreadEvent label="Call Started 03:31" />
              <ChatBubble kind="outgoing" sender="Equal AI" time="03:21" tail>Hi, who is this?</ChatBubble>
              <ChatBubble kind="incoming" sender="Dr Mehta Clinic" time="03:21" tail>This is Priya from Dr. Mehta’s clinic. Your appointment is confirmed for tomorrow.</ChatBubble>
              <ChatBubble kind="outgoing" sender="Equal AI" time="03:21" tail>Yes, I’ll be there at 10 AM</ChatBubble>
              <ThreadEvent label="Call Ended 03:31" />
              <ThreadNotice>Call has ended. You are now talking to your AI assistant.</ThreadNotice>
              <ChatBubble kind="assistant" sender="Equal AI" time="03:32" tail>Hey Keshav! Here is the summary of your call</ChatBubble>
              <div style={{ paddingLeft: 36 }}>
                <CallSummary summary="Priya from Dr. Mehta’s clinic confirmed your appointment for tomorrow at 10 AM." time="03:32" tag={{ emoji: '🪄', label: '2 instructions applied in this call' }} onShare={() => {}} width={264} />
              </div>
              <div style={{ paddingLeft: 36 }}><FeedbackCard /></div>
            </MessageList>
          </ChatWallpaper>
        </div>
      </Example>

      <H2>Selection in the chat list</H2>
      <p className="s-p">Long-press enters selection: the header becomes the selection bar, selected rows take the highlight fill and a check badge, and their chips go outlined. Try it by tapping rows.</p>
      <Example title="Selection mode" width={390} code={`<SelectionHeader count={1} allSelected={false} … />\n<ChatListItem selected … />`}>
        <SelectionDemo />
      </Example>

      <H2>Confirm, then undo</H2>
      <p className="s-p">Destructive actions ask once in a bottom sheet (illustration, title, consequence, a danger button) and then offer Undo in a snackbar. Never a dialog for this; dialogs are for decisions that change a setting.</p>
      <Example title="Delete chat" width={390} code={`<BottomSheet open={open} onClose={…}>\n  <ConfirmSheetContent destructive confirmLabel="Delete" … />\n</BottomSheet>\n<Snackbar message="Chat deleted" actionLabel="Undo" />`}>
        <SheetDemo />
      </Example>
      <Example title="A settings decision" width={390} code={`<Dialog mode="inline" title="Turn off Call Learning?" confirmLabel="Turn off" … />`}>
        <Dialog open mode="inline" title="Turn off Call Learning?" subject="Call learning" body="Your assistant will stop learning from new calls. What it already knows stays until you clear it." confirmLabel="Turn off" />
      </Example>
    </Page>
  );
}
