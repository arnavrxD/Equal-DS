import type { CSSProperties } from 'react';

/**
 * Image assets are the ONLY non-coded visuals in the system.
 * Drop a file named `<asset>.png | .webp | .svg | .jpg` into `equal-ds/assets/` and it is picked up automatically
 * (Vite glob, no manifest to edit). Until then a coded checkerboard placeholder renders at the exact size.
 */
export const assetNames = [
  'mascot-robot',
  'avatar-male',
  'avatar-female',
  'illustration-high-five',
  'illustration-trash',
  'whats-new-illustration',
  'logo-orb-light',
  'logo-orb-dark',
  'illustration-silent-mode',
  'illustration-whatsapp',
  'illustration-addresses-empty',
  'illustration-instructions-empty',
  'illustration-suggestions',
  'mascot-instructions',
  'preset-screen-protect',
  'preset-everyday-calls',
  'preset-business',
  'permission-microphone',
  'avatar-assistant-bust',
  'avatar-male-bust',
  'avatar-female-bust',
  'mascot-sleeping',
  'avatar-assistant',
  'crown-gold',
  'crown-lock',
  'feature-call-screening',
  'value-card-pattern',
  'illustration-search',
  'illustration-no-internet',
  'illustration-no-results',
  'system-keyboard',
  'assistant-phone',
  'call-handling-all',
  'call-handling-exclude',
  'call-handling-done',
  'call-setup',
  'permission-contacts',
  'permission-call-history',
  'illustration-no-contacts',
  'premium-glow',
  'feature-recordings',
  'feature-summaries',
  'coupon-seal',
  'status-waiting',
  'status-waiting-cash',
  'status-confirmed',
  'status-pending',
  'status-failed',
  'emoji-sad',
  'illustration-airplane',
  'illustration-shield-lock',
  'stat-star',
  'stat-phone',
  'illustration-warning',
  'illustration-error-sad',
  'mascot-update',
  'update-art-light',
  'update-art-dark',
  'thumbs-up',
  'star-gold',
  'avatar-testimonial-1',
  'avatar-testimonial-2',
  'avatar-testimonial-3',
] as const;
export type AssetName = (typeof assetNames)[number] | `category-${Category}`;

export const assetUsage: Record<(typeof assetNames)[number], string> = {
  'mascot-robot': 'Robot mascot — AI Assistant card on Chats (40dp, via <Mascot>)',
  'avatar-male': 'Profile avatar (male): You tab header + bottom-nav "You"',
  'avatar-female': 'Profile avatar (female): You tab header + bottom-nav "You"',
  'illustration-high-five': 'Invite a Friend card: 113 × 112, flush left, 29dp above the card (tilt baked into the file)',
  'illustration-trash': 'Delete confirm sheet (72dp)',
  'whats-new-illustration': "What's New card: robot with scripts (Language Preference), 125dp, top-right",
  'logo-orb-light': 'Equal orb, light mode (#00B140): AI avatar, FAB',
  'logo-orb-dark': 'Equal orb, dark mode (lime): AI avatar, FAB',
  'illustration-silent-mode': 'Silent Mode header (3D bell + phone, 160dp)',
  'illustration-whatsapp': 'WhatsApp Updates header + turn-off sheet (3D WhatsApp phone, 160 / 120dp)',
  'illustration-addresses-empty': 'Addresses empty state (robot on a map, 200dp)',
  'illustration-instructions-empty': 'Active Instructions empty state (robot on a book, 200dp)',
  'illustration-suggestions': '"New suggestions coming…" card: the robot reading, with call, contact, home and tag bubbles (140dp, top-right)',
  'mascot-instructions': 'Instructions hub › Active Instructions row: the robot with a magic wand on the lime tint (40dp in a 44dp circle)',
  'preset-screen-protect': 'Screen & Protect preset: 3D green shield with a tick (33dp on the green tint; 16dp in the source badge)',
  'preset-everyday-calls': 'Handle everyday calls preset: 3D green receiver with a red question mark (33dp on the blue tint; 16dp in the source badge)',
  'permission-microphone': 'Mic permission dialog: 3D studio mic with green sound waves; "Talk to your assistant" (84dp)',
  'avatar-assistant-bust': 'In-app call: the assistant persona on a transparent background, set on the gradient call disc (200dp)',
  'preset-business': 'Run my business preset: 3D brown briefcase (33dp on the orange tint; 16dp in the source badge)',
  'avatar-male-bust': 'Gender card, male (bust without the ring). Falls back to avatar-male',
  'avatar-female-bust': 'Gender card, female (bust without the ring). Falls back to avatar-female',
  'mascot-sleeping': 'System status card: sleeping robot with its warm glow (104dp)',
  'avatar-assistant': 'Meet Your Assistant card: the assistant persona with the orb badge (84 × 94 compact, 96 × 108 banner)',
  'crown-gold': 'Monetization: 3D gold crown (supplied still; animated in code by AnimatedCrown until the rotating video is exported), paywall hero (64dp), Unlock All Details (56dp), Congratulations (100dp)',
  'premium-glow': 'Premium glow: the cream-gold radial with soft light rings behind premium cards (value sheet, quota card). Light theme; dark uses the coded black-gold gradient',
  'assistant-phone': 'Experiment (Component 25): 3D phone with sparkles on the AI Assistant toggle instead of the robot (36dp). The robot stays the default',
  'illustration-search': 'Search empty state: the robot with a magnifier and sparkles; "Search across your calls" (200dp)',
  'illustration-no-internet': 'No-internet state: 3D wifi with a red strike; "No internet connection" (140dp). Reusable on any screen that needs the network',
  'illustration-no-results': 'No-results state: the robot thinking with a question mark; "No results found" (160dp)',
  'system-keyboard': 'Stand-in for the Android keyboard (Gboard) in docs and previews only; never ship it in the app (390 × 300)',
  'call-handling-all': 'Call Handling: you with Mom (favourite) and an Unknown caller; "Which calls should I answer?" hero (280dp)',
  'call-handling-exclude': 'Call Handling: you with Mom and Didi (favourites); "Anyone you want to answer yourself?" hero (280dp). Animated version: lottie call-handling-contacts',
  'call-handling-done': 'Call Handling: the robot on a call between Mom and Unknown; Perfect! / All Sorted! (300dp)',
  'call-setup': 'Call Handling: the robot between a ringing call and an unknown call; "Set up your calls" (240dp)',
  'permission-contacts': 'Permission sheet: 3D orange contact book; "Allow contacts permission" (100dp)',
  'permission-call-history': 'Permission sheet: the robot with a call-history phone; "Allow call history" (100dp)',
  'illustration-no-contacts': 'Empty state: 3D address book; "No contacts saved" (140dp)',
  'value-card-pattern': 'Plan page value card: gold circuit-line pattern across the top (SVG, 329 × 192)',
  'feature-call-screening': 'Plan page What You Get: 3D gold phone with sparkle, 24/7 Unlimited Call Screening (28dp)',
  'feature-recordings': 'Plan page What You Get: 3D gold phone with sound bars, Unlimited Recordings (28dp)',
  'feature-summaries': 'Plan page What You Get: 3D gold summary card with phone, Unlimited Summaries (28dp)',
  'crown-lock': 'Monetization: Premium Lock (SVG), the gold crown with a padlock on locked rows, bubbles and the audio scrubber (16–20dp)',
  'coupon-seal': '3D gold % seal; Monetization: gold % seal on the coupon ticket (28dp)',
  'status-waiting': 'Monetization: 3D rupee coin, Waiting for Confirmation (100dp)',
  'status-waiting-cash': 'Monetization: 3D cash stack, Waiting for Confirmation variant (100dp)',
  'status-confirmed': 'Monetization: green check seal, Payment Confirmed (100dp)',
  'status-pending': 'Monetization: orange ! seal, Payment Pending (100dp)',
  'status-failed': 'Monetization: red ✕ seal, Payment Failed (100dp)',
  'emoji-sad': 'Monetization: 3D sad face, cancellation feedback / Are you sure (100dp)',
  'illustration-airplane': 'Snooze: Airplane mode is on sheet (116dp)',
  'illustration-shield-lock': 'Account & Data: the 3D shield with a lock (140dp)',
  'stat-star': 'Account & Data trust strip: the 3D star by the rating (40dp)',
  'stat-phone': 'Account & Data trust strip: the 3D handset by calls handled (40dp)',
  'illustration-warning': 'Snooze: Assistant snooze failed sheet (88dp)',
  'illustration-error-sad': 'Snooze: Assistant activation failed sheet, red sad face (88dp)',
  'update-art-light': 'Force update art, light: the robot with the green upload badge and glow (176dp hard, 144dp sheet)',
  'update-art-dark': 'Force update art, dark: the robot with the lime upload badge and a black arrow',
  'mascot-update': 'Force update: the update robot, with the coded upload badge (160dp hard / 144dp soft)',
  'thumbs-up': 'Monetization: 3D thumbs up, Your Request is Submitted (100dp)',
  'star-gold': 'Monetization: 3D star in the “Rated 4.5/5” pill (20dp)',
  'avatar-testimonial-1': 'Subscription page: testimonial avatar (36dp)',
  'avatar-testimonial-2': 'Subscription page: testimonial avatar (36dp)',
  'avatar-testimonial-3': 'Subscription page: testimonial avatar (36dp)',
};

/** Caller categories shown as the contact avatar in the chat list, chat header and incoming bubbles. */
export const categories = [
  'na',
  'career',
  'delivery',
  'education',
  'financial',
  'gate-alert',
  'home-services',
  'order-confirmation',
  'otp',
  'others',
  'others-1',
  'political',
  'real-estate',
  'ride',
  'services',
  'travel',
] as const;
export type Category = (typeof categories)[number];

export const categoryLabel: Record<Category, string> = {
  na: 'Unknown',
  career: 'Career',
  delivery: 'Delivery',
  education: 'Education',
  financial: 'Financial',
  'gate-alert': 'Gate alert',
  'home-services': 'Home services',
  'order-confirmation': 'Order confirmation',
  otp: 'OTP',
  others: 'Others',
  'others-1': 'Others (card)',
  political: 'Political',
  'real-estate': 'Real estate',
  ride: 'Ride',
  services: 'Services',
  travel: 'Travel',
};

const files = import.meta.glob('../../../assets/*.{png,webp,svg,jpg,jpeg}', { eager: true, query: '?url', import: 'default' }) as Record<string, string>;
const registry: Partial<Record<string, string>> = {};
for (const [path, url] of Object.entries(files)) {
  const base = path.split('/').pop()!.replace(/\.[^.]+$/, '');
  registry[base] = url;
}

/** Register assets at runtime (e.g. from a CDN) — overrides files in /assets. */
export function registerAssets(map: Partial<Record<AssetName, string>>) {
  Object.assign(registry, map);
}
export const assetUrl = (name: AssetName) => registry[name];
export const hasAsset = (name: AssetName) => !!registry[name];

export type AssetProps = {
  name: AssetName;
  /** dp. Number = square. */
  size?: number;
  width?: number;
  height?: number;
  radius?: number | string;
  alt?: string;
  className?: string;
  style?: CSSProperties;
};

export function Asset({ name, size = 24, width, height, radius = 0, alt = '', className, style }: AssetProps) {
  const w = width ?? size;
  const h = height ?? size;
  const url = registry[name];
  const common: CSSProperties = { width: w, height: h, borderRadius: radius, flexShrink: 0, display: 'block', ...style };
  if (url) return <img src={url} alt={alt} width={w} height={h} className={className} style={{ ...common, objectFit: 'contain' }} draggable={false} />;
  return (
    <span
      role={alt ? 'img' : undefined}
      aria-label={alt || undefined}
      title={`asset: ${name}`}
      data-asset-placeholder={name}
      className={['eq-asset-placeholder', className].filter(Boolean).join(' ')}
      style={{
        ...common,
        backgroundColor: '#F5F5F5',
        backgroundImage:
          'linear-gradient(45deg, #E6E6E6 25%, transparent 25%), linear-gradient(-45deg, #E6E6E6 25%, transparent 25%), linear-gradient(45deg, transparent 75%, #E6E6E6 75%), linear-gradient(-45deg, transparent 75%, #E6E6E6 75%)',
        backgroundSize: `${Math.max(4, Math.round(Math.min(w, h) / 4))}px ${Math.max(4, Math.round(Math.min(w, h) / 4))}px`,
        backgroundPosition: `0 0, 0 ${Math.max(2, Math.round(Math.min(w, h) / 8))}px, ${Math.max(2, Math.round(Math.min(w, h) / 8))}px -${Math.max(2, Math.round(Math.min(w, h) / 8))}px, -${Math.max(2, Math.round(Math.min(w, h) / 8))}px 0`,
      }}
    />
  );
}
