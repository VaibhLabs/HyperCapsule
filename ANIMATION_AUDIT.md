# HyperCapsule Animation Audit & Specifications

**Application:** HyperCapsule 1.00 Stable  
**Developer:** VaibhLabs  
**Framework:** Jetpack Compose Core Motion Architecture  

---

## 1. Animation System Overview

The animation system utilizes physically based spring dynamics and hardware-accelerated transitions to produce organic feedback without blocking state dispatchers.

---

## 2. Component-Level Motion Specifications

### 2.1 Spring Toggle Switch (`HyperCapsuleSwitch`)
- **Physics Spec:**
  - Thumb Offset: `spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow)`
  - Travel: `2.dp` <-> `22.dp`
  - Track Color: `spring(stiffness = Spring.StiffnessMediumLow)` (Fades between surface `#1B1B22` and primary red `#FF454F`)
  - Specular Rim Border: Animates from `#34343D` to `#FF454F.copy(alpha = 0.6f)`
  - Haptic Pulse: Fires synchronously on press (`HapticFeedbackType.LongPress`).

### 2.2 Dynamic Expandable Disclosures (`AnimatedVisibility`)
- **Physics Spec:**
  - Enter: `expandVertically(spring(stiffness = Spring.StiffnessMediumLow)) + fadeIn(tween(180))`
  - Exit: `shrinkVertically(spring(stiffness = Spring.StiffnessMediumLow)) + fadeOut(tween(140))`
- **Usage:**
  - Auto-hide duration slider in Island Behavior
  - Heads-up duration slider in Island Behavior
  - Priority configuration action in Strategy settings

### 2.3 Micro-Tactile Press (`tactilePress`)
- **Physics Spec:**
  - Scale Factor: `0.98f` on touch down, spring rebound on touch up.
  - Spec: `HyperCapsuleMotion.TactileSpring` (`dampingRatio = 0.78f, stiffness = 400f`).
- **Usage:** All cards, action buttons, and top bar icons.

### 2.4 Carousel Pagers & Lazy Rows
- **Physics Spec:** Standard bounded `HorizontalPager` with indicator dot animation (`16dp` capsule vs `6dp` circle).
- **Smooth Fling:** High-velocity flick-to-snap using default Compose pager fling physics with zero overdraw leakage.
