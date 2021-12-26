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
    cider-jack-in-clj&cljs
    lein
    shadow
    :app

    from the clj repl:
    (ns poet-two.core)
    (-main)
    (require '[shadow.cljs.devtools.server])
    (shadow.cljs.devtools.server/start!)

    then from the cljs repl:
    (do (require '[shadow.cljs.devtools.api :as shadow]) (shadow/watch :app) (shadow/nrepl-select :app))

    that results in a working app
    and C-c C-z works to switch between...but completion didn't work...

    once I evaluated an s-expression with C-x C-e
    from within a cljs file, cider completion began working.
    But trying to say (js/alert "fyf") from cljs repl bombs with
    "No such namespace js"

    https://docs.cider.mx/cider/cljs/shadow-cljs.html
    https://github.com/clojure-emacs/cider/issues/2946
    https://shadow-cljs.github.io/docs/UsersGuide.html
    https://shadow-cljs.github.io/docs/UsersGuide.html#embedded
    may be worth investigating
## License

Copyright © 2021 FIXME
