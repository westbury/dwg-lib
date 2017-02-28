package com.onespatial.dwglib.objects;

import java.io.UnsupportedEncodingException;
import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.onespatial.dwglib.FileVersion;
import com.onespatial.dwglib.bitstreams.BitBuffer;
import com.onespatial.dwglib.bitstreams.Handle;
import com.onespatial.dwglib.bitstreams.Point3D;

public abstract class CadObject {

    protected final ObjectMap objectMap;

    public Handle handleOfThisObject;

    // Defined in this class but always set in derived classes
    protected Handle[] reactorHandles;

    private List<Handle> genericHandles = new ArrayList<>();

    private Map<Handle, Object[]> extendedEntityData = new HashMap<>();

    // Defined in this class but always set in derived classes
    protected Handle xdicobjhandle;

    public CadObject(ObjectMap objectMap) {
        this.objectMap = objectMap;
    }

    public void readFromStreams(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream,
            FileVersion fileVersion) {
        handleOfThisObject = dataStream.getHandle();

        // Page 254 Chapter 27 Extended Entity Data

        int sizeOfExtendedObjectData = dataStream.getBS();
        while (sizeOfExtendedObjectData != 0) {
            Handle appHandle = dataStream.getHandle();

            List<Object> components = new ArrayList<>();
            Stack<List<Object>> componentStack = new Stack<>();

            int expected = dataStream.position() + sizeOfExtendedObjectData * 8;

            while (dataStream.position() < expected) {
                int dxfGroupCode = dataStream.getRC();

                switch (dxfGroupCode) {
                case 0: {
                    int length = dataStream.getRC();
                    byte[] x = new byte[length * 2];
                    for (int i = 0; i < length * 2; i++) {
                        x[i] = (byte) dataStream.getRC();
                    }
                    try {
                        String text = new String(x, "UTF-16");
                        components.add(text);
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException("Should not happen");
                    }

                    int unknown = dataStream.getRC();

                    break;
                }

                case 2: {
                    int x = dataStream.getRC();
                    switch (x) {
                    case 0:
                        componentStack.push(components);
                        components = new ArrayList<>();
                        break;
                    case 1:
                        List<Object> justCompletedList = components;
                        components = componentStack.pop();
                        components.add(justCompletedList.toArray());
                        break;
                    }
                }
                break;

                case 3:
                case 5: {
                    int[] handleBytes = dataStream.getBytes(8);
                    Handle handle = new Handle(5, handleBytes);
                    // TODO distinguish between 3 and 5, otherwise
                    // the information is lost.
                    components.add(handle);
                }
                break;

                case 4: {
                    int length = dataStream.getRC();
                    byte[] x = new byte[length];
                    for (int i = 0; i < length; i++) {
                        x[i] = (byte) dataStream.getRC();
                    }
                    components.add(x);
                }
                break;

                case 10:
                case 11:
                case 12:
                case 13: {
                    double x = dataStream.getRD();
                    double y = dataStream.getRD();
                    double z = dataStream.getRD();
                    Point3D point = new Point3D(x, y, z);
                    components.add(point);
                }
                break;

                case 40:
                case 41:
                case 42: {
                    double x = dataStream.getRD();
                    components.add(x);
                }
                break;

                case 70: {
                    int x = dataStream.getRS();
                    components.add(Short.valueOf((short) x));
                }
                break;

                case 71: {
                    int x = dataStream.getRL();
                    components.add(Long.valueOf(x));
                }
                break;

                default:
                    throw new RuntimeException("Unexpected case");
                }
            }

            assert dataStream.position() == expected;

            extendedEntityData.put(appHandle, components.toArray());

            sizeOfExtendedObjectData = dataStream.getBS();
        }

        readPostCommonFields(dataStream, stringStream, handleStream, fileVersion);
    }

    abstract protected void readPostCommonFields(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream,
            FileVersion fileVersion);

    protected void readObjectTypeSpecificData(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream,
            FileVersion fileVersion) {
        // For the time being, provide this default implementation that
        // just reads all the handles.
        // Ultimately this should be an abstract method.

        try {
            do {
                Handle referencedHandle = handleStream.getHandle(handleOfThisObject);
                genericHandles.add(referencedHandle);
            } while (true);
        } catch (RuntimeException e) {

        }
        handleStream.advanceToByteBoundary();
        handleStream.assertEndOfStream();
    }

    public List<CadObject> getReactors() {
        return new AbstractList<CadObject>() {

            @Override
            public CadObject get(int index) {
                if (reactorHandles[index] == null) {
                    return null;
                } else {
                    CadObject result = objectMap.parseObject(reactorHandles[index]);
                    return result;
                }
            }

            @Override
            public int size() {
                return reactorHandles.length;
            }
        };
    }

    public Dictionary getXDictionary() {
        if (xdicobjhandle == null) {
            return null;
        } else {
            CadObject result = objectMap.parseObject(xdicobjhandle);
            return (Dictionary) result;
        }
    }

    public Map<Appid, Object[]> getExtendedEntityData() {
        return new AbstractMap<Appid, Object[]>() {

            @Override
            public Set<java.util.Map.Entry<Appid, Object[]>> entrySet() {
                return new AbstractSet<java.util.Map.Entry<Appid, Object[]>>() {

                    @Override
                    public Iterator<java.util.Map.Entry<Appid, Object[]>> iterator() {
                        final Iterator<java.util.Map.Entry<Handle, Object[]>> iter = extendedEntityData.entrySet()
                                .iterator();
                        return new Iterator<java.util.Map.Entry<Appid, Object[]>>() {

                            @Override
                            public boolean hasNext() {
                                return iter.hasNext();
                            }

                            @Override
                            public java.util.Map.Entry<Appid, Object[]> next() {
                                final java.util.Map.Entry<Handle, Object[]> e = iter.next();
                                return new java.util.Map.Entry<Appid, Object[]>() {

                                    @Override
                                    public Appid getKey() {
                                        return (Appid) objectMap.parseObject(e.getKey());
                                    }

                                    @Override
                                    public Object[] getValue() {
                                        return e.getValue();
                                    }

                                    @Override
                                    public Object[] setValue(Object[] arg0) {
                                        throw new UnsupportedOperationException();
                                    }
                                };
                            }

                            @Override
                            public void remove() {
                                throw new UnsupportedOperationException();
                            }
                        };
                    }

                    @Override
                    public int size() {
                        return extendedEntityData.size();
                    }
                };
            }
        };
    }

    public Dictionary getXdicobj() {
        if (xdicobjhandle == null) {
            return null;
        } else {
            CadObject result = objectMap.parseObject(xdicobjhandle);
            return (Dictionary) result;
        }
    }

    public List<CadObject> getGenericObjects() {
        return new AbstractList<CadObject>() {

            @Override
            public CadObject get(int index) {
                CadObject result = objectMap.parseObjectPossiblyNullOrOrphaned(genericHandles.get(index));
                return result;
            }

            @Override
            public int size() {
                return genericHandles.size();
            }
        };
    }
}
