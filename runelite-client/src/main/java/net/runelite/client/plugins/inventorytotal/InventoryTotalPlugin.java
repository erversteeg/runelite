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
import java.time.Instant;

@PluginDescriptor(
	name = "Inventory Total",
	description = "Shows total value of items in the player's inventory.",
	enabledByDefault = false
)

public class InventoryTotalPlugin extends Plugin
{
	private static final int COINS = ItemID.COINS_995;
	static final int TOTAL_GP_INDEX = 0;
	static final int TOTAL_QTY_INDEX = 1;
	static final int NO_RUN_TIME = -1;

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

	private Instant runStartTime;

	private int initialGp = 0;

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
		runStartTime = Instant.now();
		initialGp = totalGp;
	}

	void onBank()
	{
		runStartTime = null;
		initialGp = 0;
	}

	@Provides
	InventoryTotalConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(InventoryTotalConfig.class);
	}

	int [] getTotals()
	{
		final ItemContainer itemContainer = overlay.getItemContainer();

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

	public int getProfitGp()
	{
		return totalGp - initialGp;
	}

	void setTotalGp(int totalGp)
	{
		this.totalGp = totalGp;
	}

	void setTotalQty(int totalQty)
	{
		this.totalQty = totalQty;
	}

	public int getTotalQty()
	{
		return totalQty;
	}

	long elapsedRunTime()
	{
		if (runStartTime == null || !config.showRunTime())
		{
			return NO_RUN_TIME;
		}

		long elapsedRunTime = Instant
				.now()
				.minusMillis(runStartTime.toEpochMilli())
				.toEpochMilli();

		long elapsedSecs = elapsedRunTime / 1000;
		int startThreshold = config.runTimeStartThreshold();

		if (elapsedSecs < startThreshold)
		{
			return NO_RUN_TIME;
		}

		return elapsedRunTime;
	}
}
