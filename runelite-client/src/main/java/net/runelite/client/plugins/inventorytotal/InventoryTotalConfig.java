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
			position = 0,
			keyName = "enableProfitLoss",
			name = "Profit / Loss",
			description = "Configures whether or not the total runs in profit / loss."
	)
	default boolean enableProfitLoss()
	{
		return false;
	}

	@ConfigItem(
			position = 1,
			keyName = "totalBackgroundColor",
			name = "Background Color",
			description = "Configures the background color of the total box."
	)
	default Color totalColor()
	{
		return Color.decode("#939393");
	}

	@ConfigItem(
			position = 2,
			keyName = "profitBackgroundColor",
			name = "Profit Color",
			description = "Configures the background color of the total box when gaining gp in profit / loss."
	)
	default Color profitColor()
	{
		return Color.decode("#42834C");
	}

	@ConfigItem(
			position = 3,
			keyName = "lossBackgroundColor",
			name = "Loss Color",
			description = "Configures the background color of the total box when losing gp in profit / loss."
	)
	default Color lossColor()
	{
		return Color.decode("#912A2A");
	}

	@ConfigItem(
			position = 4,
			keyName = "opaqueBackground",
			name = "Opaque Background",
			description = "Configures whether or not the total box background is opaque."
	)
	default boolean opaqueBackground()
	{
		return true;
	}

	@ConfigItem(
			position = 5,
			keyName = "roundedCorners",
			name = "Rounded Corners",
			description = "Configures whether or not the total box has rounded corners."
	)
	default boolean roundCorners()
	{
		return false;
	}

	@ConfigItem(
			position = 6,
			keyName = "horizontalAlignment",
			name = "Alignment",
			description = "Configures the alignment of the total box."
	)
	default InventoryTotalAlignment horizontalAlignment()
	{
		return InventoryTotalAlignment.CENTER;
	}

	@ConfigItem(
			position = 7,
			keyName = "inventoryOffsetX",
			name = "Inventory Offset X",
			description = "Configures where the total box x-axis is located relative to the inventory."
	)
	default int inventoryXOffset()
	{
		return 0;
	}

	@ConfigItem(
			position = 8,
			keyName = "inventoryOffsetXNegative",
			name = "Inventory Offset X Negative",
			description = "Configures whether or not the total box y-axis offset is a negative number."
	)
	default boolean isInventoryXOffsetNegative()
	{
		return false;
	}

	@ConfigItem(
			position = 9,
			keyName = "inventoryOffsetY",
			name = "Inventory Offset Y",
			description = "Configures where the total box x-axis is located relative to the inventory."
	)
	default int inventoryYOffset()
	{
		return 42;
	}

	@ConfigItem(
			position = 10,
			keyName = "inventoryOffsetYNegative",
			name = "Inventory Offset Y Negative",
			description = "Configures whether or not the total box y-axis offset is a negative number."
	)
	default boolean isInventoryYOffsetNegative()
	{
		return false;
	}

	@ConfigItem(
			position = 11,
			keyName = "showRunTime",
			name = "Show Run Time",
			description = "Configures whether or not the run time is shown when available."
	)
	default boolean showRunTime()
	{
		return false;
	}

	@ConfigItem(
			position = 11,
			keyName = "showExactGp",
			name = "Show Exact Gp",
			description = "Configures whether or not the exact gp total is shown in the total box."
	)
	default boolean showExactGp()
	{
		return false;
	}

	@ConfigItem(
			position = 11,
			keyName = "showCoinStack",
			name = "Show Coin Stack",
			description = "Configures whether or not the coin stack is displayed."
	)
	default boolean showCoinStack()
	{
		return true;
	}

	@ConfigItem(
			position = 11,
			keyName = "showWhileBanking",
			name = "Show While Banking",
			description = "Configures whether or not the total box is shown while banking."
	)
	default boolean showWhileBanking()
	{
		return true;
	}
}
