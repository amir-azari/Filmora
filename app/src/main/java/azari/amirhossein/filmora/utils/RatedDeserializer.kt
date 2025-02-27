package azari.amirhossein.filmora.utils


import azari.amirhossein.filmora.models.detail.ResponseAccountStates
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class RatedDeserializer : JsonDeserializer<ResponseAccountStates.Rated?> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): ResponseAccountStates.Rated? {
        return when {
            json.isJsonObject -> {
                val value = json.asJsonObject.get("value")?.asInt ?: return null
                ResponseAccountStates.Rated(value)
            }
            json.isJsonPrimitive && json.asJsonPrimitive.isBoolean -> {
                null
            }
            else -> null
        }
    }
}

