/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2014, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.as.ejb3.subsystem;

import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.OperationStepHandler;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.RestartParentResourceHandlerBase;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceName;

public abstract class RemotingProfileChildResourceHandlerBase extends RestartParentResourceHandlerBase {

     protected RemotingProfileChildResourceHandlerBase() {
         super(EJB3SubsystemModel.REMOTING_PROFILE);
    }

    @Override
    protected void recreateParentService(final OperationContext context, final PathAddress parentAddress, final ModelNode parentModel) throws OperationFailedException {

        switch(context.getCurrentStage()) {
            case RUNTIME:
                // service installation in another step: when interruption is thrown then it is handled by RollbackHandler
                // declared in RestartParentResourceHandlerBase
                context.addStep(new OperationStepHandler() {
                    @Override
                    public void execute(OperationContext context, ModelNode operation) throws OperationFailedException {
                        RemotingProfileResourceDefinition.ADD_HANDLER.installServices(context, parentAddress, parentModel);
                    }
                }, OperationContext.Stage.RUNTIME);
            break;
            case DONE:
                // executed from RollbackHandler - service is being installed using correct configuration
                RemotingProfileResourceDefinition.ADD_HANDLER.installServices(context, parentAddress, parentModel);
                break;
        }
    }

    @Override
    protected ServiceName getParentServiceName(PathAddress parentAddress) {
       return RemotingProfileResourceDefinition.REMOTING_PROFILE_CAPABILITY.getCapabilityServiceName(parentAddress);
    }

    @Override
    protected void removeServices(OperationContext context, ServiceName parentService, ModelNode parentModel) throws OperationFailedException {
        super.removeServices(context, parentService, parentModel);
    }
}
