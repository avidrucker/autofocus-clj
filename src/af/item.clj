(ns af.item)

;; TODO: conduct review on which functions are elevated to
;; non-private status, and how each namespace further exposes
;; the "public" API of each layer of AutoFocus functionality
;; ( item -> list -> AutoFocus state -> app state -> I/O and
;; (de)serialization )

;; TODO: add documentation  (or better yet, code?) on the data
;;       shape of todo-items where :t-index is the unique ID
;; TODO: write down explicit rationale for unique ID: ability
;;       to clone/duplicate & distinguish between different todo-items 
(def item-api-design-notes
  "CURRNENTLY:
   Items in AutoFocus are responsible for remembering/maintaining
   a certain amount of data:
   - The descriptive `:text` describing what the to-do item is.
   - The `:status` of the to-do item, which can be :new, :ready, or :done
      - Items are, by default, created with a status of :new
      - Once an item is deemed ready to do, its status changes from :new to :ready
      - Once an item has been focused on, its status changes from :ready to :done
   - After an item is added to a list, it gains a `:t-index` which indicates
     when it was added to the list.

   EVENTUALLY:
   Items in AutoFocus will remember:
   - when they were made, marked, and completed.
   - whether they were created as originals or as duplicates.

   As a result of the above points, it can be calculated:
   - for any given day which items were completed in that day.
   - how long it took for X items to go from creation to completion")


;; TODO: update this function to use `update` instead of `assoc`
;; ✅
(defn set-item-to-status
  ;; note: this was originally called `set-item-ready!-2`
  "Takes in input item and input status, returns back new item
  that uses inputted status and remaining non-status data from
  inputted item. No state mutation occurs in the process.

  In the context of AutoFocus list management, this function
  is used to update :new items to :ready, and :ready items to :done"
  [{:keys [input-item input-status]}]
  (assoc input-item :status input-status))


;; ❌
(defn- item-mark  
  "Helper function which exists for the purpose of simplifying
  initial function (reading) complexity (to decrease cognitive
  load) for `stringify-item` function

  Using a lookup dictionary/table/map, we get the associated
  'mark' symbol for a todo item's current 'status' (ie. `:new`,
  `:ready`, or `:done`)"
  
  [{:keys [item dict]}]
  ((get item :status) dict))


;; TODO: Move design notes out of doc-string into either a markdown cell or, perhaps, design-notes hashmap 
;; TODO: modify stringify item to handle duplicate items by 
;; conditionally appending text as follows, an example for an item 
;; with the text `go for jog` that has been duplicated twice will 
;; be rendered as `go for jog (dup #2)` and an item that is not a 
;; duplicate will render as `go for jog`
;; 🔤
(defn stringify-item
  "Converts an item map into a printable list-item string

  Design Notes:
  - This function uses composition to simplify caller readability
  - This function also maintains a consistent function API with
  built-in flexibility by using one map whose keys may change
  over development ('hashmap associative destructuring'), rather
  than locking the function into any fixed number of arguments
  (i.e. this technique avoids PLOP)"
  [{:keys [item dict]}] ;; :as input-data ;; (item-mark input-data)
  (str "- [" (item-mark {:item item :dict dict}) "] " (get item :text)))


;; TODO: Test create-new-item function w/ just task text input where
;; the output item data status is expected to be "ready" (because this
;; is the first item being added to a list)

;; TODO: Test create-new-item function w/ just task text input where
;; the output item data status is expected to be "new" (because there
;; are already marked items in this list, and this item to be added
;; will not end up being 'auto-marked' as ready)

;; TODO: Test create-new-item function w/ dup item to confirm that
;; their base text is the same, and that a new key `:dup-number` has
;; been added to dup item


;; IDEA: add `:t-original` key-val pair to duplicate items to indicate
;; which item is the original


;; 🆕
(defn create-new-item-data
  ;; TODO: consider refactoring this function to *not* be designed for optionality...
  ;;     Q: What are the pros for, cons against, costs/risks/tradeoffs?
  ;; TODO: spec this function to indicate required inputs, optional inputs
  ;; Q: What are the tradeoffs for making a multi-arity function that calls itself to fill in "optional" data, rather than supplying data via a conditional expression?
  "A function to create new items from task text (required) and status (optional)
  - required keyword argument input-text
  - optional keyword argument input-status
  - valid statuses are :new, :ready, or :done

  Note: t-index data is not considered part of a default, new to-do item because
  a t-index denotes the order that an item has been added to a list - therefore
  a t-index is only assigned to an item once it has been added to a to-do list" 
  [{:keys [input-text input-status]}]
  {:text input-text
   :status (if input-status input-status :new)})


(defn is-duplicate?
  "if `dup-number` key is found on an item, it is a duplicate item"
  [{:keys [input-item]}]
  (some? (get input-item :dup-number)))


(defn gen-duplicate-item
  "generates a duplicate of the inputted item. Items can be originals or
  duplicates, and duplicates can be duplicated 1 or more times.

  Note: This function uses `is-duplicate?` for the conditional duplication logic"
  [{:keys [input-item]}]
  ;; use `dup-number` key-val to indicate the duplicate number, 
  ;; starting with 1 to indicate an item has been duplicated once
  (if (is-duplicate? {:input-item input-item})
    (update input-item :dup-number inc)
    (assoc input-item :dup-number 1)))


;; BAD IDEA: implement `duplicate-item` with optional append that also looks for a certain sequence of
;;           characters, for example: 'go for jog', once stopped, can be duplicated to create
;;           'go for jog (dup #1)', with the duplicates of the duplicate being 'go for jog (dup #2)',
;;           'go for jog (dup #3)', etc.. --> This is a bad idea because it uses both string
;;           programming and PLOP (place oriented programming) where a keyword and boolean flag would
;;           suffice, and also mixes domains/concerns, where the desired end result is to be able to
;;           tell if an item is a duplicate visually (rendered to the user), as well as programmatically
;;           (via code by the program/programmer).
