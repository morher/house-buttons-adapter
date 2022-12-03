# Buttons adapter
Handles button input like light switches etc.

The adapter takes button inputs through MQTT and [parses the pushes and releases into button events](./src/main/java/net/morher/house/buttons/pattern/). 
Actions can be assigned the different patterns, either directly or through templates.

## Configuration
The adapter supports sharing configuration files with other adapters by using the namespace `buttons`.
Within `inputs` each button is listed, identified by it's MQTT topic. A message with `1` or `on` (not case sensitive) represents a pressed button. Any other value represents a released button. The button can optionally be inverted.

The `input` entry has an optional field `events` that maps button events to a set of actions. The events can also be defined in `templates`. Devices that should be controlled by the actions can be set up inside the action, but for templates to make sense, each input can define a set of default devices. In this way, a template event can trigger a lamp to turn on, and the action can be used by many different inputs without them all turning on the same lamp.

### Example
```yaml
buttons:
   inputs:
    - topic: 'shellies/shelly-kitchen1/input/0'
      templates: [toggle, full, dimmed]
      lamps:
      - room: 'Living room'
        name: 'Dining Table'
      events:
         '._':
          - light:
               power: On
               brightness: 127
               effect: 'Romantic dinner'

   templates:
      toggle:
         events:
            '.!':
             - firstMatch:
                - condition:
                     light:
                        power: Off
                  action:
                   - light:
                        power: On
                        brightness: 127
                        effect: 'Auto'
                - action:
                   - light:
                        power: Off

      dimmed:
         events:
            '_':
             - light:
                  power: On
                  brightness: 10
                  effect: 'Dimmed'

      full:
         events:
            '..!':
             - light:
                  power: On
                  brightness: 255
                  effect: 'Full'



```

In this example we have created one button that controls the dining room lamp. Through templates we get three event mappings.

The first template, `toggle`, handles the single click event. The exclamation point means the event must be ended, so there are no chance of a double click.
This event is a bit complicated. It first checks if the lamp is off, if so it is turned on to about 50% brightness and the effect Auto. Otherwise, the lamp is turned off.

The template `full` provides an event mapping for doubleclicks. The action is not that complex as it simply turns the lights on full. Equally, `dimmed` provides mapping for holding the button down for a short period.

The input itself defines a fourth event mapping for a short click followed by holding the button. 
