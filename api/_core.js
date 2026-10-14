// Equal DS MCP server core: JSON-RPC handling for the Model Context Protocol, shared by the
// hosted endpoint (api/mcp.js → https://<site>/mcp) and the local stdio server (scripts/mcp-stdio.mjs).
// Data: api/_index.json, generated from site/public/llms-full.txt by scripts/build-mcp-index.mjs.

import { readFileSync } from 'node:fs';

const index = JSON.parse(readFileSync(new URL('./_index.json', import.meta.url), 'utf8'));
const entries = index.entries;
const byName = new Map(entries.map((e) => [e.name.toLowerCase(), e]));
const byAlias = new Map();
for (const e of entries) for (const a of e.aliases) if (!byAlias.has(a.toLowerCase())) byAlias.set(a.toLowerCase(), e);

const SERVER = { name: 'equal-ds', title: 'Equal AI Design System', version: '1.0.0' };
const PROTOCOLS = ['2025-06-18', '2025-03-26', '2024-11-05'];
const KINDS = ['component', 'flow', 'screen', 'rule', 'decision', 'token', 'tokens', 'asset', 'assets', 'icons'];

const INSTRUCTIONS = `Equal AI design system (React web + Jetpack Compose Android), by Equal AI.
Before writing any Equal AI UI: call get("rules/the-rules-these-always-apply") once, then search for each part you need and get its entry.
Reuse an existing component before adding one. Colours, type, spacing and motion come only from tokens. Every screen works in light and dark; in dark mode every green is lime (#BAFF29).
Entry names: component ids (e.g. "missed-call"), "flow/<id>", "rules/<section>", "decisions/<group>", "token/<name>", "asset/<name>", "tokens", "assets", "icons", "core-screens". React/Compose component names (e.g. "PaywallFooter") also work with get.`;

const TOOLS = [
  {
    name: 'search',
    title: 'Search the design system',
    description: 'Find components, flows, rules, tokens and assets in the Equal AI design system by keyword or plain words (e.g. "missed call card", "paywall footer", "dark mode green", "text/action"). Returns names to pass to get.',
    inputSchema: {
      type: 'object',
      properties: {
        query: { type: 'string', description: 'What you are looking for.' },
        kind: { type: 'string', enum: KINDS, description: 'Optional: only this kind of entry.' },
        limit: { type: 'number', description: 'Max results (default 8, max 25).' },
      },
      required: ['query'],
    },
    annotations: { readOnlyHint: true, openWorldHint: false },
  },
  {
    name: 'get',
    title: 'Get a design system entry',
    description: 'Full documentation for one entry: a component page (React + Compose imports, tokens, do/don\'t, every example in React and Kotlin), a flow (states and steps), a rule section, a token, an asset, or "tokens" / "assets" / "icons" / "core-screens". Accepts entry names from search or component names like "PaywallFooter".',
    inputSchema: {
      type: 'object',
      properties: { name: { type: 'string', description: 'Entry name (from search) or a React/Compose component name.' } },
      required: ['name'],
    },
    annotations: { readOnlyHint: true, openWorldHint: false },
  },
  {
    name: 'list',
    title: 'List design system entries',
    description: 'List entry names, optionally of one kind (component, flow, rule, decision, token, asset…). Use it to see everything that exists.',
    inputSchema: { type: 'object', properties: { kind: { type: 'string', enum: KINDS } } },
    annotations: { readOnlyHint: true, openWorldHint: false },
  },
];

const words = (s) => s.toLowerCase().split(/[^a-z0-9#/-]+/).filter((w) => w.length > 1);

function search({ query = '', kind, limit = 8 }) {
  const q = query.toLowerCase().trim();
  const terms = words(q);
  const max = Math.min(Math.max(Number(limit) || 8, 1), 25);
  const scored = [];
  for (const e of entries) {
    if (kind && e.kind !== kind) continue;
    const name = e.name.toLowerCase();
    const title = e.title.toLowerCase();
    const aliases = e.aliases.map((a) => a.toLowerCase());
    const summary = e.summary.toLowerCase();
    const text = e.text.toLowerCase();
    let s = 0;
    if (name === q || aliases.includes(q)) s += 100;
    if (title === q) s += 60;
    if (q && title.includes(q)) s += 25;
    if (q && summary.includes(q)) s += 12;
    for (const t of terms) {
      if (name.includes(t)) s += 8;
      if (title.includes(t)) s += 8;
      if (aliases.some((a) => a.includes(t))) s += 6;
      if (summary.includes(t)) s += 4;
      const hits = text.split(t).length - 1;
      if (hits) s += Math.min(hits, 10);
    }
    if (terms.length > 1 && terms.every((t) => text.includes(t))) s += 10;
    // Prefer pages over single token/asset lines when scores tie.
    if (s && (e.kind === 'token' || e.kind === 'asset')) s -= 2;
    if (s > 0) scored.push([s, e]);
  }
  scored.sort((a, b) => b[0] - a[0]);
  const top = scored.slice(0, max).map(([, e]) => ({ name: e.name, kind: e.kind, title: e.title, ...(e.group ? { group: e.group } : {}), ...(e.aliases.length && e.kind === 'component' ? { components: e.aliases } : {}), summary: e.summary }));
  if (!top.length) return `No matches for "${query}". Try fewer or different words, or call list.`;
  return top.map((r) => `- ${r.name} (${r.kind}${r.group ? `, ${r.group}` : ''}): ${r.title}${r.components ? ` [${r.components.join(', ')}]` : ''}\n  ${r.summary}`).join('\n') + '\n\nCall get with a name for the full entry.';
}

function get({ name = '' }) {
  const key = name.trim().toLowerCase().replace(/^\/flows\//, 'flow/');
  const e = byName.get(key) || byAlias.get(key) || byName.get(`flow/${key}`) || byName.get(`token/${key}`) || byName.get(`asset/${key}`);
  if (!e) {
    const near = search({ query: name, limit: 5 });
    return { error: true, text: `No entry named "${name}".\n\nClosest matches:\n${near}` };
  }
  return { error: false, text: e.text };
}

function list({ kind } = {}) {
  const rows = entries.filter((e) => !kind || e.kind === kind);
  return `${rows.length} entries${kind ? ` of kind ${kind}` : ''}:\n` + rows.map((e) => `- ${e.name}: ${e.title}`).join('\n');
}

const ok = (id, result) => ({ jsonrpc: '2.0', id, result });
const fail = (id, code, message) => ({ jsonrpc: '2.0', id, error: { code, message } });
const textResult = (text, isError = false) => ({ content: [{ type: 'text', text }], ...(isError ? { isError: true } : {}) });

/** Handles one JSON-RPC message. Returns the response, or null for notifications. */
export function handle(msg) {
  if (!msg || msg.jsonrpc !== '2.0' || typeof msg.method !== 'string') return fail(msg?.id ?? null, -32600, 'Invalid request');
  const { id, method, params = {} } = msg;
  const isNotification = id === undefined || id === null;
  try {
    switch (method) {
      case 'initialize': {
        const asked = params.protocolVersion;
        return ok(id, {
          protocolVersion: PROTOCOLS.includes(asked) ? asked : PROTOCOLS[0],
          capabilities: { tools: { listChanged: false } },
          serverInfo: SERVER,
          instructions: INSTRUCTIONS,
        });
      }
      case 'ping':
        return ok(id, {});
      case 'tools/list':
        return ok(id, { tools: TOOLS });
      case 'tools/call': {
        const args = params.arguments || {};
        if (params.name === 'search') return ok(id, textResult(search(args)));
        if (params.name === 'get') {
          const r = get(args);
          return ok(id, textResult(r.text, r.error));
        }
        if (params.name === 'list') return ok(id, textResult(list(args)));
        return fail(id, -32602, `Unknown tool: ${params.name}`);
      }
      default:
        if (isNotification) return null; // notifications/initialized, notifications/cancelled…
        return fail(id, -32601, `Method not found: ${method}`);
    }
  } catch (err) {
    return fail(id ?? null, -32603, String(err?.message || err));
  }
}

/** Handles a single message or a JSON-RPC batch. Returns null when nothing needs a reply. */
export function handlePayload(payload) {
  if (Array.isArray(payload)) {
    const out = payload.map(handle).filter(Boolean);
    return out.length ? out : null;
  }
  return handle(payload);
}

export const meta = { ...SERVER, entries: entries.length };
