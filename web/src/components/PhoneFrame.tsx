import type { ReactNode } from 'react';

/**
 * The Android design frame: every screen starts at 360 × 800dp (mdpi, 1px = 1dp) and extends only
 * vertically when the content needs it. Use this everywhere instead of hard-coding widths.
 */
export const ANDROID_FRAME = { width: 360, height: 800 } as const;

/**
 * 360 × 800 Android frame. Positioned, so sheets/dialogs overlay inside it. `height` extends the
 * frame vertically (never below 800); `'auto'` grows with the content from a minimum of 800.
 */
export function PhoneFrame({ children, height = ANDROID_FRAME.height, bezel = true }: { children: ReactNode; height?: number | 'auto'; bezel?: boolean }) {
  return (
    <div style={{ display: 'flex', justifyContent: 'center' }}>
      <div
        data-eq-frame=""
        style={{
          width: ANDROID_FRAME.width,
          height: height === 'auto' ? undefined : Math.max(height, ANDROID_FRAME.height),
          minHeight: ANDROID_FRAME.height,
          position: 'relative',
          overflow: 'clip',
          background: 'var(--eq-bg-primary)',
          borderRadius: bezel ? 28 : 0,
          boxShadow: bezel ? '0 0 0 8px #1a1a1a, 0 0 0 9px #333, 0 24px 60px rgba(0,0,0,0.25)' : undefined,
          display: 'flex',
          flexDirection: 'column',
          isolation: 'isolate',
        }}
      >
        {children}
      </div>
    </div>
  );
}

export type ScreenProps = {
  header?: ReactNode;
  children: ReactNode;
  /** Pinned bottom content (composer, nav). */
  footer?: ReactNode;
  /** Floating content above the body (bottom nav pill). */
  floating?: ReactNode;
  surface?: 'primary' | 'surface';
  /** Overlays (sheets, dialogs) rendered on top. */
  overlay?: ReactNode;
};

/** Screen scaffold: header / scrolling body / footer, with slots for floating nav and overlays. */
export function Screen({ header, children, footer, floating, surface = 'primary', overlay }: ScreenProps) {
  return (
    <div style={{ position: 'relative', flex: 1, minHeight: 0, display: 'flex', flexDirection: 'column', background: surface === 'primary' ? 'var(--eq-bg-primary)' : 'var(--eq-bg-surface)' }}>
      {header}
      <div style={{ flex: 1, minHeight: 0, overflowY: 'auto', scrollbarWidth: 'none', paddingBottom: floating ? 104 : 0 }}>{children}</div>
      {footer}
      {floating && <div style={{ position: 'absolute', left: 0, right: 0, bottom: 20, display: 'flex', justifyContent: 'center', pointerEvents: 'none' }}><div style={{ pointerEvents: 'auto' }}>{floating}</div></div>}
      {overlay}
    </div>
  );
}
