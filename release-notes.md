
# Release notes of craft-atom


## Upgrade urgency levels

`LOW`     : No need to upgrade unless there are new features you want to use.  
`MODERATE`: Program an upgrade, but it's not urgent.  
`HIGH`    : There is a critical bug that may affect a subset of users. Upgrade!  
`CRITICAL`: There is a critical bug affecting MOST USERS. Upgrade ASAP.  

------------------------------------------------------------------------------


## craft-atom 3.1.0
  * craft-atom-redis
    [IMPROVE]  - upgrade jedis dependency to 2.6.2
    [NEW]      - ZLEXCOUNT command
    [NEW]      - ZRANGEBYLEX command
    [NEW]      - ZREMRANGEBYLEX command


## craft-atom 3.0.1

  * craft-atom-util
    [FIX]      - AdaptiveByteBuffer.shink() infinite loop on some cases.




## craft-atom 3.0.0

  * all  
    [IMPROVE]  - rename package name and groupId.

  * craft-atom-rpc && craft-atom-protocol-rpc  
    [NEW]      - rpc support.

  * craft-atom-nio  
    [FIX]      - just handle `Exception` not `Throwable`.  
    [IMPROVE]  - x api ajust, it is incompatible with previous 2.x version.  

  * craft-atom-util  
    [IMPROVE]  - move 'NamedThreadFactory' to thread package.  
    [NEW]      - 'MonitoringExecutorService' interface and implementor.  
