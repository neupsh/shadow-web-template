(ns config.firebase-config)


;; This is public facing non-secret config object
;; https://firebase.google.com/docs/projects/learn-more?authuser=0#config-files-objects
(def configObject #js {
                       :apiKey "",
                       :authDomain "",
                       :databaseURL "",
                       :projectId "",
                       :storageBucket "",
                       :messagingSenderId "",
                       :appId ""
                       })
