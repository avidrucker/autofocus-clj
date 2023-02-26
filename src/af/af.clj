(ns af.af 
  "A task management system command line application project called AutoFocus."
  (:require
   [af.cli :as cli]
   ;; TODO: resolve issue where clojure.tools.cli (cli/clear) is not found
   ;; [clojure.tools.cli :as cli]
   ))

;; DONE: create af.cli namespace for cli specific functionality
;; DONE: have the user press the ENTER key for "non-interactive" pages to return back to the main menu
;; DONE: implement console clearing in the terminal
;; TODO: implement console clearing in the REPL


  ;; Q: Is this file this project's "entry point"?
  ;; Q: What is the difference in purpose/intent between af.af and af.core, as intended by the authors of tools.cli, or the authors of neil?
;; Q: What exactly is the point of the exec function in contrast with -main?
#_(defn exec
  "Invoke me with `clojure -X af.entry-point/exec`"
  [opts]
  (println "exec with" opts))


;; Q: What are the meaningful differences and similarities in purpose/role/functionality between the exec and main functions?
(defn -main
  "Invoke me with `clojure -M -m af.entry-point` or `clj -M -m af.af`"
  [& args]
  (cli/cli-do-app-cycles))

