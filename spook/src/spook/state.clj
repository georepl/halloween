(ns spook.state)


(def state (atom {}))

(defn current-state []
  @state)

(defn init-state [new-state]
  (swap! state conj new-state))

(defn assoc-state [key val]
  (swap! state assoc key val))
