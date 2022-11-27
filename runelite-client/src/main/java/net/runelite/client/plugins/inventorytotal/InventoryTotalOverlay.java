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
	private static final int CORNER_RADIUS = 10;

	private static final int TEXT_Y_OFFSET = 16;

	private final Client client;
	private final InventoryTotalPlugin plugin;
	private final InventoryTotalConfig config;

	@Inject
	private InventoryTotalOverlay(Client client, InventoryTotalPlugin plugin, InventoryTotalConfig config)
	{
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_WIDGETS);

		this.client = client;
		this.plugin = plugin;
		this.config = config;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		Widget inventoryWidget = plugin.getInventoryWidget();
		ItemContainer itemContainer = plugin.getItemContainer();

		boolean isInvHidden = inventoryWidget == null || inventoryWidget.isHidden();
		if (isInvHidden || itemContainer == null)
		{
			return null;
		}

		final TextComponent textComponent = new TextComponent();

		textComponent.setText(String.valueOf(plugin.getProfitGp()));
		textComponent.setPosition(new Point(0, 0));
		textComponent.render(graphics);

		int width = 120;
		int height = 20;

		String totalText = NumberFormat.getInstance(Locale.ENGLISH).format(plugin.getProfitGp()) + " gp";

		renderTotal(config, graphics, plugin, inventoryWidget, plugin.getProfitGp(), plugin.getTotalQty(), totalText, width, height);

		return null;
	}

	private void renderTotal(InventoryTotalConfig config, Graphics2D graphics, InventoryTotalPlugin plugin, Widget inventoryWidget, int profitGp, int totalQty, String text, int fixedWidth, int fixedHeight)
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

		Color backgroundColor = config.bankColor();

		if (plugin.getState() == InventoryTotalState.FARM)
		{
			if (profitGp >= 0)
			{
				backgroundColor = config.profitColor();
			}
			else
			{
				backgroundColor = config.lossColor();
			}
		}

		int containerAlpha = backgroundColor.getAlpha();

		if (containerAlpha > 0 && config.showBorder()) {
			graphics.setColor(config.borderColor());
			graphics.drawRoundRect(x, y, width, height, cornerRadius, cornerRadius);
		}

		graphics.setColor(backgroundColor);

		graphics.fillRoundRect(x, y, width, height, cornerRadius, cornerRadius);

		final int centerX = (width / 2) - (textWidth / 2);

		final TextComponent textComponent = new TextComponent();

		textComponent.setColor(config.textColor());
		textComponent.setText(text);
		textComponent.setPosition(new Point(x + centerX, y + TEXT_Y_OFFSET));
		textComponent.render(graphics);
	}
}
