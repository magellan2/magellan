// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.util;

import java.awt.image.BufferedImage;

/**
*
* @author
* @version
*/
public class PNG extends Object {
  
  static public void savePNG(BufferedImage bimg, java.io.OutputStream out)
    throws java.io.IOException
  {
    /*com.sun.media.jai.codec.PNGEncodeParam pngEncodeParam = 
      com.sun.media.jai.codec.PNGEncodeParam.getDefaultEncodeParam(bimg);

    com.sun.media.jai.codec.ImageEncoder enc = 
      com.sun.media.jai.codec.ImageCodec.createImageEncoder("PNG", out, pngEncodeParam);
    enc.encode(bimg);*/
  } 
}
