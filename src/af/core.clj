(ns af.core)
;; autofocus core library
;; DONE: rename namespace from `autofocus-sketch-nov-11-2022.core` back to `af.core`

;; TODO: implement the logic to transition between app ("main menu") states

(def app-level-api-design
  "Q: What are the things that can be done with AutoFocus at the 'app level' API? (TODO: convert this answer to a user story epic)
- Open the program, close the program, view menu/options to choose an action to interact with the program:
  - Add a new item to today's list
  - Review today's list (if review-able)
  - Focus on focus-able item (if one exists)
  - Read about AutoFocus & Mark Forster
  - Read about the programmer of this app
  - View the Help menu/manual")

(def list-1
  (atom []))

#_(defcell mdc-list-1
  ;;  this implementation is Maria Dot Cloud specific, and uses a MDC specific (r)atom-like data structure to store & update mutable state
  [])

