// Sample content for the Profile / Addresses / Instructions flows (copy from the flow designs).
import type { Instruction, PresetTint, SavedAddress } from '../components/Flows';

export const sampleAddresses: SavedAddress[] = [
  { id: 'a1', label: 'Home', line1: 'House no. 4, Tony Street', line2: 'Financial District', city: 'Hyderabad', pincode: '500032' },
  { id: 'a2', label: 'Work', line1: 'House no. 4, Tony Street', line2: 'Financial District', city: 'Hyderabad', pincode: '500032' },
  { id: 'a3', label: 'Arnav’s Place', line1: 'House no. 4, Tony Street', line2: 'Financial District', city: 'Hyderabad', pincode: '500032' },
  { id: 'a4', label: 'Work', line1: 'Flat 4B, Block C, Skyline Towers, Phase 2, HITECH city', line2: 'MG Road, Opposite City Center Mall, Banjara Hills', city: 'Hyderabad', pincode: '500034', direction: 'Near City Center Mall' },
];

export type Preset = { id: string; emoji: string; tint: PresetTint; title: string; description: string; instructions: Instruction[] };

const SCREEN = { emoji: '🛡️', label: 'Screen & Protect' };
const EVERYDAY = { emoji: '📞', label: 'Handle everyday calls' };
const BUSINESS = { emoji: '💼', label: 'Run my business' };

export const instructionLibrary = {
  declineSales: { id: 'decline-sales', title: 'Decline sales, promo spam calls', source: SCREEN, when: 'The caller is selling loan, insurance, credit card, product, or another unsolicited offer', do: 'Politely say I am not interested and end the conversation without discussing the offer' },
  loanEmi: { id: 'loan-emi', title: 'Handle loan, EMI & recovery calls safely', source: BUSINESS, when: 'The caller discusses a loan, EMI overdue balance, recovery, or legal notice', do: 'Note the lender, amount and due date, say I will review and respond and never confirm payment, identity, account details, or negotiate' },
  difficult: { id: 'difficult', title: 'Stay brief and calm with difficult calls', source: EVERYDAY, when: 'The caller is automated, repetitive, abusive or rude', do: 'Stay calm or brief, do not argue, and end politely, if the caller will not state a legitimate purpose' },
  takeMessage: { id: 'take-message', title: 'Take a message & callback request', source: EVERYDAY, when: 'A genuine caller has clear reason to speak with me and I am unavailable', do: 'Collect their name, callback number and message, then say you will pass the message to me' },
  delivery: { id: 'delivery', title: 'Handle delivery and courier calls', source: EVERYDAY, when: 'the caller is delivering food, a parcel, documents, or goods', do: 'share only {landmark}, ask them to wait for {wait}, and never ask for or share an OTP or full address' },
  wrongNumber: { id: 'wrong-number', title: 'Close wrong number calls politely', source: SCREEN, when: 'The caller has the wrong number', do: 'Inform them and close without sharing details' },
} satisfies Record<string, Instruction>;

export const presets: Preset[] = [
  { id: 'screen', emoji: '🛡️', tint: 'green', title: 'Screen & Protect', description: 'Decline spam, hold the line on loan and payments call, close wrong call', instructions: [instructionLibrary.declineSales, instructionLibrary.loanEmi, instructionLibrary.difficult] },
  { id: 'everyday', emoji: '📞', tint: 'blue', title: 'Handle everyday calls', description: 'Take message, guide deliveries, relay to family, start in your language', instructions: [instructionLibrary.takeMessage, instructionLibrary.delivery, instructionLibrary.difficult] },
  { id: 'business', emoji: '💼', tint: 'orange', title: 'Run my business', description: 'Capture enquiries, orders, bookings, job, and supplier calls', instructions: [instructionLibrary.loanEmi, instructionLibrary.takeMessage] },
];

/** Recommended cards → the instruction they set up, and the short preview shown in the chat. */
export const recommendations = [
  { id: 'rec-decline', emoji: '🚫', title: 'Decline sales & spam', description: 'Politely declines sales and insurance calls.', ask: 'Decline Sales & Spam calls', instruction: { ...instructionLibrary.declineSales, title: 'Decline sales calls', when: 'Caller is selling a loan, insurance or product', do: 'Politely decline and end the conversation' } },
  { id: 'rec-message', emoji: '💬', title: 'Take a message', description: 'Takes their name, number and message.', ask: 'Take a message & Callback request', instruction: instructionLibrary.takeMessage },
];

/** Follow-up questions for instructions that need input from the user. */
export const followUps: Record<string, { key: string; question: string }[]> = {
  delivery: [
    { key: 'landmark', question: 'Which landmark should I share with delivery partners?' },
    { key: 'wait', question: 'How long should I ask them to wait before calling again?' },
  ],
};

export const silentCategories = [
  { id: 'loan', label: 'Loan Offers', description: 'AI will mute loan promotional calls' },
  { id: 'card', label: 'Credit Cards', description: 'AI will mute credit card offer calls' },
  { id: 'job', label: 'Job Offers', description: 'Mute promotional calls automatically' },
  { id: 'estate', label: 'Real Estate', description: 'AI will mute property promotional calls' },
  { id: 'others', label: 'Others', description: 'AI will mute promotional calls not listed above' },
];
