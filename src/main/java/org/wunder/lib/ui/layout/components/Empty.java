package org.wunder.lib.ui.layout.components;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.wunder.lib.ui.layout.components.render.NullRenderer;
import org.wunder.lib.ui.layout.values.Value;

@Environment(EnvType.CLIENT)
public class Empty extends LayoutComponent<NullRenderer, Empty> {
    public Empty(
            Value width,
            Value height
    ) {
        super(width, height, null);
    }

    @Override
    public int getContentWidth() {
        return 0;
    }

    @Override
    public int getContentHeight() {
        return 0;
    }

    @Override
    public boolean isMouseOver(double d, double e) {
        return false;
    }

    @Override
    public void setFocused(boolean bl) {
    }

    @Override
    public boolean isFocused() {
        return false;
    }
}
