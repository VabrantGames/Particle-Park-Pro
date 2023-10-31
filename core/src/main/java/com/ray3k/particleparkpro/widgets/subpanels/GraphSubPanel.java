package com.ray3k.particleparkpro.widgets.subpanels;

import com.badlogic.gdx.graphics.g2d.ParticleEmitter.ScaledNumericValue;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.ray3k.particleparkpro.Listeners;
import com.ray3k.particleparkpro.undo.UndoManager;
import com.ray3k.particleparkpro.undo.undoables.ScaledNumericValueRelativeUndoable;
import com.ray3k.particleparkpro.undo.undoables.ScaledNumericValueUndoable;
import com.ray3k.particleparkpro.undo.undoables.SetPropertyUndoable;
import com.ray3k.particleparkpro.widgets.LineGraph;
import com.ray3k.particleparkpro.widgets.Panel;
import com.ray3k.particleparkpro.widgets.ToggleGroup;
import com.ray3k.particleparkpro.widgets.panels.EmitterPropertiesPanel.ShownProperty;
import com.ray3k.particleparkpro.widgets.styles.Styles;
import com.ray3k.stripe.Spinner;
import com.ray3k.stripe.Spinner.Orientation;

import static com.ray3k.particleparkpro.Core.*;
import static com.ray3k.particleparkpro.Listeners.*;

public class GraphSubPanel extends Panel {
    private static final float GRAPH_UNDO_DELAY = .3f;
    private Action graphUndoAction;

    public GraphSubPanel(String name, ScaledNumericValue value, boolean hasRelative, boolean hasIndependent, String tooltip, String undoDescription, String graphText, ShownProperty closeProperty, float sliderIncrement, float sliderRange) {
        final int spinnerWidth = 70;
        final int itemSpacing = 5;

        setTouchable(Touchable.enabled);

        tabTable.padRight(7);
        tabTable.left();
        var label = new Label(name, skin, "header");
        tabTable.add(label).space(3);

        if (closeProperty != null) {
            var button = new Button(skin, "close");
            tabTable.add(button);
            addHandListener(button);
            onChange(button, () -> UndoManager.add(new SetPropertyUndoable(selectedEmitter, closeProperty, false, "set " + closeProperty.name + " property")));
        }

        var graphToggleWidget = new ToggleGroup();
        bodyTable.add(graphToggleWidget).grow();

        //Normal view
        graphToggleWidget.table1.defaults().space(itemSpacing);
        graphToggleWidget.table1.left();

        //Relative
        if (hasRelative) {
            var checkBox = new CheckBox("Relative", skin);
            checkBox.setProgrammaticChangeEvents(false);
            checkBox.setChecked(value.isRelative());
            graphToggleWidget.table1.add(checkBox).left();
            addHandListener(checkBox);
            addTooltip(checkBox, "If true, the value is in addition to the emitter's value", Align.top, Align.top, Styles.tooltipBottomArrowStyle);
            onChange(checkBox, () -> UndoManager.add(new ScaledNumericValueRelativeUndoable(selectedEmitter, value, checkBox.isChecked(), undoDescription + " Relative")));
        }

        //Independent
        if (hasIndependent) {
            graphToggleWidget.table1.row();
            var checkBox = new CheckBox("Independent", skin);
            checkBox.setProgrammaticChangeEvents(false);
            graphToggleWidget.table1.add(checkBox).left();
            addHandListener(checkBox);
            addTooltip(checkBox, "If true, the value is randomly assigned per particle", Align.top, Align.top, Styles.tooltipBottomArrowStyle);
            onChange(checkBox, () -> UndoManager.add(new ScaledNumericValueRelativeUndoable(selectedEmitter, value, checkBox.isChecked(), undoDescription + " Independent")));
        }

        //High
        graphToggleWidget.table1.row();
        var table = new Table();
        graphToggleWidget.table1.add(table).top();

        table.defaults().space(itemSpacing).left();
        label = new Label("High:", skin);
        table.add(label);

        var highToggleWidget = new ToggleGroup();
        table.add(highToggleWidget);

        //High single
        highToggleWidget.table1.defaults().space(itemSpacing);
        var highValueSpinner = new Spinner(value.getHighMin(), 1, true, Orientation.RIGHT_STACK, Styles.spinnerStyle);
        highValueSpinner.setProgrammaticChangeEvents(false);
        highToggleWidget.table1.add(highValueSpinner).width(spinnerWidth);
        addIbeamListener(highValueSpinner.getTextField());
        addHandListener(highValueSpinner.getButtonPlus());
        addHandListener(highValueSpinner.getButtonMinus());
        addTooltip(highValueSpinner, "The high value for " + tooltip, Align.top, Align.top, Styles.tooltipBottomArrowStyle);
        addInfiniteSlider(highValueSpinner, sliderIncrement, sliderRange);

        var highExpandButton = new Button(skin, "moveright");
        highToggleWidget.table1.add(highExpandButton);
        addHandListener(highExpandButton);
        addTooltip(highExpandButton, "Expand to define a range for the high value", Align.top, Align.top, Styles.tooltipBottomArrowStyle);
        onChange(highExpandButton, highToggleWidget::swap);

        //High range
        highToggleWidget.table2.defaults().space(itemSpacing);
        var highMinValueSpinner = new Spinner(value.getHighMin(), 1, true, Orientation.RIGHT_STACK, Styles.spinnerStyle);
        highMinValueSpinner.setProgrammaticChangeEvents(false);
        highToggleWidget.table2.add(highMinValueSpinner).width(spinnerWidth);
        addIbeamListener(highMinValueSpinner.getTextField());
        addHandListener(highMinValueSpinner.getButtonPlus());
        addHandListener(highMinValueSpinner.getButtonMinus());
        addTooltip(highMinValueSpinner, "The minimum high value for " + tooltip, Align.top, Align.top, Styles.tooltipBottomArrowStyle);
        addInfiniteSlider(highMinValueSpinner, sliderIncrement, sliderRange);

        var highMaxValueSpinner = new Spinner(value.getHighMax(), 1, true, Orientation.RIGHT_STACK, Styles.spinnerStyle);
        highMaxValueSpinner.setProgrammaticChangeEvents(false);
        highToggleWidget.table2.add(highMaxValueSpinner).width(spinnerWidth);
        addIbeamListener(highMaxValueSpinner.getTextField());
        addHandListener(highMaxValueSpinner.getButtonPlus());
        addHandListener(highMaxValueSpinner.getButtonMinus());
        addTooltip(highMaxValueSpinner, "The maximum high value for " + tooltip, Align.top, Align.top, Styles.tooltipBottomArrowStyle);
        addInfiniteSlider(highMaxValueSpinner, sliderIncrement, sliderRange);

        var highCollapseButton = new Button(skin, "moveleft");
        highToggleWidget.table2.add(highCollapseButton);
        addHandListener(highCollapseButton);
        addTooltip(highCollapseButton, "Collapse to define a single high value", Align.top, Align.top, Styles.tooltipBottomArrowStyle);
        onChange(highCollapseButton, highToggleWidget::swap);

        if (!MathUtils.isEqual(value.getHighMin(), value.getHighMax())) highToggleWidget.swap();

        //Low
        table.row();
        label = new Label("Low:", skin);
        table.add(label);

        var lowToggleWidget = new ToggleGroup();
        table.add(lowToggleWidget);

        //Low single
        lowToggleWidget.table1.defaults().space(itemSpacing);
        var lowValueSpinner = new Spinner(value.getLowMin(), 1, true, Orientation.RIGHT_STACK, Styles.spinnerStyle);
        lowValueSpinner.setProgrammaticChangeEvents(false);
        lowToggleWidget.table1.add(lowValueSpinner).width(spinnerWidth);
        addIbeamListener(lowValueSpinner.getTextField());
        addHandListener(lowValueSpinner.getButtonPlus());
        addHandListener(lowValueSpinner.getButtonMinus());
        addTooltip(lowValueSpinner, "The low value for the " + tooltip, Align.top, Align.top, Styles.tooltipBottomArrowStyle);
        addInfiniteSlider(lowValueSpinner, sliderIncrement, sliderRange);

        var lowExpandButton = new Button(skin, "moveright");
        lowToggleWidget.table1.add(lowExpandButton);
        addHandListener(lowExpandButton);
        addTooltip(lowExpandButton, "Expand to define a range for the low value", Align.top, Align.top, Styles.tooltipBottomArrowStyle);
        onChange(lowExpandButton, lowToggleWidget::swap);

        //Low range
        lowToggleWidget.table2.defaults().space(itemSpacing);
        var lowMinValueSpinner = new Spinner(value.getLowMin(), 1, true, Orientation.RIGHT_STACK, Styles.spinnerStyle);
        lowMinValueSpinner.setProgrammaticChangeEvents(false);
        lowToggleWidget.table2.add(lowMinValueSpinner).width(spinnerWidth);
        addIbeamListener(lowMinValueSpinner.getTextField());
        addHandListener(lowMinValueSpinner.getButtonPlus());
        addHandListener(lowMinValueSpinner.getButtonMinus());
        addTooltip(lowMinValueSpinner, "The minimum low value for " + tooltip, Align.top, Align.top, Styles.tooltipBottomArrowStyle);
        addInfiniteSlider(lowMinValueSpinner, sliderIncrement, sliderRange);

        var lowMaxValueSpinner = new Spinner(value.getLowMax(), 1, true, Orientation.RIGHT_STACK, Styles.spinnerStyle);
        lowMaxValueSpinner.setProgrammaticChangeEvents(false);
        lowToggleWidget.table2.add(lowMaxValueSpinner).width(spinnerWidth);
        addIbeamListener(lowMaxValueSpinner.getTextField());
        addHandListener(lowMaxValueSpinner.getButtonPlus());
        addHandListener(lowMaxValueSpinner.getButtonMinus());
        addTooltip(lowMaxValueSpinner, "The maximum low value for " + tooltip, Align.top, Align.top, Styles.tooltipBottomArrowStyle);
        addInfiniteSlider(lowMaxValueSpinner, sliderIncrement, sliderRange);

        var lowCollapseButton = new Button(skin, "moveleft");
        lowToggleWidget.table2.add(lowCollapseButton);
        addHandListener(lowCollapseButton);
        addTooltip(lowCollapseButton, "Collapse to define a single low value", Align.top, Align.top, Styles.tooltipBottomArrowStyle);
        onChange(lowCollapseButton, lowToggleWidget::swap);

        if (!MathUtils.isEqual(value.getLowMin(), value.getLowMax())) lowToggleWidget.swap();

        //Graph small
        var graph = new LineGraph(graphText, Styles.lineGraphStyle);
        graph.setNodes(value.getTimeline(), value.getScaling());
        graph.setNodeListener(handListener);
        graphToggleWidget.table1.add(graph);

        var graphPlusButton = new Button(skin, "plus");
        graphToggleWidget.table1.add(graphPlusButton).bottom();
        addHandListener(graphPlusButton);
        addTooltip(graphPlusButton, "Expand to large graph view", Align.top, Align.top, Styles.tooltipBottomArrowStyle);

        //Expanded graph view
        graphToggleWidget.table2.defaults().space(itemSpacing);
        var graphExpanded = new LineGraph(graphText, Styles.lineGraphBigStyle);
        graphExpanded.setNodeListener(handListener);
        graphToggleWidget.table2.add(graphExpanded).grow();

        onChange(graphPlusButton, () -> {
            graphToggleWidget.swap();
            graphExpanded.setNodes(value.getTimeline(), value.getScaling());
        });

        var graphMinusButton = new Button(skin, "minus");
        graphToggleWidget.table2.add(graphMinusButton).bottom();
        addHandListener(graphMinusButton);
        addTooltip(graphMinusButton, "Collapse to normal view", Align.top, Align.top, Styles.tooltipBottomArrowStyle);
        onChange(graphMinusButton, () -> {
            graphToggleWidget.swap();
            graph.setNodes(value.getTimeline(), value.getScaling());
        });

        onChange(highValueSpinner, () -> {
            var undo = new ScaledNumericValueUndoable(selectedEmitter, value, undoDescription);
            undo.oldValue.set(value);
            undo.newValue.set(value);
            undo.newValue.setHigh(highValueSpinner.getValueAsInt());
            UndoManager.add(undo);

            highMinValueSpinner.setValue(highValueSpinner.getValueAsInt());
            highMaxValueSpinner.setValue(highValueSpinner.getValueAsInt());
        });

        onChange(highMinValueSpinner, () -> {
            var undo = new ScaledNumericValueUndoable(selectedEmitter, value, undoDescription);
            undo.oldValue.set(value);
            undo.newValue.set(value);
            undo.newValue.setHighMin(highMinValueSpinner.getValueAsInt());
            UndoManager.add(undo);

            highValueSpinner.setValue(highMinValueSpinner.getValueAsInt());
        });

        onChange(highMaxValueSpinner, () -> {
            var undo = new ScaledNumericValueUndoable(selectedEmitter, value, undoDescription);
            undo.oldValue.set(value);
            undo.newValue.set(value);
            undo.newValue.setHighMax(highMaxValueSpinner.getValueAsInt());
            UndoManager.add(undo);

            highValueSpinner.setValue(highMaxValueSpinner.getValueAsInt());
        });

        onChange(highCollapseButton, () -> {
            var undo = new ScaledNumericValueUndoable(selectedEmitter, value, undoDescription);
            undo.oldValue.set(value);
            undo.newValue.set(value);
            undo.newValue.setHigh(highValueSpinner.getValueAsInt());
            UndoManager.add(undo);

            highMinValueSpinner.setValue(highValueSpinner.getValueAsInt());
            highMaxValueSpinner.setValue(highValueSpinner.getValueAsInt());
        });

        onChange(lowValueSpinner, () -> {
            var undo = new ScaledNumericValueUndoable(selectedEmitter, value, undoDescription);
            undo.oldValue.set(value);
            undo.newValue.set(value);
            undo.newValue.setLow(lowValueSpinner.getValueAsInt());
            UndoManager.add(undo);

            lowMinValueSpinner.setValue(lowValueSpinner.getValueAsInt());
            lowMaxValueSpinner.setValue(lowValueSpinner.getValueAsInt());
        });

        onChange(lowMinValueSpinner, () -> {
            var undo = new ScaledNumericValueUndoable(selectedEmitter, value, undoDescription);
            undo.oldValue.set(value);
            undo.newValue.set(value);
            undo.newValue.setLowMin(lowMinValueSpinner.getValueAsInt());
            UndoManager.add(undo);

            lowValueSpinner.setValue(lowMinValueSpinner.getValueAsInt());
        });

        onChange(lowMaxValueSpinner, () -> {
            var undo = new ScaledNumericValueUndoable(selectedEmitter, value, undoDescription);
            undo.oldValue.set(value);
            undo.newValue.set(value);
            undo.newValue.setLowMax(lowMaxValueSpinner.getValueAsInt());
            UndoManager.add(undo);

            lowValueSpinner.setValue(lowMaxValueSpinner.getValueAsInt());
        });

        onChange(lowCollapseButton, () -> {
            var undo = new ScaledNumericValueUndoable(selectedEmitter, value, undoDescription);
            undo.oldValue.set(value);
            undo.newValue.set(value);
            undo.newValue.setLow(lowValueSpinner.getValueAsInt());
            UndoManager.add(undo);

            lowMinValueSpinner.setValue(lowValueSpinner.getValueAsInt());
            lowMaxValueSpinner.setValue(lowValueSpinner.getValueAsInt());
        });

        onChange(graph, () -> {
            var nodes = graph.getNodes();
            float[] newTimeline = new float[nodes.size];
            float[] newScaling = new float[nodes.size];
            for (int i = 0; i < nodes.size; i++) {
                var node = nodes.get(i);
                newTimeline[i] = node.percentX;
                newScaling[i] = node.percentY;
            }

            addGraphUpdateAction(value, newTimeline, newScaling, undoDescription);
        });

        onChange(graphExpanded, () -> {
            var nodes = graphExpanded.getNodes();
            float[] newTimeline = new float[nodes.size];
            float[] newScaling = new float[nodes.size];
            for (int i = 0; i < nodes.size; i++) {
                var node = nodes.get(i);
                newTimeline[i] = node.percentX;
                newScaling[i] = node.percentY;
            }

            addGraphUpdateAction(value, newTimeline, newScaling, undoDescription);
        });
    }

    private void addGraphUpdateAction(ScaledNumericValue value, float[] newTimeline, float[] newScaling, String undoDescription) {
        var oldValue = new ScaledNumericValue();
        oldValue.set(value);

        value.setTimeline(newTimeline);
        value.setScaling(newScaling);

        if (graphUndoAction != null) graphUndoAction.restart();
        else {
            graphUndoAction = new TemporalAction(GRAPH_UNDO_DELAY) {
                @Override
                protected void update(float percent) {
                }

                @Override
                protected void end() {
                    var undo = new ScaledNumericValueUndoable(selectedEmitter, value, undoDescription);
                    undo.oldValue.set(oldValue);
                    undo.newValue.set(value);
                    UndoManager.add(undo);

                    graphUndoAction = null;
                }
            };
            stage.addAction(graphUndoAction);
        }
    }
}
