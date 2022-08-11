# InfoHUD

This mod displays information you would only normally see on your F3 screen at all times in the upper left/right corner. You can see the following information at present:

![HUD Example Image](https://i.imgur.com/AZulOnB.png)

- FPS
- Position
- Heading/Direction
- Biome
- Lighting (client and server)
- Server tick performance
- Targeted block information
- Local difficulty
- Mood (when this reaches 100%, an ambient sound plays)
- Elytra status (if this is all you care about)
- Armor status (if you care about all armor)
- Speed, in m/s
- Game clock - simulated in-world time when in the Overworld, hidden otherwise

You can hide and show the HUD by pressing H. This is changeable in the normal Control settings under Miscellaneous.

You can access the HUD settings by holding control and pressing the hotkey (again, H by default). The settings will allow you to choose:
- HUD scale
- HUD position
- Which lines are visible and in what order they appear

## Dependencies
- Fabric Language Kotlin, as this is mostly written in Kotlin
- Fabric API

## Notes
- Armor and Elytra lines display the percentages in the same colors as the bars that show remaining durability in the UI
- Targeted block information lines will hide if nothing is currently in range to target
- Elytra information hides if you are not wearing an Elytra
- Game clock hides if you are not in the Overworld

### Optional Server-based Info
There are 2 server-based info lines that can be shown: Server light and Server tick performance. For these to be functional, you must have InfoHUD installed on the server as well. If you don't, you'll simply get no information from these. Single player (integrated server) works fine.

The tick performance shows ticks per second (TPS) and milliseconds per tick (MSPT). These are computed on the server. If you have the carpet mod installed, these will still work correctly. You may notice a bit of drift (sometime you will see 21 TPS, etc.), but it is fairly accurate.

While you can see some info on the "server light" line even without the mod being installed on the server, the key piece of info (darkness) needs to come from the server as it is not replicated to the client. With this info, you will get accurate light levels when it gets dark, etc. You can use this to find out the real effective light level for trying to ensure that mobs will spawn in a farm, etc.

Remember that showing this info does tax the server, even if only a little, so you may want to use this sparingly. When they are not being used by any client, the server cost is pretty much zero.
