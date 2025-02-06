import processing.core.PApplet;

public class ColorTransitioner {
    private PApplet p; // Store the PApplet instance
    private int c;
    private int prevColor;
    private int targetColor;
    private float lerpAmount = 0.0f;
    private float lerpInc;
    private float transitionTime; // seconds

    public ColorTransitioner(PApplet parent, float transTime, int startingColor, int frameRate) {
        p = parent;
        transitionTime = transTime;
        // calculate lerpInc
        lerpInc = 1.0f / (transitionTime * frameRate);
        c = startingColor;
        prevColor = startingColor;
        targetColor = startingColor;
        lerpAmount = 1.0f;
    }

    public ColorTransitioner(PApplet parent, float transTime, int startingColor) {
        this(parent, transTime, startingColor, 60);
    }

    public int update() {
        if (lerpAmount < 1.0f) {
            c = p.lerpColor(prevColor, targetColor, easeOutCubic(lerpAmount));
            lerpAmount += lerpInc;
            // p.println((int) p.red(c) + " " + (int) p.green(c) + " " + (int) p.blue(c) + " " + lerpAmount + " "
            //         + easeOutCubic(lerpAmount));
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

    private float easeOutCubic(float x) {
        return 1 - p.pow(1 - x, 3);
    }
}
