#FreeFall#

##Permissions based freefall for minecraft players##

Allow users to fall a configurable distance without damage!

---

##Installation##

Drop into your /plugins/ folder, run the server once to generate the config file, then modify to taste.

---

##Configuration##

Several options are available:

###Distance###

Set any of these to 0 for an infinite fall distance.

* `stand-max-distance`: maximum distance a user can freefall while standing.
* `sneak-max-distance`: maximum distance while sneaking (crouching).

###Messages###

Set the message to "" to cancel sending a message.
`%NAME%` here will be replaced with the user's name, and `%DIST%` will be replaced with the distance fell.
These messages will only be sent if the user is within the confines of the distance parameters mentioned above.

Be sure to always double quote these strings, and escape any double quotes used in the message.
(IE, `"%NAME% fell %DIST% blocks while standing, but didn't get hurt!"`)

* `stand-console-msg`: message to be sent to the console when standing.
* `sneak-console-msg`: message to be sent to the console when sneaking.
* `stand-player-msg`: message to be sent to the player when standing.
* `sneak-player-msg`: message to be sent to the player when sneaking.

---

##Permissions##

###Access###
Give users or groups these permissions to affect their freefall.

* `freefall.sneak`: allow the user to painlessly freefall the `sneak-max-distance` distance while sneaking.
* `freefall.stand`: allow the user to painlessly freefall the `stand-max-distance` distance while standing.

###Bypasses###
You might want to give some players the ability to bypass the limit, allowing them to freefall any distance.

* `freefall.sneak.bypass`: bypass sneak limits
* `freefall.stand.bypass`: bypass stand limits

---

##Credits##

* Me (`EntityReborn` aka `__import__`) - Code / Implementation
* Malvagio87 from 10hearts.com - Original idea

You can usually find me in `#CommandHelper` on `irc.esper.net`, usually with nick `__import__` or `Masamune`.