package org.wunder.lib.ui.layout.components;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import com.google.common.collect.ImmutableList;
import org.wunder.lib.ui.ColorHelper;
import org.wunder.lib.ui.layout.LineWithWidth;
import org.wunder.lib.ui.layout.components.render.ComponentRenderer;
import org.wunder.lib.ui.layout.components.render.TextProvider;
import org.wunder.lib.ui.layout.values.Alignment;
import org.wunder.lib.ui.layout.values.Rectangle;
import org.wunder.lib.ui.layout.values.Value;

import java.util.Iterator;
import java.util.List;

public class MultiLineText extends LayoutComponent<MultiLineText.MultiLineTextRenderer, MultiLineText> {
    net.minecraft.network.chat.Component text;
    int color = ColorHelper.DEFAULT_TEXT;
    protected MultiLineLabel multiLineLabel;
    int bufferedContentWidth = 0;
    List<LineWithWidth> lines = List.of();

    public MultiLineText(
            Value width,
            Value height,
            final net.minecraft.network.chat.Component text
    ) {
        super(width, height, new MultiLineTextRenderer());
        renderer.linkedComponent = this;
        this.text = text;
        updatedContentWidth();
    }

    public MultiLineText setColor(int cl) {
        this.color = cl;
        return this;
    }

    public static Component parse(Component text) {
        String[] parts = text.getString().split("\\*\\*");
        if (parts.length > 0) {
            boolean bold = false;
            MutableComponent c = Component.literal(parts[0]);

            for (int i = 1; i < parts.length; i++) {
                bold = !bold;
                c.append(Component.literal(parts[i]).setStyle(Style.EMPTY.withBold(bold)));
            }
            return c;
        }
        return text;
    }

    public MultiLineText setText(Component text) {
        this.text = text;
        this.updatedContentWidth();

        if (multiLineLabel != null) {
            multiLineLabel = createVanillaComponent();
        }

        return this;
    }

    protected MultiLineLabel createVanillaComponent() {
        final int wd = relativeBounds == null ? width.calculatedSize() : relativeBounds.width;
        lines = renderer.getFont()
                        .split(text, wd)
                        .stream()
                        .map((component) -> new LineWithWidth(component, renderer.getFont().width(component)))
                        .collect(ImmutableList.toImmutableList());

        return MultiLineLabel.create(renderer.getFont(), text, wd);
    }

    protected void updatedContentWidth() {
        String[] lines = text.getString().split("\n");
        if (lines.length == 0) bufferedContentWidth = 0;
        else {
            String line = lines[0];
            for (int i = 1; i < lines.length; i++)
                if (lines[i].length() > line.length())
                    line = lines[i];

            bufferedContentWidth = renderer.getWidth(Component.literal(line));
        }
    }

    @Override
    protected void onBoundsChanged() {
        super.onBoundsChanged();
        multiLineLabel = createVanillaComponent();
    }

    @Override
    public int getContentWidth() {
        return bufferedContentWidth;
    }

    @Override
    public int getContentHeight() {
        return renderer.getHeight(text);
    }

    protected static class MultiLineTextRenderer implements ComponentRenderer, TextProvider {
        MultiLineText linkedComponent;

        @Override
        public int getWidth(Component c) {
            return getFont().width(c.getVisualOrderText());
        }

        @Override
        public int getHeight(net.minecraft.network.chat.Component c) {
            if (linkedComponent == null) return 20;
            MultiLineLabel ml;
            if (linkedComponent.multiLineLabel != null) ml = linkedComponent.multiLineLabel;
            else ml = linkedComponent.createVanillaComponent();
            return ml.getLineCount() * getLineHeight(c);
        }

        @Override
        public void renderInBounds(
                PoseStack stack,
                int mouseX,
                int mouseY,
                float deltaTicks,
                Rectangle bounds,
                Rectangle clipRect
        ) {
            if (linkedComponent != null && linkedComponent.multiLineLabel != null) {
                int top = bounds.height - getHeight(linkedComponent.text);
                if (linkedComponent.vAlign == Alignment.MIN) top = 0;
                if (linkedComponent.vAlign == Alignment.CENTER) top /= 2;

                if (linkedComponent.hAlign == Alignment.CENTER) {
                    linkedComponent.multiLineLabel.renderCentered(
                            stack, bounds.width / 2, top,
                            getLineHeight(linkedComponent.text),
                            linkedComponent.color
                    );
                } else if (linkedComponent.hAlign == Alignment.MAX) {
                    int lineY = 0;
                    int lineHeight = getLineHeight(linkedComponent.text);

                    for (Iterator<LineWithWidth> iter = linkedComponent.lines.iterator(); iter.hasNext(); lineY += lineHeight) {
                        LineWithWidth textWithWidth = iter.next();
                        getFont().drawShadow(
                                stack,
                                textWithWidth.text(),
                                linkedComponent.width.calculatedSize() - textWithWidth.width(),
                                lineY,
                                linkedComponent.color
                        );
                    }
                } else {
                    linkedComponent.multiLineLabel.renderLeftAligned(
                            stack, 0, top,
                            getLineHeight(linkedComponent.text),
                            linkedComponent.color
                    );
                }
            }
        }
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
