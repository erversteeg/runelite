package net.runelite.client.plugins.inventorytotal;

import java.util.HashMap;
import java.util.Map;

public class InventoryTotalRunData {
    long profitLossInitialGp = 0;

    // static item prices so that when ItemManager updates, the Profit / Loss value doesn't all of a sudden change
    // this is cleared and repopulated at the start off each new run (after bank) and whenever new items hit the inventory
    Map<Integer, Integer> itemPrices = new HashMap<>();
}
