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


(defn valid-index?
  "returns true for valid index values, else returns false"
  [n]
  (and (integer? n) (>= n 0)))

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
  "returns true if a number if between a min and a max, inclusive"
  [{:keys [valid-floor valid-max input-n]}]
  (and (>= input-n valid-floor) (<= input-n valid-max)))


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

