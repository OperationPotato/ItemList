plugins {
	id("dev.kikugie.stonecutter")
}
stonecutter active "26.2"

stonecutter parameters {
	replacements {
		string(current.parsed.matches("<26.2")) {
			replace("McClient.gui.hud.font", "McClient.gui.font")
			replace("DYE.white()", "WHITE_DYE")
		}
	}
}
