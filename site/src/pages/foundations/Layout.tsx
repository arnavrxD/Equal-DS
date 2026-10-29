import { motion, radius, shadow, size, spacing } from '@equal/ds';
import { CopyTag, H2, Page } from '../../ui/kit';
import { FoundationCode } from '../../ui/FoundationCode';

export default function Layout() {
  return (
    <Page title="Spacing, radius & elevation" lead="Everything is in dp. Android screens are designed in a 360 × 800 frame (mdpi, 1px = 1dp) that extends only vertically when content needs it. Values were measured from the component references; touch targets never drop below 48dp.">
      <H2>Spacing</H2>
      <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
        {Object.entries(spacing).filter(([k]) => k !== '0').map(([k, v]) => (
          <div key={k} style={{ display: 'grid', gridTemplateColumns: '150px 1fr 60px', alignItems: 'center', gap: 16 }}>
            <CopyTag value={`var(--eq-space-${k})`} label={`--eq-space-${k}`} />
            <span style={{ width: v * 3, height: 14, borderRadius: 4, background: 'var(--site-accent)' }} />
            <span className="s-num" style={{ color: 'var(--site-ink-3)', fontSize: 13 }}>{v}dp</span>
          </div>
        ))}
      </div>
      <H2>Radius</H2>
      <div className="s-grid" style={{ ['--min' as string]: '120px' }}>
        {Object.entries(radius).map(([k, v]) => (
          <div key={k} className="s-cell" style={{ cursor: 'default' }}>
            <span style={{ width: 84, height: 84, borderRadius: v, background: 'var(--eq-bg-primary)', border: '1.5px solid var(--site-accent)' }} />
            <b>{k}</b>
            <span>{v === 999 ? 'full' : `${v}dp`}</span>
          </div>
        ))}
      </div>
      <H2>Elevation</H2>
      <p className="s-p">Depth is soft and offset: cards lift a little, the floating nav and sheets more. Glass chips use an inset bottom edge from <code>action/bottom-color</code>.</p>
      <div className="s-grid" style={{ ['--min' as string]: '180px' }}>
        {Object.entries(shadow).map(([k, v]) => (
          <div key={k} style={{ display: 'flex', flexDirection: 'column', gap: 12, alignItems: 'flex-start' }}>
            <span style={{ width: '100%', height: 88, borderRadius: 18, background: 'var(--eq-bg-surface)', boxShadow: v }} />
            <CopyTag value={`var(--eq-shadow-${k.replace(/[A-Z]/g, (c) => '-' + c.toLowerCase())})`} label={k} />
          </div>
        ))}
      </div>
      <H2>Sizes</H2>
      <table className="s-table">
        <tbody>
          <tr><td>Touch target</td><td className="s-num">{size.touchTarget}dp minimum, even when the visual is smaller</td></tr>
          <tr><td>Status bar · App bar</td><td className="s-num">{size.statusBar}dp · {size.appBar}dp</td></tr>
          <tr><td>Icons</td><td className="s-num">{Object.entries(size.icon).map(([k, v]) => `${k} ${v}`).join(' · ')}dp</td></tr>
          <tr><td>Avatars</td><td className="s-num">{Object.entries(size.avatar).map(([k, v]) => `${k} ${v}`).join(' · ')}dp</td></tr>
          <tr><td>Frame</td><td className="s-num">{size.frame.width} × {size.frame.height}dp (mdpi, 1px = 1dp = 1sp)</td></tr>
        </tbody>
      </table>
      <H2>Motion</H2>
      <p className="s-p">Short and purposeful: fast {motion.duration.fast}ms for press feedback, base {motion.duration.base}ms for chips, {motion.duration.switchToggle}ms ease-in-out for switches, slow {motion.duration.slow}ms for sheets. Standard easing is <code>{motion.easing.standard}</code>. Everything collapses to instant under reduced motion.</p>
      <p className="s-p"><b>Monetization timings</b> come straight from the Figma prototype, so the build moves the way the prototype does:</p>
      <table className="s-table">
        <thead><tr><th>Moment</th><th>Token</th><th>Timing</th></tr></thead>
        <tbody>
          <tr><td>Coupon applies (spring)</td><td><code>couponSpring</code> · <code>couponDelay</code></td><td>{motion.duration.couponSpring}ms after {motion.duration.couponDelay}ms, <code>spring</code></td></tr>
          <tr><td>Price drops</td><td><code>priceDrop</code> · <code>priceDelay</code></td><td>{motion.duration.priceDrop}ms after {motion.duration.priceDelay}ms</td></tr>
          <tr><td>Gold CTA shimmer</td><td><code>shimmer</code> · <code>shimmerDelay</code></td><td>{motion.duration.shimmer}ms sweep every {motion.duration.shimmerDelay + motion.duration.shimmer}ms</td></tr>
          <tr><td>Gold CTA glow</td><td><code>buttonGlow</code> · <code>buttonGlowDelay</code></td><td>{motion.duration.buttonGlow}ms after {motion.duration.buttonGlowDelay}ms, back and forth</td></tr>
          <tr><td>Payment status changes</td><td><code>paymentStatus</code> · <code>paymentDelay</code></td><td>{motion.duration.paymentStatus}ms <code>gentle</code>, waiting → confirmed after {motion.duration.paymentDelay}ms</td></tr>
          <tr><td>Reward rays (Congratulations)</td><td><code>reward</code></td><td>one turn per {motion.duration.reward / 1000}s, linear</td></tr>
        </tbody>
      </table>
      <p className="s-p">Two kinds of motion: <b>micro-interactions</b> (press, toggles, chips, sheets, the heads-up notification) are code on these tokens, in Compose <code>animate*AsState</code> / web CSS. <b>Illustrative animations</b> (mascot moments, celebrations, loading characters) are <b>Lottie JSON</b> files, played by Lottie on Android; never re-built by hand in code. Drop the JSON into <code>assets/lottie/</code> and play it with <code>{'<Lottie name="…" />'}</code> or <code>EqualLottie(R.raw.lottie_…)</code>; see <a href="#/components/lottie">Lottie animation</a>.</p>
      <H2>Code</H2>
      <FoundationCode id="layout" />
    </Page>
  );
}
