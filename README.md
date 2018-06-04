# Infrastructor [![Build Status](https://travis-ci.org/infrastructor/infrastructor.svg?branch=master)](https://travis-ci.org/infrastructor/infrastructor)
### Introduction
Infrastructor is a server provisioning and configuration management tool written in Groovy programming language. It implements Infrastructure as Code paradigm. Here are key features of Infrasructor:
* It provides a DSL on top of Groovy programming language to describe, manage and provision servers: either bare metal and virtual machines
* It is possible to use all Groovy features: loops, conditions, closures, etc.
* Portable, but requires Java Virtual Machine to run
* Flexible: Infrastructor gives a lot of freedom of code organization!
* Unix oriented: initially designed to work with unix based hosts
* Agentless: no need pre-configure target hosts, only an SSH connection is required
* Has minimal dependencies on the host’s side, just a few posix utilities are used: cat, tee, mkdir, cp, etc.

It is also designed to be easy to learn and provides rich programming functionality and extensibility. Thank you for your interest in Infrastructor! 
### Documentation
A comprehensive documentation is still in progress and will be available soon.
Please check the [wiki](https://github.com/infrastructor/infrastructor/wiki) for tutorials and references.
You may also find these slides interesting: [Bring your infrastructure under control with Infrastructor](https://www.slideshare.net/nirro/bring-you-infrastructure-under-control-at-gr8conf-europe-2018)
### Examples
A catalog of examples grows [here](https://github.com/infrastructor/examples) 
