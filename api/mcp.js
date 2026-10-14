// Hosted MCP endpoint (Streamable HTTP, stateless): POST JSON-RPC to https://<site>/mcp.
// Connect from Claude, Cursor or any MCP client with: { "type": "http", "url": "https://equal-ds.vercel.app/mcp" }

import { handlePayload, meta } from './_core.js';

const CORS = {
  'Access-Control-Allow-Origin': '*',
  'Access-Control-Allow-Methods': 'POST, GET, OPTIONS',
  'Access-Control-Allow-Headers': 'Content-Type, Accept, Mcp-Session-Id, Mcp-Protocol-Version',
};

export default async function handler(req, res) {
  for (const [k, v] of Object.entries(CORS)) res.setHeader(k, v);

  if (req.method === 'OPTIONS') return res.status(204).end();

  if (req.method === 'GET') {
    // No server-initiated stream: this server only answers requests.
    const wantsStream = String(req.headers.accept || '').includes('text/event-stream');
    if (wantsStream) return res.status(405).setHeader('Allow', 'POST').end();
    return res.status(200).json({ ...meta, transport: 'streamable-http (POST JSON-RPC to this URL)', tools: ['search', 'get', 'list'] });
  }

  if (req.method !== 'POST') return res.status(405).setHeader('Allow', 'GET, POST, OPTIONS').end();

  let body = req.body;
  if (typeof body === 'string') {
    try {
      body = JSON.parse(body);
    } catch {
      return res.status(400).json({ jsonrpc: '2.0', id: null, error: { code: -32700, message: 'Parse error' } });
    }
  }
  const reply = handlePayload(body);
  if (reply === null) return res.status(202).end();
  return res.status(200).json(reply);
}
