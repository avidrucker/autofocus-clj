(ns af.rendering
  (:require [af.list :as l]
            [af.data :as d]))

;; UI rendering logic

(defn gen-ul-from-i-attr
  ;; TODO: move to rendering namespace
  "1. a helper function to reduce amount of typing by the programmer
   2. takes in an input list of ??? (items?) where an attribute keyword is used to transform (ie. map over <_<) the input list
3. currently, this function is being used only to prepend items with custom bullet points in either the GUI or CLI (or both of? which is it?) render contexts" 
  ;; TODO: clarify this function's specific use cases, specific function/role/purpose, inputs and outputs
  ;; TODO: change parameter inputs to be taken in as a hashmap with named key-value pairs
  [input-list attr-keyword]
  [:ul 
   {:style {:list-style "none"}} 
   (for [i input-list]
     ^{:key i} 
      [:li 
       ;;ul li::marker {
       ;;content: '✅ ';
       ;;font-size: 15px;
       ;;}
       (let [new-item (= (:status i) :new)
             prepend-content (if new-item
                               ;; !!! TODO: update logic here to enable rendering of completed items  (either implement it, or it will return as a bug, ooOOoo)
                               ;; TODO: refactor out usage of globally defined constants by adding 'bullet-points' as a parameter(ized?) input
                               (d/bullet-points :outline)
                               (d/bullet-points :dotted)) 
             ]
         (if new-item
           {:style {:color "blue"}}
           {:style {:color "red"
                    ;; :display "list-item" 
                    ;; :list-style-type "U+1F604"
                    }}) 
         (str prepend-content " " (attr-keyword i)) 
         )] 
     )])

;; Web App / Graphical User Interface "GUI" Style

;; TODO: refactor this to return hiccup to be rendered by another function
(defn render-gui-list
  ;; TODO: move to rendering namespace
  [input-list title]
  (cell (html 
         [:div 
          [:p title]
          (gen-ul-from-i-attr input-list :text)])))

(render-gui-list
 ;; TODO: move to UI test namespace
 l/test-list-2 "Test List 2")

#_(cell (html 
       ;; Note: Hiccup elements *must* be wrapped in a parent element to be successfully rendered into HTML by Maria Dot Cloud
       ;; TODO: Research: Q: Is there a way to do React style  "fragments" in Hiccup?  
       [:div 
        [:p "Test List 1"]
        (gen-ul-from-i-attr af.list/test-list-1 :text)]))

;; Command Line Interface "CLI" Style

(defn render-cli-list
  ;; TODO: move to rendering namespace
  "takes in a stringified input list and a list-title, and renders out the Hiccup for the list as it would appear in a 'command-line interface'" 
  [{:keys [stringified-input-list title]}]
  (let [non-vector-list (apply list stringified-input-list)]
;; TODO: refactor this to return hiccup to be rendered by another function
    (cell (html [:div 
                 [:p title]
                 [:p non-vector-list]]))))

(render-cli-list 
 ;; TODO: move to UI test namespace
 ;; TODO: refactor to take in a list as-is  (ie. the stringification happens here when the list is passed to the function)  and with explicitly named inputs as keywords in a hashmap [{:stringified-input-list :list-title}] 
 ;; original inputs: af.list/stringified-test-list-1 "Test List 1"
 {:stringified-input-list (l/stringify-list {:input-list l/test-list-1})
  :title "Test List 1 Render 2"})

(render-cli-list 
 ;; TODO: move to UI test namespace
 {:stringified-input-list (l/stringify-list {:input-list l/test-list-2})
  :title "Test List 2 Render 1"
  })

#_(cell 
   ;; TODO: run code, comment and save OR toss it
   ;; static rendering
   (html [:div 
          [:p "Test List 1"]
          [:p af.list/stringified-test-list-1]]))





