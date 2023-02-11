(ns af.cli
  (:require
   [af.data :as d]
   [af.list :as l]
   [af.item :as i]
   [clojure.string :as s]
   ;; [clojure.repl :as r]
   ))

;; TODO: implement 'debug mode' which toggles on/off println debugging
;; TODO: confirm that 'debug mode' works as desired 
(def DEBUG-MODE-ON true)

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


(def valid-ynq-answer-choices
  #{"y" "Y" "q" "Q" "n" "N"})

(def valid-yn-answer-choices
  #{"y" "Y" "n" "N"})


(defn convert-answer-letter-to-keyword
  [input-letter]
  (case (s/upper-case input-letter)
    "Q" :quit
    "Y" :yes
    "N" :no
    :error-code-003))


;; TODO: rename `cli-ask-yes-no-quit-question` function to `cli-ask-question-with-limited-answer-set` communicate that it can do both quittable questions (such as y/n/q) as well as binary yes/no (y/n) questions
(defn- cli-ask-yes-no-quit-question
  "Asks the user for an answer to a question with a limited answer set, typically one character in length for convenience/simplicity. If the consumer of this function so desires, answers may also be numbers, whole words, or strings including whitespace and punctuation, with carriage returns being the only exception for allowable answer inputs. What the program does with the answer is up to the consumer. Therefore, answers themselves have no built in semantic meaning (such as confirming, quitting, etc.), other than what the consumer communicates to the user."
  [{:keys [input-question valid-answers invalid-input-response]}]
  (loop []
    (let [_     (println input-question)
          input (read-line)]
      (if (contains? valid-answers input)
        (do
        ;; TODO: replace the following `do-print-return` with your custom `print-and-return` utility function
          (println (str "Nice! You answered '" input "'!"))
          input)
        (do
        ;; TODO: if possible, replace the following `do-print-return` with your custom `print-and-return` utility function
          (println (str "You entered '" input "'."))
          (println invalid-input-response)
          (recur))))))


;; TODO: after refactoring `cli-ask-yes-no-quit-question`, delete this function
(defn- cli-ask-yes-no-question
  [{:keys [input-question valid-answers invalid-input-response]}]
  (loop []
    (let [_     (println input-question)
          input (read-line)]
      (if (contains? valid-answers input)
        ;; TODO: replace the following `do-print-return` with your custom `print-and-return` utility function
        (do
          (println (str "Great! You answered '" input "'!"))
          input)
        (do
          (println (str "You entered '" input "'."))
          (println invalid-input-response)
          (recur))))))


;; TODO: relocate string constants to af.data namespace
(def INVALID-YNQ-INPUT-RESPONSE
  "That wasn't a 'y', 'n' or 'q' answer. Please try again.")

(def INVALID-YN-INPUT-RESPONSE
  "That wasn't a 'y' or 'n' answer. Please try again.")

(def demo-question
 {:input-question "Do you like apples more than bananas? Please answer 'y' for 'yes', 'n' for 'no', or 'q' for 'quit: "
  :valid-answers valid-ynq-answer-choices
  :invalid-input-response INVALID-YNQ-INPUT-RESPONSE})

;; testing the asking of a y/n/q question and then converting it to a keyword
;; (convert-answer-letter-to-keyword (cli-ask-yes-no-quit-question demo-question))

(defn cli-conduct-prioritization-review ;; get-and-submit-single-comparison
  "original name: get-and-submit-single-comparison"
  [{:keys [input-list input-cursor-index]}]
  ;; 0. assuming that this list is a reviewable/prioritizable list...
  ;; 1. ask the user, do they want to do current-item more than priority-item
  ;; 2. take in the user's answer, where the valid answer choices are 'y', 'Y', 'q', 'Q', 'n', or 'N'
  ;; 3. apply the user's answer to return back a result map which contains:
  ;;    - the new list (modified if user's answer was `:yes` or unmodified otherwise)
  ;;    - the next cursor (which may be `nil` if there are no more items to review/compare/prioritize)
  ;;    - the user's intent to continue (`true` for `:yes` and `:no` answers, `false` if the user's answer was `:quit`) ;; `intent-to-continue`
  (println "...starting review...")
  (loop [current-list input-list
         current-index input-cursor-index]
    (let [;; get the user's answer to the comparison question
          ;; _ (println ["list state: " current-list "cursor index: " current-index])
          current-answer
          (convert-answer-letter-to-keyword
           (cli-ask-yes-no-quit-question
            {:input-question
             (l/get-single-comparison
              {:input-list current-list
               :input-cursor-index current-index})
             :valid-answers valid-ynq-answer-choices
             :invalid-input-response INVALID-YNQ-INPUT-RESPONSE}))

          ;; get the result of submitting a comparison with user's answer 
          submission-response   (l/submit-single-comparison {:input-list current-list
                                                             :input-cursor-index current-index
                                                             :answer-input current-answer})

          ;; TODO: Answer the question: Is there a way to destructure the following in one line?
          updated-list       (get submission-response :output-list)
          updated-cursor-index      (get submission-response :next-cursor)
          user-is-quitting?  (get submission-response :quitting-comparison)
          ;; TODO: confirm that the next line works as desired --> It does not appear to work. Q; Why does it not work as desired? What would work as desired in this case?
          ;; {:keys [updated-list updated-index user-is-quitting?]} submission-response

          ;; `comparing?` will be our sentinel value to terminate review/prioritization sessions
          comparing?         (and updated-cursor-index (not user-is-quitting?))] ;; TODO: clarify intent of code here with a comment
      (if comparing?
        (recur updated-list updated-cursor-index)
        updated-list))))

(comment
  (def review-test-list-1
    [{:t-index 0, :text "apple", :status :ready}
     {:t-index 1, :text "blueberry", :status :new}
     {:t-index 2, :text "cherry", :status :new}])

  (def round-test-1
    {:input-list review-test-list-1
     :input-cursor-index 1})

  (cli-conduct-prioritization-review round-test-1)
)


;; TODO: test using this with focus/do mode, as well as to confirm and then close out the about/read-me/text-exposition sections of the application (to then return back to the menu)
(defn- cli-press-any-key-to-continue
  [{:keys [prompt]}]
  (let [_ (println prompt)
        _ (read-line)])
  ;; TODO: replace this string with a map arg input
  (println "Proceeding..."))


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
;;       in args/params instead of using global state
;; TODO: instead of passing menu-options-input and
;;       menu-mappings to this function, instead, pass the
;;       result of d/gen-menu-string as the arg/param
;; TODO: instead of directly taking 2 args, instead pass an 
;;       arg map with keys that are named in a standard way
;;       (matching the naming conventions in the project)
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

;; TODO: relocate to af.data
(def cli-texts
  {:adding {:prompt "Please enter the text for your to-do item:"
            :cancel-confirm "... cancelling adding new item to list..."
            :success-confirm "...adding item to list..."}})


(defn- cli-conduct-add-action
  [{:keys [input-list prompt cancel-confirm success-confirm]}]
  (let [input-text (cli-quittable-get-text-from-user
                    {:prompt prompt})]
    (if (nil? input-text)
      ;; return the list as-is
      (u/print-and-return
       {:input-string cancel-confirm
        :is-debug? false
        :return-item input-list})
      ;; return list w/ new item appended
      (u/print-and-return
       {:input-string success-confirm
        :is-debug? true
        :debug-active? DEBUG-MODE-ON
        :return-item (l/add-item-to-list
        {:input-item (i/create-new-item-data {:input-text input-text})
                       :target-list input-list})}))))


;; TODO: relocate conduct-review-action function from af.list namespace function to live here


(defn cli-do-app-action
  [input-action input-list]
  (condp = input-action
    d/ADD
    (conduct-add-action  {:input-list input-list
                          :prompt (get-in cli-texts [:adding :prompt])
                          :cancel-confirm (get-in cli-texts [:adding :cancel-confirm])
                          :success-confirm (get-in cli-texts [:adding :success-confirm])
                          })

    d/PRIORITIZE
    (cli-conduct-prioritization-review 
     {:input-list input-list
      :input-cursor-index
      (l/get-index-of-first-new-item-after-priority-item 
       {:input-list input-list})}) 

    ;; TODO: implement the yes/no question asking after a user is done 
    ;; focusing/actioning of 'Is there work remaining on this task/item?'
    ;; TODO: implement the 'press any key' or 'press the ENTER key' to continue
    d/DO
    (l/conduct-focus-on-list {:input-list input-list})

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


;; :debug-active? DEBUG-MODE-ON
(defn cli-clear-buffer []
  (when DEBUG-MODE-ON
    (println "clearing the buffer..."))
  (println "\033[2J")
  (println "\033[0;0H"))


;; 2023_01_22 TIL: C-c C-k to evaluate an entire CIDER repl in Emacs in VSCode
(defn cli-do-app-cycle
  "A loop that runs the entire application."
  []
  ;; TODO: relocate this string to the af.data namespace
  (println "Welcome to AutoFocus, a time management system designed by Mark Forster. Please start by creating some to-do items to add them to your list.")
  (loop [;; initialize app state
         ;; initialize the user's to-do list
         app-state-map {:the-list d/initial-list-state}]
    (let [;; TODO: clear the terminal (probably right here?) for a 
          ;;       fresh display of the list, content, and menu choices
          ;; TODO: explore/experiment within different terminals / shells
          ;;       / OS's to confirm correct screen clearing behavior
          ;;_       (cli/clear)
          ;;_       (clojure.java.shell/sh "clear")
          ;;_       (r/cider-repl-clear-buffer)
          _        (cli-clear-buffer)

          _        (println "\033[0;0H")
          
          ;; render the user's to-do list to the command line

          current-menu (vec (d/sort-menu-options
                             {:input-unsorted
                              (d/get-valid-menu-options
                               {:input-list current-list
                                :all-menu-options d/all-menu-options-sorted})
                              :input-order d/all-menu-options-sorted}))

          ;; println debugging
          ;; _           (println ["current menu: " current-menu])
          ;; _           (println ["d/menu-strings-map: " d/menu-strings-map])

          ;; DONE: update args here to instead of being `base-menu-options`, 
          ;; to dynamically adjust as different actions become available due to 
          ;; app/list state (such as prioritizing or doing), and to not rely on 
          ;; global bindings but rather passed in arguments/parameters

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
                    :prioritize (println "Let's review the list to prioritize the items within...")
                    :do (println "Let's focus on the priority item and start taking action on it...")
                    :about-autofocus (println "Displaying the about section...")
                    :af-example-irl (println "Displaying a real-world example of AutoFocus in action...")
                    :how-to-af (println "Displaying the how-to section...")
                    :quit-application (println "Quitting...")
                    :default (println "Unknown action entered, error code 0001."))

          ;; update the list based on the user's menu choice
          new-list (cli-do-app-action action-input (get app-state-map :the-list))
          ]
      (if (= d/QUIT action-input)
        "Good-bye!"
        (recur
         {:the-list new-list})))))
