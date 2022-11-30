/*
 * Copyright (c) 2019, Jos <Malevolentdev@gmail.com>
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

import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.*;

@ConfigGroup(InventoryTotalConfig.GROUP)
public interface InventoryTotalConfig extends Config
{
	String GROUP = "inventorytotal";

	@ConfigItem(
			keyName = "profitBackgroundColor",
			name = "Profit Color",
			description = "Configures the background color of the counter when gaining gp in Profit / Loss mode."
	)
	default Color profitColor()
	{
		return Color.decode("#42834C");
	}

	@ConfigItem(
			keyName = "lossBackgroundColor",
			name = "Loss Color",
			description = "Configures the background color of the counter when losing gp in Profit / Loss mode."
	)
	default Color lossColor()
	{
		return Color.decode("#803442");
	}

	@ConfigItem(
			keyName = "bankBackgroundColor",
			name = "Total Color",
			description = "Configures the background color of the counter when the inventory total is displayed."
	)
	default Color totalColor()
	{
		return Color.decode("#938E5C");
	}

	@ConfigItem(
			keyName = "opaqueBackground",
			name = "Opaque Background",
			description = "Configures whether or not the background is opaque."
	)
	default boolean opaqueBackground()
	{
		return false;
	}

	@ConfigItem(
			keyName = "roundedCorners",
			name = "Rounded Corners",
			description = "Configures whether or not the total has rounded corners."
	)
	default boolean roundCorners()
	{
		return false;
	}

	@ConfigItem(
			keyName = "showWhenEmpty",
			name = "Show On Empty",
			description = "Configures whether or not the total is shown when there are no items in the inventory."
	)
	default boolean showOnEmpty()
	{
		return false;
	}

	@ConfigItem(
			keyName = "inventoryOffset",
			name = "Inventory Offset",
			description = "Configures where the total is positioned relative to the inventory."
	)
	default int inventoryGap()
	{
		return 18;
	}

	@ConfigItem(
			keyName = "horizontalAlignment",
			name = "Alignment",
			description = "Configures whether or not the container has rounded corners."
	)
	default InventoryTotalAlignment horizontalAlignment()
	{
		return InventoryTotalAlignment.RIGHT;
	}

	@ConfigItem(
			keyName = "enableProfitLoss",
			name = "Profit / Loss",
			description = "Configures whether or not the total runs in profit / loss."
	)
	default boolean enableProfitLoss()
	{
		return false;
	}

	@ConfigItem(
			keyName = "showProfitLossTime",
			name = "Show Profit / Loss Timer",
			description = "Configures whether or not the profit / loss timer is displayed."
	)
	default boolean showProfitLossTime()
	{
		return false;
	}

	@ConfigItem(
			keyName = "showExactGp",
			name = "Show Exact Gp",
			description = "Configures whether or not the exact gp amount is displayed."
	)
	default boolean showExactGp()
	{
		return false;
	}

	@ConfigItem(
			keyName = "showGpUnit",
			name = "Show Gp Unit",
			description = "Configures whether or not to display the gp unit for exact totals."
	)
	default boolean showGpUnit()
	{
		return true;
	}
}
