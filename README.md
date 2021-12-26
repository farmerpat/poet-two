# poet-two

generated using Luminus version "4.29"
a la
lein new luminus poet-three +re-frame +shadow-cljs

FIXME

## Prerequisites

You will need [Leiningen][1] 2.0 or above installed.

[1]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein run 
    npm install -g shadow-cljs
    npm install
    npx shadow-cljs watch app

To connect to the browser repl via cider:
    cider-connect-cljs
    localhost

## Running with cider

    set cider/cider-nrepl version to 0.27.4 to deal w/ clojures complaints
    open emacs
    cd to project root
    open a .clj file
    C-c M-j
    lein
    in cider repl:
    (ns poet-two.core)
    (-main)
    (require '[shadow.cljs.devtools.server :as server])
    (require '[shadow.cljs.devtools.api :as shadow])
    (server/start!)
    (shadow/watch :app)
    open a .cljs file
    cider-connect-cljs
    localhost
    7002
    shadow
    :app
    Yahtzee

## License

Copyright © 2021 FIXME
