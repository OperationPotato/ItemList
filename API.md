# Plugin API

The Plugin API allows our mod to be more compatible with yours, and your mod to be more compatible with ours, with just one simple class!

## Maven

Add the OperationPotato Maven repository:

```groovy
maven {
	url = "https://maven.operationpotato.com/releases"

	content {
		includeGroup "com.operationpotato"
	}
}
```

## Usage

1. Add to your dependencies:
    ```groovy
    compileOnly "com.operationpotato:skyblock-item-list-api:{VERSION}"
    ```
	* You can find the latest version number on the
	  Maven [here](https://maven.operationpotato.com/#/releases/com/operationpotato/skyblock-item-list-api)
2. Create a class that implements
   the [Plugin API](https://github.com/OperationPotato/ItemList/blob/1279215236138afe11fe32029bfabef94545c93f/src/main/java/com/operationpotato/itemlist/api/Plugin.java)
	* `registerExclusionZones`:
		* Use **Exclusion Zones** to prevent items from being placed on your screen widgets.
	* `registerExcludedScreens`:
		* Use **Excluded Screens** to automatically close the item list on your custom screens.
	* `registerHoveredItems`:
		* Provide **Hovered Item** to allow recipes, usages, favorite-ing, and more on them.
		* Consume **Hovered Item** key events.
	* `registerRecipeButtons`
		* Add custom **Recipe Buttons** to let users do things from recipe screens.
        * Please add a Config option if you do this!
3. Add an entrypoint to your class in your `fabric.mod.json`
	* If your plugin is written in Java:
    ```json
	  "skyblock-item-list": [
	    "your.java.class.here"
	  ]
    ```
	* If your plugin is written in Kotlin, you will need to specify the language adapter:
   ```json
   "skyblock-item-list": [
     {
       "adapter": "kotlin",
       "value": "your.kotlin.class.here"
     }
   ]
   ```
4. Try it in-game and report any API issues [here](https://github.com/OperationPotato/ItemList/issues)!
