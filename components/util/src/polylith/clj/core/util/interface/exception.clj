(ns polylith.clj.core.util.interface.exception
  (:require [clojure.stacktrace :as stacktrace]
            [polylith.clj.core.util.interface.color :as color]))

(defn print-error-message [e]
  (when-let [message (.getMessage ^Exception e)]
    (println message)))

(defn print-stacktrace [e]
  (stacktrace/print-stack-trace e))

(defn print-exception [cmd e]
  (println (or (-> e ex-data :err)
               (if (= "test" cmd)
                 (.getMessage ^Exception e)
                 e))))
