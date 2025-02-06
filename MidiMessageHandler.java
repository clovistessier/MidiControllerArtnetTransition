import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MidiMessageHandler {

  private BlockingQueue<MidiMessage> messageQueue;
  private int maxSize;
  private static final int DEFAULT_MAX_SIZE = 100;

  /**
   * Constructor with max size.
   *
   * @param maxSize The maximum size of the message queue.
   */
  public MidiMessageHandler(int maxSize) {
    this.maxSize = maxSize;
    messageQueue = new LinkedBlockingQueue<>(maxSize);
  }

  /**
   * Default constructor using a predefined default max size.
   */
  public MidiMessageHandler() {
    this(DEFAULT_MAX_SIZE); // Call the other constructor with the default value
  }

  public boolean hasWaitingMessages() {
    return !messageQueue.isEmpty();
  }

  public void clearMessages() {
    messageQueue.clear();
  }

  /**
   * Represents a note on event.
   *
   * @param channel  MIDI channel (0-15)
   * @param pitch    MIDI pitch (0-127)
   * @param velocity MIDI velocity (0-127)
   */
  public void noteOn(int channel, int pitch, int velocity) {
    byte statusByte = (byte) (0x90 | (channel & 0x0F));
    MidiMessage message = new MidiMessage(statusByte, (byte) pitch, (byte) velocity);
    if (!messageQueue.offer(message)) {
      System.err.println("Warning: Note On message queue is full. Message discarded.");
    }
  }

  /**
   * Represents a note off event.
   *
   * @param channel  MIDI channel (0-15)
   * @param pitch    MIDI pitch (0-127)
   * @param velocity MIDI velocity (0-127)
   */
  public void noteOff(int channel, int pitch, int velocity) {
    byte statusByte = (byte) (0x80 | (channel & 0x0F));
    MidiMessage message = new MidiMessage(statusByte, (byte) pitch, (byte) velocity);
    if (!messageQueue.offer(message)) {
      System.err.println("Warning: Note Off message queue is full. Message discarded.");
    }
  }

  /**
   * Represents a controller change event.
   *
   * @param channel MIDI channel (0-15)
   * @param number  MIDI controller number (0-127)
   * @param value   MIDI controller value (0-127)
   */
  public void controllerChange(int channel, int number, int value) {
    byte statusByte = (byte) (0xB0 | (channel & 0x0F));
    MidiMessage message = new MidiMessage(statusByte, (byte) number, (byte) value);
    if (!messageQueue.offer(message)) {
      System.err.println("Warning: Controller Change message queue is full. Message discarded.");
    } else {
      // System.out.println("channel: "+channel+" number: "+number+" value: "+value);
    }
  }

  /**
   * Retrieves and removes the first MidiMessage from the queue.
   *
   * @return The first MidiMessage object in the queue, or null if the queue is
   *         empty.
   */
  public MidiMessage getAndRemoveFirstMessage() {
    return messageQueue.poll();
  }

  /**
   * Gets the maximum size of the message queue.
   *
   * @return The maximum size.
   */
  public int getMaxsize() {
    return maxSize;
  }

  /**
   * Sets the maximum size of the message queue. This will create a new queue
   * with the new capacity and copy over the elements from the old queue.
   *
   * @param maxSize The new maximum size.
   */
  public void setMaxSize(int maxSize) {
    if (maxSize <= 0) {
      throw new IllegalArgumentException("Max size must be positive.");
    }

    this.maxSize = maxSize;
    BlockingQueue<MidiMessage> newQueue = new LinkedBlockingQueue<>(maxSize);
    messageQueue.drainTo(newQueue); // Efficiently transfer elements
    messageQueue = newQueue;
  }
}