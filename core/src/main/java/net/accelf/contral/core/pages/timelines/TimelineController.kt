package net.accelf.contral.core.pages.timelines

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.serializer
import net.accelf.contral.api.timelines.Timeline
import kotlin.reflect.KClass

internal class TimelineController(timelines: List<KClass<*>>) {

    @OptIn(InternalSerializationApi::class)
    private val json = Json {
        serializersModule = SerializersModule {
            polymorphic(Timeline::class) {
                timelines.forEach {
                    @Suppress("UNCHECKED_CAST")
                    subclass(it as KClass<Timeline>, it.serializer())
                }
            }
        }
    }

    internal fun getTimeline(params: String): Timeline = json.decodeFromString(params)
    internal fun paramsToString(params: Timeline) = json.encodeToString(params)
}
