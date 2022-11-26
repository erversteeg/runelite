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
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.Point;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;

class InventoryTotalOverlay extends Overlay
{
	private static final int COINS = ItemID.COINS_995;

	private static final int CORNER_RADIUS = 10;

	private static final int TEXT_Y_OFFSET = 16;

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
		Widget inventoryWidget = client.getWidget(WidgetInfo.INVENTORY);
		if (inventoryWidget == null || inventoryWidget.getCanvasLocation().getX() < 0 || inventoryWidget.isHidden())
		{
			inventoryWidget = client.getWidget(WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER);
		}

		final ItemContainer itemContainer = client.getItemContainer(InventoryID.INVENTORY);

		boolean isInvHidden = inventoryWidget == null || inventoryWidget.isHidden();
		if (isInvHidden || itemContainer == null)
		{
			return null;
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

		final TextComponent textComponent = new TextComponent();

		textComponent.setText(String.valueOf(totalGp));
		textComponent.setPosition(new Point(0, 0));
		textComponent.render(graphics);

		int width = 120;
		int height = 20;

		String totalText = NumberFormat.getInstance(Locale.ENGLISH).format(totalGp) + " gp";

		renderTotal(config, graphics, inventoryWidget, totalQty, totalText, width, height);

		return null;
	}

	private void renderTotal(InventoryTotalConfig config, Graphics2D graphics, Widget inventoryWidget, int totalQty, String text, int fixedWidth, int fixedHeight)
	{
		if (totalQty == 0 && !config.showOnEmpty())
		{
			return;
		}

		graphics.setFont(FontManager.getRunescapeSmallFont());
		final int textWidth = graphics.getFontMetrics().stringWidth(text);

		int width = fixedWidth;
		int height = fixedHeight;

		if (!config.isFixedSize())
		{
			width = textWidth + 20;
		}

		int x = (inventoryWidget.getCanvasLocation().getX() + inventoryWidget.getWidth() / 2) - (width / 2);
		switch (config.horizontalAlignment())
		{
			case CENTER:
				break;

			case LEFT:
				x = inventoryWidget.getCanvasLocation().getX();
				break;

			case RIGHT:
				x = inventoryWidget.getCanvasLocation().getX() + inventoryWidget.getWidth() - width;
				break;
		}

		int y = inventoryWidget.getCanvasLocation().getY() - height - config.inventoryGap();

		int cornerRadius = 0;
		if (config.hasRoundedCorners())
		{
			cornerRadius = CORNER_RADIUS;
		}

		int containerAlpha = config.backgroundColor().getAlpha();

		if (containerAlpha > 0 && config.showBorder()) {
			graphics.setColor(config.borderColor());
			graphics.drawRoundRect(x, y, width, height, cornerRadius, cornerRadius);
		}

		graphics.setColor(config.backgroundColor());
		graphics.fillRoundRect(x, y, width, height, cornerRadius, cornerRadius);

		final int centerX = (width / 2) - (textWidth / 2);

		final TextComponent textComponent = new TextComponent();

		textComponent.setColor(config.textColor());
		textComponent.setText(text);
		textComponent.setPosition(new Point(x + centerX, y + TEXT_Y_OFFSET));
		textComponent.render(graphics);
	}
}
