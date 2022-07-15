package offkilter.infohud.infoline

interface InfoLine {
    fun getInfoString(env: InfoLineEnvironment): String?
    val key: String
    val name: String
    val description: String
    val category: SettingsCategory
}