# InfoHUD Changelog
## Version 1.4.2
- Update to work with 1.19.4

## Version 1.4.1
- Update to work with 1.19.3

## Version 1.4
- Added targeted entity
- Added Moon Phase info line
- Add real dependency on fabric-language-kotlin, so if it's missing it actually tells you that instead of giving you a mysterious crash.

## Version 1.3.1
- Fix issue with negative status effects rendering under the HUD when using top right position.

## Version 1.3
- Reorganized settings screen to allow the following additions.
- Added ability to set HUD scale to whatever you like. Works similarly to the gui scale in video settings. "Auto" in our case means we follow whatever the gui scale is. The default is 2.
- Added ability to position HUD at top left (default) or top right.

## Version 1.2
- Added Game time, a clock that shows the current time in the game. Will not show in the Nether or End dimensions to match the way a normal clock item would.
- Added speed in m/s. Updates every 0.5 seconds.
- Changed sizing to effectively always use gui scale 2 except for gui scale 1 for now. This fixes some legibility issues. Eventually there may be a control in settings.

## Version 1.1
- Added local difficulty
- Added mood %
- Added Elytra status. This will hide if you are not wearing an Elytra.
- Added more general armor status for those that want status on ALL their armor pieces.
- Fixed incorrect localized string key for the hot key
- Major internal refactor to be more scalable and allow color
- Added unit tests for the settings component
- Added more info to the FPS line

## Version 1.0 
- Initial release