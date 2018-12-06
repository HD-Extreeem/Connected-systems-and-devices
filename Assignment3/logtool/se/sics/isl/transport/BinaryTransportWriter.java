/**
 * SICS ISL Java Utilities
 * http://www.sics.se/tac/    tac-dev@sics.se
 *
 * Copyright (c) 2001-2003 SICS AB. All rights reserved.
 *
 * SICS grants you the right to use, modify, and redistribute this
 * software for noncommercial purposes, on the conditions that you:
 * (1) retain the original headers, including the copyright notice and
 * this text, (2) clearly document the difference between any derived
 * software and the original, and (3) acknowledge your use of this
 * software in pertaining publications and reports.  SICS provides
 * this software "as is", without any warranty of any kind.  IN NO
 * EVENT SHALL SICS BE LIABLE FOR ANY DIRECT, SPECIAL OR INDIRECT,
 * PUNITIVE, INCIDENTAL OR CONSEQUENTIAL LOSSES OR DAMAGES ARISING OUT
 * OF THE USE OF THE SOFTWARE.
 *
 * -----------------------------------------------------------------
 *
 * BinaryTransportWriter
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson
 * Created : Mon Jan 13 17:13:34 2003
 * Updated : $Date: 2005/01/25 19:32:54 $
 *           $Revision: 1.5 $
 * Purpose :
 * To write XML-like messages in a compact binary format
 *
 * N count NameIndex T1 T2 T3... V1 V2 V3...
 * 1 1     2         ?  ?  ?
 * C V1 V2 V3 - same types as the previous "node" - only for empty nodes...
 *
 * n [and the rest as N - but without the end-tag since n is a complete node]
 * \n - ends nodes and tables
 *
 *
 */
package se.sics.isl.transport;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Enumeration;
import java.util.Hashtable;

import com.botbox.util.ArrayUtils;

public class BinaryTransportWriter extends TransportWriter
  implements BinaryTransport
{

  private static final int DEF_SIZE = 10;
  private static final int DATA_SIZE = 1024;

  private static final int TYPE_POS = 0;
  private static final int NAME_POS = 1;

  private Hashtable nameLookup = new Hashtable();

  private boolean inNode = false; // Defining a node
  private boolean inTable = false; // in a table
  private boolean checkTable = false; // if two nodes have same name...

  private int nodeLevel = 0;
  private int lastNodeLevel = 0;

  // Storage for old attributes "rows" when defining tables...
  private int[][] currentRow = new int[2][DEF_SIZE * 2]; // Type-Name pairs
  private long[][] currentIValues = new long[2][DEF_SIZE];
  private float[][] currentFValues = new float[2][DEF_SIZE];
  private Object[][] currentOValues = new Object[2][DEF_SIZE];
  private int attrCount[] = new int[2];
  private int nodeName[] = new int[] { -1, -1 };
  private int currentPos = 0;

  private int nextID = 0;

  private byte[] byteData = new byte[DATA_SIZE]; // Current 'message' buffer
  private int nrOfBytes;

  public BinaryTransportWriter() {
  }


  /*********************************************************************
   * apis for writing a block of data
   **********************************************************************/

  public int size() {
    return nrOfBytes;
  }

  // HACK!!!! BECAUSE ALIASES ARE PER CONNECTION BUT WE WANT TO USE BROADCAST. FIX THIS!!!
  public int getInitSize() {
    Enumeration enumb = nameLookup.keys();
    int oldPos = nrOfBytes;
    while (enumb.hasMoreElements()) {
      String name = (String) enumb.nextElement();
      int id = ((Integer) nameLookup.get(name)).intValue();
      writeByte(ALIAS);
      writeShort(id);
      writeString(name);
    }
    int len = nrOfBytes - oldPos;
    nrOfBytes = oldPos;
    return len;
  }

  public byte[] getInitBytes() {
    Enumeration enumb = nameLookup.keys();
    int oldPos = nrOfBytes;
    while (enumb.hasMoreElements()) {
      String name = (String) enumb.nextElement();
      int id = ((Integer) nameLookup.get(name)).intValue();
      writeByte(ALIAS);
      writeShort(id);
      writeString(name);
    }
    if (oldPos != nrOfBytes) {
      int size = nrOfBytes - oldPos;
      byte[] buffer = new byte[size];
      System.arraycopy(byteData, oldPos, buffer, 0, size);
      nrOfBytes = oldPos;
      return buffer;
    }
    return null;
  }

  public void writeInit(ByteBuffer buffer) {
    Enumeration enumb = nameLookup.keys();
    int oldPos = nrOfBytes;
    while (enumb.hasMoreElements()) {
      String name = (String) enumb.nextElement();
      int id = ((Integer) nameLookup.get(name)).intValue();
      writeByte(ALIAS);
      writeShort(id);
      writeString(name);
    }
    if (oldPos != nrOfBytes) {
      buffer.put(byteData, oldPos, nrOfBytes - oldPos);
      nrOfBytes = oldPos;
    }
  }

  public void writeInit(OutputStream stream) throws IOException {
    Enumeration enumb = nameLookup.keys();
    int oldPos = nrOfBytes;
    while (enumb.hasMoreElements()) {
      String name = (String) enumb.nextElement();
      int id = ((Integer) nameLookup.get(name)).intValue();
      writeByte(ALIAS);
      writeShort(id);
      writeString(name);
    }
    if (oldPos != nrOfBytes) {
      stream.write(byteData, oldPos, nrOfBytes - oldPos);
      nrOfBytes = oldPos;
    }
  }
  // HACK!!!! BECAUSE ALIASES ARE PER CONNECTION BUT WE WANT TO USE BROADCAST. FIX THIS!!!

  public void write(ByteBuffer buffer) {
    buffer.put(byteData, 0, nrOfBytes);
  }

  public void write(OutputStream stream) throws IOException {
    stream.write(byteData, 0, nrOfBytes);
  }

  public void write(byte[] bytes) {
    if (bytes.length < nrOfBytes) {
      throw new IndexOutOfBoundsException("Too many bytes to fit array, requires " + nrOfBytes + ", got " + bytes.length);
    }
    System.arraycopy(byteData, 0, bytes, 0, nrOfBytes);
  }

  public byte[] getBytes() {
    byte[] buffer = new byte[nrOfBytes];
    System.arraycopy(byteData, 0, buffer, 0, nrOfBytes);
    return buffer;
  }


  /*********************************************************************
   * TransportWriter API
   **********************************************************************/

  public TransportWriter attr(String name, int value) {
    if (!inNode) throw new IllegalArgumentException("Can not output attributes outside of nodes");
    int nid = getNameID(name);
    int index = setType(INT, nid);
    currentIValues[currentPos][index] = value;
    return this;
  }

  public TransportWriter attr(String name, long value) {
    if (!inNode) throw new IllegalArgumentException("Can not output attributes outside of nodes");
//     System.out.println("LONG Attribute: " + name + " = " + value);
    int nid = getNameID(name);
    int index = setType(LONG, nid);
    currentIValues[currentPos][index] = value;
    return this;
  }

  public TransportWriter attr(String name, float value) {
    if (!inNode) throw new IllegalArgumentException("Can not output attributes outside of nodes");
//     System.out.println("FLOAT Attribute: " + name);
    int nid = getNameID(name);
    int index = setType(FLOAT, nid);
    currentFValues[currentPos][index] = value;
    return this;
  }

  public TransportWriter attr(String name, String value) {
    if (!inNode) throw new IllegalArgumentException("Can not output attributes outside of nodes");
    int nid = getNameID(name);
    int index = setType(STRING, nid);
    currentOValues[currentPos][index] = value;
    return this;
  }

  public TransportWriter attr(String name, int[] value) {
    if (!inNode) throw new IllegalArgumentException("Can not output attributes outside of nodes");
    int nid = getNameID(name);
    int index = setType(INT_ARR, nid);
    currentOValues[currentPos][index] = value;
    return this;
  }

  public int getNodeLevel() {
    return nodeLevel;
  }

  public TransportWriter node(String name) {
    writeCurrentNode();

    int nid = getNameID(name);
    // Do stuff to see if this is in a table or not!!!

    // Update this node information
    currentPos = 1 - currentPos;
    nodeLevel++;
    inNode = true;
    nodeName[currentPos] = nid;
    attrCount[currentPos] = 0;
    lastNodeLevel = nodeLevel;
    return this;
  }

  public TransportWriter endNode(String name) {
    // Should verify the node name. FIX THIS!!!

    // Maybe print out the node - probably not?

    // If in table do not show the "end-node" char, otherwize do!
    writeCurrentNode();
    // not if this node is of type 'NODE'
    writeByte(END_NODE);

    nodeLevel--;
    return this;
  }

  private void writeCurrentNode() {
    writeCurrentNode(START_NODE, currentPos);
  }

  private void writeCurrentNode(int nodeType, int pos) {
    // Syncronize if several writing to same stream...
    int name = nodeName[pos];

    // Write only if name is defined
    if (name != -1) {
      if (!inTable) {
	writeByte(nodeType);
	writeByte(attrCount[pos]);
	writeShort(name);
	// Write types
	for (int i = 0, n = attrCount[pos] * 2; i < n; i += 2) {
	  int type = currentRow[pos][i + TYPE_POS];
	  writeByte(type);
	  writeShort(currentRow[pos][i + NAME_POS]);
	}
      }
      // Write values
      for (int i = 0, n = attrCount[pos]; i < n; i++) {
	int type = currentRow[pos][i * 2 + TYPE_POS];
	switch (type) {
	case INT:
	  writeInt((int) currentIValues[pos][i]);
	  break;
	case LONG:
	  long val = currentIValues[pos][i];
	  writeByte((int)(val >>> 56) & 0xff);
	  writeByte((int)(val >>> 48) & 0xff);
	  writeByte((int)(val >>> 40) & 0xff);
	  writeByte((int)(val >>> 32) & 0xff);
	  writeByte((int)(val >>> 24) & 0xff);
	  writeByte((int)(val >>> 16) & 0xff);
	  writeByte((int)(val >>> 8) & 0xff);
	  writeByte((int) val & 0xff);
	  break;
	case FLOAT:
	  writeFloat(currentFValues[pos][i]);
	  break;
	case STRING:
 	  writeString((String) currentOValues[pos][i]);
	  break;
	case INT_ARR:
 	  writeIntArr((int[]) currentOValues[pos][i]);
	  break;
	}
      }
      // Written - tag as already used!
      nodeName[pos] = -1;
    }
  }

  private void writeByte(int data) {
    if (nrOfBytes >= byteData.length) {
      byteData = (byte[]) ArrayUtils.setSize(byteData, nrOfBytes + DATA_SIZE);
    }
//     System.out.println("Writing byte: " + data + " (0x"
// 		       + Integer.toHexString(data) + ") to " + nrOfBytes);
    byteData[nrOfBytes++] = (byte) (data & 0xff);
  }

  private void writeShort(int data) {
    writeByte((data >> 8) & 0xff);
    writeByte(data & 0xff);
  }

  private void writeInt(int data) {
    writeByte((int)(data >>> 24) & 0xff);
    writeByte((int)(data >>> 16) & 0xff);
    writeByte((int)(data >>> 8) & 0xff);
    writeByte((int) data & 0xff);
  }

  private void writeFloat(float data) {
    writeInt(Float.floatToIntBits(data));
  }

  /**
   * Adds the specified string in UTF-8 machine independent format.
   */
  private void writeString(String value) {
    // Make sure there is room for the maximum encoded string size
    // where each character takes three bytes.
    int len = value.length();
    if (nrOfBytes + len * 3 >= byteData.length) {
      byteData = (byte[])
	ArrayUtils.setSize(byteData, nrOfBytes + len * 3 + DATA_SIZE);
    }

    // This code has been "inspired" from Core JavaTM Technologies Tech
    // Tips, January 10, 2003 and java.io.Data{Input,Output}Stream.
    int index = nrOfBytes + 2;
    for (int i = 0; i < len; i++) {
      char c = value.charAt(i);
      if ((c >= 0x0001) && (c <= 0x007F)) {
	byteData[index++] = (byte) c;
      } else if (c > 0x07FF) {
	byteData[index++] = (byte) (0xE0 | ((c >> 12) & 0x0F));
	byteData[index++] = (byte) (0x80 | ((c >>  6) & 0x3F));
	byteData[index++] = (byte) (0x80 | ((c >>  0) & 0x3F));
      } else {
	byteData[index++] = (byte) (0xC0 | ((c >>  6) & 0x1F));
	byteData[index++] = (byte) (0x80 | ((c >>  0) & 0x3F));
      }
    }
    int size = index - nrOfBytes - 2;
    if (size > 65535) {
      throw new IllegalArgumentException("too large string: " + len);
    }
    writeShort(size);
    nrOfBytes = index;
  }

  private void writeIntArr(int[] value) {
    // Make sure there is room for the maximum int arr
    // where each int takes 4 bytes.
    int len = value.length;
    if (nrOfBytes + len * 4 >= byteData.length) {
      byteData = (byte[])
	ArrayUtils.setSize(byteData, nrOfBytes + len * 4 + DATA_SIZE);
    }
    writeShort(len);
    for (int i = 0, n = len; i < n; i++) {
      writeInt(value[i]);
    }
  }


//   private void writeBytes(byte[] bytes) {
//     if (nrOfBytes + bytes.length >= byteData.length) {
//       byteData = (byte[])
// 	ArrayUtils.setSize(byteData, bytes.length + nrOfBytes + DATA_SIZE);
//     }
//     System.arraycopy(bytes, 0, byteData, nrOfBytes, bytes.length);
//     nrOfBytes += bytes.length;
//   }

  private int getNameID(String name) {
    Integer alias = (Integer) nameLookup.get(name);
    if (alias != null) {
      return alias.intValue();
    } else {
      int id = nextID++;
      nameLookup.put(name, new Integer(id));
      writeByte(ALIAS);
      writeShort(id);
      writeString(name);
      return id;
    }
  }

  // ensure that all nodes and atts are in the "output"
  public void finish() {
    writeCurrentNode();
  }

  public void clear() {
    nrOfBytes = 0;
  }

  private int setType(int type, int name) {
    int ac = attrCount[currentPos];
    if (ac >= currentIValues.length - 1) {
      int newSize = ac + DEF_SIZE;
      currentIValues[currentPos] =
	ArrayUtils.setSize(currentIValues[currentPos], newSize);
      currentFValues[currentPos] =
	ArrayUtils.setSize(currentFValues[currentPos], newSize);
      currentOValues[currentPos] = (Object[])
	ArrayUtils.setSize(currentOValues[currentPos], newSize);
      currentRow[currentPos] =
	ArrayUtils.setSize(currentRow[currentPos], newSize * 2);
    }

    currentRow[currentPos][ac * 2] = type;
    currentRow[currentPos][ac * 2 + 1] = name;

    return attrCount[currentPos]++;
  }


  /*********************************************************************
   * Test main
   **********************************************************************/

  public static void main(String[] a) throws Exception {
    BinaryTransportWriter bmw = new BinaryTransportWriter();

    bmw.node("test");
    bmw.attr("id", 66);
    bmw.attr("str", "rad1");
    bmw.endNode("test");
    bmw.node("test");
    bmw.attr("id", System.currentTimeMillis());
    bmw.attr("str", "rad2");
    bmw.endNode("test");
    bmw.node("test2");
    bmw.attr("id", 68.0f);
    bmw.attr("str", "rad3");
    bmw.node("subnode");
    bmw.attr("id", 69);
    bmw.attr("str", "sub-rad1");
    bmw.endNode("subnode");
    bmw.node("subnode");
    bmw.attr("id", 70);
    bmw.attr("str", "sub-rad2");
    bmw.endNode("subnode");
    bmw.node("subnode");
    bmw.attr("id", 71);
    bmw.attr("arr1", new int[]{65,66,67,68});
    bmw.attr("str", "sub-rad3");
    bmw.endNode("subnode");
    bmw.endNode("test2");
    bmw.finish();

    // Write to bytes!
    byte[] bmsg = new byte[bmw.size()];
    bmw.write(bmsg);

    System.out.println("Message:" + new String(bmsg));

    BinaryTransportReader bmr = new BinaryTransportReader();
    System.out.println("Parsing:");

    bmr.setMessage(bmsg);

    System.out.println("Printing message:");
    bmr.printMessage();

    while (bmr.nextNode(false)) {
      System.out.println("Node Name: " + bmr.getNodeName());
      System.out.println("Attribute id = " + bmr.getAttribute("id", null));
      System.out.println("Attribute str = " + bmr.getAttribute("str", null));
    }
  }

} // BinaryTransportWriter
