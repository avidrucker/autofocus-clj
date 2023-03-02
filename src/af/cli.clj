(ns af.cli
  (:require
   [af.utils :as u]
   [af.data :as d]
   [af.calc :as calc]
   [af.list :as l]
   [clojure.string :as s]
   ))


;; DONE: replace literal string hyphen/dash fences with def binding
;; TODO: relocate string constants to af.data namespace
;; TODO: evaluate whether calc and utils should be merged or kept separate
;; TODO: review entire code base for (not (nil?)) to refactor/replace with (some?)
;; TODO: review namespace for privatization
;; TODO: mark whitespace only text inputs for items as invalid text entry, 
;;       and force the user to input non-whitespace text (i.e. sanitize inputs by 
;;       trimming them)


(def DEBUG-MODE-ON 
  "debug mode which toggles on/off println debugging"
  false)


;; STRING CONSTANTS
;; TODO: relocate this & other cli copy text to top of cli namespace
(def cli-texts
  {:adding {:prompt "Please enter the text for your to-do item:"
            :cancel-confirm "... cancelling adding new item to list..."
            :success-confirm "...adding item to list..."}})

(def RETURN-TO-MAIN-MENU-STRING 
  "Please press the ENTER key to return to the main menu.")

(def INVALID-YNQ-INPUT-RESPONSE
  "That wasn't a 'y', 'n' or 'q' answer. Please try again.")

#_(def INVALID-YN-INPUT-RESPONSE
  "That wasn't a 'y' or 'n' answer. Please try again.")


;; STRING GENERATION
(defn- gen-choice-confirm-string [input newline fence]
  (str "You selected choice #" input "." newline fence))

;; TODO: write down why this is not a good idea: 'pass this to 
;;       `cli-get-number-in-range-inclusive`'
;; TODO: refactor cli functions to not directly call data items 
;;       such as NEWLINE or CLI-FENCE, instead, write functions 
;;       that move NEWLINE and CLI-FENCE to be sibling level 
;;       functions under a shared parent function, for example 
;;       `cli-choice-confirm`
(defn- cli-choice-confirm [input]
  (println (gen-choice-confirm-string input d/NEWLINE d/CLI-FENCE)))

#_(defn- cli-input-confirm [input]
    (println (str "You inputted '" input "'. Thank you for the valid input!")))

(defn- gen-invalid-input-detected-msg [input]
    (str "Invalid input '" input "' entered."))

;; TODO: refactor this function to take key-val map of {:invalid-input :invalid-confirm :re-prompt} where,
;;       instead of passing x and y, the prompt is generated externally and passed in
(defn- invalid-input-msg-and-prompt [input lower upper]
  (println (str (gen-invalid-input-detected-msg input) d/NEWLINE
                "Please enter a digit between " lower " and " upper ","
                d/NEWLINE "and then press the ENTER key:")))


;; TODO: rename invalid-input-re-request arg key so that it is clear from its name that it is a function 
;; TODO: refactor this function so that its contents know nothing about menus, choices, nor does it decide its own printout messages, instead, it takes in print out messages as functions and passes to them the appropriate arguments, for example `if-cli-choice-confirm-exists-then-print-with-input-arg`, and so on for `cli-input-confirm`, `cli-invalid-input-detected, etc.`
(defn- cli-get-number-in-range-inclusive
  "Gets a number ranging from x to y (inclusive) from the user via keyboard input and stdin. Optionally takes a map from which to direct the printing out of prompts and confirmations."
  [{:keys [lower upper choice-confirm-func invalid-input-re-request]}]
  ;; TODO: implement optional prompt for this function
  ;; (println (str "Please enter a number from " x " to " y ": "))
  ;; TIL: How to convert a non-literal string into a regular expression, via re-pattern
  (let [match-str (re-pattern (str "^[" lower "-" upper "]$"))]
    (loop []
      (let [input (read-line)
            sanitized (s/trim input)]
        (if (and (some? sanitized) (re-matches match-str sanitized))
          (do
            (choice-confirm-func sanitized)
            ;; TODO: refactor so that way this can run as a ClojureScript web app
            (Integer/parseInt sanitized))
          (do
            ;; TODO: split the following printout into 2 separate printouts
            ;; TODO: refactor to remove imported function which causes a loss of 
            ;;       intospection into the function contract
            ;;       Q: Is this an example relating to referential transparency?
            (invalid-input-re-request sanitized lower upper)
            (recur)))))))

;; TODO: instead of matching on valid letters, you could match on valid keys
;;       Q: For this application, what would be more effective, to validate 
;;          on strings, on keys, or neither?
;; TODO: rename to valid-ternary-choices
(def valid-ynq-answer-choices
  #{"y" "Y" "q" "Q" "n" "N"
    "yes" "YES" "no" "NO" "quit" "QUIT"})

;; TODO: rename to valid-binary-choices
(def valid-yn-answer-choices
  #{"y" "Y" "n" "N" "yes" "YES" "no" "NO"})

(defn convert-answer-letter-to-keyword
  [input-letter]
  (case (s/upper-case input-letter)
    "Q" :quit
    "Y" :yes
    "N" :no
    "YES" :yes
    "NO" :no
    "QUIT" :quit
    :error-code-003))


(defn- cli-ask-question-with-limited-answer-set
  "Asks the user for an answer to a question with a limited answer 
   set, typically one character in length for convenience/simplicity. 
   If the consumer of this function so desires, answers may also be 
   numbers, whole words, or strings including whitespace and 
   punctuation, with carriage returns being the only exception for 
   allowable answer inputs. What the program does with the answer is 
   up to the consumer. Therefore, answers themselves have no 
   built-in semantic meaning (such as confirming, quitting, etc.), 
   other than what the consumer communicates to the user.
   
   Example questions may include yes/no questions, yes/no/quit, or 
   to make a selection from a list/set of menu choice options."
  [{:keys [input-question valid-answers invalid-input-response]}]
  (loop []
    (let [_     (println input-question)
          input (read-line)
          sanitized (s/trim input)]
      (if (contains? valid-answers sanitized)
        (u/print-and-return
         {:input-string (str "Nice! You answered '" sanitized "'!")
          :is-debug? true
          :return-item sanitized})
        (do
          (println (str "You entered '" sanitized "'."))
          (println invalid-input-response)
          (recur))))))


;; testing the asking of a y/n/q question and then converting it to a keyword
;; (convert-answer-letter-to-keyword (cli-ask-yes-no-quit-question demo-question))


(defn cli-conduct-prioritization-review 
  "original name: get-and-submit-single-comparison

  0. assuming that this list is a prioritizable list...
  1. ask the user, do they want to do current-item more than priority-item
  2. take in the user's answer, where the valid answer choices are 'n', 'y', 'q', 'yes', 'no', or 'quit' where answers are case-insensitive
  3. apply the user's answer to return back a result map which contains:
     - the new list (modified if user's answer was `:yes`, otherwise unmodified)
     - the next cursor (which may be `nil` if there are no more items to review/compare/prioritize)
    - the user's intent to continue (`true` for `:yes` and `:no` answers, `false` if the user's answer was `:quit`) ;; `intent-to-continue`
  "
  [{:keys [input-list input-cursor-index]}]
  (println d/REVIEW-START) ;; TODO: convert to custom debug statement (when DEBUG-MODE-ON ...)
  (loop [current-list input-list
         current-index input-cursor-index]
    (let [;; generate the question to ask the user
          current-question (l/get-single-comparison
                            {:input-list current-list
                             :input-cursor-index current-index})

          ;; get the user's answer to the comparison question
          current-answer
          (convert-answer-letter-to-keyword
           (cli-ask-question-with-limited-answer-set
            {:input-question current-question
             :valid-answers valid-ynq-answer-choices
             :invalid-input-response INVALID-YNQ-INPUT-RESPONSE}))

          ;; get the result of submitting a comparison with user's answer 
          submission-response   (l/submit-single-comparison
                                 {:input-list current-list
                                  :input-cursor-index current-index
                                  :answer-input current-answer})

          ;; destructure submission response back into suitable bindings
          {:keys [next-list next-cursor-index user-is-quitting?]}
          submission-response

          ;; `continue-comparing?` is the sentinel value to stop prioritization sessions
          continue-comparing? (and next-cursor-index (not user-is-quitting?))] ;; TODO: clarify intent of code here with a comment
      
      (if continue-comparing?
        (recur next-list next-cursor-index)
        (u/print-and-return
         {:input-string d/REVIEW-END 
          :is-debug? false
          :return-item next-list})))))


;; DONE: test using this with focus/do mode, as well as to confirm 
;; and then close out the about/read-me/text-exposition sections of 
;; the application (to then return back to the menu)
(defn- cli-press-enter-key-to-continue
  [{:keys [prompt]}]
  ;; TODO: refactor this to not use `let` if it is not needed
  (let [_ (println prompt)
        _ (read-line)])
  ;; TODO: replace this string with a map arg input 'successful-continue-msg'
  (when DEBUG-MODE-ON (println "Proceeding...")))


(defn- cli-take-keyboard-input
  "This function gets abritrary text from the user via keyboard input.
  Note: Prompt is optional."
  [{:keys [prompt]}]
  (loop []
    ;; TODO: refactor out not-nil? to be some? instead
    (when (some? prompt) (println prompt))
    (let [input (read-line)
          sanitized (s/trim input)]
      (if (and (some? sanitized) (seq sanitized))
        (u/print-and-return {:input-string (str "You entered '" sanitized "'.")
                             :is-debug? false
                             :return-item sanitized})
        (do
           ;; TODO: Replace the following with a string generator function
          (println (str "Input '" sanitized
                        "' is not valid."))
          (recur))))))


;; TODO: rename function to accomodate functionality *and* context
(defn- single-q?
  "used to determine if a CLI user is attempting to quit when inputting text"
  [input-text]
  (or (= (s/lower-case input-text) "q")
      (= (s/lower-case input-text) "quit")))


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
                (calc/gen-menu-string
                 {:menu-options menu-options-input
                  :menu-mappings menu-mappings
                  :separator d/NEWLINE}))))


;; TODO: refactor/convert this function into string generation 
;; to leave printing responsibilities to the caller/parent function
;; Suggested new name `gen-selection-prompt-with-number-input`
(defn- cli-ask-for-menu-input
  [options-cnt]
  ;; TODO: replace the following with string generator sandwich function
  (println (str
            "Select by number from the above menu [1-"
            options-cnt "]: ")))


;; TODO: convert into pure func by taking in args, not global state
(defn cli-do-menu-cycle
  "display the menu and get user's menu choice"
  [{:keys [;; TODO: REFACTOR: pass into `cli-do-menu-cycle` the base menu options, rather than the full list, and add more menu options on, rather than taking them away, for simpler, more readable code  
           input-menu ;; TODO: note here that this is a list of keywords
           input-menu-strings-map]}]
  (let [menu-length (count input-menu)]
    (cli-display-menu input-menu input-menu-strings-map)
    (cli-ask-for-menu-input menu-length)
    (let [menu-choice-number
         ;; TODO: refactor cli-get-number-in-range-inclusive call 
         ;;     signature to have clearer inputs ('lower' & 
         ;;     'upper' instead of 'x' & 'y')
          (cli-get-number-in-range-inclusive
           {:lower 1
            :upper menu-length
            :choice-confirm-func cli-choice-confirm
            :invalid-input-re-request invalid-input-msg-and-prompt})]
      (get input-menu (dec menu-choice-number)))))


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
        :return-item (l/conduct-add
                      {:input-list input-list
                       :input-text input-text})
        }))))


(defn- cli-clear-buffer []
  (when DEBUG-MODE-ON
    ;; TODO: relocate string to top of namespace
    (println "clearing the buffer...")) 
  (println "\033[2J")
  (println "\033[0;0H"))


;; TODO: enable the user to press *any* key when in a non-interactive page
;;       to return back to the main menu, rather than just the ENTER key 
(defn- print-wait-on-enter-key-then-return 
  [{:keys [output-text continue-prompt return-item 
           is-debug? debug-active?]}]
  (let [result (u/print-and-return
                {:input-string output-text
                 :is-debug? is-debug?
                 :debug-active? debug-active?
                 :return-item return-item})]
    (cli-press-enter-key-to-continue
     {:prompt continue-prompt})
    result))


(defn print-text-section-and-return-to-menu
  [{:keys [input-list section-text]}] 
    (cli-clear-buffer)
    (print-wait-on-enter-key-then-return
     {:continue-prompt RETURN-TO-MAIN-MENU-STRING
      :output-text (str section-text d/NEWLINE d/CLI-FENCE)
      :is-debug? false
      :return-item input-list}))


;; TODO: rename this function, and all instances of the 
;;       word 'focus' to replace with `take-action`
(defn cli-conduct-focus-action
  [{:keys [input-list]}]
  (let [take-action-results
        (l/conduct-take-action-on-list
         {:input-list input-list})

        {:keys
         [result result-with-work-remaining priority-item]}
        take-action-results

        priority-item-text
        (get priority-item :text)]

    (cli-clear-buffer)

    (println d/CLI-FENCE)
    
    ;; TODO: replace the following line with a string generation function
    (println (str "You are currently taking action on '" priority-item-text "'..."))
    (cli-press-enter-key-to-continue
     {:prompt d/ENTER-TO-CONTINUE})
      ;; TODO: Ask the user here if there is any remaining work left (yes/no question):
      ;;       - If yes, on top of marking the priority item as done, duplicate the 
    ;;       priority item and append it to the list.

    (let [work-remaining
          (= :yes
             (convert-answer-letter-to-keyword
              (cli-ask-question-with-limited-answer-set
               {:input-question (str "Is there work remaining to be done on '"
                                     priority-item-text "'? (answer Y for yes, N for no): ")
                :valid-answers valid-yn-answer-choices
                :invalid-input-response
                "Please type Y or N to answer 'yes' or 'no', and then hit the ENTER key."})))]
      (if work-remaining
        (u/print-and-return
         {:input-string (str "Duplicating priority item...\n"
                             "Marking the priority item as done...")
          :is-debug? false
          :return-item result-with-work-remaining})
        (u/print-and-return
         {:input-string "Marking the priority item as done..."
          :is-debug? false
          :return-item result})))))


(defn cli-do-app-action
  [input-action input-list]
  (condp = input-action
    d/ADD         (cli-conduct-add-action
                   {:input-list input-list
                    :prompt (get-in cli-texts [:adding :prompt])
                    :cancel-confirm (get-in cli-texts [:adding :cancel-confirm])
                    :success-confirm (get-in cli-texts [:adding :success-confirm])})

    ;;;; TODO: refactor cli-conduct-prioritization-review to use
    ;;;; get-index-of-first-new-item-after-priority-item internally,
    ;;;; rather than externally
    d/PRIORITIZE  (cli-conduct-prioritization-review
                   {:input-list input-list
                    :input-cursor-index
                    (l/get-index-of-first-new-item-after-priority-item
                     {:input-list input-list})})

    ;; TODO: implement the yes/no question asking after a user is done 
    ;; focusing/actioning of 'Is there work remaining on this task/item?'
    ;; TODO: implement the 'press any key' or 'press the ENTER key' to continue
    d/DO          (cli-conduct-focus-action {:input-list input-list})

    ;;;; overview-and-summary, detailed-steps, real-world-example
    d/ABOUT       (print-text-section-and-return-to-menu 
                   {:input-list input-list
                    :section-text (get d/about-texts :overview-and-summary)})

    d/EXAMPLE     (print-text-section-and-return-to-menu
                   {:input-list input-list
                    :section-text (get d/about-texts :real-world-example)}) 

    d/HOW-TO      (print-text-section-and-return-to-menu
                   {:input-list input-list
                    :section-text (get d/about-texts :detailed-steps)})

    ;; TODO: relocate quit confirmation string to af.data under `application-text` binding/name
    ;; TODO: implement serialization logic so that, upon quitting, 
    ;;       the user has their list autosaved to disk
    d/QUIT (when DEBUG-MODE-ON (println "Confirming 'quit' action..."))

    ;; default/else condition/case
    (println "Invalid action detected, error code 002.")))


;; TODO: relocate this function to calc namespace, as its functionality is general
(defn- gen-item-count-string [input-list]
  (if (= 1 (count input-list))
    (str d/NEWLINE "There is 1 item in your list.")
    (str d/NEWLINE "There are " (count input-list) " items in your list.")))


(defn- gen-list-render-output
  [{:keys [input-list]}]
  (if (zero? (count input-list))
    (str d/TODO-LIST-HEADER d/LIST-EMPTY)
    (str d/TODO-LIST-HEADER
         (l/stringify-list {:input-list input-list
                            :marks-dict d/cli-marks})
         (gen-item-count-string input-list))))


;; TODO: evaluate this function as a canditate for including in public API
(defn- cli-render-list [input-list]
  (println (gen-list-render-output {:input-list input-list})))


;; 2023_01_22 TIL: C-c C-k to evaluate an entire CIDER repl in Emacs in VSCode
(defn cli-do-app-cycles
  "A loop that runs the entire application."
  []
  (loop [;; initialize app state
         ;; initialize the user's to-do list
         t-time 0
         ;; TODO: replace `initial-list-state` with loaded list when 
         ;;       deserialization has been implemented
         app-state-map {:the-list d/initial-list-state}]
    (let [;; TODO: clear the terminal (probably right here?) for a 
          ;;       fresh display of the list, content, and menu choices
          ;; TODO: explore/experiment within different terminals / shells
          ;;       / OS's to confirm correct screen clearing behavior
          ;;_       (cli/clear)
          ;;_       (clojure.java.shell/sh "clear")
          ;;_       (r/cider-repl-clear-buffer)
          ;; TODO: learn how to clear the REPL buffer programmatically
          ;;       from within a program running in the REPL
          _        (cli-clear-buffer)

          _        (when (zero? t-time)
                     ;; TODO: relocate this string to the af.data namespace
                     (println d/WELCOME-MESSAGE 
                              ))

          current-list (get app-state-map :the-list)

          ;; render the user's to-do list to the command line
          _       (cli-render-list current-list)

          current-menu (vec 
                        (calc/sort-menu-options
                         {:input-unsorted
                          (calc/get-valid-menu-options
                           {:all-menu-options d/all-menu-options-sorted

                            :prioritizable?
                            (l/is-prioritizable-list? {:input-list current-list})
                            
                            :actionable? 
                            (l/is-doable-list? {:input-list current-list})})
                          
                          :input-order d/all-menu-options-sorted}))

          ;; display the menu and get user's menu choice
          action-input (cli-do-menu-cycle
                        {:input-menu current-menu ;; this takes in different menu options depending on the list state
                         :input-menu-strings-map d/menu-strings-map})

          ;; TODO: evaluate the purpose/utility of these display messages, 
          ;;       as well as, very importantly, which ones end up being 
          ;;       seen by the end-user
          ;; confirmation messages (i.e. these are *not* println debugging statements)
          _       (case action-input
                    :add-new-item (println "Let's make a new item now...")
                    :prioritize-list (println "Let's review the list to prioritize the items within...")
                    :do-priority-item (println "Let's focus on the priority item and start taking action on it...")
                    :about-autofocus (println "Displaying the about section...")
                    :af-example-irl (println "Displaying a real-world example of AutoFocus in action...")
                    :how-to-af (println "Displaying the how-to section...")
                    :quit-application (println "Quitting...")
                    :default (println "Unknown action entered, error code 0001."))

          ;; update the list based on the user's menu choice
          new-list (cli-do-app-action action-input current-list)]
      (if (= d/QUIT action-input)
        ;; TODO: relocate this string to af.data namespace
        (println d/THANKS-BYE)
        (recur
         (inc t-time)
         {:the-list new-list})))))

