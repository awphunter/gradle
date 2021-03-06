/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.launcher.daemon.testing

import org.gradle.launcher.daemon.registry.DaemonRegistry
import org.gradle.launcher.daemon.testing.AbstractDaemonFixture.State

class TestableDaemon extends AbstractDaemonFixture {
    private final DaemonLogFileStateProbe logFileProbe
    private final DaemonRegistryStateProbe registryProbe

    TestableDaemon(File daemonLog, DaemonRegistry registry) {
        super(daemonLog)
        this.logFileProbe = new DaemonLogFileStateProbe(daemonLog, context)
        this.registryProbe = new DaemonRegistryStateProbe(registry, context)
    }

    protected void waitForState(State state) {
        def expiry = System.currentTimeMillis() + STATE_CHANGE_TIMEOUT
        def lastRegistryState = registryProbe.currentState
        def lastLogState = logFileProbe.currentState
        while (expiry > System.currentTimeMillis() && (lastRegistryState != state || lastLogState != state)) {
            Thread.sleep(200)
            lastRegistryState = registryProbe.currentState
            lastLogState = logFileProbe.currentState
        }
        if (lastRegistryState == state && lastLogState == state) {
            return
        }
        throw new AssertionError("""Timeout waiting for daemon with pid ${context.pid} to reach state ${state}.
Current registry state is ${lastRegistryState} and current log state is ${lastLogState}.""")
    }

    @Override
    protected void assertHasState(State state) {
        assert logFileProbe.currentState == state
        assert registryProbe.currentState == state
    }

    String getLog() {
        return logFileProbe.log
    }

    int getPort() {
        return logFileProbe.port
    }
}