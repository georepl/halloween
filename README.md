# halloween

Halloween is a package consisting of the spookparty and spook applications. The multiple instances of spook form a bunch of processes on the computer system that misbehave in every way you can imagine where spookparty is the mother of evil.
It creates the spook processes and behaves in a well-defined and predictable manner.
More technically spoken, spookparty is a generator which sets up an environment simulating the situation on a running computer. There are applications running using resources and causing side effects.
Sometimes these programs fail; they becomer slower, hang, or display every other kind of unwanted behaviour. But they can also interact in a more disruptive way: they block resources, cause unexpected effects with other processes and cause all sorts of imaginable troubles.

The halloween package is meant to be a test tool, in particular for system and integration testing. It simulates the situation on a running computer system and can be used to test programs that deal with monitoring and control of computer systems or to simulate an "outer world" for applications which are sensitive to the environment they run in.

I personally can imagine a lot of weired things such programs could do. But I cannot imagine everything which might be possible.
So spookparty is intended to be a never ending work in progress. Whenever you encounter bad behaviour of a process on any computer system which can not yet be simulated by a process generated with spookparty, please feel free to raise an issue on GitHub.

Spookparty is written in Clojure.

## Installation

Download from https://github.com/georepl/halloween.

## Usage

    $ java -jar spookparty-0.1.0-standalone.jar <spookplan>

This will setup and start the spook in your current environment, if not specified otherwise (you can set the spook on hold and start it manually at a later point in time. More on this in the description of spookplans).


### Accessing the mother of evil

Spookparty will create processes that can run independently from the process which generated them. These processes, also called "the spook", continue even if the process that generated them is terminated. It is recommended, however, not to kill the generating process because it keeps track of all the spook. So you can conveniently influence the spook even after it was started.

When you start spookparty it starts a Clojure Repl which lets you interact with the spook. First of all it gives you the state information of all the spooky processes having been started according to the spookplan.

#### show
TBD

### start, stop, delay, hold, resume
TBD

## spookplans

A spookplan is a text file where the setup is described.

### general stuff: port





## Examples of spookplans

...


## License

Copyright © 2016 Thomas Neuhalfen

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
