(ns af.list-test
  (:require [af.list :as sut]
            [clojure.test :refer [deftest is]]))


;; (deftest ^:unit practice-test
;;   (is (= 0 (- 1 1)) "1 minus 1 equals 0."))

(def test-items [{:text "apple" :status :new}
                 {:text "banana" :status :new}
                 {:text "cherry" :status :new}
                 {:text "dragonfruit" :status :new}])

^{:description "Unit testing t-next function ..."}
(deftest ^:unit t-next-test ;; deftest defines a single test
  (is (= 0 (sut/t-next {:target-list []}))
      ;; is defines a single assertion
      "Empty list should return a t-next value of 0.") 
  (is (= 3 (sut/t-next {:target-list [1 2 3]}))
      "A list with 3 items should return back a t-next value of 3."))

;; note: only public functions are testable
;;^{:description "Unit testing is-auto-markable-list? ..."}
#_(deftest ^:unit is-auto-markable-list?-test
  (is false
      (sut/is-auto-markable-list?
       {:input-list
        [{:t-index 0, :text "b", :status :ready}
         {:t-index 1, :text "c", :status :new}
         {:t-index 2, :text "d", :status :new}]})
      "A list with a ready item is not auto-markable."))


;; note: only public functions are testable
;;^{:description "Unit testing add-item-to-list function ..."}
#_(deftest ^:unit add-item-to-list-test
  (let [step1 (sut/add-item-to-list
               {:input-item (get test-items 0)
                :target-list []})
        step2 (sut/add-item-to-list
               {:input-item (get test-items 1)
                :target-list step1})]

    (is (= [{:t-index 0 :text "apple" :status :ready}]
           step1)
        "... Can correctly add a single item to an empty list.")
    (is (= [{:t-index 0 :text "apple" :status :ready}
            {:t-index 1 :text "banana" :status :new}]
           step2))))

;;^{:description "Integration testing conduct-focus-on-list function ..."}
#_(deftest ^:unit conduct-focus-on-list-test
  (let [step1 (sut/add-item-to-list
               {:input-item (get test-items 0)
                :target-list []})
        step2 (sut/mark-priority-item-done {:input-list step1})]
    (is (= [{:t-index 0 :text "apple" :status :done}] step2))))

^{:description "Unit testing conduct-focus-on-list function ..."}
(deftest ^:unit conduct-focus-on-list-test
  (is (= [{:t-index 0 :text "apple" :status :done}]
         (sut/mark-priority-item-done
          {:input-list [{:t-index 0 :text "apple" :status :ready}]}))
      "correctly marks the priority item as done for a list with ONLY ONE item.")
  (is (= [{:t-index 0 :text "apple" :status :ready}
          {:t-index 1 :text "banana" :status :done}]
         (sut/mark-priority-item-done
          {:input-list [{:t-index 0 :text "apple" :status :ready}
                        {:t-index 1 :text "banana" :status :ready}]}))
      "correctly marks the priority item as done for a list with MORE THAN ONE item."))


^{:description "Unit testing conduct-focus-on-list function ..."}
(deftest ^:unit get-single-comparison-test
  (is (=
       (sut/get-single-comparison
        {:input-list [{:t-index 0 :text "apple" :status :ready}
                      {:t-index 1 :text "banana" :status :new}]})
       "Do you want to 'banana' more than 'apple'?"
       )))

