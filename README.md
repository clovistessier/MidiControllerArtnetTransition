# MidiMessageHandler

A Java class for handling MIDI input in Processing with `themidibus` library

## Overview

The `MidiMessageHandler` class provides a thread-safe way to manage incoming MIDI messages. It uses a bounded queue to store incoming messages from `themidibus` when set as the parent of the `MidiBus` object. This design allows for asynchronous processing of MIDI events, preventing blocking of the main application thread.

## Features

*   **Thread-safe message queue:** Uses `LinkedBlockingQueue` for concurrent access.
*   **Bounded queue:** Prevents excessive memory usage by limiting the number of stored messages.
*   **Callback methods:**  `noteOn()`, `noteOff()`, and `controllerChange()` methods to handle specific MIDI message types from `themidibus`
*   **Clear message queue functionality:** Provides `clearMessages()` to empty the message queue.
*   **Getter and Setter for Max Queue Size:** Allows dynamic adjustment of the queue's capacity.
*   **Error Handling:** Discards messages and prints a warning to `System.err` if the queue is full.

## Usage

1.  **Include the classes:** Place `MidiMessage.java`, `MidiMessageType.java`, and `MidiMessageHandler.java` in your Processing sketch's `code` folder

2.  **Create an instance:** Create an instance of `MidiMessageHandler` in your Processing sketch and use it as the parent for the `themidibus`

    ```java

    MidiMessageHandler midiHandler;
    MidiBus myBus; // The MidiBus

    void setup() {
        midiHandler = new MidiMessageHandler(100); // Create handler with a queue size of 100
        myBus = news MidiBus(midiHandler, "IncomingDeviceName", "OutgoingDeviceName");
    }
    ```

5.  **Retrieve messages from the queue:** Use the `getAndRemoveFirstMessage()` method to process the queued messages.

    ```java
    void draw() {
        while (midiHandler.hasWaitingMessages()) {
            MidiMessage message = midiHandler.getAndRemoveFirstMessage();
            // Process the message
            println(message);

            // Check message type
            if (message.getMessageType() == MidiMessageType.CONTROL_CHANGE)
            {
                println(message.getChannel() + " " + message.getControllerNumber() + " " + message.getControllerValue());
            }
            if (message.getMessageType() == MidiMessageType.NOTE_ON || message.getMessageType() == MidiMessageType.NOTE_OFF)
            {
                println(message.getChannel() + " " + message.getNote() + " " + message.getVelocity());
            }
        }
    }
    ```

## Class Details

### `MidiMessageHandler(int maxSize)`

Constructor. Initializes the message queue with the specified maximum size.

### `boolean hasWaitingMessages()`

Returns `true` if there are messages waiting in the queue, `false` otherwise.

### `void clearMessages()`

Clears all messages from the queue.

### `void noteOn(int channel, int pitch, int velocity)`

Callback method for Note On MIDI messages. Creates a `MidiMessage` object and adds it to the queue.

### `void noteOff(int channel, int pitch, int velocity)`

Callback method for Note Off MIDI messages. Creates a `MidiMessage` object and adds it to the queue.

### `void controllerChange(int channel, int number, int value)`

Callback method for Controller Change MIDI messages. Creates a `MidiMessage` object and adds it to the queue.

### `MidiMessage getAndRemoveFirstMessage()`

Retrieves and removes the first message from the queue. Returns `null` if the queue is empty.

### `int getMaxsize()`

Returns the maximum size of the message queue.

### `void setMaxSize(int maxSize)`

Sets the maximum size of the message queue. This creates a new queue and copies the existing messages. Throws `IllegalArgumentException` if `maxSize` is not positive.

## Dependencies
*   Processing 4.* (could work on earlier versions, but haven't tested it)
*   `themidibus` MIDI library for Processing
