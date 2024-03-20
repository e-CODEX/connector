package eu.domibus.connector.ui.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.server.Command;

import java.util.concurrent.CompletableFuture;

public interface WizardStep {
    Component getComponent();
    String getStepTitle();
    default void setWizard(WizardComponent wizard) {}

    default void onForward(Command onForwardExecute) {};
    default void onBack(Command onBackExecute) {}

    default boolean isBackSupported() {
        return false;
    }
    default boolean isNextSupported() {
        return true;
    }


    public class WizardStepStateChangeEvent {
        WizardStep step;

        public WizardStepStateChangeEvent(WizardStep step) {
            this.step = step;
        }

        public WizardStep getStep() {
            return step;
        }

        public void setStep(WizardStep step) {
            this.step = step;
        }
    }

}
