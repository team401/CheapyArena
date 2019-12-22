# CheapyArena
Brought to you by those who steal code from those who write Cheesy Arena, Team 401 presents Cheapy Arena!

Cheapy Arena is our custom FMS implementation for use at Week 0, offseason, scrimmage, and unofficial game events

Some notable features:
* No scorekeeping - Cheapy Arena is only designed to run matches, and doesn't enforce any particular scoring system.
* Dynamic match configurations - Supports 1v1 up to 3v3 alliances, with support for unbalanced alliances (1v2, 1v3, etc.)
* Customizable match phase times - allows limiting or entirely disabling auto, and setting custom teleop lengths.
* Implements standard FMS protocol - no custom driver station software required for teams
* Supports standard FMS features - team e-stops, match aborting, etc.
* Uses off the shelf, cost effective Ubiquiti networking hardware
    * EdgeRouter X as the primary field router
    * EdgeSwitch 10X as the alliance station swtich (2x)
    * UniFi AP-AC-Lite as the field access point
    * All networking hardware is automatically configured by Cheapy Arena for each match (takes about 10 seconds)
    * Team VLANs automatically configured for security (prevents one team's robot from communicating with another)
    * Fully compatible with the FIRST Radio Kiosk software (the one used at events, not at home.  Ask an FTA for it)
    * Custom Radio Kiosk provided if you can't get a copy of the official one
