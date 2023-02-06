(ns af.cli
  (:require
   [af.data :as d]
   [af.list :as l]
   [af.item :as i]
   [clojure.string :as s]
   ;; [clojure.repl :as r]
   ))

;; TODO: pass this to `cli-get-number-in-range-inclusive`
;; TODO: refactor cli functions to not directly call data items such as NEWLINE or CLI-FENCE
(defn- cli-choice-confirm [input]
  (println (str "You selected choice #" input "." d/NEWLINE d/CLI-FENCE)))

#_(defn- cli-input-confirm [input]
  (println (str "You inputted '" input "'. Thank you for the valid input!")))

#_(defn- cli-invalid-input-detected [input]
  (println (str "Invalid input '" input "' entered.")))

(defn- cli-invalid-input-notification-and-request [input x y]
  (println (str "Invalid input '" input "' entered. "
                "Please enter a digit between " x " and " y ":")))


;; TODO: relocate this to af.utils namespace
(defn print-and-return
  "Potentially useful as a debugging function, also useful for cases where printing to the console before returning a value is desired."
  [input-string return-item]
  (println input-string) return-item)


;; TODO: refactor this function so that its contents know nothing about menus, choices, nor does it decide its own printout messages, instead, it takes in print out messages as functions and passes to them the appropriate arguments, for example `if-cli-choice-confirm-exists-then-print-with-input-arg`, and so on for `cli-input-confirm`, `cli-invalid-input-detected, etc.`
(defn- cli-get-number-in-range-inclusive
  "Gets a number ranging from x to y (inclusive) from the user via keyboard input and stdin. Optionally takes a map from which to direct the printing out of prompts and confirmations."
  [x y {:keys [choice-confirm-func invalid-input-re-request]}]
  ;; TODO: implement optional prompt for this function
  ;; (println (str "Please enter a number from " x " to " y ": "))
  ;; TIL: How to convert a non-literal string into a regular expression, via re-pattern
  (let [match-str (re-pattern (str "^[" x "-" y "]$"))]
    (loop []
      (let [input (read-line)]
        (if (and (not (nil? input)) (re-matches match-str input))
          (do
            ;; DONE: replace literal string hyphen/dash fences with def binding
            (choice-confirm-func input)
            #_(println (str "You selected choice #" input "."
                          d/NEWLINE d/CLI-FENCE))
            ;; (println (str "You inputted '" input
            ;;               "'. Thank you for the valid input!"))
            (Integer/parseInt input))
          (do
            ;; TODO: split the following printout into 2 separate printouts
            (invalid-input-re-request input x y)
            #_(println (str "Invalid input '" input
                          "'. Please enter a digit between " x " and " y ":"))
            (recur)))))))


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


(defn- cli-quittable-get-text-from-user
  "Note: Prompt is optional. User may abort/quit/skip the text input operation by enter in 'q' or 'Q'."
  [{:keys [prompt]}]
  (let [inputted-text (cli-take-keyboard-input {:prompt prompt})
        user-quitted? (single-q? inputted-text)]
    (when (not user-quitted?) inputted-text)))





;; TODO: convert this function into a pure function by taking
;; in args/params instead of using global state
(defn- cli-display-menu [menu-options-input menu-mappings]
  (println (str d/MAIN-MENU-HEADER 
                (d/gen-menu-string {:menu-options menu-options-input
                                  :menu-mappings menu-mappings}))))


;; TODO: convert this function into string generation to leave
;; the printing responsibilities to the caller/parent function
;; suggested new name `gen-selection-prompt-with-number-input`
(defn- cli-ask-for-menu-input
  [options-cnt]
  (println (str
            "Please make a selection from the above menu [1-"
            options-cnt "]: " )))


;; TODO: convert into pure func by taking in args, not global state
(defn cli-do-menu-cycle
  "display the menu and get user's menu choice"
  [{:keys [input-menu
           input-menu-strings-map
           input-base-menu-options]}] 
  (cli-display-menu input-menu input-menu-strings-map) ;; base-menu-options 
  (cli-ask-for-menu-input (count input-menu))
  (get input-base-menu-options
       ;; TODO: refactor cli-get-number-in-range-inclusive call signature to have clearer inputs
       (dec (cli-get-number-in-range-inclusive
             1
             (count input-menu)
             {:choice-confirm-func cli-choice-confirm
              :invalid-input-re-request cli-invalid-input-notification-and-request
              })))) 



(defn cli-do-app-action
  [input-action input-list]
  (condp = input-action
    d/ADD
    (let [input-text (cli-quittable-get-text-from-user
                      {:prompt "Please enter the text for your to-do item:"})]
      (if (nil? input-text)
        (print-and-return "...cancelling adding new item to list..." input-list) ;; return the list as-is
        (print-and-return
          (println "...adding item to list...") ;; debugging
          (l/add-item-to-list
           {:input-item (i/create-new-item-data {:input-text input-text})
            :target-list input-list})) ;; return list w/ new item appended
          ))

    d/PRIORITIZE  (print-and-return "stub for prioritizing lists" input-list)
    d/DO          (print-and-return "stub for actioning on priority item" input-list) 
    
    ;;;; overview-and-summary, detailed-steps, real-world-example
    d/ABOUT       (print-and-return (get d/about-texts :overview-and-summary) input-list)
    d/EXAMPLE     (print-and-return (get d/about-texts :real-world-example) input-list)
    d/HOW-TO      (print-and-return (get d/about-texts :detailed-steps) input-list)
    ;; TODO: implement serialization logic so that, upon quitting, the user has their list autosaved to disk
    d/QUIT (println "Confirming 'quit' action...")

    ;; default/else condition/case
    (println "Invalid action detected, error code 0002.")))


(defn- gen-item-count-string [input-list]
  (if (= 1 (count input-list))
    (str d/NEWLINE "There is 1 item in your list.")
    (str d/NEWLINE "There are " (count input-list) " items in your list.")))


(defn- gen-list-render-output
  [{:keys [input-list]}]
  (if (zero? (count input-list))
    (str d/TODO-LIST-HEADER
         "There are no items in your to-do list currently.")
    (str d/TODO-LIST-HEADER 
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
  (println "Welcome to AutoFocus, a time management system designed by Mark Forster. Please start by creating some to-do items to add them to your list.")
  (loop [;; initialize app state
         ;; initialize the user's to-do list
         app-state-map {:the-list d/initial-list-state}]
    (let [
          ;; TODO: clear the terminal (probably right here?) for a fresh display of the list, content, and menu choices
          ;;_       (cli/clear)
          ;;_       (clojure.java.shell/sh "clear")
          ;;_       (r/cider-repl-clear-buffer)
          ;;_        (println "clearing the buffer...")
          ;;_        (println "\033[2J")
          ;;_        (println "\033[0;0H")
          
          ;; render the user's to-do list to the command line
          _       (cli-render-list (get app-state-map :the-list))

          ;;;; TODO: update the arg here to instead of being `base-menu-options`, to dynamically adjust as different actions become available due to app/list state (such as prioritizing or doing), and to not rely on global bindings but rather passed in arguments/parameters
          ;; display the menu and get user's menu choice
          action-input (cli-do-menu-cycle
                        {:input-menu d/base-menu-options ;; TODO: modify this line in order to take in different menu options depending on the list state
                         :input-menu-strings-map d/menu-strings-map
                         :input-base-menu-options d/base-menu-options})
          
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
          new-list (cli-do-app-action action-input (get app-state-map :the-list))
          
          _       (cli-render-list (get app-state-map :the-list))
          ]
      (if (= d/QUIT action-input)
        "Good-bye!"
        (recur
         {:the-list new-list})))))
