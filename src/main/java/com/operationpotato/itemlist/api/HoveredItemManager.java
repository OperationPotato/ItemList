package com.operationpotato.itemlist.api;

public interface HoveredItemManager {
	void addProvider(HoveredItemProvider provider);

	@Deprecated(since = "0.0.17")
	void addConsumer(HoveredItemConsumer consumer);

	void addConsumer(HoveredItemInputConsumer consumer);
}
