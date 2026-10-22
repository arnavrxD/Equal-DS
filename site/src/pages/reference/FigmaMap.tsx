import { figmaMap, figmaRebuild } from '../../data/figmaMap';
import { Page } from '../../ui/kit';

export default function FigmaMap() {
  return (
    <Page title="Figma → code" lead="Every generically named component in the Figma file (“Component 25”, “Group 2085663987”…) and what it is in the system, plus how the file was rebuilt: fonts, frames, sizes and radii. The Figma file is read-only here; nothing in it was renamed." toc={false}>
      <h2 className="s-h2">Unnamed components</h2>
      <div style={{ overflowX: 'auto' }}>
        <table className="s-table">
          <thead><tr><th style={{ width: '22%' }}>Figma name</th><th style={{ width: '24%' }}>Where</th><th style={{ width: '18%' }}>What it is</th><th>In code (web · Compose)</th></tr></thead>
          <tbody>
            {figmaMap.map((r) => (
              <tr key={r.figma}>
                <td><code>{r.figma}</code>{r.gap && <div><span className="s-pill" style={{ marginTop: 6 }}>needs design</span></div>}</td>
                <td>{r.where}</td>
                <td style={{ fontWeight: 500 }}>{r.is}</td>
                <td style={{ color: 'var(--site-ink)' }}>{r.doc ? <a href={`#/components/${r.doc}`}>{r.code}</a> : r.code}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      <h2 className="s-h2" style={{ marginTop: 40 }}>How the file was rebuilt</h2>
      <div style={{ overflowX: 'auto' }}>
        <table className="s-table">
          <thead><tr><th style={{ width: '16%' }}>What</th><th>In the file</th><th>In the system</th></tr></thead>
          <tbody>
            {figmaRebuild.map((r) => (
              <tr key={r.what}><td style={{ fontWeight: 500 }}>{r.what}</td><td>{r.file}</td><td style={{ color: 'var(--site-ink)' }}>{r.code}</td></tr>
            ))}
          </tbody>
        </table>
      </div>
    </Page>
  );
}
