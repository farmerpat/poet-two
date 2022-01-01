(ns poet-two.db-two
  (:require
   [clojure.string :as string]
   [next.jdbc :as jdbc]
   [honey.sql :as sql]
   [honey.sql.helpers :as h]
   [poet-two.util :refer [keywordize-orders]]))

(def ds (jdbc/get-datasource "jdbc:sqlite:poet.db"))

(defn init! []
  (jdbc/execute!
   ds
   (-> (h/create-table :head-word :if-not-exists)
       (h/with-columns [[:id
                         :integer
                         (sql/call :primary-key)]
                        [:word :text]
                        ;; default value of generate-current-timestamp?
                        [:timestamp :text]])
       (sql/format)))

  (jdbc/execute!
   ds
   (-> (h/create-table :definitions :if-not-exists)
       (h/with-columns [[:id
                         :integer
                         (sql/call :primary-key)]
                        [:hw-id :integer]
                        [:number :integer]
                        [:part-of-speech :text]
                        [:definition :text]
                        [:timestamp :text]])
       (sql/format))))

(defn insert-definition! [hw-id number part-of-speech definition]
  (jdbc/execute!
   ds
   (-> (h/insert-into :definitions)
       (h/columns :hw-id :number :part-of-speech :definition :timestamp)
       (h/values [[hw-id number part-of-speech definition (generate-current-timestamp)]])
       (sql/format {:pretty true}))
   {:return-keys true}))

(defn get-definitions []
  (jdbc/execute!
   ds
   (-> {:select [:*]
        :from [:definitions]
        :where [:= 1 1]}
       (sql/format))))

(defn get-headword [word]
  (jdbc/execute-one!
   ds
   (-> {:select [:*]
        :from [:head-word]
        :where [:= :word word]}
       (sql/format))))

(defn get-headwords []
  (jdbc/execute!
   ds
   (-> {:select [:*]
        :from [:head-word]}
       (sql/format))))

(defn get-headword-definitions [hw-id]
  (jdbc/execute!
   ds
   (-> (h/select :*)
       (h/from [:head-word :hw])
       (h/join [:definitions :d] [:= :d.hw-id :hw.id])
       (h/order-by [:d.number :asc])
       (sql/format {:pretty true}))))

(defn insert-headword! [word]
  (jdbc/execute!
   ds
   (sql/format
    {:insert-into :head-word
     :columns [:word :timestamp]
     :values [[word (generate-current-timestamp)]]})
   {:return-keys true}))

(defn update-headword-word! [id word]
  (jdbc/execute!
   ds
   (-> (h/update :head-word)
       (h/set {:word word
               :timestamp (generate-current-timestamp)})
       (h/where [:= :id id])
       (sql/format))
   {:return-keys true}))

(defn drop-table! [table]
  (jdbc/execute!
   ds
   (sql/format
    {:drop-table table})))

(defn generate-current-timestamp []
  (let [now (java.time.Instant/now)]
    (str (.getEpochSecond now) "-" (.getNano now))))
