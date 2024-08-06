/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.ecodex.evidences.types;

import lombok.Getter;
import lombok.Setter;

/**
 * This class represents the details of an ECodex message. It stores information such as the sender
 * address, recipient address, national message ID, ebms message ID, hash value, and hash
 * algorithm.
 */
@Getter
@Setter
public class ECodexMessageDetails {
    private String senderAddress;
    private String recipientAddress;
    private String nationalMessageId;
    private String ebmsMessageId;
    private byte[] hashValue;
    private String hashAlgorithm;
}
