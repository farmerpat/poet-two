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

## Running with cider

### Now (was hamstrung by CFTBAT. Blew out .emacs.d and started from scratch):
```code
    C-c C-x j m -> shadow-cljs -> shadow -> :app
```

	In clj repl:
```code
	(in-ns 'poet-two.core)
	(-main)
```

### Used to be:

	set cider/cider-nrepl version to 0.27.4 to deal w/ clojures complaints
    open emacs
    cd to project root
    open a .clj file
    C-c M-j
    lein
    in cider repl:
    (in-ns poet-two.core)
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

    NOTE:
    Actually. Both clj and cljs repls think that the
    last repl buffer to have been visited is the correct
    one.
    https://github.com/clojure-emacs/cider/issues/2351

    But by https://github.com/clojure-emacs/cider/issues/2547
    maybe I'm doing it wrong...
    Perhaps
    C-c C-x j m -> shadow-cljs -> shadow -> :app

	Too many hours later, finally saw:
	...[WARNING]
	something in your configuration activated clojure-mode instead of clojurescript-mode
	this could cause problems....

## License

Copyright © 2021 FIXME
