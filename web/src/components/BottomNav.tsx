import { Icon } from '../foundations/Icon';
import { Text } from '../foundations/Text';
import { CountBadge } from './Badge';
import { EqualOrb, ProfileAvatar, type PlanTier, type ProfileAvatarVariant } from './Avatar';
import './BottomNav.css';

export type NavTab = 'chats' | 'you';

export type BottomNavProps = {
  active: NavTab;
  onChange?: (tab: NavTab) => void;
  unread?: number;
  /** Show the assistant FAB beside the pill. */
  showFab?: boolean;
  onFabClick?: () => void;
  /** Which profile avatar the "You" tab shows. */
  avatar?: ProfileAvatarVariant;
  /** Plan tier of the user: paid shows the gold-ringed avatar. */
  tier?: PlanTier;
};

/** Floating pill navigation + assistant FAB (Component ref #4). Labels are Caption; the count is Badge. */
export function BottomNav({ active, onChange, unread = 0, showFab = true, onFabClick, avatar = 'male', tier = 'free' }: BottomNavProps) {
  return (
    <div className="eq-nav">
      <nav className="eq-nav__pill" aria-label="Main">
        <button type="button" className={`eq-reset-btn eq-nav__item ${active === 'chats' ? 'is-active' : ''}`} aria-current={active === 'chats' ? 'page' : undefined} onClick={() => onChange?.('chats')}>
          <span className="eq-nav__icon">
            <Icon name={active === 'chats' ? 'chat' : 'chat-outline'} size={24} />
            {unread > 0 && (
              <span className="eq-nav__count">
                <CountBadge count={unread} size={16} ring />
              </span>
            )}
          </span>
          <Text variant="caption" color="none">Chats</Text>
        </button>
        <button type="button" className={`eq-reset-btn eq-nav__item ${active === 'you' ? 'is-active' : ''}`} aria-current={active === 'you' ? 'page' : undefined} onClick={() => onChange?.('you')}>
          <span className="eq-nav__icon">
            <span style={{ display: 'block', width: 24, height: 24, marginTop: -3 }}><ProfileAvatar variant={avatar} size={24} tier={tier} label="" /></span>
          </span>
          <Text variant="caption" color="none">You</Text>
        </button>
      </nav>
      {showFab && <Fab onClick={onFabClick} />}
    </div>
  );
}

/** Assistant FAB — the Equal orb (light/dark asset, carries its own glow). */
export function Fab({ onClick, label = 'Talk to Equal AI', size = 58 }: { onClick?: () => void; label?: string; size?: number }) {
  return (
    <button type="button" aria-label={label} onClick={onClick} className="eq-reset-btn eq-pressable eq-fab" style={{ width: size, height: size }}>
      <EqualOrb size={size} label="" />
    </button>
  );
}
