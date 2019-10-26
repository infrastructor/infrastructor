# ![Infrastructor](/files/infrastructor-logo-light.png) [![Build Status](https://travis-ci.org/infrastructor/infrastructor.svg?branch=master)](https://travis-ci.org/infrastructor/infrastructor)

### Overview
Infrastructor is a server provisioning and configuration management tool written in Groovy programming language. It implements Infrastructure as Code paradigm. Here are key features of Infrasructor:
* It provides a DSL on top of Groovy programming language to describe, manage and provision servers: either bare metal and virtual machines
* It is possible to use all Groovy features: loops, conditions, closures, etc.
* Agentless: no need to pre-configure target hosts, only an SSH connection is required
* Has minimal dependencies on the host’s side, just a few posix utilities are used: cat, tee, mkdir, cp, etc.
* Portable, but requires Java Virtual Machine to run
* Flexible: Infrastructor gives a lot of freedom of code organization!
* Unix oriented: initially designed to work with unix based hosts

It is also designed to be easy to learn and provides rich programming functionality and extensibility. Thank you for your interest in Infrastructor! 

### Installation
At first make sure Java 1.8 is present on your machine. Then Infrastructor can be installed using [SDKMAN](https://sdkman.io):
```
sdk install infrastructor
```
As an alternative you can download and unpack a ZIP file from the [releases page](https://github.com/infrastructor/infrastructor/releases)
 
### Documentation
Building comprehensive documentation can be a long process and it can always be a bit out of date.
Please check the [wiki](https://github.com/infrastructor/infrastructor/wiki) for the currently available tutorials and references.
You may also find these slides interesting:

[![Infrastructor](https://svgshare.com/i/E73.svg)](https://drive.google.com/file/d/16UNLtlF9LhEtXA77cZ0zeiVK5ttMiiQP/view)

### Examples
A catalog of examples grows [here](https://github.com/infrastructor/examples) 

### Contribution

Have a great idea and want to contribute? Here's what can be a good way to help Infrastructor:
- new action implementations
- code / usage examples
- documentation improvements
- issue reports and bug fixes
- any UX improvements
- any other ideas that makes sense to talk about!

Looking forward to your pull request!
