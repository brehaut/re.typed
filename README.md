# re.typed

A Clojure library designed to handle the same regular expression opperations as the `re-*`
functions in `clojure.core` but in a way that doesn't result in core.typed forcing you to handle
a massive type union explosion.

## Usage

```clojure
(require '[re.typed :as re])

(re/find re/all #"(ab+)+c" "ZZabbabcZZ") 
;=> ["abbabc" "ab"]
(re/find re/groups #"(ab+)+c" "ZZabbabcZZ") 
;=> ["ab"]
(re/find re/match #"(ab+)+c" "ZZabbabcZZ") 
;=> "abbabc"
(re/find re/found #"(ab+)+c" "ZZabbabcZZ") 
;=> true
```

`re/find`, `re/matches` and `re/seq` correspond to `re-find`, `re-matches` and `re-seq` from core,
but each takes an additional first argument to parameterize how to return the Matchers result.

## Changes

### 0.1.{3,4} ###

 * Update some old type syntax

### 0.1.2 ###

 * Update to core.typed 0.2.53

### 0.1.1 ###

 * Updates to core.typed 0.2.2
 * no longer a snapshot release

## License

Copyright © 2013 Andrew Brehaut, Ambrose Bonnaire-Sergeant

Distributed under the Eclipse Public License, the same as Clojure.
