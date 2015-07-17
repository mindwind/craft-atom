
# Release notes of craft-atom


## Upgrade urgency levels

`LOW`     : No need to upgrade unless there are new features you want to use.  
`MODERATE`: Program an upgrade, but it's not urgent.  
`HIGH`    : There is a critical bug that may affect a subset of users. Upgrade!  
`CRITICAL`: There is a critical bug affecting MOST USERS. Upgrade ASAP.  


------------------------------------------------------------------------------

## Atom 3.1.2
UPGRADE URGENCY: MODERATE
This release just upgrade some third party dependency.

### Atom-redis
  * [IMPROVE] Upgrade jedis to version 2.7.2, close issue #2

### Atom-protocol-rpc
  * [IMPROVE] Upgrade kryo to version 3.0.2, close issue #4

------------------------------------------------------------------------------

## Atom 3.1.1
UPGRADE URGENCY: HIGH
This release just fix a bug for master slave redis switch.

### Atom-redis
  * [FIX] issue #5

------------------------------------------------------------------------------

## Atom 3.1.0
UPGRADE URGENCY: LOW
This release mostly adds some commands for redis module.

### Atom-redis
  * [IMPROVE] - upgrade jedis dependency to 2.6.2
  * [NEW]     - ZLEXCOUNT command
  * [NEW]     - ZRANGEBYLEX command
  * [NEW]     - ZREMRANGEBYLEX command
  * [NEW]     - CLIENT KILL new form added

------------------------------------------------------------------------------

## Atom 3.0.1
UPGRADE URGENCY: MODERATE
This release mostly fix a small bug for util module

### Atom-util
  * [FIX] - AdaptiveByteBuffer.shink() infinite loop on some cases.

------------------------------------------------------------------------------

## Atom 3.0.0
UPGRADE URGENCY: LOW
This release mostly a brand new version, it is not compatible for version 2.

### Atom all modules  
  * [IMPROVE] - rename package name and groupId.

### Atom-rpc && Atom-protocol-rpc  
  * [NEW] - rpc support.

### Atom-nio  
  * [FIX]     - just handle `Exception` not `Throwable`.  
  * [IMPROVE] - x api ajust, it is incompatible with previous 2.x version.  

### Atom-util  
  * [IMPROVE] - move 'NamedThreadFactory' to thread package.  
  * [NEW]     - 'MonitoringExecutorService' interface and implementor.  
