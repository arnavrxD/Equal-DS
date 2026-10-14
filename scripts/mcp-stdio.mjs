#!/usr/bin/env node
// Local MCP server over stdio, for tools that run a command instead of calling a URL:
//   claude mcp add equal-ds -- node /path/to/equal-ds/scripts/mcp-stdio.mjs
// Same tools and data as the hosted /mcp endpoint (api/_core.js + api/_index.json).

import { createInterface } from 'node:readline';
import { handlePayload } from '../api/_core.js';

const rl = createInterface({ input: process.stdin });
rl.on('line', (line) => {
  if (!line.trim()) return;
  let msg;
  try {
    msg = JSON.parse(line);
  } catch {
    process.stdout.write(JSON.stringify({ jsonrpc: '2.0', id: null, error: { code: -32700, message: 'Parse error' } }) + '\n');
    return;
  }
  const reply = handlePayload(msg);
  if (reply !== null) process.stdout.write(JSON.stringify(reply) + '\n');
});
