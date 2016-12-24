# SheetJson

## Introduction

A music composing application that is configured by JSON files.
Each composition contains a tree of `Player`s, and optionally a config
section.

The layout of a composition is:

```javascript
{
    "config": {
        "bpm": ... // int
        "sample_rate": ..., // int
        "beats_per_bar": ..., // int
        "presets": [...]
    },
    "players" {...}
}
```

## Player Types
### Origin
The origin of a sound wave.

#### Tone
Given a wave function and a note, creates a sound wave.

```javascript
{
    "type": "tone",
    "note": "...",
    "wave_function": "..." // defaults to "sine"
}
```

#### Raw File
Plays a sound wave from an audio file.

```javascript
{
    "type": "raw_file",
    "path": "..."
}
```

### Filter
Takes another `Player` as input, and modifies it's sound wave in some
way
#### Key Activated
Plays the child `Player` when a key is pressed.

```javascript
{
    "type": "key_activated",
    "key": ..., # key code
    "child": {...}
}
```

#### Smooth Key Activated
Plays the child `Player` when a key is pressed. Smooths the child in and
out.

Takes fade in/out function, which says how the child is faded in/out.
Also takes fade in/out time, which is taken in bars.

```javascript
{
    "type": "smooth_key_activated",
    "key": ..., // key code
    "in_function": "...", // defaults to "fade_in"
    "out_function": "...", // defaults to "fade_out"
    "fade_in_time": ..., // double, defaults to 0.25
    "fade_out_time": ..., // double, defaults to 0.25
    "child": {...}
}
```

#### Toggle
Toggles a child `Player` on and off on a key press.

```javascript
{
    "type": "toggle",
    "key": ..., // key code,
    "child": {...}
}
```

#### Randomizer
Distort a child `Player` signal.

```javascript
{
    "type": "randomizer",
    "randomness": ..., // default to 1.2
    "child": {...}
}
```

#### Volume Function
Change the volume of the child `Player` according to a wave function.

```javascript
{
    "type": "volume_function",
    "wave_function": "...", 
    "frequency": ..., // in bars
    "child": {...}
}
```

#### Looper
Loop the wave function of the child `Player`.

```javascript
{
    "type": "looper",
    "bars": ...,
    "child": {...}
}
```

#### Repeater
Repeat the wave function of the child `Player`.

```javascript
{
    "type": "repeater",
    "length": ..., // in bars
    "child": {...}
}
```

### Composite
Takes a collection of `Player`s as input, and outputs a single sound
wave.

#### Combiner
Combines component `Player`'s sound waves into one.

```javascript
{
    "type": "combiner",
    "components": [...]
}
```

#### Keyboard
Assign a key to a selection of component `Player`s, acting like a keyboard.

Takes a preset (see preset section) to make the components out of.

```javascript
{
    "type": "keyboard",
    "preset": "...",
    // either
    "notes": [...], // array of note strings
    // or
    "key": "...",
    "scale": "..."
}
```

#### Riff
Play a riff of `Player`s.

```javascript
{
    "type": "riff",
    "components": [
        // either
        [{ /* player */ }, /* start time */, /* end time */], ...
        // or
        [{ /* player */ }, /* duration */], ...
    ]
}
```

#### Scroller
Use two keys to scroll between component `Player`s.

```javascript
{
    "type": "riff",
    "next": ..., // key code
    "previous": ..., // key code
    "components": [...]
}
```

#### Switcher
Switch between component `Player`s, each having their own key.

```javascript
{
    "type": "switcher",
    "keys": [...], // array of key code
    "components": [...]
}
```
