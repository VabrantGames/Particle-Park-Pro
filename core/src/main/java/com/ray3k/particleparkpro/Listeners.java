package com.ray3k.particleparkpro;

import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.Align;
import com.ray3k.particleparkpro.widgets.InfSlider;
import com.ray3k.stripe.PopTable;
import com.ray3k.stripe.PopTable.PopTableStyle;
import com.ray3k.stripe.ScrollFocusListener;
import com.ray3k.stripe.Spinner;

import static com.ray3k.particleparkpro.Core.*;
import static com.ray3k.particleparkpro.widgets.styles.Styles.infSliderStyle;

public class Listeners {
    public static SystemCursorListener handListener;
    public static SystemCursorListener ibeamListener;
    public static SystemCursorListener horizontalResizeListener;
    public static SystemCursorListener verticalResizeListener;
    public static SystemCursorListener neswResizeListener;
    public static SystemCursorListener nwseResizeListener;
    public static SystemCursorListener allResizeListener;
    public static SplitPaneSystemCursorListener splitPaneHorizontalSystemCursorListener;
    public static SplitPaneSystemCursorListener splitPaneVerticalSystemCursorListener;
    static ScrollFocusListener scrollFocusListener;
    static ScrollFocusListener foregroundScrollFocusListener;

    public static void initializeListeners() {
        handListener = new SystemCursorListener(SystemCursor.Hand);
        ibeamListener = new SystemCursorListener(SystemCursor.Ibeam);
        neswResizeListener = new SystemCursorListener(SystemCursor.NESWResize);
        nwseResizeListener = new SystemCursorListener(SystemCursor.NWSEResize);
        horizontalResizeListener = new SystemCursorListener(SystemCursor.HorizontalResize);
        verticalResizeListener = new SystemCursorListener(SystemCursor.VerticalResize);
        allResizeListener = new SystemCursorListener(SystemCursor.AllResize);
        splitPaneHorizontalSystemCursorListener = new SplitPaneSystemCursorListener(SystemCursor.HorizontalResize);
        splitPaneVerticalSystemCursorListener = new SplitPaneSystemCursorListener(SystemCursor.VerticalResize);
        scrollFocusListener = new ScrollFocusListener(stage);
        foregroundScrollFocusListener = new ScrollFocusListener(foregroundStage);
    }

    public static void onChange(Actor actor, Runnable runnable) {
        actor.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                runnable.run();
            }
        });
    }

    public static void onClick(Actor actor, Runnable runnable) {
        actor.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                runnable.run();
            }
        });
    }

    public static void onTouchDown(Actor actor, Runnable runnable) {
        actor.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                runnable.run();
                return false;
            }
        });
    }

    public static void addHandListener(Actor actor) {
        actor.addListener(handListener);
    }

    public static void addIbeamListener(Actor actor) {
        actor.addListener(ibeamListener);
    }

    public static void addScrollFocusListener(Actor actor) {
        actor.addListener(scrollFocusListener);
    }

    public static void addForegroundScrollFocusListener(Actor actor) {
        actor.addListener(foregroundScrollFocusListener);
    }

    public static void addHorizontalResizeListener(Actor actor) {
        actor.addListener(horizontalResizeListener);
    }

    public static void addVerticalResizeListener(Actor actor) {
        actor.addListener(verticalResizeListener);
    }

    public static void addNESWresizeListener(Actor actor) {
        actor.addListener(neswResizeListener);
    }

    public static void addNWSEresizeListener(Actor actor) {
        actor.addListener(nwseResizeListener);
    }

    public static void addAllResizeListener(Actor actor) {
        actor.addListener(allResizeListener);
    }

    public static void addSplitPaneHorizontalSystemCursorListener(Actor actor) {
        actor.addListener(splitPaneHorizontalSystemCursorListener);
    }

    public static void addSplitPaneVerticalSystemCursorListener(Actor actor) {
        actor.addListener(splitPaneVerticalSystemCursorListener);
    }

    public static PopTable addTooltip(Actor actor, String text, int edge, int align, float width, PopTableStyle popTableStyle) {
        return addTooltip(actor, text, edge, align, width, true, popTableStyle);
    }

    public static PopTable addTooltip(Actor actor, String text, int edge, int align, PopTableStyle popTableStyle) {
        return addTooltip(actor, text, edge, align, 0, false, popTableStyle);
    }

    public static PopTable addTooltip(Actor actor, String text, int edge, int align, PopTableStyle popTableStyle, boolean foreground) {
        return addTooltip(actor, text, edge, align, 0, false, popTableStyle, foreground);
    }

    private static PopTable addTooltip(Actor actor, String text, int edge, int align, float width, boolean defineWidth, PopTableStyle popTableStyle) {
        return addTooltip(actor, text, edge, align, width, defineWidth, popTableStyle, true);
    }

    private static PopTable addTooltip(Actor actor, String text, int edge, int align, float width, boolean defineWidth, PopTableStyle popTableStyle, boolean foreground) {
        PopTable popTable = new PopTable(popTableStyle);
        var inputListener = new ClickListener() {
            boolean dismissed;
            Action showTableAction;

            {
                popTable.setModal(false);
                popTable.setTouchable(Touchable.disabled);

                var label = new Label(text, skin);
                if (defineWidth) {
                    label.setWrap(true);
                    popTable.add(label).width(width);
                } else {
                    popTable.add(label);
                }
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1 && popTable.isHidden() && !dismissed) {
                    if (fromActor == null || !event.getListenerActor().isAscendantOf(fromActor)) {
                        if (showTableAction == null) {
                            showTableAction = Actions.delay(.5f,
                                Actions.run(() -> {
                                    showTable(actor);
                                    showTableAction = null;
                                }));
                            actor.addAction(showTableAction);
                        }
                    }
                }
            }

            private void showTable(Actor actor) {
                if (actor instanceof Disableable) {
                    if (((Disableable) actor).isDisabled()) return;
                }

                popTable.show(foreground ? foregroundStage : stage);
                popTable.attachToActor(actor, edge, align);

                popTable.moveToInsideStage();
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (pointer == -1) {
                    if (toActor == null || !toActor.isDescendantOf(event.getListenerActor())) {
                        if (!popTable.isHidden()) popTable.hide();
                        dismissed = false;
                        if (showTableAction != null) {
                            actor.removeAction(showTableAction);
                            showTableAction = null;
                        }
                    }

                }
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                dismissed = true;
                popTable.hide();
                if (showTableAction != null) {
                    actor.removeAction(showTableAction);
                    showTableAction = null;
                }
                return false;
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {

            }
        };
        actor.addListener(inputListener);
        return popTable;
    }

    public static void addInfiniteSlider(Spinner valueSpinner, float increment, float range) {
        var sliderPop = new PopTable();
        sliderPop.attachToActor(valueSpinner, Align.bottom, Align.bottom);

        var slider = new InfSlider(infSliderStyle);
        slider.setRange(range);
        slider.setIncrement(increment);
        slider.addListener(noCaptureKeyboardFocusListener);
        slider.getKnob().addListener(noCaptureKeyboardFocusListener);
        slider.getBackground().addListener(noCaptureKeyboardFocusListener);

        slider.setValue(valueSpinner.getValueAsInt());
        sliderPop.add(slider).width(100);
        addHandListener(slider.getKnob());
        onChange(slider, () -> {
            System.out.println(slider.getValue());
            valueSpinner.setValue(slider.getValue());
            valueSpinner.fire(new ChangeEvent());
        });

        valueSpinner.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
                if (event.isFocused()) {
                    sliderPop.show(stage);
                    slider.setValue(valueSpinner.getValueAsInt());
                }
                else sliderPop.hide();
            }
        });
    }
}
