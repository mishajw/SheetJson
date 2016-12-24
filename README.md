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

### Composite
Takes a collection of `Player`s as input, and outputs a single sound
wave.
