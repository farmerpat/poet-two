(ns poet-two.dict
  (:require
   [clj-http.client :as http]
   [clojure.string :as string]
   [clojure.java.io :as io :refer (reader)]
   [poet-two.db :as db]
   [taoensso.carmine :as car :refer (wcar)]))

;; dictionary
(def API-KEY "024844f0-9923-4097-a0cf-c71d87a79b6f")
;; thesaurus
(def THE-KEY "4f66e758-7105-411d-9a61-0da4f5df35bf")

(def BASE-URL "https://www.dictionaryapi.com/api/v3/references/collegiate/json/")

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

(defn main []
  (map
   (fn [word]
     (if (nil? (db/search (:word word)))
       (db/insert word)
       (println (str "entry for " (:word word) " exists...doing nothing"))))
   ;; next time:
   ;; (take 1000 (drop 2500 WORDS)
   (->> (map api-lookup (take 1000 (drop 1500 WORDS)))
        (filter valid-response?)
        (map process-response)
        flatten)))

(def THE-DICT (db/get-all))
(def WORD-COUNT (count THE-DICT))

(defn get-pos [pos]
  (filter (fn [[k v]]
            (= pos (:part-of-speech v)))
          THE-DICT))

(defn get-nouns []
  (get-pos "noun"))

(defn noun []
  (first (shuffle (get-nouns))))

(defn get-verbs []
  (get-pos "verb"))

(defn verb []
  (first (shuffle (get-verbs))))

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

(defn get-phrasal-verbs []
  (get-pos "phrasal verb"))

(defn phrasal-verb []
  (first (shuffle (get-phrasal-verbs))))

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

(defn random-word []
  (first (shuffle (seq THE-DICT))))

(defn unfned-words []
  (let [seen ["verb" "noun" "adjective" "biographical name" "idiom" "geographical name"
              "adverb" "prefix" "abbreviation" "noun phrase" "preposition" "phrasal verb"
              "French phrase" "conjunction" "adjective combining form" "auxiliary verb"
              "Latin phrase" "interjection" nil]]
    (filter (fn [[k v]]
              (not-any? (fn [sw] (= sw (:part-of-speech v))) seen))
            THE-DICT))
  )

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
