package com.onespatial.dwglib.bitstreams;

public class Value<T>
{
    private T value;

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }

    @Override
    public String toString() {
    	return value == null ? "null" : value.toString();
    }
}
