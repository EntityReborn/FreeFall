#FreeFall#

##Permissions based freefall for minecraft players##

  Allow users to fall with configurable damage!

##Installation##

  Drop into your /plugins/ folder, run the server once to generate the config file, then modify config to taste.

##Commands##
 
  The plugin is fairly self sufficient, but you can use the following commands:
  
  * `/freefall version`: find out what version of freefall you are running.
  * `/freefall reload`: reload the config file.

##Configuration##

  Several options are available:

  * Damage Calculation

    Set any of these to `0` for an infinite fall distance without damage.

    Basically, any simple calculation can be done here, 'damage' would be replaced
with the default damage if this weren't handled, 'distance' would be the distance
fallen.

    * `stand-calculation`: math expression of which the result will be the amount of damage when standing.
    * `sneak-calculation`: same as above while sneaking.

    If the result is less than zero, it will be interpreted as a damage of zero.

  * Messages

    Set the message to `""` to cancel sending a message.

    `%NAME%` here will be replaced with the user's name, and `%DIST%` will be replaced with the distance fell. These messages will only be sent if the user is within the confines of the distance parameters mentioned above.

    Be sure to always double quote these strings, and escape any double quotes used in the message. (IE, `"%NAME% fell %DIST% blocks while standing, but didn't get hurt!"`)

    Messages for when not receiving damage:

    * `stand-console-msg`: message to be sent to the console when standing.
    * `sneak-console-msg`: message to be sent to the console when sneaking.
    * `stand-player-msg`: message to be sent to the player when standing.
    * `sneak-player-msg`: message to be sent to the player when sneaking.

    Messages for when receiving damage. (These also have a `%DAMG%` keyword)

    * `stand-console-msg-damaged`: message to be sent to the console when standing and receiving damage.
    * `sneak-console-msg-damaged`: message to be sent to the console when sneaking and receiving damage.
    * `stand-player-msg-damaged`: message to be sent to the player when standing and receiving damage.
    * `sneak-player-msg-damaged`: message to be sent to the player when sneaking and receiving damage.

##Permissions##

  Permissions affect how the plugin deals with fall damage.

  * Access

    Give users or groups these permissions to allow the plugin to affect their freefall.

    * `freefall.sneak`: allow the user to have their damage re-calculated while sneaking.
    * `freefall.stand`: allow the user to have their damage re-calculated while standing.

  * Bypasses

    You might want to give some players the ability to bypass the limit, allowing them to freefall any distance.

    * `freefall.sneak.bypass`: bypass sneak limits
    * `freefall.stand.bypass`: bypass stand limits

##Credits##

  * Me (`EntityReborn` aka `__import__`) - Code / Implementation
  * Malvagio87 from 10hearts.com minecraft community - Original idea

You can usually find me in `#CommandHelper` on `irc.esper.net`, usually with nick `__import__` or `Masamune`.