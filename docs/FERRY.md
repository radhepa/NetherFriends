# The lava ferry

The Magmari carry outsiders across the lava sea from one Mooring to another on strider barges. Never one of the
other four peoples: only you.

## Taking the ferry

1. **Ask the Ferrier.** Every Mooring has one, on its ferry dock (a T-shaped lander at the end of a long pier).
   Talk to them and press **Ferry** in the dock along the bottom of the window.
2. **Choose a Mooring.** The first time anyone asks a Ferrier, they work out which other Moorings are within a
   strider's walk (up to 1,600 blocks, the nearest four). Each is listed with its distance, its direction and the
   fare: warped fungus (the striders eat the fare), about one for every 160 blocks, 2 to 8. A Ferrier whose
   Ember-friend (friendship level 4) you are takes you for nothing.
3. **Board the barge.** The fare is paid and a barge ties up at the dock: a raft of basalt slabs lashed with chain,
   with a bench, a lantern at the bow and a magma brazier at the stern, towed by a strider. Right-click it to
   climb aboard. It waits two minutes; if you don't come, it leaves and your fungus is given back.
4. **Cast off.** The Ferrier dabs magma cream on you (fire resistance for the crossing), and wades out beside the
   strider until the open sea, then lets it go on alone and walks home across the lava.
5. **The crossing.** The screen sinks into an ember dark while a few lines of the sea come up; meanwhile the far
   shore loads. Nothing is sailed through for real between the two Moorings, so no chunks in between are
   generated.
6. **Landfall.** The barge comes in from open lava to the far Mooring's dock and sets you down on its lander. The
   strider turns round and wades home with the empty barge.

If you climb off at sea, you're put straight back on ("Sit down! The sea's hungry, and you're crunchy."). If you
log out or the server stops mid-crossing, you're set down safely on a dock: the one you left from if you hadn't
crossed yet, otherwise the one you were going to.

## How it works

- `talk/Ferry.java`: the ferry itself: destinations, fares, booking, the four stages of a crossing (out, crossing,
  in, ashore), and the safety net. Destinations are found by asking the Mooring structure set where its one spot per
  region is, then loading the nearest twelve spots just far enough (structure starts, in the background) to see
  whether a Mooring with a dock really stands there. The answer is kept per Mooring until the server restarts.
- `entity/Barge.java`: the barge. It follows the waypoints it's given at a strider's pace (five blocks a second),
  takes one passenger, can't be hurt, and is never saved. On the client it keeps the game's own strider walking
  ahead of the bow (drawn by the vanilla strider renderer).
- `world/Sea.java`: open lava (lava with room above it), a dock's berth and lander (from the dock building), and a
  way between the berth and open lava about 40 blocks out, toward the far Mooring, found over loaded chunks only.
- `talk/FerryPayload.java` and `client/CrossingOverlay.java`: the crossing's dark screen.
- Lines: `data/netherfriends/ferry/magmari.json` (offers, fares, casting off, the crossing, landfall, the strider
  names), checked by `python tools/check_talk.py`.
- Test and film: `gradlew runClientGameTest -PnetherTests=Ferry` sails from the first Mooring of seed 7 to the
  nearest other one; add `-PnetherFilm` to film it and `python tools/film_ferry.py` to make the film.
