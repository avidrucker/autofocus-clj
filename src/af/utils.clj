(ns af.utils)


(defn print-and-return
  "Potentially useful as a debugging function, also useful for cases where printing to the console before returning a value is desired."
  [{:keys [input-string return-item is-debug? debug-active?]}]
  (cond
    ;; print debug statements when debugging is activated
    (and is-debug? debug-active?)
    (println input-string)

    ;; print anything that isn't a debug statement
    (not is-debug?)
    (println input-string))
  return-item)


(defn non-neg-int?
  "returns true if a number is an integer AND is 0 or greater

Note: This function appears to fail on positive decimal numbers that are equivalent to integers, such as 5.0 ... This is not a critical issue, as non-integers are outside of project scope."
  [n]
  ;; Q: What are other ways this function can be written (e.g. in idiomatic, effective, and/or simple ways)?
  ;; (comp (not (neg?)) integer?)
  (and (integer? n) (>= n 0)))

;; TODO: convert this to a test
#_((every-pred true?) 
 (non-neg-int? 5) 
 (non-neg-int? 0) 
 ((every-pred false?)
  (non-neg-int? -2)
  (non-neg-int? 3.14159)
  (non-neg-int? nil)
  (non-neg-int? false)
  (non-neg-int? true)
  (non-neg-int? [])
  ;; (non-neg-int? 5.0) ;; failing test case... probably though, this test does not matter.
  ))


(defn index-val? [n]
  ;; DONE: Implement index? to represent the accepted range of valid *and* invalid index values
  ;; TODO: rename this to be valid-index?
  ;; TODO: confirm that this function always returns only true or false
  (and (integer? n) (>= n -1)))

;; TODO: convert this to a test
#_(and ;; Q: What are the diffs between `and` and `(every-pred true?) ` 
 ;; TODO: convert this `and` block to a series of `is` tests
 (index-val? 5) 
 (index-val? 0)
 (index-val? -1) 
 (false? (index-val? -2))
 (false? (index-val? 3.14159))
 ;; This is an example of testing underlying "beyond application boundaries" implementation, rather than the program's domain logic.
 ;; (false? (index-val? 5.0)) 
 )


(defn find-first
  ;; https://stackoverflow.com/a/10192733
  ;; TODO: relocate to utils namespace
  ;; TODO: experiment using 'some' instead of 'first filter', see https://clojuredocs.org/clojure.core/some#example-542692c6c026201cdc326940
  [pred-fn coll]
  (first (filter pred-fn coll)))


(defn in-bounds-inclusive?
  ;; TODO: confirm that this function works as expected
  ;; TODO: relocate to utils namespace
  "returns true if a number if between a min and a max, inclusive"
  [{:keys [valid-floor valid-max input-n]}]
  ;; TODO: investigate if kondo-clj would find  (if x true false) to be an 'anti-pattern' or 'redundant'
  (and (>= input-n valid-floor) (<= input-n valid-max))
  ;; (if (and (>= input-n valid-floor) (<= input-n valid-max))
  ;;   true
  ;;   false)
  )


#_(true?
   ;; TODO: relocate to test namespace, call in test-runner namespace and again just below FuT
   ;; TODO: conver these to a deftest
   ;; Test in-bounds-inclusive? to confirm that it correctly evaluates below (out-of-bounds), inside (in-bounds), and above (out-of-bounds)
   (true? (in-bounds-inclusive? {:valid-floor 0 :valid-max 1 :input-n 0}))
   (true? (in-bounds-inclusive? {:valid-floor 0 :valid-max 1 :input-n 1}))
   (false? (in-bounds-inclusive? {:valid-floor 0 :valid-max 1 :input-n -1}))
   (false? (in-bounds-inclusive? {:valid-floor 0 :valid-max 1 :input-n 2})))


;; TODO: Inspect demo list to confirm it starts out empty
;; Q!: What is the current snapshot value of the current list?
;; Q!: What is the "live-cam" reactive display of the current list?
;; (comment
;;   maria.user/list-1
;;   maria.user/demo-list-1
;; )


;; IDEA: TODO: create a custom debug macro that takes in a collection
;;       of bindings/names/symbols, and adds each one into a single
;;       println statement as follows:
;;       (println ["binding-1-name: " binding-1-value
;;                 "binding-2-name: " binding-2-value
;;                 ...])

