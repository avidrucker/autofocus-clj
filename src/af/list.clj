(ns af.list
  (:require
   [af.utils :as u]
   [af.item :as i]
   [clojure.string :as s]))


(def list-api-design
  ;; TODO: convert these items to user stories
  "Q: What are the things that can be done with an AutoFocus list
  at the 'list level' API?
  - append new item to bottom of list
  - auto-mark/auto-dot first markable/dottable item (so there is
    always at least one dotted/marked item OR no markable/dottable items)
  - mark/dot item at index n (when reviewing one's list for the
    purpose of comparing / prioritizing)
  - mark bottom-most dotted item as `:done` after 'focus' session")


;; DONE: Implement auto-dotting logic for first-dottable-item
;; DONE: write a function that creates to-do list items as-is (without a t-index)
;; DONE: write a function that takes a created to-do list item without a t-index, adds a t-index key-value pair to it, and stick its onto the bottom of a to-do list
;; DONE: extract from the af.list namespace a separate af.item namespace as you see fit

;;;; TODO: implement the following function stubs
;; automark-list
;; is-automarkable-list?
;; mark-first-new-item-in-list
;; index-of-first-new-item-in-list


;; ⌚
(defn t-next
  "serves up what the upcoming t-index is to be for the next added to-do list item"
  [{:keys [target-list]}]
  ;; TODO: write a test that confirms that the first item in a list has a t-index of 0, the 2nd item a t-index of 1, etc..
  (count target-list))

;; TODO: Write a test that confirms that after creating new items that the list t-index values return back true from (distinct?) See: ClojureDocs: distinct?


;; 🆙
(defn update-list
  ;; TODO: relocate to af.modes namespace
  ;; TODO: implement stubs
  "A function which dispatches based on an action keyword (and is a pure function) to update a user's to-do list as a result of a new item addition, list reviewing, or list focusing. Note: This function may run more than once in order to update as needed for automarking, duplicating, or other purposes... TODO: Assess Q: Does this make the code harder to understand/read?

- append new item to bottom of list[1]
  - auto-mark/auto-dot first markable/dottable item as 'ready' (so there is always at least one dotted/marked item OR no markable/dottable items)
  - mark/dot item as 'ready' at index n (when reviewing one's list for the purpose of comparing / prioritizing)
  - re-mark bottom-most dotted item as 'done' after 'focus' session

[1] update-list takes a new item, to leave the item creation itself to a dedicated item creation function, which in turn leaves text input to an impure IO function"
  [{:keys [action input-list new-item-data ;; target-index ;; uncomment when you need index data reliant behavior
           ]}]
  (condp = action
    :append-new (conj input-list (conj
                                  ;; TODO: confirm that naive conj'ing is sufficient, rather than comparing with the highest index item - this may be more relevant for serialization/deserialization
                                  {:t-index (t-next {:target-list input-list})}
                                  new-item-data))
    :set-automarkable 2 ;;; TODO: implement stub
    :set-nth-ready 3 ;;; TODO: implement stub
    :set-focused-complete 4 ;;; TODO: implement stub
    ))


;; DONE: Implement the auto-marking/auto-dotting of the first added item immediately after adding it
 ;; DONE: Test that auto-marking works on the first item added to an empty list
  ;; TODO: Test that auto-marking works on a new item added to a list that has only 0 items in it of status 'done'  (in other words:  "Implement auto-marking that occurs after adding a new item to the list (such as the first item to the list, or the next item added after all the previous items were marked complete, or on a new page)")


;; PURE FUNC
;; TODO: refactor to use `pos` instead of `> 0`
;; 🔍 
(defn- has-any-of-status?
  "checks a list to see if it contains items with a given status"
  [{:keys [input-list input-status]}]
  (let [item-statuses (map :status input-list)]
    (> (count (filter #(= % input-status) item-statuses)) 0)))


;; 🏁
(defn- is-auto-markable-list?
  "A list is 'auto-markable' if there are new items and no ready items."
  [{:keys [input-list]}]
  (let [has-new-3?       (has-any-of-status? {:input-list input-list
                                              :input-status :new})
        has-ready-3?     (has-any-of-status? {:input-list input-list
                                              :input-status :ready})
        is-markable-3?   (and has-new-3? (not has-ready-3?))]
    is-markable-3?))


(comment
  ;; TODO: convert this comment block into a test block
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; REPL driven development testing
  (true?
   (is-auto-markable-list? {:input-list  [{:t-index 0, :text "a", :status :new}]}))

  #_(filter #(= (:status %) :new) [{:text "z" :status :new}])
  (println "---------------------")

  (false? (is-auto-markable-list? {:input-list
                                  [{:t-index 0, :text "b", :status :ready}
                                   {:t-index 1, :text "c", :status :new}
                                   {:t-index 2, :text "d", :status :new}]}
                                 ))

  (false? (is-auto-markable-list?
           {:input-list
            [{:t-index 0, :text "e", :status :done}
             {:t-index 1, :text "f", :status :ready}
             {:t-index 2, :text "g", :status :ready}]
            }
           ))

  (true? (is-auto-markable-list? {:input-list
                                 [{:t-index 0, :text "h", :status :done}
                                  {:t-index 1, :text "i", :status :done}
                                  {:t-index 2, :text "j", :status :new}]}))
;; TESTING END
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  )


;; 1️⃣
(defn- first-index-of-attr
  "Returns the list index of the item found with matching attribute.
  The attribute to match can be text, status, etc."
  [{:keys [input-list target-attr]}]
  ((u/find-first
    #(= (% :status) target-attr)
    input-list) :t-index))


;; 🔢
(defn- index-of-first-new-item-in-list
  "Returns the index of the top-most new status item in a list.
  If no index is found, then this function will return nil.

  Note: This function was originally named `auto-markable-index-1`"
  ;; TODO: determine whether the first found item's index is always guarenteed to be the item's index number itself
;; TODO: determine that this function returns 0 correctly for the first newly added item at precisely the right time (this should be true only once the first item has been added to a list, not before because the list would have been empty, nor just after, because the list is to be auto-marked/auto-dotted immediately after adding the first item) 
  [{:keys [input-list]}]
  (let [has-new?       (has-any-of-status? {:input-list input-list
                                           :input-status :new})]
    (when has-new?
      (let [first-found-index (first-index-of-attr
                               {:input-list input-list
                                :target-attr :new})
            ]
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


;; TODO: retire this function as deprecated OR refactor, bc it fails silently when malformed/out-of-bounds index inputs are entered
;; 📖
(defn- set-nth-item-in-list-to-status
  "Takes in an args-hashmap with three named key-value pair arguments:
  - an input-list
  - an index of an item to 'modify'
  - the target item's (soon to be) new status
  Returns back a new todo-list with the nth index element item's status set to the inputted status

  Notes:
  - This function does not currently check or acount for for out-of-bounds errors/exceptions.
  - This was called `set-nth-ready-1`
  "
  [{:keys [input-list n-index input-status]}]
  (let [new-item
        (i/set-item-to-status
         {:input-item  (get input-list n-index)
          :input-status input-status})

        new-list
        (assoc input-list n-index new-item)]
     ;; TODO: remove in-bounds check from here, this should be a separate check elsewhere  (ie. move to the domain context boundary) 
     ;; (if (in-bounds-inclusive? {:valid-floor 1
     ;; :valid-max (dec (count input-list))
     ;; :input-n n-index})
    new-list
     ;; input-list ;; else, return the list as-is  (b/c n-index is out-of-bounds)
    ))

;; TODO: test to confirm that this function works as desired
;; TODO: fix bug where it appears that all newly added items are getting marked as `:ready` !!!!
;; TODO: add spec clause to confirm that, when this function is called, it is only called on auto-markable lists
;; 🪄
(defn- set-1st-new-item-in-list-to-ready
  "This function always sets the topmost new item's status to 'ready'.
  It currently performs NO checks for data validity.
  Therefore, this function is to be called if and only if, a list is auto-markable.

  Previous names: `set-topmost-new-item-in-list-to-ready!`
                  and `set-first-new-item-to-ready`"
  [{:keys [input-list]}]
  (let [;; _        (println "...setting 1st new item in list to ready...") ;; println debugging
        new-list (set-nth-item-in-list-to-status
                  {:input-list input-list
                   :n-index (index-of-first-new-item-in-list {:input-list input-list})
                   :input-status :ready})]
    new-list))


;; 🪥
(defn- conditionally-automark-list
  "If a list is determined to be automarkable, the topmost `:new` item
  is set to `:ready`. Otherwise, returns the list as is.

  Note: The auto-markable item has the index-key closest to zero
  (lowest integer value) and a status of `:new`.

  Previous Names: `auto-mark-first-markable-item!`,
  `auto-mark-1st-markable-in-list!`, `dot-first-dottable-item`,
  `mark-first-markable-item`"
  [{:keys [input-list]}]
  (let [;; _ (println "...conditionally automarking list...") ;; println debugging    
        auto-markable (is-auto-markable-list? {:input-list input-list})]
    (if auto-markable
      (set-1st-new-item-in-list-to-ready {:input-list input-list})
      input-list)))


;; TODO: refactor list logic so that way the update-list function is called in the next namespace, rather than here, similar to `conduct-focus-on-list`
;; ➕
(defn add-item-to-list
  ;; TODO: refactor via separation principle  (ie. as possible, make one function that makes items, and make one function that adds items to a list, and compose them in that way) 
  "This function takes in an input item and target list, and then 'adds' said item to the list by making a brand new list with the input item appended on to the end.

  Note: This function is part the list namespace's 'public' API, and is meant to be used by other namespaces.
  
  Work-In-Progress language: 'transacts' (on?) the to-do items collection 'database' (i.e. the to-do list)"
  [{:keys [input-item target-list]}]
  (let [;; TODO: disable println debugging
        ;;_  (println "...adding item to list...") ;; debugging

        ;; TODO: double-check that setting this here is appropriate and effective for both new items, duplicate items, as well as any other adding items to list scenarios that utilize this function
        ;; note: adding new items does not require a target index bc new items are always appended to the end/bottom/back of the list - TODO: confirm where an index is explicitly necessary to update a to-do list
        new-list
        (update-list
         ;; new item "transaction" data ("tx-data")
         {:action :append-new
          :new-item-data input-item
          :input-list target-list})

        ;; TODO: disable println debugging
        ;; _ (println ["new list with newly added item: " new-list])
        ;; _ (println ["new list is auto-markable: "
        ;;            (auto-markable-list?-2 new-list)])

        ;; note: it would be incorrect to call set-topmost-new-item here bc new items aren't guarenteed to be automarkable
        auto-marked-new-list
        (conditionally-automark-list {:input-list new-list})

        ;; println debugging
        ;; _ (println ["auto-marked new-list: " auto-marked-new-list])
        ]

    (vec auto-marked-new-list)))


(defn stringify-list
  ;; TODO: move to utils namespace  (most specifically the mini rendering namespace) 
  [{:keys [input-list marks-dict]}]
  (s/join "\n"
          (map #(i/stringify-item
                 {:item %
                  :dict marks-dict})
               input-list)))


;; TODO: implement the logic to transition between ("list manipulation") modes
;; TODO: Implement the auto-marking/auto-dotting of the first added item immediately after adding it!!!
;; TODO: Test that auto-marking works on the first item added to an empty list


(defn filter-by-status
  [{:keys [input-list input-status]}]
  (filter
   #(= (:status %) input-status)
   input-list))


(defn is-focusable-list?
  "A list is a focusable list if it contains any `:ready` items."
  [{:keys [input-list]}]
  (pos? (count (filter-by-status {:input-list input-list
                                  :input-status :ready}))))


(defn last-of-status-from-list
  "Returns the last element in the list with the target status.
  If no items with the target status are found, `nil` is returned."
  [{:keys [input-list target-status]}]
  (last
   (filter-by-status {:input-list input-list
                      :input-status target-status})))


(defn conduct-focus-on-list
  [{:keys [input-list]}]
  ;; 1. find bottom-most dotted  (status 'ready') item
  ;; 2. update item in list  (ie. create a new list with item replaced) to have a status of 'done'
  ;; TODO: generalize last-ready-item-index to use for review comparison
  (if (is-focusable-list? {:input-list input-list})
    ;; if input-list is focusable, we focus on the last ready item...
    (let [_                     (println "...focusing on list...") ;; debugging
          index-of-item-to-focus-on
          (get (last-of-status-from-list {:input-list input-list
                                          :target-status :ready}) :t-index)

          new-list              (set-nth-item-in-list-to-status
                                 {:input-list input-list
                                  :n-index index-of-item-to-focus-on
                                  :input-status :done})

        ;; after focusing on a list, we must auto-mark again, just in case that the item that was just completed was the last `:ready` item
          automarked-new-list   (conditionally-automark-list
                                 {:input-list new-list})
          _                     (println ["post-focus list will be:"
                                          automarked-new-list])]
    ;;;; TODO: implement focus stub
    ;; index-of-item-to-focus-on
      automarked-new-list)
    ;; ... else, we return the list as-is
    (do
      (println "List is not focusable, returning list as-is...")
      input-list)))

(conduct-focus-on-list
   {:input-list
    [{:t-index 0, :text "b", :status :ready}
     {:t-index 1, :text "c", :status :new}
     {:t-index 2, :text "d", :status :new}]})


;; TODO: Test that auto-marking correctly marks on the next (2nd) item added to a list that had only 1 item in it of status 'done'
;; TODO: Implement auto-marking that occurs after adding a new item to the list (such as the first item to the list, or the next item added after all the previous items were marked complete, or on a new page)
;; TODO: Experiment taking in user input via a controlled Hiccup/Reagent component

(defn review-question [text1 text2]
  (str "Do you want to '" text2 "' more than '" text1 "'?"))


(def review-test-list
  [{:t-index 0, :text "b", :status :ready}
   {:t-index 1, :text "c", :status :new}
   {:t-index 2, :text "d", :status :new}])

(def review-session
  {:incoming-list review-test-list

   ;; :initial-item-of-comparison: ;; first-ready-item-text
   :initial-item-of-comparison (get (first
                                     (filter-by-status
                                      {:input-list review-test-list
                                       :input-status :ready}))
                                    :text)

   ;; [1 2] ;; these will be determined as the indecies of status `:new` items
   :reviewables (filter-by-status {:input-list review-test-list
                                   :input-status :new})

   ;; 0 ;; this will be initialized to the first unmarked (`:status` of `:new`)
   ;; item index, referring to the cursor position on the list of reviewable items
   :current-index-cursor (get (first (review-session :reviewables)) :t-index)

   ;; :current-item-of-comparison (review-session :initial-item-of-comparsion) 
   :answers [:no :yes] ;; these will be supplied by the user

   ;; scheduled changes to the list will be the yes's applied as a status change
   ;; from `:new` to `:ready` for the reviewable item in question
   ;; for example, a list of `[:ready :new :new]` will be converted to a list of
   ;; `[:ready :new :ready]` with input answers of `[:no :yes]`
   })

(defn individual-review
  "for a given input-list, a review cursor index,
  and an answer input, a new list is generated"
  [{:keys [input-list cursor-index answer-input]}]
  (let [current-readyest-item (filter-by-status {:input-list input-list
                                                   :input-status :ready})
          current-item (get input-list cursor-index)
          current-question (review-question )]

      )
  )

(do
  review-session
  )

;; post review lists are created as a by-product of review sessions where the answers are applied to the incoming list, and then the new list is returned back to replace the original

