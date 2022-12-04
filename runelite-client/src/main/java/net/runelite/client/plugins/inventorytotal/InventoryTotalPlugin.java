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
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.time.Instant;
import java.util.*;

@PluginDescriptor(
	name = "Inventory Total",
	description = "Shows the total GE value of items in your inventory.",
	enabledByDefault = false
)

public class InventoryTotalPlugin extends Plugin
{
	static final int COINS = ItemID.COINS_995;
	private static final int BANK_CLOSE_DELAY = 1200;
	static final int TOTAL_GP_INDEX = 0;
	static final int TOTAL_QTY_INDEX = 1;
	static final int NO_PROFIT_LOSS_TIME = -1;

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

	private long totalGp = 0;
	private long totalQty = 0;

	private Instant runStartTime;

	private Map<Integer, Integer> itemPrices = new HashMap<>();

	private long profitLossInitialGp = 0;
	private long initialGp = 0;

	private InventoryTotalMode mode = InventoryTotalMode.TOTAL;

	private InventoryTotalState state = InventoryTotalState.NONE;
	private InventoryTotalState prevState = InventoryTotalState.NONE;

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

	void onNewRun()
	{
		overlay.hide();

		runStartTime = Instant.now();

		// to handle same tick bank closing
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				profitLossInitialGp = totalGp;
				if (mode == InventoryTotalMode.TOTAL)
				{
					profitLossInitialGp += getEquipmentTotal();
				}

				if (mode == InventoryTotalMode.PROFIT_LOSS)
				{
					initialGp = profitLossInitialGp;
				}
				else
				{
					initialGp = 0;
				}

				overlay.show();
			}
		}, BANK_CLOSE_DELAY);
	}

	void onBank()
	{
		runStartTime = null;
		profitLossInitialGp = 0;
		initialGp = 0;
	}

	@Provides
	InventoryTotalConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(InventoryTotalConfig.class);
	}

	int [] getInventoryTotals()
	{
		final ItemContainer itemContainer = overlay.getInventoryItemContainer();

		if (itemContainer == null)
		{
			return new int [2];
		}

		final Item[] items = itemContainer.getItems();

		final LinkedList<Item> allItems = new LinkedList<>(Arrays.asList(items));

		int totalQty = 0;
		int totalGp = 0;

		for (Item item: allItems)
		{
			int itemId = item.getId();

			final ItemComposition itemComposition = itemManager.getItemComposition(itemId);

			final boolean isNoted = itemComposition.getNote() != -1;
			final int realItemId = isNoted ? itemComposition.getLinkedNoteId() : itemId;

			int totalPrice;
			int gePrice;

			if (itemPrices.containsKey(realItemId))
			{
				gePrice = itemPrices.get(realItemId);
			}
			else
			{
				gePrice = itemManager.getItemPrice(realItemId);
			}

			int itemQty = item.getQuantity();

			if (realItemId == COINS)
			{
				totalPrice = itemQty;
			}
			else
			{
				totalPrice = itemQty * gePrice;
			}

			totalGp += totalPrice;
			totalQty += itemQty;

			if (realItemId != COINS && !itemPrices.containsKey(realItemId))
			{
				itemPrices.put(realItemId, gePrice);
			}
		}

		int[] totals = new int[2];

		totals[TOTAL_GP_INDEX] = totalGp;
		totals[TOTAL_QTY_INDEX] = totalQty;

		return totals;
	}

	int getEquipmentTotal()
	{
		ItemContainer itemContainer = overlay.getEquipmentItemContainer();

		Item ring = itemContainer.getItem(EquipmentInventorySlot.RING.getSlotIdx());
		Item ammo = itemContainer.getItem(EquipmentInventorySlot.AMMO.getSlotIdx());

		Player player = client.getLocalPlayer();

		int [] ids = player.getPlayerComposition().getEquipmentIds();

		LinkedList<Integer> eIds = new LinkedList<>();

		for (int id: ids)
		{
			if (id < 512)
			{
				continue;
			}

			eIds.add(id - 512);
		}

		if (ring != null)
		{
			eIds.add(ring.getId());
		}

		if (ammo != null)
		{
			eIds.add(ammo.getId());
		}

		int eTotal = 0;
		for (int itemId: eIds)
		{
			int qty = 1;
			if (ammo != null && itemId == ammo.getId())
			{
				qty = ammo.getQuantity();
			}

			int gePrice;

			if (itemPrices.containsKey(itemId))
			{
				gePrice = itemPrices.get(itemId);
			}
			else
			{
				gePrice = itemManager.getItemPrice(itemId);
			}

			int totalPrice = qty * gePrice;

			eTotal += totalPrice;

			if (!itemPrices.containsKey(itemId))
			{
				itemPrices.put(itemId, gePrice);
			}
		}

		return eTotal;
	}

	void setMode(InventoryTotalMode mode)
	{
		this.mode = mode;

		switch(mode)
		{
			case TOTAL:
				initialGp = 0;
				break;
			case PROFIT_LOSS:
				initialGp = profitLossInitialGp;
				break;
		}
	}

	public InventoryTotalMode getMode()
	{
		return mode;
	}

	void setState(InventoryTotalState state)
	{
		this.prevState = this.state;
		this.state = state;
	}

	public InventoryTotalState getState()
	{
		return state;
	}

	public InventoryTotalState getPreviousState()
	{
		return prevState;
	}

	public long getProfitGp()
	{
		return totalGp - initialGp;
	}

	void setTotalGp(long totalGp)
	{
		this.totalGp = totalGp;
	}

	void setTotalQty(long totalQty)
	{
		this.totalQty = totalQty;
	}

	public long getTotalQty()
	{
		return totalQty;
	}

	public Map<Integer, Integer> getItemPrices()
	{
		return itemPrices;
	}

	long elapsedRunTime()
	{
		if (runStartTime == null || mode == InventoryTotalMode.TOTAL || !config.showProfitLossTime())
		{
			return NO_PROFIT_LOSS_TIME;
		}

		return Instant
				.now()
				.minusMillis(runStartTime.toEpochMilli())
				.toEpochMilli();
	}
}
