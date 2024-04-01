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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


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

    private WizardComponent() {
    }

    public static WizardBuilder getBuilder() {
        return new WizardBuilder();
    }

    private void initUI() {

        add(header);
        add(progressBar);
        addAndExpand(new HorizontalLayout(backButton, nextButton, finishButton, cancelButton, stepTitle));
        addAndExpand(content);
        nextButton.addClickListener(this::forwardButtonClicked);
        backButton.addClickListener(this::backButtonClicked);
        cancelButton.addClickListener(this::cancelButtonClicked);
        finishButton.addClickListener(this::finishButtonClicked);

        // set first step...
        this.content.setSizeFull();

        if (this.steps.size() > 0) {
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
            LOGGER.error("Finish Button clicked and it was not the last step in wizard! Nothing will be done!");
            return;
        }

        this.activeStep.onForward(() -> {
            finishListener.stream().forEach(lst -> lst.wizardFinished(this, this.activeStep));
        });
    }

    private void cancelButtonClicked(ClickEvent<Button> buttonClickEvent) {
        // TODO: inform listener...
        this.cancelListener.stream().forEach(lst -> lst.wizardCanceld(this, this.activeStep));
    }

    private void forwardButtonClicked(ClickEvent<Button> buttonClickEvent) {
        this.activeStep.onForward(() -> {
            int i = steps.indexOf(activeStep);
            if ((i + 1) < steps.size()) {
                WizardStep wizardStep = steps.get(i + 1);
                this.setActiveStep(wizardStep);
            }
        });
    }

    private void backButtonClicked(ClickEvent<Button> buttonClickEvent) {
        final UI ui = UI.getCurrent();
        this.activeStep.onBack(() -> {
            int i = steps.indexOf(activeStep);
            if (i - 1 >= 1) {
                WizardStep wizardStep = steps.get(i - 1);
                ui.access(() -> {
                    this.setActiveStep(wizardStep);
                });
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

    public void addCancelListener(WizardCanceldListener canceldListener) {
        this.cancelListener.add(canceldListener);
    }

    public void addFinishListener(WizardFinishedListener finishedListener) {
        this.finishListener.add(finishedListener);
    }

    public interface WizardFinishedListener {
        void wizardFinished(WizardComponent wizardComponent, WizardStep wizardStep);
    }

    public interface WizardCanceldListener {
        void wizardCanceld(WizardComponent wizardComponent, WizardStep wizardStep);
    }

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

        public WizardComponent build() {
            WizardComponent wizardComponent = new WizardComponent();
            wizardComponent.steps = wizardSteps;
            wizardComponent.finishListener = this.finishedListeners;
            wizardComponent.cancelListener = this.cancelListeners;
            wizardComponent.initUI();
            return wizardComponent;
        }
    }
}
