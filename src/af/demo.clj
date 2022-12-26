(ns af.demo
  (:require [af.list :as l]
            [af.data :as d]))

(def d-text-lists
  ;; global demo data  "namespace" as a hashamp 
  ;; cons: increased nesting, increased quantity/volume of accesor code, more naming that could be avoided, maybe also more decisions to be made regarding data storage
  ;; pros: increased data discoverability/accessibility due to data colocation
  "It's just data." 
  {:tiny '("a" "b" "c")
   :faux '("wash the dishes" "do the laundry"
           "go for a walk" "study Japanese")})

(def d-item
  "a demo item to use" 
  ;;  ^^^ "self-describing code": well named data does not need a comment
  ;; eg. DO demo-item, DON'T my-item
  {:text (first (get d-text-lists :faux)) ;; <<<  "DRY": DO hashmaps/JSON of re-usable strings/text, DON'T  "magic numbers" and  "magic strings" throughout codebase
   :status :new
   :t-index 0 ;; TODO: add documentation  (or better yet, code?)  on the data shape of todo-items where :t-index is the unique ID ;; TODO: write down explicit rationale for unique ID: ability to clone/duplicate & distinguish between different todo-items 
   })

;; TODO: relocate this code to the list or test namespaces
#_(comment
  ;; Mutable Ratom
  ;; Q: Does this ever update back to empty? TODO: Test this by referencing (?) a live view via defcell, cell, and/or html
  @maria.user/list-1

  ;; Mutable Defcell
  ;; This appears to update in real-time. TODO: confirm that this is still the case
  maria.user/demo-list-1)

(defn create-demo-list
  [input-texts]
    ;; TODO: Test multi-call to add-item-to-list (and also to update-list to add multiple items to an empty list for testing purposes
    ;; TODO: research how to sequentially build up data from collections in an effective manner  (and note different scenarios, such as iterative accumulation  (loops)  vs non-iterative  (maps? comprehension based perhaps?)) 
    ;; I want something that looks like this:
    ;;  (reduce  add-item-to-list  '()  input-texts) 
    ;; ... or this:
    ;; (apply reduce add-item-to-list input-texts) 
    ;; ... but *not* the following  (Q: and why not?) 
  (loop [i 0
         the-list d/EMPTY-LIST]
    (if (>= i (count input-texts))
      the-list
      ;; This was originally wetter: (add-item-to-list "f" (add-item-to-list "e" (add-item-to-list "d" '())))
      (recur (inc i)
             (l/add-item-to-list
              {:input-text (nth input-texts i)
               :target-list the-list})))))


(def demo-list
  (create-demo-list '("g" "h" "i")))

#_(do
  demo-list
  )

(comment 
 ;; TODO: save and relocate to compare and contrast the evolution of to-do list creation in this code sketch
 (def demo-list-of-items
   ;; TODO: replace this placeholder scaffolded data w/ dedicated function to generate dynamically
   '({:text "a" :status :ready :t-index 0}
     {:text "b" :status :new :t-index 1}
     {:text "c" :status :new :t-index 2})))
;; uncomment to test
#_@demo-list 

#_(defn add-items-to-demo-list! [{:keys [input-items input-list]}]
  (last (for [x (range (count input-items)) 
              :let [y (nth input-items x)]]
          (reset! input-list (update-list {:action :append-new 
                                           :input-list @input-list
                                           :new-item-data y}))
          ;; TODO: confirm that this only returns what you want it to  (just the final hashmap of size 3) 
          input-list)))

#_(comment (def three-item-list (l/add-items-to-demo-list! {:input-items demo-list-of-items
                                                        :input-list demo-list})))

(= 
 ;; TODO: use demo data refs instead of custom locally defined "magic" bindings
 ;; - [ ] IDEA: Name this test in the test-definitions namespace, locate in 2 places (one by where the function is defined, and one in the "test-runner" namespace)
 ;; test to confirm that items can be converted to strings as desired
 (l/stringify-item {:item d-item
                      :dict d/cli-marks})
 "- [ ] wash the dishes")


(= 
 ;; TODO: convert this to a deftest
 ;; TODO: name this test, relocate to test namespace, run in 2 places, just below the main FuT (function under test), as well as in the test-runner namespace
 (l/create-new-item-data {:input-text (first (d-text-lists :tiny))})
 {:text "a" 
  :status :new})

(=
 ;; TODO: convert this to a deftest 
 ;; TODO: Test create-new-item function w/ task text, optional keyword argument input-status of "ready" (marked)
 (l/create-new-item-data {:input-text (first (d-text-lists :faux)) :input-status :ready})
 {:text "wash the dishes" 
  :status :ready}
 )

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

(=
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


(def test-list-1
  ;; TODO: experiment to see if you can refactor with threading macro to DRY up syntax
  ;; TODO: move to test namespace
  (l/add-item-to-list
   {:input-text "f"
    :target-list (l/add-item-to-list
                  {:input-text "e"
                   :target-list (l/add-item-to-list
                                 {:input-text "d"
                                  :target-list d/EMPTY-LIST})})}))


(def test-list-pretty-1 
  ;; TODO: investigate value of 'joined on newline' stringify function vs. 'NOT joined on newline' stringify function
  ;; TODO: move to test namespace
  (map #(l/stringify-item
         {:item %
          :dict d/cli-marks})
       test-list-1))


#_(comment
 ;; TODO: convert this to a deftest
  (l/add-item-to-list
   {:input-text "c"
    :target-list (l/add-item-to-list {:input-text "b"
                                        :target-list @maria.user/list-1})}))



(def stringified-test-list-1
  ;; TODO: move to test namespace
  (l/stringify-list {:input-list test-list-1 :marks-dict d/cli-marks}))

#_(do
  ;; test-list-1
  ;;stringified-test-list-1
)

(def test-list-2 
  ;; TODO: move to test namespace
  ;; a list with three items where the first item has been successfully auto-marked / auto-dotted
  (l/conditionally-automark-list
   {:input-list test-list-1}))

(def test-list-3
;; (l/set-topmost-new-item-in-list-to-ready! 
  ;; {:input-list ;; TODO: refactor with threading macro as possible
  (l/add-item-to-list
   {:input-text "c"
    :target-list (l/add-item-to-list
                  {:input-text "b"
                   :target-list (l/add-item-to-list
                                 {:input-text "a"
                                  :target-list d/EMPTY-LIST})})})
;; }  
)
;; )

#_(do
  test-list-3
  )
;; => [{:t-index 0, :text "a", :status :ready} {:t-index 1, :text "b", :status :ready} {:t-index 2, :text "c", :status :ready}]

;;;; TODO: fix bug where all items added to a list are auto-marked to 'ready' status
(=
 ;; TODO: convert this s-exp into a deftest
 ;; TODO: move to test namespace
 test-list-3
 [{:t-index 0 :text "a" :status :ready}
  {:t-index 1 :text "b" :status :new}
  {:t-index 2 :text "c" :status :new}])


#_(do
   ;; TODO: move to tickler-file namespace
   ;; D15: tests to confirm resetting behavior... These are  (were?) useful in the context of using hashmaps  (and also?) ratoms/defcells to store the to-do list... These may be useful later, but I am archiving these for now  
   (reset! maria.user/list-1 single-item-list-1)
   (reset! maria.user/demo-list-1 single-item-list-1)
   (comment (def single-item-list-2 (update-list new-item-tx-2)))
   )

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
   ;; Note: the above  "fix issue" is a great example of a non-issue, it's more of a comment, rather than a dev-task, and doesn't necessarily give clarity in terms of an 'effective path forward'  
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

(l/conduct-focus-on-list! {:input-list test-list-2})



#_(def three-items-first-marked
    ;; TODO: relocate to demo data namespace
    (set-nth-ready-1 {:input-list three-item-list :n-index 1}))

;; TODO: Test to confirm that the correct auto-markable item index-key (the first item, with a index-key of 0) is returned for a list with three items of 'new' status
#_(= 
   ;; TODO: convert to a deftest
   ;; TODO: relocate to test namespace
   0 (auto-markable-index {:input-list three-item-list}))

;; TODO: Test to confirm that the correct auto-markable item index-key (the second item, with an index-key of 1) is returned for a list with three items where the first item has a status of 'done' and the other two items have a status of 'new'
#_(= 1 (auto-markable-index
        ;; TODO: modify this example to be a list with the first item marked as complete, where the correct index-key returned is expected to be 1, for example by calling  "conduct-focus" on the the 'three-items-first-marked' list 
        {:input-list three-items-first-marked}))

;; TODO: Confirm via a test that the first item added to an empty list is auto-dotted [dev-task]
;; TODO: Relocate user story below to the user stories namespace user stories hashmap [mgr-task]
;; - [ ] User story: The user can, upon adding a new item to an empty list, see that the newly added item has been auto-marked as  "ready" 

#_(= 
   ;; TODO: convert to a deftest
   ;; TODO: relocate to test namespace
   (update-list {:action :append-new 
                 :input-list @demo-list
                 :new-item (create-new-item {:input-text (demo-texts :d)})})
   {1 {:text "study Japanese" 
       :status :ready}}
   )

#_(defcell demo-list-2 
;; demo list with several items inside for list testing purposes
  {1 {:text "a" :status :new} 
   2 {:text "b" :status :new} 
   3 {:text "c" :status :new}})




