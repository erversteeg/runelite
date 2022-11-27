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
			description = "Configures the background color of the counter when gaining gp."
	)
	default Color profitColor()
	{
		return Color.decode("#268736");
	}

	@ConfigItem(
			keyName = "lossBackgroundColor",
			name = "Loss Color",
			description = "Configures the background color of the counter when losing gp."
	)
	default Color lossColor()
	{
		return Color.decode("#87182F");
	}

	@ConfigItem(
			keyName = "bankBackgroundColor",
			name = "Bank Color",
			description = "Configures the background color of the counter when the bank is open."
	)
	default Color bankColor()
	{
		return Color.decode("#91893C");
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
			keyName = "totalGpColor",
			name = "Total Gp Color",
			description = "Configures the text color of the total gp counter."
	)
	default Color totalGpColor()
	{
		return Color.decode("#FFFFFF");
	}

	@ConfigItem(
			keyName = "runTimeColor",
			name = "Run Time Color",
			description = "Configures the text color of the elapsed run time."
	)
	default Color runTimeColor()
	{
		return Color.decode("#FFFFFF");
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
		return 16;
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
			keyName = "showRunTime",
			name = "Show Run Time",
			description = "Configures whether or not the run time is displayed."
	)
	default boolean showRunTime()
	{
		return false;
	}

	@ConfigItem(
			keyName = "runTimeStartThreshold",
			name = "Run Time Start Threshold",
			description = "Configures the number of seconds it takes to be considered a run."
	)
	default int runTimeStartThreshold()
	{
		return 0;
	}
}
