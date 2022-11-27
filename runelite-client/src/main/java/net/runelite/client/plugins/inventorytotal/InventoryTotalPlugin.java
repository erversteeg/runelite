/*
 * Copyright (c) 2018, Jos <Malevolentdev@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.inventorytotal;

import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;

@PluginDescriptor(
	name = "Inventory Total",
	description = "Shows total value of items in the player's inventory.",
	enabledByDefault = false
)

public class InventoryTotalPlugin extends Plugin
{
	private static final int COINS = ItemID.COINS_995;
	private static final int TOTAL_GP_INDEX = 0;
	private static final int TOTAL_QTY_INDEX = 1;

	@Inject
	private InventoryTotalOverlay overlay;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private Client client;

	@Inject
	private InventoryTotalConfig config;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ItemManager itemManager;

	private int totalGp = 0;
	private int totalQty = 0;

	private int initialGp = 0;

	private InventoryTotalState state = InventoryTotalState.NONE;
	private InventoryTotalState prevState = InventoryTotalState.NONE;

	private Widget inventoryWidget;
	private ItemContainer itemContainer;

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay);
	}

	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		updateState();
	}

	private void onNewFarm()
	{
		initialGp = totalGp;
	}

	private void onBank()
	{
		initialGp = 0;
	}

	@Provides
	InventoryTotalConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(InventoryTotalConfig.class);
	}

	private void updateState()
	{
		int [] totals = getTotals();

		totalGp = totals[TOTAL_GP_INDEX];
		totalQty = totals[TOTAL_QTY_INDEX];

		inventoryWidget = client.getWidget(WidgetInfo.INVENTORY);

		boolean isBank = false;

		if (inventoryWidget == null || inventoryWidget.getCanvasLocation().getX() < 0 || inventoryWidget.isHidden())
		{
			inventoryWidget = client.getWidget(WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER);
			if (inventoryWidget != null && !inventoryWidget.isHidden())
			{
				isBank = true;
			}
		}

		prevState = state;

		if (isBank)
		{
			state = InventoryTotalState.BANK;
		}
		else
		{
			state = InventoryTotalState.FARM;
		}

		if (prevState == InventoryTotalState.BANK && state == InventoryTotalState.FARM)
		{
			onNewFarm();
		}
		else if (prevState == InventoryTotalState.FARM && state == InventoryTotalState.BANK)
		{
			onBank();
		}

		itemContainer = client.getItemContainer(InventoryID.INVENTORY);
	}

	private int [] getTotals()
	{
		if (itemContainer == null)
		{
			return new int [2];
		}

		final Item[] items = itemContainer.getItems();

		int totalQty = 0;
		int totalGp = 0;

		for (int i = 0; i < items.length; i++)
		{
			Item item = items[i];
			int itemId = item.getId();

			final ItemComposition itemComposition = itemManager.getItemComposition(itemId);

			final boolean isNoted = itemComposition.getNote() != -1;
			final int realItemId = isNoted ? itemComposition.getLinkedNoteId() : itemId;

			int gePrice;

			int itemQty = item.getQuantity();

			if (realItemId == COINS)
			{
				gePrice = itemQty;
			}
			else
			{
				gePrice = itemQty * itemManager.getItemPrice(realItemId);
			}

			totalGp += gePrice;
			totalQty += itemQty;
		}

		int [] totals = new int[2];

		totals[TOTAL_GP_INDEX] = totalGp;
		totals[TOTAL_QTY_INDEX] = totalQty;

		return totals;
	}

	public InventoryTotalState getState()
	{
		return state;
	}

	public Widget getInventoryWidget()
	{
		return inventoryWidget;
	}

	public ItemContainer getItemContainer()
	{
		return itemContainer;
	}

	public int getProfitGp()
	{
		return totalGp - initialGp;
	}

	public int getTotalQty()
	{
		return totalQty;
	}
}
