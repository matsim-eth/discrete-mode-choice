# Discrete Mode Choice for MATSim

[![Build Status](https://travis-ci.org/matsim-eth/av.png)](https://travis-ci.org/matsim-eth/mode_choice)

The Discrete Mode Choice extension for MATSim makes it easy to define fine-grained and custom mode choice behaviour in MATSim simulations. Have a look at the [Getting Started](docs/GettingStarted.md) guide to dive right in or have a look at the existing [Components](docs/Components.md) if you are already familiar with the basic concepts.

The extensions offers three major pathways for improving mode choice in MATSim:

- A fully functional replacement of `SubtourModeChoice`, but with the possibility to easily define custom constraints such as operating areas for certain mobility services or mode restrictions for specific user groups
- An "importance sampler" for MATSim which samples choice alternatives with utility-based probabilities rather than purely at random and has the potential to speed up convergence
- A "mode choice in the loop" setup, in which MATSim acts as a bare assignment model, which runs in a loop with a customizable discrete mode choice model

To learn more about these applications (and how you can implement "frozen randomness") into your simulation, have a look at the [Getting Started](docs/GettingStarted.md) guide.

For more customized applications and set-ups, have a look at [Customizing the framework](docs/Customizing.md).

## Literature

The Discrete Mode Choice extension has been used in the following publications:

- Hörl, S., M. Balac and K.W. Axhausen (2019) [Pairing discrete mode choice models and agent-based transport simulation with MATSim](https://www.research-collection.ethz.ch/handle/20.500.11850/303667), presented at the 98th Annual Meeting of the Transportation Research Board, January 2019, Washington D.C.
- Hörl, S., M. Balac and K.W. Axhausen (2018) [A first look at bridging discrete choice modeling and agent-based microsimulation in MATSim](https://www.sciencedirect.com/science/article/pii/S1877050918304496?via%3Dihub), *Procedia Computer Science*, **130**, 900-907.

## Working with the code

- Releases are generally available on [Bintray](https://bintray.com/matsim-eth/matsim/discrete-mode-choice). Un-postfixed version numbers denote releases that are comaptible with the latest stable MATSim release, i.e. 0.10.1. All version postfixed with `-latest` relate to a specific recent weekly SNAPSHOT of MATSim.

- The repository has there main branches: `latest` contains the latest release version, compatible with a recent MATSim SNAPSHOT, `stable` contains the backport to MATSim 0.10.1 and `development` contains the most recent development version, which does not have a release yet.

To use the Discrete Mode Choice extension with a standard MATSim 0.10.1 setup, add the following to your `pom.xml` dependencies:

```xml
<dependency>
	<groupId>ch.ethz.matsim</groupId>
	<artifactId>discrete_mode_choice</artifactId>
	<version>1.0.0</version>
</dependency>
```

For the version that is compatible with a SNAPSHOT, choose:

```xml
<dependency>
	<groupId>ch.ethz.matsim</groupId>
	<artifactId>discrete_mode_choice</artifactId>
	<version>1.0.0-latest</version>
</dependency>
```

 