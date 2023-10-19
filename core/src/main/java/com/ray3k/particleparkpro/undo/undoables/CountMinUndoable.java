package com.ray3k.particleparkpro.undo.undoables;

import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.ray3k.particleparkpro.Core;
import com.ray3k.particleparkpro.undo.Undoable;
import com.ray3k.particleparkpro.widgets.panels.EffectEmittersPanel;
import com.ray3k.particleparkpro.widgets.panels.EmitterPropertiesPanel;
import lombok.AllArgsConstructor;

import static com.ray3k.particleparkpro.Core.*;
import static com.ray3k.particleparkpro.widgets.panels.EffectEmittersPanel.*;
import static com.ray3k.particleparkpro.widgets.panels.EmitterPropertiesPanel.*;

@AllArgsConstructor
public class CountMinUndoable implements Undoable {
    private ParticleEmitter emitter;
    private int newValue;
    private int oldValue;

    @Override
    public void undo() {
        selectedEmitter = emitter;

        emitter.setMinParticleCount(oldValue);
        refreshDisplay();
    }

    @Override
    public void redo() {
        selectedEmitter = emitter;

        emitter.setMinParticleCount(newValue);
        refreshDisplay();
    }

    @Override
    public void start() {
        emitter.setMinParticleCount(newValue);
    }

    @Override
    public String getDescription() {
        return "change Count min";
    }

    private void refreshDisplay() {
        effectEmittersPanel.populateEmitters();
        emitterPropertiesPanel.populateScrollTable(null);
    }
}
