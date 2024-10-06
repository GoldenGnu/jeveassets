# Git Checkout #

We usually work in two branches:
  * New Features:  
https://github.com/GoldenGnu/jeveassets/tree/develop
  * Bug Fixes:  
https://github.com/GoldenGnu/jeveassets/tree/main

Bug fixes are committed to both branches.  
Experimental features get their own branch.  
The develop branch is merged back into trunk after release.  

# Git Clone #

https://github.com/GoldenGnu/jeveassets.git

# Compile #

We compile with the latest version of Oracle Java SE 8 (Yes, still using Java 8).

## Netbeans ##

Open the project and compile (F11)

## Other IDEs ##

I only use NetBeans. Feel free to make a PR with instructions

# Coding Guidelines #

  * We use tab and not space. Be sure to disable "expand tabs to space" in your IDE.
  * Swing JComponent variable names should have leading "j". Ex.: `jMyTable`
  * No other variable names should use [Systems Hungarian](https://en.wikipedia.org/wiki/Hungarian_notation)

# Contribute #

We are looking for (in random order):
  * Documentation
  * Java Programmers
  * Dedicated Testers
  * Translators

If you want to join the project contract `goldengnu` on the [jEveAssets Discord server](https://discord.gg/8kYZvbM) (PMs welcome).
