package offkilter.infohud.client

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.network.chat.Component
import java.util.*
import java.util.concurrent.CompletableFuture

class InfoLineArgumentType private constructor() : ArgumentType<InfoLine> {
    @Throws(CommandSyntaxException::class)
    override fun parse(reader: StringReader): InfoLine {
        val result = InfoLine.BY_NAME[reader.readUnquotedString()]
        return result ?: throw SimpleCommandExceptionType(Component.literal("Invalid info line identifier")).create()
    }

    override fun <S> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        enumValues<InfoLine>().forEach {
            if (it.key.startsWith(builder.remainingLowerCase)) {
                builder.suggest(it.key)
            }
        }
        return builder.buildFuture()
    }

    override fun getExamples(): Collection<String> {
        return EXAMPLES
    }

    companion object {
        private val EXAMPLES: Collection<String> = Arrays.asList("fps", "location")
        fun infoLine(): InfoLineArgumentType {
            return InfoLineArgumentType()
        }

        fun getInfoLine(context: CommandContext<*>, name: String?): InfoLine {
            return context.getArgument(name, InfoLine::class.java)
        }
    }
}
