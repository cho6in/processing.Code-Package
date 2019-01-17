import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.opengl.*; 
import processing.dxf.*; 
import java.util.Calendar; 
import controlP5.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class M_3_4_03_TOOL extends PApplet {

// M_3_4_03_TOOL.pde
// GUI.pde, Mesh.pde, TileSaver.pde
//
// Generative Gestaltung, ISBN: 978-3-87439-759-9
// First Edition, Hermann Schmidt, Mainz, 2009
// Hartmut Bohnacker, Benedikt Gross, Julia Laub, Claudius Lazzeroni
// Copyright 2009 Hartmut Bohnacker, Benedikt Gross, Julia Laub, Claudius Lazzeroni
//
// http://www.generative-gestaltung.de
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * with this tool, you can play around with multiple mesh parts
 * and draw them in lots of different ways.
 *
 * MOUSE
 * right click + drag  : rotate
 *
 * KEYS
 * m                   : menu open/close
 * s                   : save png
 * p                   : high resolution export (please update to processing 1.0.8 or
 *                       later. otherwise this will not work properly)
 * d                   : 3d export (dxf format - will not export colors)
 */


// ------ imports ------






// ------ image output ------

// factor for high resolution export
int qualityFactor = 3;                   //// 2 - 10
boolean saveOneFrame = false;
TileSaver tiler;
boolean saveDXF = false;


// ------ initial parameters and declarations ------

Mesh[] myMeshes = new Mesh[0];

int form = Mesh.FIGURE8TORUS;
float meshSpecular = 150;
int meshCount = 450;

boolean drawTriangles = true;
boolean drawQuads = false;
boolean drawNoStrip = true;
boolean drawStrip = false;
boolean useNoBlend = true;
boolean useBlendWhite = false;
boolean useBlendBlack = false;

float meshAlpha = 30;
float meshScale = 100;

float minHue = 185;
float maxHue = 200;
float minSaturation = 90;
float maxSaturation = 100;
float minBrightness = 60;
float maxBrightness = 70;

int uCount = 1;
float randomUCenter = 0;
float randomUCenterRange = TWO_PI;
float randomURange = 1.8f;

int vCount = 30;
float randomVCenter = 0;
float randomVCenterRange = TWO_PI;
float randomVRange = 5;

float paramExtra = 1;
float randomScaleRange = 1.0f;
float meshDistortion = 0;


// ------ mouse interaction ------

int offsetX = 0, offsetY = 0, clickX = 0, clickY = 0;
float rotationX = -0.5f, rotationY = 0, targetRotationX = 0, targetRotationY = 0, clickRotationX, clickRotationY;


// ------ ControlP5 ------


ControlP5 controlP5;
boolean GUI = false;
int guiEventFrame = 0;
Slider[] sliders;
Range[] ranges;
Toggle[] toggles;




public void setup() {
  

  setupGUI();

  noStroke();

  tiler=new TileSaver(this);
}


public void draw() {
  hint(ENABLE_DEPTH_TEST);

  // for high quality output
  if (tiler==null) return;
  tiler.pre();

  // dxf output
  if (saveDXF) beginRaw(DXF, timestamp()+".dxf");


  // Set general drawing mode
  if (useBlendBlack) background(0);
  else background(255);

  if (useBlendWhite || useBlendBlack) {
  }


  // Set lights
  lightSpecular(255, 255, 255);
  directionalLight(255, 255, 255, 1, 1, -1);
  specular(meshSpecular, meshSpecular, meshSpecular);
  shininess(5.0f);

  // Set view
  pushMatrix();

  translate(width*0.5f, height*0.5f);

  if (mousePressed && mouseButton==RIGHT && frameCount>guiEventFrame+1) {
    offsetX = mouseX-clickX;
    offsetY = mouseY-clickY;

    targetRotationX = clickRotationX + offsetX/PApplet.parseFloat(width) * TWO_PI;
    targetRotationY = min(max(clickRotationY + offsetY/PApplet.parseFloat(height) * TWO_PI, -HALF_PI), HALF_PI);

    rotationX += (targetRotationX-rotationX)*0.25f;
    rotationY += (targetRotationY-rotationY)*0.25f;
  }
  rotateX(-rotationY);
  rotateY(rotationX);

  scale(meshScale);


  // Generate meshes
  randomSeed(0);

  if (meshCount != myMeshes.length) {
    myMeshes = new Mesh[meshCount];

    for (int i = 0; i < meshCount; i++) {
      myMeshes[i] = new Mesh();
    }
  }


  // Set parameters and draw meshes
  surface.setTitle("Current form: " + myMeshes[0].getFormName());

  colorMode(HSB, 360, 100, 100, 100);

  for (int i = 0; i < meshCount; i++) {
    pushMatrix();

    scale(random(1/randomScaleRange, randomScaleRange));

    float uCenter = random(randomUCenter-randomUCenterRange/2, randomUCenter+randomUCenterRange/2);
    float uRange = random(randomURange);
    float vCenter = random(randomVCenter-randomVCenterRange/2, randomVCenter+randomVCenterRange/2);
    float vRange = random(randomVRange);

    myMeshes[i].setForm(form);

    if (drawTriangles && drawNoStrip) myMeshes[i].setDrawMode(TRIANGLES);
    else if (drawTriangles && drawStrip) myMeshes[i].setDrawMode(TRIANGLE_STRIP);
    else if (drawQuads && drawNoStrip) myMeshes[i].setDrawMode(QUADS);
    else if (drawQuads && drawStrip) myMeshes[i].setDrawMode(QUAD_STRIP);

    myMeshes[i].setUCount(uCount);
    myMeshes[i].setVCount(vCount);

    myMeshes[i].setUMin(uCenter-uRange/2);
    myMeshes[i].setUMax(uCenter+uRange/2);
    myMeshes[i].setVMin(vCenter-vRange/2);
    myMeshes[i].setVMax(vCenter+vRange/2);

    myMeshes[i].setParam(0, paramExtra);

    float rh = random(minHue, maxHue);
    float rs = random(minSaturation, maxSaturation);
    float rb = random(minBrightness, maxBrightness);
    myMeshes[i].setColorRange(rh, rh, rs, rs, rb, rb, meshAlpha);

    myMeshes[i].setMeshDistortion(meshDistortion);

    myMeshes[i].update();
    myMeshes[i].draw();

    popMatrix();
  }
  colorMode(RGB, 255, 255, 255, 100);

  popMatrix();

  if (useBlendWhite || useBlendBlack) {
  }

  // Image output
  if (saveOneFrame) {
    saveFrame(timestamp()+".png");
  }

  if (saveDXF) {
    endRaw();
    saveDXF = false;
  }


  // Draw GUI
  if (tiler.checkStatus() == false) {
    if (useBlendBlack || useBlendWhite) {
    }

    hint(DISABLE_DEPTH_TEST);
    noLights();
    drawGUI();

    if (useBlendBlack || useBlendWhite) {
    }
  }


  // Image output
  if (saveOneFrame) {
    if (controlP5.getGroup("menu").isOpen()) {
      saveFrame(timestamp()+"_menu.png");
    }
    saveOneFrame = false;
  }

  // Draw next tile for high quality output
  tiler.post();
}



public void keyPressed() {
  if (key=='m' || key=='M') {
    GUI = controlP5.getGroup("menu").isOpen();
    GUI = !GUI;
  }

  if (GUI) controlP5.getGroup("menu").open();
  else controlP5.getGroup("menu").close();

  if (key=='s' || key=='S') {
    saveOneFrame = true;
  }
  if (key=='p' || key=='P') {
    if (controlP5.getGroup("menu").isOpen()) {
      saveFrame(timestamp()+"_menu.png");
    }
    if (controlP5.getGroup("menu").isOpen()) controlP5.getGroup("menu").close();
    tiler.init(timestamp()+".png", qualityFactor);
  }
  if (key=='d' || key=='D') {
    saveDXF = true;
  }
}


public void mousePressed() {
  clickX = mouseX;
  clickY = mouseY;
  clickRotationX = rotationX;
  clickRotationY = rotationY;
}

public void mouseEntered(MouseEvent e) {
  loop();
}

public void mouseExited(MouseEvent e) {
  noLoop();
}



public String timestamp() {
  return String.format("%1$ty%1$tm%1$td_%1$tH%1$tM%1$tS", Calendar.getInstance());
}
// M_3_4_03_TOOL.pde
// GUI.pde, Mesh.pde, TileSaver.pde
// 
// Generative Gestaltung, ISBN: 978-3-87439-759-9
// First Edition, Hermann Schmidt, Mainz, 2009
// Hartmut Bohnacker, Benedikt Gross, Julia Laub, Claudius Lazzeroni
// Copyright 2009 Hartmut Bohnacker, Benedikt Gross, Julia Laub, Claudius Lazzeroni
//
// http://www.generative-gestaltung.de
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

public void setupGUI(){
  int activeColor = color(0,130,164);
  controlP5 = new ControlP5(this);
  //controlP5.setAutoDraw(false);
  controlP5.setColorActive(activeColor);
  controlP5.setColorBackground(color(170));
  controlP5.setColorForeground(color(50));
  controlP5.setColorCaptionLabel(color(50));
  controlP5.setColorValueLabel(color(255));

  ControlGroup ctrl = controlP5.addGroup("menu",15,25,35);
  ctrl.setColorLabel(color(255));
  ctrl.close();

  sliders = new Slider[30];
  ranges = new Range[30];
  toggles = new Toggle[30];

  int left = 0;
  int top = 5;
  int len = 300;

  int si = 0;
  int ri = 0;
  int ti = 0;
  int posY = 0;


  sliders[si++] = controlP5.addSlider("form",0,20,left,top+posY,len,15);
  sliders[si++] = controlP5.addSlider("meshScale",1,300,left,top+posY+20,len,15);
  sliders[si++] = controlP5.addSlider("meshCount",1,600,left,top+posY+40,len,15);
  posY += 70;

  toggles[ti] = controlP5.addToggle("drawTriangles",drawTriangles,left+110,top+posY,15,15);
  toggles[ti++].setLabel("Draw Triangles");
  toggles[ti] = controlP5.addToggle("drawQuads",drawQuads,left+110,top+posY+20,15,15);
  toggles[ti++].setLabel("Draw Quads");
  toggles[ti] = controlP5.addToggle("drawNoStrip",drawNoStrip,left+215,top+posY,15,15);
  toggles[ti++].setLabel("Draw No Strip");
  toggles[ti] = controlP5.addToggle("drawStrip",drawStrip,left+215,top+posY+20,15,15);
  toggles[ti++].setLabel("Draw Strip");
  toggles[ti] = controlP5.addToggle("useNoBlend",useNoBlend,left+0,top+posY,15,15);
  toggles[ti++].setLabel("No Blend");
  toggles[ti] = controlP5.addToggle("useBlendWhite",useBlendWhite,left+0,top+posY+20,15,15);
  toggles[ti++].setLabel("Blend on White"); 
  toggles[ti] = controlP5.addToggle("useBlendBlack",useBlendBlack,left+0,top+posY+40,15,15);
  toggles[ti++].setLabel("Blend on Black"); 
  posY += 70;

  sliders[si++] = controlP5.addSlider("meshAlpha",0,100,left,top+posY+0,len,15);
  sliders[si++] = controlP5.addSlider("meshSpecular",0,255,left,top+posY+20,len,15);
  posY += 50;

  ranges[ri++] = controlP5.addRange("hueRange",0,360,minHue,maxHue,left,top+posY+0,len,15);
  ranges[ri++] = controlP5.addRange("saturationRange",0,100,minSaturation,maxSaturation,left,top+posY+20,len,15);
  ranges[ri++] = controlP5.addRange("brightnessRange",0,100,minBrightness,maxBrightness,left,top+posY+40,len,15);
  posY += 70;

  sliders[si++] = controlP5.addSlider("uCount",1,40,left,top+posY,len,15);
  ranges[ri++] = controlP5.addRange("randomURange",-PI,PI,randomUCenter-randomURange/2,randomUCenter+randomURange/2,left,top+posY+20,len,15);
  sliders[si++] = controlP5.addSlider("randomUCenterRange",0,20,left,top+posY+40,len,15);
  posY += 70;

  sliders[si++] = controlP5.addSlider("vCount",1,40,left,top+posY,len,15);
  ranges[ri++] = controlP5.addRange("randomVRange",-PI,PI,randomVCenter-randomVRange/2,randomVCenter+randomVRange/2,left,top+posY+20,len,15);
  sliders[si++] = controlP5.addSlider("randomVCenterRange",0,20,left,top+posY+40,len,15);
  posY += 70;

  sliders[si++] = controlP5.addSlider("paramExtra",-10,10,left,top+posY,len,15);
  sliders[si++] = controlP5.addSlider("randomScaleRange",1,4,left,top+posY+20,len,15);
  sliders[si++] = controlP5.addSlider("meshDistortion",0,2,left,top+posY+40,len,15);
  posY += 70;


  for (int i = 0; i < si; i++) {
    sliders[i].setGroup(ctrl);
    sliders[i].getCaptionLabel().toUpperCase(true);
    sliders[i].getCaptionLabel().getStyle().padding(4,3,3,3);
    sliders[i].getCaptionLabel().getStyle().marginTop = -4;
    sliders[i].getCaptionLabel().getStyle().marginLeft = 0;
    sliders[i].getCaptionLabel().getStyle().marginRight = -14;
    sliders[i].getCaptionLabel().setColorBackground(0x99ffffff);
  }
  for (int i = 0; i < ri; i++) {
    ranges[i].setGroup(ctrl);
    ranges[i].getCaptionLabel().toUpperCase(true);
    ranges[i].getCaptionLabel().getStyle().padding(4,3,3,3);
    ranges[i].getCaptionLabel().getStyle().marginTop = -4;
    ranges[i].getCaptionLabel().setColorBackground(0x99ffffff);
  }
  for (int i = 0; i < ti; i++) {
    toggles[i].setGroup(ctrl);
    toggles[i].setColorCaptionLabel(color(50));
    toggles[i].getCaptionLabel().getStyle().padding(4,3,3,3);
    toggles[i].getCaptionLabel().getStyle().marginTop = -20;
    toggles[i].getCaptionLabel().getStyle().marginLeft = 18;
    toggles[i].getCaptionLabel().getStyle().marginRight = 5;
    toggles[i].getCaptionLabel().setColorBackground(0x99ffffff);
  }
}

public void drawGUI(){
  controlP5.show();
  //controlP5.hide();  
  controlP5.draw();
}


public void updateColors(int stat) {
  ControllerGroup ctrl = controlP5.getGroup("menu");

  for (int i = 0; i < sliders.length; i++) {
    if (sliders[i] == null) break;
    if (stat == 0) {
      sliders[i].setColorCaptionLabel(color(50));
      sliders[i].getCaptionLabel().setColorBackground(0x99ffffff);
    } 
    else {
      sliders[i].setColorCaptionLabel(color(200));
      sliders[i].getCaptionLabel().setColorBackground(0x99000000);
    }
  }
  for (int i = 0; i < ranges.length; i++) {
    if (ranges[i] == null) break;
    if (stat == 0) {
      ranges[i].setColorCaptionLabel(color(50));
      ranges[i].getCaptionLabel().setColorBackground(0x99ffffff);
    } 
    else {
      ranges[i].setColorCaptionLabel(color(200));
      ranges[i].getCaptionLabel().setColorBackground(0x99000000);
    }
  }
  for (int i = 0; i < toggles.length; i++) {
    if (toggles[i] == null) break;
    if (stat == 0) {
      toggles[i].setColorCaptionLabel(color(50));
      toggles[i].getCaptionLabel().setColorBackground(0x99ffffff);
    } 
    else {
      toggles[i].setColorCaptionLabel(color(200));
      toggles[i].getCaptionLabel().setColorBackground(0x99000000);
    }
  }
}



public void controlEvent(ControlEvent theControlEvent) {
  guiEventFrame = frameCount;

  GUI = controlP5.getGroup("menu").isOpen();

  if(theControlEvent.isController()) {
    if(theControlEvent.getController().getName().equals("randomURange")) {
      float[] f = theControlEvent.getController().getArrayValue();
      randomUCenter = (f[0] + f[1]) / 2;
      randomURange = f[1] - f[0];
    }
    if(theControlEvent.getController().getName().equals("randomVRange")) {
      float[] f = theControlEvent.getController().getArrayValue();
      randomVCenter = (f[0] + f[1]) / 2;
      randomVRange = f[1] - f[0];
    }
    if(theControlEvent.getController().getName().equals("hueRange")) {
      float[] f = theControlEvent.getController().getArrayValue();
      minHue = f[0];
      maxHue = f[1];
    }
    if(theControlEvent.getController().getName().equals("saturationRange")) {
      float[] f = theControlEvent.getController().getArrayValue();
      minSaturation = f[0];
      maxSaturation = f[1];
    }
    if(theControlEvent.getController().getName().equals("brightnessRange")) {
      float[] f = theControlEvent.getController().getArrayValue();
      minBrightness = f[0];
      maxBrightness = f[1];
    }
  }
}




public void drawTriangles() {
  if (frameCount > guiEventFrame+1) {
    guiEventFrame = frameCount;
    drawTriangles = !drawTriangles;
    drawQuads = !drawTriangles;
    toggles[1].setState(drawQuads);
  }
}

public void drawQuads() {
  if (frameCount > guiEventFrame+1) {
    guiEventFrame = frameCount;
    drawQuads = !drawQuads;
    drawTriangles = !drawQuads;
    toggles[0].setState(drawTriangles);
  }
}


public void drawNoStrip() {
  if (frameCount > guiEventFrame+1) {
    guiEventFrame = frameCount;
    drawNoStrip = !drawNoStrip;
    drawStrip = !drawNoStrip;
    toggles[3].setState(drawStrip);
  }
}

public void drawStrip() {
  if (frameCount > guiEventFrame+1) {
    guiEventFrame = frameCount;
    drawStrip = !drawStrip;
    drawNoStrip = !drawStrip;
    toggles[2].setState(drawNoStrip);
  }
}


public void useNoBlend() {
  if (frameCount > guiEventFrame+1) {
    guiEventFrame = frameCount;
    useNoBlend = !useNoBlend;
    if (useNoBlend) {
      useBlendWhite = false;
      useBlendBlack = false;
      toggles[5].setState(useBlendWhite);
      toggles[6].setState(useBlendBlack);
    } 
    else {
      useBlendWhite = true;
      useBlendBlack = false;
      toggles[5].setState(useBlendWhite);
      toggles[6].setState(useBlendBlack);
    }
    updateColors(0);
  }
}

public void useBlendWhite() {
  if (frameCount > guiEventFrame+1) {
    guiEventFrame = frameCount;
    useBlendWhite = !useBlendWhite;
    if (useBlendWhite) {
      useNoBlend = false;
      useBlendBlack = false;
      toggles[4].setState(useNoBlend);
      toggles[6].setState(useBlendBlack);
    } 
    else {
      useNoBlend = true;
      useBlendBlack = false;
      toggles[4].setState(useNoBlend);
      toggles[6].setState(useBlendBlack);
    }
    updateColors(0);
  }
}

public void useBlendBlack() {
  if (frameCount > guiEventFrame+1) {
    guiEventFrame = frameCount;
    useBlendBlack = !useBlendBlack;
    if (useBlendBlack) {
      useNoBlend = false;
      useBlendWhite = false;
      toggles[4].setState(useNoBlend);
      toggles[5].setState(useBlendWhite);
      updateColors(1);
    } 
    else {
      useNoBlend = true;
      useBlendWhite = false;
      toggles[4].setState(useNoBlend);
      toggles[5].setState(useBlendWhite);
      updateColors(0);
    }
  }
}


// M_3_4_03_TOOL.pde
// GUI.pde, Mesh.pde, TileSaver.pde
// 
// Generative Gestaltung, ISBN: 978-3-87439-759-9
// First Edition, Hermann Schmidt, Mainz, 2009
// Hartmut Bohnacker, Benedikt Gross, Julia Laub, Claudius Lazzeroni
// Copyright 2009 Hartmut Bohnacker, Benedikt Gross, Julia Laub, Claudius Lazzeroni
//
// http://www.generative-gestaltung.de
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.


class Mesh {

  // ------ constants ------

  final static int PLANE              = CUSTOM;
  final static int TUBE               = 1;
  final static int SPHERE             = 2;
  final static int TORUS              = 3;
  final static int PARABOLOID         = 4;
  final static int STEINBACHSCREW     = 5;
  final static int SINE               = 6;
  final static int FIGURE8TORUS       = 7;
  final static int ELLIPTICTORUS      = 8;
  final static int CORKSCREW          = 9;
  final static int BOHEMIANDOME       = 10;
  final static int BOW                = 11;
  final static int MAEDERSOWL         = 12;
  final static int ASTROIDALELLIPSOID = 13;
  final static int TRIAXIALTRITORUS   = 14;
  final static int LIMPETTORUS        = 15;
  final static int HORN               = 16;
  final static int SHELL              = 17;
  final static int KIDNEY             = 18;
  final static int LEMNISCAPE         = 19;
  final static int TRIANGULOID         = 20;
  final static int SUPERFORMULA       = 21;


  // ------ mesh parameters ------

  int form = PARABOLOID;

  float uMin = -PI;
  float uMax = PI;
  int uCount = 50;

  float vMin = -PI;
  float vMax = PI;
  int vCount = 50;

  float[] params = {
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1        };

  int drawMode = TRIANGLE_STRIP;
  float minHue = 0;
  float maxHue = 0;
  float minSaturation = 0;
  float maxSaturation = 0;
  float minBrightness = 50;
  float maxBrightness = 50;
  float meshAlpha = 100;

  float meshDistortion = 0;

  PVector[][] points;


  // ------ construktors ------

  Mesh() {
    form = CUSTOM;
    update();
  }

  Mesh(int theForm) {
    if (theForm >=0) {
      form = theForm;
    }
    update();
  }

  Mesh(int theForm, int theUNum, int theVNum) {
    if (theForm >=0) {
      form = theForm;
    }
    uCount = max(theUNum, 1);
    vCount = max(theVNum, 1);
    update();
  }

  Mesh(int theForm, float theUMin, float theUMax, float theVMin, float theVMax) {
    if (theForm >=0) {
      form = theForm;
    }
    uMin = theUMin;    
    uMax = theUMax;    
    vMin = theVMin;    
    vMax = theVMax;    
    update();
  }

  Mesh(int theForm, int theUNum, int theVNum, float theUMin, float theUMax, float theVMin, float theVMax) {
    if (theForm >=0) {
      form = theForm;
    }
    uCount = max(theUNum, 1);
    vCount = max(theVNum, 1);
    uMin = theUMin;    
    uMax = theUMax;    
    vMin = theVMin;    
    vMax = theVMax;    
    update();
  }



  // ------ calculate points ------

  public void update() {
    points = new PVector[vCount+1][uCount+1];

    float u, v;
    for (int iv = 0; iv <= vCount; iv++) {
      for (int iu = 0; iu <= uCount; iu++) {
        u = map(iu, 0, uCount, uMin, uMax);
        v = map(iv, 0, vCount, vMin, vMax);

        switch(form) {
        case CUSTOM: 
          points[iv][iu] = calculatePoints(u, v);
          break;
        case TUBE: 
          points[iv][iu] = Tube(u, v);
          break;
        case SPHERE: 
          points[iv][iu] = Sphere(u, v);
          break;
        case TORUS: 
          points[iv][iu] = Torus(u, v);
          break;
        case PARABOLOID: 
          points[iv][iu] = Paraboloid(u, v);
          break;
        case STEINBACHSCREW: 
          points[iv][iu] = SteinbachScrew(u, v);
          break;
        case SINE: 
          points[iv][iu] = Sine(u, v);
          break;
        case FIGURE8TORUS: 
          points[iv][iu] = Figure8Torus(u, v);
          break;
        case ELLIPTICTORUS: 
          points[iv][iu] = EllipticTorus(u, v);
          break;
        case CORKSCREW: 
          points[iv][iu] = Corkscrew(u, v);
          break;
        case BOHEMIANDOME: 
          points[iv][iu] = BohemianDome(u, v);
          break;
        case BOW: 
          points[iv][iu] = Bow(u, v);
          break;
        case MAEDERSOWL: 
          points[iv][iu] = MaedersOwl(u, v);
          break;
        case ASTROIDALELLIPSOID: 
          points[iv][iu] = AstroidalEllipsoid(u, v);
          break;
        case TRIAXIALTRITORUS: 
          points[iv][iu] = TriaxialTritorus(u, v);
          break;
        case LIMPETTORUS: 
          points[iv][iu] = LimpetTorus(u, v);
          break;
        case HORN: 
          points[iv][iu] = Horn(u, v);
          break;
        case SHELL: 
          points[iv][iu] = Shell(u, v);
          break;
        case KIDNEY: 
          points[iv][iu] = Kidney(u, v);
          break;
        case LEMNISCAPE: 
          points[iv][iu] = Lemniscape(u, v);
          break;
        case TRIANGULOID: 
          points[iv][iu] = Trianguloid(u, v);
          break;
        case SUPERFORMULA: 
          points[iv][iu] = Superformula(u, v);
          break;

        default:
          points[iv][iu] = calculatePoints(u, v);
          break;          
        }
      }
    }
  }


  // ------ getters and setters ------

  public int getForm() {
    return form;
  }
  public void setForm(int theValue) {
    form = theValue;
  }

  public String getFormName() {
    switch(form) {
    case CUSTOM: 
      return "Custom";
    case TUBE: 
      return "Tube";
    case SPHERE: 
      return "Sphere";
    case TORUS: 
      return "Torus";
    case PARABOLOID: 
      return "Paraboloid";
    case STEINBACHSCREW: 
      return "Steinbach Screw";
    case SINE: 
      return "Sine";
    case FIGURE8TORUS: 
      return "Figure 8 Torus";
    case ELLIPTICTORUS: 
      return "Elliptic Torus";
    case CORKSCREW: 
      return "Corkscrew";
    case BOHEMIANDOME: 
      return "Bohemian Dome";
    case BOW: 
      return "Bow";
    case MAEDERSOWL: 
      return "Maeders Owl";
    case ASTROIDALELLIPSOID: 
      return "Astoidal Ellipsoid";
    case TRIAXIALTRITORUS: 
      return "Triaxial Tritorus";
    case LIMPETTORUS: 
      return "Limpet Torus";
    case HORN: 
      return "Horn";
    case SHELL: 
      return "Shell";
    case KIDNEY: 
      return "Kidney";
    case LEMNISCAPE: 
      return "Lemniscape";
    case TRIANGULOID: 
      return "Trianguloid";
    case SUPERFORMULA: 
      return "Superformula";
    }
    return "";
  }

  public float getUMin() {
    return uMin;
  }
  public void setUMin(float theValue) {
    uMin = theValue;
  }

  public float getUMax() {
    return uMax;
  }
  public void setUMax(float theValue) {
    uMax = theValue;
  }

  public int getUCount() {
    return uCount;
  }
  public void setUCount(int theValue) {
    uCount = theValue;
  }

  public float getVMin() {
    return vMin;
  }
  public void setVMin(float theValue) {
    vMin = theValue;
  }

  public float getVMax() {
    return vMax;
  }
  public void setVMax(float theValue) {
    vMax = theValue;
  }

  public int getVCount() {
    return vCount;
  }
  public void setVCount(int theValue) {
    vCount = theValue;
  }

  public float[] getParams() {
    return params;
  }
  public void setParams(float[] theValues) {
    params = theValues;
  }

  public float getParam(int theIndex) {
    return params[theIndex];
  }
  public void setParam(int theIndex, float theValue) {
    params[theIndex] = theValue;
  }

  public int getDrawMode() {
    return drawMode;
  }
  public void setDrawMode(int theMode) {
    drawMode = theMode;
  }

  public float getMeshDistortion() {
    return meshDistortion;
  }
  public void setMeshDistortion(float theValue) {
    meshDistortion = theValue;
  }

  public void setColorRange(float theMinHue, float theMaxHue, float theMinSaturation, float theMaxSaturation, float theMinBrightness, float theMaxBrightness, float theMeshAlpha) {
    minHue = theMinHue;
    maxHue = theMaxHue;
    minSaturation = theMinSaturation;
    maxSaturation = theMaxSaturation;
    minBrightness = theMinBrightness;
    maxBrightness = theMaxBrightness;
    meshAlpha = theMeshAlpha;
  }

  public float getMinHue() {
    return minHue;
  }
  public void setMinHue(float minHue) {
    this.minHue = minHue;
  }

  public float getMaxHue() {
    return maxHue;
  }
  public void setMaxHue(float maxHue) {
    this.maxHue = maxHue;
  }

  public float getMinSaturation() {
    return minSaturation;
  }
  public void setMinSaturation(float minSaturation) {
    this.minSaturation = minSaturation;
  }

  public float getMaxSaturation() {
    return maxSaturation;
  }
  public void setMaxSaturation(float maxSaturation) {
    this.maxSaturation = maxSaturation;
  }

  public float getMinBrightness() {
    return minBrightness;
  }
  public void setMinBrightness(float minBrightness) {
    this.minBrightness = minBrightness;
  }

  public float getMaxBrightness() {
    return maxBrightness;
  }
  public void setMaxBrightness(float maxBrightness) {
    this.maxBrightness = maxBrightness;
  }

  public float getMeshAlpha() {
    return meshAlpha;
  }
  public void setMeshAlpha(float meshAlpha) {
    this.meshAlpha = meshAlpha;
  }


  // ------ functions for calculating the mesh points ------

  public PVector calculatePoints(float u, float v) {
    float x = u;
    float y = v;
    float z = 0;

    return new PVector(x, y, z);
  }

  public PVector defaultForm(float u, float v) {
    float x = u;
    float y = v;
    float z = 0;

    return new PVector(x, y, z);
  }

  public PVector Tube(float u, float v) {
    float x = (sin(u));
    float y = params[0] * v;
    float z = (cos(u));

    return new PVector(x, y, z);
  }

  public PVector Sphere(float u, float v) {
    v /= 2;
    v += HALF_PI;
    float x = 2 * (sin(v) * sin(u));
    float y = 2 * (params[0] * cos(v));
    float z = 2 * (sin(v) * cos(u));

    return new PVector(x, y, z);
  }

  public PVector Torus(float u, float v) {
    float x = 1 * ((params[1] + 1 + params[0] * cos(v)) * sin(u));
    float y = 1 * (params[0] * sin(v));
    float z = 1 * ((params[1] + 1 + params[0] * cos(v)) * cos(u));

    return new PVector(x, y, z);
  }

  public PVector Paraboloid(float u, float v) {
    float pd = params[0]; 
    if (pd == 0) {
      pd = 0.0001f; 
    }
    float x = power((v/pd),0.5f) * sin(u);
    float y = v;
    float z = power((v/pd),0.5f) * cos(u);

    return new PVector(x, y, z);
  }


  public PVector SteinbachScrew(float u, float v) {
    float x = u * cos(v);
    float y = u * sin(params[0] * v);
    float z = v * cos(u);

    return new PVector(x, y, z);
  }

  public PVector Sine(float u, float v) {
    float x = 2 * sin(u);
    float y = 2 * sin(params[0] * v);
    float z = 2 * sin(u+v);

    return new PVector(x, y, z);
  }


  public PVector Figure8Torus(float u, float v) {
    float x = 1.5f * cos(u) * (params[0] + sin(v) * cos(u) - sin(2*v) * sin(u) / 2);
    float y = 1.5f * sin(u) * (params[0] + sin(v) * cos(u) - sin(2*v) * sin(u) / 2) ;
    float z = 1.5f * sin(u) * sin(v) + cos(u) * sin(2*v) / 2;

    return new PVector(x, y, z);
  }

  public PVector EllipticTorus(float u, float v) {
    float x = 1.5f * (params[0] + cos(v)) * cos(u);
    float y = 1.5f * (params[0] + cos(v)) * sin(u) ;
    float z = 1.5f * sin(v) + cos(v);

    return new PVector(x, y, z);
  }

  public PVector Corkscrew(float u, float v) {
    float x = cos(u) * cos(v);
    float y = sin(u) * cos(v);
    float z = sin(v) + params[0] * u;

    return new PVector(x, y, z);
  }

  public PVector BohemianDome(float u, float v) {
    float x = 2 * cos(u);
    float y = 2 * sin(u) + params[0] * cos(v);
    float z = 2 * sin(v);

    return new PVector(x, y, z);
  }

  public PVector Bow(float u, float v) {
    u /= TWO_PI;
    v /= TWO_PI;
    float x = (2 + params[0] * sin(TWO_PI * u)) * sin(2 * TWO_PI * v);
    float y = (2 + params[0] * sin(TWO_PI * u)) * cos(2 * TWO_PI * v);
    float z = params[0] * cos(TWO_PI * u) + 3 * cos(TWO_PI * v);

    return new PVector(x, y, z);
  }

  public PVector MaedersOwl(float u, float v) {
    float x = 0.4f * (v * cos(u) - 0.5f*params[0] * power(v,2) * cos(2 * u));
    float y = 0.4f * (-v * sin(u) - 0.5f*params[0] * power(v,2) * sin(2 * u));
    float z = 0.4f * (4 * power(v,1.5f) * cos(3 * u / 2) / 3);

    return new PVector(x, y, z);
  }

  public PVector AstroidalEllipsoid(float u, float v) {
    u /= 2;
    float x = 3 * power(cos(u)*cos(v),3*params[0]);
    float y = 3 * power(sin(u)*cos(v),3*params[0]);
    float z = 3 * power(sin(v),3*params[0]);

    return new PVector(x, y, z);
  }

  public PVector TriaxialTritorus(float u, float v) {
    float x = 1.5f * sin(u) * (1 + cos(v));
    float y = 1.5f * sin(u + TWO_PI / 3 * params[0]) * (1 + cos(v + TWO_PI / 3 * params[0]));
    float z = 1.5f * sin(u + 2*TWO_PI / 3 * params[0]) * (1 + cos(v + 2*TWO_PI / 3 * params[0]));

    return new PVector(x, y, z);
  }

  public PVector LimpetTorus(float u, float v) {
    float x = 1.5f * params[0] * cos(u) / (sqrt(2) + sin(v));
    float y = 1.5f * params[0] * sin(u) / (sqrt(2) + sin(v));
    float z = 1.5f * 1 / (sqrt(2) + cos(v));

    return new PVector(x, y, z);
  }

  public PVector Horn(float u, float v) {
    u /= PI;
    //v /= PI;
    float x = (2*params[0] + u * cos(v)) * sin(TWO_PI * u);
    float y = (2*params[0] + u * cos(v)) * cos(TWO_PI * u) + 2 * u;
    float z = u * sin(v);

    return new PVector(x, y, z);
  }

  public PVector Shell(float u, float v) {
    float x = params[1] * (1 - (u / TWO_PI)) * cos(params[0]*u) * (1 + cos(v)) + params[3] * cos(params[0]*u);
    float y = params[1] * (1 - (u / TWO_PI)) * sin(params[0]*u) * (1 + cos(v)) + params[3] * sin(params[0]*u);
    float z = params[2] * (u / TWO_PI) + params[0] * (1 - (u / TWO_PI)) * sin(v);

    return new PVector(x, y, z);
  }

  public PVector Kidney(float u, float v) {
    u /= 2;
    float x = cos(u) * (params[0]*3*cos(v) - cos(3*v));
    float y = sin(u) * (params[0]*3*cos(v) - cos(3*v));
    float z = 3 * sin(v) - sin(3*v);

    return new PVector(x, y, z);
  }

  public PVector Lemniscape(float u, float v) {
    u /= 2;
    float cosvSqrtAbsSin2u = cos(v)*sqrt(abs(sin(2*params[0]*u)));
    float x = cosvSqrtAbsSin2u*cos(u);
    float y = cosvSqrtAbsSin2u*sin(u);
    float z = 3 * (power(x,2) - power(y,2) + 2 * x * y * power(tan(v),2));
    x *= 3;
    y *= 3;
    return new PVector(x, y, z);
  }

  public PVector Trianguloid(float u, float v) {
    float x = 0.75f * (sin(3*u) * 2 / (2 + cos(v)));
    float y = 0.75f * ((sin(u) + 2 * params[0] * sin(2*u)) * 2 / (2 + cos(v + TWO_PI)));
    float z = 0.75f * ((cos(u) - 2 * params[0] * cos(2*u)) * (2 + cos(v)) * ((2 + cos(v + TWO_PI/3))*0.25f));

    return new PVector(x, y, z);
  }

  public PVector Superformula(float u, float v) {
    v /= 2;

    // Superformel 1
    float a = params[0];
    float b = params[1];
    float m = (params[2]);
    float n1 = (params[3]);
    float n2 = (params[4]);
    float n3 = (params[5]);
    float r1 = pow(pow(abs(cos(m*u/4)/a), n2) + pow(abs(sin(m*u/4)/b), n3), -1/n1);

    // Superformel 2
    a = params[6];
    b = params[7];
    m = (params[8]);
    n1 = (params[9]);
    n2 = (params[10]);
    n3 = (params[11]);
    float r2 = pow(pow(abs(cos(m*v/4)/a), n2) + pow(abs(sin(m*v/4)/b), n3), -1/n1);

    float x = 2 * (r1*sin(u) * r2*cos(v));
    float y = 2 * (r2*sin(v));
    float z = 2 * (r1*cos(u) * r2*cos(v));

    return new PVector(x, y, z);
  }




  // ------ definition of some mathematical functions ------

  // the processing-function pow works a bit differently for negative bases
  public float power(float b, float e) {
    if (b >= 0 || PApplet.parseInt(e) == e) {
      return pow(b, e);
    } 
    else {
      return -pow(-b, e);
    }
  }

  public float logE(float v) {
    if (v >= 0) {
      return log(v);
    } 
    else{
      return -log(-v);
    }
  }

  public float sinh(float a) {
    return (sin(HALF_PI/2-a));
  }

  public float cosh(float a) {
    return (cos(HALF_PI/2-a));
  }

  public float tanh(float a) {
    return (tan(HALF_PI/2-a));
  }



  // ------ draw mesh ------

  public void draw() {
    int iuMax, ivMax;

    if (drawMode == QUADS || drawMode == TRIANGLES) {
      iuMax = uCount-1;
      ivMax = vCount-1;
    }
    else{
      iuMax = uCount;
      ivMax = vCount-1;
    }

    // store previously set colorMode
    pushStyle();
    colorMode(HSB, 360, 100, 100, 100);

    float minH = minHue;
    float maxH = maxHue;
    if (abs(maxH-minH) < 20) maxH = minH;
    float minS = minSaturation;
    float maxS = maxSaturation;
    if (abs(maxS-minS) < 10) maxS = minS;
    float minB = minBrightness;
    float maxB = maxBrightness;
    if (abs(maxB-minB) < 10) maxB = minB;


    for (int iv = 0; iv <= ivMax; iv++) {
      if (drawMode == TRIANGLES) {

        for (int iu = 0; iu <= iuMax; iu++) {
          fill(random(minH, maxH), random(minS, maxS), random(minB, maxB), meshAlpha);
          beginShape(drawMode);
          float r1 = meshDistortion * random(-1, 1);
          float r2 = meshDistortion * random(-1, 1);
          float r3 = meshDistortion * random(-1, 1);
          vertex(points[iv][iu].x+r1, points[iv][iu].y+r2, points[iv][iu].z+r3);
          vertex(points[iv+1][iu+1].x+r1, points[iv+1][iu+1].y+r2, points[iv+1][iu+1].z+r3);
          vertex(points[iv+1][iu].x+r1, points[iv+1][iu].y+r2, points[iv+1][iu].z+r3);
          endShape();

          fill(random(minH, maxH), random(minS, maxS), random(minB, maxB), meshAlpha);
          beginShape(drawMode);
          r1 = meshDistortion * random(-1, 1);
          r2 = meshDistortion * random(-1, 1);
          r3 = meshDistortion * random(-1, 1);
          vertex(points[iv+1][iu+1].x+r1, points[iv+1][iu+1].y+r2, points[iv+1][iu+1].z+r3);
          vertex(points[iv][iu].x+r1, points[iv][iu].y+r2, points[iv][iu].z+r3);
          vertex(points[iv][iu+1].x+r1, points[iv][iu+1].y+r2, points[iv][iu+1].z+r3);
          endShape();
        }       

      }
      else if (drawMode == QUADS) {
        for (int iu = 0; iu <= iuMax; iu++) {
          fill(random(minH, maxH), random(minS, maxS), random(minB, maxB), meshAlpha);
          beginShape(drawMode);

          float r1 = meshDistortion * random(-1, 1);
          float r2 = meshDistortion * random(-1, 1);
          float r3 = meshDistortion * random(-1, 1);
          vertex(points[iv][iu].x+r1, points[iv][iu].y+r2, points[iv][iu].z+r3);
          vertex(points[iv+1][iu].x+r1, points[iv+1][iu].y+r2, points[iv+1][iu].z+r3);
          vertex(points[iv+1][iu+1].x+r1, points[iv+1][iu+1].y+r2, points[iv+1][iu+1].z+r3);
          vertex(points[iv][iu+1].x+r1, points[iv][iu+1].y+r2, points[iv][iu+1].z+r3);

          endShape();
        }        
      }
      else{
        // Draw Strips
        fill(random(minH, maxH), random(minS, maxS), random(minB, maxB), meshAlpha);
        beginShape(drawMode);

        for (int iu = 0; iu <= iuMax; iu++) {
          float r1 = meshDistortion * random(-1, 1);
          float r2 = meshDistortion * random(-1, 1);
          float r3 = meshDistortion * random(-1, 1);
          vertex(points[iv][iu].x+r1, points[iv][iu].y+r2, points[iv][iu].z+r3);
          vertex(points[iv+1][iu].x+r1, points[iv+1][iu].y+r2, points[iv+1][iu].z+r3);
        }  

        endShape();
      }
    }

    popStyle();
  }

}


// M_3_4_01_TOOL.pde
// GUI.pde, Mesh.pde, TileSaver.pde
// 
// Generative Gestaltung, ISBN: 978-3-87439-759-9
// First Edition, Hermann Schmidt, Mainz, 2009
// Hartmut Bohnacker, Benedikt Gross, Julia Laub, Claudius Lazzeroni
// Copyright 2009 Hartmut Bohnacker, Benedikt Gross, Julia Laub, Claudius Lazzeroni
//
// http://www.generative-gestaltung.de
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

// TileSaver.pde - v0.12 2007.0326
// Marius Watz - http://workshop.evolutionzone.com
//
// Class for rendering high-resolution images by splitting them into
// tiles using the viewport.
//
// Builds heavily on solution by "surelyyoujest":
// http://processing.org/discourse/yabb_beta/YaBB.cgi?
// board=OpenGL;action=display;num=1159148942

class TileSaver {
  public boolean isTiling=false,done=true;
  public boolean doSavePreview=false;

  PApplet p;
  float FOV=60; // initial field of view
  float cameraZ, width, height;
  int tileNum=10,tileNumSq; // number of tiles
  int tileImgCnt, tileX, tileY, tilePad;
  boolean firstFrame=false, secondFrame=false;
  String tileFilename,tileFileextension=".png";
  PImage tileImg;
  float perc,percMilestone;

  // The constructor takes a PApplet reference to your sketch.
  public TileSaver(PApplet _p) {
    p=_p;
  }

  // If init() is called without specifying number of tiles, getMaxTiles()
  // will be called to estimate number of tiles according to free memory.
  public void init(String _filename) {
    init(_filename,getMaxTiles(p.width));
  }

  // Initialize using a filename to output to and number of tiles to use.
  public void init(String _filename,int _num) {
    tileFilename=_filename;
    tileNum=_num;
    tileNumSq=(tileNum*tileNum);

    // Reset tile counters to start over correctly
    tileX = 0;
    tileY = 0;
    
    width=p.width;
    height=p.height;
    cameraZ=(height/2.0f)/p.tan(p.PI*FOV/360.0f);
    p.println("TileSaver: "+tileNum+" tilesnResolution: "+
      (p.width*tileNum)+"x"+(p.height*tileNum));

    // remove extension from filename
    if(!new java.io.File(tileFilename).isAbsolute())
      tileFilename=p.sketchPath(tileFilename);
    tileFilename=noExt(tileFilename);
    p.createPath(tileFilename);

    // save preview
    if(doSavePreview) p.g.save(tileFilename+"_preview.png");

    // set up off-screen buffer for saving tiled images
    tileImg=new PImage(p.width*tileNum, p.height*tileNum);

    // start tiling
    done=false;
    isTiling=false;
    perc=0;
    percMilestone=0;
    tileInc();
  }

  // set filetype, default is TGA. pass a valid image extension as parameter.
  public void setSaveType(String extension) {
    tileFileextension=extension;
    if(tileFileextension.indexOf(".")==-1) tileFileextension="."+tileFileextension;
  }

  // pre() handles initialization of each frame.
  // It should be called in draw() before any drawing occurs.
  public void pre() {
    if(!isTiling) return;
    if(firstFrame) firstFrame=false;
    else if(secondFrame) {
      secondFrame=false;
      
      // since processing version 1.0.8 (revision 0170) the following line has to be removed,
      //        because updating of the projection works now imediately.
      // tileInc();
    }
    setupCamera();
  }

  // post() handles tile update and image saving.
  // It should be called at the very end of draw(), after any drawing.
  public void post() {
    // If first or second frame, don't update or save.
    if(firstFrame||secondFrame|| (!isTiling)) return;

    // Find image ID from reverse row order
    int imgid=tileImgCnt%tileNum+(tileNum-tileImgCnt/tileNum-1)*tileNum;
    int idx=(imgid+0)%tileNum;
    int idy=(imgid/tileNum);

    // Get current image from sketch and draw it into buffer
    p.loadPixels();
    tileImg.set(idx*p.width, idy*p.height, p.g);

    // Increment tile index
    tileImgCnt++;
    perc=100*((float)tileImgCnt/(float)tileNumSq);
    if(perc-percMilestone>5 || perc>99) {
      p.println(p.nf(perc,3,2)+"% completed. "+tileImgCnt+"/"+tileNumSq+" images saved.");
      percMilestone=perc;
    }

    if(tileImgCnt==tileNumSq) tileFinish();
    else tileInc();
  }

  public boolean checkStatus() {
    return isTiling;
  }


  // tileFinish() handles saving of the tiled image
  public void tileFinish() {
    isTiling=false;

    restoreCamera();

    // save large image to TGA
    tileFilename+="_"+(p.width*tileNum)+"x"+
      (p.height*tileNum)+tileFileextension;
    p.println("Save: "+
      tileFilename.substring(
    tileFilename.lastIndexOf(java.io.File.separator)+1));
    tileImg.save(tileFilename);
    p.println("Done tiling.n");

    // clear buffer for garbage collection
    tileImg=null;
    done=true;
  }

  // Increment tile coordinates
  public void tileInc() {
    if(!isTiling) {
      isTiling=true;
      firstFrame=true;
      secondFrame=true;
      tileImgCnt=0;
    } 
    else {
      if(tileX==tileNum-1) {
        tileX=0;
        tileY=(tileY+1)%tileNum;
      } 
      else
        tileX++;
    }
  }

  // set up camera correctly for the current tile
  public void setupCamera() {
    p.camera(width/2.0f, height/2.0f, cameraZ,
    width/2.0f, height/2.0f, 0, 0, 1, 0);
    if(isTiling) {
      float mod=1f/10f;
      p.frustum(width*((float)tileX/(float)tileNum-.5f)*mod,
      width*((tileX+1)/(float)tileNum-.5f)*mod,
      height*((float)tileY/(float)tileNum-.5f)*mod,
      height*((tileY+1)/(float)tileNum-.5f)*mod,
      cameraZ*mod, 10000);
    }

  }

  // restore camera once tiling is done
  public void restoreCamera() {
    float mod=1f/10f;
    p.camera(width/2.0f, height/2.0f, cameraZ,
    width/2.0f, height/2.0f, 0, 0, 1, 0);
    p.frustum(-(width/2)*mod, (width/2)*mod,
    -(height/2)*mod, (height/2)*mod,
    cameraZ*mod, 10000);
  }

  // checks free memory and gives a suggestion for maximum tile
  // resolution. It should work well in most cases, I've been able
  // to generate 20k x 20k pixel images with 1.5 GB RAM allocated.
  public int getMaxTiles(int width) {

    // get an instance of java.lang.Runtime, force garbage collection
    java.lang.Runtime runtime=java.lang.Runtime.getRuntime();
    runtime.gc();

    // calculate free memory for ARGB (4 byte) data, giving some slack
    // to out of memory crashes.
    int num=(int)(Math.sqrt(
    (float)(runtime.freeMemory()/4)*0.925f))/width;
    p.println(((float)runtime.freeMemory()/(1024*1024))+"/"+
      ((float)runtime.totalMemory()/(1024*1024)));

    // warn if low memory
    if(num==1) {
      p.println("Memory is low. Consider increasing memory settings.");
      num=2;
    }

    return num;
  }

  // strip extension from filename
  public String noExt(String name) {
    int last=name.lastIndexOf(".");
    if(last>0)
      return name.substring(0, last);

    return name;
  }
}
  public void settings() {  size(1000, 1000, P3D); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "M_3_4_03_TOOL" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
