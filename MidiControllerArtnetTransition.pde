import ch.bildspur.artnet.*;
import themidibus.*;
import netP5.*;
import oscP5.*;

MidiBus myBus; // The MidiBus
MidiMessageHandler handler;

ArtNetClient artnet;
byte[] dmxData = new byte[512];

String remote;
String espIp;

OscP5 oscP5;

// MIDI CC mappings
final int BRIGHTNESS_CC = 0;
final int R_CC = 1;
final int G_CC = 2;
final int B_CC = 3;
final int W_CC = 4;
final int MODE_CC = 5;
final int H_INC_CC = 6;

float r = 0;
float g = 0;
float b = 0;
float w = 0;

float h = 0.0f;
float hInc = 1.0f;
int brightness = 255;
int mode = 0;

final int NUM_COLORS = 8;
color[] colors = new color[NUM_COLORS];

float transitionTime = 1.0f; // seconds
final int FRAME_RATE = 60;

final int NUM_TRANSITIONERS = 4;
int[] colorIdxs = new int[NUM_TRANSITIONERS];
ColorTransitioner[] cts = new ColorTransitioner[NUM_TRANSITIONERS];

final int NUM_LIGHTS = 4;
SimpleDmxFixture[] lights = new SimpleDmxFixture[NUM_LIGHTS];

void setup() {
  size(800, 400);
  frameRate(FRAME_RATE);

  colorMode(HSB, 255, 255, 255);
  for (int i = 0; i < NUM_COLORS; i++) {
    colors[i] = color(((i*255)/NUM_COLORS), 255, 255);
  }
  colorMode(RGB, 255, 255, 255);

  for (int i = 0; i < NUM_TRANSITIONERS; i++) {
    colorIdxs[i] = i;
    cts[i] = new ColorTransitioner(this, transitionTime, (int) colors[i], FRAME_RATE);
  }

  int size = 8;
  for (int i = 0; i < NUM_LIGHTS; i++) {
    lights[i] = new SimpleDmxFixture(i*size, size);
  }

  MidiBus.list();  // List all available Midi devices on STDOUT. This will show each device's index and name.

  handler = new MidiMessageHandler(); // use default constructor, size 100 messages.

  // String incomingDeviceName = "SLIDER/KNOB";
  String incomingDeviceName = "USB MIDI Interface";


  // check that the desired device name is available
  boolean foundIncoming = false;
  for (String element : MidiBus.availableInputs()) {
    if (incomingDeviceName.equals(element)) {
      foundIncoming = true;
      break;
    }
  }

  if (!foundIncoming) {
    println("Desired input device " + incomingDeviceName + " is not available");
    noLoop();
    while (true) {
    }
  }

  // Open connection to Midi hardware, using handler as the parent to recieve incoming messages
  myBus = new MidiBus(handler, incomingDeviceName, -1);
  println("opened NK2");

  // create artnet client without buffer (no receving needed)
  artnet = new ArtNetClient(null);
  artnet.start();
  remote = new String("192.168.8.128");
  espIp = new String("192.168.8.201");

  oscP5 = new OscP5(this, 8000);
}

void draw() {
  // check for waiting MIDI messages
  while (handler.hasWaitingMessages()) {
    MidiMessage msg = handler.getAndRemoveFirstMessage();     // Got a message
    processMidi(msg);
  }

  for (int i = 0; i < NUM_TRANSITIONERS; i++) {
    cts[i].update();
  }

  for (int i = 0; i < NUM_LIGHTS; i++) {
    lights[i].setColor((int)cts[i].getColor());
  }

  // fill dmx array
  for (int i = 0; i < NUM_LIGHTS; i++) {
    int addr = lights[i].getAddress();
    color c = (color) lights[i].getColor();
    dmxData[addr] = (byte) lights[i].getBrightness(); // overall brightness
    dmxData[addr + 1] = (byte) red(c);
    dmxData[addr + 2] = (byte) green(c);
    dmxData[addr + 3] = (byte) blue(c);
  }

  // for (int i = 0; i < 16; i++) {
  //   print((dmxData[i]&0xFF)+" ");
  // }
  // println();

  // send dmx to localhost
  // subnet 0, universe 0
  artnet.unicastDmx(remote, 0, 0, dmxData);
  // artnet.unicastDmx(espIp, 0, 0, dmxData);

  // drawing
  // noStroke();
  stroke(0);
  strokeWeight(2);
  for (int i = 0; i < NUM_TRANSITIONERS; i++) {
    fill((color) cts[i].getColor());
    rect(i*(width/NUM_TRANSITIONERS), 0, (i+1)*(width/NUM_TRANSITIONERS), height);
  }
}

void processMidi(MidiMessage msg) {
  if (msg.getChannel() == 1) { // Check if this is the MIDI channel we're listening on
    if (msg.getMessageType() == MidiMessageType.CONTROL_CHANGE) { // Handle Control Change messages
      switch(msg.getControllerNumber()) {
      case BRIGHTNESS_CC:
        brightness = (int) map(msg.getControllerValue(), 0, 127, 0, 255);
        lights[0].setBrightness(brightness);
        lights[1].setBrightness(brightness);
        break;
      case R_CC:
        r = map(msg.getControllerValue(), 0, 127, 0.0f, 255.0f);
        break;
      case G_CC:
        g = map(msg.getControllerValue(), 0, 127, 0.0f, 255.0f);
        break;
      case B_CC:
        b = map(msg.getControllerValue(), 0, 127, 0.0f, 255.0f);
        break;
      case W_CC:
        w = map(msg.getControllerValue(), 0, 127, 0.0f, 255.0f);
        break;
      case MODE_CC:
        mode = msg.getControllerValue();
        break;
      case H_INC_CC:
        hInc = map(msg.getControllerValue(), 0, 127, 0.0f, 5.0f);
        break;
      default :
        println("got an unmapped midi cc: " + msg.getControllerNumber() + " value: " +msg.getControllerValue());
      }
    } else if (msg.getMessageType() == MidiMessageType.NOTE_ON) {
      int note = msg.getNote();
      if (note >= 60 && note <= 60 + NUM_TRANSITIONERS - 1) {
        triggerColorTransition(note-60);
      }
    }
  }
}

void mousePressed() {
  int ctIdx = (int) mouseX / (width / NUM_TRANSITIONERS);
  triggerColorTransition(ctIdx);
}

void triggerColorTransition(int ctIdx) {
  colorIdxs[ctIdx] = (colorIdxs[ctIdx] + 1) % NUM_COLORS;
  color targetColor = colors[colorIdxs[ctIdx]];
  cts[ctIdx].triggerColorTransition((int)targetColor);
}

/* incoming osc message are forwarded to the oscEvent method. */
void oscEvent(OscMessage theOscMessage) {
  if (theOscMessage.checkAddrPattern("/bloombot") == true) {
    if (theOscMessage.checkTypetag("i") == true) {
      int value = theOscMessage.get(0).intValue();
      if (value >= 0 && value < NUM_TRANSITIONERS) {
        triggerColorTransition(value);
      }
      return;
    }
  }
    /* print the address pattern and the typetag of the received OscMessage */
  print("### received an osc message.");
  print(" addrpattern: "+theOscMessage.addrPattern());
  println(" typetag: "+theOscMessage.typetag());
}

// directly map sliders and knobs to the first 16 DMX channels

// void processMidi(MidiMessage msg) {
//   if (msg.getChannel() == 1) { // Check if this is the MIDI channel we're listening on
//     if (msg.getMessageType() == MidiMessageType.CONTROL_CHANGE) { // Handle Control Change messages
//       int cc = msg.getControllerNumber();
//       if (cc >= 0 && cc <= 7) { // sliders to DMX 0-7
//         dmxData[cc] = (byte) (msg.getControllerValue()*2);
//         println("DMX "+cc+ ": "+msg.getControllerValue()*2);
//       } else if (cc >= 16 && cc <= 23) { // knobs to DMX 8-15
//         dmxData[cc-8] = (byte) (msg.getControllerValue()*2);
//         println("DMX "+(cc-8)+ ": "+msg.getControllerValue()*2);
//       } else {
//         println("got an unmapped midi cc: " + msg.getControllerNumber() + " value: " +msg.getControllerValue());
//       }
//     }
//   }
// }
