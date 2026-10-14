#!/usr/bin/env node
// Splits site/public/llms-full.txt into the entries the MCP server serves (api/_index.json):
// every component docs page, core screen, flow, rule section, token, asset and the icon list.
// Run by `npm run llms` right after build-llms.mjs, so the server never drifts from the catalog.

import { readFileSync, writeFileSync } from 'node:fs';
import { dirname, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';

const root = resolve(dirname(fileURLToPath(import.meta.url)), '..');
const full = readFileSync(resolve(root, 'site/public/llms-full.txt'), 'utf8');
const lines = full.split('\n');

/** Splits lines into blocks that start at a heading of exactly `level` (#, ##, ###…). */
function blocks(src, level) {
  const mark = '#'.repeat(level) + ' ';
  const out = [];
  let cur = null;
  let fence = false;
  for (const line of src) {
    if (line.startsWith('```')) fence = !fence;
    if (!fence && line.startsWith(mark)) {
      cur = { title: line.slice(mark.length).trim(), body: [] };
      out.push(cur);
    } else if (cur) cur.body.push(line);
  }
  return out;
}

const firstPara = (body) => body.join('\n').trim().split(/\n\s*\n/)[0].replace(/\s+/g, ' ').slice(0, 280);
const slug = (s) => s.toLowerCase().replace(/[^a-z0-9]+/g, '-').replace(/^-|-$/g, '');
const entries = [];
const add = (e) => entries.push({ ...e, text: e.text.trim() });

for (const section of blocks(lines, 2)) {
  const t = section.title;
  if (t.startsWith('Rules and recipe') || t.startsWith('Rules & decisions')) {
    const group = t.startsWith('Rules and recipe') ? 'rules' : 'decisions';
    for (const b of blocks(section.body, 3)) {
      add({ name: `${group}/${slug(b.title)}`, kind: group === 'rules' ? 'rule' : 'decision', title: b.title, summary: firstPara(b.body), aliases: [], text: `## ${b.title}\n${b.body.join('\n')}` });
    }
  } else if (t.startsWith('Components')) {
    for (const g of blocks(section.body, 3)) {
      const group = g.title.replace(/^Group:\s*/, '');
      for (const c of blocks(g.body, 4)) {
        const m = c.title.match(/^(.*) \(`([^`]+)`\)$/);
        const id = m ? m[2] : slug(c.title);
        const body = c.body.join('\n');
        const react = (body.match(/^- React: `import \{([^}]*)\}/m)?.[1] ?? '').split(',').map((s) => s.trim()).filter(Boolean);
        const compose = (body.match(/^- Compose: (.*)$/m)?.[1] ?? '').match(/`([^`]+)`/g)?.map((s) => s.slice(1, -1)) ?? [];
        add({ name: id, kind: 'component', title: m ? m[1] : c.title, group, summary: firstPara(c.body), aliases: [...new Set([...react, ...compose])], text: `# ${c.title}\nGroup: ${group}\n${body}` });
      }
    }
  } else if (t.startsWith('Core screens')) {
    add({ name: 'core-screens', kind: 'screen', title: 'Core screens', summary: firstPara(section.body), aliases: ['HomeScreen', 'ChatScreen', 'YouScreen'], text: `# ${t}\n${section.body.join('\n')}` });
  } else if (t.startsWith('Flows')) {
    for (const f of blocks(section.body, 3)) {
      const m = f.title.match(/^(.*) \(`\/flows\/([^`]+)`\)$/);
      const id = m ? m[2] : slug(f.title);
      add({ name: `flow/${id}`, kind: 'flow', title: m ? m[1] : f.title, summary: firstPara(f.body), aliases: [id], text: `# Flow: ${f.title}\n${f.body.join('\n')}` });
    }
  } else if (t.startsWith('Tokens')) {
    add({ name: 'tokens', kind: 'tokens', title: 'All tokens', summary: firstPara(section.body), aliases: [], text: `# ${t}\n${section.body.join('\n')}` });
    for (const line of section.body) {
      const m = line.match(/^- `([^`]+)`\s+(.*)$/);
      if (m) add({ name: `token/${m[1]}`, kind: 'token', title: m[1], summary: m[2].slice(0, 280), aliases: [m[1], `--eq-${m[1].replace(/^component\//, '').replace(/\//g, '-')}`], text: line });
    }
  } else if (t.startsWith('Image assets')) {
    add({ name: 'assets', kind: 'assets', title: 'All image assets', summary: firstPara(section.body), aliases: [], text: `# ${t}\n${section.body.join('\n')}` });
    for (const line of section.body) {
      const m = line.match(/^- `([^`]+)`:\s*(.*)$/);
      if (m) add({ name: `asset/${m[1]}`, kind: 'asset', title: m[1], summary: m[2].slice(0, 280), aliases: [m[1]], text: line });
    }
  } else if (t.startsWith('Icons')) {
    add({ name: 'icons', kind: 'icons', title: 'All icons', summary: firstPara(section.body), aliases: [], text: `# ${t}\n${section.body.join('\n')}` });
  }
}

const out = resolve(root, 'api/_index.json');
writeFileSync(out, JSON.stringify({ generated: 'scripts/build-mcp-index.mjs from site/public/llms-full.txt', count: entries.length, entries }) + '\n');
const kinds = entries.reduce((m, e) => ((m[e.kind] = (m[e.kind] || 0) + 1), m), {});
console.log(`  wrote api/_index.json (${entries.length} entries: ${Object.entries(kinds).map(([k, n]) => `${n} ${k}`).join(', ')})`);
