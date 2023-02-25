(ns af.data
  ;;  "It's just data."
  ;; Domain model
  ;; TODO: review namespace for privatization
  ;; TODO: review namespace for data spec candidates
  )

(def WELCOME-MESSAGE
  "Welcome to AutoFocus, a time management system designed by Mark Forster.\nPlease start by adding a to-do item to your list.")

;; TODO: find a way to abbreviate and shorten `about-texts` without losing valuable/critical meaning/context.
(def about-texts
  {:overview-and-summary "About AutoFocus

The AutoFocus algorithm and task management system was originally created by Mark Forster. This application was developed by Avi Drucker.

The way AutoFocus works is roughly as follows:
1. make a list by adding items to it
2. prioritize the list as directed by the AutoFocus algorithm
3. do the things on the list

For an example of AutoFocus in action, please select 'See Real Life AutoFocus example' from the menu. For a detailed explanation of the AutoFocus algorithm, please select 'See The AutoFocus Algorithm Steps' from the menu."
   :high-level-what-and-why "AutoFocus: What It Is and Why It's Powerful

The AutoFocus algorithm helps you (1) determine what you are most ready for and wanting to do at any given time, and (2) to take a bias towards action on such tasks. Many task management systems (including paper to-do lists) suffer a usability issue in that they tend to get cluttered and messy, and they are easily subverted to serve procrastination. AutoFocus is designed to fight against such procrastination."
   :detailed-steps "How to AutoFocus, In Detail

1. Add one or more to-do items to your list (always marking* the first 'new' item as 'ready' if and only if there are no 'ready' items)
2. Make a decision to either prioritize** your li3st, or to start taking action on the marked (ready) item, marking* items as done when you have finishing working on them, as well as re-writing to-do items at the bottom of the list if you have remaining work left to do after stopping activity on a given item.
3. Repeat steps 1 and 2 until you reach the end of your paper or computer screen, at which point you can start a new page, transferring over any items that you have yet to do

*Avi's suggested marks are, for paper and digital text respectively:
new - an open outline circle like ◯, empty brackets [ ]
ready - a circle with a dot in the center of it like ⦿, brackets with an 'o' inside [o]
done - a fully filled-in circle like ⬤, brackets with an 'x' inside [x]

** Prioritizing one's list is done by comparing the bottom-most marked as ready item (the 'priority item') with the unmarked/new items that follow it. The process is done by asking the question, 'Do I want to B more than A?' where A is the priority item and B is the unmarked/new item that comes next after A. If the answer is yes, then B gets marked as ready and becomes the new 'priority item'. If the answer is no, then we simply move on to compare A with the next unmarked/new item in the list, if it exists, skipping any items that are marked as done. This process is repeated until once for each unmarked/new item below B until there are no more unmarked/new items left to compare against the priority item."
   :real-world-example "Let's say that you have three things that you know you need to do: 'finish trig homework', 'wash the dishes', and 'pack for tomorrow's trip'. You add these three things to your list, dotting the first item as soon as you add it to your list to mark it as 'ready'. This first dotted item is also now the 'priority item'. You can now prioritize the entire list by asking for each unmarked 'new' item, 'Do I want to do this item more than the 'priority item'?' Let's say you answer 'no' to dishes and 'yes' to packing. Your list will have dotted the first and third items, with the third item being the current priority item. Now that we've reviewed/prioritized to the bottom of the list, it is a good time to start taking action on some of the items. You do your packing, and, once you stop packing, you mark it as 'done'. Then, you ask yourself, 'Am I 100% done with this task?' Let's say in this case the answer is 'no'. Because the answer is 'no', we will add a duplicate of the third item to the bottom of the list. The final list will look like this:
- [o] finish trig homework
- [ ] wash the dishes
- [x] pack for tomorrow's trip
- [ ] cont. (x1) pack for tomorrow's trip"})

(def marks 
  {:doc-strings {:new "Items that have been added to the list, but are not necessarily yet ready to be started, internally also called: 'unmarked' or 'clean'" 
                 :ready "Items marked as 'ready to do', also called 'marked' or 'dotted'"
                 :done "Items that have been focused on. Also called 'completed', these items could be toggled hidden, or archived..."} 
   
   ;; "A dictionary of mark symbols useful for rendering bullet journal style list items in a command line interface  ('CLI')"
   :cli-symbols {:new " " :ready "o" :done "x"}

   ;; Item status marks to be used for the graphical user interface ("GUI") version of AutoFocus
   :gui-symbols {:new "◯" :ready "⦿" :done "⬤"}
   
   ;;  "A stylistic guide on how to render AF items in a GUI"  
   :gui-styling {:new {:bullet "Open circle"
                       :doc-style "No special text formatting"} 
                 :ready {:bullet "Circle with a dot in it"
                         :doc-style "Special case: The bottom-most ready item is rendered with bold styling"}
                 :done {:bullet "A completely filled-in circle"
                        :doc-style "Strike-through, optional 50% opacity"}}})


#_(def shapes
    ;; TODO: reenable by uncommenting this block/s-expression
    ;; TODO: create (eg. malli) spec from this hashmap
    ;; TODO: move utils above, or type/spec/data shape data below, to resolve  "hasn't been defined" undeclared var error 
    {:shapes {:todo-item {:text {:type string?
                                 :doc-string "The text content of a todo-item"} 
                          :status {:type keyword?
                                   :valid-values #{:new :ready :done}}
                          :t-index {:type non-neg-int?} 
                          :times-cloned non-neg-int?}
              
              ;; Note: Sequential ordering or indexing appears to be necessary for the  AutoFocus "review" algorithm to correctly compare items... Q: How can this be definitively confirmed?
              ;; TODO: implement todo item
              ;; TODO: implement todo item list
              ;; TODO: imeplement app state  (modes such as 'adding', 'reviewing, 'focus' etc. + list state) 
              ;; TODO: implement UI state  (logic to show this or that component, screen, etc.) 
              ;; TODO: implement program state  (loading program, running programming, quitting) 
              :todo-list {:LIST-list {:type list?
                                      :doc-string "The list that the user adds their todo-items to."}
                          :IDX-last-marked-ready {:type index-val?
                                                  :doc-string "The index of the most recently item marked as 'ready' to do (i.e. focus on)."} 
                          :IDX-last-done {:type index-val?}}}})

(def initial-defaults-notes
  {:t-index {:notes "-2 is used as an initial :t-index value for items that are not yet appended, to differentiate from function return values of -1  (e.g. search / find / indexOf) ... That said, this would violate the above-mentioned todo-item shape... Leaving as-is for now, as it is a bit early to decide on"
             :todos "TODO: Determine whether, where, and when initial defaults are necessary, beneficial, and effective  (if not, consider removing either spec, initial-default, or both)" 
             :questions "Q: Will spec/shape checking apply after a todo-item is initialized, or once it is completely made  (i.e. from user input, or as a result of a duplication/clone operation)?  (this may affect the utility/efficacy of such a check)"}
   :times-cloned {:notes "This value should be set to zero upon 'publishing' a new todo-item to a list, unless the new item is a clone/duplicate. If a new item is a clone/dup, thne this value should be set to one plus the clone-source's :times-cloned value"
                  :todos "TODO: Determine effective, simple resolution on how to set the :times-cloned value, and the value proposition for its existence"
                  :brainstorming "IDEA: Complex cases: all items of the same text are tallied up --> Note: This leads to a conundrum of having multiple items with the same text value, where some are clones/duplicates, and others are not (some are made as a result of incomplete/unfinished work from focus sessions, and others just happen to be repeat tasks). Potentially useful rule/check that may be helpful/useful later: The times-cloned should never be higher than 1 higher than the highest cloned item of the same text value"}})

;; TODO: figure out how to fold sections of Clojure code in Emacs
(def copy-text
  ;;TODO: assess which strings are needed only for console (command line) app
  {:done-focusing "Once you finish focusing on this task, tap the enter key." ;; next button on mobile
   :skipping-review "Skipping review (list is not reviewable)..." ;; console log
   :empty-list "> There are no items in your to-do list." ;; empty state msg to user !!!
   :make-menu-selection "Please make a menu selection." ;; CLI user prompt !!!
   :automarking-first-markable "Auto-marking first markable item..." ;; console log
   :cant-automark "Unable to auto-mark. Returning list as is..." ;; console log
   ;; :cant-focus "Cannot focus at this time. Mark (dot) an item first." ;; note: this msg refers to an impossible state. D34 note: If a user cannot focus, it means that there are no focusable items, which means that new items need to be added first (of which, one will always be auto-dotted by default). TODO: evaluate whether or not you will need/want some content telling the user that, if they are trying to focus but there are no focusable items, that their next meaningful action would be to make a new to-do item
   :read-about-app "This is a stub for About AutoFocus..." ;; TODO: write out about content !!!
   :error-reading-state "It appears there was an error reading state..." ;; error log
   :error-mutating-state "It appears there was an error mutating state..." ;; error log
   :setup-demo-data "Setting up starting demo data..." ;; console log
   :demo-data-complete "... demo data setup is complete." ;; console log
   :want-to-hide-completed "Do you want to hide completed items? ([y]es / [n]o) " ;; CLI prompt ;; note: hiding of completing items is a bonus enhancement, and not part of the core autofocus feature-set
   :enter-new-item "Please enter a to-do item: " ;; CLI prompt, SPA prompt
   :index-at-end-of-arr "Index is at the end of the array: returning not found..." ;; console log
   :not-markable-or-reviewable "List is neither markable nor reviewable." ;; console log
   :cant-mark-or-review-because-no-items "Your list is empty. First, add some items." ;; empty state msg to user !!!
   ;; TODO: create dev-tasks for the various save features: auto-save, manual save, save to disk, save to browser cache
   ;; :no-hideable-found "No hideable items found. First, focus on items to complete them."
   ;; :confirm-hiding "Hiding hideable items..."
   ;; :nothing-to-save "There is no list data yet to save."
   ;; :nothing-to-toggle-hide "No hideable/showable items found. First, focus on items to them."
   :list-header "AUTOFOCUS LIST" ;; CLI display label text !!!
   :bye-message "See you!" ;; CLI msg to user !!!
   :fence "----------" ;; CLI text element to render !!!
   :menu-header "MAIN MENU" ;; CLI display label text !!!
   ;; :hiding-all-hideable "Hiding all hideable items..."
   ;; :showing-all-showable "Showing all showable items..."
   ;; :pwa-made-with "This AutoFocus PWA (Progress Web App) was made using TypeScript and ReactJS."
   ;; :cla-made-with "This AutoFocus CLA (Command Line App) was made using TypeScript and NodeJS."
   :credits1 "The AutoFocus (FVP) algorithm was designed by Mark Forster."
   :credits2 "This program was architected and written by Avi Drucker."
   :why-use-an-algo-header "Why Use an Algorithm for a To-Do List"
   :why-use-an-algo-body "The AutoFocus algorithm, unlike most to-do list systems today, you (1) determine what you are most ready for and wanting to do at any given time, and 2) to take a bias towards action on such tasks." 
   :how-it-works-header "How AutoFocus Works (simplified)"
   :how-it-works-body "1. First you add one or more todo items to your list Then, you review your list, dotting the items you feel ready to do now Lastly, you do the bottom-most dotted item & cross it out"
   :step-by-step-ex-header "Step by Step Example"
   :step-by-step-ex-body "Let's say you want to do three things: 'finish trig homework', 'wash the dishes', and 'pack tomorrow's trip'. You add these three things to your list. Note, the 1st item you add gets and becomes your 'priority' item. You then run down the list after the 1st item and ask, 'Do I want to do this item more than [current priority item]?' Let's say you answer no' to dishes and 'yes' to packing. Your list will have dots by the first and third items, and bottom-most dotted item becomes your new 'priority' item. Now that all items have been, it's a good time to do one (or more). You do your packing, and, once you stop packing, cross the last item of your list. Your list is now one dotted item, one item with no dot, one completed item marked with an X or strikethrough."
   :press-enter-key-to-return "Press the enter key to return to the main menu." 
   })

(def ENUM-PRE-INIT
  "I'm experimenting with a pre-initialization value of -2, because -1 usually indicates that something was not found... That said, this style of using negative numbers *is* something that seems very imperative and non-idiomatic in Clojure... I'd love to get some feedback on this from my Clojure senpai friends/mentors." 
  -2)


(def EMPTY-STRING "")


;; 2023_01_22 TIL: How to close a Vim window without quitting its associated/displayed buffer via `:q`
(def initial-list-state [])

(def EMPTY-LIST
  ;; note: this was '() originally
  ;; TODO: rename this to EMPTY-TODO-LIST
  [])

(def NEW-ITEM-STATUS :new)

#_(defn new-list-hashmap
    ;; TODO: relocate to domain data namespace
    ;; TODO: assess whether this function is helpful or necessary... on the fence atm on 2022_09_02
    ;; this function may not be particularly useful, unless an alias is desired, whether it be for an empty list or an empty hashmap
    "Returns an empty hashmap to serve as a new todo item list. This function may also be useful for reseting a list back to a size of zero." 
    [] {})

(defn NEW-LIST
  ;; TODO: relocate to domain data namespace
  "This initializer function useful for creating new lists, and clearing/reseting lists as needed.
   
   D17 Note: This to-do items collection was originally a hashmap, which then became a list, and is now currently a vector."
  ;; TODO: convert back to hashmap when you want to optimize, conduct performance tests/profiling
  ;; TODO: use defined constant (defined up above) instead of a vector literal
  [] EMPTY-LIST)


;; TODO: assess whether this is still useful, rm if not, refactor if yes
#_(def initial-domain-defaults
  ;; original name   "initial-defaults"
  {:todo-item {:text EMPTY-STRING
               :status NEW-ITEM-STATUS
               ;; :t-index ENUM-PRE-INIT
               ;; :times-cloned ENUM-PRE-INIT
               }
   :todo-list {:LIST-list EMPTY-LIST 
               ;; :IDX-last-marked-ready ENUM-PRE-INIT
               ;; :IDX-last-done ENUM-PRE-INIT
               }})

;; TODO: consider making FSM
(def valid-interaction-states
    ;; original name  "valid-states" 
    {:list-states
     ;; TODO: convert the following 3 lines to data description maps
     ;; viewing: the list is rendered/displayed for user viewing
     ;; adding: the list is having a new item added to it
     ;; reviewing: the list is having new items be either marked ready or skipped in top to bottom order, skipping items that are already marked ready or done  (why do we iterate over the list? the list is iterated over in order to produce a series of binary questions for the user  (eg. A or B, yes or no, with the additional bonus option to quit anytime mid-answer ) () 
     #{:viewing :adding :reviewing :focusing} 
   
     :app-states
     #{:loading :idle :taking-user-input :list-processing :quitting}})


(def cli-marks
;; original name: `marks-xo`
  ;;  "It's just data."
  ;; TODO: relocate this to domain data namespace
  ;; TODO: create explanation hashmap, instead of saving useful semantic infos as comments, so you can add these infos to the in-app 'help section'
  "A list of marks that are useful for rendering bullet journal style list items, specifically for a command line interface 'CLI' application.

  Note: When rendered to the terminal, cli marks will appear as follows:
  - [x] I am a completed item
  - [o] I am an item that is ready to be done
  - [ ] I am a new item that is not ready to be done
  "
  ;; question: are these keys below  "semantically labeled", or would it be more effective/accurate to say that they are  "coupling semantic with symbolic"? 
  {:new " " ;; these are items that are not yet ready to be started
   :ready "o" ;; also called  "marked" or  "dotted", these items are ready to start doing
   :done "x"})

(def gui-marks
  "The bullet point marks used to denote item status in a GUI version of this application.

  When rendered, to-do lists will appear something like the following:
  
  ⬤⏺ I am a completed item
  ⦿ I am an item that is ready to be done
  ◯ I am a new item that is not ready to be done
  "
  ;; TODO: relocate this to be inside of the data namespace under the user-interface hashmap
  ;; https://commons.wikimedia.org/wiki/Unicode_circle_shaped_symbols
  {:test "○◌●◯◯◯⬤⏺●◉☉" 
   :outline "◯" ;; large circle ;; TODO: try out using "dotted circle" symbol ◌ to further distinguish between the bullet-point symbols... wait a tic, this sounds like a circle that has a dot in it, a "dotted circle" 😢... Ah well
   :dotted "⦿" ;; circled bullet  (a solid line open circle with one dot in the middle)
   :filled "⏺" ;; black circle
   })

(def marks-hashmap
  {:new {:semantic-name "new"
         :semantic-name-2 "clean" ;; 'clean' refers to 'untouched' 
         :mark-name "outline"
         :mark-name-2 "empty circle"
         :mark-name-3 "faint circle outline"
         :gui-mark-symbol "◌"
         :gui-mark-symbol-alt "◯"
         :cli-mark " "
         :semantic-meaning "A newly created item that is not yet ready to be started (focusing on)."
         :notes "When making a new item in AutoFocus, an empty circle is used to indicate that the newly added item is up (available) for review, but not yet ready to do (i.e. focus on). This empty circle denotes an item as newly added, ready for review, and unready (just yet) to be actioned on."}

   :ready {:semantic-name "ready"
           :semantic-name-2 "focusable"
           :semantic-name-3 "doable"
           ;; :semantic-name-4 "marked" ;; this term isn't clear because there are many different kinds of marks
           ;; :semantic-name-5 "dotted" ;; with a dotted outline circle character, this term also becomes ambiguous
           :mark-name "dot in circle"
           :mark-name-alt "circled bullet"
           :gui-mark-symbol "⦿"
           :cli-mark "o"
           :semantic-meaning "Items that have been dotted are ready to be taken action on (in 'AutoFocus' terminology this is called 'focusing on an item'). The item closest to the end/bottom of a list that is ready is the next item that will be focused on in a focus session. No ready items"
           :notes ""}

   :done {:semantic-name "done"
          :semantic-name-2 "complete"
          :semantic-name-3 "finished"
          :mark-name "solid circle"
          :mark-name-2 "filled in circle"
          :gui-mark-symbol "⏺"
          :cli-mark "x"
          :semantic-meaning "A filled in circle bullet point in the GUI, or an x'd out CLI item listing, represents an item that has been completed. While there may be remaining work to do, once a focus session has been stopped/ended, the item is considered 'done'."
          :notes ""}})


;; DONE: move menu states data here
;; TODO: reassess whether a verbose menu option is helpful and/or necessary
;; App State Control Data
(def menu-options-map
  "This is a mapping between the terse and verbose menu option names."
  {:add         :add-new-item
   :prioritize  :prioritize-list
   :do          :do-priority-item
   :about       :about-autofocus
   :example     :af-example-irl
   :how-to      :how-to-af
   :quit        :quit-application})


; TODO: refactor out shortened names, or, replace long names with shortened names
;; Q: Is there a macro for the following 7 lines, where, instead of defining multiple defs one after the other, instead, a map or list of tuples could be passed?
(def ADD (get menu-options-map :add))
(def PRIORITIZE (get menu-options-map :prioritize))
(def DO (get menu-options-map :do))
(def ABOUT (get menu-options-map :about))
(def EXAMPLE (get menu-options-map :example))
(def HOW-TO (get menu-options-map :how-to))
(def QUIT (get menu-options-map :quit))


;; TODO: determine why base-menu-options isn't being used currently
;; TODO: IDEA: Separate the menu into two: (1) main menu for application level actions, and (2) to-do list menu for list level actions (add, prioritize, do, plus a 'return to main menu' option)
(def base-menu-options
  "Base menu options are available by default, and will display every time the menu is rendered. This is in contrast with situation-dependent (ie. conditional) menu options, such as PRIORITIZE and DO.

  Note: Menu options include both AutoFocus list actions as well application actions."
  [ADD ABOUT QUIT]) ;; EXAMPLE HOW-TO


;; DONE: implement the sorting of the menu by custom ordering/ranking
(def all-menu-options-sorted
  "used to sort menu options before they are displayed, originally named `menu-options-order`"
  [ADD PRIORITIZE DO ABOUT QUIT]) ;; EXAMPLE HOW-TO QUIT


;; TODO: relocate to application interface data namespace
(def menu-strings-map
  {ADD "Add a new to-do item"
   PRIORITIZE "Prioritize my list"
   DO "Do the highest priority item"
   ABOUT "Read about AutoFocus"
   EXAMPLE "See Real Life AutoFocus Example"
   HOW-TO "See The AufoFocus Algorithm Steps"
   QUIT "Quit the application"})


;; CLI Constants
(def CLI-FENCE "----------")

(def NEWLINE "\n")

(def MAIN-MENU-HEADER
  (str CLI-FENCE NEWLINE "Main Menu" NEWLINE))

(def TODO-LIST-HEADER
  (str CLI-FENCE NEWLINE "My To-Do List" NEWLINE))

(def REVIEW-START
  "...starting review...")

(def REVIEW-END
  "... ending review ...")

(def ENTER-TO-CONTINUE
  "Once you have stopped working on this task, press ENTER to continue.")

(def LIST-EMPTY
  "> There are no items in your to-do list currently.")

(def THANKS-BYE
  "Thank you for using AutoFocus!")