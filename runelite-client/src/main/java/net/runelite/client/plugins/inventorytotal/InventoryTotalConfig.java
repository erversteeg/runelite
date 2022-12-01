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
			description = "Configures the background color of the counter box when gaining gp in Profit / Loss mode."
	)
	default Color profitColor()
	{
		return Color.decode("#42834C");
	}

	@ConfigItem(
			keyName = "lossBackgroundColor",
			name = "Loss Color",
			description = "Configures the background color of the counter box when losing gp in Profit / Loss mode."
	)
	default Color lossColor()
	{
		return Color.decode("#803442");
	}

	@ConfigItem(
			keyName = "totalBackgroundColor",
			name = "Total Color",
			description = "Configures the background color of the counter box when showing the inventory total."
	)
	default Color totalColor()
	{
		return Color.decode("#938E5C");
	}

	@ConfigItem(
			keyName = "opaqueBackground",
			name = "Opaque Background",
			description = "Configures whether or not the counter box background is opaque."
	)
	default boolean opaqueBackground()
	{
		return false;
	}

	@ConfigItem(
			keyName = "roundedCorners",
			name = "Rounded Corners",
			description = "Configures whether or not the counter box has round corners."
	)
	default boolean roundCorners()
	{
		return false;
	}

	@ConfigItem(
			keyName = "showWhenEmpty",
			name = "Show On Empty",
			description = "Configures whether or not the counter box is shown when there are no items in the inventory."
	)
	default boolean showOnEmpty()
	{
		return false;
	}

	@ConfigItem(
			keyName = "inventoryOffset",
			name = "Inventory Offset",
			description = "Configures where the counter box is located relative to the inventory."
	)
	default int inventoryGap()
	{
		return 18;
	}

	@ConfigItem(
			keyName = "horizontalAlignment",
			name = "Alignment",
			description = "Configures the alignment of the counter box."
	)
	default InventoryTotalAlignment horizontalAlignment()
	{
		return InventoryTotalAlignment.RIGHT;
	}

	@ConfigItem(
			keyName = "enableProfitLoss",
			name = "Profit / Loss",
			description = "Configures whether or not the counter runs in profit / loss or total mode."
	)
	default boolean enableProfitLoss()
	{
		return false;
	}

	@ConfigItem(
			keyName = "showProfitLossTime",
			name = "Show Timer",
			description = "Configures whether or not a profit / loss timer is shown in the counter box."
	)
	default boolean showProfitLossTime()
	{
		return false;
	}

	@ConfigItem(
			keyName = "showExactGp",
			name = "Show Exact Gp",
			description = "Configures whether or not the exact gp total is shown in the counter box."
	)
	default boolean showExactGp()
	{
		return false;
	}

	@ConfigItem(
			keyName = "showGpUnit",
			name = "Show Gp Unit",
			description = "Configures whether or not to show the gp unit."
	)
	default boolean showGpUnit()
	{
		return true;
	}
}
