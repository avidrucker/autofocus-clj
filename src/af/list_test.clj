(ns af.list-test
  (:require [af.list :as sut]
            [clojure.test :refer [deftest is]]))


^{:description "Unit tests for the t-next function in the af.list namespace."}
(deftest ^:unit t-next-test ;; deftest defines a single test
  (is (= 0 (sut/t-next {:target-list [1]})) "Empty list should return a t-next value of 0.") ;; is defines a single assertion
  (is (= 3 (sut/t-next {:target-list [1 2 3]})) "A list with 3 items should return back a t-next value of 3."))

(deftest ^:unit practice-test
  (is (= 0 (- 1 1)) "1 minus 1 equals 0."))


