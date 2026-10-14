import type { ReactNode } from 'react';

export type PropDoc = {
  name: string;
  type: string;
  default?: string;
  description: string;
  required?: boolean;
};

export type ExampleDoc = {
  title: string;
  /** One line under the example title. */
  note?: string;
  /** Live render of the real component. */
  render: () => ReactNode;
  /** The JSX shown in the code tab (hand-written, matches `render`). */
  code: string;
  /** Canvas width in px (defaults to 390, the Android frame width). Use 'wide' for docs-width examples. */
  width?: number | 'wide';
  /** Canvas surface: page background (default) or white surface. */
  surface?: 'primary' | 'surface';
};

export type ComponentGroup = 'Actions' | 'Inputs' | 'Display' | 'Navigation' | 'Chat' | 'Assistant' | 'Cards' | 'Overlays' | 'Lists & rows' | 'You tab' | 'Monetization' | 'Flows';

export type ComponentDoc = {
  /** URL slug, e.g. 'button'. */
  id: string;
  /** Page title, e.g. 'Button'. */
  name: string;
  group: ComponentGroup;
  /** One sentence: what it is and when to use it. */
  summary: string;
  /**
   * Exported React component names this page documents. Used by Trace (hovering a live phone)
   * to map the component under the cursor to this page, so list every exported name.
   */
  components: string[];
  /** Import line, e.g. "import { Button, TextButton } from '@equal/ds';" */
  importLine: string;
  /** Compose equivalents, e.g. ['EqualButton', 'EqualTextButton']. */
  compose: string[];
  /** Section in SPEC.md, e.g. '§19 Buttons' or 'Flows'. */
  spec?: string;
  /** Alias tokens the component reads, e.g. ['button/primary', 'text/on-action']. */
  tokens: string[];
  examples: ExampleDoc[];
  props: { component: string; props: PropDoc[] }[];
  dos: string[];
  donts: string[];
};

/** One visible part of a component, in reading order (Overview → Anatomy). */
export type AnatomyPart = { part: string; description: string; spec?: string };

/** One accessibility topic (Role, Name, Keyboard, Touch target, Contrast, State, Motion), per platform. */
export type A11yRow = { topic: string; web?: string; android?: string };

/** Anatomy + accessibility for a docs page, keyed by doc id in site/src/registry/a11y.json. */
export type ComponentA11y = { anatomy: AnatomyPart[]; accessibility: A11yRow[]; gaps: string[] };
