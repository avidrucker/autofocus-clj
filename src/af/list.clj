(ns af.list
  (:require
   ;;[af.data :as d]
   [af.utils :as u]
   [clojure.string :as str]))


(def list-api-design
  ;; TODO: convert these items to user stories
  "Q: What are the things that can be done with an AutoFocus list at the 'list level' API?
- append new item to bottom of list
- auto-mark/auto-dot first markable/dottable item (so there is always at least one dotted/marked item OR no markable/dottable items)
- mark/dot item at index n (when reviewing one's list for the purpose of comparing / prioritizing)
- re-mark bottom-most dotted item as done after 'focus' session")

;; TODO: write a function that creates to-do list items as-is (without a t-index)
;; TODO: write a function that takes a created to-do list item without a t-index, adds a t-index key-value pair to it, and stick its onto the bottom of a to-do list
;; TODO: extract from the af.list namespace a separate af.item namespace as you see fit to


;; TODO: add one or more descriptive/illustrative emoji to convey as clearly and concrete as you can what each chunk of code does
;; ⌚
(defn t-next [current-list]
  ;; TODO: write a dcstring that clearly conveys what t-next is for
  ;; TODO: write a test that confirms that the first item in a list has a t-index of 0, the 2nd item a t-index of 1, etc..
  (count current-list))

;; TODO: Write a test that confirms that after creating new items that the list t-index values return back true from (distinct?) See: ClojureDocs: distinct?

#_(defn set-item-ready!
  ;; DERPRECATED, use instead `set-item-to-status`
  ;;  originally called: "mark-item-ready!" 
  ;; TODO: refactor this function to (1) be modular to change status to any given input, and (2) take a hashmap input with two named keys, 'input-item' and 'new-status'
  "Marks a todo item as 'ready to be started focusing on'

Note: Take care to note the difference between readiness of an item to start versus the readiness of a list to be reviewed or focused on... TODO: clarify this statement

Q: What are the 'real life' differences between readiness to start vs readiness to review or focus? What are the programmatic (domain logic / algorithmic) differences?"
  [todo-item]
  ;; (println "starting mouse: set-item-ready!") ;; test 
  (assoc todo-item :status :ready))


;; ✅
(defn set-item-to-status
  ;; note: this was originally called `set-item-ready!-2`
  ;; TODO: give this function a doc-string based on that of `set-item-ready!`
  ;; TODO: note in docstring that this function is *not* mutating state, it simply returns back a new item created based off of the input data
  "This function takes in an input item and an input status, and returns back a new item that uses the inputted status and the remaining non-status data from the inputted item.

  In the context of AutoFocus list management, this function is used to update :new items to :ready, and :ready items to :done"
  [{:keys [input-item input-status]}]
  (assoc input-item :status input-status))

;; ❌
(defn- item-mark  
;; helper function which exists for the purpose of simplifying initial function (reading) complexity (to decrease cognitive load) for "stringify-item" function
;; TODO: add more high level 'why' doc-string
  "Using a lookup dictionary (hashmap), we get the associated 'mark' symbol for a todo item's current 'status' (ie. new, ready, or done)" 
  [{:keys [item dict]}]
  ((get item :status) dict))

;; 📛
(defn- item-text [todo-item]
  ;; helper function which exists for the purpose of simplifying initial function (reading) complexity (to decrease cognitive load) for "stringify-item" function
  ;; TODO: add doc-string
  (get todo-item :text))

;; 🔤
(defn stringify-item
  ;; TODO: relocate to new item namespace
  ;; TODO: rename this function to be `stringify-item-for-cli`
  "converts an item hashmap into a printable list-item string
Notes: This function,
- uses composition to simplify exterior function readability
- maintains a consistent function API with built-in flexibility by using one hashmap where its keys may change over development (hashmap associative destructuring), rather than locking the function into any fixed number of arguments (i.e. this technique avoids PLOP)"
  ;; -  [ ] Q: Can format and/or string interpolation be used below to increase readability, or is the current code ideal as-is?
  ;; -  [ ] TODO: Move design notes out of doc-string into either a markdown cell or, perhaps, design-notes hashmap 
  [{:keys [item dict] :as input-data}]
  (str "- [" (item-mark input-data) "] " (item-text item)))
;; 🆕
(defn create-new-item-data
  ;; TODO: relocate this to item namespace
  ;; TODO: consider refactoring this function to *not* be designed for optionality... Q: What are the pros for, cons against, costs/risks/tradeoffs?
  "A function to create new items from task text (required) and status (optional)

required keyword argument input-text
optional keyword argument input-status
  valid statuses are :new, :ready, or :done

  Note: t-index data is not considered part of a default new to-do item, because a t-index denotes the order that an item has been added to a list - therefor a t-index is only assigned to an item once it has been added to a to-do list" 
  [{:keys [input-text input-status]}]
  {:text input-text
   :status (if input-status input-status :new)})

;; TODO: Test create-new-item function w/ just task text input where the output item data status is expected to be "new" (because there are already marked items in this list, and this item to be added will not end up being 'auto-marked' as ready)
;; TODO: Test create-new-item function w/ just task text input where the output item data status is expected to be "ready" (because this is the first item being added to a list)

;; 🆙
(defn update-list
  ;; TODO: relocate to af.modes namespace
  ;; TODO: implement stubs !!!!
  "A function which dispatches based on an action keyword (and is a pure function) to update a user's to-do list as a result of a new item addition, list reviewing, or list focusing. Note: This function may run more than once in order to update as needed for automarking, duplicating, or other purposes... TODO: Assess Q: Does this make the code harder to understand/read?

- append new item to bottom of list[1]
  - auto-mark/auto-dot first markable/dottable item as 'ready' (so there is always at least one dotted/marked item OR no markable/dottable items)
  - mark/dot item as 'ready' at index n (when reviewing one's list for the purpose of comparing / prioritizing)
  - re-mark bottom-most dotted item as 'done' after 'focus' session

[1] update-list takes a new item, to leave the item creation itself to a dedicated item creation function, which in turn leaves text input to an impure IO function" 
  [{:keys [action input-list new-item-data target-index]}] 
  (condp = action
    :append-new (conj input-list (conj
                                  ;; TODO: confirm that naive conj'ing is sufficient, rather than comparing with the highest index item - this may be more relevant for serialization/deserialization
                                  {:t-index (count input-list)}
                                  new-item-data)) 
    :set-automarkable 2 ;;; TODO: implement stub
    :set-nth-ready 3 ;;; TODO: implement stub
    :set-focused-complete 4 ;;; TODO: implement stub
    ))


;; TODO: Implement the auto-marking/auto-dotting of the first added item immediately after adding it
 ;; TODO: Test that auto-marking works on the first item added to an empty list
  ;; TODO: Test that auto-marking works on a new item added to a list that has only 0 items in it of status 'done'  (in other words:  "Implement auto-marking that occurs after adding a new item to the list (such as the first item to the list, or the next item added after all the previous items were marked complete, or on a new page)")

;; 🔍 
(defn has-any-of-status
  [{:keys [input-list input-status]}]
  (let [item-statuses (map :status input-list)]
    (> (count (filter #(= % input-status) item-statuses)) 0)))

;; 🏁
(defn auto-markable-list?-2
  "A list is 'auto-markable' if there are new items and no ready items."
  [{:keys [input-list]}]
  (let [item-statuses    (map :status input-list)
        ;; has-new          (some {:new true} item-statuses)
        ;;has-no-ready     (not-any? :ready (map #(get % :status) input-list))
        ;; has-new-2        (> (count (filter #(= % :new) item-statuses)) 0)
        has-new-3?       (has-any-of-status {:input-list input-list
                                             :input-status :new})
        ;; has-ready        (> (count (filter #(= % :ready) item-statuses)) 0)
        has-ready-3?     (has-any-of-status {:input-list input-list
                                             :input-status :ready})
        ;; is-markable?     (if (and has-new-2 (not has-ready)) true false)
        is-markable-3?   (and has-new-3? (not has-ready-3?))

        _                (println ["item-statuses: " item-statuses
                                   ;; "\nhas-new: " has-new
                                   ;; "\nhas-no-ready: " has-no-ready
                                   ;; "\nhas-new-2: " has-new-2
                                   "\nhas-new-3?: " has-new-3?
                                   ;; "\nhas-ready: " has-ready
                                   "\nhas-ready-3?: " has-ready-3?
                                   ;; "\nis-markable?: " is-markable?
                                   "\nis-markable-3?: " is-markable-3?])
        ]
    is-markable-3?))

(comment
  ;; TODO: convert this comment block into a test block
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; REPL driven development testing
  (true?
   (auto-markable-list?-2 {:input-list  [{:t-index 0, :text "a", :status :new}]}))

  #_(filter #(= (:status %) :new) [{:text "z" :status :new}])
  (println "---------------------")

  (false? (auto-markable-list?-2 {:input-list
                                  [{:t-index 0, :text "b", :status :ready}
                                   {:t-index 1, :text "c", :status :new}
                                   {:t-index 2, :text "d", :status :new}]}
                                 ))

  (false? (auto-markable-list?-2
           {:input-list
            [{:t-index 0, :text "e", :status :done}
             {:t-index 1, :text "f", :status :ready}
             {:t-index 2, :text "g", :status :ready}]
            }
           ))

  (true? (auto-markable-list?-2 {:input-list
                                 [{:t-index 0, :text "h", :status :done}
                                  {:t-index 1, :text "i", :status :done}
                                  {:t-index 2, :text "j", :status :new}]}))
;; TESTING END
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  )

(defn first-index-of-attr [{:keys [input-list target-attr]}]
  ((u/find-first
    #(= (% :status) target-attr)
    input-list) :t-index))

;; 🔢
(defn index-of-first-new-item-in-list
  ;; note: this function was originally named `auto-markable-index-1`
  "Returns the index of the top-most new status item in a list.
  If no index is found, then this function will return nil."
    ;; TODO: refactor  (write a version-2) to use  declarative style
    ;; TODO: determine whether the first found item's index is always guarenteed to be the index number
  [{:keys [input-list]}]
  (let [has-new?       (has-any-of-status {:input-list input-list
                                           :input-status :new})]
    (when has-new?
      (let [first-found-index (first-index-of-attr {:input-list input-list :target-attr :new})
            _ (println ["First found index of new items: " first-found-index])]
        first-found-index))))

(comment
  ;; TODO: convert this comment into a test block
  (index-of-first-new-item-in-list
   {:input-list
    [{:t-index 0, :text "e", :status :done}
     {:t-index 1, :text "f", :status :ready}
     {:t-index 2, :text "g", :status :ready}]})

  (index-of-first-new-item-in-list
   {:input-list
    [{:t-index 0, :text "b", :status :ready}
     {:t-index 1, :text "c", :status :new}
     {:t-index 2, :text "d", :status :new}]}
   )
  )

;; 📖
(defn set-nth-item-in-list-to-status
   ;; note: this was called set-nth-ready-1
   ;; DONE: utilize this function in conjunction (i.e. compose) with a `set-item-to-status` function which simply takes one item and one status and returns back a new item with the inputted status and the remaining data of the inputted item
   ;; TODO: retire this function as deprecated OR refactor, bc it fails silently when malformed/out-of-bounds index inputs are entered
   ;; IN-PROGRESS: refactor this function to be modular and take any input-status
  "Takes in an args-hashmap with three named key-value pair arguments,
  an input-list, an index of an item to 'modify', and the target item's (soon to be) new status.
  Returns back todo-list with the nth index element item's status set to the inputted status.
  Note: This function does not check for out-of-bounds errors/exceptions."
  [{:keys [input-list n-index input-status]}]
    ;;DONE: update this line to use `set-item-to-status` function instead of `set-item-ready!`
  (let [new-item (set-item-to-status {:input-item  (get input-list n-index)
                                      :input-status input-status})
        new-list (assoc input-list n-index new-item)
         ;;_ (println ["starting frog: set-nth-ready-in-list/n"
         ;;"new-item: " new-item 
         ;;"assoc-result: " assoc-result]) 
        ]
     ;; TODO: remove in-bounds check from here, this should be a separate check elsewhere  (ie. move to the domain context boundary) 
     ;; (if (in-bounds-inclusive? {:valid-floor 1
     ;; :valid-max (dec (count input-list))
     ;; :input-n n-index})
    new-list
     ;; input-list ;; else, return the list as-is  (b/c n-index is out-of-bounds)
    ))

;;;; TODO: implement the following function stubs
;; automark-list
;; is-automarkable-list?
;; mark-first-new-item-in-list
;; index-of-first-new-item-in-list


;; 🪄
;; TODO: test to confirm that this function works as desired
;; TODO: fix bug where it appears that all newly added items are getting marked as `:ready`
(defn set-1st-new-item-in-list-to-ready
  ;; original name: `set-topmost-new-item-in-list-to-ready!`
  ;; DONE: rename to `set-first-new-item-to-ready!` 
  ;; DONE: fix the doc-string here to clearly indicate that there is no conditional logic happening here
  "This function always sets the topmost new item's status to 'ready'.
  Therefore, this function is to be called if and only if, a list is auto-markable."
  [{:keys [input-list]}]
  (let [new-list
        (set-nth-item-in-list-to-status
         {:input-list input-list
          :n-index (index-of-first-new-item-in-list {:input-list input-list})
          :input-status :ready})]
    new-list))


;; 🪥
(defn conditionally-automark-list
  "If a list is determined to be automarkable, the topmost new item is set to ready. Otherwise, returns the list as is.

  Note: The auto-markable item has the index-key closest to zero (lowest integer value) and a status of 'new'.
  "
  ;; original: "auto-mark-first-markable-item!"
  ;; 2nd name: "auto-mark-1st-markable-in-list!"
  ;; other names: dot-first-dottable-item, mark-first-markable-item
  [{:keys [input-list]}]
  (let [auto-markable (auto-markable-list?-2 {:input-list input-list})]
    (if auto-markable
      (set-1st-new-item-in-list-to-ready {:input-list input-list})
      input-list)))


(defn add-item-to-list
  ;; TODO: refactor via separation principle  (ie. as possible, make one function that makes items, and make one function that adds items to a list, and compose them in that way) 
  ;; TODO: update to use the single hashmap argument A LA [{:keys [input-list]}]
  ;; action input-list new-item-data
  "function that creates the new-item-data, then the new-item-tx-data, and then 'transacts' (on?) the to-do items collection 'database'"
  [{:keys [input-text target-list]}]
  (let [;; data for a soon-to-be-made item
        data-for-new-item
        (create-new-item-data
         ;; example input-text: (first (af.demo/d-text-lists :tiny))
         {:input-text input-text
          :input-status :new}) ;; TODO: double-check that setting this here is appropriate and effective for both new items, duplicate items, as well as any other adding items to list scenarios that utilize this function
        ;; the necessary list transaction data to create a new item

        tx-data-to-add-new-item
        {:action :append-new
         ;; example target-list: @maria.user/list-1
         :input-list target-list
         :new-item-data data-for-new-item
         ;; note: adding new items does not require a target index bc new items are always appended to the end/bottom/back of the list
         }
        
        new-list                  (update-list tx-data-to-add-new-item)
        _                         (println ["new list with newly added item: " new-list])
        _                         (println ["new list is auto-markable: "
                                            (auto-markable-list?-2 new-list)])
        
        auto-marked-new-list
        ;; note: it would be incorrect to call set-topmost-new-item here bc new items aren't guarenteed to be automarkable
        (conditionally-automark-list {:input-list new-list})

        _                        (println ["auto-marked new-list: " auto-marked-new-list])

        ]

    (vec auto-marked-new-list)))


;; TODO: Demonstrate adding new items to a list using a pure function that takes no external (global) state (references to mutate)

;; TODO: Implement auto-dotting logic for first-dottable-item


#_(defn auto-markable-list?-1
    ;; TODO: deprecate this function in favor of auto-markable-list?-2
    ;; TODO: add clear & meaningful rationale as to why deprecating this function is effective, and for what purpose/desired end-result
    [{:keys [input-list]}]
    (cond
     (zero? (count input-list)) false ;; empty lists are not auto-markable ;; TODO: make a test for this
     (pos? (count (filter #(= :ready (:status %)) (vals input-list)))) false ;; lists with ready items are not auto-markable ;; TODO: make a test for this
     (zero? (count (filter #(= :new (:status %)) (vals input-list)))) false ;; lists with no new items are not auto-markable ;; TODO: make a test for this
     :default true ;; Q: Which is preferred in the Clojure community, & which is considered more 'idiomatic' and why? : `:default true`, `:else true`, or just `true`?
     ))


(defn stringify-list
  ;; TODO: move to utils namespace  (most specifically the mini rendering namespace) 
  [{:keys [input-list marks-dict]}]
  (str/join "\n" (map #(stringify-item
                                   {:item %
                                    :dict marks-dict})
                                 input-list)))


;; TODO: implement the logic to transition between ("list manipulation") modes
;; TODO: Implement the auto-marking/auto-dotting of the first added item immediately after adding it!!!
;; TODO: Test that auto-marking works on the first item added to an empty list

(defn is-focusable-list?
  "A list is a focusable list if it contains any `:ready` items."
  [{:keys [input-list]}]
  ;; Q: Are `pos?` and `#(> % 0)` equivalent?
  (> (count (filter #(= (:status %) :ready) input-list)) 0))

(defn last-of-status-from-list
  "Returns the last element in the list with the target status.
  If no items with the target status are found, `nil` is returned."
  [{:keys [input-list target-status]}]
    (last (filter #(= (:status %) target-status) input-list)))

(defn conduct-focus-on-list!
  [{:keys [input-list]}]
  ;; 1. find bottom-most dotted  (status 'ready') item
  ;; 2. update item in list  (ie. create a new list with item replaced) to have a status of 'done'
  ;; TODO: generalize last-ready-item-index to use for review comparison
  (if (is-focusable-list? {:input-list input-list})
    ;; if input-list is focusable, we focus on the last ready item...
    (let [index-of-item-to-focus-on
          (get (last-of-status-from-list {:input-list input-list
                                          :target-status :ready}) :t-index)

          new-list              (set-nth-item-in-list-to-status
                                 {:input-list input-list
                                  :n-index index-of-item-to-focus-on
                                  :input-status :done})

        ;; after focusing on a list, we must auto-mark again, just in case that the item that was just completed was the last `:ready` item
          automarked-new-list   (conditionally-automark-list
                                 {:input-list new-list})]
    ;;;; TODO: implement focus stub
    ;; index-of-item-to-focus-on
      automarked-new-list)
    ;; ... else, we return the list as-is
    (do
      (println "List is not focusable, returning list as-is...")
      input-list)))

(conduct-focus-on-list!
   {:input-list
    [{:t-index 0, :text "b", :status :ready}
     {:t-index 1, :text "c", :status :new}
     {:t-index 2, :text "d", :status :new}]})


;; TODO: Test that auto-marking correctly marks on the next (2nd) item added to a list that had only 1 item in it of status 'done'
;; TODO: Implement auto-marking that occurs after adding a new item to the list (such as the first item to the list, or the next item added after all the previous items were marked complete, or on a new page)
;; TODO: Experiment taking in user input via a controlled Hiccup/Reagent component


