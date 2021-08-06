(ns polylith.clj.core.poly-cli.core
  (:require [polylith.clj.core.command.interface :as command]
            [polylith.clj.core.util.interface.exception :as ex]
            [polylith.clj.core.user-input.interface :as user-input]
            [polylith.clj.core.poly-cli.native :as native])
  (:gen-class))

(defn -main
  "This is the entry point of the 'poly' command.

   It reads the input from the incoming arguments and delegates to the 'command' component,
   that reads the workspace in the read-workspace function in its core namespace:
     (-> user-input
         ws-clj/workspace-from-disk
         ws/enrich-workspace
         change/with-changes)

   - The first step ws-clj/workspace-from-disk reads the workspace from disk (or file if 'ws-file'
     is given).
   - The second step ws/enrich-workspace takes that hash map, which represents the whole workspace,
     as input and performs different kind of validations and enrichments. Worth noticing here
     is that all io operations are done in the first step while this step only operates on the
     hash map that was returned from the first step.
   - The last step change/with-changes, operates on the enhanced hash map, which again is an
     in-memory representation of the workspace. It calculates what components, bases and projects
     that are changed, and what bricks and projects to test, and adds the result to the :changes key.
   - The final workspace representation is then used by the given command that is stored in
     the 'user-input' representation."
  [& args]
  (let [input (user-input/extract-params args)]
    (if (native/should-fallback? args)
      (native/execute-in-jvm-poly! args)
      (try
        (if (:is-no-exit input)
          (-> input command/execute-command)
          (-> input command/execute-command System/exit))
        (catch Exception e
          (ex/print-exception (:cmd input) e)
          (when (-> input :is-no-exit not)
            (System/exit 1)))))))

;; Uncomment me then run `java -jar poly.jar test`
;; Adapted from https://github.com/borkdude/tools-deps-native-experiment/blob/master/src/borkdude/tdn/main.clj
;; All credits to @borkdude
#_(do
    (require
     '[clojure.edn :as edn]
     '[clojure.string :as str])

    (println
     (->> (map ns-name (all-ns))
          (remove #(clojure.string/starts-with? % "clojure"))
          (map #(clojure.string/split (str %) #"\."))
          (keep butlast)
          (map #(clojure.string/join "." %))
          distinct
          (map munge)
          (cons "clojure")
          (str/join ","))))

(comment
  (-main "test" ":all" ":no-exit")
  (-main "ws" "ws-dir:/var/folders/q7/ky18vssj6jz0mhfr8lcv1xzh0000gp/T/local_dep-2021-07-31-205208.WGAtrHHE/local-dep-old-format" ":no-exit" "replace:/Users/joakimtengstrand:USER-HOME:/var/folders/q7/ky18vssj6jz0mhfr8lcv1xzh0000gp/T/local_dep-2021-07-31-205208.WGAtrHHE/local-dep-old-format:WS-DIR")
  (-main "ws" "ws-dir:/var/folders/q7/ky18vssj6jz0mhfr8lcv1xzh0000gp/T/local_dep-2021-07-31-205208.WGAtrHHE/local-dep-old-format" ":no-exit" "replace:/Users/joakimtengstrand:USER-HOME:/var/folders/q7/ky18vssj6jz0mhfr8lcv1xzh0000gp/T/local_dep-2021-07-31-205208.WGAtrHHE/local-dep-old-format:WS-DIR" "get:user-input"))
