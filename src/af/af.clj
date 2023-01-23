(ns af.af 
  "A task management system command line application project called AutoFocus."
  (:require
   [af.data :as d]
   [af.item :as i]
   [af.list :as l]
   [clojure.string :as s]
   ;; [clojure.tools.cli :as cli]
   ))


;;;; TODO: implement console clearing



  ;; Q: Is this file this project's "entry point"?
  ;; Q: What is the difference in purpose/intent between af.af and af.core, as intended by the authors of tools.cli?
;; Q: What exactly is the point of the exec function?
(defn exec
  "Invoke me with clojure -X af.entry-point/exec"
  [opts]
  (println "exec with" opts))


;; 2023_01_22 TIL: How to close a Vim window without quitting its associated/displayed buffer via `:q`
;; TODO: relocate to af.content namespace
(def about-texts
  {:overview-and-summary "The AutoFocus algorithm and task management system was originally created by Mark Forster. This application was developed by Avi Drucker.

The way AutoFocus works is roughly as follows:
1. make a list by adding items to it
2. prioritize the list as directed by the AutoFocus algorithm
3. do the things on the list

For an example of AutoFocus in action, please select 'real-world example' from the menu. For a detailed explanation of the AutoFocus algorithm, please select 'step-by-step how-to' from the menu."
   :high-level-what-and-why "The AutoFocus algorithm helps you (1) determine what you are most ready for and wanting to do at any given time, and (2) to take a bias towards action on such tasks. Many task management systems (including to-do lists) suffer a usability issue in that they tend to get cluttered and messy, and they are easily subverted to serve procrastination. AutoFocus is designed to fight against procrastination."
   :detailed-steps "1. Add one or more to-do items to your list (always marking* the first 'new' item as 'ready' if there are no 'ready' items)
2. Make a decision to either prioritize** your list, or to start taking action on the marked (ready) item, marking* items as done when you have finishing working on them, as well as re-writing to-do items at the bottom of the list if you have remaining work left to do after stopping activity on a given item.
3. Repeat steps 1 and 2 until you reach the end of your paper or computer screen, at which point you can start a new page, transferring over any items that you have yet to do

*Avi's suggested marks are, for paper and digital text respectively:
new - an open outline circle, empty brackets [ ]
ready - a circle with a dot in the center of it, brackets with an 'o' inside [o]
done - a fully filled-in circle, brackets with an 'x' inside [x]

** Prioritizing one's list is done by comparing the bottom-most marked as ready item (the 'priority item') with the unmarked/new items that follow it. The process is done by asking the question, 'Do I want to B more than A?' where A is the priority item and B is the unmarked/new item that comes next after A. If the answer is yes, then B gets marked as ready and becomes the new 'priority item'. If the answer is no, then we simply move on to compare A with the next unmarked/new item in the list, if it exists, skipping any items that are marked as done. This process is repeated until once for each unmarked/new item below B until there are no more unmarked/new items left to compare against the priority item."
   :real-world-example "You know that you have three things you need to do: 'finish trig homework', 'wash the dishes', and 'pack for tomorrow's trip'. You add these three things to your list, dotting the first item as soon as you add it to mark it as 'ready'. This first dotted item is also now the 'priority item'. You can now prioritize the entire list by asking for each unmarked 'new' item, 'Do I want to do this item more than the 'priority item'?' Let's say you answer 'no' to dishes and 'yes' to packing. Your list will have dotted the first and third items, with the third item being the current priority item. Now that we've reviewed/prioritized to the bottom of the list, it is a good time to start taking action on some of the items. You do your packing, and, once you stop packing, you mark it as 'done'. Then, you ask yourself, 'Am I 100% done with this task?' Let's say in this case the answer is 'no'. Because the answer is 'no', we will add a duplicate of the third item to the bottom of the list. The final list will look like this:
- [o] finish trig homework
- [ ] wash the dishes
- [x] pack for tomorrow's trip
- [ ] pack for tomorrow's trip"})


(defn- cli-get-number-in-range-inclusive
  "Gets a number ranging from x to y (inclusive) from the user via keyboard input and stdin."
  [x y]
  ;; IDEA: implement optional prompt for this function
  ;; (println (str "Please enter a # between " x " and " y ": "))
  ;; TIL: How to convert a non-literal string into a regular expression, via re-pattern
  (let [match-str (re-pattern (str "^[" x "-" y "]$"))]
    (loop []
      (let [input (read-line)]
        (if (and (not (nil? input)) (re-matches match-str input))
          (do
            ;; TODO: replace literal string hyphen/dash fences with def binding 
            (println (str "You selected choice #" input "."
                          "\n----------"))
            ;; (println (str "You inputted '" input
            ;;               "'. Thank you for the valid input!"))
            (Integer/parseInt input))
          (do
            (println (str "Invalid input '" input
                          "'. Please enter a digit between " x " and " y ":"))
            (recur)))))))


;; TODO: relocate this to af.utils namespace
(defn print-and-return
  "Potentially useful as a debugging function, also useful for cases where printing to the console before returning a value is desired."
  [input-string return-item]
  (println input-string) return-item)


(defn- cli-take-keyboard-input
  "This function gets abritrary text from the user via keyboard input.
  Note: Prompt is optional."
  [{:keys [prompt]}]
  (loop []
    (when (not (nil? prompt))
      (println prompt))
    (let [input (read-line)]
      (if (and (not (nil? input)) (seq input))
        (print-and-return (str "You entered '" input "'.") input)
        (do
          (println (str "Input of '" input
                        "' does not appear to be valid."))
          (recur))))))


(defn- single-q?
  "used to determine if a CLI user is attempting to quit when inputting text"
  [input-text]
  (= (s/lower-case input-text) "q"))


(defn cli-quittable-get-text-from-user
  "Note: Prompt is optional. User may abort/quit/skip the text input operation by enter in 'q' or 'Q'."
  [{:keys [prompt]}]
  (let [inputted-text (cli-take-keyboard-input {:prompt prompt})
        user-quitted? (single-q? inputted-text)]
    (if user-quitted? nil inputted-text)))


(def menu-options-map
  "This is a mapping between the terse and verbose menu option names."
  {:add         :add-new-item
   :prioritize  :prioritize-list
   :do          :do-priority-item
   :about       :about-autofocus
   :example     :af-example-irl
   :how-to      :how-to-af
   :quit        :quit-application})


(def ADD (get menu-options-map :add))
(def PRIORITIZE (get menu-options-map :priotize))
(def DO (get menu-options-map :do))
(def ABOUT (get menu-options-map :about))
(def EXAMPLE (get menu-options-map :example))
(def HOW-TO (get menu-options-map :how-to))
(def QUIT (get menu-options-map :quit))


(def base-menu-options
  "Note: Menu options include both AutoFocus list actions as well application actions."
  [ADD ABOUT EXAMPLE HOW-TO QUIT])


;; TODO: implement the increasing of menu options dynamically based on list state
(defn get-valid-menu-options
  "By reviewing the input-list, this function can determine
  which menu options should be added to the menu-options list."
  [{:keys [input-list]}]
  (let [prioritizable-list? (str "stub") ;; TODO: implement stub
        actionable-list? (l/is-focusable-list?
                          {:input-list input-list})]
    (case
     (and prioritizable-list? actionable-list?)
      [PRIORITIZE DO]

      (and (not prioritizable-list?) actionable-list?)
      [DO]

      (and prioritizable-list? (not actionable-list?))
      [PRIORITIZE]

      (and (not prioritizable-list?) (not actionable-list?))
      [])))


;; TODO: implement the sorting of the menu by custom ordering/ranking
(def menu-options-order
  "used to sort menu options before they are displayed"
  {ADD 0
   PRIORITIZE 4
   DO 5
   ABOUT 6
   EXAMPLE 7
   HOW-TO 8
   QUIT 9})

(defn- gen-menu-item-string [index item-string]
  (str (inc index) ": " item-string))


;; 2023_01_21 TIL: The `str` function is not appropriate for converting
;;      keywords into strings. Instead, use the `name` function.
(defn- gen-menu-string
  [{:keys [menu-options menu-mappings]}]
  (s/join "\n" (map-indexed
                gen-menu-item-string
                (map menu-mappings menu-options))))


;; TODO: convert this function into a pure function by taking
;; in args/params instead of using global state
(defn- cli-display-menu [menu-options-input menu-mappings]
  (println (str "----------\n" "AutoFocus Main Menu\n" 
                (gen-menu-string {:menu-options menu-options-input
                                  :menu-mappings menu-mappings}))))


(defn- cli-ask-for-menu-input
  [options-cnt]
  (println (str
            "Please make a selection from the above menu [1-"
            options-cnt "]: " )))


(def menu-strings-map
  {ADD "Add New To-Do Item"
   PRIORITIZE "Review and Prioritize List"
   DO "Do Priority Item"
   ABOUT "Read About AutoFocus"
   EXAMPLE "See Real Life AutoFocus Example"
   HOW-TO "See The AufoFocus Algorithm Steps"
   QUIT "Quit Application"})


;; TODO: convert into pure func by taking in args, not global state
(defn cli-do-menu-cycle
  "display the menu and get user's menu choice"
  [{:keys [input-menu]}] 
  (cli-display-menu input-menu menu-strings-map) ;; base-menu-options 
  (cli-ask-for-menu-input (count input-menu))
  (get base-menu-options
       (dec (cli-get-number-in-range-inclusive
             1 (count input-menu))))) 


;; TODO: replace this with the initial/empty list constant from af.data
(def initial-list-state [])


(defn do-app-action
  [input-action input-list]
  (condp = input-action
    ADD
    (let [input-text (cli-quittable-get-text-from-user
                      {:prompt "Please enter the text for your to-do item:"})]
      (if (nil? input-text)
        (print-and-return "...cancelling adding new item to list..." input-list) ;; return the list as-is
        (do
          (println "...adding item to list...") ;; debugging
          (l/add-item-to-list
           {:input-item (i/create-new-item-data {:input-text input-text})
            :target-list input-list})) ;; return list w/ new item appended
        ))
    
    ;;;; overview-and-summary, detailed-steps, real-world-example
    ABOUT (print-and-return (get about-texts :overview-and-summary), input-list)
    EXAMPLE (print-and-return (get about-texts :real-world-example), input-list)
    HOW-TO (print-and-return (get about-texts :detailed-steps), input-list)
    ;; TODO: implement serialization logic so that, upon quitting, the user has their list autosaved
    QUIT (println "Confirming 'quit' action...")

    ;; default/else condition/case
    (println "Invalid action detected, error code 0002.")))

(defn- gen-item-count-string [input-list]
  (if (= 1 (count input-list))
    (str "\nThere is 1 item in your list.")
    (str "\nThere are " (count input-list) " items in your list.")))

(defn- gen-list-render-output
  [{:keys [input-list]}]
  (if (zero? (count input-list))
    (str "----------\n" "My AutoFocus To-Do List\n"
         "There are no items in your to-do list currently.")
    (str "----------\n" "My AutoFocus To-Do List\n"
         (l/stringify-list {:input-list input-list
                            :marks-dict d/cli-marks})
         (gen-item-count-string input-list))))


;; TODO: evaluate this function as a canditate for including in public API
(defn cli-render-list [input-list]
  (println (gen-list-render-output {:input-list input-list})))


;; 2023_01_22 TIL: C-c C-k to evaluate an entire CIDER repl in Emacs
(defn cli-do-app-cycle
  "A loop that runs the entire application."
  []
  (println "Welcome to AutoFocus, a time management system designed by Mark Forster")
  (loop [;; initialize app state
         ;; initialize the user's to-do list
         app-state-map {:the-list initial-list-state}]
    (let [
          ;; TODO: clear the terminal (probably right here?) for a fresh display of the list, content, and menu choices
          ;;_       (cli/clear)
          
          ;; render the user's to-do list to the command line
          _       (cli-render-list (get app-state-map :the-list))

          ;;;; TODO: update the arg here to instead of being `base-menu-options`, to dynamically adjust as different actions become available due to app/list state (such as prioritizing or doing), and to not rely on global bindings but rather passed in arguments/parameters
          ;; display the menu and get user's menu choice
          action-input (cli-do-menu-cycle
                        {:input-menu base-menu-options}) 

          ;; println debugging
          _       (case action-input
                    ;; - [ ] Question: Why can I not use bindings instead of keyboards here in the case expression?
                    :add-new-item (println "Let's make a new item now...")
                    :about-autofocus (println "Displaying the about section...")
                    :af-example-irl (println "Displaying a real-world example of AutoFocus in action...")
                    :how-to-af (println "Displaying the how-to section...")
                    :quit-application (println "Quitting...")
                    :default (println "Unknown action entered, error code 0001."))

          ;; update the list based on the user's menu choice
          new-list (do-app-action action-input (get app-state-map :the-list))]
      (if (= QUIT action-input)
        "Good-bye!"
        (recur
         {:the-list new-list})))))


;; Q: What are the meaningful differences and similarities in purpose/role/functionality between the exec and main functions?
(defn -main
  "Invoke me with clojure -M -m af.entry-point"
  [& args]
  ;; (println "-main with" args)
  ;; (let [input (read-line)]
  ;;   (if (or (= input "yes") (= input "no"))
  ;;     (println "Thanks for your input")
  ;;     (println "Invalid input. Please enter either 'yes' or 'no'")))
  (cli-do-app-cycle))

