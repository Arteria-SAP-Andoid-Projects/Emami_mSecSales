package com.arteriatech.emami.common;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by e10742 on 21-11-2016.
 */
public class SearchFilter {
//    public static Object predicateParams;

    /*
     * This Method takes List and filter interface as parameter. Filter List using searchFilterInterface and returns Filtered List
     * */
    public static <T> Collection<T> filter(Collection<T> sourceList, SearchFilterInterface<T> filterCondition) {
        Collection<T> result = new ArrayList<T>();
        for (T item : sourceList) {
            //Call search interface for each item and check condition, if true then add item into filteredList
            if (filterCondition.applyConditionToAdd(item)) {
                result.add(item);
            }
        }
        return result;
    }

//    public static <T> T select(Collection<T> sourceList, SearchFilterInterface<T> filterCondition) {
//        T result = null;
//        for (T element : sourceList) {
//            if (!filterCondition.applyConditionToAdd(element))
//                continue;
//            result = element;
//            break;
//        }
//        return result;
//    }
//
//    public static <T> T select(Collection<T> sourceList, SearchFilterInterface<T> filterCondition, T defaultValue) {
//        T result = defaultValue;
//        for (T element : sourceList) {
//            if (!filterCondition.applyConditionToAdd(element))
//                continue;
//            result = element;
//            break;
//        }
//        return result;
//    }
}