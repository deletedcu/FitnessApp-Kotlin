package com.liverowing.android.model.parse

import com.parse.ParseObject
import kotlin.reflect.KProperty

class ParseDelegate<out T> {
    @Suppress("UNCHECKED_CAST")
    operator fun getValue(parseObj: ParseObject, propertyMetadata: KProperty<*>): T {
        return parseObj.get(propertyMetadata.name) as T
    }

    operator fun setValue(parseObj: ParseObject, propertyMetadata: KProperty<*>, a: Any?) {
        if (a != null) {
            parseObj.put(propertyMetadata.name, a)
        }
    }
}