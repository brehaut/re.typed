(ns re.typed
  (:require [clojure.core.typed :as t :refer [ann-form]])
  (:refer-clojure :exclude [find seq])
  (:import (java.util.regex Matcher
                            Pattern)
           (clojure.lang IPersistentVector)))

(t/ann matches
     (All [a] [[Matcher -> a] Pattern String -> (t/Option a)]))
(defn matches
  "equivalent to re-matches but parameterised over how to handle matches"
  [handle-match ^Pattern re s]
  (let [m (re-matcher re s)]
    (when (.matches m)
      (handle-match m))))

(t/ann find
     (All [a]
          (Fn [[Matcher -> a] Pattern String -> (t/Option a)]
              [[Matcher -> a] Matcher -> (t/Option a)])))

(defn find
  "equivalent to re-find but parameterised over how to handle matches"
  ([handle-match ^Matcher m]
     (when (.find m)
       (handle-match m)))
  ([handle-match ^Pattern re s]
     (find handle-match (re-matcher re s))))


(t/ann seq
     (All [a]
          [[Matcher -> a] Pattern String -> (t/Seq a)]))
(defn seq
  "Equivalent to re-seq but paraterised over how to handle matches"
  [handle-match ^Pattern re s]
  (let [m (re-matcher re s)
        step (ann-form (fn step []
                         (lazy-seq
                          (when (.find m)
                            (cons (handle-match m) (step)))))
                       [-> (clojure.lang.ISeq a)])]    
    (step)))


;; match handlers

(t/ann match
     [Matcher -> (t/Option String)])
(defn match
  [^Matcher m]
  (.group m))


(t/ann groups
     (Fn [Matcher -> (t/Vec (t/Option String))]
         [Matcher (U (Value 0) (Value 1)) ->
          (t/Vec (t/Option String))]))
(defn ^:no-check groups  
  ([^Matcher m min]
     (let [gc (.groupCount m)]
       (t/loop [ret :- (t/Vec String), []
                c :- t/Int, min]
         (if (<= c gc)
           (recur (conj ret (.group m c)) (inc c))
           ret))))
  ([^Matcher m]
     (groups m 1)))


(t/ann all [Matcher -> (t/Vec (t/Option String))])
(defn all [^Matcher m]
  (groups m 0))


(t/ann found [Matcher -> true])
(def found (constantly true))
