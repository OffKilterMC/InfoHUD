# InfoHUD

This mod displays information you would only normally see on your F3 screen at all times in the upper left corner. You can see the following information at present:

- FPS
- Position
- Heading/Direction
- Biome
- Lighting (client and server)
- Server Tick Performance
- Targeted block information

There will be more added over time.

You can hide and show the HUD by pressing H. This is changeable in the normal Control settings under Miscellaneous.

You can access the HUD settings by holding control and pressing the hotkey (again, H by default). The settings will allow you to choose which lines are visible and in what order they appear.

At present, the location is fixed. The plan would be to allow this to be customized in time, along with adding more choices, as mentioned above.

If the settings screen is not your bag, you can also use the `infohud` client-side command to add/remove or move info lines. I used this for testing before I had a UI written.

This is mostly written in **Kotlin**. As such, you must be sure to install the "Fabric Language Kotlin" mod for this to work.

## Optional Server-based Info
There are 2 server-based info lines that can be shown: Server light and Server tick performance. For these to be functional, you must have InfoHUD installed on the server as well. If you don't, you'll simply get no information from these. Single player (integrated server) works fine.

The tick performance shows ticks per second (TPS) and milliseconds per tick (MSPT). These are computed on the server. If you have the carpet mod installed, these will still work correctly. You may notice a bit of drift (sometime you will see 21 TPS, etc.), but it is fairly accurate.

While you can see some info on the "server light" line even without the mod being installed on the server, the key piece of info (darkness) needs to come from the server as it is not replicated to the client. With this info, you will get accurate light levels when it gets dark, etc. You can use this to find out the real effective light level for trying to ensure that mobs will spawn in a farm, etc.

Remember that showing this info does tax the server, even if only a little bit, so you may want to use this sparingly. When they are not being used by any client, the server cost is pretty much zero.
