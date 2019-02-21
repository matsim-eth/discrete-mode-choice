# Discrete Mode Choice for MATSim

[![Build Status](https://travis-ci.org/matsim-eth/discrete-mode-choice.svg?branch=latest)](https://travis-ci.org/matsim-eth/discrete-mode-choice)

The Discrete Mode Choice extension for MATSim makes it easy to define fine-grained and custom mode choice behaviour in MATSim simulations. Have a look at the [Getting Started](docs/GettingStarted.md) guide to dive right in or have a look at the existing [Components](docs/Components.md) if you are already familiar with the basic concepts.

The extensions offers three major pathways for improving mode choice in MATSim:

- A fully functional replacement of `SubtourModeChoice`, but with the possibility to easily define custom constraints such as operating areas for certain mobility services or mode restrictions for specific user groups
- An "importance sampler" for MATSim which samples choice alternatives with utility-based probabilities rather than purely at random and has the potential to speed up convergence
- A "mode choice in the loop" setup, in which MATSim acts as a bare assignment model, which runs in a loop with a customizable discrete mode choice model

To learn more about these applications (and how you can implement "frozen randomness") into your simulation, have a look at the [Getting Started](docs/GettingStarted.md) guide.

For more customized applications and set-ups, have a look at [Customizing the framework](docs/Customizing.md).

## Working with the code

- Releases are generally available on [Bintray](https://bintray.com/matsim-eth/matsim/discrete-mode-choice). Un-postfixed version numbers denote releases that are comaptible with the latest stable MATSim release, i.e. 0.10.1. All version postfixed with `-latest` relate to a specific recent weekly SNAPSHOT of MATSim.

- The repository has two main branches: `latest` contains the latest development version. From time to time a release is deployed to Bintray, which happens with incresing the Maven version number and tagging the first commit with it. The `latest` branch is then based on that number until the next release is done. The `stable` branch contains a backport to MATSim 0.10.1.

To use the Discrete Mode Choice extension you first need to add the ETH MATSim Bintray repository to your `pom.xml`:

```xml
<repository>
    <id>matsim-eth</id>
    <url>https://dl.bintray.com/matsim-eth/matsim</url>
</repository>
```

Add the following to your `pom.xml` dependencies to use the extension with a standard MATSim 0.10.1 setup:

```xml
<dependency>
	<groupId>ch.ethz.matsim</groupId>
	<artifactId>discrete_mode_choice</artifactId>
	<version>1.0.2</version>
</dependency>
```

For the version that is compatible with a SNAPSHOT, choose:

```xml
<dependency>
	<groupId>ch.ethz.matsim</groupId>
	<artifactId>discrete_mode_choice</artifactId>
	<version>1.0.2-latest</version>
</dependency>
```

## Literature

The Discrete Mode Choice extension has been used in the following publications:

- Hörl, S., M. Balac and K.W. Axhausen (2019) [Pairing discrete mode choice models and agent-based transport simulation with MATSim](https://www.research-collection.ethz.ch/handle/20.500.11850/303667), presented at the 98th Annual Meeting of the Transportation Research Board, January 2019, Washington D.C.
- Balac, M., H. Becker, F. Ciari and K.W. Axhausen (2019) [Modeling competing free-floating carsharing operators – A case study for Zurich, Switzerland](https://www.sciencedirect.com/science/article/pii/S0968090X18316656), *Transportation Research: Part C*, **98**, 101-117.
- Balac, M., A.R. Vetrella, R. Rothfeld and B. Schmid (2018) [Demand estimation for aerial vehicles in urban settings](https://www.research-collection.ethz.ch/bitstream/handle/20.500.11850/274798/ab1355.pdf), accepted for publication in *IEEE Intelligent Transportation Systems Magazine*.
- Becker, H., M. Balac and F. Ciari (2018) [Assessing the welfare impacts of MaaS: A case study in Switzerland](https://www.research-collection.ethz.ch/handle/20.500.11850/320799), presented at the 7th Symposium of the European Association for Research in Transportation (hEART 2018), September 2018, Athens, Greece.
- Hörl, S., M. Balac and K.W. Axhausen (2018) [A first look at bridging discrete choice modeling and agent-based microsimulation in MATSim](https://www.sciencedirect.com/science/article/pii/S1877050918304496?via%3Dihub), *Procedia Computer Science*, **130**, 900-907.
