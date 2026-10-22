// Sample content for the monetization screens (Monetization Flow design).
import type { FaqItem, Testimonial } from '../components/Monetization';

export const paywallFaq: FaqItem[] = [
  { q: 'What do I get with Equal Unlimited?', a: 'Unlimited call screenings, full recordings, summaries, and zero daily cap. Your assistant handles every unknown call, no limits.' },
  { q: 'Will Equal screen all types of calls?', a: 'Yes! Delivery, ride pickups, spam, loan calls, Equal handles them all and tells you exactly why they called.' },
  { q: 'Can I cancel my Equal Unlimited plan later?', a: 'Yes, cancel anytime. Your Unlimited access stays active until the end of your billing period.' },
  { q: 'Will I get a refund if I’m not happy with Equal Unlimited?', a: 'We don’t offer refunds, but you can write to our support team and we’ll do our best to help.' },
  { q: 'View Equal Unlimited Fair Usage Policy', a: 'Tap to read the full policy.' },
];

export const testimonials: Testimonial[] = [
  { name: 'keerthana', quote: 'As a freelancer, client calls and spam used to mix together. Now I get proper summaries and live transcript support. I know exactly what each caller wants without wasting time. Very efficient tool' },
  { name: 'Arnav Raj', quote: 'It is a very useful app for screening the unknown calls. I used to get up to 10 spam calls a day. This is an important app for me. This is a must download.' },
  { name: 'Riya Kapoor', quote: 'My parents get loan and insurance calls all day. Equal answers them, and I just read the summary in the evening.' },
];

/** The experiments the Android team runs on the paywall (multiple touchpoints). */
export const paywallSubtitles = {
  unlock: 'Unlock Summaries, Transcripts, Recordings & Unlimited Call Screening',
  get: 'Get Unlimited Summaries, Transcripts, Recordings & Call Screening',
  /** Actions touchpoint. */
  actions: 'Unlock Unlimited Actions, Summaries, Transcripts, Recordings & Call Screening',
  /** Usage cap touchpoint. */
  cap: 'Unlock Unlimited Call Screening, Recordings & Summaries, Recordings & Transcripts.',
} as const;

/** Messages from Equal AI in its own chat (Plan Expired / Usage Cap option 1). */
export const officialNotices = {
  'plan-expired': { preview: 'Your Plan Expired, Renew to get Unlimited', chip: 'Renew Now', title: 'Your Plan Expired', body: 'Renew to unlock unlimited recordings, call summaries, transcripts, and unlimited call screening.', cta: 'Renew Equal AI Unlimited' },
  'recordings-locked': { preview: 'All Your recordings are locked, Unlock unlimited recordings and more with Pro.', chip: 'Get Unlimited', title: 'Your call recordings are locked', body: <>Subscribe to Equal Unlimited to get Unlimited Recordings, AI Call Summaries and Conversations.<br /><br />Trusted by 10 Lakh+ Indians.</>, cta: 'Get Equal AI Unlimited' },
  'usage-cap': { preview: 'Call limit reached, Resets in 3 days', chip: 'Get Unlimited', title: 'Weekly call limit reached, Limit Resets in 3days', body: <>You’ve reached your limit on Equal AI. Subscribe to Equal Unlimited to get Unlimited Recordings, AI Call Summaries and Conversations.<br /><br />Trusted by 10 Lakh+ Indians.</>, cta: 'Get Equal AI Unlimited' },
} as const;
