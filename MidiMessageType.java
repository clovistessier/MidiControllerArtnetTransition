public enum MidiMessageType {
    NOTE_OFF(0x80),
    NOTE_ON(0x90),
    POLYPHONIC_KEY_PRESSURE(0xA0),
    CONTROL_CHANGE(0xB0),
    PROGRAM_CHANGE(0xC0),
    CHANNEL_PRESSURE(0xD0),
    PITCH_BEND(0xE0),
    SYSTEM_EXCLUSIVE(0xF0),
    TIME_CODE_QUARTER_FRAME(0xF1),
    SONG_POSITION_POINTER(0xF2),
    SONG_SELECT(0xF3),
    TUNE_REQUEST(0xF6),
    EOX(0xF7), // End of System Exclusive
    TIMING_CLOCK(0xF8),
    START(0xFA),
    CONTINUE(0xFB),
    STOP(0xFC),
    ACTIVE_SENSING(0xFE),
    SYSTEM_RESET(0xFF),
    UNKNOWN(-1); // For unknown or invalid status bytes

    private final int statusByte;

    MidiMessageType(int statusByte) {
        this.statusByte = statusByte;
    }

    public int getStatusByte() {
        return statusByte;
    }

    public static MidiMessageType fromStatusByte(int statusByte) {
        for (MidiMessageType type : MidiMessageType.values()) {
            if (type.getStatusByte() == statusByte) {
                return type;
            }
        }
        return UNKNOWN; // Return UNKNOWN if no match is found
    }
}