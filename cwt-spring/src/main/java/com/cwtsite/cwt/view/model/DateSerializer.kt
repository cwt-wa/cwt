package com.cwtsite.cwt.view.model

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import org.springframework.boot.jackson.JsonComponent
import java.time.ZoneOffset
import java.util.*

@JsonComponent
class DateSerializer : StdSerializer<Date>(Date::class.java) {
    override fun serialize(value: Date?, gen: JsonGenerator, provider: SerializerProvider?) {
        if (value == null) {
            gen.writeNull()
        } else {
            gen.writeString(value.toInstant().atZone(ZoneOffset.UTC).toString())
        }
    }
}
