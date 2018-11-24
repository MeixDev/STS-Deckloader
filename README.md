# Slay the Spire Mod - DeckLoader

## Introduction ##
This is a mod for [**Slay the Spire**](https://store.steampowered.com/app/646570/Slay_the_Spire/). It adds the possibility to load premade builds, to allow the creation of custom challenges.

## Usage ##

* This mod currently relies on BaseMod Console. You can change the keybind used to open it in BaseMod's configuration. \
It adds 2 new commands, `deckload` and `deckbackup`. As you probably guessed, the first one is used to load a build from a file, while the second one is used to save your current build to a file.

* `deckbackup output` will save your current build to {gamedir}/builds/output.txt.
* `deckload output` will attempt to replace your build with the one contained in {gamedir}/builds/output.txt
* `dumpcontent output` will dump all the cards and relics loaded by your game to {gamedir}/builds/output.txt.\
It's absolutely not perfect, especially with the raw descriptions, but could help if you want to see which cards could be interesting for the deck you're building.

## Credits ##

Yeah the will probably be something here someday. Check out other mods on ModTheSpire's Wiki. They're all super cool. And everything is compatible with Deckloader, as long as the person loading the build got the mods needed for both Relics & Cards !

## See Also ##

Dependences:
   * [ModTheSpire](https://github.com/kiooeht/ModTheSpire) : extenal mod loader for Slay The Spire
   * [BaseMod](https://github.com/daviscook477/BaseMod) : the API mod which also provides a dev console

## Known Issues ##

* It was kinda hard to patch BaseMod's DevConsole to add my own things inside. As such, there is no AutoComplete and every single of your commands used with the mod will result in an "invalid command" answer, in addition of the real output.\
I don't think I can do anything for it now. I'm not quite satisfied with having to use BaseMod's console, and I may try to create a real menu or something for it !

## What's next ? ##

* Changing the life pool of your character. Allowing you to start with, for example, 12HP out of 45.
* Making the deckload able to optionally load a specific room, to allow for "Puzzle Fights", where you have to battle a specific enemy, elite or boss with what the build gave you.
* Some way to make the build contain a character, an ascension level or custom modifiers. I'm not quite sure it is possible atm.
* Making the decklist able to contain "already initialized cards", like those cards who get buffed permanently for the game through specific actions. Same with already initialized relics.
* ?.. I'm open to ideas ! 

## File structure ##

```$xslt
[Cards]
{card id} • {amount of times it was upgraded}
{card id} • {amount of times it was upgraded}
{card id} • {amount of times it was upgraded}
(...)
[Relics]
{relic id}
{relic id}
(...)
```
