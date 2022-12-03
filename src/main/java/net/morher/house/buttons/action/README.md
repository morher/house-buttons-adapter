# Button actions

YAML was not made for programming. To keep configuring button actions as simple as possible the root configuration object is a menu of commands.
Only one command should be defined for each action. A command is selected by configuring it.

Conditional actions are used by some commands, like `firstMatch` to branch execution base on a condition.


## Commands

### First match
Given a set of conditional actions, perform the first one where the condition is met. This works like a if-elseif-else.

```yaml
# Select command
firstMatch:
 # First conditional action
 - condition:            # if…
      light:             # …light is…
         power: Off      # …off…
   action:               # …then…
    - light:             # …set lights…
         power: On       # …on…
         brightness: 127 # … with 127 brightness…
         effect: 'Auto'  # … and effect Auto.

 # Second conditional action (no condition)
 - action:               # Otherwise…
    - light:             # …set lights…
         power: Off      # …off
```
See conditions futher down.

### Light
Sends a light command to all the selected lights. The lights can be selected by listing them directly in `lamps`. If no list is given, the inputs default lamps are used instead.

```yaml
light:
   lamps:
    - room: Living room
      name: Dining table
   power: On
   brightness: 127
   effect: 'Auto'
```

### Switch
Send a switch command to all the selected switches. The switches can be selected by listing them directly in `switches`. If no list is given, the inputs default switches are used instead.

```yaml
switch:
   switches:             # Select switches…
    - room: Kitchen      
      name: Mixer
   power: On             # … and turn it/them on
```


## Conditions

### All of
Requires all the listed conditions to be met.

```yaml
allOf:                   # Both must be true:
 - switch:               # The input default switches…
      power: On          # … are all on
 - light:                # And the input default Lamps…
      effect: 'Jungle'   # … are all set to use the effect Jungle
```

### Any of
Requires at least one of the listed conditions to be met.

```yaml
allOf:                   # Either of these, or both, must be true:
 - switch:               # The input default switches…
      power: On          # … are all on
 - light:                # And the input default Lamps…
      brightness: 127    # … are all set to brightness level 127
```

### Light state
Check the currently reported state of a light.

Keep in mind that a command action will send the command through MQTT to the responsible adapter. The adapter can then do with the command as it pleases and optionally update the state and send back to the state topic. The state will thus most likely take some time after the last command action.

```yaml
light:                   # Checks that the light state conforms to the following:
   power: On             # The light is on…
   brightness: 127       # … and set to brightness level 127…
   effect: Fairyland     # … and effect Fairyland.
```
Aspects of the light state not specified are not checked.

### Not
Inverts the inner condition.

```yaml
not:                     # Because the condition is reversed…
   switch:               # …input default switches…
      power: On          # … cannot all be On
```

### Switch state
Check the currently reported state of a switch.

Keep in mind that a command action will send the command through MQTT to the responsible adapter. The adapter can then do with the command as it pleases and optionally update the state and send back to the state topic. The state will thus most likely take some time after the last command action.

```yaml
light:                   # Checks that the switch state is:
   power: Off            # The switch is off
```
