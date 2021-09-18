MorphDragonExtensions
=====================

_Be a dragon in Minecraft_

Important Stuff
---------------

- **This code has bugs.** Please report issues on the [issue tracker][tracker].

- **Only 1.12.2 right now.** You're, of course, welcome to submit PRs or fork
  your own versions; see COPYING.

- **You need both Morph for and DragonMounts2 for 1.12.2.** I tested this
  source base against:

  - DragonMounts2-1.12.2-1.6.3; which requires:
	
	- llibrary-1.7.20-1.12.2 (yes, I know, the versions are swapped); and

  - Morph-1.12.2-7.2.1; which requires:

	- iChunUtil-1.12.2-7.2.1; but you **need a bugfix version for local files**
	  (at least until the configuration goes to upstream). I maintain [a
	  patched version of iChunUtil][iChunUtil] with the necessary fixes.

- **You'll want to patch the configs in this directory.** In lieu of forking
  Morph, I've just uploaded configs that have sensible defaults for this use
  case. You are welcome to modify them, but note that _your dragon abilities
  won't work_ if you don't at least start from this base.

Usage
-----

Install the mods in the usual way--put a JAR in the `mods` folder. You can get
that from the [releases page][releases], or clone this repository and run
`gradlew build` in a way copacetic to your platform. Then, put the `config

Then, you need to add a dragon morph. In lieu of killing a dragon, you can also
just look at one of the DM2 dragons and execute `/morph give` as Op (or have an
Op execute `/morph give [YourPlayerName]` for you). The various breeds and
genders should register as "variants", as you'd expect, but the thumbnail
renders them as an egg.

Flight is effected as if you had elytra on all the time, but you can jump
("flap") mid-flight. Your breath weapon is the usual key for DM2 (default `R`).
Punching the ground with an empty hand will "claw" or "gouge" the earth, and is
the most efficient way to remove large swaths of blocks as a dragon (helpful,
because you're about 4.9m on a side, so you won't fit into small tunnels).

[iChunUtil]: https://github.com/Grissess/iChunUtil/releases
[releases]: https://github.com/Grissess/MorphDragonExtensions/releases
[tracker]: https://github.com/Grissess/MorphDragonExtensions/issues
