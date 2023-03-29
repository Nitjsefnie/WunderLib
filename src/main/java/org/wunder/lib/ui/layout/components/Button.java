package org.wunder.lib.ui.layout.components;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.wunder.lib.ui.layout.components.render.ButtonRenderer;
import org.wunder.lib.ui.layout.values.Value;

@Environment(EnvType.CLIENT)
public class Button extends AbstractVanillaComponent<net.minecraft.client.gui.components.Button, Button> {
    public static final OnTooltip NO_TOOLTIP = (button, poseStack, i, j) -> {
    };
    public static final OnPress NO_ACTION = (button) -> {
    };

    @Override
    public boolean isFocused() {
        return super.vanillaComponent.isFocused();
    }

    @Environment(EnvType.CLIENT)
    public interface OnTooltip {
        void onTooltip(Button button, PoseStack poseStack, int mouseX, int mouseY);
    }

    @Environment(EnvType.CLIENT)
    public interface OnPress {
        void onPress(Button button);
    }

    OnPress onPress;
    Component onTooltip;

    boolean glow = false;

    public Button(
            Value width,
            Value height,
            Component component
    ) {
        super(width, height, new ButtonRenderer(), component);
        this.onPress = NO_ACTION;
        this.onTooltip = null;
    }

    public Button onPress(OnPress onPress) {
        this.onPress = onPress;
        return this;
    }

    public Button onToolTip(Component message) {
        this.onTooltip = onTooltip;
        return this;
    }

    @Override
    protected net.minecraft.client.gui.components.Button createVanillaComponent() {
        Button self = this;
        var builder = net.minecraft.client.gui.components.Button
                .builder(component, (bt) -> onPress.onPress(self))
                .bounds(0, 0, relativeBounds.width, relativeBounds.height);
        if (onTooltip != null) {
            builder.tooltip(Tooltip.create(onTooltip));
        }
        return builder.build();
    }

    public boolean isGlowing() {
        return glow;
    }
}
