(ns af.calc
  (:require
   [af.data :as d]
   [clojure.set :as cs]
   [clojure.string :as s]))


(def DEBUG-MODE-ON false)


(defn gen-menu-item-string [index item-string]
  (str (inc index) ": " item-string))


;; TODO: pass NEWLINE in as `separator` key value
;; TODO: refactor NEWLINE in `gen-menu-string` to instead be an input map arg
;; 2023_01_21 TIL: The `str` function is not appropriate for converting
;;      keywords into strings. Instead, use the `name` function.
(defn gen-menu-string
  [{:keys [menu-options menu-mappings separator]}]
  (s/join separator (map-indexed
                gen-menu-item-string
                (map menu-mappings menu-options))))


;; Question: How can I diff between two vectors of keywords in Clojure?
;; TODO: implement menu options list generation dynamically based on list state
;; TODO: refactor this funmction to take in bools `prioritizable-list?` and `doable-list?` as map arg inputs rather than as internally calculated values (ie. calc externally and pass in instead)
;; TODO: deprecate this function to instead use `calc-valid-menu-options`
(defn- invalid-menu-options
  "This is used a helper to `get-valid-menu-options` by
  indicating which menu options should be removed."
  [{:keys [prioritizable? actionable?]}]
    (cond
      (and prioritizable? actionable?) [] ;; remove nothing
      ;; remove prioritize
      (and (not prioritizable?) actionable?) [d/PRIORITIZE]
      ;; remove do
      (and prioritizable? (not actionable?)) [d/DO]
      ;; remove both options
      (and (not prioritizable?) (not actionable?))
      [d/PRIORITIZE d/DO]))


(defn get-valid-menu-options
  "By reviewing the input-list, this function can determine
  which menu options should be added to the menu-options list."
  [{:keys [all-menu-options prioritizable? actionable?]}]
  (when DEBUG-MODE-ON (println "getting valid menu option..."))
  (let [result (vec (cs/difference
        (set all-menu-options)
        (set (invalid-menu-options {:prioritizable? prioritizable?
                                    :actionable? actionable?}))))]
    result))


;; ----------------------------------
;; Question: How can I implement my own custom sorting order in Clojure?

;; TODO: relocate to af.utils namespace
;; source: https://stackoverflow.com/questions/4830900/how-do-i-find-the-index-of-an-item-in-a-vector
;; native Clojure implementation of "index-of"
(defn find-thing [needle haystack]
  (first (keep-indexed #(when (= %2 needle) %1) haystack)))

;; REPL testing
;; (find-thing QUIT menu-options-order)
;; (find-thing :potato menu-options-order)

(defn sort-menu-options
  [{:keys [input-unsorted input-order]}]
  #_(println ["...attempting to sort..."
            "coll to be ordered: " input-unsorted
            "correct ordering: " input-order])
  (sort-by #(find-thing % input-order) input-unsorted))


;; TODO: replace the old `get-valid-menu-options` function
;;       with the new `calc-valid-menu-options` function
;; DONE: create a threading macro function which
;;       appends valid menu choices onto the base menu 
(defn calc-valid-menu-options
  [{:keys [input-base-menu prioritizable? actionable?]}]
  (let [menu-additions (-> #{}
                           (conj (when prioritizable? d/PRIORITIZE))
                           (conj (when actionable? d/DO)))
        combined-menu (cs/union (set input-base-menu) menu-additions)
        _             (println ["menu additions" menu-additions
                                "combined menu" combined-menu])
        sorted-final-menu (sort-menu-options
                           {:input-unsorted combined-menu
                            :input-order d/all-menu-options-sorted})]
    (remove nil? sorted-final-menu)))


;; TODO: convert this to a test
#_(calc-valid-menu-options {:input-base-menu d/base-menu-options
                          :prioritizable? true
                          :actionable? true})


;; REPL testing
#_(def order-test [:boy :tree :apple :computer :frog])
#_(def to-be-sorted-test [:frog :apple :tree])
#_(find-thing :apple order-test)
#_(sort-by #(find-thing % order-test) to-be-sorted-test)


;; TODO: convert to test block in an appropriate test namespace
(comment
  (def test-list-empty [])

  (def test-list-done-ready-new
    [{:t-index 0, :text "b", :status :done}
     {:t-index 1, :text "c", :status :ready}
     {:t-index 2, :text "d", :status :new}])

  ;; (invalid-menu-options {:input-list test-list-empty})
  (def menu-opts-a (get-valid-menu-options
                    {:input-list test-list-empty
                     :all-menu-options d/all-menu-options-sorted}))

  ;; (invalid-menu-options {:input-list test-list-done-ready-new})
  (def menu-opts-b (get-valid-menu-options
                    {:input-list test-list-done-ready-new
                     :all-menu-options d/all-menu-options-sorted}))

#_(do
  menu-opts-a
  ;; menu-opts-b
  )

  (sort-menu-options {:input-unsorted menu-opts-a
                      :input-order d/all-menu-options-sorted})

  (sort-menu-options {:input-unsorted menu-opts-b
                      :input-order d/all-menu-options-sorted})
)  



