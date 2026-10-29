// Sample content for the monetization screens (Monetization Flow design).
import type { FaqItem, Testimonial } from '../components/Monetization';

export const paywallFaq: FaqItem[] = [
  { q: 'What do I get with Equal AI Unlimited?', a: 'You get unlimited call screening, call recordings, AI call summaries, and conversations for every call. New features are added through the year.' },
  { q: 'How does membership work?', a: 'You pay for your first month and setup auto-pay with any UPI app like PhonePe, Google Pay or Paytm. It renews every month, and you can cancel anytime.' },
  { q: 'Can I cancel anytime?', a: 'Yes, you can cancel anytime. You keep full access until your renewal date. After that, you won’t be charged again' },
  { q: 'Do you offer refunds?', a: 'Yes. If you’re not happy within 3 days of your first payment, email us at hey@equal.in and we’ll process your refund.' },
  { q: 'Which payment methods are supported?', a: 'UPI only — pay with any UPI app like PhonePe, Google Pay or Paytm, processed securely through Razorpay.' },
  { q: 'What happens to my call data?', a: 'Recordings and transcripts stay encrypted and tied to your account. We never share them with third parties and you can request deletion from Profile → Account Deletion.' },
  { q: 'How do I change my plan?', a: 'Open Profile → Manage Subscription. You can switch between monthly and yearly plans there; changes apply from the next billing cycle.' },
  { q: 'View Equal Unlimited Fair Usage Policy', a: 'Tap to read the full policy.' },
];

/** The FAQ on the Manage Subscription page (curated separately from the paywall's). */
export const manageFaq: FaqItem[] = [
  { q: 'What do I get with Equal AI Unlimited?', a: 'Unlimited call screenings, full recordings, summaries, and zero daily cap. Your assistant handles every unknown call, no limits.' },
  { q: 'Does Equal AI screen all types of calls?', a: 'Yes! Delivery, ride pickups, spam, loan calls, Equal AI handles them all and tells you exactly why they called.' },
  { q: 'Can I cancel my Equal AI Unlimited plan later?', a: 'Yes, cancel anytime. Your Unlimited access stays active until the end of your billing period.' },
  { q: 'Can I get a refund if I’m not happy with Equal AI Unlimited?', a: 'Yes. If you’re not happy within 3 days of your first payment, reach us through our helpline and we’ll process your refund.' },
];

export const testimonials: Testimonial[] = [
  { name: 'Riya S.', quote: 'Equal AI screens every unknown call so I never miss what matters.' },
  { name: 'Chaitanya Sai', quote: 'Equal AI eliminates spam and engages with unknown callers itself.' },
  { name: 'Sanjana Ramakrishna', quote: 'Saves me time and handles unknown calls like a pro.' },
];

/** The experiments the Android team runs on the paywall (multiple touchpoints). */
export const paywallSubtitles = {
  /** Production copy (headline_subtitle). */
  default: 'Get Unlimited Recordings, AI Call Summaries and Conversations. Trusted by 10 Lakh+ Indians.',
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
  'usage-cap': { preview: 'Call limit reached, Resets in 3 days', chip: 'Get Unlimited', title: 'You’ve used your free calls this week', body: <>Your weekly limit resets on Monday.<br /><br />Upgrade to keep Equal AI answering your calls.</>, cta: 'Get Equal AI Unlimited' },
} as const;
