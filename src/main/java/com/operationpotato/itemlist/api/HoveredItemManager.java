package com.operationpotato.itemlist.api;

public interface HoveredItemManager {
	void addProvider(HoveredItemProvider provider);

	void addConsumer(HoveredItemConsumer consumer);
}
