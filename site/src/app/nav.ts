import { componentDocs } from '../registry/components';
import type { ComponentGroup } from '../registry/types';

export type NavPage = { path: string; label: string; hint?: string };
export type NavGroup = { label: string; pages: NavPage[] };
export type SectionId = 'docs' | 'components' | 'screens' | 'playground';
export type Section = { id: SectionId; label: string; home: string; groups: NavGroup[] };

const GROUP_ORDER: ComponentGroup[] = ['Actions', 'Inputs', 'Display', 'Navigation', 'Chat', 'Assistant', 'Cards', 'Overlays', 'Lists & rows', 'You tab', 'Monetization', 'Flows'];

export const componentGroups: NavGroup[] = GROUP_ORDER.map((g) => ({
  label: g,
  pages: componentDocs.filter((d) => d.group === g).map((d) => ({ path: `/components/${d.id}`, label: d.name, hint: d.summary })),
})).filter((g) => g.pages.length);

export const sections: Section[] = [
  {
    id: 'docs',
    label: 'Docs',
    home: '/start',
    groups: [
      { label: 'Start', pages: [{ path: '/start', label: 'Getting started', hint: 'Web + Compose setup, token pipeline, rules' }, { path: '/start/claude', label: 'Use with Claude', hint: 'Skill, CLAUDE.md, llms.txt, starter prompts' }] },
      {
        label: 'Foundations',
        pages: [
          { path: '/foundations/colour', label: 'Colour', hint: 'Primitives, alias tokens, gradients' },
          { path: '/foundations/typography', label: 'Typography', hint: '12 DM Sans tokens' },
          { path: '/foundations/layout', label: 'Spacing & elevation', hint: 'dp scale, radius, shadows, motion' },
          { path: '/foundations/icons', label: 'Icons', hint: 'Material Symbols set' },
          { path: '/foundations/emoji', label: 'Action emoji', hint: 'Emoji on action chips' },
          { path: '/foundations/brand', label: 'Brand & illustration', hint: 'Logo, orb, mascot, avatars, categories, wallpaper' },
        ],
      },
      {
        label: 'Reference',
        pages: [
          { path: '/reference/decisions', label: 'Rules & decisions', hint: 'Every rule and why it was decided' },
          { path: '/patterns', label: 'Patterns', hint: 'Threads, selection, confirm-then-undo' },
          { path: '/reference/conflicts', label: 'Spec conflicts', hint: 'Where the designs disagree' },
          { path: '/reference/assets', label: 'Image assets', hint: 'Supplied vs still needed' },
        ],
      },
    ],
  },
  { id: 'components', label: 'Components', home: '/components', groups: [{ label: '', pages: [{ path: '/components', label: 'Overview' }] }, ...componentGroups] },
  {
    id: 'screens',
    label: 'Screens',
    home: '/screens',
    groups: [
      { label: '', pages: [{ path: '/screens', label: 'Overview', hint: 'Every core screen, live' }] },
      {
        label: 'Flows',
        pages: [
          { path: '/flows/profile', label: 'Profile', hint: 'Personal details, Silent Mode, WhatsApp' },
          { path: '/flows/addresses', label: 'Addresses', hint: 'Empty, list, edit, delete' },
          { path: '/flows/instructions', label: 'Instructions', hint: 'Hub, presets, setup chat, active' },
          { path: '/flows/contact-info', label: 'Contact info', hint: 'Header, actions, call history' },
          { path: '/flows/search', label: 'Search', hint: 'Recent, results, messages, empty and offline' },
          { path: '/flows/call-back', label: 'Call Back', hint: 'Suggested questions, dialer hand-off, outgoing call card' },
          { path: '/flows/mute', label: 'Mute', hint: 'Confirm, then the muted card' },
          { path: '/flows/reply-whatsapp', label: 'Reply on WhatsApp', hint: 'Draft, edit in place, send, confirmation' },
          { path: '/flows/save-contact', label: 'Save Contact', hint: 'Review, edit, contact form, permissions' },
          { path: '/flows/error-states', label: 'Error states', hint: 'Generation, offline, voice input' },
          { path: '/flows/global-mya', label: 'Global MYA', hint: 'The assistant sheet from the green orb' },
          { path: '/flows/call-handling', label: 'Call Handling', hint: 'Which calls I answer, who always rings you' },
          { path: '/flows/known-calls', label: 'Known-call notifications', hint: 'Ongoing notification, replies, hand-off' },
          { path: '/flows/monetization', label: 'Monetization', hint: 'Paywall, payment, plan, cancellation' },
          { path: '/flows/monetization-touchpoints', label: 'Monetization touchpoints', hint: 'Plan expired, usage cap, locked actions & recordings' },
          { path: '/flows/paywall-sheet', label: 'Sheet paywall & payment', hint: 'Sheet paywall, exit offer, payment results, processing' },
          { path: '/flows/plan-renewal', label: 'Plan value & renewal', hint: 'Expiry banners, value sheet, renew' },
          { path: '/flows/system-status', label: 'Temporarily unavailable', hint: 'System status card, notify, back on' },
          { path: '/flows/voip', label: 'Call your assistant (vOIP)', hint: 'Call me, mic permission, in-call controls' },
        ],
      },
    ],
  },
  { id: 'playground', label: 'Playground', home: '/prototype', groups: [{ label: '', pages: [{ path: '/prototype', label: 'Clickable prototype', hint: 'The whole app, wired' }] }] },
];

export function sectionFor(path: string): Section | null {
  if (path === '/') return null;
  if (path.startsWith('/components')) return sections[1];
  if (path.startsWith('/screens') || path.startsWith('/flows')) return sections[2];
  if (path.startsWith('/prototype')) return sections[3];
  return sections[0];
}

export const allPages: NavPage[] = [{ path: '/', label: 'Home', hint: 'Overview of the system' }, ...sections.flatMap((s) => s.groups.flatMap((g) => g.pages))];
