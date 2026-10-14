# Product

<!-- impeccable:product-schema 1 -->

## Platform

web

## Users

GameShelf serves players who want one independent place to discover games, keep a personal library, and exchange useful opinions without depending on a commercial storefront account.

## Product Purpose

GameShelf combines a community-maintained game catalogue with personal library tracking and structured player feedback. Success means a signed-in player can add a missing game, save it as Wishlist, Playing, or Completed, publish a Review, Recommend, or Critique post, and discuss other players' posts in one coherent flow.

## Positioning

The catalogue is maintained by its own community rather than imported from Steam or framed as a game-design tool. Catalogue contribution, personal tracking, and discussion are parts of the same product.

## Operating Context

Visitors browse and search the catalogue, open a game detail page, and read public feedback. Signed-in players maintain a library, add missing games, publish feedback, vote on useful feedback, and add comments. The implementation is also demonstrated as an INFS3208 Type I cloud project, including containerized services and Kubernetes operations.

## Capabilities and Constraints

- Preserve the Java, MySQL, Redis, Docker, Kubernetes, Nginx, and plain HTML/CSS/JavaScript stack.
- Preserve registration, sign-in, sign-out, catalogue browsing and search, game details, Wishlist/Playing/Completed library states, Review/Recommend/Critique publishing, voting, comments, and the account view.
- Allow authenticated users to add catalogue entries through `POST /api/games`.
- Do not use the Steam API and do not present the product as a game-design platform.
- Keep evidence screenshots local and outside source-control submissions.
- The submitted implementation must support a reliable live demonstration of scaling, load balancing, rolling update, rollback, and failure recovery.

## Brand Commitments

The product name is GameShelf. The interface language is English. The requested character is concise, spacious, and confident, with game content and player actions taking priority over decoration.

## Evidence on Hand

- Course brief and marking criteria under `Individual Project/` outside the repository.
- A two-page proposal at `Individual Project/GameShelf_Proposal.docx` outside the repository.
- Local-only evidence under `Individual Project/evidence`.
- The repository contains Docker Compose and Kubernetes manifests, but final cloud evidence must be captured again near the demonstration.

## Product Principles

- Make the next useful action obvious.
- Keep catalogue contribution and review publishing distinct but adjacent.
- Let game information and player writing carry the interface.
- Show honest loading, empty, validation, and error states.
- Keep the demonstration path short, repeatable, and resilient.

## Accessibility & Inclusion

Maintain keyboard navigation, visible focus, semantic landmarks, readable contrast, responsive layouts, reduced-motion support, form labels, and live status messages.
