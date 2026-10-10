# What we take from Village Friends: the conversation UI only

Built: `src/client/java/dev/netherfriends/client/TalkScreen.java` (the window), `talk/TalkPayload.java` (both
messages), `talk/Talk.java` (the server side). The rest of this file is the plan it was built from.

Read from the Village Friends graphify graph (`Village-Friends-Minecraft-Mod/graphify-out/graph.json`, built from
origin/main 4317f18, community "Friendship & Pet Screens"). Only the client-side look and layout come across.
Village Friends' dialogue bank (`talk/Talk`, `TalkWorld`, `tools/dialogue/`), friendship levels, routines and
everything server-side stay behind; Nether Friends writes its own.

## The pieces

| Village Friends | File | What it does | In Nether Friends |
|---|---|---|---|
| `FriendshipScreen` | `src/client/java/dev/villagefriends/client/FriendshipScreen.java` | The screen (`extends Screen`). `drawCard` (friendship card), `drawRibbon` (name ribbon), `panel` / `pill` / `frame` (boxes), `heart` (meter icon), `scrollbar`, typed text (`typing`, `advanceTyping`, `layoutTyping`, `restartTyping`, `finishLine`, `visibleLines`), `speaking` (gesture while a line types), `react`, `drawEmote`, `drawLevelUp`, `playUi` (blip/pop sounds), `keyPressed` (1-8, space), `isPauseScreen` false. | Copy the layout and typing logic, new palette constants, `heart` becomes a per-people meter icon (gold nugget / soul flame / ember). |
| `ReplyButton` | same file | Numbered reply bubble. | Same, netherrack bubble with a gold number badge. |
| `DockButton` | same file | Dock button with icon and a count badge. | Same, Nether icons. |
| `FriendshipPayload` (+ `Choice`) | `src/main/java/dev/villagefriends/FriendshipPayload.java` | Server to client: who is talking, the line, the choices (`MAX_CHOICES` 8). | Our own payload with the same shape, our own id. |
| `ActionPayload` | `src/main/java/dev/villagefriends/ActionPayload.java` | Client to server: the button or reply the player pressed. | Our own payload. |
| `ConversationButton` | `src/client/java/dev/villagefriends/client/ConversationButton.java` | Shared button base. | Copy. |

## Palette swap

Village Friends constants (`FriendshipScreen` lines 37-45): `INK 0xFF4E382B`, `MUTED 0xFF8B765C`, `PAPER 0xFFFFF8E9`,
`PAPER_SHADE 0xFFF2E4C9`, `EDGE 0xFF5B3A29`, `EDGE_LIGHT 0xFFB98A5E`, `SAGE 0xFF55753F`, `HEART 0xFFE0567A`.

Netherrack: `PAPER 0xFF6A2B29` (drawn over a netherrack tile at low contrast), `PAPER_SHADE 0xFF55211F`,
`EDGE 0xFF260D0C`, `EDGE_LIGHT 0xFFB35D53`, `INK 0xFFFBEEE2`, `MUTED 0xFFDCB0A2`, status `0xFFF2C14E` (gold),
reply bubble `0xFF7B3431` (hover `0xFF923F3A`), empty meter `0xFF44201D`.

Note: Village Friends uses dark ink on light paper. Here it flips to light ink on dark netherrack, so every
literal colour in the copied draw code (e.g. the `0xFFFFF1F4` panel tint, `0xFFE7DCC8` disabled paper) must be
replaced, not just the named constants.
