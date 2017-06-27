(ns haka-buddy.backend
  (:require [buddy.auth.protocols :as proto]
            [haka-buddy.util :as shibbo]))

(defn- handle-unauthorized-default
  "A default response constructor for an unauthorized request."
  [_]
  {:status 401 :headers {"Content-Type" "application/json"}
   :body "{\"status\": 401, \"detail\": \"Unauthorized\"}"})

(defn haka-login-valid? [shibbo-vals]
  (let [user-ids #{"eppn"}
        ids-in-shibbo (clojure.set/intersection user-ids (set (keys shibbo-vals)))
        has-id (not (empty? ids-in-shibbo))]
    has-id))

(defn shibbo-backend
  [& [{:keys [names checkfn] :or {checkfn haka-login-valid?}}]]
  (reify
    proto/IAuthentication
    (-parse [_ request]
      (shibbo/get-attributes request names))
    (-authenticate [_ request shib-attribs]
      (let [id
            (when (checkfn shib-attribs) shib-attribs)]
        id))))

(defn authz-backend
  []
  (reify
    proto/IAuthorization
    (-handle-unauthorized [_ request metadata]
      (handle-unauthorized-default request))))
