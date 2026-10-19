import { useState, type FormEvent } from 'react';
import { Icon } from '../foundations/Icon';
import './Composer.css';

export type ComposerProps = {
  value?: string;
  onChange?: (v: string) => void;
  onSend?: (v: string) => void;
  placeholder?: string;
  /** Force the focused look (for docs). */
  forceFocus?: boolean;
  autoFocus?: boolean;
};

/** Message composer / voice input bar (Component ref #15). Send is disabled until there is text. */
export function Composer({ value, onChange, onSend, placeholder = 'Type a message...', forceFocus, autoFocus }: ComposerProps) {
  const [internal, setInternal] = useState('');
  const text = value ?? internal;
  const set = (v: string) => (onChange ? onChange(v) : setInternal(v));
  const canSend = text.trim().length > 0;
  const submit = (e: FormEvent) => {
    e.preventDefault();
    if (!canSend) return;
    onSend?.(text.trim());
    set('');
  };
  return (
    <form className={`eq-composer ${forceFocus ? 'is-focus' : ''} ${canSend ? 'can-send' : ''}`} onSubmit={submit}>
      <input className="eq-composer__input eq-t-body1" value={text} placeholder={placeholder} onChange={(e) => set(e.target.value)} aria-label="Message" autoFocus={autoFocus} />
      <button type="submit" className="eq-reset-btn eq-pressable eq-touch eq-composer__send" disabled={!canSend} aria-label="Send">
        <Icon name="send" size={20} color={canSend || forceFocus ? 'var(--eq-text-on-action)' : 'var(--eq-text-on-dark)'} rotate={-35} />
      </button>
    </form>
  );
}
