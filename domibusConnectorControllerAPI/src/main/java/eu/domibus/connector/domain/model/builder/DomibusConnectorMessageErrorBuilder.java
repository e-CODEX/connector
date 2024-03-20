
package eu.domibus.connector.domain.model.builder;

import eu.domibus.connector.domain.model.DomibusConnectorMessageError;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.stream.Stream;

/**
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
public final class DomibusConnectorMessageErrorBuilder {

    private String text = "";
    private String details = "";
    private String source = "";
    private String step = "";
    private String processor = "";

    private DomibusConnectorMessageErrorBuilder() {
    }

    public static DomibusConnectorMessageErrorBuilder createBuilder() {
        return new DomibusConnectorMessageErrorBuilder();
    }

    /**
     * @param text short, main error text
     * @return the builder
     */
    public DomibusConnectorMessageErrorBuilder setText(@NotNull @NotBlank String text) {
        this.text = text;
        return this;
    }

    /**
     * @param details error details, eg the exception stack trace
     * @return the builder
     */
    public DomibusConnectorMessageErrorBuilder setDetails(@NotNull String details) {
        this.details = details;
        return this;
    }


    public DomibusConnectorMessageErrorBuilder setDetails(Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        this.details = sw.toString();
        return this;
    }

    /**
     * @param source Name of the component where the error occured
     * @return the builder
     */
    public DomibusConnectorMessageErrorBuilder setSource(@NotNull String source) {
        this.source = source;
        return this;
    }

    public DomibusConnectorMessageErrorBuilder setStep(@NotNull String step) {
        this.step = step;
        return this;
    }

    public DomibusConnectorMessageErrorBuilder setProcessor(@NotNull String processor) {
        this.processor = processor;
        return this;
    }

    public DomibusConnectorMessageError build() {
        if (text == null) {
            throw new RuntimeException("Text cannot be null!");
        }
        DomibusConnectorMessageError msgError = new DomibusConnectorMessageError(text, details, source, step, processor);
        return msgError;
    }


    public DomibusConnectorMessageErrorBuilder setSource(Class aClass) {
        this.source = aClass.getName();
        return this;
    }
}
