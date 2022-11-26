/*
 * Copyright (c) 2019, Jos <Malevolentdev@gmail.com>
 * Copyright (c) 2019, Rheon <https://github.com/Rheon-D>
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

import net.runelite.api.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.TextComponent;

import javax.inject.Inject;
import java.awt.Point;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;

class InventoryTotalOverlay extends Overlay
{
	private static final int COINS = ItemID.COINS_995;

	private final Client client;
	private final ItemManager itemManager;
	private final InventoryTotalConfig config;

	@Inject
	private InventoryTotalOverlay(Client client, ItemManager itemManager, InventoryTotalConfig config)
	{
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_WIDGETS);

		this.itemManager = itemManager;
		this.client = client;
		this.config = config;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		final Widget bankWidget = client.getWidget(WidgetInfo.BANK_ITEM_CONTAINER);
		final Widget inventoryWidget = client.getWidget(WidgetInfo.INVENTORY);
		final ItemContainer itemContainer = client.getItemContainer(InventoryID.INVENTORY);

		if ((inventoryWidget == null || inventoryWidget.isHidden() && (bankWidget == null || bankWidget.isHidden())) || itemContainer == null)
		{
			return null;
		}

		final Item[] items = itemContainer.getItems();

		int total = 0;

		for (int i = 0; i < items.length; i++)
		{
			Item item = items[i];
			int itemId = item.getId();

			final ItemComposition itemComposition = itemManager.getItemComposition(itemId);

			final boolean isNoted = itemComposition.getNote() != -1;
			final int realItemId = isNoted ? itemComposition.getLinkedNoteId() : itemId;

			int gePrice;

			if (realItemId == COINS)
			{
				gePrice = item.getQuantity();
			}
			else
			{
				int qty = item.getQuantity();
				gePrice = qty * itemManager.getItemPrice(realItemId);
			}

			total += gePrice;
		}

		final TextComponent textComponent = new TextComponent();

		textComponent.setText(String.valueOf(total));
		textComponent.setPosition(new Point(0, 0));
		textComponent.render(graphics);

		int width = 120;
		int height = 20;

		String totalText = NumberFormat.getInstance(Locale.ENGLISH).format(total) + " gp";

		renderTotal(config, graphics, totalText, (inventoryWidget.getCanvasLocation().getX() + inventoryWidget.getWidth() / 2) - (width / 2), inventoryWidget.getCanvasLocation().getY() - height - 10, width, height);

		return null;
	}

	private void renderTotal(InventoryTotalConfig config, Graphics2D graphics, String text, int x, int y, int width, int height)
	{
		graphics.setColor(new Color(0, 0, 0, 255));
		graphics.drawRoundRect(x, y, width, height, 10, 10);

		graphics.setColor(new Color(38, 135, 54, 150));
		graphics.fillRoundRect(x, y, width, height, 10, 10);

		graphics.setFont(FontManager.getRunescapeSmallFont());
		final int totalWidth = graphics.getFontMetrics().stringWidth(text);
		final int centerX = (width / 2) - (totalWidth / 2);

		final TextComponent textComponent = new TextComponent();
		textComponent.setText(text);
		textComponent.setPosition(new Point(x + centerX, y + 16));
		textComponent.render(graphics);
	}
}
