(ns af.af 
  ;; Q: Is this file this project's "entry point"?
  "FIXME: my new org.corfield.new/scratch project.")

;; Q: What exactly is the point of the exec function?
(defn exec
  "Invoke me with clojure -X af.entry-point/exec"
  [opts]
  (println "exec with" opts))

;; Q: What are the meaningful differences and similarities in purpose/role/functionality between the exec and main functions?
(defn -main
  "Invoke me with clojure -M -m af.entry-point"
  [& args]
  (println "-main with" args))
