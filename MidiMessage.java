public class MidiMessage {

    private byte byte1;
    private byte byte2;
    private byte byte3;

    // Default constructor
    public MidiMessage() {
        this.byte1 = 0;
        this.byte2 = 0;
        this.byte3 = 0;
    }

    // Constructor with parameters
    public MidiMessage(byte byte1, byte byte2, byte byte3) {
        this.byte1 = byte1;
        this.byte2 = byte2;
        this.byte3 = byte3;
    }
    // Getters and setters for each byte
    public byte getByte1() {
        return byte1;
    }

    public void setByte1(byte byte1) {
        this.byte1 = byte1;
    }

    public byte getByte2() {
        return byte2;
    }

    public void setByte2(byte byte2) {
        this.byte2 = byte2;
    }

    public byte getByte3() {
        return byte3;
    }

    public void setByte3(byte byte3) {
        this.byte3 = byte3;
    }

    public MidiMessageType getMessageType() {
        int statusByte = this.byte1 & 0xF0; // Mask to get the command
        return MidiMessageType.fromStatusByte(statusByte); //Use the static method from the enum
    }

    public int getChannel() {
        return (byte1 & 0x0F) + 1; // Channel is in the lower 4 bits, 1-indexed
    }

    public int getControllerNumber() {
        if (getMessageType() == MidiMessageType.CONTROL_CHANGE) {
            return byte2 & 0xFF; // Treat as unsigned
        }
        return -1; // Not a Control Change message
    }

    public int getControllerValue() {
        if (getMessageType() == MidiMessageType.CONTROL_CHANGE) {
            return byte3 & 0xFF; // Treat as unsigned
        }
        return -1; // Not a Control Change message
    }

    public int getNote() {
        if (getMessageType() == MidiMessageType.NOTE_ON || getMessageType() == MidiMessageType.NOTE_OFF) {
            return byte2 & 0xFF; // Treat as unsigned
        }
        return -1; // Not a Note On or Note Off message
    }

    public int getVelocity() {
        if (getMessageType() == MidiMessageType.NOTE_ON || getMessageType() == MidiMessageType.NOTE_OFF) {
            return byte3 & 0xFF; // Treat as unsigned
        }
        return -1; // Not a Note On or Note Off message
    }
    @Override
    public String toString() {
        return "MidiMessage{" +
                "type=" + getMessageType() +
                ", channel=" + getChannel() +
                ", byte1=0x" + String.format("%02X", byte1) +
                ", byte2=0x" + String.format("%02X", byte2) +
                ", byte3=0x" + String.format("%02X", byte3) +
                ", controllerNumber=" + getControllerNumber() +
                ", controllerValue=" + getControllerValue() +
                ", note=" + getNote() +
                ", velocity=" + getVelocity() +
                '}';
    }
}
