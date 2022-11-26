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

	@Alpha
	@ConfigItem(
			keyName = "backgroundColor",
			name = "Count Background Color",
			description = "Configures the background color of the inventory total counter."
	)
	default Color backgroundColor()
	{
		return Color.decode("#268736");
	}

	@ConfigItem(
			keyName = "textColor",
			name = "Count Text Color",
			description = "Configures the text color of the inventory total counter."
	)
	default Color textColor()
	{
		return Color.decode("#FFFFFF");
	}

	@Alpha
	@ConfigItem(
			keyName = "borderColor",
			name = "Container Border Color",
			description = "Configures the containers border color."
	)

	default Color borderColor()
	{
		return Color.decode("#000000");
	}

	@ConfigItem(
			keyName = "showBorder",
			name = "Container Border",
			description = "Configures whether or not the container has a border."
	)
	default boolean showBorder()
	{
		return true;
	}

	@ConfigItem(
			keyName = "fixedSize",
			name = "Fixed Size",
			description = "Configures whether or not the container is a fixed size or matches the text."
	)
	default boolean isFixedSize()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showWhenEmpty",
			name = "Show On Empty",
			description = "Configures whether or not the total is shown when there are no items in the inventory."
	)
	default boolean showOnEmpty()
	{
		return true;
	}

	@ConfigItem(
			keyName = "inventoryGap",
			name = "Inventory Gap",
			description = "Configures how much of a gap there is between the total and inventory."
	)
	default int inventoryGap()
	{
		return 10;
	}

	@ConfigItem(
			keyName = "roundedCorners",
			name = "Rounded Corners",
			description = "Configures whether or not the container has rounded corners."
	)
	default boolean hasRoundedCorners()
	{
		return true;
	}

	@ConfigItem(
			keyName = "horizontalAlignment",
			name = "Alignment",
			description = "Configures whether or not the container has rounded corners."
	)
	default InventoryTotalAlignment horizontalAlignment()
	{
		return InventoryTotalAlignment.CENTER;
	}
}
