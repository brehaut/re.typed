(ns re.typed
  (:require [clojure.core.typed :refer [Option AnyInteger
                                        ann ann-form loop> tc-ignore]])
  (:refer-clojure :exclude [find seq])
  (:import (java.util.regex Matcher
                            Pattern)
           (clojure.lang ISeq
                         IPersistentVector)))

(ann matches
     (All [a] [[Matcher -> a] Pattern String -> (Option a)]))
(defn matches
  "equivalent to re-matches but parameterised over how to handle matches"
  [handle-match ^Pattern re s]
  (let [m (re-matcher re s)]
    (when (.matches m)
      (handle-match m))))

(ann find
     (All [a]
          (Fn [[Matcher -> a] Pattern String -> (Option a)]
              [[Matcher -> a] Matcher -> (Option a)])))

(defn find
  "equivalent to re-find but parameterised over how to handle matches"
  ([handle-match ^Matcher m]
     (when (.find m)
       (handle-match m)))
  ([handle-match ^Pattern re s]
     (find handle-match (re-matcher re s))))


(ann seq
     (All [a]
          [[Matcher -> a] Pattern String -> (ISeq a)]))
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

(ann match
     [Matcher -> (Option String)])
(defn match
  [^Matcher m]
  (.group m))


(ann groups
     (Fn [Matcher -> (IPersistentVector (Option String))]
         [Matcher (U (Value 0) (Value 1)) ->
          (IPersistentVector (Option String))]))
(defn ^:no-check groups  
  ([^Matcher m min]
     (let [gc (.groupCount m)]
       (loop> [ret :- (IPersistentVector String) [],
               c :- AnyInteger min]
              (if (<= c gc)
                (recur (conj ret (.group m c)) (inc c))
                ret))))
  ([^Matcher m]
     (groups m 1)))


(ann all
     [Matcher -> (IPersistentVector (Option String))])
(defn all [^Matcher m]
  (groups m 0))


(ann found [Matcher -> true])
(def found (constantly true))