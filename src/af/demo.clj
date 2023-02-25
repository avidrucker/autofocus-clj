(ns af.demo
  (:require [af.item :as i]
            [af.list :as l]
            [af.data :as d]))

;; 1. create some dummy data
(def demo-text-lists 
  "'It's just data.'
  This map serves as the basis data for the demos.

  possible/potential cons: increased nesting, increased quantity / volume
  of accessor code, more naming that could be avoided, maybe also more
  decisions to be made regarding data storage
  pros: increased data discoverability/accessibility due to data colocation" 
  {:tiny '("a" "b" "c")
   :faux '("wash the dishes" "do the laundry"
           "go for a walk" "study Japanese")})

;; 2. create a dummy item
(def demo-item
  "^^^ 'self-describing code': well named data does not need a comment
  eg. DO `demo-item`, DON'T `my-item`"
  (i/create-new-item-data
   {:input-text (first (get demo-text-lists :tiny))})
  ;; ^^^  "DRY": use hashmaps/JSON of re-usable strings/text, don't use "magic numbers" and "magic strings"
  ;; eg. DO :input-text (first (get demo-text-lists :tiny))
  ;;     DON't :input-text "a"
  )

(do
  demo-item 
  )

;; 3. create an empty list... 
(def demo-list-a (atom []))

;; 4. ... then, put the demo item into the list
(reset! demo-list-a
        (l/add-item-to-list
         {:input-item demo-item
          :target-list @demo-list-a}))

(do
  @demo-list-a
  )

(do

;; 5. create two more items and add them to the demo list
  (def demo-item-2
    (i/create-new-item-data
     {:input-text (second (get demo-text-lists :tiny))}))

  (def demo-item-3
    (i/create-new-item-data
     {:input-text (last (get demo-text-lists :tiny))}))

;; TODO: research how to sequentially build up data from collections in an effective manner  (and note different scenarios, such as iterative accumulation  (loops)  vs non-iterative  (maps? comprehension based perhaps?)) 

  (reset! demo-list-a (l/add-item-to-list
                       {:input-item demo-item-2
                        :target-list @demo-list-a}))

  (reset! demo-list-a (l/add-item-to-list
                       {:input-item demo-item-3
                        :target-list @demo-list-a})))

(do
  @demo-list-a
  )

;; 6. conduct one focus on the demo list

(reset! demo-list-a (l/mark-priority-item-done
                     {:input-list @demo-list-a}))


;; 7 conduct one review on the list, answering 'y' for yes

;; STUB !!!



;; TODO: convert to demo test
(= 
 ;; TODO: use demo data refs instead of custom locally defined "magic" bindings
 ;; - [ ] IDEA: Name this test in the test-definitions namespace, locate in 2 places (one by where the function is defined, and one in the "test-runner" namespace)
 ;; test to confirm that items can be converted to strings as desired
 (i/stringify-item {:item demo-item
                      :dict d/cli-marks})
 "- [ ] a")

(= (i/stringify-item
    {:item {:text "hi" :status :ready}
     :dict d/cli-marks})
   "- [o] hi")

(=
 (i/stringify-item
  {:item {:text "hello" :status :done}
   :dict d/cli-marks})
   "- [x] hello")

;; Make a brand new item
(= 
 ;; TODO: convert this to a deftest
 ;; TODO: name this test, relocate to test namespace, run in 2 places, just below the main FuT (function under test), as well as in the test-runner namespace
 (i/create-new-item-data
  {:input-text (first (demo-text-lists :tiny))})
 {:text "a" 
  :status :new})

;; Make a new item with a status of ready (this is akin to duplicating an item when there is still more work left to be done, and you have to stop early for some reason or another)
(=
 ;; TODO: convert this to a deftest 
 ;; TODO: Test create-new-item function w/ task text, optional keyword argument input-status of "ready" (marked)
 (i/create-new-item-data
  {:input-text (first (demo-text-lists :faux))
   :input-status :ready})
 {:text "wash the dishes" 
  :status :ready})


(comment
  ;; TODO: Test single call to update-list to add one item to an empty list
  ;; TODO: transfer the function comments to where the code lives, rather than defining anonymous, uncommented, unnamed code

  #_(def new-item-data
    ;; TODO: relocate to test namespace, call just below FuT create-new-item-data, and again in test-runner namespace
    "data for a soon-to-be-made item" 
    (create-new-item-data
     {:input-text (first (af.demo/d-text-lists :tiny))}))

  #_(def new-item-tx-1
  "the necessary list transaction data to create a new item" 
  {:action :append-new
   :input-list @maria.user/list-1 
   :new-item-data new-item-data})

  #_(comment (def new-item-tx-2 {:action :append-new
                             :input-list maria.user/demo-list-1
                             :new-item-data new-item-data}))

  #_(def single-item-list-1
  "a list that has been created empty, and then had a new item added to the list with an 'append-new' item transaction" 
  ;; TODO: implement adding of a new item triggers  "check for auto-markable items" 
  (update-list new-item-tx-1))
)

#_(=
 ;; TODO: convert this to a deftest
 ;; DONE: Create a test to confirm items can be programmatically marked as ready
 ;; note: this test, while appearing to focus on status update, it also looking at t-index, and gets an item from a list, both steps which seem irrelevant to the test at hand
 ;;   --> TODO: refactor this test to focus simply on the inputted item's status and outputted item's status
 (l/set-item-to-status
  {:input-item d-item
   :input-status :ready})
 {:text (first (get d-text-lists :faux))
  :status :ready
  :t-index 0})



#_(def stringified-test-list-1
  ;; TODO: move to test namespace
  (l/stringify-list {:input-list test-list-1
                     :marks-dict d/cli-marks}))

#_(do
  ;; test-list-1
  ;;stringified-test-list-1
)

#_(def test-list-2 
  ;; TODO: move to test namespace
  ;; a list with three items where the first item has been successfully auto-marked / auto-dotted
  (l/conditionally-automark-list
   {:input-list test-list-1}))


#_(do
   ;; TODO: move to tickler-file namespace
   ;; TODO: Test reset-list to confirm that list is made to be empty w/ persistent app/list state store
   (defn reset-todo-list-1! [input-list]
     (reset! input-list '()))
   
   (defn reset-todo-list-2! []
     (reset! @maria.user/list-1 '()))
   
   (defn reset-todo-list-3! []
     (reset! maria.user/demo-list-1 '()))
   )

#_(do 
   ;; Immutable list 1 (DONE: fix issue: this immutable list needs no "reset!", it can just be re-def'd / rebinded / (rebound?) as an empty list, or as an updated ratom/atom/cell)
   ;; Note: the above  "fix issue" is a great example of (while being a potentially useful suggestion, it also represents) a non-issue, as it's more of a comment, rather than a dev-task, and doesn't necessarily give clarity in terms of a more 'effective path forward' (as opposed to other possible/potential alternatives)
   ;; TODO: move to tickler-file namespace
   (reset-todo-list-1! single-item-list-1)
   (reset-todo-list-1! @maria.user/list-1)
   (reset-todo-list-1!
    ;; has potential
    maria.user/demo-list-1)
   (reset-todo-list-2!)
   (reset-todo-list-3!
    ;; has potential
    ))


;; TODO: Test to confirm that the correct auto-markable item index-key (the second item, with an index-key of 1) is returned for a list with three items where the first item has a status of 'done' and the other two items have a status of 'new'
#_(= 1 (auto-markable-index
        ;; TODO: modify this example to be a list with the first item marked as complete, where the correct index-key returned is expected to be 1, for example by calling  "conduct-focus" on the the 'three-items-first-marked' list 
        {:input-list three-items-first-marked}))

;; TODO: Confirm via a test that the first item added to an empty list is auto-dotted [dev-task]
;; TODO: Relocate user story below to the user stories namespace user stories hashmap [mgr-task]
;; - [ ] User story: The user can, upon adding a new item to an empty list, see that the newly added item has been auto-marked as  "ready" 

;; (= 
;;    ;; TODO: convert to a deftest
;;    ;; TODO: relocate to test namespace
;;    (update-list {:action :append-new 
;;                  :input-list @demo-list
;;                  :new-item (create-new-item {:input-text (demo-texts :d)})})
;;    {1 {:text "study Japanese" 
;;        :status :ready}})
   


