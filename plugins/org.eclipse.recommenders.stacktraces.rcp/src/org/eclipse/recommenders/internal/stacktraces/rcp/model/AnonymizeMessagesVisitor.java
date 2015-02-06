/**
 * Copyright (c) 2014 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daniel Haftstein - initial API and implementation.
 */
package org.eclipse.recommenders.internal.stacktraces.rcp.model;

import org.eclipse.recommenders.internal.stacktraces.rcp.model.impl.VisitorImpl;

public class AnonymizeMessagesVisitor extends VisitorImpl {
    @Override
    public void visit(Status status) {
        status.setMessage(anonymize(status.getMessage()));
    }

    @Override
    public void visit(Throwable throwable) {
        throwable.setMessage(anonymize(throwable.getMessage()));
    }

    public String anonymize(String message) {

        return message;
    }
}