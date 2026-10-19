#!/usr/bin/env node
// Where each colour token is used: scans web components and screens for every alias and gradient
// CSS variable and maps the files back to their docs pages. Writes site/src/registry/token-usage.json,
// which the Colour page shows as "Used in". Run by `npm run site` / `npm run site:build`.

import { readFileSync, readdirSync, writeFileSync } from 'node:fs';
import { dirname, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';

const root = resolve(dirname(fileURLToPath(import.meta.url)), '..');
const read = (p) => readFileSync(resolve(root, p), 'utf8');
const tokens = JSON.parse(read('tokens/tokens.json'));

const cssVar = (name) => '--eq-' + name.replace(/^component\//, '').replace(/[/_]/g, '-');
const vars = [
  ...Object.keys(tokens.alias).map((name) => ({ name, kind: 'alias', v: cssVar(name) })),
  ...Object.keys(tokens.gradient).map((name) => ({ name: `gradient/${name}`, kind: 'gradient', v: `--eq-gradient-${name}` })),
];

// Docs pages: which exported component names belong to which page.
const registry = read('site/src/registry/components.tsx');
const nameToDoc = new Map();
for (const chunk of registry.split(/\n  \{\n    id: /).slice(1)) {
  const id = chunk.match(/^'([^']+)'/)?.[1];
  const title = chunk.match(/\n    name: (['"])((?:(?!\1).)*)\1/)?.[2];
  const comps = chunk.match(/\n    components: \[([^\]]*)\]/)?.[1] ?? '';
  for (const c of comps.matchAll(/'([A-Za-z0-9_]+)'/g)) if (!nameToDoc.has(c[1])) nameToDoc.set(c[1], { id, name: title });
}

// Each component file → the docs pages of the components it exports.
const compDir = 'web/src/components';
const files = readdirSync(resolve(root, compDir));
const pagesOfFile = new Map();
for (const f of files.filter((f) => f.endsWith('.tsx'))) {
  const src = read(`${compDir}/${f}`);
  const docs = new Map();
  for (const m of src.matchAll(/export function ([A-Z][A-Za-z0-9]*)/g)) { const d = nameToDoc.get(m[1]); if (d) docs.set(d.id, d); }
  pagesOfFile.set(f.replace(/\.tsx$/, ''), [...docs.values()]);
}

const usage = {};
for (const { name, v } of vars) {
  const pages = new Map();
  const screens = new Set();
  const re = new RegExp(`var\\(${v.replace(/[-]/g, '\\-')}[),\\s]`);
  // Also count computed names: `var(--eq-contact-avatar-${tone}-bg)` uses every token with that
  // prefix, and <Text color="accent-gold"> uses text/accent-gold.
  const textColor = name.startsWith('text/') ? new RegExp(`color=["'{]+${name.slice(5)}["'}]`) : null;
  const dynamic = (src) => [...src.matchAll(/var\((--eq-[a-z0-9-]+-)\$\{/g)].some((m) => v.startsWith(m[1]));
  for (const f of files) {
    if (!/\.(tsx|css)$/.test(f)) continue;
    const src = read(`${compDir}/${f}`);
    if (!re.test(src) && !dynamic(src) && !(textColor && textColor.test(src))) continue;
    for (const d of pagesOfFile.get(f.replace(/\.(tsx|css)$/, '')) ?? []) pages.set(d.id, d);
  }
  for (const f of readdirSync(resolve(root, 'web/src/screens')).filter((f) => f.endsWith('.tsx'))) {
    if (re.test(read(`web/src/screens/${f}`))) screens.add(f.replace(/\.tsx$/, ''));
  }
  usage[name] = { pages: [...pages.values()], screens: [...screens] };
}

writeFileSync(resolve(root, 'site/src/registry/token-usage.json'), JSON.stringify(usage, null, 1) + '\n');
const unused = Object.entries(usage).filter(([, u]) => !u.pages.length && !u.screens.length).map(([k]) => k);
console.log(`  wrote token-usage.json (${vars.length} tokens, ${unused.length} not referenced by any component or screen)`);
