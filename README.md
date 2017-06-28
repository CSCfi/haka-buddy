[![Build Status](https://travis-ci.org/CSCfi/haka-buddy.svg?branch=master)](https://travis-ci.org/CSCfi/haka-buddy)
# Prerequisite

In order to be able to use this library you should have a Shibboleth SP installed and configured appropriately. More information can be found from the [Shibboleth wiki](https://wiki.shibboleth.net/confluence/display/SHIB2). You can also benefit from [ansible-role-shibboleth-sp](//github.com/CSCfi/ansible-role-shibboleth-sp) designed at CSC. Please note that the role is still under development and non backward compatible changes may apply.

# Introduction

haka-buddy is a Clojure library designed to provide a Haka SP based authentication and authorization backend for Buddy-Auth library. This library provides the following features:
* Haka authentication handler to pass to buddy-auth wrap-authentication for Ring middleware
* Haka authorization handler to pass to buddy-auth wrap-authorization for Ring middleware

# Install

In order to take haka-buddy into use in a clojure project, simply include the dependency vector in your **project.clj**:

`[haka-buddy "0.1.0"]`

# Usage

Haka-buddy relies on shibbboleth session support and by default provides mandatory attributes released by the SP.

```clojure
(require '[haka-buddy.backends :as backends])

;; Wrap the ring handler.
(def app (-> my-handler
             (wrap-authentication backends/shibbo-backend)
             (wrap-authorization backends/authz-backend)))
```

Custom attributes can be requested by providing the backend a hashmap, containing a `:names` key associated with a vector of attribute names, as an argument.

The backend could be then setup in the following way:

```clojure
(require '[haka-buddy.backends :as backends])

;; Setup backend
(def backend (backends/shibbo-backend {:names ["my" "custom" "attributes"]}))

;; Wrap the ring handler.
(def app (-> my-handler
             (wrap-authentication backend)
             (wrap-authorization backends/authz-backend)))
```

A custom function for validating a valid login can be provided in a similar manner. The function is expected to return a boolean value. Here is an example of a backend that would let an user login as long as some of the three custom attributes is released by the shibboleth sp:

```clojure
(require '[haka-buddy.backends :as backends])

;; Custom validator
(defn my-validator? [shibbo-attributes]
  (not (empty? shibbo-attributes)))

;; Setup backend
(def backend (backends/shibbo-backend {:names ["my" "custom" "attributes"]
                                       :checkfn my-validator?}))

;; Wrap the ring handler.
(def app (-> my-handler
             (wrap-authentication backend)
             (wrap-authorization backends/authz-backend)))
```

In case the attributes are to be provided by the SP through http headers instead of AJP, the ring handler should be wrapped in the following way:

```clojure
;; Wrap the ring handler.
(def app (-> my-handler
             (wrap-authentication (backends/shibbo-backend {:use-headers? true}))
             ...)
```
The default value for `:use-headers` is set to false. Note that the attribute names returned by the http headers might differ from those returned by the AJP.
