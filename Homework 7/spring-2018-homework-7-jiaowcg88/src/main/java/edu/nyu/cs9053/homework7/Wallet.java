package edu.nyu.cs9053.homework7;

import java.util.concurrent.atomic.AtomicReference;

public class Wallet<T> {

    private final AtomicReference <T[]> array;

    private int size;

    private ArrayCreator<T> arrayCreator;

    public Wallet(ArrayCreator<T> arrayCreator, int size) {
        this.arrayCreator = arrayCreator;
        this.array = new AtomicReference<>();
        this.array.set(arrayCreator.create(size));
        this.size = 0;
    }

    public boolean contains(T value) {
        if (value == null) {
            return false;
        }
        for (int i = 0 ; i < size; i++) {
            if (array.get()[i].equals(value)) {
                return true;
            }
        }
        return false;

    }

    public boolean add(T value) {
        if (value == null || contains(value)) {
            return false;
        }
        if (size >= array.get().length) {
            ensureCapacity();
        }
        array.get()[size++] = value;
        return true;
    }

    public boolean remove(T value) {
        for (int i = 0; i < size ; i++) {
            if (!array.get()[i].equals(value)) {
                continue;
            } else {
                while (i < size-1) {
                    T temp = array.get()[i];
                    array.get()[i] = array.get()[i+1];
                    array.get()[i+1] = temp;
                    i++;
                }
                size--;
                return true;
            }
        }
        return false;
    }

    public T get(int index) {
        if (index < size) {
            return array.get()[index];
        }
        return null;
    }

    private void ensureCapacity() {
        int newSize = array.get().length * 2;
        T[] newArray = arrayCreator.create(newSize);
        java.lang.System.arraycopy(this.array.get(), 0, newArray, 0, this.array.get().length);
        this.array.set(newArray);
    }

    public int getSize() {
        return this.size;
    }

}
