---
name: GameShelf
description: A quiet editorial game catalogue and personal player library.
colors:
  warm-black: "#11100e"
  warm-black-raised: "#171612"
  panel: "#1d1b17"
  panel-hover: "#24211c"
  rule: "#3d382f"
  rule-strong: "#5b5244"
  muted-text: "#aaa294"
  bone-text: "#f4efe5"
  amber: "#e8b45d"
  amber-bright: "#f4c978"
  amber-soft: "#bd8d43"
  danger: "#f09a87"
  placeholder: "#958d81"
  label-text: "#d8d1c5"
  review-text: "#d0c9be"
  comment-text: "#d7d0c4"
  cover-lime: "#bfd96b"
  cover-violet: "#c4a4d4"
  cover-cyan: "#7cbfc0"
  cover-rose: "#dca2ae"
  cover-ice: "#afd1df"
typography:
  display:
    fontFamily: '"Aptos", "Segoe UI Variable Display", "Segoe UI", sans-serif'
    fontSize: "clamp(3.2rem, 6.3vw, 5.9rem)"
    fontWeight: 620
    lineHeight: 0.94
    letterSpacing: "-0.04em"
  headline:
    fontFamily: '"Aptos", "Segoe UI Variable Display", "Segoe UI", sans-serif'
    fontSize: "clamp(2rem, 3.5vw, 3.7rem)"
    fontWeight: 590
    lineHeight: 1
    letterSpacing: "-0.038em"
  body:
    fontFamily: '"Aptos", "Segoe UI Variable Display", "Segoe UI", sans-serif'
    fontSize: "1rem"
    fontWeight: 400
    lineHeight: 1.5
  label:
    fontFamily: '"Aptos", "Segoe UI Variable Display", "Segoe UI", sans-serif'
    fontSize: "0.78rem"
    fontWeight: 650
  title:
    fontFamily: '"Aptos", "Segoe UI Variable Display", "Segoe UI", sans-serif'
    fontSize: "1.2rem"
    fontWeight: 610
    lineHeight: 1.15
  data:
    fontFamily: '"Cascadia Mono", Consolas, monospace'
    fontSize: "0.72rem"
    fontWeight: 400
rounded:
  action-sm: "7px"
  control: "8px"
  inset: "10px"
  panel-mobile: "12px"
  surface: "14px"
spacing:
  compact: "0.65rem"
  base: "1rem"
  card: "1.3rem"
  panel: "2rem"
  layout: "3rem"
components:
  button-primary:
    backgroundColor: "{colors.amber}"
    textColor: "#19150d"
    rounded: "{rounded.control}"
    padding: "0 1.15rem"
    height: "3rem"
  button-secondary:
    backgroundColor: "transparent"
    textColor: "{colors.bone-text}"
    rounded: "{rounded.control}"
    padding: "0 1.15rem"
    height: "3rem"
  input:
    backgroundColor: "{colors.warm-black-raised}"
    textColor: "{colors.bone-text}"
    rounded: "{rounded.control}"
    padding: "0 0.9rem"
    height: "3rem"
  game-card:
    backgroundColor: "{colors.panel}"
    textColor: "{colors.bone-text}"
    rounded: "{rounded.surface}"
    padding: "1.3rem"
---

# Design System: GameShelf

## Overview

**Creative North Star: "The Quiet Game Almanac"**

GameShelf feels like a carefully edited player's almanac: warm, dark and composed, with the catalogue and player writing carrying the experience. Its confidence comes from oversized type, generous empty space, precise rules and one restrained amber voice rather than decorative gaming effects.

The system is editorial rather than storefront-like. Wide asymmetric compositions, sticky section introductions, compact data labels and deliberately sparse interactions make each page feel curated while preserving clear task flows for browsing, saving and contributing.

**Key Characteristics:**
- Warm-black fields, bone-white reading text and sparing amber emphasis.
- Broad editorial type, short headline measures and comfortable body copy.
- A dense twelve-column catalogue rhythm on wide screens that resolves to one column.
- Thin rules and restrained elevation instead of ornamental chrome.
- Small, directional motion with an intentional reduced-motion alternative.

## Colors

The palette is a warm near-black neutral system with one amber accent family. Pastel cover tones are reserved for game artwork.

- **Shelf Amber** (`#e8b45d`) marks primary actions, emphasized words, the brand mark and selection.
- **Bright Amber** (`#f4c978`) is the interaction state for hover and keyboard focus.
- **Muted Amber** (`#bd8d43`) supports quieter metadata, borders and field focus.
- **Warm Black** (`#11100e`) is the page canvas; **Raised Warm Black** (`#171612`) separates fields and discussions.
- **Panel Brown-Black** (`#1d1b17`) defines cards and panels; **Panel Hover** (`#24211c`) is the active surface.
- **Bone Text** (`#f4efe5`) is the primary reading color; **Muted Stone** (`#aaa294`) supports secondary copy.
- **Rule** (`#3d382f`) and **Strong Rule** (`#5b5244`) create structure with one-pixel dividers.
- **Danger Coral** (`#f09a87`) is reserved for errors.

**The One Amber Voice Rule.** Amber identifies meaning or action; it does not cover large page surfaces.

**The Cover Exception Rule.** Lime, violet, cyan, rose and ice belong to generated cover art, not to navigation, forms or general interface chrome.

## Typography

Display and body text use Aptos with Segoe UI fallbacks. Cascadia Mono with Consolas is limited to counts, dates and status metadata.

- **Display:** 620 weight, `clamp(3.2rem, 6.3vw, 5.9rem)`, 0.94 line-height and `-0.04em` tracking.
- **Headline:** 590 weight, `clamp(2rem, 3.5vw, 3.7rem)` and 1.0 line-height.
- **Title:** 610 weight at `1.2rem` for games and reviews.
- **Body:** `1rem`; editorial copy uses a 1.7 line-height and stays within 58-70ch.
- **Label:** compact, 650 weight labels with restrained tracking.

**The Short Headline Rule.** Keep display copy concise enough to retain the established 12ch silhouette.

**The Mono-for-Facts Rule.** Use the data face for counts, dates and status metadata, never for prose.

## Layout

The global width is `min(1320px, calc(100% - 3rem))`, becoming `min(100% - 2rem, 1320px)` below 640px. Catalogue cards use a dense twelve-column grid with 1rem gaps and an asymmetric 7/5/7 featured cadence. At 900px all major layouts collapse to one column; at 560px game cards move from split artwork and copy to a vertical composition.

Primary editorial sections pair a narrower introduction with a wider content region. Section intervals stay generous at roughly 7-9rem on wide screens and 5-7rem on small screens.

**The Breathing-Room Rule.** Preserve large section intervals and short text measures; do not fill empty regions with utility panels or decoration.

## Elevation & Depth

The system is flat by default and uses tonal layering, one-pixel rules and sparse ambient shadows. Resting cards use `0 18px 46px rgba(0,0,0,.16)`; hover raises them with `0 26px 60px rgba(0,0,0,.26)`. Form panels use `0 26px 70px rgba(0,0,0,.24)`.

**The Flat-by-Default Rule.** Dividers and tonal shifts establish structure first; shadows are reserved for raised surfaces and interaction states.

## Shapes

Standard fields and buttons use 8px corners, discussion insets use 10px and major cards and panels use 14px. Cover artwork may include one outlined circle as an internal graphic motif, while the interface remains rectilinear.

**The Quiet Radius Rule.** Stay within the established 7-14px vocabulary and avoid pill-shaped containers.

## Components

- **Buttons:** 3rem minimum height. Primary actions use Shelf Amber and dark ink; secondary actions are transparent with a Strong Rule border. Hover lifts by 2px, active returns by 1px, and keyboard focus uses a 2px Bright Amber outline.
- **Tabs:** transparent labels share one bottom rule. The active label becomes Bone Text and reveals a one-pixel amber underline. Arrow keys move between tabs.
- **Cards:** catalogue cards use Panel Brown-Black, 14px clipping and tonal hover elevation. Internal copy padding is 1.3rem.
- **Inputs:** Raised Warm Black fill, one-pixel Rule border, 8px corners and amber caret. Focus changes the border to Muted Amber and raises the surface.
- **Navigation:** compact muted labels become Bone Text on hover or active state, with a left-origin amber underline. Mobile navigation scrolls horizontally.
- **Game covers:** a solid selectable cover tone, dark initials, one outlined circle and one horizontal rule. They are graphic and typographic, never photographic.
- **Reviews and library rows:** one-pixel rules separate content; they do not become independent floating cards.

## Do's and Don'ts

### Do
- Preserve the warm-black, bone-white and amber hierarchy.
- Let generous spacing, large type and one-pixel rules create the editorial rhythm.
- Keep body copy within the established 58-70ch reading widths.
- Use cover-tone colors only inside game artwork.
- Retain visible keyboard focus, semantic state and reduced-motion behavior.

### Don't
- Introduce neon gradients, glassmorphism or gaming-dashboard effects.
- Turn every section into a rounded, shadowed card.
- Use amber as a large background wash or generic decoration.
- Replace deliberate empty space with dense widgets.
- Add pill-shaped controls or inflate the corner-radius vocabulary.
