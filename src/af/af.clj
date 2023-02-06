(ns af.af 
  "A task management system command line application project called AutoFocus."
  (:require
   [af.cli :as cli]
   ;; TODO: resolve issue where clojure.tools.cli (cli/clear) is not found
   ;; [clojure.tools.cli :as cli]
   ))

;; TODO: create af.cli namespace for cli specific functionality
;; TODO: have the user enter any key when in a non-interactive page to return back to the main menu, rather than simply 'returning' immediately, so as to give the user time to read said page's contents first w/o the visual noise of also seeing the list and menu again


;;;; TODO: implement console clearing


  ;; Q: Is this file this project's "entry point"?
  ;; Q: What is the difference in purpose/intent between af.af and af.core, as intended by the authors of tools.cli?
;; Q: What exactly is the point of the exec function?
(defn exec
  "Invoke me with clojure -X af.entry-point/exec"
  [opts]
  (println "exec with" opts))


;; Q: What are the meaningful differences and similarities in purpose/role/functionality between the exec and main functions?
(defn -main
  "Invoke me with clojure -M -m af.entry-point"
  [& args]
  ;; (println "-main with" args)
  ;; (let [input (read-line)]
  ;;   (if (or (= input "yes") (= input "no"))
  ;;     (println "Thanks for your input")
  ;;     (println "Invalid input. Please enter either 'yes' or 'no'")))
  (cli/cli-do-app-cycle))

