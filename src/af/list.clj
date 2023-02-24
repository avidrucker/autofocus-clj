(ns af.list
  (:require
   [af.utils :as u]
   [af.item :as i]
   [clojure.string :as s]))


(def DEBUG-MODE-ON false)


(def list-api-design
  ;; TODO: convert these items to user stories
  "Q: What are the things that can be done with an AutoFocus list
  at the 'list level' *user* API?
  - 'add': append new item to bottom of list
  - 'prioritize': compare two items, potentially resulting in one getting marked as 'ready'
  - 'do': take action on the 'priority item', which will result in it getting marked as 'done', and potentially also a duplicate task item being generated for further/remaining work (if there is any) on said item task

Q: What are the things that are handled by the list namespace, but not directly by the user?
  - auto-mark/auto-dot first markable/dottable item (so there is
    always at least one dotted/marked item OR no markable/dottable items)
  - mark/dot item at index n (when reviewing one's list for the
    purpose of comparing / prioritizing)
  - mark bottom-most dotted item as `:done` after 'focus' session")


;; DONE: Implement auto-dotting logic for first-dottable-item
;; DONE: write a function that creates to-do list items as-is (without a t-index)
;; DONE: write a function that takes a created to-do list item without a t-index, 
;; adds a t-index key-value pair to it, and stick its onto the bottom of a to-do list
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
  ;; TODO: write a test that confirms that the first item in a list has a t-index of 0, 
  ;; the 2nd item a t-index of 1, etc..
  (count target-list))

;; TODO: Write a test that confirms that after creating new items that the list t-index 
;; values return back true from (distinct?) See: ClojureDocs: distinct?


;; Ideas for clear layers of abstraction/hierarchy
#_"A pure dispatch function which dispatches based on an action keyword
to update a user's to-do list as a result of a new item addition,
list reviewing/prioritizing, or list focusing (ie. taking action on/doing
an item task).

Note: A separate helper dispatch function can handle things such as
 automarking, duplicating, or other post-action actions... 

DONE: Assess Q: Did the old `update-list` function make the code harder
to understand/read? A: Yes, it did.

- append new item to bottom of list[1]
- auto-mark/auto-dot first markable/dottable item as 'ready' (so there is always 
  at least one dotted/marked item OR no markable/dottable items)
- mark/dot item as 'ready' at index n (when reviewing one's list for the purpose 
  of comparing / prioritizing)
- re-mark bottom-most dotted item as 'done' after 'focus' session

[1] update-list takes a new item, to leave the item creation itself to a dedicated 
   item creation function, which in turn leaves text input to an impure IO function"


;; DONE: Implement the auto-marking/auto-dotting of the first added item immediately 
;; after adding it
;; DONE: Test that auto-marking works on the first item added to an empty list
;; TODO: Test that auto-marking works on a new item added to a list that has only 0 
;; items in it of status 'done'  (in other words:  "Implement auto-marking that 
;; occurs after adding a new item to the list (such as the first item to the list, 
;; or the next item added after all the previous items were marked complete, or on 
;; a new page)")

;; PURE FUNC
;; DONE: refactor to use `pos` instead of `> 0`
;; Q: Is there a clearer/more effective idiom for `pos?-count-filter`?
;; 🔍 
(defn- has-any-of-status?
  "Predicate function which checks a list to see
  whether it contains items of a given status."
  [{:keys [input-list input-status]}]
  (let [item-statuses (map :status input-list)]
    (pos? (count (filter #(= % input-status) item-statuses)))))


;; 🏁
(defn- is-auto-markable-list?
  "A list is 'auto-markable' if there are any new items and zero ready items."
  [{:keys [input-list]}]
  (let [has-new-items?       (has-any-of-status?
                              {:input-list input-list
                               :input-status :new})
        has-ready-items?     (has-any-of-status?
                              {:input-list input-list
                               :input-status :ready})]
    (and has-new-items? (not has-ready-items?))))


;; (comment
;;   ;; TODO: convert this comment block into a test block
;; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; ;; REPL driven development testing
;;   (true?
;;    (is-auto-markable-list? {:input-list  
;;                             [{:t-index 0, :text "a", :status :new}]}))

;;   #_(filter #(= (:status %) :new) [{:text "z" :status :new}])
;;   (println "---------------------")

;;   (false? (is-auto-markable-list?
;;            {:input-list
;;             [{:t-index 0, :text "b", :status :ready}
;;              {:t-index 1, :text "c", :status :new}
;;              {:t-index 2, :text "d", :status :new}]}))

;;   (false? (is-auto-markable-list?
;;            {:input-list
;;             [{:t-index 0, :text "e", :status :done}
;;              {:t-index 1, :text "f", :status :ready}
;;              {:t-index 2, :text "g", :status :ready}]}))

;;   (true? (is-auto-markable-list? {:input-list
;;                                  [{:t-index 0, :text "h", :status :done}
;;                                   {:t-index 1, :text "i", :status :done}
;;                                   {:t-index 2, :text "j", :status :new}]}))
;; ;; TESTING END
;; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;   )


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
  ;; TODO: confirm that this function correctly returns nil when there are no new 
  ;;       items in a list
  ;; TODO: determine whether the first found item's index is always guarenteed to 
  ;;       be the item's index number itself
  ;; TODO: determine that this function returns 0 correctly for the first newly 
  ;;       added item at precisely the right time (this should be true only once 
  ;;       the first item has been added to a list, not before because the list 
  ;;       would have been empty, nor just after, because the list is to be 
  ;;       auto-marked/auto-dotted immediately after adding the first item) 
  [{:keys [input-list]}]
  (let [has-new?       (has-any-of-status? {:input-list input-list
                                           :input-status :new})]
    (when has-new?
      (let [first-found-index (first-index-of-attr
                               {:input-list input-list
                                :target-attr :new})]
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


;; TODO: retire this function as deprecated OR refactor, bc it fails silently 
;;       when malformed/out-of-bounds index inputs are entered
;; TODO: reconsider whether this function must be retired, bc it is OK to have
;;       'unsafe' functions (ones that cannot handle invalid input) as long as
;;       they are wrapped behind an interface/abstraction w/ guardrails
;; 📖
(defn- set-nth-item-in-list-to-status
  "Takes in an args-hashmap with three named key-value pair arguments:
  - an input-list
  - an index of an item to 'modify'
  - the target item's (soon to be) new status
  Returns back a new todo-list with the nth index element item's status set to 
  the inputted status

  Notes:
  - This function does not currently check or acount for for out-of-bounds 
    errors/exceptions.
  - This was called `set-nth-ready-1`"
  [{:keys [input-list n-index input-status]}]
  (let [new-item
        (i/set-item-to-status
         {:input-item  (get input-list n-index)
          :input-status input-status})

        new-list
        (assoc input-list n-index new-item)]
    ;; TODO: remove in-bounds check from here, this should be a separate 
    ;;       check elsewhere  (ie. move to the domain context boundary) 
    ;; (if (in-bounds-inclusive? {:valid-floor 1
    ;; :valid-max (dec (count input-list))
    ;; :input-n n-index})
    new-list
    ;; input-list ;; else, return the list as-is  (b/c n-index is out-of-bounds)
    ))

;; TODO: test to confirm that this function works as desired
;; DONE: fix bug where it appears that all newly added items are getting marked 
;;       as `:ready` !!!!
;; TODO: add spec clause to confirm that, when this function is called, it is 
;;       only called on auto-markable lists
;; 🪄
(defn- set-1st-new-item-in-list-to-ready
  "This function *always* sets the topmost new item's status to 'ready'.
  It currently performs NO checks for data validity, or whether or not a
  list is 'auto-markable'. Therefore, this function is to be called if
  and only if a list is validated to be auto-markable. Previous names:
  `set-topmost-new-item-in-list-to-ready!`, `set-first-new-item-to-ready`"
  [{:keys [input-list]}]
  ;; println debugging
  ;; (println "...setting 1st new item in list to ready...")
  (set-nth-item-in-list-to-status
   {:input-list input-list
    :n-index (index-of-first-new-item-in-list {:input-list input-list})
    :input-status :ready}))


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
  ;; println debugging
  ;; (println "...conditionally automarking list...")
  (if (is-auto-markable-list? {:input-list input-list})
    (set-1st-new-item-in-list-to-ready {:input-list input-list})
    input-list))


;; TODO: refactor list logic so that way the update-list function is called in 
;; the next namespace, rather than here, similar to `conduct-focus-on-list`
;; ➕
(defn add-item-to-list
  ;; TODO: refactor via separation principle  (ie. as possible, make one 
  ;; function that makes items, and make one function that adds items to 
  ;; a list, and compose them in that way) 
  "This function takes in an input item and target list, and then 'adds' said 
   item to the list by making a brand new list with the input item appended on 
   to the end.

  Note: This function is part the list namespace's 'public' API, and is meant 
   to be used by other namespaces.
  
  Work-In-Progress language: 'transacts' (on?) the to-do items collection 
   'database' (i.e. the to-do list)"
  [{:keys [input-item target-list]}]
  (let [;; TODO: convert plain println debugging to
        ;;       custom debugger (when DEBUG-MODE-ON ...)
        ;; _  (println "...adding item to list...") ;; debugging

        ;; TODO: double-check that setting this here is appropriate and 
        ;;       effective for both new items, duplicate items, as well 
        ;;       as any other adding items to list scenarios that 
        ;;       utilize this function
        ;; note: adding new items does not require a target index bc 
        ;; new items are always appended to the end/bottom/back of the list
        item-to-be-added (conj {:t-index (t-next {:target-list target-list})} input-item) 

        new-list (conj target-list item-to-be-added)

        ;; note: it would be incorrect to call set-topmost-new-item here bc new items aren't guarenteed to be automarkable
        auto-marked-new-list
        (conditionally-automark-list {:input-list new-list})]
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


(defn- filter-by-status
  [{:keys [input-list input-status]}]
  (filter #(= (:status %) input-status) input-list))


;; TODO: refactor code so that is-doable-list? can become private to this namespace
(defn is-doable-list?
  "A list is considered a doable list if it contains any `:ready` items.
  Old name: `is-focusable-list?`"
  [{:keys [input-list]}]
  (pos? (count (filter-by-status {:input-list input-list
                                  :input-status :ready}))))


;; TODO: test to confirm that this works for lists with no items, with one item, and with multiple items of different status, and with multiple items of same status
(defn- last-of-status-from-list
  "Returns the last element in the list with the target status.
  If no items with the target status are found, `nil` is returned."
  [{:keys [input-list target-status]}]
  (last (filter-by-status {:input-list input-list
                           :input-status target-status})))


;; TODO: split this function into 2 functions, one that conducts the focus, and the other that handles the CLI I/O
;; original name: conduct-focus-on-list
(defn mark-priority-item-done
  [{:keys [input-list]}]
  ;; 1. find bottom-most dotted  (status 'ready') item
  ;; 2. update item in list  (ie. create a new list with item replaced) to have a status of 'done'
  ;; TODO: generalize last-ready-item-index to use for review comparison
  (if (is-doable-list? {:input-list input-list})
    ;; if input-list is focusable, we focus on the last ready item...
    (let [;; TODO: save this the 'success' message
          ;;_                     (println "...focusing on list...") ;; debugging
          index-of-item-to-focus-on
          (get (last-of-status-from-list {:input-list input-list
                                          :target-status :ready}) :t-index)

          new-list              (set-nth-item-in-list-to-status
                                 {:input-list input-list
                                  :n-index index-of-item-to-focus-on
                                  :input-status :done})

        ;; after focusing on a list, we must auto-mark again, just in case that the item that was just completed was the last `:ready` item
          automarked-new-list   (conditionally-automark-list
                                 {:input-list new-list})]
    ;; index-of-item-to-focus-on
      automarked-new-list)
    ;; ... else, we return the list as-is
    ;; TODO: instead of printing here, return a map with a result and a response message, optionally with a `:success` status
    ;; TODO: save as cancel/fail confirmation text
    (u/print-and-return
     {:input-string "List is not actionable, returning list as-is..."
      :is-debug? false
      :return-item input-list})))


;; TODO: Test that auto-marking correctly marks on the next (2nd) item added to a list that had only 1 item in it of status 'done'
;; TODO: Implement auto-marking that occurs after adding a new item to the list (such as the first item to the list, or the next item added after all the previous items were marked complete, or on a new page)
;; TODO: Experiment taking in user input via a controlled Hiccup/Reagent component


;; TODO: search for duplicate function in af.af namespace, remove duplicate
(defn- review-question
  [{:keys [priority-item-text cursor-item-text]}]
  (str "Do you want to '" cursor-item-text 
       "' more than '" priority-item-text "'?"))


;; TODO: refactor codebase to replace (not (nil?)) idiom with (some?)
;; TODO: refactor usage of this function to make it private (see cli.clj line 329)
(defn get-priority-item-from-list
  [{:keys [input-list]}]
  (last (filter-by-status
         {:input-list input-list
          :input-status :ready}))) 


(defn gen-priority-item-is-str
  [{:keys [input-list]}]
  (str "The current priority item is '"
       (get (get-priority-item-from-list {:input-list input-list}) :text)
       "'."))


(defn conduct-take-action-on-list
  [{:keys [input-list]}]
  (let [result                      (mark-priority-item-done {:input-list input-list})
        priority-item               (get-priority-item-from-list {:input-list input-list})
        result-with-work-remaining  (add-item-to-list
                                     {:input-item (i/gen-duplicate-item
                                                   {:input-item priority-item
                                                    :input-status :new})
                                      :target-list result})]
    {:result result
     :priority-item priority-item
     :result-with-work-remaining result-with-work-remaining}))


;; TODO: assess whether this is reusable for next-cursor
;; Q: Is it possible to run out of bounds with this function?
(defn- get-first-new-item-index-after-index-x
  "Takes an input list and input-index (likely to be a cursor-index
  or priority-item-index), and returns back the next item index
  where the item has a :status of :new"
  [{:keys [input-list input-index]}]
  (let [
        items-after-input-index (subvec input-list (inc input-index))

        ;; filter out items of :status :new, then get first in list
        next-new-item-after-index
        (first (filter-by-status {:input-list items-after-input-index
                                  :input-status :new}))
        
        result (when (some? next-new-item-after-index)
                 (get next-new-item-after-index :t-index))
        
        _      (when DEBUG-MODE-ON (println
                            "**********"
                                    "\n!!! next-new-item-after-index:"
                                    "\nindex #: " input-index
                                    "\nitem: " next-new-item-after-index
                                    "\nresult: " result
                                    "\n**********")) 
        ]
    
    ;; `first-new-item-index-after`: if another :status :new item exists
    ;; in list, we save its :t-index. otherwise, return nil for next-cursor
    ;; TODO: consider moving first to inside of code block to replace
    ;; `when-not-nil?` idiom with `when-not-empty?`
    result))


;; TODO: attempt to make this function private by refactoring out usage from cli.clj line 372
;; DONE: resolve bug where prioritizable lists are not correctly recognized as such
(defn get-index-of-first-new-item-after-priority-item
  "Returns the index of the next encountered item of `:new` status
  following priority-item. If no priority item exists, or no `:new`
  items exist, then `nil` is returned instead."
  [{:keys [input-list]}]
  (let [priority-item
        (get-priority-item-from-list {:input-list input-list})

        result ;; when the priority item exists
        (when (some? priority-item)
          ;; return next-new-item-index 
          (u/print-and-return
           {:input-string "returning index of first new item after priority item..."
            :is-debug? true
            :debug-active? DEBUG-MODE-ON
            :return-item
            (get-first-new-item-index-after-index-x
             {:input-list input-list
              :input-index (get priority-item :t-index) ;; priority item index
              })}))]
    result))


;;;; TODO: fix bug where prioritizable lists are not correctly recognized as such
;; TODO: compare with subvec implementation in this af.list namespace for a potentially terser/more concise implementation
(defn is-prioritizable-list?
  [{:keys [input-list]}]
  ;; A prioritizable list has a priority item *and* has new items
  ;; after the priority item --> In code, this can be abbreviated to
  ;;     `get-first-new-item-after-priority-item` returns not nill
    ;; 'index-of-first-prioritizable' exists
  ;; (not (nil? (get-index-of-first-new-item-after-priority-item {:input-list input-list})))
  (let [has-priority-item? (not (nil? (get-priority-item-from-list
                            {:input-list input-list})))

        index-result (when has-priority-item? 
                       (get-index-of-first-new-item-after-priority-item
                        {:input-list input-list}))
        
        has-new-item-after-priority-item?
        (some? index-result)
        
        ;; println debugging
        _        (when DEBUG-MODE-ON (println ["=========="
                           "\n!* has-priority-item?: " has-priority-item?
                           "\n!* index-result: " index-result
                           "\n!* has-new-item-after-priority-item?: " has-new-item-after-priority-item?
                           "\n=========="]))
        ]
    ((every-pred true?) has-priority-item? has-new-item-after-priority-item?)
    ))


(defn list-and-cursor-to-question 
  [{:keys [input-list cursor-input]}]
  (let [priority-item (get-priority-item-from-list {:input-list input-list})
        cursor-item (get input-list cursor-input)
        priority-item-text (get priority-item :text)
        cursor-item-text (get cursor-item :text)]
    (review-question {:priority-item-text priority-item-text 
                      :cursor-item-text cursor-item-text})))


;; TODO: move these test lists to the af.test or
;;       af.listtest (af.list.test?) namespace
(def review-test-list-1
  [{:t-index 0, :text "b", :status :ready}
   {:t-index 1, :text "c", :status :new}
   {:t-index 2, :text "d", :status :new}])

(def review-test-list-2
  [{:t-index 0, :text "a", :status :ready}
   {:t-index 1, :text "b", :status :new}
   {:t-index 2, :text "c", :status :done}
   {:t-index 3, :text "d", :status :new}])

(def review-test-list-3
  [{:t-index 0, :text "a", :status :ready}
   {:t-index 1, :text "b", :status :new}
   {:t-index 2, :text "c", :status :new}
   {:t-index 3, :text "d", :status :new}])

(def review-test-list-4
  [{:t-index 0, :text "a", :status :ready}])

(def review-test-list-5
  [{:t-index 0, :text "a", :status :ready}
   {:t-index 1, :text "b", :status :ready}])

;; (comment
  (mark-priority-item-done
   {:input-list
    [{:t-index 0, :text "b", :status :ready}
     {:t-index 1, :text "c", :status :new}
     {:t-index 2, :text "d", :status :new}]})

  (= (is-prioritizable-list? {:input-list review-test-list-1}) true)
  (= (is-prioritizable-list? {:input-list review-test-list-2}) true)
  (= (is-prioritizable-list? {:input-list review-test-list-3}) true)
  (= (is-prioritizable-list? {:input-list review-test-list-4}) false)
  (= (is-prioritizable-list? {:input-list review-test-list-5}) false)
  #_(= (is-prioritizable-list? {:input-list review-test-list-2}) false)
;; )

(get-index-of-first-new-item-after-priority-item {:input-list review-test-list-1})

;; REVIEW DESIGN
;; A review is simply one comparison of two items in a to-do list
;; (the priority :ready item and one of the following :new items)
;; where an answer of 'yes' to the comparisxon question will change 
;; the current cursor index `:new` item status to `:ready`
;; Note: This will be the "parent" function that composes together 
;; the smaller "children" functions
;; Q: What are the current responsibilities of this function?
;; Q: How can it be broken down into smaller individual responsibilities?
;; TODO: review `create-single-review-comparison` function for deprecation
#_(defn create-single-review-comparison
  "[pure function] Inputs `input-list` and `review-index`:
   - `input-list` is the user's entire current to-do list
   - `review-index` is the index referring to the cursor on 
     the list of reviewable items (`current-reviewable-items`)
   TEMPORARY INPUT user-answer: this is either :yes or :no
   
   'priority-item': This was called the 'CMWTD' or 'current most want to do' 
        item in older implementations of AF made by AD
        The priority item is simply the bottom-most 
        `:ready` item, and it is what is compared against when
        conducting review sessions.
        Alternate name: 'highest-priority-ready-item'
   "
  [{:keys [input-list cursor-input user-answer]}]
  (let [
        ;; priority-item (get-priority-item-from-list {:input-list input-list})

        ;; The cursor-index is initialized to the first `:new` item *AFTER*
        ;; the priority-item, where [THIS IS IMPORTANT] the index is of the **original** list.
        ;; This index is used in turn to get current `:new` item to compare against priority-item
        ;; Alternate names: "current-review-index", "review-cursor-index"
        ;; TODO: fix bug where the default cursor-index is set to zero, when it should be 
        ;; instead initialized to the first :new item *AFTER* the priority-item
        ;; Q: Is an auto-cursor-index necessary and/or particularly helpful/useful?
        cursor-index  (if (nil? cursor-input)
                        (get-index-of-first-new-item-after-priority-item
                         {:input-list input-list})
                        cursor-input)

        ;; priority-item-index            (get priority-item :t-index)
        ;; priority-item-text             (get priority-item :text)

        ;; all `:new` items *AFTER* the `highest-priority-ready-item`
        ;; "the reviewables"
        ;; current-reviewable-items (filter-by-status {:input-list 
        ;;                                             (subvec input-list 
        ;;                                                     (inc priority-item-index)) 
        ;;                                             :input-status :new})

        ;; Q: Is this a good use case for get-in ? TODO: tidy up this code
        ;; cursor-item-text (get (get input-list cursor-index) :t-index)

        ;; current-question
        ;; (list-and-cursor-to-question {:input-list input-list
        ;;                               :cursor-input cursor-index})
        ;; _                           (println ["current question" current-question])

        ;; YES CASE:
        ;; return the list back with the review index incremented by one,
        ;; and the current-new-item changed into a :ready item

        ;; NO CASE: return the list back with only the review index incremented by one
        ])
  {;; scheduled changes to the list will be the yes's applied as a status change
   ;; from `:new` to `:ready` for the reviewable item in question
   ;; for example, a list of `[:ready :new :new]` will be converted to a list of
   ;; `[:ready :new :ready]` with input answers of `[:no :yes]`
   })

;; TODO: split this function into two separate functions:
;;       1: can-continue-comparing?
;;       2: calc-next-cursor
(defn- can-continue-comparing?
  "FOr a givben input-list and cursor-index, whether or not
  there are remaining items to compare against is calculated."
  [{:keys [input-list input-cursor-index]}]
  (let [current-item-index (get-in input-list [input-cursor-index :t-index])
        items-after-cursor (subvec input-list (inc current-item-index))]

    ;; println debugging
    #_(println ["current-item-index: " current-item-index
              "items-after-cursor: " items-after-cursor
              "next-new-item-after-cursor: " next-new-item-after-cursor
              "next-cursor: " next-cursor])
    (pos? (count (filter-by-status {:input-list items-after-cursor :input-status :new})))))

;; testing whether a list can continue to be reviewed/prioritized
(def round-test-1
  {:input-list review-test-list-1
   :input-cursor-index 1})

(def round-test-2
  {:input-list review-test-list-1
   :input-cursor-index 2})

(= true
   (can-continue-comparing? round-test-1))
(= false
   (can-continue-comparing? round-test-2))

(defn- calc-next-cursor
  "For a given list and cursor index, in the context of a review session,
  calculates where the next cursor position will be. If there are no valid
  cursor positions remaining (ie. no `:new` items left to compare against),
  then `nil` is returned."
  [{:keys [input-list input-cursor-index]}]
  (when (can-continue-comparing? {:input-list input-list
                                :input-cursor-index input-cursor-index})
    (let [items-after-cursor (subvec input-list (inc input-cursor-index))
        ;; filter out items of :status :new, and get the first in the list
          next-new-item-after-cursor (first (filter-by-status {:input-list items-after-cursor
                                                               :input-status :new}))]
        ;; if there was another :status :new item, we return its :t-index, 
        ;; otherwise, we have nil for next-cursor
      (get next-new-item-after-cursor :t-index)
      )))

;; testing the determinationm of the next-cursor
(calc-next-cursor round-test-1)
(calc-next-cursor round-test-2)

;; TODO: split this function into 2: get-single-comparison, and submit-single-comparison where the first function gets a single comparison to serve to the user, whereas submit-single-comparison will 'modify' the app state with a user's choice, or end the review session and take the user back to the main menu 
;; original name: `submit-individual-review`
(defn get-single-comparison
  "For a given input-list and a cursor index, a prompt-question is generated."
  [{:keys [input-list input-cursor-index]}]
  (let [;; current-item (get input-list input-cursor-index)
        ;; TODO: investigate whether or not two separate indecies are necessary/helpful (ie. `input-cursor-index` and `current-item-index`)
        ;; current-item-index (get current-item :t-idex)
        current-question (list-and-cursor-to-question
                          {:input-list input-list
                           :cursor-input input-cursor-index})]
   ;; DONE: generate the appropriate question to return back to the user
    current-question ;; "Do you want to...? [stub question]" 
    ))


(defn submit-single-comparison
  "When answer-input is `:yes`, current-item gets marked as 
   `:ready` (ie. a new list is returned with the item at the 
   same index as current-item having a `:ready` status)
   
   When answer-input is `:no`, we simply update the cursor index
   and return the input-list as-is"
  [{:keys [input-list input-cursor-index answer-input]}]
  {:output-list 
   (if (= answer-input :yes)
     ;; when the answer is yes, a newly created list is returned
     (u/print-and-return
      {:input-string "... marking current item as 'ready' ..."
       :is-debug? false
       :return-item
       (set-nth-item-in-list-to-status
        {:input-list input-list
         :n-index input-cursor-index ;; current-item-index
         :input-status :ready})})
     ;; else, the input-list is returned as-is
     input-list)
   :next-cursor (calc-next-cursor {:input-list input-list
                                   :input-cursor-index input-cursor-index})
   :quitting-comparison (= answer-input :quit)})


(comment
  (def submit-test-1a (conj round-test-1 {:answer-input :yes}))
  (def submit-test-1b (conj round-test-1 {:answer-input :no}))
  (def submit-test-1c (conj round-test-1 {:answer-input :quit}))
  
  (def submit-test-2a (conj round-test-2 {:answer-input :yes}))
  (def submit-test-2b (conj round-test-2 {:answer-input :no}))
  (def submit-test-2c (conj round-test-2 {:answer-input :quit}))
  
  
  (get-single-comparison round-test-1)
  (get-single-comparison round-test-2)
  
  (submit-single-comparison submit-test-1a)
  (submit-single-comparison submit-test-1b)
  (submit-single-comparison submit-test-1c)
  (submit-single-comparison submit-test-2a)
  (submit-single-comparison submit-test-2b)
  (submit-single-comparison submit-test-2c)
  )


;; TODO: relocate this to an appropriate test namespace
#_(every? true? [(=
   {:output-list
    [{:t-index 0 :text "b" :status :ready}
     {:t-index 1 :text "c" :status :ready}
     {:t-index 2 :text "d" :status :new}]
    :next-cursor 2}
   (submit-single-comparison {:input-list review-test-list-1
                              :cursor-input 1
                              :answer-input :yes}))

  (=
   {:output-list
    [{:t-index 0 :text "b" :status :ready}
     {:t-index 1 :text "c" :status :new}
     {:t-index 2 :text "d" :status :new}]
    :next-cursor 2}
   (submit-single-comparison {:input-list review-test-list-1
                              :cursor-input 1
                              :answer-input :no}))

  (=
   {:output-list
    [{:t-index 0, :text "a", :status :ready}
     {:t-index 1, :text "b", :status :ready}
     {:t-index 2, :text "c", :status :done}
     {:t-index 3, :text "d", :status :new}],
    :next-cursor 3}
   (submit-single-comparison {:input-list review-test-list-2
                              :cursor-input 1
                              :answer-input :yes}))

  (=
   "Do you want to 'b' more than 'a'?"
   (list-and-cursor-to-question {:input-list review-test-list-2
                                 :cursor-input 1}))

  (=
   "Do you want to 'd' more than 'a'?"
   (list-and-cursor-to-question {:input-list review-test-list-2
                                 :cursor-input 3}))

  (=
   {:output-list
    [{:t-index 0, :text "a", :status :ready}
     {:t-index 1, :text "b", :status :new}
     {:t-index 2, :text "c", :status :new}
     {:t-index 3, :text "d", :status :new}],
    :next-cursor 3}
   (submit-single-comparison {:input-list review-test-list-3
                              :cursor-input 2
                              :answer-input :no}))
  ])

