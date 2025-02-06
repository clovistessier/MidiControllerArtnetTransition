public class SimpleDmxFixture {
    private int c;
    private int b;
    private int addr;
    private int size;

    public SimpleDmxFixture(int color, int brightness, int address, int dmxSize) {
        c = color;
        b = brightness;
        addr = address;
        size = dmxSize;
    }

    public SimpleDmxFixture(int address, int dmxSize) {
        this(0,255, address, dmxSize);
    }

    public void setColor(int color) {
        c = color;
    }

    public int getColor() {
        return c;
    }

    public void setBrightness(int brightness) {
        b = brightness;
    }

    public int getBrightness() {
        return b;
    }

    public void setAddress(int address) {
        addr = address;
    }

    public int getAddress() {
        return addr;
    }

    public void setSize(int dmxSize) {
        size = dmxSize;
    }

    public int getSize() {
        return size;
    }
}
