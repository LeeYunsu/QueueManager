# QueueManager
Easily communicate data with BlockQueues

## Example
QueueManagerTest demonstrates for using QueueManger

## Using QueueManager Library

- new QueueManager<>() : create Queue Object(BUFFER_MAX_SIZE is max queue size)
- setType() : set necessary configuration
- setOnDataChangedListener() : queuedata changed value(queue.poll()) callback 
- removeOnDataChangedListener() : DataChangedListener used finished must remove
