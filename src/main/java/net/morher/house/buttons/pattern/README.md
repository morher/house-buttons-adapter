# Button patterns

The button presses and releases are translated into events, based on timing between the state changes.

There are four triggers:

| Trigger | Notation | Description                                                                    |
|---------|----------|--------------------------------------------------------------------------------|
| Press   | \        | The moment the button is pressed down.                                         |
| Hold    | _        | The button has been held for a given time (800ms by default) since last event. |
| Release | /        | The moment the button is released.                                             |
| End     | !        | The button has been release more than a given time (200ms by default).         |

Each trigger result in an event. Do also note that hold will trigger repeatedly. Each new trigger will be appended to the pattern and the event will be sent.
A short click on the button will thus result in three events:
- First an event notifying that the button was pressed - "`\`"
- Second an event notifying that the button was first pressed then released - "`\/`"
- Third when 200ms has passed an event notifying that the pattern has ended - "`\/!`"

To make common pattern more readable, there are som shorthands:
- A short click can be shortened to a dot, so "`\/`" can be shortened to "`.`", and "`\/\/`" to "`..`".
- When a button is held, the preceding press is implied, so "`\_`" can be shortened to "`_`".
- Holding after a short click can similarly be shortened from "`\/\_`" to "`._`"
- The end of a hold pattern can also be abbreviated from "`_/!`" to "`_!`"

Notice that there's a difference between "`.`" and "`.!`". The former can continue into another click or a hold, while the last specifies that the pattern must have ended. In case of a single click the first event will occur about 200ms before the last.

## Example usage

### Double tap for full brightness
Listen for "`..`" to enable full brightness.

### 3 step brightness
In this example we have a single momentary switch controlling the roof-light of a kids bedroom. In the morning we might want to turn on the lights gradualy.
We can use the "`.!`" pattern to toggle the lights normally, then set up three brightness levels set by "`_`", "`__`" and "`___`". Since none of these are terminated, they will react one after another as the button is held down.

### Morse code
Or maybe that's not such a great idea...
