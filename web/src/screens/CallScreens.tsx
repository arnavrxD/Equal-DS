// In-app call flows: vOIP "Call me" with the assistant (and, later, Equal to Equal calls).
// Composed only from DS components. Sources: the vOIP and Equal To Equal Call Experience designs.
import { useEffect, useState } from 'react';
import { CallScreen, PermissionDialog, SystemNotification, SystemPermissionStandIn, useCallTimer, type CallPhase } from '../index';
import { HomeScreen } from './Screens';

export type VoipStage = 'home' | 'mic-prompt' | 'mic-system' | 'connecting' | 'connected' | 'ended' | 'pstn';

/**
 * "Call me" with the assistant over VoIP (vOIP design). Home → mic pre-permission → Android dialog →
 * Connecting → Connected (speaker / mute toggle independently) → Call ended → back Home.
 * "Not now" or "Deny" falls back to a normal phone call from the assistant (PSTN).
 */
export function VoipFlow({ stage: initial = 'home', speaker: sp = false, muted: mu = false, seconds: start = 0, micGranted = false }: { stage?: VoipStage; speaker?: boolean; muted?: boolean; seconds?: number; micGranted?: boolean }) {
  const [stage, setStage] = useState<VoipStage>(initial);
  const [speaker, setSpeaker] = useState(sp);
  const [muted, setMuted] = useState(mu);
  useEffect(() => { setStage(initial); setSpeaker(sp); setMuted(mu); }, [initial, sp, mu]);
  const seconds = useCallTimer(stage === 'connected', start);
  useEffect(() => {
    if (stage === 'connecting' && initial !== 'connecting') { const id = window.setTimeout(() => setStage('connected'), 1500); return () => window.clearTimeout(id); }
    if (stage === 'ended' && initial !== 'ended') { const id = window.setTimeout(() => setStage('home'), 2000); return () => window.clearTimeout(id); }
  }, [stage, initial]);

  if (stage === 'home' || stage === 'pstn') {
    return (
      <div style={{ position: 'relative', flex: 1, display: 'flex', flexDirection: 'column' }}>
        <HomeScreen promo="meet-assistant-carousel" onCallMe={() => setStage(micGranted ? 'connecting' : 'mic-prompt')} />
        {stage === 'pstn' && (
          <div style={{ position: 'absolute', left: 8, right: 8, top: 8, zIndex: 50 }} className="eq-heads-up">
            <SystemNotification app="Phone" title="Incoming call · Your Assistant" body="Equal AI Assistant is calling you on your phone." />
          </div>
        )}
      </div>
    );
  }
  const phase: CallPhase = stage === 'connected' ? 'connected' : stage === 'ended' ? 'ended' : 'connecting';
  const overlay = stage === 'mic-prompt' ? (
    <PermissionDialog art="permission-microphone" title="Talk to your assistant" body="Turn on your mic to talk, just like a normal call." cta="Allow microphone" onAction={() => setStage('mic-system')} secondary="Not now" onSecondary={() => setStage('pstn')} closable={false} />
  ) : stage === 'mic-system' ? (
    <SystemPermissionStandIn onAllow={() => setStage('connecting')} onDeny={() => setStage('pstn')} />
  ) : undefined;
  return (
    <CallScreen
      phase={phase}
      seconds={stage === 'ended' ? Math.max(seconds, start) : seconds}
      speaker={speaker}
      muted={muted}
      onSpeaker={() => setSpeaker((v) => !v)}
      onMute={() => setMuted((v) => !v)}
      onEnd={() => setStage('ended')}
      onBack={() => setStage('home')}
      overlay={overlay}
    />
  );
}
