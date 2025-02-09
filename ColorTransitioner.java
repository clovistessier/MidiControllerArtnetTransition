import processing.core.PApplet;

public class ColorTransitioner {
    private PApplet p; // Store the PApplet instance
    private int c;
    private int prevColor;
    private int targetColor;
    private float lerpAmount = 0.0f;
    private float lerpInc;
    private float transitionTime; // seconds
    private int brightness; // 0-255;

    public ColorTransitioner(PApplet parent, float transTime, int startingColor, int frameRate) {
        p = parent;
        transitionTime = transTime;
        // calculate lerpInc
        lerpInc = 1.0f / (transitionTime * frameRate);
        c = startingColor;
        prevColor = startingColor;
        targetColor = startingColor;
        lerpAmount = 1.0f;
        brightness = (int) (0.75 * 255);
    }

    public ColorTransitioner(PApplet parent, float transTime, int startingColor) {
        this(parent, transTime, startingColor, 60);
    }

    public int update() {
        if (lerpAmount < 1.0f) {
            c = p.lerpColor(prevColor, targetColor, easeOutCubic(lerpAmount));
            lerpAmount += lerpInc;
            // p.println((int) p.red(c) + " " + (int) p.green(c) + " " + (int) p.blue(c) + "
            // " + lerpAmount + " "
            // + easeOutCubic(lerpAmount));
            brightness = (int) map(brightnessEnvelope(lerpAmount), 0.0, 1.0, 0.75 * 255, 255.0);
        }
        return c;
    }

    public void setTargetColor(int target) {
        targetColor = target;
    }

    public void triggerColorTransition(int target) {
        prevColor = c;
        setTargetColor(target);
        lerpAmount = 0.0f;
    }

    public int getColor() {
        return c;
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int newBrightness) {
        brightness = newBrightness;
    }

    private float easeOutCubic(float x) {
        return 1 - p.pow(1 - x, 3);
    }

    // brightness envelope
    // returns
    private double brightnessEnvelope(float x) {
        if (x < 0.5) { // rising
            return 2 * x;
        } else { // falling
            return 2 * (1 - x);
        }
    }

    private double map(double value, double start1, double stop1, double start2, double stop2) {
        double normalized = (value - start1) / (stop1 - start1);
        return start2 + normalized * (stop2 - start2);
    }
}
