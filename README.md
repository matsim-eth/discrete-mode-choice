# Discrete Mode Choice for MATSim

The Discrete Mode Choice extension for MATSim makes it easy to define fine-grained and custom mode choice behaviour in MATSim simulations. Have a look at the [Getting Started](docs/GettingStarted.md) guide to dive right in or have a look at the existing [Components](docs/Components.md) if you are already familiar with the basic concepts.

The extensions offers three major pathways for improving mode choice in MATSim:

- A fully functional replacement of `SubtourModeChoice`, but with the possibility to easily define custom constraints such as operating areas for certain mobility services or mode restrictions for specific user groups
- An "importance sampler" for MATSim which samples choice alternatives with utility-based probabilities rather than purely at random and has the potential to speed up convergence
- A "mode choice in the loop" setup, in which MATSim acts as a bare assignment model, which runs in a loop with a customizable discrete mode choice model

To learn more about these applications (and how you can implement "frozen randomness") into your simulation, have a look at the [Getting Started](docs/GettingStarted.md) guide.

For more customized applications and set-ups, have a look at [Customizing the framework](docs/Customizing.md).

## Working with the repository

[![Build Status](https://travis-ci.org/matsim-eth/av.png)](https://travis-ci.org/matsim-eth/mode_choice)

TODO: Revise this in general.

- The latest changes are currently in the `1.x.x` branch. The version there is always compatible with a specific weekly SNAPSHOT of MATSim.
- The branch `1.x.x-0.10.1` contains a back-ported version that is compatible with MATSim `0.10.1`.

The code always has a fix version number, however the latest master is not deployed anywhere. For the latest Maven release, have a look at Bintray: TODO
 