package offkiltermc.infohud.infoline

abstract class InfoLineBase(
    final override val key: String,
    final override val category: SettingsCategory
) : InfoLine {
    private fun fixKey(key: String): String {
        return key.replace("-", "")
    }
    override val name = "offkilter.infohud.${fixKey(key)}.name"
    override val description = "offkilter.infohud.${fixKey(key)}.desc"
}