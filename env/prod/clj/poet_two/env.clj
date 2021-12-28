(ns poet-two.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[poet-two started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[poet-two has shut down successfully]=-"))
   :middleware identity})
