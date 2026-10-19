import { Asset, categoryLabel, type Category } from '../foundations/Asset';
import { Text } from '../foundations/Text';
import { Icon } from '../foundations/Icon';

/**
 * Equal orb logo. Real asset: `logo-orb-light` (#00B140) in light mode, `logo-orb-dark` (lime) in dark mode —
 * swapped by CSS on [data-theme]. The opaque orb is the centre 80% of the file; the outer glow overflows
 * the layout box, so `size` is the orb itself.
 */
export function EqualOrb({ size = 36, label = 'Equal AI' }: { size?: number; label?: string }) {
  const img = size / 0.8;
  const inset = (img - size) / 2;
  const layer = { position: 'absolute', left: -inset, top: -inset } as const;
  return (
    <span role={label ? 'img' : undefined} aria-label={label || undefined} aria-hidden={label ? undefined : true} style={{ position: 'relative', width: size, height: size, flexShrink: 0, display: 'inline-block' }}>
      <span className="eq-orb eq-orb--light" style={layer}><Asset name="logo-orb-light" size={img} /></span>
      <span className="eq-orb eq-orb--dark" style={layer}><Asset name="logo-orb-dark" size={img} /></span>
    </span>
  );
}

// Mascot artwork bounds inside its 4096 × 4430 canvas (measured from the alpha channel).
const MASCOT = { w: 4096, h: 4430, box: { x: 597, y: 799, w: 2915, h: 2907 } } as const;

/**
 * Equal AI robot mascot (asset `mascot-robot`). The file keeps its transparent padding (never cropped);
 * it is drawn scaled so the robot itself fills `size` × `size`, centred.
 */
export function Mascot({ size = 40, dimmed = false, label = 'Equal assistant' }: { size?: number; dimmed?: boolean; label?: string }) {
  const scale = size / Math.max(MASCOT.box.w, MASCOT.box.h);
  const imgW = MASCOT.w * scale;
  const imgH = MASCOT.h * scale;
  const left = -MASCOT.box.x * scale + (size - MASCOT.box.w * scale) / 2;
  const top = -MASCOT.box.y * scale + (size - MASCOT.box.h * scale) / 2;
  return (
    <span role={label ? 'img' : undefined} aria-label={label || undefined} aria-hidden={label ? undefined : true} style={{ position: 'relative', width: size, height: size, flexShrink: 0, display: 'inline-block' }}>
      <span style={{ position: 'absolute', left, top, filter: dimmed ? 'grayscale(1)' : undefined, opacity: dimmed ? 0.7 : 1, transition: 'filter var(--eq-duration-base), opacity var(--eq-duration-base)' }}>
        <Asset name="mascot-robot" width={imgW} height={imgH} />
      </span>
    </span>
  );
}

/** User's profile avatar — 3D character in a gold scalloped ring (supplied art). */
export type ProfileAvatarVariant = 'male' | 'female';
const PROFILE_ASPECT: Record<ProfileAvatarVariant, number> = { male: 2000 / 1783, female: 2000 / 1772 };

/**
 * Profile avatar for the You tab and the bottom-nav "You" item. `size` is the width; the head
 * intentionally rises above the ring, so height = width × ~1.12 (the file is never cropped).
 */
/** The user's plan. Paid users get the gold background + scalloped gold ring; free users the plain bust. */
export type PlanTier = 'free' | 'paid';

/** Clean bust art (free tier): width / height. */
const BUST_RATIO = 238 / 320;

export function ProfileAvatar({ variant = 'male', size = 72, tier = 'free', label = 'Your profile' }: { variant?: ProfileAvatarVariant; size?: number; tier?: PlanTier; label?: string }) {
  const h = Math.round(size * PROFILE_ASPECT[variant]);
  if (tier === 'paid') return <Asset name={variant === 'male' ? 'avatar-male' : 'avatar-female'} width={size} height={h} alt={label} />;
  // Free: the clean bust, same box height, centred in the same width.
  return (
    <span style={{ width: size, height: h, display: 'inline-flex', justifyContent: 'center', flexShrink: 0 }}>
      <Asset name={variant === 'male' ? 'avatar-male-bust' : 'avatar-female-bust'} width={Math.round(h * BUST_RATIO)} height={h} alt={label} />
    </span>
  );
}

/** Equal AI assistant avatar — the orb. Sizes 24 / 36 / 48. */
export function AiAvatar({ size = 36, label }: { size?: 24 | 36 | 48 | number; /** '' when a visible title already names Equal AI (decorative). */ label?: string }) {
  return <EqualOrb size={size} label={label} />;
}

/** Caller category avatar — the image carries its own coloured circle. */
export function CategoryAvatar({ category = 'na', size = 48, label }: { category?: Category; size?: number; /** Alt text; '' when it sits beside a visible name. Defaults to the category name. */ label?: string }) {
  return <Asset name={`category-${category}`} size={size} radius="50%" alt={label ?? categoryLabel[category]} />;
}

export type ContactAvatarProps = {
  size?: number;
  /** Caller category. Defaults to `na` (unknown caller — the sparkle). */
  category?: Category;
  /** Initials for saved contacts (overrides category). */
  initials?: string;
  /** Green check badge (selection mode). Replaces the saved badge while selected. */
  selected?: boolean;
  /** Saved contacts: the person badge at the bottom-right that marks a known contact. Default on with `initials`. */
  savedBadge?: boolean;
  /** Disc colour for initials. Defaults to one picked from the initials, so a list isn't all one colour. */
  tone?: ContactTone;
};

export const contactTones = ['blue', 'green', 'purple', 'orange', 'pink', 'teal'] as const;
export type ContactTone = (typeof contactTones)[number];

/** Stable tone for a contact: the same initials always get the same colour. */
export function contactTone(seed: string): ContactTone {
  let h = 0;
  for (const ch of seed.toUpperCase()) h = (h * 31 + ch.charCodeAt(0)) >>> 0;
  return contactTones[h % contactTones.length];
}

/**
 * Contact avatar: category icon (unknown / business callers) or initials (saved contacts). Initials
 * sit on a tinted disc (one of six hues at the same saturation and lightness, picked per contact)
 * with the person badge, the signifier for a known / saved contact.
 */
export function ContactAvatar({ size = 48, category = 'na', initials, selected, savedBadge = !!initials, tone }: ContactAvatarProps) {
  const t = tone ?? contactTone(initials ?? '');
  const badge = Math.round(size * 0.36);
  return (
    <span style={{ position: 'relative', width: size, height: size, flexShrink: 0, display: 'inline-block' }}>
      {initials ? (
        <span style={{ width: size, height: size, borderRadius: '50%', background: `var(--eq-contact-avatar-${t}-bg)`, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
          {/* Initials scale with the avatar (37.5%): 36 at 96, 18 at 48, 15 at 40. */}
          <Text variant="subheading" color="none" style={{ color: `var(--eq-contact-avatar-${t}-text)`, fontSize: Math.round(size * 0.375), lineHeight: 1.2, fontWeight: 500 }}>
            {initials.slice(0, 2).toUpperCase()}
          </Text>
        </span>
      ) : (
        <CategoryAvatar category={category} size={size} />
      )}
      {savedBadge && !selected && (
        <span aria-label="Saved contact" role="img" style={{ position: 'absolute', right: -Math.round(size * 0.1), bottom: 0, width: badge, height: badge, borderRadius: '50%', background: 'var(--eq-contact-avatar-badge-bg)', color: 'var(--eq-contact-avatar-badge-icon)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
          <Icon name="person" size={Math.round(badge * 0.7)} />
        </span>
      )}
      {selected && (
        <span role="img" aria-label="Selected" style={{ position: 'absolute', right: -2, bottom: -2, width: 18, height: 18, borderRadius: '50%', background: 'var(--eq-button-primary)', color: 'var(--eq-text-on-action)', display: 'flex', alignItems: 'center', justifyContent: 'center', boxShadow: '0 0 0 2px var(--eq-bg-surface)' }}>
          <Icon name="check" size={12} />
        </span>
      )}
    </span>
  );
}
