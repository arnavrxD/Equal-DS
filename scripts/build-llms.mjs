#!/usr/bin/env node
// Builds the machine-readable docs that let Claude (or any LLM) build with the system:
//   site/public/llms.txt                 short index (llmstxt.org format)
//   site/public/llms-full.txt            the full catalog: rules, components (props, React + Compose code),
//                                        flows, tokens, assets, icons
//   site/public/CLAUDE.md                the repo's CLAUDE.md, for download
//   site/public/equal-ds-flow-skill.zip  the Claude skill (SKILL.md + RULES.md + CATALOG.md) for claude.ai
// Everything is read from the sources (registry, tokens.json, Asset.tsx, icon manifest, Flow.tsx,
// compose-snippets.json), so it can't drift. Run: npm run llms (also runs before site / site:build).

import { readFileSync, writeFileSync, mkdirSync, rmSync, existsSync } from 'node:fs';
import { execFileSync } from 'node:child_process';
import { dirname, resolve, join } from 'node:path';
import { fileURLToPath } from 'node:url';
import { tmpdir } from 'node:os';

const root = resolve(dirname(fileURLToPath(import.meta.url)), '..');
const read = (p) => readFileSync(resolve(root, p), 'utf8');
const out = resolve(root, 'site/public');
mkdirSync(out, { recursive: true });

// A JS string literal ('…', "…" or `…`) → its value (enough for the registry's literals).
const Q = String.raw`'(?:[^'\\]|\\.)*'|"(?:[^"\\]|\\.)*"|\`(?:[^\`\\]|\\.)*\``;
const unq = (s) => (s == null ? undefined : s.slice(1, -1).replace(/\\(['"`\\])/g, '$1').replace(/\\n/g, '\n'));
const field = (chunk, name) => unq(chunk.match(new RegExp(`\\b${name}: (${Q})`))?.[1]);
const list = (chunk, name) => {
  const m = chunk.match(new RegExp(`\\b${name}: \\[([\\s\\S]*?)\\]`));
  return m ? [...m[1].matchAll(new RegExp(Q, 'g'))].map((x) => unq(x[0])) : [];
};
const stripJsx = (s) => s.replace(/<[^>]+>/g, '').replace(/\{'\s*'\}/g, ' ').replace(/&amp;/g, '&').replace(/\s+/g, ' ').trim();

// ---------------------------------------------------------------- components (registry)
const registry = read('site/src/registry/components.tsx');
const snippets = JSON.parse(read('site/src/registry/compose-snippets.json'));
const a11y = JSON.parse(read('site/src/registry/a11y.json'));
const docs = registry
  .split(/\n  \{\n    id: /)
  .slice(1)
  .map((c) => {
    const chunk = 'id: ' + c;
    const id = field(chunk, 'id');
    const examples = [];
    for (const m of chunk.matchAll(new RegExp(`title: (${Q}),[\\s\\S]*?code: (${Q})`, 'g'))) examples.push({ title: unq(m[1]), code: unq(m[2]) });
    const props = [];
    for (const m of chunk.matchAll(/component: '([^']+)', props: \[([\s\S]*?)\] \}/g)) {
      const items = [...m[2].matchAll(new RegExp(`(req|opt)\\((${Q}), (${Q})(?:, (undefined|${Q}))?, (${Q})\\)`, 'g'))].map((p) => ({
        name: unq(p[2]),
        type: unq(p[3]),
        def: p[1] === 'opt' && p[4] && p[4] !== 'undefined' ? unq(p[4]) : undefined,
        desc: unq(p[5]),
        required: p[1] === 'req',
      }));
      props.push({ component: m[1], items });
    }
    return {
      id,
      name: field(chunk, 'name'),
      group: field(chunk, 'group'),
      summary: field(chunk, 'summary'),
      importLine: field(chunk, 'importLine'),
      compose: list(chunk, 'compose'),
      tokens: list(chunk, 'tokens'),
      spec: field(chunk, 'spec'),
      dos: list(chunk, 'dos'),
      donts: list(chunk, 'donts'),
      examples,
      props,
      composeCode: snippets[id] ?? {},
    };
  })
  .filter((d) => d.id && d.name);

// ---------------------------------------------------------------- flows
const flowSrc = read('site/src/pages/Flow.tsx');
const flows = flowSrc
  .split(/\n  '?([a-z-]+)'?: \{\n    title: /)
  .slice(1)
  .reduce((acc, part, i, arr) => (i % 2 === 0 ? acc.concat([{ id: part, body: 'title: ' + arr[i + 1] }]) : acc), [])
  .map(({ id, body }) => {
    const states = [...body.matchAll(new RegExp(`\\{ key: '([^']+)', label: (${Q}),[\\s\\S]*?code: (${Q})`, 'g'))].map((m) => ({ key: m[1], label: unq(m[2]), code: unq(m[3]) }));
    const stepsBlock = body.match(/steps: \[([\s\S]*?)\n    \],/)?.[1] ?? '';
    const steps = [...stepsBlock.matchAll(/<>([\s\S]*?)<\/>,/g)].map((m) => stripJsx(m[1]));
    return { id, title: field(body, 'title'), lead: field(body, 'lead'), source: field(body, 'source'), states, steps, composeCode: snippets[`flow-${id}`] ?? {} };
  });

// ---------------------------------------------------------------- tokens, assets, icons
const tokens = JSON.parse(read('tokens/tokens.json'));
const assetSrc = read('web/src/foundations/Asset.tsx');
const usageStart = assetSrc.indexOf('export const assetUsage');
const usageBlock = assetSrc.slice(usageStart, assetSrc.indexOf('\n};', usageStart));
const assets = [...usageBlock.matchAll(/\n\s+'?([a-z0-9-]+)'?: '((?:[^'\\]|\\.)*)'/g)].map((m) => ({ name: m[1], usage: m[2].replace(/\\'/g, "'") }));
const assetFiles = new Set(
  execFileSync('ls', [resolve(root, 'assets')]).toString().split('\n').filter(Boolean).map((f) => f.replace(/\.(webp|png|svg|jpg|jpeg)$/, '')),
);
const iconSrc = read('scripts/build-icons.mjs');
const manifest = iconSrc.slice(iconSrc.indexOf('const MANIFEST'), iconSrc.indexOf('\n};', iconSrc.indexOf('const MANIFEST')));
const icons = [...manifest.matchAll(/\n\s+'?([a-z0-9-]+)'?: '([^']+)'/g)].map((m) => ({ name: m[1], source: m[2] }));

const v = (x) => (Array.isArray(x) ? `${x[0]}: ${x.slice(1).join(', ')}` : String(x));
const tokenLines = [];
for (const [k, t] of Object.entries(tokens.alias)) tokenLines.push(`- \`${k}\`  light ${v(t.light)} · dark ${v(t.dark)}${t.derived ? ' · derived' : ''}`);
const gradientLines = Object.entries(tokens.gradient).map(([k, t]) => `- \`gradient/${k}\`  light ${v(t.light)} · dark ${v(t.dark)}`);
const typeLines = Object.entries(tokens.typography)
  .filter(([k, t]) => !k.startsWith('$') && t && typeof t === 'object')
  .map(([k, t]) => `- \`${k}\`  ${t.size ?? t.fontSize ?? ''}/${t.lineHeight ?? ''} weight ${t.weight ?? t.fontWeight ?? ''}`);
const scale = (o) => Object.entries(o ?? {}).filter(([k]) => !k.startsWith('$')).map(([k, x]) => { const val = typeof x === 'object' ? x.value ?? JSON.stringify(x) : x; return String(k) === String(val) ? String(val) : `${k} ${val}`; }).join(' · ');

// ---------------------------------------------------------------- write llms-full.txt
const claudeMd = read('CLAUDE.md');
const fence = (lang, code) => '```' + lang + '\n' + code.trim() + '\n```';
const L = [];
L.push('# Equal AI Design System: full catalog', '');
L.push('> Everything Claude needs to design and build Equal AI screens and flows for web (React) and Android (Jetpack Compose). Generated from the sources by scripts/build-llms.mjs; do not edit by hand.', '');
L.push('## Rules and recipe (CLAUDE.md)', '', claudeMd.replace(/^# .*\n/, '').replace(/^## /gm, '### ').trim(), '');
// Rules & decisions (site/src/data/decisions.ts): every rule with its reason.
{
  const src = read('site/src/data/decisions.ts');
  L.push('## Rules & decisions (why each rule exists)', '');
  for (const g of src.split(/\n  \{\n    id: /).slice(1)) {
    L.push(`### ${field('id: ' + g, 'title')}`, '');
    for (const m of g.matchAll(new RegExp(`rule: (${Q}), why: (${Q}), source: (${Q})`, 'g'))) L.push(`- **${unq(m[1])}** Why: ${unq(m[2])} (Source: ${unq(m[3])})`);
    L.push('');
  }
}
L.push(`## Components (${docs.length} docs pages)`, '');
let group = '';
for (const d of docs.sort((a, b) => (a.group ?? '').localeCompare(b.group ?? '') || a.name.localeCompare(b.name))) {
  if (d.group !== group) L.push(`### Group: ${(group = d.group)}`, '');
  L.push(`#### ${d.name} (\`${d.id}\`)`, '', d.summary ?? '', '');
  if (d.importLine) L.push(`- React: \`${d.importLine}\``);
  if (d.compose.length) L.push(`- Compose: ${d.compose.map((c) => `\`${c}\``).join(', ')}`);
  if (d.tokens.length) L.push(`- Tokens: ${d.tokens.map((c) => `\`${c}\``).join(', ')}`);
  if (d.spec) L.push(`- Source: ${d.spec}`);
  L.push('');
  for (const p of d.props) {
    if (!p.items.length) continue;
    L.push(`Props of \`${p.component}\`:`);
    for (const it of p.items) L.push(`- \`${it.name}${it.required ? '' : '?'}\`: \`${it.type}\`${it.def ? ` = ${it.def}` : ''}. ${it.desc}`);
    L.push('');
  }
  if (d.dos.length) L.push('Do: ' + d.dos.join(' '), '');
  if (d.donts.length) L.push("Don't: " + d.donts.join(' '), '');
  const ax = a11y[d.id];
  if (ax?.anatomy?.length) L.push('Anatomy:', ...ax.anatomy.map((p, i) => `${i + 1}. ${p.part}: ${p.description}${p.spec ? ` (${p.spec})` : ''}`), '');
  if (ax?.accessibility?.length) L.push('Accessibility:', ...ax.accessibility.map((r) => `- ${r.topic}. Web: ${r.web ?? '–'} Android: ${r.android ?? '–'}`), '');
  if (ax?.gaps?.length) L.push('Known accessibility gaps:', ...ax.gaps.map((g) => `- ${g}`), '');
  for (const ex of d.examples) {
    L.push(`Example: ${ex.title}`, fence('tsx', ex.code));
    if (d.composeCode[ex.title]) L.push(fence('kotlin', d.composeCode[ex.title]));
    L.push('');
  }
  // Examples whose React code is generated at runtime (e.g. one per status) still have Compose code.
  for (const [title, code] of Object.entries(d.composeCode)) {
    if (d.examples.some((e) => e.title === title)) continue;
    L.push(`Example: ${title} (Compose)`, fence('kotlin', code), '');
  }
}
const screenSrc = read('site/src/pages/Screens.tsx');
const screens = [...screenSrc.matchAll(new RegExp(`\\{ key: '([^']+)', label: (${Q}),[\\s\\S]*?code: (${Q})`, 'g'))].map((m) => ({ key: m[1], label: unq(m[2]), code: unq(m[3]) }));
L.push('## Core screens (the revamped app: start every flow from these)', '');
for (const sc of screens) {
  L.push(`- \`${sc.key}\` ${sc.label}: \`${sc.code}\``);
  if (snippets.screens?.[sc.key]) L.push('  ' + fence('kotlin', snippets.screens[sc.key]).replace(/\n/g, '\n  '));
}
L.push('');
L.push(`## Flows (${flows.length})`, '');
for (const f of flows) {
  L.push(`### ${f.title} (\`/flows/${f.id}\`)`, '', f.lead ?? '', '', `Source: ${f.source ?? ''}`, '');
  if (f.steps.length) L.push(...f.steps.map((s, i) => `${i + 1}. ${s}`), '');
  L.push('States:');
  for (const s of f.states) {
    L.push(`- \`${s.key}\` ${s.label}: \`${s.code}\``);
    if (f.composeCode[s.key]) L.push('  ' + fence('kotlin', f.composeCode[s.key]).replace(/\n/g, '\n  '));
  }
  L.push('');
}
L.push('## Tokens', '', 'Alias colours (CSS `var(--eq-<name without component/>)`, TS `color.*`, Compose `EqualTheme.colors.*`):', '', ...tokenLines, '');
L.push('Gradients (CSS `var(--eq-gradient-<name>)`, Compose `EqualTheme.gradients.*`):', '', ...gradientLines, '');
L.push('Typography (DM Sans; `<Text variant>`, `.eq-t-<name>`, `EqualTheme.type.<name>`):', '', ...typeLines, '');
L.push(`Spacing: ${scale(tokens.spacing)}`, '', `Radius: ${scale(tokens.radius)}`, '', `Size: ${scale(tokens.size)}`, '');
L.push('## Image assets', '', 'Web `<Asset name="…" size={dp} />`, Compose `EqualAssetImage(EqualAssets.<PascalName>, size)`. "needed" means only a placeholder ships today; ask the designer for it.', '');
for (const a of assets) L.push(`- \`${a.name}\`${assetFiles.has(a.name) ? '' : ' (needed)'}: ${a.usage}`);
L.push('', '## Icons (Material Symbols, Outlined 400; `-fill` = filled)', '', 'Web `<Icon name="…" />`, Compose `EqualIcon(EqualIcons.<PascalName>)`.', '');
L.push(icons.map((i) => `\`${i.name}\` (${i.source})`).join(' · '), '');
const full = L.join('\n');
writeFileSync(join(out, 'llms-full.txt'), full);

// ---------------------------------------------------------------- llms.txt (index)
const idx = [
  '# Equal AI Design System',
  '',
  '> The design system for the Equal AI app (an AI call assistant): tokens, components, screens and flows for web (React) and Android (Jetpack Compose). DM Sans, Material Symbols, a 390 × 800 dp Android frame, light and dark.',
  '',
  'Read llms-full.txt before designing or building: rules, the recipe for a new flow, and every component with its props and React + Compose code.',
  '',
  '## Docs',
  '',
  '- [Full catalog](llms-full.txt): rules, components, flows, tokens, assets, icons',
  '- [CLAUDE.md](CLAUDE.md): the rules and the new-flow recipe Claude follows in the repo',
  '- [Claude skill (zip)](equal-ds-flow-skill.zip): upload in claude.ai → Settings → Capabilities → Skills',
  '- MCP server: https://equal-ds.vercel.app/mcp (tools: search, get, list). Claude Code: `claude mcp add --transport http equal-ds https://equal-ds.vercel.app/mcp`',
  '',
  '## Components',
  '',
  ...docs.map((d) => `- [${d.name}](#/components/${d.id}): ${d.summary ?? ''}`),
  '',
  '## Flows',
  '',
  ...flows.map((f) => `- [${f.title}](#/flows/${f.id}): ${f.lead ?? ''}`),
  '',
].join('\n');
writeFileSync(join(out, 'llms.txt'), idx);
writeFileSync(join(out, 'CLAUDE.md'), claudeMd);

// ---------------------------------------------------------------- the skill zip
const stage = join(tmpdir(), `equal-ds-skill-${process.pid}`);
const dir = join(stage, 'equal-ds-flow');
mkdirSync(dir, { recursive: true });
writeFileSync(join(dir, 'SKILL.md'), read('.claude/skills/equal-ds-flow/SKILL.md'));
writeFileSync(join(dir, 'RULES.md'), claudeMd);
writeFileSync(join(dir, 'CATALOG.md'), full);
const zip = join(out, 'equal-ds-flow-skill.zip');
if (existsSync(zip)) rmSync(zip);
execFileSync('python3', ['-c', 'import shutil,sys; shutil.make_archive(sys.argv[1][:-4], "zip", sys.argv[2], "equal-ds-flow")', zip, stage]);
rmSync(stage, { recursive: true, force: true });

console.log(`  wrote llms.txt, llms-full.txt (${docs.length} components, ${flows.length} flows, ${assets.length} assets, ${icons.length} icons, ${Math.round(full.length / 1024)} KB), CLAUDE.md, equal-ds-flow-skill.zip`);
