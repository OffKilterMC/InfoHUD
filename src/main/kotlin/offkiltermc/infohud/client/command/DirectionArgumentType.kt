package offkiltermc.infohud.client.command

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.network.chat.Component
import offkiltermc.infohud.client.InfoHUDSettings
import java.util.concurrent.CompletableFuture

class DirectionArgumentType private constructor() : ArgumentType<InfoHUDSettings.Direction> {
    @Throws(CommandSyntaxException::class)
    override fun parse(reader: StringReader): InfoHUDSettings.Direction {
        try {
            return InfoHUDSettings.Direction.valueOf(reader.readUnquotedString().uppercase())
        } catch (_: Exception) {
            throw SimpleCommandExceptionType(Component.literal("Invalid direction")).create()
        }
    }

    override fun <S> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        enumValues<InfoHUDSettings.Direction>().forEach {
            if (it.name.lowercase().startsWith(builder.remainingLowerCase)) {
                builder.suggest(it.name.lowercase())
            }
        }
        return builder.buildFuture()
    }

    override fun getExamples(): Collection<String> {
        return EXAMPLES
    }

    companion object {
        private val EXAMPLES = listOf("up", "down")
        fun direction(): DirectionArgumentType {
            return DirectionArgumentType()
        }

        fun getDirection(context: CommandContext<*>, name: String?): InfoHUDSettings.Direction {
            return context.getArgument(name, InfoHUDSettings.Direction::class.java)
        }
    }
}
