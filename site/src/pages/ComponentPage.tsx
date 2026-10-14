import { useEffect, useState } from 'react';
import { componentDocs } from '../registry/components';
import composeSnippets from '../registry/compose-snippets.json';
import a11yData from '../registry/a11y.json';
import type { ComponentA11y, ComponentDoc } from '../registry/types';
import { Link } from '../app/router';
import { CopyTag, DoDont, Example, md, Page, PropsTable } from '../ui/kit';

type Tab = 'overview' | 'props' | 'a11y' | 'tokens';
const TABS: { id: Tab; label: string }[] = [
  { id: 'overview', label: 'Overview' },
  { id: 'props', label: 'Properties' },
  { id: 'a11y', label: 'Accessibility' },
  { id: 'tokens', label: 'Tokens & specs' },
];
const a11yById = a11yData as Record<string, ComponentA11y>;

/** The numbered parts of a component, in reading order. */
function Anatomy({ parts }: { parts: ComponentA11y['anatomy'] }) {
  return (
    <div style={{ overflowX: 'auto' }}>
      <table className="s-table s-anatomy">
        <thead>
          <tr><th style={{ width: 44 }}>#</th><th>Part</th><th>What it does</th><th>Spec</th></tr>
        </thead>
        <tbody>
          {parts.map((p, i) => (
            <tr key={p.part}>
              <td><span className="s-anatomy__n">{i + 1}</span></td>
              <td>{p.part}</td>
              <td>{md(p.description)}</td>
              <td>{p.spec ? <span className="s-anatomy__spec">{md(p.spec)}</span> : <span style={{ color: 'var(--site-ink-3)' }}>–</span>}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

/** Accessibility per topic, web and Android side by side, plus known gaps in today's code. */
function Accessibility({ data }: { data: ComponentA11y }) {
  return (
    <>
      <p className="s-p">How this component works with screen readers, keyboards and touch, on both platforms. The floor for every component: WCAG 2.2 AA contrast, 48 dp touch targets, a name on every control, state that isn’t shown by colour alone, and motion that respects reduced-motion settings.</p>
      <div style={{ overflowX: 'auto' }}>
        <table className="s-table s-a11y">
          <thead>
            <tr><th style={{ width: 130 }}>Topic</th><th>Web (React)</th><th>Android (Compose)</th></tr>
          </thead>
          <tbody>
            {data.accessibility.map((r) => (
              <tr key={r.topic}>
                <td>{r.topic}</td>
                <td>{r.web ? md(r.web) : <span style={{ color: 'var(--site-ink-3)' }}>–</span>}</td>
                <td>{r.android ? md(r.android) : <span style={{ color: 'var(--site-ink-3)' }}>–</span>}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      {data.gaps.length > 0 && (
        <>
          <h2 className="s-h2">Known gaps</h2>
          <p className="s-p">Where today’s code falls short of the rules above. Fix these before relying on the component for a new screen.</p>
          <ul className="s-gaps">{data.gaps.map((g) => <li key={g}>{md(g)}</li>)}</ul>
        </>
      )}
    </>
  );
}

export default function ComponentPage({ doc }: { doc: ComponentDoc }) {
  const [tab, setTab] = useState<Tab>('overview');
  useEffect(() => setTab('overview'), [doc.id]);
  const i = componentDocs.findIndex((d) => d.id === doc.id);
  const prev = componentDocs[i - 1];
  const next = componentDocs[i + 1];
  return (
    <Page
      key={doc.id}
      title={doc.name}
      lead={md(doc.summary)}
      toc={false}
      meta={
        <>
          <CopyTag k="Import" value={doc.importLine} label={doc.components.join(', ')} />
          {doc.compose.map((c) => <CopyTag key={c} k="Compose" value={c} />)}
        </>
      }
    >
      <div className="s-tabs" role="tablist" aria-label={`${doc.name} documentation`}>
        {TABS.map((t) => (
          <button key={t.id} type="button" role="tab" id={`tab-${t.id}`} aria-selected={tab === t.id} aria-controls={`panel-${t.id}`} onClick={() => setTab(t.id)}>
            {t.label}
          </button>
        ))}
      </div>

      <div role="tabpanel" id={`panel-${tab}`} aria-labelledby={`tab-${tab}`} className="s-tabpanel">
        {tab === 'overview' && (
          <>
            {doc.examples.map((ex) => (
              <Example key={ex.title} title={ex.title} note={ex.note} code={ex.code} compose={(composeSnippets as Record<string, Record<string, string>>)[doc.id]?.[ex.title]} width={ex.width} surface={ex.surface}>
                {ex.render()}
              </Example>
            ))}
            {a11yById[doc.id]?.anatomy.length ? (
              <>
                <h2 className="s-h2">Anatomy</h2>
                <Anatomy parts={a11yById[doc.id].anatomy} />
              </>
            ) : null}
            <h2 className="s-h2">Usage</h2>
            <DoDont dos={doc.dos} donts={doc.donts} />
          </>
        )}

        {tab === 'a11y' && (a11yById[doc.id] ? <Accessibility data={a11yById[doc.id]} /> : <p className="s-p">Accessibility notes for this component are coming.</p>)}

        {tab === 'props' &&
          doc.props.map((p) => (
            <section key={p.component} style={{ marginTop: 8 }}>
              <h3 className="s-h3"><code style={{ fontSize: 15 }}>{`<${p.component}>`}</code></h3>
              <div className="s-scroll"><PropsTable rows={p.props} /></div>
            </section>
          ))}

        {tab === 'tokens' && (
          <>
            <p className="s-p">The alias tokens this component reads. Change the value in <code>tokens/tokens.json</code>, not in the component.</p>
            <div style={{ display: 'flex', flexWrap: 'wrap', gap: 8 }}>
              {doc.tokens.map((t) => (
                <Link key={t} to="/foundations/colour" anchor={`t-${t.replace(/^component\//, '').replace(/[/_]/g, '-')}`} className="s-tag">{t}</Link>
              ))}
            </div>
            {doc.spec && (
              <>
                <h2 className="s-h2">Spec</h2>
                <p className="s-p">{md(doc.spec)}</p>
              </>
            )}
            <h2 className="s-h2">Compose</h2>
            <div style={{ display: 'flex', flexWrap: 'wrap', gap: 8 }}>
              {doc.compose.map((c) => <CopyTag key={c} k="Compose" value={c} />)}
            </div>
          </>
        )}
      </div>

      <nav className="s-prevnext" aria-label="Previous and next component">
        {prev ? <Link to={`/components/${prev.id}`} className="s-arrowlink" style={{ color: 'var(--site-ink-2)' }}>← {prev.name}</Link> : <span />}
        {next && <Link to={`/components/${next.id}`} className="s-arrowlink">{next.name} →</Link>}
      </nav>
    </Page>
  );
}
