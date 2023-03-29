package org.wunder.lib.ui.layout.components;

import net.minecraft.network.chat.Component;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.wunder.lib.ui.layout.components.render.RangeRenderer;
import org.wunder.lib.ui.layout.values.Value;
import org.wunder.lib.ui.vanilla.Slider;

@Environment(EnvType.CLIENT)
public class Range<N extends Number> extends AbstractVanillaComponent<Slider<N>, Range<N>> {
    @Override
    public boolean isFocused() {
        return renderer.linkedComponent.isFocused();
    }

    @FunctionalInterface
    public interface ValueChanged<N extends Number> {
        void now(Range<N> range, N newValue);
    }

    private ValueChanged<N> onChange;
    private final N minValue;
    private final N maxValue;
    private final N initialValue;

    public Range(
            Value width,
            Value height,
            Component component,
            N minValue,
            N maxValue,
            N initialValue
    ) {
        super(width, height, new RangeRenderer<>(), component);
        this.onChange = (a, b) -> {
        };
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.initialValue = initialValue;
    }

    public Range(
            Value width,
            Value height,
            N minValue,
            N maxValue,
            N initialValue
    ) {
        this(width, height, null, minValue, maxValue, initialValue);
    }

    public Range<N> onChange(ValueChanged<N> onChange) {
        this.onChange = onChange;
        return this;
    }

    @Override
    protected Slider<N> createVanillaComponent() {
        Range<N> self = this;
        return new Slider<>(
                0,
                0,
                relativeBounds.width,
                relativeBounds.height,
                component,
                minValue,
                maxValue,
                initialValue,
                (s, v) -> onChange.now(self, v)
        );
    }

    public N getValue() {
        return vanillaComponent.currentValue();
    }


    @Override
    protected Component contentComponent() {
        Slider<N> dummy = new Slider<>(
                0,
                0,
                100,
                20,
                component,
                minValue,
                maxValue,
                initialValue,
                (a, b) -> {
                }
        );
        return dummy.getValueComponent(maxValue);
    }
}
