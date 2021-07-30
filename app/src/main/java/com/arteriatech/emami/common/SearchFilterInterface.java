package com.arteriatech.emami.common;

/**
 * Created by e10742 on 21-11-2016.
 */
public interface SearchFilterInterface<T> {
    /*
     * Interface to implement filter condition.
     * */
    boolean applyConditionToAdd(T item);
}
