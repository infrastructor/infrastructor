[![Build Status](https://travis-ci.org/infrastructor/infrastructor.svg?branch=master)](https://travis-ci.org/infrastructor/infrastructor)

Attention: A comprehensive documentation is still in progress and will be available soon.

# Infrastructor

## What is Infrastructor?
Infrastructor is a simple utility to manage and automate setup procedures of your servers. It is an honest automation framework: Infrastructor just connects to a specified list of hosts by SSH and runs commands you tell it to run. It is portable. Based on JVM Infrastructor can be run on many popular OS (Linux, Windows, Mac). It has a reach syntax (based on Groovy programming language) and great extensibility. Infrastructor does not require any additional packages or agents to be installed on the target hosts. The only thing it needs is an SSH connection.

## 5 minutes tutorial
Download the latest stable release of Infrastructor. Unpack the archive and add the 'bin' directory to the PATH environment variable. Then run a docker container with an sshd service pre-configured:
```
docker run -d -p 10022:22 --name infrastructor-test infrastructor/ssd:latest
```  
> There is no requirement to run the docker container above. You can use any node you have to test how Infrastructor works.

Let's install some packages to the node. To do so we need to create a file setup.groovy with the following content:
```groovy
inlineInventory {
  node host: 'localhost', port: 10022, username: devops, password: devops
}.setup {
  nodes {
    shell sudo: true, command: 'apt update'
    ['tmux', 'mc', 'htop'].each {
        shell sudo: true, command: "apt install $it -y"
    }
  }
}
```
The script above consists of two parts:
* Inventory declaration: a set of node to setup.
* Setup process declaration: a set of commands to run on the specified inventory.

> Make sure you have a Java Virtual Machine installed before running Infrastructor

To run the setup procedure open a command line and execute:
```shell
infrastructor run -f setup.groovy
```
We can achieve a bash session to check the packages has been successfully installed:
```
docker exec -it infrastructor-test bash
```
That's it! Enjoy your infrastructure automation with Infrastructor!
