package se.experis.com.case2020.lagalt.utils;

import java.lang.reflect.Field;

import org.springframework.stereotype.Component;

@Component
public class ObjectTool {

    /**
     * Updates updateObject with non null fields from partialObject Useful when
     * getting an object from db and updating it with data from a json object The
     * partialObject must be an instanceof the updateObject
     */
    public void updateNonNullFields(Object partialObject, Object updateObject) throws IllegalArgumentException {
        try {
            for (Field field : partialObject.getClass().getDeclaredFields()) {
                var value = field.get(partialObject);
                if (value != null) {
                    field.set(updateObject, value);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
