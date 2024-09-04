/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.server.Command;
import lombok.Data;

/**
 * The WizardStep interface represents a step in a wizard component. It defines methods for getting
 * the component of the step, the title of the step, and optional methods for setting the wizard,
 * handling forward and back actions, and determining if the back and next buttons are supported for
 * the step.
 *
 * <p>The WizardStepStateChangeEvent class represents an event that is fired when the state of a
 * wizard step changes.
 */
public interface WizardStep {
    Component getComponent();

    String getStepTitle();

    default void setWizard(WizardComponent wizard) {
    }

    default void onForward(Command onForwardExecute) {
    }

    default void onBack(Command onBackExecute) {
    }

    default boolean isBackSupported() {
        return false;
    }

    default boolean isNextSupported() {
        return true;
    }

    /**
     * Represents an event that occurs when the state of a wizard step changes.
     */
    @Data
    class WizardStepStateChangeEvent {
        WizardStep step;

        public WizardStepStateChangeEvent(WizardStep step) {
            this.step = step;
        }
    }
}
