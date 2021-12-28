(ns poet-two.dict
  (:require
   [clj-http.client :as http]
   [clojure.string :as string]
   [clojure.walk :as walk]
   [clojure.java.io :as io :refer (reader)]
   [poet-two.db :as db]
   ;;[taoensso.carmine :as car :refer (wcar)]
   ))

;; dictionary
(def API-KEY "024844f0-9923-4097-a0cf-c71d87a79b6f")
;; thesaurus
(def THE-KEY "4f66e758-7105-411d-9a61-0da4f5df35bf")

(def BASE-URL "https://www.dictionaryapi.com/api/v3/references/collegiate/json/")

;; TODO?
;; poet-two.util
(defn weighted-choice [option-one option-two chance]
  (if (< (rand) chance)
    (option-one)
    (option-two)))

(defn conj-if-value [col nil-or-value]
  (if (empty? nil-or-value)
    col
    (conj col nil-or-value)))

(defn empty-or-many [f chance-for-empty]
  (if (< chance-for-empty (rand))
    []
    (conj-if-value [(f)] (empty-or-many f chance-for-empty))))

(defn get-words []
  (map
   string/trim
   (with-open [rdr (io/reader "resources/seed_words.txt")]
     (doall (line-seq rdr)))))

(def WORDS (get-words))

(defn build-api-query-string []
  (str "?key=" API-KEY))

(defn api-lookup [word]
  (let [w (str BASE-URL word (build-api-query-string))]
    (http/get w {:as :json})))

(defn contains-all? [m ks]
  (every? (fn [k] (contains? m k)) ks))

(defn valid-response? [res]
  (and (contains-all? res [:status :body])
       (= (:status res) 200)
       (> (count (:body res)) 0)
       (map? (first (:body res)))))

(defn process-response [res]
  "Accepts: a response from the MW API
   Return: a sequence of definition maps"
  (map
   (fn [w]
     {:part-of-speech (:fl w)
      :word (:id (:meta w))
      :origin-date (:date w)
      :definitions (:shortdef w)})
   (:body res)))

(defn not-main []
  (map
   (fn [word]
     (if (nil? (db/word-search (:word word)))
       (db/word-insert word)
       (println (str "entry for " (:word word) " exists...doing nothing"))))
   ;; next time:
   ;; (take 1000 (drop 4500 WORDS)
   (->> (map api-lookup (take 1000 (drop 3500 WORDS)))
        (filter valid-response?)
        (map process-response)
        flatten)))


(def THE-DICT (db/word-get-all))
(def WORD-COUNT (count THE-DICT))

(defn trim-word-tag [s]
  (apply str (take-while #(not (= \: %)) s)))

(defn get-pos [pos]
  (map (fn [[word-key word-map]]
         (merge {:key word-key}
                (assoc word-map
                       :word
                       (trim-word-tag (:word word-map)))))
       (filter (fn [[k v]]
                 (= pos (:part-of-speech v)))
               THE-DICT)))

(defn get-nouns []
  (get-pos "noun"))

(defn noun []
  {:noun (first (shuffle (get-nouns)))})

(defn get-verbs []
  (get-pos "verb"))

(defn verb []
  {:verb (first (shuffle (get-verbs)))})

(defn get-adjectives []
  (get-pos "adjective"))

(defn adjective []
  (first (shuffle (get-adjectives))))

(defn get-biographical-names []
  (get-pos "biographical name"))

(defn biographical-name []
  (first (shuffle (get-biographical-names))))

(defn get-idioms []
  (get-pos "idiom"))

(defn idiom []
  (first (shuffle (get-idioms))))

(defn get-geographical-names []
  (get-pos "geographical name"))

(defn geographical-name []
  (first (shuffle (get-geographical-names))))

(defn get-adverbs []
  (get-pos "adverb"))

(defn adverb []
  (first (shuffle (get-adverbs))))

(defn get-prefixes []
  (get-pos "prefix"))

(defn prefix []
  (first (shuffle (get-prefixes))))

(defn get-abbreviations []
  (get-pos "abbreviation"))

(defn abbreviation []
  (first (shuffle (get-abbreviations))))

(defn get-noun-phrases []
  (get-pos "noun phrase"))

(defn noun-phrase []
  (first (shuffle (get-noun-phrases))))

(defn get-prepositions []
  (get-pos "preposition"))

(defn preposition []
  (first (shuffle (get-prepositions))))

;; sticks a :position key in each of ms
;; whose value is a zero-based index of
;; the order in which it was processed
(defn assign-positions [ms]
  (into []
        (map (fn [n]
               (let [w (nth ms n)]
                 ;;(println "n: " n)
                 ;;(println "w: " w)
                 (assoc w :position n)))
             (range (count ms)))))

(defn preposition* []
  {:preposition*
   (assign-positions (flatten (empty-or-many preposition 0.3)))})

(defn get-phrasal-verbs []
  (get-pos "phrasal verb"))

(defn phrasal-verb []
  {:phrasal-verb
   (first (shuffle (get-phrasal-verbs)))})

(defn get-french-phrases []
  (get-pos "French phrase"))

(defn french-phrase []
  (first (shuffle (get-french-phrases))))

(defn get-conjunctions []
  (get-pos "conjunction"))

(defn conjunction []
  (first (shuffle (get-conjunctions))))

(defn get-adjective-combining-forms []
  (get-pos "adjective combining form"))

(defn adjective-combining-form []
  (first (shuffle (get-adjective-combining-forms))))

(defn get-auxiliary-verbs []
  (get-pos "auxiliary verb"))

(defn auxiliary-verb []
  (first (shuffle (get-auxiliary-verbs))))

(defn get-latin-phrases []
  (get-pos "Latin phrase"))

(defn latin-phrase []
  (first (shuffle (get-latin-phrases))))

(defn get-interjections []
  (get-pos "interjection"))

(defn interjection []
  (first (shuffle (get-interjections))))

(defn get-pluaral-nouns []
  (get-pos "plural noun"))

(defn plural-noun []
  {:plural-noun (first (shuffle (get-pluaral-nouns)))})

(defn random-word []
  (first (shuffle (seq THE-DICT))))

(defn unfned-words []
  (let [seen ["verb" "noun" "adjective" "biographical name" "idiom" "geographical name"
              "adverb" "prefix" "abbreviation" "noun phrase" "preposition" "phrasal verb"
              "French phrase" "conjunction" "adjective combining form" "auxiliary verb"
              "Latin phrase" "interjection" "plural noun" nil]]
    (filter (fn [[k v]]
              (not-any? (fn [sw] (= sw (:part-of-speech v))) seen))
            THE-DICT)))

(defn rand-elt [elts]
   (nth elts (rand-int (count elts))))

(defn get-articles []
  [{:key "a"
    :word "a"
    :part-of-speech "article"
    :origin-date nil
    :definitions ["used as a function word before singular nouns when the referent is unspecified"]}
   {:key "the"
    :word "the"
    :part-of-speech "article"
    :origin-date nil
    :definitions ["used to indicate a person or thing that has already been mentioned or seen or is clearly understood from the situation"
                  "used to refer to things or people that are common in daily life"
                  "used to refer to things that occur in nature"]}])

(defn article []
  {:article (first (shuffle (get-articles)))})

;; TODO
;; get-pronouns, etc

(defn spaces-to-dashes [s]
  (clojure.string/replace s " " "-"))

(def SUBJECTS [[article noun] [plural-noun]])
(defn subject []
  (let [phrase (reduce
                merge
                (map #(%) (rand-elt SUBJECTS)))
        order (into [] (keys phrase))]
    ;;(println phrase)
    ;; (merge (reduce merge phrase)
    ;;        {:order order})
    (merge phrase
           {:order order})))

(def PREDICATES [[verb]])
;;(def s (into {} (map (fn [m] [(keyword (:part-of-speech m)) m]) s)))
(defn predicate []
  (let [phrase (reduce
                merge
                (map #(%) (rand-elt PREDICATES)))
        order (into [] (keys phrase))]
    (merge phrase {:order order})))

(defn walk-map-with-keys [f m ks]
  (reduce merge (map (fn [k]
                       {k (f (k m))})
        ks)))

;; TODO
;; get smarter
(defn pluralize [w]
  (assoc w :word (str (:word w) "s")))

(def TENSES [:past :present :future])
(defn tense [] (rand-elt TENSES))

;; could also pass in :order keys zipmapped w/ functions to be applied for each key
(defn walk-sentence [f s]
  (merge
   (walk-map-with-keys f (:subject s) (get-in s [:subject :order]))
   (walk-map-with-keys f (:predicate s) (get-in s [:predicate :order]))))

(defn sentence-words [s]
  (flatten
   (cons (vals
          (walk-map-with-keys #(:word %) (:subject s) (get-in s [:subject :order])))
         (vals
          (walk-map-with-keys #(:word %) (:predicate s) (get-in s [:predicate :order]))))))

(defn print-sentence [s]
  (let [words (sentence-words s)
        first-word (clojure.string/capitalize (first words))
        other-words (reduce (fn [this that]
                              (if (nil? that)
                                (str this)
                                (str this " " that))) (rest words))]
    (print (str first-word " "))
    (print (str other-words "."))
    (println)))

;; TODO
;; more smarter
;;
;; In the future, there should be default "rules"
;; specified by this function that are taken
;; unless there is already a tense entry for the
;; given word in the system, whatever that means.
(defn text-word->past-tense [text]
  (cond (= \e (last text))  (str text "d")
        :else (str text "ed")))

;; TODO
;; more smarter
(defn text-word->present-tense [text]
  (str text "s"))

(defn text-word->future-tense [text]
  (str "will " text))

(defn word->past-tense [word]
  (let [transformed-text (text-word->past-tense (:word word))]
    (assoc word :word transformed-text)))

(defn word->present-tense [word]
  (let [transformed-text (text-word->present-tense (:word word))]
    (assoc word :word transformed-text)))

(defn word->future-tense [word]
  (let [transformed-text (text-word->future-tense (:word word))]
    (assoc word :word transformed-text)))

(defn tensify-word [word tense]
  (case tense
    :past (word->past-tense word)
    :present (word->present-tense word)
    :future (word->future-tense word)))

(defn walk-sentence [f s]
  (merge
   (walk-map-with-keys f (:subject s) (get-in s [:subject :order]))
   (walk-map-with-keys f (:predicate s) (get-in s [:predicate :order]))))

(defn tensify-sentence [s tense]
  (assoc s :predicate (merge (walk-map-with-keys
                              (fn [w] (tensify-word w tense))
                              (:predicate s)
                              (get-in s [:predicate :order]))
                             {:order (get-in s [:predicate :order])})))

(defn sentence []
  (let [tense (tense)
        timestamp (db/generate-current-timestamp)
        s {:subject (subject)
           :predicate (predicate)
           :info {:timestamp timestamp
                  :tense tense}}]
    (tensify-sentence s tense)))

;; Sentence => Noun-Phrase + Verb-Phrase
;; Noun-Phrase => Article + Adj* + Noun + PP* | Noun
;; Verb-Phrase => Verb + Noun-Phrase
;; Adj* => 0, Adj + Adj*
;; PP* => 0, PP + PP*
;; PP => Prep + Noun-Phrase
;; Adj => big, little, blue, green
;; Prep => to, in, by, with

;; a response from api-lookup has a :body
;; and that body is a LazySeq of matching words
;; each entry of the seq has
;; :uros (pronunciantion)
;; :date (of origin)
;; :meta (which has :id (e.g. "fouul:1"...e.g. the key of the word) src, stems
;; :et (etymology)
;; :hom (no idea)
;; :shortdef (concise defintion)
;; :def (sequence of n detailed definitions)
;; :fl (part of speech)
;; :syns (synonyms)
;; :hwi (info about audio file of word spoken?)
