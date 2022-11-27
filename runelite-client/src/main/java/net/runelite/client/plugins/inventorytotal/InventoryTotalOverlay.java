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
	private static final int INVENTORY_GAP_OFFSET = 6;
	private static final String PROFIT_LOSS_TIME_FORMAT = "%02d:%02d:%02d";
	private static final String PROFIT_LOSS_TIME_NO_HOURS_FORMAT = "%02d:%02d";

	private final Client client;
	private final InventoryTotalPlugin plugin;
	private final InventoryTotalConfig config;

	private Widget inventoryWidget;
	private ItemContainer itemContainer;

	@Inject
	private InventoryTotalOverlay(Client client, InventoryTotalPlugin plugin, InventoryTotalConfig config)
	{
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_WIDGETS);

		this.client = client;
		this.plugin = plugin;
		this.config = config;
	}

	void updatePluginState()
	{
		if (config.enableProfitLoss())
		{
			plugin.setMode(InventoryTotalMode.PROFIT_LOSS);
		}
		else
		{
			plugin.setMode(InventoryTotalMode.TOTAL);
		}

		int [] totals = plugin.getTotals();

		plugin.setTotalGp(totals[InventoryTotalPlugin.TOTAL_GP_INDEX]);
		plugin.setTotalQty(totals[InventoryTotalPlugin.TOTAL_QTY_INDEX]);

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

		if (isBank)
		{
			plugin.setState(InventoryTotalState.BANK);
		}
		else
		{
			plugin.setState(InventoryTotalState.RUN);
		}

		if (plugin.getPreviousState() == InventoryTotalState.BANK && plugin.getState() == InventoryTotalState.RUN)
		{
			plugin.onNewRun();
		}
		else if (plugin.getPreviousState() == InventoryTotalState.RUN && plugin.getState() == InventoryTotalState.BANK)
		{
			plugin.onBank();
		}

		itemContainer = client.getItemContainer(InventoryID.INVENTORY);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		updatePluginState();

		boolean isInvHidden = inventoryWidget == null || inventoryWidget.isHidden();
		if (isInvHidden || itemContainer == null)
		{
			return null;
		}

		int height = 20;

		String totalText;
		if (config.showExactGp())
		{
			totalText = getExactFormattedGp();
		}
		else
		{
			totalText = getFormattedGp();
			totalText = totalText.replace(".0", "");
		}

		if (config.showGpUnit())
		{
			totalText += " gp";
		}

		String formattedRunTime = getFormattedRunTime();
		String runTimeText = null;

		if (formattedRunTime != null)
		{
			runTimeText = " (" + formattedRunTime + ")";
		}

		renderTotal(config, graphics, plugin, inventoryWidget, plugin.getProfitGp(),
				plugin.getTotalQty(), totalText, runTimeText, height);

		return null;
	}

	private void renderTotal(InventoryTotalConfig config, Graphics2D graphics, InventoryTotalPlugin plugin,
							 Widget inventoryWidget, long profitGp, long totalQty, String totalText,
							 String runTimeText, int height)
	{
		if (totalQty == 0 && !config.showOnEmpty())
		{
			return;
		}

		graphics.setFont(FontManager.getRunescapeSmallFont());
		final int totalWidth = graphics.getFontMetrics().stringWidth(totalText);

		int fixedRunTimeWidth = 0;
		int actualRunTimeWidth = 0;

		if (runTimeText != null && runTimeText.length() >= 2)
		{
			fixedRunTimeWidth = 5 * (runTimeText.length() - 2) + (3 * 2) + 5;
			actualRunTimeWidth = graphics.getFontMetrics().stringWidth(runTimeText);
		}

		int width = totalWidth + fixedRunTimeWidth + 20;

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

		int y = inventoryWidget.getCanvasLocation().getY() - height - config.inventoryGap() + INVENTORY_GAP_OFFSET;

		Color backgroundColor = config.totalColor();

		if (plugin.getState() == InventoryTotalState.RUN && plugin.getMode() == InventoryTotalMode.PROFIT_LOSS)
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

		int opacity = 150;
		if (config.opaqueBackground())
		{
			opacity = 255;
		}

		backgroundColor = new Color(backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue(), opacity);

		int cornerRadius = CORNER_RADIUS;
		if (!config.roundCorners())
		{
			cornerRadius = 0;
		}

		int containerAlpha = backgroundColor.getAlpha();

		if (containerAlpha > 0) {
			graphics.setColor(Color.BLACK);
			graphics.drawRoundRect(x, y, width, height, cornerRadius, cornerRadius);
		}

		graphics.setColor(backgroundColor);

		graphics.fillRoundRect(x, y, width, height, cornerRadius, cornerRadius);

		TextComponent textComponent = new TextComponent();

		textComponent.setColor(config.totalGpColor());
		textComponent.setText(totalText);
		textComponent.setPosition(new Point(x + 10, y + TEXT_Y_OFFSET));
		textComponent.render(graphics);

		if (runTimeText != null)
		{
			textComponent = new TextComponent();

			textComponent.setColor(config.runTimeColor());
			textComponent.setText(runTimeText);
			textComponent.setPosition(new Point((x + width) - 10 - actualRunTimeWidth, y + TEXT_Y_OFFSET));
			textComponent.render(graphics);
		}
	}

	private String getFormattedGp()
	{
		long total = plugin.getProfitGp();

		if (total >= 1000000000 || total <= -1000000000)
		{
			double bTotal = total / 1000000000.0;
			return getTruncatedTotal(bTotal) + "B";
		}
		else
		{
			if (total >= 1000000 || total <= -1000000)
			{
				double mTotal = total / 1000000.0;
				return getTruncatedTotal(mTotal) + "M";
			}
			else
			{
				if (total >= 1000 || total <= -1000)
				{
					double kTotal = total / 1000.0;
					return getTruncatedTotal(kTotal) + "K";
				}
				else
				{
					return getExactFormattedGp();
				}
			}
		}
	}

	private String getTruncatedTotal(double total)
	{
		String totalString = Double.toString(total);

		int dotIndex = totalString.indexOf('.');
		if (dotIndex < totalString.length() - 1)
		{
			return totalString.substring(0, dotIndex + 2);
		}

		return totalString;
	}

	private String getExactFormattedGp()
	{
		return NumberFormat.getInstance(Locale.ENGLISH).format(plugin.getProfitGp());
	}

	private String getFormattedRunTime()
	{
		long profitLossTime = plugin.elapsedProfitLossTime();
		if (profitLossTime == InventoryTotalPlugin.NO_PROFIT_LOSS_TIME)
		{
			return null;
		}

		long totalSecs = profitLossTime / 1000;
		long totalMins = totalSecs / 60;

		long hrs = totalMins / 60;
		long mins = totalMins % 60;
		long secs = totalSecs % 60;

		if (hrs > 0)
		{
			return String.format(PROFIT_LOSS_TIME_FORMAT, hrs, mins, secs);
		}
		else
		{
			return String.format(PROFIT_LOSS_TIME_NO_HOURS_FORMAT, mins, secs);
		}
	}

	public ItemContainer getItemContainer()
	{
		return itemContainer;
	}
}
