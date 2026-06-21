plugins {
	id("dev.kikugie.stonecutter")
}
stonecutter active "26.2"

stonecutter parameters {
	replacements {
		string(current.parsed.matches("<26.2")) {
			replace("DYE.white()", "WHITE_DYE")
		}
	}
}
