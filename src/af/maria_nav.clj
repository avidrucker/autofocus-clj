(ns af.maria-nav)
;; original name 'maria.user

(def global-nav-index
  ;; mutable!
  ;; DONE: relocate the creation of this binding to above the 000 section, and change the initial value to be 0 instead of 1
  ;; TODO: consider the benefits of changing initial value to be -1 instead of 0, to account for swap! inside of a do block 
  "A running 'global document counter' (generator?) that is to be incremented each time a given function is called, to feed to render-nav-at-index. As of D31, the atom value starts at 2, in order to account for the statically rendered 1st and 2nd items, as well as the 'increment and then render' nature of the nav-rendering function (contrary to its current name of 'render-and-inc')."
  (atom 2))

#_(do
 ;; deref the atom to inspect its value
 @global-nav-index)

(def anchor-style
  ;; CONSTANT
  "styling for the doc-nav jump links" 
  {:display "inline" :color "blue"
   :text-decoration "underline" :cursor "pointer"
   :padding 0 :margin 0})

(defn render-link-to
  ;; TODO: determine whether I can refactor to use dynamic polymorphism dispatch (instead of using an `if`) via either hashmaps or multi-arity function, & if so, do it, if not, document the reason(s) why 
  "a function to render an individual link with a hashmap input with a function, a text label for the link, and the target element ID" 
  [{:keys [f target-id label-text]}] 
  (let [f-to-bind
        (if (nil? target-id)
          ;; `_e` is not used here, but appears to be necessary for use as an on-click handler...
          ;; Q: What does `_e` stand for, 'event', 'element', something else?
          (fn [_e] (f)) 
          (fn [_e] (f target-id)))]
    ;; `html` in Maria Dot Cloud is an alias for `chia.view.hiccup/element`
    (html [:p {:style anchor-style
               :on-click f-to-bind}
           label-text])))

;; CELL RENDERINGS

#_(cell (html 
       [:setion 
        [:h2 {:id "000"} "000: Top of Document"]
        ;; TODO: figure out how to get first jump link to work as desired
        [:div 
         (render-link-to 
          {:f jasich.scroll/scroll-to-id
           :target-id :001
           :label-text "Code Start Next >"})]]))

#_(cell (html [:section
             ;; TODO: figure out how to wire up the jump links as desired
             [:h2 {:id "001"} "001: Top of Code"]
             [:div
              [:span {:style anchor-style} "< Prev 000"]
              [:span " ... "]
              [:span {:style anchor-style} "002 Next >"]]
             [:div [:span "Top of Doc ... Overview"]] 
             ]))

#_(cell (html [:section
             [:h2 {:id "002"} "002: Overview"]
             [:div "< Prev 001 ... 100 Next >"]
             [:div "Code Start ... Prjct Mgmt"]]))

;; DOCUMENT NAVIGATION AS DATA

(def doc-nav
  ;; TODO: remove 'name collisions', i.e. make sure each document navigation value is unique
  "I am encapsulating the 'Document Navigation' as data. This data *will* change over the course of this project. I've decided to use the current number keywords to evoke the college course '101' naming convention, as well as to bucket sections into groups - key-value pairs ending in 00 are the 'chapter headings', and the rest are 'sub-headings'. I believe that, by using such a naming & nesting structure, I will gain some flexibility *and* convenience of categorizing/labeling semantic labels as well as styling by chapter/namespace." 
  '({:000 "Top of Doc" ;; Top of Document 
     :001 "Code Start" ;; Top of Code
     :002 "Overview" ;; Document Navigation and Project Outline 
     }
    {:100 "Prjct Mgmt" ;; Project Manangement 
     :101 "Stories" ;; User Stories
     :102 "Tickets" ;; also called  "Dev Tasks" 
     :103 "Bugs" 
     :104 "Features"
     :105 "Enhancements" 
     }
    {:200 "Data" 
     :201 "Domain" ;; AutoFocus Domain Data (marks, items, lists, list interaction rules, text content)
     :202 "Non-Domain" ;; Data that isn't part of the AutoFocus domain 
     } 
    {:300 "Logic" 
     :301 "Utilities"
     :302 "Domain" ;; Domain Logic  (utility functions, list & app state transitions, item & list manipulation/processing)
     ;; TODO: Create one namespace for each application boundary: UI  (Hiccup)  rendering, user input handling, domain constants, string/item/list formatting, item logic, list logic, list modes logic  (finite state machine), app state/menus logic, document navigation widget, project management, tests
     :303 "Items"
     :304 "Lists"
     :305 "Modes" ;; list interactions such as viewing, adding, reviewing, focusing
     :306 "App States" 
     }
    {:400 "I/O" ;;  Input Output 
     :401 "Interface" ;;  "UI" User Interface rendering  (the output) 
     :402 "User Input" ;; User input handling
     }
    {:500 "Testing"
     ;; TODO: merge/convert demos into tests
     :501 "Demos" ;; Initial rough testing w/ manual/concrete data
     :502 "US Tests" ;; User Stories as Tests: testing w/ user/app layer APIs
     :503 "Bug Tests" ;; Bug Fixes as Tests
     }
    {:600 "Code End" ;; The code stops here, buster!
     :601 "Doc Bottom" ;; Bottom of Document 
     }))

(def sorted-nav
  "I sort the document navigation map into a sorted, flattened list format. This will help me to render out the individual items as both directory jump links as well as clear and easy-to-read headings." 
  (seq (into (sorted-map) (apply merge-with flatten {} doc-nav))))

(def nav-texts
  (map second sorted-nav))

(def nav-text-lengths 
  (map count nav-texts))

(def nav-texts-over-len-10
  ;; TODO: run this as a validation test to confirm that doc-nav items are desirably short in length
  "confirms that all nav-texts are of length 10 or shorter" 
  (filter #(> % 10) nav-text-lengths))

(defn find-dups [coll]
  (filter #(> (second %) 1) (frequencies coll)))

(find-dups
 ;; TODO: rename duplicate section headers to reduce ambiguity in content navigation
 nav-texts)


;; rendering out of the items in doc-nav as a flat list of key-value pairs
#_(cell (html 
       [:ul 
        (for [item sorted-nav] 
          [:li (str (first item) ;; str name first
                    ": " 
                    (second item))])]))

(comment (cell 
          ;; DONE: Visually block out semantic Hiccup widget w/ elements that have actual link styling & "finished/polished" appearance, but no real associated data
          (html [:section
                 [:h2 "Heading 2"]
                 [:p {:style anchor-style} "< Prev"]
                 [:span " ... "] 
                 [:p {:style anchor-style} "Next >"]])))


(defn render-nav-section-widget 
  ;; TODO: split up this function into smaller, modular pieces to compose together
  ;; DONE: Create modular widget function which takes input data as arguments 
  ;;;; TODO: implement conditional logic to handle literal edge cases where there is either no previous jump link or no next jump link  (fix now existant bug on edge cases) 
  ;; TODO: test all three formats  (A, B, & C)  that this widget can display in
  ;; TODO: refactor this code to remove extraneous if/when forms, and instead use dynamic polymorphism
  "for given named input keys, this widget will either render either A, B, or C format:
A. current title and next jump link
B. current title, previous jump link, and next jump likn
C. current and previous jump link" 
  [{:keys [current-name current-id
           prev-name prev-id
           next-name next-id]}]
  (let [heading2
        [:h2 {:id (name current-id)
              :style {:padding-top "3em"}} (str current-id ": " current-name)] ;; {:id (name current-id)}
        
        has-prev-jump (not (nil? prev-name))
        has-next-jump (not (nil? next-name)) 
        ;; TODO: locate edge case bug for widget rendering
        _ (comment (println ["for id... '" current-id 
                             "' ... and name '" current-name "'..." 
                             "has-prev-jump:" has-prev-jump
                             "has-next-jump:" has-next-jump])) 
        
        ;;;; TODO: if the above does not fix the issue, try wrapping the assignment to prev-jump in a when form with 'has-prev-jump' as the predicate clause
        prev-jump
        (when (true? has-prev-jump)
          (render-link-to
           {:f jasich.scroll/scroll-to-id
            :target-id (str prev-id) 
            :label-text (str "< Prev " prev-id)}))
        
        next-jump
        (when (true? has-next-jump)
          (render-link-to
           {:f jasich.scroll/scroll-to-id
            :target-id (str next-id) 
            :label-text (str next-id " Next >")}))]

    (cell (html [:section heading2 
                 [:div ;; test ;; {:style {:border "1px solid white"}} 
                  (when (true? has-prev-jump) prev-jump) 
                  [:span ;; test ;; {:style {:display "inline" :border "1px solid blue"}} 
                   " ... "]
                  (when (true? has-next-jump) next-jump)]
                 [:div 
                  [:span (str 
                          (when (true? has-prev-jump) prev-name)
                          " ... " 
                          (when (true? has-next-jump) next-name))]]]))))

;; TODO: convert this to a test
(comment (render-nav-section-widget
          ;; DONE: Render out widget with dummy data
          ;; TODO: confirm that the appropriate numerical ID has been assigned to the 'Current Topic' heading element
          {:current-name "Current Topic" 
           :current-id "001" 
           :prev-name "Prev Topic" 
           :prev-id "000" 
           :next-name "Next Topic" 
           :next-id "002"}))


(defn render-nav-at-index
  ;; DONE: render out one actual section nav widget successfully
  ;; TODO: resolve bug where
  ;; TODO: create jump link widgets for  "return to top of code",  "go to bottom of code",  "top of document", and  "bottom of document"    
  "This function will render the current nav widget.
  This function takes as input a hashmap with two key-value pairs:
  a list of nav items, and an index i
  With these inputs, the function renders out the heading text for the item
  at index i with an ID that will serve as a target for other jump links.
  The function will also render jump links to the previous i-1 and next 1+1 nav widget headers.
  A rough example would look as follows:

  # 501 Demos
  <-- Prev (500 Testing) ... Next (502 User Stories as Tests) -->
  "
  [{:keys [input-index input-nav]}]
  (let [current-topic (nth input-nav input-index nil) 
        prev-topic (nth input-nav (dec input-index) nil) 
        next-topic (nth input-nav (inc input-index) nil)
        _ (comment (println ["current-topic: " current-topic
                             "prev-topic: " prev-topic
                             "next-topic: " next-topic
                             "input-index: " input-index]))]
    ;;;; TODO: conditionally supply keys as appropriate for where/when there exists a previous, next, or both  (neither case not necessary for now)
    (cond
     ;; no previous 
     (= input-index 0)
     (render-nav-section-widget 
      {:current-name (second current-topic) 
       :current-id (name (first current-topic)) ;; name 
       :next-name (second next-topic)
       :next-id (name (first next-topic)) ;; name
       })
     
     ;; no next
     (= input-index (dec (count input-nav)))
     (render-nav-section-widget 
      {:current-name (second current-topic) 
       :current-id (name (first current-topic)) ;; name 
       :prev-name (second prev-topic)
       :prev-id (name (first prev-topic)) ;; name 
       })
     
     ;; previous and next both exist
     :else
     (render-nav-section-widget 
      {:current-name (second current-topic) 
       :current-id (name (first current-topic)) ;;  name 
       :prev-name (second prev-topic)
       :prev-id (name (first prev-topic)) ;; name
       :next-name (second next-topic)
       :next-id (name (first next-topic)) ;; name
       }) 
     )))

;; TODO: convert this to a test
#_(render-nav-at-index
 ;; note: this input-index value is a magic number
 {:input-index 1
  :input-nav sorted-nav})


(defn render-nav-and-inc
  ;; TODO: rename to be render-nav-and-inc! to make clear that there will be side-effects and mutation
  ;; TODO: Implement a render-nav-and-inc function for convenience
  ;; TODO: Rename this function to make it clear that the incrementing happens first... Unless you change the logic to instead start with index  "zero" rather than  "negative one"  
  "Note: The incrementing actually happens first.

This function intentionally breaks the single responsibility principle for the sake of convenience. It's purpose is to render one instance of the nav widget, and then to increment the global-nav-index by one so that, when this function is called again, the next nav item will be rendered, and so on." 
  [{:keys [input-nav]}]
  (do 
   ;; (println (str "Processing doc-nav: " input-nav))
   ;; (println (str "BEFORE: about to update global-nav-index which is at " @global-nav-index))
   ;; increment the global-nav-index atom by one
   (swap! global-nav-index inc)
   ;; (println (str "AFTER: updated global-nav-index which is at " @global-nav-index))
   (render-nav-at-index
    {:input-index @global-nav-index
     :input-nav input-nav})))

(comment
  ;; example rendering out of a nav widget
  (render-nav-and-inc
   ;; first modular nav widget ✨
   {:input-nav sorted-nav})

  ;; TODO: write test to confirm that after rendering a nav widget that the global counter has been incremented
  @global-nav-index

  ;; note: instead of testing indecies, we can also simply call the function again to confirm that we get the next nav widget
  (render-nav-and-inc {:input-nav sorted-nav})

  #_(render-link-to
 ;; DONE: confirm that dynamic link render function binding is successful for the NO-target-id case
   {:f scrollToPageBottom :label-text "Go To Page Bottom 2"})

;; TODO: add this to list of learning examples for future Maria Dot Cloud learners
  #_(cell (html [:h2 {:id "random"} "I am a test jump link target"]))

  #_(cell
   (html
    [:p {:style anchor-style
         :on-click
         (fn [e] (scrollToPageBottom))}
     ;; TODO: implement into custom "anchor" style the appropriate mouse pointer on-hover behavior  
     ;; TODO: dev epic: Dev can click on jump links to quickly navigate through a long document
     ;; TODO: dev story: Dev can click on the  "Go To Page Bottom" jump link, no matter how long the page is, and be instantly taken to the bottom of the page
     ;; TODO: dev task: Dev fixes issue where  "Go To Page Bottom" jump link is hard-coded with a  "magic number", instead of a reference to, for example, document.body.scrollHeight
     ;; TODO: dev task: Dev fixes issue where  "Go to Page Top " jump link is hard-coded to go to height zero, rather than to a given heading 
     ;; TODO: dev task: Dev learns how to use ClojureScript JavaScript interop to access document object properities/attributes such as document.body.scrollHeight  ( JS example: window.scrollTo(0, document.body.scrollHeight); ) 
     "Go To Page Bottom"]))

  )

;; How to make a function that tells you what inputs it expects, and complains via a warning/error when it doesn't get what it needs/wants/expects/was promised to receive
;; for example, metadata input/output checking, checking for prescence of XYZ key value pair existence, etc.
(comment
  ;;for example to take a function that looks like this
  (defn func1
    [e]
    (jasich.scroll/scroll-to-id "random"))

  ;;to something like this
  (defn func2
    [{:keys [event]}] ;; or should this be [element]
    (jasich.scroll/scroll-to-id "random"))
)

;; TODO: confirm that the list of namespaces is encapsulating what I need/want it/them to cover
;; Document Navigation:
;; 000 Top of Document, 100 Project Management, 101 Dev Tasks, 102 User Stories, 200 Data (marks, items, lists, list interaction rules, text content), 201 Domain Data, 202 Demo Data, 300 Logic (utility functions, list & app state transitions, item & list manipulation/processing), 301 Utilities, 302 Domain Logic, 400 User Interface (IO), 401 Interface Rendering, 402 User Input Handling, 500 Testing, 501 Demos (Initial rough testing w/ manual/concrete data), 502 User Stories as Testing SSoT (testing w/ user/app layer APIs), 600 Bottom of Document,
;; Note: Vim tips: By using x to delete and save 1 character to memory, $ to go to the end of a line, p to paste, j to move down a line, I can convert a vertical list into a punctuated (comma delineated) single line list

#_(render-nav-and-inc
 ;; TODO: implement skipping entirely of  "chapter names"  (100, 200, 300...)  and stick jump links progressing by only  "sub-chapter names"  (101, 102, 201, 202...)  instead. Note: It may be helpful to simply rename the  "chapter names" to incude the first relevant sub-chapter section instead 
 {:input-nav sorted-nav})

(cell (html 
       [:setion 
        [:h2 {:id "000"} "601: Bottom of Document"]
        ;; TODO: wire up this jump link so it works as desired
        [:div 
         [:span {:style anchor-style} "< Prev 600"]
         [:span " ... "]
         [:span {:style anchor-style} "<< Back 000"]]
        [:div "Code End .... Top of Doc"]]
       ))

