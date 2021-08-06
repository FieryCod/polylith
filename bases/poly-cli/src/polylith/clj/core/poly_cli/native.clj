(ns polylith.clj.core.poly-cli.native
  (:import
   [java.lang ProcessBuilder Process]))

(def FALLBACK_COMMANDS #{"test"})

(defn should-fallback?
  [args]
  (and (= (System/getProperty "org.graalvm.nativeimage.kind") "executable")
       (contains? FALLBACK_COMMANDS (first args))))

(defn execute-in-jvm-poly!
  "Executes the command that is known to fail in native image in java based poly tool version"
  [args]
  (let [pb (doto (ProcessBuilder. ^java.util.List (concat ["./poly-jar"] (vec args)))
                  (.inheritIO))
        process (.start pb)
        _ (.waitFor ^Process process)
        exit-value (.exitValue ^Process process)]
    (System/exit exit-value)))
