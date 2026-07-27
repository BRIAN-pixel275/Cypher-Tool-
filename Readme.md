# CypherTool

A command-line Java tool for encrypting and decrypting messages using **ROT13**, **Atbash**, and **Caesar** ciphers — with robust, loop-until-valid input handling and a clean `exit` command available at every prompt.

```
$ java CypherTool.java
Welcome to the Cypher Tool!

Select operation:
1. Encrypt
2. Decrypt
$> 1

Select cypher:
1. ROT13
2. Atbash
3. Caesar (custom shift)
$> 1

Enter the message:
$> Hello, kood//!

Encrypted message (ROT13):
Uryyb, xbbq//!
```

---

## Features

- **Two operations** — encrypt or decrypt, chosen up front.
- **Three ciphers**:
  - `ROT13` — a fixed Caesar shift of 13.
  - `Atbash` — mirrors the alphabet (A↔Z, B↔Y, …).
  - `Caesar` — a configurable shift from 1–25.
- **Robust input validation** — invalid menu choices, empty messages, and bad shift values are rejected with clear feedback and re-prompted, never crash the program.
- **`exit` from anywhere** — typing `exit` at any prompt quits cleanly. Typing the literal word `exit` as your *message* asks for confirmation instead of silently discarding it.
- **Non-alphabetic characters are preserved** — spaces, punctuation, and digits pass through every cipher unchanged; only letters are transformed, and case is preserved.
- **Graceful shutdown on closed input** — if the input stream ends unexpectedly (e.g. piped input running out), the program exits cleanly instead of throwing an exception.

---

## Project structure

```
.
├── CypherTool.java   # All program logic: input handling, ciphers, main loop
├── Main.java          # Thin entry point that delegates to CypherTool.main()
└── README.md
```

---

## Getting started

### Prerequisites

- Java 11 or later (for single-file source-code execution).
- Java 17+ recommended (the code uses `switch` expressions).

### Run it

No separate compile step needed:

```
java CypherTool.java
```

Or compile and run in the traditional two-step way:

```
javac CypherTool.java Main.java
java Main
```

---

## How it works

The program is built around one rule: **`getInput()` is the only place that ever touches raw, unvalidated strings.** By the time it returns a non-null `InputData` object, every field on it is guaranteed valid — `main()` never re-checks anything.

```
print welcome
  │
  ▼
┌─────────────────────────────┐
│         getInput()          │◄─── loops back here after
│  collects & validates one   │     every processed message
│         full request        │
└─────────────┬───────────────┘
              │ (null if "exit" was typed anywhere)
              ▼
      encrypt() / decrypt()
              │
              ▼
        print the result
```

`getInput()` itself is four small steps, each following the same shape:

```
readOperation() → readCypher() → [readShift() if Caesar] → readMessage()
```

And each of those four methods repeats the same validation loop:

```
while (true) {
    print prompt
    read a line (EOF-safe — a closed input stream is treated like "exit")
    trim it, check for "exit"
    if valid → return it
    else → print feedback, loop again
}
```

### The cipher engine

ROT13 and Caesar are the same operation under the hood — ROT13 is just Caesar with a fixed shift of 13 — so both call a single shared helper:

```java
shiftAlphabetic(s, shift)
```

Atbash is mathematically different (a mirror, not a shift), so it has its own function. Decryption is nearly free for two of the three ciphers, because they are their own mathematical inverses:

| Cipher | Decrypt is... |
|---|---|
| ROT13 | The same call again — shifting by 13 twice = shifting by 26 = no change |
| Atbash | The same call again — mirroring twice returns the original |
| Caesar | `shiftAlphabetic(s, 26 - shift)` — the complement cancels the original shift |

---

## Known edge cases handled

| Scenario | Behavior |
|---|---|
| User types `exit` as their actual message | Asks for confirmation once — type `exit` again to quit, or type anything else and *that* becomes the message. |
| Input stream closes without `exit` (e.g. piped input runs out) | Exits cleanly with `"Goodbye!"` instead of an uncaught `NoSuchElementException`. |
| Invalid menu choice, empty message, or out-of-range shift | Clear feedback is printed and the same prompt repeats. |

---

## Design decisions at a glance

- **Enums over raw strings** — user input is converted to `Operation` and `Cypher` enums immediately, so nothing downstream compares against magic strings, and the compiler enforces exhaustive handling in every `switch`.
- **One validation-loop shape, reused four times** — `readOperation()`, `readCypher()`, `readShift()`, and `readMessage()` all follow the identical prompt → read → trim → check-exit → validate → loop-or-return pattern.
- **`null` as a single "stop" signal** — both "user typed exit" and "input stream closed" collapse to the same `null`, so no method needs special-case logic for either.
- **Confirmation over guessing** — when "exit" the message collides with `exit` the command, the program asks rather than silently picking one.

---

## Possible future enhancements

- Extract a `Cypher` interface so adding a new cipher means writing one class instead of touching the enum, two `switch` statements, and two methods.
- Add a fourth cipher (e.g. Vigenère) reusing `shiftAlphabetic()` per character with a repeating key.
- Add JUnit tests asserting `decrypt(encrypt(s)) == s` for all ciphers, plus regression tests for the input-validation edge cases.
- A config-driven cipher registry, so ciphers can register themselves instead of requiring manual menu/switch updates.