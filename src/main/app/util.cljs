(ns app.util)

(defn non-neg-minus
  [& more]
  (let [n (apply - more)]
    (if (neg? n) 0 n)))
