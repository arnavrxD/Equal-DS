import { Locked, UnlockAudioPill } from './Monetization';
import { useEffect, useMemo, useRef, useState, type ReactNode } from 'react';
import { Emoji, Icon } from '../foundations/Icon';
import { Text } from '../foundations/Text';
import { TextButton } from './Button';
import './VoiceNote.css';

const SPEEDS = [1, 1.2, 1.5, 2] as const;
const fmt = (s: number) => `${String(Math.floor(s / 60)).padStart(2, '0')}:${String(Math.floor(s % 60)).padStart(2, '0')}`;

/** Deterministic pseudo-waveform so the same recording always draws the same bars. */
function useBars(count: number, seed: number) {
  return useMemo(() => {
    let x = seed || 7;
    return Array.from({ length: count }, (_, i) => {
      x = (x * 9301 + 49297) % 233280;
      const r = x / 233280;
      const envelope = 0.55 + 0.45 * Math.sin((i / count) * Math.PI * 3);
      return r < 0.18 ? 4 : Math.max(5, Math.round((6 + r * 14) * envelope + 2));
    });
  }, [count, seed]);
}

export type VoicePlayerProps = {
  /** seconds */
  duration: number;
  /** Controlled progress 0–duration. Omit to let the component simulate playback. */
  position?: number;
  seed?: number;
  bars?: number;
  /** sent: inside your own outgoing bubble (green player, `voice/player-bg-sent`). */
  tone?: 'neutral' | 'sent';
};

/** Recording player — coded waveform, tabular times, speed pill that cycles 1x → 2x. */
export function VoicePlayer({ duration, position, seed = 42, bars = 28, tone = 'neutral' }: VoicePlayerProps) {
  const [playing, setPlaying] = useState(false);
  const [speed, setSpeed] = useState<(typeof SPEEDS)[number]>(1);
  const [pos, setPos] = useState(0);
  const raf = useRef<number>(0);
  const heights = useBars(bars, seed);
  const current = position ?? pos;

  useEffect(() => {
    if (!playing || position !== undefined) return;
    let last = performance.now();
    const tick = (now: number) => {
      setPos((p) => {
        const next = p + ((now - last) / 1000) * speed;
        last = now;
        if (next >= duration) {
          setPlaying(false);
          return 0;
        }
        return next;
      });
      raf.current = requestAnimationFrame(tick);
    };
    raf.current = requestAnimationFrame(tick);
    return () => cancelAnimationFrame(raf.current);
  }, [playing, speed, duration, position]);

  const played = Math.round((current / duration) * bars);
  return (
    <div className={`eq-voice ${tone === 'sent' ? 'is-sent' : ''}`}>
      <div className="eq-voice__row">
        <button type="button" className="eq-reset-btn eq-touch" aria-label={playing ? 'Pause' : 'Play'} onClick={() => setPlaying((p) => !p)} style={{ color: 'var(--eq-text-secondary)' }}>
          <Icon name={playing ? 'pause' : 'play'} size={22} />
        </button>
        <div className="eq-voice__wave" aria-hidden>
          {heights.map((h, i) => (
            <span key={i} style={{ height: h, background: i < played ? 'var(--eq-waveform-bar-played)' : 'var(--eq-waveform-bar)' }} />
          ))}
        </div>
        <button
          type="button"
          className="eq-reset-btn eq-touch eq-voice__speed eq-t-caption eq-tnum"
          aria-label={`Playback speed ${speed}x`}
          onClick={() => setSpeed(SPEEDS[(SPEEDS.indexOf(speed) + 1) % SPEEDS.length])}
        >
          {speed}x
        </button>
      </div>
      <div className="eq-voice__times">
        <Text variant="micro" color="tertiary">{fmt(current)}</Text>
        <Text variant="micro" color="tertiary">{fmt(duration)}</Text>
      </div>
    </div>
  );
}

export type CallSummaryProps = {
  duration?: number;
  summary: ReactNode;
  time?: string;
  label?: string;
  onShare?: () => void;
  /** Hide the recording (summary only). */
  noRecording?: boolean;
  width?: number | string;
  /** Pill left of the timestamp, e.g. { emoji: '🪄', label: 'Instruction Applied' }. */
  tag?: { emoji?: string; label: string };
  /**
   * Monetization. 'audio': the recording is blurred under a 🔒 Unlock Audio pill. 'all': the
   * recording and the summary text are both locked.
   */
  locked?: 'audio' | 'all';
  onUnlock?: () => void;
  /** Replaces the recording player, e.g. the gold crown-locked `<LockedRecording>` on the free plan. */
  recording?: ReactNode;
  /** Rows under the summary, e.g. `<SummaryActions>` (Call Back · Add to Calendar). */
  footer?: ReactNode;
};

/** Call summary card used inside the thread (Component ref #11, right column + Equal AI variant). */
export function CallSummary({ duration = 157, summary, time, label = 'Call Summary', onShare, noRecording, width = 268, tag, locked, onUnlock, recording, footer }: CallSummaryProps) {
  return (
    <div className="eq-summary" style={{ width }}>
      {!noRecording && (
        <div style={{ padding: 8, position: 'relative' }}>
          {recording ? recording : locked ? (
            <>
              <div style={{ filter: 'blur(3px)', pointerEvents: 'none' }} aria-hidden><VoicePlayer duration={duration} /></div>
              <div style={{ position: 'absolute', inset: 0, display: 'flex', alignItems: 'center', justifyContent: 'center' }}><UnlockAudioPill onClick={onUnlock} /></div>
            </>
          ) : (
            <VoicePlayer duration={duration} />
          )}
        </div>
      )}
      <div style={{ padding: noRecording ? '10px 10px 6px' : '0 10px 6px' }}>
        <Text as="div" variant="caption" color="tertiary">{label}</Text>
        {locked === 'all' ? <Locked><Text as="div" variant="body1">{summary}</Text></Locked> : <Text as="div" variant="body1">{summary}</Text>}
        {(time || tag) && (
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: 8, marginTop: 6 }}>
            {tag ? (
              <span className="eq-summary__tag">
                {tag.emoji && <Emoji char={tag.emoji} size={12} />}
                <span className="eq-t-caption">{tag.label}</span>
              </span>
            ) : <span />}
            {time && <Text variant="micro" color="tertiary">{time}</Text>}
          </div>
        )}
      </div>
      {footer}
      {onShare && (
        <>
          <hr className="eq-divider" />
          <div style={{ height: 44, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <TextButton leadingIcon="share" onClick={onShare}>Share</TextButton>
          </div>
        </>
      )}
    </div>
  );
}
