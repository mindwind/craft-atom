# craft-atom
A crafted and atomic component library for java.

## What is it?
It is a multi module projects, each module is a crafted and atomic java library for specific feature.  
The module list and brief introduction:

- craft-atom-io  
  A base io model api definition, other component implements it such as: ```craft-atom-nio```.

- craft-atom-nio  
  A very thin wrap for java nio to make it use easily, it implements ```craft-atom-io``` api model definition.

- craft-atom-redis  
  A easy use redis java client base on jedis.

- craft-atom-lock  
  A kind of distributed lock base on redis.

- craft-atom-util  
  A util component with some useful tools.

- craft-atom-test  
  A unit test supported component used by other atom component.

- craft-atom-protocol  
  A base protocol codec api definition, other component implements it such as: ```craft-atom-protocol-http```.

- craft-atom-protocol-http  
  A simple http protocol implementation.  

- craft-atom-protocol-ssl
  A simple ssl protocol implementation.  

- craft-atom-protocol-textline
  A simple textline protocol implementation.

## Goal
- One hour: less than one hour, you can use and understand it easily following the guide.
- One day : less than one day, you can dive into source code and master it.


## Documentation


