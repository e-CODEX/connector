/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.ui.component;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A WizardComponent is a custom component that allows users to navigate through a series of steps
 * or screens in a wizard-like fashion. It extends the VerticalLayout class.
 *
 * <p>Note: The WizardComponent class should be used within a Vaadin application.
 */
@SuppressWarnings("squid:S1135")
public class WizardComponent extends VerticalLayout {
    private static final Logger LOGGER = LogManager.getLogger(WizardComponent.class);
    private final ProgressBar progressBar = new ProgressBar(10, 100, 10);
    private final Button nextButton = new Button("Next");
    private final Button backButton = new Button("Back");
    private final Button cancelButton = new Button("Cancel");
    private final Button finishButton = new Button("Finish");
    private final Label stepTitle = new Label("");
    private final Text header = new Text("Create Link");
    private final Div content = new Div();
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private LinkedList<WizardStep> steps = new LinkedList<>();
    private WizardStep activeStep;
    private List<WizardFinishedListener> finishListener;
    private List<WizardCanceldListener> cancelListener;

    public static WizardBuilder getBuilder() {
        return new WizardBuilder();
    }

    /**
     * A builder class for creating instances of the WizardComponent.
     *
     * <p>The WizardBuilder class provides methods for adding steps to the wizard, as well as
     * adding listeners for when the wizard is finished or cancelled. Once all steps and listeners
     * have been added, the build() method can be called to create and initialize the
     * WizardComponent instance.
     *
     * @see WizardStep
     * @see WizardFinishedListener
     * @see WizardCanceldListener
     * @see WizardComponent
     */
    public static class WizardBuilder {
        private final LinkedList<WizardStep> wizardSteps = new LinkedList<>();
        private final List<WizardFinishedListener> finishedListeners = new ArrayList<>();
        private final List<WizardCanceldListener> cancelListeners = new ArrayList<>();

        private WizardBuilder() {
        }

        public WizardBuilder addStep(WizardStep step) {
            wizardSteps.addLast(step);
            return this;
        }

        public WizardBuilder addFinishedListener(WizardFinishedListener lst) {
            finishedListeners.add(lst);
            return this;
        }

        public WizardBuilder addCancelListener(WizardCanceldListener lst) {
            cancelListeners.add(lst);
            return this;
        }

        /**
         * Builds and initializes a {@link WizardComponent} instance.
         *
         * <p>The build() method is used to create a new instance of {@link WizardComponent},
         * set the steps, finish listeners, cancel listeners and initialize the user interface.
         *
         * @return the built and initialized {@link WizardComponent} instance
         * @see WizardComponent
         * @see WizardComponent.WizardBuilder
         */
        public WizardComponent build() {
            var wizardComponent = new WizardComponent();
            wizardComponent.steps = wizardSteps;
            wizardComponent.finishListener = this.finishedListeners;
            wizardComponent.cancelListener = this.cancelListeners;
            wizardComponent.initUI();
            return wizardComponent;
        }
    }

    /**
     * A functional interface for listening to the event when a wizard is finished. The
     * wizardFinished() method will be called when the wizard is finished. The parameters
     * wizardComponent and wizardStep represent the wizard component and the current wizard step,
     * respectively.
     *
     * @see WizardComponent
     * @see WizardStep
     */
    public interface WizardFinishedListener {
        void wizardFinished(WizardComponent wizardComponent, WizardStep wizardStep);
    }

    /**
     * A functional interface for listening to the event when a wizard is cancelled.
     */
    public interface WizardCanceldListener {
        void wizardCanceld(WizardComponent wizardComponent, WizardStep wizardStep);
    }


    private WizardComponent() {
    }

    private void initUI() {
        add(header);
        add(progressBar);
        addAndExpand(
            new HorizontalLayout(backButton, nextButton, finishButton, cancelButton, stepTitle));
        addAndExpand(content);
        nextButton.addClickListener(this::forwardButtonClicked);
        backButton.addClickListener(this::backButtonClicked);
        cancelButton.addClickListener(this::cancelButtonClicked);
        finishButton.addClickListener(this::finishButtonClicked);

        // set first step...
        this.content.setSizeFull();

        if (!this.steps.isEmpty()) {
            this.setActiveStep(this.steps.getFirst());
        }

        this.progressBar.setMin(0);
        this.progressBar.setMax((this.steps.size()));
        this.progressBar.setValue(0);
        this.backButton.setEnabled(false);
        this.nextButton.setEnabled(true);
    }

    public void sendWizardStepChangeEvent(WizardStep.WizardStepStateChangeEvent e) {
        updateButtons(e.getStep());
    }

    private void finishButtonClicked(ClickEvent<Button> buttonClickEvent) {
        // TODO: create finish code...
        if (getStepIndex(this.activeStep) != this.steps.size() - 1) {
            LOGGER.error(
                "Finish Button clicked and it was not the last step in wizard! Nothing will "
                    + "be done!"
            );
            return;
        }

        this.activeStep.onForward(() ->
                                      finishListener
                                          .forEach(
                                              lst -> lst.wizardFinished(this, this.activeStep)))
        ;
    }

    private void cancelButtonClicked(ClickEvent<Button> buttonClickEvent) {
        // TODO: inform listener...
        this.cancelListener.forEach(lst -> lst.wizardCanceld(this, this.activeStep));
    }

    private void forwardButtonClicked(ClickEvent<Button> buttonClickEvent) {
        this.activeStep.onForward(() -> {
            int i = steps.indexOf(activeStep);
            if ((i + 1) < steps.size()) {
                var wizardStep = steps.get(i + 1);
                this.setActiveStep(wizardStep);
            }
        });
    }

    private void backButtonClicked(ClickEvent<Button> buttonClickEvent) {
        final var ui = UI.getCurrent();
        this.activeStep.onBack(() -> {
            int i = steps.indexOf(activeStep);
            if (i - 1 >= 1) {
                var wizardStep = steps.get(i - 1);
                ui.access(() -> this.setActiveStep(wizardStep));
            }
        });
    }

    private void setActiveStep(WizardStep wizardStep) {
        this.content.removeAll();
        this.content.add(wizardStep.getComponent());
        this.activeStep = wizardStep;
        this.stepTitle.setText(wizardStep.getStepTitle());

        updateButtons(wizardStep);
    }

    private void updateButtons(WizardStep wizardStep) {
        int i = getStepIndex(wizardStep);
        nextButton.setEnabled(
            i + 1 < steps.size() && activeStep.isNextSupported()
        );

        finishButton.setEnabled(i + 1 == steps.size());

        backButton.setEnabled(
            (i > 0) && activeStep.isBackSupported()
        );
    }

    private int getStepIndex(WizardStep wizardStep) {
        return steps.indexOf(activeStep);
    }

    public void addCancelListener(WizardCanceldListener wizardCanceldListener) {
        this.cancelListener.add(wizardCanceldListener);
    }

    public void addFinishListener(WizardFinishedListener finishedListener) {
        this.finishListener.add(finishedListener);
    }
}
