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

import com.google.gson.Gson;
import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.RuneScapeProfileChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

@PluginDescriptor(
	name = "Inventory Total",
	description = "Shows the total GE value of all items in your inventory.",
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

	@Inject
	private ConfigManager configManager;

	@Inject
	private Gson gson;

	private String profileKey = "";

	private InventoryTotalRunData runData;

	private InventoryTotalMode mode = InventoryTotalMode.TOTAL;

	private InventoryTotalState state = InventoryTotalState.NONE;
	private InventoryTotalState prevState = InventoryTotalState.NONE;

	private long totalGp = 0;
	private long totalQty = 0;

	private long initialGp = 0;

	private long runStartTime = 0;

	private long lastWriteSaveTime = 0;

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(overlay);

		runData = new InventoryTotalRunData();
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay);
	}

	@Subscribe
	public void onRuneScapeProfileChanged(RuneScapeProfileChanged e)
	{
		profileKey = configManager.getRSProfileKey();
		if (profileKey != null)
		{
			runData = getSavedData();
		}
	}

	@Provides
	InventoryTotalConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(InventoryTotalConfig.class);
	}

	void onNewRun()
	{
		overlay.showInterstitial();

		runStartTime = Instant.now().toEpochMilli();

		// to handle same tick bank closing
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				runData.profitLossInitialGp = totalGp;

				if (mode == InventoryTotalMode.TOTAL)
				{
					runData.profitLossInitialGp += getEquipmentTotal();
				}

				if (mode == InventoryTotalMode.PROFIT_LOSS)
				{
					initialGp = runData.profitLossInitialGp;
				}
				else
				{
					initialGp = 0;
				}

				writeSavedData();

				overlay.hideInterstitial();
			}
		}, BANK_CLOSE_DELAY);
	}

	void onBank()
	{
		runData.profitLossInitialGp = 0;
		runData.itemPrices.clear();

		initialGp = 0;
		runStartTime = 0;

		writeSavedData();
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

			if (runData.itemPrices.containsKey(realItemId))
			{
				gePrice = runData.itemPrices.get(realItemId);
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

			if (realItemId != COINS && !runData.itemPrices.containsKey(realItemId))
			{
				runData.itemPrices.put(realItemId, gePrice);
				writeSavedData();
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

			if (runData.itemPrices.containsKey(itemId))
			{
				gePrice = runData.itemPrices.get(itemId);
			}
			else
			{
				gePrice = itemManager.getItemPrice(itemId);
			}

			int totalPrice = qty * gePrice;

			eTotal += totalPrice;

			if (!runData.itemPrices.containsKey(itemId))
			{
				runData.itemPrices.put(itemId, gePrice);
				writeSavedData();
			}
		}

		return eTotal;
	}

	// max invoke rate approximately once per tick
	// mainly so that initially this isn't getting invoked multiple times after item prices are added to the map
	void writeSavedData()
	{
		if (state == InventoryTotalState.BANK || Instant.now().toEpochMilli() - lastWriteSaveTime < 600)
		{
			return;
		}

		String profile = configManager.getRSProfileKey();

		String json = gson.toJson(runData);
		configManager.setConfiguration(InventoryTotalConfig.GROUP, profile, "inventory_total_data", json);

		lastWriteSaveTime = Instant.now().toEpochMilli();
	}

	private InventoryTotalRunData getSavedData()
	{
		String profile = configManager.getRSProfileKey();
		String json = configManager.getConfiguration(InventoryTotalConfig.GROUP, profile, "inventory_total_data");

		InventoryTotalRunData savedData = gson.fromJson(json, InventoryTotalRunData.class);

		if (savedData == null)
		{
			return new InventoryTotalRunData();
		}
		return savedData;
	}

	long elapsedRunTime()
	{
		if (runStartTime == 0 || mode == InventoryTotalMode.TOTAL || !config.showRunTime())
		{
			return NO_PROFIT_LOSS_TIME;
		}

		return Instant
				.now()
				.minusMillis(runStartTime)
				.toEpochMilli();
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
				initialGp = runData.profitLossInitialGp;
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

	public long getTotalGp()
	{
		return totalGp;
	}

	void setTotalQty(long totalQty)
	{
		this.totalQty = totalQty;
	}

	public long getTotalQty()
	{
		return totalQty;
	}
}
