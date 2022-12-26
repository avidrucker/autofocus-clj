(ns autofocus-sketch-nov-11-2022.jasich)

(in-ns 'jasich.scroll)
;; custom smooth-scroll scrollIntoView script by GitHub user "jasich"
;; source gist: https://gist.github.com/jasich/21ab25db923e85e1252bed13cf65f0d8
;; TODO: Implement scrollIntoView solution that does not use a third party script

#_(do)
;; TODO: confirm whether I can switch namespaces once or more successfully to save code to particular custom namespaces inside of  **one** do form
 ;; Research results: It appears that having the  (in-ns) form inside of a  (do) form does not result in the desired, error-free namespace switching, at least in Maria Dot Cloud, as far as 2022/11/05... More research, such as running this code from a local REPL, may give more insights into this. 

(def speed 500)
 
(def moving-frequency 15)
 
(defn cur-doc-top []
  (+ (.. js/document -body -scrollTop) (.. js/document -documentElement -scrollTop)))

(defn element-top [elem top]
  (if (.-offsetParent elem)
    (let [client-top (or (.-clientTop elem) 0)
          offset-top (.-offsetTop elem)]
      (+ top client-top offset-top (element-top (.-offsetParent elem) top)))
    top))

(defn scroll-to-id
  [elem-id]
  (let [elem (.getElementById js/document elem-id)
        hop-count (/ speed moving-frequency)
        doc-top (cur-doc-top)
        gap (/ (- (element-top elem 0) doc-top) hop-count)]
    (doseq [i (range 1 (inc hop-count))]
      (let [hop-top-pos (* gap i)
            move-to (+ hop-top-pos doc-top)
            timeout (* moving-frequency i)]
        (.setTimeout js/window (fn []
                                 (.scrollTo js/window 0 move-to))
                     timeout)))))

;; TODO: test out using a custom (sub?) namespace within a pre-defined namespace such as the 'maria namespace
(in-ns 'maria.custom-gui)

(do 
 ;; DONE: implement naive scroll to top
 ;; DONE: implement naive scroll to bottom
 ;; TODO: Implement scroll to bottom w/o magic numbers
 ;; TODO: implement scroll to heading  (statically or dynamically rendered)  element A, element B, ... etc.
 
 (defn scrollDownABunch [] (.scrollTo js/window 0 20000)) 
 
 (defn scrollToPageBottom [] (.scrollTo js/window 0 50000)) 
 
 )
