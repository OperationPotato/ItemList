plugins {
	id("dev.kikugie.stonecutter")
}
stonecutter active "26.3"

stonecutter parameters {
	if (sc.current?.version?.startsWith("26.3") == true) {
		swaps["minecraft"] = "26.3"
	}
}
