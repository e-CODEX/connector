package eu.domibus.connector.ui.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import javax.jms.Message;
import java.util.ArrayList;
import java.util.List;


public class WebQueue {

    @Setter
    private String name;
    @Getter
    @Setter
    private List<Message> messages = new ArrayList<>();
    @Getter
    @Setter
    private List<Message> dlqMessages = new ArrayList<>();
    @Getter
    @Setter
    private int msgsOnQueue;
    @Getter
    @Setter
    private int msgsOnDlq;

    public String getName() {
        return StringUtils.capitalize(name).replace("/([A-Z])/g", "$1").trim();
    }
}
